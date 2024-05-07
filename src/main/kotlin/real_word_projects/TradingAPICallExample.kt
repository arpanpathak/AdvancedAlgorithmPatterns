package real_word_projects

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

// Generic Async REST API call wrapper. This enables non-blocking Network I/O API calls with ease using DSL
class RequestBuilder {
    var url: String = ""
    var method: String = "GET"
    private val headers = mutableMapOf<String, String>()

    fun headers(vararg headers: Pair<String, String>) {
        headers.forEach {
            this.headers[it.first] = it.second
        }
    }

    fun build(): Request = Request.Builder()
        .url(url)
        .method(method, null)
        .apply {
            headers.forEach { addHeader(it.key, it.value) }
        }
        .build()
}

suspend fun OkHttpClient.executeAsync(block: RequestBuilder.() -> Unit): Response {
    val requestBuilder = RequestBuilder().apply(block)
    val request = requestBuilder.build()
    return withContext(Dispatchers.IO) {
        this@executeAsync.newCall(request).execute()
    }
}

fun Response.prettyPrintJson(): String {
    this.body?.string()?.let { json ->
        val jsonElement = JsonParser.parseString(json)
        return GsonBuilder().setPrettyPrinting().create().toJson(jsonElement)
    }
    return "No content to display"
}

suspend fun main() {
    val client = OkHttpClient()

    // Define your Stock Platform API Keys
    // This is just for demonstration purpose. Use secured vault to store API keys
    val API_KEY = "<Your-API-Key>"
    val API_SECRET = "Your-API-Secret"
    val API_PLATFORM_ACCOUNT_URL = "https://paper-api.alpaca.markets/v2/account"

    val response = client.executeAsync {
        url = API_PLATFORM_ACCOUNT_URL
        headers(
            "APCA-API-KEY-ID" to API_KEY,
            "APCA-API-SECRET-KEY" to API_SECRET
        )
    }

    response.use {
        if (it.isSuccessful) {
            println("Successfully retrieved account :\n ${it.prettyPrintJson()}")
        } else {
            println("Failed to retrieve data. Status code: ${it.code}")
        }
    }
}
