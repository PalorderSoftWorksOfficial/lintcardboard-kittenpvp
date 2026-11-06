plugins {
    `java-library`
}

dependencies {
    api(project(":server-brand"))
    compileOnly(libs.paper.api)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.release.set(25)
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("lintcardboard-plugin")
    archiveVersion.set("0.1.0")
    from(project(":server-brand").extensions.getByType<SourceSetContainer>()["main"].output)
}
