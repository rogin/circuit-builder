package org.ogin.cb;

import java.util.Collections;
import java.util.List;

import org.ogin.cb.models.ALIAS;
import org.ogin.cb.models.COMPONENT;
import org.ogin.cb.models.CONNECTION;

public class CircuitData {
    private List<COMPONENT> components = Collections.emptyList();
    private List<CONNECTION> connections = Collections.emptyList();
    private List<ALIAS> aliases = Collections.emptyList();

    public CircuitData() {}

    public CircuitData(List<COMPONENT> components, List<CONNECTION> connections) {
        setComponents(components);
        setConnections(connections);
    }

    /**
     * @return the components
     */
    public List<COMPONENT> getComponents() {
        return components;
    }

    /**
     * @return the aliases
     */
    public List<ALIAS> getAliases() {
        return aliases;
    }

    /**
     * @param aliases the aliases to set
     */
    public void setAliases(List<ALIAS> aliases) {
        this.aliases = aliases;
    }

    /**
     * @return the connections
     */
    public List<CONNECTION> getConnections() {
        return connections;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(List<CONNECTION> connections) {
        this.connections = connections;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(List<COMPONENT> components) {
        this.components = components;
    }
}