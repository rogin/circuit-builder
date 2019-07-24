package org.ogin.cb.gui.components;

import org.ogin.cb.models.TokenType;

public class In extends BuiltinComponent {
    
    private static final long serialVersionUID = -4177424487465814566L;

    /**
     * Create a new instance with a single OUT pin
     */
    public In() {
        this(1);
    }

    /**
     * Create a new instance with the specified number of OUT pins
     * @param outPins
     */
    public In(int outPins) {
        super(TokenType.IN, 0, outPins);
    }
}