package app.naga.audiotranscription.feature.voice

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.naga.audiotranscription.data.audio.AudioRecorder
import app.naga.audiotranscription.domain.transcription.VoiceTranscriber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceStore @Inject constructor(
    application: Application,
    private val audioRecorder: AudioRecorder,
    private val voiceTranscriber: VoiceTranscriber,
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(VoiceUiState())
    val state: StateFlow<VoiceUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<VoiceUiEffect>()
    val effect: SharedFlow<VoiceUiEffect> = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            voiceTranscriber.initialize()
        }
    }

    fun sendAction(action: VoiceAction) {
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        when (action) {
            VoiceAction.StartRecording -> start()
            VoiceAction.StopRecording -> stop()
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun start() {
        _state.value = _state.value.copy(isRecording = true)
        audioRecorder.startRecording { onBufferReady ->
            voiceTranscriber.transcribe(onBufferReady)
        }
    }

    private fun stop() {
        audioRecorder.stopRecording()
        voiceTranscriber.dispose()
        _state.value = _state.value.copy(isRecording = false)
    }
}

sealed class VoiceAction {
    object StartRecording : VoiceAction()
    object StopRecording : VoiceAction()
}