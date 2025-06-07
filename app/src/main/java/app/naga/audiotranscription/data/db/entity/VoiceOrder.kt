package app.naga.audiotranscription.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "voice_order")
data class VoiceOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val action: String,
    val createAt: Date
)