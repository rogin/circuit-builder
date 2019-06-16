package org.ogin.cb.gui;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import org.ogin.cb.gui.menu.*;

public class MainFrame extends JFrame implements DialogProvider {

    private static final long serialVersionUID = 7546234560603703591L;

    //private MainContentPanel contentPanel;
    private MainMenuBar menuBar;
    private OpenFileAction openFileAction;
    private SaveAsFileAction saveAsFileAction;
    private ValidateFileAction validateFileAction;
    private MenuListener menuListener;

    public MainFrame() {
        super("Circuit Builder");
        init();
    }

    private void init() {
        createContentPanel();
        createFileActions();
        createMenuBar();
        attachPropertyChangeListeners();

        setSize(800, 600);
        setLocationByPlatform(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createContentPanel() {
        //contentPanel = new MainContentPanel();
        //setContentPane(contentPanel);
        
        JSplitPane s = new JSplitPane();
        s.setLeftComponent(new Drawer());
        s.setRightComponent(new MainContentPanel());
        setContentPane(s);
    }

    private void createFileActions() {
        openFileAction = new OpenFileAction(this);
        validateFileAction = new ValidateFileAction(this);
        saveAsFileAction = new SaveAsFileAction(this);
    }

    private void createMenuBar() {
        menuBar = new MainMenuBar(this);
        setJMenuBar(menuBar);
    }

    private void attachPropertyChangeListeners() {
        menuListener = new MenuListener(this);
        openFileAction.addPropertyChangeListener(menuListener);
        saveAsFileAction.addPropertyChangeListener(menuListener);
        validateFileAction.addPropertyChangeListener(menuListener);
    }

    private void detachPropertyChangeListeners() {
        openFileAction.removePropertyChangeListener(menuListener);
        saveAsFileAction.removePropertyChangeListener(menuListener);
        validateFileAction.removePropertyChangeListener(menuListener);
    }

    public Action getOpenFileAction() {
        return openFileAction;
    }

    public Action getSaveAsFileAction() {
        return saveAsFileAction;
    }

	public Action getValidateFileAction() {
		return validateFileAction;
    }
    
    @Override
    public void dispose() {
        detachPropertyChangeListeners();
        super.dispose();
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