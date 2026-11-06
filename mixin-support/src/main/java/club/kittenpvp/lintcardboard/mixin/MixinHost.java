package club.kittenpvp.lintcardboard.mixin;

import club.kittenpvp.lintcardboard.common.FailureDetail;
import club.kittenpvp.lintcardboard.common.OperationOutcome;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class MixinHost {
    private static volatile List<Path> roots = List.of();
    private static volatile URLClassLoader mixinClassLoader;
    private static volatile MixinTransformation transformation;
    private static volatile Path evidencePath;
    private static volatile String configResource;
    private static final ThreadLocal<ClassLoader> transformLoader = new ThreadLocal<>();
    private static final List<FailureDetail> failures = new CopyOnWriteArrayList<>();
    private static final java.util.Set<String> transformedClasses = ConcurrentHashMap.newKeySet();

    private MixinHost() {
    }

    public static OperationOutcome<MixinTransformation> start(List<Path> mixinRoots, String configResource, Path evidencePath) {
        try {
            List<URL> urls = new ArrayList<>();
            for (Path root : mixinRoots) {
                urls.add(root.toUri().toURL());
            }
            roots = List.copyOf(mixinRoots);
            mixinClassLoader = new URLClassLoader(urls.toArray(new URL[0]), MixinHost.class.getClassLoader());
            transformation = new MixinTransformation();
            MixinHost.evidencePath = evidencePath;
            MixinHost.configResource = configResource;
            MixinBootstrap.init();
            Mixins.addConfiguration(configResource);
            gotoPhase(MixinEnvironment.Phase.INIT);
            gotoPhase(MixinEnvironment.Phase.DEFAULT);
            transformation.preboot();
            writeEvidence("SUCCESS", null);
            return new OperationOutcome.Success<>(transformation, mapOf(currentEvidence("SUCCESS", null)));
        } catch (Throwable failure) {
            FailureDetail detail = new FailureDetail(configResource, "mixin engine must start before target classes load", failure.toString());
            writeEvidence("FAILURE", detail, stackTrace(failure));
            return new OperationOutcome.Failure(detail, mapOf(currentEvidence("FAILURE", detail)));
        }
    }

    public static MixinTransformation transformation() {
        return transformation;
    }

    public static ClassLoader mixinClassLoader() {
        ClassLoader loader = mixinClassLoader;
        return loader != null ? loader : MixinHost.class.getClassLoader();
    }

    public static void beginTransform(ClassLoader loader) {
        transformLoader.set(loader);
    }

    public static void endTransform() {
        transformLoader.remove();
    }

    public static void recordFailure(String className, Throwable failure) {
        failures.add(new FailureDetail(className, "transformation must not fail silently", failure.toString()));
        if (evidencePath != null) {
            writeEvidence("FAILURE", failures.get(failures.size() - 1));
        }
    }

    public static void noteTransformed(String className) {
        transformedClasses.add(className);
        if (evidencePath != null) {
            writeEvidence("SUCCESS", null);
        }
    }

    public static List<FailureDetail> failures() {
        return List.copyOf(failures);
    }

    public static InputStream findResource(String name) {
        for (Path root : roots) {
            try {
                if (Files.isDirectory(root)) {
                    Path candidate = root.resolve(name);
                    if (Files.isRegularFile(candidate)) {
                        return Files.newInputStream(candidate);
                    }
                } else if (Files.isRegularFile(root)) {
                    try (JarFile jar = new JarFile(root.toFile())) {
                        ZipEntry entry = jar.getEntry(name);
                        if (entry != null) {
                            try (InputStream stream = jar.getInputStream(entry)) {
                                return new java.io.ByteArrayInputStream(readAll(stream));
                            }
                        }
                    }
                }
            } catch (IOException ignored) {
            }
        }
        ClassLoader fallback = MixinHost.class.getClassLoader();
        return fallback != null ? fallback.getResourceAsStream(name) : null;
    }

    public static byte[] readClassBytes(String className) {
        String resource = className.replace('.', '/') + ".class";
        ClassLoader loader = transformLoader.get();
        if (loader != null) {
            try (InputStream stream = loader.getResourceAsStream(resource)) {
                if (stream != null) {
                    return readAll(stream);
                }
            } catch (IOException ignored) {
            }
        }
        try (InputStream stream = findResource(resource)) {
            return stream != null ? readAll(stream) : null;
        } catch (IOException ignored) {
            return null;
        }
    }

    private static synchronized void writeEvidence(String outcome, FailureDetail failure) {
        writeEvidence(outcome, failure, null);
    }

    private static synchronized void writeEvidence(String outcome, FailureDetail failure, String stackTrace) {
        Properties evidence = currentEvidence(outcome, failure);
        if (stackTrace != null) {
            evidence.setProperty("failure.stackTrace", stackTrace);
        }
        MixinEvidence.store(evidencePath, evidence);
    }

    private static String stackTrace(Throwable failure) {
        StringBuilder text = new StringBuilder();
        Throwable current = failure;
        while (current != null && text.length() < 4000) {
            text.append(current).append(" <- ");
            current = current.getCause();
        }
        return text.toString();
    }

    private static Properties currentEvidence(String outcome, FailureDetail failure) {
        Properties evidence = new Properties();
        evidence.setProperty("outcome", outcome);
        if (configResource != null) {
            evidence.setProperty("mixin.config", configResource);
        }
        evidence.setProperty("mixin.service", LintCardboardMixinService.class.getName());
        List<String> names = new ArrayList<>(transformedClasses);
        java.util.Collections.sort(names);
        evidence.setProperty("mixin.transformedClasses", String.join(",", names));
        if (failure != null) {
            evidence.setProperty("failure.subject", failure.getSubject());
            evidence.setProperty("failure.requirement", failure.getRequirement());
            evidence.setProperty("failure.cause", failure.getCause());
        }
        return evidence;
    }

    private static void gotoPhase(MixinEnvironment.Phase phase) throws Exception {
        Method method = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
        method.setAccessible(true);
        method.invoke(null, phase);
    }

    private static byte[] readAll(InputStream stream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int read;
        while ((read = stream.read(chunk)) != -1) {
            buffer.write(chunk, 0, read);
        }
        return buffer.toByteArray();
    }

    private static java.util.Map<String, String> mapOf(Properties evidence) {
        java.util.Map<String, String> values = new java.util.LinkedHashMap<>();
        for (String name : evidence.stringPropertyNames()) {
            values.put(name, evidence.getProperty(name));
        }
        return values;
    }
}
