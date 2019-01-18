package org.ogin.cb.models;

public class CONNECTION {
    private NODE node1;
    private NODE node2;

    public CONNECTION(NODE node1, NODE node2) {
        this.setNode1(node1);
        this.setNode2(node2);
    }

    /**
     * @return the node2
     */
    public NODE getNode2() {
        return node2;
    }

    /**
     * @param node2 the node2 to set
     */
    public void setNode2(NODE node2) {
        this.node2 = node2;
    }

    /**
     * @return the node1
     */
    public NODE getNode1() {
        return node1;
    }

    /**
     * @param node1 the node1 to set
     */
    public void setNode1(NODE node1) {
        this.node1 = node1;
    }
}