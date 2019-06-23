package org.ogin.cb.gui.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.StringUtils;
import org.ogin.cb.gui.PropertyNames;

public class SaveAsFileAction extends AbstractAction {
    private static final long serialVersionUID = -5613801065299062965L;

    private static final String SDL_EXTENSION = "sdl";
    
    private Component parent;
    private File selectedFile;
    private JFileChooser fileChooser;
    private PropertyChangeSupport pcs;

    public SaveAsFileAction(Component parent) {
        this.parent = parent;
        putValue(NAME, "Save As...");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
        String.format("%1$s Files (*.%1$s)", SDL_EXTENSION), SDL_EXTENSION));

        pcs = new PropertyChangeSupport(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        int returnVal = fileChooser.showSaveDialog(parent);

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File fileSelectionFromDialog = getWithExtension(fileChooser.getSelectedFile());
            //prompt to overwrite if existing file
            if(acceptsOverwrite(fileSelectionFromDialog)) {
                File oldFile = selectedFile;
                selectedFile = fileSelectionFromDialog;
                pcs.firePropertyChange(PropertyNames.SaveAs.name(), oldFile, selectedFile);   
            }
        }
    }

    /**
     * ensure .SDL extension was added, else add it ourselves
     * @param file
     * @return
     */
    private File getWithExtension(File file) {
        File result = file;

        String filename = file.getName();
        if(!StringUtils.endsWith(StringUtils.lowerCase(filename), "." + SDL_EXTENSION)) {
            result = new File(file.getParentFile(), filename + '.' + SDL_EXTENSION);
        }

        return result;
    }

    /**
     * Confirms with the user that the existing file should be overwritten.
     * Only displays if the file exists. Treat new files with acceptance.
     * @param file
     * @return accepts
     */
    private boolean acceptsOverwrite(File file) {
        boolean accepts = true;

        if(file.exists()) {
            int response = JOptionPane.showConfirmDialog(parent,
            "The file " + file.getName() +
            " already exists. Do you want to replace the existing file?",
            "Ovewrite file", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

            accepts = response == JOptionPane.YES_OPTION;
        }

        return accepts;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}