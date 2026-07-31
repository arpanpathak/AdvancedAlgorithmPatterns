# 4.10 Copy List With Random Pointer

> **Source:** [`src/main/kotlin/linkedlist/CopyLinkedListWithRandomPointer.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/CopyLinkedListWithRandomPointer.kt)
> **Pattern:** node-map deep copy · **Core page**

## The Problem

Deep-copy a linked list whose nodes also have a `random` pointer (to any node or null).

- Constraints: n ≤ 1000.

## Examples

```
Input:  head = [[7,null],[13,0],[11,4],[10,2],[1,0]]   (val, random-index)
Output: the same structure with brand-new nodes
```

## Intuition — map old node → new node, then wire both pointers in one pass

The `random` pointer can point *forward* — you can't know the target's clone until it exists. So first create all clones, recording `old → new` in a map; then a second pass wires `next` and `random` via the map:

```kotlin
val nodeMap = mutableMapOf<Node, Node>()

// pass 1: clone every node, map old -> new
var curr = head
while (curr != null) {
    nodeMap[curr] = Node(curr.`val`)
    curr = curr.next
}

// pass 2: wire the pointers through the map
curr = head
while (curr != null) {
    nodeMap[curr]?.next = curr.next?.let { nodeMap[it] }
    nodeMap[curr]?.random = curr.random?.let { nodeMap[it] }
    curr = curr.next
}

return nodeMap[head]
```

**Why two passes?** The clone of a `random` target may not exist yet when cloning in one pass — the map defers the wiring until every clone exists. The [6.2](../ch06-graphs/clone-graph.md) clone-graph pattern: memo-before-recurse, here memo-before-wire.

**Why the map and not a per-node field?** The `old → new` map is the [10.x](../ch10-hash-tables/pattern-primer.md) look-up contract — O(1) per pointer. (The O(1)-space variant interleaves clones `old.next = new, new.next = old.next` and unweaves after — the map version is the readable answer.)

## Approach 1 — Interleaved cloning (O(1) space)

Insert each clone after its original, wire, then unweave: the space-optimal upgrade.

## Approach 2 — Node map (the repo's version)

```kotlin
class CopyLinkedListWithRandomPointer {
    class Node(var `val`: Int) {
        var next: Node? = null
        var random: Node? = null
    }

    /**
     * @param node list head
     * @return     deep copy with all pointers mirrored
     */
    fun copyRandomList(node: Node?): Node? {
        if (node == null) return null

        val nodeMap = mutableMapOf<Node, Node>()

        var curr = node
        while (curr != null) {
            nodeMap[curr] = Node(curr.`val`)
            curr = curr.next
        }

        curr = node
        while (curr != null) {
            nodeMap[curr]?.next = curr.next?.let { nodeMap[it] }
            nodeMap[curr]?.random = curr.random?.let { nodeMap[it] }
            curr = curr.next
        }
        return nodeMap[node]
    }
}
```

```java
import java.util.*;

public class CopyListWithRandomPointer {
    static class Node {
        int val;
        Node next, random;
        Node(int v) { val = v; }
    }

    /**
     * @param head list head
     * @return     deep copy with all pointers mirrored
     */
    public Node copyRandomList(Node head) {
        if (head == null) return null;

        Map<Node, Node> map = new HashMap<>();

        for (Node cur = head; cur != null; cur = cur.next) {
            map.put(cur, new Node(cur.val));
        }

        for (Node cur = head; cur != null; cur = cur.next) {
            map.get(cur).next = map.get(cur.next);
            map.get(cur).random = map.get(cur.random);
        }
        return map.get(head);
    }
}
```

```cpp
#include <unordered_map>

class CopyListWithRandomPointer {
    struct Node {
        int val;
        Node* next;
        Node* random;
        Node(int v) : val(v), next(nullptr), random(nullptr) {}
    };

public:
    /**
     * @param head list head
     * @return     deep copy with all pointers mirrored
     */
    Node* copyRandomList(Node* head) {
        if (!head) return nullptr;

        std::unordered_map<Node*, Node*> map;

        for (Node* cur = head; cur; cur = cur->next) {
            map[cur] = new Node(cur->val);
        }
        for (Node* cur = head; cur; cur = cur->next) {
            map[cur]->next = map[cur->next];
            map[cur]->random = map[cur->random];
        }
        return map[head];
    }
};
```

```python
def copy_random_list(head: "Optional[Node]") -> "Optional[Node]":
    """
    @param head: list head
    @return:     deep copy with all pointers mirrored
    """
    if not head:
        return None

    node_map = {}
    cur = head
    while cur:
        node_map[cur] = Node(cur.val)
        cur = cur.next

    cur = head
    while cur:
        node_map[cur].next = node_map.get(cur.next)
        node_map[cur].random = node_map.get(cur.random)
        cur = cur.next

    return node_map[head]
```

```rust
use std::collections::HashMap;
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param head list head
    /// @return     deep copy with all pointers mirrored
    pub fn copy_random_list(head: Option<Rc<RefCell<Node>>>) -> Option<Rc<RefCell<Node>>> {
        let mut map: HashMap<i32, Rc<RefCell<Node>>> = HashMap::new();
        let mut cur = head.clone();
        while let Some(n) = cur.clone() {
            let id = n.borrow().val;
            map.insert(id, Rc::new(RefCell::new(Node::new(id))));
            cur = n.borrow().next.clone();
        }

        cur = head;
        while let Some(n) = cur.clone() {
            let id = n.borrow().val;
            let clone = map.get(&id).unwrap();
            if let Some(nx) = n.borrow().next.clone() {
                clone.borrow_mut().next = map.get(&nx.borrow().val).cloned();
            }
            if let Some(r) = n.borrow().random.clone() {
                clone.borrow_mut().random = map.get(&r.borrow().val).cloned();
            }
            cur = n.borrow().next.clone();
        }
        head.clone().map(|h| map.get(&h.borrow().val).unwrap().clone())
    }
}
```

## Dry run

**Input:** `head = 7 → 13 → 11 → 10 → 1` with `random: 13→0, 11→4, 10→2, 1→0, 7→null`.

```
pass 1: map = {7:new7, 13:new13, 11:new11, 10:new10, 1:new1}   (val-only clones)

pass 2 (wiring through the map):
  7:  next = map[13] = new13.  random = null.
  13: next = map[11] = new11.  random = map[7] = new7.
  11: next = map[10].          random = map[1] = new1.
  10: next = map[1].           random = map[11] = new11.
  1:  next = null.             random = map[7] = new7.

Output: a parallel structure where every new pointer hits a new node ✓
```

The map's role is the deferral: `11.random` points to node `1`, which sits *after* it — impossible to wire in a single forward pass. The two-phase (clone-all, then wire) makes forward pointers legal; the map gives O(1) resolution. `map.get(cur.next)` handles nulls naturally.

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** The node map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Clone Graph** ([6.2](../ch06-graphs/clone-graph.md)) — the same memo-before-wire idea on graphs.
- **Interview follow-up:** "How would you make it O(1) space?" Interleave: for each old node insert `new` right after it (`old.next = new; new.next = old.next`), wire `new.random = old.random.next`, then unweave into two lists. The map version is 3× clearer; the interleave is the space-constrained upgrade — name both.
