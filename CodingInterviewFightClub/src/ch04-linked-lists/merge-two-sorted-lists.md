# 4.3 Merge Two Sorted Lists

> **Source:** [`src/main/kotlin/linkedlist/MergeTwoSortedLIst.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/MergeTwoSortedLIst.kt)
> **Pattern:** dummy node + compare-and-advance · **Core page**

## The Problem

Merge two sorted linked lists into one sorted list. Return the merged list's head.

- Constraints: $0 \le n, m \le 50$ (both lists sorted ascending).

## Examples

```
Input:  list1 = 1 -> 2 -> 4, list2 = 1 -> 3 -> 4
Output: 1 -> 1 -> 2 -> 3 -> 4 -> 4

Input:  list1 = [], list2 = []           -> Output: []
Input:  list1 = [], list2 = [0]          -> Output: [0]
```

## Intuition — take the smaller head, always

Both lists are sorted, so at every step the *smallest remaining node overall* is one of the two heads. Compare, detach the smaller, append it to the merged tail, advance that list. This is the compare-and-advance merge from [4.0](pattern-primer.md) — $O(1)$ work per node, no backtracking.

**The dummy node** absorbs the "empty input" and "head is chosen from which list?" cases: we build the result *after* a sentinel, and return `dummy.next` at the end. Without it, the very first comparison would need a special case for "is this the first node?"

**The tail extension:** when one list empties, the rest of the other list can be appended **by reference** (no copying) — its nodes are already in order and non-overlapping with the merged part.

## Approach 1 — Copy to arrays, merge, rebuild

Dump both lists to arrays, do the classic merge, rebuild a list: $O(n+m)$ time, $O(n+m)$ space. Correct but pointless — the list version is the same merge without the arrays.

## Approach 2 — Dummy-node merge (optimal)

```kotlin
/**
 * @param list1 head of the first sorted list
 * @param list2 head of the second sorted list
 * @return      head of the merged sorted list
 */
fun mergeTwoLists(list1: ListNode?, list2: ListNode?): ListNode? {
    val dummy = ListNode(0)        // sentinel: no head special cases
    var tail = dummy

    var a = list1
    var b = list2

    while (a != null && b != null) {
        if (a.`val` <= b.`val`) {
            tail.next = a          // take from list1
            a = a.next
        } else {
            tail.next = b          // take from list2
            b = b.next
        }
        tail = tail.next
    }

    tail.next = a ?: b             // append the remainder by reference
    return dummy.next
}
```

```java
public class MergeTwoSortedLists {
    /**
     * @param list1 head of the first sorted list
     * @param list2 head of the second sorted list
     * @return      head of the merged sorted list
     */
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0), tail = dummy;
        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                tail.next = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }
        tail.next = list1 != null ? list1 : list2;
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

class MergeTwoSortedLists {
public:
    /**
     * @param list1 head of the first sorted list
     * @param list2 head of the second sorted list
     * @return      head of the merged sorted list
     */
    ListNode* mergeTwoLists(ListNode* list1, ListNode* list2) {
        ListNode dummy(0);
        ListNode* tail = &dummy;
        while (list1 && list2) {
            if (list1->val <= list2->val) {
                tail->next = list1;
                list1 = list1->next;
            } else {
                tail->next = list2;
                list2 = list2->next;
            }
            tail = tail->next;
        }
        tail->next = list1 ? list1 : list2;
        return dummy.next;
    }
};
```

```python
def merge_two_lists(list1: ListNode | None, list2: ListNode | None) -> ListNode | None:
    """
    @param list1: head of the first sorted list
    @param list2: head of the second sorted list
    @return:      head of the merged sorted list
    """
    dummy = tail = ListNode(0)          # sentinel: no head special cases
    while list1 and list2:
        if list1.val <= list2.val:
            tail.next = list1           # take from list1
            list1 = list1.next
        else:
            tail.next = list2           # take from list2
            list2 = list2.next
        tail = tail.next
    tail.next = list1 or list2          # append the remainder by reference
    return dummy.next
```

```rust
impl Solution {
    /// @param list1 head of the first sorted list
    /// @param list2 head of the second sorted list
    /// @return      head of the merged sorted list
    pub fn merge_two_lists(
        list1: Option<Box<ListNode>>,
        list2: Option<Box<ListNode>>,
    ) -> Option<Box<ListNode>> {
        let mut dummy = Box::new(ListNode::new(0));
        let mut tail = &mut dummy;

        let (mut a, mut b) = (list1, list2);
        while a.is_some() && b.is_some() {
            if a.as_ref().unwrap().val <= b.as_ref().unwrap().val {
                tail.next = a;
                a = tail.next.as_mut().unwrap().next.take();
            } else {
                tail.next = b;
                b = tail.next.as_mut().unwrap().next.take();
            }
            tail = tail.next.as_mut().unwrap();
        }
        tail.next = a.or(b);
        dummy.next
    }
}
```

> **Rust note:** the ownership dance (`take()` to extract the remainder before moving) is the price of Rust's memory safety; the algorithm is identical.

## Dry run

**Input:** `list1 = 1 -> 2 -> 4`, `list2 = 1 -> 3 -> 4`

```
dummy -> null
a=1, b=1: 1 <= 1 -> take a's 1.  tail: dummy->1.  a=2
a=2, b=1: 2 <= 1? no -> take b's 1.  tail: ...->1.  b=3
a=2, b=3: 2 <= 3 -> take a's 2.  a=4
a=4, b=3: 4 <= 3? no -> take b's 3.  b=4
a=4, b=4: 4 <= 4 -> take a's 4.  a=null
b=4 remains -> tail.next = b (the 4)
Result: dummy.next = 1 -> 1 -> 2 -> 3 -> 4 -> 4 ✓
```

Note the equal-value tie (`1 <= 1`) prefers list1 — harmless here, but worth noticing since a stable-merge argument (if the problem ever asks about stability) depends on it.

## Complexity

**Time.**

$$
T(n, m) = O(n + m)
$$

**Space.** $O(1)$ (nodes are reused, not copied; the dummy is a single sentinel).

## Variants & follow-ups

- **Merge K Sorted Lists** (`src/main/kotlin/linkedlist/MergeKSortedList.kt` + heap + iterative variants) — pairwise merge this page $k-1$ times ($O(kn^2)$) vs. a heap-based $k$-way merge ($O(nk \log k)$). The repo ships all three; the heap version is the interview answer.
- **Add Two Numbers** (`src/main/kotlin/linkedlist/AddTwoNumbers.kt`) — same tail-building skeleton, but the "value" is computed (carry) instead of chosen.
- **Interview follow-up:** "Merge recursively?" `merge(a, b) = a.val <= b.val ? (a.next = merge(a.next, b); a) : (b.next = merge(a, b.next); b)` — elegant, $O(n+m)$ stack. Good to write second.
- **Interview follow-up:** "Why is appending the remainder by reference safe?" The remaining nodes are already sorted and all larger than the merged tail; they overlap nothing already consumed — attaching the pointer (not copying) is both correct and $O(1)$.
