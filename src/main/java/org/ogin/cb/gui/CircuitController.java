package org.ogin.cb.gui;

import java.awt.Cursor;
import java.awt.Point;
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
import org.ogin.cb.gui.dnd.PinDragProvider;
import org.ogin.cb.gui.dnd.GateTransferHandler;

public class CircuitController implements PropertyChangeListener {
    
    private CircuitModel model;
    private JComponent canvas;

    /** provides movement functionality for gates */
    private ComponentMover gateMover;

    /** provides ability to delete components via keyboard */
    private KeyListener componentKeyListener;

    /** provides focus to clicked components */
    private MouseListener focusProvider;

    /** provides drag-n-drop between pins to create wires */
    private PinDragProvider pinDragProvider;

    public CircuitController(CircuitModel model, JComponent canvas) {
        this.model = model;
        this.canvas = canvas;

        init();
	}

	private void init() {
        pinDragProvider = new PinDragProvider(model, this::onWireCreated);

        //allows the model to be populated by components added to the canvas
        canvas.addContainerListener(model);
        //ensure listener added after the model so new components are available
        //when processing DnD events
        canvas.addContainerListener(pinDragProvider);

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
        focusProvider = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                event.getComponent().requestFocus();
            }
        };

        gateMover = new ComponentMover(Cursor.HAND_CURSOR);
        // if we enable grid snapping, ensure new items are created on the boundary
        // mover.setSnapSize(new Dimension(10, 10));

        canvas.setTransferHandler(new GateTransferHandler(canvas, this::onGateCreation));

        createDefaultComponents();
    }

    /**
     * Responds to a new gate being dragged onto the canvas at the specified location.
     * @param point
     * @param gate
     */
    private void onGateCreation(Point point, AbstractGate gate) {
        gate.setLocation(point);

        addCommonComponent(gate, gate.isMovable());

        if (gate.isMovable()) {
            // this allows us to move the component around the canvas
            gateMover.registerComponent(gate);
            gate.requestFocusInWindow();
        }
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

        component.addMouseListener(focusProvider);
        component.addKeyListener(componentKeyListener);

        if (randomizeName) {
            // change the name to avoid lookup errors
            component.setName(component.getName() + Long.toString(RandomUtils.nextLong()));
        }

        // very important
        component.repaint();
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
    }

    /**
     * Perform the steps common to all components when they're removed from the canvas.
     * @param component
     */
    private void detachCommonComponent(JComponent component) {
        component.removeKeyListener(componentKeyListener);
        component.removeMouseListener(focusProvider);

        canvas.remove(component);

        // very important
        canvas.repaint();
    }

    /**
     * Responds to a new wire being created between the specified pins.
     * @param srcOutPin
     * @param destInPin
     */
    private void onWireCreated(Pin srcOutPin, Pin destInPin) {
        // do this as the post-serialized pin provided here does not have the
        // parent container when calling getParent() - something to do with
        // Swing that can be researched later.
        Pin locatedOutPin = model.findOutPin(srcOutPin);

        if (locatedOutPin != null) {
            Wire wire = new Wire(locatedOutPin, destInPin);

            attachComponent(wire);
        }
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
        onGateCreation(new Point(0, 100), new Power());
        onGateCreation(new Point(0, 200), new In(3));
        onGateCreation(new Point(0, 300), new Ground());
        onGateCreation(new Point(650, 200), new Out(3));
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        //if menu option "New" was selected
        if(StringUtils.equals(event.getPropertyName(), PropertyNames.New.name())) {
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