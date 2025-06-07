package app.naga.audiotranscription.domain.model

import java.util.Date

data class VoiceOrder(
    val id: Int? = null,
    val text: String,
    val action: Action,
    val createAt: Date
) {

    sealed class Action {
        object Dialog : Action()
        object Unknown : Action()

        fun name(): String {
            return when (this) {
                Dialog -> "dialog"
                else -> "unknown"
            }
        }

        companion object {
            fun fromString(action: String): Action {
                return when (action) {
                    "dialog" -> Dialog
                    else -> Unknown
                }
            }
        }
    }
}