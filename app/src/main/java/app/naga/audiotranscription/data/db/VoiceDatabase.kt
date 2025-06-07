package app.naga.audiotranscription.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.naga.audiotranscription.data.db.converters.DateConverter
import app.naga.audiotranscription.data.db.dao.VoiceOrderDao
import app.naga.audiotranscription.data.db.entity.VoiceOrderEntity

@Database(entities = [VoiceOrderEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class VoiceDatabase : RoomDatabase() {
    abstract fun voiceOrderDao(): VoiceOrderDao

    companion object {
        @Volatile
        private var INSTANCE: VoiceDatabase? = null

        fun getDatabase(context: Context): VoiceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VoiceDatabase::class.java,
                    "voice_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}