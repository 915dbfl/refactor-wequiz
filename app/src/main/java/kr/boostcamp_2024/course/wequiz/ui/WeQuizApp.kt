package kr.boostcamp_2024.course.wequiz.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kr.boostcamp_2024.course.designsystem.ui.theme.WeQuizTheme
import kr.boostcamp_2024.course.wequiz.R

@Composable
fun WeQuizApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val localContextResource = LocalContext.current.resources
    val onShowErrorSnackbar: (throwable: Throwable) -> Unit = { throwable ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = throwable.message ?: localContextResource.getString(R.string.default_error_message),
            )
        }
    }

    WeQuizTheme {
        Box {
            WeQuizNavHost(
                modifier = Modifier
                    .fillMaxSize(),
                onShowErrorSnackbar = onShowErrorSnackbar,
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .systemBarsPadding()
                    .align(Alignment.BottomCenter),
            )
        }
    }
}
