# ADR 0003: Java and Kotlin hybrid

- Status: Proposed
- Date: 2026-11-06

## Context

The runtime integrates with Java-based Minecraft, Fabric Loader, Mixin and server APIs while also benefiting from Kotlin's concise models and orchestration code.

## Decision

Use Gradle Kotlin DSL with both Java and Kotlin source sets. Keep both compilers on the same explicit JVM target. Prefer Java for instrumentation, Mixin and low-level JVM integration where compatibility favors Java. Prefer Kotlin for domain models, orchestration, configuration and diagnostics.

## Consequences

- Modules must define clear language boundaries.
- Do not duplicate APIs in both languages.
- The version catalog pins Kotlin, Gradle plugins and runtime dependencies.
- Generated code and compiler options must be validated by CI.