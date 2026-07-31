# 2.15 Maximal Square

> **Source:** [`src/main/kotlin/array/dp/MaximalSquare.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/MaximalSquare.kt)
> **Pattern:** 2-D DP, min-of-three · **Core page**

## The Problem

Given a binary matrix of `'0'`/`'1'`, return the **area of the largest square** made of `'1'`s.

- Constraints: $m, n \le 300$; characters `'0'` or `'1'`.

## Examples

```
Input:  matrix = [["1","0","1","0","0"],
                  ["1","0","1","1","1"],
                  ["1","1","1","1","1"],
                  ["1","0","0","1","0"]]
Output: 4   (the 2x2 block of 1s)

Input:  matrix = [["0","1"],["1","0"]]   -> Output: 1
```

## Intuition — `dp[i][j]` = the largest square *ending* at (i,j)

Define `dp[i][j]` = side length of the largest all-1 square whose **bottom-right corner is (i, j)**. The recurrence is the [2.7](maximum-product-subarray.md)-style local-state idea:

$$
dp[i][j] = 1 + \min(dp[i-1][j],\; dp[i][j-1],\; dp[i-1][j-1])
$$

**Why the min-of-three?** A square ending at (i,j) of side `s` requires squares of side `s-1` ending at the *left*, *above*, and *diagonally-above-left* cells — all three must hold `'1'`-blocks of that size. The min is the limiting factor: the largest square you can extend is one more than the smallest of the three neighbors. If any is 0, `dp[i][j] = 1` (just the cell itself).

**Why bottom-right anchoring?** The three neighbors `(i-1,j), (i,j-1), (i-1,j-1)` are all "earlier" in row-major order — so a single pass computes the whole table, and the answer is the max over all `dp` values. This is the same "local definition, global max" shape as [2.1](longest-common-substring.md).

**Area vs side:** the repo tracks `maxSize` (side) and returns `maxSize * maxSize` — the problem asks for area. The 1x1 base case (`i == 0 || j == 0` → `dp = 1`) is the boundary seeding.

## Approach 1 — Brute force per cell (O(m^2 n^2))

For each `(i,j)`, try growing a square outward: correct, quartic on the worst case.

## Approach 2 — Min-of-three DP (the repo's version, optimal)

```kotlin
class MaximalSquare {
    /**
     * @param matrix binary matrix of '0'/'1'
     * @return       area of the largest all-1 square
     */
    fun maximalSquare(matrix: Array<CharArray>): Int {
        if (matrix.isEmpty() || matrix[0].isEmpty()) return 0
        val m = matrix.size
        val n = matrix[0].size
        val dp = Array(m) { IntArray(n) }
        var maxSize = 0

        for (i in 0 until m) {
            for (j in 0 until n) {
                if (matrix[i][j] == '1') {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1                     // boundary cell
                    } else {
                        dp[i][j] = minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1
                    }
                    maxSize = maxOf(maxSize, dp[i][j])
                }
            }
        }
        return maxSize * maxSize                         // area
    }
}
```

```java
public class MaximalSquare {
    /**
     * @param matrix binary matrix of '0'/'1'
     * @return       area of the largest all-1 square
     */
    public int maximalSquare(char[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] dp = new int[m][n];
        int maxSize = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == '1') {
                    if (i == 0 || j == 0) dp[i][j] = 1;                       // boundary
                    else dp[i][j] = Math.min(Math.min(dp[i-1][j], dp[i][j-1]), dp[i-1][j-1]) + 1;
                    maxSize = Math.max(maxSize, dp[i][j]);
                }
            }
        }
        return maxSize * maxSize;                        // area
    }
}
```

```cpp
#include <vector>

class MaximalSquare {
public:
    /**
     * @param matrix binary matrix of '0'/'1'
     * @return       area of the largest all-1 square
     */
    int maximalSquare(std::vector<std::vector<char>>& matrix) {
        int m = matrix.size(), n = matrix[0].size();
        std::vector<std::vector<int>> dp(m, std::vector<int>(n, 0));
        int maxSize = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == '1') {
                    if (i == 0 || j == 0) dp[i][j] = 1;                       // boundary
                    else dp[i][j] = std::min({dp[i-1][j], dp[i][j-1], dp[i-1][j-1]}) + 1;
                    maxSize = std::max(maxSize, dp[i][j]);
                }
            }
        }
        return maxSize * maxSize;                        // area
    }
};
```

```python
def maximal_square(matrix: list[list[str]]) -> int:
    """
    @param matrix: binary matrix of '0'/'1'
    @return:       area of the largest all-1 square
    """
    m, n = len(matrix), len(matrix[0])
    dp = [[0] * n for _ in range(m)]
    max_size = 0

    for i in range(m):
        for j in range(n):
            if matrix[i][j] == "1":
                if i == 0 or j == 0:
                    dp[i][j] = 1                     # boundary cell
                else:
                    dp[i][j] = min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1]) + 1
                max_size = max(max_size, dp[i][j])
    return max_size * max_size                       # area
```

```rust
impl Solution {
    /// @param matrix binary matrix of '0'/'1'
    /// @return       area of the largest all-1 square
    pub fn maximal_square(matrix: Vec<Vec<char>>) -> i32 {
        let (m, n) = (matrix.len(), matrix[0].len());
        let mut dp = vec![vec![0i32; n]; m];
        let mut max_size = 0;

        for i in 0..m {
            for j in 0..n {
                if matrix[i][j] == '1' {
                    dp[i][j] = if i == 0 || j == 0 { 1 }                     // boundary
                               else { dp[i-1][j].min(dp[i][j-1]).min(dp[i-1][j-1]) + 1 };
                    max_size = max_size.max(dp[i][j]);
                }
            }
        }
        max_size * max_size                          // area
    }
}
```

### 2. `MaximalRectangle.kt` — the histogram-stack upgrade

The classic "largest rectangle in a binary matrix" via **per-row histograms + the [8.5](../ch08-stacks/largest-rectangle-in-histogram.md) stack**:

```kotlin
class MaximalRectangle {
    fun largestRectangleArea(heights: IntArray): Int {
        val stack = Stack<Int>()
        var (maxArea, i) = listOf(0, 0)

        while (i <= heights.size) {
            val currentHeight = if (i == heights.size) 0 else heights[i]   // sentinel 0 flushes

            when {
                stack.isEmpty() || heights[stack.last()] <= currentHeight -> stack.add(i++)
                else -> {
                    val height = heights[stack.pop()]
                    val width = if (stack.isEmpty()) i else i - stack.peek() - 1
                    maxArea = maxOf(maxArea, height * width)
                }
            }
        }
        return maxArea
    }
    // ... plus the per-row histogram accumulation: heights[j] = if (matrix[i][j] == '1') heights[j] + 1 else 0
}
```

**What's cool:** the `i == heights.size ? 0` sentinel flushes the stack without a post-loop; the `when` is the monotonic-stack three-way decision ([8.5](../ch08-stacks/largest-rectangle-in-histogram.md) compressed); and the row-major histogram update turns the matrix problem into repeated 1-D problems.


## Dry run

**Input:** `matrix = [["1","1"],["1","1"]]`.

```
i=0, j=0: '1', boundary -> dp[0][0]=1.  maxSize=1
i=0, j=1: '1', boundary -> dp[0][1]=1.  maxSize=1
i=1, j=0: '1', boundary -> dp[1][0]=1.  maxSize=1
i=1, j=1: '1' -> dp[1][1] = min(1,1,1) + 1 = 2.  maxSize=2

Output: 2*2 = 4 ✓
```

The min-of-three at `(1,1)`: all three neighbors report side-1 squares, so the cell extends them to side 2. If any neighbor were 0 (say `dp[0][1] = 0`), the min would be 0 and the cell could only start a fresh side-1 square — that's the "all three must support it" rule in one expression.

## Complexity

**Time.** One pass over the grid:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** The dp table (two rows suffice — the rolling-row variant):

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Maximal Rectangle** (`grid/histogram/MaximalRectangle.kt`) — the rectangle version: per-row histogram + [8.5](../ch08-stacks/largest-rectangle-in-histogram.md)'s monotonic stack.
- **Largest Square Area In Matrix** (`google/LargestSquareAreaInMatrix.kt`) — the same DP under another name.
- **Interview follow-up:** "Why `min` of the three neighbors and not `max`?" A side-s square at (i,j) needs side-(s-1) squares at *all three* neighbors. The weakest neighbor is the constraint — `min` selects it. Using `max` would count squares that don't fully tile the cell's neighborhood.
