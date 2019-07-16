package org.ogin.cb.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.ogin.cb.CircuitData;
import org.ogin.cb.gui.components.*;
import org.ogin.cb.gui.dnd.GateTransferHandler;
import org.ogin.cb.gui.dnd.PinSelection;
import org.ogin.cb.gui.dnd.PinTransferHandler;

public class Canvas extends JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = -7698407387574080927L;

    private ComponentMover componentMover;

    private KeyListener componentKeyListener;

    private MouseListener componentMouseListener;

    private PinTransferHandler pinTransferHandler;

    public Canvas() {
        init();
    }

    private void init() {
        // remove layout manager as we move components manually
        setLayout(null);

        //delete currently focused component when Delete or BackSpace pressed
        componentKeyListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if(e.getComponent() instanceof AbstractGate) {
                        AbstractGate gate = (AbstractGate)e.getComponent();
                        if(gate.isMovable()) {
                            detachComponent(gate);
                        }
                    } else if(e.getComponent() instanceof Wire) {
                        detachComponent((Wire)e.getComponent());
                    }
                }

                /*
                if(e.getKeyCode() == KeyEvent.VK_D) {
                    debugComponents();
                }
                */
            }
        };

        //on mouse click, give the component focus
        componentMouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.getComponent().requestFocus();
            }
        };

        componentMover = new ComponentMover();
        componentMover.setMovementCursor(Cursor.HAND_CURSOR);
        // if we enable grid snapping, ensure new items are created on the boundary
        // mover.setSnapSize(new Dimension(10, 10));

        setTransferHandler(new GateTransferHandler(this, this::attachComponent));

        pinTransferHandler = new PinTransferHandler(this::handleWireCreation);

        debugFocus();

        createDefaultComponents();
    }

    /*
    private void debugComponents() {
        System.err.println("Dumping component locations");
        for(Component c : getComponents()) {
            System.err.println(c.getClass().getSimpleName() + " location: " + c.getLocation());
            JComponent x = (JComponent)c;
            for(Component child : x.getComponents()) {
                System.err.println("\t" + child.getClass().getSimpleName() + " location: " + child.getLocation());
                if(child instanceof Pin) {
                    Pin pin = (Pin) child;
                    //System.err.println("Found a pin");
                }
            }
        }
    }
    */

    private void debugFocus() {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        focusManager.addPropertyChangeListener(
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    String properties = e.getPropertyName();
                    if (("focusOwner".equals(properties)) && (e.getNewValue() != null)) {
                        Component component = (Component)e.getNewValue();
                        //String name = component.getName();

                        System.out.println(component.getClass().getSimpleName() + " took focus");
                    }
                }
            }
        );
    }

    private void attachComponent(Point point, AbstractGate gate) {
        gate.setLocation(point);

        addCommonComponent(gate);

        if(gate.isMovable()) {
            //this allows us to move the component around the canvas
            componentMover.registerComponent(gate);
            gate.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    //System.err.println("movement from " + e.getComponent() + " and " + e.getSource());
                }
            });
            gate.requestFocusInWindow();
        }

        attachDragForOutPin(gate.getOutPin());

        attachDropFromInPins(gate.getInPins());
    }

    private void attachComponent(Wire wire) {
        addCommonComponent(wire);
        wire.requestFocusInWindow();
    }

    private void addCommonComponent(JComponent component) {
        add(component);

        component.addMouseListener(componentMouseListener);
        component.addKeyListener(componentKeyListener);

        //very important
        component.repaint();
    }

    private void attachDropFromInPins(Pin[] inPins) {
        for(Pin pin : inPins) {
            pin.setTransferHandler(pinTransferHandler);
        }
    }

    private void attachDragForOutPin(Pin pin) {
        DragSource source = DragSource.getDefaultDragSource();
        DragGestureListener dragListener = new DragGestureListener() {

            @Override
            public void dragGestureRecognized(DragGestureEvent dragEvent) {
                System.out.println("dragGestureRecognized()");
                dragEvent.startDrag(DragSource.DefaultLinkDrop, new PinSelection(PinTransferHandler.FLAVOR, pin));
            }
        };
        source.createDefaultDragGestureRecognizer(pin, DnDConstants.ACTION_LINK, dragListener);
    }

    /**
     * Remove the component from the canvas.
     * @param gate
     */
    private void detachComponent(AbstractGate gate) {
        componentMover.deregisterComponent(gate);

        detachCommonComponent(gate);

        //TODO remove each attached wire
    }

    private void detachComponent(Wire wire) {
        detachCommonComponent(wire);
    }

    private void detachCommonComponent(JComponent c) {
        c.removeKeyListener(componentKeyListener);
        c.removeMouseListener(componentMouseListener);
        
        remove(c);

        //very important
        repaint();
    }

    private void handleWireCreation(Pin srcOutPin, Pin destInPin) {
        //do this as the post-serialized pin provided here does not have the
        //parent container when calling getParent() - something to do with
        //Swing that can be researched later.
        Pin locatedOutPin = findPinInComponentTree(srcOutPin);

        if(locatedOutPin != null) {
            Wire wire = new Wire(srcOutPin, destInPin);

            attachComponent(wire);
        }
    }

    private Pin findPinInComponentTree(Pin toFind) {
        for(Component comp : getComponents()) {
            //System.err.println("main component: " + c.getClass().getSimpleName());
            if(comp instanceof JComponent) {
                for(Component child : ((JComponent)comp).getComponents()) {
                    //System.err.println("\t" + child.getClass().getSimpleName());
                    if(child instanceof Pin) {
                        if(toFind.equals(child)) {
                            return (Pin)child;
                        }
                    }
                }
            }
        }

        System.err.println("Failed to find pin in component tree: " + toFind);
        
        return null;
    }

    public CircuitData getModelData() {
        CircuitData data = new CircuitData();

        for(Component c : getComponents()) {
            //TODO extract necessary data
            if(c instanceof AbstractGate) {
                AbstractGate gate = (AbstractGate)c;

                //avoid builtin types?
                if(gate instanceof BuiltinComponent) {
                    continue;
                }
            } else if(c instanceof Wire) {
                //append wire data
            }
        }

        return data;
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
        attachComponent(new Point(0, 200), new In());
        attachComponent(new Point(0, 300), new Ground());
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