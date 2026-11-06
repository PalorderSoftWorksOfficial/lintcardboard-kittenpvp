# Fabric API compatibility policy

## Scope

Implement server-side Fabric API behavior in independently testable adapters. Prioritize lifecycle, events, commands/Brigadier, networking/custom payloads, entity interactions, tags, data operations, registries and selected world interactions.

Client rendering, screens, HUDs and client-only features are outside the initial scope.

## Adapter contract

For each API, record:

- Fabric contract and upstream version;
- host API or hook used;
- semantic differences;
- platform-specific requirements;
- threading requirements;
- supported, conditional, rejected or unknown status;
- unit and integration tests.

A Bukkit/Paper event with a similar name is not automatically semantically equivalent to a Fabric event.

## Coverage levels

- Native: behavior is provided by the host with equivalent semantics.
- Adapted: a bridge maps the Fabric contract to host behavior.
- Partial: a documented subset is supported.
- Unsupported: behavior cannot be safely represented.
- Unverified: code or theory exists but tests do not establish behavior.

Only tested behavior may be listed as supported. Compilation, class presence or a successful plugin enable message is not sufficient evidence.