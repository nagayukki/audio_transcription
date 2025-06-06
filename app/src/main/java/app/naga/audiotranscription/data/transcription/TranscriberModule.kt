package app.naga.audiotranscription.data.transcription

import app.naga.audiotranscription.domain.transcription.VoiceTranscriber
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TranscriberModule {
    @Singleton
    @Binds
    internal abstract fun provideVoiceTranscriber(transcriber: MimiVoiceTranscriber): VoiceTranscriber
}