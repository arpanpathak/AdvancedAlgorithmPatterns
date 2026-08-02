# 3.27 Toeplitz Matrix

> **Source**: [`src/main/kotlin/array/ToeplitzMatrix.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/ToeplitzMatrix.kt)
> **Pattern**: diagonal-consistency check · **Core page**

## The Problem

Every top-left→bottom-right diagonal has equal values.

- Constraints: m, n ≤ 20.

## Examples

```
Input:  [[1,2,3,4],[5,1,2,3],[9,5,1,2]]   -> Output: true
Input:  [[1,2],[2,2]]                     -> Output: false
```

## Intuition — each cell must equal its top-left neighbor

A cell belongs to a diagonal; consistency means `matrix[r][c] == matrix[r-1][c-1]` for all interior cells — checking neighbors is O(1) per cell:

```kotlin
for (r in 1 until m) {
    for (c in 1 until n) {
        if (matrix[r][c] != matrix[r - 1][c - 1]) return false
    }
}
return true
```

**Why neighbor comparison suffices?** Equality is transitive along the diagonal — if every cell matches its predecessor, the whole diagonal is uniform.

## Approach 1 — Check each diagonal explicitly (the repo's style)

Walk each diagonal start: correct, more code.

## Approach 2 — Neighbor check (optimal)

```kotlin
class ToeplitzMatrix {
    /**
     * @param matrix input matrix
     * @return       true iff every diagonal is uniform
     */
    fun isToeplitzMatrix(matrix: Array<IntArray>): Boolean {
        val m = matrix.size
        val n = matrix[0].size

        for (r in 1 until m) {
            for (c in 1 until n) {
                if (matrix[r][c] != matrix[r - 1][c - 1]) return false
            }
        }
        return true
    }
}
```

```java
public class ToeplitzMatrix {
    /**
     * @param matrix input matrix
     * @return       true iff every diagonal is uniform
     */
    public boolean isToeplitzMatrix(int[][] matrix) {
        for (int r = 1; r < matrix.length; r++)
            for (int c = 1; c < matrix[0].length; c++)
                if (matrix[r][c] != matrix[r - 1][c - 1]) return false;
        return true;
    }
}
```

```cpp
#include <vector>

class ToeplitzMatrix {
public:
    /**
     * @param matrix input matrix
     * @return       true iff every diagonal is uniform
     */
    bool isToeplitzMatrix(std::vector<std::vector<int>>& matrix) {
        for (int r = 1; r < (int)matrix.size(); r++)
            for (int c = 1; c < (int)matrix[0].size(); c++)
                if (matrix[r][c] != matrix[r - 1][c - 1]) return false;
        return true;
    }
};
```

```python
def is_toeplitz_matrix(matrix: list[list[int]]) -> bool:
    """
    @param matrix: input matrix
    @return:       true iff every diagonal is uniform
    """
    return all(
        matrix[r][c] == matrix[r - 1][c - 1]
        for r in range(1, len(matrix))
        for c in range(1, len(matrix[0]))
    )
```

```rust
impl Solution {
    /// @param matrix input matrix
    /// @return       true iff every diagonal is uniform
    pub fn is_toeplitz_matrix(matrix: Vec<Vec<i32>>) -> bool {
        for r in 1..matrix.len() {
            for c in 1..matrix[0].len() {
                if matrix[r][c] != matrix[r - 1][c - 1] { return false; }
            }
        }
        true
    }
}
```

## Reading the code — what's actually happening

```kotlin
for (r in 1 until m) {
    for (c in 1 until n) {
        if (matrix[r][c] != matrix[r - 1][c - 1]) return false
    }
}
return true
```

A Toeplitz matrix is one where every top-left → bottom-right diagonal is a single constant. Instead of walking each diagonal separately (there are `m + n - 1` of them, each with its own bookkeeping), notice what a diagonal *is*: a chain of cells where each step moves down-right by one.

- **Loops start at row 1 and column 1, not 0.** The first row and first column have no top-left neighbor, so there's nothing to check there — every other cell `(r, c)` does have a predecessor `(r-1, c-1)` up and to the left.
- **The comparison `matrix[r][c] != matrix[r-1][c-1]` checks one link of the chain.** It asks "did this cell stay the same as the one before it on its diagonal?" If a single link is broken, the diagonal is inconsistent → the matrix fails immediately.
- **Why is one link per cell enough?** Equality is transitive: if cell A equals its predecessor B, and B equals its predecessor C, then A equals C — and by induction every cell on the diagonal equals its origin. The diagonal's uniformity is entirely determined by its adjacent-pair checks, so covering all interior cells covers all diagonals.

Trace the example matrix: `(1,1)=1 == (0,0)=1` ✓, `(1,2)=2 == (0,1)=2` ✓, `(1,3)=3 == (0,2)=3` ✓, `(2,1)=5 == (1,0)=5` ✓, `(2,2)=1 == (1,1)=1` ✓, `(2,3)=2 == (1,2)=2` ✓ — every diagonal holds → `true` ✓.

## Dry run

**Input:** `[[1,2,3,4],[5,1,2,3],[9,5,1,2]]`.

```
(1,1): 1 == matrix[0][0]=1 ✓.  (1,2): 2 == 2 ✓.  (1,3): 3 == 3 ✓.
(2,1): 5 == 5 ✓.  (2,2): 1 == 1 ✓.  (2,3): 2 == 2 ✓.
Output: true ✓
```

## Complexity

**Time.** One pass:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** Constants:

$$
S(m, n) = O(1)
$$

## Variants & follow-ups

- **Transpose Matrix** ([3.28](transpose-matrix.md)) — the matrix-operation sibling.
- **Interview follow-up:** "Why is checking one neighbor enough?" Diagonal equality is transitive — `a == b` and `b == c` imply `a == c`. Each adjacent pair check extends the guarantee along the whole diagonal.
