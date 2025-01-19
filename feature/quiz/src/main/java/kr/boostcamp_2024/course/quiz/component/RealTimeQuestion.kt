package kr.boostcamp_2024.course.quiz.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kr.boostcamp_2024.course.designsystem.ui.annotation.PreviewKoLightDarkBackground
import kr.boostcamp_2024.course.designsystem.ui.theme.WeQuizTheme

@Composable
internal fun RealTimeQuestion(
    isOwner: Boolean = false,
    questions: List<String>,
    selectedIndex: Int,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        questions.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            ) {
                RadioButton(
                    enabled = isOwner.not(),
                    selected = selectedIndex == index,
                    onClick = null,
                    modifier = Modifier.padding(8.dp),
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.CenterVertically),
                )
            }
        }
    }
}

@PreviewKoLightDarkBackground
@Composable
private fun RealTimeQuestionPreview() {
    WeQuizTheme {
        RealTimeQuestion(
            questions = listOf("문제1", "문제2", "문제3", "문제4", "문제5"),
            selectedIndex = 0,
        )
    }
}
