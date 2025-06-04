package app.naga.audiotranscription

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.naga.audiotranscription.ui.AudioTranscriptionApp
import app.naga.audiotranscription.ui.theme.AudioTranscriptionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioTranscriptionTheme {
                AudioTranscriptionApp()
            }
        }
    }
}
