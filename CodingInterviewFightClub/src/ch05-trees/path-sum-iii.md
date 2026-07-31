# 5.9 Path Sum III

> **Source:** [`src/main/kotlin/tree/PathSumIII.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/PathSumIII.kt)
> **Pattern:** prefix sums on a tree · **Core page**

## The Problem

Given a binary tree and a `targetSum`, count the number of **downward paths** (parent-to-child, any start, any end) whose values sum to the target.

- Constraints: $0 \le n \le 1000$; values and target fit in `Int` (use `Long` for running sums).

## Examples

```
Input:  root = [10,5,-3,3,2,null,11,3,-2,null,1], targetSum = 8
Output: 3   (5->3, 5->2->1, -3->11)
```

## Intuition — "sum ending here" = "prefix sum ending here" - "prefix sum before the start"

For any path, the sum of `path(start..node)` equals `prefix(node) - prefix(parent-of-start)`. So counting paths *ending at the current node* with sum `target` is counting earlier prefix sums equal to `prefix(current) - target` — the exact [10.8](../ch10-hash-tables/subarray-sum-equals-k.md) idea ([10.8] is added in this same scan), transplanted onto a tree with DFS carry/restore:

```
dfs(node, currentSum):
    newSum = currentSum + node.val
    count += prefixCount[newSum - target]     # paths ending here with sum == target
    prefixCount[newSum]++                     # this prefix is now available below
    count += dfs(left) + dfs(right)
    prefixCount[newSum]--                     # undo: siblings must not see this path
    return count
```

**Why does the DFS carry/restore matter?** A tree path must be *downward along one root-to-node branch* — not across siblings. The map is updated before descending and **rolled back after** (`prefixCount[newSum]--`), so each branch sees only its own ancestors' prefixes. This is the [12.0](../ch12-backtracking/pattern-primer.md) undo contract applied to a map instead of a list.

**The `prefixCount[0] = 1` seed:** a path starting at the *root* has no preceding prefix — its "prefix before the start" is 0, so the seed makes `prefix(current) == target` count as one valid path.

**Why `Long`?** Values can be negative and large; the running sum may overflow `Int` on deep paths — the repo casts (`currentSum: Long`).

## Approach 1 — Double DFS from every node (O(n^2))

For each node, count downward paths with sum `target` starting there (the repo's `pathSum`): correct, and quadratic on skewed trees.

## Approach 2 — DFS with prefix-sum map (the repo's `pathSum_prefix_sum`, optimal)

```kotlin
class PathSumIII {
    /**
     * @param root      tree root
     * @param targetSum target path sum
     * @return          number of downward paths summing to targetSum
     */
    fun pathSum(root: TreeNode?, targetSum: Int): Int {
        // Prefix sums seen on the current root-to-node branch, and their counts
        val prefixSumCount = HashMap<Long, Int>()
        prefixSumCount[0L] = 1                        // the empty prefix: paths starting at the root

        fun dfs(node: TreeNode?, currentSum: Long): Int {
            if (node == null) return 0

            val newSum = currentSum + node.`val`
            // Paths ending at this node with sum == target
            var pathCount = prefixSumCount.getOrDefault(newSum - targetSum.toLong(), 0)

            // This prefix becomes available to descendants
            prefixSumCount[newSum] = prefixSumCount.getOrDefault(newSum, 0) + 1

            pathCount += dfs(node.left, newSum) + dfs(node.right, newSum)

            // Undo: siblings must not see this branch's prefixes
            prefixSumCount[newSum] = prefixSumCount[newSum]!! - 1
            return pathCount
        }
        return dfs(root, 0L)
    }
}
```

```java
import java.util.*;

public class PathSumIII {
    /**
     * @param root      tree root
     * @param targetSum target path sum
     * @return          number of downward paths summing to targetSum
     */
    public int pathSum(TreeNode root, int targetSum) {
        Map<Long, Integer> prefix = new HashMap<>();
        prefix.put(0L, 1);                             // the empty prefix

        return dfs(root, 0L, targetSum, prefix);
    }

    private int dfs(TreeNode node, long sum, int target, Map<Long, Integer> prefix) {
        if (node == null) return 0;

        sum += node.val;
        int count = prefix.getOrDefault(sum - target, 0);   // paths ending here with sum == target

        prefix.merge(sum, 1, Integer::sum);                // available to descendants
        count += dfs(node.left, sum, target, prefix) + dfs(node.right, sum, target, prefix);
        prefix.merge(sum, -1, Integer::sum);               // undo for siblings
        return count;
    }
}
```

```cpp
#include <unordered_map>

class PathSumIII {
    int dfs(TreeNode* node, long sum, int target, std::unordered_map<long, int>& prefix) {
        if (!node) return 0;

        sum += node->val;
        int count = prefix[sum - target];                  // paths ending here with sum == target

        prefix[sum]++;
        count += dfs(node->left, sum, target, prefix) + dfs(node->right, sum, target, prefix);
        prefix[sum]--;                                    // undo for siblings
        return count;
    }

public:
    /**
     * @param root      tree root
     * @param targetSum target path sum
     * @return          number of downward paths summing to targetSum
     */
    int pathSum(TreeNode* root, int targetSum) {
        std::unordered_map<long, int> prefix{{0L, 1}};    // the empty prefix
        return dfs(root, 0L, targetSum, prefix);
    }
};
```

```python
def path_sum(root: Optional["TreeNode"], target_sum: int) -> int:
    """
    @param root:       tree root
    @param target_sum: target path sum
    @return:           number of downward paths summing to target_sum
    """
    prefix = {0: 1}                        # the empty prefix

    def dfs(node, cur: int) -> int:
        if node is None:
            return 0
        cur += node.val
        count = prefix.get(cur - target_sum, 0)   # paths ending here with sum == target
        prefix[cur] = prefix.get(cur, 0) + 1      # available to descendants
        count += dfs(node.left, cur) + dfs(node.right, cur)
        prefix[cur] -= 1                          # undo for siblings
        return count

    return dfs(root, 0)
```

```rust
use std::cell::RefCell;
use std::collections::HashMap;
use std::rc::Rc;

impl Solution {
    /// @param root       tree root
    /// @param target_sum target path sum
    /// @return           number of downward paths summing to target_sum
    pub fn path_sum(root: Option<Rc<RefCell<TreeNode>>>, target_sum: i32) -> i32 {
        let mut prefix: HashMap<i64, i32> = HashMap::new();
        prefix.insert(0, 1);                       // the empty prefix

        fn dfs(node: Option<Rc<RefCell<TreeNode>>>, cur: i64, target: i64,
               prefix: &mut HashMap<i64, i32>) -> i32 {
            let Some(n) = node else { return 0; };
            let n = n.borrow();
            let cur = cur + n.val as i64;
            let count = *prefix.get(&(cur - target)).unwrap_or(&0);  // paths ending here
            *prefix.entry(cur).or_insert(0) += 1;                    // available to descendants
            let count = count + dfs(n.left.clone(), cur, target, prefix)
                              + dfs(n.right.clone(), cur, target, prefix);
            *prefix.get_mut(&cur).unwrap() -= 1;                     // undo for siblings
            count
        }

        dfs(root, 0, target_sum as i64, &mut prefix)
    }
}
```

## Dry run

**Input:** `root = [10,5,-3,3,2,null,11,3,-2,null,1]`, `targetSum = 8`. DFS preorder with carry/restore.

```
prefix = {0:1}, cur = 0
10: cur=10.  count += prefix[10-8=2]? 0.  prefix={0:1,10:1}
  5: cur=15.  count += prefix[7]? 0.  prefix={0:1,10:1,15:1}
    3: cur=18.  count += prefix[10]? 1 -> count=1.  prefix[18]=1.  (path 5->3 ✓)
      (3's children 3,-2 visited under cur=18...)
    3: cur=21.  count += prefix[13]? 0.  prefix[21]=1
     -2: cur=19.  count += prefix[11]? 0.  prefix[19]=1
    undo 21,19,18,15...
    2: cur=17.  count += prefix[9]? 0.  prefix[17]=1
      1: cur=18.  count += prefix[10]? 1 -> count=2.  (path 5->2->1 ✓)
    undo 17,18...
  undo 10...
  -3: cur=7.  count += prefix[-1]? 0.  prefix[7]=1
    11: cur=18.  count += prefix[10]? 1 -> count=3.  (path -3->11 ✓)
  undo 7,18...

Output: 3 ✓
```

The map is the entire branch's prefix history at any moment: when `1` (under `5->2`) is visited, `prefix[10]` is still present because `5`'s prefix hasn't been undone yet — so the path `5->2->1` (sum 8) counts exactly once. And the restore (`prefix[newSum]--`) keeps `-3`'s branch from seeing `5`'s prefixes — no cross-branch paths leak in.

## Complexity

**Time.** Each node visited once, O(1) map ops:

$$
T(n) = O(n)
$$

**Space.** The prefix map (branch depth entries) + recursion:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Path Sum / Path Sum II** (`tree/PathSum.kt`, `tree/PathSum_II.kt`) — the fixed-from-root versions: boolean / enumerate, no prefix map needed.
- **Subarray Sum Equals K** ([10.8](../ch10-hash-tables/subarray-sum-equals-k.md)) — the 1-D array version of this exact prefix-counting; this page is the tree transplant.
- **Longest Path With Different Adjacent Characters** (`tree/LongestPathWithDifferentAdjacentCharacters.kt`) — the same DFS-carry-restore machinery on a general tree.
- **Interview follow-up:** "Why the carry/restore instead of a global map?" A path must lie on a single root-to-node branch; a global map would count *cross-branch* combos that aren't downward paths. The increment-before-descend / decrement-after-return keeps the map equal to exactly the current branch's prefixes — the undo is the correctness, not a nicety.
