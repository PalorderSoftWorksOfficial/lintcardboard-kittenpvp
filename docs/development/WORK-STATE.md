# Work state

Last updated: 2026-11-06
Branch: main
Repository path: D:\lintcardboard-kittenpvp
Public remote: https://github.com/PalorderSoftWorksOfficial/lintcardboard-kittenpvp

## Current objective

Leaf-style branding and the hybrid bridge plugin are proven live on Paper
26.2 build 135. Next: wire real Paper events into BridgeHub dispatch, then
start the M2 loader vertical slice.

## Verified completed

- New `server-brand` module: id `lintcardboard`, name `LintCardboard`,
  version `0.1.0`, startup-line format, all unit-tested. Release 21 so the
  mixin sources share the exact constants (they inline, no new game
  runtime dependency).
- New `plugin` module against real paper-api 26.2.build.135-stable:
  `LintCardboardPlugin` owns a pure-Java `BridgeHub` (ordered forwarding,
  null skipping, loud hook-named failures, unregister handles), logs the
  brand through the Bukkit API, and writes evidence. 5 hub tests green.
- `livePaperTest` now proves the full hybrid story: the live log shows
  `This server is running LintCardboard version ...`, both brand mixins
  transformed, the plugin observes `Bukkit.getServer().getName()` as
  `LintCardboard` with the bridge active, the server reaches `Done`, and
  the verdict records SUCCESS on all eight checks.
- Fresh `./gradlew test --rerun-tasks`: 20 tests, 0 failures.
- ADR 0006 records the brand and bridge design, including the `plugin`
  project cross-reference quirk and the deterministic jar path fix.
- Earlier M0/M1 proofs and ADR 0005 still hold unchanged.

## Current milestone

M1 foundation extended with brand and hybrid bridge, both live-proven.
`@Inject` callbacks remain out of scope until loader bridging is solved.

## Next actions

1. Wire Paper events (join, tick, enable phases) into BridgeHub dispatch
   with a mod-side listener fixture and extend the live verdict.
2. Run `livePaperTest` against Purpur 26.2 and Leaf 26.2 and record
   per-fork verdicts.
3. Begin M2 loader vertical slice with real fabric.mod.json fixtures.

## Verification log

- `./gradlew test --rerun-tasks`: 20 tests, 0 failures, BUILD SUCCESSFUL.
- `./gradlew :live-tests:livePaperTest`: BUILD SUCCESSFUL, live verdict
  SUCCESS with brand line, plugin evidence and clean exit.
- No Purpur or Leaf server run has happened yet.
