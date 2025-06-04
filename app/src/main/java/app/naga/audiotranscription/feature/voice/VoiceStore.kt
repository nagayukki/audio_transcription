package app.naga.audiotranscription.feature.voice

import androidx.lifecycle.ViewModel
import app.naga.audiotranscription.data.audio.AudioRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class VoiceStore @Inject constructor(
    private val audioRecorder: AudioRecorder,
) : ViewModel() {
    private val _state = MutableStateFlow(VoiceUiState())
    val state: StateFlow<VoiceUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<VoiceUiEffect>()
    val effect: SharedFlow<VoiceUiEffect> = _effect.asSharedFlow()

    fun start() {
        _state.value = _state.value.copy(isRecording = true)
    }

    fun stop() {
        _state.value = _state.value.copy(isRecording = false)
    }
}