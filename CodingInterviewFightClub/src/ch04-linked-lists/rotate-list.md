# 4.16 Rotate List

> **Source**: [`src/main/kotlin/linkedlist/RotateList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/RotateList.kt)
> **Pattern**: circularize + cut · **Core page**

## The Problem

Rotate the list right by `k` positions.

- Constraints: n ≤ 500; k ≤ 2×10⁹.

## Examples

```
Input:  head = [1,2,3,4,5], k = 2   -> Output: [4,5,1,2,3]
Input:  head = [0,1,2], k = 4       -> Output: [2,0,1]
```

## Intuition — make it circular, then cut at the rotation point

Find the length and tail; `rotations = k % length`; link tail→head; walk `length - rotations` steps and break — the new head is there:

```kotlin
var length = 1
var tail = head
while (tail?.next != null) { tail = tail.next; length++ }

val rotations = k % length
if (rotations == 0) return head

tail.next = head                  // circularize
var steps = length - rotations
var newTail = head
while (steps > 1) { newTail = newTail?.next; steps-- }

val newHead = newTail?.next
newTail?.next = null              // cut
return newHead
```

**Why `k % length`?** Rotating by the full length returns the original — the modulo drops the redundant laps. The [3.x](../ch03-arrays/pattern-primer.md) rotation trick, on a list.

## Approach 1 — Move the tail k times (O(nk))

Rotate one step per k: correct, slow.

## Approach 2 — Circularize + cut (the repo's version, optimal)

```kotlin
class RotateList {
    /**
     * @param head list head
     * @param k    rotations
     * @return     rotated list head
     */
    fun rotateRight(head: ListNode?, k: Int): ListNode? {
        if (head == null) return null

        var length = 1
        var tail = head
        while (tail?.next != null) {
            tail = tail.next
            length++
        }

        val rotations = k % length
        if (rotations == 0) return head

        tail.next = head
        var steps = length - rotations
        var newTail = head
        while (steps > 1) { newTail = newTail?.next; steps-- }

        val newHead = newTail?.next
        newTail?.next = null
        return newHead
    }
}
```

```java
public class RotateList {
    /**
     * @param head list head
     * @param k    rotations
     * @return     rotated list head
     */
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null) return head;

        int length = 1;
        ListNode tail = head;
        while (tail.next != null) { tail = tail.next; length++; }

        int rotations = k % length;
        if (rotations == 0) return head;

        tail.next = head;                     // circularize

        int steps = length - rotations;
        ListNode newTail = head;
        while (steps > 1) { newTail = newTail.next; steps--; }

        ListNode newHead = newTail.next;
        newTail.next = null;                  // cut
        return newHead;
    }
}
```

```cpp
class RotateList {
public:
    /**
     * @param head list head
     * @param k    rotations
     * @return     rotated list head
     */
    ListNode* rotateRight(ListNode* head, int k) {
        if (!head || !head->next) return head;

        int length = 1;
        ListNode* tail = head;
        while (tail->next) { tail = tail->next; length++; }

        int rotations = k % length;
        if (rotations == 0) return head;

        tail->next = head;                    // circularize

        int steps = length - rotations;
        ListNode* newTail = head;
        while (steps > 1) { newTail = newTail->next; steps--; }

        ListNode* newHead = newTail->next;
        newTail->next = nullptr;              // cut
        return newHead;
    }
};
```

```python
def rotate_right(head: Optional["ListNode"], k: int) -> Optional["ListNode"]:
    """
    @param head: list head
    @param k:    rotations
    @return:     rotated list head
    """
    if not head or not head.next:
        return head

    length = 1
    tail = head
    while tail.next:
        tail = tail.next
        length += 1

    rotations = k % length
    if rotations == 0:
        return head

    tail.next = head                 # circularize

    steps = length - rotations
    new_tail = head
    while steps > 1:
        new_tail = new_tail.next
        steps -= 1

    new_head = new_tail.next
    new_tail.next = None             # cut
    return new_head
```

```rust
impl Solution {
    /// @param head list head
    /// @param k    rotations
    /// @return     rotated list head
    pub fn rotate_right(mut head: Option<Box<ListNode>>, k: i32) -> Option<Box<ListNode>> {
        if head.is_none() { return None; }

        let mut len = 0;
        let mut tail = &mut head;
        while let Some(n) = tail.as_mut() {
            len += 1;
            tail = &mut n.next;
        }
        let rotations = k % len;
        if rotations == 0 { return head; }

        // circularize: tail.next = head
        if let Some(t) = tail { t.next = head.clone(); }

        let cut_at = len - rotations;
        let mut cur = &mut head;
        for _ in 1..cut_at {
            cur = &mut cur.as_mut().unwrap().next;
        }
        let new_head = cur.as_mut().unwrap().next.take();
        new_head
    }
}
```

## Dry run

**Input:** `head = [1,2,3,4,5], k = 2`.

```
length=5, tail=5.  rotations = 2.  tail.next = head -> circular.
steps = 5-2 = 3.  newTail: 1 -> 2 -> 3 (steps 3->2->1).
newHead = 3.next = 4.  newTail.next = null.
Output: 4->5->1->2->3 ✓
```

## Complexity

**Time.** Length + cut:

$$
T(n) = O(n)
$$

**Space.** Pointers:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Rotate Array** — the array twin (reverse-triple trick).
- **Interview follow-up:** "Why does the modulo matter?" k can exceed n hugely — each full lap returns the list unchanged. `k % length` is the effective shift; skipping the laps keeps the cut walk ≤ n.
