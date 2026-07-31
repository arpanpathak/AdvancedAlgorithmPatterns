# 4.14 Maximum Twin Sum Of A Linked List

> **Source:** [`src/main/kotlin/linkedlist/MaximumTwinSumOfALinkedList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/MaximumTwinSumOfALinkedList.kt)
> **Pattern:** middle + reverse + pair walk · **Core page**

## The Problem

Max `(node[i] + node[n-1-i])` over the first half's twins.

- Constraints: n even, 2 ≤ n ≤ 10⁵.

## Examples

```
Input:  head = [5,4,2,1]   -> Output: 6   (5+1, 4+2)
Input:  head = [4,2,2,3]   -> Output: 7   (4+3, 2+2)
```

## Intuition — fold the list: middle, reverse, compare

The [4.8](middle-of-the-linked-list.md) middle + [4.1](reverse-linked-list.md) reverse + a paired walk — the [4.9](palindrome-linked-list.md) choreography without the equality test:

```kotlin
val middle = findMiddle(head)     // first middle
val reversed = reverse(middle)    // second half, reversed
var maxSum = 0
var first = head
var second = reversed

while (second != null) {
    maxSum = maxOf(maxSum, first!!.`val` + second.`val`)
    first = first.next
    second = second.next
}
return maxSum
```

**Why reverse the second half?** The twin of `node[i]` is at position `n-1-i` — walking both halves *toward each other* pairs them naturally. One reverse makes the pairing a linear walk.

## Approach 1 — Values to array (O(n) space)

Copy to an array, pair indices: correct, violates the O(1)-space spirit.

## Approach 2 — Reverse-half walk (the repo's version, optimal)

```kotlin
class MaximumTwinSumOfALinkedList {
    /**
     * @param head list head
     * @return     max twin sum
     */
    fun pairSum(head: ListNode?): Int {
        val middle = findMiddle(head)
        val reversed = reverse(middle)

        var maxSum = 0
        var first = head
        var second = reversed

        while (second != null) {
            maxSum = maxOf(maxSum, first!!.`val` + second.`val`)
            first = first.next
            second = second.next
        }
        return maxSum
    }

    private fun findMiddle(head: ListNode?): ListNode? {
        var slow = head
        var fast = head
        while (fast?.next != null && fast.next?.next != null) {
            slow = slow?.next
            fast = fast.next?.next
        }
        return slow
    }

    private fun reverse(head: ListNode?): ListNode? {
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
public class MaximumTwinSumOfALinkedList {
    /**
     * @param head list head
     * @return     max twin sum
     */
    public int pairSum(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) { slow = slow.next; fast = fast.next.next; }

        ListNode prev = null, cur = slow.next;
        while (cur != null) { ListNode next = cur.next; cur.next = prev; prev = cur; cur = next; }

        int best = 0;
        ListNode first = head, second = prev;
        while (second != null) {
            best = Math.max(best, first.val + second.val);
            first = first.next;
            second = second.next;
        }
        return best;
    }
}
```

```cpp
class MaximumTwinSumOfALinkedList {
public:
    /**
     * @param head list head
     * @return     max twin sum
     */
    int pairSum(ListNode* head) {
        ListNode* slow = head, *fast = head;
        while (fast->next && fast->next->next) { slow = slow->next; fast = fast->next->next; }

        ListNode* prev = nullptr, *cur = slow->next;
        while (cur) { ListNode* next = cur->next; cur->next = prev; prev = cur; cur = next; }

        int best = 0;
        ListNode* first = head;
        ListNode* second = prev;
        while (second) {
            best = std::max(best, first->val + second->val);
            first = first->next;
            second = second->next;
        }
        return best;
    }
};
```

```python
def pair_sum(head: Optional["ListNode"]) -> int:
    """
    @param head: list head
    @return:     max twin sum
    """
    slow = fast = head
    while fast.next and fast.next.next:
        slow = slow.next
        fast = fast.next.next

    prev, cur = None, slow.next
    while cur:
        nxt = cur.next
        cur.next = prev
        prev, cur = cur, nxt

    best = 0
    first, second = head, prev
    while second:
        best = max(best, first.val + second.val)
        first = first.next
        second = second.next
    return best
```

```rust
impl Solution {
    /// @param head list head
    /// @return     max twin sum
    pub fn pair_sum(head: Option<Box<ListNode>>) -> i32 {
        // middle
        let mut slow = &head;
        let mut fast = &head;
        while fast.as_ref().is_some_and(|f| f.next.as_ref().is_some_and(|n| n.next.is_some())) {
            slow = &slow.as_ref().unwrap().next;
            fast = &fast.as_ref().unwrap().next.as_ref().unwrap().next;
        }
        // reverse the second half from slow.next (Rust: rebuild the boxes)
        let mut second = slow.as_ref().unwrap().next.clone();
        let mut prev = None;
        while let Some(mut node) = second {
            let next = node.next.take();
            node.next = prev;
            prev = Some(node);
            second = next;
        }

        let mut best = 0;
        let mut first = &head;
        let mut second = &prev;
        while let Some(s) = second {
            best = best.max(first.as_ref().unwrap().val + s.val);
            first = &first.as_ref().unwrap().next;
            second = &s.next;
        }
        best
    }
}
```

## Dry run

**Input:** `head = [5,4,2,1]`.

```
middle: slow stops at node 4 (index 1).  reverse [2,1] -> [1,2].
paired walk: 5+1 = 6.  4+2 = 6.  best = 6 ✓
Input: [4,2,2,3]: 4+3 = 7.  2+2 = 4.  best = 7 ✓
```

## Complexity

**Time.** Middle + reverse + walk:

$$
T(n) = O(n)
$$

**Space.** Pointers:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Palindrome Linked List** ([4.9](palindrome-linked-list.md)) — the same fold, equality instead of max.
- **Interview follow-up:** "Why reverse from `slow.next` and not the middle node?" Reversing from `slow.next` leaves the first half (including the middle) intact for the paired walk — the twins are `first` (head-side) and `reversed` (tail-side), and the second half has exactly n/2 nodes.
