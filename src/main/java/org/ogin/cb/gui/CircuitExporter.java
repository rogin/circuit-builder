package org.ogin.cb.gui;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.ogin.cb.CircuitData;
import org.ogin.cb.Constants;
import org.ogin.cb.gui.components.AbstractGate;
import org.ogin.cb.gui.components.Pin;
import org.ogin.cb.gui.components.Wire;
import org.ogin.cb.models.COMPONENT;
import org.ogin.cb.models.CONNECTION;
import org.ogin.cb.models.NODE;
import org.ogin.cb.models.TokenType;

/**
 * Listens for Gates and Wires to be added to the Canvas,
 * then allows data export into a savable format of type
 * {@link CircuitData}.
 */
public class CircuitExporter implements ContainerListener {
    private List<AbstractGate> gates = new ArrayList<>();
    private List<Wire> wires = new ArrayList<>();

    @Override
    public void componentAdded(ContainerEvent e) {
        if(e.getChild() instanceof AbstractGate) {
            gates.add((AbstractGate)e.getChild());
        }
        else if(e.getChild() instanceof Wire) {
            wires.add((Wire)e.getChild());
        } else {
            System.out.println("Ignoring added type: " + e.getChild().getClass());
        }
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
        if(e.getChild() instanceof AbstractGate) {
            gates.remove((AbstractGate)e.getChild());
        }
        else if(e.getChild() instanceof Wire) {
            wires.remove((Wire)e.getChild());
        } else {
            System.err.println("Ignoring removed type: " + e.getChild().getClass());
        }
    }

    public CircuitData getModelData() {
        List<COMPONENT> components = new ArrayList<>(gates.size());

        for(AbstractGate gate : gates) {
            components.add(gate.asComponent());
        }
        
        List<CONNECTION> connections = new ArrayList<>(wires.size());

        for (Wire wire : wires) {
            CONNECTION connection = new CONNECTION(asNode(wire.getOutPin()), asNode(wire.getInPin()));
            connections.add(connection);
        }

        return new CircuitData(components, connections);
    }

    private NODE asNode(Pin pin) {
        AbstractGate gate = null;
        int gateIndex = 0;

        //get the pin's gate and the gate's index
        for(;gateIndex < gates.size(); gateIndex++) {
            gate = gates.get(gateIndex);

            if(pin.isIn() && ArrayUtils.contains(gate.getInPins(), pin)) {
                break;
            }
            else if(!pin.isIn() && ArrayUtils.contains(gate.getOutPins(), pin)) {
                break;
            }
        }

        NODE node = new NODE();
        //it's ok if it's not one of either IN or OUT
        node.INOrOUT = gate.getTokenType();
        node.integer = pin.getNumber();

        if(gate.getTokenType() == TokenType.GROUND) {
            node.index = Constants.GROUNDX;
        } else if(gate.getTokenType() == TokenType.POWER) {
            node.index = Constants.POWERX;
        } else {
            node.index = gateIndex;
        }
        
        return node;
    }
}