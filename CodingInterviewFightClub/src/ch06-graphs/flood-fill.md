# 6.10 Flood Fill

> **Source:** [`src/main/kotlin/grid/FloodFill.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/FloodFill.kt)
> **Pattern:** DFS/BFS on a grid · **Core page**

## The Problem

Given an `image` (2-D grid of pixel colors), a starting cell `(sr, sc)`, and a `color`: change the starting cell and **every connected cell with the same original color** to the new color.

- Constraints: $m, n \le 50$; colors fit in `Int`.

## Examples

```
Input:  image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2
Output: [[2,2,2],[2,2,0],[2,0,1]]
```

## Intuition — the canonical grid traversal

Flood fill *is* BFS/DFS on a grid: the starting cell is the seed; its 4-directional neighbors are the frontier; a neighbor is visited only if it has the **same original color** (the "connectivity" condition). The repo's DFS:

```
dfs(r, c):
    if out of bounds or image[r][c] != startColor: return
    image[r][c] = color                      # paint
    dfs(r+1, c); dfs(r-1, c); dfs(r, c+1); dfs(r, c-1)
```

**Why capture `startColor` before mutating?** Once painted, a cell's color changes — the connectivity test must compare against the *original* color, saved before the loop. (And the `startColor == color` early return avoids an infinite re-paint loop.)

**Grid vs graph:** this is the [6.1](word-ladder.md)/[6.2](clone-graph.md) BFS/DFS machinery with implicit neighbors (the 4 offsets) instead of an adjacency list. The `visited`-equivalence is "painted" — a painted cell is never re-queued because its color no longer matches `startColor`.

**The 4-directional offsets** — `(+1,0), (-1,0), (0,+1), (0,-1)` — are the grid's "edges". BFS (queue) and DFS (recursion/stack) both work; DFS is the shortest code, BFS the "paint outward in rings" intuition. This is also the engine behind [17.7](../ch17-advanced-graphs/evaluate-division.md)'s grid cousins and every island-counting problem.

## Approach 1 — BFS with a queue (also optimal)

Seed the queue, paint-and-enqueue same-color neighbors: O(mn), the "spread in rings" view of the same algorithm.

## Approach 2 — Recursive DFS (the repo's version, optimal)

```kotlin
class FloodFill {
    /**
     * @param image pixel grid
     * @param sr    start row
     * @param sc    start column
     * @param color new color for the connected region
     * @return      the painted grid
     */
    fun floodFill(image: Array<IntArray>, sr: Int, sc: Int, color: Int): Array<IntArray> {
        val startColor = image[sr][sc]
        if (startColor == color) return image          // nothing to do (and avoids re-paint loops)

        fun dfs(r: Int, c: Int) {
            if (r !in image.indices || c !in image[0].indices || image[r][c] != startColor) return
            image[r][c] = color                        // paint
            dfs(r + 1, c); dfs(r - 1, c)               // down, up
            dfs(r, c + 1); dfs(r, c - 1)               // right, left
        }

        dfs(sr, sc)
        return image
    }
}
```

```java
public class FloodFill {
    /**
     * @param image pixel grid
     * @param sr    start row
     * @param sc    start column
     * @param color new color for the connected region
     * @return      the painted grid
     */
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int startColor = image[sr][sc];
        if (startColor == color) return image;         // nothing to do

        dfs(image, sr, sc, startColor, color);
        return image;
    }

    private void dfs(int[][] image, int r, int c, int startColor, int color) {
        if (r < 0 || r >= image.length || c < 0 || c >= image[0].length
                || image[r][c] != startColor) return;
        image[r][c] = color;                           // paint
        dfs(image, r + 1, c, startColor, color);       // down, up, right, left
        dfs(image, r - 1, c, startColor, color);
        dfs(image, r, c + 1, startColor, color);
        dfs(image, r, c - 1, startColor, color);
    }
}
```

```cpp
#include <vector>

class FloodFill {
    void dfs(std::vector<std::vector<int>>& image, int r, int c, int start, int color) {
        if (r < 0 || r >= (int)image.size() || c < 0 || c >= (int)image[0].size()
            || image[r][c] != start) return;
        image[r][c] = color;                            // paint
        dfs(image, r + 1, c, start, color);             // down, up, right, left
        dfs(image, r - 1, c, start, color);
        dfs(image, r, c + 1, start, color);
        dfs(image, r, c - 1, start, color);
    }

public:
    /**
     * @param image pixel grid
     * @param sr    start row
     * @param sc    start column
     * @param color new color for the connected region
     * @return      the painted grid
     */
    std::vector<std::vector<int>> floodFill(std::vector<std::vector<int>>& image,
                                            int sr, int sc, int color) {
        int start = image[sr][sc];
        if (start != color) dfs(image, sr, sc, start, color);
        return image;
    }
};
```

```python
def flood_fill(image: list[list[int]], sr: int, sc: int, color: int) -> list[list[int]]:
    """
    @param image: pixel grid
    @param sr:    start row
    @param sc:    start column
    @param color: new color for the connected region
    @return:      the painted grid
    """
    start_color = image[sr][sc]
    if start_color == color:
        return image                     # nothing to do

    def dfs(r: int, c: int) -> None:
        if r < 0 or r >= len(image) or c < 0 or c >= len(image[0]) or image[r][c] != start_color:
            return
        image[r][c] = color              # paint
        dfs(r + 1, c); dfs(r - 1, c)     # down, up
        dfs(r, c + 1); dfs(r, c - 1)     # right, left

    dfs(sr, sc)
    return image
```

```rust
impl Solution {
    /// @param image pixel grid
    /// @param sr    start row
    /// @param sc    start column
    /// @param color new color for the connected region
    /// @return      the painted grid
    pub fn flood_fill(image: Vec<Vec<i32>>, sr: i32, sc: i32, color: i32) -> Vec<Vec<i32>> {
        let mut image = image;
        let start = image[sr as usize][sc as usize];
        if start == color { return image; }              // nothing to do

        fn dfs(image: &mut Vec<Vec<i32>>, r: i32, c: i32, start: i32, color: i32) {
            let (m, n) = (image.len() as i32, image[0].len() as i32);
            if r < 0 || r >= m || c < 0 || c >= n || image[r as usize][c as usize] != start {
                return;
            }
            image[r as usize][c as usize] = color;       // paint
            dfs(image, r + 1, c, start, color);          // down, up, right, left
            dfs(image, r - 1, c, start, color);
            dfs(image, r, c + 1, start, color);
            dfs(image, r, c - 1, start, color);
        }

        dfs(&mut image, sr, sc, start, color);
        image
    }
}
```

## Dry run

**Input:** `image = [[1,1,1],[1,1,0],[1,0,1]]`, `sr = 1, sc = 1, color = 2`. `startColor = 1`.

```
dfs(1,1): paint -> 2.
  dfs(2,1): [1][0]? wait — row 2, col 1 = image[2][1] = 0 != 1 -> return.
  dfs(0,1): image[0][1] = 1 -> paint 2.
    dfs(1,1): already 2 != 1 -> return.  dfs(-1,1): out of bounds.
    dfs(0,0): paint 2.  dfs(0,2): paint 2.
      (0,2)'s neighbors: (0,3) oob, (1,2): image[1][2] = 0 -> return; (-1,2) oob; (0,1) painted.
    dfs(1,0): image[1][0] = 1 -> paint 2.
      neighbors: (2,0): image[2][0] = 1 -> paint 2.
        (2,0)'s neighbors: (3,0) oob, (1,0) painted, (2,-1) oob, (2,1) 0 -> return.
      (1,-1) oob, (1,1) painted, (0,0) painted.
    ... the region {all 1s connected to (1,1)} becomes 2.

Output: [[2,2,2],[2,2,0],[2,0,1]] ✓
```

The painted-2 cells act as the visited set: once a cell becomes 2, the `!= startColor` test rejects re-visits — no separate `visited` array needed. The `0`s stay untouched because they never matched the start color.

## Complexity

**Time.** Each cell visited at most once:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** Recursion depth (worst case the whole grid):

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Number Of Islands / island-perimeter family** (`grid/`) — the same 4-directional DFS counting regions instead of painting them.
- **Max Area Of Island / Making A Large Island** (`grid/MakingALargeIsland_AnotherApproach.kt`) — flood fill as a building block for region stats.
- **Interview follow-up:** "Why does painting double as the visited set?" The connectivity test is `image[r][c] == startColor`; a painted cell no longer matches, so it can never be enqueued/recursed again. The mutation *is* the bookkeeping — which is also why the `startColor == color` early return matters (else painting wouldn't change the test and the recursion would loop).
