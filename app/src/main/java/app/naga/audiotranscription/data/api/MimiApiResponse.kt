package app.naga.audiotranscription.data.api

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    val code: Int,
    val error: String,
    val status: String,
    val progress: Int,
    val kind: String,
    val accessToken: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    val operationId: String,
    val selfLink: String,
    val targetLink: String,
    val startTimestamp: Long,
    val endTimestamp: Long
)
