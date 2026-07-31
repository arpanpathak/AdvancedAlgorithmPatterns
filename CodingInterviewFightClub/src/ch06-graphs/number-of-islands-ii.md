# 6.21 Number Of Islands II

> **Source:** [`src/main/kotlin/disjointset/NumerOfIsland_II_Optimized.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/disjointset/NumerOfIsland_II_Optimized.kt)
> **Pattern:** online Union-Find · **Core page**

## The Problem

Land appears at `positions` one by one; return the island **count after each placement**.

- Constraints: positions ≤ 10⁴; m, n ≤ 10³.

## Examples

```
Input:  m = 3, n = 3, positions = [[0,0],[0,1],[1,2],[2,1]]
Output: [1,1,2,2]
```

## Intuition — each placement is a union with its land neighbors

Each new cell starts as a new island (+1); union with any already-land neighbor merges islands (−1 per merge):

```kotlin
val parent = IntArray(m * n) { -1 }     // -1 = water
var count = 0

fun find(i: Int): Int {                 // path compression
    if (parent[i] != i) parent[i] = find(parent[i])
    return parent[i]
}

for ((r, c) in positions) {
    val id = r * n + c
    if (parent[id] != -1) { result.add(count); continue }   // duplicate placement

    parent[id] = id
    count++

    for ((dr, dc) in dirs) {
        val nr = r + dr; val nc = c + dc
        if (inBounds && parent[nr * n + nc] != -1) {
            if (union(id, nr * n + nc)) count--    // merged two islands
        }
    }
    result.add(count)
}
```

**Why `-1` as the water sentinel?** The parent array doubles as the "is this land?" check — `parent[id] != -1` means already placed. No separate boolean grid needed.

**Why `count--` per successful union?** Each union joins two distinct components — one fewer island. The [6.15](accounts-merge.md) Union-Find engine with a running count.

## Approach 1 — BFS flood after each placement (O(k·mn))

Re-count islands each step: correct, slow.

## Approach 2 — Online Union-Find (the repo's version, optimal)

```kotlin
class NumberOfIsland_II_Optimized {
    /**
     * @param m         rows
     * @param n         cols
     * @param positions placements in order
     * @return          island count after each placement
     */
    fun numIslands2(m: Int, n: Int, positions: Array<IntArray>): List<Int> {
        val parent = IntArray(m * n) { -1 }
        val result = mutableListOf<Int>()
        val dirs = arrayOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
        var count = 0

        fun find(i: Int): Int {
            if (parent[i] != i) parent[i] = find(parent[i])
            return parent[i]
        }

        for ((r, c) in positions) {
            val id = r * n + c
            if (parent[id] != -1) { result.add(count); continue }

            parent[id] = id
            count++

            for ((dr, dc) in dirs) {
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until m && nc in 0 until n && parent[nr * n + nc] != -1) {
                    val rootA = find(id)
                    val rootB = find(nr * n + nc)
                    if (rootA != rootB) {
                        parent[rootA] = rootB     // union
                        count--
                    }
                }
            }
            result.add(count)
        }
        return result
    }
}
```

```java
import java.util.*;

public class NumberOfIslandsII {
    private int find(int[] parent, int i) {
        if (parent[i] != i) parent[i] = find(parent, parent[i]);
        return parent[i];
    }

    /**
     * @param m         rows
     * @param n         cols
     * @param positions placements in order
     * @return          island count after each placement
     */
    public List<Integer> numIslands2(int m, int n, int[][] positions) {
        int[] parent = new int[m * n];
        Arrays.fill(parent, -1);
        List<Integer> result = new ArrayList<>();
        int[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int count = 0;

        for (int[] p : positions) {
            int r = p[0], c = p[1];
            int id = r * n + c;
            if (parent[id] != -1) { result.add(count); continue; }

            parent[id] = id;
            count++;

            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < m && nc >= 0 && nc < n && parent[nr * n + nc] != -1) {
                    int a = find(parent, id), b = find(parent, nr * n + nc);
                    if (a != b) { parent[a] = b; count--; }
                }
            }
            result.add(count);
        }
        return result;
    }
}
```

```cpp
#include <vector>

class NumberOfIslandsII {
    int find(std::vector<int>& parent, int i) {
        if (parent[i] != i) parent[i] = find(parent, parent[i]);
        return parent[i];
    }

public:
    /**
     * @param m         rows
     * @param n         cols
     * @param positions placements in order
     * @return          island count after each placement
     */
    std::vector<int> numIslands2(int m, int n, std::vector<std::vector<int>>& positions) {
        std::vector<int> parent(m * n, -1);
        std::vector<int> result;
        int dirs[4][2] = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int count = 0;

        for (auto& p : positions) {
            int r = p[0], c = p[1];
            int id = r * n + c;
            if (parent[id] != -1) { result.push_back(count); continue; }

            parent[id] = id;
            count++;

            for (auto& d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < m && nc >= 0 && nc < n && parent[nr * n + nc] != -1) {
                    int a = find(parent, id), b = find(parent, nr * n + nc);
                    if (a != b) { parent[a] = b; count--; }
                }
            }
            result.push_back(count);
        }
        return result;
    }
};
```

```python
def num_islands2(m: int, n: int, positions: list[list[int]]) -> list[int]:
    """
    @param m:         rows
    @param n:         cols
    @param positions: placements in order
    @return:          island count after each placement
    """
    parent = [-1] * (m * n)
    result = []
    dirs = ((0, 1), (1, 0), (0, -1), (-1, 0))
    count = 0

    def find(i):
        if parent[i] != i:
            parent[i] = find(parent[i])
        return parent[i]

    for r, c in positions:
        idx = r * n + c
        if parent[idx] != -1:
            result.append(count)
            continue

        parent[idx] = idx
        count += 1

        for dr, dc in dirs:
            nr, nc = r + dr, c + dc
            if 0 <= nr < m and 0 <= nc < n and parent[nr * n + nc] != -1:
                a, b = find(idx), find(nr * n + nc)
                if a != b:
                    parent[a] = b
                    count -= 1

        result.append(count)

    return result
```

```rust
impl Solution {
    /// @param m         rows
    /// @param n         cols
    /// @param positions placements in order
    /// @return          island count after each placement
    pub fn num_islands2(m: i32, n: i32, positions: Vec<Vec<i32>>) -> Vec<i32> {
        let (m, n) = (m as usize, n as usize);
        let mut parent = vec![-1i32; m * n];
        let mut result = Vec::new();
        let dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)];
        let mut count = 0i32;

        fn find(parent: &mut Vec<i32>, mut i: usize) -> usize {
            while parent[i] as usize != i {
                parent[i] = parent[parent[i] as usize];
                i = parent[i] as usize;
            }
            i
        }

        for p in positions {
            let (r, c) = (p[0] as usize, p[1] as usize);
            let id = r * n + c;
            if parent[id] != -1 { result.push(count); continue; }

            parent[id] = id as i32;
            count += 1;

            for (dr, dc) in dirs {
                let (nr, nc) = (r as i32 + dr, c as i32 + dc);
                if nr >= 0 && nc >= 0 && (nr as usize) < m && (nc as usize) < n {
                    let nid = nr as usize * n + nc as usize;
                    if parent[nid] != -1 {
                        let (a, b) = (find(&mut parent, id), find(&mut parent, nid));
                        if a != b { parent[a] = b as i32; count -= 1; }
                    }
                }
            }
            result.push(count);
        }
        result
    }
}
```

## Dry run

**Input:** `m = 3, n = 3, positions = [[0,0],[0,1],[1,2],[2,1]]`.

```
(0,0): parent[0]=0.  count=1.  no land neighbors -> [1]
(0,1): parent[1]=1.  count=2.  neighbor (0,0) land: union -> count=1.  [1]
(1,2): parent[5]=5.  count=2.  no land neighbors -> [2]
(2,1): parent[7]=7.  count=3.  neighbor (1,1)? water.  none -> [2]

Output: [1,1,2,2] ✓
```

Each placement's arithmetic is local: `+1` for the new island, `−1` per successful union. The parent array's `-1` sentinel doubles as the land check; path compression keeps `find` near-O(1), so each placement is O(1)-ish amortized.

## Complexity

**Time.** k placements × 4 unions:

$$
T(k) = O(k \cdot \alpha)
$$

**Space.** The parent array:

$$
S = O(m \cdot n)
$$

## Variants & follow-ups

- **Accounts Merge** ([6.15](accounts-merge.md)) — the Union-Find engine with name grouping.
- **Redundant Connection** ([6.11](redundant-connection.md)) — the cycle-edge twin.
- **Interview follow-up:** "Why is `count` decremented only on a *successful* union?" The union only merges *different* components — the `a != b` check is what makes `count--` exact. Re-unioning the same island (a duplicate placement or a same-root neighbor) would corrupt the count; the guard prevents both.
