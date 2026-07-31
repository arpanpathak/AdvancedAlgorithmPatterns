# 5.21 Longest Univalue Path

> **Source:** [`src/main/kotlin/tree/LongestUnivaluePath.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/LongestUnivaluePath.kt)
> **Pattern:** post-order chain + global best · **Core page**

## The Problem

The longest path where **every node has the same value** (edges count; path may bend).

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  root = [5,4,5,1,1,null,5]   -> Output: 2   (5→5→5: the two 5-children of the right)
Input:  root = [1,4,5,4,4,null,5]   -> Output: 2
```

## Intuition — each node reports its *straight* chain; the global best bends

Post-order: each node computes the longest **single-direction** univalue chain through each child (`left + 1` if the child matches, else 0). The global best can **bend** through the node (`leftChain + rightChain`); the node *reports* only the longer straight chain upward:

```kotlin
var maxLength = 0

fun dfs(node: TreeNode?): Int {
    if (node == null) return 0

    val left = dfs(node.left)
    val right = dfs(node.right)

    val currentLeft = if (node.left?.`val` == node.`val`) left + 1 else 0
    val currentRight = if (node.right?.`val` == node.`val`) right + 1 else 0

    maxLength = maxOf(maxLength, currentLeft + currentRight)   // bend through me
    return maxOf(currentLeft, currentRight)                     // straight chain up
}
```

**Why the mismatch resets to 0?** A child with a different value contributes *nothing* to the node's univalue chain — the chain must be all-equal, so a break zeroes that branch.

**Why `maxLength` vs `return`?** The [5.4](binary-tree-maximum-path-sum.md) global-best idiom: the path through a node (bending) is a *candidate*; the upward report is only the straight part (a parent can't use a bent path). The distinction is the whole algorithm.

## Approach 1 — Per-node path scan (O(n²))

For each node, walk its equal-value chains: correct, slow.

## Approach 2 — Post-order chain report (the repo's version, optimal)

```kotlin
class LongestUnivaluePath {
    /**
     * @param root tree root
     * @return     length of the longest univalue path (in edges)
     */
    fun longestUnivaluePath(root: TreeNode?): Int {
        var maxLength = 0

        fun dfs(node: TreeNode?): Int {
            if (node == null) return 0

            val left = dfs(node.left)
            val right = dfs(node.right)

            val currentLeft = if (node.left?.`val` == node.`val`) left + 1 else 0
            val currentRight = if (node.right?.`val` == node.`val`) right + 1 else 0

            maxLength = maxOf(maxLength, currentLeft + currentRight)
            return maxOf(currentLeft, currentRight)
        }

        dfs(root)
        return maxLength
    }
}
```

```java
public class LongestUnivaluePath {
    private int best = 0;

    private int dfs(TreeNode node) {
        if (node == null) return 0;

        int left = dfs(node.left);
        int right = dfs(node.right);

        int cl = node.left != null && node.left.val == node.val ? left + 1 : 0;
        int cr = node.right != null && node.right.val == node.val ? right + 1 : 0;

        best = Math.max(best, cl + cr);        // bend through me
        return Math.max(cl, cr);               // straight chain up
    }

    /**
     * @param root tree root
     * @return     length of the longest univalue path (in edges)
     */
    public int longestUnivaluePath(TreeNode root) {
        best = 0;
        dfs(root);
        return best;
    }
}
```

```cpp
#include <algorithm>

class LongestUnivaluePath {
    int best = 0;

    int dfs(TreeNode* node) {
        if (!node) return 0;

        int left = dfs(node->left);
        int right = dfs(node->right);

        int cl = node->left && node->left->val == node->val ? left + 1 : 0;
        int cr = node->right && node->right->val == node->val ? right + 1 : 0;

        best = std::max(best, cl + cr);        // bend through me
        return std::max(cl, cr);               // straight chain up
    }

public:
    /**
     * @param root tree root
     * @return     length of the longest univalue path (in edges)
     */
    int longestUnivaluePath(TreeNode* root) {
        best = 0;
        dfs(root);
        return best;
    }
};
```

```python
def longest_univalue_path(root: Optional["TreeNode"]) -> int:
    """
    @param root: tree root
    @return:     length of the longest univalue path (in edges)
    """
    best = 0

    def dfs(node):
        nonlocal best
        if not node:
            return 0

        left = dfs(node.left)
        right = dfs(node.right)

        cl = left + 1 if node.left and node.left.val == node.val else 0
        cr = right + 1 if node.right and node.right.val == node.val else 0

        best = max(best, cl + cr)        # bend through me
        return max(cl, cr)               # straight chain up

    dfs(root)
    return best
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root tree root
    /// @return     length of the longest univalue path (in edges)
    pub fn longest_univalue_path(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        fn dfs(node: Option<Rc<RefCell<TreeNode>>>, best: &mut i32) -> i32 {
            match node {
                None => 0,
                Some(n) => {
                    let left = dfs(n.borrow().left.clone(), best);
                    let right = dfs(n.borrow().right.clone(), best);
                    let val = n.borrow().val;

                    let cl = if n.borrow().left.as_ref().is_some_and(|l| l.borrow().val == val) { left + 1 } else { 0 };
                    let cr = if n.borrow().right.as_ref().is_some_and(|r| r.borrow().val == val) { right + 1 } else { 0 };

                    *best = (*best).max(cl + cr);   // bend through me
                    cl.max(cr)                      // straight chain up
                }
            }
        }

        let mut best = 0;
        dfs(root, &mut best);
        best
    }
}
```

## Dry run

**Input:** `root = [5,4,5,1,1,null,5]`.

```
dfs(4): children 1,1 (mismatch) -> cl=0, cr=0.  report 0.  best 0.
dfs(left 5): child 4 (mismatch) -> 0.  report 0.
dfs(right 5): left null -> 0.  right 5: dfs(5): 0,0 -> report 0.  cr = 0+1 = 1.  best 1.
  report 1.
dfs(root 5): left: 4 mismatch -> 0.  right: 5 matches -> cr = 1+1 = 2.
  best = max(1, 0 + 2) = 2.  report 2.

Output: 2 ✓
```

The chain accounting: the right 5's child 5 gives `cr = 1` at the right 5, which the root 5 extends to `cr = 2` — a straight 5→5→5 chain of 2 edges. The `cl + cr` bend candidate (e.g. a node with matching children on both sides) would capture paths through a middle node.

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

- **Binary Tree Maximum Path Sum** ([5.4](binary-tree-maximum-path-sum.md)) — the same global-best/bend-vs-straight structure with sums.
- **Diameter Of Binary Tree** ([5.10](diameter-of-binary-tree.md)) — the same post-order, counting edges without the value condition.
- **Interview follow-up:** "Why does the upward return differ from the best?" A parent's chain must be straight — a bent path through a child can't extend upward. The return is the *usable* (straight) length; the global best separately considers the unusable-but-valid bend. Two values, two purposes.
