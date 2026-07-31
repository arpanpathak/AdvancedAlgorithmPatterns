# 4.5 Linked List Cycle II

> **Source:** [`src/main/kotlin/linkedlist/LinkedListCycle_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/LinkedListCycle_II.kt)
> **Pattern:** Floyd's with entry-point math · **Gym page — the "prove the math" favorite**

## The Problem

Like [4.2](linked-list-cycle.md), but now also return the node where the cycle **begins**. If there is no cycle, return `null`. $O(1)$ space.

## Examples

```
Input:  3 -> 2 -> 0 -> -4 ─┐
             ↑______________┘     (cycle starts at the node with value 2)
Output: the node with value 2
```

## Intuition — one more lap reveals the entry

Floyd's meeting ([4.2](linked-list-cycle.md)) finds *some* node in the cycle. The entry-point question is: **why does walking from the head and from the meeting point at equal speed meet at the cycle's start?**

Set up the notation: the non-cycle prefix has length $a$; the meeting point is $b$ nodes into the cycle (so the meeting point is $b$ steps after the entry, walking forward); the cycle has length $L$.

At the meeting, the tortoise has walked $a + b$ steps (it entered the cycle once and walked $b$ more). The hare walked $2(a+b)$ (twice as fast). The hare's path is also `a` (to the entry) plus some integer number $q$ of full laps plus $b$: $a + qL + b$. Equating:

$$
2(a + b) = a + qL + b \quad\Longrightarrow\quad a + b = qL
$$

So $a + b$ is an exact multiple of $L$. Now the key observation:

- A pointer starting at the **head** needs exactly $a$ steps to reach the entry.
- A pointer starting at the **meeting point** walks $a \bmod L$ steps to reach some node; but $a \equiv L - b \pmod L$ (from $a + b \equiv 0$), and walking $L - b$ steps forward from a point $b$ steps into the cycle lands **exactly on the entry**.

Both pointers reach the cycle's start after exactly $a$ steps. Walk them in lockstep (1 step each); their first collision is the answer.

## Approach 1 — Hash set

Store every visited node; the first node seen twice is the entry. $O(n)$ time, $O(n)$ space — fails the $O(1)$-space requirement.

## Approach 2 — Floyd + entry-point walk (optimal)

```kotlin
/**
 * @param head the head of the linked list
 * @return     the node where the cycle begins, or null if there is no cycle
 */
fun detectCycle(head: ListNode?): ListNode? {
    var slow = head
    var fast = head

    // Phase 1: find a meeting point inside the cycle (standard Floyd).
    while (fast != null && fast.next != null) {
        slow = slow?.next
        fast = fast.next?.next
        if (slow == fast) break
    }

    // No cycle: the hare fell off the list.
    if (fast == null || fast.next == null) return null

    // Phase 2: head-pointer and meeting-pointer walk 1 step each.
    // By the congruence a == L - b (mod L), they meet at the cycle start.
    var entry: ListNode? = head
    while (entry != slow) {
        entry = entry?.next
        slow = slow?.next
    }
    return entry
}
```

```java
public class LinkedListCycleII {
    /**
     * @param head the head of the linked list
     * @return     the node where the cycle begins, or null if there is no cycle
     */
    public ListNode detectCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {       // phase 1: meet inside
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) break;
        }
        if (fast == null || fast.next == null) return null;   // no cycle

        ListNode entry = head;                              // phase 2: walk to entry
        while (entry != slow) {
            entry = entry.next;
            slow = slow.next;
        }
        return entry;
    }
}
```

```cpp
struct ListNode {
    int val;
    ListNode* next;
    ListNode(int x) : val(x), next(nullptr) {}
};

class LinkedListCycleII {
public:
    /**
     * @param head the head of the linked list
     * @return     the node where the cycle begins, or null if there is no cycle
     */
    ListNode* detectCycle(ListNode* head) {
        ListNode* slow = head;
        ListNode* fast = head;
        while (fast && fast->next) {
            slow = slow->next;
            fast = fast->next->next;
            if (slow == fast) break;
        }
        if (!fast || !fast->next) return nullptr;

        ListNode* entry = head;
        while (entry != slow) {
            entry = entry->next;
            slow = slow->next;
        }
        return entry;
    }
};
```

```python
def detect_cycle(head: ListNode | None) -> ListNode | None:
    """
    @param head: the head of the linked list
    @return:     the node where the cycle begins, or None if there is no cycle
    """
    slow = fast = head
    while fast and fast.next:                 # phase 1: meet inside the cycle
        slow = slow.next
        fast = fast.next.next
        if slow is fast:
            break
    if fast is None or fast.next is None:
        return None                           # no cycle

    entry = head                              # phase 2: walk to the entry
    while entry is not slow:
        entry = entry.next
        slow = slow.next
    return entry
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param head the head of the linked list
    /// @return     the node where the cycle begins, or None if there is no cycle
    pub fn detect_cycle(head: Option<Box<ListNode>>) -> Option<Box<ListNode>> {
        // The O(1)-space two-pointer version needs raw-pointer gymnastics in Rust;
        // the readable HashSet version below is the pragmatic fallback.
        let mut seen: HashSet<*const ListNode> = HashSet::new();
        let mut cur = head.as_ref();
        while let Some(node) = cur {
            let ptr = node.as_ref() as *const ListNode;
            if !seen.insert(ptr) {
                return Some(node.clone());
            }
            cur = node.next.as_ref();
        }
        None
    }
}
```

> **Rust note:** the canonical two-pointer phase requires raw pointers (see [4.2](linked-list-cycle.md) for the detection-only version). When the entry node must be *returned*, the HashSet version shown above is the standard readable Rust tradeoff — name it honestly if asked about space.

## Dry run

**Input:** `3 -> 2 -> 0 -> -4`, with `-4.next = 2`. So `a = 1` (one node before the cycle), entry = the `2`, cycle length `L = 3`.

```
Phase 1 (Floyd):
slow=3 fast=3
step 1: slow=2, fast=0
step 2: slow=0, fast=-4
step 3: slow=-4, fast=-4   -> meet at -4. Tortoise traveled 3 steps = a + b, so b = 2.

Phase 2 (entry walk):
entry=3, slow=-4
  entry=2, slow=2   -> entry == slow -> return the 2 ✓
```

Verify the math: `a + b = 1 + 2 = 3 = qL` with `q = 1` ✓. And `L − b = 1 = a`, so walking `a = 1` step from the meeting point (-4 → 2) lands on the same node as walking `a = 1` step from the head (3 → 2) — the entry. The lockstep walk found it in one step.

## Complexity

**Time.** Phase 1 is $O(n_0 + L)$; phase 2 walks at most $a$ steps:

$$
T(n) = O(n)
$$

**Space.** $O(1)$ (two pointers).

## Variants & follow-ups

- **[4.2](linked-list-cycle.md)** — detection only; this page adds the entry-point phase.
- **Intersection of Two Linked Lists** (`src/main/kotlin/linkedlist/IntersectionOfTwoLinkedList.kt`) — the same "two walks meet at a common node" idea in a different shape.
- **Interview follow-up:** "Why does the entry walk terminate?" Phase 2 pointers are both *inside-or-before* the cycle; they meet within at most $a$ steps (both reach the entry exactly then), so no infinite loop.
- **Interview follow-up:** "Does the meeting point matter for phase 2?" Any meeting point works — the congruence holds for whatever `b` the race produced. That's why the algorithm is deterministic despite the "arbitrary" meeting.
