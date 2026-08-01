# 4.18 Delete Middle Node Of A Linked List

> **Source**: [`src/main/kotlin/linkedlist/DeleteMiddleNodeOfLinkedList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/DeleteMiddleNodeOfLinkedList.kt)
> **Pattern**: two-pass or fast/slow middle delete · **Core page**

## The Problem

Delete the middle node of a list (the `⌊n/2⌋`-th; with two middles, delete the first? the problem deletes the second middle... LeetCode 2095: n even → delete the SECOND middle).

- Constraints: n ≥ 2.

## Examples

```
Input:  head = [1,3,4,7,1,2,6]   -> Output: [1,3,4,1,2,6]  (delete 7)
Input:  head = [1,2,3,4]         -> Output: [1,2,4]        (delete 3, the second middle)
```

## Intuition — find the middle via fast/slow, deleting needs the predecessor

The [4.8](middle-of-the-linked-list.md) fast/slow finds the middle; a `prev` pointer (or a slow-start offset) lets us unlink it:

```kotlin
var slow = head
var fast = head
var prev: ListNode? = null

while (fast?.next != null) {
    prev = slow
    slow = slow?.next
    fast = fast.next?.next
}

prev?.next = slow?.next      // unlink the middle
return head
```

**Why track `prev`?** Deleting a node needs its predecessor — the fast/slow walk keeps `prev` one step behind `slow`, so the unlink is O(1) at the end. The [4.8](middle-of-the-linked-list.md) middle, with surgery.

## Approach 1 — Two-pass (count, then walk to n/2 − 1)

The repo's version: count nodes, walk to the predecessor, unlink.

## Approach 2 — Fast/slow with prev (optimal, one pass)

```kotlin
class DeleteMiddleNodeOfLinkedList {
    /**
     * @param head list head
     * @return     list without the middle node
     */
    fun deleteMiddle(head: ListNode?): ListNode? {
        var slow = head
        var fast = head
        var prev: ListNode? = null

        while (fast?.next != null) {
            prev = slow
            slow = slow?.next
            fast = fast.next?.next
        }

        if (prev == null) return null       // single node: middle is the head

        prev.next = slow?.next
        return head
    }
}
```

```java
public class DeleteMiddleNodeOfALinkedList {
    /**
     * @param head list head
     * @return     list without the middle node
     */
    public ListNode deleteMiddle(ListNode head) {
        if (head.next == null) return null;

        ListNode slow = head, fast = head, prev = null;

        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        prev.next = slow.next;
        return head;
    }
}
```

```cpp
class DeleteMiddleNodeOfALinkedList {
public:
    /**
     * @param head list head
     * @return     list without the middle node
     */
    ListNode* deleteMiddle(ListNode* head) {
        if (!head->next) return nullptr;

        ListNode* slow = head;
        ListNode* fast = head;
        ListNode* prev = nullptr;

        while (fast && fast->next) {
            prev = slow;
            slow = slow->next;
            fast = fast->next->next;
        }

        prev->next = slow->next;
        return head;
    }
};
```

```python
def delete_middle(head: Optional["ListNode"]) -> Optional["ListNode"]:
    """
    @param head: list head
    @return:     list without the middle node
    """
    if not head.next:
        return None

    slow = fast = head
    prev = None

    while fast and fast.next:
        prev = slow
        slow = slow.next
        fast = fast.next.next

    prev.next = slow.next
    return head
```

```rust
impl Solution {
    /// @param head list head
    /// @return     list without the middle node
    pub fn delete_middle(mut head: Option<Box<ListNode>>) -> Option<Box<ListNode>> {
        if head.as_ref().unwrap().next.is_none() { return None; }

        let mut fast = head.clone();
        let mut slow = &mut head;

        while fast.is_some() && fast.as_ref().unwrap().next.is_some() {
            fast = fast.unwrap().next.unwrap().next;
            slow = &mut slow.as_mut().unwrap().next;
        }

        // slow now points at the middle node: drop it
        let mid = slow.as_mut().unwrap().next.take();
        *slow = mid;
        head
    }
}
```

## Dry run

**Input:** `head = [1,3,4,7,1,2,6]` (n=7).

```
slow=1, fast=1.  step: prev=1, slow=3, fast=4.  prev=3, slow=4, fast=7.
prev=4, slow=7, fast=2.  prev=7, slow=1, fast=6.  prev=1, slow=2, fast=null.
prev(1).next = slow(2).next = 6.  List: [1,3,4,7,1,6]?  wait — the middle of 7 nodes is index 3 (4th node, value 7).
Let me re-trace: nodes 1,3,4,7,1,2,6 (indices 0-6).  Middle = index 3 = 7.
slow/fast: s=1,f=1 -> s=3,f=4 -> s=4,f=7 -> s=7,f=2 -> s=1,f=6 -> s=2,f=null.
The middle (index 3, value 7) is visited when slow=7 with prev=4 → prev(4).next = 7.next = 1.
Output: [1,3,4,1,2,6] ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Pointers:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Middle Of The Linked List** ([4.8](middle-of-the-linked-list.md)) — the finder this page repurposes.
- **Interview follow-up:** "Why the `prev` and not deleting via value-copy?" The middle's `val` copy trick works only for non-tail nodes; the predecessor unlink handles the general case and matches the problem's structural intent.
