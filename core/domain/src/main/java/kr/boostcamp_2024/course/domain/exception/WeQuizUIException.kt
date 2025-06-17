package kr.boostcamp_2024.course.domain.exception

sealed class WeQuizUIException(
    open val messageId: Int? = null,
    override val cause: Throwable? = null,
) : Exception(cause) {
    data class NetworkUIException(
        override val messageId: Int?,
        override val cause: Throwable? = null,
    ) : WeQuizUIException(messageId, cause)

    data class TooManyRequestsUIException(
        override val messageId: Int?,
        override val cause: Throwable? = null,
    ) : WeQuizUIException(messageId, cause)

    data class AuthenticationUIException(
        override val messageId: Int?,
        override val cause: Throwable? = null,
    ) : WeQuizUIException(messageId, cause)

    data object CancellationUIException : WeQuizUIException(null, null)

    data class UnknownUIException(
        override val messageId: Int?,
        override val cause: Throwable? = null,
    ) : WeQuizUIException(messageId, cause)

    companion object {
        fun fromThrowable(messageId: Int? = null, throwable: Throwable?): WeQuizUIException =
            when (throwable) {
                is WeQuizException.NetworkException -> NetworkUIException(messageId, throwable)
                is WeQuizException.TooManyRequestsException -> TooManyRequestsUIException(
                    messageId,
                    throwable,
                )

                is WeQuizException.AuthenticationException -> AuthenticationUIException(
                    messageId,
                    throwable,
                )

                is WeQuizException.CancellationException -> CancellationUIException
                is WeQuizException.UnknownException -> UnknownUIException(messageId, throwable)
                else -> UnknownUIException(messageId, throwable)
            }
    }
}
