package org.ogin.cb.gui;

import javax.swing.Action;
import javax.swing.JFrame;

public class MainFrame extends JFrame {

    private MainContentPanel contentPanel;
    private MainMenuBar menuBar;
    private OpenFileAction openFileAction;

    public MainFrame() {
        super("Circuit Builder");
        init();
    }

    private void init() {
        createFileActions();
        createContentPanel();
        createMenuBar();
        
        openFileAction.addPropertyChangeListener(contentPanel);

        setSize(800, 600);
        setLocationByPlatform(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createMenuBar() {
        menuBar = new MainMenuBar(this);
        setJMenuBar(menuBar);
    }

    private void createContentPanel() {
        contentPanel = new MainContentPanel();
        setContentPane(contentPanel);
    }

    private void createFileActions() {
        openFileAction = new OpenFileAction(this);
    }

    public Action getOpenFileAction() {
        return openFileAction;
    }
}