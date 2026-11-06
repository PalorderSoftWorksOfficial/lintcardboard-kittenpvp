package club.kittenpvp.lintcardboard.bootstrap;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

final class AgentEvidence {
    private AgentEvidence() {
    }

    static void store(Path path, Properties evidence) {
        try (OutputStream out = Files.newOutputStream(path)) {
            evidence.store(out, "lintcardboard bootstrap proof");
        } catch (Exception failure) {
            throw new IllegalStateException("lintcardboard could not record bootstrap evidence at " + path, failure);
        }
    }
}
