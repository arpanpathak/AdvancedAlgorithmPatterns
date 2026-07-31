# 17.5 Shortest Path Visiting All Nodes

> **Source:** [`src/main/kotlin/graph/tsp/ShortestPathVisitingAllNodes.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/tsp/ShortestPathVisitingAllNodes.kt)
> **Pattern:** BFS over bitmask states · **Core page**

## The Problem

Given an undirected graph (adjacency list), return the length of the **shortest path that visits every node** (start anywhere, revisit allowed).

- Constraints: $1 \le n \le 12$ (small — the state space is $n \cdot 2^n$).

## Examples

```
Input:  graph = [[1,2,3],[0],[0],[0]]   -> Output: 4   (e.g., 1-0-2-0-3)
Input:  graph = [[1],[0,2,4],[1,3,4],[2],[1,2]] -> Output: 4
```

## Intuition — the state is (node, visited-mask); BFS over *states*

This is the TSP spirit ([17.4](travelling-salesman-held-karp.md)) without the "each node once" constraint — revisits allowed, so it's a *shortest path* problem, and the shortest path over states is **BFS**. The state isn't just the node: it's `(node, mask)` where `mask` = which nodes have been visited. The goal state is `(any node, all bits set)`.

```
queue of (node, mask, steps); start all nodes with their own bit set
visited[node][mask] = seen this state before

while queue:
    (node, mask, steps) = pop
    if mask == all-on: return steps            # visited everything!
    for neighbor in graph[node]:
        newMask = mask | (1 << neighbor)
        if not visited[neighbor][newMask]:
            visited[neighbor][newMask] = true
            push (neighbor, newMask, steps + 1)
```

**Why is `visited[node][mask]` (not just `visited[node]`) the right dedup?** Two paths reaching the same node with *different* visited sets have different futures — the one that has visited more can finish sooner. BFS over the combined state is what makes "revisit allowed" correct: the same node may be re-entered, but only as part of a *new* mask.

**Why BFS?** All edges have weight 1, so BFS finds the minimum steps; the first state with the full mask is the answer. The state count is $n \cdot 2^n$ — tiny at $n \le 12$ (12 × 4096), which is why the constraint says 12.

**Why start from every node?** The start is free — seeding all `(i, 1<<i)` states is the "try all starting points at once" BFS trick, saving a factor of n.

## Approach 1 — TSP-style DP (O(n^2 2^n))

`dp[mask][node]` over the same states: correct, but BFS is simpler and faster for the unweighted case.

## Approach 2 — Multi-source BFS over states (the repo's version, optimal)

```kotlin
class ShortestPathVisitingAllNodes {
    data class State(val node: Int, val mask: Int, val steps: Int)

    /**
     * @param graph adjacency list
     * @return      shortest path length visiting every node
     */
    fun shortestPathLength(graph: Array<IntArray>): Int {
        val n = graph.size
        val target = (1 shl n) - 1
        val visited = Array(n) { BooleanArray(target + 1) }
        val queue = ArrayDeque<State>().apply {
            (0 until n).forEach { i ->
                add(State(i, 1 shl i, 0))          // every node can start the walk
                visited[i][1 shl i] = true
            }
        }

        while (queue.isNotEmpty()) {
            val (node, mask, steps) = queue.removeFirst()
            if (mask == target) return steps        // visited everything: done

            graph[node].forEach { neighbor ->
                val newMask = mask or (1 shl neighbor)
                if (!visited[neighbor][newMask]) {
                    visited[neighbor][newMask] = true
                    queue.add(State(neighbor, newMask, steps + 1))
                }
            }
        }
        return -1                                   // unreachable (graph is connected in practice)
    }
}
```

```java
import java.util.*;

public class ShortestPathVisitingAllNodes {
    /**
     * @param graph adjacency list
     * @return      shortest path length visiting every node
     */
    public int shortestPathLength(int[][] graph) {
        int n = graph.length;
        int target = (1 << n) - 1;
        boolean[][] visited = new boolean[n][target + 1];
        Deque<int[]> queue = new ArrayDeque<>();     // {node, mask, steps}

        for (int i = 0; i < n; i++) {                // every node can start the walk
            queue.add(new int[]{i, 1 << i, 0});
            visited[i][1 << i] = true;
        }

        while (!queue.isEmpty()) {
            int[] state = queue.poll();
            int node = state[0], mask = state[1], steps = state[2];
            if (mask == target) return steps;        // visited everything: done

            for (int next : graph[node]) {
                int newMask = mask | (1 << next);
                if (!visited[next][newMask]) {
                    visited[next][newMask] = true;
                    queue.add(new int[]{next, newMask, steps + 1});
                }
            }
        }
        return -1;
    }
}
```

```cpp
#include <queue>
#include <vector>

class ShortestPathVisitingAllNodes {
public:
    /**
     * @param graph adjacency list
     * @return      shortest path length visiting every node
     */
    int shortestPathLength(std::vector<std::vector<int>>& graph) {
        int n = graph.size();
        int target = (1 << n) - 1;
        std::vector<std::vector<bool>> visited(n, std::vector<bool>(target + 1, false));
        std::queue<std::vector<int>> q;              // {node, mask, steps}

        for (int i = 0; i < n; i++) {                // every node can start the walk
            q.push({i, 1 << i, 0});
            visited[i][1 << i] = true;
        }

        while (!q.empty()) {
            auto state = q.front(); q.pop();
            int node = state[0], mask = state[1], steps = state[2];
            if (mask == target) return steps;        // visited everything: done

            for (int next : graph[node]) {
                int newMask = mask | (1 << next);
                if (!visited[next][newMask]) {
                    visited[next][newMask] = true;
                    q.push({next, newMask, steps + 1});
                }
            }
        }
        return -1;
    }
};
```

```python
from collections import deque

def shortest_path_length(graph: list[list[int]]) -> int:
    """
    @param graph: adjacency list
    @return:      shortest path length visiting every node
    """
    n = len(graph)
    target = (1 << n) - 1
    visited = [[False] * (target + 1) for _ in range(n)]
    q = deque()

    for i in range(n):                       # every node can start the walk
        q.append((i, 1 << i, 0))
        visited[i][1 << i] = True

    while q:
        node, mask, steps = q.popleft()
        if mask == target:
            return steps                     # visited everything: done

        for nxt in graph[node]:
            new_mask = mask | (1 << nxt)
            if not visited[nxt][new_mask]:
                visited[nxt][new_mask] = True
                q.append((nxt, new_mask, steps + 1))
    return -1
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param graph adjacency list
    /// @return      shortest path length visiting every node
    pub fn shortest_path_length(graph: Vec<Vec<i32>>) -> i32 {
        let n = graph.len();
        let target = (1usize << n) - 1;
        let mut visited = vec![vec![false; target + 1]; n];
        let mut q: VecDeque<(usize, usize, i32)> = VecDeque::new();

        for i in 0..n {                      // every node can start the walk
            q.push_back((i, 1usize << i, 0));
            visited[i][1usize << i] = true;
        }

        while let Some((node, mask, steps)) = q.pop_front() {
            if mask == target { return steps; }   // visited everything: done

            for &nxt in &graph[node] {
                let nxt = nxt as usize;
                let new_mask = mask | (1usize << nxt);
                if !visited[nxt][new_mask] {
                    visited[nxt][new_mask] = true;
                    q.push_back((nxt, new_mask, steps + 1));
                }
            }
        }
        -1
    }
}
```


## Dry run

**Input:** `graph = [[1,2,3],[0],[0],[0]]` (star: center 0, leaves 1,2,3). `n = 4`, `target = 1111 (15)`.

```
seeds: (0,0001,0), (1,0010,0), (2,0100,0), (3,1000,0)

Level 0 pops:
(0,0001,0) -> push (1,0011,1), (2,0101,1), (3,1001,1)
(1,0010,0) -> (0,0011,1) already visited (from seed 0) -> skip
(2,0100,0) -> (0,0101,1) new.  (3,1000,0) -> (0,1001,1) new.

Level 1 pops (steps=1 states, in order):
(1,0011,1): (0,0011,1) visited.
(2,0101,1): (0,0101,1) visited.
(3,1001,1): (0,1001,1) visited.
(0,0101,1): push (1,0111,2), (3,1101,2)   (2,0101,2) visited
(0,1001,1): push (1,1011,2), (2,1101,2)   (3,1001,2) visited

Level 2 pops (steps=2):
(1,0111,2): (0,0111,3) new.
(2,0111,2): (0,0111,3) visited.
(3,1101,2): (0,1101,3) new.
(1,1011,2): (0,1011,3) new.
(2,1101,2): (0,1101,3) visited.

Level 3 pops (steps=3):
(0,0111,3): push (1,0111,3) visited, (2,0111,3) visited, (3,1111,4) NEW! mask == target.

Output: 4 ✓   (e.g., walk 2 -> 0 -> 1 -> 0 -> 3)
```

The state dedup is doing visible work: `(0,0111,3)` is reached from `(1,0111,2)` and `(2,0111,2)` — the second arrival is skipped, saving a whole subtree. And `(3,1111,4)` is the first full-mask state popped; because BFS pops in step order, that's the minimum.

## Complexity

**Time.** $n \cdot 2^n$ states, each with degree edges:

$$
T(n) = O(n \cdot 2^n \cdot \bar{d}) = O(n \cdot 2^n)
$$

**Space.** The visited table:

$$
S(n) = O(n \cdot 2^n)
$$

## Variants & follow-ups

- **Travelling Salesman** ([17.4](travelling-salesman-held-karp.md)) — the same mask-as-state idea with *weights* and a no-revisit constraint: DP instead of BFS.
- **Minimum Genetic Mutation / Word Ladder** ([6.1](../ch06-graphs/word-ladder.md)) — BFS over string states; the state space idea in another costume.
- **Interview follow-up:** "Why can't you just BFS over nodes?" Revisits are allowed, so the plain node-BFS distance to the last unvisited node ignores *which* nodes are already covered. The mask in the state is what makes "I've seen more" count — the future depends on the visited set, not just the current node. That's why the state is `(node, mask)`, and why $n \le 12$: $n \cdot 2^n$ states are the real search space.
