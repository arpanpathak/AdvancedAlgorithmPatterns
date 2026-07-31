# 4.15 Odd Even Linked List

> **Source**: [`src/main/kotlin/linkedlist/OddEvenLinkedList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/OddEvenLinkedList.kt)
> **Pattern**: dual-thread relinking · **Core page**

## The Problem

Group nodes by **position**: all odd-indexed first, then even-indexed.

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  head = [1,2,3,4,5]   -> Output: [1,3,5,2,4]
Input:  head = [2,1,3,5,6,4,7]  -> Output: [2,3,6,7,1,5,4]
```

## Intuition — two threads: odd.next = odd.next.next; even likewise

`odd` and `even` walkers each skip one node; the even head is saved to stitch at the end:

```kotlin
var odd = head
var even = head?.next
var evenHead = even

while (even?.next != null) {
    odd?.next = odd?.next?.next
    odd = odd?.next

    even.next = even.next?.next
    even = even.next
}

odd?.next = evenHead
return head
```

**Why the two-skip?** Nodes at odd positions link to the next odd (skip the even between) — same for evens. The `while (even?.next != null)` guard handles both parities of list length.

## Approach 1 — Collect into lists, rebuild (O(n) space)

Gather odds/evens, rewire: correct, wasteful.

## Approach 2 — Dual-thread relink (the repo's version, optimal)

```kotlin
class OddEvenLinkedList {
    /**
     * @param head list head
     * @return     odd-then-even grouped list
     */
    fun oddEvenList(head: ListNode?): ListNode? {
        var odd = head
        var even = head?.next
        var evenHead = even

        while (even?.next != null) {
            odd?.next = odd?.next?.next
            odd = odd?.next

            even.next = even.next?.next
            even = even.next
        }

        odd?.next = evenHead
        return head
    }
}
```

```java
public class OddEvenLinkedList {
    /**
     * @param head list head
     * @return     odd-then-even grouped list
     */
    public ListNode oddEvenList(ListNode head) {
        if (head == null) return null;

        ListNode odd = head, even = head.next, evenHead = even;

        while (even != null && even.next != null) {
            odd.next = odd.next.next;
            odd = odd.next;

            even.next = even.next.next;
            even = even.next;
        }

        odd.next = evenHead;
        return head;
    }
}
```

```cpp
class OddEvenLinkedList {
public:
    /**
     * @param head list head
     * @return     odd-then-even grouped list
     */
    ListNode* oddEvenList(ListNode* head) {
        if (!head) return nullptr;

        ListNode* odd = head;
        ListNode* even = head->next;
        ListNode* evenHead = even;

        while (even && even->next) {
            odd->next = odd->next->next;
            odd = odd->next;

            even->next = even->next->next;
            even = even->next;
        }

        odd->next = evenHead;
        return head;
    }
};
```

```python
def odd_even_list(head: Optional["ListNode"]) -> Optional["ListNode"]:
    """
    @param head: list head
    @return:     odd-then-even grouped list
    """
    if not head:
        return None

    odd, even = head, head.next
    even_head = even

    while even and even.next:
        odd.next = odd.next.next
        odd = odd.next

        even.next = even.next.next
        even = even.next

    odd.next = even_head
    return head
```

```rust
impl Solution {
    /// @param head list head
    /// @return     odd-then-even grouped list
    pub fn odd_even_list(mut head: Option<Box<ListNode>>) -> Option<Box<ListNode>> {
        let mut odd = head.as_mut();
        let mut even = odd.as_mut().and_then(|o| o.next.as_mut());

        while even.as_ref().is_some_and(|e| e.next.is_some()) {
            // odd.next = odd.next.next
            let odd_next = odd.as_mut().unwrap().next.as_mut().unwrap().next.take();
            odd.as_mut().unwrap().next = odd_next;
            odd = odd.as_mut().unwrap().next.as_mut();

            // even.next = even.next.next
            let even_next = even.as_mut().unwrap().next.as_mut().unwrap().next.take();
            even.as_mut().unwrap().next = even_next;
            even = even.as_mut().unwrap().next.as_mut();
        }

        // odd.next = evenHead (the original head.next, saved before mutation)
        let even_head = head.as_ref().and_then(|h| h.next.clone());
        odd.as_mut().unwrap().next = even_head;
        head
    }
}
```

## Dry run

**Input:** `head = [1,2,3,4,5]`.

```
odd=1, even=2, evenHead=2.
even.next=3: odd.next = 1->3.  odd=3.  even.next = 2->4.  even=4.
even.next=5: odd.next = 3->5.  odd=5.  even.next = 4->null.  even=null.
odd.next = evenHead: 5->2.
Output: 1->3->5->2->4 ✓
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

- **Reorder List** — the same two-thread relinking with a reverse.
- **Interview follow-up:** "Why save `evenHead`?" The even thread's head is orphaned when the loop starts (odd's next is rewired) — saving it before the mutations and stitching `odd.next = evenHead` at the end completes the regrouping.
