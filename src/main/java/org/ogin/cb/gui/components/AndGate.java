package org.ogin.cb.gui.components;

import java.awt.Graphics;

import org.ogin.cb.models.ANDGATE;
import org.ogin.cb.models.COMPONENT;

public class AndGate extends AbstractGate {
    private static final long serialVersionUID = 6500137755893899975L;
    
    public AndGate() {
        super("AndGate", 2);

        setToolTipText("AND");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintAnd(g, this, false);
    }

    @Override
    public COMPONENT asComponent() {
        return new ANDGATE(getName(), getInPins().length);
    }
}