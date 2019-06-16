package org.ogin.cb.gui;

public interface DialogProvider {
    void notifyError(String title, String message);

    void notifyInfo(String title, String message);
}