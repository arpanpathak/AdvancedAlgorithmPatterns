# 5.10 Diameter Of Binary Tree

> **Source:** [`src/main/kotlin/graph/DiameterOfBinaryTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/DiameterOfBinaryTree.kt)
> **Pattern:** post-order height + global best · **Core page**

## The Problem

Given a binary tree, return the **length of the longest path** between any two nodes (in edges; the path need not pass through the root).

- Constraints: $0 \le n \le 10^4$.

## Examples

```
Input:  root = [1,2,3,4,5]
Output: 3   (the path 4-2-1-3 or 5-2-1-3, 3 edges)
```

## Intuition — every path is "left height + right height" through some node; maximize over all nodes

Any tree path has a **highest node** (the LCA of its endpoints). The path through that node has length `leftHeight + rightHeight` (heights measured in edges). So the diameter is:

$$
\text{diameter} = \max_{\text{node}} (\text{height(left)} + \text{height(right)})
$$

**The post-order computation** — the [5.1](maximum-depth-of-binary-tree.md) height recursion, upgraded: each node computes its two child heights and *both* returns its own height (`1 + max(left, right)`) *and* checks the candidate `2 + left + right` against a global `max`. Same shape as [5.4](binary-tree-maximum-path-sum.md)'s "global best + return the single-branch contribution" idiom.

**Why `height(null) = -1`?** With edges-as-length, a leaf has height 0: `height(node) = 1 + max(left, right)`, so `height(leaf) = 1 + max(-1, -1) = 0`, and the candidate through a leaf is `2 + (-1) + (-1) = 0` — a single node has diameter 0, consistent. The repo's `-1` base is exactly this calibration.

**Why not just `height(root.left) + height(root.right)`?** The longest path may sit entirely inside a subtree — `[1,2,3,4,5]`-style trees where the root's own span is short but a grandchild's is long. Only the *global max over all nodes* catches that.

## Approach 1 — Height per node, recomputed (O(n^2))

For each node compute both subtree heights from scratch: correct, quadratic on skewed trees.

## Approach 2 — Post-order with a running max (the repo's version, optimal)

```kotlin
class DiameterOfBinaryTree {
    /**
     * @param root tree root
     * @return     longest path between any two nodes (in edges)
     */
    fun diameterOfBinaryTree(root: TreeNode?): Int {
        var max = 0

        fun height(root: TreeNode?): Int {
            if (null == root) return -1                 // edge-based height: null = -1

            val left = height(root.left)
            val right = height(root.right)

            max = maxOf(max, 2 + left + right)          // path through this node
            return 1 + maxOf(left, right)               // this node's height for the parent
        }

        height(root)
        return max
    }
}
```

```java
public class DiameterOfBinaryTree {
    private int max = 0;

    /**
     * @param root tree root
     * @return     longest path between any two nodes (in edges)
     */
    public int diameterOfBinaryTree(TreeNode root) {
        height(root);
        return max;
    }

    private int height(TreeNode node) {
        if (node == null) return -1;                    // edge-based height: null = -1

        int left = height(node.left);
        int right = height(node.right);

        max = Math.max(max, 2 + left + right);          // path through this node
        return 1 + Math.max(left, right);               // this node's height for the parent
    }
}
```

```cpp
class DiameterOfBinaryTree {
    int max = 0;

    int height(TreeNode* node) {
        if (!node) return -1;                           // edge-based height: null = -1

        int left = height(node->left);
        int right = height(node->right);

        max = std::max(max, 2 + left + right);          // path through this node
        return 1 + std::max(left, right);               // this node's height for the parent
    }

public:
    /**
     * @param root tree root
     * @return     longest path between any two nodes (in edges)
     */
    int diameterOfBinaryTree(TreeNode* root) {
        height(root);
        return max;
    }
};
```

```python
def diameter_of_binary_tree(root: Optional["TreeNode"]) -> int:
    """
    @param root: tree root
    @return:     longest path between any two nodes (in edges)
    """
    max_d = 0

    def height(node) -> int:
        nonlocal max_d
        if node is None:
            return -1                      # edge-based height: null = -1

        left = height(node.left)
        right = height(node.right)

        max_d = max(max_d, 2 + left + right)   # path through this node
        return 1 + max(left, right)            # this node's height for the parent

    height(root)
    return max_d
```

```rust
use std::cell::RefCell;
use std::rc::Rc;

impl Solution {
    /// @param root tree root
    /// @return     longest path between any two nodes (in edges)
    pub fn diameter_of_binary_tree(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        let mut max = 0;

        fn height(node: Option<Rc<RefCell<TreeNode>>>, max: &mut i32) -> i32 {
            let Some(n) = node else { return -1; };    // edge-based height: null = -1
            let n = n.borrow();

            let left = height(n.left.clone(), max);
            let right = height(n.right.clone(), max);

            *max = (*max).max(2 + left + right);       // path through this node
            1 + left.max(right)                        // this node's height for the parent
        }

        height(root, &mut max);
        max
    }
}
```

## Dry run

**Input:** `root = [1,2,3,4,5]` — 1 (left 2, right 3); 2 (left 4, right 5).

```
height(4) = 1 + max(-1,-1) = 0.  height(5) = 0.
height(2): left=0, right=0.  max = max(0, 2+0+0) = 2.  return 1.
height(3) = 0.
height(1): left=1, right=0.  max = max(2, 2+1+0) = 3.  return 1.

Output: 3 ✓   (the path 4-2-1-3, three edges)
```

The `max` update at node 2 (candidate 2) is *local* — the path `4-2-5` — but node 1's candidate 3 wins: `left height 1` (through 2→4/5) + `right height 0` (3). The height return (single-branch) and the diameter check (both branches) are computed from the same two child values — one recursion, two answers.

## Complexity

**Time.** Each node visited once:

$$
T(n) = O(n)
$$

**Space.** Recursion depth (worst case skewed):

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Binary Tree Maximum Path Sum** ([5.4](binary-tree-maximum-path-sum.md)) — the weighted twin: same post-order "global best + return single-branch" skeleton, with values instead of edge counts (and the `max(0, ...)` drop for negative contributions).
- **Diameter Of N-Array Tree** (`tree/DiameterOfNArrayTree.kt`) — the same two-largest-children idea over a list of children.
- **Longest Univalue Path** (`tree/LongestUnivaluePath.kt`) — diameter with a value-equality constraint on the edges that count.
- **Interview follow-up:** "Why `-1` for null height?" Counting *edges* means a leaf is height 0 — `1 + max(-1, -1) = 0` — and a single-node "path" is diameter 0 (`2 + (-1) + (-1)`). The `-1` base is the edge-counting calibration, not an arbitrary sentinel.
