package graph.topological_sort

class AlienDictionary_BFS {
    fun alienOrder(words: Array<String>): String {
        val graph = mutableMapOf<Char, HashSet<Char>>()
        val inDegree = mutableMapOf<Char, Int>()
        words.forEach { word ->
            word.forEach { char ->
                inDegree.putIfAbsent(char, 0)
                graph.putIfAbsent(char, hashSetOf())
            }
        }

        for (i in 0 until words.size - 1) {
            val currentWord = words[i]
            val nextWord = words[i + 1]
            val minLength = minOf(currentWord.length, nextWord.length)
            for (j in 0 until minLength) {
                val currentChar = currentWord[j]
                val nextChar = nextWord[j]
                if (currentChar != nextChar) {
                    if (graph[currentChar]!!.add(nextChar)) {
                        inDegree[nextChar] = inDegree[nextChar]!! + 1
                    }
                    break
                }
                // Handle invalid case (e.g., "abc" before "ab")
                if (j == minLength - 1 && currentWord.length > nextWord.length) return ""
            }
        }

        val queue = ArrayDeque<Char>().apply { addAll(inDegree.filter { it.value == 0 }.keys) }
        val result = StringBuilder()
        while (queue.isNotEmpty()) {
            val char = queue.removeFirst()
            result.append(char)
            graph[char]?.forEach { neighbor ->
                inDegree[neighbor] = inDegree[neighbor]!! - 1
                if (inDegree[neighbor] == 0) queue.add(neighbor)
            }
        }

        return if (result.length == inDegree.size) result.toString() else ""
    }
}