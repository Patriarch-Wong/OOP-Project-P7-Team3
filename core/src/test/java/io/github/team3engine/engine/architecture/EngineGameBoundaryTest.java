package io.github.team3engine.engine.architecture;

import org.junit.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class EngineGameBoundaryTest {

    @Test
    public void engineSourcesMustNotImportGamePackages() throws IOException {
        Path engineRoot = resolveEngineRoot();
        assertTrue("Engine source root not found: " + engineRoot, Files.isDirectory(engineRoot));

        List<Path> violations = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(engineRoot)) {
            paths.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".java"))
                 .forEach(path -> {
                     try {
                         if (importsGamePackage(path)) {
                             violations.add(path);
                         }
                     } catch (IOException e) {
                         throw new UncheckedIOException(e);
                     }
                 });
        }

        assertTrue("Engine sources must not import io.github.team3engine.game.* but found: " + violations,
                violations.isEmpty());
    }

    private boolean importsGamePackage(Path sourceFile) throws IOException {
        try (Stream<String> lines = Files.lines(sourceFile, StandardCharsets.UTF_8)) {
            return lines.map(String::trim)
                        .anyMatch(line -> line.startsWith("import io.github.team3engine.game."));
        }
    }

    private Path resolveEngineRoot() {
        Path[] candidates = new Path[] {
                Paths.get("core", "src", "main", "java", "io", "github", "team3engine", "engine"),
                Paths.get("src", "main", "java", "io", "github", "team3engine", "engine")
        };

        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }

        return candidates[0];
    }
}
