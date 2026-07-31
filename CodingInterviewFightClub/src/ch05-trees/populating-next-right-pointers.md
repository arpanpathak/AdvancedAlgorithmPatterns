# 5.15 Populating Next Right Pointers In Each Node II

> **Source:** [`src/main/kotlin/tree/PopulateNextRightPointersInEachNode_II_Constant.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/PopulateNextRightPointersInEachNode_II_Constant.kt) (+ `PopulateNextRightPointersInEachNode_II.kt` — the queue version)
> **Pattern:** level-linking with O(1) space · **Core page**

## The Problem

Fill each node's `next` pointer to its **right neighbor** in the same level (a general binary tree — not perfect).

- Constraints: n ≤ 6000; must be O(1) space (no queue).

## Examples

```
Input:  root = [1,2,3,4,5,null,7]
Output: the tree with next pointers: 1->null, 2->3, 3->null, 4->5, 5->7, 7->null
```

## Intuition — walk a level, link the *next* level as you go

The constant-space trick: while traversing level L with `node`, build level L+1's links — `prev` threads the children, `nextLevelStart` remembers the first node of L+1 for the next outer loop:

```kotlin
var current: Node? = root

while (current != null) {
    var nextLevelStart: Node? = null
    var prev: Node? = null
    var node = current

    while (node != null) {                    // walk level L
        if (node.left != null) {
            if (prev != null) prev.next = node.left
            prev = node.left
            if (nextLevelStart == null) nextLevelStart = node.left
        }
        if (node.right != null) {             // same threading for the right child
            if (prev != null) prev.next = node.right
            prev = node.right
            if (nextLevelStart == null) nextLevelStart = node.right
        }
        node = node.next                      // level L's own links
    }
    current = nextLevelStart                  // descend to L+1
}
```

**Why no queue?** Level L's `next` pointers are already wired (built by the previous outer iteration) — `node.next` walks the level for free. The [5.2](binary-tree-level-order-traversal.md) fence's queue is replaced by the links themselves.

**Why track `nextLevelStart`?** The outer loop needs the first node of the next level; the `prev`-threading only knows the *last* linked child. Two variables — `prev` (the tail being built) and `nextLevelStart` (the head to descend to) — are the whole state.

## Approach 1 — BFS queue (the `_II.kt` file)

Level-fenced BFS, link within each level: correct, O(n) space — the easy version.

## Approach 2 — O(1)-space level threading (the repo's constant version, optimal)

```kotlin
class PopulateNextRightPointersInEachNode_II_Constant {
    /**
     * @param root tree root
     * @return     root with next pointers filled
     */
    fun connect(root: Node?): Node? {
        var current: Node? = root

        while (current != null) {
            var nextLevelStart: Node? = null
            var prev: Node? = null
            var node = current

            while (node != null) {
                if (node.left != null) {
                    if (prev != null) prev.next = node.left
                    prev = node.left
                    if (nextLevelStart == null) nextLevelStart = node.left
                }
                if (node.right != null) {
                    if (prev != null) prev.next = node.right
                    prev = node.right
                    if (nextLevelStart == null) nextLevelStart = node.right
                }
                node = node.next
            }
            current = nextLevelStart
        }
        return root
    }
}
```

```java
public class PopulatingNextRightPointers {
    /**
     * @param root tree root
     * @return     root with next pointers filled
     */
    public Node connect(Node root) {
        Node current = root;

        while (current != null) {
            Node nextStart = null, prev = null;
            Node node = current;

            while (node != null) {
                if (node.left != null) {
                    if (prev != null) prev.next = node.left;
                    prev = node.left;
                    if (nextStart == null) nextStart = node.left;
                }
                if (node.right != null) {
                    if (prev != null) prev.next = node.right;
                    prev = node.right;
                    if (nextStart == null) nextStart = node.right;
                }
                node = node.next;
            }
            current = nextStart;
        }
        return root;
    }
}
```

```cpp
class PopulatingNextRightPointers {
public:
    /**
     * @param root tree root
     * @return     root with next pointers filled
     */
    Node* connect(Node* root) {
        Node* current = root;

        while (current) {
            Node* nextStart = nullptr;
            Node* prev = nullptr;
            Node* node = current;

            while (node) {
                if (node->left) {
                    if (prev) prev->next = node->left;
                    prev = node->left;
                    if (!nextStart) nextStart = node->left;
                }
                if (node->right) {
                    if (prev) prev->next = node->right;
                    prev = node->right;
                    if (!nextStart) nextStart = node->right;
                }
                node = node->next;
            }
            current = nextStart;
        }
        return root;
    }
};
```

```python
def connect(root: "Optional[Node]") -> "Optional[Node]":
    """
    @param root: tree root
    @return:     root with next pointers filled
    """
    current = root

    while current:
        next_start = None
        prev = None
        node = current

        while node:
            for child in (node.left, node.right):
                if child:
                    if prev:
                        prev.next = child
                    prev = child
                    if next_start is None:
                        next_start = child
            node = node.next

        current = next_start

    return root
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root tree root
    /// @return     root with next pointers filled
    pub fn connect(root: Option<Rc<RefCell<Node>>>) -> Option<Rc<RefCell<Node>>> {
        let mut current = root.clone();

        while let Some(cur_node) = current.clone() {
            let mut next_start: Option<Rc<RefCell<Node>>> = None;
            let mut prev: Option<Rc<RefCell<Node>>> = None;
            let mut node = Some(cur_node);

            while let Some(n) = node.clone() {
                for child in [n.borrow().left.clone(), n.borrow().right.clone()].into_iter().flatten() {
                    if let Some(p) = prev.clone() {
                        p.borrow_mut().next = Some(child.clone());
                    }
                    prev = Some(child.clone());
                    if next_start.is_none() {
                        next_start = Some(child.clone());
                    }
                }
                node = n.borrow().next.clone();
            }
            current = next_start;
        }
        root
    }
}
```

## Dry run

**Input:** `root = [1,2,3,4,5,null,7]`.

```
outer: current = 1
  inner (level 1): node=1: children 2, 3:
    prev=null -> 2 becomes prev, next_start=2.  2.next = 3.  prev=3, next_start stays 2.
  current = 2

outer: current = 2
  inner (level 2): node=2: children 4, 5: prev=4, next_start=4.  4.next=5.  prev=5.
                   node=3: child 7: 5.next=7.  prev=7.
  current = 4

outer: current = 4: level 3 has no children -> next_start = null -> exit.

Output: 1->null, 2->3, 3->null, 4->5, 5->7, 7->null ✓
```

The elegance: level 1's `next` pointers (`2.next = 3`) are built by the *outer* pass's `prev`-threading, then the *inner* pass walks them (`node = node.next`) to reach both 2 and 3 — which is how 5 links across to 7 (a non-sibling, via 3). The `nextLevelStart` hand-off is the level descent: no queue, no stack, O(1) space.

## Complexity

**Time.** Each node touched once per level-membership:

$$
T(n) = O(n)
$$

**Space.** Three pointers:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Binary Tree Level Order Traversal** ([5.2](binary-tree-level-order-traversal.md)) — the queue version this page replaces.
- **Interview follow-up:** "Why does this work on a *general* tree (Part II) when the original assumed perfect?" The prev-threading doesn't care about sibling structure — it links whatever children exist in scan order. The perfect-tree version can shortcut (`node.left.next = node.right`); the general one needs the threading, which is strictly more general.
