package org.ogin.cb.gui.components;

import java.awt.Graphics;

import org.ogin.cb.models.COMPONENT;
import org.ogin.cb.models.NANDGATE;

public class NandGate extends AbstractGate {
    private static final long serialVersionUID = 374183684419541559L;
    
    public NandGate() {
        super("NandGate", 2);

        setToolTipText("NAND");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintAnd(g, this, true);
    }

    @Override
    public COMPONENT asComponent() {
        return new NANDGATE(getName(), getInPins().length);
    }
}