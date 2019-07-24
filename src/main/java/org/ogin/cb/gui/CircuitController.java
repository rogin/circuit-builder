package org.ogin.cb.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.ogin.cb.gui.components.AbstractGate;
import org.ogin.cb.gui.components.Ground;
import org.ogin.cb.gui.components.In;
import org.ogin.cb.gui.components.Out;
import org.ogin.cb.gui.components.Pin;
import org.ogin.cb.gui.components.Power;
import org.ogin.cb.gui.components.Wire;
import org.ogin.cb.gui.dnd.GateTransferHandler;
import org.ogin.cb.gui.dnd.PinDragSourceListener;
import org.ogin.cb.gui.dnd.PinSelection;
import org.ogin.cb.gui.dnd.PinTransferHandler;

public class CircuitController implements PropertyChangeListener {
    private static final Color PIN_COLOR_VALID_DROP = Color.GREEN;
    private static final Color PIN_COLOR_INVALID_DROP = Color.RED;
    
    private CircuitModel model;
    private JComponent canvas;

    /** provides movement functionality for gates */
    private ComponentMover gateMover;

    /** provides ability to delete components via keyboard */
    private KeyListener componentKeyListener;

    /** provides focus to clicked components */
    private MouseListener componentMouseListener;

    /** provides notifications when a wire is drawn */
    private PinTransferHandler pinTransferHandler;

    /** toggles cursor during pin drag events */
    private PinDragSourceListener pinDragSourceListener;

    public CircuitController(CircuitModel model, JComponent canvas) {
        this.model = model;
        this.canvas = canvas;

        init();
	}

	private void init() {
        //allows the model to be populated by components added to the canvas
        canvas.addContainerListener(model);

        // delete currently focused component when Delete or BackSpace pressed
        componentKeyListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (e.getComponent() instanceof AbstractGate) {
                        AbstractGate gate = (AbstractGate) e.getComponent();
                        // avoid deleting immovable gates - POWER, GROUND, etc
                        if (gate.isMovable()) {
                            detachComponent(gate);
                        }
                    } else if (e.getComponent() instanceof Wire) {
                        detachComponent((Wire) e.getComponent());
                    }
                }
            }
        };

        // on mouse click, give the component focus
        componentMouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.getComponent().requestFocus();
            }
        };

        gateMover = new ComponentMover(Cursor.HAND_CURSOR);
        // if we enable grid snapping, ensure new items are created on the boundary
        // mover.setSnapSize(new Dimension(10, 10));

        canvas.setTransferHandler(new GateTransferHandler(canvas, this::attachComponent));

        pinTransferHandler = new PinTransferHandler(this::handleWireCreation);
        pinDragSourceListener = new PinDragSourceListener();

        createDefaultComponents();
    }

    /**
     * Attach a component onto the canvas at the specified point.
     * @param point
     * @param gate
     */
    private void attachComponent(Point point, AbstractGate gate) {
        gate.setLocation(point);

        addCommonComponent(gate, gate.isMovable());

        if (gate.isMovable()) {
            // this allows us to move the component around the canvas
            gateMover.registerComponent(gate);
            gate.requestFocusInWindow();
        }

        configureDragSources(gate.getOutPins());

        assignTransferHandler(gate.getInPins());
    }

    /**
     * Attach a component onto the canvas.
     * @param wire
     */
    private void attachComponent(Wire wire) {
        addCommonComponent(wire, false);
        // wire.requestFocusInWindow();
    }

    /**
     * Perform the steps common to all components when they're added to the canvas.
     * @param component
     * @param randomizeName
     */
    private void addCommonComponent(JComponent component, boolean randomizeName) {
        canvas.add(component);

        component.addMouseListener(componentMouseListener);
        component.addKeyListener(componentKeyListener);

        if (randomizeName) {
            // change the name to avoid lookup errors
            component.setName(component.getName() + Long.toString(RandomUtils.nextLong()));
        }

        // very important
        component.repaint();
    }

    private void assignTransferHandler(Pin[] inPins) {
        for (Pin pin : inPins) {
            pin.setTransferHandler(pinTransferHandler);
        }
    }

    private void configureDragSources(Pin[] outPins) {
        for(Pin outPin : outPins) {
            DragSource source = new DragSource();

            source.addDragSourceListener(pinDragSourceListener);
            source.addDragSourceListener(new DragSourceAdapter() {
                @Override
                public void dragDropEnd(DragSourceDropEvent event) {
                    configureInPinsAfterDragEvent((Pin) event.getDragSourceContext().getComponent());
                }
            });
            DragGestureListener dragListener = new DragGestureListener() {

                @Override
                public void dragGestureRecognized(DragGestureEvent dragEvent) {
                    prepareInPinBackgroundsForDragEvent((Pin) dragEvent.getComponent());
                    dragEvent.startDrag(DragSource.DefaultLinkDrop, new PinSelection(PinTransferHandler.PIN_FLAVOR, outPin));
                }
            };
            source.createDefaultDragGestureRecognizer(outPin, DnDConstants.ACTION_LINK, dragListener);
        }
    }

    private void configureInPinsAfterDragEvent(Pin outPinEventOriginator) {
        for (Pin pin : model.getInPins()) {
            pin.setBackground(Pin.DEFAULT_BACKGROUND_COLOR);
        }

        // all IN pins on the same gate as the OUT drag originator were marked
        // unavailable, so review and reset those that should be available.
        for (Pin pin : model.getGateOwningOutPin(outPinEventOriginator).getInPins()) {
            if (!model.isWired(pin)) {
                markInPinAvailable(pin);
            }
        }
    }

    private void prepareInPinBackgroundsForDragEvent(Pin outPinEventOriginator) {
        // all IN pins on the same gate as the OUT drag originator need to be
        // unavailable
        for (Pin pin : model.getGateOwningOutPin(outPinEventOriginator).getInPins()) {
            markInPinUnavailable(pin);
        }

        for (Pin pin : model.getInPins()) {
            Color newBackground = pin.getDropTarget().isActive() ? PIN_COLOR_VALID_DROP : PIN_COLOR_INVALID_DROP;
            pin.setBackground(newBackground);
        }
    }

    /**
     * Remove a component from the canvas.
     * 
     * @param gate
     */
    private void detachComponent(AbstractGate gate) {
        detachCommonComponent(gate);

        gateMover.deregisterComponent(gate);

        for (Wire wire : model.getWiresAttachedToGate(gate)) {
            detachComponent(wire);
        }
    }

    /**
     * Remove a component from the canvas.
     * 
     * @param wire
     */
    private void detachComponent(Wire wire) {
        detachCommonComponent(wire);

        wire.removeListeners();
        // now that a wire is not attached to the IN pin,
        // allow it to be used as a drop target
        markInPinAvailable(wire.getInPin());
    }

    /**
     * Perform the steps common to all components when they're removed from the canvas.
     * @param component
     */
    private void detachCommonComponent(JComponent component) {
        component.removeKeyListener(componentKeyListener);
        component.removeMouseListener(componentMouseListener);

        canvas.remove(component);

        // very important
        canvas.repaint();
    }

    private void handleWireCreation(Pin srcOutPin, Pin destInPin) {
        // do this as the post-serialized pin provided here does not have the
        // parent container when calling getParent() - something to do with
        // Swing that can be researched later.
        Pin locatedOutPin = model.findOutPin(srcOutPin);

        if (locatedOutPin != null) {
            Wire wire = new Wire(locatedOutPin, destInPin);

            attachComponent(wire);
            markInPinUnavailable(destInPin);
        }
    }

    public void markInPinUnavailable(Pin pin) {
        pin.getDropTarget().setActive(false);
    }

    public void markInPinAvailable(Pin pin) {
        pin.getDropTarget().setActive(true);
    }

    private void clearCanvas() {
        for(Wire wire : model.getWires()) {
            detachComponent(wire);
        }

        for(AbstractGate gate : model.getGates()) {
            detachComponent(gate);
        }
    }

    private void createDefaultComponents() {
        attachComponent(new Point(0, 100), new Power());
        attachComponent(new Point(0, 200), new In(3));
        attachComponent(new Point(0, 300), new Ground());
        attachComponent(new Point(650, 200), new Out(3));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //if menu option "New" was selected
        if(StringUtils.equals(evt.getPropertyName(), PropertyNames.New.name())) {
            //verify the user is OK resetting the canvas
            if(acceptsReset()) {
                clearCanvas();
                createDefaultComponents();
            }
        }
    }

    private boolean acceptsReset() {
        int response = JOptionPane.showConfirmDialog(canvas.getTopLevelAncestor(),
            "Are you sure you want to clear your current work?",
            "Reset", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        return response == JOptionPane.YES_OPTION;
    }
}