package app.naga.audiotranscription.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import app.naga.audiotranscription.R

@Composable
fun PermissionDialog(
    showPermissionDialog: MutableState<Boolean>,
    onOkClick: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = { showPermissionDialog.value = false },
        confirmButton = {
            TextButton(
                onClick = onOkClick
            ) {
                Text(stringResource(R.string.common_ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { showPermissionDialog.value = false } // ダイアログを閉じる
            ) {
                Text(stringResource(R.string.common_cancel))
            }
        },
        title = { Text(stringResource(R.string.permission_alert_title)) },
        text = { Text(stringResource(R.string.permission_alert_message)) }
    )
}