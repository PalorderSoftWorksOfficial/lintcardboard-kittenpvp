# Platform support policy

## What a support claim means

A platform is supported only for an exact combination of:
- Minecraft version;
- platform and build identifier;
- Java runtime;
- Fabric Loader version;
- LintCardboard revision;
- tested mod versions and capabilities.

The same mod passing on Paper is not proof for Purpur or Leaf. The same Paper API surface does not prove that internal class transformations are interchangeable.

## Support tiers

- Experimental: the adapter is being investigated; no compatibility promise.
- Partial: the server starts and a documented feature subset passes tests.
- Supported: required test suite passes on the published build and no known critical issue invalidates the claim.
- Unsupported: a necessary runtime behavior cannot be provided safely.
- Unknown: insufficient evidence.

## Fork adapter policy

Prefer the shared Paper adapter. Add a fork-specific adapter only where a verified difference requires it. Keep platform detection and capabilities centralized.

## Folia

Folia requires region-aware execution and ownership guarantees. Never mark it supported because the plugin enables or the global scheduler runs. Test entity, region, global and async scheduling behavior as appropriate.

## Version policy

Pin build identifiers in test configuration. Do not say "26.2 supported" when only one unrecorded local build was tried. Re-run compatibility tests when updating server builds, loader, Mixin or mapping versions.