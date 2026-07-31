# 5.29 Lowest Common Ancestor III (Parent Pointers)

> **Source**: [`src/main/kotlin/tree/LowestCommonAncestor_III.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/LowestCommonAncestor_III.kt)
> **Pattern**: two-pointer parent climb · **Core page**

## The Problem

LCA of `p` and `q` where nodes have **parent pointers** (p and q exist in the tree).

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  the tree with parent pointers; p and q given
Output: their LCA
```

## Intuition — walk both upward; when one hits the root, restart at the other

The classic two-pointer cycle trick: `parent1` climbs from p; on null, it restarts at q. Same for `parent2` from q. They meet at the LCA:

```kotlin
var parent1 = p
var parent2 = q

while (parent1 != parent2) {
    parent1 = parent1?.parent ?: q     // from p's side; restart at q at the top
    parent2 = parent2?.parent ?: p     // from q's side; restart at p at the top
}
return parent1
```

**Why the restart?** Without knowing the depth, the two walkers can't synchronize — restarting swaps the roles so both traverse the same total path length, meeting exactly at the LCA. The [4.2](../ch04-linked-lists/linked-list-cycle.md) "meeting point of two walkers" geometry.

## Approach 1 — Path set (O(n) space)

Collect p's ancestors into a set; climb q until a hit: correct, heavier.

## Approach 2 — Two-pointer restart (the repo's version, optimal)

```kotlin
class LowestCommonAncestor_III {
    class Node(var `val`: Int) {
        var left: TreeNode? = null
        var right: TreeNode? = null
        var parent: Node? = null
    }

    /**
     * @param p first node
     * @param q second node
     * @return  their lowest common ancestor
     */
    fun lowestCommonAncestor(p: Node?, q: Node?): Node? {
        var parent1 = p
        var parent2 = q

        while (parent1 != parent2) {
            parent1 = parent1?.parent ?: q
            parent2 = parent2?.parent ?: p
        }
        return parent1
    }
}
```

```java
public class LowestCommonAncestorIII {
    static class Node {
        int val;
        Node left, right, parent;
        Node(int v) { val = v; }
    }

    /**
     * @param p first node
     * @param q second node
     * @return  their lowest common ancestor
     */
    public Node lowestCommonAncestor(Node p, Node q) {
        Node a = p, b = q;

        while (a != b) {
            a = a.parent == null ? q : a.parent;
            b = b.parent == null ? p : b.parent;
        }
        return a;
    }
}
```

```cpp
class LowestCommonAncestorIII {
    struct Node {
        int val;
        Node* parent;
    };

public:
    /**
     * @param p first node
     * @param q second node
     * @return  their lowest common ancestor
     */
    Node* lowestCommonAncestor(Node* p, Node* q) {
        Node* a = p;
        Node* b = q;

        while (a != b) {
            a = a->parent ? a->parent : q;
            b = b->parent ? b->parent : p;
        }
        return a;
    }
};
```

```python
def lowest_common_ancestor(p: "Node", q: "Node") -> "Node":
    """
    @param p: first node
    @param q: second node
    @return:  their lowest common ancestor
    """
    a, b = p, q

    while a != b:
        a = a.parent if a.parent else q
        b = b.parent if b.parent else p

    return a
```

```rust
impl Solution {
    /// @param p first node
    /// @param q second node
    /// @return  their lowest common ancestor
    pub fn lowest_common_ancestor(p: Option<Rc<RefCell<Node>>>, q: Option<Rc<RefCell<Node>>>) -> Option<Rc<RefCell<Node>>> {
        let (mut a, mut b) = (p.clone(), q.clone());

        while a.as_ref().map(|n| Rc::as_ptr(n)) != b.as_ref().map(|n| Rc::as_ptr(n)) {
            let a_next = a.as_ref().and_then(|n| n.borrow().parent.clone());
            let b_next = b.as_ref().and_then(|n| n.borrow().parent.clone());

            a = a_next.unwrap_or_else(|| q.clone().unwrap());
            b = b_next.unwrap_or_else(|| p.clone().unwrap());
        }
        a
    }
}
```

## Dry run

**Input:** a tree where p is a leaf at depth 3, q at depth 2, LCA at depth 1.

```
walk: a climbs p's chain, b climbs q's.  a reaches root first (deeper start) -> restarts at q.
Both walkers now traverse root-to-... paths of equal total length, converging at the LCA.
```
The key: each walker's *total* path length is `depth(p) + depth(q) - depth(LCA)` — identical for both, so they synchronize exactly at the LCA on their second pass.

## Complexity

**Time.** Two climbs:

$$
T = O(\text{depth}(p) + \text{depth}(q))
$$

**Space.** Constants:

$$
S = O(1)
$$

## Variants & follow-ups

- **Lowest Common Ancestor** ([5.3](lowest-common-ancestor.md)) — the no-parent-pointer version.
- **Interview follow-up:** "Why do the walkers meet exactly?" After the restarts, both have walked `depth(p) + depth(q) - depth(LCA)` steps when they reach the LCA — equal totals force a simultaneous arrival. It's the [4.2](../ch04-linked-lists/linked-list-cycle.md) two-pointer meet-in-the-middle in tree form.
