# ADR 0005: Horizon-style own Mixin wrapper with live Paper proof

- Status: Accepted, proven on live Paper 26.2 build 135
- Date: 2026-11-06

## Context

LintCardboard needs real SpongePowered Mixin support on Paper servers without
depending on Fabric Loader's Knot. CraftCanvasMC Horizon proves the pattern:
host the Mixin engine with an own `IMixinService`, drive it from an own
transformer, and launch the game under it. ADR 0002 kept the production
bootstrap open; this decision records the wrapper design that earned a live
proof.

## Decision

Wrap `net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7` (the same engine Horizon
uses, with ASM 9.10.1) in a new `mixin-support` module behind our own API:

- `LintCardboardMixinService` implements the engine service SPIs and resolves
  classes from configured roots plus the loader active during transformation.
- `MixinBlackboard` provides the engine global property service.
- `MixinTransformation` drives `IMixinTransformer.transformClassBytes`, so
  frame computation stays inside the engine's own writer.
- `LintCardboardTransformer` is the single `ClassFileTransformer` registered
  by the javaagent, with ordered services and an opt-in package gate.
- `MixinHost` owns engine lifecycle and writes file evidence. Every start and
  every applied class is recorded; failures carry subject, requirement, cause
  and a stack-chain summary. Nothing is ever reported successful on failure.
- The `bootstrap-agent` shades the wrapper, the engine and ASM into one
  agent jar with only our service registrations, and optionally starts the
  engine from agent arguments.

## Proven findings

- Live proof on Paper 26.2 build 135 (sha256 verified): a `BrandMixin`
  overwrites `ServerBuildInfoImpl.brandName`, the server prints
  `This server is running (lintcardboard) Paper version ...`, reaches
  `Done`, and stops cleanly. Verdict file records every check.
- Mixin configs we ship set `required: true`. An unresolved `require = 1`
  injector then throws instead of silently doing nothing. Default Mixin
  semantics ignore unresolved injectors, which our tests pin down.
- Mixin classes must be class file v65 or older while the engine runs under
  compatibility `JAVA_21` with ASM 9.x. Test fixtures and the live mixin
  compile with release 21 in dedicated source sets; everything else stays
  on 25.
- The shaded agent jar must carry `Implementation-Version` matching the
  shaded ASM, because the engine detects the ASM version from the package
  metadata and refuses newer compatibility levels without it.
- Paperclip resolves and loads the real server jar after premain, so engine
  selection must wait for game classes. The transformer gate
  (`net/minecraft/server/`, `io/papermc/paper/`) keeps the engine away from
  the launcher classpath until the server loader exists.
- Paperclip's game loader does not delegate to the application loader, so
  injected code may not reference Mixin runtime types. `@Overwrite` works
  today; `@Inject` callbacks need a loader-bridging strategy first. That
  work is explicitly out of scope and recorded as a limitation, not claimed.
- Appending the engine jar to the bootstrap search is rejected: bootstrap
  code cannot see platform modules such as `java.sql`, which the engine
  needs. The attempt failed loudly with evidence and was reverted.
- Paper's `fill-data` object URLs returned 404 for every tested build, so
  the live test downloads through the mcjarhub mirror and verifies the
  PaperMC-published sha256 before use.

## Consequences

- `mixin-support` owns engine details; the agent only wires lifecycle.
- Live-server behavior is observable and asserted, not assumed:
  unit tests prove engine application and loud failure, and
  `livePaperTest` proves end-to-end branding on the pinned Paper build.
- Purpur and Leaf runs reuse the same probe once their builds are local.
- Mixin injector support stays limited to overwrites until game-loader
  visibility for callback types is solved and proven the same way.
