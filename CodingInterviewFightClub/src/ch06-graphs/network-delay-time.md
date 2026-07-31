# 6.12 Network Delay Time

> **Source:** [`src/main/kotlin/graph/greedy/NetworkDelayTime.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/greedy/NetworkDelayTime.kt)
> **Pattern:** pure Dijkstra · **Core page**

## The Problem

Given `times[i] = [u, v, w]` (a signal travels u→v in w ms), `n` nodes and a start `k`: return the **time for ALL nodes to receive the signal**, or `-1` if some node is unreachable.

- Constraints: $1 \le n \le 100$; weights fit in `Int`.

## Examples

```
Input:  times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2
Output: 2   (node 1 at 1ms, node 3 at 1ms, node 4 at 2ms)
```

## Intuition — the answer is the *largest shortest-path distance*; compute them all with Dijkstra

This is Dijkstra's algorithm in its purest interview form: the signal spreads along shortest paths, so the last node to receive it is the one with the **maximum shortest-path distance** from `k`. Run Dijkstra ([6.5](cheapest-flights-with-k-stops.md) is the stop-budgeted variant; this is the plain version):

```
dist[k] = 0; pq = [(0, k)]
while pq not empty:
    (time, u) = pq.poll()
    if time > dist[u]: continue          # stale entry
    for (v, weight) in adj[u]:
        if dist[u] + weight < dist[v]:
            dist[v] = dist[u] + weight
            pq.add((dist[v], v))
answer = max(dist[1..n]); -1 if any is INF
```

**Why `if (time > dists[u]) continue`?** A node can be pushed multiple times (each improvement); only the best entry matters — the stale ones are skipped. This is the [7.1](../ch07-heaps/top-k-frequent-elements.md)-style lazy-deletion discipline on a PQ.

**Why is the max the answer (not a sum)?** The signal travels *in parallel* along every edge — all nodes start receiving simultaneously, so the completion time is the slowest (largest) shortest path, not the total. The repo's `maxDelay`/`visitedCount` track it inline: `visitedCount == n` ⟺ no `-1`.

**Why Dijkstra and not BFS?** BFS counts hops; here edges have *weights* (latency) — the PQ is what makes weighted shortest paths work. The `dist` array is the memo; the PQ is the frontier.

## Approach 1 — Bellman-Ford / Floyd-Warshall (O(n·E) / O(n³))

Correct for these sizes (see [17.8](../ch17-advanced-graphs/bellman-ford.md), `graph/dp/FloydWarshallAlgorithm.kt`), but the PQ version is the right tool.

## Approach 2 — Dijkstra with a stale-skip (the repo's version, optimal)

```kotlin
class NetworkDelayTime {
    data class State(val time: Int, val node: Int)

    /**
     * @param times directed [u, v, weight] edges
     * @param n     node count (1-indexed)
     * @param k     source node
     * @return      time for all nodes to receive the signal, or -1
     */
    fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int {
        val adj = times.groupBy({ it[0] }, { it[1] to it[2] })

        val dists = IntArray(n + 1) { Int.MAX_VALUE }.apply { this[k] = 0 }
        val pq = PriorityQueue<State>(compareBy { it.time })
        pq.add(State(0, k))

        var maxDelay = 0
        var visitedCount = 0

        while (pq.isNotEmpty()) {
            val (time, u) = pq.poll()

            if (time > dists[u]) continue        // stale entry: a better path was found

            visitedCount++
            maxDelay = maxOf(maxDelay, time)     // this node is settled at its final time

            adj[u]?.forEach { (v, weight) ->
                val newTime = dists[u] + weight
                if (newTime < dists[v]) {        // relaxation
                    dists[v] = newTime
                    pq.add(State(newTime, v))
                }
            }
        }
        return if (visitedCount == n) maxDelay else -1
    }
}
```

```java
import java.util.*;

public class NetworkDelayTime {
    /**
     * @param times directed [u, v, weight] edges
     * @param n     node count (1-indexed)
     * @param k     source node
     * @return      time for all nodes to receive the signal, or -1
     */
    public int networkDelayTime(int[][] times, int n, int k) {
        List<int[]>[] adj = new List[n + 1];
        for (int i = 1; i <= n; i++) adj[i] = new ArrayList<>();
        for (int[] t : times) adj[t[0]].add(new int[]{t[1], t[2]});

        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, k});

        int visited = 0, maxDelay = 0;
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int time = top[0], u = top[1];

            if (time > dist[u]) continue;        // stale entry

            visited++;
            maxDelay = Math.max(maxDelay, time);

            for (int[] e : adj[u]) {
                int v = e[0], w = e[1];
                if (dist[u] + w < dist[v]) {     // relaxation
                    dist[v] = dist[u] + w;
                    pq.offer(new int[]{dist[v], v});
                }
            }
        }
        return visited == n ? maxDelay : -1;
    }
}
```

```cpp
#include <queue>
#include <vector>
#include <climits>

class NetworkDelayTime {
public:
    /**
     * @param times directed [u, v, weight] edges
     * @param n     node count (1-indexed)
     * @param k     source node
     * @return      time for all nodes to receive the signal, or -1
     */
    int networkDelayTime(std::vector<std::vector<int>>& times, int n, int k) {
        std::vector<std::vector<std::pair<int,int>>> adj(n + 1);
        for (auto& t : times) adj[t[0]].push_back({t[1], t[2]});

        std::vector<int> dist(n + 1, INT_MAX);
        dist[k] = 0;

        auto cmp = [](auto& a, auto& b) { return a.first > b.first; };
        std::priority_queue<std::pair<int,int>, std::vector<std::pair<int,int>>, decltype(cmp)> pq(cmp);
        pq.push({0, k});

        int visited = 0, maxDelay = 0;
        while (!pq.empty()) {
            auto [time, u] = pq.top(); pq.pop();

            if (time > dist[u]) continue;        // stale entry

            visited++;
            maxDelay = std::max(maxDelay, time);

            for (auto& [v, w] : adj[u]) {
                if (dist[u] + w < dist[v]) {     // relaxation
                    dist[v] = dist[u] + w;
                    pq.push({dist[v], v});
                }
            }
        }
        return visited == n ? maxDelay : -1;
    }
};
```

```python
import heapq

def network_delay_time(times: list[list[int]], n: int, k: int) -> int:
    """
    @param times: directed [u, v, weight] edges
    @param n:     node count (1-indexed)
    @param k:     source node
    @return:      time for all nodes to receive the signal, or -1
    """
    adj = {}
    for u, v, w in times:
        adj.setdefault(u, []).append((v, w))

    dist = {i: float("inf") for i in range(1, n + 1)}
    dist[k] = 0
    pq = [(0, k)]

    visited = 0
    max_delay = 0

    while pq:
        time, u = heapq.heappop(pq)

        if time > dist[u]:
            continue                        # stale entry

        visited += 1
        max_delay = max(max_delay, time)

        for v, w in adj.get(u, []):
            if dist[u] + w < dist[v]:       # relaxation
                dist[v] = dist[u] + w
                heapq.heappush(pq, (dist[v], v))

    return max_delay if visited == n else -1
```

```rust
use std::cmp::Reverse;
use std::collections::{BinaryHeap, HashMap};

impl Solution {
    /// @param times directed [u, v, weight] edges
    /// @param n     node count (1-indexed)
    /// @param k     source node
    /// @return      time for all nodes to receive the signal, or -1
    pub fn network_delay_time(times: Vec<Vec<i32>>, n: i32, k: i32) -> i32 {
        let mut adj: HashMap<i32, Vec<(i32, i32)>> = HashMap::new();
        for t in &times { adj.entry(t[0]).or_default().push((t[1], t[2])); }

        let mut dist: HashMap<i32, i32> = (1..=n).map(|i| (i, i32::MAX)).collect();
        dist.insert(k, 0);
        let mut pq = BinaryHeap::new();
        pq.push((Reverse(0), k));

        let (mut visited, mut max_delay) = (0, 0);
        while let Some((Reverse(time), u)) = pq.pop() {
            if time > dist[&u] { continue; }        // stale entry

            visited += 1;
            max_delay = max_delay.max(time);

            if let Some(edges) = adj.get(&u) {
                for &(v, w) in edges {
                    if dist[&u] + w < dist[&v] {    // relaxation
                        dist.insert(v, dist[&u] + w);
                        pq.push((Reverse(dist[&u] + w), v));
                    }
                }
            }
        }
        if visited == n { max_delay } else { -1 }
    }
}
```

## Dry run

**Input:** `times = [[2,1,1],[2,3,1],[3,4,1]]`, `n = 4`, `k = 2`.

```
adj: 2->[(1,1),(3,1)], 3->[(4,1)]
dist = [_, 2:0, 1:INF, 3:INF, 4:INF].  pq = [(0,2)]

pop (0,2): time 0 <= dist[2]=0 -> settle.  visited=1, maxDelay=0.
  2->1: 0+1 < INF -> dist[1]=1.  pq+=(1,1)
  2->3: 0+1 < INF -> dist[3]=1.  pq+=(1,3)
pop (1,1): settle.  visited=2, maxDelay=1.  (no outgoing edges)
pop (1,3): settle.  visited=3, maxDelay=1.  3->4: 1+1 < INF -> dist[4]=2.  pq+=(2,4)
pop (2,4): settle.  visited=4, maxDelay=2.

visited == n -> Output: 2 ✓   (node 1 and 3 at 1ms, node 4 at 2ms)
```

The parallel-spread reading: at time 1ms, nodes 1 and 3 both have the signal; node 4 gets it one more hop later. `maxDelay` is just "the last settlement time" — the slowest node. The stale-skip never fires here (each node settles once), but on a graph with re-relaxations it's what keeps the loop linear.

## Complexity

**Time.** E relaxations × O(log V):

$$
T(V, E) = O(E \log V)
$$

**Space.** dist + PQ:

$$
S(V) = O(V)
$$

## Variants & follow-ups

- **Cheapest Flights With K Stops** ([6.5](cheapest-flights-with-k-stops.md)) — Dijkstra with a *stop budget* in the state (`dist[u][k]`).
- **Path With Maximum Probability** (`probability/PathWithMaximumProbability.kt`) — Dijkstra on log-probabilities (maximize instead of minimize).
- **Bellman-Ford** ([17.8](../ch17-advanced-graphs/bellman-ford.md)) — the no-PQ alternative that handles negative edges.
- **Interview follow-up:** "Why can't plain BFS answer this?" BFS finds minimum *hops*; edge weights make a 1-hop 100ms path worse than a 3-hop 3ms path. The PQ is what lets `dist` be refined in increasing order — the invariant that makes the first settlement final.
