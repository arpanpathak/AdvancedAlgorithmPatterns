# 3.7 Spiral Matrix

> **Source:** [`src/main/kotlin/array/SpiralMatrix.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/SpiralMatrix.kt) · [`SpiralMatrix_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/SpiralMatrix_II.kt) (the "generate" twin)
> **Pattern:** boundary peeling · **Core page**

## The Problem

Given an `m × n` matrix, return all its elements in **spiral order** (right, down, left, up, repeat, shrinking inward).

- Constraints: $1 \le m, n \le 10$; values fit in `Int`.

## Examples

```
Input:  [[1, 2, 3],
         [4, 5, 6],
         [7, 8, 9]]
Output: [1, 2, 3, 6, 9, 8, 7, 4, 5]

Input:  [[1, 2, 3, 4],
         [5, 6, 7, 8],
         [9, 10, 11, 12]]
Output: [1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7]
```

## Intuition — peel the onion

The spiral is just **concentric rectangular rings**, read clockwise from the outermost to the innermost. So keep four boundaries — `top`, `bottom`, `left`, `right` — and at each step:

1. Walk the **top row** `left → right`, then `top++` (that row is consumed).
2. Walk the **right column** `top → bottom`, then `right--`.
3. Walk the **bottom row** `right → left` (if any rows remain), then `bottom--`.
4. Walk the **left column** `bottom → top` (if any columns remain), then `left++`.

Stop when the boundaries cross. The delicate part is the **odd-dimension case**: after walking the top row and right column, the bottom row and left column may already be *consumed or overlapping* — that's why steps 3 and 4 need the `top <= bottom` / `left <= right` guards before executing (otherwise you'd re-read a single middle row/column or go out of bounds).

The repo's version counts visited elements and guards each inner walk with `if (count < totalElements)` — a slightly different but equivalent way to say the same thing; the boundary-guard version below is the cleaner formulation.

## Approach 1 — Simulate with a visited set

Walk right/down/left/up, turning when the next cell is out of bounds or already visited: $O(mn)$ time, $O(mn)$ space. Correct but the visited array is unnecessary.

## Approach 2 — Boundary peeling (optimal)

```kotlin
/**
 * @param matrix the m x n matrix to read in spiral order
 * @return       all elements of matrix in spiral order
 */
fun spiralOrder(matrix: Array<IntArray>): List<Int> {
    if (matrix.isEmpty() || matrix[0].isEmpty()) return emptyList()

    val result = mutableListOf<Int>()
    var top = 0
    var bottom = matrix.size - 1
    var left = 0
    var right = matrix[0].size - 1

    while (top <= bottom && left <= right) {
        // Walk the top row left -> right, then drop it.
        for (j in left..right) result.add(matrix[top][j])
        top++

        // Walk the right column top -> bottom, then drop it.
        for (i in top..bottom) result.add(matrix[i][right])
        right--

        // Remaining rows? Walk the bottom row right -> left.
        if (top <= bottom) {
            for (j in right downTo left) result.add(matrix[bottom][j])
            bottom--
        }

        // Remaining columns? Walk the left column bottom -> top.
        if (left <= right) {
            for (i in bottom downTo top) result.add(matrix[i][left])
            left++
        }
    }
    return result
}
```

```java
import java.util.*;

public class SpiralMatrix {
    /**
     * @param matrix the m x n matrix to read in spiral order
     * @return       all elements of matrix in spiral order
     */
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        int top = 0, bottom = matrix.length - 1;
        int left = 0, right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {
            for (int j = left; j <= right; j++) result.add(matrix[top][j]);
            top++;
            for (int i = top; i <= bottom; i++) result.add(matrix[i][right]);
            right--;
            if (top <= bottom) {
                for (int j = right; j >= left; j--) result.add(matrix[bottom][j]);
                bottom--;
            }
            if (left <= right) {
                for (int i = bottom; i >= top; i--) result.add(matrix[i][left]);
                left++;
            }
        }
        return result;
    }
}
```

```cpp
#include <vector>

class SpiralMatrix {
public:
    /**
     * @param matrix the m x n matrix to read in spiral order
     * @return       all elements of matrix in spiral order
     */
    std::vector<int> spiralOrder(const std::vector<std::vector<int>>& matrix) {
        std::vector<int> result;
        int top = 0, bottom = (int)matrix.size() - 1;
        int left = 0, right = (int)matrix[0].size() - 1;

        while (top <= bottom && left <= right) {
            for (int j = left; j <= right; j++) result.push_back(matrix[top][j]);
            top++;
            for (int i = top; i <= bottom; i++) result.push_back(matrix[i][right]);
            right--;
            if (top <= bottom) {
                for (int j = right; j >= left; j--) result.push_back(matrix[bottom][j]);
                bottom--;
            }
            if (left <= right) {
                for (int i = bottom; i >= top; i--) result.push_back(matrix[i][left]);
                left++;
            }
        }
        return result;
    }
};
```

```python
def spiral_order(matrix: list[list[int]]) -> list[int]:
    """
    @param matrix: the m x n matrix to read in spiral order
    @return:       all elements of matrix in spiral order
    """
    result: list[int] = []
    top, bottom = 0, len(matrix) - 1
    left, right = 0, len(matrix[0]) - 1

    while top <= bottom and left <= right:
        for j in range(left, right + 1):
            result.append(matrix[top][j])     # top row
        top += 1
        for i in range(top, bottom + 1):
            result.append(matrix[i][right])   # right column
        right -= 1
        if top <= bottom:                     # guard for odd dimensions
            for j in range(right, left - 1, -1):
                result.append(matrix[bottom][j])  # bottom row
            bottom -= 1
        if left <= right:                     # guard for odd dimensions
            for i in range(bottom, top - 1, -1):
                result.append(matrix[i][left])    # left column
            left += 1
    return result
```

```rust
impl Solution {
    /// @param matrix the m x n matrix to read in spiral order
    /// @return       all elements of matrix in spiral order
    pub fn spiral_order(matrix: Vec<Vec<i32>>) -> Vec<i32> {
        let mut result = Vec::new();
        let (mut top, mut bottom) = (0usize, matrix.len() - 1);
        let (mut left, mut right) = (0usize, matrix[0].len() - 1);

        while top <= bottom && left <= right {
            for j in left..=right { result.push(matrix[top][j]); }
            top += 1;
            for i in top..=bottom { result.push(matrix[i][right]); }
            if right == 0 { break; }
            right -= 1;
            if top <= bottom {
                for j in (left..=right).rev() { result.push(matrix[bottom][j]); }
                if bottom == 0 { break; }
                bottom -= 1;
            }
            if left <= right {
                for i in (top..=bottom).rev() { result.push(matrix[i][left]); }
                left += 1;
            }
        }
        result
    }
}
```

> **Rust note:** `usize` can't underflow, so the `right == 0` / `bottom == 0` guards replace the implicit `-1` sentinels of the other languages; breaking out is equivalent to "no more layers."

## Dry run

**Input:** `matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]`

```
top=0 bottom=2 left=0 right=2
  top row:       1, 2, 3        -> top=1
  right column:  6, 9           -> right=1
  bottom row:    8, 7           -> bottom=1
  left column:   4              -> left=1
top=1 bottom=1 left=1 right=1
  top row:       5              -> top=2
  right column:  (top=2 > bottom=1, empty) -> right=0
  guard top<=bottom? 2<=1 NO -> skip
  guard left<=right? 1<=0 NO -> skip
  loop ends (top=2 > bottom=1)
Result: [1, 2, 3, 6, 9, 8, 7, 4, 5] ✓
```

The guards are load-bearing at the last layer: after the center `5` is read by the top-row pass, both remaining walks are *empty* (the single middle cell was already consumed). Without `if (top <= bottom)` and `if (left <= right)`, the bottom-row pass would re-add the center — or worse, go out of bounds on a 1×N matrix.

**Odd-shape check:** `matrix = [[1, 2, 3, 4]]` (1×4):

```
top=0 bottom=0 left=0 right=3
  top row: 1, 2, 3, 4 -> top=1
  right column: (top=1 > bottom=0, empty) -> right=2
  guard top<=bottom? 1<=0 NO -> skip bottom row ✓
  guard left<=right? 0<=2 YES -> left column: (bottom=0, top=1 -> empty) -> left=1
  loop ends
Result: [1, 2, 3, 4] ✓   (no double-read, no crash)
```

## Complexity

**Time.** Every cell is visited exactly once:

$$
T(m, n) = O(mn)
$$

**Space.** $O(1)$ auxiliary (plus the $O(mn)$ output).

## Variants & follow-ups

- **Spiral Matrix II** (`src/main/kotlin/array/SpiralMatrix_II.kt`) — the *generation* twin: fill an `n × n` matrix with `1..n²` in spiral order. Identical boundary peeling, writes instead of reads.
- **Diagonal Traverse** (`src/main/kotlin/array/DiagonalTraverse.kt`) — another "walk the matrix in a fixed pattern" problem; boundary checks are the whole game.
- **Interview follow-up:** "Why can't we skip the guards when `m == n`?" For square matrices the guards never fire (each pass consumes a full layer), but for rectangles they're mandatory. Saying that distinction unprompted shows you understand *why* the guards exist.
- **Interview follow-up:** "Prove every element is read once." The boundaries partition the matrix into disjoint rings; each ring is read once (four walks, no overlap after the guards), and the outer loop runs exactly once per ring.
