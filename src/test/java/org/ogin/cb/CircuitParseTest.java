package org.ogin.cb;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.ogin.cb.parser.SDLException;

public class CircuitParseTest {

    private static final String SDL_FILE = "/SDLs/valid/figure3-2";

    @Test
    public void parsesValidFile() {
        Circuit c = new Circuit();

        try {
            c.load(SDL_FILE);
        } catch (SDLException e) {
            e.printStackTrace();
            fail("Failed to parse a valid SDL file: " + SDL_FILE);

        }
    }
}