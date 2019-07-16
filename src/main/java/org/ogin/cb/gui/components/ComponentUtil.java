package org.ogin.cb.gui.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;

import org.ogin.cb.gui.Canvas;
/**
 * Assists with generating bounds from pin data.
 */
public class ComponentUtil {
    /**
     * For a given component, calculate its bounds relative to its {@link Canvas} ancestor.
     */
    public static Rectangle getBoundsRelativeToCanvas(Component c) {
        Rectangle bounds = c.getBounds();

        addLocation(c.getParent(), bounds);

        return bounds;
    }

    private static void addLocation(Container container, Rectangle bounds) {
        if(container != null && !(container instanceof Canvas)) {
            
            Point loc = container.getLocation();
            bounds.x += loc.x;
            bounds.y += loc.y;

            addLocation(container.getParent(), bounds);
        }
    }
}