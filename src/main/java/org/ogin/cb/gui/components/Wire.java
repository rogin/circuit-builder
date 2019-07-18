package org.ogin.cb.gui.components;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Wire extends JComponent {

    private static final long serialVersionUID = -8784359443576588198L;
    
    private static final Color DEFAULT_LINE_COLOR = Color.BLACK;
    private static final Color FOCUSED_LINE_COLOR = Color.BLUE;

	private Pin out;
    private Pin in;
    private boolean drawAsIncline;
    private Color lineColor;
    private ComponentAdapter moveListener;

    public Wire(Pin outPin, Pin inPin) {
        this.out = outPin;
        this.in = inPin;
        calculateBounds();
        setDefaultLineColor();
        // listen for focus events
        enableEvents(AWTEvent.FOCUS_EVENT_MASK);
        attachMovementListener();
    }

    private void attachMovementListener() {
        //listen for movement actions on the pins
        moveListener = new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                calculateBounds();
                repaint();
            }
        };
        out.addMoveListener(moveListener);
        in.addMoveListener(moveListener);
    }

    private void calculateBounds() {
        Rectangle newBounds = new Rectangle();
        drawAsIncline = computePinBounds(newBounds);

        setMinimumSize(new Dimension(5, 5));
        setLocation(newBounds.getLocation());
        setPreferredSize(newBounds.getSize());
        setSize(getPreferredSize());
    }

    /**
     * Given two pins, compute the bounds for a wire relative to the canvas.
     * Also determine the direction the line should be drawn (incline vs decline).
     */
    private boolean computePinBounds(Rectangle boundsHolder) {
        Rectangle outBounds = ComponentUtil.getBoundsRelativeToCanvas(out);
        Rectangle inBounds = ComponentUtil.getBoundsRelativeToCanvas(in);
        
        Rectangle.union(outBounds, inBounds, boundsHolder);

        //System.err.println("computed bounds: " + boundsHolder);

        /*
        //we desire to have the edges be at the center of the pins

        ***THIS LOOKS NICE, but there's a chance our straight lines will appear invisible***

        //get bounds of either pin as they're the same size
        Rectangle rect = outBounds.getBounds();

        //offset the start of the bounds by add half the width and height of a pin
        boundsHolder.x += rect.width/2;
        boundsHolder.y += rect.height/2;

        //subtract two halves from the total width & height (a half for each pin)
        boundsHolder.width -= rect.width;
        boundsHolder.height -= rect.height;

        System.err.println("computed bounds after centering: " + boundsHolder);
        */

        //determine if we should draw a line as an incline (lower left to upper right)
        //or a decline (upper left to lower right) based on the pins' positions on the canvas
        return (outBounds.x < inBounds.x && outBounds.y > inBounds.y) || (outBounds.x > inBounds.x && outBounds.y < inBounds.y);
    }
    
    private void setDefaultLineColor() {
        lineColor = DEFAULT_LINE_COLOR;
    }

    private void setFocusedLineColor() {
        lineColor = FOCUSED_LINE_COLOR;
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_CLICKED) {
            requestFocusInWindow();
            e.consume();
        }
        super.processMouseEvent(e);
    }

    @Override
    protected void processFocusEvent(FocusEvent e) {
        if (e.getID() == FocusEvent.FOCUS_GAINED) {
            setFocusedLineColor();
            repaint();
        } else if (e.getID() == FocusEvent.FOCUS_LOST) {
            setDefaultLineColor();
            repaint();
        }

        super.processFocusEvent(e);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(lineColor);

        Rectangle bounds = getBounds();

        if(drawAsIncline) {
            //lower left to upper right
            g.drawLine(0, bounds.height, bounds.width, 0);
        } else {
            //upper left to lower right
            g.drawLine(0, 0, bounds.width, bounds.height);
        }
    }

    public Pin getOutPin() {
		return out;
	}

	public Pin getInPin() {
		return in;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Wire)) {
            return false;
        }

        Wire other = (Wire)obj;

        return new EqualsBuilder()
        .append(getOutPin(), other.getOutPin())
        .append(getInPin(), other.getInPin())
        .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
        .append(getOutPin())
        .append(getInPin())
        .toHashCode();
    }

	public void removeListeners() {
        out.removeMoveListener(moveListener);
        in.removeMoveListener(moveListener);
	}
}