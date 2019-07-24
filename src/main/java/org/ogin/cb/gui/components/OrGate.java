package org.ogin.cb.gui.components;

import java.awt.Graphics;

import org.ogin.cb.models.COMPONENT;
import org.ogin.cb.models.ORGATE;

public class OrGate extends AbstractGate {
    private static final long serialVersionUID = 7390744385679911501L;
    
    public OrGate() {
        super("OrGate", 2);

        setToolTipText("OR");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintOr(g, this, false);
    }

    @Override
    public COMPONENT asComponent() {
        return new ORGATE(getName(), getInPins().length);
    }
}