# 5.19 Recover A Tree From Preorder

> **Source:** [`src/main/kotlin/tree/RecoverATreeFromPreOrderTraversal.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/RecoverATreeFromPreOrderTraversal.kt)
> **Pattern:** depth-guided reconstruction · **Core page**

## The Problem

Rebuild the binary tree from `traversal` — preorder with `-` counts marking depth (e.g. `"1-2--3--4-5--6--7"`).

- Constraints: n ≤ 1000.

## Examples

```
Input:  traversal = "1-2--3--4-5--6--7"
Output: root 1 with children 2 (leaves 3,4) and 5 (leaves 6,7)
```

## Intuition — read depth, then the value; recurse with an expected depth

The string is preorder with explicit depths. A shared index walks it; `buildTree(depth)` reads the next node *iff* its dash-count equals `depth`, else rewinds and returns null (the node belongs to the caller's sibling):

```kotlin
fun buildTree(depth: Int): TreeNode? {
    if (index >= traversal.length) return null

    var currentDepth = 0
    while (index < traversal.length && traversal[index] == '-') {
        currentDepth++
        index++
    }

    if (currentDepth != depth) {
        index -= currentDepth          // rewind: not our node
        return null
    }

    // read the value digits
    var value = 0
    while (index < traversal.length && traversal[index].isDigit()) {
        value = value * 10 + (traversal[index++] - '0')
    }

    val node = TreeNode(value)
    node.left = buildTree(depth + 1)   // children live one level deeper
    node.right = buildTree(depth + 1)
    return node
}
```

**Why the rewind?** Preorder lists `node, left-subtree, right-subtree` — when `buildTree(depth)` is called for a child but the next token is shallower, the token belongs to the caller's *right* sibling. The index rewind lets the caller re-read it — the [5.6](serialize-and-deserialize-binary-tree.md) deserializer's shared-index discipline.

**Why `depth + 1` for both children?** The dash-count is the depth — children are exactly one level deeper. The mismatch test (`currentDepth != depth`) is what stops the descent at the right boundary.

## Approach 1 — Stack-based iterative parse

Track (node, depth) on a stack: also correct, more bookkeeping.

## Approach 2 — Shared-index recursion (the repo's version, optimal)

```kotlin
class RecoverATreeFromPreOrderTraversal {
    private var index = 0

    /**
     * @param traversal preorder-with-dashes string
     * @return          the rebuilt tree root
     */
    fun recoverFromPreorder(traversal: String): TreeNode? {
        index = 0
        return buildTree(0)
    }

    private fun buildTree(depth: Int): TreeNode? {
        if (index >= traversal.length) return null

        var currentDepth = 0
        while (index < traversal.length && traversal[index] == '-') {
            currentDepth++
            index++
        }

        if (currentDepth != depth) {
            index -= currentDepth      // rewind: belongs to a sibling
            return null
        }

        var value = 0
        while (index < traversal.length && traversal[index].isDigit()) {
            value = value * 10 + (traversal[index++] - '0')
        }

        val node = TreeNode(value)
        node.left = buildTree(depth + 1)
        node.right = buildTree(depth + 1)
        return node
    }
}
```

```java
public class RecoverATreeFromPreorder {
    private String s;
    private int index = 0;

    private TreeNode build(int depth) {
        if (index >= s.length()) return null;

        int dashes = 0;
        while (index < s.length() && s.charAt(index) == '-') { dashes++; index++; }

        if (dashes != depth) {
            index -= dashes;                    // rewind
            return null;
        }

        int value = 0;
        while (index < s.length() && Character.isDigit(s.charAt(index))) {
            value = value * 10 + (s.charAt(index++) - '0');
        }

        TreeNode node = new TreeNode(value);
        node.left = build(depth + 1);
        node.right = build(depth + 1);
        return node;
    }

    /**
     * @param traversal preorder-with-dashes string
     * @return          the rebuilt tree root
     */
    public TreeNode recoverFromPreorder(String traversal) {
        s = traversal;
        index = 0;
        return build(0);
    }
}
```

```cpp
#include <string>

class RecoverATreeFromPreorder {
    std::string s;
    int index = 0;

    TreeNode* build(int depth) {
        if (index >= (int)s.size()) return nullptr;

        int dashes = 0;
        while (index < (int)s.size() && s[index] == '-') { dashes++; index++; }

        if (dashes != depth) {
            index -= dashes;                    // rewind
            return nullptr;
        }

        int value = 0;
        while (index < (int)s.size() && std::isdigit(s[index])) {
            value = value * 10 + (s[index++] - '0');
        }

        TreeNode* node = new TreeNode(value);
        node->left = build(depth + 1);
        node->right = build(depth + 1);
        return node;
    }

public:
    /**
     * @param traversal preorder-with-dashes string
     * @return          the rebuilt tree root
     */
    TreeNode* recoverFromPreorder(std::string traversal) {
        s = traversal;
        index = 0;
        return build(0);
    }
};
```

```python
def recover_from_preorder(traversal: str) -> Optional["TreeNode"]:
    """
    @param traversal: preorder-with-dashes string
    @return:          the rebuilt tree root
    """
    index = 0

    def build(depth: int):
        nonlocal index
        if index >= len(traversal):
            return None

        dashes = 0
        while index < len(traversal) and traversal[index] == "-":
            dashes += 1
            index += 1

        if dashes != depth:
            index -= dashes               # rewind
            return None

        value = 0
        while index < len(traversal) and traversal[index].isdigit():
            value = value * 10 + int(traversal[index])
            index += 1

        node = TreeNode(value)
        node.left = build(depth + 1)
        node.right = build(depth + 1)
        return node

    return build(0)
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param traversal preorder-with-dashes string
    /// @return          the rebuilt tree root
    pub fn recover_from_preorder(traversal: String) -> Option<Rc<RefCell<TreeNode>>> {
        let bytes: Vec<char> = traversal.chars().collect();
        let mut index = 0usize;

        fn build(bytes: &Vec<char>, index: &mut usize, depth: usize) -> Option<Rc<RefCell<TreeNode>>> {
            if *index >= bytes.len() { return None; }

            let mut dashes = 0;
            while *index < bytes.len() && bytes[*index] == '-' { dashes += 1; *index += 1; }

            if dashes != depth {
                *index -= dashes;               // rewind
                return None;
            }

            let mut value = 0;
            while *index < bytes.len() && bytes[*index].is_ascii_digit() {
                value = value * 10 + bytes[*index] as i32 - '0' as i32;
                *index += 1;
            }

            let node = Rc::new(RefCell::new(TreeNode::new(value)));
            node.borrow_mut().left = build(bytes, index, depth + 1);
            node.borrow_mut().right = build(bytes, index, depth + 1);
            Some(node)
        }

        build(&bytes, &mut index, 0)
    }
}
```

## Dry run

**Input:** `traversal = "1-2--3--4-5--6--7"`.

```
build(0): dashes=0 (value 1).  node 1.  left = build(1):
  build(1): dashes=1 (value 2).  node 2.  left = build(2):
    build(2): dashes=2 (value 3).  node 3.  children build(3): dashes=0 != 3 -> rewind, null x2.
  right = build(2): dashes=2 (value 4).  node 4.  children null.
  right of 1: build(1): dashes=1 (value 5).  node 5.  children: 6 (dashes=2), 7 (dashes=2).
```

The rewind is the mechanism: after node 3's subtree, `build(3)` reads `-4` (1 dash), sees `1 != 3`, rewinds 1 char — the caller `build(2)` re-reads it as its right child 4. The shared index makes the preorder's "next sibling" hand-off automatic.

## Complexity

**Time.** Each char consumed once (rewinds re-read only at boundaries):

$$
T(n) = O(n)
$$

**Space.** Recursion:

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **Construct From Preorder + Inorder** ([5.5](construct-binary-tree-from-preorder-and-inorder.md)) — the index-map reconstruction sibling.
- **Serialize And Deserialize** ([5.6](serialize-and-deserialize-binary-tree.md)) — the shared-index deserializer this page reuses.
- **Interview follow-up:** "Why must the index be shared and rewound?" The recursion's depth test is the *only* way to know where a subtree ends — the next token's dashes decide whether it belongs to this subtree or the caller's sibling. Rewinding hands the token back; without it, sibling boundaries would be lost.
