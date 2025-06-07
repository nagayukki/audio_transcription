package app.naga.audiotranscription.data.repository

import app.naga.audiotranscription.domain.repository.AuthRepository
import app.naga.audiotranscription.domain.repository.VoiceOrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    internal abstract fun provideAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    internal abstract fun provideVoiceOrderRepository(repository: VoiceOrderRepositoryImpl): VoiceOrderRepository
}