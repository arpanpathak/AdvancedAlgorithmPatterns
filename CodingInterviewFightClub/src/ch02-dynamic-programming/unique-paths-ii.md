# 2.32 Unique Paths II

> **Source**: [`src/main/kotlin/grid/dynamic_programming/UniquePaths_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/dynamic_programming/UniquePaths_II.kt)
> **Pattern**: obstacle-zeroed grid DP · **Core page**

## The Problem

Paths avoiding **obstacles** (1 = blocked).

- Constraints: m, n ≤ 100.

## Examples

```
Input:  obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]   -> Output: 2
```

## Intuition — the [2.31](unique-paths.md) DP, obstacles zero the cell

An obstacle cell has 0 paths; the start being blocked → 0 immediately:

```kotlin
val dp = Array(R) { IntArray(C) }.apply { this[0][0] = 1 }
if (obstacleGrid[0][0] == 1) return 0

for (r in 0 until R) {
    for (c in 0 until C) {
        if (obstacleGrid[r][c] == 1) { dp[r][c] = 0; continue }

        if (r > 0) dp[r][c] += dp[r - 1][c]
        if (c > 0) dp[r][c] += dp[r][c - 1]
    }
}
return dp[R - 1][C - 1]
```

**Why zero and skip?** An obstacle can't be entered — its path count is 0, and it contributes nothing downstream.

## Approach 1 — Obstacle-zeroed DP (the repo's version, optimal)

```kotlin
class UniquePaths_II {
    /**
     * @param obstacleGrid 0/1 grid
     * @return             number of paths avoiding obstacles
     */
    fun uniquePathsWithObstacles(obstacleGrid: Array<IntArray>): Int {
        val (R, C) = obstacleGrid.size to obstacleGrid[0].size
        val dp = Array(R) { IntArray(C) }.apply { this[0][0] = 1 }

        if (obstacleGrid[0][0] == 1) return 0

        for (r in 0 until R) {
            for (c in 0 until C) {
                if (obstacleGrid[r][c] == 1) {
                    dp[r][c] = 0
                    continue
                }

                if (r > 0) dp[r][c] += dp[r - 1][c]
                if (c > 0) dp[r][c] += dp[r][c - 1]
            }
        }
        return dp[R - 1][C - 1]
    }
}
```

```java
public class UniquePathsII {
    /**
     * @param obstacleGrid 0/1 grid
     * @return             number of paths avoiding obstacles
     */
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int R = obstacleGrid.length, C = obstacleGrid[0].length;
        if (obstacleGrid[0][0] == 1) return 0;

        int[] dp = new int[C];
        dp[0] = 1;

        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                if (obstacleGrid[r][c] == 1) dp[c] = 0;
                else if (c > 0) dp[c] += dp[c - 1];
            }
        }
        return dp[C - 1];
    }
}
```

```cpp
#include <vector>

class UniquePathsII {
public:
    /**
     * @param obstacleGrid 0/1 grid
     * @return             number of paths avoiding obstacles
     */
    int uniquePathsWithObstacles(std::vector<std::vector<int>>& obstacleGrid) {
        int R = obstacleGrid.size(), C = obstacleGrid[0].size();
        if (obstacleGrid[0][0] == 1) return 0;

        std::vector<long> dp(C, 0);
        dp[0] = 1;

        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                if (obstacleGrid[r][c] == 1) dp[c] = 0;
                else if (c > 0) dp[c] += dp[c - 1];
            }
        }
        return (int)dp[C - 1];
    }
};
```

```python
def unique_paths_with_obstacles(obstacle_grid: list[list[int]]) -> int:
    """
    @param obstacle_grid: 0/1 grid
    @return:              number of paths avoiding obstacles
    """
    if obstacle_grid[0][0] == 1:
        return 0

    R, C = len(obstacle_grid), len(obstacle_grid[0])
    dp = [0] * C
    dp[0] = 1

    for r in range(R):
        for c in range(C):
            if obstacle_grid[r][c] == 1:
                dp[c] = 0
            elif c > 0:
                dp[c] += dp[c - 1]

    return dp[-1]
```

```rust
impl Solution {
    /// @param obstacle_grid 0/1 grid
    /// @return              number of paths avoiding obstacles
    pub fn unique_paths_with_obstacles(obstacle_grid: Vec<Vec<i32>>) -> i32 {
        let (r, c) = (obstacle_grid.len(), obstacle_grid[0].len());
        if obstacle_grid[0][0] == 1 { return 0; }

        let mut dp = vec![0i64; c];
        dp[0] = 1;

        for row in 0..r {
            for col in 0..c {
                if obstacle_grid[row][col] == 1 { dp[col] = 0; }
                else if col > 0 { dp[col] += dp[col - 1]; }
            }
        }
        dp[c - 1] as i32
    }
}
```

## Dry run

**Input:** `[[0,0,0],[0,1,0],[0,0,0]]`.

```
row 0: dp [1,1,1].  row 1: c=1 obstacle -> dp[1]=0.  -> [1,0,1].
row 2: c=1: 0+dp[0]=1.  c=2: 1+dp[1]=1... wait dp[2] was 1, += dp[1]=0 -> 1.  [1,1,1].
Output: 2 ✓
```

## Complexity

**Time.** Cells:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** One row:

$$
S(m, n) = O(n)
$$

## Variants & follow-ups

- **Unique Paths** ([2.31](unique-paths.md)) — the obstacle-free ancestor.
- **Interview follow-up:** "Why does the 1-D version zero `dp[c]` in place?" The obstacle's row-overwrite kills the above-path contribution; `dp[c] += dp[c-1]` then correctly adds only the left. The rolling row stays exact.
