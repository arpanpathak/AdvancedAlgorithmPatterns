# 5.32 Count Nodes Equal To Average Of Subtree

> **Source**: [`src/main/kotlin/tree/CountNodeEqualsAverage.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/CountNodeEqualsAverage.kt)
> **Pattern**: subtree (sum, count) pair · **Core page**

## The Problem

Count nodes whose value equals the **average** (integer division) of their subtree.

- Constraints: n ≤ 1000.

## Examples

```
Input:  root = [4,8,5,0,1,null,6]   -> Output: 5
```

## Intuition — post-order returns (sum, count); compare val to sum/count

```kotlin
data class Result(val sum: Int, val count: Int)

var count = 0

fun dfs(node: TreeNode?): Result {
    if (node == null) return Result(0, 0)

    val left = dfs(node.left)
    val right = dfs(node.right)

    val sum = node.`val` + left.sum + right.sum
    val size = 1 + left.count + right.count

    if (node.`val` == sum / size) count++
    return Result(sum, size)
}
```

## Approach 1 — Post-order pair (the repo's version, optimal)

```kotlin
class CountNodeEqualsAverage {
    data class Result(val sum: Int, val count: Int)

    var count = 0

    /**
     * @param root tree root
     * @return     count of nodes equal to their subtree average
     */
    fun averageOfSubtree(root: TreeNode?): Int {
        count = 0
        dfs(root)
        return count
    }

    private fun dfs(node: TreeNode?): Result {
        if (node == null) return Result(0, 0)

        val left = dfs(node.left)
        val right = dfs(node.right)

        val sum = node.`val` + left.sum + right.sum
        val size = 1 + left.count + right.count

        if (node.`val` == sum / size) count++
        return Result(sum, size)
    }
}
```

```java
public class CountNodesEqualToAverage {
    private int count = 0;

    private int[] dfs(TreeNode node) {
        if (node == null) return new int[]{0, 0};

        int[] left = dfs(node.left);
        int[] right = dfs(node.right);

        int sum = node.val + left[0] + right[0];
        int size = 1 + left[1] + right[1];

        if (node.val == sum / size) count++;
        return new int[]{sum, size};
    }

    /**
     * @param root tree root
     * @return     count of nodes equal to their subtree average
     */
    public int averageOfSubtree(TreeNode root) {
        count = 0;
        dfs(root);
        return count;
    }
}
```

```cpp
class CountNodesEqualToAverage {
    int count = 0;

    std::pair<int, int> dfs(TreeNode* node) {
        if (!node) return {0, 0};

        auto [ls, lc] = dfs(node->left);
        auto [rs, rc] = dfs(node->right);

        int sum = node->val + ls + rs;
        int size = 1 + lc + rc;

        if (node->val == sum / size) count++;
        return {sum, size};
    }

public:
    /**
     * @param root tree root
     * @return     count of nodes equal to their subtree average
     */
    int averageOfSubtree(TreeNode* root) {
        count = 0;
        dfs(root);
        return count;
    }
};
```

```python
def average_of_subtree(root: Optional["TreeNode"]) -> int:
    """
    @param root: tree root
    @return:     count of nodes equal to their subtree average
    """
    count = 0

    def dfs(node):
        nonlocal count
        if not node:
            return (0, 0)

        left_sum, left_count = dfs(node.left)
        right_sum, right_count = dfs(node.right)

        total_sum = node.val + left_sum + right_sum
        size = 1 + left_count + right_count

        if node.val == total_sum // size:
            count += 1

        return (total_sum, size)

    dfs(root)
    return count
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root tree root
    /// @return     count of nodes equal to their subtree average
    pub fn average_of_subtree(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        fn dfs(node: Option<Rc<RefCell<TreeNode>>>, count: &mut i32) -> (i32, i32) {
            match node {
                None => (0, 0),
                Some(n) => {
                    let (ls, lc) = dfs(n.borrow().left.clone(), count);
                    let (rs, rc) = dfs(n.borrow().right.clone(), count);

                    let sum = n.borrow().val + ls + rs;
                    let size = 1 + lc + rc;

                    if n.borrow().val == sum / size { *count += 1; }
                    (sum, size)
                }
            }
        }

        let mut count = 0;
        dfs(root, &mut count);
        count
    }
}
```

## Dry run

**Input:** the example tree.

```
0: (0,1) avg 0 == 0 ✓.  1: (1,1) ✓.  6: (6,1) ✓.
8: sum 0+1+8=9, size 3, avg 3 != 8.
5: sum 9+5=14? 8's subtree (9,3) + 5 + ... = (14,4), avg 3 != 5.
4: sum 4+14+6=24, size 7, avg 3 != 4.
count = 3 (0, 1, 6)?  The expected answer is 5 — the tree [4,8,5,0,1,null,6]:
  0 ✓, 1 ✓, 6 ✓, 5: subtree (5+0+1)/3 = 2 != 5.  8: (8+0+1)/3 = 3 != 8.  4: (4+8+5+0+1+6)/6 = 4 ✓!
  count = 4?  Hmm the known answer for this tree is 5: nodes 0, 1, 6, 4, and 5?
  (5+0+1)/3 = 2.  Not 5.  The official answer: 5 nodes (0, 1, 6, 4, and... let me recount:
  Actually LeetCode's example [4,8,5,0,1,null,6] -> 5.  Nodes whose value == floor(avg):
  0: avg(0)=0 ✓.  1: avg(1)=1 ✓.  6: avg(6)=6 ✓.  5: subtree {5,0,1} sum 6 / 3 = 2 ✗.
  8: subtree {8,0,1} sum 9 / 3 = 3 ✗.  4: whole tree sum 24 / 6 = 4 ✓.  That's 4.
  Hmm — LeetCode 2265 example: [4,8,5,0,1,null,6] → 5.  Let me trust the problem: 5.
  Possibly 5 counts because... the correct tree is [4,8,5,0,1,null,6] where 8 has children 0,1 and
  5 has right 6.  Nodes: 0(✓), 1(✓), 6(✓), 8: (8+0+1)/3 = 3 ✗, 5: (5+6)/2 = 5 ✓!  (5's subtree
  is {5, 6} — the 6 hangs under 5, not under 8!).  Yes: 5's children: null and 6.  avg (5+6)/2 = 5 ✓.
  count = 5 ✓
```

## Complexity

**Time.** Each node once:

$$
T(n) = O(n)
$$

**Space.** Recursion:

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why a pair return?" The average needs both the sum and the count of the subtree — a single value can't carry both; the [5.4](binary-tree-maximum-path-sum.md) pair-return discipline.
