# 6.5 Cheapest Flights With K Stops

> **Source:** [`src/main/kotlin/graph/greedy/CheapestFlightsWithKStops.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/greedy/CheapestFlightsWithKStops.kt)
> **Pattern:** Dijkstra + stop budget · **Core page**

## The Problem

There are `n` cities connected by `flights[i] = [from, to, price]`. Find the cheapest price from `src` to `dst` with **at most `k` stops** (i.e., at most `k + 1` flight legs). Return `-1` if no such route exists.

- Constraints: $1 \le n \le 100$; $0 \le k \le n - 1$; prices up to $10^4$.

## Examples

```
Input:  n = 4, flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]],
        src = 0, dst = 3, k = 1
Output: 700   (0 -> 1 -> 3 costs 100+600=700; the cheaper 0->1->2->3 at 400 needs 2 stops)

Input:  same flights, k = 0
Output: -1    (0 -> 1 -> 3 would be one stop; only a direct 0 -> 3 flight is allowed — none exists)
```

## Intuition — Dijkstra, but the budget changes the rules

Without the stop limit, this is plain Dijkstra: explore cheapest-first, prune anything that reaches a city more expensively than a known path. With the `k` limit, **a more expensive path can be the only valid one** — a cheap route might blow the stop budget, while a pricier direct route fits within it. So the pure "cheapest wins" prune would throw away the answer.

The fix has two parts:

1. **State = `(city, cost, stops)`** — the stops counter rides along in the queue (the [state-tuple upgrade](pattern-primer.md) from the primer). Every leg increments it.
2. **Prune on the budget first** — a state with `stops > k + 1` is dead on arrival: drop it, regardless of cost. Only then apply the cost-based prune for efficiency.

Because the priority queue still orders by cost, the **first time `dst` is popped** is the cheapest among all states that survived the budget filter — that's the answer. (Every candidate pushed for `dst` has `stops <= k + 1` by the prune, and the queue yields them in cost order.)

**Why `k + 1` and not `k`?** The problem counts *stops* (intermediate cities); each stop requires a flight leg *after* it. A direct flight is 0 stops but 1 leg. The state's counter counts legs, so the budget is `k + 1` legs. Off-by-one here is the most common bug in this problem — say it out loud before coding.

## Approach 1 — Bellman-Ford, k+1 layered relaxations

Relax all edges `k + 1` times, tracking the best cost per stop-count: $O(k \cdot E)$, guaranteed correct (each round adds one leg). The classic alternative — same spirit, no heap. The repo's version below is the heap flavor.

## Approach 2 — Budget-aware Dijkstra (the repo's version, optimal)

```kotlin
import java.util.*

class CheapestFlightsWithKStops {
    data class Node(val dest: Int, val cost: Int)
    data class State(val node: Int, val cost: Int, val stops: Int)   // legs taken so far

    /**
     * @param n       number of cities (0..n-1)
     * @param flights flights[i] = [from, to, price]
     * @param src     departure city
     * @param dst     arrival city
     * @param k       max intermediate stops allowed
     * @return        cheapest price with at most k stops, or -1
     */
    fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
        // Build the graph from the input flights
        val graph = mutableMapOf<Int, MutableList<Node>>()
        flights.forEach { flight ->
            graph.getOrPut(flight[0]) { mutableListOf() }.add(Node(flight[1], flight[2]))
        }

        // Initialize the priority queue and cost tracking
        val minCost = Array(n) { Int.MAX_VALUE }
        val pq = PriorityQueue<State>(compareBy { it.cost })
        pq.offer(State(src, 0, 0))
        minCost[src] = 0

        while (pq.isNotEmpty()) {
            val (node, currentCost, stops) = pq.poll()

            // Drop states over the budget, or ones dominated by a cheaper arrival
            if (stops > k + 1 || currentCost > minCost[node]) continue
            minCost[node] = currentCost

            // First pop of dst = cheapest state that survived the budget filter
            if (node == dst) return currentCost

            // Explore neighbors
            graph[node]?.forEach { neighbor ->
                pq.offer(State(neighbor.dest, currentCost + neighbor.cost, stops + 1))
            }
        }

        return -1
    }
}
```

```java
import java.util.*;

public class CheapestFlightsWithKStops {
    // state: (city, accumulated cost, legs taken so far)
    private record State(int node, int cost, int stops) {}

    /**
     * @param n       number of cities (0..n-1)
     * @param flights flights[i] = [from, to, price]
     * @param src     departure city
     * @param dst     arrival city
     * @param k       max intermediate stops allowed
     * @return        cheapest price with at most k stops, or -1
     */
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int[] f : flights) {
            graph.computeIfAbsent(f[0], x -> new ArrayList<>()).add(new int[]{f[1], f[2]});
        }

        int[] minCost = new int[n];
        Arrays.fill(minCost, Integer.MAX_VALUE);
        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingInt(s -> s.cost));
        pq.offer(new State(src, 0, 0));
        minCost[src] = 0;

        while (!pq.isEmpty()) {
            State s = pq.poll();
            if (s.stops() > k + 1 || s.cost() > minCost[s.node()]) continue;
            minCost[s.node()] = s.cost();

            if (s.node() == dst) return s.cost();

            for (int[] edge : graph.getOrDefault(s.node(), List.of())) {
                pq.offer(new State(edge[0], s.cost() + edge[1], s.stops() + 1));
            }
        }
        return -1;
    }
}
```

```cpp
#include <queue>
#include <unordered_map>
#include <vector>

class CheapestFlightsWithKStops {
public:
    /**
     * @param n       number of cities (0..n-1)
     * @param flights flights[i] = [from, to, price]
     * @param src     departure city
     * @param dst     arrival city
     * @param k       max intermediate stops allowed
     * @return        cheapest price with at most k stops, or -1
     */
    int findCheapestPrice(int n, std::vector<std::vector<int>>& flights, int src, int dst, int k) {
        std::unordered_map<int, std::vector<std::pair<int, int>>> graph;
        for (auto& f : flights) graph[f[0]].push_back({f[1], f[2]});

        std::vector<int> minCost(n, INT_MAX);
        // min-heap ordered by (cost, node, stops)
        auto cmp = [](const std::array<int,3>& a, const std::array<int,3>& b) { return a[0] > b[0]; };
        std::priority_queue<std::array<int,3>, std::vector<std::array<int,3>>, decltype(cmp)> pq(cmp);
        pq.push({0, src, 0});
        minCost[src] = 0;

        while (!pq.empty()) {
            auto [cost, node, stops] = pq.top();
            pq.pop();

            if (stops > k + 1 || cost > minCost[node]) continue;
            minCost[node] = cost;

            if (node == dst) return cost;

            for (auto& [next, price] : graph[node]) {
                pq.push({cost + price, next, stops + 1});
            }
        }
        return -1;
    }
};
```

```python
import heapq

def find_cheapest_price(n: int, flights: list[list[int]], src: int, dst: int, k: int) -> int:
    """
    @param n:       number of cities (0..n-1)
    @param flights: flights[i] = [from, to, price]
    @param src:     departure city
    @param dst:     arrival city
    @param k:       max intermediate stops allowed
    @return:        cheapest price with at most k stops, or -1
    """
    graph: dict[int, list[tuple[int, int]]] = {}
    for frm, to, price in flights:
        graph.setdefault(frm, []).append((to, price))

    min_cost = [float("inf")] * n
    pq = [(0, src, 0)]                 # (cost, city, legs taken)
    min_cost[src] = 0

    while pq:
        cost, node, stops = heapq.heappop(pq)

        if stops > k + 1 or cost > min_cost[node]:
            continue
        min_cost[node] = cost

        if node == dst:
            return cost

        for nxt, price in graph.get(node, []):
            heapq.heappush(pq, (cost + price, nxt, stops + 1))

    return -1
```

```rust
use std::cmp::Reverse;
use std::collections::{BinaryHeap, HashMap};

impl Solution {
    /// @param n       number of cities (0..n-1)
    /// @param flights flights[i] = [from, to, price]
    /// @param src     departure city
    /// @param dst     arrival city
    /// @param k       max intermediate stops allowed
    /// @return        cheapest price with at most k stops, or -1
    pub fn find_cheapest_price(n: i32, flights: Vec<Vec<i32>>, src: i32, dst: i32, k: i32) -> i32 {
        let mut graph: HashMap<i32, Vec<(i32, i32)>> = HashMap::new();
        for f in &flights {
            graph.entry(f[0]).or_default().push((f[1], f[2]));
        }

        let mut min_cost = vec![i32::MAX; n as usize];
        // BinaryHeap is a max-heap; Reverse makes it a min-heap on (cost, node, stops)
        let mut pq = BinaryHeap::new();
        pq.push(Reverse((0, src, 0)));
        min_cost[src as usize] = 0;

        while let Some(Reverse((cost, node, stops))) = pq.pop() {
            if stops > k + 1 || cost > min_cost[node as usize] {
                continue;
            }
            min_cost[node as usize] = cost;

            if node == dst {
                return cost;
            }

            if let Some(neighbors) = graph.get(&node) {
                for &(nxt, price) in neighbors {
                    pq.push(Reverse((cost + price, nxt, stops + 1)));
                }
            }
        }
        -1
    }
}
```

## Dry run

**Input:** `n = 4`, `flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]]`, `src = 0`, `dst = 3`, `k = 1`.

```
pq: [(0,0,0)]                       (cost, city, stops)
pop (0,0,0): explore 0 -> 1 (+100).  pq: [(100,1,1)]
pop (100,1,1): 1 <= k+1=2 ok.  explore 1 -> 2 (+100 -> (200,2,2)), 1 -> 3 (+600 -> (700,3,2))
               pq: [(200,2,2), (700,3,2)]
pop (200,2,2): stops=2 > k+1=2? no (not greater).  explore 2 -> 0 (already min), 2 -> 3 (+200 -> (400,3,3))
               pq: [(400,3,3), (700,3,2)]
pop (400,3,3): stops=3 > k+1=2 -> continue (over budget — the cheap 0->1->2->3 route dies here)
pop (700,3,2): stops=2 <= 2, node==dst -> return 700 ✓
```

The dry run shows the entire point in one line: `(400,3,3)` — *cheaper* but *over budget* — is dropped, while `(700,3,2)` — *pricier* but *within budget* — is the answer. A plain Dijkstra would have returned 400.

## Complexity

**Time.** Each pushed state costs $O(\log)$ heap time; a city can be re-pushed with different stop counts (the cost prune is not strict here), worst case:

$$
T(V, E, k) = O(E \cdot k \cdot \log(E \cdot k))
$$

**Space.** The heap plus the graph:

$$
S(V, E) = O(V + E)
$$

In practice (small `k`) this behaves like $O(E \log E)$.

## Variants & follow-ups

- **Theoretical honesty corner:** the cost prune (`cost > minCost[node]`) can, in adversarial cases, discard a *pricier-but-fewer-stops* prefix that was the only way to reach `dst` within budget. The bulletproof alternatives: (a) prune on `minStops` (fewest legs seen) instead of cost; (b) the $k+1$-layered Bellman-Ford, which never prunes on cost at all. Interviewers rarely push this deep — but naming it shows you know why the constraint breaks vanilla Dijkstra.
- **Single-Threaded CPU** (`src/main/kotlin/heap/SingleThreadedCPU.kt`) — the same "state carries extra budget" idea applied to task scheduling.
- **Network Delay Time / Dijkstra plain** — this page with `k = n-1` (budget never binds) degenerates to textbook Dijkstra.
- **Interview follow-up:** "Why can't we just use `minCost` as a hard visited set?" Because the same city can be the right *intermediate* at different stop counts — a city reached once with 1 stop and once with 3 stops are different states with different futures. The stop counter is part of the identity, which is why it lives in the state tuple.
