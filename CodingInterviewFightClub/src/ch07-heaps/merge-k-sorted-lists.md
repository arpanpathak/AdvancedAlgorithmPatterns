# 7.11 Merge K Sorted Lists

> **Source:** [`src/main/kotlin/linkedlist/MergeKSortedListHeap.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/MergeKSortedListHeap.kt) (+ `MergeKSortedList.kt`, `MergeKSortedListIterative.kt`)
> **Pattern:** k-way heap merge · **Core page**

## The Problem

Merge `k` sorted linked lists into one sorted list.

- Constraints: k ≤ 10⁴; total nodes ≤ 10⁴.

## Examples

```
Input:  lists = [[1,4,5],[1,3,4],[2,6]]
Output: [1,1,2,3,4,4,5,6]
```

## Intuition — a heap of k heads, always pop the smallest

The [4.x](../ch04-linked-lists/pattern-primer.md) two-list merge generalized: instead of comparing 2 fronts, compare **k** fronts. A min-heap holds the current head of each list; pop the min, append, push its next:

```kotlin
val pq = PriorityQueue<ListNode>(compareBy { it.`val` })
for (list in lists) list?.let { pq.offer(it) }

val dummy = ListNode(0)
var current = dummy

while (pq.isNotEmpty()) {
    val smallest = pq.poll()
    current.next = smallest
    current = smallest

    smallest.next?.let { pq.offer(it) }     // refill with the popped list's next
}
return dummy.next
```

**Why a heap and not a scan?** Scanning k fronts per pick is O(k·n); the heap makes each pick O(log k). The [7.1](top-k-frequent-elements.md) min-heap engine, nodes as entries.

**Why refill after pop?** Each list contributes at most one candidate at a time — after the smallest node is consumed, its list's *next* node becomes that list's candidate. The heap stays size ≤ k.

## Approach 1 — Sequential pairwise merge (O(n·k))

Merge list 1+2, then +3, ...: correct, quadratic in k.

## Approach 2 — Heap merge (the repo's version, optimal)

```kotlin
import java.util.*

class MergeKSortedListHeap {
    /**
     * @param lists k sorted linked lists
     * @return      merged sorted list
     */
    fun mergeKLists(lists: Array<ListNode?>): ListNode? {
        val dummy = ListNode(0)
        var current = dummy
        val pq = PriorityQueue<ListNode>(compareBy { it.`val` })

        for (list in lists) list?.let { pq.offer(it) }

        while (pq.isNotEmpty()) {
            val smallest = pq.poll()
            current.next = smallest
            current = smallest

            smallest.next?.let { pq.offer(it) }
        }
        return dummy.next
    }
}
```

```java
import java.util.*;

public class MergeKSortedLists {
    /**
     * @param lists k sorted linked lists
     * @return      merged sorted list
     */
    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);

        for (ListNode list : lists) if (list != null) pq.offer(list);

        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;

        while (!pq.isEmpty()) {
            ListNode smallest = pq.poll();
            cur.next = smallest;
            cur = smallest;

            if (smallest.next != null) pq.offer(smallest.next);
        }
        return dummy.next;
    }
}
```

```cpp
#include <queue>
#include <vector>

class MergeKSortedLists {
    struct Cmp {
        bool operator()(ListNode* a, ListNode* b) { return a->val > b->val; }
    };

public:
    /**
     * @param lists k sorted linked lists
     * @return      merged sorted list
     */
    ListNode* mergeKLists(std::vector<ListNode*>& lists) {
        std::priority_queue<ListNode*, std::vector<ListNode*>, Cmp> pq;

        for (ListNode* list : lists) if (list) pq.push(list);

        ListNode dummy(0);
        ListNode* cur = &dummy;

        while (!pq.empty()) {
            ListNode* smallest = pq.top(); pq.pop();
            cur->next = smallest;
            cur = smallest;

            if (smallest->next) pq.push(smallest->next);
        }
        return dummy.next;
    }
};
```

```python
import heapq

def merge_k_lists(lists: list[Optional["ListNode"]]) -> Optional["ListNode"]:
    """
    @param lists: k sorted linked lists
    @return:      merged sorted list
    """
    heap = [(head.val, i, head) for i, head in enumerate(lists) if head]
    heapq.heapify(heap)

    dummy = ListNode(0)
    cur = dummy

    while heap:
        _, i, node = heapq.heappop(heap)
        cur.next = node
        cur = node

        if node.next:
            heapq.heappush(heap, (node.next.val, i, node.next))

    return dummy.next
```

```rust
use std::cmp::Ordering;
use std::collections::BinaryHeap;

impl PartialOrd for ListNode { fn partial_cmp(&self, o: &Self) -> Option<Ordering> { Some(self.cmp(o)) } }
impl Ord for ListNode { fn cmp(&self, o: &Self) -> Ordering { o.val.cmp(&self.val) } }
impl PartialEq for ListNode { fn eq(&self, o: &Self) -> bool { self.val == o.val } }
impl Eq for ListNode {}

impl Solution {
    /// @param lists k sorted linked lists
    /// @return      merged sorted list
    pub fn merge_k_lists(lists: Vec<Option<Box<ListNode>>>) -> Option<Box<ListNode>> {
        let mut heap: BinaryHeap<Box<ListNode>> = BinaryHeap::new();
        for list in lists.into_iter().flatten() { heap.push(list); }

        let mut dummy = Box::new(ListNode::new(0));
        let mut cur = &mut dummy;

        while let Some(mut smallest) = heap.pop() {
            if let Some(next) = smallest.next.take() { heap.push(next); }
            cur.next = Some(smallest);
            cur = cur.next.as_mut().unwrap();
        }
        dummy.next
    }
}
```

## Dry run

**Input:** `lists = [[1,4,5],[1,3,4],[2,6]]`.

```
heap: (1, l1), (1, l2), (2, l3)
pop 1 (l1): append.  push l1.next = 4.  heap: (1,l2), (2,l3), (4,l1)
pop 1 (l2): append.  push l2.next = 3.  heap: (2,l3), (3,l2), (4,l1)
pop 2 (l3): append.  push l3.next = 6.  heap: (3,l2), (4,l1), (6,l3)
pop 3 (l2): append.  push l2.next = 4.  heap: (4,l1), (4,l2), (6,l3)
pop 4 (l1): append.  push l1.next = 5.  heap: (4,l2), (5,l1), (6,l3)
pop 4 (l2): append.  no next.
pop 5 (l1): append.  pop 6 (l3): append.

Output: 1 -> 1 -> 2 -> 3 -> 4 -> 4 -> 5 -> 6 ✓
```

The heap always holds one candidate per non-empty list — the k smallest current heads. Each pop-refill cycle advances exactly one node; the tie (1,1) breaks by list identity (stable). Total nodes n → n pops × O(log k).

## Complexity

**Time.** n pops × log k:

$$
T(n, k) = O(n \log k)
$$

**Space.** The heap:

$$
S(n, k) = O(k)
$$

## Variants & follow-ups

- **Merge Two Sorted Lists** — the k=2 special case ([4.x](../ch04-linked-lists/pattern-primer.md) two-pointer merge).
- **Kth Smallest Element In A Sorted Matrix** (`matrix/`) — the same heap-over-k-rows idea on rows.
- **Interview follow-up:** "Why is heap-merge better than pairwise?" Pairwise merge does k merges of growing lists — each node moves O(k) times. The heap moves each node once (pop + push), total O(n log k). The `MergeKSortedListIterative.kt` variant uses divide-and-conquer pairwise (O(n log k) too, no heap) — name both.
