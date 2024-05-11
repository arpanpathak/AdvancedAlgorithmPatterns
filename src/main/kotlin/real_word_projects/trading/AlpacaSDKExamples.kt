package real_word_projects.trading

import commons.FileWriter.appendJsonToFile
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.jacobpeterson.alpaca.AlpacaAPI
import net.jacobpeterson.alpaca.model.endpoint.account.Account
import net.jacobpeterson.alpaca.model.endpoint.order.Order
import net.jacobpeterson.alpaca.model.endpoint.order.enums.OrderSide
import net.jacobpeterson.alpaca.model.endpoint.order.enums.OrderTimeInForce
import net.jacobpeterson.alpaca.model.properties.DataAPIType
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType

fun main() = runBlocking {
    val alpacaAPI = AlpacaAPI(
        "API_Key_ID",
        "API_Secret",
        EndpointAPIType.PAPER,
        DataAPIType.IEX
    )

    try {
        val account = async { fetchAccount(alpacaAPI) }
        val order = async { placeOrder(alpacaAPI) }

        val combinedData = mergeData(account.await(), order.await())
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
        "GOOGL",
        100,
        OrderSide.BUY,
        OrderTimeInForce.DAY,
        170.00,
        false
    )  // Assuming toString gives a meaningful representation
}

fun mergeData(account: Account, order: Order): Map<String, Any> {
    return mapOf(
        "Account" to account,
        "Order" to order
    )
}
