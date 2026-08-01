# 4.4 Remove Nth Node From End

> **Source**: [`src/main/kotlin/linkedlist/RemoveNthNodeFromEndOfList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/RemoveNthNodeFromEndOfList.kt)
> **Pattern**: fast/slow offset · **Core page**

## The Problem

Remove the n-th node **from the end** (n valid).

- Constraints: n ≥ 1.

## Examples

```
Input:  head = [1,2,3,4,5], n = 2   -> Output: [1,2,3,5]
```

## Intuition — fast runs n ahead; when fast hits null, slow is at the victim

A dummy head + fast/slow with an n-step offset — the unlink happens exactly when fast exhausts:

```kotlin
val dummy = ListNode(0).apply { next = head }
var fast: ListNode? = dummy
var slow: ListNode? = dummy

repeat(n) { fast = fast?.next }       // n ahead

while (fast?.next != null) {          // until fast is at the tail
    slow = slow?.next
    fast = fast?.next
}

slow?.next = slow?.next?.next         // unlink the victim
return dummy.next
```

**Why the dummy?** Removing the head (n == size) needs a predecessor — the dummy provides one uniformly. The [4.18](delete-middle-node-of-linked-list.md) surgery with an offset instead of a middle.

## Approach 1 — Count then walk (the repo's version)

Two passes: count, then walk to `size - n - 1`, unlink.

## Approach 2 — Fast/slow offset (optimal, one pass)

```kotlin
class RemoveNthNodeFromEndOfList {
    /**
     * @param head list head
     * @param n    position from the end (1-based)
     * @return     list without the n-th-from-end node
     */
    fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
        val dummy = ListNode(0).apply { next = head }
        var fast: ListNode? = dummy
        var slow: ListNode? = dummy

        repeat(n) { fast = fast?.next }

        while (fast?.next != null) {
            slow = slow?.next
            fast = fast?.next
        }

        slow?.next = slow?.next?.next
        return dummy.next
    }
}
```

```java
public class RemoveNthNodeFromEnd {
    /**
     * @param head list head
     * @param n    position from the end (1-based)
     * @return     list without the n-th-from-end node
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode fast = dummy, slow = dummy;
        for (int i = 0; i < n; i++) fast = fast.next;

        while (fast.next != null) {
            slow = slow.next;
            fast = fast.next;
        }

        slow.next = slow.next.next;
        return dummy.next;
    }
}
```

```cpp
class RemoveNthNodeFromEnd {
public:
    /**
     * @param head list head
     * @param n    position from the end (1-based)
     * @return     list without the n-th-from-end node
     */
    ListNode* removeNthFromEnd(ListNode* head, int n) {
        ListNode dummy(0);
        dummy.next = head;

        ListNode* fast = &dummy;
        ListNode* slow = &dummy;

        for (int i = 0; i < n; i++) fast = fast->next;

        while (fast->next) {
            slow = slow->next;
            fast = fast->next;
        }

        slow->next = slow->next->next;
        return dummy.next;
    }
};
```

```python
def remove_nth_from_end(head: Optional["ListNode"], n: int) -> Optional["ListNode"]:
    """
    @param head: list head
    @param n:    position from the end (1-based)
    @return:     list without the n-th-from-end node
    """
    dummy = ListNode(0)
    dummy.next = head

    fast = slow = dummy
    for _ in range(n):
        fast = fast.next

    while fast.next:
        slow = slow.next
        fast = fast.next

    slow.next = slow.next.next
    return dummy.next
```

```rust
impl Solution {
    /// @param head list head
    /// @param n    position from the end (1-based)
    /// @return     list without the n-th-from-end node
    pub fn remove_nth_from_end(head: Option<Box<ListNode>>, n: i32) -> Option<Box<ListNode>> {
        let mut dummy = Box::new(ListNode::new(0));
        dummy.next = head;

        let mut fast = dummy.clone();
        for _ in 0..n { fast = fast.next.unwrap(); }

        let mut slow = &mut dummy;
        while fast.next.is_some() {
            fast = fast.next.unwrap();
            slow = slow.next.as_mut().unwrap();
        }

        let next = slow.next.as_mut().unwrap().next.take();
        slow.next = next;
        dummy.next
    }
}
```

## Dry run

**Input:** `head = [1,2,3,4,5], n = 2`.

```
dummy -> 1 2 3 4 5.  fast walks 2: dummy->2.  slow = dummy.
while fast.next != null: fast 2->3->4->5->null? trace: fast=2, slow=dummy.
  step: slow=1, fast=3.  slow=2, fast=4.  slow=3, fast=5.  slow=4, fast=null? 5.next==null -> stop.
slow=4.  slow.next = 5.next = null.  Output: [1,2,3,4]? 

wait — n=2 from the end of [1,2,3,4,5] is 4!  The victim is 4, output [1,2,3,5].
Re-trace: fast starts at dummy, +2 -> node 2.  Loop: fast=2: slow=1,fast=3.  slow=2,fast=4.
slow=3,fast=5.  fast.next==null -> stop.  slow=3.  slow.next = 4.next = 5.  Output: [1,2,3,5] ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The dummy:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Delete Middle Node** ([4.18](delete-middle-node-of-linked-list.md)) — the same surgery with the fast/slow middle.
- **Interview follow-up:** "Why the n-step offset instead of counting?" The offset makes the walk one-pass: when fast reaches the end, slow is exactly n behind — the victim's predecessor.
