package org.ogin.cb.gui.dnd;

import java.awt.Cursor;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceEvent;

/**
 * Changes the drag cursor if the drop target is valid
 */
public class PinDragSourceListener extends DragSourceAdapter {

    private Cursor originalCursor;

    @Override
    public void dragEnter(DragSourceDragEvent event) {
        //keep original cursor
        originalCursor = event.getDragSourceContext().getCursor();

        //override with new cursor to indicate that we're a valid drop target
        event.getDragSourceContext().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void dragExit(DragSourceEvent event) {
        //reset original cursor
        event.getDragSourceContext().setCursor(originalCursor);
    }
}