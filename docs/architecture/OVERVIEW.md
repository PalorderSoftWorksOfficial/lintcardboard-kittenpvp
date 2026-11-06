# System architecture

## Purpose

LintCardboard adapts Fabric server-side mod behavior to a Paper-derived host runtime. It does not run Fabric as a second server. The host owns the Minecraft world, tick loop, connections, entities, persistence and shutdown.

The architecture has two independent axes:

1. Minecraft version: mappings, game classes, registry behavior and version-specific hooks.
2. Server platform: Paper API baseline, fork-specific capabilities, schedulers and implementation differences.

Do not merge these axes into one collection of version/fork conditionals.

## Logical layers

### Bootstrap
Runs before target classes are defined when transformations require it. Owns launch integration, instrumentation setup and initialization order. The bootstrap design remains a feasibility question; see BOOTSTRAP.md.

### Fabric loader
Uses upstream Fabric Loader functionality where practical for mod metadata, dependency resolution, nested JARs, environment filtering, classloading and entrypoints. See ../loader/LOADER.md.

### Transformation
Coordinates Mixin, ASM, access wideners and class tweakers. It must validate targets against the exact host server build. See ../transformation/TRANSFORMATIONS.md.

### Compatibility API
Provides explicit adapters for selected Fabric API contracts. Each adapter documents semantic differences rather than pretending Bukkit/Paper and Fabric APIs are interchangeable. See ../api/API-COMPATIBILITY.md.

### Platform SPI
Describes detected host capabilities and provides lifecycle, scheduler, registry, networking and server access services. Paper is the baseline. Fork adapters extend or replace only behavior that actually differs. See ../platforms/PLATFORMS.md.

### Version layer
Minecraft 26.2-specific hooks and mappings live in a version-specific module. Future Minecraft versions get their own implementation rather than silently sharing incompatible assumptions.

### Diagnostics
Produces startup reports, per-mod compatibility decisions, transformation failures and platform descriptors. A failed mandatory operation must never be hidden by a success status.

## Runtime invariants

- One authoritative game server.
- One authoritative world and persistence model.
- No additional tick loop.
- No duplicate game-class identities.
- No arbitrary world mutation from worker threads.
- No transformation after a target class has already been defined.
- No platform support claim without platform-specific evidence.
- No successful mod status when mandatory entrypoints or transformations failed.

## Dependency direction

- Common contracts do not depend on platform implementations.
- Loader and transformation modules depend on common contracts and upstream libraries.
- Platform adapters implement the platform SPI.
- Version modules provide game-version-specific services.
- Plugin and launcher assemblies compose the modules.

Avoid circular module dependencies. Record any necessary exception in an ADR.

## First vertical slice

1. Start a target Paper server with the selected bootstrap.
2. Discover a test Fabric mod.
3. Resolve its metadata and dependencies.
4. Execute a compatible initializer at a verified lifecycle point.
5. Register a supported callback.
6. Observe the callback on the host server.
7. Shut down cleanly.
8. Repeat on Purpur and Leaf when matching builds are available.

A separate controlled test must validate pre-definition transformation. A successful initializer test is not proof of Mixin compatibility.