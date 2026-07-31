# 18.19 Convert BST To Sorted Doubly Linked List

> **Source**: [`src/main/kotlin/tree/bst/ConvertBInarySearchTreeToSortedDoublyLinkedList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bst/ConvertBInarySearchTreeToSortedDoublyLinkedList.kt)
> **Pattern**: inorder threading · **Core page**

## The Problem

Convert a BST into a **sorted circular doubly-linked list** in place (left = prev, right = next).

- Constraints: n ≤ 10⁴; must be in-place (no new nodes).

## Examples

```
Input:  root = [4,2,5,1,3]
Output: the circular list 1 <-> 2 <-> 3 <-> 4 <-> 5 (head = 1)
```

## Intuition — inorder visits sorted; thread prev/next as you go

The inorder walk ([5.7](../ch05-trees/binary-tree-inorder-traversal-iterative.md)) visits values ascending. During the walk, link each node to the *previous* one; after the walk, close the circle:

```kotlin
var first: Node? = null
var last: Node? = null

fun dfs(node: Node?) {
    if (node == null) return

    dfs(node.left)                     // inorder: left first

    if (last != null) {
        last.right = node              // forward link
        node.left = last               // backward link
    } else {
        first = node                   // the smallest
    }
    last = node

    dfs(node.right)
}

// close the circle
first.left = last
last.right = first
```

**Why is inorder the sorted order?** A BST's inorder = ascending values — threading during the walk produces the sorted list with no extra work. The `left`/`right` pointers become `prev`/`next` — the conversion is a relabeling of the existing pointers.

## Approach 1 — Collect nodes, rewire (O(n) space)

Inorder into a list, link: correct, violates in-place.

## Approach 2 — Inorder threading (the repo's version, optimal)

```kotlin
class ConvertBInarySearchTreeToSortedDoublyLinkedList {
    class Node(var `val`: Int) {
        var left: Node? = null
        var right: Node? = null
    }

    /**
     * @param root BST root
     * @return     head of the sorted circular doubly-linked list
     */
    fun treeToDoublyList(root: Node?): Node? {
        if (root == null) return null

        var first: Node? = null
        var last: Node? = null

        fun dfs(node: Node?) {
            if (node == null) return

            dfs(node.left)

            if (last != null) {
                last!!.right = node
                node.left = last
            } else {
                first = node
            }
            last = node

            dfs(node.right)
        }

        dfs(root)

        first?.left = last
        last?.right = first
        return first
    }
}
```

```java
public class ConvertBSTToSortedDoublyLinkedList {
    static class Node {
        int val;
        Node left, right;
        Node(int v) { val = v; }
    }

    private Node first = null, last = null;

    private void dfs(Node node) {
        if (node == null) return;

        dfs(node.left);

        if (last != null) {
            last.right = node;
            node.left = last;
        } else {
            first = node;
        }
        last = node;

        dfs(node.right);
    }

    /**
     * @param root BST root
     * @return     head of the sorted circular doubly-linked list
     */
    public Node treeToDoublyList(Node root) {
        if (root == null) return null;
        first = last = null;

        dfs(root);

        first.left = last;
        last.right = first;
        return first;
    }
}
```

```cpp
class ConvertBSTToSortedDoublyLinkedList {
    Node* first = nullptr;
    Node* last = nullptr;

    void dfs(Node* node) {
        if (!node) return;

        dfs(node->left);

        if (last) {
            last->right = node;
            node->left = last;
        } else {
            first = node;
        }
        last = node;

        dfs(node->right);
    }

public:
    /**
     * @param root BST root
     * @return     head of the sorted circular doubly-linked list
     */
    Node* treeToDoublyList(Node* root) {
        if (!root) return nullptr;
        first = last = nullptr;

        dfs(root);

        first->left = last;
        last->right = first;
        return first;
    }
};
```

```python
def tree_to_doubly_list(root: "Optional[Node]") -> "Optional[Node]":
    """
    @param root: BST root
    @return:     head of the sorted circular doubly-linked list
    """
    if not root:
        return None

    first = last = None

    def dfs(node):
        nonlocal first, last
        if not node:
            return

        dfs(node.left)

        if last:
            last.right = node
            node.left = last
        else:
            first = node
        last = node

        dfs(node.right)

    dfs(root)

    first.left = last
    last.right = first
    return first
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root BST root
    /// @return     head of the sorted circular doubly-linked list
    pub fn tree_to_doubly_list(root: Option<Rc<RefCell<Node>>>) -> Option<Rc<RefCell<Node>>> {
        if root.is_none() { return None; }

        let mut first: Option<Rc<RefCell<Node>>> = None;
        let mut last: Option<Rc<RefCell<Node>>> = None;

        fn dfs(node: Option<Rc<RefCell<Node>>>, first: &mut Option<Rc<RefCell<Node>>>,
               last: &mut Option<Rc<RefCell<Node>>>) {
            if let Some(n) = node {
                dfs(n.borrow().left.clone(), first, last);

                if let Some(l) = last.clone() {
                    l.borrow_mut().right = Some(n.clone());
                    n.borrow_mut().left = Some(l);
                } else {
                    *first = Some(n.clone());
                }
                *last = Some(n.clone());

                dfs(n.borrow().right.clone(), first, last);
            }
        }

        dfs(root.clone(), &mut first, &mut last);

        if let (Some(f), Some(l)) = (first.clone(), last.clone()) {
            f.borrow_mut().left = Some(l.clone());
            l.borrow_mut().right = Some(f);
        }
        first
    }
}
```

## Dry run

**Input:** `root = [4,2,5,1,3]`.

```
inorder: 1, 2, 3, 4, 5
dfs(1): first = 1.  last = 1.
dfs(2): last(1).right = 2.  2.left = 1.  last = 2.
dfs(3): 2.right = 3.  3.left = 2.  last = 3.
dfs(4): 3.right = 4.  4.left = 3.  last = 4.
dfs(5): 4.right = 5.  5.left = 4.  last = 5.

close: 1.left = 5.  5.right = 1.

Output: head 1: 1 <-> 2 <-> 3 <-> 4 <-> 5 (circular) ✓
```

The inorder visit order *is* the list order — each step threads one `prev`/`next` pair, and the `first`/`last` bookends close the circle. No new nodes: the BST's `left`/`right` become the list's `prev`/`next`.

## Complexity

**Time.** One inorder walk:

$$
T(n) = O(n)
$$

**Space.** Recursion (or O(1) with Morris):

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **Binary Tree Inorder Traversal** ([5.7](../ch05-trees/binary-tree-inorder-traversal-iterative.md)) — the walk this page threads.
- **Interview follow-up:** "Why does this need no new nodes?" The BST's two pointers per node are exactly the DLL's two pointers — the conversion relabels them during the sorted walk. The `first`/`last` bookkeeping is the only extra state.
