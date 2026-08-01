# 2.37 Minimum Path Sum

> **Source**: [`src/main/kotlin/array/dp/MinimumPathSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/MinimumPathSum.kt)
> **Pattern**: grid min DP · **Core page**

## The Problem

Min sum along right/down paths (0,0)→(m-1,n-1).

- Constraints: m, n ≤ 200.

## Examples

```
Input:  grid = [[1,3,1],[1,5,1],[4,2,1]]   -> Output: 7
```

## Intuition — each cell's best = its value + the cheaper predecessor

```kotlin
val dp = Array(grid.size) { IntArray(grid[0].size) }

for (i in grid.indices) {
    for (j in 0 until grid[i].size) {
        dp[i][j] = grid[i][j]
        dp[i][j] += when {
            i == 0 && j == 0 -> 0
            i == 0 -> dp[i][j - 1]
            j == 0 -> dp[i - 1][j]
            else -> minOf(dp[i - 1][j], dp[i][j - 1])
        }
    }
}
return dp[grid.size - 1][grid[0].size - 1]
```

## Approach 1 — Grid DP (the repo's version, optimal)

```kotlin
class MinimumPathSum {
    /**
     * @param grid weighted grid
     * @return     min path sum
     */
    fun minPathSum(grid: Array<IntArray>): Int {
        if (grid.isNullOrEmpty()) return 0

        val dp = Array(grid.size) { IntArray(grid[0].size) }

        for (i in grid.indices) {
            for (j in 0 until grid[i].size) {
                dp[i][j] = grid[i][j]
                dp[i][j] += when {
                    i == 0 && j == 0 -> 0
                    i == 0 -> dp[i][j - 1]
                    j == 0 -> dp[i - 1][j]
                    else -> minOf(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }
        return dp[grid.size - 1][grid[0].size - 1]
    }
}
```

```java
public class MinimumPathSum {
    /**
     * @param grid weighted grid
     * @return     min path sum
     */
    public int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] dp = new int[n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0) dp[j] = grid[0][0];
                else if (i == 0) dp[j] = dp[j - 1] + grid[i][j];
                else if (j == 0) dp[j] = dp[j] + grid[i][j];
                else dp[j] = Math.min(dp[j], dp[j - 1]) + grid[i][j];
            }
        }
        return dp[n - 1];
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MinimumPathSum {
public:
    /**
     * @param grid weighted grid
     * @return     min path sum
     */
    int minPathSum(std::vector<std::vector<int>>& grid) {
        int m = grid.size(), n = grid[0].size();
        std::vector<int> dp(n, 0);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0) dp[j] = grid[0][0];
                else if (i == 0) dp[j] = dp[j - 1] + grid[i][j];
                else if (j == 0) dp[j] = dp[j] + grid[i][j];
                else dp[j] = std::min(dp[j], dp[j - 1]) + grid[i][j];
            }
        }
        return dp[n - 1];
    }
};
```

```python
def min_path_sum(grid: list[list[int]]) -> int:
    """
    @param grid: weighted grid
    @return:     min path sum
    """
    m, n = len(grid), len(grid[0])
    dp = [0] * n

    for i in range(m):
        for j in range(n):
            if i == 0 and j == 0:
                dp[j] = grid[0][0]
            elif i == 0:
                dp[j] = dp[j - 1] + grid[i][j]
            elif j == 0:
                dp[j] = dp[j] + grid[i][j]
            else:
                dp[j] = min(dp[j], dp[j - 1]) + grid[i][j]

    return dp[-1]
```

```rust
impl Solution {
    /// @param grid weighted grid
    /// @return     min path sum
    pub fn min_path_sum(grid: Vec<Vec<i32>>) -> i32 {
        let (m, n) = (grid.len(), grid[0].len());
        let mut dp = vec![0; n];

        for i in 0..m {
            for j in 0..n {
                dp[j] = if i == 0 && j == 0 { grid[0][0] }
                    else if i == 0 { dp[j - 1] + grid[i][j] }
                    else if j == 0 { dp[j] + grid[i][j] }
                    else { dp[j].min(dp[j - 1]) + grid[i][j] };
            }
        }
        dp[n - 1]
    }
}
```

## Dry run

**Input:** the example.

```
dp row 0: [1,4,5].  row 1: [2, min(4+5=9? dp[1]=2? ...] — trace: (1,0): dp[0]+grid=2.
  (1,1): min(dp[1]=4, dp[0]=2) + 5 = 7.  (1,2): min(5,7)+1 = 6.  -> [2,7,6].
row 2: (2,0): 2+4=6.  (2,1): min(7,6)+2 = 8.  (2,2): min(6,8)+1 = 7.
Output: 7 ✓
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

- **Unique Paths** ([2.31](unique-paths.md)) — the counting twin.
- **Interview follow-up:** "Why is the one-row DP safe?" `dp[j]` (old = above) and `dp[j-1]` (new = left) are the only dependencies — the recurrence needs exactly one row of history.
