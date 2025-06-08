package app.naga.audiotranscription.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.naga.audiotranscription.feature.voice.VoiceStore
import androidx.compose.runtime.collectAsState
import app.naga.audiotranscription.feature.voiceOrder.VoiceOrderAction
import app.naga.audiotranscription.feature.voiceOrder.VoiceOrderStore
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import app.naga.audiotranscription.feature.voiceOrder.VoiceOrderScreen

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

@Preview
@Composable
fun PreviewMainContent() {
    MainContent()
}