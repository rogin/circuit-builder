package org.ogin.cb.gui.components;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.ogin.cb.models.COMPONENT;
import org.ogin.cb.models.TokenType;

public abstract class AbstractGate extends JComponent {
    private static final long serialVersionUID = -2171942032839935522L;

    private static final Border DEFAULT_BORDER = BorderFactory.createEtchedBorder();
    private static final Border FOCUSED_BORDER = BorderFactory.createEtchedBorder(Color.blue, Color.blue);

    protected boolean movable;
    protected TokenType type = TokenType.UNKNOWN;

    protected Pin[] inPins;
    protected Pin[] outPins;

    public AbstractGate(String name) {
        this(name, true);
    }

    public AbstractGate(String name, boolean movable) {
        this(name, movable, 0);
    }

    public AbstractGate(String name, int inPins) {
        this(name, true, inPins);
    }

    public AbstractGate(String name, boolean movable, int inPins) {
        this(name, true, inPins, 1);
    }

    public AbstractGate(String name, boolean movable, int inPins, int outPins) {
        setName(name);
        this.movable = movable;

        init(inPins, outPins);
    }

    private void init(int inPinCount, int outPinCount) {
        setPreferredSize(new Dimension(50, 50));
        setMinimumSize(getPreferredSize());
        setSize(getPreferredSize());
        setToolTipText(getName());
        setDefaultBorder();
        // listen for focus events
        enableEvents(AWTEvent.FOCUS_EVENT_MASK);
        createInPins(inPinCount);
        createOutPins(outPinCount);
    }

    public abstract COMPONENT asComponent();

    private void createOutPins(int count) {
        outPins = new Pin[count];

        // split our height into specified # of sections
        int sectionHeight = getHeight() / (count + 1);

        final int IN_PIN_COUNT = getInPins().length;

        Point location = new Point(0, 0);

        for (int idx = 0; idx < count; idx++) {
            Pin pin = new Pin(false, idx+1+IN_PIN_COUNT);

            // 1-based counting
            int sectionStart = sectionHeight * (idx+1);

            location.x = getWidth() - pin.getWidth();
            
            // subtract half of pin's size so its middle touches our boundary line
            location.y = sectionStart - (pin.getHeight() / 2);

            attachPin(pin, location);

            outPins[idx] = pin;
        }
    }

    private void createInPins(int count) {
        inPins = new Pin[count];

        // split our height into specified # of sections
        int sectionHeight = getHeight() / (count + 1);

        // we can reuse this; only the Y will change
        Point location = new Point(0, 0);

        for (int idx = 0; idx < count; idx++) {
            Pin pin = new Pin(true, idx+1);

            // 1-based counting
            int sectionStart = sectionHeight * (idx+1);

            // subtract half of pin's size so its middle touches our boundary line
            location.y = sectionStart - (pin.getHeight() / 2);

            attachPin(pin, location);

            inPins[idx] = pin;
        }
    }

    private void attachPin(Pin pin, Point location) {
        pin.setLocation(location);
        add(pin);
    }

    private void setDefaultBorder() {
        setBorder(DEFAULT_BORDER);
    }

    private void setFocusedBorder() {
        setBorder(FOCUSED_BORDER);
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
            setFocusedBorder();
        } else if (e.getID() == FocusEvent.FOCUS_LOST) {
            setDefaultBorder();
        }

        super.processFocusEvent(e);
    }

    /**
     * Default paint which outputs the "name" field. Not recommended to call from
     * child overrides.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(new Font("Dialog", Font.BOLD, 10));
        g.drawString(getName(), 2, 15);
    }

    public boolean isMovable() {
        return movable;
    }

    public Pin[] getOutPins() {
        return outPins;
    }

    public Pin[] getInPins() {
        return inPins;
    }

    public TokenType getTokenType() {
		return type;
	}

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AbstractGate)) {
            return false;
        }
        
        AbstractGate other = (AbstractGate)obj;

        return new EqualsBuilder()
        .append(isMovable(), other.isMovable())
        .append(getName(), other.getName())
        .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
        .append(isMovable())
        .append(getName())
        .toHashCode();
    }
}