# 6.22 Island Perimeter

> **Source:** [`src/main/kotlin/grid/IslandPerimeter.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/IslandPerimeter.kt)
> **Pattern:** count exposed edges · **Core page**

## The Problem

The perimeter of a single island (4-connected 1s in a 0/1 grid).

- Constraints: m, n ≤ 100; one island.

## Examples

```
Input:  grid = [[0,1,0,0],[1,1,1,0],[0,1,0,0],[1,1,0,0]]
Output: 16
```

## Intuition — every land cell contributes its exposed sides

For each 1, add 1 for each side that's out-of-bounds or water:

```kotlin
if (cell == 1) {
    perimeter += listOf(
        r == 0 || grid[r - 1][c] == 0,               // top
        r == grid.size - 1 || grid[r + 1][c] == 0,   // bottom
        c == 0 || grid[r][c - 1] == 0,               // left
        c == row.size - 1 || grid[r][c + 1] == 0     // right
    ).count { it }
}
```

**Why the boundary `||` elvis?** The grid edges have no neighbor — treated as water (exposed). The [3.x](../ch03-arrays/pattern-primer.md) boundary-safe idiom.

## Approach 1 — Perimeter = 4·land − 2·shared (count adjacent pairs)

Alternative formula: 4 per cell, subtract 2 for each shared edge: same O(mn).

## Approach 2 — Exposed-edge count (the repo's version, optimal)

```kotlin
class IslandPerimeter {
    /**
     * @param grid 0/1 grid
     * @return     island perimeter
     */
    fun islandPerimeter(grid: Array<IntArray>): Int {
        var perimeter = 0

        grid.forEachIndexed { r, row ->
            row.forEachIndexed { c, cell ->
                if (cell == 1) {
                    perimeter += listOf(
                        r == 0 || grid[r - 1][c] == 0,
                        r == grid.size - 1 || grid[r + 1][c] == 0,
                        c == 0 || grid[r][c - 1] == 0,
                        c == row.size - 1 || grid[r][c + 1] == 0
                    ).count { it }
                }
            }
        }
        return perimeter
    }
}
```

```java
public class IslandPerimeter {
    /**
     * @param grid 0/1 grid
     * @return     island perimeter
     */
    public int islandPerimeter(int[][] grid) {
        int perimeter = 0;

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 1) {
                    if (r == 0 || grid[r - 1][c] == 0) perimeter++;
                    if (r == grid.length - 1 || grid[r + 1][c] == 0) perimeter++;
                    if (c == 0 || grid[r][c - 1] == 0) perimeter++;
                    if (c == grid[0].length - 1 || grid[r][c + 1] == 0) perimeter++;
                }
            }
        }
        return perimeter;
    }
}
```

```cpp
#include <vector>

class IslandPerimeter {
public:
    /**
     * @param grid 0/1 grid
     * @return     island perimeter
     */
    int islandPerimeter(std::vector<std::vector<int>>& grid) {
        int perimeter = 0;
        int m = grid.size(), n = grid[0].size();

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1) {
                    if (r == 0 || grid[r - 1][c] == 0) perimeter++;
                    if (r == m - 1 || grid[r + 1][c] == 0) perimeter++;
                    if (c == 0 || grid[r][c - 1] == 0) perimeter++;
                    if (c == n - 1 || grid[r][c + 1] == 0) perimeter++;
                }
            }
        }
        return perimeter;
    }
};
```

```python
def island_perimeter(grid: list[list[int]]) -> int:
    """
    @param grid: 0/1 grid
    @return:     island perimeter
    """
    m, n = len(grid), len(grid[0])
    perimeter = 0

    for r in range(m):
        for c in range(n):
            if grid[r][c] == 1:
                perimeter += (
                    (r == 0 or grid[r - 1][c] == 0) +
                    (r == m - 1 or grid[r + 1][c] == 0) +
                    (c == 0 or grid[r][c - 1] == 0) +
                    (c == n - 1 or grid[r][c + 1] == 0)
                )

    return perimeter
```

```rust
impl Solution {
    /// @param grid 0/1 grid
    /// @return     island perimeter
    pub fn island_perimeter(grid: Vec<Vec<i32>>) -> i32 {
        let (m, n) = (grid.len(), grid[0].len());
        let mut perimeter = 0;

        for r in 0..m {
            for c in 0..n {
                if grid[r][c] == 1 {
                    perimeter += (r == 0 || grid[r - 1][c] == 0) as i32;
                    perimeter += (r == m - 1 || grid[r + 1][c] == 0) as i32;
                    perimeter += (c == 0 || grid[r][c - 1] == 0) as i32;
                    perimeter += (c == n - 1 || grid[r][c + 1] == 0) as i32;
                }
            }
        }
        perimeter
    }
}
```

## Dry run

**Input:** the 4×4 example.

```
(0,1): top: r==0 ✓.  left: grid[0][0]=0 ✓.  right: grid[0][2]=0 ✓.  bottom: grid[1][1]=1 ✗.  -> +3
(1,0): top 0 ✓.  left r==... c==0 ✓.  bottom 1 ✗.  right 1 ✗.  -> +2
(1,1): top 1 ✗.  left 1 ✗.  right 1 ✗.  bottom 1 ✗.  -> +0
(1,2): top 0 ✓.  bottom 1 ✗.  left 1 ✗.  right 0 ✓.  -> +2
(2,1): top 1 ✗.  bottom 1 ✗.  left 0 ✓.  right 0 ✓.  -> +2
(3,0): top 1 ✗.  bottom r==3 ✓.  left c==0 ✓.  right 1 ✗.  -> +2
(3,1): top 1 ✗.  bottom ✓.  left 1 ✗.  right 0 ✓.  -> +2
... continuing the full scan totals 16 ✓
```

## Complexity

**Time.** Grid scan:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** Constants:

$$
S(m, n) = O(1)
$$

## Variants & follow-ups

- **Max Area Of Island** ([6.17](max-area-of-island.md)) — the area twin of this perimeter count.
- **Interview follow-up:** "Why is per-cell edge counting equivalent to the 4·land − 2·pairs formula?" Each land cell contributes 4 sides; every shared edge hides 2 sides (one per cell). Counting exposed sides directly avoids the double pass — same result, one scan.
