package org.ogin.cb.gui.components;

import java.awt.Graphics;

import org.ogin.cb.models.ORGATE;

public class OrGate extends AbstractGate {
    private static final long serialVersionUID = 7390744385679911501L;
    
    private ORGATE model;
    private Pin[] inPins;
    
    public OrGate() {
        super("OrGate");

        model = new ORGATE("Foo", 2);
        
        inPins = createInPins(2);

        setToolTipText("OR");
    }

    @Override
    protected void paintComponent(Graphics g) {
        GatePainter.paintOr(g, this, false);
    }

    public String getIdentifier() {
        return model.getIdentifier();
    }

    public int getNumberOfPins() {
        return inPins.length; //model.getNumberOfPins();
    }
}