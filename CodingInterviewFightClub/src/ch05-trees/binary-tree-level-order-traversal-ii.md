# 5.26 Binary Tree Level Order Traversal II

> **Source**: [`src/main/kotlin/tree/bfs/BinaryTreeLevelOrderTraversal_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bfs/BinaryTreeLevelOrderTraversal_II.kt)
> **Pattern**: BFS fence + reverse · **Core page**

## The Problem

Level-order traversal **bottom-up** (leaves' level first).

- Constraints: n ≤ 2000.

## Examples

```
Input:  root = [3,9,20,null,null,15,7]   -> Output: [[15,7],[9,20],[3]]
```

## Intuition — the [5.2](binary-tree-level-order-traversal.md) BFS, adding at the front

The fence BFS collects top-down; prepending each level gives bottom-up:

```kotlin
val result = LinkedList<List<Int>>()

while (queue.isNotEmpty()) {
    val size = queue.size
    val level = mutableListOf<Int>()

    repeat(size) {
        val current = queue.poll()
        level.add(current.`val`)
        current.left?.let { queue.offer(it) }
        current.right?.let { queue.offer(it) }
    }
    result.addFirst(level)     // bottom-up!
}
return result
```

## Approach 1 — BFS + reverse (the repo's version, optimal)

```kotlin
import java.util.*

class BinaryTreeLevelOrderTraversal_II {
    /**
     * @param root tree root
     * @return     bottom-up level order
     */
    fun levelOrderBottom(root: TreeNode?): List<List<Int>> {
        val result = LinkedList<List<Int>>()
        val queue = LinkedList<TreeNode>()

        if (root == null) return listOf()
        queue.offer(root)

        while (queue.isNotEmpty()) {
            val size = queue.size
            val level = mutableListOf<Int>()

            repeat(size) {
                val current = queue.poll()
                level.add(current.`val`)

                current.left?.let { queue.offer(it) }
                current.right?.let { queue.offer(it) }
            }
            result.addFirst(level)
        }
        return result
    }
}
```

```java
import java.util.*;

public class BinaryTreeLevelOrderTraversalII {
    /**
     * @param root tree root
     * @return     bottom-up level order
     */
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        LinkedList<List<Integer>> result = new LinkedList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.addFirst(level);
        }
        return result;
    }
}
```

```cpp
#include <vector>
#include <queue>
#include <algorithm>

class BinaryTreeLevelOrderTraversalII {
public:
    /**
     * @param root tree root
     * @return     bottom-up level order
     */
    std::vector<std::vector<int>> levelOrderBottom(TreeNode* root) {
        std::vector<std::vector<int>> result;
        if (!root) return result;

        std::queue<TreeNode*> queue;
        queue.push(root);

        while (!queue.empty()) {
            int size = queue.size();
            std::vector<int> level;

            for (int i = 0; i < size; i++) {
                TreeNode* node = queue.front(); queue.pop();
                level.push_back(node->val);

                if (node->left) queue.push(node->left);
                if (node->right) queue.push(node->right);
            }
            result.push_back(level);
        }

        std::reverse(result.begin(), result.end());
        return result;
    }
};
```

```python
from collections import deque

def level_order_bottom(root: Optional["TreeNode"]) -> list[list[int]]:
    """
    @param root: tree root
    @return:     bottom-up level order
    """
    result = []
    if not root:
        return result

    queue = deque([root])
    while queue:
        level = []
        for _ in range(len(queue)):
            node = queue.popleft()
            level.append(node.val)

            if node.left:
                queue.append(node.left)
            if node.right:
                queue.append(node.right)

        result.append(level)

    return result[::-1]
```

```rust
use std::collections::VecDeque;
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root tree root
    /// @return     bottom-up level order
    pub fn level_order_bottom(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<Vec<i32>> {
        let mut result = Vec::new();
        let mut queue: VecDeque<Option<Rc<RefCell<TreeNode>>>> = VecDeque::new();
        if root.is_some() { queue.push_back(root); }

        while !queue.is_empty() {
            let mut level = Vec::new();

            for _ in 0..queue.len() {
                if let Some(Some(node)) = queue.pop_front() {
                    let n = node.borrow();
                    level.push(n.val);

                    if n.left.is_some() { queue.push_back(n.left.clone()); }
                    if n.right.is_some() { queue.push_back(n.right.clone()); }
                }
            }
            result.push(level);
        }

        result.reverse();
        result
    }
}
```

## Dry run

**Input:** `root = [3,9,20,null,null,15,7]`.

```
top-down: [[3],[9,20],[15,7]] -> addFirst / reverse -> [[15,7],[9,20],[3]] ✓
```

## Complexity

**Time.** Each node once:

$$
T(n) = O(n)
$$

**Space.** Queue + result:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Binary Tree Level Order Traversal** ([5.2](binary-tree-level-order-traversal.md)) — the top-down ancestor.
- **Interview follow-up:** "Why `addFirst`/reverse instead of a stack?" Same effect; the linked-list head-insert keeps the BFS structure recognizable — the [5.2](binary-tree-level-order-traversal.md) fence with the output direction flipped.
