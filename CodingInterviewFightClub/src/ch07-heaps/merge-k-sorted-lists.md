# 7.11 Merge K Sorted Lists

> **Source:** [`src/main/kotlin/linkedlist/MergeKSortedListHeap.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/MergeKSortedListHeap.kt) (heap) · [`MergeKSortedList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/MergeKSortedList.kt) (divide & conquer, **top-down**) · [`MergeKSortedListIterative.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/MergeKSortedListIterative.kt) (divide & conquer, **bottom-up**)
> **Pattern:** k-way heap merge / divide & conquer · **Core page**

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

## Approach 3 — Divide & Conquer, Top-Down (recursive; the repo's `MergeKSortedList.kt`)

### The idea: turn "merge k lists" into "merge 2 lists, log k times"

The heap attack compares *all k fronts at once*. Divide & conquer sidesteps the comparison machinery entirely: **you already know how to merge two sorted lists** — so just keep pairing lists up until only one remains. This is literally merge sort with *lists* playing the role of *elements*:

> `mergeKLists(lists) = mergeTwoLists( mergeKLists(left half), mergeKLists(right half) )`

Split the array of lists in half, recursively merge each half into one sorted list, then merge those two. The recursion bottoms out at a single list — which is already sorted — and unwinds by merging. The shape is a balanced binary tree of merges, exactly $\lceil \log_2 k \rceil$ levels deep.

**Why is the halving the whole trick?** With *sequential* pairwise merging (Approach 1), the first list gets merged into the result $k-1$ times — its nodes move $O(k)$ times each. With halving, every list's nodes are merged once per level, and there are only $\lceil \log_2 k \rceil$ levels. That's the difference between $O(nk)$ and $O(n \log k)$ — the entire point of this approach.

```kotlin
class MergeKSortedList {
    fun mergeTwoLists(list1: ListNode?, list2: ListNode?): ListNode? {
        val head = ListNode(0) // dummy node
        var ptr = head

        var ptr1 = list1
        var ptr2 = list2

        while (ptr1 != null && ptr2 != null) {
            if (ptr1.`val` < ptr2.`val`) {
                ptr.next = ListNode(ptr1.`val`)
                ptr1 = ptr1.next
            } else {
                ptr.next = ListNode(ptr2.`val`)
                ptr2 = ptr2.next
            }
            ptr = ptr.next!!
        }

        ptr.next = ptr1 ?: ptr2

        return head.next
    }

    fun mergeKLists(lists: Array<ListNode?>): ListNode? {
        if (lists.isEmpty())
            return null

        fun merge(start: Int, end: Int): ListNode? {
            if (start == end)
                return lists[start]                     // one list left: already sorted
            val mid = start + (end - start) / 2
            val left = merge(start, mid)                // sorted merge of the left half
            val right = merge(mid + 1, end)             // sorted merge of the right half

            return mergeTwoLists(left, right)           // combine the two halves
        }

        return merge(0, lists.lastIndex)
    }
}
```

```python
def merge_two_lists(l1, l2):
    dummy = ListNode(0)
    cur = dummy
    while l1 and l2:
        if l1.val < l2.val:
            cur.next, l1 = l1, l1.next
        else:
            cur.next, l2 = l2, l2.next
        cur = cur.next
    cur.next = l1 or l2
    return dummy.next

def merge_k_lists(lists):
    def merge(lo, hi):
        if lo == hi:            # single list -> already sorted
            return lists[lo]
        mid = (lo + hi) // 2
        return merge_two_lists(merge(lo, mid), merge(mid + 1, hi))
    return merge(0, len(lists) - 1) if lists else None
```

```java
public class MergeKSortedLists {
    /**
     * @param lists k sorted linked lists
     * @return      merged sorted list (divide & conquer, top-down)
     */
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists.length == 0) return null;
        return merge(lists, 0, lists.length - 1);
    }

    private ListNode merge(ListNode[] lists, int lo, int hi) {
        if (lo == hi) return lists[lo];                    // one list left
        int mid = lo + (hi - lo) / 2;
        return mergeTwoLists(merge(lists, lo, mid), merge(lists, mid + 1, hi));
    }

    private ListNode mergeTwoLists(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), cur = dummy;
        while (a != null && b != null) {
            if (a.val < b.val) { cur.next = a; a = a.next; }
            else               { cur.next = b; b = b.next; }
            cur = cur.next;
        }
        cur.next = (a != null) ? a : b;
        return dummy.next;
    }
}
```

```cpp
class MergeKSortedLists {
public:
    /**
     * @param lists k sorted linked lists
     * @return      merged sorted list (divide & conquer, top-down)
     */
    ListNode* mergeKLists(std::vector<ListNode*>& lists) {
        if (lists.empty()) return nullptr;
        return merge(lists, 0, (int)lists.size() - 1);
    }

private:
    ListNode* merge(std::vector<ListNode*>& lists, int lo, int hi) {
        if (lo == hi) return lists[lo];
        int mid = lo + (hi - lo) / 2;
        return mergeTwoLists(merge(lists, lo, mid), merge(lists, mid + 1, hi));
    }

    ListNode* mergeTwoLists(ListNode* a, ListNode* b) {
        ListNode dummy(0);
        ListNode* cur = &dummy;
        while (a && b) {
            if (a->val < b->val) { cur->next = a; a = a->next; }
            else                 { cur->next = b; b = b->next; }
            cur = cur->next;
        }
        cur->next = a ? a : b;
        return dummy.next;
    }
};
```

```rust
impl Solution {
    /// @param lists k sorted linked lists
    /// @return      merged sorted list (divide & conquer, top-down)
    pub fn merge_k_lists(lists: Vec<Option<Box<ListNode>>>) -> Option<Box<ListNode>> {
        fn merge_two(mut a: Option<Box<ListNode>>, mut b: Option<Box<ListNode>>)
            -> Option<Box<ListNode>> {
            let mut dummy = Box::new(ListNode::new(0));
            let mut cur = &mut dummy;
            while a.is_some() && b.is_some() {
                let pick = if a.as_ref().unwrap().val < b.as_ref().unwrap().val { &mut a } else { &mut b };
                cur.next = pick.take();
                cur = cur.next.as_mut().unwrap();
            }
            cur.next = a.or(b);
            dummy.next
        }

        fn merge(lists: &[Option<Box<ListNode>>], lo: usize, hi: usize)
            -> Option<Box<ListNode>> {
            if lo == hi { return lists[lo].clone(); }
            let mid = lo + (hi - lo) / 2;
            merge_two(merge(lists, lo, mid), merge(lists, mid + 1, hi))
        }

        if lists.is_empty() { None } else { merge(&lists, 0, lists.len() - 1) }
    }
}
```

### Reading the code — what's actually happening

- **`mergeTwoLists` is the building block** (the [4.3](../ch04-linked-lists/merge-two-sorted-lists.md) two-pointer merge): a dummy head, a walker that always attaches the *smaller* of the two fronts, then the tail append `ptr.next = ptr1 ?: ptr2` when one list runs out. Everything above it is just an *orchestration* of this one function.
- **`if (start == end) return lists[start]` is the base case.** A segment of one list is trivially "already merged" — it's sorted by the problem statement. This is what stops the recursion.
- **`mid = start + (end - start) / 2` splits the segment.** The `(end - start) / 2` form (instead of `(start + end) / 2`) is overflow-safe for huge `k` — the [1.0](../ch01-binary-search/pattern-primer.md) hygiene.
- **`left` and `right` are computed *before* combining.** The recursion dives to the leaves first (post-order): both halves must be fully merged into single sorted lists before `mergeTwoLists` can combine them. That's the divide-and-conquer contract — and it's why the call graph is a balanced binary tree with $k$ leaves.
- **`merge(0, lists.lastIndex)` covers the whole array.** The empty-array guard returns `null` first, and a single-list input falls straight through to the base case.

### Dry run

**Input:** `lists = [[1,4,5],[1,3,4],[2,6]]`, so `k = 3`.

```
merge(0, 2)  ── mid = 1
├─ merge(0, 1) ── mid = 0
│  ├─ merge(0, 0) = [1,4,5]
│  └─ merge(1, 1) = [1,3,4]
│  └─ mergeTwoLists([1,4,5], [1,3,4]) = [1,1,3,4,4,5]
└─ merge(2, 2) = [2,6]
└─ mergeTwoLists([1,1,3,4,4,5], [2,6]) = [1,1,2,3,4,4,5,6]

Output: [1,1,2,3,4,4,5,6] ✓
```

Watch how the tree's height is $\lceil \log_2 3 \rceil = 2$: every node is merged exactly twice (once at the bottom pair level, once at the top), never $k$ times. That's the $O(\log k)$ factor in action.

### Correctness proof (top-down)

**Lemma (two-list merge).** For any two sorted lists $A, B$, `mergeTwoLists(A, B)` returns the sorted list containing exactly the elements of $A \cup B$.

*Proof by loop invariant.* The invariant: after each iteration, the output chain from `dummy.next` to `ptr` is sorted and contains exactly the elements of $A$ and $B$ consumed so far, and every remaining element of both lists is $\ge$ the last node's value. The chosen front is the smaller of the two current heads; since each list is sorted, that front is the *minimum of all remaining elements*, so appending it preserves sortedness and the invariant. When one list empties, its tail is appended: every element of that tail is $\ge$ the last consumed value (the two heads were compared at the previous step, and the survivor was the larger), so sortedness holds. The output is complete because every element is consumed exactly once. ∎

**Theorem (top-down correctness).** `merge(lo, hi)` returns the sorted merge of the original lists `lists[lo..hi]`.

*Proof by induction on segment length* $m = hi - lo + 1$.
- **Base ($m = 1$):** returns `lists[lo]`, which is sorted by the problem statement — correct.
- **Step ($m > 1$):** By the induction hypothesis, `merge(lo, mid)` is the sorted merge of `lists[lo..mid]` and `merge(mid+1, hi)` is the sorted merge of `lists[mid+1..hi]`. By the Lemma, `mergeTwoLists` of those two sorted lists is their sorted merge. Together that is exactly the sorted merge of `lists[lo..hi]`. ∎

Therefore `merge(0, k-1)` is the sorted merge of all $k$ lists — the answer.

## Approach 4 — Divide & Conquer, Bottom-Up (iterative; the repo's `MergeKSortedListIterative.kt`)

### The idea: do the same pairwise merging, but with a loop instead of a recursion stack

Top-down recursion *conceptually* builds a merge tree top-to-bottom; bottom-up builds the exact same tree **level by level, from the leaves up**. The classic trick is **interval doubling**:

> Round 1 merges pairs `(0,1), (2,3), (4,5), …` into `lists[0], lists[2], lists[4], …`. Round 2 merges `(0,2), (4,6), …` — because `lists[0]` now *is* the merge of originals 0–1 and `lists[2]` the merge of 2–3. After $\lceil \log_2 k \rceil$ rounds, `lists[0]` holds the merge of everything.

Each round doubles the *span* of the merge stored at each write position — hence "interval doubling". It's the same balanced tree as Approach 3, just traversed in breadth-first order, and it needs **zero extra space beyond the input array** (no recursion stack).

```kotlin
class MergeKSortedListIterative {
    fun mergeKLists(lists: Array<ListNode?>): ListNode? {
        if (lists.isEmpty()) return null

        var interval = 1
        val n = lists.size
        while (interval < n) {
            for (i in 0 until n - interval step interval * 2) {
                lists[i] = mergeTwoLists(lists[i], lists[i + interval])
            }
            interval *= 2
        }

        return lists[0]
    }

    fun mergeTwoLists(list1: ListNode?, list2: ListNode?): ListNode? {
        val head = ListNode(0) // Dummy node
        var ptr = head

        var ptr1 = list1
        var ptr2 = list2

        while (ptr1 != null && ptr2 != null) {
            if (ptr1.`val` < ptr2.`val`) {
                ptr.next = ptr1
                ptr1 = ptr1.next
            } else {
                ptr.next = ptr2
                ptr2 = ptr2.next
            }
            ptr = ptr.next!!
        }

        ptr.next = ptr1 ?: ptr2

        return head.next
    }
}
```

```python
def merge_k_lists(lists):
    if not lists:
        return None
    interval = 1
    n = len(lists)
    while interval < n:
        for i in range(0, n - interval, interval * 2):
            lists[i] = merge_two_lists(lists[i], lists[i + interval])
        interval *= 2
    return lists[0]
```

```java
public class MergeKSortedListsIterative {
    /**
     * @param lists k sorted linked lists
     * @return      merged sorted list (divide & conquer, bottom-up)
     */
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists.length == 0) return null;

        int interval = 1;
        while (interval < lists.length) {
            for (int i = 0; i < lists.length - interval; i += interval * 2) {
                lists[i] = mergeTwoLists(lists[i], lists[i + interval]);
            }
            interval *= 2;
        }
        return lists[0];
    }

    private ListNode mergeTwoLists(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), cur = dummy;
        while (a != null && b != null) {
            if (a.val < b.val) { cur.next = a; a = a.next; }
            else               { cur.next = b; b = b.next; }
            cur = cur.next;
        }
        cur.next = (a != null) ? a : b;
        return dummy.next;
    }
}
```

```cpp
class MergeKSortedListsIterative {
public:
    /**
     * @param lists k sorted linked lists
     * @return      merged sorted list (divide & conquer, bottom-up)
     */
    ListNode* mergeKLists(std::vector<ListNode*>& lists) {
        if (lists.empty()) return nullptr;

        int interval = 1;
        while (interval < (int)lists.size()) {
            for (int i = 0; i < (int)lists.size() - interval; i += interval * 2) {
                lists[i] = mergeTwoLists(lists[i], lists[i + interval]);
            }
            interval *= 2;
        }
        return lists[0];
    }

private:
    ListNode* mergeTwoLists(ListNode* a, ListNode* b) {
        ListNode dummy(0);
        ListNode* cur = &dummy;
        while (a && b) {
            if (a->val < b->val) { cur->next = a; a = a->next; }
            else                 { cur->next = b; b = b->next; }
            cur = cur->next;
        }
        cur->next = a ? a : b;
        return dummy.next;
    }
};
```

```rust
impl Solution {
    /// @param lists k sorted linked lists
    /// @return      merged sorted list (divide & conquer, bottom-up)
    pub fn merge_k_lists(mut lists: Vec<Option<Box<ListNode>>>) -> Option<Box<ListNode>> {
        if lists.is_empty() { return None; }

        fn merge_two(mut a: Option<Box<ListNode>>, mut b: Option<Box<ListNode>>)
            -> Option<Box<ListNode>> {
            let mut dummy = Box::new(ListNode::new(0));
            let mut cur = &mut dummy;
            while a.is_some() && b.is_some() {
                let pick = if a.as_ref().unwrap().val < b.as_ref().unwrap().val { &mut a } else { &mut b };
                cur.next = pick.take();
                cur = cur.next.as_mut().unwrap();
            }
            cur.next = a.or(b);
            dummy.next
        }

        let mut interval = 1;
        let n = lists.len();
        while interval < n {
            let mut i = 0;
            while i + interval < n {
                let right = lists[i + interval].take();
                let left = lists[i].take();
                lists[i] = merge_two(left, right);
                i += interval * 2;
            }
            interval *= 2;
        }
        lists[0].take()
    }
}
```

### Reading the code — what's actually happening

- **`interval` is the span being merged at each write position.** With `interval = 1`, we merge adjacent originals; with `interval = 2`, we merge the results of two adjacent pairs; the span doubles every round. The `while (interval < n)` condition keeps doubling until one merged list covers everything.
- **The `for` loop's step is `interval * 2`** — the write positions `0, 2·interval, 4·interval, …` are exactly the start indices of the current spans, and the loop guard `i < n - interval` ensures `lists[i + interval]` is a valid partner. Positions that aren't multiples of `2·interval` become garbage after their span is folded into a lower index — nobody reads them again, which is why in-place reuse is safe.
- **`lists[i] = mergeTwoLists(lists[i], lists[i + interval])` folds the pair into the lower slot.** After the round, `lists[i]` *is* the sorted merge of the original lists `i .. i + 2·interval - 1` — the input for the next round's bigger spans.
- **`return lists[0]` after the loop.** Once `interval >= n`, the span starting at 0 covers all original lists, so `lists[0]` is the complete answer. A single-list input skips the loop entirely and returns `lists[0]` directly.

### Dry run

**Input:** `lists = [[1,4,5],[1,3,4],[2,6]]`, so `n = 3`.

```
interval = 1:
  i = 0: lists[0] = merge([1,4,5], [1,3,4]) = [1,1,3,4,4,5]
  i = 2: lists[2] = merge([2,6], null)      = [2,6]
interval = 2:
  i = 0: lists[0] = merge([1,1,3,4,4,5], [2,6]) = [1,1,2,3,4,4,5,6]
interval = 4: 4 < 3? no -> exit.

Output: lists[0] = [1,1,2,3,4,4,5,6] ✓
```

Note how `k = 3` is *not* a power of two — the loop still works because `n - interval` caps each round's merge count, and the leftover `lists[2]` rides along until the final round. Same merge tree as Approach 3, built with two loops and no call stack.

### Correctness proof (bottom-up)

**Invariant.** At the start of the round with `interval = d`, for every index `i` that is a multiple of `2d`, `lists[i]` holds the sorted merge of the original lists `i .. i + d - 1`.

- **Base ($d = 1$):** every index `i` (a multiple of 2) holds original `lists[i]`, which is the sorted merge of the single list `i .. i` — trivially true.
- **Step:** During the round, each write position `i` (multiple of `2d`) merges `lists[i]` — the sorted merge of `i .. i + d - 1` (by invariant) — with `lists[i + d]`, the sorted merge of `i + d .. i + 2d - 1` (by invariant, since `i + d` is a multiple of `d`). By the two-list-merge Lemma, the result is the sorted merge of `i .. i + 2d - 1`, stored back into `lists[i]` — exactly the invariant for the next round with `interval = 2d`. ∎

**Termination and conclusion.** `interval` doubles every round, so after at most $\lceil \log_2 k \rceil$ rounds `interval \ge n` and the loop exits. The invariant at that point says `lists[0]` is the sorted merge of `0 .. 0 + interval - 1 \supseteq 0 .. n - 1` — the full answer. ∎

## Why divide & conquer beats sequential pairwise — the proof that matters

The heap, top-down D&C, and bottom-up D&C all hit $O(n \log k)$. The naive "merge list 1+2, then +3, …" hits $O(nk)$. The reason is a counting argument on **how many times each node gets re-pointed**:

- **Sequential pairwise:** when list $i$ is merged in, it is the *second* argument of a merge whose result keeps growing. Node $x$ from list $1$ passes through $k - 1$ merge calls — it is examined $O(k)$ times. Total: $O(nk)$.
- **Divide & conquer (either flavor):** each node lives in exactly one merge per level of the tree, and the tree has $\lceil \log_2 k \rceil$ levels. Each node is examined $O(\log k)$ times. Total: $O(n \log k)$.

The halving is not a micro-optimization — it changes the exponent of $k$. That single sentence is the interview answer to "why not just merge one by one?"

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

All four approaches in one table ($n$ = total nodes, $k$ = number of lists):

| Approach | Time | Extra Space | Notes |
|---|---|---|---|
| 1. Sequential pairwise | $O(nk)$ | $O(1)$ | each node re-examined $k$ times — the one to avoid |
| 2. Heap merge | $O(n \log k)$ | $O(k)$ | one pop+push per node; heap holds $k$ heads |
| 3. D&C top-down | $O(n \log k)$ | $O(\log k)$ (call stack) | recursion tree has $\lceil \log_2 k \rceil$ levels |
| 4. D&C bottom-up | $O(n \log k)$ | $O(1)$ | interval doubling in place; no stack, no heap |

**Why all three good ones are $O(n \log k)$:** each node is merged into a bigger list once per *level* of the merge tree, and the tree has $\lceil \log_2 k \rceil$ levels — whether the levels are realized by a heap of $k$ candidates (approach 2), a recursion tree (approach 3), or interval doubling (approach 4). The constant factors differ (the heap does a $\log k$ comparison per node; D&C does plain two-front comparisons), which is why the D&C versions are often a hair faster in practice despite the same bound.

$$
T(n, k) = O(n \log k), \qquad S(n, k) = O(k) \text{ (heap) / } O(\log k) \text{ (top-down) / } O(1) \text{ (bottom-up)}
$$

## Variants & follow-ups

- **Merge Two Sorted Lists** — the k=2 special case ([4.3](../ch04-linked-lists/merge-two-sorted-lists.md) two-pointer merge): the `mergeTwoLists` building block every approach here reuses.
- **Kth Smallest Element In A Sorted Matrix** (`matrix/`) — the same heap-over-k-rows idea on rows.
- **External merge sort** — when the lists don't fit in memory, the bottom-up D&C *is* the standard answer: merge runs of size $B$, then $2B$, $4B$, … on disk. Same interval-doubling, different medium — a favorite system-design follow-up.
- **Interview follow-up:** "Heap or divide & conquer?" Heap: $O(n \log k)$ time, $O(k)$ space, simplest to explain, works on any "k smallest heads" setup. Top-down D&C: same time, $O(\log k)$ stack, no heap bookkeeping. Bottom-up D&C: same time, $O(1)$ extra space, no recursion — the winner when $k$ is huge and you can mutate the input array. Name all three and say why each shines.
- **Interview follow-up:** "Why is `MergeKSortedListIterative.kt`'s loop correct when it mutates `lists` in place?" Because the invariant says each write position `i` (a multiple of the current span) only ever merges two *completed* spans into a bigger one at a *lower* index — the values written are always merges of the original lists, never partial work. Positions past `i + interval` are read before they're written in this round, and their results are folded into yet-lower indices next round. The in-place reuse is safe precisely because the merge tree is *left-leaning into the start of the array*.
- **Interview follow-up:** "What's the worst case for the recursion depth in top-down?" Exactly $\lceil \log_2 k \rceil$ — the depth of a balanced binary tree with $k$ leaves, independent of list lengths. (A naive `merge(lists[0], mergeKLists(rest))` recursion would be depth $k$ — that's just sequential pairwise wearing a recursion costume, still $O(nk)$.)
