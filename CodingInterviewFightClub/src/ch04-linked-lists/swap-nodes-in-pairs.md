# 4.11 Swap Nodes In Pairs

> **Source:** [`src/main/kotlin/linkedlist/SwapNodesInPairs.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/SwapNodesInPairs.kt)
> **Pattern:** dummy-head rewire · **Core page**

## The Problem

Swap every **adjacent pair** of nodes in a linked list (swap nodes, not values).

- Constraints: n ≤ 100.

## Examples

```
Input:  head = [1,2,3,4]   -> Output: [2,1,4,3]
Input:  head = []          -> Output: []
Input:  head = [1]         -> Output: [1]
```

## Intuition — a dummy head and a `prev` that re-links each pair

Each swap rewires four pointers: `prev.next = node2`, `node1.next = node2.next`, `node2.next = node1`, then `prev = node1`:

```kotlin
val dummy = ListNode(0).apply { next = head }
var prev: ListNode? = dummy

while (prev?.next != null && prev.next?.next != null) {
    val (node1, node2) = prev.next to prev.next?.next

    prev.next = node2               // connect the pair's front
    node1?.next = node2?.next       // node1 jumps to the next pair
    node2?.next = node1             // node2 lands before node1

    prev = node1                    // prev now sits before the next pair
}
return dummy.next
```

**Why the dummy?** The head changes (node 2 becomes first) — the dummy gives a stable `prev` for the first swap and a fixed `dummy.next` to return. The [4.1](reverse-linked-list.md) sentinel-head discipline.

**Why check `prev.next?.next`?** A pair needs two nodes; the loop stops when fewer remain — the odd tail is left in place.

## Approach 1 — Recursive (swap first pair, recurse)

`newHead = head.next; head.next.next = head; head.next = swapPairs(...)`: the elegant one-liner, recursion depth O(n).

## Approach 2 — Iterative with dummy (the repo's version)

```kotlin
class SwapNodesInPairs {
    /**
     * @param head list head
     * @return     head with adjacent pairs swapped
     */
    fun swapPairs(head: ListNode?): ListNode? {
        val dummy = ListNode(0).apply { next = head }
        var prev: ListNode? = dummy

        while (prev?.next != null && prev.next?.next != null) {
            val (node1, node2) = prev.next to prev.next?.next

            prev.next = node2
            node1?.next = node2?.next
            node2?.next = node1

            prev = node1
        }
        return dummy.next
    }
}
```

```java
public class SwapNodesInPairs {
    /**
     * @param head list head
     * @return     head with adjacent pairs swapped
     */
    public ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;

        while (prev.next != null && prev.next.next != null) {
            ListNode n1 = prev.next;
            ListNode n2 = prev.next.next;

            prev.next = n2;              // connect the pair's front
            n1.next = n2.next;           // n1 jumps to the next pair
            n2.next = n1;                // n2 lands before n1

            prev = n1;
        }
        return dummy.next;
    }
}
```

```cpp
class SwapNodesInPairs {
public:
    /**
     * @param head list head
     * @return     head with adjacent pairs swapped
     */
    ListNode* swapPairs(ListNode* head) {
        ListNode dummy(0);
        dummy.next = head;
        ListNode* prev = &dummy;

        while (prev->next && prev->next->next) {
            ListNode* n1 = prev->next;
            ListNode* n2 = prev->next->next;

            prev->next = n2;             // connect the pair's front
            n1->next = n2->next;         // n1 jumps to the next pair
            n2->next = n1;               // n2 lands before n1

            prev = n1;
        }
        return dummy.next;
    }
};
```

```python
def swap_pairs(head: Optional["ListNode"]) -> Optional["ListNode"]:
    """
    @param head: list head
    @return:     head with adjacent pairs swapped
    """
    dummy = ListNode(0)
    dummy.next = head
    prev = dummy

    while prev.next and prev.next.next:
        n1 = prev.next
        n2 = prev.next.next

        prev.next = n2              # connect the pair's front
        n1.next = n2.next           # n1 jumps to the next pair
        n2.next = n1                # n2 lands before n1

        prev = n1
    return dummy.next
```

```rust
impl Solution {
    /// @param head list head
    /// @return     head with adjacent pairs swapped
    pub fn swap_pairs(head: Option<Box<ListNode>>) -> Option<Box<ListNode>> {
        let mut dummy = Box::new(ListNode::new(0));
        dummy.next = head;
        let mut prev = &mut dummy;

        while prev.next.is_some() && prev.next.as_ref().unwrap().next.is_some() {
            let mut n2 = prev.next.as_mut().unwrap().next.take().unwrap();
            let n1 = prev.next.take().unwrap();

            prev.next = Some(n2);
            n2.next = Some(n1);
            // n1 already points to the tail of the remaining list

            prev = &mut n2.next.as_mut().unwrap().next;  // advance past the pair
            // (the Rust borrow gymnastics mirror the 4 pointer rewires)
        }
        dummy.next
    }
}
```

## Dry run

**Input:** `head = 1 → 2 → 3 → 4`.

```
dummy -> 1 -> 2 -> 3 -> 4.  prev = dummy
pair (1,2): prev.next = 2.  1.next = 3.  2.next = 1.  list: dummy -> 2 -> 1 -> 3 -> 4.  prev = 1
pair (3,4): prev.next = 4.  3.next = null.  4.next = 3.  list: dummy -> 2 -> 1 -> 4 -> 3.  prev = 3
prev.next == null -> stop.

Output: 2 -> 1 -> 4 -> 3 ✓
```

The four rewires per pair are the entire algorithm: `prev.next` adopts the second node, `node1.next` skips to the next pair's start, `node2.next` closes the swap. `prev` advancing to `node1` positions it *before* the next pair. Odd length `[1,2,3]`: the `(1,2)` swap leaves 3 dangling correctly → `[2,1,3]`.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Dummy + pointers:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Reverse Nodes In K Groups** ([4.12](reverse-nodes-in-k-groups.md)) — the generalization: reverse every k-block.
- **Reverse Linked List** ([4.1](reverse-linked-list.md)) — the reversal engine each pair performs.
- **Interview follow-up:** "Why is `node1.next = node2.next` before `node2.next = node1`?" The order matters: once `node2.next = node1` executes, the pointer to the next pair is *lost* (node2 no longer points at it). The save (`node1.next = node2.next`) must happen first — the [4.1](reverse-linked-list.md) next-save discipline.
