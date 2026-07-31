# 5.6 Binary Tree Inorder Traversal (Iterative)

> **Source:** [`src/main/kotlin/tree/BInaryTreeInOrderTraversalIterative.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/BInaryTreeInOrderTraversalIterative.kt)
> **Pattern:** explicit stack · **Core page**

## The Problem

Given the root of a binary tree, return the **in-order traversal** of its nodes' values — left subtree, node, right subtree — **without recursion**.

- Constraints: $0 \le n \le 100$ (LeetCode), but the *point* is trees large or skewed enough that the $O(h)$ call stack would be a real risk.

## Examples

```
Input:      1
             \
              2
             /
            3
Output: [1, 3, 2]

Input:  root = []    -> Output: []
Input:  root = [1]   -> Output: [1]
```

## Intuition — the call stack becomes an explicit stack

Recursion's in-order is deceptively simple: `inorder(node) = inorder(node.left); visit(node); inorder(node.right)`. The recursion *implicitly* uses the call stack to remember "I was here, now resume with the right subtree." The iterative version must build that same memory **by hand** — which is exactly why the interviewer asks.

The shape of the stack is the insight: you never visit a node when you first see it — you **push** it and keep going left. Only when you can't go left anymore do you **pop** and visit, then step right. In terms of the [traversal table](pattern-primer.md): in-order visits the left spine first, then the node, then the right spine — and the stack *is* the spine.

**The two-phase loop:** the outer `while (current != null || stack.isNotEmpty())` has two inner phases — a *descend* phase (push and go left) and a *visit* phase (pop, record, jump right). Each node is pushed exactly once and popped exactly once, so the whole walk is $O(n)$.

## Approach 1 — Recursion (baseline, for contrast)

```kotlin
fun inorder(root: TreeNode?): List<Int> = root?.let {
    inorder(it.left) + listOf(it.`val`) + inorder(it.right)
} ?: emptyList()
```

Two lines, obviously correct — and exactly why this problem exists: the *implicit* stack costs $O(h)$ real call-stack memory, which overflows on a skewed tree of ~$10^5$ nodes. The iterative version below is the same algorithm with the stack made explicit and moved to the heap.

## Approach 2 — Explicit-stack simulation (the repo's version, optimal)

```kotlin
class BInaryTreeInOrderTraversalIterative {
    /**
     * @param root the root of the binary tree
     * @return     the in-order (left, node, right) node values
     */
    fun inorderTraversal(root: TreeNode?): List<Int> {
        val stack = ArrayDeque<TreeNode>()
        val result = mutableListOf<Int>()
        var current = root

        while (current != null || stack.isNotEmpty()) {
            // Traverse to the leftmost node
            while (current != null) {
                stack.addLast(current)
                current = current.left
            }

            // Visit the node
            current = stack.removeLast()
            result.add(current.`val`)

            // Move to the right subtree
            current = current.right
        }
        return result
    }
}
```

```java
import java.util.*;

public class BinaryTreeInorderTraversalIterative {
    /**
     * @param root the root of the binary tree
     * @return     the in-order (left, node, right) node values
     */
    public List<Integer> inorderTraversal(TreeNode root) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        List<Integer> result = new ArrayList<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {            // descend the left spine
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();               // leftmost unvisited node
            result.add(current.val);             // visit
            current = current.right;             // now its right subtree
        }
        return result;
    }
}
```

```cpp
#include <vector>
#include <stack>

class BInaryTreeInOrderTraversalIterative {
public:
    /**
     * @param root the root of the binary tree
     * @return     the in-order (left, node, right) node values
     */
    std::vector<int> inorderTraversal(TreeNode* root) {
        std::vector<int> result;
        std::stack<TreeNode*> st;
        TreeNode* current = root;

        while (current != nullptr || !st.empty()) {
            while (current != nullptr) {         // descend the left spine
                st.push(current);
                current = current->left;
            }
            current = st.top();
            st.pop();
            result.push_back(current->val);      // visit
            current = current->right;            // now its right subtree
        }
        return result;
    }
};
```

```python
def inorder_traversal(root: TreeNode | None) -> list[int]:
    """
    @param root: the root of the binary tree
    @return:     the in-order (left, node, right) node values
    """
    stack: list[TreeNode] = []
    result: list[int] = []
    current = root

    while current is not None or stack:
        while current is not None:       # descend the left spine
            stack.append(current)
            current = current.left
        current = stack.pop()            # leftmost unvisited node
        result.append(current.val)       # visit
        current = current.right          # now its right subtree
    return result
```

```rust
impl Solution {
    /// @param root the root of the binary tree
    /// @return     the in-order (left, node, right) node values
    pub fn inorder_traversal(root: Option<Rc<RefCell<TreeNode>>>) -> Vec<i32> {
        let mut stack: Vec<Rc<RefCell<TreeNode>>> = Vec::new();
        let mut result = Vec::new();
        let mut current = root;

        while current.is_some() || !stack.is_empty() {
            while let Some(node) = current {           // descend the left spine
                stack.push(node.clone());
                current = node.borrow().left.clone();
            }
            let node = stack.pop().unwrap();           // leftmost unvisited node
            result.push(node.borrow().val);            // visit
            current = node.borrow().right.clone();     // now its right subtree
        }
        result
    }
}
```

> **Rust note:** `stack` holds `Rc` clones (cheap refcount bumps); each node is cloned on push and dropped after pop — net effect: every node alive on the stack exactly once, mirroring the other languages.

## Dry run

**Input:** the tree above (`1 -> right 2 -> left 3`).

```
stack=[], current=1, result=[]
  descend: push 1, current=null        stack=[1]
  visit:   pop 1, result=[1], current=2
  descend: push 2, current=3; push 3, current=null   stack=[2,3]
  visit:   pop 3, result=[1,3], current=null
  visit:   pop 2, result=[1,3,2], current=null
  stack empty, current=null -> stop
Output: [1, 3, 2] ✓
```

A nice property of in-order on a BST: the result is the *sorted* order — the same `[1, 2, 3]`-style sequence you'd get from a sorted array, which is why "in-order = sorted" is the standard BST litmus test.

## Complexity

**Time.** Each node pushed once, popped once:

$$
T(n) = O(n)
$$

**Space.** The stack holds at most one left-spine at a time — worst case is a skewed tree:

$$
S(n) = O(h) \text{ on the heap (no call-stack risk)}, \quad h \in [\log n, n]
$$

This is the whole point of the problem: identical asymptotics to recursion, but the memory lives on the *heap*, so a $10^5$-node skewed tree runs where recursion would `StackOverflowError`.

## Variants & follow-ups

- **Iterative pre/post-order** — same skeleton, different visit timing: pre-order visits at *push* time; post-order needs a "was I here before?" marker (two-stack or reversed-pre-order tricks).
- **Morris Traversal** — $O(1)$ space by temporarily *threading* right pointers: if a node's left subtree has no rightmost node yet, wire it to the current node, walk left; when you return, unthread and visit. Interviewers rarely demand it — mention it as the "constant-space curiosity."
- **BST Iterator** (`src/main/kotlin/tree/bst/`) — this exact loop turned into `hasNext()` / `next()`: the stack *is* the iterator state, and each `next()` does one descend+visit.
- **Kth Smallest Element In A BST** — run this traversal and stop at the $k$-th visit.
- **Interview follow-up:** "Recursive version?" Write it first — two lines, obviously correct — *then* offer this page as the stack-safe refinement. Showing both and explaining the tradeoff is the answer.
