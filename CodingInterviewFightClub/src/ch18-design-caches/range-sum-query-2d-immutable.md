# 18.18 Range Sum Query 2D - Immutable

> **Source**: [`src/main/kotlin/array/prefixsum/2DPrefixSumImmutable.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/2DPrefixSumImmutable.kt)
> **Pattern**: 2-D prefix sums · **Core page**

## The Problem

`sumRegion(r1, c1, r2, c2)` — the rectangle sum, O(1) per query.

- Constraints: grid ≤ 200×200; queries ≤ 10⁴.

## Examples

```
["NumMatrix","sumRegion","sumRegion","sumRegion"]
[[[[3,0,1,4,2],[5,6,3,2,1],[1,2,0,1,5],[4,1,0,1,7],[1,0,3,0,5]]],[2,1,4,3],[1,1,2,2],[1,2,2,4]]
-> [null,8,11,12]
```

## Intuition — prefix sums with one extra row/col, then inclusion-exclusion

`prefix[i][j]` = sum of the rectangle `[0,i)×[0,j)`. A sub-rectangle is four prefix lookups:

```kotlin
init {
    prefix = Array(rows + 1) { IntArray(cols + 1) }

    for (i in 1..rows) {
        for (j in 1..cols) {
            prefix[i][j] = prefix[i - 1][j] + prefix[i][j - 1] - prefix[i - 1][j - 1] + matrix[i - 1][j - 1]
        }
    }
}

fun sumRegion(row1: Int, col1: Int, row2: Int, col2: Int): Int =
    prefix[row2 + 1][col2 + 1] - prefix[row1][col2 + 1] - prefix[row2 + 1][col1] + prefix[row1][col1]
```

**Why the `+1` padding?** It eliminates boundary branches — `prefix[i-1]` at i=1 reads row 0 (zeros), no special casing. The [3.x](../ch03-arrays/pattern-primer.md) prefix-sum padding, in 2-D.

**Why the four-term formula?** The big rectangle minus the top strip minus the left strip plus the (double-subtracted) corner — the 2-D inclusion-exclusion. The `+1` in the query indices converts 0-based input to 1-based prefix coordinates.

## Approach 1 — Sum per query (O(mn) per query)

Loop the rectangle: correct, slow.

## Approach 2 — 2-D prefix (the repo's version, optimal)

```kotlin
class NumMatrix(matrix: Array<IntArray>) {
    private val prefix: Array<IntArray>

    init {
        val rows = matrix.size
        val cols = matrix[0].size
        prefix = Array(rows + 1) { IntArray(cols + 1) }

        for (i in 1..rows) {
            for (j in 1..cols) {
                prefix[i][j] = prefix[i - 1][j] + prefix[i][j - 1] -
                    prefix[i - 1][j - 1] + matrix[i - 1][j - 1]
            }
        }
    }

    /**
     * @param row1 top row
     * @param col1 left col
     * @param row2 bottom row
     * @param col2 right col
     * @return     rectangle sum
     */
    fun sumRegion(row1: Int, col1: Int, row2: Int, col2: Int): Int =
        prefix[row2 + 1][col2 + 1] - prefix[row1][col2 + 1] -
        prefix[row2 + 1][col1] + prefix[row1][col1]
}
```

```java
public class NumMatrix {
    private final int[][] prefix;

    public NumMatrix(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        prefix = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                prefix[i][j] = prefix[i - 1][j] + prefix[i][j - 1]
                             - prefix[i - 1][j - 1] + matrix[i - 1][j - 1];
            }
        }
    }

    /**
     * @param row1 top row
     * @param col1 left col
     * @param row2 bottom row
     * @param col2 right col
     * @return     rectangle sum
     */
    public int sumRegion(int row1, int col1, int row2, int col2) {
        return prefix[row2 + 1][col2 + 1] - prefix[row1][col2 + 1]
             - prefix[row2 + 1][col1] + prefix[row1][col1];
    }
}
```

```cpp
#include <vector>

class NumMatrix {
    std::vector<std::vector<int>> prefix;

public:
    NumMatrix(std::vector<std::vector<int>>& matrix) {
        int m = matrix.size(), n = matrix[0].size();
        prefix.assign(m + 1, std::vector<int>(n + 1, 0));

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                prefix[i][j] = prefix[i - 1][j] + prefix[i][j - 1]
                             - prefix[i - 1][j - 1] + matrix[i - 1][j - 1];
            }
        }
    }

    /**
     * @param row1 top row
     * @param col1 left col
     * @param row2 bottom row
     * @param col2 right col
     * @return     rectangle sum
     */
    int sumRegion(int row1, int col1, int row2, int col2) {
        return prefix[row2 + 1][col2 + 1] - prefix[row1][col2 + 1]
             - prefix[row2 + 1][col1] + prefix[row1][col1];
    }
};
```

```python
class NumMatrix:
    def __init__(self, matrix: list[list[int]]):
        m, n = len(matrix), len(matrix[0])
        self.prefix = [[0] * (n + 1) for _ in range(m + 1)]

        for i in range(1, m + 1):
            for j in range(1, n + 1):
                self.prefix[i][j] = (self.prefix[i - 1][j] + self.prefix[i][j - 1]
                                     - self.prefix[i - 1][j - 1] + matrix[i - 1][j - 1])

    def sum_region(self, row1: int, col1: int, row2: int, col2: int) -> int:
        return (self.prefix[row2 + 1][col2 + 1] - self.prefix[row1][col2 + 1]
                - self.prefix[row2 + 1][col1] + self.prefix[row1][col1])
```

```rust
struct NumMatrix {
    prefix: Vec<Vec<i32>>,
}

impl NumMatrix {
    fn new(matrix: Vec<Vec<i32>>) -> Self {
        let (m, n) = (matrix.len(), matrix[0].len());
        let mut prefix = vec![vec![0; n + 1]; m + 1];

        for i in 1..=m {
            for j in 1..=n {
                prefix[i][j] = prefix[i - 1][j] + prefix[i][j - 1]
                             - prefix[i - 1][j - 1] + matrix[i - 1][j - 1];
            }
        }
        Self { prefix }
    }

    /// @param row1 top row
    /// @param col1 left col
    /// @param row2 bottom row
    /// @param col2 right col
    /// @return     rectangle sum
    fn sum_region(&self, row1: i32, col1: i32, row2: i32, col2: i32) -> i32 {
        let (r1, c1, r2, c2) = (row1 as usize, col1 as usize, row2 as usize, col2 as usize);
        self.prefix[r2 + 1][c2 + 1] - self.prefix[r1][c2 + 1]
            - self.prefix[r2 + 1][c1] + self.prefix[r1][c1]
    }
}
```

## Dry run

**Input:** the 5×5 example; `sumRegion(2,1,4,3)`.

```
prefix build (1-based): each cell = top + left - corner + matrix value.
sumRegion(2,1,4,3) =
  prefix[5][4] - prefix[2][4] - prefix[5][1] + prefix[2][1]
  = (sum of rows 0..4, cols 0..3) - (rows 0..1) - (cols 0 only) + (the double-subtracted corner)
  = 8 ✓   (rows 2..4 cols 1..3: [2,0,1]+[1,0,1]+[0,3,0] = 8)
```

## Complexity

**Time.** Build O(mn); query O(1):

$$
T = O(mn) \text{ build}, \quad O(1) \text{ query}
$$

**Space.** The prefix table:

$$
S = O(mn)
$$

## Variants & follow-ups

- **Range Sum Query Mutable** ([14.8](../ch14-sorting/segment-tree-and-fenwick.md)) — the Fenwick version when updates exist.
- **Interview follow-up:** "Why the `+1` padding?" 1-based indexing makes every `prefix[i-1]` legal at the boundaries — the build loop reads zeros instead of branching. The query's `+1`s are the same convention applied to the input's 0-based coordinates.
