# 2.31 Unique Paths

> **Source**: [`src/main/kotlin/grid/dynamic_programming/UniquePaths_I.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/dynamic_programming/UniquePaths_I.kt)
> **Pattern**: grid DP / combinatorics · **Core page**

## The Problem

Robot moves right/down from (0,0) to (m-1,n-1). Number of paths.

- Constraints: m, n ≤ 100.

## Examples

```
Input:  m = 3, n = 7   -> Output: 28
```

## Intuition — every cell's path count is the sum of its two ancestors

`dp[r][c] = dp[r-1][c] + dp[r][c-1]` with the first row/col = 1 (only one way along the edges):

```kotlin
val dp = Array(m) { IntArray(n) { 1 } }

for (row in 1 until m) {
    for (col in 1 until n) {
        dp[row][col] = dp[row - 1][col] + dp[row][col - 1]
    }
}
return dp[m - 1][n - 1]
```

**Why the 1-filled initialization?** The top row and left column each have exactly one path (straight along the edge). The [2.29](min-cost-climbing-stairs.md) two-step DP in 2-D.

## Approach 1 — Grid DP (the repo's version)

## Approach 2 — Combinatorics (O(1) space)

The path has (m-1) downs and (n-1) rights — $\binom{m+n-2}{m-1}$.

```kotlin
class UniquePaths_I {
    /**
     * @param m rows
     * @param n cols
     * @return  number of right/down paths
     */
    fun uniquePaths(m: Int, n: Int): Int {
        val dp = Array(m) { IntArray(n) { 1 } }

        for (row in 1 until m) {
            for (col in 1 until n) {
                dp[row][col] = dp[row - 1][col] + dp[row][col - 1]
            }
        }
        return dp[m - 1][n - 1]
    }
}
```

```java
public class UniquePaths {
    /**
     * @param m rows
     * @param n cols
     * @return  number of right/down paths
     */
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        java.util.Arrays.fill(dp, 1);

        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[c] += dp[c - 1];
            }
        }
        return dp[n - 1];
    }
}
```

```cpp
#include <vector>

class UniquePaths {
public:
    /**
     * @param m rows
     * @param n cols
     * @return  number of right/down paths
     */
    int uniquePaths(int m, int n) {
        std::vector<int> dp(n, 1);

        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[c] += dp[c - 1];
            }
        }
        return dp[n - 1];
    }
};
```

```python
def unique_paths(m: int, n: int) -> int:
    """
    @param m: rows
    @param n: cols
    @return:  number of right/down paths
    """
    dp = [1] * n

    for _ in range(1, m):
        for c in range(1, n):
            dp[c] += dp[c - 1]

    return dp[-1]
```

```rust
impl Solution {
    /// @param m rows
    /// @param n cols
    /// @return  number of right/down paths
    pub fn unique_paths(m: i32, n: i32) -> i32 {
        let n = n as usize;
        let mut dp = vec![1i64; n];

        for _ in 1..m {
            for c in 1..n {
                dp[c] += dp[c - 1];
            }
        }
        dp[n - 1] as i32
    }
}
```

## Dry run

**Input:** `m = 3, n = 3`.

```
dp rows: [1,1,1] -> r=1: [1,2,3] -> r=2: [1,3,6].
Output: 6 ✓  (3x3 grid has 6 paths)
```

## Complexity

**Time.** Cells:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** One row (or O(mn)):

$$
S(m, n) = O(n)
$$

## Variants & follow-ups

- **Unique Paths II** ([2.32](unique-paths-ii.md)) — obstacles zero the cells.
- **Interview follow-up:** "Why does the 1-D rolling row work?" `dp[c]` (old = above) + `dp[c-1]` (new = left) — the recurrence needs exactly one row of history; each pass overwrites in place.
