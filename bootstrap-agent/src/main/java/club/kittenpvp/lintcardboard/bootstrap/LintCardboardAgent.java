package club.kittenpvp.lintcardboard.bootstrap;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.Properties;

public final class LintCardboardAgent {
    private LintCardboardAgent() {
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        install(agentArgs, instrumentation);
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        install(agentArgs, instrumentation);
    }

    static void install(String agentArgs, Instrumentation instrumentation) {
        AgentArguments arguments = AgentArguments.parse(agentArgs);
        long registrationNanos = System.nanoTime();
        writeRegistrationEvidence(arguments, registrationNanos);
        instrumentation.addTransformer(new ProofTransformer(arguments.mode(), registrationNanos, arguments.agentOut()));
    }

    private static void writeRegistrationEvidence(AgentArguments arguments, long registrationNanos) {
        if (arguments.agentOut() == null) {
            return;
        }
        Properties evidence = new Properties();
        evidence.setProperty("agent.installed", "true");
        evidence.setProperty("agent.mode", arguments.mode());
        evidence.setProperty("agent.registrationNanos", Long.toString(registrationNanos));
        AgentEvidence.store(arguments.agentOut(), evidence);
    }

    record AgentArguments(String mode, Path agentOut) {
        static AgentArguments parse(String agentArgs) {
            String mode = ProofTransformer.MODE_PROOF;
            Path agentOut = null;
            if (agentArgs != null) {
                for (String part : agentArgs.split(";")) {
                    String[] entry = part.split("=", 2);
                    if (entry.length != 2) {
                        continue;
                    }
                    if (entry[0].trim().equals("mode")) {
                        mode = entry[1].trim();
                    }
                    if (entry[0].trim().equals("agentOut")) {
                        agentOut = Path.of(entry[1].trim());
                    }
                }
            }
            return new AgentArguments(mode, agentOut);
        }
    }
}
