# Lifecycle and threading

## Host ownership

The host server remains authoritative for world state, chunks, players, connections, entities, inventories, ticks, persistence and shutdown. LintCardboard does not create a parallel server or tick loop.

## Lifecycle phases

Model bootstrap, transformation preparation, game initialization, server initialization, server started, world initialization and server stopping as explicit phases.

Map Fabric callbacks to host lifecycle hooks only when their semantics genuinely match. Document differences; do not make timing guarantees the host API cannot provide.

## Thread ownership

### Conventional Paper-derived servers
Use the documented server execution context for mutable game-state access. Do not perform unsafe world mutations from arbitrary workers.

### Folia
Treat region ownership as a first-class constraint. Use the appropriate global, region, entity or async scheduler based on the task. Entity work must respect entity scheduler ownership. Region work must execute in the owning region. Cross-region operations need an explicit supported design.

Do not expose a fake universal main-thread executor that hides region ownership.

## Worker pool

Background work may include archive reads, metadata parsing, dependency analysis, report generation and non-game I/O. It must not directly mutate live world state.

## Reload policy

Initial releases may reload configuration where safe. Installing/removing mods, changing entrypoints, modifying Mixins or changing transformation configuration requires a process restart. Do not advertise arbitrary hot-unloading.