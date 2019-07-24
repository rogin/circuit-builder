package org.ogin.cb.gui.components;

import org.ogin.cb.models.TokenType;

public class In extends BuiltinComponent {
    
    private static final long serialVersionUID = -4177424487465814566L;

    /**
     * Create with 1 out pin
     */
    public In() {
        this(1);
    }

    public In(int outPins) {
        super(TokenType.IN, 0, outPins);
    }
}