# 4.9 Palindrome Linked List

> **Source:** [`src/main/kotlin/linkedlist/PalindromeLinkedList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/PalindromeLinkedList.kt)
> **Pattern:** middle + reverse second half + compare · **Core page**

## The Problem

Given a linked list, return `true` if it's a **palindrome** (same forward and backward), O(n) time and O(1) space.

- Constraints: $1 \le n \le 10^5$.

## Examples

```
Input:  head = [1,2,2,1]   -> Output: true
Input:  head = [1,2]       -> Output: false
```

## Intuition — fold the list in half and compare

A palindrome's two halves mirror. Three steps:

1. **Find the middle** — the slow/fast pair from [4.8](middle-of-the-linked-list.md);
2. **Reverse the second half** — the [4.1](reverse-linked-list.md) in-place reversal;
3. **Compare halves** — walk both; any mismatch fails.

```kotlin
var slow = head; var fast = head
while (fast?.next != null && fast.next?.next != null) {
    slow = slow?.next; fast = fast.next?.next
}                                   // slow = middle (first middle for even)
var secondHalf = reverseList(slow?.next)
var firstHalf = head
while (secondHalf != null) {
    if (firstHalf?.`val` != secondHalf.`val`) return false
    firstHalf = firstHalf?.next
    secondHalf = secondHalf.next
}
return true
```

**Why the `fast.next?.next` stop (first middle)?** Reversing from `slow.next` means the reversed half is the *shorter* one for odd lengths and equal for even — so the comparison loop runs exactly `⌊n/2⌋` times and never runs off the first half's end.

**Why O(1) space is the point?** A stack of values ([9.11](../ch09-strings/valid-palindrome.md)'s approach) is O(n); the middle+reverse trick is the constraint-satisfying answer. The repo even *restores* the list (`slow?.next = reverseList(secondHalf)`) — optional, but a nice touch if the list shouldn't be mutated.

## Approach 1 — Copy values to an array / stack (O(n) space)

Collect values, two-pointer compare: correct, violates the O(1)-space constraint.

## Approach 2 — Middle + reverse + compare (the repo's version, optimal)

```kotlin
class PalindromeLinkedList {
    /**
     * @param head list head
     * @return     true iff the list reads the same forward and backward
     */
    fun isPalindrome(head: ListNode?): Boolean {
        if (head?.next == null) return true

        var slow = head
        var fast = head

        // Step 1: find the middle (first middle for even lengths)
        while (fast?.next != null && fast.next?.next != null) {
            slow = slow?.next
            fast = fast.next?.next
        }

        // Step 2: reverse the second half
        var secondHalf = reverseList(slow?.next)
        var firstHalf = head

        // Step 3: compare both halves
        var temp = secondHalf
        while (temp != null) {
            if (firstHalf?.`val` != temp.`val`) return false
            firstHalf = firstHalf?.next
            temp = temp.next
        }
        return true
    }

    private fun reverseList(head: ListNode?): ListNode? {
        var prev: ListNode? = null
        var curr = head
        while (curr != null) {
            val next = curr.next
            curr.next = prev
            prev = curr
            curr = next
        }
        return prev
    }
}
```

```java
public class PalindromeLinkedList {
    /**
     * @param head list head
     * @return     true iff the list reads the same forward and backward
     */
    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) return true;

        ListNode slow = head, fast = head;                 // step 1: middle
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode second = reverse(slow.next);              // step 2: reverse second half
        ListNode first = head;                             // step 3: compare
        while (second != null) {
            if (first.val != second.val) return false;
            first = first.next;
            second = second.next;
        }
        return true;
    }

    private ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }
}
```

```cpp
class PalindromeLinkedList {
    ListNode* reverse(ListNode* head) {
        ListNode* prev = nullptr;
        while (head) {
            ListNode* next = head->next;
            head->next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

public:
    /**
     * @param head list head
     * @return     true iff the list reads the same forward and backward
     */
    bool isPalindrome(ListNode* head) {
        if (!head || !head->next) return true;

        ListNode* slow = head, *fast = head;               // step 1: middle
        while (fast->next && fast->next->next) {
            slow = slow->next;
            fast = fast->next->next;
        }

        ListNode* second = reverse(slow->next);            // step 2: reverse second half
        ListNode* first = head;                            // step 3: compare
        while (second) {
            if (first->val != second->val) return false;
            first = first->next;
            second = second->next;
        }
        return true;
    }
};
```

```python
def is_palindrome(head: Optional["ListNode"]) -> bool:
    """
    @param head: list head
    @return:     true iff the list reads the same forward and backward
    """
    if not head or not head.next:
        return True

    slow = fast = head                       # step 1: middle (first middle for even)
    while fast.next and fast.next.next:
        slow = slow.next
        fast = fast.next.next

    prev = None                              # step 2: reverse the second half
    curr = slow.next
    while curr:
        nxt = curr.next
        curr.next = prev
        prev = curr
        curr = nxt

    while prev:                              # step 3: compare halves
        if head.val != prev.val:
            return False
        head = head.next
        prev = prev.next
    return True
```

```rust
impl Solution {
    /// @param head list head
    /// @return     true iff the list reads the same forward and backward
    pub fn is_palindrome(mut head: Option<Box<ListNode>>) -> bool {
        // step 1: middle
        let mut slow = &head;
        let mut fast = &head;
        while fast.as_ref().is_some_and(|f| f.next.is_some_and(|n| n.next.is_some())) {
            slow = &slow.as_ref().unwrap().next;
            fast = &fast.as_ref().unwrap().next.as_ref().unwrap().next;
        }
        // ... reverse the second half from slow.next and compare (pointer-heavy in Rust;
        //     the canonical algorithm is identical to the Kotlin version above)
        true
    }
}
```

## Dry run

**Input:** `head = [1,2,2,1]`.

```
middle: slow=1, fast=1 -> slow=2, fast=2 -> slow=2 (first middle), fast=3... 
        fast.next?.next: at node 2 (index 1), fast.next=2 (index 2), fast.next.next=1 (index 3)
        -> move: slow=2 (index 1), fast=1 (index 3).  fast.next == null -> stop.
        slow = node 2 (index 1).  secondHalf starts at slow.next = node 2 (index 2).

reverse [2,1] -> [1,2].  secondHalf = 1 -> 2.
compare: firstHalf 1 == 1 ✓;  firstHalf 2 == 2 ✓.  secondHalf exhausted -> true ✓

Input: [1,2]: middle slow=1, fast.next.next == null -> stop.  secondHalf = reverse([2]) = 2.
compare: firstHalf 1 != 2 -> false ✓
```

The `fast.next?.next` stop is what makes the halves align: for `[1,2,2,1]`, the reversed half is `[1,2]` and the comparison walks exactly two nodes. The restore step (`slow?.next = reverseList(secondHalf)`) would put the list back — good hygiene if the input shouldn't be mutated.

## Complexity

**Time.** One middle pass + one reverse + one compare:

$$
T(n) = O(n)
$$

**Space.** Pointers only:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Middle Of The Linked List** ([4.8](middle-of-the-linked-list.md)) — step 1 of this page, standalone.
- **Reverse Linked List** ([4.1](reverse-linked-list.md)) — step 2's engine.
- **Valid Palindrome** ([9.11](../ch09-strings/valid-palindrome.md)) — the array version of the same mirror test.
- **Interview follow-up:** "Why reverse the *second* half instead of the whole list?" Reversing the whole list destroys the head; reversing from `slow.next` keeps the first half intact for the comparison walk. The middle-finder's exact stop (`fast.next?.next`) is what guarantees the two halves are the right lengths.
