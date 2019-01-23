package org.ogin.cb;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.net.URL;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.ogin.cb.parser.SDLException;

/**
 * AllInvalidSDLsFailTest
 */
public class AllInvalidSDLsFailTest {

    private static final String SDL_FOLDER = "/SDLs/invalid/";

    static Stream<String> invalidFiles() {
        URL resource = AllInvalidSDLsFailTest.class.getResource(SDL_FOLDER);
        File f = new File(resource.getFile());

        Stream.Builder<String> stream = Stream.builder();
        for(String child : f.list()) {
            stream.add(SDL_FOLDER + FilenameUtils.removeExtension(child));
        }
        return stream.build();
    }

    @ParameterizedTest
    @MethodSource("invalidFiles")
    public void failsInvalidFile(String filename) {
        Circuit c = new Circuit();

        assertThrows(SDLException.class, () -> c.load(filename));
    }
}