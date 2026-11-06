package club.kittenpvp.lintcardboard.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class OperationOutcomeTest {
    @Test
    fun successCarriesValueAndEvidence() {
        val outcome: OperationOutcome<String> =
            OperationOutcome.Success("ready", mapOf("probe" to "bootstrap"))

        assertTrue(outcome.succeeded)
        assertEquals("ready", (outcome as OperationOutcome.Success).value)
        assertEquals("bootstrap", outcome.evidence["probe"])
    }

    @Test
    fun failureKeepsMandatoryErrorsVisible() {
        val outcome: OperationOutcome<String> =
            OperationOutcome.Failure(
                FailureDetail("ControlledTarget", "transform before definition", "marker absent"),
                mapOf("mode" to "fail"),
            )

        assertFalse(outcome.succeeded)
        assertEquals("ControlledTarget", (outcome as OperationOutcome.Failure).detail.subject)
    }
}
