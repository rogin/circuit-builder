package org.ogin.cb.gui.components;

public abstract class BuiltinComponent extends AbstractGate {

    private static final long serialVersionUID = -111097775264802556L;

    public BuiltinComponent(String name) {
        super(name, false);
    }

    public String getIdentifier() {
        return name;
    }

    public int getNumberOfPins() {
        return 0;
    }
}