# 17.16 The Great Town Split (Tree Edge Cut)

> **Source**: [`src/main/kotlin/google/MInDifferenceBetweenTotalSums.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/google/MInDifferenceBetweenTotalSums.kt)
> **Pattern**: subtree-sum post-order · **Core page**

## The Problem

A tree of N regions with populations. Remove **one edge** to split it into two parts with populations as **close as possible** — return the minimum difference.

- Constraints: N ≤ 10⁵.

## Examples

```
Input:  a star tree with total population 100 and one branch of 40
Output: 20   (cut that branch: |40 - 60| = 20)
```

## Intuition — each edge's cut size is its subtree's sum

Removing the edge above a subtree isolates that subtree — the two parts have sums `subtreeSum` and `total - subtreeSum`. A post-order computes every subtree sum; the answer is `min |total - 2*subtreeSum|`:

```kotlin
fun dfs(node: Int): Long {
    var sum = population[node].toLong()

    for (child in children[node]) {
        sum += dfs(child)
    }

    minDiff = minOf(minDiff, abs(total - 2 * sum))   // the cut above this subtree
    return sum
}
```

**Why `total - 2*sum`?** The difference between the two parts is `|sum - (total - sum)| = |2*sum - total|` — one formula per edge. The [5.4](../ch05-trees/binary-tree-maximum-path-sum.md) post-order sum-return, with a global best.

**Why post-order?** A subtree's sum needs its children's sums first — the recursion computes bottom-up, testing the cut *above* each node as it returns. The [5.10](../ch05-trees/diameter-of-binary-tree.md) post-order discipline.

## Approach 1 — For each edge, BFS both sides (O(N²))

Cut and count: correct, slow.

## Approach 2 — Post-order subtree sums (the repo's version, optimal)

```kotlin
class MInDifferenceBetweenTotalSums {
    private lateinit var children: Array<MutableList<Int>>
    private lateinit var population: LongArray
    private var total = 0L
    private var minDiff = Long.MAX_VALUE

    /**
     * @param n       region count
     * @param edges   tree edges (0-based)
     * @param pops    region populations
     * @return        minimum population difference after one cut
     */
    fun minDifference(n: Int, edges: Array<IntArray>, pops: LongArray): Long {
        children = Array(n) { mutableListOf() }
        population = pops
        total = pops.sum()

        for ((u, v) in edges) {
            children[u].add(v)
            children[v].add(u)
        }

        minDiff = Long.MAX_VALUE
        dfs(0, -1)
        return minDiff
    }

    private fun dfs(node: Int, parent: Int): Long {
        var sum = population[node]

        for (child in children[node]) {
            if (child == parent) continue
            sum += dfs(child, node)
        }

        if (parent != -1) {      // the cut above this node (not the root's fake edge)
            minDiff = minOf(minDiff, kotlin.math.abs(total - 2 * sum))
        }
        return sum
    }
}
```

```java
import java.util.*;

public class TheGreatTownSplit {
    private List<Integer>[] children;
    private long[] population;
    private long total, minDiff = Long.MAX_VALUE;

    private long dfs(int node, int parent) {
        long sum = population[node];

        for (int child : children[node]) {
            if (child != parent) sum += dfs(child, node);
        }

        if (parent != -1) {
            minDiff = Math.min(minDiff, Math.abs(total - 2 * sum));
        }
        return sum;
    }

    /**
     * @param n     region count
     * @param edges tree edges (0-based)
     * @param pops  region populations
     * @return      minimum population difference after one cut
     */
    public long minDifference(int n, int[][] edges, long[] pops) {
        children = new ArrayList[n];
        for (int i = 0; i < n; i++) children[i] = new ArrayList<>();

        for (int[] e : edges) {
            children[e[0]].add(e[1]);
            children[e[1]].add(e[0]);
        }

        population = pops;
        total = 0;
        for (long p : pops) total += p;
        minDiff = Long.MAX_VALUE;

        dfs(0, -1);
        return minDiff;
    }
}
```

```cpp
#include <vector>
#include <cmath>
#include <climits>

class TheGreatTownSplit {
    std::vector<std::vector<int>> children;
    std::vector<long long> population;
    long long total = 0, minDiff = LLONG_MAX;

    long long dfs(int node, int parent) {
        long long sum = population[node];

        for (int child : children[node]) {
            if (child != parent) sum += dfs(child, node);
        }

        if (parent != -1) {
            minDiff = std::min(minDiff, std::llabs(total - 2 * sum));
        }
        return sum;
    }

public:
    /**
     * @param n     region count
     * @param edges tree edges (0-based)
     * @param pops  region populations
     * @return      minimum population difference after one cut
     */
    long long minDifference(int n, std::vector<std::vector<int>>& edges, std::vector<long long>& pops) {
        children.assign(n, {});
        for (auto& e : edges) {
            children[e[0]].push_back(e[1]);
            children[e[1]].push_back(e[0]);
        }

        population = pops;
        total = 0;
        for (long long p : pops) total += p;
        minDiff = LLONG_MAX;

        dfs(0, -1);
        return minDiff;
    }
};
```

```python
def min_difference(n: int, edges: list[list[int]], pops: list[int]) -> int:
    """
    @param n:     region count
    @param edges: tree edges (0-based)
    @param pops:  region populations
    @return:      minimum population difference after one cut
    """
    children = [[] for _ in range(n)]
    for u, v in edges:
        children[u].append(v)
        children[v].append(u)

    total = sum(pops)
    min_diff = float("inf")

    def dfs(node, parent):
        nonlocal min_diff
        total_sum = pops[node]

        for child in children[node]:
            if child != parent:
                total_sum += dfs(child, node)

        if parent != -1:
            min_diff = min(min_diff, abs(total - 2 * total_sum))

        return total_sum

    dfs(0, -1)
    return min_diff
```

```rust
impl Solution {
    /// @param n     region count
    /// @param edges tree edges (0-based)
    /// @param pops  region populations
    /// @return      minimum population difference after one cut
    pub fn min_difference(n: i32, edges: Vec<Vec<i32>>, pops: Vec<i64>) -> i64 {
        let n = n as usize;
        let mut children = vec![Vec::new(); n];
        for e in &edges {
            children[e[0] as usize].push(e[1] as usize);
            children[e[1] as usize].push(e[0] as usize);
        }

        let total: i64 = pops.iter().sum();
        let mut min_diff = i64::MAX;

        fn dfs(node: usize, parent: i64, children: &Vec<Vec<usize>>, pops: &Vec<i64>,
               total: i64, min_diff: &mut i64) -> i64 {
            let mut sum = pops[node];

            for &child in &children[node] {
                if child as i64 != parent {
                    sum += dfs(child, node as i64, children, pops, total, min_diff);
                }
            }

            if parent != -1 {
                *min_diff = (*min_diff).min((total - 2 * sum).abs());
            }
            sum
        }

        dfs(0, -1, &children, &pops, total, &mut min_diff);
        min_diff
    }
}
```

## Dry run

**Input:** star: node 0 (pop 20), children 1 (40), 2 (40).

```
total = 100.
dfs(1): leaf.  sum 40.  cut above 1: |100 - 80| = 20.  minDiff 20.
dfs(2): leaf.  sum 40.  cut above 2: |100 - 80| = 20.  minDiff 20.
dfs(0): sum = 20 + 40 + 40 = 100.  no cut above root.

Output: 20 ✓
```

Each non-root node's return tests the edge *above* it — one candidate per edge. The `parent != -1` guard skips the root's phantom edge. The formula `|total - 2*sum|` is the whole insight: one subtree sum per edge, O(N) total.

## Complexity

**Time.** One DFS:

$$
T(N) = O(N)
$$

**Space.** Children + recursion:

$$
S(N) = O(N)
$$

## Variants & follow-ups

- **Maximum Product Of Splitted Binary Tree** (`tree/`) — the binary-tree twin of this cut (product instead of difference).
- **Binary Tree Maximum Path Sum** ([5.4](../ch05-trees/binary-tree-maximum-path-sum.md)) — the post-order sum-return engine.
- **Interview follow-up:** "Why does the post-order test the cut on return?" A node's subtree sum is complete only after its children return — the cut above the node is evaluable exactly then. The `parent` parameter distinguishes tree edges from the root's phantom cut.
