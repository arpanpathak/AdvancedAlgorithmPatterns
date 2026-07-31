# 12.4 N-Queens

> **Source:** [`src/main/kotlin/backtracking/NQueen.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/backtracking/NQueen.kt)
> **Pattern:** row-by-row placement · **Core page**

## The Problem

Place `n` queens on an `n x n` chessboard so that **no two attack each other** (no shared row, column, or diagonal). Return all distinct configurations.

- Constraints: $1 \le n \le 9$.

## Examples

```
Input:  n = 4
Output: [[".Q..","...Q","Q...","..Q."],
         ["..Q.","Q...","...Q",".Q.."]]   (two solutions)

Input:  n = 1
Output: [["Q"]]
```

## Intuition — one queen per row, placed with a diagonal check

Since two queens can never share a row, the placement is forced into a per-row decision: **for row 0..n-1, choose a column for that row's queen.** The backtracking state is `placed[row] = col` — a single `IntArray` instead of a board.

At row `row`, trying column `col` is legal iff:

1. no earlier queen is in column `col` (`placed[prev] != col`);
2. no earlier queen is on either diagonal: `|prevRow - row| == |prevCol - col|`.

Both are $O(n)$ checks against the `placed` array — no board needed. This is the **pruning**: the attack check runs *before* placing, so branches that can't lead to a solution die at the earliest row.

**Why row-by-row?** The "no two in a row" rule makes rows a natural recursion axis; the column + diagonal checks then guarantee the remaining rules. It also makes the state *small*: `placed` is length `n`, not `n²`.

**Why `Math.abs` on both sides?** Two squares share a diagonal iff the row difference equals the column difference in absolute value — the "slope is ±1" condition. The `abs` folds both diagonals into one comparison.

**The board is only materialized at the leaf** — the repo builds the `List<String>` board when `row == n`, reading `placed` back. Classic "keep the cheap state during search, format at the end" design.

## Approach 1 — Permutation + filter

Every solution is a permutation of columns; enumerate all $n!$ and filter attacks: $O(n! \cdot n)$ — wasteful, since most permutations are eliminated early.

## Approach 2 — Row-by-row with attack pruning (the repo's version, optimal)

```kotlin
class NQueen {
    private lateinit var placed: IntArray          // placed[row] = column of the queen in row

    /**
     * @param n board size
     * @return  all n-queens configurations as string boards
     */
    fun solveNQueens(n: Int): List<List<String>> {
        val results = mutableListOf<List<String>>()
        placed = IntArray(n) { -1 }                // -1 = no queen placed in this row yet

        fun dfs(row: Int) {
            if (row == n) {                        // all rows placed: a complete solution
                val board = List(n) { CharArray(n) { '.' } }
                for (r in placed.indices) {
                    board[r][placed[r]] = 'Q'
                }
                results.add(board.map { it.joinToString("") })
                return
            }

            for (col in 0 until n) {
                if (isSafe(row, col)) {
                    placed[row] = col              // choose
                    dfs(row + 1)                   // explore
                    placed[row] = -1               // undo
                }
            }
        }

        dfs(0)
        return results
    }

    private fun isSafe(row: Int, col: Int): Boolean {
        for (prevRow in 0 until row) {
            val prevCol = placed[prevRow]
            if (prevCol == col ||                // same column
                Math.abs(prevRow - row) == Math.abs(prevCol - col)   // same diagonal
            ) {
                return false
            }
        }
        return true
    }
}
```

```java
import java.util.*;

public class NQueens {
    private int[] placed;                            // placed[row] = column

    /**
     * @param n board size
     * @return  all n-queens configurations as string boards
     */
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> results = new ArrayList<>();
        placed = new int[n];
        Arrays.fill(placed, -1);
        dfs(0, n, results);
        return results;
    }

    private void dfs(int row, int n, List<List<String>> results) {
        if (row == n) {                              // all rows placed: a complete solution
            char[][] board = new char[n][n];
            for (char[] r : board) Arrays.fill(r, '.');
            for (int r = 0; r < n; r++) board[r][placed[r]] = 'Q';

            List<String> config = new ArrayList<>();
            for (char[] r : board) config.add(new String(r));
            results.add(config);
            return;
        }

        for (int col = 0; col < n; col++) {
            if (isSafe(row, col)) {
                placed[row] = col;                   // choose
                dfs(row + 1, n, results);            // explore
                placed[row] = -1;                    // undo
            }
        }
    }

    private boolean isSafe(int row, int col) {
        for (int prevRow = 0; prevRow < row; prevRow++) {
            if (placed[prevRow] == col ||
                Math.abs(prevRow - row) == Math.abs(placed[prevRow] - col)) {
                return false;
            }
        }
        return true;
    }
}
```

```cpp
#include <cmath>
#include <string>
#include <vector>

class NQueens {
    std::vector<int> placed;                         // placed[row] = column

    bool isSafe(int row, int col) {
        for (int prev = 0; prev < row; prev++) {
            if (placed[prev] == col ||
                std::abs(prev - row) == std::abs(placed[prev] - col)) {
                return false;
            }
        }
        return true;
    }

    void dfs(int row, int n, std::vector<std::vector<std::string>>& results) {
        if (row == n) {                              // all rows placed: a complete solution
            std::vector<std::string> board(n, std::string(n, '.'));
            for (int r = 0; r < n; r++) board[r][placed[r]] = 'Q';
            results.push_back(board);
            return;
        }

        for (int col = 0; col < n; col++) {
            if (isSafe(row, col)) {
                placed[row] = col;                   // choose
                dfs(row + 1, n, results);            // explore
                placed[row] = -1;                    // undo
            }
        }
    }

public:
    /**
     * @param n board size
     * @return  all n-queens configurations as string boards
     */
    std::vector<std::vector<std::string>> solveNQueens(int n) {
        placed.assign(n, -1);
        std::vector<std::vector<std::string>> results;
        dfs(0, n, results);
        return results;
    }
};
```

```python
def solve_n_queens(n: int) -> list[list[str]]:
    """
    @param n: board size
    @return:  all n-queens configurations as string boards
    """
    placed = [-1] * n                        # placed[row] = column
    results = []

    def is_safe(row: int, col: int) -> bool:
        for prev in range(row):
            if placed[prev] == col or abs(prev - row) == abs(placed[prev] - col):
                return False
        return True

    def dfs(row: int) -> None:
        if row == n:                         # all rows placed: a complete solution
            board = ["." * col + "Q" + "." * (n - col - 1) for col in placed]
            results.append(board)
            return

        for col in range(n):
            if is_safe(row, col):
                placed[row] = col            # choose
                dfs(row + 1)                 # explore
                placed[row] = -1             # undo

    dfs(0)
    return results
```

```rust
impl Solution {
    /// @param n board size
    /// @return  all n-queens configurations as string boards
    pub fn solve_n_queens(n: i32) -> Vec<Vec<String>> {
        let n = n as usize;
        let mut placed = vec![usize::MAX; n];      // placed[row] = column
        let mut results = Vec::new();

        fn is_safe(row: usize, col: usize, placed: &Vec<usize>) -> bool {
            for prev in 0..row {
                if placed[prev] == col || prev.abs_diff(row) == placed[prev].abs_diff(col) {
                    return false;
                }
            }
            true
        }

        fn dfs(row: usize, n: usize, placed: &mut Vec<usize>, results: &mut Vec<Vec<String>>) {
            if row == n {                            // all rows placed: a complete solution
                let board: Vec<String> = placed.iter()
                    .map(|&c| {
                        let mut s = vec!['.'; n];
                        s[c] = 'Q';
                        s.into_iter().collect()
                    })
                    .collect();
                results.push(board);
                return;
            }

            for col in 0..n {
                if is_safe(row, col, placed) {
                    placed[row] = col;               // choose
                    dfs(row + 1, n, placed, results);  // explore
                    placed[row] = usize::MAX;        // undo
                }
            }
        }

        dfs(0, n, &mut placed, &mut results);
        results
    }
}
```

## Dry run

**Input:** `n = 4`.

```
placed = [-1,-1,-1,-1]
dfs(0):
  col 0: isSafe(0,0) true -> placed=[0,-1,-1,-1]
    dfs(1):
      col 0: same column as row 0 -> no.  col 1: |0-1|==|0-1| diagonal -> no.
      col 2: safe -> placed=[0,2,-1,-1]
        dfs(2):
          col 0: column/diagonal vs row0 -> no.  col 1: diagonal with row1 -> no.
          col 2: column -> no.  col 3: safe -> placed=[0,2,3,-1]
            dfs(3):
              col 0..2: attacked.  col 3: column -> no.  no safe column -> return
            undo -> placed=[0,2,-1,-1]
          ...
          no safe column at row 2 either (with col 2 at row 1) -> return
      col 3: safe -> placed=[0,3,-1,-1]
        dfs(2):
          col 0: |1-2| vs |3-0|? 1 vs 3 no; column 0 vs 3? no.  diagonal with row1? |1-2|=1, |3-0|=3 no.
                 wait: prev row 1 has col 3; row 2 col 0: |1-2|=1, |3-0|=3 -> not same diagonal.  col 0 != 3.
                 vs row 0 col 0: same column -> ATTACK.  no.
          col 1: vs row0 col0: |0-2|=2, |0-1|=1 no; column 1 vs 0 no.  vs row1 col3: |1-2|=1, |3-1|=2 no; column no.
                 safe -> placed=[0,3,1,-1]
            dfs(3):
              col 0: column vs row0 -> no.  col 1: column vs row2 -> no.
              col 2: vs row1 col3: |1-3|=2, |3-2|=1 no; vs row2 col1: |2-3|=1, |1-2|=1 -> DIAGONAL -> no.
              col 3: column vs row1 -> no.  no safe -> return
          col 2: vs row0 col0: |0-2|=2,|0-2|=2 -> diagonal -> no.
          col 3: column vs row1 -> no.
          ... dead end
  col 1: isSafe(0,1) true -> ... (the mirror configuration)
  col 2, col 3: (the other two solutions' starts)
Total: 2 solutions for n=4 ✓
```

The trace shows the pruning cascade: dead ends (like `[0,2,...]`) die within two rows of the conflict, never exploring the full depth. The `placed[row] = -1` undos let each row try its next column from a clean slate.

## Complexity

**Time.** $O(n!)$ placements worst case (each row has at most $n$ legal columns, pruning kills most):

$$
T(n) = O(n!) \quad \text{effectively far less}
$$

**Space.** The placed array plus output:

$$
S(n) = O(n \cdot n!)
$$

## Variants & follow-ups

- **N-Queens II** (`src/main/kotlin/backtracking/NQueen_II.kt`) — *count* the solutions instead of listing: the same tree, no board formatting at the leaves.
- **N-Queens Optimized** (`src/main/kotlin/backtracking/NQueenOptimized.kt`) — the `isSafe` scan replaced by **column/diagonal/anti-diagonal boolean arrays**: $O(1)$ per check instead of $O(n)$.
- **Sudoku Solver** ([12.7](sudoku-solver.md)) — the same placement+prune skeleton with a 9x9 board and a validity check per digit.
- **Interview follow-up:** "Why is one queen per row without loss of generality?" Two queens can never share a row (they'd attack). So *any* valid board has exactly one queen per row, and the search space is exactly the choice of column per row — $n^n$ down to a much smaller pruned tree. State this before coding; it's the reduction that makes the problem backtracking-shaped.
