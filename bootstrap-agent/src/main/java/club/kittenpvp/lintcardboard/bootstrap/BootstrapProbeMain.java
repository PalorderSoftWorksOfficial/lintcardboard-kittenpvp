package club.kittenpvp.lintcardboard.bootstrap;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Properties;

public final class BootstrapProbeMain {
    private BootstrapProbeMain() {
    }

    public static void main(String[] args) throws Exception {
        Path evidencePath = Path.of(args[0]);
        Properties evidence = new Properties();
        long beforeLoad = System.nanoTime();
        Class<?> target;
        try {
            target = Class.forName(ProofTransformer.TARGET_CLASS_NAME);
        } catch (Throwable failure) {
            returnFailed(evidencePath, evidence, ProofTransformer.TARGET_CLASS_NAME, "transform before definition", failure.toString());
            return;
        }
        long afterLoad = System.nanoTime();
        boolean transformed = readMarker(target);
        evidence.setProperty("probe.beforeLoadNanos", Long.toString(beforeLoad));
        evidence.setProperty("probe.afterLoadNanos", Long.toString(afterLoad));
        evidence.setProperty("probe.transformed", Boolean.toString(transformed));
        evidence.setProperty("probe.classLoader", target.getClassLoader() == null ? "bootstrap" : target.getClassLoader().toString());
        evidence.setProperty("probe.classIdentity", Integer.toHexString(System.identityHashCode(target)));
        evidence.setProperty("probe.originalValue", String.valueOf(target.getMethod("originalValue").invoke(null)));
        if (!transformed) {
            returnFailed(evidencePath, evidence, ProofTransformer.TARGET_CLASS_NAME, "transform before definition", "marker field absent; transformer never ran");
            return;
        }
        evidence.setProperty("outcome", "SUCCESS");
        AgentEvidence.store(evidencePath, evidence);
    }

    private static boolean readMarker(Class<?> target) throws Exception {
        try {
            Field marker = target.getField(ProofTransformer.MARKER_FIELD);
            return marker.getBoolean(null);
        } catch (NoSuchFieldException missing) {
            return false;
        }
    }

    private static void returnFailed(Path evidencePath, Properties evidence, String subject, String requirement, String cause) {
        evidence.setProperty("outcome", "FAILURE");
        evidence.setProperty("failure.subject", subject);
        evidence.setProperty("failure.requirement", requirement);
        evidence.setProperty("failure.cause", cause);
        AgentEvidence.store(evidencePath, evidence);
        System.exit(1);
    }
}
