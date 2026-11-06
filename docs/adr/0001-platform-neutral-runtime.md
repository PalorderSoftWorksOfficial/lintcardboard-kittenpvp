# ADR 0001: Platform-neutral runtime with fork adapters

- Status: Proposed
- Date: 2026-11-06

## Context

The project targets Paper and Paper-derived servers, including Purpur and Leaf, with other forks evaluated individually. Folia has a distinct region-threaded execution model.

## Decision

Keep the Fabric compatibility runtime independent of server-fork implementation details. Provide a common Paper API adapter and focused platform adapters selected through capability detection. Keep Minecraft-version-specific code in a separate version layer.

## Consequences

- Common behavior is implemented once where semantics match.
- Fork-specific differences are isolated and testable.
- A result on Paper does not automatically prove Purpur or Leaf compatibility.
- Folia requires dedicated scheduler and ownership semantics.
- The module structure must avoid platform dependencies leaking into the loader.

## Open questions

- Which bootstrap hooks are stable across the target server versions?
- Which Paper APIs are sufficient for the initial vertical slice?
- Which fork build metadata can be used reliably for identification?