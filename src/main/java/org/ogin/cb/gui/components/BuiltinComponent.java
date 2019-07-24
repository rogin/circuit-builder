package org.ogin.cb.gui.components;

import org.ogin.cb.models.COMPONENT;
import org.ogin.cb.models.StateType;
import org.ogin.cb.models.TokenType;

public abstract class BuiltinComponent extends AbstractGate {

    private static final long serialVersionUID = -111097775264802556L;

    public BuiltinComponent(TokenType type, int inPins, int outPins) {
        super(type.name(), false, inPins, outPins);
        this.type = type;
	}

	@Override
    public COMPONENT asComponent() {
        return new COMPONENT(getName(), getTokenType(), getInPins().length){
        
            @Override
            public StateType GetPin(int integer) {
                return StateType.UNK;
            }
        };
    }
}