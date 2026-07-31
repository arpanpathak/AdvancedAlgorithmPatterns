# 10.4 Valid Sudoku

> **Source:** [`src/main/kotlin/array/hashtable/ValidSudoku.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/ValidSudoku.kt)
> **Pattern:** per-row/col/box sets · **Core page**

## The Problem

Determine whether a `9 x 9` Sudoku board is **valid** — each row, each column, and each of the nine `3 x 3` sub-boxes contains the digits `1-9` with **no duplicates** (empty cells `'.'` are ignored; the board need not be solvable).

- Constraints: fixed `9 x 9` board.

## Examples

```
Input:  board = [["5","3",".",".","7",".",".",".","."],
                 ["6",".",".","1","9","5",".",".","."],
                 [".","9","8",".",".",".",".","6","."],
                 ["8",".",".",".","6",".",".",".","3"],
                 ["4",".",".","8",".","3",".",".","1"],
                 ["7",".",".",".","2",".",".",".","6"],
                 [".","6",".",".",".",".","2","8","."],
                 [".",".",".","4","1","9",".",".","5"],
                 [".",".",".",".","8",".",".","7","9"]]
Output: true

Input:  same board, but the first row is ["8","3",...]  (8 twice in column 0)
Output: false
```

## Intuition — one cell, three duplicate checks

Every filled cell belongs to exactly one row, one column, and one `3x3` sub-box. The rule "no duplicates in a row/column/box" becomes, per cell: *"is this digit already present in my row, my column, or my box?"* — three `Set.add` calls.

**The data layout:** nine row sets, nine column sets, and nine sub-box sets indexed by `(i/3, j/3)`. The classic off-by-one trap is the sub-box index: `i/3` and `j/3` both in `0..2` — that's the whole "which box am I in" question, and `board[i/3][j/3]` style mistakes are the classic bug. The repo indexes subgrids as a 2-D array of sets to make it explicit.

**Why `Set.add` returning `false` is the check:** `add` returns `false` exactly when the element was already present — one call performs "is it there?" + "insert it" together. A `if (!rows[i].add(num)) return false` pattern is the whole algorithm per constraint.

**Why not count digits instead?** Counting per row/col/box works but needs a separate loop per constraint (27 passes). The three-set version checks all constraints *in a single cell sweep* — one pass over the board.

## Approach 1 — Brute force: validate rows, then columns, then boxes

Three separate passes with `int[10]` counters: correct, $O(81)$, but three times the code and none of the "one sweep" elegance.

## Approach 2 — Three sets per cell (the repo's version, optimal)

```kotlin
class ValidSudoku {
    /**
     * @param board 9x9 Sudoku board with '.' for empty cells
     * @return      true iff no row, column, or 3x3 box contains duplicates
     */
    fun isValidSudoku(board: Array<CharArray>): Boolean {
        // Initialize sets for rows, columns, and subgrids
        val rows = Array(9) { mutableSetOf<Char>() }
        val cols = Array(9) { mutableSetOf<Char>() }
        val subgrids = Array(3) { Array(3) { mutableSetOf<Char>() } }

        for (i in board.indices) {
            for (j in board[i].indices) {
                val num = board[i][j]
                if (num != '.') {
                    // Check row
                    if (!rows[i].add(num)) return false

                    // Check column
                    if (!cols[j].add(num)) return false

                    // Check subgrid: box = (row/3, col/3)
                    if (!subgrids[i / 3][j / 3].add(num)) return false
                }
            }
        }
        return true
    }
}
```

```java
import java.util.*;

public class ValidSudoku {
    /**
     * @param board 9x9 Sudoku board with '.' for empty cells
     * @return      true iff no row, column, or 3x3 box contains duplicates
     */
    public boolean isValidSudoku(char[][] board) {
        Set<Character>[] rows = new HashSet[9];
        Set<Character>[] cols = new HashSet[9];
        Set<Character>[] boxes = new HashSet[9];
        for (int i = 0; i < 9; i++) {
            rows[i] = new HashSet<>();
            cols[i] = new HashSet<>();
            boxes[i] = new HashSet<>();
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char c = board[i][j];
                if (c == '.') continue;

                int box = (i / 3) * 3 + j / 3;        // 0..8 box index
                if (!rows[i].add(c) || !cols[j].add(c) || !boxes[box].add(c)) {
                    return false;
                }
            }
        }
        return true;
    }
}
```

```cpp
#include <set>
#include <vector>

class ValidSudoku {
public:
    /**
     * @param board 9x9 Sudoku board with '.' for empty cells
     * @return      true iff no row, column, or 3x3 box contains duplicates
     */
    bool isValidSudoku(std::vector<std::vector<char>>& board) {
        std::set<char> rows[9], cols[9], boxes[9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char c = board[i][j];
                if (c == '.') continue;

                int box = (i / 3) * 3 + j / 3;        // 0..8 box index
                if (rows[i].count(c) || cols[j].count(c) || boxes[box].count(c)) {
                    return false;
                }
                rows[i].insert(c);
                cols[j].insert(c);
                boxes[box].insert(c);
            }
        }
        return true;
    }
};
```

```python
def is_valid_sudoku(board: list[list[str]]) -> bool:
    """
    @param board: 9x9 Sudoku board with '.' for empty cells
    @return:       true iff no row, column, or 3x3 box contains duplicates
    """
    rows = [set() for _ in range(9)]
    cols = [set() for _ in range(9)]
    boxes = [set() for _ in range(9)]

    for i in range(9):
        for j in range(9):
            c = board[i][j]
            if c == ".":
                continue

            box = (i // 3) * 3 + j // 3             # 0..8 box index
            if c in rows[i] or c in cols[j] or c in boxes[box]:
                return False
            rows[i].add(c)
            cols[j].add(c)
            boxes[box].add(c)
    return True
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param board 9x9 Sudoku board with '.' for empty cells
    /// @return       true iff no row, column, or 3x3 box contains duplicates
    pub fn is_valid_sudoku(board: Vec<Vec<char>>) -> bool {
        let mut rows: Vec<HashSet<char>> = (0..9).map(|_| HashSet::new()).collect();
        let mut cols: Vec<HashSet<char>> = (0..9).map(|_| HashSet::new()).collect();
        let mut boxes: Vec<HashSet<char>> = (0..9).map(|_| HashSet::new()).collect();

        for i in 0..9 {
            for j in 0..9 {
                let c = board[i][j];
                if c == '.' { continue; }

                let box = (i / 3) * 3 + j / 3;      // 0..8 box index
                if !rows[i].insert(c) || !cols[j].insert(c) || !boxes[box].insert(c) {
                    return false;
                }
            }
        }
        true
    }
}
```

## Dry run

**Input:** the standard solved-valid board. Trace the first two rows' cells that matter:

```
row 0: '5' -> rows[0]={5}, cols[0]={5}, box0={5}
       '3' -> rows[0]={5,3}, cols[1]={3}, box0={5,3}
row 1: '6' -> rows[1]={6}, cols[0]={5,6}, box0={5,3,6}
       '1' -> rows[1]={6,1}, cols[3]={1}, box1={1}
       '9' -> rows[1]={6,1,9}, cols[4]={9}, box1={1,9}
       '5' -> rows[1]={6,1,9,5}, cols[5]={5}, box1={1,9,5}
... every add succeeds -> true ✓
```

The corrupted variant (first row starts `["8","3",...]`): at `(0,0)` row 0 gets `8`, but later `(1,1)` is also `8` in *column 0* — wait, in the standard board row 1 col 1 is `'6'`. The classic invalid case is putting `8` at `(0,0)` AND having `8` elsewhere in column 0 — when the second `8` is scanned, `cols[0].add('8')` returns `false` → the whole check returns `false`. One cell, three constraints, and the box index `(i/3)*3 + j/3` is the only arithmetic on the page.

## Complexity

**Time.** The board is fixed at 81 cells:

$$
T = O(81) = O(1)
$$

**Space.** At most 9 entries per set, 27 sets:

$$
S = O(9 \cdot 9) = O(1)
$$

## Variants & follow-ups

- **Sudoku Solver** (`src/main/kotlin/`) — backtracking over the same row/col/box sets; validity becomes a `canPlace(digit, i, j)` check against three sets. This page is the checker that solver calls millions of times.
- **N-Queens** (`src/main/kotlin/array/backtracking/NQueen.kt`) — the same "one cell, three constraint sets" shape: rows, diagonals, anti-diagonals instead of rows/cols/boxes.
- **Equal Row And Column Pairs** (`src/main/kotlin/array/hashtable/EqualRowAndColumnPairs.kt`) — canonicalizing rows/columns into map keys (the [9.2](../ch09-strings/group-anagrams.md) move) to count matches.
- **Interview follow-up:** "Why not use a `int[10]` per row/col/box?" Counting works — but checking "no count exceeds 1" needs a post-scan per constraint, while `Set.add`'s boolean does the check *during* the single cell sweep. Same asymptotics (the board is constant-size anyway); the sets are simply the tighter expression.
