# LintCardboard agent guide

Read this file first at the start of every task. It is the index and operating contract, not the full project specification.

## Mission
Build LintCardboard: a Java/Kotlin Fabric-mod compatibility runtime for Paper and Paper-derived Minecraft servers. Minecraft 26.2 is the first target. Paper is the common API baseline; Purpur, Leaf and other forks use capability detection and focused adapters. Folia requires a distinct execution model. Do not claim compatibility without tests.

## First actions
1. Inspect the working directory, Git branch, status and remotes. Preserve existing user work.
2. Read docs/development/WORK-STATE.md.
3. Read only the topic document needed for the current task:
   - system design: docs/architecture/OVERVIEW.md
   - module boundaries: docs/architecture/MODULE-BOUNDARIES.md
   - launch and early transformation: docs/architecture/BOOTSTRAP.md
   - server forks and capabilities: docs/platforms/PLATFORMS.md
   - platform claims: docs/platforms/SUPPORT-POLICY.md
   - Fabric discovery and dependencies: docs/loader/LOADER.md
   - Mixins and bytecode: docs/transformation/TRANSFORMATIONS.md
   - lifecycle and threading: docs/runtime/LIFECYCLE-THREADING.md
   - API coverage: docs/api/API-COMPATIBILITY.md
   - tests and benchmarks: docs/testing/TEST-STRATEGY.md
   - build and language conventions: docs/development/BUILD-AND-STYLE.md
   - milestones: docs/development/ROADMAP.md
   - checkpoint rules: docs/development/CONTEXT-MANAGEMENT.md
   - release gates: docs/development/RELEASE-CHECKLIST.md
   - architectural decisions: docs/adr/
4. Verify current upstream versions and source behavior before relying on remembered details.

Do not read every document by default. Follow links only when the active task requires them. Avoid loading unrelated implementation details.

## Engineering rules
- All project code uses the root package club.kittenpvp.lintcardboard.
- Use Gradle Kotlin DSL and a genuine Java/Kotlin hybrid with one explicit JVM target.
- Reuse upstream Fabric Loader and Fabric API where valid; do not casually reimplement them.
- Separate Minecraft-version-specific code from server-platform code.
- Paper is the common API baseline, not the only target.
- Keep Purpur, Leaf, Pufferfish, Gale and Folia differences behind adapters/capabilities.
- Folia is not an ordinary single-main-thread Paper server.
- The host owns the world, ticks, players, entities, persistence and shutdown. Never start a second server or tick loop.
- Never claim a mod, API or platform works without an executable test on the relevant version and server build.
- Mandatory transformation failure must not be reported as successful mod loading.
- Do not invent APIs, swallow errors, fake test results or label placeholders as implemented.
- No code comments or Javadocs. Use descriptive names and focused classes.
- Do not disable host optimizations without evidence and a documented reason.

## Execution policy
Do not stop after planning or creating empty folders. Work through the current milestone's acceptance criteria, implement a vertical slice, run tests, diagnose failures and continue until the milestone is complete or a concrete external blocker is reached. Do not spin forever on an impossible blocker: record evidence, write an ADR, choose the next viable investigation and continue.

Inspect existing code and relevant upstream source before substantial changes. Prefer small coherent changes over giant rewrites.

## Context persistence
The repository is the durable memory of the work. Update docs/development/WORK-STATE.md after a meaningful milestone, a major decision, a blocker, a change of subsystem, and before ending a session or when context is getting large. Record:
- current milestone and objective;
- verified completed work;
- exact commands/tests and outcomes;
- changed files;
- blockers with evidence;
- next concrete actions.

Keep this file short. Put detailed knowledge in focused documents. Add documents only for distinct topics. Never paste all documentation into this file.

At the start of a new session, reload this file and only the topic documents required for the next action. Never assume unrecorded conversational context.

## Completion standard
A milestone is complete only when its acceptance criteria pass, relevant tests have been run, docs reflect actual behavior and WORK-STATE.md is updated. Report partial completion honestly.