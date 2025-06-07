package app.naga.audiotranscription.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import app.naga.audiotranscription.data.db.entity.VoiceOrderEntity

@Dao
interface VoiceOrderDao {
    @Insert
    suspend fun insert(voiceOrder: VoiceOrderEntity)

    @Query("SELECT * FROM voice_order ORDER BY createAt DESC")
    suspend fun getAll(): List<VoiceOrderEntity>

    @Delete
    suspend fun delete(voiceOrder: VoiceOrderEntity)
}