# Platform model

## Objective

Support the Paper ecosystem without making the common runtime depend on one fork's internals.

Paper is the reference API baseline. Purpur and Leaf should use the common Paper adapter where behavior matches, with narrow fork-specific adapters only where required. Other Paper-derived servers are evaluated through explicit capabilities. Folia uses a distinct execution model.

## Platform descriptor

The runtime should build a descriptor containing:

- platform identifier;
- reported server and Minecraft versions;
- build identifier when obtainable;
- available API capabilities;
- scheduler and execution capabilities;
- registry and networking capabilities;
- selected transformation profile;
- detection evidence and confidence.

Suggested identifiers: PAPER, PURPUR, LEAF, PUFFERFISH, GALE, FOLIA, OTHER_PAPER_FORK and UNKNOWN.

Do not rely only on a server-name string. Use documented build information, API features, known markers and capability probes. Preserve the evidence behind each detection.

## Adapter hierarchy

### Common Paper adapter
Implements behavior supported by the Paper API contract. This is the baseline for Paper-derived servers.

### Purpur adapter
Adds only verified Purpur-specific behavior. Do not duplicate common Paper code.

### Leaf adapter
Adds only verified Leaf-specific behavior. Preserve Leaf optimizations; do not disable them just to make an unverified mod work.

### Pufferfish and Gale
Use the common adapter when possible. Add optional modules after checking actual target-version builds and behavior.

### Folia adapter
Implements region-aware scheduling and ownership rules. It is not a thin alias for Paper's global main thread.

### Generic fork adapter
May be selected when Paper API and capability checks pass. A generic detection result is not a compatibility guarantee.

## Capability approach

Prefer small capabilities such as Paper API baseline, global scheduler, region scheduler, entity scheduler, regionized execution, custom payload support, required registry behavior and transformation profile compatibility.

Adapters should request capabilities rather than branch repeatedly on a platform name.

## Compatibility policy

A mod passing Paper tests is not automatically supported on Purpur, Leaf or another fork. Transformation targets and internal implementation behavior can differ. Record results against the exact server build.

Unknown forks can be evaluated through the generic adapter, but their status remains unverified until integration tests pass.