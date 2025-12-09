package graph

class WordLadder_II_clean {

    private data class GraphData(
        val graph: Map<String, Set<String>>,
        val distMap: Map<String, Int>,
        val foundEnd: Boolean
    )

    fun findLadders(beginWord: String, endWord: String, wordList: List<String>): List<List<String>> {
        val wordSet = wordList.toSet()
        if (endWord !in wordSet) return emptyList()

        val graphData = buildPrunedGraph(beginWord, endWord, wordSet)

        if (!graphData.foundEnd) return emptyList()

        val result = mutableListOf<List<String>>()
        reconstructPaths(
            current = beginWord,
            endWord = endWord,
            graph = graphData.graph,
            path = mutableListOf(),
            result = result
        )
        return result
    }

    // --- Helper 1: Neighbor Generation ---
    private fun getNeighbors(word: String, wordSet: Set<String>, endWord: String): List<String> = buildList {
        val chars = word.toCharArray()
        for (i in word.indices) {
            val originalChar = chars[i]
            for (c in 'a'..'z') {
                if (c == originalChar) continue

                chars[i] = c
                val newWord = String(chars)
                chars[i] = originalChar

                if (newWord == endWord || newWord in wordSet) {
                    add(newWord)
                }
            }
        }
    }

    // --- Helper 2: BFS (Pruning and Distance Calculation) ---
    private fun buildPrunedGraph(beginWord: String, endWord: String, wordSet: Set<String>): GraphData {
        val graph = mutableMapOf<String, MutableSet<String>>()
        val distMap = mutableMapOf<String, Int>()
        distMap[beginWord] = 0

        var currentLevel = setOf(beginWord)
        var foundEnd = false

        while (currentLevel.isNotEmpty() && !foundEnd) {
            val nextLevel = mutableSetOf<String>()

            for (word in currentLevel) {
                val dist = distMap[word]!!

                getNeighbors(word, wordSet, endWord).forEach { newWord ->
                    val newDist = dist + 1
                    val existingDist = distMap[newWord]

                    when {
                        // Case 1: New Word discovered (shortest path)
                        existingDist == null -> {
                            distMap[newWord] = newDist
                            nextLevel.add(newWord)
                            graph.getOrPut(word) { mutableSetOf() }.add(newWord)
                        }
                        // Case 2: Alternative shortest path found (essential for Word Ladder II)
                        existingDist == newDist -> {
                            graph.getOrPut(word) { mutableSetOf() }.add(newWord)
                        }
                    }

                    if (newWord == endWord) foundEnd = true
                }
            }
            currentLevel = nextLevel
        }

        return GraphData(graph, distMap, foundEnd)
    }

    // --- Helper 3: DFS (Path Reconstruction) ---
    private fun reconstructPaths(
        current: String,
        endWord: String,
        graph: Map<String, Set<String>>,
        path: MutableList<String>,
        result: MutableList<List<String>>
    ) {
        path.add(current)

        if (current == endWord) {
            result.add(path.toList())
        } else {
            // The graph is already pruned to only contain valid next steps (dist + 1)
            graph[current]?.forEach { next ->
                reconstructPaths(next, endWord, graph, path, result)
            }
        }

        path.removeLast()
    }
}