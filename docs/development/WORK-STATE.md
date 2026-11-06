# Work state

Last updated: 2026-11-06
Branch: main
Repository path: D:\lintcardboard-kittenpvp
Public remote: https://github.com/PalorderSoftWorksOfficial/lintcardboard-kittenpvp

## Current objective

Mixin wrapper proven live on Paper 26.2 build 135. Next: repeat the probe
on Purpur and Leaf, then start the M2 loader vertical slice.

## Verified completed

- Pinned Paper 26.2 build 135 stable (sha256
  ef3dbc68b013deafdbbee3bebf1ba351671ebcda94a8f136809b7532d5ba2fe1).
  Paper's fill-data object URLs returned 404 for all tested builds, so the
  live test downloads through the mcjarhub mirror and verifies the
  PaperMC-published checksum first.
- New `mixin-support` module wraps sponge-mixin 0.17.3+mixin.0.8.7 with our
  own `IMixinService`, blackboard, engine driver and agent transformer,
  following Horizon's proven shape without copying its launcher.
- `livePaperTest` boots the real Paper 26.2-135 server with the agent and a
  `BrandMixin` overwrite of `ServerBuildInfoImpl.brandName`. The live log
  shows `This server is running (lintcardboard) Paper version ...`, the
  server reaches `Done (15.031s)!`, stops cleanly, and the verdict file
  records SUCCESS for install, engine start, target transformation,
  branding, boot and exit.
- Fresh `./gradlew test --rerun-tasks`: 13 tests, 0 failures.
- ADR 0005 records the wrapper decision and every hard finding:
  `required: true` configs, release-21 mixin classes under JAVA_21,
  shaded-ASM `Implementation-Version`, the transformer gate for
  Paperclip's late server loader, and the `@Inject` callback visibility
  limit (overwrites only, until loader bridging is proven).
- Earlier M0 proof and platform detection still pass unchanged.

## Current milestone

M0 exit evidence complete including live-server mixin application. M1
foundation extended with mixin-support and live-tests.

## Next actions

1. Run `livePaperTest` against Purpur 26.2 and Leaf 26.2 builds and record
   per-fork verdicts through the platform capability detector.
2. Begin M2 loader vertical slice with real fabric.mod.json fixtures.
3. Solve game-loader visibility for `@Inject` callbacks and prove it the
   same live way before claiming injector support.

## Verification log

- `./gradlew test --rerun-tasks`: 13 tests, 0 failures, BUILD SUCCESSFUL.
- `./gradlew :live-tests:livePaperTest`: BUILD SUCCESSFUL, live verdict
  SUCCESS with brand line observed in server.log.
- No Purpur or Leaf server run has happened yet.
