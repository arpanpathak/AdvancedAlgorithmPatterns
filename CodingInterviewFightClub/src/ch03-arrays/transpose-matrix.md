# 3.28 Transpose Matrix

> **Source**: [`src/main/kotlin/array/TransposeMatrix.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/TransposeMatrix.kt)
> **Pattern**: index swap · **Core page**

## The Problem

Return the matrix transposed (`result[j][i] = matrix[i][j]`).

- Constraints: m, n ≤ 1000.

## Examples

```
Input:  [[1,2,3],[4,5,6]]   -> Output: [[1,4],[2,5],[3,6]]
```

## Intuition — write to the swapped index

```kotlin
val transposed = Array(cols) { IntArray(rows) }
for (i in matrix.indices) {
    for (j in matrix[i].indices) {
        transposed[j][i] = matrix[i][j]
    }
}
return transposed
```

## Approach 1 — In-place rotation (square only)

The swap trick needs a square matrix; this is the general case.

## Approach 2 — Output matrix (the repo's version, optimal)

```kotlin
class TransposeMatrix {
    /**
     * @param matrix input matrix
     * @return       transposed matrix
     */
    fun transpose(matrix: Array<IntArray>): Array<IntArray> {
        val rows = matrix.size
        val cols = matrix[0].size
        val transposed = Array(cols) { IntArray(rows) }

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                transposed[j][i] = matrix[i][j]
            }
        }
        return transposed
    }
}
```

```java
public class TransposeMatrix {
    /**
     * @param matrix input matrix
     * @return       transposed matrix
     */
    public int[][] transpose(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] result = new int[n][m];

        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                result[j][i] = matrix[i][j];
        return result;
    }
}
```

```cpp
#include <vector>

class TransposeMatrix {
public:
    /**
     * @param matrix input matrix
     * @return       transposed matrix
     */
    std::vector<std::vector<int>> transpose(std::vector<std::vector<int>>& matrix) {
        int m = matrix.size(), n = matrix[0].size();
        std::vector<std::vector<int>> result(n, std::vector<int>(m));

        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                result[j][i] = matrix[i][j];
        return result;
    }
};
```

```python
def transpose(matrix: list[list[int]]) -> list[list[int]]:
    """
    @param matrix: input matrix
    @return:       transposed matrix
    """
    return [[matrix[i][j] for i in range(len(matrix))] for j in range(len(matrix[0]))]
```

```rust
impl Solution {
    /// @param matrix input matrix
    /// @return       transposed matrix
    pub fn transpose(matrix: Vec<Vec<i32>>) -> Vec<Vec<i32>> {
        let (m, n) = (matrix.len(), matrix[0].len());
        let mut result = vec![vec![0; m]; n];

        for i in 0..m {
            for j in 0..n {
                result[j][i] = matrix[i][j];
            }
        }
        result
    }
}
```

## Dry run

**Input:** `[[1,2,3],[4,5,6]]`.

```
result[0][0]=1, result[1][0]=2, result[2][0]=3, result[0][1]=4, result[1][1]=5, result[2][1]=6.
Output: [[1,4],[2,5],[3,6]] ✓
```

## Complexity

**Time.** Every cell:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** The output:

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Rotate Image** — the square-matrix rotation using transpose.
- **Interview follow-up:** "Why can't this be in-place for rectangular matrices?" In-place transposition permutes cells in cycles that depend on m, n — for squares the swap trick works; the general case needs the output buffer (or cycle-following).
