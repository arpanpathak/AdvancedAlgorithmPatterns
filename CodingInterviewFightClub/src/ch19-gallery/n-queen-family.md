# 19.4 N-Queens — The Four Implementations, Captured

> **Sources:** [`src/main/kotlin/backtracking/NQueen.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/backtracking/NQueen.kt) · `NQueenOptimized.kt` · `NQueen_II.kt` · `array/backtracking/NQueen.kt`
> **Pattern:** variant gallery — the set-based board [12.4](../ch12-backtracking/n-queens.md) summarizes

## The family map

| File | Idea | Why it's cool |
|---|---|---|
| `NQueen.kt` | board + `isSafe` loops | the canonical version [12.4](../ch12-backtracking/n-queens.md) documents |
| `NQueenOptimized.kt` | **set of columns/diagonals** — no board scan | O(1) safety checks, elegant to read |
| `NQueen_II.kt` | count-only | no board, just the three sets |
| `array/backtracking/NQueen.kt` | board + row-based backtracking | same algorithm, different directory — the repo's practice echoes |

## The star: `NQueenOptimized.kt`

The whole safety check is *three string keys in a set* — `"c" + col`, `"d" + (row - col)`, `"a" + (row + col)` — because a queen attacks along constant column, constant `row - col` (main diagonal), and constant `row + col` (anti-diagonal):

```kotlin
fun solveNQueens(n: Int): List<List<String>> {
    val results = mutableListOf<List<String>>()
    val board = Array(n) { CharArray(n) { '.' } }
    val occupied = mutableSetOf<String>()

    fun isSafe(r: Int, c: Int) =
        listOf("c$c", "d${r - c}", "a${r + c}").none { it in occupied }

    fun toggleQueen(r: Int, c: Int, remove: Boolean = false) {
        val keys = listOf("c$c", "d${r - c}", "a${r + c}")
        when (remove) {
            true -> occupied.removeAll(keys)
            else -> occupied.addAll(keys)
        }
    }

    fun backtrack(r: Int) {
        if (r == n) {
            results.add(board.map { String(it) })
            return
        }

        for (c in 0 until n) {
            if (isSafe(r, c)) {
                board[r][c] = 'Q'
                toggleQueen(r, c, remove = false)

                backtrack(r + 1)

                board[r][c] = '.'
                toggleQueen(r, c, remove = true)     // undo
            }
        }
    }

    backtrack(0)
    return results
}
```

**What makes it cool:**

- **No board scanning.** `isSafe` is three set lookups — O(1) per cell instead of the classic O(n) row/col/diagonal loops ([12.4](../ch12-backtracking/n-queens.md)'s `isSafe` scans). For `n = 9` that's the difference between 9 checks and 3 per candidate.
- **The diagonal identity is encoded in the key.** `r - c` is constant along a `\` diagonal; `r + c` along `/`. Two integer arithmetic facts replace two loops.
- **`toggleQueen(r, c, remove)`** — add and remove are the *same* function with a flag; the [12.0](../ch12-backtracking/pattern-primer.md) undo contract in one signature.
- **`backtrack(r)` only ever places one queen per row** — the row index is the recursion depth, so no "have I placed a queen in this row?" check exists at all.

## The count-only: `NQueen_II.kt`

When the problem asks for the *count*, the board isn't even needed — just the three sets:

```kotlin
// sketch of the count-only shape (NQueen_II.kt)
fun totalNQueens(n: Int): Int {
    var count = 0
    val cols = mutableSetOf<Int>()
    val diag1 = mutableSetOf<Int>()   // r - c
    val diag2 = mutableSetOf<Int>()   // r + c

    fun backtrack(r: Int) {
        if (r == n) { count++; return }
        for (c in 0 until n) {
            if (c !in cols && (r - c) !in diag1 && (r + c) !in diag2) {
                cols.add(c); diag1.add(r - c); diag2.add(r + c)
                backtrack(r + 1)
                cols.remove(c); diag1.remove(r - c); diag2.remove(r + c)
            }
        }
    }

    backtrack(0)
    return count
}
```

Same O(1) checks, zero board — the purest statement of the constraint sets. The classic `isSafe`-loop version ([12.4](../ch12-backtracking/n-queens.md)) is the *explanatory* form; this is the *computational* form.

## Dry run

**Input:** `n = 4`.

```
backtrack(0): c=0: safe? yes.  place Q(0,0).  keys: c0, d0, a0.
  backtrack(1): c=0,1: blocked (c0/d0/a0...).  c=2: d(1-2)=-1, a(1+2)=3 free -> place Q(1,2).
    backtrack(2): all cols blocked by row-0/row-1 queens -> dead end.  undo.
    c=3: d(1-3)=-2, a(1+3)=4 free -> place Q(1,3).
      backtrack(2): c=1: d(2-1)=1, a(2+1)=3 free -> place Q(2,1).
        backtrack(3): c=3: d(3-3)=0 blocked.  c=0? a(3)=3 blocked... c=2? d(1) blocked.  dead end.
        undo Q(2,1).
      c=3 for row 2: d(2-3)=-1, a(5) — place Q(2,3)? then row 3 has no free col.  dead end.
    undo Q(1,3).  undo Q(0,0).
  c=1: place Q(0,1).  -> eventually the symmetric solution [[.Q..],[...Q],[Q...],[..Q.]]
  ...

Output: 2 solutions for n = 4 ✓
```

The set keys do the work: after `Q(0,0)`, every cell with `c=0` OR `r-c=0` OR `r+c=0` is blocked — which is exactly the queen's attack lines. Row 1's first free column is 2 (`d=-1, a=3` clear), and the backtracking explores/undoes from there.

## Complexity

**Time.** Same `O(n!)` worst-case bound, but the O(1) safety check removes a factor of n per node:

$$
T(n) = O(n!) \text{ with } O(1) \text{ per-cell checks}
$$

**Space.** The board + three set-keys-per-queen:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **N-Queens** ([12.4](../ch12-backtracking/n-queens.md)) — the full page with the board-scanning `isSafe` and the bitmap alternative.
- **N-Queens II** (`backtracking/NQueen_II.kt`) — the count-only twin, shown above.
- **Sudoku Solver** ([12.7](../ch12-backtracking/sudoku-solver.md)) — the same "encode constraints as keys" trick with row/col/box sets ([10.7](../ch10-hash-tables/valid-sudoku.md)'s sets).
- **Interview follow-up:** "Why do `r-c` and `r+c` uniquely identify diagonals?" Two cells share a `\` diagonal iff their `r-c` is equal; share `/` iff `r+c` equal. Both are constant along their diagonal — so the integers *are* the diagonal IDs, no geometry needed.
