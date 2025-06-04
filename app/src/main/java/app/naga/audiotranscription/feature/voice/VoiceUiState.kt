package app.naga.audiotranscription.feature.voice

data class VoiceUiState(
    val isRecording: Boolean = false
)

sealed class VoiceUiEffect {

}