# 5.13 Binary Tree ZigZag Level Order

> **Source:** [`src/main/kotlin/tree/BinaryTreeZigZagLevelOrderTraversal.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/BinaryTreeZigZagLevelOrderTraversal.kt)
> **Pattern:** level fence + alternate insertion side · **Core page**

## The Problem

Level-order traversal, but alternate direction each level (left-to-right, then right-to-left, ...).

- Constraints: tree size ≤ 2000.

## Examples

```
Input:  root = [3,9,20,null,null,15,7]
Output: [[3],[20,9],[15,7]]   (level 1 reversed)
```

## Intuition — the [5.2](binary-tree-level-order-traversal.md) fence with a parity flip

Same BFS + level fence; the only change is **where the value lands in the level list**:

```kotlin
val queue: Queue<IndexedNode> = LinkedList()
var level = 0

while (queue.isNotEmpty()) {
    val levelSize = queue.size
    val currentLevel = LinkedList<Int>()

    for (i in 0 until levelSize) {
        val (currentNode, _) = queue.poll()

        if (level % 2 == 0) currentLevel.add(currentNode?.`val` ?: 0)
        else                currentLevel.addFirst(currentNode?.`val` ?: 0)   // zig!

        currentNode?.left?.let { queue.add(IndexedNode(it, 2 * i + 1)) }
        currentNode?.right?.let { queue.add(IndexedNode(it, 2 * i + 2)) }
    }
    level++
    result.add(currentLevel)
}
```

**Why `addFirst` on odd levels?** The BFS visits left-to-right every level; the zigzag wants right-to-left on odd levels. `addFirst` prepends each arriving value — the last visited ends up first — reversing the level *without* a separate reverse pass.

**Why the `IndexedNode` data class?** The repo carries `(node, index)` for a heap-index flavor — the index isn't needed for the zigzag itself, but the [5.2](binary-tree-level-order-traversal.md) data-class-BFS pattern is the template. (The `?: 0` on `val` is defensive; tree nodes are non-null in practice.)

## Approach 1 — BFS then reverse odd levels (two passes)

Traverse normally, reverse every odd-indexed level: correct, extra O(level) work per odd level.

## Approach 2 — Level fence + parity `addFirst` (the repo's version, optimal)

```kotlin
import java.util.*

class BinaryTreeZigZagLevelOrderTraversal {
    data class IndexedNode(var node: TreeNode?, var index: Int)

    /**
     * @param root tree root
     * @return     zigzag level-order values
     */
    fun zigzagLevelOrder(root: TreeNode?): List<List<Int>> {
        val result = mutableListOf<MutableList<Int>>()
        if (root == null) return result

        val queue: Queue<IndexedNode> = LinkedList()
        queue.add(IndexedNode(root, 0))
        var level = 0

        while (queue.isNotEmpty()) {
            val levelSize = queue.size
            val currentLevel = LinkedList<Int>()

            for (i in 0 until levelSize) {
                val (currentNode, _) = queue.poll()

                if (level % 2 == 0) {
                    currentLevel.add(currentNode?.`val` ?: 0)
                } else {
                    currentLevel.addFirst(currentNode?.`val` ?: 0)
                }

                currentNode?.left?.let { queue.add(IndexedNode(it, 2 * i + 1)) }
                currentNode?.right?.let { queue.add(IndexedNode(it, 2 * i + 2)) }
            }
            level++
            result.add(currentLevel)
        }
        return result
    }
}
```

```java
import java.util.*;

public class BinaryTreeZigzagLevelOrderTraversal {
    /**
     * @param root tree root
     * @return     zigzag level-order values
     */
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int size = queue.size();
            LinkedList<Integer> level = new LinkedList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (leftToRight) level.addLast(node.val);
                else level.addFirst(node.val);

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            leftToRight = !leftToRight;
            result.add(level);
        }
        return result;
    }
}
```

```cpp
#include <queue>
#include <vector>
#include <deque>

class BinaryTreeZigzagLevelOrderTraversal {
public:
    /**
     * @param root tree root
     * @return     zigzag level-order values
     */
    std::vector<std::vector<int>> zigzagLevelOrder(TreeNode* root) {
        std::vector<std::vector<int>> result;
        if (!root) return result;

        std::queue<TreeNode*> queue;
        queue.push(root);
        bool leftToRight = true;

        while (!queue.empty()) {
            int size = queue.size();
            std::deque<int> level;

            for (int i = 0; i < size; i++) {
                TreeNode* node = queue.front(); queue.pop();

                if (leftToRight) level.push_back(node->val);
                else level.push_front(node->val);

                if (node->left) queue.push(node->left);
                if (node->right) queue.push(node->right);
            }
            leftToRight = !leftToRight;
            result.emplace_back(level.begin(), level.end());
        }
        return result;
    }
};
```

```python
from collections import deque

def zigzag_level_order(root: Optional["TreeNode"]) -> list[list[int]]:
    """
    @param root: tree root
    @return:     zigzag level-order values
    """
    result = []
    if not root:
        return result

    queue = deque([root])
    left_to_right = True

    while queue:
        level = deque()
        for _ in range(len(queue)):
            node = queue.popleft()

            if left_to_right:
                level.append(node.val)
            else:
                level.appendleft(node.val)

            if node.left:
                queue.append(node.left)
            if node.right:
                queue.append(node.right)

        result.append(list(level))
        left_to_right = not left_to_right

    return result
```

```rust
use std::collections::VecDeque;
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root tree root
    /// @return     zigzag level-order values
    pub fn zigzag_level_order(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<Vec<i32>> {
        let mut result = Vec::new();
        let mut queue = VecDeque::new();
        if root.is_some() { queue.push_back(root); }
        let mut left_to_right = true;

        while !queue.is_empty() {
            let mut level = VecDeque::new();

            for _ in 0..queue.len() {
                if let Some(Some(node)) = queue.pop_front() {
                    let n = node.borrow();

                    if left_to_right { level.push_back(n.val); }
                    else { level.push_front(n.val); }

                    if n.left.is_some() { queue.push_back(n.left.clone()); }
                    if n.right.is_some() { queue.push_back(n.right.clone()); }
                }
            }
            result.push(level.into_iter().collect());
            left_to_right = !left_to_right;
        }
        result
    }
}
```

## Dry run

**Input:** `root = [3,9,20,null,null,15,7]`.

```
level 0 (LTR): queue=[3].  poll 3 -> level [3].  enqueue 9, 20.  result [[3]]
level 1 (RTL): poll 9 -> addFirst -> [9].  poll 20 -> addFirst -> [20,9].
               enqueue 15, 7 (from 20).  result [[3],[20,9]]
level 2 (LTR): poll 15 -> [15].  poll 7 -> [15,7].  result [[3],[20,9],[15,7]] ✓
```

The parity flip is the whole difference from [5.2](binary-tree-level-order-traversal.md): level 1's `addFirst` turns the left-to-right BFS visit order into a right-to-left level. The BFS *never changes direction* — only the insertion side does, which is why the queue stays plain.

## Complexity

**Time.** Each node visited once:

$$
T(n) = O(n)
$$

**Space.** Queue + one level:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Binary Tree Level Order Traversal** ([5.2](binary-tree-level-order-traversal.md)) — the base machine; zigzag is a one-line delta.
- **Vertical Order Traversal** — the `data class` BFS with column indices (the repo's `BinaryTreeVerticalOrderTraversal.kt`, section on the [5.2](binary-tree-level-order-traversal.md) page).
- **Interview follow-up:** "Why `addFirst` instead of `Collections.reverse(level)`?" `addFirst` is O(1) per element — the reversal is interleaved with the traversal, not a second pass. The parity test (`level % 2`) chooses the insertion side; a boolean `leftToRight` flip is the equivalent.
