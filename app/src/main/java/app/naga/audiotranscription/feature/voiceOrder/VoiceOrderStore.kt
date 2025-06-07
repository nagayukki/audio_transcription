package app.naga.audiotranscription.feature.voiceOrder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.naga.audiotranscription.domain.repository.VoiceOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceOrderStore @Inject constructor(
    application: Application,
    private val voiceOrderRepository: VoiceOrderRepository
): AndroidViewModel(application) {
    private val _state = MutableStateFlow(VoiceOrderUiState())
    val state: StateFlow<VoiceOrderUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<VoiceOrderUiEffect>()
    val effect: SharedFlow<VoiceOrderUiEffect> = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                orders = voiceOrderRepository.getVoiceOrders()
            )
        }
    }

    fun sendAction(action: VoiceOrderAction) {
        when (action) {
            else -> Unit
        }
    }
}

sealed class VoiceOrderAction {

}