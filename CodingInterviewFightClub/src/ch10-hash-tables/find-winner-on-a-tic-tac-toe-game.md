# 10.22 Find Winner On A TicTacToe Game

> **Source**: [`src/main/kotlin/simulation/FindWinnerOnATicTacToeGame.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/simulation/FindWinnerOnATicTacToeGame.kt)
> **Pattern**: row/col/diagonal check after each move · **Core page**

## The Problem

Given `moves` (alternating A, B), return the outcome: "A"/"B"/"Draw"/"Pending".

- Constraints: moves ≤ 9.

## Examples

```
Input:  moves = [[0,0],[2,0],[1,1],[2,1],[2,2]]   -> Output: "A"
Input:  moves = [[0,0],[1,1],[0,1],[0,2],[1,0],[2,0]] -> Output: "B"
```

## Intuition — place, then check the just-touched lines

After each move, check the row, column, and (if on a diagonal) the diagonals for a three-in-a-row:

```kotlin
if (checkRow(row, player) || checkColumn(col, player) ||
    checkDiagonal(player) || checkAntiDiagonal(player)) return player
```

**Why check only the affected lines?** Only the just-placed cell can complete a line — checking the whole board each move is redundant. The [18.20](../ch18-design-caches/design-tic-tac-toe.md) counter design makes this O(1).

## Approach 1 — Check whole board per move (O(9) per move)

Scan all rows/cols/diagonals: fine at this size.

## Approach 2 — Line-check per move (the repo's version, optimal)

```kotlin
class FindWinnerOnATicTacToeGame {
    private val board = Array(3) { CharArray(3) }
    private val players = charArrayOf('A', 'B')

    /**
     * @param moves alternating A/B placements
     * @return      "A", "B", "Draw", or "Pending"
     */
    fun tictactoe(moves: Array<IntArray>): String {
        for ((i, move) in moves.withIndex()) {
            val player = players[i % 2]
            val (row, col) = move
            board[row][col] = player

            if (checkRow(row, player) || checkColumn(col, player) ||
                checkDiagonal(player) || checkAntiDiagonal(player)) {
                return player.toString()
            }
        }

        return if (moves.size == 9) "Draw" else "Pending"
    }

    private fun checkRow(row: Int, player: Char): Boolean =
        (0 until 3).all { board[row][it] == player }

    private fun checkColumn(col: Int, player: Char): Boolean =
        (0 until 3).all { board[it][col] == player }

    private fun checkDiagonal(player: Char): Boolean =
        (0 until 3).all { board[it][it] == player }

    private fun checkAntiDiagonal(player: Char): Boolean =
        (0 until 3).all { board[it][2 - it] == player }
}
```

```java
public class FindWinnerOnATicTacToeGame {
    private char[][] board = new char[3][3];

    private boolean win(int r, int c, char p) {
        boolean row = true, col = true, diag = true, anti = true;

        for (int i = 0; i < 3; i++) {
            row &= board[r][i] == p;
            col &= board[i][c] == p;
            diag &= board[i][i] == p;
            anti &= board[i][2 - i] == p;
        }
        return row || col || diag || anti;
    }

    /**
     * @param moves alternating A/B placements
     * @return      "A", "B", "Draw", or "Pending"
     */
    public String tictactoe(int[][] moves) {
        for (int i = 0; i < moves.length; i++) {
            char p = i % 2 == 0 ? 'A' : 'B';
            board[moves[i][0]][moves[i][1]] = p;

            if (win(moves[i][0], moves[i][1], p)) return String.valueOf(p);
        }
        return moves.length == 9 ? "Draw" : "Pending";
    }
}
```

```cpp
#include <vector>
#include <string>

class FindWinnerOnATicTacToeGame {
    char board[3][3] = {};

    bool win(int r, int c, char p) {
        bool row = true, col = true, diag = true, anti = true;

        for (int i = 0; i < 3; i++) {
            row &= board[r][i] == p;
            col &= board[i][c] == p;
            diag &= board[i][i] == p;
            anti &= board[i][2 - i] == p;
        }
        return row || col || diag || anti;
    }

public:
    /**
     * @param moves alternating A/B placements
     * @return      "A", "B", "Draw", or "Pending"
     */
    std::string tictactoe(std::vector<std::vector<int>>& moves) {
        for (int i = 0; i < (int)moves.size(); i++) {
            char p = i % 2 == 0 ? 'A' : 'B';
            board[moves[i][0]][moves[i][1]] = p;

            if (win(moves[i][0], moves[i][1], p)) return std::string(1, p);
        }
        return moves.size() == 9 ? "Draw" : "Pending";
    }
};
```

```python
def tictactoe(moves: list[list[int]]) -> str:
    """
    @param moves: alternating A/B placements
    @return:      "A", "B", "Draw", or "Pending"
    """
    board = [[""] * 3 for _ in range(3)]

    def win(r, c, p):
        return (all(board[r][i] == p for i in range(3)) or
                all(board[i][c] == p for i in range(3)) or
                (r == c and all(board[i][i] == p for i in range(3))) or
                (r + c == 2 and all(board[i][2 - i] == p for i in range(3))))

    for i, (r, c) in enumerate(moves):
        p = "A" if i % 2 == 0 else "B"
        board[r][c] = p
        if win(r, c, p):
            return p

    return "Draw" if len(moves) == 9 else "Pending"
```

```rust
impl Solution {
    /// @param moves alternating A/B placements
    /// @return      "A", "B", "Draw", or "Pending"
    pub fn tictactoe(moves: Vec<Vec<i32>>) -> String {
        let mut board = [[' '; 3]; 3];

        for (i, m) in moves.iter().enumerate() {
            let p = if i % 2 == 0 { 'A' } else { 'B' };
            let (r, c) = (m[0] as usize, m[1] as usize);
            board[r][c] = p;

            let row = (0..3).all(|i| board[r][i] == p);
            let col = (0..3).all(|i| board[i][c] == p);
            let diag = (0..3).all(|i| board[i][i] == p);
            let anti = (0..3).all(|i| board[i][2 - i] == p);

            if row || col || diag || anti {
                return p.to_string();
            }
        }

        if moves.len() == 9 { "Draw".into() } else { "Pending".into() }
    }
}
```

## Dry run

**Input:** `moves = [[0,0],[2,0],[1,1],[2,1],[2,2]]`.

```
A(0,0).  B(2,0).  A(1,1).  B(2,1).  A(2,2): row 2 = A,B,A no.  col 2 = A? no...
  diagonal (1,1): (0,0) A, (1,1) A, (2,2) A -> WIN -> "A" ✓
```

## Complexity

**Time.** ≤ 9 moves × O(1):

$$
T = O(1)
$$

**Space.** The 3×3 board:

$$
S = O(1)
$$

## Variants & follow-ups

- **Design TicTacToe** ([18.20](../ch18-design-caches/design-tic-tac-toe.md)) — the O(1) counter version for large n.
- **Interview follow-up:** "Why check only four lines?" A win must include the just-placed cell — only its row, column, and (if on a diagonal) the two diagonals can complete. The checks are O(3) each; skipping unrelated lines is both correct and constant-time.
