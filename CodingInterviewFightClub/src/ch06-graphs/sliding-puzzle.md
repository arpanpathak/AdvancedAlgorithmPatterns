# 6.25 Sliding Puzzle

> **Source**: [`src/main/kotlin/math/SlidingPuzzle.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/SlidingPuzzle.kt)
> **Pattern**: board-state BFS · **Core page**

## The Problem

Min moves to solve the 2×3 sliding puzzle to `"123450"`.

- Constraints: 6 tiles.

## Examples

```
Input:  board = [[1,2,3],[4,0,5]]   -> Output: 1
Input:  board = [[4,1,2],[5,0,3]]   -> Output: 5
```

## Intuition — states are strings; BFS over the 0's swaps

Flatten the board to a string; BFS where each state swaps the `0` with an adjacent tile (the [6.1](word-ladder.md) implicit graph, string states):

```kotlin
val start = board.flatMap { it.asIterable() }.joinToString("")
if (start == target) return 0

val queue: Queue<Pair<String, Int>> = LinkedList()
queue.offer(start to 0)
visited.add(start)

while (queue.isNotEmpty()) {
    val (current, moves) = queue.poll()
    if (current == target) return moves

    val zeroIndex = current.indexOf('0')
    for (swapIndex in neighbors[zeroIndex]) {
        val next = current.toCharArray().apply { ... swap ... }.concatToString()
        if (visited.add(next)) queue.offer(next to moves + 1)
    }
}
return -1
```

**Why the static `neighbors` table?** The 0's legal swaps depend only on its position — a 6-entry adjacency table replaces bounds checks ([6.1](word-ladder.md) neighbor-generation discipline).

## Approach 1 — Board-state BFS (the repo's version, optimal)

```kotlin
import java.util.*

class SlidingPuzzle {
    /**
     * @param board 2x3 puzzle board
     * @return      min moves to "123450", or -1
     */
    fun slidingPuzzle(board: Array<IntArray>): Int {
        val target = "123450"
        val start = board.flatMap { it.asIterable() }.joinToString("")
        if (start == target) return 0

        val dirs = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
        val visited = mutableSetOf<String>()
        val queue: Queue<Pair<String, Int>> = LinkedList()
        queue.offer(start to 0)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val (current, moves) = queue.poll()
            if (current == target) return moves

            val zeroIndex = current.indexOf('0')
            val (zeroRow, zeroCol) = zeroIndex / 3 to zeroIndex % 3

            for ((dr, dc) in dirs) {
                val nr = zeroRow + dr
                val nc = zeroCol + dc
                if (nr in 0 until 2 && nc in 0 until 3) {
                    val swapIndex = nr * 3 + nc
                    val chars = current.toCharArray()
                    chars[zeroIndex] = chars[swapIndex]
                    chars[swapIndex] = '0'
                    val next = chars.concatToString()

                    if (visited.add(next)) queue.offer(next to moves + 1)
                }
            }
        }
        return -1
    }
}
```

```java
import java.util.*;

public class SlidingPuzzle {
    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    /**
     * @param board 2x3 puzzle board
     * @return      min moves to "123450", or -1
     */
    public int slidingPuzzle(int[][] board) {
        String target = "123450";
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) for (int v : row) sb.append(v);
        String start = sb.toString();

        if (start.equals(target)) return 0;

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(start);
        visited.add(start);
        int moves = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                if (cur.equals(target)) return moves;

                int z = cur.indexOf('0');
                int r = z / 3, c = z % 3;

                for (int[] d : DIRS) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr >= 0 && nr < 2 && nc >= 0 && nc < 3) {
                        char[] chars = cur.toCharArray();
                        chars[z] = chars[nr * 3 + nc];
                        chars[nr * 3 + nc] = '0';

                        String next = new String(chars);
                        if (visited.add(next)) queue.offer(next);
                    }
                }
            }
            moves++;
        }
        return -1;
    }
}
```

```cpp
#include <string>
#include <queue>
#include <unordered_set>

class SlidingPuzzle {
    int dirs[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

public:
    /**
     * @param board 2x3 puzzle board
     * @return      min moves to "123450", or -1
     */
    int slidingPuzzle(std::vector<std::vector<int>>& board) {
        std::string start;
        for (auto& row : board) for (int v : row) start += std::to_string(v);

        if (start == "123450") return 0;

        std::queue<std::string> queue;
        std::unordered_set<std::string> visited;
        queue.push(start);
        visited.insert(start);
        int moves = 0;

        while (!queue.empty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                std::string cur = queue.front(); queue.pop();
                if (cur == "123450") return moves;

                int z = cur.find('0');
                int r = z / 3, c = z % 3;

                for (auto& d : dirs) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr >= 0 && nr < 2 && nc >= 0 && nc < 3) {
                        std::swap(cur[z], cur[nr * 3 + nc]);
                        if (visited.insert(cur).second) queue.push(cur);
                        std::swap(cur[z], cur[nr * 3 + nc]);   // restore
                    }
                }
            }
            moves++;
        }
        return -1;
    }
};
```

```python
from collections import deque

def sliding_puzzle(board: list[list[int]]) -> int:
    """
    @param board: 2x3 puzzle board
    @return:      min moves to "123450", or -1
    """
    start = "".join(str(v) for row in board for v in row)
    if start == "123450":
        return 0

    dirs = ((1, 0), (-1, 0), (0, 1), (0, -1))
    queue = deque([(start, 0)])
    visited = {start}

    while queue:
        cur, moves = queue.popleft()
        if cur == "123450":
            return moves

        z = cur.index("0")
        r, c = divmod(z, 3)

        for dr, dc in dirs:
            nr, nc = r + dr, c + dc
            if 0 <= nr < 2 and 0 <= nc < 3:
                chars = list(cur)
                chars[z], chars[nr * 3 + nc] = chars[nr * 3 + nc], chars[z]
                nxt = "".join(chars)

                if nxt not in visited:
                    visited.add(nxt)
                    queue.append((nxt, moves + 1))

    return -1
```

```rust
use std::collections::{HashSet, VecDeque};

impl Solution {
    /// @param board 2x3 puzzle board
    /// @return      min moves to "123450", or -1
    pub fn sliding_puzzle(board: Vec<Vec<i32>>) -> i32 {
        let start: String = board.iter().flatten().map(|v| (b'0' + *v as u8) as char).collect();
        if start == "123450" { return 0; }

        let dirs = [(1, 0), (-1, 0), (0, 1), (0, -1)];
        let mut queue: VecDeque<(String, i32)> = VecDeque::new();
        let mut visited: HashSet<String> = HashSet::new();
        queue.push_back((start.clone(), 0));
        visited.insert(start);

        while let Some((cur, moves)) = queue.pop_front() {
            if cur == "123450" { return moves; }

            let z = cur.find('0').unwrap();
            let (r, c) = (z / 3, z % 3);

            for (dr, dc) in dirs {
                let (nr, nc) = (r as i32 + dr, c as i32 + dc);
                if nr >= 0 && nr < 2 && nc >= 0 && nc < 3 {
                    let mut chars: Vec<char> = cur.chars().collect();
                    let swap = nr as usize * 3 + nc as usize;
                    chars.swap(z, swap);

                    let next: String = chars.into_iter().collect();
                    if visited.insert(next.clone()) {
                        queue.push_back((next, moves + 1));
                    }
                }
            }
        }
        -1
    }
}
```

## Dry run

**Input:** `board = [[4,1,2],[5,0,3]]`.

```
start "412503".  BFS level 0: "412503".
level 1: 0 at index 4 (row 1, col 1): neighbors (0,1)->idx1: swap -> "412053"; (1,0)->idx3: "410523";
          (1,2)->idx5: "412530".  three states.
level 2: ... eventually "123450" found at level 5 ✓ (the known answer)
```

BFS guarantees the minimum: the first time the target is dequeued, every shorter path has already been explored. The string state makes visited-tracking trivial — the [6.1](word-ladder.md) machinery on a 6!-state space (max 720 states, trivially fast).

## Complexity

**Time.** ≤ 6! states × 4 swaps:

$$
T = O(6! \cdot 4) = O(1)
$$

**Space.** The visited set:

$$
S = O(6!) = O(1)
$$

## Variants & follow-ups

- **Word Ladder** ([6.1](word-ladder.md)) — the implicit-graph BFS ancestor.
- **Interview follow-up:** "Why flatten to a string?" String states are hashable and comparable — the board's geometry reduces to index arithmetic (`row * 3 + col`). The [6.1](word-ladder.md) "state as a hashable key" principle, in its purest form.
