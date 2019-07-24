package org.ogin.cb.gui;

import java.awt.Color;
import java.awt.Component;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.ogin.cb.gui.components.*;
import org.ogin.cb.gui.dnd.GateTransferHandler;
import org.ogin.cb.gui.dnd.PinDragSourceListener;
import org.ogin.cb.gui.dnd.PinSelection;
import org.ogin.cb.gui.dnd.PinTransferHandler;

public class Canvas extends JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = -7698407387574080927L;

    private static final Color PIN_COLOR_VALID_DROP = Color.GREEN;
    private static final Color PIN_COLOR_INVALID_DROP = Color.RED;

    private ComponentMover gateMover;

    private KeyListener componentKeyListener;

    private MouseListener componentMouseListener;

    private PinTransferHandler pinTransferHandler;
    private PinDragSourceListener pinDragSourceListener;

    /**
     * Initializes the Canvas and its default components. Object instantiations
     * must call this or its implementation is undefined. You may want to add
     * listeners before calling this, or else certain actions will be missed.
     */
    public void init() {
        // remove layout manager as we move components manually
        setLayout(null);

        // delete currently focused component when Delete or BackSpace pressed
        componentKeyListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (e.getComponent() instanceof AbstractGate) {
                        AbstractGate gate = (AbstractGate) e.getComponent();
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

        gateMover = new ComponentMover();
        gateMover.setMovementCursor(Cursor.HAND_CURSOR);
        // if we enable grid snapping, ensure new items are created on the boundary
        // mover.setSnapSize(new Dimension(10, 10));

        setTransferHandler(new GateTransferHandler(this, this::attachComponent));

        pinTransferHandler = new PinTransferHandler(this::handleWireCreation);
        pinDragSourceListener = new PinDragSourceListener();

        createDefaultComponents();
    }

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

    private void attachComponent(Wire wire) {
        addCommonComponent(wire, false);
        // wire.requestFocusInWindow();
    }

    private void addCommonComponent(JComponent component, boolean randomizeName) {
        add(component);

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
                    dragEvent.startDrag(DragSource.DefaultLinkDrop, new PinSelection(PinTransferHandler.FLAVOR, outPin));
                }
            };
            source.createDefaultDragGestureRecognizer(outPin, DnDConstants.ACTION_LINK, dragListener);
        }
    }

    private void configureInPinsAfterDragEvent(Pin outPinEventOriginator) {
        for (Pin pin : getInPins()) {
            pin.setBackground(Pin.DEFAULT_BACKGROUND_COLOR);
        }

        // all IN pins on the same gate as the OUT drag originator were marked
        // unavailable, so review and reset those that should be available.
        for (Pin pin : getGateOwningOutPin(outPinEventOriginator).getInPins()) {
            if (!isWired(pin)) {
                markInPinAvailable(pin);
            }
        }
    }

    private boolean isWired(Pin inPin) {
        for (Wire wire : getWires()) {
            if (wire.getInPin().equals(inPin)) {
                return true;
            }
        }

        return false;
    }

    private void prepareInPinBackgroundsForDragEvent(Pin outPinEventOriginator) {
        // all IN pins on the same gate as the OUT drag originator need to be
        // unavailable
        for (Pin pin : getGateOwningOutPin(outPinEventOriginator).getInPins()) {
            markInPinUnavailable(pin);
        }

        for (Pin pin : getInPins()) {
            Color newBackground = pin.getDropTarget().isActive() ? PIN_COLOR_VALID_DROP : PIN_COLOR_INVALID_DROP;
            pin.setBackground(newBackground);
        }
    }

    private List<Pin> getInPins() {
        List<Pin> inPins = new ArrayList<>();

        for (Component c : getComponents()) {
            if (c instanceof AbstractGate) {
                AbstractGate gate = (AbstractGate) c;
                inPins.addAll(Arrays.asList(gate.getInPins()));
            }
        }

        return inPins;
    }

    private AbstractGate getGateOwningOutPin(Pin outPinToFind) {
        for (Component c : getComponents()) {
            if (c instanceof AbstractGate) {
                AbstractGate gate = (AbstractGate) c;
                if(ArrayUtils.contains(gate.getOutPins(), outPinToFind)) {
                    return gate;
                }
            }
        }

        System.err.println("Failed to find gate from out pin in component tree: " + outPinToFind);

        return null;
    }

    /**
     * Remove the component from the canvas.
     * 
     * @param gate
     */
    private void detachComponent(AbstractGate gate) {
        gateMover.deregisterComponent(gate);

        detachCommonComponent(gate);

        List<Wire> attachedWires = getWiresAttachedToGate(gate);

        for (Wire wire : attachedWires) {
            detachComponent(wire);
        }
    }

    private List<Wire> getWiresAttachedToGate(AbstractGate gate) {
        List<Wire> attachedWires = new ArrayList<>();

        for (Wire wire : getWires()) {
            if(ArrayUtils.contains(gate.getInPins(), wire.getInPin())) {
                attachedWires.add(wire);
            }

            if(ArrayUtils.contains(gate.getOutPins(), wire.getOutPin())) {
                attachedWires.add(wire);
            }
        }

        return attachedWires;
    }

    private List<Wire> getWires() {
        List<Wire> wires = new ArrayList<>();

        for (Component c : getComponents()) {
            if (c instanceof Wire) {
                wires.add((Wire) c);
            }
        }

        return wires;
    }

    private void detachComponent(Wire wire) {
        detachCommonComponent(wire);
        wire.removeListeners();
        // now that a wire is not attached to the IN pin,
        // allow it to be used as a drop target
        markInPinAvailable(wire.getInPin());
    }

    private void detachCommonComponent(JComponent c) {
        c.removeKeyListener(componentKeyListener);
        c.removeMouseListener(componentMouseListener);

        remove(c);

        // very important
        repaint();
    }

    private void handleWireCreation(Pin srcOutPin, Pin destInPin) {
        // do this as the post-serialized pin provided here does not have the
        // parent container when calling getParent() - something to do with
        // Swing that can be researched later.
        Pin locatedOutPin = findOutPin(srcOutPin);

        if (locatedOutPin != null) {
            Wire wire = new Wire(locatedOutPin, destInPin);

            attachComponent(wire);
            markInPinUnavailable(destInPin);
        }
    }

    private Pin findOutPin(Pin outPinToFind) {
        for (Component c : getComponents()) {
            if (c instanceof AbstractGate) {
                AbstractGate gate = (AbstractGate) c;
                int index = ArrayUtils.indexOf(gate.getOutPins(), outPinToFind);
                if(index >= 0) {
                    return gate.getOutPins()[index];
                }
            }
        }

        System.err.println("Failed to find OUT pin in component tree: " + outPinToFind);

        return null;
    }

    public void markInPinUnavailable(Pin pin) {
        pin.getDropTarget().setActive(false);
    }

    public void markInPinAvailable(Pin pin) {
        pin.getDropTarget().setActive(true);
    }

    private void clearCanvas() {
        for(Component c : getComponents()) {
            if(c instanceof Wire) {
                detachComponent((Wire)c);
            }else if(c instanceof AbstractGate) {
                detachComponent((AbstractGate)c);
            } else {
                System.err.println("TODO: implement removal of " + c.getClass().getSimpleName());
            }
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
        if(StringUtils.equals(evt.getPropertyName(), PropertyNames.New.name())) {
            if(acceptsReset()) {
                clearCanvas();
                createDefaultComponents();
            }
        }
    }

    private boolean acceptsReset() {
        int response = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to clear your current work?",
            "Reset", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        return response == JOptionPane.YES_OPTION;
    }
}