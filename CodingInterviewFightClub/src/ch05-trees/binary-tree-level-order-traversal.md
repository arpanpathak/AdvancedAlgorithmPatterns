# 5.2 Binary Tree Level Order Traversal

> **Source:** [`src/main/kotlin/tree/BinaryTreeLevelOrderTraversal.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/BinaryTreeLevelOrderTraversal.kt)
> **Pattern:** BFS with level fencing · **Core page**

## The Problem

Given a binary tree's root, return its nodes' values in **level order** — top to bottom, left to right, grouped by level.

- Constraints: $0 \le n \le 2000$.

## Examples

```
Input:      3
           / \
          9  20
             /  \
            15   7
Output: [[3], [9, 20], [15, 7]]
```

## Intuition — the queue holds "frontier", the fence holds "level"

BFS keeps a queue of nodes to visit. The key detail for *grouped* output: **snapshot the queue's size at the start of each level** — that's exactly how many nodes belong to this level (all their children will form the next). Process exactly that many, collecting values; everything enqueued during that processing belongs to the *next* level.

Why snapshot and not `while (!queue.isEmpty())`? Without the fence, BFS still visits in level order — but you can't tell *where* one level ends and the next begins. The `size = queue.size` snapshot IS the level boundary. This "level fencing" is the single most reused BFS idiom in tree problems (Right Side View, Averages Per Level, Largest Per Row — the whole `tree/bfs/` folder is this page wearing different costumes).

## Approach 1 — DFS with depth indexing

Recursively visit, tracking depth; append to `result[depth]`. $O(n)$ time, $O(h)$ space — correct but BFS is the natural fit (and the iterative one is stack-safe).

## Approach 2 — BFS with level fencing (the repo's version, optimal)

```kotlin
/**
 * @param root the root of the binary tree
 * @return     the node values grouped by level, top to bottom
 */
fun levelOrder(root: TreeNode?): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    if (root == null) return result

    val queue: Queue<TreeNode> = LinkedList()
    queue.add(root)

    while (queue.isNotEmpty()) {
        val level = mutableListOf<Int>()
        val size = queue.size            // FENCE: how many nodes are in THIS level

        repeat(size) {
            val node = queue.poll()
            level.add(node.`val`)

            node.left?.let(queue::add)   // these belong to the NEXT level
            node.right?.let(queue::add)
        }
        result.add(level)
    }
    return result
}
```

```java
import java.util.*;

public class BinaryTreeLevelOrderTraversal {
    /**
     * @param root the root of the binary tree
     * @return     the node values grouped by level, top to bottom
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int size = queue.size();                 // FENCE: this level's size
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            result.add(level);
        }
        return result;
    }
}
```

```cpp
#include <vector>
#include <queue>

class BinaryTreeLevelOrderTraversal {
public:
    /**
     * @param root the root of the binary tree
     * @return     the node values grouped by level, top to bottom
     */
    std::vector<std::vector<int>> levelOrder(TreeNode* root) {
        std::vector<std::vector<int>> result;
        if (!root) return result;

        std::queue<TreeNode*> q;
        q.push(root);

        while (!q.empty()) {
            int size = (int)q.size();                // FENCE
            std::vector<int> level;
            for (int i = 0; i < size; i++) {
                TreeNode* node = q.front();
                q.pop();
                level.push_back(node->val);
                if (node->left) q.push(node->left);
                if (node->right) q.push(node->right);
            }
            result.push_back(level);
        }
        return result;
    }
};
```

```python
from collections import deque

def level_order(root: TreeNode | None) -> list[list[int]]:
    """
    @param root: the root of the binary tree
    @return:     the node values grouped by level, top to bottom
    """
    result: list[list[int]] = []
    if root is None:
        return result

    queue = deque([root])
    while queue:
        size = len(queue)                    # FENCE: this level's size
        level: list[int] = []
        for _ in range(size):
            node = queue.popleft()
            level.append(node.val)
            if node.left:
                queue.append(node.left)
            if node.right:
                queue.append(node.right)
        result.append(level)
    return result
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param root the root of the binary tree
    /// @return     the node values grouped by level, top to bottom
    pub fn level_order(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<Vec<i32>> {
        let mut result = Vec::new();
        let mut queue = VecDeque::new();
        if let Some(r) = root {
            queue.push_back(r);
        }

        while !queue.is_empty() {
            let size = queue.len();              // FENCE
            let mut level = Vec::new();
            for _ in 0..size {
                let node = queue.pop_front().unwrap();
                let n = node.borrow();
                level.push(n.val);
                if let Some(l) = n.left.clone() { queue.push_back(l); }
                if let Some(r) = n.right.clone() { queue.push_back(r); }
            }
            result.push(level);
        }
        result
    }
}
```

### 3. `BinaryTreeVerticalOrderTraversal.kt` — the `data class` BFS state

The vertical-order BFS carries `(node, column)` — and the repo even shows the *functional DFS sketch* commented out, with `TreeMap` + `getOrPut`:

```kotlin
// The commented-out functional DFS (the "what if" sketch):
//   fun dfs(node: TreeNode?, verticalIndex: Int = 0) {
//       if (node == null) return
//       val bucket = result.getOrPut(verticalIndex) { LinkedList() }
//       bucket.add(node.`val`)
//       dfs(node.left, verticalIndex - 1)
//       dfs(node.right, verticalIndex + 1)
//   }
//   return result.map { it.value }

// The BFS version uses an explicit state carrier:
data class VerticalIndex(val node: TreeNode, val verticalIndex: Int)

fun verticalOrder(root: TreeNode?): List<List<Int>> {
    if (root == null) return emptyList()
    val result = TreeMap<Int, ArrayList<Int>>()
    val queue: Queue<VerticalIndex> = LinkedList()
    queue.offer(VerticalIndex(root, 0))
    // ... BFS with (node, column) pairs; TreeMap keeps columns sorted
}
```

**What's cool:** the commented DFS is the *teaching artifact* — it shows the natural (but order-incorrect) recursion before the BFS that fixes level order; `getOrPut(verticalIndex) { LinkedList() }` is the bucket-create idiom; and the `data class` state carrier is the [6.x](../ch06-graphs/pattern-primer.md) "BFS with payload" pattern.


## Dry run

**Input:** the tree above.

```
queue: [3]
  level 1: size=1 -> pop 3, enqueue 9, 20.  level=[3]
queue: [9, 20]
  level 2: size=2 -> pop 9 (leaf, nothing enqueued); pop 20, enqueue 15, 7.  level=[9, 20]
queue: [15, 7]
  level 3: size=2 -> pop 15, 7 (leaves).  level=[15, 7]
queue: []
Result: [[3], [9, 20], [15, 7]] ✓
```

Notice how the fence (`size` snapshotted *before* processing) keeps the children (15, 7) in the *next* bucket even though they enter the queue while level 2 is still being processed. That's the entire trick, in one sentence.

## Complexity

**Time.** Each node enters and leaves the queue once:

$$
T(n) = O(n)
$$

**Space.** $O(w)$ where $w$ = max queue size (the widest level, up to $\lceil n/2 \rceil$ in a complete tree).

## Variants & follow-ups

- **Binary Tree Level Order Traversal II** (`src/main/kotlin/tree/bfs/BinaryTreeLevelOrderTraversal_II.kt`) — same BFS, `result.reverse()` at the end.
- **Binary Tree Right Side View** (`src/main/kotlin/tree/BinaryTreeRightSideView.kt`) — take the *last* node of each level.
- **Average Of Levels / Largest Value Per Row / Find Largest Per Row** (`tree/bfs/`) — reduce each level instead of copying it.
- **Check Completeness Of A Binary Tree** (`tree/bfs/CheckCompletenessOfBinaryTree.kt`) — BFS without fences; the first `null` ends the "seen non-null" window.
- **Zigzag Level Order** (`src/main/kotlin/tree/BinaryTreeZigZagLevelOrderTraversal.kt`) — same BFS, reverse every other level.
- **Interview follow-up:** "What if the tree is huge and skewed?" BFS queue is $O(1)$ for skewed trees (one node per level) — it's the *balanced* tree that maxes the queue at $O(n/2)$. Contrast with DFS stack being $O(n)$ for skewed. Knowing which shape hurts which traversal is the depth-signal.
