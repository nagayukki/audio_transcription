package app.naga.audiotranscription.domain.model

data class AccessToken(
    val accessToken: String,
    val expiresIn: Int,
    val startTimeStamp: Long,
    val endTimeStamp: Long,
)
