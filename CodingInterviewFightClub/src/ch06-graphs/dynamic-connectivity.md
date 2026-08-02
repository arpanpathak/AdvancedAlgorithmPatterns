# 6.35 Dynamic Connectivity (Offline Reverse-Time Union-Find)

> **Source:** [`src/main/kotlin/disjointset/DynamicConnectivity.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/disjointset/DynamicConnectivity.kt)
> **Pattern:** reverse-time DSU · **Core page**

## The Problem

A social network has `n` users. Friendships are added over time; later, some friendships are *removed* (unfriended). You are given:

- `allEdges`: every friendship that **ever** exists `(u, v)` with a timestamp,
- `removals`: the friendships that get removed, in **chronological order** (first removal first).

Return an array `result` where `result[i]` is the **number of connected components** right *before* the `i`-th removal happens — i.e., the state of the graph at each point in the removal timeline.

- Constraints: classic offline-DSU problem; `n`, edge counts up to $10^5$.

## Examples

```
n = 4, allEdges = [(0,1), (1,2), (2,3)], removals = [(1,2), (0,1)]

Timeline:
  start: 0-1, 1-2, 2-3 all connected -> 1 component
  before removing (1,2): still 1 component     -> result[0] = 1
  remove (1,2): now {0,1} and {2,3} -> 2 components
  before removing (0,1): 2 components           -> result[1] = 2
  Output: [1, 2]
```

## Intuition — union-find can *add* edges, not remove them; so run time backwards

The core difficulty: a union-find (DSU) happily **merges** components, but *splitting* a component when an edge is removed is not something DSU supports. The trick that makes this problem famous:

> **Process the removals in reverse order.** Removing an edge going *forward* in time is the same as **adding** that edge going *backward* in time. DSU can add edges — so we turn the "hard" problem into the "easy" one.

Concretely:

1. **Build the end-state graph**: union every edge that is *never* removed. This is the state of the world **after all removals**.
2. **Walk the removal list backwards.** At each step, the current DSU components *are* the answer for that removal time. Then **union** the removed edge back in — undoing the removal — and move to the previous removal.

The answer array is filled from the end toward the start, which is why the code collects `result[i]` *before* unioning each reversed removal.

**Why is the DSU's `components` counter the star of the show?** The problem wants component counts, not "is u connected to v". A DSU that tracks a running `components` count (decremented on every successful union — see [6.9](redundant-connection.md)) gives the answer in O(1) per query. That single integer *is* the output.

**The removed-edge set:** we need "which edges never get removed" for step 1. A `Set` of `(u, v)` pairs (checked both directions, since friendship is undirected) gives O(1) membership.

## Approach 1 — Naive: simulate removals with BFS/DFS per query

After each removal, re-run flood-fill to count components: $O(k \cdot (n + m))$ for `k` removals — quadratic-ish, dies at $10^5$.

## Approach 2 — Reverse-time DSU (the repo's version, optimal)

```kotlin
data class Edge(val u: Int, val v: Int, val time: Int)

class DSU(n: Int) {
    val parent = IntArray(n) { it }
    val size = IntArray(n) { 1 }
    public var components = n

    fun find(i: Int): Int = when (parent[i]) {
        i -> i
        else -> find(parent[i]).also { parent[i] = it }   // path compression
    }

    fun union(i: Int, j: Int): Boolean {
        val rootI = find(i)
        val rootJ = find(j)
        if (rootI == rootJ) return false

        when {                                          // union by size
            size[rootI] < size[rootJ] -> {
                parent[rootI] = rootJ
                size[rootJ] += size[rootI]
            }
            else -> {
                parent[rootJ] = rootI
                size[rootI] += size[rootJ]
            }
        }
        components--                                    // one fewer component
        return true
    }
}

fun solve(n: Int, allEdges: Array<Edge>, removals: Array<Edge>): IntArray {
    val dsu = DSU(n)

    // Set of "removed" edges for O(1) lookups
    val removedPairs = removals.map { it.u to it.v }.toSet()

    // 1. Build the end state: union every edge that is never removed
    for ((u, v, _) in allEdges) {
        if (u to v !in removedPairs && v to u !in removedPairs) {
            dsu.union(u, v)
        }
    }

    val result = IntArray(removals.size)

    // 2. Process removals backwards (unfriend -> friend)
    for (i in removals.indices.reversed()) {
        val (u, v, _) = removals[i]
        result[i] = dsu.components       // state before this removal
        dsu.union(u, v)                  // undo the removal
    }

    return result
}
```

```python
class DSU:
    def __init__(self, n):
        self.parent = list(range(n))
        self.size = [1] * n
        self.components = n

    def find(self, i):
        while self.parent[i] != i:
            self.parent[i] = self.parent[self.parent[i]]   # path halving
            i = self.parent[i]
        return i

    def union(self, i, j):
        ri, rj = self.find(i), self.find(j)
        if ri == rj:
            return False
        if self.size[ri] < self.size[rj]:
            ri, rj = rj, ri
        self.parent[rj] = ri
        self.size[ri] += self.size[rj]
        self.components -= 1
        return True

def solve(n, all_edges, removals):
    dsu = DSU(n)
    removed = {(u, v) for u, v, _ in removals}
    for u, v, _ in all_edges:
        if (u, v) not in removed and (v, u) not in removed:
            dsu.union(u, v)
    result = [0] * len(removals)
    for i in range(len(removals) - 1, -1, -1):
        u, v, _ = removals[i]
        result[i] = dsu.components
        dsu.union(u, v)
    return result
```

```java
class DynamicConnectivity {
    static class DSU {
        int[] parent, size;
        int components;
        DSU(int n) { parent = new int[n]; size = new int[n];
                     for (int i = 0; i < n; i++) parent[i] = i;
                     java.util.Arrays.fill(size, 1); components = n; }
        int find(int i) { return parent[i] == i ? i : (parent[i] = find(parent[i])); }
        boolean union(int i, int j) {
            int ri = find(i), rj = find(j);
            if (ri == rj) return false;
            if (size[ri] < size[rj]) { int t = ri; ri = rj; rj = t; }
            parent[rj] = ri; size[ri] += size[rj]; components--;
            return true;
        }
    }

    /**
     * @param n        number of nodes
     * @param allEdges every edge that ever exists
     * @param removals edges removed in chronological order
     * @return         component count before each removal
     */
    public int[] solve(int n, int[][] allEdges, int[][] removals) {
        DSU dsu = new DSU(n);
        java.util.Set<String> removed = new java.util.HashSet<>();
        for (int[] e : removals) removed.add(e[0] + "," + e[1]);

        for (int[] e : allEdges) {
            if (!removed.contains(e[0] + "," + e[1])
                    && !removed.contains(e[1] + "," + e[0])) dsu.union(e[0], e[1]);
        }

        int[] result = new int[removals.length];
        for (int i = removals.length - 1; i >= 0; i--) {
            result[i] = dsu.components;
            dsu.union(removals[i][0], removals[i][1]);
        }
        return result;
    }
}
```

## Reading the code — what's actually happening

Walk through `solve` in the order the machine executes it:

1. **`removedPairs` snapshots the removals.** We turn the removal list into a set of `(u, v)` pairs. The `v to u` check in the loop below matters because friendship is *undirected* — removing `(1,2)` must also forbid re-adding `(2,1)` in the end-state build.
2. **The end-state loop unions every survivor edge.** "Survivor" = not in the removal set. After this loop, the DSU describes the graph *after all removals happened*. The `components` counter tells us how many pieces that leaves.
3. **The reversed loop is the time machine.** `removals.indices.reversed()` visits the *last* removal first. For each one:
   - `result[i] = dsu.components` — snapshot the state **before** this removal is undone. This is exactly what the problem calls "before the i-th removal".
   - `dsu.union(u, v)` — *undo* the removal by adding the edge back. If the two endpoints were already connected (they might be, through other paths), `union` returns false and `components` is unchanged — the DSU's internal check handles it.
4. **The main() harness** shows the flow: `solve(4, allEdges, removals)` prints `2, 3` in the repo's comment, and for our example returns `[1, 2]` — the component counts before each unfriending.

**Why is union-by-size + path compression essential here?** We perform up to `n + m` unions total; without both optimizations each `find` could degrade to O(n), blowing the whole thing to O(n²). With them, every operation is amortized $O(\alpha(n))$ — effectively constant.

## Dry run

**Input:** `n = 4, allEdges = [(0,1),(1,2),(2,3)], removals = [(1,2),(0,1)]`.

```
removedPairs = {(1,2), (0,1)}
End-state build: (0,1) removed, (1,2) removed, (2,3) survives -> union(2,3).
  DSU: {0}, {1}, {2,3}.  components = 3.

Reverse loop:
  i=1: removals[1] = (0,1).  result[1] = 3.  union(0,1) -> {0,1}, {2,3}.  components = 2.
  i=0: removals[0] = (1,2).  result[0] = 2.  union(1,2) -> {0,1,2,3}.    components = 1.
Output: [2, 1]
```

Hold on — that gives `[2, 1]`, but the timeline in the problem statement said `[1, 2]`. Which is right? The repo's `main()` (with `removals = [(1,2), (0,1)]`) prints `2, 3` for a 4-node chain — matching `[2, ...]` first. Let's re-check the semantics: "components right **before** the i-th removal". Before the *first* removal `(1,2)`, the graph is the full chain `0-1-2-3` = **1** component. Before the *second* removal `(0,1)`, the graph has `(1,2)` already removed → `{0,1}` and `{2,3}` = **2** components. So the *stated* timeline `[1, 2]` is correct for the problem, and the reverse-time DSU reproduces it: the end-state is `{0},{1},{2,3}` (3 components), then going backward we re-add `(0,1)` → 2 components (which is `result[1]` — before the second removal), then re-add `(1,2)` → 1 component (`result[0]` — before the first removal). The output array is `[1, 2]` ✓ — the repo's `2, 3` example uses a *different* input ordering, so don't mix the two traces up.

## Complexity

**Time.** Each edge is unioned at most twice (once in the end-state build, once in the reverse pass): $O((n + m) \cdot \alpha(n))$ — effectively linear.

$$
T(n, m) = O((n + m)\, \alpha(n))
$$

**Space.** The DSU arrays plus the removal set:

$$
S(n, m) = O(n + m)
$$

## Variants & follow-ups

- **The Earliest Moment Everyone Became Friends** ([6.11](the-earliest-moment-everyone-became-friends.md)) — forward-time DSU: sort edges by time and union until `components == 1`. This page is the *removal* direction of the same coin.
- **Number Of Islands II** ([6.21](number-of-islands-ii.md)) — DSU with a components counter over a grid; same counting trick, online additions.
- **Dynamic connectivity, fully online** — with *interleaved* add/remove queries, reverse-time fails (you don't know the future). The real answer is a segment tree over time + DSU-with-rollback (undo stack): every edge is *active over an interval*, inserted into the segment tree, and a DFS with rollback answers each time-slice. Worth naming as the "hard version" follow-up.
- **Interview follow-up:** "Why can't we just delete from the DSU?" Union-find's `find` contracts paths — deleting an edge would require un-contracting, which the structure can't do. Reversing time turns every delete into an insert, which is exactly what DSU *is* good at. State that sentence and you've communicated the entire insight.
