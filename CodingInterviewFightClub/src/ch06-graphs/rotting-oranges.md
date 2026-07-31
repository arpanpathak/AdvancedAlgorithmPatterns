# 6.14 Rotting Oranges

> **Source:** [`src/main/kotlin/grid/RottingOranges.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/RottingOranges.kt)
> **Pattern:** multi-source BFS with minute-fencing · **Core page**

## The Problem

Given a grid (`0` empty, `1` fresh, `2` rotten), each minute every fresh orange adjacent (4-directional) to a rotten one rots. Return the **minutes until no fresh orange remains**, or `-1`.

- Constraints: $1 \le m, n \le 10$; values 0/1/2.

## Examples

```
Input:  grid = [[2,1,1],[1,1,0],[0,1,1]]   -> Output: 4
Input:  grid = [[2,1,1],[0,1,1],[1,0,1]]   -> Output: -1   (the corner orange is unreachable)
```

## Intuition — seed ALL rotten oranges, BFS one "minute" per level

Single-source BFS from [6.10](flood-fill.md) generalizes: every rotten orange is a source, and one BFS *level* = one minute. The fencing trick from [5.2](../ch05-trees/binary-tree-level-order-traversal.md) — `repeat(queue.size)` per level — is the minute counter:

```
queue = all cells == 2; freshCount = count of 1s
minutes = 0
while queue not empty and freshCount > 0:
    repeat(queue.size):            # this level = one minute
        (r, c) = poll; for each neighbor:
            if grid[neighbor] == 1: rot it; freshCount--; enqueue
    minutes++
return freshCount == 0 ? minutes : -1
```

**Why seed all sources?** Rotting spreads from *every* rotten orange simultaneously — multi-source BFS. The single `queue` with all initial sources keeps each level's spread in sync, so `minutes` counts real elapsed time, not per-source hops.

**Why count fresh oranges?** `-1` detection: if a fresh orange is unreachable (separated by walls), the BFS ends with `freshCount > 0`. Tracking the count during rotting avoids a final grid scan and doubles as the loop's early exit (`freshCount == 0` → no need to spread further).

**Why `repeat(queue.size)` before polling?** The queue holds this minute's frontier *plus* next minute's additions; the snapshot ensures all current-minute rots spread before counting a new minute. Same fence as [5.2](../ch05-trees/binary-tree-level-order-traversal.md) and [6.1](word-ladder.md)'s level counting.

## Approach 1 — Time-DP per cell (O(mn) per minute)

Repeatedly scan and rot neighbors until stable: correct, $O(\text{minutes} \cdot mn)$.

## Approach 2 — Multi-source BFS (the repo's version, optimal)

```kotlin
class RottingOranges {
    /**
     * @param grid 0=empty, 1=fresh, 2=rotten
     * @return     minutes until no fresh orange, or -1
     */
    fun orangesRotting(grid: Array<IntArray>): Int {
        val (rows, cols) = grid.size to grid[0].size
        val directions = arrayOf(Pair(0, 1), Pair(1, 0), Pair(0, -1), Pair(-1, 0))
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        var remainingFreshCount = 0

        // Seed all rotten oranges and count the fresh ones
        grid.forEachIndexed { r, row ->
            row.forEachIndexed { c, value ->
                when (value) {
                    2 -> queue.add(Pair(r, c))
                    1 -> remainingFreshCount++
                }
            }
        }

        if (remainingFreshCount == 0) return 0     // nothing to rot

        var minutes = 0

        while (queue.isNotEmpty()) {
            repeat(queue.size) {                    // one minute = one full level
                val (r, c) = queue.poll()
                directions.forEach { (dr, dc) ->
                    val (nr, nc) = r + dr to c + dc
                    if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2            // rot it
                        remainingFreshCount--
                        queue.add(Pair(nr, nc))
                    }
                }
            }
            minutes++
        }

        return if (remainingFreshCount == 0) minutes - 1 else -1
    }
}
```

```java
import java.util.*;

public class RottingOranges {
    /**
     * @param grid 0=empty, 1=fresh, 2=rotten
     * @return     minutes until no fresh orange, or -1
     */
    public int orangesRotting(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};
        Queue<int[]> queue = new LinkedList<>();
        int fresh = 0;

        for (int r = 0; r < rows; r++)               // seed sources, count fresh
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) queue.offer(new int[]{r, c});
                else if (grid[r][c] == 1) fresh++;
            }

        if (fresh == 0) return 0;

        int minutes = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();                 // one minute = one full level
            for (int k = 0; k < size; k++) {
                int[] cell = queue.poll();
                for (int[] d : dirs) {
                    int nr = cell[0] + d[0], nc = cell[1] + d[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;            // rot it
                        fresh--;
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }
            minutes++;
        }
        return fresh == 0 ? minutes - 1 : -1;
    }
}
```

```cpp
#include <queue>
#include <vector>

class RottingOranges {
public:
    /**
     * @param grid 0=empty, 1=fresh, 2=rotten
     * @return     minutes until no fresh orange, or -1
     */
    int orangesRotting(std::vector<std::vector<int>>& grid) {
        int rows = grid.size(), cols = grid[0].size();
        std::vector<std::pair<int,int>> dirs = {{0,1},{1,0},{0,-1},{-1,0}};
        std::queue<std::pair<int,int>> queue;
        int fresh = 0;

        for (int r = 0; r < rows; r++)               // seed sources, count fresh
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) queue.push({r, c});
                else if (grid[r][c] == 1) fresh++;
            }

        if (fresh == 0) return 0;

        int minutes = 0;
        while (!queue.empty()) {
            int size = queue.size();                 // one minute = one full level
            for (int k = 0; k < size; k++) {
                auto [r, c] = queue.front(); queue.pop();
                for (auto& [dr, dc] : dirs) {
                    int nr = r + dr, nc = c + dc;
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;            // rot it
                        fresh--;
                        queue.push({nr, nc});
                    }
                }
            }
            minutes++;
        }
        return fresh == 0 ? minutes - 1 : -1;
    }
};
```

```python
from collections import deque

def oranges_rotting(grid: list[list[int]]) -> int:
    """
    @param grid: 0=empty, 1=fresh, 2=rotten
    @return:      minutes until no fresh orange, or -1
    """
    rows, cols = len(grid), len(grid[0])
    queue = deque()
    fresh = 0

    for r in range(rows):                       # seed sources, count fresh
        for c in range(cols):
            if grid[r][c] == 2:
                queue.append((r, c))
            elif grid[r][c] == 1:
                fresh += 1

    if fresh == 0:
        return 0

    minutes = 0
    while queue:
        for _ in range(len(queue)):             # one minute = one full level
            r, c = queue.popleft()
            for dr, dc in ((0, 1), (1, 0), (0, -1), (-1, 0)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols and grid[nr][nc] == 1:
                    grid[nr][nc] = 2            # rot it
                    fresh -= 1
                    queue.append((nr, nc))
        minutes += 1

    return minutes - 1 if fresh == 0 else -1
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param grid 0=empty, 1=fresh, 2=rotten
    /// @return     minutes until no fresh orange, or -1
    pub fn oranges_rotting(mut grid: Vec<Vec<i32>>) -> i32 {
        let (rows, cols) = (grid.len(), grid[0].len());
        let mut queue: VecDeque<(usize, usize)> = VecDeque::new();
        let mut fresh = 0;

        for r in 0..rows {                          // seed sources, count fresh
            for c in 0..cols {
                match grid[r][c] {
                    2 => queue.push_back((r, c)),
                    1 => fresh += 1,
                    _ => {}
                }
            }
        }

        if fresh == 0 { return 0; }

        let dirs = [(0i32, 1i32), (1, 0), (0, -1), (-1, 0)];
        let mut minutes = 0;
        while !queue.is_empty() {
            let size = queue.len();                 // one minute = one full level
            for _ in 0..size {
                let (r, c) = queue.pop_front().unwrap();
                for (dr, dc) in dirs {
                    let (nr, nc) = (r as i32 + dr, c as i32 + dc);
                    if nr >= 0 && nr < rows as i32 && nc >= 0 && nc < cols as i32
                       && grid[nr as usize][nc as usize] == 1 {
                        grid[nr as usize][nc as usize] = 2;   // rot it
                        fresh -= 1;
                        queue.push_back((nr as usize, nc as usize));
                    }
                }
            }
            minutes += 1;
        }
        if fresh == 0 { minutes - 1 } else { -1 }
    }
}
```

## Dry run

**Input:** `grid = [[2,1,1],[1,1,0],[0,1,1]]` — 6 fresh oranges.

```
seed: queue = [(0,0)], fresh = 6.

minute 1: process (0,0)      -> rots (0,1), (1,0).   fresh 4.  queue = [(0,1),(1,0)]
minute 2: process both       -> (0,1) rots (0,2); (1,0) rots (1,1).  fresh 2.  queue = [(0,2),(1,1)]
minute 3: process both       -> (0,2) none; (1,1) rots (2,1).        fresh 1.  queue = [(2,1)]
minute 4: process (2,1)      -> rots (2,2).          fresh 0.  queue = [(2,2)]
minute 5: process (2,2)      -> no fresh neighbors.  queue drains.
minutes = 5 -> return 5 - 1 = 4 ✓
```

The `minutes - 1` is the off-by-one truth: the loop's final pass processes a frontier with *nothing left to rot* (minute 5), so the real elapsed time is one less than the level count. The `fresh == 0` guard is what distinguishes "done" (4) from "stuck" — the `-1` case: a wall-separated fresh orange leaves `fresh > 0` when the queue drains.

## Complexity

**Time.** Each cell visited once:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** The queue:

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Flood Fill** ([6.10](flood-fill.md)) — single-source sibling; the `repeat(queue.size)` level fence is the only addition here.
- **Walls And Gates** ([17.11](../ch17-advanced-graphs/walls-and-gates.md)) — the same multi-source BFS writing *distances* instead of minutes.
- **Shortest Path In Grid With Obstacles Elimination** (`grid/a_star/`) — BFS with state (row, col, remaining-eliminations).
- **Interview follow-up:** "Why seed every rotten orange at once?" Rotting spreads in parallel from all sources; a per-source BFS would overcount minutes (a cell reached by source A at minute 2 might be re-reached by source B at minute 1). One shared queue with level-fencing keeps all frontiers synchronized on the same clock.
