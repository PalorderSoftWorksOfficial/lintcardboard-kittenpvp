plugins {
    id("org.jetbrains.kotlin.jvm")
    `java-library`
}

dependencies {
    api(project(":common"))
    implementation(libs.asm)
    implementation(libs.sponge.mixin)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

sourceSets {
    create("mixinFixture") {
        java.srcDir("src/mixin-fixture/java")
    }
}

dependencies {
    add("mixinFixtureImplementation", libs.sponge.mixin)
}

tasks.named<JavaCompile>("compileMixinFixtureJava") {
    options.release.set(21)
}

tasks.withType<Test> {
    useJUnitPlatform()
    classpath += files(sourceSets["mixinFixture"].output)
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
