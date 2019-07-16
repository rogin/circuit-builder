package org.ogin.cb.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import org.ogin.cb.gui.components.Pin;

public class PinSelection implements Transferable {
    private DataFlavor dataFlavor;
    private Pin data;
    
    public PinSelection(DataFlavor dataFlavor, Pin data) {
        this.dataFlavor = dataFlavor;
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {dataFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(dataFlavor);
    }

    @Override
    public Pin getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if(!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }

        return data;
    }
}