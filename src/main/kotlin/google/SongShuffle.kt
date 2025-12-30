package google

import java.util.*

data class Song(val artist: String, val title: String)

fun shufflePlaylist(playlist: List<Song>, k: Int = 1): List<Song> {
    val result = mutableListOf<Song>()

    /**
     * Intuition:
     * 1. Track artist frequency (in-degree) to prioritize those with the most songs.
     * 2. Use a Max-Heap to greedily pick the most frequent artist to avoid "clustering" at the end.
     * 3. Use a Deque as a sliding window (buffer) to enforce a gap of 'k' between same-artist songs.
     */
    val artistMap = playlist.groupBy { it.artist }
        .mapValues { (_, songs) -> ArrayDeque(songs) }

    val maxHeap = PriorityQueue<String>(compareByDescending { artistMap[it]?.size ?: 0 })
    maxHeap.addAll(artistMap.keys)

    val waitQueue: Deque<String> = ArrayDeque()

    while (maxHeap.isNotEmpty()) {
        val currentArtist = maxHeap.poll()

        artistMap[currentArtist]?.pollFirst()?.let { result.add(it) }

        waitQueue.offerLast(currentArtist)

        if (waitQueue.size > k) {
            val releasedArtist = waitQueue.pollFirst()
            if (artistMap[releasedArtist]?.isNotEmpty() == true) {
                maxHeap.offer(releasedArtist)
            }
        }
    }

    // At the end of shufflePlaylist
    return if (result.size == playlist.size) result else emptyList()
// or throw an exception if the constraint is impossible to meet
}

fun main() {
    val playlist = listOf(
        Song("Sound Garden", "Fell on Black Days"),
        Song("Sound Garden", "Black Hole Sun"),
        Song("Nirvana", "Drain You"),
        Song("Nirvana", "Lithium"),
        Song("Stone Temple Pilots", "Interstate Love song")
    )

    val shuffled = shufflePlaylist(playlist)
    shuffled.forEach { (artist, title) ->
        println("$artist: $title")
    }
}
