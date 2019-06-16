package org.ogin.cb.gui.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ogin.cb.gui.PropertyNames;

public class OpenFileAction extends AbstractAction {
    private static final long serialVersionUID = -679323188064845185L;

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
            pcs.firePropertyChange(PropertyNames.Open.name(), oldFile, selectedFile);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}