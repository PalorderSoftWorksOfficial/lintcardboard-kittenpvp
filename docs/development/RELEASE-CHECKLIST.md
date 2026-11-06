# Release checklist

A release must not claim general compatibility based on a single successful server startup.

## Build
- [ ] Clean Gradle build succeeds using the committed wrapper.
- [ ] Dependency versions are pinned and reviewed.
- [ ] Artifacts are reproducible and have recorded checksums.
- [ ] No secrets, local server files or generated runtime data are included.

## Runtime
- [ ] Bootstrap ordering is demonstrated.
- [ ] Loader and class identity behavior are verified.
- [ ] Mandatory transformation failures are visible.
- [ ] Shutdown does not leave runtime threads or resources.
- [ ] No parallel server or tick loop exists.

## Platforms
- [ ] Exact server build identifiers are recorded.
- [ ] Paper integration tests pass.
- [ ] Purpur integration tests pass before claiming Purpur support.
- [ ] Leaf integration tests pass before claiming Leaf support.
- [ ] Other forks are named and tested individually.
- [ ] Folia passes region-aware tests before claiming support.

## Fabric compatibility
- [ ] Mod compatibility results include exact versions.
- [ ] Fixture mod suite passes.
- [ ] Unsupported APIs and limitations are documented.
- [ ] Compatibility matrix is current.

## Performance and documentation
- [ ] Baseline and LintCardboard measurements are reproducible.
- [ ] README and installation docs match the actual release.
- [ ] Known issues and upgrade requirements are published.
