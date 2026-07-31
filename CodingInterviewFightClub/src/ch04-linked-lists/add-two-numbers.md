# 4.7 Add Two Numbers

> **Source:** [`src/main/kotlin/linkedlist/AddTwoNumbers.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/AddTwoNumbers.kt)
> **Pattern:** digit-wise addition with carry · **Core page**

## The Problem

Two numbers stored as **reversed** linked lists (`2→4→3` = 342), return their sum as a reversed list (`7→0→8` = 807).

- Constraints: $1 \le n, m \le 100$; digits 0–9.

## Examples

```
Input:  l1 = [2,4,3], l2 = [5,6,4]   -> Output: [7,0,8]   (342 + 465 = 807)
Input:  l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9] -> Output: [8,9,9,9,0,0,0,1]
```

## Intuition — add like a child does: digit by digit, carry the overflow

The reversed layout is a gift — least-significant digits first means a **single left-to-right pass** mirrors real addition:

```
carry = 0; dummy head
while l1 or l2 or carry:
    sum = l1.val (or 0) + l2.val (or 0) + carry
    next.val = sum % 10
    carry = sum / 10
    advance whichever lists aren't exhausted
```

**Why the `?: 0` elvis?** The two lists can differ in length — a missing digit contributes 0. The loop runs *while either list or the carry* remains, so a trailing `carry = 1` (e.g. `999 + 1`) gets its own final node.

**Why a dummy head?** The result's first node is created inside the loop; a dummy `ListNode(0)` lets `ptr.next = ...` work uniformly without a "first node special case" — the same sentinel idiom as [4.1](reverse-linked-list.md)'s pointers and [18.1](../ch18-design-caches/lru-cache.md)'s head/tail dummies.

**The repo's `carry = if (sum > 9) 1 else 0`** — digits are 0-9, so `sum ≤ 19` and the carry is always 0 or 1; `sum / 10` is the compressed spelling.

## Approach 1 — Convert to integers (overflow!)

Read both lists into `Long`, add, re-emit: breaks on 100-digit numbers — the problem's hidden constraint.

## Approach 2 — Digit-wise with carry (the repo's version, optimal)

```kotlin
class ListNode(var `val`: Int) {
    var next: ListNode? = null
}

class AddTwoNumbers {
    /**
     * @param l1 first number (reversed digits)
     * @param l2 second number (reversed digits)
     * @return   the sum (reversed digits)
     */
    fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
        var carry = 0
        val head = ListNode(0)               // dummy head
        var ptr = head
        var (n1, n2) = Pair(l1, l2)

        while (n1 != null || n2 != null) {
            val sum = (n1?.`val` ?: 0) + (n2?.`val` ?: 0) + carry
            ptr.next = ListNode(sum % 10)
            ptr = ptr.next!!

            carry = if (sum > 9) 1 else 0

            if (n1 != null) n1 = n1.next
            if (n2 != null) n2 = n2.next
        }

        if (carry > 0) {                     // the final carry gets its own node
            ptr.next = ListNode(carry)
        }
        return head.next
    }
}
```

```java
public class AddTwoNumbers {
    /**
     * @param l1 first number (reversed digits)
     * @param l2 second number (reversed digits)
     * @return   the sum (reversed digits)
     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode head = new ListNode(0), ptr = head;   // dummy head
        int carry = 0;

        while (l1 != null || l2 != null) {
            int sum = (l1 == null ? 0 : l1.val) + (l2 == null ? 0 : l2.val) + carry;
            ptr.next = new ListNode(sum % 10);
            ptr = ptr.next;
            carry = sum / 10;

            if (l1 != null) l1 = l1.next;
            if (l2 != null) l2 = l2.next;
        }

        if (carry > 0) ptr.next = new ListNode(carry);   // final carry node
        return head.next;
    }
}
```

```cpp
class AddTwoNumbers {
public:
    /**
     * @param l1 first number (reversed digits)
     * @param l2 second number (reversed digits)
     * @return   the sum (reversed digits)
     */
    ListNode* addTwoNumbers(ListNode* l1, ListNode* l2) {
        ListNode* head = new ListNode(0);     // dummy head
        ListNode* ptr = head;
        int carry = 0;

        while (l1 || l2) {
            int sum = (l1 ? l1->val : 0) + (l2 ? l2->val : 0) + carry;
            ptr->next = new ListNode(sum % 10);
            ptr = ptr->next;
            carry = sum / 10;

            if (l1) l1 = l1->next;
            if (l2) l2 = l2->next;
        }

        if (carry) ptr->next = new ListNode(carry);   // final carry node
        return head->next;
    }
};
```

```python
def add_two_numbers(l1: Optional["ListNode"], l2: Optional["ListNode"]) -> Optional["ListNode"]:
    """
    @param l1: first number (reversed digits)
    @param l2: second number (reversed digits)
    @return:   the sum (reversed digits)
    """
    head = ListNode(0)           # dummy head
    ptr = head
    carry = 0

    while l1 or l2:
        sum_ = (l1.val if l1 else 0) + (l2.val if l2 else 0) + carry
        ptr.next = ListNode(sum_ % 10)
        ptr = ptr.next
        carry = sum_ // 10

        if l1: l1 = l1.next
        if l2: l2 = l2.next

    if carry:                    # final carry node
        ptr.next = ListNode(carry)
    return head.next
```

```rust
impl Solution {
    /// @param l1 first number (reversed digits)
    /// @param l2 second number (reversed digits)
    /// @return   the sum (reversed digits)
    pub fn add_two_numbers(l1: Option<Box<ListNode>>, l2: Option<Box<ListNode>>) -> Option<Box<ListNode>> {
        let mut dummy = Some(Box::new(ListNode::new(0)));   // dummy head
        let mut ptr = &mut dummy;
        let (mut n1, mut n2) = (l1, l2);
        let mut carry = 0;

        while n1.is_some() || n2.is_some() || carry > 0 {
            let v1 = n1.as_ref().map_or(0, |n| n.val);
            let v2 = n2.as_ref().map_or(0, |n| n.val);
            let sum = v1 + v2 + carry;

            ptr.as_mut().unwrap().next = Some(Box::new(ListNode::new(sum % 10)));
            ptr = &mut ptr.as_mut().unwrap().next;
            carry = sum / 10;

            n1 = n1.and_then(|n| n.next);
            n2 = n2.and_then(|n| n.next);
        }
        dummy.unwrap().next
    }
}
```

## Dry run

**Input:** `l1 = [2,4,3]`, `l2 = [5,6,4]` (342 + 465).

```
carry=0
n1=2, n2=5: sum = 2+5+0 = 7.  node 7.  carry = 7/10 = 0.
n1=4, n2=6: sum = 4+6+0 = 10. node 0.  carry = 1.
n1=3, n2=4: sum = 3+4+1 = 8.  node 8.  carry = 0.
lists exhausted, carry 0 -> stop.

Output: [7,0,8] ✓   (807 = 342 + 465)
```

The carry hand-off is the whole algorithm: at the tens place, `4+6` overflows to `0` and carries `1` into the hundreds. The trailing-carry branch handles `9999 + 1`: four 0-nodes then a final `1` — `[0,0,0,1]` prepended, exactly `10000`.

## Complexity

**Time.** One pass over the longer list:

$$
T(n, m) = O(\max(n, m))
$$

**Space.** The result list (plus O(1) extra):

$$
S(n, m) = O(\max(n, m))
$$

## Variants & follow-ups

- **Add Two Numbers II** — the *non-reversed* version: reverse both lists first (or use stacks), then the same carry loop.
- **Multiply Strings** (`math/MultiplyStrings.kt`) — digit-wise multiplication instead of addition: per-digit products into a running array.
- **Interview follow-up:** "Why is the reversed layout convenient?" Real addition propagates carries right-to-left; reversed lists make that a left-to-right scan — no stack needed. The `sum % 10 / sum / 10` pair is the entire digit-wise arithmetic.
