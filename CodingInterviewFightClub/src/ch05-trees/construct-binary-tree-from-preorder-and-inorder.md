# 5.7 Construct Binary Tree From Preorder And Inorder

> **Source:** [`src/main/kotlin/tree/ConstructBinaryTreeFromPreorderAndInOrderTraversal.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/ConstructBinaryTreeFromPreorderAndInOrderTraversal.kt)
> **Pattern:** recursive range rebuild with an index map · **Core page**

## The Problem

Given the `preorder` and `inorder` traversals of a binary tree (distinct values), rebuild the tree.

- Constraints: $1 \le n \le 3000$; values distinct.

## Examples

```
preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
Output: 3 (left 9, right 20 (left 15, right 7))
```

## Intuition — the two traversals disagree in exactly the right way

The pair of traversals is self-describing:

- **preorder** gives the roots in order: `preorder[0]` is the root, then the root of the left subtree, etc. — the *preorder index always points at the next root*.
- **inorder** partitions each subtree: the root's index in `inorder` splits the array into the **left subtree range** and **right subtree range**.

The recursion: take the next preorder value as the root; look up its inorder index `mid`; recurse `build(left, mid - 1)` for the left child and `build(mid + 1, right)` for the right child. A shared `rootIndex` into preorder advances with every node built — no position math needed, because preorder visits nodes in exactly the order the recursion needs them.

**Why is the inorder index map the whole trick?** Without it, finding `mid` is an O(n) scan per node → O(n²). The repo precomputes `value -> inorder index` once — O(1) per lookup, O(n) total.

**Why "left subtree first"?** The recursion consumes preorder left-to-right: root, then all of the left subtree (its nodes are the next ones in preorder), then the right subtree. The `left > right` base case terminates each branch exactly when its range empties.

## Approach 1 — Re-scan inorder per node (O(n^2))

Linear search for `mid` at every level: correct, quadratic on skewed trees.

## Approach 2 — Precomputed index map + range recursion (the repo's version, optimal)

```kotlin
class ConstructBinaryTreeFromPreorderAndInOrderTraversal {
    private val rootIndices = mutableMapOf<Int, Int>()

    /**
     * @param preorder preorder traversal (roots first)
     * @param inorder  inorder traversal (left, root, right)
     * @return        the rebuilt tree
     */
    fun buildTree(preorder: IntArray, inorder: IntArray): TreeNode? {
        // Build the value -> inorder index map
        inorder.forEachIndexed { index, value -> rootIndices[value] = index }
        var rootIndex = 0

        fun buildTree(left: Int, right: Int): TreeNode? {
            return when {
                left > right -> null                       // empty range: no subtree
                else -> {
                    val rootVal = preorder[rootIndex++]    // next preorder value is this root
                    TreeNode(rootVal).apply {
                        this.left = buildTree(left, rootIndices[rootVal]!! - 1)
                        this.right = buildTree(rootIndices[rootVal]!! + 1, right)
                    }
                }
            }
        }
        return buildTree(0, preorder.size - 1)
    }
}
```

```java
import java.util.*;

public class ConstructBinaryTreeFromPreorderAndInorder {
    private final Map<Integer, Integer> index = new HashMap<>();
    private int rootIdx = 0;

    /**
     * @param preorder preorder traversal (roots first)
     * @param inorder  inorder traversal (left, root, right)
     * @return        the rebuilt tree
     */
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        for (int i = 0; i < inorder.length; i++) index.put(inorder[i], i);
        return build(preorder, 0, inorder.length - 1);
    }

    private TreeNode build(int[] preorder, int left, int right) {
        if (left > right) return null;                     // empty range: no subtree

        int rootVal = preorder[rootIdx++];                 // next preorder value is this root
        TreeNode node = new TreeNode(rootVal);
        int mid = index.get(rootVal);
        node.left = build(preorder, left, mid - 1);        // left subtree range
        node.right = build(preorder, mid + 1, right);      // right subtree range
        return node;
    }
}
```

```cpp
#include <unordered_map>
#include <vector>

class ConstructBinaryTreeFromPreorderAndInorder {
    std::unordered_map<int, int> index;
    int rootIdx = 0;

    TreeNode* build(const std::vector<int>& pre, int left, int right) {
        if (left > right) return nullptr;                  // empty range: no subtree

        int rootVal = pre[rootIdx++];                      // next preorder value is this root
        auto* node = new TreeNode(rootVal);
        int mid = index[rootVal];
        node->left = build(pre, left, mid - 1);            // left subtree range
        node->right = build(pre, mid + 1, right);          // right subtree range
        return node;
    }

public:
    /**
     * @param preorder preorder traversal (roots first)
     * @param inorder  inorder traversal (left, root, right)
     * @return        the rebuilt tree
     */
    TreeNode* buildTree(std::vector<int>& preorder, std::vector<int>& inorder) {
        for (int i = 0; i < (int)inorder.size(); i++) index[inorder[i]] = i;
        return build(preorder, 0, inorder.size() - 1);
    }
};
```

```python
def build_tree(preorder: list[int], inorder: list[int]) -> Optional["TreeNode"]:
    """
    @param preorder: preorder traversal (roots first)
    @param inorder:  inorder traversal (left, root, right)
    @return:         the rebuilt tree
    """
    index = {v: i for i, v in enumerate(inorder)}
    root_idx = 0

    def build(left: int, right: int) -> Optional["TreeNode"]:
        nonlocal root_idx
        if left > right:
            return None                          # empty range: no subtree

        root_val = preorder[root_idx]            # next preorder value is this root
        root_idx += 1
        mid = index[root_val]
        node = TreeNode(root_val)
        node.left = build(left, mid - 1)         # left subtree range
        node.right = build(mid + 1, right)       # right subtree range
        return node

    return build(0, len(preorder) - 1)
```

```rust
use std::cell::RefCell;
use std::collections::HashMap;
use std::rc::Rc;

impl Solution {
    /// @param preorder preorder traversal (roots first)
    /// @param inorder  inorder traversal (left, root, right)
    /// @return        the rebuilt tree
    pub fn build_tree(preorder: Vec<i32>, inorder: Vec<i32>) -> Option<Rc<RefCell<TreeNode>>> {
        let index: HashMap<i32, usize> = inorder.iter().enumerate()
            .map(|(i, &v)| (v, i)).collect();
        let mut root_idx = 0;

        fn build(pre: &Vec<i32>, index: &HashMap<i32, usize>, root_idx: &mut usize,
                 left: usize, right: usize) -> Option<Rc<RefCell<TreeNode>>> {
            if left > right { return None; }        // empty range: no subtree
            let root_val = pre[*root_idx];          // next preorder value is this root
            *root_idx += 1;
            let mid = index[&root_val];
            Some(Rc::new(RefCell::new(TreeNode {
                val: root_val,
                left: build(pre, index, root_idx, left, mid - 1),
                right: build(pre, index, root_idx, mid + 1, right),
            })))
        }

        build(&preorder, &index, &mut root_idx, 0, preorder.len() - 1)
    }
}
```

## Dry run

**Input:** `preorder = [3,9,20,15,7]`, `inorder = [9,3,15,20,7]`.

```
index map: {9:0, 3:1, 15:2, 20:3, 7:4}.  rootIndex = 0

build(0,4): rootVal = preorder[0] = 3.  rootIndex=1.  mid = 1.
  left  = build(0, 0): rootVal = preorder[1] = 9.  rootIndex=2.  mid=0.
           left = build(0,-1) -> null.  right = build(1,0) -> null.  -> node 9.
  right = build(2, 4): rootVal = preorder[2] = 20.  rootIndex=3.  mid=3.
           left  = build(2, 2): rootVal = preorder[3] = 15.  rootIndex=4.  mid=2.
                    left = build(2,1) null.  right = build(3,2) null.  -> node 15.
           right = build(4, 4): rootVal = preorder[4] = 7.  rootIndex=5.  mid=4.
                    left = build(4,3) null.  right = build(5,4) null.  -> node 7.

Tree:  3 (left 9, right 20 (left 15, right 7)) ✓
```

The shared `rootIndex` is the silent hero: it advances exactly once per node in preorder order, and the recursion's left-first consumption matches it perfectly — no index arithmetic on preorder is ever needed.

## Complexity

**Time.** Each node built once, O(1) index lookups:

$$
T(n) = O(n)
$$

**Space.** The index map + recursion depth:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Construct From Preorder And Postorder** (`tree/` variants) — postorder gives *roots last*; the same range split with the roles mirrored.
- **Serialize And Deserialize** ([5.5](serialize-and-deserialize-binary-tree.md)) — the reverse direction: a single traversal plus sentinels instead of two traversals.
- **Construct Binary Tree From String / From Preorder** (`tree/ConstructBinaryTreeFromString.kt`) — rebuild from a serialized string, the same recursion over a shared index.
- **Interview follow-up:** "Why must the values be distinct?" The `value -> inorder index` map is a bijection — duplicate values would make `mid` ambiguous and the left/right split ill-defined. Distinctness is the precondition that turns the two traversals into a unique tree.
