package kr.boostcamp_2024.course.designsystem.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kr.boostcamp_2024.course.domain.exception.WeQuizUIException

open class BaseViewModel : ViewModel() {
    protected val _errorChannel = Channel<WeQuizUIException>()
    val errorFlow = _errorChannel.receiveAsFlow()

    protected fun handleError(messageId: Int? = null, throwable: Throwable? = null) {
        viewModelScope.launch {
            _errorChannel.send(WeQuizUIException.fromThrowable(messageId, throwable))
        }
    }
}
