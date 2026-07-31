# 5.11 Recover Binary Search Tree

> **Source:** [`src/main/kotlin/tree/bst/RecoverBinarySearchTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bst/RecoverBinarySearchTree.kt)
> **Pattern:** in-order detects the two swapped nodes · **Core page**

## The Problem

Two nodes of a BST were swapped. Restore the tree **without changing its structure** — O(1) extra space (apart from recursion).

- Constraints: tree size ≤ 10⁴; exactly two nodes swapped.

## Examples

```
Input:  [1,3,null,null,2]   -> Output: [3,1,null,null,2]   (swap 1 and 3)
Input:  [3,1,4,null,null,2] -> Output: [2,1,4,null,null,3]   (swap 2 and 3)
```

## Intuition — an in-order walk is *almost* sorted; the two violations are the swaps

A BST's in-order traversal is sorted. Swapping two nodes creates **at most two descending pairs** (the [5.7](binary-tree-inorder-traversal-iterative.md) walk with a `prev` pointer):

```
first = null; second = null; prev = null
dfs(node):
    dfs(node.left)
    if prev != null && prev.val > node.val:   # a violation
        if first == null: first = prev        # first offender
        second = node                         # second offender (updated each time)
    prev = node
    dfs(node.right)
swap(first.val, second.val)
```

**Why two violations for *adjacent* swaps, and why does `second = node` every time work?** If the swapped nodes are adjacent in the sorted order (e.g. `[1,3,2,4]`), there's ONE descending pair `(3,2)` — `first = 3`, `second = 2`. If non-adjacent (`[1,4,3,2,5]`... actually `[3,2,1]` style: `[1,4,3,2]`), there are TWO pairs `(4,3)` and `(3,2)` — `first` keeps the first pair's left, `second` is overwritten to the last pair's right. Both cases collapse to `swap(first, second)`.

**Why in-order and not a heap check?** The BST property is exactly "in-order is sorted" — one traversal both finds the offenders and stays O(n). The [5.7](binary-tree-inorder-traversal-iterative.md) iterator is the iterative twin; the repo's recursive `dfs` is the compact form.

## Approach 1 — Collect values, sort, rewrite (O(n) space)

In-order collect → sort → assign back: correct, but the O(1)-space constraint is the point.

## Approach 2 — In-order with prev/first/second (the repo's version, optimal)

```kotlin
class RecoverBinarySearchTree {
    /**
     * @param root BST root with exactly two swapped nodes
     */
    fun recoverTree(root: TreeNode?) {
        var first: TreeNode? = null
        var second: TreeNode? = null
        var prev: TreeNode? = null

        fun dfs(node: TreeNode?) {
            if (node == null) return

            dfs(node.left)

            // Identify swapped nodes
            if (prev != null && prev!!.`val` > node.`val`) {
                if (first == null) {
                    first = prev          // first out-of-order node
                }
                second = node             // second out-of-order node
            }
            prev = node

            dfs(node.right)
        }

        dfs(root)

        // Swap the values of the two nodes
        first?.let { f ->
            second?.let { s ->
                val temp = f.`val`
                f.`val` = s.`val`
                s.`val` = temp
            }
        }
    }
}
```

```java
public class RecoverBinarySearchTree {
    private TreeNode first = null, second = null, prev = null;

    private void dfs(TreeNode node) {
        if (node == null) return;

        dfs(node.left);

        if (prev != null && prev.val > node.val) {
            if (first == null) first = prev;   // first offender
            second = node;                     // last offender
        }
        prev = node;

        dfs(node.right);
    }

    /**
     * @param root BST root with exactly two swapped nodes
     */
    public void recoverTree(TreeNode root) {
        dfs(root);

        int t = first.val;
        first.val = second.val;
        second.val = t;
    }
}
```

```cpp
class RecoverBinarySearchTree {
    TreeNode* first = nullptr;
    TreeNode* second = nullptr;
    TreeNode* prev = nullptr;

    void dfs(TreeNode* node) {
        if (!node) return;

        dfs(node->left);

        if (prev && prev->val > node->val) {
            if (!first) first = prev;   // first offender
            second = node;              // last offender
        }
        prev = node;

        dfs(node->right);
    }

public:
    /**
     * @param root BST root with exactly two swapped nodes
     */
    void recoverTree(TreeNode* root) {
        dfs(root);
        std::swap(first->val, second->val);
    }
};
```

```python
def recover_tree(root: Optional["TreeNode"]) -> None:
    """
    @param root: BST root with exactly two swapped nodes
    """
    first = second = prev = None

    def dfs(node):
        nonlocal first, second, prev
        if not node:
            return

        dfs(node.left)

        if prev and prev.val > node.val:
            if first is None:
                first = prev            # first offender
            second = node               # last offender
        prev = node

        dfs(node.right)

    dfs(root)
    first.val, second.val = second.val, first.val
```

```rust
impl Solution {
    /// @param root BST root with exactly two swapped nodes
    pub fn recover_tree(root: &mut Option<Rc<RefCell<TreeNode>>>) {
        let mut first: Option<Rc<RefCell<TreeNode>>> = None;
        let mut second: Option<Rc<RefCell<TreeNode>>> = None;
        let mut prev: Option<Rc<RefCell<TreeNode>>> = None;

        fn dfs(node: Option<Rc<RefCell<TreeNode>>>,
               first: &mut Option<Rc<RefCell<TreeNode>>>,
               second: &mut Option<Rc<RefCell<TreeNode>>>,
               prev: &mut Option<Rc<RefCell<TreeNode>>>) {
            if let Some(n) = node {
                dfs(n.borrow().left.clone(), first, second, prev);

                if let Some(p) = prev.clone() {
                    if p.borrow().val > n.borrow().val {
                        if first.is_none() { *first = Some(p.clone()); }
                        *second = Some(n.clone());
                    }
                }
                *prev = Some(n.clone());

                dfs(n.borrow().right.clone(), first, second, prev);
            }
        }

        dfs(root.clone(), &mut first, &mut second, &mut prev);
        if let (Some(f), Some(s)) = (first, second) {
            std::mem::swap(&mut f.borrow_mut().val, &mut s.borrow_mut().val);
        }
    }
}
```

## Dry run

**Input:** `root = [1,3,null,null,2]` (the swapped BST: values 1 and 3 swapped).

```
in-order: 3, 2, 1   (the sorted order should be 1, 2, 3)

dfs(3): prev=null -> prev=3
dfs(2): prev=3.  3 > 2 -> violation.  first=3, second=2.  prev=2
dfs(1): prev=2.  2 > 1 -> violation.  first stays 3, second=1.  prev=1

swap(first=3, second=1) -> in-order becomes 1, 2, 3 ✓
```

Two violations here because the swapped nodes (3 and 1) are non-adjacent in sorted order: the first pair `(3,2)` sets `first`, the last pair `(2,1)` overwrites `second`. With an adjacent swap (`[1,3,2,4]`), one violation `(3,2)` gives `first=3, second=2` directly. The `first`-only-once + `second`-always pattern handles both.

## Complexity

**Time.** One in-order pass:

$$
T(n) = O(n)
$$

**Space.** Recursion depth (or O(1) with Morris):

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **Binary Tree Inorder Traversal** ([5.7](binary-tree-inorder-traversal-iterative.md)) — the traversal engine; Morris threading achieves true O(1) space.
- **Validate Binary Search Tree** (`tree/bst/ValidateBinarySearchTree.kt`) — the same prev-pointer idea for *checking* instead of repairing.
- **Interview follow-up:** "Why does `second = node` on *every* violation work?" If the two swaps are adjacent, only one violation fires — `second` is that pair's right node. If non-adjacent, two fire — `second` ends as the *second* pair's right node, which is the second swapped value. The overwrite is the "last violation" bookkeeping; `first`'s `if (first == null)` is the "first violation" guard.
