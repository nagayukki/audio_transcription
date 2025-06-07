package app.naga.audiotranscription.di

import android.content.Context
import androidx.room.Room
import app.naga.audiotranscription.data.db.VoiceDatabase
import app.naga.audiotranscription.data.db.dao.VoiceOrderDao
import app.naga.audiotranscription.domain.repository.VoiceOrderRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideVoiceDatabase(@ApplicationContext context: Context): VoiceDatabase {
        return Room.databaseBuilder(
            context,
            VoiceDatabase::class.java,
            "voice_database"
        ).build()
    }

    @Provides
    fun provideVoiceOrderDao(database: VoiceDatabase): VoiceOrderDao {
        return database.voiceOrderDao()
    }
}