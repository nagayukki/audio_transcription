package app.naga.audiotranscription.data.repository

import app.naga.audiotranscription.data.api.MimiApiService
import app.naga.audiotranscription.domain.model.AccessToken
import app.naga.audiotranscription.domain.repository.AuthRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val mimiApiService: MimiApiService
) : AuthRepository {

    override suspend fun fetchAccessToken(): AccessToken {
        val grantType = "https://auth.mimi.fd.ai/grant_type/client_credentials"
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val clientIdParam = "$applicationId:$clientId".toRequestBody("text/plain".toMediaTypeOrNull())
        val clientSecretParam = clientSecret.toRequestBody("text/plain".toMediaTypeOrNull())
        val scope = (
                "https://apis.mimi.fd.ai/auth/asr/http-api-service;" +
                        "https://apis.mimi.fd.ai/auth/asr/websocket-api-service"
//                        "https://apis.mimi.fd.ai/auth/nict-asr/http-api-service;" +
//                        "https://apis.mimi.fd.ai/auth/nict-asr/websocket-api-service;" +
//                        "https://apis.mimi.fd.ai/auth/nict-tts/http-api-service;" +
//                        "https://apis.mimi.fd.ai/auth/nict-tra/http-api-service"
                ).toRequestBody("text/plain".toMediaTypeOrNull())

        // API呼び出し実行
        val response = mimiApiService.fetchToken(grantType, clientIdParam, clientSecretParam, scope)
        return AccessToken(
            accessToken = response.accessToken,
            expiresIn = response.expiresIn,
            startTimeStamp = response.startTimestamp,
            endTimeStamp = response.endTimestamp
        )
    }

    companion object {
        private val applicationId: String = ""
        private val clientId: String = ""
        private val clientSecret: String = ""
    }
}