plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile> {
    options.release.set(25)
}

tasks.withType<org.gradle.api.tasks.testing.Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.asm)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.named<org.gradle.jvm.tasks.Jar>("jar") {
    manifest {
        attributes(
            "Premain-Class" to "club.kittenpvp.lintcardboard.bootstrap.LintCardboardAgent",
            "Agent-Class" to "club.kittenpvp.lintcardboard.bootstrap.LintCardboardAgent",
            "Can-Redefine-Classes" to "false",
            "Can-Retransform-Classes" to "false",
        )
    }
    from({
        configurations.runtimeClasspath.get().filter { it.name.startsWith("asm-") }.map { zipTree(it) }
    }) {
        exclude("module-info.class")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val agentJarTask = tasks.named<org.gradle.jvm.tasks.Jar>("jar")
tasks.named<org.gradle.api.tasks.testing.Test>("test") {
    dependsOn(agentJarTask)
    systemProperty("lintcardboard.agent.jar", agentJarTask.get().archiveFile.get().asFile.absolutePath)
}
