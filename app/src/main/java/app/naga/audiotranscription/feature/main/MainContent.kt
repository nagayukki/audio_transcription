package app.naga.audiotranscription.feature.main

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.naga.audiotranscription.feature.voice.VoiceStore
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import app.naga.audiotranscription.feature.voice.VoiceAction
import app.naga.audiotranscription.feature.voice.VoiceUiState
import app.naga.audiotranscription.feature.voiceOrder.VoiceOrderAction
import app.naga.audiotranscription.feature.voiceOrder.VoiceOrderStore
import app.naga.audiotranscription.feature.voiceOrder.VoiceOrderUiEffect
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import app.naga.audiotranscription.feature.voiceOrder.VoiceOrderScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContent() {
    val navController = rememberNavController()
    val voiceStore: VoiceStore = hiltViewModel()
    val orderStore: VoiceOrderStore = hiltViewModel()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                navController = navController,
                voiceStore = voiceStore,
                orderStore = orderStore
            )
        }
        composable("voiceOrder") {
            val state by orderStore.state.collectAsState()
            VoiceOrderScreen(
                state = state,
                onAdd = { text, action ->
                    orderStore.sendAction(VoiceOrderAction.Insert(text, action))
                },
                onDelete = { order ->
                    orderStore.sendAction(VoiceOrderAction.Delete(order))
                }
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    voiceStore: VoiceStore,
    orderStore: VoiceOrderStore
) {
    val activity = LocalActivity.current
    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO) { isGranted ->
        if (isGranted) {
            return@rememberPermissionState
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            // 設定画面
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
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { },
                actions = {
                    TextButton(onClick = { navController.navigate("voiceOrder") }) {
                        Text("コマンド管理")
                    }
                }
            )
        },
        bottomBar = {
            MainBottomBar(
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
                    Text("OK")
                }
            },
            title = { Text("お知らせ") },
            text = { Text(dialogMessage ?: "") }
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
            .scrollable(state = rememberScrollState(), orientation = Orientation.Vertical),
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
            Text(text = voiceState.result?.text ?: "No result")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainBottomBar(
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
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewMainContent() {
    MainContent()
}