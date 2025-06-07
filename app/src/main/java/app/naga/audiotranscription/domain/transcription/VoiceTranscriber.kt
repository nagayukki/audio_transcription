package app.naga.audiotranscription.domain.transcription

import kotlinx.coroutines.flow.Flow

interface VoiceTranscriber {
    val transcribeEvent: Flow<VoiceTranscribeEvent>
    suspend fun initialize(): Boolean
    fun transcribe(data: ByteArray)
    fun dispose()
}

sealed class VoiceTranscribeEvent {
    data class Transcription(val text: String) : VoiceTranscribeEvent()
    object Start : VoiceTranscribeEvent()
    object Finish : VoiceTranscribeEvent()
    object Error : VoiceTranscribeEvent()
}