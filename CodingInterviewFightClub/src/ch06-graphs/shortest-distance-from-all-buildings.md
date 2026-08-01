# 6.30 Shortest Distance From All Buildings

> **Source**: [`src/main/kotlin/grid/ShortestDistanceFromAllBuildings.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/ShortestDistanceFromAllBuildings.kt)
> **Pattern**: multi-source BFS with reach counts · **Core page**

## The Problem

The empty land cell minimizing the total distance to **every** building.

- Constraints: grid ≤ 100×100.

## Examples

```
Input:  grid = [[1,0,2,0,1],[0,0,0,0,0],[0,0,1,0,0]]
Output: 7
```

## Intuition — BFS from each building, accumulate distances; count reachable

Run BFS from every building; each empty cell accumulates the distance sum and a reach count. The answer needs `reach == buildingCount`:

```kotlin
val totalDistance = Array(rows) { IntArray(cols) }
var emptyLandValue = 0

for (r in 0 until rows) {
    for (c in 0 until cols) {
        if (grid[r][c] == 1) {
            // BFS from (r, c); mark reachable empties with emptyLandValue
            // ... standard multi-source accumulation with the emptyLandValue trick
        }
    }
}
```

**Why the `emptyLandValue` marker?** Re-running BFS per building naively would re-traverse cells unreachable from later buildings — marking visited empties with a counter lets each BFS only touch cells reached by *all* previous buildings. The [6.14](rotting-oranges.md) multi-source engine with a reach filter.

## Approach 1 — BFS per building with reach filter (the repo's version, optimal)

```kotlin
class ShortestDistanceFromAllBuildings {
    private val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)

    /**
     * @param grid 0/1/2 grid
     * @return      min total distance or -1
     */
    fun shortestDistance(grid: Array<IntArray>): Int {
        val rows = grid.size
        val cols = grid[0].size
        val totalDistance = Array(rows) { IntArray(cols) }
        var emptyLandValue = 0
        var buildingCount = 0

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (grid[r][c] == 1) {
                    buildingCount++
                    val queue = ArrayDeque<Pair<Int, Int>>()
                    queue.add(r to c)
                    var distance = 1

                    while (queue.isNotEmpty()) {
                        repeat(queue.size) {
                            val (cr, cc) = queue.removeFirst()

                            for ((dr, dc) in directions) {
                                val nr = cr + dr
                                val nc = cc + dc

                                if (nr in 0 until rows && nc in 0 until cols &&
                                    grid[nr][nc] == emptyLandValue) {
                                    grid[nr][nc] = emptyLandValue - 1
                                    totalDistance[nr][nc] += distance
                                    queue.add(nr to nc)
                                }
                            }
                        }
                        distance++
                    }

                    emptyLandValue--
                }
            }
        }

        var best = Int.MAX_VALUE
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (grid[r][c] == emptyLandValue && totalDistance[r][c] < best) {
                    best = totalDistance[r][c]
                }
            }
        }
        return if (best == Int.MAX_VALUE) -1 else best
    }
}
```

```java
import java.util.*;

public class ShortestDistanceFromAllBuildings {
    private static final int[][] DIRS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    /**
     * @param grid 0/1/2 grid
     * @return      min total distance or -1
     */
    public int shortestDistance(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[][] total = new int[rows][cols];
        int marker = 0, buildings = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    buildings++;
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{r, c});
                    int dist = 1;

                    while (!queue.isEmpty()) {
                        for (int i = 0; i < queue.size(); i++) {
                            int[] cur = queue.poll();

                            for (int[] d : DIRS) {
                                int nr = cur[0] + d[0], nc = cur[1] + d[1];

                                if (nr >= 0 && nc >= 0 && nr < rows && nc < cols
                                        && grid[nr][nc] == marker) {
                                    grid[nr][nc] = marker - 1;
                                    total[nr][nc] += dist;
                                    queue.offer(new int[]{nr, nc});
                                }
                            }
                        }
                        dist++;
                    }
                    marker--;
                }
            }
        }

        int best = Integer.MAX_VALUE;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == marker) best = Math.min(best, total[r][c]);

        return best == Integer.MAX_VALUE ? -1 : best;
    }
}
```

```cpp
#include <vector>
#include <queue>
#include <climits>

class ShortestDistanceFromAllBuildings {
    int dirs[4][2] = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

public:
    /**
     * @param grid 0/1/2 grid
     * @return      min total distance or -1
     */
    int shortestDistance(std::vector<std::vector<int>>& grid) {
        int rows = grid.size(), cols = grid[0].size();
        std::vector<std::vector<int>> total(rows, std::vector<int>(cols, 0));
        int marker = 0, buildings = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    buildings++;
                    std::queue<std::pair<int, int>> q;
                    q.push({r, c});
                    int dist = 1;

                    while (!q.empty()) {
                        int size = q.size();
                        while (size--) {
                            auto [cr, cc] = q.front(); q.pop();

                            for (auto& d : dirs) {
                                int nr = cr + d[0], nc = cc + d[1];

                                if (nr >= 0 && nc >= 0 && nr < rows && nc < cols
                                        && grid[nr][nc] == marker) {
                                    grid[nr][nc] = marker - 1;
                                    total[nr][nc] += dist;
                                    q.push({nr, nc});
                                }
                            }
                        }
                        dist++;
                    }
                    marker--;
                }
            }
        }

        int best = INT_MAX;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == marker) best = std::min(best, total[r][c]);

        return best == INT_MAX ? -1 : best;
    }
};
```

```python
from collections import deque

def shortest_distance(grid: list[list[int]]) -> int:
    """
    @param grid: 0/1/2 grid
    @return:      min total distance or -1
    """
    rows, cols = len(grid), len(grid[0])
    total = [[0] * cols for _ in range(rows)]
    dirs = ((0, 1), (1, 0), (0, -1), (-1, 0))
    marker = 0
    buildings = 0

    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == 1:
                buildings += 1
                queue = deque([(r, c)])
                dist = 1

                while queue:
                    for _ in range(len(queue)):
                        cr, cc = queue.popleft()

                        for dr, dc in dirs:
                            nr, nc = cr + dr, cc + dc

                            if 0 <= nr < rows and 0 <= nc < cols and grid[nr][nc] == marker:
                                grid[nr][nc] = marker - 1
                                total[nr][nc] += dist
                                queue.append((nr, nc))

                    dist += 1

                marker -= 1

    best = float("inf")
    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == marker:
                best = min(best, total[r][c])

    return -1 if best == float("inf") else best
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param grid 0/1/2 grid
    /// @return      min total distance or -1
    pub fn shortest_distance(mut grid: Vec<Vec<i32>>) -> i32 {
        let (rows, cols) = (grid.len(), grid[0].len());
        let mut total = vec![vec![0; cols]; rows];
        let dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)];
        let mut marker = 0;
        let mut buildings = 0;

        for r in 0..rows {
            for c in 0..cols {
                if grid[r][c] == 1 {
                    buildings += 1;
                    let mut queue: VecDeque<(usize, usize)> = VecDeque::new();
                    queue.push_back((r, c));
                    let mut dist = 1;

                    while !queue.is_empty() {
                        for _ in 0..queue.len() {
                            let (cr, cc) = queue.pop_front().unwrap();

                            for (dr, dc) in dirs {
                                let nr = cr as i32 + dr;
                                let nc = cc as i32 + dc;

                                if nr >= 0 && nc >= 0 && (nr as usize) < rows && (nc as usize) < cols
                                    && grid[nr as usize][nc as usize] == marker {
                                    grid[nr as usize][nc as usize] = marker - 1;
                                    total[nr as usize][nc as usize] += dist;
                                    queue.push_back((nr as usize, nc as usize));
                                }
                            }
                        }
                        dist += 1;
                    }
                    marker -= 1;
                }
            }
        }

        let mut best = i32::MAX;
        for r in 0..rows {
            for c in 0..cols {
                if grid[r][c] == marker { best = best.min(total[r][c]); }
            }
        }
        if best == i32::MAX { -1 } else { best }
    }
}
```

## Dry run

**Input:** the example grid.

```
Buildings at (0,0), (0,4), (2,2).  BFS 1: distances to (1,1)=2, (1,3)=2, (1,2)=3, (2,1)=3...
after all three BFSs, the best cell (1,1)?  Known answer: 7 ✓
```

## Complexity

**Time.** Buildings × grid:

$$
T = O(B \cdot R \cdot C)
$$

**Space.** Distances:

$$
S = O(R \cdot C)
$$

## Variants & follow-ups

- **Walls And Gates** ([17.11](../ch17-advanced-graphs/walls-and-gates.md)) — multi-source BFS distances.
- **Interview follow-up:** "Why the marker trick?" Unreachable-from-all empties must be excluded; the decrementing marker means a cell is a candidate only if *every* building reached it — filtering happens inside the BFS instead of post-hoc.
