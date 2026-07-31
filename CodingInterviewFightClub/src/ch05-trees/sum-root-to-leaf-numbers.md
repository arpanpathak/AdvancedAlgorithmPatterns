# 5.18 Sum Root To Leaf Numbers

> **Source:** [`src/main/kotlin/tree/SumRootToLeafNumbers.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/SumRootToLeafNumbers.kt)
> **Pattern:** carry-the-number DFS · **Core page**

## The Problem

Sum all root-to-leaf numbers (each path is a base-10 number).

- Constraints: n ≤ 1000; digits 0-9.

## Examples

```
Input:  root = [1,2,3]      -> Output: 25   (12 + 13)
Input:  root = [4,9,0,5,1]  -> Output: 1026 (495 + 491 + 40)
```

## Intuition — carry the running number down; add at leaves

A node's number = `parentNumber * 10 + node.val` — the [5.4](binary-tree-maximum-path-sum.md) carry-down pattern with the number as state:

```kotlin
fun dfs(node: TreeNode?, sumSoFar: Int) {
    if (node == null) return
    val newSum = sumSoFar * 10 + node.`val`

    when {
        node.left == null && node.right == null -> sum += newSum   // a leaf: bank it
        else -> { dfs(node.left, newSum); dfs(node.right, newSum) }
    }
}
```

**Why the leaf check?** Only complete paths count — the sum accumulates exactly at leaves; internal nodes just carry the partial number.

## Approach 1 — Collect all paths then sum (O(n·h) strings)

String-concatenate each path, parse, sum: correct, slower.

## Approach 2 — Carry-down DFS (the repo's version, optimal)

```kotlin
class SumRootToLeafNumbers {
    /**
     * @param root tree root
     * @return     sum of all root-to-leaf numbers
     */
    fun sumNumbers(root: TreeNode?): Int {
        var sum = 0

        fun dfs(node: TreeNode?, sumSoFar: Int) {
            if (node == null) return
            val newSum = sumSoFar * 10 + node.`val`

            when {
                node.left == null && node.right == null -> sum += newSum
                else -> {
                    dfs(node.left, newSum)
                    dfs(node.right, newSum)
                }
            }
        }

        dfs(root, 0)
        return sum
    }
}
```

```java
public class SumRootToLeafNumbers {
    private int sum = 0;

    private void dfs(TreeNode node, int soFar) {
        if (node == null) return;
        int next = soFar * 10 + node.val;

        if (node.left == null && node.right == null) sum += next;
        else {
            dfs(node.left, next);
            dfs(node.right, next);
        }
    }

    /**
     * @param root tree root
     * @return     sum of all root-to-leaf numbers
     */
    public int sumNumbers(TreeNode root) {
        sum = 0;
        dfs(root, 0);
        return sum;
    }
}
```

```cpp
class SumRootToLeafNumbers {
    int sum = 0;

    void dfs(TreeNode* node, int soFar) {
        if (!node) return;
        int next = soFar * 10 + node->val;

        if (!node->left && !node->right) sum += next;
        else {
            dfs(node->left, next);
            dfs(node->right, next);
        }
    }

public:
    /**
     * @param root tree root
     * @return     sum of all root-to-leaf numbers
     */
    int sumNumbers(TreeNode* root) {
        sum = 0;
        dfs(root, 0);
        return sum;
    }
};
```

```python
def sum_numbers(root: Optional["TreeNode"]) -> int:
    """
    @param root: tree root
    @return:     sum of all root-to-leaf numbers
    """
    total = 0

    def dfs(node, so_far):
        nonlocal total
        if not node:
            return
        nxt = so_far * 10 + node.val

        if not node.left and not node.right:
            total += nxt
        else:
            dfs(node.left, nxt)
            dfs(node.right, nxt)

    dfs(root, 0)
    return total
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root tree root
    /// @return     sum of all root-to-leaf numbers
    pub fn sum_numbers(root: Option<Rc<RefCell<TreeNode>>>) -> i32 {
        fn dfs(node: Option<Rc<RefCell<TreeNode>>>, so_far: i32) -> i32 {
            match node {
                None => 0,
                Some(n) => {
                    let next = so_far * 10 + n.borrow().val;
                    if n.borrow().left.is_none() && n.borrow().right.is_none() {
                        next
                    } else {
                        dfs(n.borrow().left.clone(), next) + dfs(n.borrow().right.clone(), next)
                    }
                }
            }
        }
        dfs(root, 0)
    }
}
```

## Dry run

**Input:** `root = [4,9,0,5,1]`.

```
dfs(4, 0): next = 4.  internal -> dfs(9, 4), dfs(0, 4)
dfs(9, 4): next = 49.  internal -> dfs(5, 49), dfs(1, 49)
  dfs(5, 49): next = 495.  leaf -> sum += 495
  dfs(1, 49): next = 491.  leaf -> sum += 491
dfs(0, 4): next = 40.  leaf -> sum += 40

Output: 495 + 491 + 40 = 1026 ✓
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

- **Path Sum** ([5.9](path-sum-iii.md)) — the same carry-down with sums instead of numbers.
- **Binary Tree Maximum Path Sum** ([5.4](binary-tree-maximum-path-sum.md)) — the global-best family.
- **Interview follow-up:** "Why `*10 + val` instead of strings?" The running number is the path encoded in decimal — each step appends a digit arithmetically. Leaves bank the complete number; the `when` distinguishes carries from completions.
