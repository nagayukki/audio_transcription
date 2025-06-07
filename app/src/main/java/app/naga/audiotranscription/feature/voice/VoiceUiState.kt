package app.naga.audiotranscription.feature.voice

data class VoiceUiState(
    val isRecording: Boolean = false,
    val result: VoiceResult? = null
)

data class VoiceResult(
    val sessionId: String,
    val text: String
)

sealed class VoiceUiEffect {
    data class Error(val message: String) : VoiceUiEffect()
}