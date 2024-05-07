package real_word_projects.trading

import net.jacobpeterson.alpaca.AlpacaAPI
import net.jacobpeterson.alpaca.model.endpoint.account.Account
import net.jacobpeterson.alpaca.model.properties.DataAPIType
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType

fun main() {
    // Initialize AlpacaAPI with your API key and secret
    val alpacaAPI = AlpacaAPI(
        "<Your-Paper-APIKey>",
        "<Your-Paper-APISecret>",
        EndpointAPIType.PAPER,
        DataAPIType.IEX
    )

    // Using runCatching to handle potential exceptions
    runCatching {
        val account: Account = alpacaAPI.account().get()

        println("Account ID: ${account.id}")
        println("Account Status: ${account.status}")
        println("Portfolio value: \$${account.portfolioValue}")
        println("Portfolio currency: ${account.currency}")

    }.onFailure {
        it.printStackTrace()
    }
}
