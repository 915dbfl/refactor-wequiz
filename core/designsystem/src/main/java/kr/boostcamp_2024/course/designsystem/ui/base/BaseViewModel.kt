package kr.boostcamp_2024.course.designsystem.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kr.boostcamp_2024.course.domain.exception.WeQuizException
import kr.boostcamp_2024.course.domain.exception.WeQuizUIException

open class BaseViewModel : ViewModel() {
    private val _errorFlow = Channel<WeQuizUIException>()
    val errorFlow = _errorFlow.receiveAsFlow()

    protected suspend fun handleError(messageId: Int? = null, throwable: Throwable? = null) =
        when (throwable) {
            is WeQuizException.CancellationException, is CancellationException -> {
                // no-op
            }
            else -> _errorFlow.send(WeQuizUIException.fromThrowable(messageId, throwable))
        }
}
