# 3.6 Rotate Image

> **Source:** [`src/main/kotlin/array/RotateImage.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/RotateImage.kt)
> **Pattern:** matrix decomposition (transpose + reverse) · **Core page**

## The Problem

Rotate an `n × n` matrix **90° clockwise**, **in place** ($O(1)$ extra space).

- Constraints: $1 \le n \le 20$.

## Examples

```
Input:  [[1, 2, 3],
         [4, 5, 6],
         [7, 8, 9]]
Output: [[7, 4, 1],
         [8, 5, 2],
         [9, 6, 3]]

Input:  [[5, 1, 9, 11],
         [2, 4, 8, 10],
         [13, 3, 6, 7],
         [15, 14, 12, 16]]
Output: [[15, 13, 2, 5],
         [14, 3, 4, 1],
         [12, 6, 8, 9],
         [16, 7, 10, 11]]
```

## Intuition — a 90° rotation is two cheap operations

Rotating by 90° clockwise is the composition of two *easy* operations:

1. **Transpose** (mirror across the main diagonal): `matrix[i][j] <-> matrix[j][i]`.
2. **Reverse each row** (mirror across the vertical axis).

Why does this compose correctly? Track a cell `(i, j)`:

$$
(i, j) \xrightarrow{\text{transpose}} (j, i) \xrightarrow{\text{reverse row}} (j, n-1-i)
$$

And the target of a 90° clockwise rotation is exactly $(j, n-1-i)$ — the row index becomes the old column, the column index becomes $n-1$ minus the old row. (Check: the top-right corner `(0, n-1)` must move to the bottom-right `(n-1, n-1)`; the formula gives `(n-1, n-1-0) = (n-1, n-1)` ✓.)

Both operations are trivially in-place:

- Transpose visits the upper triangle (`j >= i`), swapping with the lower triangle.
- Row reversal is the two-pointer swap from [3.1](two-sum-ii.md) on each row.

**Why this beats the "rotate four cells" loop:** the four-way swap version (moving each element through 4 positions) is harder to get right (ring/offset bookkeeping); the two-step version is each step individually obvious, and the composition argument proves correctness. Interviews love hearing the composition *before* the code.

## Approach 1 — Extra matrix

Copy to a new matrix and read rotated: `rotated[j][n-1-i] = matrix[i][j]`. $O(n^2)$ time, $O(n^2)$ space — fails the in-place requirement.

## Approach 2 — Transpose + reverse (optimal)

```kotlin
/**
 * @param matrix the n x n matrix to rotate 90° clockwise in place
 * @return       Unit; matrix is mutated
 */
fun rotate(matrix: Array<IntArray>) {
    val n = matrix.size

    // Step 1: transpose — mirror across the main diagonal.
    for (i in 0 until n) {
        for (j in i until n) {
            swap(matrix, i, j, j, i)
        }
    }

    // Step 2: reverse every row — mirror across the vertical axis.
    for (i in 0 until n) {
        var left = 0
        var right = n - 1
        while (left < right) {
            swap(matrix, i, left, i, right)
            left++
            right--
        }
    }
}

/**
 * @param matrix the matrix being modified
 * @param i, j   the first cell (row, col)
 * @param m, n   the second cell (row, col)
 * @return       Unit; the two cells are swapped
 */
fun swap(matrix: Array<IntArray>, i: Int, j: Int, m: Int, n: Int) {
    matrix[i][j] = matrix[m][n].also { matrix[m][n] = matrix[i][j] }
}
```

```java
public class RotateImage {
    /**
     * @param matrix the n x n matrix to rotate 90° clockwise in place
     */
    public void rotate(int[][] matrix) {
        int n = matrix.length;

        for (int i = 0; i < n; i++) {                 // transpose
            for (int j = i; j < n; j++) {
                int tmp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = tmp;
            }
        }
        for (int i = 0; i < n; i++) {                 // reverse each row
            int left = 0, right = n - 1;
            while (left < right) {
                int tmp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = tmp;
                left++;
                right--;
            }
        }
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class RotateImage {
public:
    /**
     * @param matrix the n x n matrix to rotate 90° clockwise in place
     */
    void rotate(std::vector<std::vector<int>>& matrix) {
        int n = (int)matrix.size();

        for (int i = 0; i < n; i++) {                 // transpose
            for (int j = i; j < n; j++) {
                std::swap(matrix[i][j], matrix[j][i]);
            }
        }
        for (int i = 0; i < n; i++) {                 // reverse each row
            std::reverse(matrix[i].begin(), matrix[i].end());
        }
    }
};
```

```python
def rotate(matrix: list[list[int]]) -> None:
    """
    @param matrix: the n x n matrix to rotate 90° clockwise in place
    """
    n = len(matrix)
    for i in range(n):                        # transpose
        for j in range(i, n):
            matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]
    for row in matrix:                        # reverse each row
        row.reverse()
```

```rust
impl Solution {
    /// @param matrix the n x n matrix to rotate 90° clockwise in place
    pub fn rotate(matrix: &mut Vec<Vec<i32>>) {
        let n = matrix.len();
        for i in 0..n {                       // transpose
            for j in i..n {
                let tmp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = tmp;
            }
        }
        for row in matrix.iter_mut() {        // reverse each row
            row.reverse();
        }
    }
}
```

## Dry run

**Input:** `matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]`

```
Step 1 — transpose (swap across the main diagonal):
      [1, 2, 3]        [1, 4, 7]
      [4, 5, 6]  --->  [2, 5, 8]
      [7, 8, 9]        [3, 6, 9]
      swaps: (0,1)<->(1,0): 2<->4; (0,2)<->(2,0): 3<->7; (1,2)<->(2,1): 6<->8

Step 2 — reverse each row:
      [1, 4, 7]        [7, 4, 1]
      [2, 5, 8]  --->  [8, 5, 2]
      [3, 6, 9]        [9, 6, 3]   ✓
```

Verify one cell through the composition: `(0, 2)` = 3 → transpose → `(2, 0)` = 3 → reverse row 2 → `(2, 2)` = 3. Target of the rotation: `(0,2)` must go to `(2, n-1-0) = (2, 2)` ✓ — the corner landed exactly where the problem's output shows it.

## Complexity

**Time.** Transpose visits $\frac{n(n-1)}{2}$ upper-triangle cells, reversal visits $\frac{n^2}{2}$ positions:

$$
T(n) = O(n^2)
$$

**Space.** $O(1)$ — pure in-place swaps.

## Variants & follow-ups

- **Spiral Matrix** ([3.7](spiral-matrix.md)) — the reading counterpart; boundary peeling instead of two mirrors.
- **Rotate Image (counter-clockwise)** — transpose + reverse *columns* (mirror across the horizontal axis). Same composition trick, one changed step.
- **Rotate Array** (`src/main/kotlin/array/twopointer/RotateArray.kt`) — the 1D cousin; reverse-three-times is the same "decompose into simple mirrors" philosophy.
- **Interview follow-up:** "Prove the composition." Row-reversal maps `(r, c) -> (r, n-1-c)`; transpose maps `(r, c) -> (c, r)`. Composing: `(r, c) -> (c, n-1-r)`, which is the definition of a 90° clockwise rotation — every cell lands where it must.
- **Interview follow-up:** "Rotate by 180°?" Reverse every row AND reverse the rows' order — or equivalently, swap `(i,j)` with `(n-1-i, n-1-j)` for the upper-left quadrant.
