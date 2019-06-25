package org.ogin.cb.gui.components;

import java.awt.Graphics;

public class AndGate extends AbstractGate {
    private static final long serialVersionUID = 6500137755893899975L;
    
    private Pin[] inPins;
    
    public AndGate() {
        super("AndGate");

        inPins = createInPins(2);

        setToolTipText("AND");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintAnd(g, this, false);
    }
}