# 5.4 Binary Tree Maximum Path Sum

> **Source:** [`src/main/kotlin/tree/BinaryTreeMaximumPathSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/BinaryTreeMaximumPathSum.kt)
> **Pattern:** post-order with a global best · **Core page**

## The Problem

Given the root of a binary tree, return the **maximum path sum** — where a *path* is any sequence of nodes connected by parent-child edges, starting and ending anywhere, visiting each node at most once.

- Constraints: $1 \le n \le 3 \times 10^4$; $-1000 \le val \le 1000$.

## Examples

```
Input:      -10
           /  \
          9    20
              /  \
             15   7
Output: 42   (the path 15 -> 20 -> 7, sum = 15 + 20 + 7)

Input:  [-10, 9, 20, null, null, 15, 7]   (same tree, flattened)
Input:  root = [-3]            -> Output: -3   (single-node path)
```

## Intuition — "what can I hand my parent" vs "what's the best answer so far"

A path through a binary tree, viewed from any node `v`, has exactly three shapes:

1. **straight up** — starts somewhere in `v`'s subtree and ends *at* `v` (the parent will extend it);
2. **through `v`** — starts in `v`'s left subtree, passes through `v`, ends in `v`'s right subtree (the parent *cannot* extend this — it already uses both children);
3. **fully inside** a subtree, not touching `v` at all.

So the recursion returns the **partial** value — the best *straight-up* sum a parent can use — while a separate **global** variable records the best *complete* candidate (shapes 2 and 3) seen anywhere. This is exactly the ["global best" idiom](pattern-primer.md) from the primer: the function return is *"what can I hand my parent"*, the global is *"the best answer seen so far"*. Confusing the two is the #1 trap in this problem.

**Why the implicit clip?** The problem allows *negative* values, so a path may consist of a single node (`-3` in the example). And a child whose best contribution is negative is never worth extending through. The repo's code gets this clip *implicitly*: `max(max(left, right) + node.val, node.val)` — if both arms are negative, extending either one would lower the sum below just taking `node.val`, so `currentMax` falls back to the node alone. No explicit `max(child, 0)` needed; the `node.val` term *is* the clip.

## Approach 1 — Brute force, all-pairs paths

For every node, run a DFS to compute every path sum: $O(n^2)$ or worse, hopeless at $n = 3 \times 10^4$. The post-order version below visits each node once.

## Approach 2 — Post-order with a global best (the repo's version, optimal)

```kotlin
class BinaryTreeMaximumPathSum {
    /**
     * @param root the root of the binary tree
     * @return     the maximum sum over all paths in the tree
     */
    fun maxPathSum(root: TreeNode?): Int {
        var ans = Int.MIN_VALUE

        fun getMaxPathSum(node: TreeNode?): Int {
            if (node == null) return 0

            val left = getMaxPathSum(node.left)
            val right = getMaxPathSum(node.right)

            val currentMax = maxOf(maxOf(left, right) + node.`val`, node.`val`)  // straight-up
            val maxSoFar = maxOf(currentMax, left + right + node.`val`)          // through node
            ans = maxOf(maxSoFar, ans)                                           // global best

            return currentMax                                                   // hand to parent
        }

        getMaxPathSum(root)
        return ans
    }
}
```

```java
public class BinaryTreeMaximumPathSum {
    private int ans = Integer.MIN_VALUE;

    /**
     * @param root the root of the binary tree
     * @return     the maximum sum over all paths in the tree
     */
    public int maxPathSum(TreeNode root) {
        getMaxPathSum(root);
        return ans;
    }

    private int getMaxPathSum(TreeNode node) {
        if (node == null) return 0;

        int left = getMaxPathSum(node.left);
        int right = getMaxPathSum(node.right);

        int currentMax = Math.max(Math.max(left, right) + node.val, node.val);  // straight-up
        int maxSoFar = Math.max(currentMax, left + right + node.val);           // through node
        ans = Math.max(maxSoFar, ans);                                          // global best

        return currentMax;                                                      // hand to parent
    }
}
```

```cpp
#include <algorithm>
#include <climits>

class BinaryTreeMaximumPathSum {
    int ans = INT_MIN;

    /**
     * @param node current subtree root
     * @return     best straight-up path sum ending at node (for the parent)
     */
    int getMaxPathSum(TreeNode* node) {
        if (node == nullptr) return 0;

        int left = getMaxPathSum(node->left);
        int right = getMaxPathSum(node->right);

        int currentMax = std::max(std::max(left, right) + node->val, node->val);
        int maxSoFar = std::max(currentMax, left + right + node->val);
        ans = std::max(maxSoFar, ans);

        return currentMax;
    }

public:
    /**
     * @param root the root of the binary tree
     * @return     the maximum sum over all paths in the tree
     */
    int maxPathSum(TreeNode* root) {
        getMaxPathSum(root);
        return ans;
    }
};
```

```python
def max_path_sum(root: TreeNode | None) -> int:
    """
    @param root: the root of the binary tree
    @return:     the maximum sum over all paths in the tree
    """
    ans = float("-inf")

    def get_max_path_sum(node: TreeNode | None) -> int:
        nonlocal ans
        if node is None:
            return 0

        left = get_max_path_sum(node.left)
        right = get_max_path_sum(node.right)

        current_max = max(max(left, right) + node.val, node.val)  # straight-up
        max_so_far = max(current_max, left + right + node.val)    # through node
        ans = max(max_so_far, ans)                                # global best

        return current_max                                        # hand to parent

    get_max_path_sum(root)
    return ans
```

```rust
use std::cell::RefCell;
use std::rc::Rc;

impl Solution {
    /// @param root the root of the binary tree
    /// @return     the maximum sum over all paths in the tree
    pub fn max_path_sum(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        let mut ans = i32::MIN;

        fn dfs(node: &Option<Rc<RefCell<TreeNode>>>, ans: &mut i32) -> i32 {
            if let Some(n) = node {
                let n = n.borrow();
                let left = dfs(&n.left, ans);
                let right = dfs(&n.right, ans);

                let current_max = (left.max(right) + n.val).max(n.val);  // straight-up
                let max_so_far = current_max.max(left + right + n.val);  // through node
                *ans = (*ans).max(max_so_far);                           // global best

                current_max                                             // hand to parent
            } else {
                0
            }
        }

        dfs(&root, &mut ans);
        ans
    }
}
```

## Dry run

**Input:** the tree above.

```
dfs(-10):  ans=-inf
  dfs(9):  leaf -> left=0, right=0
           currentMax = max(0+9, 9) = 9
           maxSoFar   = max(9, 0+0+9) = 9        ans = 9
           return 9
  dfs(20):
    dfs(15): leaf -> currentMax=15, maxSoFar=15  ans = max(9,15) = 15, return 15
    dfs(7):  leaf -> currentMax=7,  maxSoFar=7   ans = 15, return 7
    currentMax = max(max(15,7)+20, 20) = 35      (straight-up: 15->20)
    maxSoFar   = max(35, 15+7+20) = 42           (through: 15->20->7)
    ans = max(15, 42) = 42, return 35
  currentMax = max(max(9,35)+(-10), -10) = max(25, -10) = 25
  maxSoFar   = max(25, 9+35-10) = 34
  ans = max(42, 34) = 42
Answer: 42 ✓
```

Notice the decisive moment at node `20`: the *returned* value is `35` (the parent can only use the `15 -> 20` arm), while the *global* captures `42` (the `15 -> 20 -> 7` path the parent can never extend). This is the partial-vs-complete split, visible in one trace.

## Complexity

**Time.** Every node visited once:

$$
T(n) = O(n)
$$

**Space.** Recursion stack, depth = height:

$$
S(n) = O(h), \quad h \in [\log n, n]
$$

## Variants & follow-ups

- **Diameter Of Binary Tree** — the *same* skeleton with one line changed: `maxSoFar = left + right` (sum of *lengths*, no `node.val`). If you can derive this page's answer, diameter is a 30-second delta.
- **Longest Univalue Path** (`src/main/kotlin/tree/LongestUnivaluePath.kt`) — straight-up arms are only kept when the child's value equals the parent's.
- **Maximum Product Of Splitted Binary Tree** (`src/main/kotlin/tree/MaximumProductOfSplittedBinaryTree.kt`) — post-order to compute every subtree sum, then maximize `sum * (total - sum)`.
- **Interview follow-up:** "What if values are all negative?" Then the answer is the maximum single node (the `node.val` term in `currentMax` handles it — `-3` alone wins). Walking through that case *before* being asked is a strong signal.
- **Interview follow-up:** "Why `Int.MIN_VALUE` and not `0` for `ans`?" Because with all-negative values a correct answer is negative — initializing to `0` would mask the real maximum.
