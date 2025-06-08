package app.naga.audiotranscription.feature.voice

data class VoiceUiState(
    val recordState: RecordState = RecordState.Stopped,
    val result: VoiceResult? = null
) {
    val isRecording: Boolean get() = recordState == RecordState.Started
}

data class VoiceResult(
    val sessionId: String,
    val text: String
)

sealed class RecordState {
    object Loading : RecordState()
    object Started : RecordState()
    object Stopped : RecordState()
}

sealed class VoiceUiEffect {
    data class Error(val message: String) : VoiceUiEffect()
}