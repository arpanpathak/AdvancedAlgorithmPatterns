# 7.4 Trapping Rain Water II

> **Source:** [`src/main/kotlin/heap/TrappingRainWater_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/heap/TrappingRainWater_II.kt)
> **Pattern:** min-heap boundary expansion · **Core page**

## The Problem

Given an `m x n` matrix of heights, return the volume of water it can trap after raining — water collects in any cell whose surrounding walls are tall enough to hold it, and can flow out only over the *lowest* wall on its boundary path.

- Constraints: $1 \le m, n \le 200$; $0 \le height[i][j] \le 2 \times 10^4$.

## Examples

```
Input:  heightMap = [[1,4,3,1,3,2],
                    [3,2,1,3,2,4],
                    [2,3,3,2,3,1]]
Output: 4   (one unit at (1,1), (1,2), (1,4) and (2,3) — the interior low spots)

Input:  heightMap = [[3,3,3,3,3],
                    [3,2,2,2,3],
                    [3,2,1,2,3],
                    [3,2,2,2,3],
                    [3,3,3,3,3]]
Output: 10   (the bowl: every interior cell holds 3 - height)
```

## Intuition — water escapes over the *lowest* wall

The 1-D version (a line of walls) has a classic two-pointer solution. In 2-D, water at any interior cell can escape along *any* path to the boundary — and it always escapes over the **lowest wall it can reach**. So the water a cell can hold is decided by the *minimum, over all escape paths, of the maximum wall height on that path* — which sounds awful, until you flip the viewpoint:

**Think of the boundary as a wall that grows.** Start with every border cell inside a **min-heap** (by height) and mark it visited. Pop the *lowest* boundary cell — call its height `h`. Water anywhere next to it, unseen, can be held up to exactly `h`: if the neighbor is lower than `h`, the difference is trapped water; if it's higher, it becomes a new (taller) wall. Either way, the neighbor joins the boundary at height `max(h, neighborHeight)` — and the boundary just absorbed one more cell.

This is the [priority processing](pattern-primer.md) move: the heap always hands us the **weakest point** of the current boundary, because that's the point that decides whether water can escape. Processing in increasing height is what makes "the lowest wall" the only thing that matters at each step.

**Why BFS + heap and not flood fill?** Flood fill from each cell is $O((mn)^2)$. The heap walk visits every cell exactly once, in boundary-height order — $O(mn \log(mn))$. The heap *is* the flood: it keeps water from breaking out too early.

## Approach 1 — Per-cell flood fill (too slow)

For every interior cell, find the bottleneck wall on its lowest escape path: $O((mn)^2)$. Correct in principle, hopeless at $200 \times 200$.

## Approach 2 — Min-heap boundary expansion (the repo's version, optimal)

```kotlin
import java.util.*

private data class Cell(val height: Int, val r: Int, val c: Int)

fun trapRainWater(heightMap: Array<IntArray>): Int {
    if (heightMap.isEmpty() || heightMap[0].isEmpty()) return 0

    val rows = heightMap.size
    val cols = heightMap[0].size
    val visited = Array(rows) { BooleanArray(cols) }
    val pq = PriorityQueue<Cell>(compareBy { it.height })     // boundary, weakest wall on top

    // Seed the boundary: all border cells
    for (r in 0 until rows) {
        pq.offer(Cell(heightMap[r][0], r, 0))
        pq.offer(Cell(heightMap[r][cols - 1], r, cols - 1))
        visited[r][0] = true
        visited[r][cols - 1] = true
    }
    for (c in 1 until cols - 1) {
        pq.offer(Cell(heightMap[0][c], 0, c))
        pq.offer(Cell(heightMap[rows - 1][c], rows - 1, c))
        visited[0][c] = true
        visited[rows - 1][c] = true
    }

    var water = 0
    val d = intArrayOf(0, 1, 0, -1, 0)                          // 4-directional deltas

    while (pq.isNotEmpty()) {
        val (h, r, c) = pq.poll()                               // the weakest wall now

        for (i in 0 until 4) {
            val nr = r + d[i]
            val nc = c + d[i + 1]

            if (nr in 0 until rows && nc in 0 until cols && !visited[nr][nc]) {
                visited[nr][nc] = true
                water += maxOf(0, h - heightMap[nr][nc])        // trapped up to the wall
                pq.offer(Cell(maxOf(h, heightMap[nr][nc]), nr, nc))   // boundary grows
            }
        }
    }
    return water
}
```

```java
import java.util.*;

public class TrappingRainWaterII {
    private record Cell(int height, int r, int c) {}

    /**
     * @param heightMap m x n matrix of ground heights
     * @return          total water volume trapped
     */
    public int trapRainWater(int[][] heightMap) {
        if (heightMap.length == 0 || heightMap[0].length == 0) return 0;

        int rows = heightMap.length, cols = heightMap[0].length;
        boolean[][] visited = new boolean[rows][cols];
        PriorityQueue<Cell> pq = new PriorityQueue<>(Comparator.comparingInt(Cell::height));

        for (int r = 0; r < rows; r++) {                 // seed the boundary
            pq.offer(new Cell(heightMap[r][0], r, 0));
            pq.offer(new Cell(heightMap[r][cols - 1], r, cols - 1));
            visited[r][0] = visited[r][cols - 1] = true;
        }
        for (int c = 1; c < cols - 1; c++) {
            pq.offer(new Cell(heightMap[0][c], 0, c));
            pq.offer(new Cell(heightMap[rows - 1][c], rows - 1, c));
            visited[0][c] = visited[rows - 1][c] = true;
        }

        int water = 0;
        int[] d = {0, 1, 0, -1, 0};
        while (!pq.isEmpty()) {
            Cell cell = pq.poll();                       // the weakest wall now
            for (int i = 0; i < 4; i++) {
                int nr = cell.r() + d[i], nc = cell.c() + d[i + 1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    water += Math.max(0, cell.height() - heightMap[nr][nc]);
                    pq.offer(new Cell(Math.max(cell.height(), heightMap[nr][nc]), nr, nc));
                }
            }
        }
        return water;
    }
}
```

```cpp
#include <functional>
#include <queue>
#include <vector>

class TrappingRainWaterII {
    struct Cell {
        int height, r, c;
        bool operator>(const Cell& o) const { return height > o.height; }
    };

public:
    /**
     * @param heightMap m x n matrix of ground heights
     * @return          total water volume trapped
     */
    int trapRainWater(std::vector<std::vector<int>>& heightMap) {
        int rows = heightMap.size(), cols = heightMap[0].size();
        std::vector<std::vector<bool>> visited(rows, std::vector<bool>(cols, false));
        std::priority_queue<Cell, std::vector<Cell>, std::greater<Cell>> pq;

        for (int r = 0; r < rows; r++) {                 // seed the boundary
            pq.push({heightMap[r][0], r, 0});
            pq.push({heightMap[r][cols - 1], r, cols - 1});
            visited[r][0] = visited[r][cols - 1] = true;
        }
        for (int c = 1; c < cols - 1; c++) {
            pq.push({heightMap[0][c], 0, c});
            pq.push({heightMap[rows - 1][c], rows - 1, c});
            visited[0][c] = visited[rows - 1][c] = true;
        }

        int water = 0;
        int dr[4] = {0, 1, 0, -1}, dc[4] = {1, 0, -1, 0};
        while (!pq.empty()) {
            auto [h, r, c] = pq.top();                   // the weakest wall now
            pq.pop();

            for (int i = 0; i < 4; i++) {
                int nr = r + dr[i], nc = c + dc[i];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    water += std::max(0, h - heightMap[nr][nc]);
                    pq.push({std::max(h, heightMap[nr][nc]), nr, nc});
                }
            }
        }
        return water;
    }
};
```

```python
import heapq

def trap_rain_water(height_map: list[list[int]]) -> int:
    """
    @param height_map: m x n matrix of ground heights
    @return:           total water volume trapped
    """
    if not height_map or not height_map[0]:
        return 0

    rows, cols = len(height_map), len(height_map[0])
    visited = [[False] * cols for _ in range(rows)]
    pq = []                                        # (height, r, c) min-heap

    for r in range(rows):                          # seed the boundary
        for c in (0, cols - 1):
            heapq.heappush(pq, (height_map[r][c], r, c))
            visited[r][c] = True
    for c in range(1, cols - 1):
        for r in (0, rows - 1):
            heapq.heappush(pq, (height_map[r][c], r, c))
            visited[r][c] = True

    water = 0
    while pq:
        h, r, c = heapq.heappop(pq)                # the weakest wall now
        for nr, nc in ((r + 1, c), (r - 1, c), (r, c + 1), (r, c - 1)):
            if 0 <= nr < rows and 0 <= nc < cols and not visited[nr][nc]:
                visited[nr][nc] = True
                water += max(0, h - height_map[nr][nc])
                heapq.heappush(pq, (max(h, height_map[nr][nc]), nr, nc))
    return water
```

```rust
use std::cmp::Reverse;
use std::collections::BinaryHeap;

impl Solution {
    /// @param height_map m x n matrix of ground heights
    /// @return          total water volume trapped
    pub fn trap_rain_water(height_map: Vec<Vec<i32>>) -> i32 {
        let (rows, cols) = (height_map.len(), height_map[0].len());
        let mut visited = vec![vec![false; cols]; rows];
        // BinaryHeap is a max-heap; Reverse makes it a min-heap on (height, r, c)
        let mut pq: BinaryHeap<Reverse<(i32, usize, usize)>> = BinaryHeap::new();

        for r in 0..rows {                           // seed the boundary
            for &c in &[0, cols - 1] {
                pq.push(Reverse((height_map[r][c], r, c)));
                visited[r][c] = true;
            }
        }
        for c in 1..cols - 1 {
            for &r in &[0, rows - 1] {
                pq.push(Reverse((height_map[r][c], r, c)));
                visited[r][c] = true;
            }
        }

        let mut water = 0;
        while let Some(Reverse((h, r, c))) = pq.pop() {   // the weakest wall now
            for (nr, nc) in [(r + 1, c), (r.wrapping_sub(1), c), (r, c + 1), (r, c.wrapping_sub(1))] {
                if nr < rows && nc < cols && !visited[nr][nc] {
                    visited[nr][nc] = true;
                    water += (h - height_map[nr][nc]).max(0);
                    pq.push(Reverse((h.max(height_map[nr][nc]), nr, nc)));
                }
            }
        }
        water
    }
}
```

> **Rust note:** `wrapping_sub` avoids an underflow panic on `r = 0` / `c = 0`; the subsequent `nr < rows && nc < cols` bounds check rejects the wrapped value, so the wrap is never observed.

## Dry run

**Input:** the bowl `[[3,3,3,3,3],[3,2,2,2,3],[3,2,1,2,3],[3,2,2,2,3],[3,3,3,3,3]]` — all 9 interior cells are height <= 2, rim is 3, so each interior cell traps `3 - height`.

```
Seed: 16 border cells (all height 3) in pq.
Border pops (in any order among ties) discover the interior:

pop (3, 1,0) -> neighbor (1,1)=2: water += 1, push (3,1,1).          total=1
pop (3, 3,0) -> neighbor (3,1)=2: water += 1, push (3,3,1).          total=2
pop (3, 1,4) -> neighbor (1,3)=2: water += 1, push (3,1,3).          total=3
pop (3, 3,4) -> neighbor (3,3)=2: water += 1, push (3,3,3).          total=4
pop (3, 0,2) -> neighbor (1,2)=2: water += 1, push (3,1,2).          total=5
pop (3, 2,0) -> neighbor (2,1)=2: water += 1, push (3,2,1).          total=6
pop (3, 2,4) -> neighbor (2,3)=2: water += 1, push (3,2,3).          total=7
pop (3, 4,2) -> neighbor (3,2)=2: water += 1, push (3,3,2).          total=8
(remaining border pops find only visited neighbors -> nothing)

Interior walls (height 3) pop now:
pop (3,1,1): all 4 neighbors visited -> nothing
pop (3,1,2): neighbor (2,2)=1 unseen: water += 3-1 = 2, push (3,2,2).  total=10
pop (3,1,3), (3,2,1), (3,2,3), (3,3,1), (3,3,2), (3,3,3): all neighbors visited
pop (3,2,2): all neighbors visited -> nothing

Total = 10 ✓
```

The center cell `(2,2)` is discovered *last* (it is reachable only through other interior cells), and it contributes `2` — the deepest part of the bowl holds the most water. Every interior cell was discovered exactly once, each from a height-3 wall, so each added `3 - height`.

## Complexity

**Time.** Every cell enters and leaves the heap exactly once:

$$
T(m, n) = O(mn \log(mn))
$$

**Space.** The heap holds at most the whole frontier; the visited grid is $O(mn)$:

$$
S(m, n) = O(mn)
$$

## Variants & follow-ups

- **Trapping Rain Water (1-D)** — the two-pointer original; this page is its "the boundary is a line, not a ring" special case.
- **Minimum Time To Collect All Apples / island-style BFS** — the same "frontier grows from a seed ring" shape with a plain queue instead of a heap (unweighted).
- **Dijkstra-flavored expansions generally** — any "expand from a boundary, always through the cheapest frontier cell" problem is this loop. The heap choice is what converts "cheapest frontier" from $O(n)$ scans into $O(\log n)$.
- **Interview follow-up:** "Why must the boundary start as the *whole* border?" Water can escape over any border cell, so every border cell is a potential outlet. Seeding only one corner would make that corner the only outlet — the heap would then over-fill everything else. The full-border seed is what makes the "weakest wall" claim true for *every* escape path.
