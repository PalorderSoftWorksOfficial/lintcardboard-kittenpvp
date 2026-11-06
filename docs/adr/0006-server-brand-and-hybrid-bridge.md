# ADR 0006: Leaf-style server brand and hybrid bridge plugin

- Status: Accepted, proven on live Paper 26.2 build 135
- Date: 2026-11-06

## Context

After the engine wrapper (ADR 0005), LintCardboard needs a visible server
identity like Leaf's and a concrete hybrid layer: a Paper plugin that wraps
the Paper API and NMS surface so adapted mods register into it and it
forwards their hooks, instead of mods touching the server directly.

## Decision

- New `server-brand` module owns the identity: id `lintcardboard`, name
  `LintCardboard`, version `0.1.0`, plus the startup-line format. It
  compiles to release 21 so the release-21 mixin sources can consume the
  same constants the host code uses; the constant inlines into merged
  bytecode, so transformed game classes gain no new runtime dependency.
- Two required overwrites apply the brand: `BrandMixin` replaces
  `ServerBuildInfoImpl.brandName`, and `ModNameMixin` replaces
  `MinecraftServer.getServerModName`. The brand id is deliberately left
  alone so brand-compatibility checks keep working.
- New `plugin` module (the planned host-plugin boundary) compiles against
  the real `paper-api:26.2.build.135-stable` from PaperMC's maven-public,
  never a stub. `LintCardboardPlugin` owns a `BridgeHub` for exactly its
  own enable/disable lifecycle, logs the brand it observes through the
  Bukkit API, and writes file evidence. The plugin jar bundles the brand
  classes because the plugin classloader is isolated.
- `BridgeHub` is pure Java with no Bukkit dependency: mods register named
  hooks, wrappers dispatch contexts, results forward in registration order,
  nulls are skipped, and a handler failure throws `BridgeException`
  naming the hook with its cause preserved. Loud failure, no silent drops.
- The live probe installs the plugin jar, then requires all of: branded
  log line, both mixins transformed, plugin evidence showing
  `Bukkit.getServer().getName()` as `LintCardboard` with the bridge
  active, server `Done`, and a clean exit.

## Proven findings

- Live log shows `This server is running LintCardboard version
  26.2-135-...` and `[LintCardboard] LintCardboard bridge active, server
  brand: LintCardboard`. Verdict SUCCESS on all eight checks.
- A project literally named `plugin` cannot be referenced from another
  build script with `tasks.named`; the probe build uses the deterministic
  jar path `libs/lintcardboard-plugin-0.1.0.jar` with an explicit
  `dependsOn` instead.
- Unit run across all modules: 20 tests, 0 failures.

## Consequences

- Brand changes are one constant plus required mixins; any unresolved
  brand target fails the boot loudly instead of shipping a half-branded
  server.
- Mod-facing hooks go through `BridgeHub.dispatch`, which is unit-tested
  independently of any server. Paper event wiring comes next and must be
  proven the same live way.
- Injector-style callbacks remain out of scope until game-loader
  visibility is solved (ADR 0005).
