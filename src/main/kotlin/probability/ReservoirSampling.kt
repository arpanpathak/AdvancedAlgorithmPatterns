package probability

import kotlin.random.Random

class ReservoirSampling {
    fun <T> reservoirSampling(stream: Sequence<T>, k: Int): List<T> {
        val reservoir = mutableListOf<T>()
        var count = 0

        stream.forEach { item ->
            count++
            when {
                count <= k -> reservoir.add(item)
                else -> {
                    val index = Random.nextInt(count)
                    // Replace item
                    if ( index < k)
                        reservoir[index] = item
                }
            }
        }

        return reservoir
    }
}