# 5.17 Find Largest Value In Each Tree Row

> **Source:** [`src/main/kotlin/tree/bfs/FindLargestValueInEachTreeRow.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bfs/FindLargestValueInEachTreeRow.kt)
> **Pattern:** BFS level fence + per-level max · **Core page**

## The Problem

The **maximum value per level** of a binary tree.

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  root = [1,3,2,5,3,null,9]   -> Output: [1,3,9]
```

## Intuition — the [5.2](binary-tree-level-order-traversal.md) fence with a max

BFS with the level fence; track `max` per level instead of collecting the level:

```kotlin
val queue: Queue<TreeNode> = LinkedList()
root?.let { queue.offer(it) }

while (queue.isNotEmpty()) {
    val size = queue.size
    var max = Int.MIN_VALUE

    repeat(size) {
        val current = queue.poll()
        max = maxOf(max, current.`val`)

        current.left?.let { queue.offer(it) }
        current.right?.let { queue.offer(it) }
    }
    result.add(max)
}
```

**Why the fence?** The `repeat(size)` groups one level per outer iteration — the max resets per level, the queue drains level by level ([5.2](binary-tree-level-order-traversal.md) engine).

## Approach 1 — DFS with depth-indexed maxes

`dfs(node, depth)` updating `result[depth]`: also O(n), recursion-based.

## Approach 2 — BFS level max (the repo's version, optimal)

```kotlin
import java.util.*

class FindLargestValueInEachTreeRow {
    /**
     * @param root tree root
     * @return     max value per level
     */
    fun largestValues(root: TreeNode?): List<Int> {
        val result = mutableListOf<Int>()
        val queue: Queue<TreeNode> = LinkedList()
        root?.let { queue.offer(it) }

        while (queue.isNotEmpty()) {
            val size = queue.size
            var max = Int.MIN_VALUE

            repeat(size) {
                val current = queue.poll()
                max = maxOf(max, current.`val`)

                current.left?.let { queue.offer(it) }
                current.right?.let { queue.offer(it) }
            }
            result.add(max)
        }
        return result
    }
}
```

```java
import java.util.*;

public class FindLargestValueInEachTreeRow {
    /**
     * @param root tree root
     * @return     max value per level
     */
    public List<Integer> largestValues(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            int max = Integer.MIN_VALUE;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                max = Math.max(max, node.val);

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(max);
        }
        return result;
    }
}
```

```cpp
#include <queue>
#include <vector>
#include <climits>

class FindLargestValueInEachTreeRow {
public:
    /**
     * @param root tree root
     * @return     max value per level
     */
    std::vector<int> largestValues(TreeNode* root) {
        std::vector<int> result;
        if (!root) return result;

        std::queue<TreeNode*> queue;
        queue.push(root);

        while (!queue.empty()) {
            int size = queue.size();
            int max = INT_MIN;

            for (int i = 0; i < size; i++) {
                TreeNode* node = queue.front(); queue.pop();
                max = std::max(max, node->val);

                if (node->left) queue.push(node->left);
                if (node->right) queue.push(node->right);
            }
            result.push_back(max);
        }
        return result;
    }
};
```

```python
from collections import deque

def largest_values(root: Optional["TreeNode"]) -> list[int]:
    """
    @param root: tree root
    @return:     max value per level
    """
    result = []
    if not root:
        return result

    queue = deque([root])
    while queue:
        level_max = float("-inf")
        for _ in range(len(queue)):
            node = queue.popleft()
            level_max = max(level_max, node.val)

            if node.left:
                queue.append(node.left)
            if node.right:
                queue.append(node.right)

        result.append(level_max)
    return result
```

```rust
use std::collections::VecDeque;
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root tree root
    /// @return     max value per level
    pub fn largest_values(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<i32> {
        let mut result = Vec::new();
        let mut queue = VecDeque::new();
        if root.is_some() { queue.push_back(root); }

        while !queue.is_empty() {
            let mut level_max = i32::MIN;

            for _ in 0..queue.len() {
                if let Some(Some(node)) = queue.pop_front() {
                    let n = node.borrow();
                    level_max = level_max.max(n.val);

                    if n.left.is_some() { queue.push_back(n.left.clone()); }
                    if n.right.is_some() { queue.push_back(n.right.clone()); }
                }
            }
            result.push(level_max);
        }
        result
    }
}
```

## Dry run

**Input:** `root = [1,3,2,5,3,null,9]`.

```
level 0: queue [1].  max = 1.  result [1].  enqueue 3, 2.
level 1: queue [3,2].  max(3, 2) = 3.  result [1,3].  enqueue 5, 3, 9.
level 2: queue [5,3,9].  max = 9.  result [1,3,9].  no children.

Output: [1,3,9] ✓
```

## Complexity

**Time.** Each node once:

$$
T(n) = O(n)
$$

**Space.** Queue:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Binary Tree Level Order Traversal** ([5.2](binary-tree-level-order-traversal.md)) — the fence machine this page adapts.
- **Maximum Level Sum** (`tree/bfs/MaximumLevelSumOfABinaryTreee.kt`) — the same fence with a sum and the level index.
- **Interview follow-up:** "Why BFS over DFS here?" Both work; BFS's fence makes "per level" literal — the max resets exactly at level boundaries with no depth bookkeeping. The `Int.MIN_VALUE` seed is safe because tree values always beat it.
