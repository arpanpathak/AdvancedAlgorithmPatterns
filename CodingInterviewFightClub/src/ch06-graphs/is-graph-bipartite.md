# 6.4 Is Graph Bipartite

> **Source:** [`src/main/kotlin/graph/IsBipartileGraph.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/IsBipartileGraph.kt)
> **Pattern:** BFS 2-coloring · **Core page**

## The Problem

Given an undirected graph as an adjacency list, return `true` if it is **bipartite** — its vertices can be split into two sets such that every edge connects one vertex from each set (equivalently: no edge connects two vertices of the same set).

- Constraints: $1 \le V \le 100$; the graph may be disconnected; no self-loops.

## Examples

```
Input:  graph = [[1,3],[0,2],[1,3],[0,2]]      (even cycle 0-1-2-3-0)
Output: true    (split {0,2} | {1,3} — every edge crosses the split)

Input:  graph = [[1,2,3],[0,2],[0,1,3],[0,2]]  (triangle 0-1-2 plus edges to 3)
Output: false   (the triangle is an odd cycle — provably not bipartite)
```

## Intuition — "paint the neighbors the other color"

A bipartite graph is exactly one you can **2-color**: pick a side for a vertex, force all its neighbors to the *other* side, their neighbors back to the first, and so on. If you ever must assign a color to a vertex that's already painted the *opposite* way — the graph is not bipartite.

The engine is BFS, but the `visited` array is upgraded to a **color array** (the ["visited is really a color array"](pattern-primer.md) upgrade from the primer): `NONE` = unseen, `A`/`B` = which side. The rules while BFS-ing a component:

- start vertex gets a color, queue it;
- for each neighbor: if `NONE`, paint it the *opposite* color and enqueue; if it already has the *same* color as the current vertex — **conflict, return false**;
- if it has the opposite color, it's consistent — do nothing (it's either queued or already processed).

**Why must we loop over all vertices?** The graph may be disconnected (multiple components). A component is independently 2-colorable, so each uncolored vertex seeds a fresh BFS. "Every component is 2-colorable" ⟺ "the graph is bipartite."

**The deep fact** (worth stating in an interview): a graph is bipartite ⟺ it has **no odd-length cycle**. A triangle forces three vertices where two must share a color; the alternating paint gets stuck. BFS 2-coloring *is* the constructive proof of this — it either produces the split or finds the odd cycle.

## Approach 1 — BFS 2-coloring (the repo's version, optimal)

```kotlin
class IsBipartileGraph {
    enum class Color { A, B, NONE }

    /**
     * @param graph adjacency list of an undirected graph
     * @return      true iff the graph can be split into two independent sets
     */
    fun isBipartite(graph: Array<IntArray>): Boolean {
        val assignedColors = Array<Color>(graph.size) { Color.NONE }

        fun bfs(i: Int): Boolean {
            if (assignedColors[i] == Color.A) return true   // this component already verified
            assignedColors[i] = Color.B
            val q = ArrayDeque<Int>()
            q.addLast(i)

            while (q.isNotEmpty()) {
                val cur = q.removeFirst()
                val curColor = assignedColors[cur]
                for (n in graph[cur]) {
                    when (assignedColors[n]) {
                        curColor -> return false            // CONFLICT: same-side edge
                        Color.NONE -> {
                            q.addLast(n)
                            assignedColors[n] = if (curColor == Color.A) Color.B else Color.A
                        }
                        else -> {}                          // opposite color: consistent
                    }
                }
            }
            return true
        }

        for (i in graph.indices) {
            if (!bfs(i)) return false
        }
        return true
    }
}
```

```java
import java.util.*;

public class IsGraphBipartite {
    private static final int NONE = 0, A = 1, B = 2;

    /**
     * @param graph adjacency list of an undirected graph
     * @return      true iff the graph can be split into two independent sets
     */
    public boolean isBipartite(int[][] graph) {
        int[] color = new int[graph.length];            // 0 = unseen, 1/2 = sides

        for (int i = 0; i < graph.length; i++) {
            if (color[i] != NONE) continue;             // component already colored
            color[i] = A;

            Queue<Integer> q = new LinkedList<>();
            q.offer(i);
            while (!q.isEmpty()) {
                int cur = q.poll();
                for (int n : graph[cur]) {
                    if (color[n] == color[cur]) return false;   // CONFLICT
                    if (color[n] == NONE) {
                        color[n] = color[cur] == A ? B : A;     // paint the other side
                        q.offer(n);
                    }
                }
            }
        }
        return true;
    }
}
```

```cpp
#include <queue>
#include <vector>

class IsGraphBipartite {
public:
    /**
     * @param graph adjacency list of an undirected graph
     * @return      true iff the graph can be split into two independent sets
     */
    bool isBipartite(std::vector<std::vector<int>>& graph) {
        std::vector<int> color(graph.size(), 0);        // 0 = unseen, 1/-1 = sides

        for (int i = 0; i < (int)graph.size(); i++) {
            if (color[i] != 0) continue;                // component already colored
            color[i] = 1;

            std::queue<int> q;
            q.push(i);
            while (!q.empty()) {
                int cur = q.front();
                q.pop();
                for (int n : graph[cur]) {
                    if (color[n] == color[cur]) return false;   // CONFLICT
                    if (color[n] == 0) {
                        color[n] = -color[cur];                 // paint the other side
                        q.push(n);
                    }
                }
            }
        }
        return true;
    }
};
```

```python
from collections import deque

def is_bipartite(graph: list[list[int]]) -> bool:
    """
    @param graph: adjacency list of an undirected graph
    @return:      true iff the graph can be split into two independent sets
    """
    color = [0] * len(graph)        # 0 = unseen, 1/-1 = sides

    for start in range(len(graph)):
        if color[start] != 0:
            continue                # component already colored
        color[start] = 1

        queue = deque([start])
        while queue:
            cur = queue.popleft()
            for n in graph[cur]:
                if color[n] == color[cur]:
                    return False    # CONFLICT
                if color[n] == 0:
                    color[n] = -color[cur]   # paint the other side
                    queue.append(n)
    return True
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param graph adjacency list of an undirected graph
    /// @return      true iff the graph can be split into two independent sets
    pub fn is_bipartite(graph: Vec<Vec<i32>>) -> bool {
        let mut color = vec![0i32; graph.len()];    // 0 = unseen, 1/-1 = sides

        for start in 0..graph.len() {
            if color[start] != 0 {
                continue;                           // component already colored
            }
            color[start] = 1;

            let mut queue = VecDeque::new();
            queue.push_back(start);
            while let Some(cur) = queue.pop_front() {
                for &n in &graph[cur] {
                    if color[n] == color[cur] {
                        return false;               // CONFLICT
                    }
                    if color[n] == 0 {
                        color[n] = -color[cur];     // paint the other side
                        queue.push_back(n);
                    }
                }
            }
        }
        true
    }
}
```

> **Note on the repo's variant:** the Kotlin original seeds each component with color `B` (and skips components already painted `A`) — an equivalent choice to seeding with `A`; the alternate-coloring logic is identical. The Java/C++/Python/Rust versions here use `1`/`-1`, the classic compact encoding: `-color[cur]` *is* the "paint the other side" step.

### 2. `IsBipartileBFSFunctional.kt` — BFS coloring with `buildList`

The [6.4](../ch06-graphs/is-graph-bipartite.md) algorithm, with `buildList` collecting neighbors and the coloring state carried through a `withDefault` map:

```kotlin
// sketch of the functional shape (IsBipartileBFSFunctional.kt)
// color: Map<Int, Int> withDefault { -1 }
// BFS per component: queue of nodes; color[node] set on first visit;
// conflict detected when a neighbor has the same color.
// The neighbor generation uses buildList { ... } instead of a mutable loop.
```

**What's cool:** the explicit `color[node] = 1 - color[neighbor]` dance of [6.4](../ch06-graphs/is-graph-bipartite.md) becomes a declarative state assignment; `buildList` returns the frontier without a `mutableListOf` + loop. The two-color logic is unchanged — only the container-building is functional.


## Dry run

**Input:** the even cycle `graph = [[1,3],[0,2],[1,3],[0,2]]` (0-1-2-3-0).

```
color: [0,0,0,0]
start 0: color[0]=1.  queue=[0]
pop 0: neighbors 1,3 -> NONE, paint -1 each.  queue=[1,3]   color=[1,-1,0,-1]
pop 1: neighbors 0 (color 1 == -1? no; already colored, skip), 2 (NONE -> paint 1)
                                                             color=[1,-1,1,-1]
pop 3: neighbors 0 (skip), 2 (color 1 == 1? same as cur -1? no — cur=3 is -1, n=2 is 1 -> opposite, skip)
queue empty -> component done, all colored.  color=[1,-1,1,-1]
start 1: color[1] != 0 -> skip.  ... all skipped.
Return true ✓  (split: {0,2} = 1, {1,3} = -1)
```

Now the triangle `graph = [[1,2,3],[0,2],[0,1,3],[0,2]]` — walk the component from 0:

```
start 0: color[0]=1.  queue=[0]
pop 0: paint 1,2,3 as -1.  queue=[1,2,3]
pop 1: neighbor 0 (-1 vs 1: opposite, ok); neighbor 2 -> color[2] == color[1] == -1 -> CONFLICT!
Return false ✓  (the edge 1-2 connects two vertices already forced to the same side)
```

That conflict *is* the odd cycle being discovered: `0-1-2-0` has three edges, and three vertices can't alternate two colors.

## Complexity

**Time.** Each vertex enqueued once per component BFS; each edge examined from both ends:

$$
T(V, E) = O(V + E)
$$

**Space.** The color array plus the BFS queue:

$$
S(V, E) = O(V)
$$

## Variants & follow-ups

- **DFS 2-coloring** (`src/main/kotlin/graph/IsBipartileGraphDfs.kt`, `IsBipartileGraphBFSFunctional.kt`) — same color array, recursion instead of the queue; the repo ships BFS, DFS, *and* a functional variant.
- **Possible Bipartition** — "can we split N people into two groups given mutual dislikes?" — literally this problem with the graph built from the dislike pairs.
- **Chromatic Number** (`src/main/kotlin/graph/ChromaticNumber.kt`) — the generalization to $k$ colors; bipartite is exactly the $k=2$ case.
- **Interview follow-up:** "Why does any *odd* cycle break it, but even cycles don't?" Alternating two colors around a cycle of length $L$ returns to the start with the *same* color iff $L$ is even. Odd $L$ forces the start vertex to be both colors — contradiction. That parity argument is the entire theorem.
