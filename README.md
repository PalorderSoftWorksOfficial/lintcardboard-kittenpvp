# LintCardboard

**Fabric server-side mods, meeting Paper's world.**

LintCardboard is an experimental compatibility project exploring how Fabric's mod loader, lifecycle and APIs can coexist with Paper and the servers built around it.

Paper is the starting point, not the finish line. The project is being designed for Paper, Purpur and Leaf, with room for other Paper-derived servers. Folia is a separate challenge: its region-threaded execution model changes how server work must be scheduled.

The aim is practical: make useful Fabric server-side mods available to more server operators without replacing the server they already rely on.

> **Status: early research and development.** There is no claim of universal Fabric-mod compatibility, and no production-ready release is available yet.

## Why this exists

Fabric mods and Paper plugins live in different runtime ecosystems. Fabric Loader handles mod discovery, dependencies, entrypoints and classloading; Fabric API adds hooks into Minecraft. Paper has its own plugin lifecycle and modifies the Minecraft server implementation. Finding a JAR is not the same as safely running it.

LintCardboard is an attempt to solve that problem deliberately rather than hide the difficult parts behind a single loader command.

The project takes architectural inspiration from [Cardboard](https://github.com/PalorderSoftWorksOfficial/cardboard), while building a separate implementation with a Java/Kotlin codebase and a broader Paper-fork target.

## Server targets

| Server | Role | Status |
|---|---|---|
| Paper | Common API baseline and first integration target | Planned; unverified |
| Purpur | Paper-compatible platform with optional fork adapter | Planned; unverified |
| Leaf | Primary deployment target for KittenPvP | Planned; unverified |
| Pufferfish | Additional Paper-derived target | Under evaluation |
| Gale | Additional Paper-derived target | Under evaluation |
| Folia | Dedicated region-threaded execution adapter | Research required |
| Other Paper forks | Capability-based integration | Case by case |

These are project targets, not compatibility guarantees. Support will be recorded against exact Minecraft versions, server builds, loader versions and test results.

## Design principles

- **One server owns the world.** The host remains responsible for ticks, players, entities, chunks and persistence.
- **Paper is the common ground.** Shared behavior belongs in a common platform adapter; fork-specific differences stay isolated.
- **Capabilities over name checks.** Detect what a server actually exposes instead of assuming every fork behaves identically.
- **Transformations happen early.** Mixins and bytecode changes must be prepared before their target classes are defined.
- **Failures stay visible.** A mod with a failed mandatory transformation is not reported as loaded.
- **Evidence before compatibility claims.** Support means a reproducible test on the relevant platform and build.
- **No second tick loop.** LintCardboard adapts the host runtime; it does not start another Minecraft server inside it.

## Architecture at a glance

The implementation is split into focused responsibilities:

- **Bootstrap and transformation:** early startup, instrumentation and class transformation.
- **Fabric loader integration:** metadata, dependencies, nested JARs, classloading and entrypoints.
- **Compatibility API:** adapters for selected Fabric lifecycle, event, command, networking and registry behavior.
- **Platform SPI:** a common Paper baseline plus focused Purpur, Leaf, Folia and other fork adapters.
- **Version layer:** Minecraft-specific mappings and behavior isolated from the platform layer.
- **Diagnostics and tests:** compatibility reports, reproducible test mods and per-platform integration tests.

Start with [AGENTS.md](AGENTS.md), then open only the relevant design note. The [architecture overview](docs/architecture/OVERVIEW.md) explains the boundaries.

## Development status

The repository is in the feasibility and foundation stage. The first meaningful milestone is not a large modpack: it is proving that a small Fabric test mod can be discovered, initialized and execute a supported callback inside the host server. A controlled Mixin test must also demonstrate that transformation happens before the target class is defined.

See the [roadmap](docs/development/ROADMAP.md) and [current work state](docs/development/WORK-STATE.md) for verified progress.

## Compatibility reports

A compatibility result is specific to:

**Minecraft version × server platform/build × Fabric Loader version × mod version**

The project uses four result levels:

- **SUPPORTED** — reproduced successfully in the stated test environment.
- **CONDITIONAL** — works with documented limitations.
- **REJECTED** — a known requirement cannot be met safely.
- **UNKNOWN** — there is not enough evidence.

A result on Paper does not automatically prove the same mod works on Purpur, Leaf or Folia.

## Building

The Gradle wrapper and exact dependency versions will be committed after the initial build foundation is validated. Until then, do not assume that a successful build has been established.

The intended build uses Gradle Kotlin DSL with Java and Kotlin compiled to the same JVM target.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for the workflow. Architectural changes should include an ADR under docs/adr/. Compatibility changes should include a reproducible test and an update to the compatibility matrix.

A compile is useful. A passing server integration test is evidence.

## License

License selection is pending. No source should be copied from reference projects until its license and the intended reuse have been reviewed.