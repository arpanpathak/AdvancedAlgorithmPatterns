package commons

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class AlpacaWebSocketFactory(private val apiKeyId: String, private val apiSecret: String, private val scope: CoroutineScope) {

    private val url = ALPACA_MARKET_WEBSOCKET_STREAM_URL
    private val httpClient = OkHttpClient()

    fun createWebSocket(streams: List<String>): Channel<String> {
        val request = Request.Builder()
            .url(url)
            .addHeader("APCA-API-KEY-ID", apiKeyId)
            .addHeader("APCA-API-SECRET-KEY", apiSecret)
            .build()

        val messageChannel = Channel<String>(Channel.UNLIMITED)
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("Connected to the server!")
                authenticate(webSocket)
                subscribeToStreams(webSocket, streams)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch {
                    messageChannel.send(text)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("Closing: $code / $reason")
                webSocket.close(1000, null)  // Ensure normal closure
                scope.launch {
                    messageChannel.close()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Error: " + t.message)
            }
        }

        httpClient.newWebSocket(request, webSocketListener)
        return messageChannel
    }

    private fun authenticate(webSocket: WebSocket) {
        webSocket.send("""
            {
                "action": "authenticate",
                "data": {
                    "key_id": "$apiKeyId",
                    "secret_key": "$apiSecret"
                }
            }
        """.trimIndent())
    }

    private fun subscribeToStreams(webSocket: WebSocket, streams: List<String>) {
        val formattedStreams = streams.joinToString(separator = "\",\"", prefix = "\"", postfix = "\"")
        webSocket.send("""
            {
                "action": "listen",
                "data": {
                    "streams": [$formattedStreams]
                }
            }
        """.trimIndent())
    }
}
