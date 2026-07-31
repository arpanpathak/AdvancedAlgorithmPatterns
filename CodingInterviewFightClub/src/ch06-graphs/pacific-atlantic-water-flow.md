# 6.18 Pacific Atlantic Water Flow

> **Source:** [`src/main/kotlin/grid/PacificAtlanticWaterFlow.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/PacificAtlanticWaterFlow.kt)
> **Pattern:** reverse-flow multi-source BFS · **Core page**

## The Problem

Cells from which water flows to **both** the Pacific (top/left edges) and Atlantic (bottom/right edges); water flows to equal-or-lower neighbors.

- Constraints: m, n ≤ 200.

## Examples

```
Input:  heights = [[1,2,2,3,5],
                   [3,2,3,4,4],
                   [2,4,5,3,1],
                   [6,7,1,4,5],
                   [5,1,1,2,4]]
Output: [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]
```

## Intuition — reverse the flow: BFS *from* each ocean, uphill

"Water flows downhill to an ocean" is hard per-cell. Flip it: **start at each ocean's shore and walk *uphill* (to higher-or-equal neighbors)** — cells reachable from the Pacific shore can drain to the Pacific:

```
pacificReachable  = bfs from all top/left edge cells
atlanticReachable = bfs from all bottom/right edge cells
answer = cells in both sets
```

**Why reverse the direction?** Testing every cell's drainage is O(cells × paths); the shore-seeded BFS is one flood per ocean, O(m·n). The [6.16](surrounded-regions.md) border-seed trick with a slope rule.

**Why "higher-or-equal" going up?** `next.height >= cur.height` reversed = `cur.height >= next.height` downhill — exactly "water flows to equal-or-lower neighbors". The comparison direction is the whole logic.

## Approach 1 — DFS per cell with memo (check downhill)

For each cell, memoize "can reach Pacific/Atlantic": correct, more bookkeeping.

## Approach 2 — Reverse BFS from both shores (the repo's version, optimal)

```kotlin
import java.util.*

class PacificAtlanticWaterFlow {
    data class Cell(val r: Int, val c: Int)

    private val directions = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)

    /**
     * @param heights elevation grid
     * @return        cells draining to both oceans
     */
    fun pacificAtlantic(heights: Array<IntArray>): List<List<Int>> {
        val rows = heights.size
        val cols = heights[0].size

        fun bfs(starts: List<Cell>): Array<BooleanArray> {
            val reachable = Array(rows) { BooleanArray(cols) }.apply {
                starts.forEach { (r, c) -> this[r][c] = true }
            }

            val queue: Queue<Cell> = LinkedList(starts)

            while (queue.isNotEmpty()) {
                val (r, c) = queue.poll()

                for ((dr, dc) in directions) {
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until rows && nc in 0 until cols &&
                        !reachable[nr][nc] && heights[nr][nc] >= heights[r][c]) {
                        reachable[nr][nc] = true      // uphill from here
                        queue.offer(Cell(nr, nc))
                    }
                }
            }
            return reachable
        }

        val pacific = bfs(
            (0 until rows).map { Cell(it, 0) } + (0 until cols).map { Cell(0, it) }
        )
        val atlantic = bfs(
            (0 until rows).map { Cell(it, cols - 1) } + (0 until cols).map { Cell(rows - 1, it) }
        )

        return (0 until rows).flatMap { r ->
            (0 until cols).filter { c -> pacific[r][c] && atlantic[r][c] }
                .map { c -> listOf(r, c) }
        }
    }
}
```

```java
import java.util.*;

public class PacificAtlanticWaterFlow {
    private int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private boolean[][] bfs(int[][] h, Queue<int[]> starts) {
        int m = h.length, n = h[0].length;
        boolean[][] reach = new boolean[m][n];

        for (int[] s : starts) reach[s[0]][s[1]] = true;

        while (!starts.isEmpty()) {
            int[] cur = starts.poll();

            for (int[] d : dirs) {
                int nr = cur[0] + d[0], nc = cur[1] + d[1];
                if (nr >= 0 && nr < m && nc >= 0 && nc < n &&
                    !reach[nr][nc] && h[nr][nc] >= h[cur[0]][cur[1]]) {
                    reach[nr][nc] = true;
                    starts.offer(new int[]{nr, nc});
                }
            }
        }
        return reach;
    }

    /**
     * @param heights elevation grid
     * @return        cells draining to both oceans
     */
    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        int m = heights.length, n = heights[0].length;

        Queue<int[]> pacificStarts = new LinkedList<>();
        Queue<int[]> atlanticStarts = new LinkedList<>();
        for (int i = 0; i < m; i++) {
            pacificStarts.offer(new int[]{i, 0});
            atlanticStarts.offer(new int[]{i, n - 1});
        }
        for (int j = 0; j < n; j++) {
            pacificStarts.offer(new int[]{0, j});
            atlanticStarts.offer(new int[]{m - 1, j});
        }

        boolean[][] pacific = bfs(heights, pacificStarts);
        boolean[][] atlantic = bfs(heights, atlanticStarts);

        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (pacific[i][j] && atlantic[i][j]) result.add(Arrays.asList(i, j));
        return result;
    }
}
```

```cpp
#include <queue>
#include <vector>

class PacificAtlanticWaterFlow {
    int dirs[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    std::vector<std::vector<bool>> bfs(std::vector<std::vector<int>>& h,
                                       std::queue<std::pair<int, int>>& starts) {
        int m = h.size(), n = h[0].size();
        std::vector<std::vector<bool>> reach(m, std::vector<bool>(n, false));

        while (!starts.empty()) {
            auto [r, c] = starts.front(); starts.pop();
            reach[r][c] = true;

            for (auto& d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < m && nc >= 0 && nc < n &&
                    !reach[nr][nc] && h[nr][nc] >= h[r][c]) {
                    starts.push({nr, nc});
                }
            }
        }
        return reach;
    }

public:
    /**
     * @param heights elevation grid
     * @return        cells draining to both oceans
     */
    std::vector<std::vector<int>> pacificAtlantic(std::vector<std::vector<int>>& heights) {
        int m = heights.size(), n = heights[0].size();

        std::queue<std::pair<int, int>> ps, as;
        for (int i = 0; i < m; i++) { ps.push({i, 0}); as.push({i, n - 1}); }
        for (int j = 0; j < n; j++) { ps.push({0, j}); as.push({m - 1, j}); }

        auto pacific = bfs(heights, ps);
        auto atlantic = bfs(heights, as);

        std::vector<std::vector<int>> result;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (pacific[i][j] && atlantic[i][j]) result.push_back({i, j});
        return result;
    }
};
```

```python
from collections import deque

def pacific_atlantic(heights: list[list[int]]) -> list[list[int]]:
    """
    @param heights: elevation grid
    @return:        cells draining to both oceans
    """
    rows, cols = len(heights), len(heights[0])
    dirs = ((1, 0), (-1, 0), (0, 1), (0, -1))

    def bfs(starts):
        reach = [[False] * cols for _ in range(rows)]
        queue = deque(starts)
        for r, c in starts:
            reach[r][c] = True

        while queue:
            r, c = queue.popleft()
            for dr, dc in dirs:
                nr, nc = r + dr, c + dc
                if (0 <= nr < rows and 0 <= nc < cols and
                        not reach[nr][nc] and heights[nr][nc] >= heights[r][c]):
                    reach[nr][nc] = True
                    queue.append((nr, nc))
        return reach

    pacific = bfs([(r, 0) for r in range(rows)] + [(0, c) for c in range(cols)])
    atlantic = bfs([(r, cols - 1) for r in range(rows)] + [(rows - 1, c) for c in range(cols)])

    return [[r, c] for r in range(rows) for c in range(cols)
            if pacific[r][c] and atlantic[r][c]]
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param heights elevation grid
    /// @return        cells draining to both oceans
    pub fn pacific_atlantic(heights: Vec<Vec<i32>>) -> Vec<Vec<i32>> {
        let (m, n) = (heights.len(), heights[0].len());
        let dirs = [(1, 0), (-1, 0), (0, 1), (0, -1)];

        fn bfs(starts: Vec<(usize, usize)>, h: &Vec<Vec<i32>>,
               m: usize, n: usize) -> Vec<Vec<bool>> {
            let mut reach = vec![vec![false; n]; m];
            let mut queue = VecDeque::new();
            for (r, c) in &starts { reach[*r][*c] = true; queue.push_back((*r, *c)); }

            while let Some((r, c)) = queue.pop_front() {
                for (dr, dc) in dirs {
                    let (nr, nc) = (r as i32 + dr, c as i32 + dc);
                    if nr >= 0 && nr < m as i32 && nc >= 0 && nc < n as i32 {
                        let (ur, uc) = (nr as usize, nc as usize);
                        if !reach[ur][uc] && h[ur][uc] >= h[r][c] {
                            reach[ur][uc] = true;
                            queue.push_back((ur, uc));
                        }
                    }
                }
            }
            reach
        }

        let pacific = bfs((0..m).map(|r| (r, 0)).chain((0..n).map(|c| (0, c))).collect(),
                          &heights, m, n);
        let atlantic = bfs((0..m).map(|r| (r, n - 1)).chain((0..n).map(|c| (m - 1, c))).collect(),
                           &heights, m, n);

        let mut result = Vec::new();
        for r in 0..m {
            for c in 0..n {
                if pacific[r][c] && atlantic[r][c] { result.push(vec![r as i32, c as i32]); }
            }
        }
        result
    }
}
```

## Dry run

**Input:** `heights = [[1,2],[2,1]]`.

```
Pacific starts: (0,0), (0,1), (1,0).   Atlantic starts: (1,1), (0,1), (1,0).

Pacific BFS: (0,0)=1 -> neighbors: (1,0)=2 >= 1 ✓ reachable.  (0,1)=2 >= 1 ✓.
             from (1,0)=2 -> (1,1)=1 >= 2? no.  from (0,1)=2 -> (1,1)=1? no.
             pacific = {(0,0),(0,1),(1,0)}
Atlantic BFS: (1,1)=1 -> (0,1)=2 ✓, (1,0)=2 ✓.
             from (0,1)=2 -> (0,0)=1? no.  from (1,0)=2 -> (0,0)=1? no.
             atlantic = {(1,1),(0,1),(1,0)}

intersection: (0,1) and (1,0) -> [[0,1],[1,0]] ✓
```

The reverse flow at work: cell (0,1) (height 2) drains to *both* oceans — its downhill paths are to the Pacific top edge and the Atlantic right edge. The BFS finds it from both shores because climbing from either shore reaches it (`2 >= 2` uphill allowed). Cells (0,0) and (1,1) are valleys reachable from only one side — their `>=` checks fail the other ocean's climb.

## Complexity

**Time.** Two floods:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** Two boolean grids:

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Surrounded Regions** ([6.16](surrounded-regions.md)) — the same border-seed reverse thinking with a different slope rule (none).
- **Max Area Of Island** ([6.17](max-area-of-island.md)) — the sink-based sibling.
- **Interview follow-up:** "Why is reverse flow the right frame?" The forward question ("can this cell reach an ocean?") has m·n starting points. The reverse question ("which cells can an ocean's shore reach uphill?") has O(m+n) starts and one flood — the [6.16](surrounded-regions.md) "start from the answer's boundary" pattern at its most valuable.
