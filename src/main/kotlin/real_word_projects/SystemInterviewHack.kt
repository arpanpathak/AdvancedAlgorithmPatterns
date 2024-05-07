package real_word_projects

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


// Concurrency /Async I/O
fun main() = runBlocking<Unit> {
    launch { // launch a new coroutine in the background
//        delay(1000L)
        testApis()
        println("World!")
    }.join()

    println("Hello,") // main coroutine continues here immediately
    // delay(2000L) // delaying for 2 seconds to keep JVM alive

}

// Generic Filter Function
fun <T> List<T>.customFilter(predicate: (T) -> Boolean): List<T> {
    val resultList = mutableListOf<T>()
    for (item in this) {
        if (predicate(item)) {
            resultList.add(item)
        }
    }
    return resultList
}


/**
 * CRUD API Calls and usage
 *
 */
class ApiManager {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun makeRequest(
        url: String,
        method: String = "GET",
        body: String? = null,
        onResult: (String?) -> Unit
    ) {
        coroutineScope.launch {
            val response = performRequest(url, method, body)
            withContext(Dispatchers.Main) {
                onResult(response)
            }
        }
    }

    private fun performRequest(urlString: String, method: String, body: String?): String? {
        val url = URL(urlString)
        var urlConnection: HttpURLConnection? = null
        return try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.apply {
                requestMethod = method
                if (body != null && (method == "POST" || method == "PUT")) {
                    doOutput = true
                    outputStream.write(body.toByteArray())
                }

                println("Will connect to URL : ${urlConnection}")
                connect()
                println("Done Response Code :- $responseCode  , $responseMessage")

            }
            if (urlConnection.responseCode in 200..299) {
                urlConnection.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            urlConnection?.disconnect()
        }
    }
}

suspend fun testApis() {
//    val apiManager = ApiManager()
//    val apiUrl = "https://real-api.com/api/users"  // Change to your actual API URL
//
//    listOf(
//        "GET" to null,
//        "POST" to """
//            {
//                "name": "John",
//                "age": 30
//            }
//        """
//    ).forEach { (method, body) ->
//        try {
//            val responseDeferred = async {
//                apiManager.makeRequest(apiUrl, method, body) { response ->
//                    println("$method Response: $response")
//                }
//            }
//            responseDeferred.await()
//        } catch (e: Exception) {
//            println("Failed to complete request: $method, Error: ${e.message}")
//        }
//    }
}
/*-- End of CUD API Tests **/