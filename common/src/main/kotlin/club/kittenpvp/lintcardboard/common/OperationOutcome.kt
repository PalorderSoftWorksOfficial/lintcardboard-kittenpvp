package club.kittenpvp.lintcardboard.common

sealed interface OperationOutcome<out T> {
    data class Success<T>(val value: T, val evidence: Map<String, String>) : OperationOutcome<T>

    data class Failure(val detail: FailureDetail, val evidence: Map<String, String>) : OperationOutcome<Nothing>
}

val OperationOutcome<*>.succeeded: Boolean
    get() = this is OperationOutcome.Success<*>
