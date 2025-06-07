package app.naga.audiotranscription.data.transcription

import app.naga.audiotranscription.data.websocket.MimiTranscriptionEvent
import app.naga.audiotranscription.data.websocket.MimiTranscriptionResponse
import app.naga.audiotranscription.data.websocket.MimiWebSocketClient
import app.naga.audiotranscription.domain.model.AccessToken
import app.naga.audiotranscription.domain.repository.AuthRepository
import app.naga.audiotranscription.domain.transcription.VoiceTranscribeEvent
import app.naga.audiotranscription.domain.transcription.VoiceTranscriber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.ByteString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MimiVoiceTranscriber @Inject constructor(
    private val authRepository: AuthRepository,
    private val mimiWebSocketClient: MimiWebSocketClient
) : VoiceTranscriber {
    private var accessToken: AccessToken? = null

    override val transcribeEvent: Flow<VoiceTranscribeEvent> get() {
        return mimiWebSocketClient.transcriptionFlow.map {
            when (it) {
                is MimiTranscriptionEvent.Closed -> VoiceTranscribeEvent.Finish
                is MimiTranscriptionEvent.Error -> VoiceTranscribeEvent.Error
                MimiTranscriptionEvent.Open -> VoiceTranscribeEvent.Start
                is MimiTranscriptionEvent.Transcription -> {
                    val text = convert(it.response)
                    VoiceTranscribeEvent.Transcription(text)
                }
            }
        }
    }

    override suspend fun initialize(): Boolean {
        if (accessToken != null) return true
        accessToken = runCatching { authRepository.fetchAccessToken() }
            .getOrNull()
        return accessToken != null
    }

    override fun transcribe(data: ByteArray) {
        val token = accessToken ?: return
        if (!mimiWebSocketClient.isConnected()) {
            mimiWebSocketClient.connect(token.accessToken)
        }
        val byteString: ByteString = ByteString.of(*data)
        mimiWebSocketClient.sendBinary(byteString)
    }

    override fun dispose() {
        mimiWebSocketClient.disconnect()
    }

    private fun convert(response: MimiTranscriptionResponse): String {
        val thresholdMs = 10
        val items = response.response
        if (items.isEmpty()) return ""

        val builder = StringBuilder()
        var prevEnd: Int? = null
        for ((i, item) in items.withIndex()) {
            val start = item.time.getOrNull(0) ?: continue
            val end = item.time.getOrNull(1) ?: continue
            if (prevEnd != null) {
                if (start - prevEnd > thresholdMs) {
                    builder.append("\n")
                }
            }
            builder.append(item.result)
            prevEnd = end
        }
        return builder.toString()
    }
}