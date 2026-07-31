# 6.23 N-Coloring Greedy (Flower Planting)

> **Source:** [`src/main/kotlin/graph/NColoringGreedy.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/NColoringGreedy.kt) (+ `WelshPowellClean.kt` — degree-ordered greedy)
> **Pattern:** greedy vertex coloring · **Core page**

## The Problem

Color a graph's vertices with ≤ 4 colors so no two adjacent share a color (the flower-planting variant: 4 flowers).

- Constraints: n ≤ 10⁴; each vertex ≤ 3 neighbors.

## Examples

```
Input:  n = 3, paths = [[1,2],[2,3],[3,1]]   -> Output: [1,2,3]  (a triangle needs 3 colors)
```

## Intuition — every vertex just avoids its neighbors' colors

Greedy coloring: process vertices in order; for each, pick the **first color not used by any neighbor**. With max-degree ≤ 3, 4 colors always suffice:

```kotlin
val colors = IntArray(n)          // 1..4

for (i in 0 until n) {
    val used = colors of i's neighbors (as a boolean set)
    for (color in 1..4) if (color not in used) { colors[i] = color; break }
}
```

**Why is greedy always correct here?** Each vertex has ≤ 3 neighbors — at most 3 colors are blocked, so one of the 4 is always free. The greedy never needs backtracking; the [11.0](../ch11-greedy/pattern-primer.md) "the local choice always works" shape.

**Why does Welsh-Powell sort by degree?** The degree-ordered variant processes high-degree vertices first — the classic optimization that makes greedy coloring *more* effective on general graphs (fewer colors used), though 4 suffices by the degree bound here.

## Approach 1 — Brute force (4ⁿ)

Try all colorings: correct, absurd.

## Approach 2 — Greedy with neighbor-color avoidance (the repo's version, optimal)

```kotlin
class NColoringGreedy {
    /**
     * @param n     vertex count
     * @param paths undirected edges (1-based)
     * @return      a valid 4-coloring
     */
    fun gardenNoAdj(n: Int, paths: Array<IntArray>): IntArray {
        val graph = Array(n) { mutableListOf<Int>() }
        for ((u, v) in paths) {
            graph[u - 1].add(v - 1)
            graph[v - 1].add(u - 1)
        }

        val colors = IntArray(n)

        for (i in 0 until n) {
            val used = BooleanArray(5)          // colors 1..4
            for (neighbor in graph[i]) {
                if (colors[neighbor] != 0) used[colors[neighbor]] = true
            }

            for (color in 1..4) {
                if (!used[color]) {
                    colors[i] = color
                    break
                }
            }
        }
        return colors
    }
}
```

```java
import java.util.*;

public class NColoringGreedy {
    /**
     * @param n     vertex count
     * @param paths undirected edges (1-based)
     * @return      a valid 4-coloring
     */
    public int[] gardenNoAdj(int n, int[][] paths) {
        List<Integer>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();

        for (int[] p : paths) {
            graph[p[0] - 1].add(p[1] - 1);
            graph[p[1] - 1].add(p[0] - 1);
        }

        int[] colors = new int[n];

        for (int i = 0; i < n; i++) {
            boolean[] used = new boolean[5];
            for (int nb : graph[i]) if (colors[nb] != 0) used[colors[nb]] = true;

            for (int c = 1; c <= 4; c++) {
                if (!used[c]) { colors[i] = c; break; }
            }
        }
        return colors;
    }
}
```

```cpp
#include <vector>

class NColoringGreedy {
public:
    /**
     * @param n     vertex count
     * @param paths undirected edges (1-based)
     * @return      a valid 4-coloring
     */
    std::vector<int> gardenNoAdj(int n, std::vector<std::vector<int>>& paths) {
        std::vector<std::vector<int>> graph(n);
        for (auto& p : paths) {
            graph[p[0] - 1].push_back(p[1] - 1);
            graph[p[1] - 1].push_back(p[0] - 1);
        }

        std::vector<int> colors(n, 0);

        for (int i = 0; i < n; i++) {
            bool used[5] = {false};
            for (int nb : graph[i]) if (colors[nb]) used[colors[nb]] = true;

            for (int c = 1; c <= 4; c++) {
                if (!used[c]) { colors[i] = c; break; }
            }
        }
        return colors;
    }
};
```

```python
def garden_no_adj(n: int, paths: list[list[int]]) -> list[int]:
    """
    @param n:     vertex count
    @param paths: undirected edges (1-based)
    @return:      a valid 4-coloring
    """
    graph = [[] for _ in range(n)]
    for u, v in paths:
        graph[u - 1].append(v - 1)
        graph[v - 1].append(u - 1)

    colors = [0] * n

    for i in range(n):
        used = {colors[nb] for nb in graph[i] if colors[nb]}
        for c in range(1, 5):
            if c not in used:
                colors[i] = c
                break

    return colors
```

```rust
impl Solution {
    /// @param n     vertex count
    /// @param paths undirected edges (1-based)
    /// @return      a valid 4-coloring
    pub fn garden_no_adj(n: i32, paths: Vec<Vec<i32>>) -> Vec<i32> {
        let n = n as usize;
        let mut graph = vec![Vec::new(); n];
        for p in &paths {
            graph[p[0] as usize - 1].push(p[1] as usize - 1);
            graph[p[1] as usize - 1].push(p[0] as usize - 1);
        }

        let mut colors = vec![0; n];

        for i in 0..n {
            let mut used = [false; 5];
            for &nb in &graph[i] {
                if colors[nb] != 0 { used[colors[nb] as usize] = true; }
            }

            for c in 1..=4 {
                if !used[c as usize] { colors[i] = c; break; }
            }
        }
        colors
    }
}
```

## Dry run

**Input:** `n = 3, paths = [[1,2],[2,3],[3,1]]`.

```
graph: 0:[1,2], 1:[0,2], 2:[0,1]
i=0: no neighbors colored.  color 1.  colors=[1,0,0]
i=1: neighbor 0 used 1.  first free = 2.  colors=[1,2,0]
i=2: neighbors 0 (1), 1 (2) used.  first free = 3.  colors=[1,2,3]

Output: [1,2,3] ✓  (a triangle needs exactly 3 colors)
```

With each vertex ≤ 3 neighbors, the "first free color" loop is guaranteed to find one in 1..4 — the greedy's correctness is the degree bound, not luck. A path graph `[[1,2],[2,3]]` would color `[1,2,1]` — reusing color 1 when legal (the greedy's defining trait).

## Complexity

**Time.** Each vertex's neighbors:

$$
T(V, E) = O(V + E)
$$

**Space.** The graph + colors:

$$
S(V, E) = O(V + E)
$$

## Variants & follow-ups

- **Is Graph Bipartite** ([6.4](is-graph-bipartite.md)) — the 2-color special case (BFS coloring).
- **Welsh-Powell** (`graph/NColoringGraph.kt`) — degree-ordered greedy for fewer colors.
- **Interview follow-up:** "Why does the degree bound make greedy exact?" A vertex with ≤ 3 neighbors sees ≤ 3 blocked colors — 4 colors guarantee a free one at every step, so the greedy never fails and no backtracking exists. The bound is the problem's gift; general graph coloring (chromatic number) is NP-hard precisely because this argument breaks.
