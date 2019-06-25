package org.ogin.cb.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.ogin.cb.Circuit;
import org.ogin.cb.CircuitData;
import org.ogin.cb.CircuitWriter;
import org.ogin.cb.parser.SDLException;

public class MenuListener implements PropertyChangeListener {
    private DialogProvider dialogProvider;
    private Supplier<CircuitData> dataSupplier;

    public MenuListener(DialogProvider dialogProvider, Supplier<CircuitData> dataSupplier) {
        this.dialogProvider = dialogProvider;
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
            dialogProvider.notifyError("Save failed", e.getMessage());
        }

        if(success) {
            dialogProvider.notifyInfo("Success", "Data written.");
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
                    dialogProvider.notifyError("Error opening file", "Open operation was interrupted. Please try again.");
                } catch (ExecutionException e) {
                    //TODO show full stack trace
                    dialogProvider.notifyError("Error opening file", e.getMessage());
                }

                if(data != null) {
                    //complete
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
                    dialogProvider.notifyError("Error opening file", "Validation operation was interrupted. Please try again.");
                } catch (ExecutionException e) {
                    //TODO show full stack trace?
                    dialogProvider.notifyError("Validation failed", e.getMessage());

                }

                if(data != null) {
                    dialogProvider.notifyInfo("Success", "File has been validated");
                }
            }
        };

        worker.run();
    }
}