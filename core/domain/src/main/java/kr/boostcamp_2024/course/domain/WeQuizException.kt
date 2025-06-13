package kr.boostcamp_2024.course.domain

sealed class WeQuizException(
    open val messageId: Int? = null,
    override val cause: Throwable? = null,
) : Exception(cause) {

    data class NetworkException(
        override val messageId: Int?,
        override val cause: Throwable? = null,
    ) : WeQuizException(messageId, cause)

    data class TooManyRequestsException(
        override val messageId: Int?,
        override val cause: Throwable? = null,
    ) : WeQuizException(messageId, cause)

    data class AuthenticationException(
        override val messageId: Int?,
        override val cause: Throwable? = null,
    ) : WeQuizException(messageId, cause)

    data object CancellationException : WeQuizException(null, null)

    data class UnknownException(
        override val messageId: Int?,
        override val cause: Throwable? = null,
    ) : WeQuizException(messageId, cause)
}
