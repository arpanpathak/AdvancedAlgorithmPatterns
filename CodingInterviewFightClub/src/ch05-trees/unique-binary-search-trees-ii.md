# 5.28 Unique Binary Search Trees II

> **Source**: [`src/main/kotlin/tree/bst/UniqueBinarySearchTrees_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bst/UniqueBinarySearchTrees_II.kt)
> **Pattern**: Cartesian tree generation · **Core page**

## The Problem

Generate **all** BSTs with nodes 1..n.

- Constraints: n ≤ 8 (Catalan explosion).

## Examples

```
Input:  n = 3   -> Output: 5 trees
```

## Intuition — the [5.27](unique-binary-search-trees.md) DP, materialized

For each root in `[start, end]`, combine every left tree with every right tree:

```kotlin
fun construct(start: Int, end: Int): List<TreeNode?> {
    when {
        start > end -> return listOf(null)
        start == end -> return listOf(TreeNode(start))
    }

    val result = mutableListOf<TreeNode?>()

    for (root in start..end) {
        val leftTrees = construct(start, root - 1)
        val rightTrees = construct(root + 1, end)

        for (left in leftTrees) {
            for (right in rightTrees) {
                val node = TreeNode(root)
                node.left = left
                node.right = right
                result.add(node)
            }
        }
    }
    return result
}
```

**Why the full Cartesian product?** Each root pairs every left-structure with every right-structure — the multiplication rule of [5.27](unique-binary-search-trees.md), turned into actual trees.

## Approach 1 — Recursive generation (the repo's version, optimal)

```kotlin
class UniqueBinarySearchTrees_II {
    /**
     * @param n node count
     * @return  all BSTs with nodes 1..n
     */
    fun generateTrees(n: Int): List<TreeNode?> {
        if (n == 0) return emptyList()

        fun construct(start: Int, end: Int): List<TreeNode?> {
            when {
                start > end -> return listOf(null)
                start == end -> return listOf(TreeNode(start))
            }

            val result = mutableListOf<TreeNode?>()

            for (root in start..end) {
                val leftTrees = construct(start, root - 1)
                val rightTrees = construct(root + 1, end)

                for (left in leftTrees) {
                    for (right in rightTrees) {
                        val node = TreeNode(root)
                        node.left = left
                        node.right = right
                        result.add(node)
                    }
                }
            }
            return result
        }

        return construct(1, n)
    }
}
```

```java
import java.util.*;

public class UniqueBinarySearchTreesII {
    private List<TreeNode> construct(int start, int end) {
        List<TreeNode> result = new ArrayList<>();

        if (start > end) { result.add(null); return result; }

        for (int root = start; root <= end; root++) {
            for (TreeNode left : construct(start, root - 1)) {
                for (TreeNode right : construct(root + 1, end)) {
                    TreeNode node = new TreeNode(root);
                    node.left = left;
                    node.right = right;
                    result.add(node);
                }
            }
        }
        return result;
    }

    /**
     * @param n node count
     * @return  all BSTs with nodes 1..n
     */
    public List<TreeNode> generateTrees(int n) {
        return construct(1, n);
    }
}
```

```cpp
#include <vector>

class UniqueBinarySearchTreesII {
    std::vector<TreeNode*> construct(int start, int end) {
        std::vector<TreeNode*> result;

        if (start > end) { result.push_back(nullptr); return result; }

        for (int root = start; root <= end; root++) {
            for (TreeNode* left : construct(start, root - 1)) {
                for (TreeNode* right : construct(root + 1, end)) {
                    TreeNode* node = new TreeNode(root);
                    node->left = left;
                    node->right = right;
                    result.push_back(node);
                }
            }
        }
        return result;
    }

public:
    /**
     * @param n node count
     * @return  all BSTs with nodes 1..n
     */
    std::vector<TreeNode*> generateTrees(int n) {
        return construct(1, n);
    }
};
```

```python
def generate_trees(n: int) -> list[Optional["TreeNode"]]:
    """
    @param n: node count
    @return:  all BSTs with nodes 1..n
    """
    def construct(start: int, end: int) -> list:
        if start > end:
            return [None]

        result = []
        for root in range(start, end + 1):
            for left in construct(start, root - 1):
                for right in construct(root + 1, end):
                    node = TreeNode(root)
                    node.left = left
                    node.right = right
                    result.append(node)

        return result

    return construct(1, n) if n else []
```

```rust
use std::rc::Rc;
use std::cell::RefCell;

impl Solution {
    /// @param n node count
    /// @return  all BSTs with nodes 1..n
    pub fn generate_trees(n: i32) -> Vec<Option<Rc<RefCell<TreeNode>>>> {
        fn construct(start: i32, end: i32) -> Vec<Option<Rc<RefCell<TreeNode>>>> {
            if start > end { return vec![None]; }

            let mut result = Vec::new();
            for root in start..=end {
                for left in construct(start, root - 1) {
                    for right in construct(root + 1, end) {
                        let node = Rc::new(RefCell::new(TreeNode::new(root)));
                        node.borrow_mut().left = left.clone();
                        node.borrow_mut().right = right.clone();
                        result.push(Some(node));
                    }
                }
            }
            result
        }

        if n == 0 { vec![] } else { construct(1, n) }
    }
}
```

## Dry run

**Input:** `n = 3`.

```
construct(1,3): root 1: left [null], right = construct(2,3): root 2: left [null], right [3].
    -> tree 1(2,3).  root 2 in (2,3): left [null], right [null]? also (2,3) root 2 right [3], root 3 left [2]...
    The 5 Catalan trees are generated in the standard enumeration ✓
```

## Complexity

**Time.** Catalan(n) trees:

$$
T(n) = O(C_n)
$$

**Space.** The trees:

$$
S(n) = O(C_n)
$$

## Variants & follow-ups

- **Unique Binary Search Trees** ([5.27](unique-binary-search-trees.md)) — the counting ancestor.
- **Interview follow-up:** "Why is the base `start > end → [null]`?" An empty range has exactly one "tree": null. The [5.16](delete-node-in-a-bst.md) null-return discipline, as a list-of-one.
