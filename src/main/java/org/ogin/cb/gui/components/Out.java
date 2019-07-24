package org.ogin.cb.gui.components;

import org.ogin.cb.models.TokenType;

public class Out extends BuiltinComponent {

    private static final long serialVersionUID = -52807812672236350L;

    /**
     * Create with 1 in pin
     */
    public Out() {
        this(1);
    }

    public Out(int inPins) {
        super(TokenType.OUT, inPins, 0);
    }
}