package graph

class WordLadder_II_FinalCutPro {
    fun findLadders(beginWord: String, endWord: String, wordList: List<String>): List<List<String>> {
        val wordSet = wordList.toSet()
        if (endWord !in wordSet) return emptyList()

        val adj = mutableMapOf<String, MutableList<String>>()
        val distance = mutableMapOf<String, Int>().apply { put(beginWord, 0) }
        val queue = ArrayDeque<String>().apply { add(beginWord) }
        var found = false

        // Seperate method inside for neighbor generation
        fun getNeighbors(curr: String): List<String> {
            val neighbors = mutableListOf<String>()
            for (i in curr.indices) {
                for (char in 'a'..'z') {
                    if (char == curr[i]) continue
                    val next = curr.substring(0, i) + char + curr.substring(i + 1)
                    if (next in wordSet) neighbors.add(next)
                }
            }
            return neighbors
        }

        // Phase 1: BFS
        while (queue.isNotEmpty() && !found) {
            repeat(queue.size) {
                val curr = queue.removeFirst()
                val currDist = distance[curr]!!

                for (next in getNeighbors(curr)) {
                    if (distance[next] == null || distance[next] == currDist + 1) {
                        adj.getOrPut(next) { mutableListOf() }.add(curr)

                        if (distance[next] == null) {
                            distance[next] = currDist + 1
                            queue.add(next)
                        }
                        if (next == endWord) found = true
                    }
                }
            }
        }

        // Phase 2: Backtrack
        val results = mutableListOf<List<String>>()
        fun backtrack(curr: String, path: MutableList<String>) {
            if (curr == beginWord) {
                results.add(path.reversed())
                return
            }
            adj[curr]?.forEach { prev ->
                path.add(prev)
                backtrack(prev, path)
                path.removeAt(path.size - 1)
            }
        }

        if (found) backtrack(endWord, mutableListOf(endWord))
        return results
    }
}