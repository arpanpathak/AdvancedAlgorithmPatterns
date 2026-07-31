# 5.3 Lowest Common Ancestor

> **Source:** [`src/main/kotlin/tree/LowestCommonAncestor.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/LowestCommonAncestor.kt)
> **Pattern:** post-order "found?" propagation · **Core page**

## The Problem

Given the root of a binary tree and two nodes `p` and `q`, return their **lowest common ancestor (LCA)** — the deepest node that has both `p` and `q` as descendants (a node may be its own descendant).

- Constraints: $2 \le n \le 10^5$; all values unique; `p != q`; both exist in the tree.

## Examples

```
Input:      3
           / \
          5   1
         / \ / \
        6  2 0  8
          / \
         7   4
p = 5, q = 1      -> Output: 3
p = 5, q = 4      -> Output: 5   (5 is an ancestor of 4, so 5 is its own descendant)
```

## Intuition — "report up who you found"

This is the purest **post-order propagation** problem in the chapter. At every node the recursion answers one question: *"does my subtree contain `p`, `q`, or both?"* — and the answers flow **up**:

- a child subtree that found **nothing** reports `null`;
- a child subtree that found **one** of the targets reports that node;
- the first node whose **left and right both report a target** is the LCA — one target is under the left subtree, the other under the right, so nothing deeper can be an ancestor of both.

There's no global state, no visited set, no parent pointers — the tree's own structure does the work. The `root == p || root == q` check also handles the "ancestor of itself" case: if `p` sits *above* `q`, the recursion unwinds and `p` is reported all the way up.

**Why post-order and not pre/in-order?** You can't know whether a node is the LCA until you know what its children found. The decision needs the *children's* answers — that is literally the definition of post-order.

## Approach 1 — Parent-pointer walk (works for any DAG, needs extra pass)

Do a DFS recording each node's parent into a map, then walk `p`'s ancestors into a set and climb `q` until it hits one. $O(n)$ time, $O(n)$ space. Elegant and general — but the recursive version below needs no extra storage at all.

## Approach 2 — Recursive post-order propagation (the repo's version, optimal)

```kotlin
class LowestCommonAncestor {
    /**
     * @param root the root of the binary tree
     * @param p    first target node
     * @param q    second target node
     * @return     the lowest common ancestor of p and q
     */
    fun lowestCommonAncestor(root: TreeNode?, p: TreeNode?, q: TreeNode?): TreeNode? {
        if (root == null || root === p || root === q) return root

        val left = lowestCommonAncestor(root.left, p, q)
        val right = lowestCommonAncestor(root.right, p, q)

        return if (left != null && right != null) root else left ?: right
    }
}
```

```java
public class LowestCommonAncestor {
    /**
     * @param root the root of the binary tree
     * @param p    first target node
     * @param q    second target node
     * @return     the lowest common ancestor of p and q
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left != null && right != null) return root;
        return left != null ? left : right;
    }
}
```

```cpp
class LowestCommonAncestor {
public:
    /**
     * @param root the root of the binary tree
     * @param p    first target node
     * @param q    second target node
     * @return     the lowest common ancestor of p and q
     */
    TreeNode* lowestCommonAncestor(TreeNode* root, TreeNode* p, TreeNode* q) {
        if (root == nullptr || root == p || root == q) return root;

        TreeNode* left = lowestCommonAncestor(root->left, p, q);
        TreeNode* right = lowestCommonAncestor(root->right, p, q);

        if (left && right) return root;
        return left ? left : right;
    }
};
```

```python
def lowest_common_ancestor(root: TreeNode | None, p: TreeNode, q: TreeNode) -> TreeNode | None:
    """
    @param root: the root of the binary tree
    @param p:    first target node
    @param q:    second target node
    @return:     the lowest common ancestor of p and q
    """
    if root is None or root is p or root is q:
        return root

    left = lowest_common_ancestor(root.left, p, q)
    right = lowest_common_ancestor(root.right, p, q)

    if left is not None and right is not None:
        return root
    return left if left is not None else right
```

```rust
impl Solution {
    /// @param root the root of the binary tree
    /// @param p    first target node
    /// @param q    second target node
    /// @return     the lowest common ancestor of p and q
    pub fn lowest_common_ancestor(
        root: Option<Rc<RefCell<TreeNode>>>,
        p: Option<Rc<RefCell<TreeNode>>>,
        q: Option<Rc<RefCell<TreeNode>>>,
    ) -> Option<Rc<RefCell<TreeNode>>> {
        if root.is_none() || root == p || root == q {
            return root;
        }
        let left = Self::lowest_common_ancestor(
            root.as_ref().unwrap().borrow().left.clone(), p.clone(), q.clone());
        let right = Self::lowest_common_ancestor(
            root.as_ref().unwrap().borrow().right.clone(), p.clone(), q.clone());

        if left.is_some() && right.is_some() {
            return root;
        }
        if left.is_some() { left } else { right }
    }
}
```

> **Rust note:** the `root == p` comparison works because `Rc` compares by pointee *identity* here — both point to the same heap node — which is exactly the reference equality the algorithm needs. `p.clone()`/`q.clone()` are cheap refcount bumps, not deep copies.

## Dry run

**Input:** the tree above, `p = 5`, `q = 1`. Trace the recursion (each frame reports one value up):

```
lowestCommonAncestor(3):
  left:  lowestCommonAncestor(5): root == p -> report 5      (whole 5-subtree short-circuits)
  right: lowestCommonAncestor(1): root == q -> report 1      (whole 1-subtree short-circuits)
  left=5 != null AND right=1 != null -> return 3  ✓  (LCA)
```

Now `p = 5`, `q = 4`:

```
lowestCommonAncestor(3):
  left:  lowestCommonAncestor(5): root == p -> report 5
  right: lowestCommonAncestor(1) ->
           left: lowestCommonAncestor(0) -> null
           right: lowestCommonAncestor(8) -> null
           left=right=null -> return null
  left=5, right=null -> return 5  ✓  (5 is an ancestor of 4)
```

The first trace shows the short-circuit: once a subtree root *is* a target, we never explore below it — and that's safe, because that node is trivially the LCA of the targets within it.

## Complexity

**Time.** Each node is visited at most once (the two target subtrees short-circuit early, but the bound stays worst-case):

$$
T(n) = O(n)
$$

**Space.** Recursion stack, depth = height:

$$
S(n) = O(h), \quad h \in [\log n, n]
$$

## Variants & follow-ups

- **LCA of a BST** (`src/main/kotlin/tree/bst/`) — the BST property prunes the search to one path: walk from root, go left if both targets are smaller, right if both are larger, stop at the first node between them. $O(h)$ time — a *different* algorithm that only works because of the ordering.
- **Lowest Common Ancestor III** (`src/main/kotlin/tree/LowestCommonAncestor_III.kt`) — the variant where `p` or `q` may be *absent*: recursion alone can't distinguish "not found" from "found here", so the answer must be verified with a second pass.
- **LCA of Deepest Leaves / Kth Ancestor** — binary lifting precomputes $2^k$-ancestors for $O(\log n)$ ancestor queries; the LCA becomes a two-pointer lift.
- **Interview follow-up:** "Why doesn't the null-return collide with the `root == p` return?" Because a *real* find always returns a non-null node and `null` means "nothing found" — the two signals never overlap. The one place this ambiguity bites is LCA III above.
