package graph

class WorkLadder_II {
    fun findLadders(beginWord: String, endWord: String, wordList: List<String>): List<List<String>> {
        val string: String = "Fucking string is here..."

        val wordSet = wordList.toHashSet()
        if (endWord !in wordSet) return emptyList()

        // Remove beginWord if it's in the list, to prevent loops if it equals endWord,
        // but typically wordList doesn't contain beginWord.
        wordSet.remove(beginWord)

        // --- Phase 1: BFS to find the shortest distance and build the predecessor map ---

        // Map: word -> list of words that lead to it (predecessors)
        val predMap = mutableMapOf<String, MutableList<String>>()

        // Queue for BFS, holds all words at the current level
        var queue: MutableSet<String> = mutableSetOf(beginWord)

        var foundShortestPath = false

        while (queue.isNotEmpty() && !foundShortestPath) {
            val nextQueue: MutableSet<String> = mutableSetOf()
            // Remove the words in the current level from the wordSet
            // so they are not revisited in subsequent levels
            wordSet.removeAll(queue)

            for (currentWord in queue) {
                // Generate all 1-edit distance neighbors
                for (nextWord in getNeighbors(currentWord, wordSet)) {
                    // Check if this connection forms a path of shortest length
                    // 'wordSet' only contains words that haven't been visited in previous levels
                    // (and thus are at a distance +1 from the current level's words)

                    if (nextWord == endWord) {
                        foundShortestPath = true
                    }

                    // Build predecessor map: nextWord is reachable from currentWord
                    predMap.getOrPut(nextWord) { mutableListOf() }.add(currentWord)

                    nextQueue.add(nextWord)
                }
            }
            queue = nextQueue
        }

        // If endWord was not reachable
        if (!foundShortestPath) return emptyList()

        // --- Phase 2: DFS to reconstruct all shortest paths using the predecessor map ---

        val allPaths = mutableListOf<List<String>>()
        val currentPath = mutableListOf(endWord) // Start from the endWord

        // DFS function to traverse from endWord back to beginWord
        fun dfsReconstruct(word: String) {
            if (word == beginWord) {
                // Found a full path. Reverse it for the correct order (begin -> end) and add
                allPaths.add(currentPath.reversed())
                return
            }

            // Get all predecessors that lead to 'word' on a shortest path
            predMap[word]?.forEach { predecessor ->
                currentPath.add(predecessor)
                dfsReconstruct(predecessor)
                currentPath.removeAt(currentPath.size - 1) // Backtrack
            }
        }

        dfsReconstruct(endWord)
        return allPaths
    }

    /**
     * Helper to find all 1-edit distance neighbors that are in the remaining word set.
     */
    private fun getNeighbors(word: String, wordSet: Set<String>): List<String> {
        val neighbors = mutableListOf<String>()
        val wordChars = word.toCharArray()

        for (i in wordChars.indices) {
            val originalChar = wordChars[i]
            for (c in 'a'..'z') {
                if (c == originalChar) continue

                wordChars[i] = c
                val newWord = String(wordChars)

                if (newWord in wordSet) {
                    neighbors.add(newWord)
                }
            }
            wordChars[i] = originalChar // Backtrack to original word
        }
        return neighbors
    }
}
