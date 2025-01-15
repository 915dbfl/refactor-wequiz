package kr.boostcamp_2024.course.main.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kr.boostcamp_2024.course.designsystem.ui.annotation.PreviewKoLightDark
import kr.boostcamp_2024.course.designsystem.ui.theme.WeQuizTheme
import kr.boostcamp_2024.course.main.R

@Composable
internal fun MainDropDownMenu(
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onEditUserClick: () -> Unit,
    onLogOutClick: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.txt_main_menu_edit)) },
            onClick = {
                onEditUserClick()
                onDismissRequest()
            },
        )
        HorizontalDivider()
        DropdownMenuItem(
            text = { Text(stringResource(R.string.txt_main_menu_logout)) },
            onClick = {
                onLogOutClick()
                onDismissRequest()
            },
        )
    }
}

@PreviewKoLightDark
@Composable
private fun MainDropDownMenuPreview() {
    WeQuizTheme {
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart),
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "")
            }
            MainDropDownMenu(
                isExpanded = expanded,
                onDismissRequest = { expanded = false },
                onEditUserClick = {},
                onLogOutClick = {},
            )
        }
    }
}
