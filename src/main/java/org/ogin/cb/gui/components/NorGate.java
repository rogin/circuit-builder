package org.ogin.cb.gui.components;

import java.awt.Graphics;

import org.ogin.cb.models.COMPONENT;
import org.ogin.cb.models.NORGATE;

public class NorGate extends AbstractGate {
    private static final long serialVersionUID = -6463469639591589295L;

	public NorGate() {
        super("NorGate", 2);

        setToolTipText("NOR");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintOr(g, this, true);
    }

    @Override
    public COMPONENT asComponent() {
        return new NORGATE(getName(), getInPins().length);
    }
}