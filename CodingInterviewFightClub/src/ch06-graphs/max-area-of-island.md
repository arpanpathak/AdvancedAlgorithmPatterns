# 6.17 Max Area Of Island

> **Source:** [`src/main/kotlin/grid/MaxAreaOfIsland.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/MaxAreaOfIsland.kt)
> **Pattern:** sink-and-count DFS · **Core page**

## The Problem

The **maximum area** of an island in a 0/1 grid (4-connected 1s).

- Constraints: m, n ≤ 50.

## Examples

```
Input:  grid = [[0,0,1,0,0,0,0,1,0,0,0,0,0],
                [0,0,0,0,0,0,0,1,1,1,0,0,0],
                [0,1,1,0,1,0,0,0,0,0,0,0,0], ...]
Output: 6   (the big island at the top-right)
```

## Intuition — DFS each 1, sink it, count the size

Every land cell starts a DFS that floods its whole island, *sinking* visited cells (set to 0) so no island is counted twice — the [6.16](surrounded-regions.md) `#`-marking idea, here as permanent removal:

```kotlin
fun dfs(r: Int, c: Int): Int {
    if (r < 0 || c < 0 || r >= rows || c >= cols || grid[r][c] == 0) return 0

    grid[r][c] = 0                                // sink: visited
    return 1 + dfs(r+1, c) + dfs(r-1, c) + dfs(r, c+1) + dfs(r, c-1)
}

var maxArea = 0
for (r in 0 until rows)
    for (c in 0 until cols)
        if (grid[r][c] == 1) maxArea = maxOf(maxArea, dfs(r, c))
```

**Why sink instead of a visited set?** Mutating `grid[r][c] = 0` is the [6.x](../ch06-graphs/pattern-primer.md) visited-set in place — each island's cells are consumed exactly once. No extra memory, no double-count.

**Why `1 + four neighbors`?** The area is the count of land cells in the component; each cell contributes itself plus its four-connected neighbors' areas. The bounds check doubles as the base case (`return 0` on water/boundary).

## Approach 1 — BFS per island (queue, count pops)

Same sink logic with a queue: correct, identical complexity.

## Approach 2 — Sink-and-count DFS (the repo's version, optimal)

```kotlin
class MaxAreaOfIsland {
    /**
     * @param grid 0/1 grid
     * @return     max island area
     */
    fun maxAreaOfIsland(grid: Array<IntArray>): Int {
        if (grid.isEmpty()) return 0

        val (rows, cols) = grid.size to grid[0].size
        var maxArea = 0

        val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        fun dfs(r: Int, c: Int): Int {
            if (r < 0 || c < 0 || r >= rows || c >= cols || grid[r][c] == 0) return 0

            grid[r][c] = 0                    // sink: visited
            return 1 + directions.sumOf { (dr, dc) -> dfs(r + dr, c + dc) }
        }

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (grid[r][c] == 1) {
                    maxArea = maxOf(maxArea, dfs(r, c))
                }
            }
        }
        return maxArea
    }
}
```

```java
public class MaxAreaOfIsland {
    private int rows, cols;

    private int dfs(int[][] grid, int r, int c) {
        if (r < 0 || c < 0 || r >= rows || c >= cols || grid[r][c] == 0) return 0;

        grid[r][c] = 0;                       // sink: visited
        return 1 + dfs(grid, r + 1, c) + dfs(grid, r - 1, c)
                 + dfs(grid, r, c + 1) + dfs(grid, r, c - 1);
    }

    /**
     * @param grid 0/1 grid
     * @return     max island area
     */
    public int maxAreaOfIsland(int[][] grid) {
        rows = grid.length;
        cols = grid[0].length;
        int best = 0;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == 1) best = Math.max(best, dfs(grid, r, c));

        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MaxAreaOfIsland {
    int rows, cols;

    int dfs(std::vector<std::vector<int>>& grid, int r, int c) {
        if (r < 0 || c < 0 || r >= rows || c >= cols || grid[r][c] == 0) return 0;

        grid[r][c] = 0;                       // sink: visited
        return 1 + dfs(grid, r + 1, c) + dfs(grid, r - 1, c)
                 + dfs(grid, r, c + 1) + dfs(grid, r, c - 1);
    }

public:
    /**
     * @param grid 0/1 grid
     * @return     max island area
     */
    int maxAreaOfIsland(std::vector<std::vector<int>>& grid) {
        rows = grid.size();
        cols = grid[0].size();
        int best = 0;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == 1) best = std::max(best, dfs(grid, r, c));

        return best;
    }
};
```

```python
def max_area_of_island(grid: list[list[int]]) -> int:
    """
    @param grid: 0/1 grid
    @return:      max island area
    """
    rows, cols = len(grid), len(grid[0])
    best = 0

    def dfs(r: int, c: int) -> int:
        if r < 0 or c < 0 or r >= rows or c >= cols or grid[r][c] == 0:
            return 0

        grid[r][c] = 0                    # sink: visited
        return 1 + dfs(r + 1, c) + dfs(r - 1, c) + dfs(r, c + 1) + dfs(r, c - 1)

    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == 1:
                best = max(best, dfs(r, c))

    return best
```

```rust
impl Solution {
    /// @param grid 0/1 grid
    /// @return     max island area
    pub fn max_area_of_island(mut grid: Vec<Vec<i32>>) -> i32 {
        let (rows, cols) = (grid.len(), grid[0].len());
        let mut best = 0;

        fn dfs(grid: &mut Vec<Vec<i32>>, r: i32, c: i32, rows: i32, cols: i32) -> i32 {
            if r < 0 || c < 0 || r >= rows || c >= cols || grid[r as usize][c as usize] == 0 {
                return 0;
            }
            grid[r as usize][c as usize] = 0;        // sink: visited
            1 + dfs(grid, r + 1, c, rows, cols) + dfs(grid, r - 1, c, rows, cols)
              + dfs(grid, r, c + 1, rows, cols) + dfs(grid, r, c - 1, rows, cols)
        }

        for r in 0..rows {
            for c in 0..cols {
                if grid[r][c] == 1 {
                    best = best.max(dfs(&mut grid, r as i32, c as i32, rows as i32, cols as i32));
                }
            }
        }
        best
    }
}
```

## Dry run

**Input:** `grid = [[0,0,1,0,0],[0,1,1,0,0],[0,0,1,0,0]]`.

```
r=0,c=2 (1): dfs(0,2):
  sink (0,2).  area 1
  dfs(1,2): sink (1,2).  1 + dfs(2,2): sink.  1 + 0s = 3
  dfs(1,1): sink (1,1).  1 + 0 = 1
  total: 1 + 3 + 1 = 5? — recount: dfs(0,2) = 1 + dfs(1,2)=3 + ... let me recount:

dfs(0,2): sink (0,2).  return 1 + dfs(1,2) + 0 + 0 + 0
dfs(1,2): sink (1,2).  return 1 + dfs(2,2) + dfs(1,1) + 0 + 0
dfs(2,2): sink (2,2).  return 1
dfs(1,1): sink (1,1).  return 1
=> dfs(1,2) = 1 + 1 + 1 = 3.  dfs(0,2) = 1 + 3 = 4.  best = 4 ✓
```

The sink makes each cell count exactly once: the cross-shaped island (4 cells) yields 4. The `grid[r][c] = 0` mutation is what prevents re-entry from another branch — without it, the same cell would be counted in every neighbor's area. Water cells and boundaries short-circuit to 0, ending the flood.

## Complexity

**Time.** Each cell visited at most once:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** Recursion depth:

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Number Of Islands** (`grid/NumberOfIslands.kt`) — count instead of max-area; identical machinery.
- **Making A Large Island** (`grid/MakingALargeIsland.kt`) — the "flip one 0" upgrade: component IDs + neighbor sums.
- **Surrounded Regions** ([6.16](surrounded-regions.md)) — the border-seed sibling.
- **Interview follow-up:** "Why mutate instead of a visited set?" The grid is the visit log — `0` = water or visited, `1` = unexplored land. Mutation is O(1) per cell with zero allocation; the cost is destroying the input, which the problem permits. Name the tradeoff and the interviewer knows you've internalized it.
