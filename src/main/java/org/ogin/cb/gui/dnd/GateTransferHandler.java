package org.ogin.cb.gui.dnd;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.function.BiConsumer;

import javax.swing.TransferHandler;

import org.ogin.cb.gui.Drawer;
import org.ogin.cb.gui.components.AbstractGate;

/**
 * Handles gate transfers from the {@link Drawer} to the {@link Canvas}.
 * On successful drop, calls the provided callback.
 */
public class GateTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 8909797536899451637L;

    private Component target;
    private BiConsumer<Point, AbstractGate> callback;

    /**
     * Constructs a new instance for the specified component with the
     * callback to be used when a drop occurs.
     * @param target target component
     * @param callback callback for when a drop occurs
     */
    public GateTransferHandler(Component target, BiConsumer<Point, AbstractGate> callback) {
        this.target = target;
        this.callback = callback;
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
        if(!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }

        //avoid dropping onto existing child components
        if(isOverChildComponent(support)) {
            return false;
        }

        boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;
        if (copySupported) {
            support.setDropAction(COPY);
            return true;
        }

        // COPY is not supported, so reject the transfer
        return false;
    }

    private boolean isOverChildComponent(TransferSupport support) {
        Point point = support.getDropLocation().getDropPoint();

        Component compAtLocation = target.getComponentAt(point);
        return compAtLocation != null && !compAtLocation.equals(target);
    }

    public boolean importData(TransferHandler.TransferSupport info) {
        if (!info.isDrop()) {
            return false;
        }

        if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }

        String data = null;
        try {
            data = (String) info.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
            return false;
        }

        if(data != null) {
            AbstractGate gate = instantiateGate(data);

            if(gate != null) {
                callback.accept(info.getDropLocation().getDropPoint(), gate);
                return true;
            }
        }

        return false;
    }

    private AbstractGate instantiateGate(String data) {
        //TODO refactor this to be safe
        AbstractGate gate = null;

        try {
            String className = String.format("org.ogin.cb.gui.components.%sGate", data);
            gate = (AbstractGate) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return gate;
    }
}