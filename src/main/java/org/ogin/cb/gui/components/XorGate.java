package org.ogin.cb.gui.components;

import java.awt.Graphics;

import org.ogin.cb.models.COMPONENT;
import org.ogin.cb.models.XORGATE;

public class XorGate extends AbstractGate {

	private static final long serialVersionUID = -7897220222220607339L;

    public XorGate() {
        super("XorGate", 2);

        setToolTipText("XOR");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintXor(g, this);
    }

    @Override
    public COMPONENT asComponent() {
        return new XORGATE(getName(), getInPins().length);
    }
}