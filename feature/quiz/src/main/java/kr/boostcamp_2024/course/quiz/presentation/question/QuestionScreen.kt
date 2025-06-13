package kr.boostcamp_2024.course.quiz.presentation.question

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
import kr.boostcamp_2024.course.domain.exception.WeQuizUIException
import kr.boostcamp_2024.course.domain.model.Quiz
import kr.boostcamp_2024.course.domain.model.RealTimeQuiz
import kr.boostcamp_2024.course.quiz.viewmodel.QuestionViewModel

@Composable
internal fun QuestionScreen(
    onNavigationButtonClick: () -> Unit,
    onQuizFinished: (String?, String?) -> Unit,
    snackbarHostState: SnackbarHostState,
    onShowErrorSnackbar: (WeQuizUIException) -> Unit,
    viewModel: QuestionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val localLifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.errorFlow, localLifecycleOwner) {
        localLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.errorFlow.collectLatest { exception -> onShowErrorSnackbar(exception) }
        }
    }

    uiState.userOmrId?.let { userOmrId ->
        LaunchedEffect(userOmrId) {
            onQuizFinished(userOmrId, null)
        }
    }

    if (uiState.quiz is RealTimeQuiz) {
        val quiz = uiState.quiz as RealTimeQuiz

        if (quiz.ownerId == uiState.currentUserId) {
            OwnerQuestionScreen(
                quiz = quiz,
                currentUserId = uiState.currentUserId,
                onQuizFinished = { _, quizId -> onQuizFinished(null, quizId) },
                snackbarHostState = snackbarHostState,
                onShowErrorSnackbar = onShowErrorSnackbar,
            )
        } else {
            UserQuestionScreen(
                onNavigationButtonClick = onNavigationButtonClick,
                onQuizFinished = { userOmrId, _ ->
                    onQuizFinished(userOmrId, null)
                },
                snackbarHostState = snackbarHostState,
                onShowErrorSnackbar = onShowErrorSnackbar,
            )
        }
    } else if (uiState.quiz is Quiz) {
        uiState.countDownTime?.let { currentCountDownTime ->
            GeneralQuestionScreen(
                quizTitle = uiState.quiz?.title,
                currentPage = uiState.currentPage,
                questions = uiState.questions,
                countDownTime = currentCountDownTime,
                selectedIndexList = uiState.selectedIndexList,
                onOptionSelected = viewModel::selectOption,
                onNextButtonClick = viewModel::nextPage,
                onPreviousButtonClick = viewModel::previousPage,
                onSubmitButtonClick = viewModel::submitAnswers,
                onNavigationButtonClick = onNavigationButtonClick,
                onBlanksSelected = viewModel::selectBlanks,
                blankQuestionContents = uiState.blankQuestionContents,
                blankWords = uiState.blankWords,
                removeBlankContent = viewModel.blankQuestionManager::removeBlankContent,
                addBlankContent = viewModel.blankQuestionManager::addBlankContent,
                getBlankQuestionAnswer = viewModel.blankQuestionManager::getAnswer,
                isLoading = uiState.isLoading,
                onShowErrorSnackbar = onShowErrorSnackbar,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
