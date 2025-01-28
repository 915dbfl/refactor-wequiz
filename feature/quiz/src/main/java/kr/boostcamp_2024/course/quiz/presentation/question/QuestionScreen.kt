package kr.boostcamp_2024.course.quiz.presentation.question

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.boostcamp_2024.course.domain.model.Quiz
import kr.boostcamp_2024.course.domain.model.RealTimeQuiz
import kr.boostcamp_2024.course.quiz.viewmodel.QuestionViewModel

@Composable
internal fun QuestionScreen(
    onNavigationButtonClick: () -> Unit,
    onQuizFinished: (String?, String?) -> Unit,
    onShowErrorSnackbar: (Throwable) -> Unit,
    questionViewModel: QuestionViewModel = hiltViewModel(),
) {
    val uiState by questionViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.quiz is RealTimeQuiz) {
        val quiz = uiState.quiz as RealTimeQuiz

        if (quiz.ownerId == uiState.currentUserId) {
            OwnerQuestionScreen(
                quiz = quiz,
                currentUserId = uiState.currentUserId,
                onQuizFinished = { _, quizId -> onQuizFinished(null, quizId) },
                onShowErrorSnackbar = onShowErrorSnackbar,
            )
        } else {
            UserQuestionScreen(
                onNavigationButtonClick = onNavigationButtonClick,
                onQuizFinished = { userOmrId, _ ->
                    onQuizFinished(userOmrId, null)
                },
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
                onOptionSelected = questionViewModel::selectOption,
                onNextButtonClick = questionViewModel::nextPage,
                onPreviousButtonClick = questionViewModel::previousPage,
                onSubmitButtonClick = questionViewModel::submitAnswers,
                onNavigationButtonClick = onNavigationButtonClick,
                showErrorMessage = questionViewModel::showErrorMessage,
                onBlanksSelected = questionViewModel::selectBlanks,
                blankQuestionContents = uiState.blankQuestionContents,
                blankWords = uiState.blankWords,
                removeBlankContent = questionViewModel.blankQuestionManager::removeBlankContent,
                addBlankContent = questionViewModel.blankQuestionManager::addBlankContent,
                getBlankQuestionAnswer = questionViewModel.blankQuestionManager::getAnswer,
                isLoading = uiState.isLoading,
            )
        }
    }

    uiState.errorMessageId?.let { errorMessageId ->
        val errorMessage = stringResource(errorMessageId)
        LaunchedEffect(errorMessageId) {
            onShowErrorSnackbar(Exception(errorMessage))
            questionViewModel.shownErrorMessage()
        }
    }

    uiState.userOmrId?.let { userOmrId ->
        LaunchedEffect(userOmrId) {
            onQuizFinished(userOmrId, null)
        }
    }
}
