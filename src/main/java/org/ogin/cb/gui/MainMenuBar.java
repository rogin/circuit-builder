package org.ogin.cb.gui;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.ogin.cb.gui.menu.*;

public class MainMenuBar extends JMenuBar {
    private static final long serialVersionUID = -7399158819249556079L;

    protected MainFrame parent;
    
    protected JMenu fileMenu;
    protected JMenuItem newItem;
    protected JMenuItem openItem;
    protected JMenuItem saveAsItem;
    protected JMenuItem validateItem;
    protected JMenuItem exitItem;

    private NewFileAction newFileAction;
    private OpenFileAction openFileAction;
    private SaveAsFileAction saveAsFileAction;
    private ValidateFileAction validateFileAction;

    public MainMenuBar(MainFrame parent) {
        this.parent = parent;

        init();
    }
    private void init() {
        createFileActions();

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        newItem = new JMenuItem();
        newItem.setAction(newFileAction);
        fileMenu.add(newItem);
        
        openItem = new JMenuItem();
        openItem.setAction(openFileAction);
        fileMenu.add(openItem);
        
        saveAsItem = new JMenuItem();
        saveAsItem.setAction(saveAsFileAction);
        fileMenu.add(saveAsItem);
        
        validateItem = new JMenuItem();
        validateItem.setAction(validateFileAction);
        fileMenu.add(validateItem);
        
        fileMenu.addSeparator();
        
        exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        //TODO verify exit when unsaved data is present
        exitItem.addActionListener((event) -> System.exit(0));
        fileMenu.add(exitItem);

        add(fileMenu);
    }

    private void createFileActions() {
        newFileAction = new NewFileAction();
        openFileAction = new OpenFileAction(parent);
        validateFileAction = new ValidateFileAction(parent);
        saveAsFileAction = new SaveAsFileAction(parent);
    }

    public Action getNewFileAction() {
        return newFileAction;
    }

    public Action getOpenFileAction() {
        return openFileAction;
    }

    public Action getValidateFileAction() {
        return validateFileAction;
    }

    public Action getSaveAsFileAction() {
        return saveAsFileAction;
    }
}