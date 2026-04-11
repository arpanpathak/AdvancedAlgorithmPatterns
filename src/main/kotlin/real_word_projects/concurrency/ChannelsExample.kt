package concurrency

import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Ping: sends a value, waits for pong's acknowledgment
suspend fun ping(
    sendChannel: Channel<Int>,
    ackChannel: Channel<Unit>,
    count: Int
) {
    for (i in 0 until count) {
        println("PING: sending $i")
        sendChannel.send(i)          // give value to pong
        ackChannel.receive()         // wait until pong says "done"
    }
    sendChannel.close()
    ackChannel.close()
}

// Pong: receives value, prints it, then signals back
suspend fun pong(
    receiveChannel: Channel<Int>,
    ackChannel: Channel<Unit>
) {
    for (value in receiveChannel) {
        println("PONG: received $value")
        ackChannel.send(Unit)        // tell ping: ready for next
    }
}

fun main(args: Array<String>): Unit = runBlocking {
    val dataChannel = Channel<Int>()   // ping → pong (data)
    val ackChannel = Channel<Unit>()   // pong → ping (handshake)

    launch { ping(dataChannel, ackChannel, 5) }
    launch { pong(dataChannel, ackChannel) }
    val task = async {

    }
}