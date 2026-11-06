# ADR 0002: Bootstrap strategy remains open pending proof

- Status: Accepted for investigation
- Date: 2026-11-06

## Context

Fabric transformations may require registration before target Minecraft classes are defined. Paper-derived server launchers have their own classloading and transformation behavior. A plugin enable callback cannot be assumed to be early enough.

## Decision

Do not select a production bootstrap architecture until a reproducible proof compares an instrumentation agent, custom launcher, Fabric Loader game-provider integration and a first-class server integration.

## Consequences

- The first milestone is feasibility, not broad API implementation.
- No module should claim a working Fabric runtime before the proof passes.
- Startup order, class identity and transformation timing must be recorded for every tested platform.
- If bootstrap differs across forks, isolate that difference in bootstrap adapters.

## Acceptance evidence

A controlled class is transformed before JVM definition through the chosen process, and the actual host server starts and stops correctly.