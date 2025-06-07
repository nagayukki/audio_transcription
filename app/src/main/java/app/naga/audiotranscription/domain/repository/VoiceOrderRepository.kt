package app.naga.audiotranscription.domain.repository

import app.naga.audiotranscription.domain.model.VoiceOrder

interface VoiceOrderRepository {
    suspend fun addVoiceOrder(voiceOrder: VoiceOrder)

    suspend fun getVoiceOrders(): List<VoiceOrder>

    suspend fun deleteVoiceOrder(voiceOrder: VoiceOrder)

}