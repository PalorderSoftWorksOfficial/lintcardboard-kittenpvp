# ADR 0004: Pinned versions and agent-first bootstrap proof

- Status: Accepted for M0 investigation
- Date: 2026-11-06

## Context

M0 requires verified upstream versions before any compatibility claim. ADR 0002
keeps the production bootstrap choice open until a reproducible proof exists.

## Verified versions

- Target: Minecraft 26.2 on Java 25 (local runtime 25.0.4).
- Paper 26.2 latest stable: build 129 (first stable build 83, 2026-07-26).
- Purpur 26.2: build 2633 stable. Leaf 26.2: experimental build 123.
- Folia 26.2: beta build 7, no stable. No fork may be claimed from Paper results.
- Fabric Loader: 0.20.1 latest stable; 0.19.5 pairs with 26.2/26.3 era mods.
- Fabric Loom 1.17 with Gradle 9.5.1 for 26.2 mod development.
- Fabric Mixin fork 0.17.3 with MixinExtras 0.5.5 observed in 26.2 crash reports.
- Build pins: Gradle 9.5.1, Kotlin 2.4.21 (JVM target 25), ASM 9.9, JUnit 5.13.4.
- ASM 9.9 reads the Java 25 class files produced by this build.

## Decision

Prove the bootstrap ordering with a Java instrumentation agent first. The agent
registers a ClassFileTransformer in premain, transforms one controlled class
before the JVM defines it, and records registration, transform and definition
timestamps to files. A subprocess probe loads the class, checks the ASM-added
marker field, and fails loudly when ordering does not hold. A second probe
proves a refused mandatory transformation exits nonzero and is never reported
as success.

## Consequences

- M0 exit evidence exists without a full Paper server: controlled-class
  transformation before definition is reproducible through the real agent path.
- Production selection stays open per ADR 0002. Launcher, game-provider and
  server-integration options still need comparison against a live 26.2 server.
- The transformer uses load-time registration only. Retransformation stays
  disabled until a use case requires it.
- Fork differences are not proven. Paper, Purpur and Leaf server runs are the
  next step, using the capability detector added in platform-spi.
