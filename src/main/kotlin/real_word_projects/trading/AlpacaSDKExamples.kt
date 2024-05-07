package real_word_projects.trading

import net.jacobpeterson.alpaca.AlpacaAPI
import net.jacobpeterson.domain.alpaca.account.Account

fun main() {
    // Initialize AlpacaAPI with your API key and secret
    val alpacaAPI = AlpacaAPI(
        "<YourAPIKey>",
        "YourAPISecret",
    )

    // Using runCatching to handle potential exceptions
    runCatching {
        val account: Account = alpacaAPI.account

        println("Account ID: ${account.id}")
        println("Account Status: ${account.status}")
    }.onFailure {
        it.printStackTrace()
    }
}
