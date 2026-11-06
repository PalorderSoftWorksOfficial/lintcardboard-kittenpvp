# Gradle module boundaries

This is the intended module map. The feasibility milestone may change it. Do not create modules that have no real implementation or test boundary.

| Module | Owns | Must not own |
|---|---|---|
| common | version-neutral data, results, contracts | platform implementation |
| bootstrap-api | launch/bootstrap SPI and status contracts | Fabric or fork internals |
| bootstrap-agent | Java agent and early instrumentation | gameplay API adapters |
| loader | upstream Fabric Loader integration and mod resolution | host platform assumptions |
| transform | Mixin/ASM/access transformation services | mod gameplay features |
| api | stable LintCardboard compatibility contracts | concrete Paper classes |
| fabric-api | Fabric API adapter implementations | fork detection |
| platform-spi | platform descriptor and capability contracts | concrete platform implementation |
| platform-common | common Paper API integration | fork-only behavior |
| platform-paper | Paper-specific composition and verification | generic loader logic |
| platform-purpur | verified Purpur-specific behavior only | duplicated Paper implementation |
| platform-leaf | verified Leaf-specific behavior only | duplicated Paper implementation |
| platform-folia | region-aware scheduling and Folia integration | fake global main-thread abstraction |
| platform-generic | capability-driven fallback for unknown forks | unsupported assumptions |
| minecraft-26.2 | version-specific mappings and hooks | fork-specific detection |
| plugin | host plugin entrypoint and operator integration | low-level bootstrap internals |
| cli | offline diagnostics when useful | mandatory server runtime |

Pufferfish and Gale adapters may be added only when they require distinct code. If the common Paper adapter passes on those platforms, record that result rather than create empty modules.

The final composition should depend on the smallest required module set. Keep test fixtures independent from production artifacts.