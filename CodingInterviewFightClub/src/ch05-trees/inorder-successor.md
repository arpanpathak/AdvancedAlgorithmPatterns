# 5.24 Inorder Successor In BST

> **Source**: [`src/main/kotlin/tree/bst/InorderSuccessor.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bst/InorderSuccessor.kt)
> **Pattern**: BST walk with successor memory · **Core page**

## The Problem

The next node after `p` in inorder (or null).

- Constraints: n ≤ 10⁴; p exists.

## Examples

```
Input:  root = [2,1,3], p = 1   -> Output: 2
Input:  root = [5,3,6,2,4,null,null,1], p = 6 -> Output: null
```

## Intuition — descend; every left turn remembers the node

The successor of `p` is the smallest node > p. Walk from the root: go left when `p < current` (current is a *candidate* successor — remember it); go right otherwise:

```kotlin
var successor: TreeNode? = null
var current = root

while (current != null) {
    if (p!!.`val` < current.`val`) {
        successor = current      // candidate: the smallest so far that's > p
        current = current.left
    } else {
        current = current.right
    }
}
return successor
```

**Why does a left turn remember?** Every node we turn left at is > p (we're going to its left subtree) — the *last* such node is the smallest > p, i.e. the successor. The [5.3](lowest-common-ancestor.md) BST walk with a memory.

## Approach 1 — Inorder traversal list (O(n) space)

Collect inorder, find p's next: correct, wasteful.

## Approach 2 — Successor-memory walk (the repo's version, optimal)

```kotlin
class InorderSuccessor {
    /**
     * @param root BST root
     * @param p    target node
     * @return     inorder successor of p
     */
    fun inorderSuccessor(root: TreeNode?, p: TreeNode?): TreeNode? {
        var successor: TreeNode? = null
        var current = root

        while (current != null) {
            if (p!!.`val` < current.`val`) {
                successor = current
                current = current.left
            } else {
                current = current.right
            }
        }
        return successor
    }
}
```

```java
public class InorderSuccessorInBST {
    /**
     * @param root BST root
     * @param p    target node
     * @return     inorder successor of p
     */
    public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
        TreeNode successor = null;

        while (root != null) {
            if (p.val < root.val) {
                successor = root;
                root = root.left;
            } else {
                root = root.right;
            }
        }
        return successor;
    }
}
```

```cpp
class InorderSuccessorInBST {
public:
    /**
     * @param root BST root
     * @param p    target node
     * @return     inorder successor of p
     */
    TreeNode* inorderSuccessor(TreeNode* root, TreeNode* p) {
        TreeNode* successor = nullptr;

        while (root) {
            if (p->val < root->val) {
                successor = root;
                root = root->left;
            } else {
                root = root->right;
            }
        }
        return successor;
    }
};
```

```python
def inorder_successor(root: Optional["TreeNode"], p: Optional["TreeNode"]) -> Optional["TreeNode"]:
    """
    @param root: BST root
    @param p:    target node
    @return:     inorder successor of p
    """
    successor = None

    while root:
        if p.val < root.val:
            successor = root
            root = root.left
        else:
            root = root.right

    return successor
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root BST root
    /// @param p    target node
    /// @return     inorder successor of p
    pub fn inorder_successor(root: Option<Rc<RefCell<TreeNode>>>, p: Option<Rc<RefCell<TreeNode>>>) -> Option<Rc<RefCell<TreeNode>>> {
        let p_val = p.unwrap().borrow().val;
        let mut cur = root;
        let mut successor: Option<Rc<RefCell<TreeNode>>> = None;

        while let Some(node) = cur.clone() {
            if p_val < node.borrow().val {
                successor = Some(node.clone());
                cur = node.borrow().left.clone();
            } else {
                cur = node.borrow().right.clone();
            }
        }
        successor
    }
}
```

## Dry run

**Input:** `root = [2,1,3], p = 1`.

```
cur=2: 1 < 2 -> successor=2.  cur=1.
cur=1: 1 < 1? no -> cur=1.right = null.
Output: 2 ✓

Input: p = 2: cur=2: 2 < 2? no -> cur=3.  cur=3: 2 < 3 -> successor=3.  cur=null.
Output: 3 ✓
```

## Complexity

**Time.** Height walk:

$$
T(n) = O(h)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **BST Iterator** ([18.12](../ch18-design-caches/bst-iterator.md)) — the streaming version.
- **Interview follow-up:** "Why is the last left-turn the successor?" The successor is the smallest node > p. Every left turn's node is > p and *descends* toward p — the last one before reaching p's subtree is the tightest upper bound, exactly the successor. No parent pointers needed.
