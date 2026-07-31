# 6.20 Making A Large Island

> **Source:** [`src/main/kotlin/grid/MakingALargeIsland.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/MakingALargeIsland.kt)
> **Pattern:** island IDs + neighbor sum · **Core page**

## The Problem

Flip **at most one** 0 to 1 to maximize the island area.

- Constraints: n ≤ 500.

## Examples

```
Input:  grid = [[1,0],[0,1]]   -> Output: 3   (flip (0,1) or (1,0): joins both 1s)
Input:  grid = [[1,1],[1,0]]   -> Output: 4
```

## Intuition — label every island, then test each 0's neighbors

Two passes:

1. **Label** — DFS each island, stamping cells with an `islandId` (2, 3, ...) and recording `islandSizes[id]`;
2. **Test** — for each 0, sum the sizes of its *distinct* neighboring islands + 1 (itself).

```kotlin
var islandId = 2
for (r, c) where grid[r][c] == 1: size = dfs(r, c, islandId); islandSizes[islandId] = size; islandId++

for (r, c) where grid[r][c] == 0:
    val neighbors = distinct island IDs around (r, c)
    candidate = 1 + neighbors.sumOf { islandSizes[it] }
    maxArea = maxOf(maxArea, candidate)
```

**Why IDs instead of sizes per component?** The [6.17](max-area-of-island.md) sink approach destroys the labels — here each island's identity must persist for the neighbor-sum. The ID stamp is the visited-set AND the lookup key.

**Why distinct neighbors?** A 0 surrounded by the same island on two sides joins it once — `setOf(ids)` dedupes (the classic `[[1,1],[1,0]]` correctness point).

## Approach 1 — For each 0, BFS the union (O(n⁴))

Flip, flood, measure, revert: correct, slow.

## Approach 2 — Label + neighbor sum (the repo's version, optimal)

```kotlin
class MakingALargeIsland {
    /**
     * @param grid 0/1 grid (mutated with island IDs)
     * @return     max island area after flipping one 0
     */
    fun largestIsland(grid: Array<IntArray>): Int {
        val n = grid.size
        val directions = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)
        val islandSizes = mutableMapOf<Int, Int>()
        var maxArea = 0
        var islandId = 2

        fun dfs(r: Int, c: Int, id: Int): Int {
            if (r !in grid.indices || c !in grid[0].indices || grid[r][c] != 1) return 0
            grid[r][c] = id
            var size = 1
            for ((dr, dc) in directions) {
                size += dfs(r + dr, c + dc, id)
            }
            return size
        }

        for (r in 0 until n) {
            for (c in 0 until n) {
                if (grid[r][c] == 1) {
                    val size = dfs(r, c, islandId)
                    islandSizes[islandId] = size
                    maxArea = maxOf(maxArea, size)
                    islandId++
                }
            }
        }

        for (r in 0 until n) {
            for (c in 0 until n) {
                if (grid[r][c] == 0) {
                    val neighborIds = mutableSetOf<Int>()
                    for ((dr, dc) in directions) {
                        val nr = r + dr
                        val nc = c + dc
                        if (nr in 0 until n && nc in 0 until n && grid[nr][nc] > 1) {
                            neighborIds.add(grid[nr][nc])
                        }
                    }
                    maxArea = maxOf(maxArea, 1 + neighborIds.sumOf { islandSizes[it] ?: 0 })
                }
            }
        }
        return maxArea
    }
}
```

```java
import java.util.*;

public class MakingALargeIsland {
    private int n;
    private int[][] grid;
    private Map<Integer, Integer> sizes = new HashMap<>();
    private int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    private int dfs(int r, int c, int id) {
        if (r < 0 || c < 0 || r >= n || c >= n || grid[r][c] != 1) return 0;
        grid[r][c] = id;

        int size = 1;
        for (int[] d : dirs) size += dfs(r + d[0], c + d[1], id);
        return size;
    }

    /**
     * @param grid 0/1 grid (mutated with island IDs)
     * @return     max island area after flipping one 0
     */
    public int largestIsland(int[][] grid) {
        this.grid = grid;
        n = grid.length;
        int max = 0, id = 2;

        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++)
                if (grid[r][c] == 1) {
                    int size = dfs(r, c, id);
                    sizes.put(id++, size);
                    max = Math.max(max, size);
                }

        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++)
                if (grid[r][c] == 0) {
                    Set<Integer> seen = new HashSet<>();
                    int candidate = 1;
                    for (int[] d : dirs) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] > 1
                                && seen.add(grid[nr][nc])) {
                            candidate += sizes.get(grid[nr][nc]);
                        }
                    }
                    max = Math.max(max, candidate);
                }
        return max;
    }
}
```

```cpp
#include <vector>
#include <unordered_map>
#include <unordered_set>
#include <algorithm>

class MakingALargeIsland {
    int n;
    std::vector<std::vector<int>> grid;
    std::unordered_map<int, int> sizes;
    int dirs[4][2] = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    int dfs(int r, int c, int id) {
        if (r < 0 || c < 0 || r >= n || c >= n || grid[r][c] != 1) return 0;
        grid[r][c] = id;

        int size = 1;
        for (auto& d : dirs) size += dfs(r + d[0], c + d[1], id);
        return size;
    }

public:
    /**
     * @param grid 0/1 grid (mutated with island IDs)
     * @return     max island area after flipping one 0
     */
    int largestIsland(std::vector<std::vector<int>>& grid) {
        this->grid = grid;
        n = grid.size();
        int max = 0, id = 2;

        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++)
                if (grid[r][c] == 1) {
                    int size = dfs(r, c, id);
                    sizes[id++] = size;
                    max = std::max(max, size);
                }

        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++)
                if (grid[r][c] == 0) {
                    std::unordered_set<int> seen;
                    int candidate = 1;
                    for (auto& d : dirs) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] > 1
                                && seen.insert(grid[nr][nc]).second) {
                            candidate += sizes[grid[nr][nc]];
                        }
                    }
                    max = std::max(max, candidate);
                }
        return max;
    }
};
```

```python
def largest_island(grid: list[list[int]]) -> int:
    """
    @param grid: 0/1 grid (mutated with island IDs)
    @return:      max island area after flipping one 0
    """
    n = len(grid)
    dirs = ((0, 1), (0, -1), (1, 0), (-1, 0))
    sizes = {}
    best = 0
    island_id = 2

    def dfs(r, c, iid):
        if not (0 <= r < n and 0 <= c < n) or grid[r][c] != 1:
            return 0
        grid[r][c] = iid
        return 1 + sum(dfs(r + dr, c + dc, iid) for dr, dc in dirs)

    for r in range(n):
        for c in range(n):
            if grid[r][c] == 1:
                size = dfs(r, c, island_id)
                sizes[island_id] = size
                best = max(best, size)
                island_id += 1

    for r in range(n):
        for c in range(n):
            if grid[r][c] == 0:
                seen = set()
                for dr, dc in dirs:
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < n and 0 <= nc < n and grid[nr][nc] > 1:
                        seen.add(grid[nr][nc])
                best = max(best, 1 + sum(sizes[i] for i in seen))

    return best
```

```rust
use std::collections::{HashMap, HashSet};

impl Solution {
    /// @param grid 0/1 grid (mutated with island IDs)
    /// @return     max island area after flipping one 0
    pub fn largest_island(mut grid: Vec<Vec<i32>>) -> i32 {
        let n = grid.len();
        let dirs = [(0, 1), (0, -1), (1, 0), (-1, 0)];
        let mut sizes: HashMap<i32, i32> = HashMap::new();
        let mut best = 0;
        let mut island_id = 2;

        fn dfs(grid: &mut Vec<Vec<i32>>, r: i32, c: i32, id: i32, dirs: &[(i32, i32)]) -> i32 {
            if r < 0 || c < 0 || r >= grid.len() as i32 || c >= grid[0].len() as i32
                || grid[r as usize][c as usize] != 1 { return 0; }
            grid[r as usize][c as usize] = id;
            1 + dirs.iter().map(|&(dr, dc)| dfs(grid, r + dr, c + dc, id, dirs)).sum::<i32>()
        }

        for r in 0..n {
            for c in 0..n {
                if grid[r][c] == 1 {
                    let size = dfs(&mut grid, r as i32, c as i32, island_id, &dirs);
                    sizes.insert(island_id, size);
                    best = best.max(size);
                    island_id += 1;
                }
            }
        }

        for r in 0..n {
            for c in 0..n {
                if grid[r][c] == 0 {
                    let mut seen = HashSet::new();
                    for (dr, dc) in dirs {
                        let (nr, nc) = (r as i32 + dr, c as i32 + dc);
                        if nr >= 0 && nc >= 0 && nr < n as i32 && nc < n as i32 && grid[nr as usize][nc as usize] > 1 {
                            seen.insert(grid[nr as usize][nc as usize]);
                        }
                    }
                    best = best.max(1 + seen.iter().map(|i| sizes[i]).sum::<i32>());
                }
            }
        }
        best
    }
}
```

## Dry run

**Input:** `grid = [[1,0],[0,1]]`.

```
label: (0,0) island 2, size 1.  (1,1) island 3, size 1.
test (0,1) 0: neighbors (0,0)=2, (1,1)=3 -> candidate 1 + 1 + 1 = 3.
test (1,0) 0: same -> 3.

Output: 3 ✓
Input: [[1,1],[1,0]]: island 2 at (0,0),(0,1),(1,0) size 3.  test (1,1): neighbors all id 2
(one distinct) -> 1 + 3 = 4 ✓
```

The dedupe is the correctness detail: `[[1,1],[1,0]]`'s flipped cell touches the same island on two sides — the `Set` counts it once, giving 4, not 5. The label pass's IDs make each island's size O(1)-lookup.

## Complexity

**Time.** Label + test passes:

$$
T(n) = O(n^2)
$$

**Space.** Sizes map + recursion:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Max Area Of Island** ([6.17](max-area-of-island.md)) — the sink version (no IDs needed).
- **Number Of Islands** — the counting sibling.
- **Interview follow-up:** "Why stamp IDs instead of a visited set?" The test pass needs each island's *size by identity* — a boolean visited set can't look up "which island is here?". The numeric ID is both the visited mark and the map key; the `> 1` check in the test pass reads it directly.
