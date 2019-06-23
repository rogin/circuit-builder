package org.ogin.cb.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

public class Pin extends JComponent {
    private boolean isIn;
    
    public Pin(boolean isIn) {
        this.isIn = isIn;
        setPreferredSize(new Dimension(10, 10));
        setMinimumSize(getPreferredSize());
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        setOpaque(true);
    }

    public boolean isIn() {
        return isIn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}