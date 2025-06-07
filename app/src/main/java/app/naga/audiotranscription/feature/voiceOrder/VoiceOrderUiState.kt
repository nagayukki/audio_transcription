package app.naga.audiotranscription.feature.voiceOrder

import app.naga.audiotranscription.domain.model.VoiceOrder

data class VoiceOrderUiState(
    val orders: List<VoiceOrder> = emptyList()
)

sealed class VoiceOrderUiEffect {
}