# 17.9 Reconstruct Itinerary

> **Source:** [`src/main/kotlin/graph/euler/circuit/path/ReconstructItenary.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/euler/circuit/path/ReconstructItenary.kt)
> **Pattern:** Hierholzer's algorithm · **Core page**

## The Problem

Given `tickets` (directed flights `[from, to]`), reconstruct an itinerary starting at `"JFK"` that uses **every ticket exactly once**. If multiple valid routes exist, return the **lexicographically smallest**.

- Constraints: tickets form a valid Eulerian path (guaranteed reachable).

## Examples

```
Input:  tickets = [["MUC","LHR"],["JFK","MUC"],["SFO","SJC"],["LHR","SFO"]]
Output: ["JFK","MUC","LHR","SFO","SJC"]

Input:  tickets = [["JFK","SFO"],["JFK","ATL"],["SFO","ATL"],["ATL","JFK"],["ATL","SFO"]]
Output: ["JFK","ATL","JFK","SFO","ATL","SFO"]
```

## Intuition — "use every edge once" is an **Eulerian path**; Hierholzer builds it by DFS with a *priority queue of destinations*

Each flight is an edge; the itinerary is a walk using every edge exactly once. **Hierholzer's algorithm** finds it in one elegant DFS:

1. Build `adj[airport] = min-heap of destinations` (a `PriorityQueue<String>` — the lexicographic requirement is met by always taking the smallest destination).
2. `dfs(airport)`: while the airport has unused destinations, **poll the smallest** and recurse into it.
3. **Add the airport to the *front* of the itinerary in post-order** (`addFirst`).

**Why post-order addFirst?** The naive greedy (always take the smallest edge) can dead-end early — e.g., `JFK → SFO` first at `["JFK","SFO"],["JFK","ATL"],...` would strand ATL. Hierholzer's fix: when DFS returns to a node with leftover edges, the *deferred* node is prepended — the post-order accumulation *is* the backtracking, built into the recursion instead of explicit.

**Why does the min-heap make it lexicographic?** Polling the smallest available destination at every step yields the lexicographically smallest Eulerian path — the greedy order plus the post-order correction is exactly the canonical solution.

## Approach 1 — Backtracking over unused tickets (exponential)

Try every unused ticket at each step and backtrack on dead ends: correct, factorial on dense tickets.

## Approach 2 — Hierholzer with a priority queue (the repo's version, optimal)

```kotlin
import java.util.PriorityQueue
import java.util.LinkedList

class ReconstructItenary {
    /**
     * @param tickets directed flights [from, to]
     * @return       itinerary starting at "JFK" using every ticket
     */
    fun findItinerary(tickets: List<List<String>>): List<String> {
        val adj = mutableMapOf<String, PriorityQueue<String>>()
        val itinerary = LinkedList<String>()

        // Build the graph: each airport's destinations sorted (min-heap)
        tickets.forEach { (source, destination) ->
            adj.getOrPut(source) { PriorityQueue<String>() }.add(destination)
        }

        // Recursive Hierholzer's Algorithm (DFS for an Eulerian Path)
        fun dfs(airport: String) {
            val destinations = adj[airport]

            while (destinations != null && destinations.isNotEmpty()) {
                // Poll the lexicographically smallest destination
                val nextAirport = destinations.poll()
                dfs(nextAirport)
            }

            // Add the current airport to the front of the list in post-order
            itinerary.addFirst(airport)
        }

        dfs("JFK")
        return itinerary
    }
}
```

```java
import java.util.*;

public class ReconstructItinerary {
    private final Map<String, PriorityQueue<String>> adj = new HashMap<>();
    private final LinkedList<String> itinerary = new LinkedList<>();

    /**
     * @param tickets directed flights [from, to]
     * @return       itinerary starting at "JFK" using every ticket
     */
    public List<String> findItinerary(List<List<String>> tickets) {
        for (List<String> t : tickets) {
            adj.computeIfAbsent(t.get(0), k -> new PriorityQueue<>()).add(t.get(1));
        }

        dfs("JFK");
        return itinerary;
    }

    private void dfs(String airport) {
        PriorityQueue<String> destinations = adj.get(airport);
        while (destinations != null && !destinations.isEmpty()) {
            dfs(destinations.poll());          // smallest destination first
        }
        itinerary.addFirst(airport);           // post-order prepend
    }
}
```

```cpp
#include <map>
#include <queue>
#include <string>
#include <vector>

class ReconstructItinerary {
    std::map<std::string, std::priority_queue<std::string,
              std::vector<std::string>, std::greater<std::string>>> adj;
    std::vector<std::string> itinerary;

    void dfs(const std::string& airport) {
        auto& q = adj[airport];
        while (!q.empty()) {
            auto next = q.top(); q.pop();
            dfs(next);                          // smallest destination first
        }
        itinerary.push_back(airport);           // post-order append (reversed later)
    }

public:
    /**
     * @param tickets directed flights [from, to]
     * @return       itinerary starting at "JFK" using every ticket
     */
    std::vector<std::string> findItinerary(std::vector<std::vector<std::string>>& tickets) {
        for (auto& t : tickets) adj[t[0]].push(t[1]);
        dfs("JFK");
        std::reverse(itinerary.begin(), itinerary.end());   // post-order -> actual order
        return itinerary;
    }
};
```

```python
import heapq

def find_itinerary(tickets: list[list[str]]) -> list[str]:
    """
    @param tickets: directed flights [from, to]
    @return:        itinerary starting at "JFK" using every ticket
    """
    adj = {}
    for frm, to in tickets:
        heapq.heappush(adj.setdefault(frm, []), to)   # min-heap of destinations

    itinerary = []

    def dfs(airport: str) -> None:
        while adj.get(airport):
            dfs(heapq.heappop(adj[airport]))          # smallest destination first
        itinerary.append(airport)                     # post-order

    dfs("JFK")
    return itinerary[::-1]                            # post-order -> actual order
```

```rust
use std::collections::{BinaryHeap, HashMap};
use std::cmp::Reverse;

impl Solution {
    /// @param tickets directed flights [from, to]
    /// @return       itinerary starting at "JFK" using every ticket
    pub fn find_itinerary(tickets: Vec<Vec<String>>) -> Vec<String> {
        let mut adj: HashMap<String, BinaryHeap<Reverse<String>>> = HashMap::new();
        for t in &tickets {
            adj.entry(t[0].clone()).or_default().push(Reverse(t[1].clone()));
        }

        let mut itinerary: Vec<String> = Vec::new();

        fn dfs(airport: &str, adj: &mut HashMap<String, BinaryHeap<Reverse<String>>>,
               itinerary: &mut Vec<String>) {
            while let Some(Reverse(next)) = adj.get_mut(airport).and_then(|q| q.pop()) {
                dfs(&next, adj, itinerary);           // smallest destination first
            }
            itinerary.push(airport.to_string());      // post-order
        }

        dfs("JFK", &mut adj, &mut itinerary);
        itinerary.reverse();                          // post-order -> actual order
        itinerary
    }
}
```

> **Sources:** [`src/main/kotlin/graph/euler/`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/tree/main/src/main/kotlin/graph/euler) — `circuit/FindEulerianCircuit.kt`, `circuit/path/ValidArrangementOfPairsRecursive.kt`, `circuit/CrackingTheSafe.kt`, `circuit/path/ReconstructItenary.kt` ([17.9](../ch17-advanced-graphs/reconstruct-itinerary.md) covers the itinerary star)
> **Pattern:** variant gallery — Hierholzer's algorithm in three costumes

### The family map

| File | Problem | Twist on Hierholzer |
|---|---|---|
| `FindEulerianCircuit.kt` | find a circuit (start == end) | post-order + `reversed()`; explicit all-edges-used check |
| `ValidArrangementOfPairsRecursive.kt` | Eulerian *path* in a directed graph | degree-difference to pick the start node |
| `CrackingTheSafe.kt` | shortest superstring of all kⁿ passwords | de Bruijn graph: Hierholzer on `n-1`-length prefixes |
| `ReconstructItenary.kt` | lexicographic itinerary | min-heap destinations ([17.9](../ch17-advanced-graphs/reconstruct-itinerary.md)) |

### 1. `FindEulerianCircuit.kt` — the circuit version

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

### 2. `ValidArrangementOfPairsRecursive.kt` — finding the right start

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

### 3. `CrackingTheSafe.kt` — the de Bruijn spin

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

### Dry run (cracking the safe)

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


## Dry run

**Input:** `tickets = [["JFK","SFO"],["JFK","ATL"],["SFO","ATL"],["ATL","JFK"],["ATL","SFO"]]`.

```
adj: JFK:[ATL,SFO], SFO:[ATL], ATL:[JFK,SFO]

dfs(JFK): poll ATL (smallest).  dfs(ATL): poll JFK.  dfs(JFK): poll SFO.
  dfs(SFO): poll ATL.  dfs(ATL): poll SFO.  dfs(SFO): empty -> post-order SFO.
    ATL empty -> post-order ATL.  SFO empty -> post-order SFO.
    JFK empty -> post-order JFK.  ATL empty -> post-order ATL.
    JFK empty -> post-order JFK.

post-order: [SFO, ATL, SFO, JFK, ATL, JFK] -> reversed: [JFK, ATL, JFK, SFO, ATL, SFO] ✓
```

The lexicographic trap is visible: the greedy "always smallest" would take `JFK→ATL` first, then `ATL→JFK`, then `JFK→SFO` — exactly the correct route, because the min-heap forces it. A naive `JFK→SFO` first would strand `ATL`; Hierholzer's post-order would still recover it, but the min-heap makes the lexicographic order automatic.

## Complexity

**Time.** Each edge pushed and popped once:

$$
T(E) = O(E \log E)
$$

**Space.** The adjacency heaps + itinerary:

$$
S = O(V + E)
$$

## Variants & follow-ups

- **Find Eulerian Circuit / Valid Arrangement Of Pairs** (`graph/euler/circuit/`) — Hierholzer on circuits and its rearrangement cousins; the same post-order DFS.
- **Cracking The Safe** (`graph/euler/circuit/CrackingTheSafe.kt`) — Eulerian paths applied to de Bruijn sequences; the algorithm behind the "shortest superstring of all PINs" puzzle.
- **Interview follow-up:** "Why does post-order prepend work where greedy backtracking is exponential?" When DFS exhausts a node's destinations and returns, any *leftover* edges of an ancestor are handled when the ancestor resumes — the prepend puts the deferred node in the right position automatically. Each edge is traversed exactly once, so the "backtracking" costs O(E) instead of factorial — the greedy order plus the structural deferral *is* the optimal route.
