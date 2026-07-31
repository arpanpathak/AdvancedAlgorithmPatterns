# 18.20 Design TicTacToe

> **Source**: [`src/main/kotlin/math/DesignTicTacToe.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/DesignTicTacToe.kt)
> **Pattern**: row/col/diagonal counters · **Core page**

## The Problem

`move(row, col, player)` returns the winner (1/2) or 0 — O(1) per move, n×n board.

- Constraints: n ≤ 100; moves ≤ n².

## Examples

```
["TicTacToe","move","move","move","move","move","move","move"]
[[3],[0,0,1],[0,2,2],[2,2,1],[1,1,2],[2,0,1],[1,0,2],[2,1,1]]
-> [null,0,0,0,0,0,0,1]
```

## Intuition — count per line with +1/−1 marks; a line hits ±n

Instead of a board, track each row/col/diagonal as a **signed count**: player 1 adds +1, player 2 adds −1. A line is won when its count reaches `n` or `-n`:

```kotlin
class TicTacToe(n: Int) {
    private val rows = IntArray(n)
    private val cols = IntArray(n)
    private var diagonal = 0
    private var antiDiagonal = 0

    fun move(row: Int, col: Int, player: Int): Int {
        val mark = if (player == 1) 1 else -1

        rows[row] += mark
        cols[col] += mark
        if (row == col) diagonal += mark
        if (row + col == n - 1) antiDiagonal += mark

        return if (rows[row] == n || cols[col] == n || diagonal == n || antiDiagonal == n ||
                   rows[row] == -n || cols[col] == -n || diagonal == -n || antiDiagonal == -n) {
            player
        } else 0
    }
}
```

**Why counters beat a board?** The board needs a per-move scan (O(n)); the counters update O(1) and the win test is four comparisons. The [10.22](../ch10-hash-tables/find-winner-on-a-tic-tac-toe-game.md) line-check, made incremental.

**Why ±n?** A line's count is the sum of its players' marks — all-player-1 → n, all-player-2 → −n. The sign encodes the winner.

## Approach 1 — Board + scan per move (O(n))

Check the affected lines each move: fine at n ≤ 3, O(n) otherwise.

## Approach 2 — Signed line counters (the repo's version, optimal)

```kotlin
class TicTacToe(n: Int) {
    private val rows = IntArray(n)
    private val cols = IntArray(n)
    private var diagonal = 0
    private var antiDiagonal = 0
    private val size = n

    /**
     * @param row    move row
     * @param col    move col
     * @param player 1 or 2
     * @return       winner (1/2) or 0
     */
    fun move(row: Int, col: Int, player: Int): Int {
        val mark = if (player == 1) 1 else -1

        rows[row] += mark
        cols[col] += mark
        if (row == col) diagonal += mark
        if (row + col == size - 1) antiDiagonal += mark

        return if (rows[row] == size || cols[col] == size || diagonal == size || antiDiagonal == size ||
                   rows[row] == -size || cols[col] == -size || diagonal == -size || antiDiagonal == -size) {
            player
        } else 0
    }
}
```

```java
public class TicTacToe {
    private final int[] rows, cols;
    private int diagonal = 0, antiDiagonal = 0;
    private final int n;

    public TicTacToe(int n) {
        this.n = n;
        rows = new int[n];
        cols = new int[n];
    }

    /**
     * @param row    move row
     * @param col    move col
     * @param player 1 or 2
     * @return       winner (1/2) or 0
     */
    public int move(int row, int col, int player) {
        int mark = player == 1 ? 1 : -1;

        rows[row] += mark;
        cols[col] += mark;
        if (row == col) diagonal += mark;
        if (row + col == n - 1) antiDiagonal += mark;

        if (rows[row] == n || cols[col] == n || diagonal == n || antiDiagonal == n ||
            rows[row] == -n || cols[col] == -n || diagonal == -n || antiDiagonal == -n) {
            return player;
        }
        return 0;
    }
}
```

```cpp
#include <vector>

class TicTacToe {
    std::vector<int> rows, cols;
    int diagonal = 0, antiDiagonal = 0;
    int n;

public:
    TicTacToe(int n) : n(n), rows(n, 0), cols(n, 0) {}

    /**
     * @param row    move row
     * @param col    move col
     * @param player 1 or 2
     * @return       winner (1/2) or 0
     */
    int move(int row, int col, int player) {
        int mark = player == 1 ? 1 : -1;

        rows[row] += mark;
        cols[col] += mark;
        if (row == col) diagonal += mark;
        if (row + col == n - 1) antiDiagonal += mark;

        if (rows[row] == n || cols[col] == n || diagonal == n || antiDiagonal == n ||
            rows[row] == -n || cols[col] == -n || diagonal == -n || antiDiagonal == -n) {
            return player;
        }
        return 0;
    }
};
```

```python
class TicTacToe:
    def __init__(self, n: int):
        self.n = n
        self.rows = [0] * n
        self.cols = [0] * n
        self.diag = 0
        self.anti = 0

    def move(self, row: int, col: int, player: int) -> int:
        mark = 1 if player == 1 else -1

        self.rows[row] += mark
        self.cols[col] += mark
        if row == col:
            self.diag += mark
        if row + col == self.n - 1:
            self.anti += mark

        if (self.rows[row] == self.n or self.cols[col] == self.n or
                self.diag == self.n or self.anti == self.n or
                self.rows[row] == -self.n or self.cols[col] == -self.n or
                self.diag == -self.n or self.anti == -self.n):
            return player
        return 0
```

```rust
struct TicTacToe {
    rows: Vec<i32>,
    cols: Vec<i32>,
    diag: i32,
    anti: i32,
    n: i32,
}

impl TicTacToe {
    fn new(n: i32) -> Self {
        Self { rows: vec![0; n as usize], cols: vec![0; n as usize], diag: 0, anti: 0, n }
    }

    /// @param row    move row
    /// @param col    move col
    /// @param player 1 or 2
    /// @return       winner (1/2) or 0
    fn r#move(&mut self, row: i32, col: i32, player: i32) -> i32 {
        let mark = if player == 1 { 1 } else { -1 };

        self.rows[row as usize] += mark;
        self.cols[col as usize] += mark;
        if row == col { self.diag += mark; }
        if row + col == self.n - 1 { self.anti += mark; }

        let won = [self.rows[row as usize], self.cols[col as usize], self.diag, self.anti]
            .iter().any(|&v| v == self.n || v == -self.n);
        if won { player } else { 0 }
    }
}
```

## Dry run

**Input:** `n = 3`; the 7-move sequence ending in player 1's win.

```
move(0,0,1): rows[0]=1, cols[0]=1, diag=1.  no win.
move(0,2,2): rows[0]=0, cols[2]=-1.  no.
move(2,2,1): rows[2]=1, cols[2]=0, diag=2.  no.
move(1,1,2): rows[1]=-1, cols[1]=-1, diag=1, anti=-1.  no.
move(2,0,1): rows[2]=2, cols[0]=2, anti=0.  no.
move(1,0,2): rows[1]=-2, cols[0]=1.  no.
move(2,1,1): rows[2]=3 == n -> return 1 ✓
```

Player 1 fills row 2 (moves at (2,2), (2,0), (2,1)) — `rows[2]` reaches 3 and the win test fires. The ±n symmetry: had player 2 filled a line, its counter would be −3.

## Complexity

**Time.** O(1) per move:

$$
T = O(1)
$$

**Space.** The counters:

$$
S = O(n)
$$

## Variants & follow-ups

- **Find Winner On A TicTacToe Game** ([10.22](../ch10-hash-tables/find-winner-on-a-tic-tac-toe-game.md)) — the board-scan version for the fixed 3×3.
- **Interview follow-up:** "Why can a single counter represent a whole line?" The counter is the signed sum of marks — it can only be n (all 1s) or −n (all −1s) if the line is full of one player. Any mixed line's absolute value stays below n, so the ±n test is exact, not heuristic.
