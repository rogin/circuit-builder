package org.ogin.cb;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.ogin.cb.parser.SDLException;

public class CircuitWriterTest {

    private static final String SDL_FILE = "/SDLs/valid/figure3-2";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void writesValidFile() throws IOException {
        Circuit c = new Circuit();
        CircuitData data = null;

        try {
            c.load(SDL_FILE);
            data = c.getData();
        } catch (SDLException e) {
            e.printStackTrace();
            fail("Failed to parse a valid SDL file: " + SDL_FILE);
        }

        if(data != null) {
            File outputFile = tempFolder.newFile("test-output.sdl");
            System.err.println(outputFile.getAbsolutePath());
            try(PrintStream out = new PrintStream(outputFile)) {
                CircuitWriter writer = new CircuitWriter(out, data);
                writer.write();
            }
        }
    }
}