package org.ogin.cb.gui.components;

import java.awt.Graphics;

public class NorGate extends AbstractGate {
    private static final long serialVersionUID = -6463469639591589295L;

	public NorGate() {
        super("NorGate");

        createInPins(2);

        setToolTipText("NOR");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintOr(g, this, true);
    }
}