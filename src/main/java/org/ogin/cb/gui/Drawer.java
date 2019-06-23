package org.ogin.cb.gui;

import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 * Displays a list of elements which can be dragged onto a canvas.
 */
public class Drawer extends JPanel {

    private static final long serialVersionUID = -6861319042385297815L;

    public Drawer() {
        init();
    }

    private void init() {
        JList<String> list = new JList<String>(new String[] {"Or", "Nor", "Xor", "And", "Nand", "Not"});
        list.setDragEnabled(true);
        list.setDropMode(DropMode.ON);
        add(list);
    }
}