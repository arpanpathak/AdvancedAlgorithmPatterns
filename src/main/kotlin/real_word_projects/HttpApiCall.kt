package real_word_projects

import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.net.HttpURLConnection
import java.net.URL

class HttpApiCall {
    fun callApi(url: String) {
        try {
            val urlObj = URL(url)
            with(urlObj.openConnection() as HttpURLConnection) {
                // Optional default is GET
                requestMethod = "GET"

                println("URL : $url")
                println("Response Code : $responseCode")
                println("Response Message : $responseMessage")

                // Reading response from input Stream
                inputStream.bufferedReader().use {
                    val response = it.readText()
                    println("Response: $response")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchUrl(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            // Return the body as a string
            return response.body?.string() ?: ""
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val url = "https://jsonplaceholder.typicode.com/posts/1" // This URL points to a specific post in the JSONPlaceholder API

//            async { fetchUrlAsync(url) }  // Asynchronously fetch the URL
//
//            result.await()
//                .onSuccess { content ->
//                    println("Response: $content")
//                }
//                .onFailure { error ->
//                    println("Error occurred: ${error.message}")
//                }
        }
    }
}