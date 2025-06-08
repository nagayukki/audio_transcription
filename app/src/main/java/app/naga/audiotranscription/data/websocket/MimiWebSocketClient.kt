package app.naga.audiotranscription.data.websocket

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

sealed class MimiTranscriptionEvent {
    data class Transcription(val response: MimiTranscriptionResponse) : MimiTranscriptionEvent()
    data class Error(val throwable: Throwable) : MimiTranscriptionEvent()
    data class Closed(val code: Int, val reason: String) : MimiTranscriptionEvent()
    object Open : MimiTranscriptionEvent()
}

@Singleton
class MimiWebSocketClient @Inject constructor(): WebSocketListener() {
    private lateinit var webSocket: WebSocket
    private var client: OkHttpClient? = null

    private val _transcriptionFlow = MutableSharedFlow<MimiTranscriptionEvent>(
        extraBufferCapacity = 1
    )
    val transcriptionFlow: SharedFlow<MimiTranscriptionEvent> get() = _transcriptionFlow

    fun isConnected(): Boolean {
        return client != null
    }

    fun connect(accessToken: String) {
        if (client != null) {
            return
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()
        this.client = client

        // TODO: 切り替える仕組みを作成
        // FYI: https://mimi.readme.io/reference/mimi-asr
        // https://mimi.readme.io/reference/mimi-asr-powered-by-nict
        // 戻りの形式に対応してないのでasrのみ
        val request = Request.Builder()
            .url("wss://service.mimi.fd.ai")
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            // asr
            .addHeader("x-mimi-process", "asr")
            // nict-asr
//            .addHeader("Content-Type", "audio/x-pcm;bit=16;rate=16000;channels=1")
//            .addHeader("x-mimi-process", "nict-asr")
//            .addHeader("x-mimi-input-language", "ja")
//            .addHeader("x-mimi-nict-asr-options", "response_format=v2;progressive=false;temporary=true;temporary_interval=1500")
            .build()

        webSocket = client.newWebSocket(request, this)
    }

    fun disconnect() {
        close()
    }

    fun sendText(message: String) {
        webSocket.send(message)
    }

    fun sendBinary(data: ByteString) {
        webSocket.send(data)
    }

    private fun close(code: Int = 1000, reason: String = "Normal Closure") {
        webSocket.close(code, reason)
        client?.dispatcher?.executorService?.shutdown()
        client = null
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(TAG,"WebSocket opened: ${response.message}")
        _transcriptionFlow.tryEmit(MimiTranscriptionEvent.Open)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d(TAG,"Received message: $text")
        val gson = com.google.gson.Gson()
        try {
            val response = gson.fromJson(text, MimiTranscriptionResponse::class.java)
            _transcriptionFlow.tryEmit(MimiTranscriptionEvent.Transcription(response))
        } catch (e: Exception) {
            _transcriptionFlow.tryEmit(MimiTranscriptionEvent.Error(e))
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d(TAG,"Received bytes: ${bytes.hex()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG,"Closing: $code / $reason")
        close(code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG,"Closed: $code / $reason")
        _transcriptionFlow.tryEmit(MimiTranscriptionEvent.Closed(code, reason))
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d(TAG,"Error: ${t.message}")
        response?.let {
            Log.d(TAG,"Response: ${it.code} ${it.message}")
        }
        _transcriptionFlow.tryEmit(MimiTranscriptionEvent.Error(t))
    }

    companion object {
        private const val TAG = "MimiWebSocketClient"
    }
}

data class MimiTranscriptionItem(
    val pronunciation: String,
    val result: String,
    val time: List<Int>
)

data class MimiTranscriptionResponse(
    val response: List<MimiTranscriptionItem>,
    val session_id: String,
    val status: String,
    val type: String
)