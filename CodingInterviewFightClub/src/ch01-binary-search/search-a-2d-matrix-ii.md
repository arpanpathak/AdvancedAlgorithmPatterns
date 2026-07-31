# 1.21 Search A 2D Matrix II

> **Source**: [`src/main/kotlin/array/SearchA2dMatrix_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/SearchA2dMatrix_II.kt)
> **Pattern**: staircase search · **Core page**

## The Problem

Search a target in a matrix sorted **per row and per column** (not globally).

- Constraints: m, n ≤ 300.

## Examples

```
Input:  matrix = [[1,4,7,11,15],[2,5,8,12,19],[3,6,9,16,22],[10,13,14,17,24],[18,21,23,26,30]], target = 5
Output: true
```

## Intuition — start at the top-right; each step eliminates a row or column

At `(row, col)` (top-right), the cell is the row's max and the column's min. `matrix[r][c] > target` → the whole column is bigger → `col--`; `< target` → the whole row is smaller → `row++`:

```kotlin
var (row, col) = matrix.size - 1 to 0     // bottom-left works too

while (row >= 0 && col < matrix[0].size) {
    when {
        matrix[row][col] > target -> row--
        matrix[row][col] < target -> col++
        else -> return true
    }
}
return false
```

**Why one elimination per step?** The corner guarantees it: at the top-right, going left shrinks everything, going down grows everything — the binary-search "halve" becomes "eliminate a row or column". O(m+n) steps.

## Approach 1 — Binary search each row (O(m log n))

Correct, ignores the column ordering.

## Approach 2 — Staircase walk (the repo's version, optimal)

```kotlin
class SearchA2dMatrix_II {
    /**
     * @param matrix row-and-column sorted matrix
     * @param target search value
     * @return       true iff found
     */
    fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
        var (row, col) = matrix.size - 1 to 0

        while (row >= 0 && col < matrix[0].size) {
            when {
                matrix[row][col] > target -> row--
                matrix[row][col] < target -> col++
                else -> return true
            }
        }
        return false
    }
}
```

```java
public class SearchA2DMatrixII {
    /**
     * @param matrix row-and-column sorted matrix
     * @param target search value
     * @return       true iff found
     */
    public boolean searchMatrix(int[][] matrix, int target) {
        int row = matrix.length - 1, col = 0;

        while (row >= 0 && col < matrix[0].length) {
            if (matrix[row][col] == target) return true;
            else if (matrix[row][col] > target) row--;
            else col++;
        }
        return false;
    }
}
```

```cpp
#include <vector>

class SearchA2DMatrixII {
public:
    /**
     * @param matrix row-and-column sorted matrix
     * @param target search value
     * @return       true iff found
     */
    bool searchMatrix(std::vector<std::vector<int>>& matrix, int target) {
        int row = matrix.size() - 1, col = 0;

        while (row >= 0 && col < matrix[0].size()) {
            if (matrix[row][col] == target) return true;
            else if (matrix[row][col] > target) row--;
            else col++;
        }
        return false;
    }
};
```

```python
def search_matrix(matrix: list[list[int]], target: int) -> bool:
    """
    @param matrix: row-and-column sorted matrix
    @param target: search value
    @return:       true iff found
    """
    row, col = len(matrix) - 1, 0

    while row >= 0 and col < len(matrix[0]):
        if matrix[row][col] == target:
            return True
        elif matrix[row][col] > target:
            row -= 1
        else:
            col += 1

    return False
```

```rust
impl Solution {
    /// @param matrix row-and-column sorted matrix
    /// @param target search value
    /// @return       true iff found
    pub fn search_matrix(matrix: Vec<Vec<i32>>, target: i32) -> bool {
        let (mut row, mut col) = (matrix.len() as i32 - 1, 0);

        while row >= 0 && (col as usize) < matrix[0].len() {
            let cell = matrix[row as usize][col as usize];
            if cell == target { return true; }
            else if cell > target { row -= 1; }
            else { col += 1; }
        }
        false
    }
}
```

## Dry run

**Input:** the example, `target = 5`.

```
start (4,0)=18 > 5 -> row 3.  (3,0)=10 > 5 -> row 2.  (2,0)=3 < 5 -> col 1.
(2,1)=6 > 5 -> row 1.  (1,1)=5 == 5 -> true ✓
```

Each step shrinks the search space by a full row or column — the walk can't loop (row only decreases, col only increases).

## Complexity

**Time.** At most m+n steps:

$$
T(m, n) = O(m + n)
$$

**Space.** Constants:

$$
S(m, n) = O(1)
$$

## Variants & follow-ups

- **Search A 2D Matrix** ([1.5](search-a-2d-matrix.md)) — the globally-sorted version (true binary search).
- **Interview follow-up:** "Why the corner and not the center?" The corner has the row-max/column-min property that makes every comparison decisive — the center doesn't. The corner choice is the entire algorithm.
