package club.kittenpvp.lintcardboard.live;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class LivePaperProbe {
    private static final String PAPER_26_2_135_SHA256 = "ef3dbc68b013deafdbbee3bebf1ba351671ebcda94a8f136809b7532d5ba2fe1";
    private static final String PAPER_DOWNLOAD_URL = "https://fill-data.papermc.io/v1/objects/ef3dbc68b013deafdbbee3bebf1ba2fe1/paper-26.2-135.jar";

    public static void main(String[] args) throws Exception {
        Arguments arguments = Arguments.parse(args);
        int exit = run(arguments);
        System.exit(exit);
    }

    static String paperDownloadUrl() {
        return PAPER_DOWNLOAD_URL;
    }

    static String paperSha256() {
        return PAPER_26_2_135_SHA256;
    }

    private static int run(Arguments arguments) throws Exception {
        Files.createDirectories(arguments.workDir);
        Path pluginsDir = arguments.workDir.resolve("plugins");
        Files.createDirectories(pluginsDir);
        Files.copy(arguments.pluginJar, pluginsDir.resolve("lintcardboard.jar"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Files.writeString(arguments.workDir.resolve("eula.txt"), "eula=true\n", StandardCharsets.UTF_8);
        Files.writeString(arguments.workDir.resolve("server.properties"), String.join("\n",
            "server-port=25566",
            "online-mode=false",
            "motd=LintCardboard live proof",
            "") + "\n", StandardCharsets.UTF_8);

        List<String> command = new ArrayList<>();
        command.add(arguments.javaExe.toString());
        command.add("-Xms1G");
        command.add("-Xmx1G");
        String agentArg = "-javaagent:" + arguments.agentJar.toAbsolutePath()
            + "=mode=live"
            + ";agentOut=" + arguments.agentOut.toAbsolutePath()
            + ";mixinJar=" + arguments.mixinJar.toAbsolutePath()
            + ";mixinConfig=" + arguments.mixinConfig
            + ";mixinOut=" + arguments.mixinOut.toAbsolutePath()
            + ";mixinGate=net/minecraft/server/,io/papermc/paper/";
        command.add(agentArg);
        command.add("-jar");
        command.add(arguments.paperJar.toAbsolutePath().toString());
        command.add("--nogui");

        Path logFile = arguments.workDir.resolve("server.log");
        Process process = new ProcessBuilder(command)
            .directory(arguments.workDir.toFile())
            .redirectErrorStream(true)
            .start();

        boolean done = false;
        boolean branded = false;
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(arguments.timeoutSec);
        try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter log = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = output.readLine()) != null) {
                log.write(line);
                log.newLine();
                if (line.contains("Done (")) {
                    done = true;
                    break;
                }
                if (System.nanoTime() > deadline) {
                    break;
                }
            }
            log.flush();
        }

        sendStop(process, arguments.workDir);
        boolean exited = process.waitFor(180, TimeUnit.SECONDS);
        if (!exited) {
            process.destroyForcibly();
        }
        drainToLog(process, arguments.workDir.resolve("server.log"));

        List<String> logLines = Files.readAllLines(logFile, StandardCharsets.UTF_8);
        for (String line : logLines) {
            if (line.contains(arguments.brandMarker)) {
                branded = true;
                break;
            }
        }

        Properties agentEvidence = readProperties(arguments.agentOut);
        Properties mixinEvidence = readProperties(arguments.mixinOut);
        Properties pluginEvidence = readProperties(arguments.workDir.resolve("plugins/LintCardboard/lintcardboard-plugin.properties"));
        String transformed = mixinEvidence.getProperty("mixin.transformedClasses", "");
        boolean brandTransformed = transformed.contains("io.papermc.paper.ServerBuildInfoImpl");
        boolean modNameTransformed = transformed.contains("net.minecraft.server.MinecraftServer");
        boolean pluginReady = "LintCardboard".equals(pluginEvidence.getProperty("plugin.brandSeen"))
            && Boolean.parseBoolean(pluginEvidence.getProperty("plugin.bridgeActive", "false"));

        Properties verdict = new Properties();
        verdict.setProperty("outcome", done && branded && brandTransformed && modNameTransformed && pluginReady ? "SUCCESS" : "FAILURE");
        verdict.setProperty("probe.done", Boolean.toString(done));
        verdict.setProperty("probe.branded", Boolean.toString(branded));
        verdict.setProperty("probe.brandTransformed", Boolean.toString(brandTransformed));
        verdict.setProperty("probe.modNameTransformed", Boolean.toString(modNameTransformed));
        verdict.setProperty("probe.pluginReady", Boolean.toString(pluginReady));
        verdict.setProperty("probe.exit", Integer.toString(exited ? process.exitValue() : -1));
        verdict.setProperty("probe.agentInstalled", agentEvidence.getProperty("agent.installed", "false"));
        verdict.setProperty("probe.mixinOutcome", mixinEvidence.getProperty("outcome", "missing"));
        store(arguments.verdictOut, verdict);

        if (!done || !branded || !brandTransformed || !modNameTransformed || !pluginReady) {
            System.err.println("Live proof failed: done=" + done + " branded=" + branded + " brandTransformed=" + brandTransformed + " modNameTransformed=" + modNameTransformed + " pluginReady=" + pluginReady);
            System.err.println("Mixin evidence: " + mixinEvidence);
            System.err.println("Plugin evidence: " + pluginEvidence);
            return 1;
        }
        System.out.println("Live proof succeeded: LintCardboard branded server booted with bridge plugin active.");
        return 0;
    }

    private static void sendStop(Process process, Path workDir) {
        try {
            OutputStream stdin = process.getOutputStream();
            stdin.write("stop\n".getBytes(StandardCharsets.UTF_8));
            stdin.flush();
        } catch (IOException failure) {
            System.err.println("Unable to send stop to server in " + workDir + ": " + failure);
        }
    }

    private static void drainToLog(Process process, Path logFile) {
        try (BufferedReader remaining = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter log = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND, java.nio.file.StandardOpenOption.CREATE)) {
            String line;
            while ((line = remaining.readLine()) != null) {
                log.write(line);
                log.newLine();
            }
        } catch (IOException ignored) {
        }
    }

    private static Properties readProperties(Path path) {
        Properties values = new Properties();
        if (Files.isRegularFile(path)) {
            try (var stream = Files.newInputStream(path)) {
                values.load(stream);
            } catch (IOException ignored) {
            }
        }
        return values;
    }

    private static void store(Path path, Properties values) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (OutputStream output = Files.newOutputStream(path)) {
            values.store(output, "lintcardboard-live");
        }
    }

    record Arguments(Path javaExe, Path paperJar, Path agentJar, Path mixinJar, Path pluginJar, String mixinConfig, Path workDir, Path agentOut, Path mixinOut, Path verdictOut, String brandMarker, long timeoutSec) {
        static Arguments parse(String[] args) {
            Path javaExe = null;
            Path paperJar = null;
            Path agentJar = null;
            Path mixinJar = null;
            Path pluginJar = null;
            String mixinConfig = "lintcardboard.mixins.json";
            Path workDir = null;
            Path agentOut = null;
            Path mixinOut = null;
            Path verdictOut = null;
            String brandMarker = "(lintcardboard)";
            long timeoutSec = 600;
            for (String arg : args) {
                String[] entry = arg.split("=", 2);
                if (entry.length != 2) {
                    continue;
                }
                switch (entry[0]) {
                    case "--javaExe" -> javaExe = Path.of(entry[1]);
                    case "--paperJar" -> paperJar = Path.of(entry[1]);
                    case "--agentJar" -> agentJar = Path.of(entry[1]);
                    case "--mixinJar" -> mixinJar = Path.of(entry[1]);
                    case "--pluginJar" -> pluginJar = Path.of(entry[1]);
                    case "--mixinConfig" -> mixinConfig = entry[1];
                    case "--workDir" -> workDir = Path.of(entry[1]);
                    case "--agentOut" -> agentOut = Path.of(entry[1]);
                    case "--mixinOut" -> mixinOut = Path.of(entry[1]);
                    case "--verdictOut" -> verdictOut = Path.of(entry[1]);
                    case "--brandMarker" -> brandMarker = entry[1];
                    case "--timeoutSec" -> timeoutSec = Long.parseLong(entry[1]);
                    default -> {
                    }
                }
            }
            if (javaExe == null || paperJar == null || agentJar == null || mixinJar == null || pluginJar == null || workDir == null
                || agentOut == null || mixinOut == null || verdictOut == null) {
                throw new IllegalArgumentException("Missing required probe argument");
            }
            return new Arguments(javaExe, paperJar, agentJar, mixinJar, pluginJar, mixinConfig, workDir, agentOut, mixinOut, verdictOut, brandMarker, timeoutSec);
        }
    }
}
