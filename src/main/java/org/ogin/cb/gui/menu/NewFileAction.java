package org.ogin.cb.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import javax.swing.AbstractAction;

import org.ogin.cb.gui.PropertyNames;

public class NewFileAction extends AbstractAction {

    private static final long serialVersionUID = 5086707065320378275L;
    
    private Date oldValue;
    private PropertyChangeSupport pcs;

    public NewFileAction() {
        putValue(NAME, "New");
        putValue(MNEMONIC_KEY, KeyEvent.VK_N);

        pcs = new PropertyChangeSupport(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Date now = new Date();
        PropertyChangeEvent event = new PropertyChangeEvent(this, PropertyNames.New.name(), oldValue, now);
        oldValue = now;
        pcs.firePropertyChange(event);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}