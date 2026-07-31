# 5.20 Range Sum Of BST

> **Source:** [`src/main/kotlin/graph/bst/RangeSumOfBST.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/bst/RangeSumOfBST.kt)
> **Pattern:** BST-pruned traversal · **Core page**

## The Problem

Sum of node values in `[low, high]` — the BST structure prunes the walk.

- Constraints: n ≤ 2×10⁴.

## Examples

```
Input:  root = [10,5,15,3,7,null,18], low = 7, high = 15   -> Output: 32 (10+7+15)
```

## Intuition — skip subtrees the range can't contain

Inorder visits sorted values; the BST property prunes harder — a node < low means its *left* subtree is all < low (skip it); a node > high means the *right* subtree is all > high:

```kotlin
fun dfs(node: TreeNode?) {
    if (node == null) return

    if (node.`val` > low) dfs(node.left)       // left may still hold values >= low
    if (node.`val` in low..high) sum += node.`val`
    if (node.`val` < high) dfs(node.right)     // right may still hold values <= high
}
```

**Why the conditional recursions?** A node ≤ low: its left subtree is entirely ≤ low — skipping it is *provably* safe (BST ordering). The two guards cut the visited set to the range's neighborhood.

## Approach 1 — Full traversal

Visit every node, add in-range: O(n) — correct, ignores the BST.

## Approach 2 — Pruned traversal (the repo's version, optimal)

```kotlin
class RangeSumOfBST {
    /**
     * @param root BST root
     * @param low  range lower bound
     * @param high range upper bound
     * @return     sum of values in [low, high]
     */
    fun rangeSumBST(root: TreeNode?, low: Int, high: Int): Int {
        var sum = 0

        fun dfs(node: TreeNode?) {
            if (node == null) return

            if (node.`val` > low) dfs(node.left)
            if (node.`val` in low..high) sum += node.`val`
            if (node.`val` < high) dfs(node.right)
        }

        dfs(root)
        return sum
    }
}
```

```java
public class RangeSumOfBST {
    private int sum = 0;

    private void dfs(TreeNode node, int low, int high) {
        if (node == null) return;

        if (node.val > low) dfs(node.left, low, high);
        if (node.val >= low && node.val <= high) sum += node.val;
        if (node.val < high) dfs(node.right, low, high);
    }

    /**
     * @param root BST root
     * @param low  range lower bound
     * @param high range upper bound
     * @return     sum of values in [low, high]
     */
    public int rangeSumBST(TreeNode root, int low, int high) {
        sum = 0;
        dfs(root, low, high);
        return sum;
    }
}
```

```cpp
class RangeSumOfBST {
    int sum = 0;

    void dfs(TreeNode* node, int low, int high) {
        if (!node) return;

        if (node->val > low) dfs(node->left, low, high);
        if (node->val >= low && node->val <= high) sum += node->val;
        if (node->val < high) dfs(node->right, low, high);
    }

public:
    /**
     * @param root BST root
     * @param low  range lower bound
     * @param high range upper bound
     * @return     sum of values in [low, high]
     */
    int rangeSumBST(TreeNode* root, int low, int high) {
        sum = 0;
        dfs(root, low, high);
        return sum;
    }
};
```

```python
def range_sum_bst(root: Optional["TreeNode"], low: int, high: int) -> int:
    """
    @param root: BST root
    @param low:  range lower bound
    @param high: range upper bound
    @return:     sum of values in [low, high]
    """
    total = 0

    def dfs(node):
        nonlocal total
        if not node:
            return

        if node.val > low:
            dfs(node.left)
        if low <= node.val <= high:
            total += node.val
        if node.val < high:
            dfs(node.right)

    dfs(root)
    return total
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root BST root
    /// @param low  range lower bound
    /// @param high range upper bound
    /// @return     sum of values in [low, high]
    pub fn range_sum_bst(root: Option<Rc<RefCell<TreeNode>>>, low: i32, high: i32) -> i32 {
        fn dfs(node: Option<Rc<RefCell<TreeNode>>>, low: i32, high: i32) -> i32 {
            match node {
                None => 0,
                Some(n) => {
                    let val = n.borrow().val;
                    let mut sum = if val >= low && val <= high { val } else { 0 };
                    if val > low { sum += dfs(n.borrow().left.clone(), low, high); }
                    if val < high { sum += dfs(n.borrow().right.clone(), low, high); }
                    sum
                }
            }
        }
        dfs(root, low, high)
    }
}
```

## Dry run

**Input:** `root = [10,5,15,3,7,null,18]`, `low = 7, high = 15`.

```
dfs(10): val > 7 -> left.  in range -> +10.  val < 15 -> right.
  dfs(5): val > 7? no -> skip left (3 and under are all < 7).  not in range.  val < 15 -> right.
    dfs(7): val > 7? no (equal: left subtree all < 7... equal means left may have 7s? strictly
            a left value < 7, so skip is safe... actually val > low uses >, so 7 > 7 false -> skip
            left.  hmm the guard should be >= for correctness with equal values; with BST no dups
            typically, 7 > 7 false skips the left which contains values < 7 — correct).
            in range -> +7.
  dfs(15): in range -> +15.  val < 15? no -> skip right (18 > 15... wait 18 > high=15 so skip ✓).

Output: 10 + 7 + 15 = 32 ✓
```

## Complexity

**Time.** Range-neighborhood only:

$$
T(n) = O(h + k) \quad (k \text{ = in-range nodes})
$$

**Space.** Recursion:

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **Binary Search Tree Iterator** ([18.12](../ch18-design-caches/bst-iterator.md)) — the streaming inorder sibling.
- **Interview follow-up:** "Why are the guards safe?" A BST node's left subtree holds values *strictly less* than the node — if the node ≤ low, the whole left subtree is out of range, and the `if (val > low)` guard skips it. The symmetric right guard uses `< high`. The pruning is exact, not heuristic.
