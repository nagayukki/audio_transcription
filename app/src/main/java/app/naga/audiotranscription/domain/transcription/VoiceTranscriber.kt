package app.naga.audiotranscription.domain.transcription

interface VoiceTranscriber {
    suspend fun initialize(): Boolean
    fun transcribe(data: ByteArray)
    fun dispose()
}