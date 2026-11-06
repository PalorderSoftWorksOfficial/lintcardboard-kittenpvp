package club.kittenpvp.lintcardboard.platform

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlatformDetectorTest {
    @Test
    fun paperKeepsCommonBaseline() {
        val descriptor = PlatformDetector.detect("Paper", "26.2", "129", emptyMap())

        assertEquals(PlatformIdentifier.PAPER, descriptor.identifier)
        assertEquals(DetectionConfidence.HIGH, descriptor.confidence)
        assertTrue(descriptor.capabilities.contains(PlatformCapability.PAPER_API_BASELINE))
        assertTrue(descriptor.capabilities.contains(PlatformCapability.GLOBAL_SCHEDULER))
    }

    @Test
    fun purpurKeepsPaperBaseline() {
        val descriptor = PlatformDetector.detect("Purpur", "26.2", "2633", emptyMap())

        assertEquals(PlatformIdentifier.PURPUR, descriptor.identifier)
        assertTrue(descriptor.capabilities.contains(PlatformCapability.PAPER_API_BASELINE))
        assertTrue(descriptor.capabilities.contains(PlatformCapability.GLOBAL_SCHEDULER))
    }

    @Test
    fun leafKeepsPaperBaseline() {
        val descriptor = PlatformDetector.detect("Leaf", "26.2", "123", emptyMap())

        assertEquals(PlatformIdentifier.LEAF, descriptor.identifier)
        assertTrue(descriptor.capabilities.contains(PlatformCapability.PAPER_API_BASELINE))
        assertTrue(descriptor.capabilities.contains(PlatformCapability.GLOBAL_SCHEDULER))
    }

    @Test
    fun foliaRequiresRegionizedExecutionWithoutGlobalScheduler() {
        val descriptor = PlatformDetector.detect("Folia", "26.2", "7", emptyMap())

        assertEquals(PlatformIdentifier.FOLIA, descriptor.identifier)
        assertTrue(descriptor.capabilities.contains(PlatformCapability.REGIONIZED_EXECUTION))
        assertTrue(descriptor.capabilities.contains(PlatformCapability.REGION_SCHEDULER))
        assertTrue(descriptor.capabilities.contains(PlatformCapability.ENTITY_SCHEDULER))
        assertTrue(!descriptor.capabilities.contains(PlatformCapability.GLOBAL_SCHEDULER))
    }

    @Test
    fun genericForkDetectedFromPaperApiMarker() {
        val descriptor =
            PlatformDetector.detect("MysteryFork", "26.2", null, mapOf("paper-api" to "present"))

        assertEquals(PlatformIdentifier.OTHER_PAPER_FORK, descriptor.identifier)
        assertEquals(DetectionConfidence.MEDIUM, descriptor.confidence)
        assertTrue(descriptor.capabilities.contains(PlatformCapability.PAPER_API_BASELINE))
    }

    @Test
    fun unknownServerStaysCapabilityFree() {
        val descriptor = PlatformDetector.detect("Vanilla", "26.2", null, emptyMap())

        assertEquals(PlatformIdentifier.UNKNOWN, descriptor.identifier)
        assertEquals(DetectionConfidence.LOW, descriptor.confidence)
        assertTrue(descriptor.capabilities.isEmpty())
    }

    @Test
    fun detectionPreservesEvidence() {
        val descriptor = PlatformDetector.detect("Paper", "26.2", "129", mapOf("spark" to "present"))

        assertEquals("Paper", descriptor.evidence["serverName"])
        assertEquals("26.2", descriptor.evidence["minecraftVersion"])
        assertEquals("129", descriptor.evidence["build"])
        assertEquals("present", descriptor.evidence["marker.spark"])
    }
}
