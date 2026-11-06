import java.io.File
import java.net.URI
import java.security.MessageDigest
import java.time.Duration

plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(libs.sponge.mixin)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

sourceSets {
    create("liveMixin") {
        java.srcDir("src/live-mixin/java")
        resources.srcDir("src/live-mixin/resources")
    }
}

dependencies {
    add("liveMixinImplementation", libs.sponge.mixin)
}

tasks.named<org.gradle.api.tasks.compile.JavaCompile>("compileLiveMixinJava") {
    options.release.set(21)
}

val liveDir = layout.buildDirectory.dir("live-paper")
val paperJar = liveDir.map { it.file("paper-26.2-135.jar") }
val paperUrl = "https://mcjarhub.com/api/v1/types/paper/versions/26.2/download"
val paperSha256 = "ef3dbc68b013deafdbbee3bebf1ba351671ebcda94a8f136809b7532d5ba2fe1"
val paperUserAgent = "lintcardboard/0.1 (https://github.com/PalorderSoftWorksOfficial/lintcardboard-kittenpvp)"

fun sha256Hex(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { stream ->
        val buffer = ByteArray(65536)
        while (true) {
            val read = stream.read(buffer)
            if (read == -1) {
                break
            }
            digest.update(buffer, 0, read)
        }
    }
    return digest.digest().joinToString("") { byte -> "%02x".format(byte) }
}

tasks.register("downloadPaper") {
    outputs.file(paperJar)
    doLast {
        val target = paperJar.get().asFile
        if (target.exists() && sha256Hex(target) == paperSha256) {
            logger.lifecycle("Paper jar already present with matching checksum.")
            return@doLast
        }
        target.parentFile.mkdirs()
        logger.lifecycle("Downloading Paper 26.2 build 135 via mcjarhub mirror (checksum verified against PaperMC)...")
        val connection = URI(paperUrl).toURL().openConnection()
        connection.setRequestProperty("User-Agent", paperUserAgent)
        connection.getInputStream().use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        }
        val actual = sha256Hex(target)
        if (actual != paperSha256) {
            target.delete()
            throw GradleException("Paper jar checksum mismatch: expected $paperSha256 but was $actual")
        }
        logger.lifecycle("Paper jar verified.")
    }
}

val liveMixinJar = tasks.register<org.gradle.jvm.tasks.Jar>("liveMixinJar") {
    archiveBaseName.set("live-mixin")
    from(sourceSets["liveMixin"].output)
}

val agentJarPath = project(":bootstrap-agent").tasks.named<org.gradle.jvm.tasks.Jar>("jar").flatMap { it.archiveFile }

tasks.register<JavaExec>("livePaperTest") {
    group = "verification"
    description = "Boots a live Paper 26.2 server with the LintCardboard agent and branding mixin, then verifies the proof."
    dependsOn("downloadPaper", liveMixinJar, ":bootstrap-agent:jar")
    mainClass.set("club.kittenpvp.lintcardboard.live.LivePaperProbe")
    classpath = sourceSets.main.get().runtimeClasspath
    val work = liveDir.map { it.dir("server") }
    val mixinJarFile = liveMixinJar.flatMap { it.archiveFile }
    val agentFile = agentJarPath.get().asFile.absolutePath
    val mixinFile = mixinJarFile.get().asFile.absolutePath
    val paperFile = paperJar.get().asFile.absolutePath
    val workPath = work.get().asFile.absolutePath
    val livePath = liveDir.get().asFile.absolutePath
    val javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"
    args(
        "--javaExe=$javaBin",
        "--paperJar=$paperFile",
        "--agentJar=$agentFile",
        "--mixinJar=$mixinFile",
        "--mixinConfig=lintcardboard.mixins.json",
        "--workDir=$workPath",
        "--agentOut=$workPath/lintcardboard-agent.properties",
        "--mixinOut=$workPath/lintcardboard-mixin.properties",
        "--verdictOut=$livePath/live-verdict.properties",
        "--brandMarker=(lintcardboard)",
        "--timeoutSec=600",
    )
    timeout.set(Duration.ofMinutes(15))
}
