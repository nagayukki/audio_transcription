package app.naga.audiotranscription.feature.main

import android.Manifest
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import app.naga.audiotranscription.R
import app.naga.audiotranscription.feature.voice.RecordState
import app.naga.audiotranscription.feature.voice.VoiceAction
import app.naga.audiotranscription.feature.voice.VoiceStore
import app.naga.audiotranscription.feature.voice.VoiceUiEffect
import app.naga.audiotranscription.feature.voice.VoiceUiState
import app.naga.audiotranscription.feature.voiceOrder.VoiceOrderAction
import app.naga.audiotranscription.feature.voiceOrder.VoiceOrderStore
import app.naga.audiotranscription.feature.voiceOrder.VoiceOrderUiEffect
import app.naga.audiotranscription.ui.common.PermissionDialog
import app.naga.audiotranscription.utils.openAppSettings
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    voiceStore: VoiceStore,
    orderStore: VoiceOrderStore
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val activity = LocalActivity.current
    val context = LocalContext.current
    var showPermissionDialog = remember { mutableStateOf(false) }
    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO) { isGranted ->
        if (isGranted) {
            return@rememberPermissionState
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            showPermissionDialog.value = true
        }
    }
    val voiceState = voiceStore.state.collectAsState()

    // Dialogの表示状態
    var dialogMessage by remember { mutableStateOf<String?>(null) }

    // TODO: タイミング
    LaunchedEffect(Unit) {
        voiceStore.state.map { it.result }
            .distinctUntilChanged()
            .collect { voiceResult ->
                val result = voiceResult ?: return@collect
                orderStore.sendAction(
                    VoiceOrderAction.HandleText(result.sessionId, result.text)
                )
            }
    }

    LaunchedEffect(Unit) {
        voiceStore.effect.collect {
            when (it) {
                is VoiceUiEffect.Error -> {
                    snackbarHostState.showSnackbar(
                        message = it.message,
                        withDismissAction = true,
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        orderStore.effect.collect {
            when (it) {
                is VoiceOrderUiEffect.Dialog -> {
                    dialogMessage = it.message
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { },
                actions = {
                    TextButton(onClick = { navController.navigate("voiceOrder") }) {
                        Text(stringResource(R.string.main_command_management))
                    }
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                recordState = voiceState.value.recordState,
                onTapRecordButton = {
                    if (recordAudioPermissionState.status.isGranted) {
                        if (voiceState.value.isRecording) {
                            voiceStore.sendAction(VoiceAction.StopRecording)
                        } else {
                            voiceStore.sendAction(VoiceAction.StartRecording)
                        }
                    } else {
                        recordAudioPermissionState.launchPermissionRequest()
                    }
                }
            )
        }
    ) { innerPadding ->
        MainBody(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            voiceState = voiceState.value
        )
    }

    if (dialogMessage != null) {
        AlertDialog(
            onDismissRequest = { dialogMessage = null },
            confirmButton = {
                TextButton(onClick = { dialogMessage = null }) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            title = { Text(stringResource(R.string.main_notice_title)) },
            text = { Text(dialogMessage ?: "") }
        )
    }

    if (showPermissionDialog.value) {
        PermissionDialog(
            showPermissionDialog = showPermissionDialog,
            onOkClick = {
                showPermissionDialog.value = false
                openAppSettings(context)
            }
        )
    }
}

@Composable
fun MainBody(
    modifier: Modifier = Modifier,
    voiceState: VoiceUiState
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(text = voiceState.result?.text ?: stringResource(R.string.main_no_result))
        }
    }
}

@Composable
fun MainBottomBar(
    recordState: RecordState,
    onTapRecordButton: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onTapRecordButton,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = when (recordState) {
                    is RecordState.Started -> Icons.Filled.Close
                    is RecordState.Stopped -> Icons.Filled.PlayArrow
                    is RecordState.Loading -> Icons.Filled.Refresh
                },
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

