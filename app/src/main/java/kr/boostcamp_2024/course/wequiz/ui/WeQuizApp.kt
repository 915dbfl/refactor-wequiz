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
                        is WeQuizException.NetworkException -> localContextResource.getString(R.string.network_error_message)
                        is WeQuizException.UnknownException -> localContextResource.getString(R.string.default_error_message)
                        else -> localContextResource.getString(R.string.default_error_message)
                    }
                }
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
