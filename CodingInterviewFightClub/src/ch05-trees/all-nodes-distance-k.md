# 5.12 All Nodes Distance K In Binary Tree

> **Source:** [`src/main/kotlin/tree/AllNodesDistanceKinBinaryTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/AllNodesDistanceKinBinaryTree.kt)
> **Pattern:** parent map → 3-direction DFS · **Core page**

## The Problem

All nodes at **distance k** from a given `target` node in a binary tree.

- Constraints: k ≥ 0; all values unique.

## Examples

```
Input:  root = [3,5,1,6,2,0,8,null,null,7,4], target = 5, k = 2
Output: [7,4,1]   (5's children at distance 2: 7, 4; and the grandparent's other child: 1)
```

## Intuition — the tree becomes a graph via a parent map; DFS 3 directions

A tree node's neighbors are `left`, `right`, and **parent**. Record parents in one pass; then DFS from `target` outward — visiting `left`, `right`, and `parent` — collecting nodes exactly at depth `k`:

```kotlin
val parentMap = mutableMapOf<TreeNode, TreeNode?>()

fun dfs(node: TreeNode?, parent: TreeNode?) {
    if (node == null) return
    parentMap[node] = parent
    if (node == target) collectNodesAtDistanceK(node, k, mutableSetOf(), result)
    dfs(node.left, node)
    dfs(node.right, node)
}

fun collectNodesAtDistanceK(node: TreeNode?, k: Int, visited: MutableSet<TreeNode>, result: MutableList<Int>) {
    if (node == null || visited.contains(node)) return
    visited.add(node)
    when (k) {
        0 -> result.add(node.`val`)
        else -> {
            collectNodesAtDistanceK(node.left, k - 1, visited, result)
            collectNodesAtDistanceK(node.right, k - 1, visited, result)
            collectNodesAtDistanceK(parentMap[node], k - 1, visited, result)   // up!
        }
    }
}
```

**Why the parent map?** The target isn't the root — reaching the *other side* of the tree requires walking up. The parent map turns the rooted tree into an undirected graph in one pass.

**Why `visited`?** The walk can go up to a parent and back down into a *visited* subtree — the set prevents infinite loops (the tree-as-graph needs a visited set exactly like [6.2](../ch06-graphs/clone-graph.md)).

**Why `k` decrement with a `when`?** The depth counter is the level fence ([5.2](binary-tree-level-order-traversal.md)) in DFS clothing: at `k == 0` the node is recorded; otherwise the three neighbors are explored one level deeper.

## Approach 1 — Convert to adjacency + BFS (O(n) space, more machinery)

Build a full graph, run BFS from target: correct, but the parent-map version needs less structure.

## Approach 2 — Parent map + 3-direction DFS (the repo's version, optimal)

```kotlin
class AllNodesDistanceKinBinaryTree {
    val parentMap = mutableMapOf<TreeNode, TreeNode?>()

    /**
     * @param root   tree root
     * @param target the center node
     * @param k      target distance
     * @return       values of nodes at distance k from target
     */
    fun distanceK(root: TreeNode?, target: TreeNode?, k: Int): List<Int> {
        val result = mutableListOf<Int>()

        fun dfs(node: TreeNode?, parent: TreeNode?) {
            if (node == null) return
            parentMap[node] = parent
            if (node == target) collectNodesAtDistanceK(node, k, mutableSetOf(), result)
            dfs(node.left, node)
            dfs(node.right, node)
        }

        dfs(root, null)
        return result
    }

    private fun collectNodesAtDistanceK(
        node: TreeNode?, k: Int, visited: MutableSet<TreeNode>, result: MutableList<Int>
    ) {
        if (node == null || visited.contains(node)) return
        visited.add(node)

        when (k) {
            0 -> result.add(node.`val`)
            else -> {
                collectNodesAtDistanceK(node.left, k - 1, visited, result)
                collectNodesAtDistanceK(node.right, k - 1, visited, result)
                collectNodesAtDistanceK(parentMap[node], k - 1, visited, result)
            }
        }
    }
}
```

```java
import java.util.*;

public class AllNodesDistanceK {
    private Map<TreeNode, TreeNode> parent = new HashMap<>();

    /**
     * @param root   tree root
     * @param target the center node
     * @param k      target distance
     * @return       values of nodes at distance k from target
     */
    public List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        buildParents(root, null);

        List<Integer> result = new ArrayList<>();
        collect(target, k, new HashSet<>(), result);
        return result;
    }

    private void buildParents(TreeNode node, TreeNode par) {
        if (node == null) return;
        parent.put(node, par);
        buildParents(node.left, node);
        buildParents(node.right, node);
    }

    private void collect(TreeNode node, int k, Set<TreeNode> visited, List<Integer> result) {
        if (node == null || visited.contains(node)) return;
        visited.add(node);

        if (k == 0) { result.add(node.val); return; }
        collect(node.left, k - 1, visited, result);
        collect(node.right, k - 1, visited, result);
        collect(parent.get(node), k - 1, visited, result);
    }
}
```

```cpp
#include <unordered_map>
#include <unordered_set>
#include <vector>

class AllNodesDistanceK {
    std::unordered_map<TreeNode*, TreeNode*> parent;

    void build(TreeNode* node, TreeNode* par) {
        if (!node) return;
        parent[node] = par;
        build(node->left, node);
        build(node->right, node);
    }

    void collect(TreeNode* node, int k, std::unordered_set<TreeNode*>& visited, std::vector<int>& result) {
        if (!node || visited.count(node)) return;
        visited.insert(node);

        if (k == 0) { result.push_back(node->val); return; }
        collect(node->left, k - 1, visited, result);
        collect(node->right, k - 1, visited, result);
        collect(parent[node], k - 1, visited, result);
    }

public:
    /**
     * @param root   tree root
     * @param target the center node
     * @param k      target distance
     * @return       values of nodes at distance k from target
     */
    std::vector<int> distanceK(TreeNode* root, TreeNode* target, int k) {
        build(root, nullptr);

        std::vector<int> result;
        std::unordered_set<TreeNode*> visited;
        collect(target, k, visited, result);
        return result;
    }
};
```

```python
def distance_k(root: Optional["TreeNode"], target: Optional["TreeNode"], k: int) -> list[int]:
    """
    @param root:   tree root
    @param target: the center node
    @param k:      target distance
    @return:       values of nodes at distance k from target
    """
    parent = {}

    def build(node, par):
        if not node:
            return
        parent[node] = par
        build(node.left, node)
        build(node.right, node)

    build(root, None)

    result = []

    def collect(node, depth, visited):
        if not node or node in visited:
            return
        visited.add(node)

        if depth == 0:
            result.append(node.val)
            return
        collect(node.left, depth - 1, visited)
        collect(node.right, depth - 1, visited)
        collect(parent.get(node), depth - 1, visited)

    collect(target, k, set())
    return result
```

```rust
use std::cell::RefCell;
use std::collections::{HashMap, HashSet};
use std::rc::Rc;

impl Solution {
    /// @param root   tree root
    /// @param target the center node
    /// @param k      target distance
    /// @return       values of nodes at distance k from target
    pub fn distance_k(root: Option<Rc<RefCell<TreeNode>>>, target: Option<Rc<RefCell<TreeNode>>>, k: i32) -> Vec<i32> {
        let mut parent: HashMap<i32, Option<Rc<RefCell<TreeNode>>>> = HashMap::new();

        fn build(node: Option<Rc<RefCell<TreeNode>>>, par: Option<Rc<RefCell<TreeNode>>>,
                 parent: &mut HashMap<i32, Option<Rc<RefCell<TreeNode>>>>) {
            if let Some(n) = node.clone() {
                parent.insert(n.borrow().val, par);
                build(n.borrow().left.clone(), Some(n.clone()), parent);
                build(n.borrow().right.clone(), Some(n.clone()), parent);
            }
        }

        fn collect(node: Option<Rc<RefCell<TreeNode>>>, depth: i32,
                   parent: &HashMap<i32, Option<Rc<RefCell<TreeNode>>>>,
                   visited: &mut HashSet<i32>, result: &mut Vec<i32>) {
            if let Some(n) = node {
                let val = n.borrow().val;
                if visited.contains(&val) { return; }
                visited.insert(val);

                if depth == 0 { result.push(val); return; }
                collect(n.borrow().left.clone(), depth - 1, parent, visited, result);
                collect(n.borrow().right.clone(), depth - 1, parent, visited, result);
                if let Some(p) = parent.get(&val) {
                    collect(p.clone(), depth - 1, parent, visited, result);
                }
            }
        }

        build(root, None, &mut parent);
        let mut result = Vec::new();
        collect(target, k, &parent, &mut HashSet::new(), &mut result);
        result
    }
}
```

## Dry run

**Input:** `root = [3,5,1,6,2,0,8,null,null,7,4]`, `target = 5`, `k = 2`.

```
parent map: 3->null, 5->3, 1->3, 6->5, 2->5, 0->1, 8->1, 7->2, 4->2

collect(5, 2, {}):
  depth 2 -> explore left(6,1), right(2,1), parent(3,1)
  collect(6, 1): children at depth 0: 6's children null -> nothing.
  collect(2, 1): left(7,0) -> add 7.  right(4,0) -> add 4.  parent(5) visited.
  collect(3, 1): left(5) visited.  right(1,0) -> add 1.  parent(null).

Output: [7,4,1] ✓
```

The parent hop is the key move: `collect(3, 1)` walks *up* from the target's subtree into the rest of the tree, then down to node 1. The visited set stops the re-descent into 5's subtree — without it, the walk would oscillate 5 → 3 → 5 forever. The k-counter gates the collection: exactly depth-2 nodes make the list.

## Complexity

**Time.** Each node visited once across the walk:

$$
T(n) = O(n)
$$

**Space.** Parent map + visited:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Clone Graph** ([6.2](../ch06-graphs/clone-graph.md)) — the visited-set discipline for graphs; a tree with parent pointers is a graph.
- **Binary Tree Level Order Traversal** ([5.2](binary-tree-level-order-traversal.md)) — the k-counter as DFS vs the level fence as BFS.
- **Interview follow-up:** "Why does the tree need a visited set?" A tree is acyclic *top-down* — but the parent pointer adds upward edges, creating cycles (5 → 3 → 5). The moment you add the third direction, the tree becomes a graph and the [6.2](../ch06-graphs/clone-graph.md) visited discipline becomes mandatory.
