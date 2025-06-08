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
        // FYI: https://mimi.readme.io/reference/scope
        // https://mimi.readme.io/docs/firststep-auth#step5--%E3%82%A2%E3%82%AF%E3%82%BB%E3%82%B9%E3%83%88%E3%83%BC%E3%82%AF%E3%83%B3%E3%81%AE%E7%99%BA%E8%A1%8C
        val grantType = "https://auth.mimi.fd.ai/grant_type/client_credentials"
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val clientIdParam = "$applicationId:$clientId".toRequestBody("text/plain".toMediaTypeOrNull())
        val clientSecretParam = clientSecret.toRequestBody("text/plain".toMediaTypeOrNull())
        val scope = (
            // Use asr settings
                "https://apis.mimi.fd.ai/auth/asr/websocket-api-service"
            // Use nic-asr settings
//                        "https://apis.mimi.fd.ai/auth/nict-asr/websocket-api-service"
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

    // TODO: 定義を別のところに
    // 設定する
    companion object {
        private val applicationId: String = ""
        private val clientId: String = ""
        private val clientSecret: String = ""
    }
}