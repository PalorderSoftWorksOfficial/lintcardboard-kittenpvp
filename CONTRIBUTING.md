# Contributing to LintCardboard

LintCardboard is early-stage research and development. The most useful contribution is a reproducible result: a test that passes, a transformation failure that is isolated, or a platform difference backed by source and runtime evidence.

## Before changing code

1. Read AGENTS.md.
2. Read docs/development/WORK-STATE.md.
3. Open only the design notes relevant to the change.
4. Check the current upstream API and exact server build.
5. Preserve existing local changes.

## Design changes

Use an architecture decision record under docs/adr/ for decisions that affect bootstrap, classloading, module boundaries, API semantics or platform support. Include the problem, alternatives, evidence, decision and consequences.

Keep Minecraft-version-specific code separate from server-platform code. Prefer the common Paper adapter when behavior is genuinely common. Add a fork adapter only for a verified difference.

## Tests and compatibility

A passing compile does not prove runtime compatibility. Include unit tests for logic and integration tests for host-server behavior. Record exact server build, Java runtime, Fabric Loader version and mod version in compatibility evidence.

Never mark a platform or mod supported based on a different fork's test result.

## Code conventions

- Root package: club.kittenpvp.lintcardboard.
- Java and Kotlin use the same explicit JVM target.
- No code comments or Javadocs.
- Keep classes focused and names descriptive.
- Do not swallow exceptions or report fake success.
- Do not commit runtime worlds, generated build output, credentials or local configuration.

## Before committing

Run the relevant tests and a clean Gradle build using the wrapper. Update the compatibility matrix and WORK-STATE.md when the change affects either. Keep commits focused and explain the verified behavior in the commit message.