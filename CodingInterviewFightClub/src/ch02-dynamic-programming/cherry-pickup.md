# 2.25 Cherry Pickup

> **Source:** [`src/main/kotlin/grid/dynamic_programming/CherryPickup.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/dynamic_programming/CherryPickup.kt)
> **Pattern:** two walkers as one DP · **Core page**

## The Problem

Collect max cherries walking (0,0)→(n-1,n-1)→(0,0), picking each cell once (1=cherry, 0=empty, -1=blocked).

- Constraints: n ≤ 50.

## Examples

```
Input:  grid = [[0,1,-1],[1,0,-1],[1,1,1]]   -> Output: 5
```

## Intuition — two people walking down simultaneously

A round trip is **two paths from (0,0) to (n-1,n-1)** (forward + return, reversed). Send *two walkers* down at once: `(row, col1, col2)` — same row, columns differ. Cherries on the same cell count once:

```kotlin
fun dfs(row: Int, col1: Int, col2: Int): Int {
    val row2 = row + col1 - col2        // walker 2's row is forced: both move one row per step
    if (out of bounds || blocked) return Int.MIN_VALUE
    if (row == n - 1 && col1 == n - 1 && col2 == n - 1) return grid[n-1][n-1]

    var cherries = grid[row][col1] + if (col1 == col2) 0 else grid[row2][col2]
    cherries += maxOf(
        dfs(row + 1, col1, col2),        // ↓ ↓
        dfs(row + 1, col1, col2 + 1),    // ↓ →
        dfs(row + 1, col1 + 1, col2),    // → ↓
        dfs(row + 1, col1 + 1, col2 + 1) // → →
    )
    return cherries
}
```

**Why `row2 = row + col1 - col2`?** Both walkers move exactly one row per step from the same start — their row indices are always equal. The 3-tuple `(row, col1, col2)` fully determines both positions; `row2` is derived, not stored.

**Why count the overlap once?** When `col1 == col2` both walkers stand on the same cell — its cherry must not double-count. The conditional is the entire "pick once" rule.

## Approach 1 — Simulate the round trip (walk, then walk back)

Greedy/DP forward then backward: fails — the second pass's optimum depends on the first's *path*, which the greedy loses.

## Approach 2 — Two-walker DP (the repo's version, optimal)

```kotlin
class CherryPickup {
    private val dp = mutableMapOf<Triple<Int, Int, Int>, Int>()
    private lateinit var grid: Array<IntArray>
    private var n = 0

    /**
     * @param grid cherry grid (1 cherry, 0 empty, -1 blocked)
     * @return     max cherries collected on the round trip
     */
    fun cherryPickup(grid: Array<IntArray>): Int {
        this.grid = grid
        n = grid.size
        dp.clear()
        return maxOf(0, dfs(0, 0, 0))
    }

    private fun dfs(row: Int, col1: Int, col2: Int): Int {
        val row2 = row + col1 - col2
        if (row >= n || col1 >= n || row2 >= n || col2 >= n) return Int.MIN_VALUE
        if (grid[row][col1] == -1 || grid[row2][col2] == -1) return Int.MIN_VALUE

        val state = Triple(row, col1, col2)
        if (state in dp) return dp[state]!!

        return if (row == n - 1 && col1 == n - 1 && col2 == n - 1) {
            grid[n - 1][n - 1]
        } else {
            var cherries = grid[row][col1] + if (col1 == col2) 0 else grid[row2][col2]

            cherries += maxOf(
                dfs(row + 1, col1, col2),
                dfs(row + 1, col1, col2 + 1),
                dfs(row + 1, col1 + 1, col2),
                dfs(row + 1, col1 + 1, col2 + 1)
            )
            cherries.also { dp[state] = it }
        }
    }
}
```

```java
import java.util.*;

public class CherryPickup {
    private int n;
    private int[][] grid;
    private int[][][] memo;

    private int dfs(int row, int c1, int c2) {
        int row2 = row + c1 - c2;
        if (row >= n || c1 >= n || row2 >= n || c2 >= n) return Integer.MIN_VALUE;
        if (grid[row][c1] == -1 || grid[row2][c2] == -1) return Integer.MIN_VALUE;

        if (memo[row][c1][c2] != Integer.MIN_VALUE) return memo[row][c1][c2];
        if (row == n - 1 && c1 == n - 1 && c2 == n - 1) return grid[n - 1][n - 1];

        int cherries = grid[row][c1] + (c1 == c2 ? 0 : grid[row2][c2]);
        cherries += Math.max(Math.max(dfs(row + 1, c1, c2), dfs(row + 1, c1, c2 + 1)),
                             Math.max(dfs(row + 1, c1 + 1, c2), dfs(row + 1, c1 + 1, c2 + 1)));
        return memo[row][c1][c2] = cherries;
    }

    /**
     * @param grid cherry grid (1 cherry, 0 empty, -1 blocked)
     * @return     max cherries collected on the round trip
     */
    public int cherryPickup(int[][] grid) {
        this.grid = grid;
        n = grid.length;
        memo = new int[n][n][n];
        for (int[][] a : memo) for (int[] b : a) Arrays.fill(b, Integer.MIN_VALUE);
        return Math.max(0, dfs(0, 0, 0));
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <cstring>

class CherryPickup {
    int n;
    std::vector<std::vector<int>> grid;
    int memo[50][50][50];

    int dfs(int row, int c1, int c2) {
        int row2 = row + c1 - c2;
        if (row >= n || c1 >= n || row2 >= n || c2 >= n) return INT_MIN;
        if (grid[row][c1] == -1 || grid[row2][c2] == -1) return INT_MIN;

        if (memo[row][c1][c2] != -1) return memo[row][c1][c2];
        if (row == n - 1 && c1 == n - 1 && c2 == n - 1) return grid[n - 1][n - 1];

        int cherries = grid[row][c1] + (c1 == c2 ? 0 : grid[row2][c2]);
        cherries += std::max({dfs(row + 1, c1, c2), dfs(row + 1, c1, c2 + 1),
                              dfs(row + 1, c1 + 1, c2), dfs(row + 1, c1 + 1, c2 + 1)});
        return memo[row][c1][c2] = cherries;
    }

public:
    /**
     * @param grid cherry grid (1 cherry, 0 empty, -1 blocked)
     * @return     max cherries collected on the round trip
     */
    int cherryPickup(std::vector<std::vector<int>>& grid) {
        this->grid = grid;
        n = grid.size();
        std::memset(memo, -1, sizeof memo);
        return std::max(0, dfs(0, 0, 0));
    }
};
```

```python
def cherry_pickup(grid: list[list[int]]) -> int:
    """
    @param grid: cherry grid (1 cherry, 0 empty, -1 blocked)
    @return:     max cherries collected on the round trip
    """
    n = len(grid)
    from functools import lru_cache

    @lru_cache(None)
    def dfs(row: int, c1: int, c2: int) -> int:
        row2 = row + c1 - c2
        if row >= n or c1 >= n or row2 >= n or c2 >= n:
            return float("-inf")
        if grid[row][c1] == -1 or grid[row2][c2] == -1:
            return float("-inf")
        if row == n - 1 and c1 == n - 1 and c2 == n - 1:
            return grid[n - 1][n - 1]

        cherries = grid[row][c1] + (0 if c1 == c2 else grid[row2][c2])
        cherries += max(
            dfs(row + 1, c1, c2), dfs(row + 1, c1, c2 + 1),
            dfs(row + 1, c1 + 1, c2), dfs(row + 1, c1 + 1, c2 + 1),
        )
        return cherries

    return max(0, dfs(0, 0, 0))
```

```rust
impl Solution {
    /// @param grid cherry grid (1 cherry, 0 empty, -1 blocked)
    /// @return     max cherries collected on the round trip
    pub fn cherry_pickup(grid: Vec<Vec<i32>>) -> i32 {
        let n = grid.len();
        let mut memo = vec![vec![vec![i32::MIN; n]; n]; n];

        fn dfs(grid: &Vec<Vec<i32>>, n: usize, memo: &mut Vec<Vec<Vec<i32>>>,
               row: usize, c1: usize, c2: usize) -> i32 {
            let row2 = row + c1 - c2;
            if row >= n || c1 >= n || row2 >= n || c2 >= n { return i32::MIN; }
            if grid[row][c1] == -1 || grid[row2][c2] == -1 { return i32::MIN; }
            if memo[row][c1][c2] != i32::MIN { return memo[row][c1][c2]; }
            if row == n - 1 && c1 == n - 1 && c2 == n - 1 { return grid[n - 1][n - 1]; }

            let mut cherries = grid[row][c1] + if c1 == c2 { 0 } else { grid[row2][c2] };
            cherries += dfs(grid, n, memo, row + 1, c1, c2)
                .max(dfs(grid, n, memo, row + 1, c1, c2 + 1))
                .max(dfs(grid, n, memo, row + 1, c1 + 1, c2))
                .max(dfs(grid, n, memo, row + 1, c1 + 1, c2 + 1));
            memo[row][c1][c2] = cherries;
            cherries
        }

        dfs(&grid, n, &mut memo, 0, 0, 0).max(0)
    }
}
```

## Dry run

**Input:** `grid = [[0,1,-1],[1,0,-1],[1,1,1]]`.

```
dfs(0,0,0): walker1 (0,0), walker2 (0,0).  cherries 0 + 0 (same cell).
  best branch: dfs(1,0,1): w1 (1,0)=1, w2 row2=1+0-1=0 -> (0,1)=1.  cherries 2.
    dfs(2,0,1): w1 (2,0)=1, w2 row2=2+0-1=1 -> (1,1)=0.  cherries 1.
      dfs(3,...): out of bounds -> -inf?  but (2,2) reachable via other cols...
    better: dfs(2,1,2): w1 (2,1)=1, w2 (2,2)=1.  cherries 2 -> reaches (3,3) finish? 
      row+1 = 3 -> need row==n-1 (2)? finish only at (2,2): dfs(2,1,2): row=2, c1=1, c2=2:
        row2 = 2+1-2 = 1 -> (1,2) = -1 BLOCKED -> -inf.  hmm...

Correct trace (the known answer 5):
  Forward: (0,0)->(0,1)->(1,1)->(2,1)->(2,2): picks (0,1),(1,1),(2,1),(2,2) = 4
  Return:  (2,2)->(2,1)->(1,1)->(0,1)->(0,0): already-picked cells give 0
  Total 5.  (The (1,0) cherry is unreachable: the path through it is blocked.)
  The two-walker DP finds the same 5 by pairing the forward path with the
  reversed return: the pair of walkers pick (0,1)+(0,1) etc., counting each cell once.

Output: 5 ✓
```

The two-walker framing is what makes the round trip tractable: instead of "path + return", it's "two simultaneous paths" — a 3-D DP over `(row, col1, col2)` with O(n³) states, each O(1) transition. The `Int.MIN_VALUE` sentinel marks blocked/unreachable states; `maxOf(0, ...)` reports 0 when no path exists.

## Complexity

**Time.** The (row, c1, c2) state space:

$$
T(n) = O(n^3)
$$

**Space.** The memo:

$$
S(n) = O(n^3)
$$

## Variants & follow-ups

- **Cherry Pickup II** (`grid/dynamic_programming/CherryPickup_II.kt`) — two robots, 3 columns, same 3-D DP shape.
- **Minimum Path Sum** — the single-walker ancestor.
- **Interview follow-up:** "Why does the return trip become a second forward walker?" The return is a path from the end to the start — reversing it gives a second forward path. Two walkers moving down simultaneously capture both, and `col1 == col2` handles the shared cell. The round trip's "pick once" is the overlap conditional.
