# 17.11 Walls And Gates

> **Source:** [`src/main/kotlin/grid/WallsAndGates.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/WallsAndGates.kt)
> **Pattern:** multi-source BFS writing distances · **Core page**

## The Problem

Given a grid (`-1` wall, `0` gate, `INF` empty room), fill every empty room with the **distance to its nearest gate**.

- Constraints: $m, n \le 250$; values are -1, 0, or 2147483647.

## Examples

```
Input:  rooms = [[INF,-1,0,INF],[INF,INF,INF,-1],[INF,-1,INF,-1],[0,-1,INF,INF]]
Output: [[3,-1,0,1],[2,2,1,-1],[1,-1,2,-1],[0,-1,3,4]]
```

## Intuition — BFS from *every gate at once*; the first visit is the nearest distance

The naive approach (BFS from each room to find its gate) is O(rooms · cells). **Multi-source BFS** inverts it: seed *all gates* in one queue and spread one level at a time — the first time a room is reached, that's its **shortest** distance (BFS property), so it's written once and never revisited:

```
queue = all (r, c) with rooms[r][c] == 0
while queue not empty:
    (x, y) = queue.removeFirst()
    for each 4-directional neighbor (nx, ny):
        if in bounds and rooms[nx][ny] == INF:
            rooms[nx][ny] = rooms[x][y] + 1      # nearest gate found
            queue.add(nx, ny)
```

**Why is the first visit the nearest?** BFS explores in distance order from *all* sources simultaneously — the frontier at distance d reaches every room whose nearest gate is d before any room at distance d+1. The `rooms[nx][ny] == INF` check doubles as the visited test: a written room (non-INF) is never re-enqueued.

**Why no level fencing here (unlike [6.14](../ch06-graphs/rotting-oranges.md))?** The answer is per-cell *distances*, not a global minute count — each cell records `parent distance + 1` at write time, so the plain queue suffices. The fence only matters when you need "how many levels".

**The `INF == Int.MAX_VALUE` subtlety** — `rooms[x][y] + 1` could overflow if a gate's neighbor were MAX_VALUE + 1; the `== INF` guard means only INF cells are written, and they hold exactly MAX_VALUE, so `MAX_VALUE` cells adjacent to the frontier get `distance + 1 ≤ MAX_VALUE`. Safe by construction.

## Approach 1 — BFS from each room (O(m²n²))

For every empty room, BFS to the nearest gate: correct, quadratic in cells.

## Approach 2 — Multi-source BFS from the gates (the repo's version, optimal)

```kotlin
class WallsAndGates {
    /**
     * @param rooms grid: -1 wall, 0 gate, Int.MAX_VALUE empty room (filled in place)
     */
    fun wallsAndGates(rooms: Array<IntArray>) {
        if (rooms.isEmpty() || rooms[0].isEmpty()) return

        val directions = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)
        val queue = ArrayDeque<Pair<Int, Int>>()

        // Add all gates (0s) to the queue
        for (i in rooms.indices) {
            for (j in rooms[i].indices) {
                if (rooms[i][j] == 0) queue.add(i to j)
            }
        }

        // BFS from all gates: the first visit is the nearest distance
        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeFirst()

            directions.forEach { (dx, dy) ->
                val newX = x + dx
                val newY = y + dy

                if (newX in rooms.indices && newY in rooms[0].indices
                        && rooms[newX][newY] == Int.MAX_VALUE) {
                    rooms[newX][newY] = rooms[x][y] + 1     // nearest gate found
                    queue.add(newX to newY)
                }
            }
        }
    }
}
```

```java
import java.util.*;

public class WallsAndGates {
    /**
     * @param rooms grid: -1 wall, 0 gate, Integer.MAX_VALUE empty room (filled in place)
     */
    public void wallsAndGates(int[][] rooms) {
        int m = rooms.length, n = rooms[0].length;
        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < m; i++)                          // seed all gates
            for (int j = 0; j < n; j++)
                if (rooms[i][j] == 0) queue.offer(new int[]{i, j});

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int[] d : dirs) {
                int nx = cell[0] + d[0], ny = cell[1] + d[1];
                if (nx >= 0 && nx < m && ny >= 0 && ny < n && rooms[nx][ny] == Integer.MAX_VALUE) {
                    rooms[nx][ny] = rooms[cell[0]][cell[1]] + 1;   // nearest gate found
                    queue.offer(new int[]{nx, ny});
                }
            }
        }
    }
}
```

```cpp
#include <queue>
#include <vector>
#include <climits>

class WallsAndGates {
public:
    /**
     * @param rooms grid: -1 wall, 0 gate, INT_MAX empty room (filled in place)
     */
    void wallsAndGates(std::vector<std::vector<int>>& rooms) {
        int m = rooms.size(), n = rooms[0].size();
        std::vector<std::pair<int,int>> dirs = {{0,1},{0,-1},{1,0},{-1,0}};
        std::queue<std::pair<int,int>> queue;

        for (int i = 0; i < m; i++)                          // seed all gates
            for (int j = 0; j < n; j++)
                if (rooms[i][j] == 0) queue.push({i, j});

        while (!queue.empty()) {
            auto [x, y] = queue.front(); queue.pop();
            for (auto& [dx, dy] : dirs) {
                int nx = x + dx, ny = y + dy;
                if (nx >= 0 && nx < m && ny >= 0 && ny < n && rooms[nx][ny] == INT_MAX) {
                    rooms[nx][ny] = rooms[x][y] + 1;        // nearest gate found
                    queue.push({nx, ny});
                }
            }
        }
    }
};
```

```python
from collections import deque

def walls_and_gates(rooms: list[list[int]]) -> None:
    """
    @param rooms: grid: -1 wall, 0 gate, 2^31-1 empty room (filled in place)
    """
    rows, cols = len(rooms), len(rooms[0])
    queue = deque()

    for r in range(rows):                    # seed all gates
        for c in range(cols):
            if rooms[r][c] == 0:
                queue.append((r, c))

    while queue:
        x, y = queue.popleft()
        for dx, dy in ((0, 1), (0, -1), (1, 0), (-1, 0)):
            nx, ny = x + dx, y + dy
            if 0 <= nx < rows and 0 <= ny < cols and rooms[nx][ny] == 2**31 - 1:
                rooms[nx][ny] = rooms[x][y] + 1    # nearest gate found
                queue.append((nx, ny))
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param rooms grid: -1 wall, 0 gate, i32::MAX empty room (filled in place)
    pub fn walls_and_gates(rooms: &mut Vec<Vec<i32>>) {
        let (m, n) = (rooms.len(), rooms[0].len());
        let mut queue: VecDeque<(usize, usize)> = VecDeque::new();

        for i in 0..m {                                   // seed all gates
            for j in 0..n {
                if rooms[i][j] == 0 { queue.push_back((i, j)); }
            }
        }

        while let Some((x, y)) = queue.pop_front() {
            for (dx, dy) in [(0i32, 1i32), (0, -1), (1, 0), (-1, 0)] {
                let (nx, ny) = (x as i32 + dx, y as i32 + dy);
                if nx >= 0 && nx < m as i32 && ny >= 0 && ny < n as i32
                   && rooms[nx as usize][ny as usize] == i32::MAX {
                    rooms[nx as usize][ny as usize] = rooms[x][y] + 1;   // nearest gate
                    queue.push_back((nx as usize, ny as usize));
                }
            }
        }
    }
}
```

## Dry run

**Input:** `rooms = [[INF,-1,0,INF],[INF,INF,INF,-1],[INF,-1,INF,-1],[0,-1,INF,INF]]`.

```
seed: gates at (0,2) and (3,0).  queue = [(0,2),(3,0)]

level 0: pop (0,2): writes (0,3)=1, (1,2)=1.   pop (3,0): writes (2,0)=1.
level 1: (0,3): no INF neighbors.  (1,2): writes (1,1)=2.  (2,0): no neighbors (2,1 is -1).
level 2: (1,1): writes (0,1)? -1 no; (2,1)? -1; (1,0)=2.
level 3: (1,0): writes (0,0)=3.

Output: [[3,-1,0,1],[2,2,1,-1],[1,-1,2,-1],[0,-1,3,4]] ✓
```

The grid fills in distance rings around both gates simultaneously: `(0,3)` and `(1,2)` get 1 (adjacent to the gate at `(0,2)`), then `(1,1)` gets 2, then `(1,0)` gets 2 via the `(3,0)` gate's ring — and `(0,0)` ends at 3 (two rings from `(0,2)`'s gate). The `== INF` guard writes each room exactly once — its nearest distance.

## Complexity

**Time.** Each room visited once:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** The queue:

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Rotting Oranges** ([6.14](../ch06-graphs/rotting-oranges.md)) — the same multi-source BFS counting *minutes*; this page writes *distances* (no level fence needed).
- **Shortest Distance From All Buildings** (`grid/ShortestDistanceFromAllBuildings.kt`) — the inverse: BFS from each building, summing distances.
- **01 Matrix / As Far From Land As Possible** — the same "multi-source BFS from zeros" pattern.
- **Interview follow-up:** "Why seed all gates instead of BFS per room?" BFS per room is O(cells²) — each empty room re-explores the grid. Multi-source runs *one* BFS where the first visit to a room is provably its nearest gate (BFS's level ordering from all sources). The `INF`-check is both the distance test and the visited set — no separate bookkeeping.
