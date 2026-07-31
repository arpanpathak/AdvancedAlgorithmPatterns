# 6.16 Surrounded Regions

> **Source:** [`src/main/kotlin/grid/SurroundedRegion.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/SurroundedRegion.kt) (+ `SurroundedRegionDfs.kt`)
> **Pattern:** border BFS marking · **Core page**

## The Problem

Capture all `'O'` regions **not connected to the border** — flip them to `'X'`. Border-connected `'O'`s stay.

- Constraints: m, n ≤ 200.

## Examples

```
Input:  board = [["X","X","X","X"],
                 ["X","O","O","X"],
                 ["X","X","O","X"],
                 ["X","O","X","X"]]
Output: [["X","X","X","X"],
         ["X","X","X","X"],
         ["X","X","X","X"],
         ["X","O","X","X"]]   (the corner O survives; it touches the border)
```

## Intuition — mark border-connected O's first, flip everything else

"Surrounded" means *not* connected to the border. So flip the question: find all `'O'`s reachable from the border — they survive; every other `'O'` is captured:

```
1. BFS from every border 'O', marking reachable cells '#' (or a visited set)
2. Second pass: '#' -> 'O' (survive), 'O' -> 'X' (captured), 'X' stays
```

**Why border-first?** Checking each interior region's connectivity separately is O(regions × board); the border-seed BFS is a single O(m·n) pass. The `'#'` temporary mark (the repo's choice) is the classic three-state trick — no separate visited set.

**Why the `'#'` sentinel?** The pass order does the flip: after the BFS, `'#'` marks survivors, `'O'` marks captives, `'X'` was never open. A single `when` converts all three in one sweep.

## Approach 1 — DFS per interior region (check-then-flip)

For each interior O, test connectivity to the border, then flip: correct, O(regions × cells) worst case.

## Approach 2 — Border BFS marking (the repo's version, optimal)

```kotlin
class SurroundedRegionBfs {
    /**
     * @param board grid of 'X' and 'O' (modified in place)
     */
    fun solve(board: Array<CharArray>) {
        if (board.isEmpty() || board[0].isEmpty()) return

        val m = board.size
        val n = board[0].size
        val queue = ArrayDeque<Pair<Int, Int>>()

        // Add border 'O's to the queue
        for (i in 0 until m) {
            if (board[i][0] == 'O') queue.add(i to 0)
            if (board[i][n - 1] == 'O') queue.add(i to n - 1)
        }
        for (j in 0 until n) {
            if (board[0][j] == 'O') queue.add(0 to j)
            if (board[m - 1][j] == 'O') queue.add(m - 1 to j)
        }

        // BFS marking border-connected regions
        val dirs = arrayOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeFirst()
            board[x][y] = '#'              // survivor mark

            for ((dx, dy) in dirs) {
                val nx = x + dx
                val ny = y + dy
                if (nx in 0 until m && ny in 0 until n && board[nx][ny] == 'O') {
                    queue.add(nx to ny)
                }
            }
        }

        // Flip: '#' survives, remaining 'O' is captured
        for (i in 0 until m) {
            for (j in 0 until n) {
                when (board[i][j]) {
                    '#' -> board[i][j] = 'O'
                    'O' -> board[i][j] = 'X'
                }
            }
        }
    }
}
```

```java
import java.util.*;

public class SurroundedRegions {
    /**
     * @param board grid of 'X' and 'O' (modified in place)
     */
    public void solve(char[][] board) {
        int m = board.length, n = board[0].length;
        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < m; i++) {
            if (board[i][0] == 'O') queue.offer(new int[]{i, 0});
            if (board[i][n - 1] == 'O') queue.offer(new int[]{i, n - 1});
        }
        for (int j = 0; j < n; j++) {
            if (board[0][j] == 'O') queue.offer(new int[]{0, j});
            if (board[m - 1][j] == 'O') queue.offer(new int[]{m - 1, j});
        }

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            int[] top = queue.poll();
            board[top[0]][top[1]] = '#';

            for (int[] d : dirs) {
                int nx = top[0] + d[0], ny = top[1] + d[1];
                if (nx >= 0 && nx < m && ny >= 0 && ny < n && board[nx][ny] == 'O') {
                    queue.offer(new int[]{nx, ny});
                }
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == '#') board[i][j] = 'O';
                else if (board[i][j] == 'O') board[i][j] = 'X';
            }
        }
    }
}
```

```cpp
#include <queue>
#include <vector>

class SurroundedRegions {
public:
    /**
     * @param board grid of 'X' and 'O' (modified in place)
     */
    void solve(std::vector<std::vector<char>>& board) {
        int m = board.size(), n = board[0].size();
        std::queue<std::pair<int, int>> queue;

        for (int i = 0; i < m; i++) {
            if (board[i][0] == 'O') queue.push({i, 0});
            if (board[i][n - 1] == 'O') queue.push({i, n - 1});
        }
        for (int j = 0; j < n; j++) {
            if (board[0][j] == 'O') queue.push({0, j});
            if (board[m - 1][j] == 'O') queue.push({m - 1, j});
        }

        int dirs[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.empty()) {
            auto [x, y] = queue.front(); queue.pop();
            board[x][y] = '#';

            for (auto& d : dirs) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && nx < m && ny >= 0 && ny < n && board[nx][ny] == 'O') {
                    queue.push({nx, ny});
                }
            }
        }

        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                if (board[i][j] == '#') board[i][j] = 'O';
                else if (board[i][j] == 'O') board[i][j] = 'X';
            }
    }
};
```

```python
from collections import deque

def solve(board: list[list[str]]) -> None:
    """
    @param board: grid of 'X' and 'O' (modified in place)
    """
    m, n = len(board), len(board[0])
    queue = deque()

    for i in range(m):
        if board[i][0] == "O":
            queue.append((i, 0))
        if board[i][n - 1] == "O":
            queue.append((i, n - 1))
    for j in range(n):
        if board[0][j] == "O":
            queue.append((0, j))
        if board[m - 1][j] == "O":
            queue.append((m - 1, j))

    while queue:
        x, y = queue.popleft()
        board[x][y] = "#"                    # survivor mark

        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            nx, ny = x + dx, y + dy
            if 0 <= nx < m and 0 <= ny < n and board[nx][ny] == "O":
                queue.append((nx, ny))

    for i in range(m):
        for j in range(n):
            if board[i][j] == "#":
                board[i][j] = "O"
            elif board[i][j] == "O":
                board[i][j] = "X"
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param board grid of 'X' and 'O' (modified in place)
    pub fn solve(board: &mut Vec<Vec<char>>) {
        let (m, n) = (board.len(), board[0].len());
        let mut queue = VecDeque::new();

        for i in 0..m {
            if board[i][0] == 'O' { queue.push_back((i, 0)); }
            if board[i][n - 1] == 'O' { queue.push_back((i, n - 1)); }
        }
        for j in 0..n {
            if board[0][j] == 'O' { queue.push_back((0, j)); }
            if board[m - 1][j] == 'O' { queue.push_back((m - 1, j)); }
        }

        let dirs = [(1, 0), (-1, 0), (0, 1), (0, -1)];
        while let Some((x, y)) = queue.pop_front() {
            board[x][y] = '#';               // survivor mark

            for (dx, dy) in dirs {
                let (nx, ny) = (x as i32 + dx, y as i32 + dy);
                if nx >= 0 && nx < m as i32 && ny >= 0 && ny < n as i32 && board[nx as usize][ny as usize] == 'O' {
                    queue.push_back((nx as usize, ny as usize));
                }
            }
        }

        for i in 0..m {
            for j in 0..n {
                if board[i][j] == '#' { board[i][j] = 'O'; }
                else if board[i][j] == 'O' { board[i][j] = 'X'; }
            }
        }
    }
}
```

## Dry run

**Input:** the 4×4 example.

```
Border seeds: (3,1) is the only border 'O'.

BFS from (3,1): mark '#'.  neighbors: (2,1)='X', (3,0)='X', (3,2)='X', (3,2)... only (3,1) reached.
  (the interior O's at (1,1),(1,2),(2,2) are NOT connected to the border -> unmarked)

Flip pass: '#' (3,1) -> 'O' survives.  'O' at (1,1),(1,2),(2,2) -> 'X' captured.

Output: the expected grid ✓
```

The flip pass's `when` is the whole outcome: survivors (`'#'`) restore to `'O'`, captives (`'O'`) become `'X'`, and pre-existing `'X'` untouched. The BFS never touches the interior — being unreachable from the border IS the capture condition.

## Complexity

**Time.** Border scan + BFS + flip:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** The queue:

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Rotting Oranges** ([6.14](rotting-oranges.md)) — the multi-source BFS sibling (all rotten seeds at once).
- **Number Of Islands** (`grid/NumberOfIslands.kt`) — the same grid-DSF/BFS machinery counting components.
- **Interview follow-up:** "Why the `'#'` intermediate mark?" The problem needs a three-way distinction — survivor, captive, wall — during the BFS. `'#'` holds the survivor state in-place; without it you'd need a separate visited set and a second board scan to know which O's to keep. The temporary token collapses both into the grid itself.
