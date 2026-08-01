# 5.34 Binary Search Tree To Greater Sum Tree

> **Source**: [`src/main/kotlin/graph/bst/BinarySearchTreeToGreaterSumTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/bst/BinarySearchTreeToGreaterSumTree.kt)
> **Pattern**: reverse inorder accumulation · **Core page**

## The Problem

Replace each node's value with the sum of all values ≥ it.

- Constraints: n ≤ 100.

## Examples

```
Input:  root = [4,1,6,0,2,5,7,null,null,null,3,null,null,null,8]
Output: [30,36,21,36,35,26,15,null,null,null,33,null,null,null,8]
```

## Intuition — reverse inorder (right, node, left) accumulates the running sum

A BST's reverse inorder visits descending values; the running sum IS the "greater sum":

```kotlin
var sum = 0

fun bstToGst(root: TreeNode?): TreeNode? {
    if (root != null) {
        bstToGst(root.right)
        sum += root.`val`
        root.`val` = sum
        bstToGst(root.left)
    }
    return root
}
```

## Approach 1 — Reverse inorder accumulation (the repo's version, optimal)

```kotlin
class BinarySearchTreeToGreaterSumTree {
    /**
     * @param root BST root
     * @return     greater-sum tree root
     */
    fun bstToGst(root: TreeNode?): TreeNode? {
        var sum = 0

        fun dfs(node: TreeNode?) {
            if (node == null) return

            dfs(node.right)
            sum += node.`val`
            node.`val` = sum
            dfs(node.left)
        }

        dfs(root)
        return root
    }
}
```

```java
public class BinarySearchTreeToGreaterSumTree {
    private int sum = 0;

    private void dfs(TreeNode node) {
        if (node == null) return;

        dfs(node.right);
        sum += node.val;
        node.val = sum;
        dfs(node.left);
    }

    /**
     * @param root BST root
     * @return     greater-sum tree root
     */
    public TreeNode bstToGst(TreeNode root) {
        sum = 0;
        dfs(root);
        return root;
    }
}
```

```cpp
class BinarySearchTreeToGreaterSumTree {
    int sum = 0;

    void dfs(TreeNode* node) {
        if (!node) return;

        dfs(node->right);
        sum += node->val;
        node->val = sum;
        dfs(node->left);
    }

public:
    /**
     * @param root BST root
     * @return     greater-sum tree root
     */
    TreeNode* bstToGst(TreeNode* root) {
        sum = 0;
        dfs(root);
        return root;
    }
};
```

```python
def bst_to_gst(root: Optional["TreeNode"]) -> Optional["TreeNode"]:
    """
    @param root: BST root
    @return:     greater-sum tree root
    """
    total = 0

    def dfs(node):
        nonlocal total
        if not node:
            return

        dfs(node.right)
        total += node.val
        node.val = total
        dfs(node.left)

    dfs(root)
    return root
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root BST root
    /// @return     greater-sum tree root
    pub fn bst_to_gst(root: Option<Rc<RefCell<TreeNode>>>) -> Option<Rc<RefCell<TreeNode>>> {
        fn dfs(node: Option<Rc<RefCell<TreeNode>>>, sum: &mut i32) {
            if let Some(n) = node {
                dfs(n.borrow().right.clone(), sum);

                *sum += n.borrow().val;
                n.borrow_mut().val = *sum;

                dfs(n.borrow().left.clone(), sum);
            }
        }

        dfs(root.clone(), &mut 0);
        root
    }
}
```

## Dry run

**Input:** the example.

```
reverse inorder: 8 -> sum 8, val 8.  7 -> 15.  6 -> 21.  5 -> 26.  4 -> 30.
3 -> 33.  2 -> 35.  1 -> 36.  0 -> 36.
Output matches the expected tree ✓
```

## Complexity

**Time.** One walk:

$$
T(n) = O(n)
$$

**Space.** Recursion:

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **Binary Tree Inorder Traversal** ([5.7](binary-tree-inorder-traversal-iterative.md)) — the reversed walk.
- **Interview follow-up:** "Why reverse inorder?" The "sum of all ≥ me" is a suffix of the sorted order — walking descending builds it in one pass with a running total.
