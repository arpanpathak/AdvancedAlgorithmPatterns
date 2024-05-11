package real_word_projects.trading

import commons.AlpacaAuthFields
import commons.AlpacaWebSocketFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object RealtimeMarketData {
    const val API_KEY_ID = "API Key id"
    const val API_SECRET = "Secret"

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {

        lateinit var messageChannel: Channel<String>

        try {
            val factory = AlpacaWebSocketFactory(API_KEY_ID, API_SECRET, this)

            // Read documentation for channel symbols https://alpaca.markets/deprecated/docs/api-documentation/api-v2/market-data/alpaca-data-api-v1/streaming/
            messageChannel = factory.createWebSocket(listOf("T.AAPL", "T.GOOG"))
            launch {
                for (message in messageChannel) {
                    println("Received message: $message")
                }
            }

            // Wait for a period of time or until you decide to close the channel and end the program
            delay(10000)

        } catch (e: Exception) {
            println("Error occured $e")
        } finally {
            messageChannel.close()
        }

    }
}
