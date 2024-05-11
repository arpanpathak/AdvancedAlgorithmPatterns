package commons

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

object FileWriter {
    private val gson = Gson()

    suspend fun appendJsonToFile(fileName: String, data: Any) {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()  // Configure Gson with pretty printing
        val jsonData = gson.toJson(data) + "\n"

        withContext(Dispatchers.IO) {
            val path = Paths.get(fileName)
            // Ensure directory exists or create it
            path.parent?.let {
                if (!Files.exists(it)) {
                    Files.createDirectories(it)
                }
            }
            // Append the pretty-printed JSON data to the file
            Files.write(path, jsonData.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)
        }
    }

    private suspend fun appendTextToFile(fileName: String, text: String) {
        withContext(Dispatchers.IO) {
            val path = Paths.get(fileName)
            AsynchronousFileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND).use { fileChannel ->
                val byteBuffer = ByteBuffer.wrap(text.toByteArray())
                val position = fileChannel.size()  // Get the current size of file to append at the end
                fileChannel.write(byteBuffer, position).get()  // Perform the write operation
            }
        }
    }
}
