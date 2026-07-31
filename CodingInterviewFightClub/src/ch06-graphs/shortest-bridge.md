# 6.26 Shortest Bridge

> **Source**: [`src/main/kotlin/grid/ShortestBridge.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/ShortestBridge.kt)
> **Pattern**: DFS sink + multi-source BFS · **Core page**

## The Problem

The shortest bridge (number of 0s to flip) connecting two islands.

- Constraints: n ≤ 100.

## Examples

```
Input:  grid = [[0,1],[1,0]]              -> Output: 1
Input:  grid = [[1,1,1,1,1],[1,0,0,0,1],[1,0,1,0,1],[1,0,0,0,1],[1,1,1,1,1]]  -> Output: 1
```

## Intuition — label island 1 via DFS, expand with BFS until island 2

1. **DFS** the first island, marking cells as `2` and seeding a queue;
2. **BFS** from all island-2 cells through water until any `1` is touched — the BFS depth is the bridge length.

```kotlin
fun dfs(x: Int, y: Int) {
    if (x !in 0 until n || y !in 0 until n || grid[x][y] != 1) return
    grid[x][y] = 2          // mark the first island
    queue.add(x to y)       // seed the BFS
    directions.forEach { (dx, dy) -> dfs(x + dx, y + dy) }
}

// find the first 1, dfs it, then BFS the queue
var distance = 0
while (queue.isNotEmpty()) {
    repeat(queue.size) {
        val (x, y) = queue.removeFirst()
        for ((dx, dy) in directions) {
            val nx = x + dx; val ny = y + dy
            if (nx !in 0 until n || ny !in 0 until n) continue
            when (grid[nx][ny]) {
                1 -> return distance          // reached island 2!
                0 -> { grid[nx][ny] = 2; queue.add(nx to ny) }
            }
        }
    }
    distance++
}
return -1
```

**Why the DFS+queue combo?** The DFS labels the *whole* first island (any start would do); the BFS then expands from every island cell simultaneously — the level-fenced distance IS the bridge length. The [6.17](max-area-of-island.md) sink + [6.14](../ch06-graphs/rotting-oranges.md) multi-source BFS in one problem.

**Why mark water as `2` during BFS?** The visited-set-in-grid trick — expanded water can't be re-expanded; island-1 cells (`2`) are skipped implicitly by the `when`.

## Approach 1 — BFS between every island-1/2 pair (O(n⁴))

All-pairs distance: correct, slow.

## Approach 2 — DFS label + multi-source BFS (the repo's version, optimal)

```kotlin
class ShortestBridge {
    /**
     * @param grid 0/1 grid with two islands
     * @return     min water cells to flip
     */
    fun shortestBridge(grid: Array<IntArray>): Int {
        val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
        val queue = ArrayDeque<Pair<Int, Int>>()
        val n = grid.size

        fun dfs(x: Int, y: Int) {
            if (x !in 0 until n || y !in 0 until n || grid[x][y] != 1) return
            grid[x][y] = 2
            queue.add(x to y)
            directions.forEach { (dx, dy) -> dfs(x + dx, y + dy) }
        }

        outer@ for (i in 0 until n) for (j in 0 until n) {
            if (grid[i][j] == 1) { dfs(i, j); break@outer }
        }

        var distance = 0
        while (queue.isNotEmpty()) {
            repeat(queue.size) {
                val (x, y) = queue.removeFirst()

                for ((dx, dy) in directions) {
                    val nx = x + dx
                    val ny = y + dy
                    if (nx !in 0 until n || ny !in 0 until n) continue

                    when (grid[nx][ny]) {
                        1 -> return distance
                        0 -> {
                            grid[nx][ny] = 2
                            queue.add(nx to ny)
                        }
                    }
                }
            }
            distance++
        }
        return -1
    }
}
```

```java
import java.util.*;

public class ShortestBridge {
    private int[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private Queue<int[]> queue = new LinkedList<>();

    private void dfs(int[][] grid, int x, int y) {
        int n = grid.length;
        if (x < 0 || y < 0 || x >= n || y >= n || grid[x][y] != 1) return;

        grid[x][y] = 2;
        queue.offer(new int[]{x, y});
        for (int[] d : dirs) dfs(grid, x + d[0], y + d[1]);
    }

    /**
     * @param grid 0/1 grid with two islands
     * @return     min water cells to flip
     */
    public int shortestBridge(int[][] grid) {
        int n = grid.length;

        outer:
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (grid[i][j] == 1) { dfs(grid, i, j); break outer; }

        int distance = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int s = 0; s < size; s++) {
                int[] cell = queue.poll();

                for (int[] d : dirs) {
                    int nx = cell[0] + d[0], ny = cell[1] + d[1];
                    if (nx < 0 || ny < 0 || nx >= n || ny >= n) continue;

                    if (grid[nx][ny] == 1) return distance;
                    if (grid[nx][ny] == 0) {
                        grid[nx][ny] = 2;
                        queue.offer(new int[]{nx, ny});
                    }
                }
            }
            distance++;
        }
        return -1;
    }
}
```

```cpp
#include <vector>
#include <queue>

class ShortestBridge {
    int dirs[4][2] = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    std::queue<std::pair<int, int>> queue;

    void dfs(std::vector<std::vector<int>>& grid, int x, int y) {
        int n = grid.size();
        if (x < 0 || y < 0 || x >= n || y >= n || grid[x][y] != 1) return;

        grid[x][y] = 2;
        queue.push({x, y});
        for (auto& d : dirs) dfs(grid, x + d[0], y + d[1]);
    }

public:
    /**
     * @param grid 0/1 grid with two islands
     * @return     min water cells to flip
     */
    int shortestBridge(std::vector<std::vector<int>>& grid) {
        int n = grid.size();

        bool found = false;
        for (int i = 0; i < n && !found; i++)
            for (int j = 0; j < n && !found; j++)
                if (grid[i][j] == 1) { dfs(grid, i, j); found = true; }

        int distance = 0;
        while (!queue.empty()) {
            int size = queue.size();
            for (int s = 0; s < size; s++) {
                auto [x, y] = queue.front(); queue.pop();

                for (auto& d : dirs) {
                    int nx = x + d[0], ny = y + d[1];
                    if (nx < 0 || ny < 0 || nx >= n || ny >= n) continue;

                    if (grid[nx][ny] == 1) return distance;
                    if (grid[nx][ny] == 0) {
                        grid[nx][ny] = 2;
                        queue.push({nx, ny});
                    }
                }
            }
            distance++;
        }
        return -1;
    }
};
```

```python
from collections import deque

def shortest_bridge(grid: list[list[int]]) -> int:
    """
    @param grid: 0/1 grid with two islands
    @return:     min water cells to flip
    """
    n = len(grid)
    dirs = ((0, 1), (1, 0), (0, -1), (-1, 0))
    queue = deque()

    def dfs(x, y):
        if not (0 <= x < n and 0 <= y < n) or grid[x][y] != 1:
            return
        grid[x][y] = 2
        queue.append((x, y))
        for dx, dy in dirs:
            dfs(x + dx, y + dy)

    for i in range(n):
        for j in range(n):
            if grid[i][j] == 1:
                dfs(i, j)
                break
        else:
            continue
        break

    distance = 0
    while queue:
        for _ in range(len(queue)):
            x, y = queue.popleft()

            for dx, dy in dirs:
                nx, ny = x + dx, y + dy
                if not (0 <= nx < n and 0 <= ny < n):
                    continue

                if grid[nx][ny] == 1:
                    return distance
                if grid[nx][ny] == 0:
                    grid[nx][ny] = 2
                    queue.append((nx, ny))

        distance += 1

    return -1
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param grid 0/1 grid with two islands
    /// @return     min water cells to flip
    pub fn shortest_bridge(grid: Vec<Vec<i32>>) -> i32 {
        let n = grid.len();
        let dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)];
        let mut g = grid;
        let mut queue: VecDeque<(usize, usize)> = VecDeque::new();

        fn dfs(g: &mut Vec<Vec<i32>>, x: i32, y: i32, queue: &mut VecDeque<(usize, usize)>) {
            let n = g.len() as i32;
            if x < 0 || y < 0 || x >= n || y >= n || g[x as usize][y as usize] != 1 { return; }
            g[x as usize][y as usize] = 2;
            queue.push_back((x as usize, y as usize));
            for (dx, dy) in [(0, 1), (1, 0), (0, -1), (-1, 0)] {
                dfs(g, x + dx, y + dy, queue);
            }
        }

        'outer: for i in 0..n {
            for j in 0..n {
                if g[i][j] == 1 { dfs(&mut g, i as i32, j as i32, &mut queue); break 'outer; }
            }
        }

        let mut distance = 0;
        while !queue.is_empty() {
            for _ in 0..queue.len() {
                let (x, y) = queue.pop_front().unwrap();

                for (dx, dy) in dirs {
                    let (nx, ny) = (x as i32 + dx, y as i32 + dy);
                    if nx < 0 || ny < 0 || nx >= n as i32 || ny >= n as i32 { continue; }
                    let (ux, uy) = (nx as usize, ny as usize);

                    if g[ux][uy] == 1 { return distance; }
                    if g[ux][uy] == 0 {
                        g[ux][uy] = 2;
                        queue.push_back((ux, uy));
                    }
                }
            }
            distance += 1;
        }
        -1
    }
}
```

## Dry run

**Input:** `grid = [[0,1],[1,0]]`.

```
dfs(0,1): mark 2.  queue [(0,1)].  neighbors (1,1)=0 not 1; (0,0)=0 -> done.
BFS distance=0: pop (0,1).  neighbors: (1,1)=0 -> mark 2, enqueue.  (0,0)=0 -> mark, enqueue.
distance=1: pop (1,1): neighbor (1,0)=1 -> return 1 ✓
```

The level fence counts water rings: distance 0 is the island itself, distance 1 the first water ring, etc. The first `1` touched is island 2 — the ring number is the bridge length.

## Complexity

**Time.** DFS + BFS each cell once:

$$
T(n) = O(n^2)
$$

**Space.** The queue:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Max Area Of Island** ([6.17](max-area-of-island.md)) — the DFS sink engine.
- **Rotting Oranges** ([6.14](rotting-oranges.md)) — the multi-source BFS fence.
- **Interview follow-up:** "Why is the bridge = the BFS ring number?" The first ring of water touching island 2 is exactly the water cells that *separate* the islands — flipping them connects the two. BFS's level fencing measures that separation in minimum rings.
