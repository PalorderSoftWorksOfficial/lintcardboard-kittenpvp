package club.kittenpvp.lintcardboard.platform

object PlatformDetector {
    private const val PAPER_API_MARKER = "paper-api"

    fun detect(
        serverName: String,
        minecraftVersion: String,
        build: String?,
        markers: Map<String, String>,
    ): PlatformDescriptor {
        val normalized = serverName.lowercase()
        val identifier = identify(normalized, markers)
        val capabilities = capabilitiesFor(identifier)
        val confidence = confidenceFor(identifier)
        val evidence = buildEvidence(serverName, minecraftVersion, build, markers)
        return PlatformDescriptor(
            identifier = identifier,
            serverName = serverName,
            minecraftVersion = minecraftVersion,
            build = build,
            capabilities = capabilities,
            evidence = evidence,
            confidence = confidence,
        )
    }

    private fun identify(normalized: String, markers: Map<String, String>): PlatformIdentifier =
        when {
            normalized.contains("folia") -> PlatformIdentifier.FOLIA
            normalized.contains("purpur") -> PlatformIdentifier.PURPUR
            normalized.contains("leaf") -> PlatformIdentifier.LEAF
            normalized.contains("pufferfish") -> PlatformIdentifier.PUFFERFISH
            normalized.contains("gale") -> PlatformIdentifier.GALE
            normalized.contains("paper") -> PlatformIdentifier.PAPER
            markers[PAPER_API_MARKER] == "present" -> PlatformIdentifier.OTHER_PAPER_FORK
            else -> PlatformIdentifier.UNKNOWN
        }

    private fun capabilitiesFor(identifier: PlatformIdentifier): Set<PlatformCapability> =
        when (identifier) {
            PlatformIdentifier.FOLIA ->
                setOf(
                    PlatformCapability.PAPER_API_BASELINE,
                    PlatformCapability.REGION_SCHEDULER,
                    PlatformCapability.ENTITY_SCHEDULER,
                    PlatformCapability.REGIONIZED_EXECUTION,
                    PlatformCapability.CUSTOM_PAYLOAD,
                    PlatformCapability.DYNAMIC_REGISTRIES,
                    PlatformCapability.EARLY_TRANSFORMATION,
                )
            PlatformIdentifier.UNKNOWN -> emptySet()
            else ->
                setOf(
                    PlatformCapability.PAPER_API_BASELINE,
                    PlatformCapability.GLOBAL_SCHEDULER,
                    PlatformCapability.CUSTOM_PAYLOAD,
                    PlatformCapability.DYNAMIC_REGISTRIES,
                    PlatformCapability.EARLY_TRANSFORMATION,
                )
        }

    private fun confidenceFor(identifier: PlatformIdentifier): DetectionConfidence =
        when (identifier) {
            PlatformIdentifier.UNKNOWN -> DetectionConfidence.LOW
            PlatformIdentifier.OTHER_PAPER_FORK -> DetectionConfidence.MEDIUM
            else -> DetectionConfidence.HIGH
        }

    private fun buildEvidence(
        serverName: String,
        minecraftVersion: String,
        build: String?,
        markers: Map<String, String>,
    ): Map<String, String> {
        val evidence = mutableMapOf(
            "serverName" to serverName,
            "minecraftVersion" to minecraftVersion,
            "build" to (build ?: "absent"),
        )
        markers.forEach { (key, value) -> evidence["marker.$key"] = value }
        return evidence
    }
}
