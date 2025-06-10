package kr.boostcamp_2024.course.domain

import java.io.IOException

sealed class WeQuizException(
    open val messageId: Int? = null,
    override val cause: Throwable? = null
) : Exception(cause) {

    data class NetworkException(
        override val messageId: Int?,
        override val cause: Throwable? = null
    ) : WeQuizException(messageId, cause)

    data class UnknownException(
        override val messageId: Int?,
        override val cause: Throwable? = null
    ) : WeQuizException(messageId, cause)

    companion object {
        fun fromThrowable(messageId: Int? = null, throwable: Throwable? = null): WeQuizException {
            return when (throwable) {
                is IOException -> NetworkException(messageId, throwable)
                else -> UnknownException(messageId, throwable)
            }
        }
    }
}
