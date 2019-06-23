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
        
        inPins = createInPins(2, 3);

        setToolTipText("OR");
    }

    /*
    private Pin createInPinAt(int sections, int sectionSelection){
        Pin pin = new Pin(true);

        Dimension dims = pin.getSize();
        Point location = new Point(0, 0);
        double heightSection = (getBounds().getHeight()/sections) * sectionSelection;
        
        location.y = (int)(heightSection - (dims.getHeight()/2));

        attachPin(pin, location);

        return pin;
    }
    */

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