package app.naga.audiotranscription.data.repository

import app.naga.audiotranscription.data.db.dao.VoiceOrderDao
import app.naga.audiotranscription.data.db.entity.VoiceOrderEntity
import app.naga.audiotranscription.domain.model.VoiceOrder
import app.naga.audiotranscription.domain.repository.VoiceOrderRepository
import java.util.Date
import javax.inject.Inject


class VoiceOrderRepositoryImpl @Inject constructor(
    private val dao: VoiceOrderDao
): VoiceOrderRepository {
    override suspend fun addVoiceOrder(voiceOrder: VoiceOrder) {
        dao.insert(
            voiceOrder = VoiceOrderEntity(
                text = voiceOrder.text,
                action = voiceOrder.action.name(),
                createAt = Date()
            )
        )
    }

    override suspend fun getVoiceOrders(): List<VoiceOrder> {
        return dao.getAll().map {
            VoiceOrder(
                id = it.id,
                text = it.text,
                action = VoiceOrder.Action.fromString(it.action),
                createAt = it.createAt,
            )
        }
    }

    override suspend fun deleteVoiceOrder(voiceOrder: VoiceOrder) {
        dao.delete(
            VoiceOrderEntity(
                id = voiceOrder.id ?: 0,
                text = voiceOrder.text,
                action = voiceOrder.action.name(),
                createAt = Date()
            )
        )
    }
}