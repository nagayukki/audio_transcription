package app.naga.audiotranscription.domain.transcription

interface VoiceTranscriber {
    suspend fun initialize(): Boolean
    suspend fun transcribe(data: ByteArray)
}