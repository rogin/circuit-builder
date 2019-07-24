package org.ogin.cb.gui;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Stores the components modeling a circuit. It listens for component {@link AbstractGate gates}
 * and {@link Wire wires} being added to the {@link Canvas}, provides component search and filtering,
 * options, and can export the circuit into a savable format of type {@link CircuitData}.
 */
public class CircuitModel implements ContainerListener {
    private List<AbstractGate> gates = new ArrayList<>();
    private List<Wire> wires = new ArrayList<>();

    @Override
    public void componentAdded(ContainerEvent event) {
        if(event.getChild() instanceof AbstractGate) {
            gates.add((AbstractGate)event.getChild());
        }
        else if(event.getChild() instanceof Wire) {
            wires.add((Wire)event.getChild());
        } else {
            System.out.println("Ignoring added type: " + event.getChild().getClass());
        }
    }

    @Override
    public void componentRemoved(ContainerEvent event) {
        if(event.getChild() instanceof AbstractGate) {
            gates.remove((AbstractGate)event.getChild());
        }
        else if(event.getChild() instanceof Wire) {
            wires.remove((Wire)event.getChild());
        } else {
            System.err.println("Ignoring removed type: " + event.getChild().getClass());
        }
    }

    /**
     * Return a copy of the gates
     */
    public List<AbstractGate> getGates() {
        return new ArrayList<>(gates);
    }

    /**
     * Return a copy of the wires
     */
    public List<Wire> getWires() {
        return new ArrayList<>(wires);
    }

    /**
     * Converts the model data into an export format.
     * @return
     */
    public CircuitData getExportData() {
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

	public List<Wire> getWiresAttachedToGate(AbstractGate gate) {
        List<Wire> attachedWires = new ArrayList<>();

        for (Wire wire : wires) {
            if(ArrayUtils.contains(gate.getInPins(), wire.getInPin())
                || ArrayUtils.contains(gate.getOutPins(), wire.getOutPin())) {
                attachedWires.add(wire);
            }
        }

        return attachedWires;
    }

	public AbstractGate getGateOwningOutPin(Pin outPin) {
        for (AbstractGate gate : gates) {
            if(ArrayUtils.contains(gate.getOutPins(), outPin)) {
                return gate;
            }
        }

        System.err.println("Failed to find gate from OUT pin: " + outPin);

        return null;
    }

    /**
     * Return a list of all IN pins on the available gates.
     */
	public List<Pin> getInPins() {
        List<Pin> inPins = new ArrayList<>();

        for (AbstractGate gate : gates) {
            inPins.addAll(Arrays.asList(gate.getInPins()));
        }

        return inPins;
    }

    /**
     * Determines whether an IN pin is part of an existing wire.
     * @param inPin
     * @return true if used in a wire
     */
	public boolean isWired(Pin inPin) {
        for (Wire wire : wires) {
            if (wire.getInPin().equals(inPin)) {
                return true;
            }
        }

        return false;
    }

	public Pin findOutPin(Pin outPinToFind) {
        for (AbstractGate gate : gates) {
            int index = ArrayUtils.indexOf(gate.getOutPins(), outPinToFind);
            if(index >= 0) {
                return gate.getOutPins()[index];
            }
        }

        System.err.println("Failed to find OUT pin: " + outPinToFind);

        return null;
    }
}