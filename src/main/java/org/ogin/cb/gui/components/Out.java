package org.ogin.cb.gui.components;

import org.ogin.cb.models.TokenType;

public class Out extends BuiltinComponent {

    private static final long serialVersionUID = -52807812672236350L;

    /**
     * Create a new instance with a single IN pin
     */
    public Out() {
        this(1);
    }

    /**
     * Create a new instance with the specified number of IN pins
     * @param inPins
     */
    public Out(int inPins) {
        super(TokenType.OUT, inPins, 0);
    }
}