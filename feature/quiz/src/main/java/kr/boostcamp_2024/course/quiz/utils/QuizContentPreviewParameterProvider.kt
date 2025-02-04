package kr.boostcamp_2024.course.quiz.utils

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kr.boostcamp_2024.course.domain.model.BlankQuestion
import kr.boostcamp_2024.course.domain.model.ChoiceQuestion
import kr.boostcamp_2024.course.domain.model.Question

class QuizContentPreviewParameterProvider : PreviewParameterProvider<Question> {
    override val values = sequenceOf(
        BlankQuestion(
            id = "1",
            title = "문제 제목",
            questionContent = listOf(
                mapOf("text" to "바나나", "type" to "blank"),
                mapOf("text" to "는 원래 하얗다", "type" to "text"),
            ),
            solution = "문제 해설",
            userAnswers = emptyList(),
        ),
        ChoiceQuestion(
            id = "2",
            "문제 제목",
            description = "문제 설명",
            solution = "문제 해설",
            answer = 0,
            choices = listOf("객관식 1", "객관식 2", "객관식 3", "객관식 4"),
            userAnswers = emptyList(),
        ),
    )
}
