# 5.8 Binary Tree Right Side View

> **Source:** [`src/main/kotlin/tree/BinaryTreeRightSideView.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/BinaryTreeRightSideView.kt)
> **Pattern:** DFS with first-per-level · **Core page**

## The Problem

Return the values you'd see looking at a binary tree from the **right side**: the rightmost node of each level.

- Constraints: $0 \le n \le 100$.

## Examples

```
Input:  root = [1,2,3,null,5,null,4]
Output: [1,3,4]   (level 0: 1, level 1: 3, level 2: 4)
```

## Intuition — the first node visited at each level *from the right*

The rightmost node of level k is "the first node visited at depth k when exploring right-before-left". The repo's DFS is delightfully minimal:

```
dfs(node, level):
    if node == null: return
    if level == rightSide.size: rightSide.add(node.val)   # first time we reach this depth
    dfs(node.right, level + 1)                            # right first!
    dfs(node.left, level + 1)
```

**Why does `level == rightSide.size` pick the rightmost?** `rightSide` grows one entry per level, in level order. The *first* time the DFS reaches depth k, the list has exactly k entries — so `level == size` fires only on the first visit to that depth. Visiting right-before-left makes that first visit the rightmost node. (The [5.2](binary-tree-level-order-traversal.md) BFS alternative — take the last node of each level — is the mirror.)

**Why DFS over BFS?** BFS needs a queue and level bookkeeping; the DFS version is ~6 lines and reuses the [5.1](maximum-depth-of-binary-tree.md) recursion shape. Both are O(n); the DFS-first-visit trick is the elegant one.

## Approach 1 — BFS, take the last of each level (also correct)

Level-order with level fencing ([5.2](binary-tree-level-order-traversal.md)); the last node per level is the rightmost. Straightforward, slightly more code.

## Approach 2 — DFS right-first, first-visit-per-level (the repo's version, optimal)

```kotlin
class BinaryTreeRightSideView {
    /**
     * @param root tree root
     * @return     the rightmost value of each level
     */
    fun rightSideView(root: TreeNode?): List<Int> {
        val rightSide = mutableListOf<Int>()

        fun dfs(node: TreeNode?, level: Int) {
            when {
                node == null -> return
                level == rightSide.size -> rightSide.add(node.`val`)   // first visit to this depth
            }
            dfs(node?.right, level + 1)    // right first: the first visitor is the rightmost
            dfs(node?.left, level + 1)
        }
        dfs(root, 0)
        return rightSide
    }
}
```

```java
import java.util.*;

public class BinaryTreeRightSideView {
    private final List<Integer> rightSide = new ArrayList<>();

    /**
     * @param root tree root
     * @return     the rightmost value of each level
     */
    public List<Integer> rightSideView(TreeNode root) {
        dfs(root, 0);
        return rightSide;
    }

    private void dfs(TreeNode node, int level) {
        if (node == null) return;
        if (level == rightSide.size()) rightSide.add(node.val);   // first visit to this depth
        dfs(node.right, level + 1);    // right first
        dfs(node.left, level + 1);
    }
}
```

```cpp
#include <vector>

class BinaryTreeRightSideView {
    std::vector<int> rightSide;

    void dfs(TreeNode* node, int level) {
        if (!node) return;
        if (level == (int)rightSide.size()) rightSide.push_back(node->val);   // first visit
        dfs(node->right, level + 1);    // right first
        dfs(node->left, level + 1);
    }

public:
    /**
     * @param root tree root
     * @return     the rightmost value of each level
     */
    std::vector<int> rightSideView(TreeNode* root) {
        dfs(root, 0);
        return rightSide;
    }
};
```

```python
def right_side_view(root: Optional["TreeNode"]) -> list[int]:
    """
    @param root: tree root
    @return:     the rightmost value of each level
    """
    right_side = []

    def dfs(node, level: int) -> None:
        if node is None:
            return
        if level == len(right_side):      # first visit to this depth
            right_side.append(node.val)
        dfs(node.right, level + 1)        # right first
        dfs(node.left, level + 1)

    dfs(root, 0)
    return right_side
```

```rust
use std::cell::RefCell;
use std::rc::Rc;

impl Solution {
    /// @param root tree root
    /// @return     the rightmost value of each level
    pub fn right_side_view(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<i32> {
        let mut right_side: Vec<i32> = Vec::new();

        fn dfs(node: Option<Rc<RefCell<TreeNode>>>, level: usize, right_side: &mut Vec<i32>) {
            if let Some(n) = node {
                let n = n.borrow();
                if level == right_side.len() {       // first visit to this depth
                    right_side.push(n.val);
                }
                dfs(n.right.clone(), level + 1, right_side);   // right first
                dfs(n.left.clone(), level + 1, right_side);
            }
        }

        dfs(root, 0, &mut right_side);
        right_side
    }
}
```


## Dry run

**Input:** `root = [1,2,3,null,5,null,4]` — level 1: 2 (left), 3 (right); level 2: 5 (under 2), 4 (under 3).

```
dfs(1, 0): level 0 == size 0 -> add 1.   rightSide = [1]
  dfs(3, 1): level 1 == size 1 -> add 3. rightSide = [1,3]
    dfs(4, 2): level 2 == size 2 -> add 4.  rightSide = [1,3,4]   (3's right child first)
    dfs(null, 2) -> return.
  dfs(2, 1): level 1 != size 3 -> no add.   (2 is not the rightmost of level 1)
    dfs(5, 2): level 2 != size 3 -> no add. (5 is not the rightmost of level 2)
    dfs(null, 2) -> return.

Output: [1,3,4] ✓
```

The right-first ordering is what decides level 2: `4` (under the right subtree) is visited before `5` (under the left subtree), so `4` claims the `level == size` slot. The `size` guard means "this level hasn't been claimed yet" — one line of state replacing a whole level bookkeeping structure.

## Complexity

**Time.** Every node visited once:

$$
T(n) = O(n)
$$

**Space.** Recursion depth (worst case skewed):

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Binary Tree Level Order Traversal** ([5.2](binary-tree-level-order-traversal.md)) — the BFS mirror: fencing + take the level's last node.
- **Find Largest Value In Each Tree Row** (`tree/bfs/`) — the same per-level claim, *max* instead of rightmost.
- **Binary Tree Zigzag Level Order Traversal** (`tree/BinaryTreeZigZagLevelOrderTraversal.kt`) — level order with direction flips; the same fence machinery.
- **Interview follow-up:** "Why is `level == rightSide.size` the right test rather than a `level > size` guard?" Because the DFS claims each level exactly once — `size` *is* the number of claimed levels, so equality fires on the first visit and only the first. Any later visit to the same depth finds `level < size` and is correctly ignored. The size-as-counter is the minimal state.
