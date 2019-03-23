package com.github.levry.imq.embedded.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author levry
 */
class ResourcesTest {

    private static final String[] COPYING_ORIGINAL_FILES = {
            "lib/props/broker/default.properties",
            "lib/props/broker/install.properties",
            "var/instances/imqbroker/etc/accesscontrol.properties",
            "var/instances/imqbroker/etc/passwd",
            "var/instances/imqbroker/props/config.properties"
    };

    @Test
    void copyResourcesRecursivelyFromJar(@TempDir Path tempDir) throws IOException {
        URL jar = getClass().getResource("/test/imq-embedded.jar");
        URL source = new URL("jar:" + jar + "!/openmq");
        File destination = tempDir.toFile();

        Resources.copyResourcesRecursively(source, destination);

        assertThat(tempDir).exists();
        assertThat(walkFiles(tempDir)).containsExactlyInAnyOrder(COPYING_ORIGINAL_FILES);
    }

    @Test
    void copyResourcesRecursivelyFromDir(@TempDir Path tempDir) throws IOException {
        URL dir = getClass().getResource("/openmq");
        File destination = tempDir.toFile();

        Resources.copyResourcesRecursively(dir, destination);

        assertThat(tempDir).exists();
        assertThat(walkFiles(tempDir)).containsExactlyInAnyOrder(COPYING_ORIGINAL_FILES);
    }

    private List<String> walkFiles(Path tempDir) throws IOException {
        try (Stream<Path> paths = Files.walk(tempDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(tempDir::relativize)
                    .map(Path::toString)
                    .map(s -> s.replaceAll("\\\\", "/"))
                    .collect(Collectors.toList());
        }
    }

}