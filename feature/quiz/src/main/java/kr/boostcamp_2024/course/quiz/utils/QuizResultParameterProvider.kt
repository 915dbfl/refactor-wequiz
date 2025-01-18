package kr.boostcamp_2024.course.quiz.utils

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kr.boostcamp_2024.course.domain.model.BlankQuestion
import kr.boostcamp_2024.course.domain.model.ChoiceQuestion
import kr.boostcamp_2024.course.domain.model.Question

class QuizResultParameterProvider: PreviewParameterProvider<List<Question>> {
    override val values = sequenceOf(
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
    )
}
