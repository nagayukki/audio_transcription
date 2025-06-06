package app.naga.audiotranscription.data.transcription

import app.naga.audiotranscription.data.websocket.MimiWebSocketClient
import app.naga.audiotranscription.domain.model.AccessToken
import app.naga.audiotranscription.domain.repository.AuthRepository
import app.naga.audiotranscription.domain.transcription.VoiceTranscriber
import okio.ByteString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MimiVoiceTranscriber @Inject constructor(
    private val authRepository: AuthRepository,
    private val mimiWebSocketClient: MimiWebSocketClient
) : VoiceTranscriber {
    private var accessToken: AccessToken? = null

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
}