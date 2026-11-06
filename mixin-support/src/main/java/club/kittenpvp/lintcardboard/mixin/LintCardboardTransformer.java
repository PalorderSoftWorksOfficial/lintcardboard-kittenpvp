package club.kittenpvp.lintcardboard.mixin;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LintCardboardTransformer implements ClassFileTransformer {
    private final List<Transformation> services;
    private final List<String> gates;

    public LintCardboardTransformer(List<Transformation> services) {
        this(services, List.of());
    }

    public LintCardboardTransformer(List<Transformation> services, String gatePrefix) {
        this(services, gatePrefix == null ? List.of() : List.of(gatePrefix));
    }

    public LintCardboardTransformer(List<Transformation> services, List<String> gatePrefixes) {
        List<Transformation> ordered = new ArrayList<>(services);
        ordered.sort(Comparator.comparingInt(Transformation::priority));
        this.services = List.copyOf(ordered);
        this.gates = List.copyOf(gatePrefixes == null ? List.of() : gatePrefixes);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null || className.startsWith("java/") || className.startsWith("jdk/")) {
            return null;
        }
        String dotted = className.replace('/', '.');
        if (!gates.isEmpty() && gates.stream().noneMatch(dotted::startsWith)) {
            return null;
        }
        MixinHost.beginTransform(loader);
        try {
            byte[] current = classfileBuffer;
            boolean changed = false;
            for (Transformation service : services) {
                if (!service.shouldTransform(dotted)) {
                    continue;
                }
                byte[] next;
                try {
                    next = service.transform(dotted, current);
                } catch (Throwable failure) {
                    MixinHost.recordFailure(dotted, failure);
                    throw new IllegalClassFormatException("LintCardboard transformation failed for " + dotted + ": " + failure);
                }
                if (next != null) {
                    current = next;
                    changed = true;
                }
            }
            return changed ? current : null;
        } finally {
            MixinHost.endTransform();
        }
    }
}
