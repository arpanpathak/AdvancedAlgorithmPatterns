# 5.27 Unique Binary Search Trees

> **Source**: [`src/main/kotlin/tree/bst/UniqueBinarySearchTrees.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bst/UniqueBinarySearchTrees.kt)
> **Pattern**: Catalan DP · **Core page**

## The Problem

The number of BSTs with nodes 1..n.

- Constraints: n ≤ 19.

## Examples

```
Input:  n = 3   -> Output: 5
```

## Intuition — each root splits the nodes; counts multiply

With root `r`: `r-1` nodes left, `n-r` right. `dp[n] = Σ dp[r-1] * dp[n-r]` — the Catalan recurrence:

```kotlin
val dp = IntArray(n + 1).apply { this[0] = 1; this[1] = 1 }

for (nodes in 2..n) {
    for (root in 1..nodes) {
        dp[nodes] += dp[root - 1] * dp[nodes - root]
    }
}
return dp[n]
```

**Why the product?** Each left BST combines with each right BST — independent choices multiply. The [2.0](../ch02-dynamic-programming/pattern-primer.md) partition DP, over root positions.

## Approach 1 — Catalan DP (the repo's version, optimal)

```kotlin
class UniqueBinarySearchTrees {
    /**
     * @param n node count
     * @return  number of BSTs
     */
    fun numTrees(n: Int): Int {
        val dp = IntArray(n + 1).apply { this[0] = 1; this[1] = 1 }

        for (nodes in 2..n) {
            for (root in 1..nodes) {
                dp[nodes] += dp[root - 1] * dp[nodes - root]
            }
        }
        return dp[n]
    }
}
```

```java
public class UniqueBinarySearchTrees {
    /**
     * @param n node count
     * @return  number of BSTs
     */
    public int numTrees(int n) {
        int[] dp = new int[n + 1];
        dp[0] = dp[1] = 1;

        for (int nodes = 2; nodes <= n; nodes++) {
            for (int root = 1; root <= nodes; root++) {
                dp[nodes] += dp[root - 1] * dp[nodes - root];
            }
        }
        return dp[n];
    }
}
```

```cpp
#include <vector>

class UniqueBinarySearchTrees {
public:
    /**
     * @param n node count
     * @return  number of BSTs
     */
    int numTrees(int n) {
        std::vector<long> dp(n + 1, 0);
        dp[0] = dp[1] = 1;

        for (int nodes = 2; nodes <= n; nodes++) {
            for (int root = 1; root <= nodes; root++) {
                dp[nodes] += dp[root - 1] * dp[nodes - root];
            }
        }
        return (int)dp[n];
    }
};
```

```python
def num_trees(n: int) -> int:
    """
    @param n: node count
    @return:  number of BSTs
    """
    dp = [0] * (n + 1)
    dp[0] = dp[1] = 1

    for nodes in range(2, n + 1):
        for root in range(1, nodes + 1):
            dp[nodes] += dp[root - 1] * dp[nodes - root]

    return dp[n]
```

```rust
impl Solution {
    /// @param n node count
    /// @return  number of BSTs
    pub fn num_trees(n: i32) -> i32 {
        let n = n as usize;
        let mut dp = vec![0i64; n + 1];
        dp[0] = 1;
        dp[1] = 1;

        for nodes in 2..=n {
            for root in 1..=nodes {
                dp[nodes] += dp[root - 1] * dp[nodes - root];
            }
        }
        dp[n] as i32
    }
}
```

## Reading the code — what's actually happening

```kotlin
val dp = IntArray(n + 1).apply { this[0] = 1; this[1] = 1 }
for (nodes in 2..n) {
    for (root in 1..nodes) {
        dp[nodes] += dp[root - 1] * dp[nodes - root]
    }
}
return dp[n]
```

The key question: when you build a BST from `nodes` sorted values (1…`nodes`), **every value is a candidate root**. Fix one root `r`, and the structure is forced: all `r-1` smaller values must live in the left subtree (they're all less than `r`, and BST order requires them left), and all `nodes - r` larger values live in the right subtree.

- **`dp[0] = 1` and `dp[1] = 1` are the trivial bases.** An empty tree (0 nodes) is one valid structure; a single-node tree is one valid structure. Everything else is built from these.
- **The inner loop sums over every possible root.** `dp[root - 1]` counts the BSTs you can make from the left-side values; `dp[nodes - root]` counts those from the right-side values. For a *fixed* root, any left structure combines with any right structure — so the count for that root is the **product**. Summing over all roots gives `dp[nodes]`.
- **Why does `dp[2]` come out as 2?** Root 1: left is empty (`dp[0]=1`), right has one value (`dp[1]=1`) → 1·1 = 1 tree. Root 2: mirror → 1. Total 2 — a chain with 1 at top or a chain with 2 at top. Correct.
- **`dp[3]` = 5 by the same sum:** root 1 → `dp[0]·dp[2] = 2`, root 2 → `dp[1]·dp[1] = 1`, root 3 → `dp[2]·dp[0] = 2`; total 5. This recurrence `C(n) = Σ C(i)·C(n-1-i)` *is* the Catalan number sequence — the same numbers count balanced parentheses and polygon triangulations, which is why interviewers love asking it.

## Dry run

**Input:** `n = 3`.

```
dp[0]=1, dp[1]=1.
nodes=2: root 1: dp[0]*dp[1]=1.  root 2: dp[1]*dp[0]=1.  dp[2]=2.
nodes=3: root 1: dp[0]*dp[2]=2.  root 2: dp[1]*dp[1]=1.  root 3: dp[2]*dp[0]=2.  dp[3]=5.
Output: 5 ✓
```

## Complexity

**Time.** O(n²):

$$
T(n) = O(n^2)
$$

**Space.** The DP:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Unique Binary Search Trees II** ([5.28](unique-binary-search-trees-ii.md)) — generate the trees.
- **Interview follow-up:** "Why is this the Catalan number?" The recurrence `C(n) = Σ C(i)C(n-1-i)` IS Catalan — the same DP appears in balanced parentheses ([12.4](../ch12-backtracking/generate-parentheses.md)) and polygon triangulation. One DP, many problems.
