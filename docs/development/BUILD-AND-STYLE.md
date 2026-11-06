# Build and code style

## Build system

Use Gradle Kotlin DSL, a version catalog and the committed Gradle wrapper. Pin plugin and library versions. Avoid dynamic versions and unreviewed transitive dependency changes.

Java and Kotlin must target the same explicitly configured JVM toolchain. Configure tests for the same runtime baseline used by the selected server build.

## Language boundaries

Prefer Java for instrumentation, Mixin classes, ASM visitors and low-level APIs when interoperability makes it simpler. Prefer Kotlin for typed models, orchestration, configuration and diagnostics. Do not duplicate contracts in both languages.

## Source conventions

- Package root: club.kittenpvp.lintcardboard.
- One primary responsibility per class.
- Prefer immutable data models.
- Use explicit error/result types at subsystem boundaries.
- Preserve causes when wrapping exceptions.
- Avoid reflection in hot paths when stable handles can be cached.
- No code comments or Javadocs; make code self-describing.
- No empty catch blocks or swallowed exceptions.
- No global mutable state unless the lifecycle and ownership are explicit.

## Validation

Before a commit, run the relevant tests and a clean Gradle build. Report exact commands and outcomes. Do not claim that tests passed when they were skipped or could not run.