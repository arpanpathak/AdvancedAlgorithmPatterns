# 19.11 Kahn's Algorithm — The Filter-Seed & DFS-Expression Versions

> **Sources:** [`src/main/kotlin/graph/topological_sort/AlienDictionary_BFS.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/topological_sort/AlienDictionary_BFS.kt) · `CourseSchedule_II_BFS.kt` · `graph/cycle/CourseSchedule.kt`
> **Pattern:** variant gallery — the queue seed without a for loop, and cycle-Detection as an expression

## The pattern the user flagged

The classic Kahn's ([6.3](../ch06-graphs/course-schedule-ii.md)) seeds its queue with a for loop:

```kotlin
val queue = ArrayDeque<Int>()
for (i in inDegree.indices) if (inDegree[i] == 0) queue.add(i)
```

The repo's BFS versions replace that loop with **a single `filter` expression** — and the diff is the whole "functional vs imperative" lesson:

```kotlin
// CourseSchedule_II_BFS.kt — filter to find the zero-in-degree nodes
val queue = ArrayDeque<Int>().apply {
    inDegree.indices.filter { inDegree[it] == 0 }.forEach { add(it) }
}
```

And in the *Alien Dictionary* BFS — with a `Map<Char, Int>` in-degree, the filter reads even more declaratively:

```kotlin
// AlienDictionary_BFS.kt
val queue = ArrayDeque<Char>().apply {
    addAll(inDegree.filter { it.value == 0 }.keys)     // all zero-in-degree chars, one expression
}
```

**What's cool:** the loop's three steps (allocate, iterate, conditionally add) become a pipeline: `filter` picks the qualifying indices/keys, `forEach`/`addAll` loads the queue. The `apply` block keeps it as a single initialization expression — the queue is *born* seeded, never built-then-filled. This is the exact "filter instead of for" upgrade the user asked about.

## The full `AlienDictionary_BFS.kt` — the BFS twin of [6.8](../ch06-graphs/alien-dictionary.md)

```kotlin
class AlienDictionary_BFS {
    fun alienOrder(words: Array<String>): String {
        val graph = mutableMapOf<Char, HashSet<Char>>()
        val inDegree = mutableMapOf<Char, Int>()

        // Every letter is a node with in-degree 0 to start
        words.forEach { word ->
            word.forEach { char ->
                inDegree.putIfAbsent(char, 0)
                graph.putIfAbsent(char, hashSetOf())
            }
        }

        // Adjacent word pairs reveal one edge each
        for (i in 0 until words.size - 1) {
            val currentWord = words[i]
            val nextWord = words[i + 1]
            val minLength = minOf(currentWord.length, nextWord.length)

            for (j in 0 until minLength) {
                val currentChar = currentWord[j]
                val nextChar = nextWord[j]
                if (currentChar != nextChar) {
                    if (graph[currentChar]!!.add(nextChar)) {   // new edge?
                        inDegree[nextChar] = inDegree[nextChar]!! + 1
                    }
                    break
                }
                // Prefix contradiction: "abc" can't come before "ab"
                if (j == minLength - 1 && currentWord.length > nextWord.length) return ""
            }
        }

        // Kahn's with the filter-seeded queue
        val queue = ArrayDeque<Char>().apply {
            addAll(inDegree.filter { it.value == 0 }.keys)
        }
        val result = StringBuilder()

        while (queue.isNotEmpty()) {
            val char = queue.removeFirst()
            result.append(char)
            graph[char]?.forEach { neighbor ->
                inDegree[neighbor] = inDegree[neighbor]!! - 1
                if (inDegree[neighbor] == 0) queue.add(neighbor)
            }
        }

        // Cycle check: all letters emitted?
        return if (result.length == inDegree.size) result.toString() else ""
    }
}
```

vs [6.8](../ch06-graphs/alien-dictionary.md)'s three-state DFS — this is the **Kahn's BFS twin**: same edges, opposite traversal, and the cycle test is `result.length == inDegree.size` instead of a `VISITING` flag. The filter-seed is the one-line signature of the BFS family.

## The DFS cycle check as an expression: `CourseSchedule.kt`

The same problem family's DFS version makes the whole recursion a `when` expression with a three-state enum:

```kotlin
class CourseSchedule {
    private var graph: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    private lateinit var status: Array<NodeStatus>
    private enum class NodeStatus { UNVISITED, EXPLORING, DONE }

    private fun hasCycle(numCourses: Int): Boolean {
        fun dfs(node: Int): Boolean = when (status[node]) {
            NodeStatus.EXPLORING -> true                     // back edge: cycle
            NodeStatus.DONE -> false                         // already proven acyclic
            NodeStatus.UNVISITED -> {
                status[node] = NodeStatus.EXPLORING
                graph[node]?.forEach { if (dfs(it)) return true }
                status[node] = NodeStatus.DONE
                false
            }
        }

        for (i in 0 until numCourses) {
            if (status[i] == NodeStatus.UNVISITED && dfs(i)) return true
        }
        return false
    }

    fun canFinish(numCourses: Int, prerequisites: Array<IntArray>): Boolean {
        graph = mutableMapOf()
        prerequisites.forEach { (course, prereq) ->
            graph.getOrPut(prereq) { mutableListOf() }.add(course)
        }
        status = Array(numCourses) { NodeStatus.UNVISITED }
        return !hasCycle(numCourses)
    }
}
```

**What's cool:** `dfs(node): Boolean = when (status[node])` — the three states *are* the three branches, no `if/else` chain. The `return true` inside `forEach` is the early-exit idiom (non-local return from the lambda). `getOrPut(prereq) { mutableListOf() }.add(course)` is the graph-build in one line — the [6.8](../ch06-graphs/alien-dictionary.md) pattern's skeleton.

## Dry run

**Input:** `prerequisites = [[1,0],[2,0],[3,1],[3,2]]` (4 courses).

```
inDegree = [0,1,1,2].  filter { it == 0 } -> indices [0].  queue = [0]

pop 0 -> result [0].  neighbors 1,2: inDegree[1]-- = 0 -> add; inDegree[2]-- = 0 -> add.
pop 1 -> result [0,1].  neighbor 3: inDegree[3]-- = 1 (not 0).
pop 2 -> result [0,1,2].  neighbor 3: inDegree[3]-- = 0 -> add.
pop 3 -> result [0,1,2,3].

result.size == 4 -> order [0,1,2,3] ✓   (a valid topological order)
```

The filter-seed's contribution: the queue starts *already containing every initially-free course*. With a cycle (e.g. `[1,0],[0,1]`), the queue drains early, `result.size < numCourses`, and the empty order is returned — the cycle check falls out of the size comparison.

## Complexity

**Time.** O(V + E) — filter is one O(V) pass:

$$
T(V, E) = O(V + E)
$$

**Space.** Graph + in-degree + queue:

$$
S(V, E) = O(V + E)
$$

## Variants & follow-ups

- **Course Schedule II** ([6.3](../ch06-graphs/course-schedule-ii.md)) — the imperative Kahn's page; this page's `filter`-seed is its functional upgrade.
- **Alien Dictionary** ([6.8](../ch06-graphs/alien-dictionary.md)) — the DFS three-state page; `AlienDictionary_BFS.kt` is the Kahn's twin shown here.
- **Interview follow-up:** "Why is `filter` better than the for loop here?" It's not *faster* — it's *declarative*: `inDegree.filter { it.value == 0 }` states the selection criterion, the loop states the mechanics. In an FP-friendly interview, that's the style signal; in a performance review, they're identical.
