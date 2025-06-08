package app.naga.audiotranscription.feature.voice

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.naga.audiotranscription.data.audio.AudioRecorder
import app.naga.audiotranscription.domain.transcription.VoiceTranscribeEvent
import app.naga.audiotranscription.domain.transcription.VoiceTranscriber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
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
        bind()
    }

    override fun onCleared() {
        super.onCleared()
        stop()
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
        if (_state.value.recordState != RecordState.Stopped) {
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(recordState = RecordState.Loading)
            val result = runCatching {
                voiceTranscriber.initialize()
            }
            when {
                result.isSuccess -> {
                    val value = result.getOrNull()
                    if (value == false) {
                        _state.value = _state.value.copy(recordState = RecordState.Stopped)
                        // TODO: エラーの詳細
                        _effect.emit(VoiceUiEffect.Error("Error1"))
                        return@launch
                    }
                }
                result.isFailure -> {
                    val error = result.exceptionOrNull()
                    _state.value = _state.value.copy(recordState = RecordState.Stopped)
                    // TODO: エラーの詳細
                    _effect.emit(VoiceUiEffect.Error(error?.message ?: "Error2"))
                    return@launch
                }
            }
            _state.value = _state.value.copy(recordState = RecordState.Started, result = null)
            audioRecorder.startRecording { onBufferReady ->
                voiceTranscriber.transcribe(onBufferReady)
            }
        }
    }

    private fun stop() {
        audioRecorder.stopRecording()
        voiceTranscriber.dispose()
        _state.value = _state.value.copy(recordState = RecordState.Stopped)
    }

    private fun bind() {
        viewModelScope.launch {
            voiceTranscriber.transcribeEvent.collect {
                Log.d("Transcription", it.toString())
                when (it) {
                    VoiceTranscribeEvent.Finish -> {
                        if (audioRecorder.isRecording) {
                            stop()
                        }
                    }
                    VoiceTranscribeEvent.Error -> {
                        // TODO: エラーの詳細
                        _effect.emit(VoiceUiEffect.Error("Error3"))
                        stop()
                    }
                    VoiceTranscribeEvent.Start -> {}
                    is VoiceTranscribeEvent.Transcription -> {
                        val state = _state.value
                        // TODO: mimiから返ってくるのでそれをつかうのもよいが時間あれば
                        val result = state.result ?: VoiceResult(
                            sessionId = UUID.randomUUID().toString(),
                            text = "",
                        )
                        _state.value = state.copy(
                            result = result.copy(
                                text = it.text
                            )
                        )
                    }
                }
            }
        }
    }
}

sealed class VoiceAction {
    object StartRecording : VoiceAction()
    object StopRecording : VoiceAction()
}