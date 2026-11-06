# Work state

Last updated: 2026-11-06
Branch: main
Repository path: D:\lintcardboard-kittenpvp
Public remote: https://github.com/PalorderSoftWorksOfficial/lintcardboard-kittenpvp

## Current objective

M0 feasibility proof complete for bootstrap ordering; M1 build foundation in
place. Next: live Paper 26.2 server integration.

## Verified completed

- Pinned upstream versions: Paper 26.2 build 129 stable, Purpur 2633 stable,
  Leaf 26.2 experimental 123, Folia beta only, Fabric Loader 0.20.1 stable,
  Loom 1.17 with Gradle 9.5.1, Mixin fork 0.17.3, Kotlin 2.4.21, ASM 9.9,
  JUnit 5.13.4, Java 25 toolchain with JVM target 25.
- Gradle Kotlin DSL multi-project build with wrapper 9.5.1, version catalog,
  and modules common, platform-spi, bootstrap-agent.
- Java instrumentation agent proves a controlled class is transformed before
  JVM definition: registration, transform and definition timestamps are
  recorded and asserted in order, with class identity and marker checks.
- Mandatory transformation refusal exits nonzero and is recorded as FAILURE,
  never as success.
- Capability-based platform detector covers Paper, Purpur, Leaf, Pufferfish,
  Gale, Folia, generic forks and unknown servers with preserved evidence.
- Fresh `./gradlew test --rerun-tasks`: 11 tests, 0 failures, BUILD SUCCESSFUL.
  Clean `./gradlew clean build`: BUILD SUCCESSFUL.
- ADR 0004 records versions and the agent-first proof decision.

## Current milestone

M0 exit evidence recorded for the controlled-class proof. M1 foundation done.
Full server bootstrap comparison still open per ADR 0002.

## Next actions

1. Run the agent against a live Paper 26.2 build 129 server and record
   startup, transformation and shutdown evidence.
2. Repeat on Purpur 2633 and Leaf 123 when builds are available locally.
3. Begin M2 loader vertical slice with real fabric.mod.json fixtures.

## Verification log

- `./gradlew test --rerun-tasks`: 11 tests, 0 failures, BUILD SUCCESSFUL.
- `./gradlew clean build`: BUILD SUCCESSFUL.
- Agent jar verified to contain Premain-Class manifest and shaded ASM.
- No Paper server integration test has been run yet.
