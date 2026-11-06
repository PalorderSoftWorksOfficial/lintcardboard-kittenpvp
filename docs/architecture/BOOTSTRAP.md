# Bootstrap and launch integration

## Problem

Fabric transformations may need to run before a target Minecraft class is defined. A normal plugin enable callback is not sufficient evidence that the JVM has not already loaded that class. Prove bootstrap ordering rather than assume it.

## Approaches to investigate

### Java instrumentation agent
Evaluate premain, class-file transformers, ordering relative to server agents and packaging constraints. Establish whether the transformation framework can be initialized safely through this route.

### Custom launcher
Evaluate a launcher that owns process startup and supplies the host server entrypoint. Determine how Paper-derived launchers, bundlers and libraries are invoked and which assumptions are stable.

### Fabric Loader game-provider integration
Inspect the exact Fabric Loader SPI and Knot launch process. A game-provider interface alone does not mean an existing provider can run inside an already bootstrapped Paper server.

### First-class server integration
Investigate whether a small maintained patch or integration point in server bootstrap is needed. If so, isolate it and document its maintenance cost.

## Required proof

- exact startup order;
- transformation registration time;
- whether target classes are still undefined;
- mapping namespace and classloader identity;
- interoperability with the target server's transformations;
- clean startup and shutdown;
- behavior on Paper, then Purpur and Leaf.

Use a controlled class and a real Mixin/transformer. Logging "Mixin ready" is not proof.

## Decision policy

Do not select a production bootstrap architecture until the proof is reproducible. Record evidence and alternatives in an ADR. If the approach works only on one fork, document that and add the smallest possible fork-specific bootstrap layer. Keep the common runtime platform-neutral.