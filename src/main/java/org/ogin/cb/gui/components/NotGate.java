package org.ogin.cb.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class NotGate extends AbstractGate {
    private static final long serialVersionUID = -6463469639591589295L;

	public NotGate() {
        super("NotGate");

        createInPins(1, 2);

        setToolTipText("NOT");
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        Dimension dim = getPreferredSize();
        
        //three tips of triangle
        Point2D top = new Point(5, 5);
        Point2D right = new Point(dim.width -5, dim.height / 2);
        Point2D bottom = new Point(5, dim.height-5);
        
        g2.drawLine((int)top.getX(), (int)top.getY(), (int)right.getX(), (int)right.getY());
        g2.draw(new Line2D.Float(top, right));
        g2.draw(new Line2D.Float(right, bottom));
        g2.draw(new Line2D.Float(bottom, top));

        //the "NOT" circle
        g2.drawOval((int)right.getX()-3, (int)right.getY()-3, 6, 6);
        //Shape round = new RoundRectangle2D.Float((int)right.getX()-3, (int)right.getY()-3, 6, 6, 5, 25);
        //g2.draw(round);

        /*
        //we can also draw a path
        int xPoints[] = {5, dim.width-5, 5};
        int yPoints[] = {5, dim.height/2, dim.height-5};

        GeneralPath triangle = new GeneralPath();

        triangle.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < xPoints.length; i++) {
            triangle.lineTo(xPoints[i], yPoints[i]);
        }
        triangle.closePath();
        g2.draw(triangle);
        */
    }
}