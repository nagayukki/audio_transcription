package app.naga.audiotranscription.feature.voiceOrder

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.naga.audiotranscription.domain.model.VoiceOrder
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

    private val _effect = MutableSharedFlow<VoiceOrderUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<VoiceOrderUiEffect> = _effect.asSharedFlow()

    private val voiceData = mutableMapOf<String, String>()

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                orders = voiceOrderRepository.getVoiceOrders()
            )
        }
    }

    fun sendAction(action: VoiceOrderAction) {
        when (action) {
            is VoiceOrderAction.Insert -> {
                viewModelScope.launch {
                    voiceOrderRepository.addVoiceOrder(VoiceOrder(text = action.text, action = action.action))
                    _state.value = _state.value.copy(
                        orders = voiceOrderRepository.getVoiceOrders()
                    )
                }
            }
            is VoiceOrderAction.Delete -> {
                viewModelScope.launch {
                    voiceOrderRepository.deleteVoiceOrder(action.order)
                    _state.value = _state.value.copy(
                        orders = voiceOrderRepository.getVoiceOrders()
                    )
                }
            }
            is VoiceOrderAction.HandleText -> {
                val prevText = voiceData[action.sessionId] ?: ""
                val currentText = action.text
                voiceData[action.sessionId] = currentText
                val diffText = findDifference(prevText, currentText)
                Log.d("Transcription", "diffText: $diffText")
                val orders = findOrder(diffText)
                orders.forEach {
                    when(it.action) {
                        VoiceOrder.Action.Dialog -> {
                            _effect.tryEmit(VoiceOrderUiEffect.Dialog("${it.text}を検出しました"))
                        }
                        VoiceOrder.Action.Unknown -> Unit
                    }
                }
            }
        }
    }

    private fun findDifference(prevText: String, currentText: String): String {
        return if (currentText.startsWith(prevText)) {
            currentText.removePrefix(prevText).trim()
        } else {
            currentText
        }
    }

    private fun findOrder(text: String): List<VoiceOrder> {
        return _state.value.orders.mapNotNull { order ->
            if (text.contains(order.text)) {
                order
            } else {
                null
            }
        }
    }

}

sealed class VoiceOrderAction {
    data class Insert(val text: String, val action: VoiceOrder.Action) : VoiceOrderAction()
    data class Delete(val order: VoiceOrder) : VoiceOrderAction()
    data class HandleText(val sessionId: String, val text: String) : VoiceOrderAction()
}