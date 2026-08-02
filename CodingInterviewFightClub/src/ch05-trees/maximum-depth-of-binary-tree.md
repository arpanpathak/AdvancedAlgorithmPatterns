# 5.1 Maximum Depth Of Binary Tree

> **Source:** [`src/main/kotlin/tree/MaximumDepthOfBinaryTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/MaximumDepthOfBinaryTree.kt)
> **Pattern:** post-order recursion · **Core page — the smallest complete tree DP**

## The Problem

Given the root of a binary tree, return its **maximum depth** — the number of nodes along the longest path from the root down to the farthest leaf.

- Constraints: $0 \le n \le 10^4$.

## Examples

```
Input:      3
           / \
          9  20
             /  \
            15   7
Output: 3   (the path 3 -> 20 -> 15, three nodes)
```

## Intuition — "how deep am I?" is "1 + deepest child"

The depth of a node is defined recursively:

$$
\text{depth}(node) =
\begin{cases}
0 & node = null \\[1mm]
1 + \max\!\big(\text{depth}(node.left),\; \text{depth}(node.right)\big) & \text{otherwise}
\end{cases}
$$

The `+1` counts the current node; the `max` picks the deeper child. The `null → 0` base case is what terminates the recursion (a null child contributes nothing). This is **post-order** in spirit — you need both children's depths *before* you can compute the parent's — and it's the canonical "smallest tree DP": one line of combine, no global state.

## Approach 1 — BFS (count levels)

A level-order walk ([5.2](binary-tree-level-order-traversal.md)) can count levels: each BFS "level fence" is one depth unit. $O(n)$ time, $O(w)$ space.

## Approach 2 — Recursive post-order (the repo's version, optimal)

```kotlin
/**
 * @param root the root of the binary tree
 * @return     the maximum depth (longest root-to-leaf node count)
 */
fun maxDepth(root: TreeNode?): Int {
    if (root == null) return 0
    return 1 + maxOf(maxDepth(root.left), maxDepth(root.right))
}
```

```java
public class MaximumDepthOfBinaryTree {
    /**
     * @param root the root of the binary tree
     * @return     the maximum depth (longest root-to-leaf node count)
     */
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }
}
```

```cpp
#include <algorithm>

class MaximumDepthOfBinaryTree {
public:
    /**
     * @param root the root of the binary tree
     * @return     the maximum depth (longest root-to-leaf node count)
     */
    int maxDepth(TreeNode* root) {
        if (root == nullptr) return 0;
        return 1 + std::max(maxDepth(root->left), maxDepth(root->right));
    }
};
```

```python
def max_depth(root: TreeNode | None) -> int:
    """
    @param root: the root of the binary tree
    @return:     the maximum depth (longest root-to-leaf node count)
    """
    if root is None:
        return 0
    return 1 + max(max_depth(root.left), max_depth(root.right))
```

```rust
impl Solution {
    /// @param root the root of the binary tree
    /// @return     the maximum depth (longest root-to-leaf node count)
    pub fn max_depth(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        match root {
            None => 0,
            Some(node) => {
                let n = node.borrow();
                1 + Self::max_depth(n.left.clone()).max(Self::max_depth(n.right.clone()))
            }
        }
    }
}
```

## Reading the code — what's actually happening

```kotlin
fun maxDepth(root: TreeNode?): Int {
    if (root == null) return 0
    return 1 + maxOf(maxDepth(root.left), maxDepth(root.right))
}
```

Follow one node and the whole tree follows:

- **`if (root == null) return 0` is the ground floor.** A non-existent subtree has depth 0 — it contributes no nodes. This is the base case that stops the recursion: every leaf's children are null, so every leaf computes `1 + max(0, 0) = 1`.
- **`maxDepth(root.left)` and `maxDepth(root.right)` ask the children first.** We can't know how deep this node is until we know how deep its children are. The recursion dives all the way down to the leaves, then bubbles answers back up — this bottom-up order is what "post-order" means.
- **`maxOf(...)` picks the deeper child.** The longest path through this node goes through whichever child is taller. If one child is null (depth 0) and the other is depth 7, the node's depth is `1 + 7` — the null side never drags it down.
- **The `+1` counts this node itself.** Every level of recursion adds one for the node currently being visited, so a chain of `k` nodes reports depth exactly `k`.

Unroll it for the example: `maxDepth(9)` = 1 (leaf). `maxDepth(15)` = 1, `maxDepth(7)` = 1, so `maxDepth(20)` = `1 + max(1,1)` = 2. Then `maxDepth(3)` = `1 + max(1, 2)` = 3 ✓. Each line of code does exactly one thing, and the whole algorithm is the recursive definition of depth written literally.

## Dry run

**Input:** the tree above. Trace the recursion (each line = one frame):

```
maxDepth(3):
  maxDepth(9): 9 is a leaf -> 1 + max(0, 0) = 1
  maxDepth(20):
    maxDepth(15): 1 + max(0, 0) = 1
    maxDepth(7):  1 + max(0, 0) = 1
    -> 1 + max(1, 1) = 2
  -> 1 + max(1, 2) = 3
Answer: 3 ✓
```

The stack depth is exactly the tree height (3 here) — worth noting because that's the $O(h)$ space cost and the skew-tree overflow risk.

## Complexity

**Time.** Every node visited once:

$$
T(n) = O(n)
$$

**Space.** $O(h)$ recursion stack, $h \in [\log n, n]$.

## Variants & follow-ups

- **Balanced Binary Tree** (`src/main/kotlin/tree/BalancedBinaryTree.kt`) — same traversal, but the combine checks `|left - right| <= 1` and propagates an "unbalanced" signal up (often via a sentinel like `-1`).
- **Diameter of Binary Tree** — the *sum* of the two child depths instead of the max — the classic follow-up that turns "max child" into "both children".
- **Minimum Depth** — the mirror: `1 + min(...)` — *but* with the corner case that a node with only one child still counts the non-null side (a leaf check is needed). Saying that trap unprompted is a strong signal.
- **Interview follow-up:** "Iterative version?" Level-order BFS counting fences, or an explicit-stack DFS tracking depth per node. Both $O(n)$; the BFS one doubles as [5.2](binary-tree-level-order-traversal.md).
