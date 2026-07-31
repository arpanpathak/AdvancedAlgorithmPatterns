# 17.3 Min Cost To Connect All Points (Prim's)

> **Source:** [`src/main/kotlin/tree/mst/MinCostToConnectAllPointsPrims.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/mst/MinCostToConnectAllPointsPrims.kt)
> **Pattern:** Prim's MST · **Core page**

## The Problem

Given `points[i] = [x, y]`, return the **minimum cost** to connect all points with edges whose weight is the **Manhattan distance** between the endpoints.

- Constraints: $1 \le n \le 1000$; coordinates fit in `Int`.

## Examples

```
Input:  points = [[0,0],[2,2],[3,10],[5,2],[7,0]]
Output: 20    (the MST weight — see [6.6](../ch06-graphs/min-cost-to-connect-all-points.md) for the Kruskal trace)
```

## Intuition — grow one tree by always taking the cheapest frontier edge

The complete graph here has $n(n-1)/2$ edges — materializing them all is wasteful. **Prim's algorithm** grows a tree from node 0:

1. Push `(0, 0)` into a min-heap (the seed edge).
2. Pop the cheapest edge `(dest, weight)`; if `dest` is already in the tree, skip; otherwise add it to the tree, add `weight` to the cost, and push edges from `dest` to *every* not-yet-in-tree point (with Manhattan distances computed on the fly).

The heap holds the **frontier** — every edge crossing the cut between "in the tree" and "outside". The cheapest crossing edge is always popped next, which is exactly the **cut property** ([17.0](pattern-primer.md)): an MST always contains the cheapest edge crossing any cut, so greedily taking the cheapest frontier edge is optimal.

**Why compute distances on the fly instead of an edge list?** The points are complete (every pair is an edge); a heap of frontier edges with lazy distance computation costs $O(n^2 \log n)$ and no edge list — ideal for dense graphs, which is why the repo labels it "Better for Dense Graph".

**The `notVisited.remove(dest)` check** is the "already in the tree?" test: `remove` returns `true` only if the node was still outside, so stale heap entries (a node pushed twice) are skipped automatically — the lazy-deletion pattern from [7.1](../ch07-heaps/top-k-frequent-elements.md)'s cousins.

## Approach 1 — Kruskal + Union-Find (see [6.6](../ch06-graphs/min-cost-to-connect-all-points.md))

Sort all edges and union components: $O(E \log E)$ — better for *sparse* graphs; needs the full edge list.

## Approach 2 — Prim's with a frontier heap (the repo's version, optimal for dense)

```kotlin
import java.util.*
import kotlin.math.abs

class MinCostToConnectAllPointsPrims {
    data class Node(val dest: Int, val weight: Int)

    /**
     * @param points points[i] = [x, y]
     * @return       minimum cost to connect all points
     */
    fun minCostConnectPoints(points: Array<IntArray>): Int {
        fun dist(from: Int, to: Int) =
            abs(points[from][0] - points[to][0]) + abs(points[from][1] - points[to][1])

        val notVisited = points.indices.toMutableSet()
        val pq = PriorityQueue<Node>(compareBy { it.weight })
        pq.add(Node(0, 0))                     // seed: start the tree at point 0
        var totalCost = 0

        while (notVisited.isNotEmpty()) {
            val (dest, weight) = pq.poll()
            if (!notVisited.remove(dest)) continue   // stale entry: already in the tree

            totalCost += weight                      // cheapest crossing edge: take it
            for (neighbour in notVisited) {
                pq.add(Node(neighbour, dist(dest, neighbour)))   // extend the frontier
            }
        }
        return totalCost
    }
}
```

```java
import java.util.*;

public class MinCostToConnectAllPointsPrims {
    private record Node(int dest, int weight) {}

    /**
     * @param points points[i] = [x, y]
     * @return       minimum cost to connect all points
     */
    public int minCostConnectPoints(int[][] points) {
        Set<Integer> remaining = new HashSet<>();
        for (int i = 0; i < points.length; i++) remaining.add(i);

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(Node::weight));
        pq.add(new Node(0, 0));                    // seed: start the tree at point 0
        int total = 0;

        while (!remaining.isEmpty()) {
            Node node = pq.poll();
            if (!remaining.remove(node.dest())) continue;   // stale entry

            total += node.weight();                // cheapest crossing edge: take it
            for (int other : remaining) {
                pq.add(new Node(other, dist(points, node.dest(), other)));  // extend frontier
            }
        }
        return total;
    }

    private int dist(int[][] p, int a, int b) {
        return Math.abs(p[a][0] - p[b][0]) + Math.abs(p[a][1] - p[b][1]);
    }
}
```

```cpp
#include <functional>
#include <queue>
#include <set>
#include <vector>
#include <cmath>

class MinCostToConnectAllPointsPrims {
public:
    /**
     * @param points points[i] = [x, y]
     * @return       minimum cost to connect all points
     */
    int minCostConnectPoints(std::vector<std::vector<int>>& points) {
        std::set<int> remaining;
        for (int i = 0; i < (int)points.size(); i++) remaining.insert(i);

        auto dist = [&](int a, int b) {
            return std::abs(points[a][0] - points[b][0]) + std::abs(points[a][1] - points[b][1]);
        };

        std::priority_queue<std::pair<int, int>,
                            std::vector<std::pair<int, int>>,
                            std::greater<>> pq;          // {weight, dest}
        pq.push({0, 0});                                 // seed: start the tree at point 0
        int total = 0;

        while (!remaining.empty()) {
            auto [weight, dest] = pq.top(); pq.pop();
            if (!remaining.erase(dest)) continue;        // stale entry

            total += weight;                             // cheapest crossing edge: take it
            for (int other : remaining) {
                pq.push({dist(dest, other), other});     // extend the frontier
            }
        }
        return total;
    }
};
```

```python
import heapq

def min_cost_connect_points(points: list[list[int]]) -> int:
    """
    @param points: points[i] = [x, y]
    @return:       minimum cost to connect all points
    """
    n = len(points)
    remaining = set(range(n))
    pq = [(0, 0)]                      # (weight, dest): seed the tree at point 0
    total = 0

    while remaining:
        weight, dest = heapq.heappop(pq)
        if dest not in remaining:
            continue                   # stale entry: already in the tree
        remaining.remove(dest)
        total += weight                # cheapest crossing edge: take it

        for other in remaining:
            d = abs(points[dest][0] - points[other][0]) + abs(points[dest][1] - points[other][1])
            heapq.heappush(pq, (d, other))     # extend the frontier
    return total
```

```rust
use std::cmp::Reverse;
use std::collections::{BinaryHeap, HashSet};

impl Solution {
    /// @param points points[i] = [x, y]
    /// @return       minimum cost to connect all points
    pub fn min_cost_connect_points(points: Vec<Vec<i32>>) -> i32 {
        let n = points.len();
        let mut remaining: HashSet<usize> = (0..n).collect();
        let mut pq: BinaryHeap<Reverse<(i32, usize)>> = BinaryHeap::new();
        pq.push(Reverse((0, 0)));            // seed the tree at point 0
        let mut total = 0;

        while !remaining.is_empty() {
            let Reverse((weight, dest)) = pq.pop().unwrap();
            if !remaining.remove(&dest) { continue; }   // stale entry
            total += weight;                 // cheapest crossing edge: take it

            for &other in &remaining {
                let d = (points[dest][0] - points[other][0]).abs()
                      + (points[dest][1] - points[other][1]).abs();
                pq.push(Reverse((d, other)));    // extend the frontier
            }
        }
        total
    }
}
```


## Dry run

**Input:** `points = [[0,0],[2,2],[3,10],[5,2],[7,0]]`.

```
distances: 0-1:4, 0-2:13, 0-3:7, 0-4:7, 1-2:9, 1-3:3, 1-4:7, 2-3:10, 2-4:14, 3-4:4

pq = [(0,0)], remaining = {0,1,2,3,4}, total = 0

pop (0,0): add 0.  total=0.   push (4,1),(7,3),(7,4),(13,2)
pop (4,1): add 1.  total=4.   push (3,3),(7,4),(9,2)
pop (3,3): add 3.  total=7.   push (4,4),(10,2)
pop (4,4): add 4.  total=11.  push (14,2)
pop (7,3): stale (3 in tree) -> skip.  pop (7,4): stale -> skip.
pop (9,2): add 2.  total=20.  (no remaining nodes to push)
remaining empty -> stop.  Output: 20 ✓
```

The heap's frontier discipline in action: after adding 4, the stale entries `(7,3)` and `(7,4)` surface and are skipped by the `remove` check — the same lazy-deletion pattern as the heap chapters. Every added edge was the cheapest *crossing* the current tree, which is the cut property — so the greedy is an MST.

## Complexity

**Time.** Each of n nodes pushes up to n edges:

$$
T(n) = O(n^2 \log n)
$$

**Space.** The frontier heap:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Kruskal's version** ([6.6](../ch06-graphs/min-cost-to-connect-all-points.md)) — edge-sorted + Union-Find: $O(E \log E)$, better for sparse graphs. The two pages side by side are the MST family complete.
- **Dense Prim ($O(n^2)$)** — without a heap: track the cheapest edge to the tree per node, scanning each round; the classic dense-graph alternative.
- **Interview follow-up:** "Why is the heap version 'better for dense graphs' as the repo says?" With all $n^2$ edges implicit (Manhattan distances computed on demand), there's no edge list to sort — the frontier heap touches only the edges it needs. Kruskal's edge sort would materialize and sort $O(n^2)$ edges; Prim's lazy heap amortizes them across the growth.
