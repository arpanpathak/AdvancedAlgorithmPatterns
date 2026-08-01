# 5.31 Balanced Binary Tree

> **Source**: [`src/main/kotlin/tree/BalancedBinaryTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/BalancedBinaryTree.kt)
> **Pattern**: height check with early exit · **Core page**

## The Problem

Is the tree height-balanced (every node's subtrees differ by ≤ 1)?

- Constraints: n ≤ 5000.

## Examples

```
Input:  root = [3,9,20,null,null,15,7]   -> Output: true
Input:  root = [1,2,2,3,3,null,null,4,4] -> Output: false
```

## Intuition — post-order heights; -1 propagates imbalance

```kotlin
fun checkHeight(node: TreeNode?): Int {
    if (node == null) return 0

    val leftHeight = checkHeight(node.left)
    if (leftHeight == -1) return -1

    val rightHeight = checkHeight(node.right)
    if (rightHeight == -1) return -1

    if (abs(leftHeight - rightHeight) > 1) return -1

    return maxOf(leftHeight, rightHeight) + 1
}
return checkHeight(root) != -1
```

**Why the -1 sentinel?** The first unbalanced subtree poisons the whole check — propagating -1 avoids recomputing (the [5.4](binary-tree-maximum-path-sum.md) global-state escape hatch).

## Approach 1 — Height with early exit (the repo's version, optimal)

```kotlin
class BalancedBinaryTree {
    /**
     * @param root tree root
     * @return     true iff height-balanced
     */
    fun isBalanced(root: TreeNode?): Boolean {
        fun checkHeight(node: TreeNode?): Int {
            if (node == null) return 0

            val leftHeight = checkHeight(node.left)
            if (leftHeight == -1) return -1

            val rightHeight = checkHeight(node.right)
            if (rightHeight == -1) return -1

            if (abs(leftHeight - rightHeight) > 1) return -1

            return maxOf(leftHeight, rightHeight) + 1
        }

        return checkHeight(root) != -1
    }
}
```

```java
public class BalancedBinaryTree {
    private int height(TreeNode node) {
        if (node == null) return 0;

        int left = height(node.left);
        if (left == -1) return -1;

        int right = height(node.right);
        if (right == -1) return -1;

        if (Math.abs(left - right) > 1) return -1;
        return Math.max(left, right) + 1;
    }

    /**
     * @param root tree root
     * @return     true iff height-balanced
     */
    public boolean isBalanced(TreeNode root) {
        return height(root) != -1;
    }
}
```

```cpp
#include <cstdlib>
#include <algorithm>

class BalancedBinaryTree {
    int height(TreeNode* node) {
        if (!node) return 0;

        int left = height(node->left);
        if (left == -1) return -1;

        int right = height(node->right);
        if (right == -1) return -1;

        if (std::abs(left - right) > 1) return -1;
        return std::max(left, right) + 1;
    }

public:
    /**
     * @param root tree root
     * @return     true iff height-balanced
     */
    bool isBalanced(TreeNode* root) {
        return height(root) != -1;
    }
};
```

```python
def is_balanced(root: Optional["TreeNode"]) -> bool:
    """
    @param root: tree root
    @return:     true iff height-balanced
    """
    def height(node):
        if not node:
            return 0

        left = height(node.left)
        if left == -1:
            return -1

        right = height(node.right)
        if right == -1:
            return -1

        if abs(left - right) > 1:
            return -1

        return max(left, right) + 1

    return height(root) != -1
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root tree root
    /// @return     true iff height-balanced
    pub fn is_balanced(root: Option<Rc<RefCell<TreeNode>>>) -> bool {
        fn height(node: Option<Rc<RefCell<TreeNode>>>) -> i32 {
            match node {
                None => 0,
                Some(n) => {
                    let left = height(n.borrow().left.clone());
                    if left == -1 { return -1; }

                    let right = height(n.borrow().right.clone());
                    if right == -1 { return -1; }

                    if (left - right).abs() > 1 { -1 } else { left.max(right) + 1 }
                }
            }
        }
        height(root) != -1
    }
}
```

## Dry run

**Input:** `root = [1,2,2,3,3,null,null,4,4]`.

```
4 leaves: height 1.  3s: height 2.  left 2: left subtree 3 (h 2), right 3 (h 2) -> h 3.
right 2: height 1.  root 1: left 3 vs right 1 -> |2| > 1 -> -1 -> false ✓
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

- **Maximum Depth Of Binary Tree** ([5.1](maximum-depth-of-binary-tree.md)) — the height engine.
- **Interview follow-up:** "Why return -1 instead of a boolean + height pair?" The sentinel folds both pieces of info into one return — O(n) total with zero extra state.
