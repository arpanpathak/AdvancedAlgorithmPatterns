package graph.topological_sort

class AlienDictionary {
    enum class State {
        NOT_VISITED, VISITING, VISITED
    }

    fun alienOrder(words: Array<String>): String {
        val graph = mutableMapOf<Char, MutableSet<Char>>()
        val visited = mutableMapOf<Char, State>()  // 0 = visiting, 1 = visited, -1 = not visited
        val result = StringBuilder()

        // Step 1: Build the graph and visited map
        words.forEach { word ->
            word.forEach { char ->
                graph.putIfAbsent(char, mutableSetOf())
                visited.putIfAbsent(char, State.NOT_VISITED)
            }
        }

        // Step 2: Build edges between characters
        for (i in 1 until words.size) {
            val (word1, word2) = words[i - 1] to words[i]

            // Check if the second word is a prefix of the first
            if (word1.startsWith(word2) && word1.length > word2.length) {
                return ""  // Invalid case, prefix conflict
            }

            for (j in 0 until minOf(word1.length, word2.length)) {
                if (word1[j] != word2[j]) {
                    graph[word1[j]]?.add(word2[j])
                    break
                }
            }
        }

        // Step 3: Perform DFS to find topological sort
        fun dfs(node: Char): Boolean {
            if (visited[node] == State.VISITING) return false  // Cycle detected
            if (visited[node] == State.VISITED) return true  // Already visited

            visited[node] = State.VISITING  // Mark as visiting
            graph[node]?.forEach { if (!dfs(it)) return false }

            visited[node] = State.VISITED  // Mark as visited
            result.append(node) // Add to result in topological order
            return true
        }

        // Step 4: Process all nodes (characters)
        for (char in visited.keys) {
            if (visited[char] == State.NOT_VISITED && !dfs(char)) {
                return ""  // Cycle detected
            }
        }

        return result.reverse().toString() // Reverse to get the correct order
    }
}