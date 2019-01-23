package org.ogin.cb.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.ogin.cb.Circuit;
import org.ogin.cb.CircuitData;
import org.ogin.cb.parser.SDLException;

public class MainContentPanel extends JPanel implements PropertyChangeListener {

    public MainContentPanel() {
        super(new BorderLayout());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        JPanel parent = this;

        if (StringUtils.equals(OpenFileAction.SELECTED_FILE_CHANGED_PROPERTY, evt.getPropertyName())) {
            // TODO show load icon
            SwingWorker<CircuitData, Void> worker = new SwingWorker<CircuitData, Void>() {

                @Override
                protected CircuitData doInBackground() throws SDLException {
                    File file = (File) evt.getNewValue();
                    Circuit c = new Circuit();
                    
                    c.load(file);

                    return c.getData();
                }

                @Override
                protected void done() {
                    // TODO remove load icon
                    CircuitData data = null;

                    try {
                        data = get();
                    } catch (InterruptedException e) {
                        JOptionPane.showMessageDialog(parent, "Open operation was interrupted. Please try again.", "Error opening file", JOptionPane.ERROR_MESSAGE);
                    } catch (ExecutionException e) {
                        //TODO show full stack trace
                        JOptionPane.showMessageDialog(parent, e.getMessage(), "Error opening file", JOptionPane.ERROR_MESSAGE);

                    }

                    if(data != null) {
                        //complete
                    }
                }
            };

            worker.run();
        }
    }
}