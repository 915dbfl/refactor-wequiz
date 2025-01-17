package kr.boostcamp_2024.course.category.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import kr.boostcamp_2024.course.category.R
import kr.boostcamp_2024.course.designsystem.ui.annotation.PreviewKoLightDark
import kr.boostcamp_2024.course.designsystem.ui.theme.WeQuizTheme

@Composable
internal fun CategorySettingMenu(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd),
    ) {
        IconButton(
            onClick = { expanded = true },
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.des_btn_settings),
            )
        }
        CategoryDropdownMenu(
            expanded = expanded,
            onDropDownMenuDismiss = { expanded = false },
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
        )
    }
}

@Composable
internal fun CategoryDropdownMenu(
    expanded: Boolean,
    onDropDownMenuDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDropDownMenuDismiss() },
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.txt_category_fix)) },
            onClick = {
                onDropDownMenuDismiss()
                onEditClick()
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.txt_category_delete)) },
            onClick = {
                onDropDownMenuDismiss()
                onDeleteClick()
            },
        )
    }
}

@Composable
@PreviewKoLightDark
private fun CategorySettingMenuPreview() {
    WeQuizTheme {
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart),
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.Settings, contentDescription = "")
            }
            CategoryDropdownMenu(
                expanded = expanded,
                onDropDownMenuDismiss = { expanded = false },
                onEditClick = {},
                onDeleteClick = {},
            )
        }
    }
}
