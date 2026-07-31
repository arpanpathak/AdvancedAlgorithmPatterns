# 4.12 Reverse Nodes In K Groups

> **Source:** [`src/main/kotlin/linkedlist/ReverseNodesInKGroups.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/ReverseNodesInKGroups.kt)
> **Pattern:** block reversal with group pointers · **Core page**

## The Problem

Reverse the nodes of a linked list **k at a time**; a trailing partial group stays as-is.

- Constraints: $1 \le k \le n \le 5000$.

## Examples

```
Input:  head = [1,2,3,4,5], k = 2   -> Output: [2,1,4,3,5]
Input:  head = [1,2,3,4,5], k = 3   -> Output: [3,2,1,4,5]
```

## Intuition — find the group's end, reverse the group, stitch

The [4.1](reverse-linked-list.md) reversal applied per group, with `start`/`end` framing each block:

```
dummy -> head.  start = end = dummy

while end.next != null:
    advance end k steps (or bail: not enough nodes)
    nextGroupStart = end.next
    (newStart, newEnd) = reverse(start.next, end)   # reverse inside the group
    start.next = newStart                           # stitch front
    newEnd.next = nextGroupStart                    # stitch back
    start = end = newEnd
return dummy.next
```

**Why `end` advanced k steps first?** The group's boundaries must be known *before* reversing — if fewer than k nodes remain, `end` hits null and the tail stays untouched. The k-step probe is the "is there a full group?" check.

**Why reverse with a `(newStart, newEnd)` pair?** The group's new head (old tail) connects forward; the group's new tail (old head) connects to the next group. One reverse returns both — the [4.1](reverse-linked-list.md) engine with its boundary stitches.

## Approach 1 — Recursive (reverse k, recurse on the rest)

Clean, but O(n/k) recursion frames.

## Approach 2 — Iterative block reversal (the repo's version)

```kotlin
class ReverseNodesInKGroups {
    /**
     * @param head list head
     * @param k    group size
     * @return     head with each k-group reversed
     */
    fun reverseKGroup(head: ListNode?, k: Int): ListNode? {
        if (head == null || k == 1) return head

        val dummy = ListNode(0)
        dummy.next = head
        var start: ListNode? = dummy
        var end: ListNode? = dummy

        while (end?.next != null) {
            for (i in 0 until k) {                    // probe the group
                end = end?.next
                if (end == null) return dummy.next    // not enough nodes
            }

            val nextGroupStart = end?.next            // save the seam
            val (newStart, newEnd) = reverse(start?.next, end)

            start?.next = newStart                    // stitch front
            newEnd?.next = nextGroupStart             // stitch back

            start = newEnd
            end = newEnd
        }
        return dummy.next
    }

    private fun reverse(head: ListNode?, tail: ListNode?): Pair<ListNode?, ListNode?> {
        var prev: ListNode? = null
        var curr = head
        val newTail = head                            // the group's old head

        while (prev !== tail) {
            val next = curr?.next
            curr?.next = prev
            prev = curr
            curr = next
        }
        return prev to newTail                        // (newStart, newEnd)
    }
}
```

```java
public class ReverseNodesInKGroup {
    /**
     * @param head list head
     * @param k    group size
     * @return     head with each k-group reversed
     */
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k == 1) return head;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode start = dummy, end = dummy;

        while (end.next != null) {
            for (int i = 0; i < k; i++) {
                end = end.next;
                if (end == null) return dummy.next;   // not enough nodes
            }

            ListNode nextGroup = end.next;
            ListNode[] rev = reverse(start.next, end);

            start.next = rev[0];                      // stitch front
            rev[1].next = nextGroup;                  // stitch back

            start = rev[1];
            end = rev[1];
        }
        return dummy.next;
    }

    private ListNode[] reverse(ListNode head, ListNode tail) {
        ListNode prev = null, cur = head;
        ListNode newTail = head;

        while (prev != tail) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return new ListNode[]{prev, newTail};
    }
}
```

```cpp
class ReverseNodesInKGroup {
    std::pair<ListNode*, ListNode*> reverse(ListNode* head, ListNode* tail) {
        ListNode* prev = nullptr;
        ListNode* cur = head;
        ListNode* newTail = head;

        while (prev != tail) {
            ListNode* next = cur->next;
            cur->next = prev;
            prev = cur;
            cur = next;
        }
        return {prev, newTail};
    }

public:
    /**
     * @param head list head
     * @param k    group size
     * @return     head with each k-group reversed
     */
    ListNode* reverseKGroup(ListNode* head, int k) {
        if (!head || k == 1) return head;

        ListNode dummy(0);
        dummy.next = head;
        ListNode* start = &dummy;
        ListNode* end = &dummy;

        while (end->next) {
            for (int i = 0; i < k; i++) {
                end = end->next;
                if (!end) return dummy.next;         // not enough nodes
            }

            ListNode* nextGroup = end->next;
            auto [newStart, newEnd] = reverse(start->next, end);

            start->next = newStart;                  // stitch front
            newEnd->next = nextGroup;                // stitch back

            start = end = newEnd;
        }
        return dummy.next;
    }
};
```

```python
def reverse_k_group(head: Optional["ListNode"], k: int) -> Optional["ListNode"]:
    """
    @param head: list head
    @param k:    group size
    @return:     head with each k-group reversed
    """
    if not head or k == 1:
        return head

    dummy = ListNode(0)
    dummy.next = head
    start = end = dummy

    while end.next:
        for _ in range(k):
            end = end.next
            if not end:
                return dummy.next           # not enough nodes

        next_group = end.next

        # reverse [start.next, end]
        prev, cur = None, start.next
        new_tail = start.next
        while prev is not end:
            nxt = cur.next
            cur.next = prev
            prev, cur = cur, nxt

        start.next = prev                   # stitch front
        new_tail.next = next_group          # stitch back

        start = end = new_tail

    return dummy.next
```

```rust
impl Solution {
    /// @param head list head
    /// @param k    group size
    /// @return     head with each k-group reversed
    pub fn reverse_k_group(head: Option<Box<ListNode>>, k: i32) -> Option<Box<ListNode>> {
        let mut dummy = Box::new(ListNode::new(0));
        dummy.next = head;
        let mut start = &mut dummy;

        loop {
            // probe: does a full k-group remain?
            let mut end = &start.clone().next;
            for _ in 0..k {
                end = match end { Some(n) => &n.next, None => return dummy.next };
            }
            // (Rust ownership makes the iterative stitch verbose; the
            //  recursive version below is the idiomatic spelling)
        }
    }
}

// The recursive spelling (same algorithm, cleaner in Rust):
//   if fewer than k nodes remain, return head
//   else reverse the first k, then head.next = reverse_k_group(rest, k)
```

## Dry run

**Input:** `head = [1,2,3,4,5]`, `k = 3`.

```
dummy -> 1 -> 2 -> 3 -> 4 -> 5.  start = end = dummy

probe: end walks to 3.  nextGroup = 4.
reverse [1,2,3]: prev walks 1<-2<-3.  newTail = 1.
stitch: start.next = 3.  1.next = 4.  list: dummy -> 3 -> 2 -> 1 -> 4 -> 5.
start = end = 1.

probe: end walks 1->4->5, then end.next == null?  end = 5, next = null -> the loop's end.next
       check: 5.next == null -> while ends? NO: after advancing, end = 5 which HAS a next? 
       5.next == null -> next iteration of outer while: end.next == null -> exit.

Output: 3 -> 2 -> 1 -> 4 -> 5 ✓
```

The probe-then-reverse rhythm: the k-step `end` walk both *finds* the group and *validates* it (null = bail, tail intact). The reverse returns the group's new ends; the two stitches (front from `start`, back to `nextGroupStart`) splice it into the list. `k=2` on the same list: groups `[1,2]`, `[3,4]`, tail 5 → `[2,1,4,3,5]` ✓.

## Complexity

**Time.** Two passes per node (probe + reverse):

$$
T(n) = O(n)
$$

**Space.** Pointers only:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Swap Nodes In Pairs** ([4.11](swap-nodes-in-pairs.md)) — the k=2 special case.
- **Reverse Linked List** ([4.1](reverse-linked-list.md)) — the per-group engine.
- **Interview follow-up:** "Why the `prev !== tail` stop in reverse?" The reverse must stop *exactly at the group's tail* — not the list's end. Comparing `prev` to the captured `tail` node (identity, not value) bounds the reversal to the group; `newEnd = old head` carries the seam for the back-stitch.
