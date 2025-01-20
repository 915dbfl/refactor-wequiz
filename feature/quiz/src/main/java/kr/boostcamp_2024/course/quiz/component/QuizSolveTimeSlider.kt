package kr.boostcamp_2024.course.quiz.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kr.boostcamp_2024.course.designsystem.ui.annotation.PreviewKoLightDarkBackground
import kr.boostcamp_2024.course.designsystem.ui.theme.WeQuizTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuizSolveTimeSlider(
    value: Float,
    steps: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Slider(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = { onValueChange(it) },
        steps = steps,
        valueRange = valueRange,
        thumb = {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 40.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (value.toInt()).toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        },
    )
}

@PreviewKoLightDarkBackground
@Composable
private fun QuizSolveTimeSliderPreview() {
    WeQuizTheme {
        QuizSolveTimeSlider(
            value = 0f,
            steps = 10,
            valueRange = 0f..10f,
            onValueChange = {},
        )
    }
}
