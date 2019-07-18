package org.ogin.cb.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyboardFocusManager;
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

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.ogin.cb.CircuitData;
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

    public Canvas() {
        init();
    }

    private void init() {
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

                if(e.getKeyCode() == KeyEvent.VK_D) { debugComponents(); }
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

        debugFocus();

        createDefaultComponents();
    }

     private void debugComponents() {
        System.err.println("Dumping component locations");
        for(Component c : getComponents()) {
            System.err.println(c.getClass().getSimpleName() + " location: " + c.getLocation()); JComponent x = (JComponent)c;
            for(Component child : x.getComponents()) {
                System.err.println("\t" + child.getClass().getSimpleName() + " location: " + child.getLocation());
                //if(child instanceof Pin) {
                    //Pin pin = (Pin) child;
                    //System.err.println("Found a pin");
                //}
            }
        }
    }
    

    private void debugFocus() {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        focusManager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String properties = e.getPropertyName();
                if (("focusOwner".equals(properties)) && (e.getNewValue() != null)) {
                    Component component = (Component) e.getNewValue();
                    // String name = component.getName();

                    System.out.println(component.getClass().getSimpleName() + " took focus");
                }
            }
        });
    }

    private void attachComponent(Point point, AbstractGate gate) {
        gate.setLocation(point);

        addCommonComponent(gate, gate.isMovable());

        if (gate.isMovable()) {
            // this allows us to move the component around the canvas
            gateMover.registerComponent(gate);
            gate.requestFocusInWindow();
        }

        configureDragSource(gate.getOutPin());

        assignTransferHandler(gate.getInPins());
    }

    private void attachComponent(Wire wire) {
        addCommonComponent(wire, false);
        //wire.requestFocusInWindow();
    }

    private void addCommonComponent(JComponent component, boolean randomizeName) {
        add(component);

        component.addMouseListener(componentMouseListener);
        component.addKeyListener(componentKeyListener);

        if(randomizeName) {
            //change the name to avoid lookup errors
            component.setName(component.getName() + Long.toString(RandomUtils.nextLong()));
        }

        // very important
        component.repaint();
    }

    private void assignTransferHandler(Pin[] inPins) {
        for(Pin pin : inPins) {
            pin.setTransferHandler(pinTransferHandler);
        }
    }

    private void configureDragSource(Pin outPin) {
        DragSource source = DragSource.getDefaultDragSource();
        source.addDragSourceListener(pinDragSourceListener);
        source.addDragSourceListener(new DragSourceAdapter() {
            @Override
            public void dragDropEnd(DragSourceDropEvent event) {
                configureInPinsAfterDragEvent((Pin)event.getDragSourceContext().getComponent());
            }
        });
        DragGestureListener dragListener = new DragGestureListener() {

            @Override
            public void dragGestureRecognized(DragGestureEvent dragEvent) {
                prepareInPinBackgroundsForDragEvent((Pin)dragEvent.getComponent());
                dragEvent.startDrag(DragSource.DefaultLinkDrop, new PinSelection(PinTransferHandler.FLAVOR, outPin));
            }
        };
        source.createDefaultDragGestureRecognizer(outPin, DnDConstants.ACTION_LINK, dragListener);
    }

    private void configureInPinsAfterDragEvent(Pin outPinEventOriginator) {
        for(Pin pin : getInPins()) {
            pin.setBackground(Pin.DEFAULT_BACKGROUND_COLOR);
        }

        // all IN pins on the same gate as the OUT drag originator were marked
        // unavailable, so review and reset those that should be available.
        for(Pin pin : getGateOwningOutPin(outPinEventOriginator).getInPins()) {
            if(!isWired(pin)) {
                markInPinAvailable(pin);
            }
        }
    }

    private boolean isWired(Pin inPin) {
        for(Wire wire : getWires()) {
            if(wire.getInPin().equals(inPin)) {
                return true;
            }
        }

        return false;
    }

    private void prepareInPinBackgroundsForDragEvent(Pin outPinEventOriginator) {
        // all IN pins on the same gate as the OUT drag originator need to be unavailable
        for(Pin pin : getGateOwningOutPin(outPinEventOriginator).getInPins()) {
            markInPinUnavailable(pin);
        }

        for(Pin pin : getInPins()) {
            Color newBackground = pin.getDropTarget().isActive() ? PIN_COLOR_VALID_DROP : PIN_COLOR_INVALID_DROP;
            pin.setBackground(newBackground);
        }
    }

    private List<Pin> getInPins() {
        List<Pin> inPins = new ArrayList<>();

        for(Component c : getComponents()) {
            if(c instanceof AbstractGate) {
                AbstractGate gate = (AbstractGate)c;
                inPins.addAll(Arrays.asList(gate.getInPins()));
            }
        }

        return inPins;
    }

    private AbstractGate getGateOwningOutPin(Pin outPinToFind) {
        for(Component c : getComponents()) {
            if(c instanceof AbstractGate) {
                AbstractGate gate = (AbstractGate)c;
                if(outPinToFind.equals(gate.getOutPin())) {
                    return gate;
                }
            }
        }

        System.err.println("Failed to find gate from out pin in component tree: " + outPinToFind);
        
        return null;
    }

    /**
     * Remove the component from the canvas.
     * @param gate
     */
    private void detachComponent(AbstractGate gate) {
        gateMover.deregisterComponent(gate);

        detachCommonComponent(gate);

        List<Wire> attachedWires = getWiresAttachedToGate(gate);
        
        for(Wire wire : attachedWires) {
            detachComponent(wire);
        }
    }

    private List<Wire> getWiresAttachedToGate(AbstractGate gate) {
        List<Wire> attachedWires = new ArrayList<>();

        for(Wire wire : getWires()) {
            for(Pin pin : gate.getInPins()) {
                if(pin.equals(wire.getInPin())) {
                    attachedWires.add(wire);
                    //continue to the next wire
                    continue;
                }
            }

            if(gate.getOutPin().equals(wire.getOutPin())) {
                attachedWires.add(wire);
            }
        }

        return attachedWires;
    }

    private List<Wire> getWires() {
        List<Wire> wires = new ArrayList<>();

        for(Component c : getComponents()) {
            if(c instanceof Wire) {
                wires.add((Wire)c);
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

        //very important
        repaint();
    }

    private void handleWireCreation(Pin srcOutPin, Pin destInPin) {
        //do this as the post-serialized pin provided here does not have the
        //parent container when calling getParent() - something to do with
        //Swing that can be researched later.
        Pin locatedOutPin = findOutPin(srcOutPin);

        if(locatedOutPin != null) {
            Wire wire = new Wire(locatedOutPin, destInPin);

            attachComponent(wire);
            markInPinUnavailable(destInPin);
        }
    }

    private Pin findOutPin(Pin outPinToFind) {
        for(Component c : getComponents()) {
            if(c instanceof AbstractGate) {
                AbstractGate gate = (AbstractGate)c;
                if(outPinToFind.equals(gate.getOutPin())) {
                    return gate.getOutPin();
                }
            }
        }

        System.err.println("Failed to find pin in component tree: " + outPinToFind);
        
        return null;
    }

    public void markInPinUnavailable(Pin pin) {
        pin.getDropTarget().setActive(false);
    }
    
    public void markInPinAvailable(Pin pin) {
        pin.getDropTarget().setActive(true);
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