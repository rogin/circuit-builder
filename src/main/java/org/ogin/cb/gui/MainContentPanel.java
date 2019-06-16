package org.ogin.cb.gui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

public class MainContentPanel extends JPanel {

    private static final long serialVersionUID = -7698407387574080927L;

    public MainContentPanel() {
        init();
    }

    private void init() {
        setLayout(null);
        setTransferHandler(new CustomTransferHandler(this));
    }

    private void createDataAtLocation(Point point, String data) {
        JLabel label = new JLabel(data);
        
        add(label);
        
        Insets insets = getInsets();
        Dimension size = label.getPreferredSize();
        label.setBounds(point.x + insets.left, point.y + insets.top, size.width, size.height);
    }

    /*
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gr = (Graphics2D)g;
        gr.drawString("Test", 100, 100);

        Shape square = new Rectangle2D.Float(100.0f, 100.0f, 100.0f, 100.0f);
        Shape circle = new Ellipse2D.Float(100.0f, 100.0f, 100.0f, 100.0f);
        gr.draw(square);
        gr.draw(circle);
    }
    */

    private class CustomTransferHandler extends TransferHandler {

        private static final long serialVersionUID = 8909797536899451637L;

        private MainContentPanel parent;

        private CustomTransferHandler(MainContentPanel parent) {
            this.parent = parent;
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
                parent.createDataAtLocation(info.getDropLocation().getDropPoint(), data);
            }

            return true;
        }
    }
}