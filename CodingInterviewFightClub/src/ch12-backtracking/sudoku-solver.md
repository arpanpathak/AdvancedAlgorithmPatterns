# 12.7 Sudoku Solver

> **Source:** [`src/main/kotlin/backtracking/SudokuSolver.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/backtracking/SudokuSolver.kt)
> **Pattern:** cell-by-cell with validity · **Core page**

## The Problem

Fill the empty cells (`'.'`) of a 9x9 Sudoku board so that every row, column, and 3x3 box contains the digits 1-9 exactly once. The input is guaranteed to have exactly one solution; solve it in place.

- Constraints: fixed 9x9 board; exactly one solution.

## Examples

```
Input:  board = [["5","3",".",".","7",".",".",".","."],
                 ["6",".",".","1","9","5",".",".","."],
                 ...]                     (the classic puzzle)
Output: the completed board (in place)
```

## Intuition — find the next empty cell, try every digit that fits

The search space is "assign digits to empty cells," and the pruning is the Sudoku validity check from [10.4](../ch10-hash-tables/valid-sudoku.md). The recursion:

```
solve():
    find the next empty cell (row, col)          # scan row-major
    if none: return true                          # board complete
    for ch in '1'..'9':
        if isValid(board, row, col, ch):          # not in row/col/box
            board[row][col] = ch                  # choose
            if solve(): return true               # explore — this digit led to a solution
            board[row][col] = '.'                 # undo (the backtrack)
    return false                                  # no digit works here: dead end
```

**Why return-early and not collect?** The problem asks for *one* solution (guaranteed unique), so the recursion returns `true` the moment a complete assignment is found — the success signal propagates up the call stack, and the board is already mutated to the answer. This is backtracking in its "find one" mode, versus the "enumerate all" mode of [12.1](subsets.md).

**The `isValid` check is the entire pruning.** For a candidate digit at `(row, col)`: scan row, column, and the 3x3 box for a conflict. The box coordinates are the trickiest arithmetic on the page:

```
boxRow = 3 * (row / 3) + i / 3
boxCol = 3 * (col / 3) + i % 3
```

— iterating `i in 0..8` walks the box's 9 cells. (The same indexing as [10.4](../ch10-hash-tables/valid-sudoku.md), turned into a query.)

**Why is the backtracking undo critical here?** A digit that fits *locally* may still lead to a dead end later. `board[row][col] = '.'` restores the cell so the next digit (or the caller's alternative) starts clean — without it, the board accumulates poisoned assignments and the search fails.

## Approach 1 — Brute force all $9^{81}$ boards (impossible)

No pruning: astronomical. The validity check is what makes the search tractable.

## Approach 2 — Backtracking with row/col/box validity (the repo's version, optimal)

```kotlin
class SudokuSolver {
    /**
     * @param board 9x9 Sudoku board, '.' for empty cells; solved in place
     */
    fun solveSudoku(board: Array<CharArray>) {
        solve(board)
    }

    private fun solve(board: Array<CharArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board[row][col] == '.') {                // an empty cell
                    for (ch in '1'..'9') {
                        if (isValid(board, row, col, ch)) {
                            board[row][col] = ch             // choose
                            if (solve(board)) return true    // this digit led to a solution
                            board[row][col] = '.'            // undo (the backtrack)
                        }
                    }
                    return false                             // no digit works here: dead end
                }
            }
        }
        return true                                          // no empty cells: solved
    }

    private fun isValid(board: Array<CharArray>, row: Int, col: Int, ch: Char): Boolean {
        for (i in 0..8) {
            if (board[row][i] == ch || board[i][col] == ch) return false     // row & column
            val boxRow = 3 * (row / 3) + i / 3               // walk the 3x3 box
            val boxCol = 3 * (col / 3) + i % 3
            if (board[boxRow][boxCol] == ch) return false
        }
        return true
    }
}
```

```java
public class SudokuSolver {
    /**
     * @param board 9x9 Sudoku board, '.' for empty cells; solved in place
     */
    public void solveSudoku(char[][] board) {
        solve(board);
    }

    private boolean solve(char[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == '.') {                // an empty cell
                    for (char ch = '1'; ch <= '9'; ch++) {
                        if (isValid(board, row, col, ch)) {
                            board[row][col] = ch;            // choose
                            if (solve(board)) return true;   // this digit led to a solution
                            board[row][col] = '.';           // undo (the backtrack)
                        }
                    }
                    return false;                            // no digit works here: dead end
                }
            }
        }
        return true;                                         // no empty cells: solved
    }

    private boolean isValid(char[][] board, int row, int col, char ch) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == ch || board[i][col] == ch) return false;   // row & column
            int boxRow = 3 * (row / 3) + i / 3;              // walk the 3x3 box
            int boxCol = 3 * (col / 3) + i % 3;
            if (board[boxRow][boxCol] == ch) return false;
        }
        return true;
    }
}
```

```cpp
#include <vector>

class SudokuSolver {
    bool isValid(std::vector<std::vector<char>>& board, int row, int col, char ch) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == ch || board[i][col] == ch) return false;   // row & column
            int boxRow = 3 * (row / 3) + i / 3;              // walk the 3x3 box
            int boxCol = 3 * (col / 3) + i % 3;
            if (board[boxRow][boxCol] == ch) return false;
        }
        return true;
    }

    bool solve(std::vector<std::vector<char>>& board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == '.') {                // an empty cell
                    for (char ch = '1'; ch <= '9'; ch++) {
                        if (isValid(board, row, col, ch)) {
                            board[row][col] = ch;            // choose
                            if (solve(board)) return true;   // this digit led to a solution
                            board[row][col] = '.';           // undo (the backtrack)
                        }
                    }
                    return false;                            // no digit works here: dead end
                }
            }
        }
        return true;                                         // no empty cells: solved
    }

public:
    /**
     * @param board 9x9 Sudoku board, '.' for empty cells; solved in place
     */
    void solveSudoku(std::vector<std::vector<char>>& board) {
        solve(board);
    }
};
```

```python
def solve_sudoku(board: list[list[str]]) -> None:
    """
    @param board: 9x9 Sudoku board, '.' for empty cells; solved in place
    """
    def is_valid(row: int, col: int, ch: str) -> bool:
        for i in range(9):
            if board[row][i] == ch or board[i][col] == ch:
                return False                       # row & column
            br, bc = 3 * (row // 3) + i // 3, 3 * (col // 3) + i % 3
            if board[br][bc] == ch:
                return False                       # the 3x3 box
        return True

    def solve() -> bool:
        for row in range(9):
            for col in range(9):
                if board[row][col] == ".":         # an empty cell
                    for ch in "123456789":
                        if is_valid(row, col, ch):
                            board[row][col] = ch   # choose
                            if solve():
                                return True        # this digit led to a solution
                            board[row][col] = "."  # undo (the backtrack)
                    return False                   # no digit works here: dead end
        return True                                # no empty cells: solved

    solve()
```

```rust
impl Solution {
    /// @param board 9x9 Sudoku board, '.' for empty cells; solved in place
    pub fn solve_sudoku(board: &mut Vec<Vec<char>>) {
        fn is_valid(board: &Vec<Vec<char>>, row: usize, col: usize, ch: char) -> bool {
            for i in 0..9 {
                if board[row][i] == ch || board[i][col] == ch { return false; }  // row & column
                let (br, bc) = (3 * (row / 3) + i / 3, 3 * (col / 3) + i % 3);
                if board[br][bc] == ch { return false; }          // the 3x3 box
            }
            true
        }

        fn solve(board: &mut Vec<Vec<char>>) -> bool {
            for row in 0..9 {
                for col in 0..9 {
                    if board[row][col] == '.' {                   // an empty cell
                        for ch in '1'..='9' {
                            if is_valid(board, row, col, ch) {
                                board[row][col] = ch;             // choose
                                if solve(board) { return true; }  // this digit led to a solution
                                board[row][col] = '.';            // undo (the backtrack)
                            }
                        }
                        return false;                             // no digit works here: dead end
                    }
                }
            }
            true                                                  // no empty cells: solved
        }

        solve(board);
    }
}
```

## Dry run

**Input:** the classic puzzle's first three cells: `5 3 . / 6 . . / . 9 8`.

```
solve():
  (0,0)='5' filled. (0,1)='3' filled. (0,2)='.':
    try '1': row has 5,3 no; col: board[0..8][2] = . . . no 1; box (0..2,0..2) = 5,3,. no 1.
             valid -> place '1'.  recurse:
      (0,3)='7' ... fills a few more, then:
      (2,0)='.': try '1': column 0 has 5,6 -> no 1... but wait, box (0..2,0..2) now has '1' at (0,2)!
                 -> is '1' valid at (2,0)? column 0: 5,6 -> no conflict; box contains '1' at (0,2) -> INVALID.
             try '2': ... eventually one digit works.
      ...
      eventually a later cell finds NO valid digit -> return false up the stack:
      (0,2) receives false -> undo: place '.', try '2':
             valid -> place '2'.  recurse... (the real solution path)
  ...
  (8,8) filled -> no empty cells -> return true (propagates up)
```

The critical moment: the digit `'1'` at `(0,2)` *looks* locally valid, leads the search into a wall, gets **undone**, and `'2'` takes its place. That undo is the difference between this working and silently failing.

## Complexity

**Time.** Bounded by $9^{k}$ (k empty cells) worst case; the validity pruning makes real puzzles fast:

$$
T(k) = O(9^k) \text{ worst case, far less in practice}
$$

**Space.** Recursion depth (≤ 81) plus the board:

$$
S = O(81) = O(1)
$$

## Variants & follow-ups

- **Sudoku Solver (set-based)** (`src/main/kotlin/backtracking/SudokuSolverSet.kt`) — precompute row/col/box *sets* (the [10.4](../ch10-hash-tables/valid-sudoku.md) layout) and prune candidates against them: `isValid` becomes O(1) membership, at the cost of maintaining the sets during undo.
- **Valid Sudoku** ([10.4](../ch10-hash-tables/valid-sudoku.md)) — the checker this solver calls per candidate; the two pages are the same data, two directions.
- **N-Queens** ([12.4](n-queens.md)) — the same placement+prune skeleton with an $n \times n$ board and `isSafe` instead of `isValid`.
- **Interview follow-up:** "Why return early instead of continuing to enumerate?" The problem guarantees a single solution, so the first complete assignment *is* the answer. The `return true` chain is the success signal — each frame restores nothing on the success path (the board is already the solution) and only undoes on the failure path. That asymmetry is the "find one" flavor of backtracking.
