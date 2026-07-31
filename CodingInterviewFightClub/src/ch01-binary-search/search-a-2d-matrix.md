# 1.15 Search A 2D Matrix

> **Source:** [`src/main/kotlin/binarysearch/SearchA2dMatrix.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/binarysearch/SearchA2dMatrix.kt)
> **Pattern:** index unrolling · **Core page**

## The Problem

Given an $m \times n$ matrix where **each row is sorted left-to-right** and **the first element of each row is greater than the last element of the previous row**, determine whether a `target` is present.

The two properties together mean the whole matrix, read row by row, is one **globally sorted** sequence of length $mn$.

- Constraints: $1 \le m, n \le 100$.

## Examples

```
matrix = [
  [1,  3,  5,  7],
  [10, 11, 16, 20],
  [23, 30, 34, 60]
]
target = 3   -> true
target = 13  -> false
target = 60  -> true
```

## Intuition — flatten the matrix without flattening it

The killer observation: the matrix is **one sorted array with a $n$-wide stride**. If you number the cells $0..mn-1$ in row-major order, then

$$
\text{row} = \lfloor idx / n \rfloor, \qquad \text{col} = idx \bmod n
$$

and the sequence `matrix[row][col]` is non-decreasing. So the whole thing is a **plain binary search** (Template B, exact match) over the *virtual* 1D range $[0, mn-1]$, with the array access replaced by the two conversions above.

The unrolling identity is the entire trick: $\lfloor idx/n \rfloor$ and $idx \bmod n$ are just the quotient and remainder of dividing the flat index by the row width — the standard "row-major" memory layout that every 2D array uses under the hood.

## Approach 1 — Search each row

Binary search each row: $O(m \log n)$. Works, but ignores the cross-row ordering — the matrix is *one* sorted sequence, so a single binary search over all $mn$ cells is strictly better.

## Approach 2 — Single binary search on the flattened index (optimal)

```kotlin
/**
 * @param matrix the m x n matrix, rows sorted and first-of-row > last-of-previous-row
 * @param target the value to find
 * @return       true iff target is present in the matrix
 */
fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
    if (matrix.isEmpty() || matrix[0].isEmpty()) return false

    val (m, n) = matrix.size to matrix[0].size
    var (left, right) = 0 to m * n - 1

    while (left <= right) {
        val mid = left + (right - left) / 2
        val midValue = matrix[mid / n][mid % n]   // flatten: row = idx / n, col = idx % n

        when {
            midValue == target -> return true
            midValue < target  -> left = mid + 1
            else               -> right = mid - 1
        }
    }
    return false
}
```

```java
public class SearchA2dMatrix {
    /**
     * @param matrix the m x n matrix, rows sorted and first-of-row > last-of-previous-row
     * @param target the value to find
     * @return       true iff target is present in the matrix
     */
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix.length == 0 || matrix[0].length == 0) return false;
        int m = matrix.length, n = matrix[0].length;
        int left = 0, right = m * n - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int value = matrix[mid / n][mid % n];   // row-major unrolling
            if (value == target) return true;
            if (value < target) left = mid + 1;
            else right = mid - 1;
        }
        return false;
    }
}
```

```cpp
#include <vector>

class SearchA2dMatrix {
public:
    /**
     * @param matrix the m x n matrix, rows sorted and first-of-row > last-of-previous-row
     * @param target the value to find
     * @return       true iff target is present in the matrix
     */
    bool searchMatrix(const std::vector<std::vector<int>>& matrix, int target) {
        if (matrix.empty() || matrix[0].empty()) return false;
        int m = (int)matrix.size(), n = (int)matrix[0].size();
        int left = 0, right = m * n - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int value = matrix[mid / n][mid % n];
            if (value == target) return true;
            if (value < target) left = mid + 1;
            else right = mid - 1;
        }
        return false;
    }
};
```

```python
def search_matrix(matrix: list[list[int]], target: int) -> bool:
    """
    @param matrix: the m x n matrix, rows sorted and first-of-row > last-of-previous-row
    @param target: the value to find
    @return:       True iff target is present in the matrix
    """
    if not matrix or not matrix[0]:
        return False
    m, n = len(matrix), len(matrix[0])
    left, right = 0, m * n - 1

    while left <= right:
        mid = left + (right - left) // 2
        value = matrix[mid // n][mid % n]      # row-major unrolling
        if value == target:
            return True
        if value < target:
            left = mid + 1
        else:
            right = mid - 1
    return False
```

```rust
impl Solution {
    /// @param matrix the m x n matrix, rows sorted and first-of-row > last-of-previous-row
    /// @param target the value to find
    /// @return       true iff target is present in the matrix
    pub fn search_matrix(matrix: Vec<Vec<i32>>, target: i32) -> bool {
        if matrix.is_empty() || matrix[0].is_empty() {
            return false;
        }
        let (m, n) = (matrix.len(), matrix[0].len());
        let (mut left, mut right) = (0usize, m * n - 1);

        while left <= right {
            let mid = left + (right - left) / 2;
            let value = matrix[mid / n][mid % n];
            if value == target {
                return true;
            }
            if value < target {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        false
    }
}
```

## Dry run

**Input:** `matrix` as above, `target = 3`. Flattened search space $[0, 11]$:

```
left=0  right=11  mid=5   idx 5 -> row 1, col 1 -> 11    11 > 3 -> right=4
left=0  right=4   mid=2   idx 2 -> row 0, col 2 -> 5     5  > 3 -> right=1
left=0  right=1   mid=0   idx 0 -> row 0, col 0 -> 1     1  < 3 -> left=1
left=1  right=1   mid=1   idx 1 -> row 0, col 1 -> 3     3 == 3 -> return true ✓
```

The unrolling is easy to verify by hand:

| flat idx | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| row (idx/4) | 0 | 0 | 0 | 0 | 1 | 1 | 1 | 1 | 2 | 2 | 2 | 2 |
| col (idx%4) | 0 | 1 | 2 | 3 | 0 | 1 | 2 | 3 | 0 | 1 | 2 | 3 |
| value | 1 | 3 | 5 | 7 | 10 | 11 | 16 | 20 | 23 | 30 | 34 | 60 |

The `value` row is strictly increasing — it's a 1D sorted array wearing a 2D costume, and the binary search never notices.

## Complexity

**Time.** The virtual array has $mn$ cells:

$$
T(m, n) = O(\log(mn))
$$

**Space.** $O(1)$.

## Variants & follow-ups

- **Search a 2D Matrix II** (classic sibling) — rows *and* columns are sorted independently, but the cross-row property is gone. Binary search over the whole thing fails; the $O(m + n)$ "staircase search" from the top-right corner is the canonical answer.
- **Kth Smallest Element in a Sorted Matrix** — uses this unrolling idea *and* a binary-search-on-answer over the value range.
- **Interview follow-up:** "What if rows are sorted but the first-of-row property doesn't hold?" The global sortedness collapses; you'd fall back to row-by-row binary search ($O(m \log n)$) or the staircase walk ($O(m + n)$).
