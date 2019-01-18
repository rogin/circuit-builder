package org.ogin.cb.models;

public class ALIAS {

    private String identifier;
    // = (can stand in place of, is a synonym for)
    private NODE node;

    public ALIAS(String identifier, NODE node) {
        this.setIdentifier(identifier);
        this.setNode(node);
    }

    /**
     * @return the node
     */
    public NODE getNode() {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(NODE node) {
        this.node = node;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}