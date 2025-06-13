package kr.boostcamp_2024.course.wequiz.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kr.boostcamp_2024.course.designsystem.ui.theme.WeQuizTheme
import kr.boostcamp_2024.course.domain.WeQuizException
import kr.boostcamp_2024.course.wequiz.R

@Composable
fun WeQuizApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val localContextResource = LocalContext.current.resources
    val onShowErrorSnackbar: (exception: WeQuizException) -> Unit = { exception ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = exception.messageId?.let {
                    localContextResource.getString(it)
                } ?: run {
                    when (exception) {
                        is WeQuizException.NetworkException -> localContextResource.getString(R.string.err_network_message)
                        is WeQuizException.TooManyRequestsException -> localContextResource.getString(R.string.err_too_many_requests_message)
                        is WeQuizException.AuthenticationException -> localContextResource.getString(R.string.error_auth_message)
                        is WeQuizException.UnknownException -> localContextResource.getString(R.string.err_default_message)
                        else -> localContextResource.getString(R.string.err_default_message)
                    }
                },
            )
        }
    }

    WeQuizTheme {
        WeQuizNavHost(
            snackbarHostState = snackbarHostState,
            modifier = Modifier.fillMaxSize(),
            onShowErrorSnackbar = onShowErrorSnackbar,
        )
    }
}
