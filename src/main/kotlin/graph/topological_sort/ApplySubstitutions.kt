package graph.topological_sort

import java.util.*

class ApplySubstitutions {
    fun applySubstitutions(replacements: List<List<String>>, text: String): String {
        val map = replacements.associate { it[0] to it[1] }.toMutableMap()
        val adjList = mutableMapOf<String, MutableList<String>>().withDefault { mutableListOf() }
        val inDegree = mutableMapOf<String, Int>().withDefault { 0 }

        // Build the dependency graph
        for ((key, value) in replacements) {
            var i = 0
            while (i < value.length) {
                if (i + 2 < value.length && value[i] == '%' && value[i + 2] == '%') {
                    val depKey = value[i + 1].toString()
                    adjList[depKey]?.add(key) // depKey -> key
                    inDegree[key] = inDegree.getOrDefault(key, 0) + 1
                    i += 3 // Move past %X%
                } else {
                    i++
                }
            }
        }

        // Kahn's Algorithm for Topological Sorting
        val queue: Queue<String> = LinkedList()
        for ((key, degree) in inDegree) {
            if (degree == 0) queue.add(key) // Independent nodes
        }

        while (queue.isNotEmpty()) {
            val node = queue.poll()
            val resolvedValue = StringBuilder()

            var i = 0
            while (i < map[node]!!.length) {
                if (i + 2 < map[node]!!.length && map[node]!![i] == '%' && map[node]!![i + 2] == '%') {
                    val depKey = map[node]!![i + 1].toString()
                    resolvedValue.append(map[depKey] ?: "%$depKey%")
                    i += 3
                } else {
                    resolvedValue.append(map[node]!![i])
                    i++
                }
            }

            map[node] = resolvedValue.toString()

            // Reduce dependencies for dependent keys
            for (dependent in adjList[node]!!) {
                inDegree[dependent] = inDegree[dependent]!! - 1
                if (inDegree[dependent] == 0) queue.add(dependent)
            }
        }

        // Replace placeholders in the final text
        fun resolveFinalText(s: String): String {
            val sb = StringBuilder()
            var i = 0
            while (i < s.length) {
                if (i + 2 < s.length && s[i] == '%' && s[i + 2] == '%') {
                    val key = s[i + 1].toString()
                    sb.append(map[key] ?: "%$key%")
                    i += 3
                } else {
                    sb.append(s[i])
                    i++
                }
            }
            return sb.toString()
        }

        return resolveFinalText(text)
    }
}
