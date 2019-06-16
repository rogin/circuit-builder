package org.ogin.cb.gui;

import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenuBar extends JMenuBar {
    private static final long serialVersionUID = -7399158819249556079L;

    protected MainFrame parent;
    
    protected JMenu fileMenu;
    protected JMenuItem openItem;
    protected JMenuItem saveAsItem;
    protected JMenuItem validateItem;
    protected JMenuItem exitItem;

    public MainMenuBar(MainFrame parent) {
        this.parent = parent;

        init();
    }
    private void init() {
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        openItem = new JMenuItem();
        openItem.setAction(parent.getOpenFileAction());
        fileMenu.add(openItem);
        saveAsItem = new JMenuItem();
        saveAsItem.setAction(parent.getSaveAsFileAction());
        fileMenu.add(saveAsItem);
        validateItem = new JMenuItem();
        validateItem.setAction(parent.getValidateFileAction());
        fileMenu.add(validateItem);
        fileMenu.addSeparator();
        exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        //TODO verify exit when unsaved data is present
        exitItem.addActionListener((event) -> System.exit(0));
        fileMenu.add(exitItem);

        add(fileMenu);
    }
}