package org.ogin.cb.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Pin extends JComponent {
    private static final long serialVersionUID = 2800439609879647896L;

	public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    
    private boolean isIn;
    private int number;
    
    public Pin(boolean isIn, int number) {
        this.isIn = isIn;
        this.number = number;
        setPreferredSize(new Dimension(10, 10));
        setMinimumSize(getPreferredSize());
        setSize(getPreferredSize());
        setBackground(DEFAULT_BACKGROUND_COLOR);
        setOpaque(true);
        //we need something that will make our components unique for lookups
        setName(Long.toString(RandomUtils.nextLong()));
    }

    public boolean isIn() {
        return isIn;
    }

    public int getNumber() {
        return number;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * Add a component listener to our <em>parent</em> component.
     * @param listener
     */
    public void addMoveListener(ComponentListener listener) {
        ((AbstractGate)getParent()).addComponentListener(listener);
    }

    /**
     * Remove a component listener from our <em>parent</em> component.
     * @param listener
     */
    public void removeMoveListener(ComponentListener listener) {
        ((AbstractGate)getParent()).removeComponentListener(listener);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Pin)) {
            return false;
        }

        Pin other = (Pin)obj;

        return new EqualsBuilder()
        .append(isIn(), other.isIn())
        .append(getNumber(), other.getNumber())
        .append(getName(), other.getName())
        .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
        .append(isIn())
        .append(getNumber())
        .append(getName())
        .toHashCode();
    }

    @Override
    public String toString() {
        return String.format("%s[%s,%s,%d]", getClass().getSimpleName(), isIn() ? "IN": "OUT", getNumber(), getName());
    }
}