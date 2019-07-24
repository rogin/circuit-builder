package org.ogin.cb.gui;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.ogin.cb.Circuit;
import org.ogin.cb.CircuitData;
import org.ogin.cb.CircuitWriter;
import org.ogin.cb.parser.SDLException;

public class MenuListener implements PropertyChangeListener {
    private Container container;
    private Supplier<CircuitData> dataSupplier;

    public MenuListener(Container container, Supplier<CircuitData> dataSupplier) {
        this.container = container;
        this.dataSupplier = dataSupplier;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO utilize load / save icons

        if(StringUtils.equals(evt.getPropertyName(), PropertyNames.Open.name())) {
            openFile((File) evt.getNewValue());
        } else if(StringUtils.equals(evt.getPropertyName(), PropertyNames.Validate.name())) {
            validateFile((File) evt.getNewValue());
        } else if(StringUtils.equals(evt.getPropertyName(), PropertyNames.SaveAs.name())) {
            saveFile((File) evt.getNewValue());
        }
    }

    private void saveFile(File file) {
        CircuitData data = dataSupplier.get();

        boolean success = false;
        try {
            CircuitWriter.write(data, file.getAbsolutePath());
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            notifyError("Save failed", e.getMessage());
        }

        if(success) {
            notifyInfo("Success", "Data written.");
        }
    }

    private void openFile(File file) {
        SwingWorker<CircuitData, Void> worker = new SwingWorker<CircuitData, Void>() {

            @Override
            protected CircuitData doInBackground() throws SDLException {
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
                    notifyError("Error opening file", "Open operation was interrupted. Please try again.");
                } catch (ExecutionException e) {
                    //TODO show full stack trace
                    notifyError("Error opening file", e.getMessage());
                }

                if(data != null) {
                    notifyInfo("Open file", "The file was read successfully, but the parsing logic is currently not implemented.");
                }
            }
        };

        worker.run();
    }

    private void validateFile(File file) {
        SwingWorker<CircuitData, Void> worker = new SwingWorker<CircuitData, Void>() {

            @Override
            protected CircuitData doInBackground() throws SDLException {
                Circuit c = new Circuit();
                
                c.load(file);

                return c.getData();
            }

            @Override
            protected void done() {
                CircuitData data = null;

                try {
                    data = get();
                } catch (InterruptedException e) {
                    notifyError("Error opening file", "Validation operation was interrupted. Please try again.");
                } catch (ExecutionException e) {
                    //TODO show full stack trace?
                    notifyError("Validation failed", e.getMessage());

                }

                if(data != null) {
                    notifyInfo("Success", "File has been validated");
                }
            }
        };

        worker.run();
    }

    private void notifyError(String title, String message) {
        JOptionPane.showMessageDialog(container, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void notifyInfo(String title, String message) {
        JOptionPane.showMessageDialog(container, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}