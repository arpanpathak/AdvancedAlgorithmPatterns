# 19.6 The Eulerian Family — Captured

> **Sources:** [`src/main/kotlin/graph/euler/`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/tree/main/src/main/kotlin/graph/euler) — `circuit/FindEulerianCircuit.kt`, `circuit/path/ValidArrangementOfPairsRecursive.kt`, `circuit/CrackingTheSafe.kt`, `circuit/path/ReconstructItenary.kt` ([17.9](../ch17-advanced-graphs/reconstruct-itinerary.md) covers the itinerary star)
> **Pattern:** variant gallery — Hierholzer's algorithm in three costumes

## The family map

| File | Problem | Twist on Hierholzer |
|---|---|---|
| `FindEulerianCircuit.kt` | find a circuit (start == end) | post-order + `reversed()`; explicit all-edges-used check |
| `ValidArrangementOfPairsRecursive.kt` | Eulerian *path* in a directed graph | degree-difference to pick the start node |
| `CrackingTheSafe.kt` | shortest superstring of all kⁿ passwords | de Bruijn graph: Hierholzer on `n-1`-length prefixes |
| `ReconstructItenary.kt` | lexicographic itinerary | min-heap destinations ([17.9](../ch17-advanced-graphs/reconstruct-itinerary.md)) |

## 1. `FindEulerianCircuit.kt` — the circuit version

The full Hierholzer with an explicit **"all edges used" verification** — the DFS alone can't prove Eulerian-ness; the leftover-edge check does:

```kotlin
class EulerianCircuit(private val graph: Map<Int, List<Int>>) {

    fun findEulerianCircuit(): List<Int>? {
        if (!hasEulerianCircuit()) return null

        val circuit = mutableListOf<Int>()
        val remainingEdges = graph.mapValues { it.value.toMutableList() }.toMutableMap()

        val startVertex = graph.keys.firstOrNull { graph[it]?.isNotEmpty() == true } ?: return emptyList()

        dfsHierholzer(startVertex, remainingEdges, circuit)

        // Final check: if any edges remain, the graph was not Eulerian
        val allEdgesUsed = remainingEdges.values.all { it.isEmpty() }
        return if (allEdgesUsed) circuit.reversed() else null
    }

    private fun dfsHierholzer(
        u: Int,
        remainingEdges: MutableMap<Int, MutableList<Int>>,
        circuit: MutableList<Int>
    ) {
        // consume edges while available; post-order append = the circuit
        while (remainingEdges[u]?.isNotEmpty() == true) {
            val v = remainingEdges[u]!!.removeFirst()
            dfsHierholzer(v, remainingEdges, circuit)
        }
        circuit.add(u)
    }

    private fun hasEulerianCircuit(): Boolean {
        // every vertex with edges must have even degree (undirected) — the circuit condition
        return graph.all { (_, neighbors) -> neighbors.size % 2 == 0 }
    }
}
```

**What's cool:** `hasEulerianCircuit()` is the *precondition* (all degrees even), `allEdgesUsed` is the *postcondition* (the DFS consumed everything), and the `circuit.reversed()` is the post-order inversion — the three-part correctness story [17.9](../ch17-advanced-graphs/reconstruct-itinerary.md) tells with prose, told here with checks.

## 2. `ValidArrangementOfPairsRecursive.kt` — finding the right start

The Eulerian *path* (not circuit) needs a start node with `outDegree - inDegree == 1`; the degree map picks it, defaulting to any node:

```kotlin
class ValidArrangementOfPairsRecursive {
    fun validArrangement(pairs: Array<IntArray>): Array<IntArray> {
        val graph = mutableMapOf<Int, ArrayDeque<Int>>()
        val degree = mutableMapOf<Int, Int>().withDefault { 0 }

        // Build graph and track degree difference (out - in)
        pairs.forEach { (u, v) ->
            graph.getOrPut(u) { ArrayDeque() }.add(v)
            degree[u] = degree.getValue(u) + 1
            degree[v] = degree.getValue(v) - 1
        }

        // Find the start node (outDegree > inDegree)
        val start = degree.keys.firstOrNull { degree.getValue(it) == 1 } ?: pairs[0][0]

        val path = mutableListOf<IntArray>()

        // Recursive DFS for Hierholzer's algorithm
        fun dfs(u: Int) {
            while (graph[u]?.isNotEmpty() == true) {
                val v = graph[u]!!.removeFirst()
                dfs(v)
                path.add(intArrayOf(u, v))     // post-order: edge added after its tail
            }
        }

        dfs(start)
        return path.toTypedArray()
    }
}
```

**What's cool:** the degree map is *one* pass (`+1` for out, `-1` for in); the start-node rule ("outdegree − indegree = 1") is a single `firstOrNull`; and the edges themselves — not just vertices — are the post-order output, so the path is returned as `[u,v]` pairs without reconstruction. The `withDefault { 0 }` makes every node's degree computable without `getOrDefault` noise.

## 3. `CrackingTheSafe.kt` — the de Bruijn spin

The shortest string containing every `n`-digit password over `k` digits is an Eulerian path in the de Bruijn graph whose nodes are `(n-1)`-length prefixes:

```kotlin
class CrackingTheSafe {
    fun crackSafe(n: Int, k: Int): String {
        val visited = mutableSetOf<String>()
        val result = StringBuilder()

        fun dfs(currentPrefix: String) {
            for (i in 0 until k) {
                val digit = i.toString()
                val nextPassword = currentPrefix + digit

                if (nextPassword !in visited) {
                    visited.add(nextPassword)

                    val nextPrefix = nextPassword.substring(1)   // slide the window
                    dfs(nextPrefix)

                    result.append(digit)                          // post-order append
                }
            }
        }

        dfs("0".repeat(n - 1))
        return result + "0".repeat(n - 1)   // close the cycle: the first n-1 digits repeat
    }
}
```

**What's cool:** the `visited` set *is* the edge-set (each password is an edge from its `n-1` prefix); the post-order `result.append(digit)` is Hierholzer in miniature; and the trailing `+ "0".repeat(n-1)` closes the cyclic superstring — the classic de Bruijn construction that [17.9](../ch17-advanced-graphs/reconstruct-itinerary.md)'s "Eulerian path" chapter promises and this file delivers.

## Dry run (cracking the safe)

**Input:** `n = 2`, `k = 2` (all 2-digit binary passwords: 00, 01, 10, 11).

```
dfs("0"): digits 0,1:
  password "00" (new) -> dfs("0") [already in progress; "00" only once]
      "0"+1 = "01" (new) -> dfs("1"): "10" new -> dfs("0"): "00" seen, "01" seen.  append "0".
          append "0" -> result "00".  (from "10" branch: appends 0... )
  ...
The post-order appends build "00110..." and the final +"0" closes it.

Output: "00110" (or "01100") — length k^n + n - 1 = 5, containing 00, 01, 10, 11 ✓
```

Every `(n-1)`-prefix is a node; every password is an edge; the DFS never repeats an edge, and the post-order append + cycle-closing suffix produces the minimal superstring. This is the "why Eulerian paths matter beyond flight itineraries" answer.

## Complexity

All three: linear in edges with the greedy post-order consumption:

$$
T(V, E) = O(V + E)
$$

**Space.** The adjacency + recursion:

$$
S(V, E) = O(V + E)
$$

## Variants & follow-ups

- **Reconstruct Itinerary** ([17.9](../ch17-advanced-graphs/reconstruct-itinerary.md)) — the lexicographic member of the family, with the min-heap twist.
- **Theory.kt** (`graph/euler/circuit/Theory.kt`) — the repo's own prose on Eulerian conditions.
- **Interview follow-up:** "Why is post-order essential in all three?" A greedy pre-order would dead-end on a bridge-like edge (consume a cut edge early, strand the rest). Post-order appends a node/edge *after* its subtree is consumed — the deferral is what lets the DFS backtrack into remaining edges automatically. All three files are that one idea, three costumes.
