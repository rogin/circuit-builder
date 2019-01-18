package org.ogin.cb.parser;

public class SDLException extends Exception {

	private static final long serialVersionUID = 5346897045004314745L;

    public SDLException(String message) {
        super(message);
    }

    public SDLException(String message, Throwable throwable) {
        super(message, throwable);
    }
}