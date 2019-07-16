package org.ogin.cb.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.function.BiConsumer;

import javax.swing.TransferHandler;

import org.ogin.cb.gui.components.Pin;

public class PinTransferHandler extends TransferHandler {
    private static final long serialVersionUID = 5717351146136786091L;

    public static final DataFlavor FLAVOR = new DataFlavor(Pin.class, "Pin");

    private BiConsumer<Pin, Pin> callback;

    public PinTransferHandler(BiConsumer<Pin, Pin> callback) {
        this.callback = callback;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDataFlavorSupported(FLAVOR)) {
            return false;
        }

        boolean linkSupported = (LINK & support.getSourceDropActions()) == LINK;
        if (linkSupported) {
            support.setDropAction(LINK);
            return true;
        }

        // LINK is not supported, so reject the transfer
        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if(!canImport(support)) {
            return false;
        }
        
        Object data = null;
        try {
            data = support.getTransferable().getTransferData(FLAVOR);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }

        if(data != null && FLAVOR.getRepresentationClass().isInstance(data)) {
            callback.accept((Pin)data, (Pin)support.getComponent());
            return true;
        }

        return false;
    }
}