# 4.1 Reverse Linked List

> **Source:** [`src/main/kotlin/linkedlist/ReverseLinkedList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/ReverseLinkedList.kt) · [`ReverseLinkedListIterative.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/ReverseLinkedListIterative.kt)
> **Pattern:** recursion / pointer rewiring · **Core page — the "hello world" of lists**

## The Problem

Reverse a singly linked list and return the new head.

- Constraints: $0 \le n \le 5000$; nodes have `val` and `next`.

## Examples

```
Input:  1 -> 2 -> 3 -> 4 -> 5 -> null
Output: 5 -> 4 -> 3 -> 2 -> 1 -> null

Input:  null
Output: null
```

## Intuition — two ways to see the same rewiring

**Iterative view.** Walk the list, and for each node, flip its `next` to point *backward* instead of forward. The trick is keeping a `prev` pointer so the flip doesn't lose the rest of the list:

```
prev <- cur -> next
      cur.next = prev    (flip)
      prev = cur, cur = next   (step)
```

After the loop, `prev` is the old tail = the new head.

**Recursive view.** `reverse(head)` returns the reversed list of *everything from `head` onward*, with `head` as the new tail. The "aha": after recursing into `head.next`, the sub-list `head.next -> ... -> tail` is already reversed and its tail is `head.next`... wait — after `reverse(head.next)` returns `newHead`, the *old* `head.next` is now the *last* node of the reversed sub-list, so `head.next.next = head` re-attaches `head` at the end, and `head.next = null` seals it.

Both are the same rewiring; the recursive one hides the loop in the call stack.

## Approach 1 — Iterative (optimal, no stack)

```kotlin
/**
 * @param head the head of the singly linked list
 * @return     the head of the reversed list
 */
fun reverseList(head: ListNode?): ListNode? {
    var prev: ListNode? = null
    var cur = head

    while (cur != null) {
        val next = cur.next      // save the rest before we break the link
        cur.next = prev          // flip the pointer backward
        prev = cur               // advance prev
        cur = next               // advance cur
    }
    return prev                  // prev = old tail = new head
}
```

## Approach 2 — Recursive (the repo's version)

```kotlin
/**
 * @param head the head of the singly linked list
 * @return     the head of the reversed list
 */
fun reverseList(head: ListNode?): ListNode? {
    if (head?.next == null) return head          // base: 0 or 1 node

    val reversedHead = reverseList(head.next)    // reverse everything after head

    head.next?.next = head                       // head's successor points back at head
    head.next = null                             // head becomes the new tail

    return reversedHead
}
```

```java
public class ReverseLinkedList {
    /**
     * @param head the head of the singly linked list
     * @return     the head of the reversed list
     */
    public ListNode reverseList(ListNode head) {
        ListNode prev = null, cur = head;
        while (cur != null) {
            ListNode next = cur.next;   // save the rest
            cur.next = prev;            // flip backward
            prev = cur;
            cur = next;
        }
        return prev;
    }
}
```

```cpp
struct ListNode {
    int val;
    ListNode* next;
    ListNode(int x) : val(x), next(nullptr) {}
};

class ReverseLinkedList {
public:
    /**
     * @param head the head of the singly linked list
     * @return     the head of the reversed list
     */
    ListNode* reverseList(ListNode* head) {
        ListNode* prev = nullptr;
        ListNode* cur = head;
        while (cur) {
            ListNode* next = cur->next;   // save the rest
            cur->next = prev;             // flip backward
            prev = cur;
            cur = next;
        }
        return prev;
    }
};
```

```python
def reverse_list(head: ListNode | None) -> ListNode | None:
    """
    @param head: the head of the singly linked list
    @return:     the head of the reversed list
    """
    prev, cur = None, head
    while cur:
        nxt = cur.next        # save the rest before breaking the link
        cur.next = prev       # flip backward
        prev, cur = cur, nxt
    return prev
```

```rust
impl Solution {
    /// @param head the head of the singly linked list
    /// @return     the head of the reversed list
    pub fn reverse_list(head: Option<Box<ListNode>>) -> Option<Box<ListNode>> {
        let mut prev = None;
        let mut cur = head;
        while let Some(mut node) = cur {
            let next = node.next.take();   // save the rest
            node.next = prev;              // flip backward
            prev = Some(node);
            cur = next;
        }
        prev
    }
}
```

> **Rust note:** `Box<ListNode>` forces explicit ownership moves — `node.next.take()` is the idiomatic way to "read then clear" a field, which maps exactly to saving `next` before the flip. The logic is identical to the other four languages.

## Dry run

**Input:** `1 -> 2 -> 3 -> null`

```
Iterative:
cur=1  next=2  1.next=null  prev=1  cur=2
cur=2  next=3  2.next=1     prev=2  cur=3
cur=3  next=null  3.next=2  prev=3  cur=null
return prev = 3 -> 2 -> 1 -> null ✓
```

```
Recursive (call stack):
reverseList(1) -> reverseList(2) -> reverseList(3) -> base (3.next null) returns 3
  back in reverseList(2): head=2, reversedHead=3
    2.next.next = 2.next.next where 2.next=3 -> 3.next = 2
    2.next = null
    return 3
  back in reverseList(1): head=1, reversedHead=3
    1.next.next = 1.next.next where 1.next=2 -> 2.next = 1
    1.next = null
    return 3
Result: 3 -> 2 -> 1 ✓
```

The stack trace shows the recursion "unwinds" the rewiring in reverse order — each frame re-attaches its node at the tail of the already-reversed suffix.

## Complexity

**Time.**

$$
T(n) = O(n)
$$

**Space.** Iterative $O(1)$; recursive $O(n)$ for the call stack.

## Variants & follow-ups

- **Reverse Nodes In K Groups** (`src/main/kotlin/linkedlist/ReverseNodesInKGroups.kt`) — reverse a *segment* at a time; the segment-reverse is this page's loop with explicit bounds.
- **Palindrome Linked List** (`src/main/kotlin/linkedlist/PalindromeLinkedList.kt`) — find the middle (hare), reverse the second half (this page), compare.
- **Swap Nodes In Pairs** (`src/main/kotlin/linkedlist/SwapNodesInPairs.kt`) — the same rewiring discipline at a smaller granularity.
- **Interview follow-up:** "Recursive or iterative — which does the interviewer want?" Iterative for $O(1)$ space and no stack risk; recursive for elegance. Say both, write the iterative one.
- **Interview follow-up:** "Why must we save `next` before the flip?" Because after `cur.next = prev`, the old successor is unreachable — without the saved reference, the rest of the list is lost. This is *the* classic linked-list bug; name it before you're asked.
