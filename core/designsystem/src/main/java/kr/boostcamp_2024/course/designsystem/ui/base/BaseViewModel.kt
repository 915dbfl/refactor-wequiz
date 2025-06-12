package kr.boostcamp_2024.course.designsystem.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kr.boostcamp_2024.course.domain.WeQuizException

open class BaseViewModel: ViewModel() {
    protected val _errorChannel = Channel<WeQuizException>()
    val errorFlow = _errorChannel.receiveAsFlow()

    protected fun handleError(messageId: Int? = null, throwable: Throwable? = null) {
        viewModelScope.launch {
            _errorChannel.send(WeQuizException.fromThrowable(messageId, throwable))
        }
    }
}
