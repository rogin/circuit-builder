package org.ogin.cb.gui;

import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenuBar extends JMenuBar {
    protected MainFrame parent;
    
    protected JMenu fileMenu;
    protected JMenuItem openItem;
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
        fileMenu.addSeparator();
        exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.addActionListener((event) -> System.exit(0));
        fileMenu.add(exitItem);

        add(fileMenu);
    }
}