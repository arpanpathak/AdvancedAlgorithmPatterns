# 5.14 Count Good Nodes In Binary Tree

> **Source:** [`src/main/kotlin/tree/CountGoodNodeInBInaryTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/CountGoodNodeInBInaryTree.kt)
> **Pattern:** DFS carrying the running max · **Core page**

## The Problem

Count nodes whose value is **≥ every value on the root-to-node path** (a "good" node).

- Constraints: tree size ≤ 10⁵; values fit in `Int`.

## Examples

```
Input:  root = [3,1,4,3,null,1,5]   -> Output: 4   (nodes 3, 3, 4, 5)
Input:  root = [3,3,null,4,2]       -> Output: 3
```

## Intuition — carry the max-so-far down the path; the node is good iff it's ≥ that

One number fully encodes the path's history: the maximum seen so far. DFS with `maxSoFar`:

```kotlin
fun dfs(node: TreeNode?, maxSoFar: Int): Int {
    node ?: return 0

    val newMax = maxOf(maxSoFar, node.`val`)
    val good = if (node.`val` >= maxSoFar) 1 else 0

    return good + dfs(node.left, newMax) + dfs(node.right, newMax)
}
// call: dfs(root, Int.MIN_VALUE)
```

**Why is `maxSoFar` enough state?** "Good" depends only on whether the node beats the path's best so far — not on the path itself. The running max is the [5.4](binary-tree-maximum-path-sum.md) "carry state down" idiom (there: the path sum; here: the path max).

**Why `Int.MIN_VALUE` at the root?** The root is always good — no ancestor exists, and `root.val >= MIN_VALUE` always holds. The sentinel makes the base case uniform.

## Approach 1 — Collect paths, check each (O(n·h))

Enumerate every root-to-node path and scan: correct, wasteful.

## Approach 2 — Running-max DFS (the repo's version, optimal)

```kotlin
class CountGoodNodeInBInaryTree {
    /**
     * @param root tree root
     * @return     number of good nodes
     */
    fun goodNodes(root: TreeNode?): Int {
        return dfs(root, Int.MIN_VALUE)
    }

    private fun dfs(node: TreeNode?, maxSoFar: Int): Int {
        node ?: return 0

        val newMax = maxOf(maxSoFar, node.`val`)
        val good = if (node.`val` >= maxSoFar) 1 else 0

        return good + dfs(node.left, newMax) + dfs(node.right, newMax)
    }
}
```

```java
public class CountGoodNodesInBinaryTree {
    /**
     * @param root tree root
     * @return     number of good nodes
     */
    public int goodNodes(TreeNode root) {
        return dfs(root, Integer.MIN_VALUE);
    }

    private int dfs(TreeNode node, int maxSoFar) {
        if (node == null) return 0;

        int newMax = Math.max(maxSoFar, node.val);
        int good = node.val >= maxSoFar ? 1 : 0;

        return good + dfs(node.left, newMax) + dfs(node.right, newMax);
    }
}
```

```cpp
#include <algorithm>
#include <climits>

class CountGoodNodesInBinaryTree {
    int dfs(TreeNode* node, int maxSoFar) {
        if (!node) return 0;

        int newMax = std::max(maxSoFar, node->val);
        int good = node->val >= maxSoFar ? 1 : 0;

        return good + dfs(node->left, newMax) + dfs(node->right, newMax);
    }

public:
    /**
     * @param root tree root
     * @return     number of good nodes
     */
    int goodNodes(TreeNode* root) {
        return dfs(root, INT_MIN);
    }
};
```

```python
def good_nodes(root: Optional["TreeNode"]) -> int:
    """
    @param root: tree root
    @return:     number of good nodes
    """
    def dfs(node, max_so_far):
        if not node:
            return 0
        new_max = max(max_so_far, node.val)
        good = 1 if node.val >= max_so_far else 0
        return good + dfs(node.left, new_max) + dfs(node.right, new_max)

    return dfs(root, float("-inf"))
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root tree root
    /// @return     number of good nodes
    pub fn good_nodes(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        fn dfs(node: Option<Rc<RefCell<TreeNode>>>, max_so_far: i32) -> i32 {
            match node {
                None => 0,
                Some(n) => {
                    let val = n.borrow().val;
                    let new_max = max_so_far.max(val);
                    let good = if val >= max_so_far { 1 } else { 0 };
                    good + dfs(n.borrow().left.clone(), new_max)
                        + dfs(n.borrow().right.clone(), new_max)
                }
            }
        }

        dfs(root, i32::MIN)
    }
}
```

## Reading the code — what's actually happening

```kotlin
private fun dfs(node: TreeNode?, maxSoFar: Int): Int {
    node ?: return 0
    val newMax = maxOf(maxSoFar, node.`val`)
    val good = if (node.`val` >= maxSoFar) 1 else 0
    return good + dfs(node.left, newMax) + dfs(node.right, newMax)
}
```

Think of `maxSoFar` as **the path's running record** — the largest value encountered between the root and where we are right now. Every node's "goodness" is decided by one comparison against that record.

- **`node ?: return 0` handles the void.** A null child contributes no nodes and no goodness — return 0 and let the parent add it up.
- **`newMax = maxOf(maxSoFar, node.val)` updates the record for the children.** If this node sets a new high, the record must reflect it for everything below — a deeper node is compared against *this* maximum, not the stale one. This is why the two recursive calls pass `newMax`, not the original `maxSoFar`.
- **`good = if (node.val >= maxSoFar) 1 else 0` judges this node against the OLD record.** Note the timing: we compare before updating. A node is good iff it's at least as large as every value that came before it on the path — the record *from the ancestors*, not including itself. (Including itself would make every node trivially good, since `val >= val`.) The `>=` means ties count as good.
- **`good + dfs(left) + dfs(right)` composes the answer.** This node's verdict plus whatever the two subtrees report. Every node is visited exactly once and contributes exactly 1 or 0 — the sum is the total count of good nodes.

Trace `[3,1,4,3,null,1,5]`: root `3` (good, record 3) → left `1` (1 < 3, bad, record stays 3) → its child `3` (3 ≥ 3, good) → right `4` (good, record 4) → `1` (bad) → `5` (5 ≥ 4, good). Total: 3 + 4 + 3 + 5 = 4 good nodes ✓.

## Dry run

**Input:** `root = [3,1,4,3,null,1,5]`.

```
dfs(3, MIN): val 3 >= MIN -> good.  newMax=3.  1 + left + right
  dfs(1, 3): 1 >= 3? no.  newMax=3.  0 + dfs(3, 3)
    dfs(3, 3): 3 >= 3 -> good.  -> 1
  dfs(4, 3): 4 >= 3 -> good.  newMax=4.  1 + dfs(1, 4)
    dfs(1, 4): 1 >= 4? no.  newMax=4.  0 + dfs(5, 4)
      dfs(5, 4): 5 >= 4 -> good.  -> 1

Total: 1 + (0 + 1) + (1 + (0 + 1)) = 1 + 1 + 2 = 4 ✓
```

The running max is the path's memory: node 3 (the left grandchild) is good because its path `3 → 1 → 3` has max 3 — equal counts as good. Node 1 under 4 is bad because the path max is 4. The `>=` comparison and `newMax` propagation are the entire logic — no path storage, O(1) per node.

## Complexity

**Time.** Each node visited once:

$$
T(n) = O(n)
$$

**Space.** Recursion depth:

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **Binary Tree Maximum Path Sum** ([5.4](binary-tree-maximum-path-sum.md)) — the running-value-down DFS family (there: sums).
- **Path Sum III** ([5.9](path-sum-iii.md)) — the prefix-sum twin with a map.
- **Interview follow-up:** "Why is `maxSoFar` a complete summary of the path?" "Good" is a *threshold* property: a node is good iff it beats the path maximum, and the maximum is one number. Any other path summary (sum, length) would be the wrong state — naming the invariant is the answer.
