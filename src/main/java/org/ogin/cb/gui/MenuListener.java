package org.ogin.cb.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.ogin.cb.Circuit;
import org.ogin.cb.CircuitData;
import org.ogin.cb.parser.SDLException;

public class MenuListener implements PropertyChangeListener {
    private DialogProvider dialogProvider;
    
    public MenuListener(DialogProvider dialogProvider) {
        this.dialogProvider = dialogProvider;
	}

	@Override
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO utilize load / save icons

        if (PropertyNames.Open.name() == evt.getPropertyName()) {
            openFile((File)evt.getNewValue());
        } else if(PropertyNames.Validate.name() == evt.getPropertyName()) {
            validateFile((File)evt.getNewValue());
        } else if(PropertyNames.SaveAs.name() == evt.getPropertyName()) {
            saveFile((File)evt.getNewValue());
        }
    }

    private void saveFile(File file) {
        //TODO save data to file
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