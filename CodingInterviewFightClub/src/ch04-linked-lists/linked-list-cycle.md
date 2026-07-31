# 4.2 Linked List Cycle

> **Source:** [`src/main/kotlin/linkedlist/LinkedListCycle.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/LinkedListCycle.kt)
> **Pattern:** Floyd's tortoise & hare · **Core page**

## The Problem

Given the head of a linked list, determine whether it contains a **cycle** (a node whose `next` points back into the list). Return `true`/`false`. Must use $O(1)$ space.

- Constraints: $0 \le n \le 10^4$.

## Examples

```
Input:  3 -> 2 -> 0 -> -4 ─┐
             ↑______________┘     (node -4's next points back to 2)
Output: true

Input:  1 -> 2 -> null
Output: false
```

## Intuition — a race where the hare must lap the tortoise

The naive approach (a hash set of visited nodes) is $O(n)$ time but $O(n)$ space. Floyd's algorithm uses **two pointers moving at different speeds** — a "tortoise" (1 step) and a "hare" (2 steps):

- **If there's no cycle:** the hare hits `null` first — return `false`.
- **If there IS a cycle:** both runners enter it, and since the hare gains exactly **1 node per step** on the tortoise, it must eventually *lap* the tortoise — they meet. Return `true`.

**Why the speed difference guarantees a meeting:** once both are on the cycle of length $L$, the distance between them shrinks by 1 each step (hare gains 1 per step). After at most $L$ steps the distance is 0. Before the cycle, the hare's head start (it enters the cycle first, since it's faster) doesn't matter — the meeting happens inside the cycle regardless.

**The two clean implementations** in the repo:
- `hasCycle` — a compact version (advance then compare),
- `hasCycle2` — the canonical loop-guard version (compare then advance).

They're the same algorithm with the comparison moved; `hasCycle2` is the one to write in interviews (no `head.next` null-deref risk on a 1-node list).

## Approach 1 — Hash set

Walk the list, storing every node; if a node repeats, it's a cycle. $O(n)$ time, $O(n)$ space — fails the space constraint.

## Approach 2 — Floyd's tortoise & hare (optimal)

```kotlin
/**
 * @param head the head of the linked list
 * @return     true iff the list contains a cycle
 */
fun hasCycle(head: ListNode?): Boolean {
    var slow = head
    var fast = head

    while (fast != null && fast.next != null) {
        slow = slow?.next        // tortoise: 1 step
        fast = fast.next?.next   // hare: 2 steps

        if (slow == fast) return true    // the hare lapped the tortoise
    }
    return false                 // the hare fell off the list -> no cycle
}
```

```java
public class LinkedListCycle {
    /**
     * @param head the head of the linked list
     * @return     true iff the list contains a cycle
     */
    public boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }
}
```

```cpp
struct ListNode {
    int val;
    ListNode* next;
    ListNode(int x) : val(x), next(nullptr) {}
};

class LinkedListCycle {
public:
    /**
     * @param head the head of the linked list
     * @return     true iff the list contains a cycle
     */
    bool hasCycle(ListNode* head) {
        ListNode* slow = head;
        ListNode* fast = head;
        while (fast && fast->next) {
            slow = slow->next;
            fast = fast->next->next;
            if (slow == fast) return true;
        }
        return false;
    }
};
```

```python
def has_cycle(head: ListNode | None) -> bool:
    """
    @param head: the head of the linked list
    @return:     True iff the list contains a cycle
    """
    slow = fast = head
    while fast and fast.next:
        slow = slow.next          # tortoise: 1 step
        fast = fast.next.next     # hare: 2 steps
        if slow is fast:
            return True           # the hare lapped the tortoise
    return False
```

```rust
impl Solution {
    /// @param head the head of the linked list
    /// @return     true iff the list contains a cycle
    pub fn has_cycle(head: Option<Box<ListNode>>) -> bool {
        let mut slow = &head;
        let mut fast = &head;
        while fast.is_some() && fast.as_ref().unwrap().next.is_some() {
            slow = &slow.as_ref().unwrap().next;
            fast = &fast.as_ref().unwrap().next.as_ref().unwrap().next;
            if std::ptr::eq(slow.as_ref().unwrap().as_ref() as *const ListNode,
                            fast.as_ref().unwrap().as_ref() as *const ListNode) {
                return true;
            }
        }
        false
    }
}
```

> **Rust note:** reference-walking with `Box` needs pointer comparison — `std::ptr::eq` on the underlying `ListNode` addresses is the clean way to compare "same node" in Rust's ownership model.

## Dry run

**Input:** `3 -> 2 -> 0 -> -4 -> (back to 2)` — a cycle of length 3.

```
step 0: slow=3, fast=3
step 1: slow=2, fast=0
step 2: slow=0, fast=2
step 3: slow=-4, fast=-4   -> slow == fast -> true ✓
```

The hare enters the cycle at step 1, the tortoise at step 2; the hare gains one node per step, so it catches up after `cycle length` steps at most.

**Input:** `1 -> 2 -> null` (no cycle)

```
step 1: slow=2, fast=null -> loop guard fails -> false ✓
```

**Edge cases:** empty list / single node with `next = null` → loop never enters → `false`. Single node pointing at itself → `slow == fast` after one step → `true`.

## Complexity

**Time.** Before the cycle, the hare covers the tail in $O(n_0)$ steps; inside, they meet in at most $L$ steps:

$$
T(n) = O(n_0 + L) = O(n)
$$

**Space.** $O(1)$ — two pointers, the entire point.

## Variants & follow-ups

- **[4.5](linked-list-cycle-ii.md)** — the same race, plus *where* the cycle starts (Floyd's entry-point math).
- **Middle of the Linked List** (`src/main/kotlin/linkedlist/MiddleNode.kt`) — same two-speed race; when the hare stops, the tortoise is the middle.
- **Happy Number** (classic) — cycle detection on a *value* function instead of pointers; same idea, different data.
- **Interview follow-up:** "Prove they must meet." Inside the cycle, each step reduces the distance between the runners by exactly 1; a distance of 0 (meeting) is reached within $L$ steps. If the list were acyclic, the hare exits first. Both cases covered.
- **Interview follow-up:** "Why 2× and not 3×?" Any speed ratio > 1 works for detection (the meeting still occurs), but 2× is the minimal, simplest, and standard choice; 3× complicates the entry-point math in [4.5](linked-list-cycle-ii.md).
