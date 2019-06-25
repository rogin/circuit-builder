package org.ogin.cb.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.ogin.cb.CircuitData;
import org.ogin.cb.gui.components.*;

public class Canvas extends JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = -7698407387574080927L;

    private ComponentMover componentMover;

    private KeyListener componentKeyListener;

    private MouseListener componentMouseListener;

    public Canvas() {
        init();
    }

    private void init() {
        // remove layout manager as we move components manually
        setLayout(null);

        //delete component when Delete or BackSpace pressed
        componentKeyListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if(e.getComponent() instanceof AbstractGate) {
                        detachComponent((AbstractGate)e.getComponent());
                    }
                }
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

        setTransferHandler(new CustomTransferHandler(this, this::attachComponent));

        debugFocus();

        createDefaultComponents();
    }

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

        add(gate);

        gate.addMouseListener(componentMouseListener);
        gate.addKeyListener(componentKeyListener);

        if(gate.isMovable()) {
            //this allows us to move the component around the canvas
            componentMover.registerComponent(gate);
            gate.requestFocusInWindow();
        }
    }

    /**
     * Remove the component from the canvas.
     * @param gate
     */
    private void detachComponent(AbstractGate gate) {
        gate.removeKeyListener(componentKeyListener);
        componentMover.deregisterComponent(gate);
        gate.removeMouseListener(componentMouseListener);

        //TODO detach connected wires

        remove(gate);

        //very important
        repaint();
    }

    public CircuitData getModelData() {
        CircuitData data = new CircuitData();

        for(Component c : getComponents()) {
            if(c instanceof AbstractGate) {
                //TODO extract necessary data
                AbstractGate gate = (AbstractGate)c;

                //avoid builtin types?
                if(gate instanceof BuiltinComponent) {
                    continue;
                }
            }
        }

        return data;
    }

    private void clearCanvas() {
        for(Component c : getComponents()) {
            if(c instanceof AbstractGate) {
                detachComponent((AbstractGate)c);
            } else {
                //TODO implement
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
        if(evt.getPropertyName() == PropertyNames.New.name()) {
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

    /*
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gr = (Graphics2D)g;
        gr.drawString("Test", 100, 100);

        Shape square = new Rectangle2D.Float(100.0f, 100.0f, 100.0f, 100.0f);
        Shape circle = new Ellipse2D.Float(100.0f, 100.0f, 100.0f, 100.0f);
        gr.draw(square);
        gr.draw(circle);
    }
    */
}