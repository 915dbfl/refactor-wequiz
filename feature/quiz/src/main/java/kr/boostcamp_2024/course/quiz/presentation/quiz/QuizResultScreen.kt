package kr.boostcamp_2024.course.quiz.presentation.quiz

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kr.boostcamp_2024.course.designsystem.ui.theme.component.WeQuizCircularProgressIndicator
import kr.boostcamp_2024.course.domain.exception.WeQuizUIException
import kr.boostcamp_2024.course.domain.model.BlankQuestion
import kr.boostcamp_2024.course.domain.model.ChoiceQuestion
import kr.boostcamp_2024.course.quiz.viewmodel.QuizResultViewModel

@Composable
internal fun QuizResultScreen(
    onNavigationButtonClick: () -> Unit,
    onQuestionClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    onShowErrorSnackbar: (WeQuizUIException) -> Unit,
    viewModel: QuizResultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val localLifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.errorFlow, localLifecycleOwner) {
        localLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.errorFlow.collectLatest { exception -> onShowErrorSnackbar(exception) }
        }
    }

    if (uiState.isManager) {
        OwnerQuizResultScreen(
            questions = uiState.questions,
            quizTitle = uiState.quizTitle,
            onNavigationButtonClick = onNavigationButtonClick,
            onQuestionClick = onQuestionClick,
            snackbarHostState = snackbarHostState,
        )
    } else {
        GeneralQuizResultScreen(
            quizTitle = uiState.quizTitle,
            quizResult = uiState.quizResult,
            onNavigationButtonClick = onNavigationButtonClick,
            onQuestionClick = onQuestionClick,
            snackbarHostState = snackbarHostState,
        )
    }

    if (uiState.isLoading) {
        WeQuizCircularProgressIndicator()
    }
}

val quizResultPreviewQuestions =
    listOf(
        ChoiceQuestion(
            id = "1",
            title = "1번 문제",
            solution = null,
            description = "1번 문제 설명",
            answer = 1,
            choices = listOf(),
            userAnswers = listOf(),
        ),
        ChoiceQuestion(
            id = "2",
            title = "2번 문제",
            solution = null,
            description = "2번 문제 설명",
            answer = 1,
            choices = listOf(),
            userAnswers = listOf(),
        ),
        BlankQuestion(
            id = "3",
            title = "3번 문제 (낱말 맞추기)",
            solution = null,
            userAnswers = emptyList(),
            questionContent = emptyList(),
        ),
    )
