package org.ogin.cb.gui.components;

import java.awt.Graphics;

import org.ogin.cb.models.COMPONENT;
import org.ogin.cb.models.NOTGATE;

public class NotGate extends AbstractGate {
    private static final long serialVersionUID = -6463469639591589295L;

	public NotGate() {
        super("NotGate", 1);

        setToolTipText("NOT");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintNot(g, this);
    }

    @Override
    public COMPONENT asComponent() {
        return new NOTGATE(getName());
    }
}