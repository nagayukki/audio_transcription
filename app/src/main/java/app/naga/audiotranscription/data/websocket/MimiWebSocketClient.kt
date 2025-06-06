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

@Singleton
class MimiWebSocketClient @Inject constructor(): WebSocketListener() {
    private lateinit var webSocket: WebSocket
    private var client: OkHttpClient? = null
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
        Log.i(TAG,"accessToken: $accessToken")
        val request = Request.Builder()
            .url("wss://service.mimi.fd.ai")
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "audio/x-pcm;bit=16;rate=16000;channels=1")
            .addHeader("x-mimi-process", "asr")
            .addHeader("x-mimi-lid-options", "lang=ja|en")
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
//        Log.i(TAG, "sendBinary: ${data.hex()}")
        webSocket.send(data)
    }

    private fun close(code: Int = 1000, reason: String = "Normal Closure") {
        webSocket.close(code, reason)
        client?.dispatcher?.executorService?.shutdown()
        client = null
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.i(TAG,"WebSocket opened: ${response.message}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.i(TAG,"Received message: $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.i(TAG,"Received bytes: ${bytes.hex()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.i(TAG,"Closing: $code / $reason")
        close(code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.i(TAG,"Closed: $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.i(TAG,"Error: ${t.message}")
        response?.let {
            Log.i(TAG,"Response: ${it.code} ${it.message}")
        }
    }

    companion object {
        private const val TAG = "MimiWebSocketClient"
    }
}