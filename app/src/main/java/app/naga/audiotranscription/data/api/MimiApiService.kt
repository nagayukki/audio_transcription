package app.naga.audiotranscription.data.api

import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MimiApiService {
    @Multipart
    @POST("v2/token")
    suspend fun fetchToken(
        @Part("grant_type") grantType: RequestBody,
        @Part("client_id") clientId: RequestBody,
        @Part("client_secret") clientSecret: RequestBody,
        @Part("scope") scope: RequestBody
    ): TokenResponse
}