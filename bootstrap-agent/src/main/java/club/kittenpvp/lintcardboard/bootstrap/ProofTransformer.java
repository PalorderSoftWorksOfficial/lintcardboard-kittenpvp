package club.kittenpvp.lintcardboard.bootstrap;

import java.lang.instrument.ClassFileTransformer;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.Properties;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public final class ProofTransformer implements ClassFileTransformer {
    static final String MODE_PROOF = "proof";
    static final String MODE_FAIL = "fail";
    static final String TARGET_INTERNAL_NAME = "club/kittenpvp/lintcardboard/bootstrap/ControlledTarget";
    static final String TARGET_CLASS_NAME = "club.kittenpvp.lintcardboard.bootstrap.ControlledTarget";
    static final String MARKER_FIELD = "LINTCARDBOARD_TRANSFORMED";

    private final String mode;
    private final long registrationNanos;
    private final Path agentOut;

    public ProofTransformer(String mode, long registrationNanos, Path agentOut) {
        this.mode = mode;
        this.registrationNanos = registrationNanos;
        this.agentOut = agentOut;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!TARGET_INTERNAL_NAME.equals(className)) {
            return null;
        }
        long transformNanos = System.nanoTime();
        if (MODE_FAIL.equals(mode)) {
            throw new IllegalStateException("lintcardboard mandatory transformation refused for " + TARGET_CLASS_NAME);
        }
        byte[] transformed = withMarkerField(classfileBuffer);
        writeTransformEvidence(transformNanos);
        return transformed;
    }

    private void writeTransformEvidence(long transformNanos) {
        if (agentOut == null) {
            return;
        }
        Properties evidence = new Properties();
        evidence.setProperty("agent.installed", "true");
        evidence.setProperty("agent.mode", mode);
        evidence.setProperty("agent.registrationNanos", Long.toString(registrationNanos));
        evidence.setProperty("agent.transformNanos", Long.toString(transformNanos));
        evidence.setProperty("agent.target", TARGET_CLASS_NAME);
        AgentEvidence.store(agentOut, evidence);
    }

    static byte[] withMarkerField(byte[] original) {
        ClassReader reader = new ClassReader(original);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public void visitEnd() {
                FieldVisitor field = visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, MARKER_FIELD, "Z", null, 1);
                if (field != null) {
                    field.visitEnd();
                }
                super.visitEnd();
            }
        };
        reader.accept(visitor, 0);
        return writer.toByteArray();
    }
}
