package app.naga.audiotranscription

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.naga.audiotranscription.feature.main.MainContent
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {}

@Composable
fun AudioTranscriptionApp(modifier: Modifier = Modifier) {
    MainContent()
}
