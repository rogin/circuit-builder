package org.ogin.cb.gui;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 7546234560603703591L;

    private MainMenuBar menuBar;
    private CircuitModel model;
    private CircuitController controller;
    private Canvas canvas;

    public MainFrame() {
        super("Circuit Builder");
        init();
    }

    private void init() {
        createContentPanel();
        createMenuBar();
        attachPropertyChangeListeners();

        setSize(800, 600);
        setLocationByPlatform(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createContentPanel() {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(new Drawer());

        model = new CircuitModel();
        canvas = new Canvas();

        controller = new CircuitController(model, canvas);

        splitPane.setRightComponent(canvas);
        setContentPane(splitPane);
    }

    private void createMenuBar() {
        menuBar = new MainMenuBar(this);
        setJMenuBar(menuBar);
    }

    private void attachPropertyChangeListeners() {
        MenuListener menuListener = new MenuListener(this, model::getExportData);
        menuBar.getNewFileAction().addPropertyChangeListener(controller);
        menuBar.getOpenFileAction().addPropertyChangeListener(menuListener);
        menuBar.getSaveAsFileAction().addPropertyChangeListener(menuListener);
        menuBar.getValidateFileAction().addPropertyChangeListener(menuListener);
    }
}