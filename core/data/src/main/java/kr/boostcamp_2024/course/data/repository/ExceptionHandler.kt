package kr.boostcamp_2024.course.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import kr.boostcamp_2024.course.domain.exception.WeQuizException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> runCatchingWeQuiz(block: suspend () -> T): T {
    try {
        return block()
    } catch (e: Throwable) {
        throw e.toWeQuizException()
    }
}

private fun Throwable.toWeQuizException(): WeQuizException =
    when (this) {
        is FirebaseNetworkException,
        is UnknownHostException,
        -> WeQuizException.NetworkException(this)

        is FirebaseTooManyRequestsException -> WeQuizException.TooManyRequestsException(this)
        is FirebaseAuthException -> WeQuizException.AuthenticationException(this)
        is CancellationException -> WeQuizException.CancellationException
        else -> WeQuizException.UnknownException(this)
    }
