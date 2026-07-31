# 4.8 Middle Of The Linked List

> **Source:** [`src/main/kotlin/linkedlist/MiddleNode.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/MiddleNode.kt)
> **Pattern:** slow-fast pointers · **Core page**

## The Problem

Return the **middle node** of a linked list (the second middle when even-length).

- Constraints: $1 \le n \le 100$.

## Examples

```
Input:  head = [1,2,3,4,5]   -> Output: node 3
Input:  head = [1,2,3,4,5,6] -> Output: node 4   (second of the two middles)
```

## Intuition — the fast pointer doubles the slow one

Two pointers from the head: `slow` advances one step, `fast` two. When `fast` reaches the end, `slow` is exactly at the middle — because `slow` has traveled half of `fast`'s distance:

```kotlin
var slow = head
var fast = head
while (fast?.next != null) {
    slow = slow?.next
    fast = fast.next?.next
}
return slow
```

**Why does the loop condition `fast?.next != null` give the *second* middle?** With even length, `fast` lands on `null` after the last node — `slow` stops one past the true middle, i.e. the second middle. The [4.2](linked-list-cycle.md) Floyd template with a different stop rule.

**Why not count then walk?** Counting is two passes; slow-fast is one pass and O(1) space — the [4.2](linked-list-cycle.md)/[4.6](find-the-duplicate-number.md) pointer-choreography family, here in its simplest form.

## Approach 1 — Count then walk (two passes)

First pass counts nodes, second pass walks `n/2`: correct, two passes.

## Approach 2 — Slow-fast pointers (the repo's version, optimal)

```kotlin
class MiddleNode {
    /**
     * @param head list head
     * @return     the middle node
     */
    fun middleNode(head: ListNode?): ListNode? {
        if (head == null) return null

        var slow = head
        var fast = head

        while (fast?.next != null) {
            slow = slow?.next
            fast = fast.next?.next
        }
        return slow
    }
}
```

```java
public class MiddleOfTheLinkedList {
    /**
     * @param head list head
     * @return     the middle node
     */
    public ListNode middleNode(ListNode head) {
        ListNode slow = head, fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }
}
```

```cpp
class MiddleOfTheLinkedList {
public:
    /**
     * @param head list head
     * @return     the middle node
     */
    ListNode* middleNode(ListNode* head) {
        ListNode* slow = head;
        ListNode* fast = head;

        while (fast && fast->next) {
            slow = slow->next;
            fast = fast->next->next;
        }
        return slow;
    }
};
```

```python
def middle_node(head: Optional["ListNode"]) -> Optional["ListNode"]:
    """
    @param head: list head
    @return:     the middle node
    """
    slow = fast = head

    while fast and fast.next:
        slow = slow.next
        fast = fast.next.next
    return slow
```

```rust
impl Solution {
    /// @param head list head
    /// @return     the middle node
    pub fn middle_node(head: Option<Box<ListNode>>) -> Option<Box<ListNode>> {
        let mut slow = &head;
        let mut fast = &head;

        while fast.as_ref().is_some_and(|f| f.next.is_some()) {
            slow = &slow.as_ref().unwrap().next;
            fast = &fast.as_ref().unwrap().next.as_ref().unwrap().next;
        }
        slow.clone()
    }
}
```

## Dry run

**Input:** `head = [1,2,3,4,5]` (odd).

```
slow=1, fast=1
fast.next != null: slow=2, fast=3
fast.next != null: slow=3, fast=5
fast.next == null -> stop.  return slow = 3 ✓

Input: head = [1,2,3,4,5,6] (even):
slow=1, fast=1 -> slow=2, fast=3 -> slow=3, fast=5 -> slow=4, fast=null.  return 4 ✓
```

The doubling is exact: `fast` covers 2 steps per `slow` step, so when `fast` exhausts, `slow` has covered half. The even case stops with `fast == null` (not `fast.next == null`) — leaving `slow` at the **second** middle, which is what the problem wants.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Two pointers:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Linked List Cycle** ([4.2](linked-list-cycle.md)) — the same slow/fast pair, detecting cycles instead of halves.
- **Palindrome Linked List** ([4.9](palindrome-linked-list.md)) — uses this page's middle-finding as step 1.
- **Interview follow-up:** "What if you want the *first* middle for even lengths?" Stop one step earlier: iterate while `fast.next?.next != null`. The two-pointer speed is unchanged — only the stop condition picks which middle.
