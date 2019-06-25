package org.ogin.cb.gui.components;

import java.awt.Graphics;

public class NandGate extends AbstractGate {
    private static final long serialVersionUID = 374183684419541559L;
    
    private Pin[] inPins;
    
    public NandGate() {
        super("NandGate");

        inPins = createInPins(2);

        setToolTipText("NAND");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintAnd(g, this, true);
    }
}