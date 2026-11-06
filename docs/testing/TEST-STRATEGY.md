# Testing strategy

## Test layers

### Unit tests
Metadata parsing, dependency graph logic, capability evaluation, platform detection, configuration validation and compatibility report generation.

### Fixture mods
Build real Fabric JARs with valid metadata for a basic initializer, dependencies and conflicts, nested JAR, server-only environment, lifecycle callback, event registration, command registration, networking, controlled Mixin and invalid transformation.

### Bootstrap integration tests
Start an actual target server with the selected bootstrap and prove:
- transformer registration precedes target class definition;
- class identity is consistent;
- a real Fabric entrypoint runs at the intended phase;
- a supported callback executes;
- shutdown completes without leaked threads or resources.

### Platform matrix
Run common integration tests on Paper first, then Purpur and Leaf. Add other forks only when a real 26.2 build exists and is pinned. Folia requires dedicated region ownership and concurrency tests.

### Performance tests
Compare the same server build and configuration with and without LintCardboard. Measure MSPT p50/p95/p99, TPS, allocation rate, CPU, heap, GC pauses, startup and transformation costs. Record hardware, flags, build identifiers and workload.

## Evidence rules

Store reproducible commands, server build identifiers, logs and test summaries. Do not convert an unrun test into a pass. Update the compatibility matrix after each meaningful integration run.