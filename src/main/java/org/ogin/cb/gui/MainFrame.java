package org.ogin.cb.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

public class MainFrame extends JFrame implements DialogProvider {

    private static final long serialVersionUID = 7546234560603703591L;

    private MainMenuBar menuBar;
    private CircuitExporter exporter;
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
        
        exporter = new CircuitExporter();
        
        canvas = new Canvas();
        canvas.addContainerListener(exporter);
        canvas.init();

        splitPane.setRightComponent(canvas);
        setContentPane(splitPane);
    }

    private void createMenuBar() {
        menuBar = new MainMenuBar(this);
        setJMenuBar(menuBar);
    }

    private void attachPropertyChangeListeners() {
        MenuListener menuListener = new MenuListener(this, exporter::getModelData);
        menuBar.getNewFileAction().addPropertyChangeListener(canvas);
        menuBar.getOpenFileAction().addPropertyChangeListener(menuListener);
        menuBar.getSaveAsFileAction().addPropertyChangeListener(menuListener);
        menuBar.getValidateFileAction().addPropertyChangeListener(menuListener);
    }

    @Override
    public void notifyError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void notifyInfo(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}