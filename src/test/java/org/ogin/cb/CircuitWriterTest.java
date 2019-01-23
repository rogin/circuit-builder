package org.ogin.cb;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;
import org.ogin.cb.parser.SDLException;

@ExtendWith(TempDirectory.class)
public class CircuitWriterTest {

    private static final String SDL_FILE = "/SDLs/valid/figure3-2";

    @Test
    public void writesValidFile(@TempDir Path tempFolder) throws IOException {
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
            File outputFile = new File(tempFolder.toFile(), "test-output.sdl");
            try(PrintStream out = new PrintStream(outputFile)) {
                CircuitWriter writer = new CircuitWriter(out, data);
                writer.write();
            }
        }
    }
}