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
