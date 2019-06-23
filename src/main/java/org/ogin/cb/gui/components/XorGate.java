package org.ogin.cb.gui.components;

import java.awt.Graphics;

public class XorGate extends AbstractGate {

	private static final long serialVersionUID = -7897220222220607339L;

    public XorGate() {
        super("XorGate");

        createInPins(2, 3);

        setToolTipText("XOR");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintXor(g, this);
    }
}