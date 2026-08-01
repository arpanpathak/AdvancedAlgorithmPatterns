# 5.30 Step-By-Step Directions From A Binary Tree Node To Another

> **Source**: [`src/main/kotlin/tree/StepByStepDirectionsFromANodeToAnother.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/StepByStepDirectionsFromANodeToAnother.kt)
> **Pattern**: LCA + path strings · **Core page**

## The Problem

The direction string (U, L, R) from `startValue` to `destValue` in a binary tree.

- Constraints: n ≤ 10⁵; values unique.

## Examples

```
Input:  root = [5,1,2,3,null,6,4], startValue = 3, destValue = 6
Output: "UURL"   (3 up to 1, up to 5, right to 2, left... wait: 3→1(U),1→5(U),5→2(R),2→6(L)? 2's left is 6 -> "UURL")
```

## Intuition — find the LCA; the path = U's up to the LCA + the L/R path down

Every tree path passes through the [5.3](lowest-common-ancestor.md). Find the LCA; walk from it to each target collecting directions; the start-side becomes U's:

```kotlin
fun findNode(root: TreeNode?, key: Int): TreeNode? = when {
    root == null -> null
    root.`val` == key -> root
    else -> findNode(root.left, key) ?: findNode(root.right, key)
}

fun lowestCommonAncestor(root: TreeNode?, p: TreeNode?, q: TreeNode?): TreeNode? {
    if (root == null || root === p || root === q) return root

    val left = lowestCommonAncestor(root.left, p, q)
    val right = lowestCommonAncestor(root.right, p, q)

    return when {
        left != null && right != null -> root
        left != null -> left
        else -> right
    }
}

// then: dfs from the LCA to start (collect "U" per edge) and to dest (collect L/R),
// concatenate.
```

**Why the LCA first?** The path uniquely decomposes: upward from start to the LCA, downward from the LCA to dest. Without the LCA, the up/down split is ambiguous.

## Approach 1 — LCA + two path walks (the repo's version, optimal)

```kotlin
class StepByStepDirectionsFromANodeToAnother {
    /**
     * @param root        tree root
     * @param startValue  start node value
     * @param destValue   destination node value
     * @return            U/L/R directions
     */
    fun getDirections(root: TreeNode?, startValue: Int, destValue: Int): String {
        val start = findNode(root, startValue)
        val dest = findNode(root, destValue)
        val lca = lowestCommonAncestor(root, start, dest)

        val startPath = StringBuilder()
        val destPath = StringBuilder()

        fun walk(node: TreeNode?, target: TreeNode?, sb: StringBuilder, up: Boolean): Boolean {
            if (node == null) return false
            if (node === target) return true

            if (walk(node.left, target, sb, up)) {
                sb.append(if (up) 'U' else 'L')
                return true
            }
            if (walk(node.right, target, sb, up)) {
                sb.append(if (up) 'U' else 'R')
                return true
            }
            return false
        }

        walk(lca, start, startPath, true)
        walk(lca, dest, destPath, false)

        return startPath.toString() + destPath.reverse().toString()
    }
}
```

```java
public class StepByStepDirections {
    private TreeNode findNode(TreeNode root, int key) {
        if (root == null || root.val == key) return root;
        return findNode(root.left, key) != null ? findNode(root.left, key) : findNode(root.right, key);
    }

    private TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;

        TreeNode left = lca(root.left, p, q);
        TreeNode right = lca(root.right, p, q);

        if (left != null && right != null) return root;
        return left != null ? left : right;
    }

    private boolean walk(TreeNode node, TreeNode target, StringBuilder sb, boolean up) {
        if (node == null) return false;
        if (node == target) return true;

        if (walk(node.left, target, sb, up)) { sb.append(up ? 'U' : 'L'); return true; }
        if (walk(node.right, target, sb, up)) { sb.append(up ? 'U' : 'R'); return true; }
        return false;
    }

    /**
     * @param root       tree root
     * @param startValue start node value
     * @param destValue  destination node value
     * @return           U/L/R directions
     */
    public String getDirections(TreeNode root, int startValue, int destValue) {
        TreeNode start = findNode(root, startValue);
        TreeNode dest = findNode(root, destValue);
        TreeNode ancestor = lca(root, start, dest);

        StringBuilder up = new StringBuilder();
        StringBuilder down = new StringBuilder();

        walk(ancestor, start, up, true);
        walk(ancestor, dest, down, false);

        return up.toString() + down.reverse().toString();
    }
}
```

```cpp
#include <string>

class StepByStepDirections {
    TreeNode* findNode(TreeNode* root, int key) {
        if (!root || root->val == key) return root;
        return findNode(root->left, key) ? findNode(root->left, key) : findNode(root->right, key);
    }

    TreeNode* lca(TreeNode* root, TreeNode* p, TreeNode* q) {
        if (!root || root == p || root == q) return root;

        TreeNode* left = lca(root->left, p, q);
        TreeNode* right = lca(root->right, p, q);

        if (left && right) return root;
        return left ? left : right;
    }

    bool walk(TreeNode* node, TreeNode* target, std::string& sb, bool up) {
        if (!node) return false;
        if (node == target) return true;

        if (walk(node->left, target, sb, up)) { sb += up ? 'U' : 'L'; return true; }
        if (walk(node->right, target, sb, up)) { sb += up ? 'U' : 'R'; return true; }
        return false;
    }

public:
    /**
     * @param root       tree root
     * @param startValue start node value
     * @param destValue  destination node value
     * @return           U/L/R directions
     */
    std::string getDirections(TreeNode* root, int startValue, int destValue) {
        TreeNode* start = findNode(root, startValue);
        TreeNode* dest = findNode(root, destValue);
        TreeNode* ancestor = lca(root, start, dest);

        std::string up, down;
        walk(ancestor, start, up, true);
        walk(ancestor, dest, down, false);

        std::reverse(down.begin(), down.end());
        return up + down;
    }
};
```

```python
def get_directions(root: Optional["TreeNode"], start_value: int, dest_value: int) -> str:
    """
    @param root:        tree root
    @param start_value: start node value
    @param dest_value:  destination node value
    @return:            U/L/R directions
    """
    def find_node(node, key):
        if not node or node.val == key:
            return node
        return find_node(node.left, key) or find_node(node.right, key)

    def lca(node, p, q):
        if not node or node is p or node is q:
            return node

        left = lca(node.left, p, q)
        right = lca(node.right, p, q)

        if left and right:
            return node
        return left or right

    def walk(node, target, sb, up):
        if not node:
            return False
        if node is target:
            return True

        if walk(node.left, target, sb, up):
            sb.append("U" if up else "L")
            return True
        if walk(node.right, target, sb, up):
            sb.append("U" if up else "R")
            return True
        return False

    start = find_node(root, start_value)
    dest = find_node(root, dest_value)
    ancestor = lca(root, start, dest)

    up, down = [], []
    walk(ancestor, start, up, True)
    walk(ancestor, dest, down, False)

    return "".join(up + down[::-1])
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param root        tree root
    /// @param start_value start node value
    /// @param dest_value  destination node value
    /// @return            U/L/R directions
    pub fn get_directions(root: Option<Rc<RefCell<TreeNode>>>, start_value: i32, dest_value: i32) -> String {
        fn find_node(node: &Option<Rc<RefCell<TreeNode>>>, key: i32) -> Option<Rc<RefCell<TreeNode>>> {
            if let Some(n) = node {
                if n.borrow().val == key { return Some(n.clone()); }
                if let Some(found) = find_node(&n.borrow().left, key) { return Some(found); }
                find_node(&n.borrow().right, key)
            } else { None }
        }

        fn lca(node: &Option<Rc<RefCell<TreeNode>>>, p: &Option<Rc<RefCell<TreeNode>>>, q: &Option<Rc<RefCell<TreeNode>>>) -> Option<Rc<RefCell<TreeNode>>> {
            match node {
                None => None,
                Some(n) => {
                    if let Some(np) = p { if Rc::ptr_eq(n, np) { return Some(n.clone()); } }
                    if let Some(nq) = q { if Rc::ptr_eq(n, nq) { return Some(n.clone()); } }

                    let left = lca(&n.borrow().left, p, q);
                    let right = lca(&n.borrow().right, p, q);

                    if left.is_some() && right.is_some() { Some(n.clone()) }
                    else if left.is_some() { left }
                    else { right }
                }
            }
        }

        fn walk(node: &Option<Rc<RefCell<TreeNode>>>, target: &Option<Rc<RefCell<TreeNode>>>,
                sb: &mut Vec<char>, up: bool) -> bool {
            match node {
                None => false,
                Some(n) => {
                    if let Some(t) = target { if Rc::ptr_eq(n, t) { return true; } }

                    if walk(&n.borrow().left, target, sb, up) {
                        sb.push(if up { 'U' } else { 'L' });
                        return true;
                    }
                    if walk(&n.borrow().right, target, sb, up) {
                        sb.push(if up { 'U' } else { 'R' });
                        return true;
                    }
                    false
                }
            }
        }

        let start = find_node(&root, start_value);
        let dest = find_node(&root, dest_value);
        let ancestor = lca(&root, &start, &dest);

        let mut up = Vec::new();
        let mut down = Vec::new();
        walk(&ancestor, &start, &mut up, true);
        walk(&ancestor, &dest, &mut down, false);

        down.reverse();
        up.into_iter().chain(down).collect()
    }
}
```

## Dry run

**Input:** the example.

```
lca(3, 6) = 1.  startPath: 3 → 1: "U".  destPath: 1 → 6: 1.left=2... wait 6 is 2's left child: 1→2 ("R"), 2→6 ("L") → down collected reversed: walk appends "L" then "R" -> reversed "RL"?
Hmm — the walk appends as it RETURNS, so destPath = "LR" (L from 2→6 first... the recursion reaches 6 via 2: walk(1): left=2: walk(2): left=6 found -> append 'L', return.  walk(1) left returned true -> append 'R'??  then destPath = "RL"? no:

walk(lca=1, dest=6, up=false): node 1: walk left (2): walk(2): walk left (6): found -> append 'L' -> true.  back at 2: append 'L'? no wait:
walk(2): left walk(6) returns true -> append('L') → "L".  return true.
walk(1): left walk(2) returned true -> append('R') → "LR".  return true.
destPath = "LR" (this is root→dest in reverse? no — it's dest→root!).  reversed -> "RL".
up = "U".  result = "U" + "RL" = "URL"?  But the expected is "UURL"... 

The example: start=3, dest=6.  3's parent is 1.  1's parent is 5.  6 is under 2 (5's right).  
LCA(3, 6): 3 is under 1, 6 is under 5... LCA = 5!  (not 1).  3→1→5 (up twice = "UU"), 5→2→6 ("RL").
result = "UU" + reverse("LR") = "UU" + "RL" = "UURL" ✓
```

## Complexity

**Time.** Three walks:

$$
T(n) = O(n)
$$

**Space.** Recursion:

$$
S(n) = O(h)
$$

## Variants & follow-ups

- **Lowest Common Ancestor** ([5.3](lowest-common-ancestor.md)) — the decomposition core.
- **Interview follow-up:** "Why reverse the down-path?" The walk appends edges *dest→LCA* (return order); the actual path is LCA→dest, so the directions must be reversed — the up-path is uniform 'U's and needs no reversal.
