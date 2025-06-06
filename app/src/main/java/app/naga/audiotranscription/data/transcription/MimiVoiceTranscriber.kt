package app.naga.audiotranscription.data.transcription

import app.naga.audiotranscription.domain.model.AccessToken
import app.naga.audiotranscription.domain.repository.AuthRepository
import app.naga.audiotranscription.domain.transcription.VoiceTranscriber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MimiVoiceTranscriber @Inject constructor(
    private val authRepository: AuthRepository
) : VoiceTranscriber {
    private var accessToken: AccessToken? = null

    override suspend fun initialize(): Boolean {
        if (accessToken != null) return true
        accessToken = runCatching { authRepository.fetchAccessToken() }
            .getOrNull()
        return accessToken != null
    }

    override suspend fun transcribe(data: ByteArray) {
        if (accessToken == null) return
    }
}