# 4.17 Intersection Of Two Linked Lists

> **Source**: [`src/main/kotlin/linkedlist/IntersectionOfTwoLinkedList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/IntersectionOfTwoLinkedList.kt)
> **Pattern**: two-pointer length equalization · **Core page**

## The Problem

The node where two singly linked lists **intersect** (by reference), or null.

- Constraints: n, m ≤ 3×10⁴; no cycles.

## Examples

```
Input:  listA = [4,1,8,4,5], listB = [5,6,1,8,4,5]
Output: the node 8 (intersection)
```

## Intuition — the pointers walk both lists; the switch equalizes the tail

`pA` walks A then B; `pB` walks B then A — both traverse the same total length, so they **meet at the intersection** (or both null):

```kotlin
var pA = headA
var pB = headB

while (pA != pB) {
    pA = if (pA == null) headB else pA.next
    pB = if (pB == null) headA else pB.next
}
return pA
```

**Why the switch?** After the switch both pointers have walked `len(A) + len(B)`-ish total steps — the difference in head-to-intersection lengths is absorbed, so they synchronize exactly at the intersection. The [5.29](../ch05-trees/lowest-common-ancestor-iii.md) two-pointer meet, on lists.

## Approach 1 — Hash set of A's nodes (O(n) space)

Store A's nodes, walk B for the first hit: correct, heavier.

## Approach 2 — Two-pointer switch (the repo's version, optimal)

```kotlin
class IntersectionOfTwoLinkedList {
    /**
     * @param headA first list
     * @param headB second list
     * @return      intersection node or null
     */
    fun getIntersectionNode(headA: ListNode?, headB: ListNode?): ListNode? {
        if (headA == null || headB == null) return null

        var pA = headA
        var pB = headB

        while (pA != pB) {
            pA = if (pA == null) headB else pA.next
            pB = if (pB == null) headA else pB.next
        }
        return pA
    }
}
```

```java
public class IntersectionOfTwoLinkedLists {
    /**
     * @param headA first list
     * @param headB second list
     * @return      intersection node or null
     */
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) return null;

        ListNode a = headA, b = headB;
        while (a != b) {
            a = a == null ? headB : a.next;
            b = b == null ? headA : b.next;
        }
        return a;
    }
}
```

```cpp
class IntersectionOfTwoLinkedLists {
public:
    /**
     * @param headA first list
     * @param headB second list
     * @return      intersection node or null
     */
    ListNode* getIntersectionNode(ListNode* headA, ListNode* headB) {
        if (!headA || !headB) return nullptr;

        ListNode* a = headA;
        ListNode* b = headB;

        while (a != b) {
            a = a ? a->next : headB;
            b = b ? b->next : headA;
        }
        return a;
    }
};
```

```python
def get_intersection_node(headA: Optional["ListNode"], headB: Optional["ListNode"]) -> Optional["ListNode"]:
    """
    @param headA: first list
    @param headB: second list
    @return:      intersection node or null
    """
    if not headA or not headB:
        return None

    a, b = headA, headB
    while a is not b:
        a = headB if a is None else a.next
        b = headA if b is None else b.next

    return a
```

```rust
impl Solution {
    /// @param head_a first list
    /// @param head_b second list
    /// @return       intersection node or null
    pub fn get_intersection_node(head_a: Option<Box<ListNode>>, head_b: Option<Box<ListNode>>) -> Option<Box<ListNode>> {
        let (mut a, mut b) = (head_a.clone(), head_b.clone());

        while a.as_ref().map(|n| Rc::as_ptr(n)) != b.as_ref().map(|n| Rc::as_ptr(n)) {
            a = match a { Some(_) => a.unwrap().next, None => head_b.clone() };
            b = match b { Some(_) => b.unwrap().next, None => head_a.clone() };
        }
        a
    }
}
```

## Reading the code — what's actually happening

```kotlin
var pA = headA
var pB = headB
while (pA != pB) {
    pA = if (pA == null) headB else pA.next
    pB = if (pB == null) headA else pB.next
}
return pA
```

The problem: the two lists have different lengths *before* the shared tail, so starting both at their heads means they'd never arrive at the intersection together. The fix is beautifully simple — **make each pointer walk the entire other list**.

- **`pA` walks A, then B; `pB` walks B, then A.** When `pA` falls off the end of A (null), it teleports to B's head; when `pB` falls off B, it teleports to A's head. After the switch, both pointers have walked `len(A) + len(B)`-worth of nodes in total — but crucially, their *remaining* distance to the intersection is now identical.
- **Why do they synchronize?** Let `c` be the shared tail length, `a` = A's unique prefix, `b` = B's unique prefix. Pointer A reaches the intersection after `a + c` steps on its first lap; if it misses (it does when `a ≠ b`), it needs `b + c` more on the second lap — total `a + b + 2c` steps... actually the elegant way to see it: after `a + c + b` steps, A is at the intersection (it walked A's full `a + c`, then B's prefix `b`). Similarly B is at the intersection after `b + c + a` steps — the same number. Both pointers arrive at the first common node **simultaneously**.
- **If there's no intersection, they both reach null together** — after `len(A) + len(B)` steps both pointers are null, the loop exits with `pA == pB == null`, and we return null. One code path handles both cases.
- **The null-guards at the start** (`headA == null || headB == null`) short-circuit the degenerate inputs, though the loop would also terminate correctly on them.

Trace `A = [4,1,8,4,5], B = [5,6,1,8,4,5]`: A walks `4,1,8…` while B walks `5,6,1,8…` — A's pointer hits `8` after 7 steps (its 4,1 then B's 5,6,1), B hits `8` after 7 steps (5,6,1 then A's 4,1) — they meet at node `8` ✓.

## Dry run

**Input:** A = [4,1,8,4,5], B = [5,6,1,8,4,5]; intersection at 8.

```
a walks: 4,1,8...  b walks: 5,6,1,8...
a: 4-1-8-4-5-null->B:5-6-1-8   (7 steps to the 8)
b: 5-6-1-8-4-5-null->A:4-1-8   (7 steps to the 8)
They arrive at the 8-node simultaneously → return it ✓
```

## Complexity

**Time.** O(n + m):

$$
T(n, m) = O(n + m)
$$

**Space.** Pointers:

$$
S(n, m) = O(1)
$$

## Variants & follow-ups

- **Lowest Common Ancestor III** ([5.29](../ch05-trees/lowest-common-ancestor-iii.md)) — the identical walk-and-switch on parent pointers.
- **Interview follow-up:** "Why do the pointers necessarily meet?" Each pointer's total walk is `len(A) + len(B)` steps — after that both are null (no intersection) or they coincide earlier at the shared tail. The switch equalizes the differing head distances.
