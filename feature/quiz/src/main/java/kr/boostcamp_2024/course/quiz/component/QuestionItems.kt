package kr.boostcamp_2024.course.quiz.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kr.boostcamp_2024.course.designsystem.ui.annotation.PreviewKoLightDarkBackground
import kr.boostcamp_2024.course.designsystem.ui.theme.WeQuizTheme

@Composable
internal fun QuestionItems(choice: List<String>, answer: Int, onClick: () -> Unit) {
    Column(modifier = Modifier.selectableGroup()) {
        choice.forEachIndexed { idx, it ->
            RadioTextButton(
                text = it,
                selected = (answer == idx),
                onclick = onClick,
            )
        }
    }
}

@PreviewKoLightDarkBackground
@Composable
private fun QuestionItemsPreview() {
    WeQuizTheme {
        QuestionItems(
            choice = listOf("A", "B", "C", "D"),
            answer = 0,
            onClick = { },
        )
    }
}
