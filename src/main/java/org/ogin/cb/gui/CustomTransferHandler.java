package org.ogin.cb.gui;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.function.BiConsumer;

import javax.swing.TransferHandler;

import org.ogin.cb.gui.components.AbstractGate;

public class CustomTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 8909797536899451637L;

    private BiConsumer<Point, AbstractGate> callback;

    public CustomTransferHandler(BiConsumer<Point, AbstractGate> callback) {
        this.callback = callback;
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
        if(!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
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
            //consider logging
            return false;
        }

        if(data != null) {
            AbstractGate gate = instantiateGate(data);

            if(gate != null) {
                callback.accept(info.getDropLocation().getDropPoint(), gate);
            }
        }

        return true;
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