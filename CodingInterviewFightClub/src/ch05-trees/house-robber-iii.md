# 5.25 House Robber III

> **Source**: [`src/main/kotlin/graph/HouseRobber3.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/HouseRobber3.kt)
> **Pattern**: two-state tree DP · **Core page**

## The Problem

Max loot from a binary tree of houses — no two **directly connected** (parent-child) houses can both be robbed.

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  root = [3,2,3,null,3,null,1]   -> Output: 7   (3 + 3 + 1)
Input:  root = [3,4,5,1,3,null,1]      -> Output: 9   (4 + 5)
```

## Intuition — each node reports (rob me, skip me)

Post-order returns a pair: `[robHere, skipHere]`. `robHere = val + skip(left) + skip(right)`; `skipHere = max(both) + max(both)`:

```kotlin
fun dfs(node: TreeNode?): IntArray {
    if (node == null) return intArrayOf(0, 0)

    val left = dfs(node.left)
    val right = dfs(node.right)

    val robHere = node.`val` + left[1] + right[1]
    val skipHere = maxOf(left[0], left[1]) + maxOf(right[0], right[1])

    return intArrayOf(robHere, skipHere)
}
return maxOf(dfs(root)[0], dfs(root)[1])
```

**Why the two-state return?** The parent's decision needs *both* options per child — robbing the parent forbids children (skip), skipping allows either. The [2.4](../ch02-dynamic-programming/house-robber.md) include/exclude DP, lifted onto a tree.

## Approach 1 — Memoized DFS over node+state

`rob(node, canRob)` memo: also correct.

## Approach 2 — Post-order pair DP (the repo's version, optimal)

```kotlin
class HouseRobber3 {
    /**
     * @param root tree root
     * @return     max loot
     */
    fun rob(root: TreeNode?): Int {
        fun dfs(node: TreeNode?): IntArray {
            if (node == null) return intArrayOf(0, 0)

            val left = dfs(node.left)
            val right = dfs(node.right)

            val robHere = node.`val` + left[1] + right[1]
            val skipHere = maxOf(left[0], left[1]) + maxOf(right[0], right[1])

            return intArrayOf(robHere, skipHere)
        }

        val result = dfs(root)
        return maxOf(result[0], result[1])
    }
}
```

```java
public class HouseRobberIII {
    private int[] dfs(TreeNode node) {
        if (node == null) return new int[]{0, 0};

        int[] left = dfs(node.left);
        int[] right = dfs(node.right);

        int rob = node.val + left[1] + right[1];
        int skip = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);

        return new int[]{rob, skip};
    }

    /**
     * @param root tree root
     * @return     max loot
     */
    public int rob(TreeNode root) {
        int[] result = dfs(root);
        return Math.max(result[0], result[1]);
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class HouseRobberIII {
    std::vector<int> dfs(TreeNode* node) {
        if (!node) return {0, 0};

        auto left = dfs(node->left);
        auto right = dfs(node->right);

        int rob = node->val + left[1] + right[1];
        int skip = std::max(left[0], left[1]) + std::max(right[0], right[1]);

        return {rob, skip};
    }

public:
    /**
     * @param root tree root
     * @return     max loot
     */
    int rob(TreeNode* root) {
        auto result = dfs(root);
        return std::max(result[0], result[1]);
    }
};
```

```python
def rob(root: Optional["TreeNode"]) -> int:
    """
    @param root: tree root
    @return:     max loot
    """
    def dfs(node):
        if not node:
            return (0, 0)

        left = dfs(node.left)
        right = dfs(node.right)

        rob_here = node.val + left[1] + right[1]
        skip_here = max(left) + max(right)

        return (rob_here, skip_here)

    return max(dfs(root))
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root tree root
    /// @return     max loot
    pub fn rob(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        fn dfs(node: Option<Rc<RefCell<TreeNode>>>) -> (i32, i32) {
            match node {
                None => (0, 0),
                Some(n) => {
                    let left = dfs(n.borrow().left.clone());
                    let right = dfs(n.borrow().right.clone());

                    let rob = n.borrow().val + left.1 + right.1;
                    let skip = left.0.max(left.1) + right.0.max(right.1);
                    (rob, skip)
                }
            }
        }

        let (rob, skip) = dfs(root);
        rob.max(skip)
    }
}
```

## Dry run

**Input:** `root = [3,2,3,null,3,null,1]`.

```
leaf 3: (3, 0).  leaf 1: (1, 0).
node 2 (right 3): rob = 2 + 0 + 0 = 2.  skip = 0 + 3 = 3.  -> (2, 3).
node 3 (right 1): rob = 3 + 0 + 0 = 3.  skip = 0 + 1 = 1.  -> (3, 1).
root 3 (left 2, right 3): rob = 3 + 3 + 1 = 7.  skip = max(2,3) + max(3,1) = 3 + 3 = 6.
Output: max(7, 6) = 7 ✓
```

## Complexity

**Time.** Each node once:

$$
T(n) = O(n)
$$

**Space.** Recursion:

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **House Robber** ([2.4](../ch02-dynamic-programming/house-robber.md)) — the array ancestor of this tree DP.
- **Binary Tree Maximum Path Sum** ([5.4](binary-tree-maximum-path-sum.md)) — the post-order pair-return discipline shared.
- **Interview follow-up:** "Why two values per node?" The parent needs both "if I rob, the child must skip" and "if I skip, the child is free" — one number can't express both. The pair is the complete interface between levels.
