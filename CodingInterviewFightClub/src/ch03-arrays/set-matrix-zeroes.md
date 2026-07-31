# 3.12 Set Matrix Zeroes

> **Source:** [`src/main/kotlin/array/SetMatrixZeroes.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/SetMatrixZeroes.kt)
> **Pattern:** first-row/col markers · **Core page**

## The Problem

Given an `m × n` matrix, set entire **rows and columns** containing a `0` to zeroes — in place, **O(1) extra space**.

- Constraints: $1 \le m, n \le 200$; values fit in `Int`.

## Examples

```
Input:  matrix = [[1,1,1],[1,0,1],[1,1,1]]
Output: [[1,0,1],[0,0,0],[1,0,1]]
```

## Intuition — use the first row and first column as the "which rows/cols to zero" memo

The naive approach needs two sets (rows, cols) → O(m + n) space. The O(1) trick: **the matrix itself is the memo**. The first row can record "column j must be zeroed" and the first column "row i must be zeroed":

1. **Snapshot the first row/col** — do they *originally* contain a 0? (They'll be overwritten as markers, so the flags must be saved first.)
2. **Mark** — for every `matrix[i][j] == 0` with `i, j ≥ 1`, set `matrix[i][0] = 0` (row i is doomed) and `matrix[0][j] = 0` (column j is doomed).
3. **Zero out** — for `i, j ≥ 1`, if either marker is 0, zero the cell.
4. **Restore** — using the saved flags, zero the first row / first column if needed.

**Why skip the first row/col during marking?** They're the marker lanes — overwriting them while scanning would corrupt the memo. The two saved booleans (`firstRowHasZero`, `firstColHasZero`) are the only extra state, restoring what the lanes sacrificed.

**Why is this O(1) space and not "cheating"?** The matrix has O(mn) cells; reusing the boundary as bookkeeping is the same trick as [1.16](../ch01-binary-search/search-in-rotated-sorted-array-ii.md)'s in-place flags and [3.10](product-of-array-except-self.md)'s answer-as-memo.

## Approach 1 — Row/col sets (O(m + n) space)

Collect the doomed rows/cols in two sets, then zero: correct, but violates the constraint.

## Approach 2 — First-row/col markers (the repo's version, optimal)

```kotlin
class SetMatrixZeroes {
    /**
     * @param matrix m x n matrix (zeroed in place, O(1) space)
     */
    fun setZeroes(matrix: Array<IntArray>) {
        val firstRowHasZero = matrix[0].any { it == 0 }        // snapshot
        val firstColHasZero = matrix.any { it[0] == 0 }

        // Use first row/col as markers
        for (i in 1 until matrix.size) {
            for (j in 1 until matrix[0].size) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0
                    matrix[0][j] = 0
                }
            }
        }

        // Zero out based on markers
        for (i in 1 until matrix.size) {
            for (j in 1 until matrix[0].size) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0
                }
            }
        }

        // Restore the first row and column
        if (firstRowHasZero) matrix[0].fill(0)
        if (firstColHasZero) matrix.forEach { it[0] = 0 }
    }
}
```

```java
public class SetMatrixZeroes {
    /**
     * @param matrix m x n matrix (zeroed in place, O(1) space)
     */
    public void setZeroes(int[][] matrix) {
        boolean firstRow = false, firstCol = false;
        for (int j = 0; j < matrix[0].length; j++) if (matrix[0][j] == 0) firstRow = true;
        for (int[] row : matrix) if (row[0] == 0) firstCol = true;

        for (int i = 1; i < matrix.length; i++) {        // mark with the lanes
            for (int j = 1; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) { matrix[i][0] = 0; matrix[0][j] = 0; }
            }
        }

        for (int i = 1; i < matrix.length; i++) {        // zero out by markers
            for (int j = 1; j < matrix[0].length; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;
            }
        }

        if (firstRow) Arrays.fill(matrix[0], 0);         // restore
        if (firstCol) for (int[] row : matrix) row[0] = 0;
    }
}
```

```cpp
#include <vector>

class SetMatrixZeroes {
public:
    /**
     * @param matrix m x n matrix (zeroed in place, O(1) space)
     */
    void setZeroes(std::vector<std::vector<int>>& matrix) {
        bool firstRow = false, firstCol = false;
        for (int j = 0; j < (int)matrix[0].size(); j++) if (matrix[0][j] == 0) firstRow = true;
        for (auto& row : matrix) if (row[0] == 0) firstCol = true;

        for (int i = 1; i < (int)matrix.size(); i++) {        // mark with the lanes
            for (int j = 1; j < (int)matrix[0].size(); j++) {
                if (matrix[i][j] == 0) { matrix[i][0] = 0; matrix[0][j] = 0; }
            }
        }

        for (int i = 1; i < (int)matrix.size(); i++) {        // zero out by markers
            for (int j = 1; j < (int)matrix[0].size(); j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;
            }
        }

        if (firstRow) std::fill(matrix[0].begin(), matrix[0].end(), 0);
        if (firstCol) for (auto& row : matrix) row[0] = 0;
    }
};
```

```python
def set_zeroes(matrix: list[list[int]]) -> None:
    """
    @param matrix: m x n matrix (zeroed in place, O(1) space)
    """
    first_row = any(v == 0 for v in matrix[0])         # snapshot
    first_col = any(row[0] == 0 for row in matrix)

    for i in range(1, len(matrix)):                    # mark with the lanes
        for j in range(1, len(matrix[0])):
            if matrix[i][j] == 0:
                matrix[i][0] = 0
                matrix[0][j] = 0

    for i in range(1, len(matrix)):                    # zero out by markers
        for j in range(1, len(matrix[0])):
            if matrix[i][0] == 0 or matrix[0][j] == 0:
                matrix[i][j] = 0

    if first_row:
        for j in range(len(matrix[0])):
            matrix[0][j] = 0
    if first_col:
        for i in range(len(matrix)):
            matrix[i][0] = 0
```

```rust
impl Solution {
    /// @param matrix m x n matrix (zeroed in place, O(1) space)
    pub fn set_zeroes(matrix: &mut Vec<Vec<i32>>) {
        let (m, n) = (matrix.len(), matrix[0].len());
        let first_row = matrix[0].iter().any(|&v| v == 0);       // snapshot
        let first_col = matrix.iter().any(|r| r[0] == 0);

        for i in 1..m {                                          // mark with the lanes
            for j in 1..n {
                if matrix[i][j] == 0 { matrix[i][0] = 0; matrix[0][j] = 0; }
            }
        }

        for i in 1..m {                                          // zero out by markers
            for j in 1..n {
                if matrix[i][0] == 0 || matrix[0][j] == 0 { matrix[i][j] = 0; }
            }
        }

        if first_row { for v in matrix[0].iter_mut() { *v = 0; } }
        if first_col { for r in matrix.iter_mut() { r[0] = 0; } }
    }
}
```

## Dry run

**Input:** `matrix = [[1,1,1],[1,0,1],[1,1,1]]`.

```
firstRowHasZero = false, firstColHasZero = false

mark: i=1,j=1: matrix[1][1] = 0 -> matrix[1][0]=0, matrix[0][1]=0
      no other zeros -> markers: matrix = [[1,0,1],[0,0,1],[1,1,1]]

zero: i=1: j=1: matrix[1][0]=0 -> 0.  j=2: matrix[1][0]=0 -> 0.
      i=2: j=1: matrix[0][1]=0 -> 0.  j=2: no marker -> stays 1.

restore: both flags false -> nothing.

matrix = [[1,0,1],[0,0,0],[1,0,1]] ✓
```

The marker lanes in action: the single interior 0 at `(1,1)` paints `matrix[1][0]` and `matrix[0][1]`, and the second pass spreads those marks across row 1 and column 1 — without the `firstRow/firstCol` snapshot, the lane's own zeros (which are *marks*, not data) would be misread. The snapshot is the correctness hinge.

## Complexity

**Time.** Three passes:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** Two booleans:

$$
S(m, n) = O(1)
$$

## Variants & follow-ups

- **Transpose / Rotate Image** ([3.6](rotate-image.md)) — more in-place matrix surgery; the same "use the structure as the memo" discipline.
- **Set Matrix Zeroes (O(m+n) sets)** — the simpler first answer: two sets, zero rows/cols; the O(1) upgrade is this page's marker trick.
- **Interview follow-up:** "Why must the first row/col flags be saved before marking?" The marking pass *writes* into the first row/col — after that, `matrix[0][j]` no longer tells you whether column j was originally doomed. The saved booleans are the original truth, restored at the end.
