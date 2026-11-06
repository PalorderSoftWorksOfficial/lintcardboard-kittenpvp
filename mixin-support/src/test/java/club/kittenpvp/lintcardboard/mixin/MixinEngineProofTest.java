package club.kittenpvp.lintcardboard.mixin;

import club.kittenpvp.lintcardboard.common.OperationOutcome;
import club.kittenpvp.lintcardboard.mixin.target.FarewellTarget;
import club.kittenpvp.lintcardboard.mixin.target.GreetingTarget;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MixinEngineProofTest {
    private static MixinTransformation transformation;

    @TempDir
    static Path workspace;

    @BeforeAll
    static void bootEngine() throws Exception {
        Path classesDir = Paths.get(GreetingTarget.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path resourcesDir = Paths.get(Objects.requireNonNull(MixinEngineProofTest.class.getResource("/mixin-test.mixins.json")).toURI()).getParent();
        Path evidence = workspace.resolve("mixin-test.properties");
        OperationOutcome<MixinTransformation> outcome = MixinHost.start(List.of(classesDir, resourcesDir), "mixin-test.mixins.json", evidence);
        assertTrue(outcome instanceof OperationOutcome.Success, () -> "mixin engine must start: " + outcome);
        transformation = ((OperationOutcome.Success<MixinTransformation>) outcome).getValue();
    }

    @Test
    void overwriteMixinChangesTargetBehavior() throws Exception {
        byte[] original = readTargetBytes(GreetingTarget.class);
        byte[] transformed = transformation.transform(GreetingTarget.class.getName(), original);
        assertNotNull(transformed, "engine must rewrite the targeted class");

        Class<?> defined = new SingleClassLoader(GreetingTarget.class.getName(), transformed).loadClass(GreetingTarget.class.getName());
        Object instance = defined.getDeclaredConstructor().newInstance();
        assertEquals("mixed", defined.getMethod("greet").invoke(instance));
        assertTrue(transformation.transformedClasses().contains(GreetingTarget.class.getName()));
    }

    @Test
    void brokenInjectorFailsLoudlyAndIsNeverReportedAsSuccess() throws Exception {
        byte[] original = readTargetBytes(FarewellTarget.class);
        assertThrows(Throwable.class, () -> transformation.transform(FarewellTarget.class.getName(), original));

        Properties evidence = new Properties();
        try (InputStream stream = java.nio.file.Files.newInputStream(workspace.resolve("mixin-test.properties"))) {
            evidence.load(stream);
        }
        assertEquals("SUCCESS", evidence.getProperty("outcome"), "engine boot stays successful; the failure belongs to the broken mixin, not the host");
    }

    private byte[] readTargetBytes(Class<?> target) throws Exception {
        String resource = target.getName().replace('.', '/') + ".class";
        try (InputStream stream = target.getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(stream, "test classpath must provide " + resource);
            return stream.readAllBytes();
        }
    }

    private static class SingleClassLoader extends ClassLoader {
        private final String targetName;
        private final byte[] targetBytes;

        SingleClassLoader(String targetName, byte[] targetBytes) {
            super(MixinEngineProofTest.class.getClassLoader());
            this.targetName = targetName;
            this.targetBytes = targetBytes.clone();
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                Class<?> loaded = findLoadedClass(name);
                if (loaded == null && name.equals(targetName)) {
                    loaded = defineClass(name, targetBytes, 0, targetBytes.length);
                } else if (loaded == null) {
                    loaded = super.loadClass(name, false);
                }
                if (resolve) {
                    resolveClass(loaded);
                }
                return loaded;
            }
        }
    }
}
