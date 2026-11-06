# Fabric Loader integration

## Principle

Reuse upstream Fabric Loader mechanisms wherever technically valid. Do not build a second, incomplete implementation of Fabric's metadata parser, dependency resolver or entrypoint system without a demonstrated need.

## Areas to inspect

- fabric.mod.json metadata and schema;
- mod identifiers and versions;
- required, optional and conflicting dependencies;
- nested JARs;
- environment declarations;
- language adapters;
- entrypoints and their ordering;
- mapping resolver and namespace behavior;
- loader classpath and classloader identity;
- interaction with Knot and the game provider.

## Class identity

All mods that share Minecraft or Fabric API types must resolve those types consistently. Arbitrarily placing every mod in an unrelated classloader is not a compatibility strategy. Understand upstream loader behavior before changing it.

## Discovery pipeline

1. Enumerate configured mod sources.
2. Validate archive structure.
3. Parse metadata safely.
4. Identify environment requirements.
5. Resolve mandatory dependencies and conflicts.
6. Identify nested JARs.
7. Determine entrypoints and language adapters.
8. Run platform/version compatibility analysis.
9. Prepare the loader environment.
10. Initialize only after the bootstrap has proven it is safe.

## Failure behavior

Report the offending mod ID, file, requirement and cause. Missing mandatory dependencies, invalid metadata and unsupported environments must not become warning-only success states.

## Test fixtures

Maintain small independent Fabric mods for metadata, dependencies, nested JARs, initializers, events, commands and transformations. A fabricated descriptor is not a substitute for building and loading a real fixture JAR.