# 12.13 N-Queens II

> **Source**: [`src/main/kotlin/backtracking/NQueen_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/backtracking/NQueen_II.kt)
> **Pattern**: count-only backtracking · **Core page**

## The Problem

The **number** of distinct N-Queens solutions.

- Constraints: n ≤ 9.

## Examples

```
Input:  n = 4   -> Output: 2
```

## Intuition — the [12.3](n-queens.md) backtrack, counting instead of collecting

Same placement checks (column + diagonals), but the base case increments a counter instead of building a board:

```kotlin
val placed = IntArray(n) { -1 }    // placed[row] = column
var solutionCount = 0

fun backtrack(row: Int) {
    if (row == n) { solutionCount++; return }

    for (col in 0 until n) {
        if (isSafe(row, col)) {
            placed[row] = col
            backtrack(row + 1)
            placed[row] = -1
        }
    }
}
```

**Why the column array instead of a board?** Only the queen's column per row matters for the attack checks — `placed[row]` and the diagonal tests `|placed[r] - col| == row - r`. The [12.3](n-queens.md) engine with a leaner state.

## Approach 1 — Collect-then-count (board list size)

Run N-Queens I, return `solutions.size`: correct, wasteful.

## Approach 2 — Count-on-base-case (the repo's version, optimal)

```kotlin
class NQueen_II {
    /**
     * @param n board size
     * @return  number of solutions
     */
    fun totalNQueens(n: Int): Int {
        val placed = IntArray(n) { -1 }
        var solutionCount = 0

        fun isSafe(row: Int, col: Int): Boolean {
            for (r in 0 until row) {
                if (placed[r] == col ||
                    abs(placed[r] - col) == row - r) return false
            }
            return true
        }

        fun backtrack(row: Int) {
            if (row == n) {
                solutionCount++
                return
            }

            for (col in 0 until n) {
                if (isSafe(row, col)) {
                    placed[row] = col
                    backtrack(row + 1)
                    placed[row] = -1
                }
            }
        }

        backtrack(0)
        return solutionCount
    }
}
```

```java
public class NQueensII {
    private int count = 0;

    private boolean safe(int[] placed, int row, int col) {
        for (int r = 0; r < row; r++) {
            if (placed[r] == col || Math.abs(placed[r] - col) == row - r) return false;
        }
        return true;
    }

    private void backtrack(int[] placed, int n, int row) {
        if (row == n) { count++; return; }

        for (int col = 0; col < n; col++) {
            if (safe(placed, row, col)) {
                placed[row] = col;
                backtrack(placed, n, row + 1);
            }
        }
    }

    /**
     * @param n board size
     * @return  number of solutions
     */
    public int totalNQueens(int n) {
        count = 0;
        backtrack(new int[n], n, 0);
        return count;
    }
}
```

```cpp
#include <vector>
#include <cmath>

class NQueensII {
    int count = 0;

    bool safe(std::vector<int>& placed, int row, int col) {
        for (int r = 0; r < row; r++) {
            if (placed[r] == col || std::abs(placed[r] - col) == row - r) return false;
        }
        return true;
    }

    void backtrack(std::vector<int>& placed, int n, int row) {
        if (row == n) { count++; return; }

        for (int col = 0; col < n; col++) {
            if (safe(placed, row, col)) {
                placed[row] = col;
                backtrack(placed, n, row + 1);
            }
        }
    }

public:
    /**
     * @param n board size
     * @return  number of solutions
     */
    int totalNQueens(int n) {
        count = 0;
        std::vector<int> placed(n, -1);
        backtrack(placed, n, 0);
        return count;
    }
};
```

```python
def total_n_queens(n: int) -> int:
    """
    @param n: board size
    @return:  number of solutions
    """
    placed = [-1] * n
    count = 0

    def safe(row: int, col: int) -> bool:
        for r in range(row):
            if placed[r] == col or abs(placed[r] - col) == row - r:
                return False
        return True

    def backtrack(row: int) -> None:
        nonlocal count
        if row == n:
            count += 1
            return

        for col in range(n):
            if safe(row, col):
                placed[row] = col
                backtrack(row + 1)
                placed[row] = -1

    backtrack(0)
    return count
```

```rust
impl Solution {
    /// @param n board size
    /// @return  number of solutions
    pub fn total_n_queens(n: i32) -> i32 {
        let n = n as usize;
        let mut placed = vec![-1i32; n];
        let mut count = 0;

        fn safe(placed: &Vec<i32>, row: usize, col: i32) -> bool {
            for r in 0..row {
                if placed[r] == col || (placed[r] - col).abs() == (row - r) as i32 {
                    return false;
                }
            }
            true
        }

        fn backtrack(placed: &mut Vec<i32>, n: usize, row: usize, count: &mut i32) {
            if row == n { *count += 1; return; }

            for col in 0..n as i32 {
                if safe(placed, row, col) {
                    placed[row] = col;
                    backtrack(placed, n, row + 1, count);
                }
            }
        }

        backtrack(&mut placed, n, 0, &mut count);
        count
    }
}
```

## Dry run

**Input:** `n = 4`.

```
row 0: cols 0..3.  try 1 (safe).  row 1: col 3 safe (1,3) -> row 2: col 0? attacks (1,3) diag?
  (0,1) col 0 attacks? placed[0]=1, col 0: |1-0|=1 == row 2-0? no.  placed[1]=3 col 0: no.
  diag (1,3): |3-0|=3 == 1? no.  so col 0 safe at row 2? (0,1) diag: |1-0| = 1, row diff 2 -> no.
  ok (2,0).  row 3: no col safe -> backtrack...
  (known: n=4 has exactly 2 solutions) -> count reaches 2 ✓
```

## Complexity

**Time.** n! pruning:

$$
T(n) = O(n!)
$$

**Space.** The column array:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **N-Queens** ([12.3](n-queens.md)) — the board-building sibling.
- **Interview follow-up:** "Why no board in the state?" The row index is implicit (recursion depth); `placed[r]` gives each queen's column; the diagonal test is arithmetic. The board is pure output, not state.
