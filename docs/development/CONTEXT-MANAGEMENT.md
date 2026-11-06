# Context management

## Objective

Keep long-running agent work recoverable without forcing every model invocation to ingest the complete project specification.

## Document loading

Always read AGENTS.md first. It is deliberately short.

Then read WORK-STATE.md. Load only the topic document required for the active task. Do not read the entire documentation tree by default.

Examples:
- platform detection: PLATFORMS.md and the relevant ADR;
- launch integration: BOOTSTRAP.md and TRANSFORMATIONS.md;
- mod discovery: LOADER.md;
- Folia scheduler: PLATFORMS.md and LIFECYCLE-THREADING.md;
- API implementation: API-COMPATIBILITY.md;
- a test change: TEST-STRATEGY.md and the affected component contract.

Read upstream sources only when behavior or versions matter. Prefer relevant files and documentation sections over whole repositories.

## Checkpoint triggers

Update WORK-STATE.md after a meaningful implementation or test milestone, a major design decision, a blocker, a task focus change, before ending a work session, and when context is becoming large.

Save code and test artifacts before checkpointing. A checkpoint is not a substitute for saving implementation.

## Checkpoint content

Keep it concise and factual:
- date/time and branch;
- current milestone and objective;
- verified completed work;
- files changed;
- exact commands/tests and outcomes;
- blockers and evidence;
- next concrete action.

Do not paste entire design documents or speculative progress into the checkpoint.

## Session recovery

1. Read AGENTS.md.
2. Read WORK-STATE.md.
3. Inspect Git status and recent commits.
4. Load only relevant topic documents.
5. Continue from the first incomplete acceptance criterion.

Do not restart planning when implementation already exists.

## Long-running work

Continue through the active milestone instead of stopping after a plan. If an external dependency blocks one task, record the error and make progress on another independent task. Do not loop endlessly or claim the blocker is solved.

The repository is the durable source of truth, not conversational memory.