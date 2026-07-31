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
