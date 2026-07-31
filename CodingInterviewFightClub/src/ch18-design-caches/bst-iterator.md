# 18.12 BST Iterator

> **Source:** [`src/main/kotlin/tree/bst/BSTIterator.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bst/BSTIterator.kt)
> **Pattern:** iterative inorder with a left-spine stack · **Core page**

## The Problem

`next()` returns the next smallest BST value; `hasNext()` — both O(1) amortized, O(h) space.

- Constraints: n ≤ 10⁵.

## Examples

```
["BSTIterator","next","next","hasNext","next","hasNext","next","hasNext","next","hasNext"]
[[[7,3,15,null,null,9,20]],[],[],[],[],[],[],[],[],[]]
-> [null,3,7,true,9,true,15,true,20,false]
```

## Intuition — the [5.7](../ch05-trees/binary-tree-inorder-traversal-iterative.md) traversal as a stateful iterator

Inorder = left, node, right. The iterative version pushes the **left spine** onto a stack; `next()` pops the top (the next smallest), and pushes the popped node's **right subtree's left spine** — the [5.7](../ch05-trees/binary-tree-inorder-traversal-iterative.md) loop, split into constructor + next:

```kotlin
class BSTIterator(root: TreeNode?) {
    private val stack = ArrayDeque<TreeNode>()

    init { pushAllLeftNodes(root) }

    private fun pushAllLeftNodes(node: TreeNode?) {
        var current = node
        while (current != null) {
            stack.addFirst(current)
            current = current.left
        }
    }

    fun next(): Int {
        val node = stack.removeFirst()
        pushAllLeftNodes(node.right)      // the right subtree's left spine
        return node.`val`
    }

    fun hasNext(): Boolean = stack.isNotEmpty()
}
```

**Why does this produce inorder?** The stack holds the left spine — the next smallest is always on top (leftmost unvisited). After visiting a node, its right subtree's left spine joins the stack, preserving the order. Each node pushed once, popped once → O(1) amortized.

**Why O(h) space?** The stack holds at most one root-to-leaf spine (the current left frontier) — height h, not n. The [5.7](../ch05-trees/binary-tree-inorder-traversal-iterative.md) space story, now as a streaming API.

## Approach 1 — Collect all values, index them (O(n) space)

Inorder traversal into a list, `next` = pointer: correct, violates the O(h) constraint.

## Approach 2 — Left-spine stack iterator (the repo's version, optimal)

```kotlin
class BSTIterator(root: TreeNode?) {
    private val stack = ArrayDeque<TreeNode>()

    init {
        pushAllLeftNodes(root)
    }

    private fun pushAllLeftNodes(node: TreeNode?) {
        var current = node
        while (current != null) {
            stack.addFirst(current)
            current = current.left
        }
    }

    /**
     * @return the next smallest value
     */
    fun next(): Int {
        val node = stack.removeFirst()
        pushAllLeftNodes(node.right)
        return node.`val`
    }

    /**
     * @return true iff a next value exists
     */
    fun hasNext(): Boolean = stack.isNotEmpty()
}
```

```java
import java.util.*;

public class BSTIterator {
    private final Deque<TreeNode> stack = new ArrayDeque<>();

    public BSTIterator(TreeNode root) {
        pushLeft(root);
    }

    private void pushLeft(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }

    /**
     * @return the next smallest value
     */
    public int next() {
        TreeNode node = stack.pop();
        pushLeft(node.right);           // the right subtree's left spine
        return node.val;
    }

    /**
     * @return true iff a next value exists
     */
    public boolean hasNext() {
        return !stack.isEmpty();
    }
}
```

```cpp
#include <stack>

class BSTIterator {
    std::stack<TreeNode*> stack;

    void pushLeft(TreeNode* node) {
        while (node) {
            stack.push(node);
            node = node->left;
        }
    }

public:
    BSTIterator(TreeNode* root) { pushLeft(root); }

    /**
     * @return the next smallest value
     */
    int next() {
        TreeNode* node = stack.top(); stack.pop();
        pushLeft(node->right);          // the right subtree's left spine
        return node->val;
    }

    /**
     * @return true iff a next value exists
     */
    bool hasNext() {
        return !stack.empty();
    }
};
```

```python
class BSTIterator:
    """left-spine stack iterator"""

    def __init__(self, root: Optional["TreeNode"]):
        self.stack = []
        self._push_left(root)

    def _push_left(self, node):
        while node:
            self.stack.append(node)
            node = node.left

    def next(self) -> int:
        node = self.stack.pop()
        self._push_left(node.right)     # the right subtree's left spine
        return node.val

    def has_next(self) -> bool:
        return bool(self.stack)
```

```rust
use std::cell::RefCell;
use std::rc::Rc;

struct BSTIterator {
    stack: Vec<Rc<RefCell<TreeNode>>>,
}

impl BSTIterator {
    fn new(root: Option<Rc<RefCell<TreeNode>>>) -> Self {
        let mut it = Self { stack: Vec::new() };
        it.push_left(root);
        it
    }

    fn push_left(&mut self, mut node: Option<Rc<RefCell<TreeNode>>>) {
        while let Some(n) = node {
            self.stack.push(n.clone());
            node = n.borrow().left.clone();
        }
    }

    /// @return the next smallest value
    fn next(&mut self) -> i32 {
        let node = self.stack.pop().unwrap();
        self.push_left(node.borrow().right.clone());   // the right subtree's left spine
        node.borrow().val
    }

    /// @return true iff a next value exists
    fn has_next(&self) -> bool {
        !self.stack.is_empty()
    }
}
```

## Dry run

**Input:** `root = [7,3,15,null,null,9,20]`.

```
init: pushLeft(7): stack [7,3].

next(): pop 3.  pushLeft(3.right = null).  return 3 ✓
next(): pop 7.  pushLeft(7.right = 15): stack [15, 9].  return 7 ✓
hasNext(): true ✓
next(): pop 9.  pushLeft(null).  return 9 ✓
next(): pop 15.  pushLeft(15.right = 20): stack [20].  return 15 ✓
next(): pop 20.  pushLeft(null).  return 20 ✓
hasNext(): false ✓
```

The pop-then-push-right rhythm is the iterator's heartbeat: each `next` takes the leftmost unvisited node, and the right subtree's left spine replenishes the stack — the [5.7](../ch05-trees/binary-tree-inorder-traversal-iterative.md) traversal split into a stateful machine. Order emitted: 3, 7, 9, 15, 20 — perfect inorder.

## Complexity

**Time.** Amortized O(1) per op (each node pushed once, popped once):

$$
T = O(1) \text{ amortized}
$$

**Space.** The left-spine stack:

$$
S = O(h)
$$

## Variants & follow-ups

- **Binary Tree Inorder Traversal** ([5.7](../ch05-trees/binary-tree-inorder-traversal-iterative.md)) — the one-shot traversal this page streams.
- **Peeking Iterator** ([18.3](peeking-iterator.md)) — the iterator-decorator family.
- **Interview follow-up:** "Why is `hasNext` O(1) with no lookahead?" The stack's emptiness IS the answer — the next smallest node is always the top, and the invariant "stack holds the left spine of the unvisited frontier" never needs a peek. The O(1)-amortized argument is the push-once/pop-once accounting.
