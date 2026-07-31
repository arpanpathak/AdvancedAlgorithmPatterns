# 4.4 Remove Nth Node From End

> **Source:** [`src/main/kotlin/linkedlist/RemoveNthNodeFromEndOfList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/RemoveNthNodeFromEndOfList.kt)
> **Pattern:** dummy node + offset pointers · **Core page**

## The Problem

Remove the `n`-th node **from the end** of a singly linked list and return the head. Must do it in **one pass**.

- Constraints: $1 \le n \le \text{list length}$.

## Examples

```
Input:  1 -> 2 -> 3 -> 4 -> 5, n = 2
Output: 1 -> 2 -> 3 -> 5          (removed the 4)

Input:  [1], n = 1
Output: []                        (the head itself is removed)

Input:  [1, 2], n = 1
Output: [1]
```

## Intuition — the offset race

"n-th from the end" with no length computation: run a `fast` pointer `n` steps ahead, then walk `slow` and `fast` in lockstep. When `fast` reaches the end, `slow` is exactly at the node *before* the target — because the gap between them is exactly `n`.

**Why `slow` lands one *before* the target:** we need the *predecessor* to unlink. Start both at a dummy; advance `fast` by `n`; then move both until `fast.next == null`. `slow` is now at the node whose successor is the target → `slow.next = slow.next.next`.

**Why the dummy is mandatory:** when `n == list length`, the target is the *head* — there's no predecessor. The dummy (`dummy.next = head`) gives the head a predecessor, so the unlink works uniformly.

## Approach 1 — Two passes

Count the length, then walk to `len - n` and unlink: $O(n)$ time but two passes and careful off-by-ones. Correct, but the problem explicitly wants one pass.

## Approach 2 — One pass with offset pointers (optimal)

```kotlin
/**
 * @param head the head of the linked list
 * @param n    the distance from the end of the node to remove (1-based)
 * @return     the head of the list with the n-th-from-end node removed
 */
fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
    val dummy = ListNode(0)      // gives the real head a predecessor
    dummy.next = head

    var fast: ListNode? = dummy
    var slow: ListNode? = dummy

    // Give fast an n-step head start.
    repeat(n) { fast = fast?.next }

    // Walk both until fast is at the last node.
    while (fast?.next != null) {
        fast = fast?.next
        slow = slow?.next
    }

    // slow.next is the target — unlink it.
    slow?.next = slow?.next?.next

    return dummy.next
}
```

```java
public class RemoveNthNodeFromEnd {
    /**
     * @param head the head of the linked list
     * @param n    the distance from the end of the node to remove (1-based)
     * @return     the head of the list with the n-th-from-end node removed
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode fast = dummy, slow = dummy;

        for (int i = 0; i < n; i++) fast = fast.next;   // n-step head start

        while (fast.next != null) {                     // walk to the last node
            fast = fast.next;
            slow = slow.next;
        }
        slow.next = slow.next.next;                     // unlink the target
        return dummy.next;
    }
}
```

```cpp
struct ListNode {
    int val;
    ListNode* next;
    ListNode(int x) : val(x), next(nullptr) {}
};

class RemoveNthNodeFromEnd {
public:
    /**
     * @param head the head of the linked list
     * @param n    the distance from the end of the node to remove (1-based)
     * @return     the head of the list with the n-th-from-end node removed
     */
    ListNode* removeNthFromEnd(ListNode* head, int n) {
        ListNode dummy(0);
        dummy.next = head;
        ListNode* fast = &dummy;
        ListNode* slow = &dummy;

        for (int i = 0; i < n; i++) fast = fast->next;
        while (fast->next) {
            fast = fast->next;
            slow = slow->next;
        }
        slow->next = slow->next->next;
        return dummy.next;
    }
};
```

```python
def remove_nth_from_end(head: ListNode | None, n: int) -> ListNode | None:
    """
    @param head: the head of the linked list
    @param n:    the distance from the end of the node to remove (1-based)
    @return:     the head of the list with the n-th-from-end node removed
    """
    dummy = ListNode(0, head)
    fast = slow = dummy

    for _ in range(n):
        fast = fast.next                      # n-step head start

    while fast.next:
        fast = fast.next
        slow = slow.next

    slow.next = slow.next.next                # unlink the target
    return dummy.next
```

```rust
impl Solution {
    /// @param head the head of the linked list
    /// @param n    the distance from the end of the node to remove (1-based)
    /// @return     the head of the list with the n-th-from-end node removed
    pub fn remove_nth_from_end(head: Option<Box<ListNode>>, n: i32) -> Option<Box<ListNode>> {
        let mut dummy = Box::new(ListNode { val: 0, next: head });
        let mut fast = dummy.as_ref() as *const ListNode;
        let mut slow = dummy.as_mut() as *mut ListNode;

        for _ in 0..n {
            // advance fast by n via raw pointers
            unsafe {
                fast = (*fast).next.as_ref().map_or(std::ptr::null(), |b| b.as_ref() as *const ListNode);
            }
        }
        unsafe {
            while !(*fast).next.is_none() {
                fast = (*(*fast).next.as_ref().unwrap()).as_ref() as *const ListNode;
                slow = (*slow).next.as_mut().unwrap().as_mut() as *mut ListNode;
            }
            // unlink
            let target = &mut (*slow).next;
            *target = target.as_mut().unwrap().next.take();
        }
        dummy.next
    }
}
```

> **Rust note:** raw-pointer walks are the pragmatic way to keep this one-pass shape in Rust; the safe alternative would collect indices or use a `Vec` of node refs. The algorithm is identical.

## Dry run

**Input:** `1 -> 2 -> 3 -> 4 -> 5`, `n = 2`

```
dummy -> 1 -> 2 -> 3 -> 4 -> 5 -> null
head start: fast = dummy.next.next (the 3)? No — advance n=2 from dummy:
  fast: dummy -> 1 -> 2   (fast now at 2)
walk until fast.next == null:
  fast=2, slow=dummy -> fast=3, slow=1
  fast=3, slow=1   -> fast=4, slow=2
  fast=4, slow=2   -> fast=5, slow=3
  fast=5: fast.next == null -> stop. slow=3.
unlink: slow.next = slow.next.next -> 3.next = 5
Result: dummy.next = 1 -> 2 -> 3 -> 5 ✓
```

The gap between `fast` (at 5, the last node) and `slow` (at 3) is exactly 2 — so `slow.next` (the 4) is the 2nd from the end. The race is just a physical implementation of "distance = n".

**Edge case — remove the head:** `[1], n = 1` → fast advances to the 1, `fast.next == null` immediately, `slow = dummy`, unlink `dummy.next = dummy.next.next = null`. Result `[]` ✓ — no special case needed, the dummy did its job.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** $O(1)$.

## Variants & follow-ups

- **Rotate List** (`src/main/kotlin/linkedlist/RotateList.kt`) — the same "offset" thinking, but the offset is the rotation amount and you relink instead of unlink.
- **[4.2](linked-list-cycle.md) / [4.5](linked-list-cycle-ii.md)** — the other great races of this chapter.
- **Interview follow-up:** "Why does `slow` stop one BEFORE the target?" Because unlinking needs the predecessor. If we stopped `slow` ON the target, we couldn't reach its predecessor without a third pointer or a previous-iteration memory — the offset race is the clean way to get the predecessor directly.
- **Interview follow-up:** "Without the dummy, what breaks?" When `n == length`, the target is the head and has no predecessor — you'd need an `if (slow == dummy)` style special case (or a different formulation). The dummy removes the branch entirely.
