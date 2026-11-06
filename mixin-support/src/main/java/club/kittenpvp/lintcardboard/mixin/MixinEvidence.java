package club.kittenpvp.lintcardboard.mixin;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class MixinEvidence {
    private MixinEvidence() {
    }

    public static void store(Path path, Properties values) {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Properties copy = new Properties();
            copy.putAll(values);
            try (OutputStream output = Files.newOutputStream(path)) {
                copy.store(output, "lintcardboard-mixin");
            }
        } catch (IOException failure) {
            throw new IllegalStateException("Unable to write mixin evidence to " + path, failure);
        }
    }
}
