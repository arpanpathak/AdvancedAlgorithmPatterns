# 6.11 The Earliest Moment Everyone Became Friends

> **Source:** [`src/main/kotlin/disjointset/TheEarliestMomentEveryoneBecameFriends.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/disjointset/TheEarliestMomentEveryoneBecameFriends.kt)
> **Pattern:** DSU with a components counter · **Core page**

## The Problem

Given `logs[i] = [timestamp, x, y]` (x and y became friends at that time) and `n` people, return the **earliest timestamp** when everyone is connected (directly or transitively), or `-1`.

- Constraints: $1 \le n \le 100$; logs sorted-or-not; friends relation is transitive.

## Examples

```
logs = [[20190101,0,1],[20190104,3,4],[20190107,2,3],[20190211,1,5],
        [20190224,2,4],[20190301,0,3],[20190312,1,2],[20190322,4,5]], n = 6
Output: 20190301   (the union that merges the last two components)
```

## Intuition — the DSU **components counter** turns "are all connected?" into an O(1) test

Plain Union-Find ([6.9](redundant-connection.md)) answers "are x and y connected?"; this problem asks "is *everyone* connected?" The clean upgrade: the DSU **counts its components** (`components` starts at `n`, decremented on every *successful* union). Then "everyone connected" ⟺ `components == 1`.

```
logs.sortBy { it[0] }                    # process friendships in time order
for (time, x, y) in logs:
    ds.union(x, y)
    if (ds.components == 1) return time  # first moment of full connectivity
return -1
```

**Why does sorting + first-hit work?** Connectivity only *improves* over time (unions never un-union). So the set of "moments when components == 1" is a suffix — the first such moment is the earliest answer. No need to check every log; the counter does it.

**Why `components--` only on a *real* union?** `union` of two already-connected people shouldn't change the component count — the repo's `union` checks `rootX != rootY` before decrementing (and the `else` branch is where the rank-based attach happens). The counter is only meaningful if it counts *merges*, not redundant edges.

**Rank-based union:** the repo attaches the shorter tree under the taller (`rank[rootX] > rank[rootY] → parent[rootY] = rootX`, ties bump the rank) — the [6.9](redundant-connection.md) page's compression plus this balancing keeps every op near-O(1) α(n).

## Approach 1 — BFS/DFS per timestamp (O(E·V))

Rebuild connectivity after each log: correct, quadratic.

## Approach 2 — DSU with a components counter (the repo's version, optimal)

```kotlin
class DisjointSet(n: Int) {
    private val parent = IntArray(n) { it }
    private val rank = IntArray(n)
    var components = n
        private set

    fun find(x: Int): Int {
        if (parent[x] != x) parent[x] = find(parent[x])   // path compression
        return parent[x]
    }

    fun union(x: Int, y: Int) {
        val rootX = find(x)
        val rootY = find(y)
        if (rootX != rootY) {                              // a real merge
            when {
                rank[rootX] > rank[rootY] -> parent[rootY] = rootX
                rank[rootX] < rank[rootY] -> parent[rootX] = rootY
                else -> {                                  // tie: pick one, bump rank
                    parent[rootY] = rootX
                    rank[rootX]++
                }
            }
            components--                                   // one fewer component
        }
    }
}

class TheEarliestMomentEveryoneBecameFriends {
    /**
     * @param logs [timestamp, x, y] friendship events
     * @param n    number of people
     * @return     earliest time everyone is connected, or -1
     */
    fun earliestAcq(logs: Array<IntArray>, n: Int): Int {
        val ds = DisjointSet(n)
        logs.sortBy { it[0] }                              // process in time order

        logs.forEach { (time, x, y) ->
            ds.union(x, y)
            if (ds.components == 1) return time            // everyone connected
        }
        return -1
    }
}
```

```java
import java.util.*;

public class TheEarliestMomentEveryoneBecameFriends {
    private static class DisjointSet {
        int[] parent, rank;
        int components;

        DisjointSet(int n) {
            parent = new int[n];
            rank = new int[n];
            components = n;
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
            return parent[x];
        }

        void union(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return;                              // no merge
            if (rank[rx] > rank[ry]) parent[ry] = rx;
            else if (rank[rx] < rank[ry]) parent[rx] = ry;
            else { parent[ry] = rx; rank[rx]++; }              // tie
            components--;
        }
    }

    /**
     * @param logs [timestamp, x, y] friendship events
     * @param n    number of people
     * @return     earliest time everyone is connected, or -1
     */
    public int earliestAcq(int[][] logs, int n) {
        Arrays.sort(logs, Comparator.comparingInt(a -> a[0]));  // time order
        DisjointSet ds = new DisjointSet(n);

        for (int[] log : logs) {
            ds.union(log[1], log[2]);
            if (ds.components == 1) return log[0];              // everyone connected
        }
        return -1;
    }
}
```

```cpp
#include <algorithm>
#include <vector>

class TheEarliestMomentEveryoneBecameFriends {
    struct DSU {
        std::vector<int> parent, rank;
        int components;
        DSU(int n) : parent(n), rank(n, 0), components(n) {
            for (int i = 0; i < n; i++) parent[i] = i;
        }
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
            return parent[x];
        }
        void unite(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return;
            if (rank[rx] > rank[ry]) parent[ry] = rx;
            else if (rank[rx] < rank[ry]) parent[rx] = ry;
            else { parent[ry] = rx; rank[rx]++; }
            components--;
        }
    };

public:
    /**
     * @param logs [timestamp, x, y] friendship events
     * @param n    number of people
     * @return     earliest time everyone is connected, or -1
     */
    int earliestAcq(std::vector<std::vector<int>>& logs, int n) {
        std::sort(logs.begin(), logs.end());                   // time order
        DSU ds(n);

        for (auto& log : logs) {
            ds.unite(log[1], log[2]);
            if (ds.components == 1) return log[0];             // everyone connected
        }
        return -1;
    }
};
```

```python
class DisjointSet:
    """@param n: number of people"""

    def __init__(self, n: int):
        self.parent = list(range(n))
        self.rank = [0] * n
        self.components = n

    def find(self, x: int) -> int:
        if self.parent[x] != x:
            self.parent[x] = self.find(self.parent[x])   # path compression
        return self.parent[x]

    def union(self, x: int, y: int) -> None:
        rx, ry = self.find(x), self.find(y)
        if rx == ry:
            return                                       # no merge
        if self.rank[rx] > self.rank[ry]:
            self.parent[ry] = rx
        elif self.rank[rx] < self.rank[ry]:
            self.parent[rx] = ry
        else:
            self.parent[ry] = rx
            self.rank[rx] += 1                           # tie
        self.components -= 1                             # one fewer component


def earliest_acq(logs: list[list[int]], n: int) -> int:
    """
    @param logs: [timestamp, x, y] friendship events
    @param n:    number of people
    @return:     earliest time everyone is connected, or -1
    """
    ds = DisjointSet(n)
    logs.sort(key=lambda log: log[0])                    # time order

    for time, x, y in logs:
        ds.union(x, y)
        if ds.components == 1:
            return time                                  # everyone connected
    return -1
```

```rust
struct DisjointSet {
    parent: Vec<usize>,
    rank: Vec<i32>,
    components: i32,
}

impl DisjointSet {
    fn new(n: usize) -> Self {
        DisjointSet { parent: (0..n).collect(), rank: vec![0; n], components: n as i32 }
    }

    fn find(&mut self, x: usize) -> usize {
        if self.parent[x] != x {
            let root = self.find(self.parent[x]);
            self.parent[x] = root;                       // path compression
        }
        self.parent[x]
    }

    fn union(&mut self, x: usize, y: usize) {
        let (rx, ry) = (self.find(x), self.find(y));
        if rx == ry { return; }                          // no merge
        if self.rank[rx] > self.rank[ry] { self.parent[ry] = rx; }
        else if self.rank[rx] < self.rank[ry] { self.parent[rx] = ry; }
        else { self.parent[ry] = rx; self.rank[rx] += 1; }   // tie
        self.components -= 1;                            // one fewer component
    }
}

impl Solution {
    /// @param logs [timestamp, x, y] friendship events
    /// @param n    number of people
    /// @return     earliest time everyone is connected, or -1
    pub fn earliest_acq(logs: Vec<Vec<i32>>, n: i32) -> i32 {
        let mut logs = logs;
        logs.sort_by_key(|l| l[0]);                      // time order
        let mut ds = DisjointSet::new(n as usize);

        for log in logs {
            ds.union(log[1] as usize, log[2] as usize);
            if ds.components == 1 { return log[0]; }     // everyone connected
        }
        -1
    }
}
```

## Dry run

**Input:** the example logs, `n = 6`.

```
components starts at 6.

t=20190101 (0,1): union -> {0,1} merged.        components=5
t=20190104 (3,4): union -> {3,4} merged.        components=4
t=20190107 (2,3): union -> {2,3,4} merged.      components=3
t=20190211 (1,5): union -> {0,1,5} merged.      components=2
t=20190224 (2,4): find(2) and find(4) same root -> no merge.  components stays 2.
t=20190301 (0,3): union -> {0,1,5} and {2,3,4} MERGE.  components=1 -> return 20190301 ✓
```

The two components after 20190211 are `{0,1,5}` and `{2,3,4}`; the 20190224 log is redundant (already connected — the counter correctly ignores it), and the 20190301 log is the bridge that unites the halves. The `components == 1` check fires the moment the last gap closes.

## Complexity

**Time.** Sort + E near-O(1) unions:

$$
T(E) = O(E \log E) + O(E \cdot \alpha(n))
$$

**Space.** The DSU arrays:

$$
S = O(n)
$$

## Variants & follow-ups

- **Redundant Connection** ([6.9](redundant-connection.md)) — the same DSU primitive detecting *cycles* instead of counting components.
- **Number Of Islands II / Account Merge** (`disjointset/`) — the components counter is the shared trick: "how many islands are left?" answers in O(1) after each union.
- **Kruskal's MST** ([6.6](min-cost-to-connect-all-points.md)) — `components == 1` is exactly Kruskal's termination condition.
- **Interview follow-up:** "Why must the counter decrement only on *real* merges?" A redundant edge (already-same-root) changes connectivity not at all — counting it would under-report components and answer the query too early. The `rootX != rootY` guard is what makes `components` a truthful "number of connected groups" invariant.
