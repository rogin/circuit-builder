package org.ogin.cb.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class OpenFileAction extends AbstractAction {
    public static final String SELECTED_FILE_CHANGED_PROPERTY = "selectedFile";
    
    private Component parent;
    private File selectedFile;
    private JFileChooser fileChooser;
    private PropertyChangeSupport pcs;

    public OpenFileAction(Component parent) {
        this.parent = parent;
        putValue(NAME, "Open...");
        putValue(MNEMONIC_KEY, KeyEvent.VK_O);

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("SDL File", "sdl"));

        pcs = new PropertyChangeSupport(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        int returnVal = fileChooser.showOpenDialog(parent);

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File oldFile = selectedFile;
            selectedFile = fileChooser.getSelectedFile();
            pcs.firePropertyChange(SELECTED_FILE_CHANGED_PROPERTY, oldFile, selectedFile);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}