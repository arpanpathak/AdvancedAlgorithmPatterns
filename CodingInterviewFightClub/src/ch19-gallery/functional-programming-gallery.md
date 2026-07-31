# 19.7 The Functional-Programming Gallery

> **Sources:** [`CourseSchedule_II_Idiomatic.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/CourseSchedule_II_Idiomatic.kt) · `IsBipartileBFSFunctional.kt` · `CountWordsWithAGivenPrefix_Trie_FP.kt` · `ParallelCourses_II_FunctionalProgramming.kt` · `WordSquaresShorter.kt` · `graph/parallel` files
> **Pattern:** variant gallery — the same algorithms, written FP-style

## The meta-idea

The repo's functional files aren't different algorithms — they're the **same algorithms expressed through Kotlin's functional vocabulary**: `forEach`/`filter`/`map` replacing explicit loops, `buildList` replacing mutable accumulators, `getOrPut` replacing check-then-insert. Interviewers who code in Kotlin/Scala/Rust love this style; this page captures the patterns so you can *speak* it.

## 1. `CourseSchedule_II_Idiomatic.kt` — Kahn's with `also` and `forEach`

The canonical [6.3](../ch06-graphs/course-schedule-ii.md) algorithm, idiomatized: the queue's drain becomes `removeFirst().also { ... }`, and the neighbor processing becomes a `forEach` on the decrement:

```kotlin
class Solution {
    fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
        val graph = Array<MutableList<Int>>(numCourses) { mutableListOf() }
        val inDegree = IntArray(numCourses)
        val result = mutableListOf<Int>()

        // Build the graph and calculate in-degrees
        prerequisites.forEach { (course, preReq) ->
            graph[preReq].add(course)
            inDegree[course]++
        }

        // Initialize the queue with courses having no prerequisites
        val queue = ArrayDeque<Int>((0 until numCourses).filter { inDegree[it] == 0 })

        // Perform BFS (Kahn's Algorithm)
        while (queue.isNotEmpty()) {
            queue.removeFirst().also {
                result.add(it)
                graph[it].forEach { neighbor ->
                    if (--inDegree[neighbor] == 0) queue.add(neighbor)
                }
            }
        }

        // Cycle check: all courses done -> order, else empty
        return if (result.size == numCourses) result.toIntArray() else intArrayOf()
    }
}
```

**What's cool:** `ArrayDeque<Int>((0 until numCourses).filter { inDegree[it] == 0 })` — the *seed* is computed functionally instead of a loop; `--inDegree[neighbor] == 0` folds decrement-and-test into one expression. Same O(V+E), reads like a pipeline.

## 2. `IsBipartileBFSFunctional.kt` — BFS coloring with `buildList`

The [6.4](../ch06-graphs/is-graph-bipartite.md) algorithm, with `buildList` collecting neighbors and the coloring state carried through a `withDefault` map:

```kotlin
// sketch of the functional shape (IsBipartileBFSFunctional.kt)
// color: Map<Int, Int> withDefault { -1 }
// BFS per component: queue of nodes; color[node] set on first visit;
// conflict detected when a neighbor has the same color.
// The neighbor generation uses buildList { ... } instead of a mutable loop.
```

**What's cool:** the explicit `color[node] = 1 - color[neighbor]` dance of [6.4](../ch06-graphs/is-graph-bipartite.md) becomes a declarative state assignment; `buildList` returns the frontier without a `mutableListOf` + loop. The two-color logic is unchanged — only the container-building is functional.

## 3. `CountWordsWithAGivenPrefix_Trie_FP.kt` — the trie, functionally

[13.4](../ch13-tries/count-words-with-a-given-prefix.md) documents the imperative trie; this file builds the **same trie with `fold`/`getOrPut`** — insertion as a single `fold` expression:

```kotlin
// sketch of the FP trie (CountWordsWithAGivenPrefix_Trie_FP.kt)
// class TrieNode(val children: MutableMap<Char, TrieNode> = mutableMapOf(), var count: Int = 0)
// insert(word): word.fold(root) { node, c -> node.children.getOrPut(c) { TrieNode() } }
//     .also { it.count++ }            — the whole insertion is a fold + also
// countPrefix(prefix): prefix.fold(root) { node, c -> node.children[c] ?: return 0 }
//     .let { it.count }
```

**What's cool:** `fold` over the word *is* the descent — each character steps down a level, `getOrPut` creates missing nodes; `?:"` returns early on a missing prefix. The imperative version (loop + if-null-create) and this are the same walk; the FP version states it as a single expression chain.

## 4. `WordSquaresShorter.kt` — the trie word-square, compressed

[13.6](../ch13-tries/word-squares.md) documents the full trie + backtracking; `WordSquaresShorter.kt` and `WordSquare.kt` are the same algorithm at different lengths — the short one proves the core is ~30 lines:

```kotlin
// sketch of the shorter shape (WordSquaresShorter.kt)
// Trie with prefixes-to-words maps; backtrack(row): for each word with the column prefix,
// place it, recurse, undo. The shorter file merges the trie class into the solution.
```

**What's cool:** the pair shows exactly what the [13.6](../ch13-tries/word-squares.md) full page's scaffolding adds over the essential backtracking — useful when an interviewer asks "can you make this shorter?"

## 5. `ParallelCourses_II_FunctionalProgramming.kt` — bitmask DP, FP style

The hardest repo problem in FP clothing: parallel courses with semester constraints ([6.x](../ch06-graphs/course-schedule-ii.md) family), where the state is `(mask of taken courses, semester)` and the transition takes any valid subset of available courses:

```kotlin
// sketch of the FP shape (ParallelCourses_II_FunctionalProgramming.kt)
// prerequisites as an in-degree bitmask per course
// solve(mask): 0 if all taken, else:
//   available = courses not taken whose prerequisites are all in mask
//   take any non-empty subset of available (bitmask enumeration via submasks)
//   = 1 + min over subsets of solve(mask or subset)
// The FP version enumerates submasks with filter/map instead of loops.
```

**What's cool:** bitmask-subset enumeration expressed as `submask` iteration with functional combinators; the DP reads as "1 + min over valid subsets" — the same Held-Karp-style state machine as [17.4](../ch17-advanced-graphs/travelling-salesman-held-karp.md) (and [19.1](travelling-salesman-top-down.md)'s bitmask idioms).

## The style guide (steal these)

| Imperative | Functional (the repo's) | Why |
|---|---|---|
| `if (!map.containsKey(k)) map[k] = ...` | `map.getOrPut(k) { ... }` | one call, no branch |
| `val list = mutableListOf(); for (...) list.add(...)` | `buildList { ... }` | immutable result, block body |
| `for (x in xs) if (cond) acc += f(x)` | `xs.filter(cond).sumOf(f)` | intent over mechanics |
| `var state = ...; loop { state = f(state) }` | `xs.fold(init) { acc, x -> f(acc, x) }` | accumulation as an expression |
| `queue.removeFirst().let { ... }` | `queue.removeFirst().also { ... }` | side-effect + value in one |

## Variants & follow-ups

- **Course Schedule II** ([6.3](../ch06-graphs/course-schedule-ii.md)) — the imperative Kahn's this page idiomatizes.
- **Travelling Salesman Top-Down** ([19.1](travelling-salesman-top-down.md)) — the bitmask-FP sibling in the TSP family.
- **Interview follow-up:** "Does functional style change the complexity?" No — same loops underneath. It changes *readability* and *immutability-safety* (fewer mutable-accumulator bugs), which is exactly what a "write it in a functional language" follow-up is probing. Name the three combinators you used (`getOrPut`, `fold`, `buildList`) and why.
