# 5.16 Delete Node In A BST

> **Source:** [`src/main/kotlin/tree/bst/DeleteNodeinABST.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bst/DeleteNodeinABST.kt)
> **Pattern:** BST search + successor splice · **Core page**

## The Problem

Delete `key` from a BST, keeping it a valid BST.

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  root = [5,3,6,2,4,null,7], key = 3
Output: [5,4,6,2,null,null,7]   (successor 4 takes 3's place)
```

## Intuition — search; then handle 0, 1, or 2 children

The BST search recurses left/right. At the target:

- **0 children** → return null (the parent drops it);
- **1 child** → return the child (it replaces the node);
- **2 children** → replace the value with the **inorder successor** (the min of the right subtree), then delete that successor from the right subtree.

```kotlin
fun deleteNode(root: TreeNode?, key: Int): TreeNode? = when {
    root == null -> null
    key < root.`val` -> root.apply { left = deleteNode(left, key) }
    key > root.`val` -> root.apply { right = deleteNode(right, key) }
    else -> when {
        root.left == null -> root.right
        root.right == null -> root.left
        else -> {
            root.`val` = minValue(root.right!!)      // successor value
            root.right = deleteNode(root.right, root.`val`)   // delete the successor
            root
        }
    }
}
```

**Why the successor and not just any replacement?** The successor (smallest in the right subtree) is > every left value and < every right value — the only safe swap. Deleting it from the right subtree recurses into the 0/1-child case.

**Why is recursion the whole design?** Each branch returns the (possibly new) subtree root; the parent's `left`/`right` assignment re-links — the [5.0](pattern-primer.md) "return the new root" contract.

## Approach 1 — Search + successor splice (the repo's version, optimal)

```kotlin
class DeleteNodeinABST {
    /**
     * @param root BST root
     * @param key  value to delete
     * @return     new root
     */
    fun deleteNode(root: TreeNode?, key: Int): TreeNode? {
        return when {
            root == null -> null
            key < root.`val` -> root.apply { left = deleteNode(left, key) }
            key > root.`val` -> root.apply { right = deleteNode(right, key) }
            else -> when {
                root.left == null -> root.right
                root.right == null -> root.left
                else -> {
                    root.`val` = minValue(root.right!!)
                    root.right = deleteNode(root.right, root.`val`)
                    root
                }
            }
        }
    }

    private fun minValue(node: TreeNode): Int {
        var current = node
        while (current.left != null) current = current.left!!
        return current.`val`
    }
}
```

```java
public class DeleteNodeInABST {
    /**
     * @param root BST root
     * @param key  value to delete
     * @return     new root
     */
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) return null;

        if (key < root.val) { root.left = deleteNode(root.left, key); return root; }
        if (key > root.val) { root.right = deleteNode(root.right, key); return root; }

        if (root.left == null) return root.right;
        if (root.right == null) return root.left;

        root.val = minValue(root.right);                    // successor
        root.right = deleteNode(root.right, root.val);      // delete the successor
        return root;
    }

    private int minValue(TreeNode node) {
        while (node.left != null) node = node.left;
        return node.val;
    }
}
```

```cpp
class DeleteNodeInABST {
    int minValue(TreeNode* node) {
        while (node->left) node = node->left;
        return node->val;
    }

public:
    /**
     * @param root BST root
     * @param key  value to delete
     * @return     new root
     */
    TreeNode* deleteNode(TreeNode* root, int key) {
        if (!root) return nullptr;

        if (key < root->val) { root->left = deleteNode(root->left, key); return root; }
        if (key > root->val) { root->right = deleteNode(root->right, key); return root; }

        if (!root->left) return root->right;
        if (!root->right) return root->left;

        root->val = minValue(root->right);                  // successor
        root->right = deleteNode(root->right, root->val);   // delete the successor
        return root;
    }
};
```

```python
def delete_node(root: Optional["TreeNode"], key: int) -> Optional["TreeNode"]:
    """
    @param root: BST root
    @param key:  value to delete
    @return:     new root
    """
    if not root:
        return None

    if key < root.val:
        root.left = delete_node(root.left, key)
        return root
    if key > root.val:
        root.right = delete_node(root.right, key)
        return root

    if not root.left:
        return root.right
    if not root.right:
        return root.left

    def min_value(node):
        while node.left:
            node = node.left
        return node.val

    root.val = min_value(root.right)                 # successor
    root.right = delete_node(root.right, root.val)   # delete the successor
    return root
```

```rust
impl Solution {
    /// @param root BST root
    /// @param key  value to delete
    /// @return     new root
    pub fn delete_node(root: Option<Rc<RefCell<TreeNode>>>, key: i32) -> Option<Rc<RefCell<TreeNode>>> {
        fn min_value(mut node: Rc<RefCell<TreeNode>>) -> i32 {
            while node.borrow().left.is_some() {
                let left = node.borrow().left.clone().unwrap();
                node = left;
            }
            node.borrow().val
        }

        let root = match root {
            None => return None,
            Some(r) => r,
        };

        if key < root.borrow().val {
            let left = root.borrow().left.clone();
            root.borrow_mut().left = Self::delete_node(left, key);
        } else if key > root.borrow().val {
            let right = root.borrow().right.clone();
            root.borrow_mut().right = Self::delete_node(right, key);
        } else if root.borrow().left.is_none() {
            return root.borrow().right.clone();
        } else if root.borrow().right.is_none() {
            return root.borrow().left.clone();
        } else {
            let v = min_value(root.borrow().right.clone().unwrap());
            let right = root.borrow().right.clone();
            root.borrow_mut().val = v;
            root.borrow_mut().right = Self::delete_node(right, v);
        }
        Some(root)
    }
}
```

## Dry run

**Input:** `root = [5,3,6,2,4,null,7]`, `key = 3`.

```
deleteNode(5, 3): 3 < 5 -> left = deleteNode(3, 3)
  deleteNode(3, 3): found.  two children:
    minValue(4-subtree) = 4.  root.val = 4.
    right = deleteNode(4, 4): found, left null -> return right (null).
    3's node now: val 4, left 2, right null.  return it.
left = 4-node.  return 5-node.

Output: [5,4,6,2,null,null,7] ✓
```

The successor (4) moves up into 3's place; the original 4 node (a leaf) is deleted by the recursive call. A leaf deletion returns null; a one-child deletion returns the child — both propagate up through the `left`/`right =` assignments.

## Complexity

**Time.** Height-bound search + successor walk:

$$
T(n) = O(h)
$$

**Space.** Recursion:

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **Insert Into A BST** — the simpler insert twin.
- **Validate BST / Recover BST** ([5.11](recover-binary-search-tree.md)) — the BST-property family.
- **Interview follow-up:** "Why the successor instead of the predecessor?" Both work — the successor (right-subtree min) is > all left values and ≤ all right values. The choice is arbitrary; the two-child case *must* replace with one of them to preserve the BST property, and the recursive delete keeps the structure intact.
