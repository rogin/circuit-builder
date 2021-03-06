package org.ogin.cb;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang3.ArrayUtils;
import org.ogin.cb.models.ALIAS;
import org.ogin.cb.models.COMPONENT;
import org.ogin.cb.models.CONNECTION;
import org.ogin.cb.models.NODE;
import org.ogin.cb.models.TokenType;

public class CircuitWriter {
    private static final TokenType[] BUILT_IN_COMPONENTS = {TokenType.GROUND, TokenType.IN, TokenType.OUT, TokenType.POWER};

    private PrintStream out;
    private CircuitData data;

    protected CircuitWriter(PrintStream out, CircuitData data) {
        this.out = out;
        this.data = data;
    }

    public static void write(CircuitData data, String filename) throws IOException {
        try(PrintStream out = new PrintStream(new File(filename))) {
            CircuitWriter writer = new CircuitWriter(out, data);
            writer.write();
        }
    }

    protected void write() {
        writeComponents();
        writeAliases();
        writeConnections();
        writeEnd();
    }

    private void writeComponents() {
        out.println(TokenType.COMPONENTS);

        for(COMPONENT c : data.getComponents()) {
            //avoid writing built-in components
            if(isBuiltInComponent(c)) continue;

            //use default # of pins
            if(c.getNumberOfPins() > 2) {
                out.printf("%s *%s %s", c.GetType(), c.getNumberOfPins(), c.getIdentifier());
            } else {
                out.printf("%s %s", c.GetType(), c.getIdentifier());
            }
            out.println();
        }

        out.println();
    }

    private boolean isBuiltInComponent(COMPONENT c) {
        return ArrayUtils.contains(BUILT_IN_COMPONENTS, c.GetType());
    }

    private void writeAliases() {
        if(data.getAliases().isEmpty()) {
            return;
        }

        out.println(TokenType.ALIASES);

        for(ALIAS a : data.getAliases()) {
            out.printf("%s = %s\n", a.getIdentifier(), formatNode(a.getNode()));
        }

        out.println();
    }

    private void writeConnections() {
        out.println(TokenType.CONNECTIONS);

        for(CONNECTION c : data.getConnections()) {
            out.printf("%s - %s\n", formatNode(c.getNode1()), formatNode(c.getNode2()));
        }

        out.println();
    }

    private String formatNode(NODE n) {
        //TODO output ALIAS if one exists

        if(n.index == Constants.GROUNDX) {
            return TokenType.GROUND.name();
        } else if(n.index == Constants.POWERX) {
            return TokenType.POWER.name();
        }
        
        if(n.index == Constants.INS || n.index == Constants.OUTS) {
            return String.format("%s#%d", n.INOrOUT, n.integer);
        } else {
            COMPONENT c = data.getComponents().get(n.index);
            return String.format("%s#%s", c.getIdentifier(), n.integer);
        }
    }

    private void writeEnd() {
        out.println("$ Generated by Circuit-Builder.");
        out.print(TokenType.END);
    }
}