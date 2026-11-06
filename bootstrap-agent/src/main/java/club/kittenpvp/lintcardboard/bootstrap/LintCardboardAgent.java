package club.kittenpvp.lintcardboard.bootstrap;

import club.kittenpvp.lintcardboard.common.OperationOutcome;
import club.kittenpvp.lintcardboard.mixin.LintCardboardTransformer;
import club.kittenpvp.lintcardboard.mixin.MixinHost;
import club.kittenpvp.lintcardboard.mixin.MixinTransformation;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
        if (arguments.mixinJar() != null) {
            OperationOutcome<MixinTransformation> outcome = MixinHost.start(List.of(arguments.mixinJar()), arguments.mixinConfig(), arguments.mixinOut());
            if (outcome instanceof OperationOutcome.Success<MixinTransformation> success) {
                instrumentation.addTransformer(new LintCardboardTransformer(List.of(success.getValue()), arguments.mixinGates()));
            } else if (outcome instanceof OperationOutcome.Failure failure) {
                throw new IllegalStateException("Mixin host failed to start: " + failure.getDetail());
            }
        }
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

    record AgentArguments(String mode, Path agentOut, Path mixinJar, String mixinConfig, Path mixinOut, List<String> mixinGates) {
        static AgentArguments parse(String agentArgs) {
            String mode = ProofTransformer.MODE_PROOF;
            Path agentOut = null;
            Path mixinJar = null;
            String mixinConfig = "lintcardboard.mixins.json";
            Path mixinOut = null;
            List<String> mixinGates = List.of();
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
                    if (entry[0].trim().equals("mixinJar")) {
                        mixinJar = Path.of(entry[1].trim());
                    }
                    if (entry[0].trim().equals("mixinConfig")) {
                        mixinConfig = entry[1].trim();
                    }
                    if (entry[0].trim().equals("mixinOut")) {
                        mixinOut = Path.of(entry[1].trim());
                    }
                    if (entry[0].trim().equals("mixinGate")) {
                        List<String> gates = new java.util.ArrayList<>();
                        for (String gate : entry[1].split(",")) {
                            gates.add(gate.trim().replace('/', '.'));
                        }
                        mixinGates = List.copyOf(gates);
                    }
                }
            }
            if (mixinJar != null && mixinOut == null) {
                mixinOut = Paths.get("lintcardboard-mixin.properties");
            }
            return new AgentArguments(mode, agentOut, mixinJar, mixinConfig, mixinOut, mixinGates);
        }
    }
}
