package app.naga.audiotranscription.domain.repository

import app.naga.audiotranscription.domain.model.AccessToken

interface AuthRepository {
    suspend fun fetchAccessToken(): AccessToken

}