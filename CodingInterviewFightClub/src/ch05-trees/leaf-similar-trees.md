# 5.22 Leaf-Similar Trees

> **Source:** [`src/main/kotlin/tree/LeafSimilar.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/LeafSimilar.kt)
> **Pattern:** leaf-sequence equality · **Core page**

## The Problem

Are two trees' leaf-value sequences equal (left-to-right)?

- Constraints: n ≤ 200.

## Examples

```
Input:  root1 = [3,5,1,6,2,9,8,null,null,7,4], root2 = [3,5,1,6,7,4,2,null,null,null,null,null,null,9,8]
Output: true   (both leaf sequences are [6,7,4,9,8])
```

## Intuition — collect the leaf sequences, compare

DFS each tree appending values at leaves; the sequences are in left-to-right order by construction:

```kotlin
fun dfs(node: TreeNode?, leafValues: MutableList<Int>) {
    if (node != null) {
        if (node.left == null && node.right == null) leafValues.add(node.`val`)
        dfs(node.left, leafValues)
        dfs(node.right, leafValues)
    }
}
return leaves1 == leaves2
```

**Why is the order guaranteed?** Preorder visits left subtrees before right — leaves append left-to-right. The list equality is the whole test.

## Approach 1 — Streaming comparison (O(h) space, yield leaves)

Generator-based compare: avoids full lists, same idea.

## Approach 2 — Collect and compare (the repo's version, optimal)

```kotlin
class LeafSimilar {
    /**
     * @param root1 first tree
     * @param root2 second tree
     * @return      true iff leaf sequences match
     */
    fun leafSimilar(root1: TreeNode?, root2: TreeNode?): Boolean {
        val leaves1 = mutableListOf<Int>()
        val leaves2 = mutableListOf<Int>()

        dfs(root1, leaves1)
        dfs(root2, leaves2)

        return leaves1 == leaves2
    }

    fun dfs(node: TreeNode?, leafValues: MutableList<Int>) {
        if (node != null) {
            if (node.left == null && node.right == null) leafValues.add(node.`val`)
            dfs(node.left, leafValues)
            dfs(node.right, leafValues)
        }
    }
}
```

```java
import java.util.*;

public class LeafSimilarTrees {
    private void dfs(TreeNode node, List<Integer> leaves) {
        if (node == null) return;
        if (node.left == null && node.right == null) leaves.add(node.val);
        dfs(node.left, leaves);
        dfs(node.right, leaves);
    }

    /**
     * @param root1 first tree
     * @param root2 second tree
     * @return      true iff leaf sequences match
     */
    public boolean leafSimilar(TreeNode root1, TreeNode root2) {
        List<Integer> a = new ArrayList<>(), b = new ArrayList<>();
        dfs(root1, a);
        dfs(root2, b);
        return a.equals(b);
    }
}
```

```cpp
#include <vector>

class LeafSimilarTrees {
    void dfs(TreeNode* node, std::vector<int>& leaves) {
        if (!node) return;
        if (!node->left && !node->right) leaves.push_back(node->val);
        dfs(node->left, leaves);
        dfs(node->right, leaves);
    }

public:
    /**
     * @param root1 first tree
     * @param root2 second tree
     * @return      true iff leaf sequences match
     */
    bool leafSimilar(TreeNode* root1, TreeNode* root2) {
        std::vector<int> a, b;
        dfs(root1, a);
        dfs(root2, b);
        return a == b;
    }
};
```

```python
def leaf_similar(root1: Optional["TreeNode"], root2: Optional["TreeNode"]) -> bool:
    """
    @param root1: first tree
    @param root2: second tree
    @return:      true iff leaf sequences match
    """
    def leaves(node):
        if not node:
            return []
        if not node.left and not node.right:
            return [node.val]
        return leaves(node.left) + leaves(node.right)

    return leaves(root1) == leaves(root2)
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root1 first tree
    /// @param root2 second tree
    /// @return      true iff leaf sequences match
    pub fn leaf_similar(root1: Option<Rc<RefCell<TreeNode>>>, root2: Option<Rc<RefCell<TreeNode>>>) -> bool {
        fn collect(node: Option<Rc<RefCell<TreeNode>>>, out: &mut Vec<i32>) {
            if let Some(n) = node {
                let left = n.borrow().left.clone();
                let right = n.borrow().right.clone();
                if left.is_none() && right.is_none() { out.push(n.borrow().val); }
                collect(left, out);
                collect(right, out);
            }
        }

        let (mut a, mut b) = (Vec::new(), Vec::new());
        collect(root1, &mut a);
        collect(root2, &mut b);
        a == b
    }
}
```

## Dry run

**Input:** `root1 = [3,5,1,6,2,9,8,null,null,7,4]`, `root2` as above.

```
root1 leaves (preorder): 6 (from 5's left), 7, 4 (from 2), 9, 8 (from 1) -> [6,7,4,9,8]
root2 leaves: 6, 7, 4, 9, 8 -> [6,7,4,9,8]

[6,7,4,9,8] == [6,7,4,9,8] -> true ✓
```

## Complexity

**Time.** Both trees once:

$$
T(n) = O(n_1 + n_2)
$$

**Space.** Two leaf lists:

$$
S = O(n_1 + n_2)
$$

## Variants & follow-ups

- **Find Largest Value Per Row** ([5.17](find-largest-value-in-each-tree-row.md)) — the collect-per-level sibling.
- **Interview follow-up:** "Why preorder (not level-order) for leaves?" Leaves in preorder appear left-to-right — exactly the sequence the problem defines. Level-order would group by depth and break the order.
