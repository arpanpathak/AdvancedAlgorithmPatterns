# 5.35 Path Sum

> **Source**: [`src/main/kotlin/tree/PathSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/PathSum.kt)
> **Pattern**: root-to-leaf target subtraction · **Core page**

## The Problem

Does a **root-to-leaf** path sum to `targetSum`?

- Constraints: n ≤ 5000.

## Examples

```
Input:  root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum = 22 -> Output: true
```

## Intuition — subtract the value as you descend; the leaf test

```kotlin
fun hasPathSum(root: TreeNode?, targetSum: Int): Boolean {
    return when {
        root == null -> false
        root.left == null && root.right == null && targetSum == root.`val` -> true
        else -> hasPathSum(root.left, targetSum - root.`val`) ||
                hasPathSum(root.right, targetSum - root.`val`)
    }
}
```

## Approach 1 — Target subtraction (the repo's version, optimal)

```kotlin
class PathSum {
    /**
     * @param root      tree root
     * @param targetSum target sum
     * @return          true iff a root-to-leaf path sums to it
     */
    fun hasPathSum(root: TreeNode?, targetSum: Int): Boolean {
        return when {
            root == null -> false
            root.left == null && root.right == null && targetSum == root.`val` -> true
            else -> hasPathSum(root.left, targetSum - root.`val`) ||
                    hasPathSum(root.right, targetSum - root.`val`)
        }
    }
}
```

```java
public class PathSum {
    /**
     * @param root      tree root
     * @param targetSum target sum
     * @return          true iff a root-to-leaf path sums to it
     */
    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) return false;
        if (root.left == null && root.right == null) return targetSum == root.val;

        return hasPathSum(root.left, targetSum - root.val)
            || hasPathSum(root.right, targetSum - root.val);
    }
}
```

```cpp
class PathSum {
public:
    /**
     * @param root      tree root
     * @param targetSum target sum
     * @return          true iff a root-to-leaf path sums to it
     */
    bool hasPathSum(TreeNode* root, int targetSum) {
        if (!root) return false;
        if (!root->left && !root->right) return targetSum == root->val;

        return hasPathSum(root->left, targetSum - root->val)
            || hasPathSum(root->right, targetSum - root->val);
    }
};
```

```python
def has_path_sum(root: Optional["TreeNode"], target_sum: int) -> bool:
    """
    @param root:      tree root
    @param target_sum: target sum
    @return:          true iff a root-to-leaf path sums to it
    """
    if not root:
        return False
    if not root.left and not root.right:
        return target_sum == root.val

    return has_path_sum(root.left, target_sum - root.val) or \
           has_path_sum(root.right, target_sum - root.val)
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root       tree root
    /// @param target_sum target sum
    /// @return           true iff a root-to-leaf path sums to it
    pub fn has_path_sum(root: Option<Rc<RefCell<TreeNode>>>, target_sum: i32) -> bool {
        match root {
            None => false,
            Some(n) => {
                let left = n.borrow().left.clone();
                let right = n.borrow().right.clone();
                let val = n.borrow().val;

                if left.is_none() && right.is_none() { return target_sum == val; }

                Self::has_path_sum(left, target_sum - val)
                    || Self::has_path_sum(right, target_sum - val)
            }
        }
    }
}
```

## Reading the code — what's actually happening

```kotlin
fun hasPathSum(root: TreeNode?, targetSum: Int): Boolean {
    return when {
        root == null -> false
        root.left == null && root.right == null && targetSum == root.`val` -> true
        else -> hasPathSum(root.left, targetSum - root.`val`) ||
                hasPathSum(root.right, targetSum - root.`val`)
    }
}
```

The neat trick here is **working backward**: instead of carrying a running sum down and comparing to the target at the leaf, we *subtract* values as we descend and ask "did the target shrink to exactly zero at this node?" — well, to exactly the node's value.

- **`root == null -> false` is the dead-end case.** An empty subtree can't contain a root-to-leaf path. This also handles the "tree is empty" input. (Note: it does *not* mean "empty path with sum 0 counts" — leaves, not nulls, are the goal.)
- **The leaf test is the heart: `root.left == null && root.right == null && targetSum == root.val`.** Only when both children are missing do we have a complete root-to-leaf path. And the check is a *single equality* — thanks to the subtract-as-you-go design, `targetSum` at this point already means "the remaining sum this leaf must supply". If it equals the leaf's value, the path totals the original target.
- **The else branch descends with the budget reduced.** `targetSum - root.val` is "what the rest of the path still needs to add". The `||` means "either the left subtree contains such a continuation, or the right one does" — a path exists if *any* branch finds one, and the recursion explores both.
- **Why subtract and not accumulate?** If we carried `soFar` down, the leaf test would need `soFar + root.val == targetSum` — two pieces of state threaded through every call. Subtracting folds that state into the single `targetSum` parameter, which is why the leaf case is one comparison.

Trace `targetSum = 22` on the example: `5` → remaining 17 → `4` → remaining 13 → `11` → remaining 2 → `7`: leaf, `2 != 7` no; backtrack → `2`: leaf, `2 == 2` ✓ → `true`. The recursion found the path `5 → 4 → 11 → 2` by subtracting exactly along it.

## Dry run

**Input:** the example; `targetSum = 22`.

```
5 -> 4 -> 11 -> 7: 5+4+11+7 = 27 no.  5->4->11->2 = 22 ✓ -> true
```

## Complexity

**Time.** Each node once (worst):

$$
T(n) = O(n)
$$

**Space.** Recursion:

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **Path Sum II** — collect the paths.
- **Path Sum III** ([5.9](path-sum-iii.md)) — any start/end, prefix-sum map.
- **Interview follow-up:** "Why subtract instead of accumulate?" Carrying `target - soFar` makes the leaf test a single equality — no separate sum state.
