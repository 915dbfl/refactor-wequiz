package kr.boostcamp_2024.course.domain.exception

sealed class WeQuizException(
    override val cause: Throwable? = null,
) : Exception(cause) {
    data class NetworkException(
        override val cause: Throwable? = null,
    ) : WeQuizException(cause)

    data class TooManyRequestsException(
        override val cause: Throwable? = null,
    ) : WeQuizException(cause)

    data class AuthenticationException(
        override val cause: Throwable? = null,
    ) : WeQuizException(cause)

    data object CancellationException : WeQuizException(null)

    data class UnknownException(
        override val cause: Throwable? = null,
    ) : WeQuizException(cause)
}
