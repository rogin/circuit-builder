package org.ogin.cb.gui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

public final class GatePainter {

	public static void paintOr(Graphics g, JComponent component, boolean includeNotIndicator) {
        drawOr((Graphics2D)g, component, includeNotIndicator, 5);
    }

    public static void paintXor(Graphics g, JComponent component) {
        Graphics2D g2 = (Graphics2D)g;
        
        final int leftOffset = 10;
        drawOr(g2, component, false, leftOffset);

        //draw the eXclusive indicator
        GeneralPath path = new GeneralPath();
        path.moveTo(5, 5);
        path.curveTo(5, 5, 20, 20, 5,  component.getHeight()-5);

        g2.draw(path);
	}
    
    private static void drawOr(Graphics2D g2, JComponent component, boolean includeNotIndicator, int leftOffset) {
        Rectangle bounds = component.getBounds();
        int xPoints[] = {leftOffset, bounds.width/2, bounds.width-5, bounds.width/2, leftOffset, leftOffset};
        int yPoints[] = {5, 5, bounds.height/2, bounds.height-5, bounds.height-5, 5};

        GeneralPath path = new GeneralPath();

        path.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < xPoints.length; i++) {
            //upper curve
            if(i == 2) {
                path.curveTo(path.getCurrentPoint().getX(), path.getCurrentPoint().getY(),
                xPoints[i], path.getCurrentPoint().getY(), xPoints[i], yPoints[i]);
            }
            //lower curve
            else if(i == 3) {
                path.curveTo(path.getCurrentPoint().getX(), path.getCurrentPoint().getY(),
                path.getCurrentPoint().getX(), yPoints[i], xPoints[i], yPoints[i]);
            }
            //return curve
            else if(i == 5) {
                //System.err.printf("final curve from %s\n", path.getCurrentPoint());
                path.curveTo(path.getCurrentPoint().getX(), path.getCurrentPoint().getY(),
                xPoints[0]+15, yPoints[0]+15, xPoints[0], yPoints[0]);
            } else {
                //System.err.println("line " + i);
                path.lineTo(xPoints[i], yPoints[i]);
            }
        }

        /*
        int xPoints[] = {5, dim.width-20, dim.width-20, 5};
        int yPoints[] = {5, 5, dim.height-5, dim.height-5};

        Point rightMid = new Point(dim.width, dim.height/2);

        GeneralPath path = new GeneralPath();

        path.moveTo(xPoints[0], yPoints[0]);
        path.lineTo(xPoints[1], yPoints[1]);
        
        path.curveTo(xPoints[1], yPoints[1], rightMid.x, rightMid.y, xPoints[2], yPoints[2]);

        path.lineTo(xPoints[3], yPoints[3]);
        path.lineTo(xPoints[0], yPoints[0]);
        */

        path.closePath();
        g2.draw(path);

        if(includeNotIndicator) {
            paintNotIndicator(g2, xPoints[2], yPoints[2]);
        }
    }

    private static final void paintNotIndicator(Graphics2D g2, int x, int y) {
        //the "NOT" circle
        final int RADIUS = 3;
        g2.drawOval(x-RADIUS, y-RADIUS, RADIUS*2, RADIUS*2);
    }

    public static void paintAnd(Graphics g, JComponent component, boolean includeNotIndicator) {
        Graphics2D g2 = (Graphics2D)g;

        Rectangle bounds = component.getBounds();
        int xPoints[] = {5, bounds.width/2, bounds.width-5, bounds.width/2, 5};
        int yPoints[] = {5, 5, bounds.height/2, bounds.height-5, bounds.height-5};

        GeneralPath path = new GeneralPath();

        path.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < xPoints.length; i++) {
            //upper curve
            if(i == 2) {
                path.curveTo(path.getCurrentPoint().getX(), path.getCurrentPoint().getY(),
                xPoints[i], path.getCurrentPoint().getY(), xPoints[i], yPoints[i]);
            }
            //lower curve
            else if(i == 3) {
                path.curveTo(path.getCurrentPoint().getX(), path.getCurrentPoint().getY(),
                path.getCurrentPoint().getX(), yPoints[i], xPoints[i], yPoints[i]);
            } else {
                path.lineTo(xPoints[i], yPoints[i]);
            }
        }

        /*
        int xPoints[] = {5, dim.width-20, dim.width-20, 5};
        int yPoints[] = {5, 5, dim.height-5, dim.height-5};

        Point rightMid = new Point(dim.width, dim.height/2);

        GeneralPath path = new GeneralPath();

        path.moveTo(xPoints[0], yPoints[0]);
        path.lineTo(xPoints[1], yPoints[1]);
        
        path.curveTo(xPoints[1], yPoints[1], rightMid.x, rightMid.y, xPoints[2], yPoints[2]);

        path.lineTo(xPoints[3], yPoints[3]);
        path.lineTo(xPoints[0], yPoints[0]);
        */

        path.closePath();
        g2.draw(path);

        if(includeNotIndicator) {
            paintNotIndicator(g2, xPoints[2], yPoints[2]);
        }
	}

	public static void paintNot(Graphics g, JComponent component) {
        Graphics2D g2 = (Graphics2D)g;

        Rectangle bounds = component.getBounds();
        
        //three tips of triangle
        Point top = new Point(5, 5);
        Point right = new Point(bounds.width -5, bounds.height / 2);
        Point bottom = new Point(5, bounds.height-5);
        
        g2.drawLine((int)top.getX(), (int)top.getY(), (int)right.getX(), (int)right.getY());
        g2.draw(new Line2D.Double(top, right));
        g2.draw(new Line2D.Double(right, bottom));
        g2.draw(new Line2D.Double(bottom, top));

        paintNotIndicator(g2, (int)right.getX(), (int)right.getY());
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