package club.kittenpvp.lintcardboard.platform

data class PlatformDescriptor(
    val identifier: PlatformIdentifier,
    val serverName: String,
    val minecraftVersion: String,
    val build: String?,
    val capabilities: Set<PlatformCapability>,
    val evidence: Map<String, String>,
    val confidence: DetectionConfidence,
)
