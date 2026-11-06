package club.kittenpvp.lintcardboard.bootstrap;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BootstrapOrderingTest {
    @TempDir
    Path workspace;

    @Test
    void proofAgentTransformsControlledTargetBeforeDefinition() throws Exception {
        Path agentEvidence = workspace.resolve("agent-proof.properties");
        Path probeEvidence = workspace.resolve("probe-proof.properties");
        ProbeResult probe = launchProbe("proof", agentEvidence, probeEvidence);

        assertEquals(0, probe.exit(), () -> "probe JVM must exit cleanly in proof mode: " + probe.output());
        Properties agent = load(agentEvidence);
        Properties evidence = load(probeEvidence);
        assertEquals("SUCCESS", evidence.getProperty("outcome"), () -> "probe must report success: " + probe.output());
        assertEquals("true", evidence.getProperty("probe.transformed"));
        assertEquals("original", evidence.getProperty("probe.originalValue"));

        long registration = Long.parseLong(agent.getProperty("agent.registrationNanos"));
        long transform = Long.parseLong(agent.getProperty("agent.transformNanos"));
        long beforeLoad = Long.parseLong(evidence.getProperty("probe.beforeLoadNanos"));
        long afterLoad = Long.parseLong(evidence.getProperty("probe.afterLoadNanos"));
        assertTrue(registration > 0L, "agent must record registration");
        assertTrue(transform > registration, "transformer must observe the class after agent registration");
        assertTrue(beforeLoad <= transform, "transformation must happen at first class load");
        assertTrue(transform < afterLoad, "class definition must complete after transformation");
        assertEquals(ProofTransformer.TARGET_CLASS_NAME, agent.getProperty("agent.target"));
    }

    @Test
    void mandatoryTransformationFailureIsNeverReportedAsSuccess() throws Exception {
        Path agentEvidence = workspace.resolve("agent-fail.properties");
        Path probeEvidence = workspace.resolve("probe-fail.properties");
        ProbeResult probe = launchProbe("fail", agentEvidence, probeEvidence);

        assertNotEquals(0, probe.exit(), () -> "probe must exit nonzero when the mandatory transformation fails: " + probe.output());
        Properties evidence = load(probeEvidence);
        assertEquals("FAILURE", evidence.getProperty("outcome"));
        assertTrue(evidence.getProperty("failure.subject").contains("ControlledTarget"));
    }

    private record ProbeResult(int exit, String output) {
    }

    private ProbeResult launchProbe(String mode, Path agentEvidence, Path probeEvidence) throws Exception {
        String agentJar = System.getProperty("lintcardboard.agent.jar");
        assertTrue(agentJar != null && Files.isRegularFile(Path.of(agentJar)), "agent jar system property must point at a built jar");

        List<String> command = new ArrayList<>();
        command.add(javaExecutable());
        command.add("-javaagent:" + agentJar + "=mode=" + mode + ";agentOut=" + agentEvidence.toAbsolutePath());
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(BootstrapProbeMain.class.getName());
        command.add(probeEvidence.toAbsolutePath().toString());

        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        boolean finished = process.waitFor(120, TimeUnit.SECONDS);
        String output = new String(process.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("probe JVM timed out in " + mode + " mode: " + output);
        }
        return new ProbeResult(process.exitValue(), output);
    }

    private Properties load(Path path) throws Exception {
        assertTrue(Files.isRegularFile(path), () -> "expected evidence file at " + path);
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            properties.load(in);
        }
        return properties;
    }

    private static String javaExecutable() {
        Path bin = Path.of(System.getProperty("java.home"), "bin");
        if (Files.isExecutable(bin.resolve("java"))) {
            return bin.resolve("java").toString();
        }
        return bin.resolve("java.exe").toString();
    }
}
