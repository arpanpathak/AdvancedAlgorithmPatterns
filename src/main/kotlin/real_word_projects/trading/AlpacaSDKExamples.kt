package real_word_projects.trading

import commons.FileWriter.appendJsonToFile
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.jacobpeterson.alpaca.AlpacaAPI
import net.jacobpeterson.alpaca.model.endpoint.account.Account
import net.jacobpeterson.alpaca.model.endpoint.order.Order
import net.jacobpeterson.alpaca.model.endpoint.order.enums.OrderSide
import net.jacobpeterson.alpaca.model.endpoint.order.enums.OrderTimeInForce
import net.jacobpeterson.alpaca.model.endpoint.streaming.enums.StreamingMessageType
import net.jacobpeterson.alpaca.model.properties.DataAPIType
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType

fun main() = runBlocking {
    val alpacaAPI = AlpacaAPI(
        "PKN1UPZO3EU9F72CC9DI",
        "l3uW96bJmeavQ6ICTGmhaqN1CcTg2vYGOQKDN1vB",
        EndpointAPIType.PAPER,
        DataAPIType.IEX
    )

    try {
        val account = async { fetchAccount(alpacaAPI) }
        val order = async { placeOrder(alpacaAPI) }
        val messageReceived = CompletableDeferred<Unit>()

        val combinedData = mergeData(account.await(), order.await())
        withContext(Dispatchers.Default) { subscribeToTradeUpdates(alpacaAPI) }
        println("Combined Data: $combinedData")

        appendJsonToFile("trading_data.json", combinedData)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun fetchAccount(api: AlpacaAPI): Account {
    return api.account().get()  // Simulated API call
}

suspend fun placeOrder(api: AlpacaAPI): Order {
    return api.orders().requestLimitOrder(
        "AAPL",
        100,
        OrderSide.BUY,
        OrderTimeInForce.DAY,
        180.00,
        false
    )  // Assuming toString gives a meaningful representation
}

fun mergeData(account: Account, order: Order): Map<String, Any> {
    return mapOf(
        "Account" to account,
        "Order" to order
    )
}

suspend fun subscribeToTradeUpdates(api: AlpacaAPI) {
    api.streaming().connect()
    println("Attempting to connect to Alpaca streaming service.")

    // Wait until the WebSocket reports it is connected
    while (!api.streaming().isConnected()) {
        delay(100)  // Delay a bit before checking again to avoid a tight loop
        println("Waiting for connection...")
    }

    println("Connected to Alpaca streaming service.")

    api.streaming().setListener { messageType, message ->
        println("Message received - Type: $messageType, Message: $message")
    }

    api.streaming().streams(StreamingMessageType.TRADE_UPDATES)
    println("Subscribed to trade updates and listener set.")

    // Additional logging to monitor the status
    delay(5000)  // Wait a few seconds to catch initial messages
    println("Checking if still connected: ${api.streaming().isConnected()}")
}
