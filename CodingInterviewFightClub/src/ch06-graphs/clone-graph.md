# 6.2 Clone Graph

> **Source:** [`src/main/kotlin/graph/CloneGraph.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/CloneGraph.kt)
> **Pattern:** DFS + memo map · **Core page**

## The Problem

Given a reference to a node in a connected undirected graph, return a **deep copy** of the whole graph. Each node has a unique integer value and a list of neighbors; the clone must have *new* node objects with identical structure (the same values and the same edges — but no shared nodes with the original).

- Constraints: $1 \le V \le 100$; the graph is connected and undirected.

## Examples

```
Input:  adjList = [[2,4],[1,3],[2,4],[1,3]]     (a square: 1-2-3-4-1)
Output: the same square, built from fresh node objects
        (check: clone[1].neighbors != original[1].neighbors, but values match)
```

## Intuition — clone with a memo, and memo *before* you recurse

A deep copy is a traversal where, instead of *visiting* a node, you *build* it. DFS visits every node; the question is how to avoid cloning the same original node twice — which would either loop forever (cycles!) or produce duplicated nodes.

The answer is a **map from original → clone**. Two roles at once:

1. **memo** — `map[original]` already exists → return it, don't rebuild;
2. **visited** — a node we've already cloned is a node we don't recurse into.

The critical ordering detail: **register the clone in the map *before* recursing into neighbors**, not after. In a cycle, node `A`'s neighbor list leads back to `A`; if the map entry for `A` is created only *after* its neighbors are processed, that recursive call re-enters `A` and you recurse forever. Registering first means the back-edge finds the clone instantly. (This is the exact mirror of the "claim the node before exploring" rule for `visited` sets.)

**Why DFS?** BFS works too (same map, queue instead of stack), but DFS mirrors the structure most directly — "clone me, then clone my neighbors" is a recursive sentence, so the recursion is the translation.

## Approach 1 — Recursive DFS with a clone map (the repo's version, optimal)

```kotlin
data class Node(val `val`: Int, val neighbors: MutableList<Node?> = mutableListOf())

class CloneGraph {
    val map = mutableMapOf<Node, Node>()          // original -> clone (memo + visited)

    /**
     * @param node an original graph node (any node of the graph)
     * @return     the deep copy of the graph
     */
    fun cloneGraph(node: Node?): Node? {
        if (node == null) return null
        map[node]?.let { return it }              // already cloned -> hand back the clone

        val clonedNode = Node(node.`val`).also { map[node] = it }   // register FIRST
        node.neighbors.forEach { clonedNode.neighbors.add(cloneGraph(it)) }

        return clonedNode
    }
}
```

```java
import java.util.*;

public class CloneGraph {
    private Map<Node, Node> map = new HashMap<>();

    /**
     * @param node an original graph node (any node of the graph)
     * @return     the deep copy of the graph
     */
    public Node cloneGraph(Node node) {
        if (node == null) return null;
        if (map.containsKey(node)) return map.get(node);

        Node clone = new Node(node.val);          // register FIRST (breaks cycles)
        map.put(node, clone);

        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(cloneGraph(neighbor));
        }
        return clone;
    }

    static class Node {
        public int val;
        public List<Node> neighbors = new ArrayList<>();
        public Node(int val) { this.val = val; }
    }
}
```

```cpp
#include <unordered_map>
#include <vector>

class CloneGraph {
    std::unordered_map<Node*, Node*> map;

    /**
     * @param node an original graph node (any node of the graph)
     * @return     the deep copy of the graph
     */
    Node* cloneGraph(Node* node) {
        if (node == nullptr) return nullptr;
        if (map.count(node)) return map[node];

        Node* clone = new Node(node->val);        // register FIRST (breaks cycles)
        map[node] = clone;

        for (Node* neighbor : node->neighbors) {
            clone->neighbors.push_back(cloneGraph(neighbor));
        }
        return clone;
    }
};
```

```python
def clone_graph(node: Node | None) -> Node | None:
    """
    @param node: an original graph node (any node of the graph)
    @return:     the deep copy of the graph
    """
    clones: dict[Node, Node] = {}

    def dfs(original: Node | None) -> Node | None:
        if original is None:
            return None
        if original in clones:
            return clones[original]               # already cloned -> hand back the clone

        clone = Node(original.val)
        clones[original] = clone                  # register FIRST (breaks cycles)
        clone.neighbors = [dfs(n) for n in original.neighbors]
        return clone

    return dfs(node)
```

```rust
use std::cell::RefCell;
use std::collections::HashMap;
use std::rc::Rc;

impl Solution {
    /// @param node an original graph node (any node of the graph)
    /// @return     the deep copy of the graph
    pub fn clone_graph(node: Option<Rc<RefCell<Node>>>) -> Option<Rc<RefCell<Node>>> {
        let mut clones: HashMap<*const RefCell<Node>, Rc<RefCell<Node>>> = HashMap::new();

        fn dfs(
            node: Option<Rc<RefCell<Node>>>,
            clones: &mut HashMap<*const RefCell<Node>, Rc<RefCell<Node>>>,
        ) -> Option<Rc<RefCell<Node>>> {
            let original = node?;
            let raw = Rc::as_ptr(&original);
            if let Some(clone) = clones.get(&raw) {
                return Some(clone.clone());       // already cloned -> hand back the clone
            }

            let clone = Rc::new(RefCell::new(Node::new(original.borrow().val)));
            clones.insert(raw, clone.clone());    // register FIRST (breaks cycles)

            for neighbor in &original.borrow().neighbors {
                let n = neighbor.clone();
                clone.borrow_mut().neighbors.push(dfs(n, clones).unwrap());
            }
            Some(clone)
        }

        dfs(node, &mut clones)
    }
}
```

> **Rust note:** the map is keyed by raw pointer (`Rc::as_ptr`) because `Node` isn't `Hash`/`Eq`. The pointer is a stable identity for the duration of the clone — safe here since the originals outlive the map.

## Dry run

**Input:** the square `1-2-3-4-1` (adjacency: `1:[2,4]`, `2:[1,3]`, `3:[2,4]`, `4:[1,3]`).

```
cloneGraph(1):  map={}
  not in map -> create clone1, register: map={1:clone1}
  neighbor 2: cloneGraph(2)
    create clone2, register: map={1:clone1, 2:clone2}
    neighbor 1: cloneGraph(1) -> map hit! return clone1     (back-edge, no recursion)
    neighbor 3: cloneGraph(3)
      create clone3, register
      neighbor 2 -> map hit, return clone2
      neighbor 4 -> cloneGraph(4)
        create clone4, register
        neighbor 1 -> map hit, return clone1
        neighbor 3 -> map hit, return clone3
      -> clone4 complete
    -> clone3 complete: neighbors [clone2, clone4]
  -> clone2 complete: neighbors [clone1, clone3]
  neighbor 4: cloneGraph(4) -> map hit! return clone4
-> clone1 complete: neighbors [clone2, clone4]  ✓  (mirror of the original)
```

Every back-edge resolves through the map in $O(1)$; the recursion depth equals the DFS depth of the graph (up to $V$).

## Complexity

**Time.** Each node cloned once; each edge traversed twice (undirected):

$$
T(V, E) = O(V + E)
$$

**Space.** The map holds all $V$ clones; the recursion stack adds DFS depth:

$$
S(V, E) = O(V)
$$

## Variants & follow-ups

- **BFS version** — same `original -> clone` map, queue instead of recursion: pop a node, walk its neighbors, create clones for unseen ones, wire them up. Identical complexity; the register-before-enqueue rule plays the same cycle-breaking role.
- **Copy List With Random Pointer** (`src/main/kotlin/linkedlist/`) — the linked-list cousin: same "map original → copy, register before wiring" skeleton; the graph is a linked list plus one extra pointer.
- **Island-style flood fill** — DFS/BFS with a `visited` grid; the map's job is done by mutating the grid in place.
- **Interview follow-up:** "What breaks if we register after recursion?" The cycle `1↔2` would clone 1, clone 2, then hit the `1` back-edge with no map entry → recurse into 1 again → infinite loop / stack overflow. Saying this unprompted — that the *order* of registration is the entire bug surface — is the depth signal.
