package org.ogin.cb.gui;

import javax.swing.JPanel;

/**
 * A simple panel without a layout manager to simplify moving components across it.
 */
public class Canvas extends JPanel {

    private static final long serialVersionUID = -7698407387574080927L;

    public Canvas() {
        init();
    }

    private void init() {
        // remove layout manager as we move components manually
        setLayout(null);
    }
}