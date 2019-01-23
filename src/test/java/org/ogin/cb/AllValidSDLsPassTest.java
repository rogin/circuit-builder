package org.ogin.cb;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.net.URL;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.ogin.cb.parser.SDLException;

/**
 * AllValidSDLsPassTest
 */
public class AllValidSDLsPassTest {

    private static final String SDL_FOLDER = "/SDLs/valid/";

    static Stream<String> validFiles() {
        URL resource = AllValidSDLsPassTest.class.getResource(SDL_FOLDER);
        File f = new File(resource.getFile());

        Stream.Builder<String> stream = Stream.builder();
        for(String child : f.list()) {
            stream.add(SDL_FOLDER + FilenameUtils.removeExtension(child));
        }
        return stream.build();
    }

    @ParameterizedTest
    @MethodSource("validFiles")
    public void parsesValidFile(String filename) {
        Circuit c = new Circuit();

        try {
            c.load(filename);
        } catch (SDLException e) {
            e.printStackTrace();
            fail("Failed to parse a valid SDL file: " + filename);

        }
    }
}