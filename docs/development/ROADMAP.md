# Development roadmap

Milestones are evidence gates, not calendar promises.

## M0 — Feasibility

- Inspect current Paper, Purpur and Leaf 26.2 bootstraps.
- Verify compatible Fabric Loader, Fabric API, Mixin, Loom, Gradle, Kotlin and Java versions.
- Compare instrumentation agent, launcher and server integration approaches.
- Prove loader/class identity behavior.
- Demonstrate a controlled class transformation before class definition.
- Record decisions and reproduction steps.

Exit gate: a reproducible bootstrap and transformation proof on the first reference platform. If the design fails, record evidence and investigate the next viable approach.

## M1 — Build foundation

- Gradle Kotlin DSL multi-project build.
- Java/Kotlin toolchain alignment.
- Version catalog and wrapper.
- Common contracts and platform SPI.
- Formatting, static checks and unit tests.

## M2 — First loader vertical slice

- Real Fabric metadata and dependency handling.
- Test mod discovery and initialization.
- Verified lifecycle mapping.
- One supported callback.
- Clean startup and shutdown.

## M3 — Paper ecosystem adapters

- Paper common adapter.
- Purpur and Leaf verification.
- Capability detection.
- Fork-specific behavior only where evidence requires it.

## M4 — Transformations

- Mixin service.
- ASM and mapping integration.
- Access wideners and class tweakers.
- Platform-specific target validation and failure reports.

## M5 — Fabric API coverage

Add server-side API adapters incrementally and test each contract.

## M6 — Additional platforms

Evaluate Pufferfish, Gale and other Paper forks against actual 26.2 builds. Implement Folia's distinct execution adapter only with region-aware tests.

## M7 — Beta readiness

- documented compatibility matrix;
- repeatable integration suite;
- performance baseline;
- installation and troubleshooting guides;
- reproducible packaging and CI;
- no unresolved false-success paths.

Do not skip a blocked feasibility question by implementing speculative upper layers.