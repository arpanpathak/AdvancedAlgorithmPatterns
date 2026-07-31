# 7.12 Find Score Of An Array After Marking All Elements

> **Source:** [`src/main/kotlin/heap/FindScoreOfAnArrayAfterMarkingAllElements.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/heap/FindScoreOfAnArrayAfterMarkingAllElements.kt)
> **Pattern:** min-heap with lazy skip · **Core page**

## The Problem

Repeatedly take the **smallest unmarked** element, add it to the score, then mark it and its neighbors.

- Constraints: n ≤ 10⁵; score fits in `Long`.

## Examples

```
Input:  nums = [2,1,3,4,5,2]   -> Output: 7   (take 1, then 3, then 5... = 1+3+... let me verify: 
take 1 (idx 1): mark 1, 0(idx), 2(idx).  remaining 3,4,5,2? indices 3,4,5: take 2 (idx 5, the
smallest unmarked: values 3(idx3),4(idx4),2(idx5) -> take 2): mark 5, 4.  remaining 3 (idx3).
take 3: score = 1 + 2 + 3 = 6?  The known answer for [2,1,3,4,5,2] is 7: take 1, then 3, then 5: 
1 (idx1) marks 1,0,2.  smallest unmarked: 3 (idx2)? idx2 marked!  idx3=4, idx4=5, idx5=2: take 2
(idx5), marks 5,4.  remaining idx3=4.  score = 1+2+4 = 7 ✓ (my earlier 3 was marked).
```

## Intuition — a heap of (value, index); skip already-marked on pop

The smallest unmarked element is the heap top — but marking neighbors can invalidate entries. Pop, **skip if marked**, else score it and mark `i-1, i, i+1`:

```kotlin
val minHeap = PriorityQueue<Pair<Int, Int>> { a, b ->
    if (a.first == b.first) a.second - b.second else a.first - b.first
}
nums.forEachIndexed { index, value -> minHeap.add(Pair(value, index)) }

var score = 0L
while (minHeap.isNotEmpty()) {
    val (value, index) = minHeap.poll()
    if (marked[index]) continue        // already consumed via a neighbor

    score += value
    marked[index] = true
    if (index > 0) marked[index - 1] = true
    if (index < n - 1) marked[index + 1] = true
}
return score
```

**Why the tie-break comparator?** Equal values need an index tie-break so the heap is deterministic (any order is correct; the tie-break makes it stable). The [7.1](top-k-frequent-elements.md) heap-with-comparator pattern.

**Why skip-on-pop?** A neighbor's marking can make a queued element ineligible — the heap doesn't know. The `if (marked[index]) continue` is the [7.3](../ch07-heaps/sliding-window-median.md) lazy-deletion discipline: validate at pop time, not push time.

## Approach 1 — Scan for the min each round (O(n²))

Linear scan per step: correct, slow.

## Approach 2 — Min-heap with lazy skip (the repo's version, optimal)

```kotlin
import java.util.*

class FindScoreOfAnArrayAfterMarkingAllElements {
    /**
     * @param nums input array
     * @return     the final score
     */
    fun findScore(nums: IntArray): Long {
        val n = nums.size
        val marked = BooleanArray(n) { false }

        val minHeap = PriorityQueue<Pair<Int, Int>> { a, b ->
            if (a.first == b.first) a.second - b.second else a.first - b.first
        }

        nums.forEachIndexed { index, value -> minHeap.add(Pair(value, index)) }

        var score = 0L

        while (minHeap.isNotEmpty()) {
            val (value, index) = minHeap.poll()
            if (marked[index]) continue

            score += value
            marked[index] = true
            if (index > 0) marked[index - 1] = true
            if (index < n - 1) marked[index + 1] = true
        }
        return score
    }
}
```

```java
import java.util.*;

public class FindScoreOfAnArrayAfterMarkingAllElements {
    /**
     * @param nums input array
     * @return     the final score
     */
    public long findScore(int[] nums) {
        int n = nums.length;
        boolean[] marked = new boolean[n];

        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) ->
            a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);          // {value, index}

        for (int i = 0; i < n; i++) heap.offer(new int[]{nums[i], i});

        long score = 0;
        while (!heap.isEmpty()) {
            int[] top = heap.poll();
            int idx = top[1];
            if (marked[idx]) continue;

            score += top[0];
            marked[idx] = true;
            if (idx > 0) marked[idx - 1] = true;
            if (idx < n - 1) marked[idx + 1] = true;
        }
        return score;
    }
}
```

```cpp
#include <queue>
#include <vector>

class FindScoreOfAnArrayAfterMarkingAllElements {
public:
    /**
     * @param nums input array
     * @return     the final score
     */
    long long findScore(std::vector<int>& nums) {
        int n = nums.size();
        std::vector<bool> marked(n, false);

        auto cmp = [](const std::pair<int, int>& a, const std::pair<int, int>& b) {
            return a.first != b.first ? a.first > b.first : a.second > b.second;
        };
        std::priority_queue<std::pair<int, int>, std::vector<std::pair<int, int>>, decltype(cmp)> heap(cmp);

        for (int i = 0; i < n; i++) heap.push({nums[i], i});

        long long score = 0;
        while (!heap.empty()) {
            auto [value, idx] = heap.top(); heap.pop();
            if (marked[idx]) continue;

            score += value;
            marked[idx] = true;
            if (idx > 0) marked[idx - 1] = true;
            if (idx < n - 1) marked[idx + 1] = true;
        }
        return score;
    }
};
```

```python
import heapq

def find_score(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     the final score
    """
    n = len(nums)
    marked = [False] * n
    heap = [(v, i) for i, v in enumerate(nums)]
    heapq.heapify(heap)

    score = 0
    while heap:
        value, idx = heapq.heappop(heap)
        if marked[idx]:
            continue

        score += value
        marked[idx] = True
        if idx > 0:
            marked[idx - 1] = True
        if idx < n - 1:
            marked[idx + 1] = True

    return score
```

```rust
use std::cmp::Reverse;
use std::collections::BinaryHeap;

impl Solution {
    /// @param nums input array
    /// @return     the final score
    pub fn find_score(nums: Vec<i32>) -> i64 {
        let n = nums.len();
        let mut marked = vec![false; n];

        let mut heap: BinaryHeap<(Reverse<i32>, Reverse<usize>)> = nums
            .iter().enumerate()
            .map(|(i, &v)| (Reverse(v), Reverse(i)))
            .collect();

        let mut score = 0i64;
        while let Some((Reverse(value), Reverse(idx))) = heap.pop() {
            if marked[idx] { continue; }

            score += value as i64;
            marked[idx] = true;
            if idx > 0 { marked[idx - 1] = true; }
            if idx < n - 1 { marked[idx + 1] = true; }
        }
        score
    }
}
```

## Dry run

**Input:** `nums = [2,1,3,4,5,2]`.

```
heap: (1,1), (2,0), (2,5), (3,2), (4,3), (5,4)
pop (1,1): unmarked.  score=1.  mark 1, 0, 2.
pop (2,0): marked -> skip.
pop (2,5): unmarked.  score=3.  mark 5, 4.
pop (3,2): marked -> skip.
pop (4,3): unmarked.  score=7.  mark 3.
pop (5,4): marked -> skip.

Output: 7 ✓
```

The lazy skip is essential: (2,0) and (3,2) are in the heap when popped, but their indices were marked by the (1,1) take — the `continue` discards them. The heap's sorted order guarantees each take is the globally smallest unmarked element.

## Complexity

**Time.** Each element pushed/popped once:

$$
T(n) = O(n \log n)
$$

**Space.** Heap + marks:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Top K Frequent Elements** ([7.1](top-k-frequent-elements.md)) — the min-heap-with-comparator engine.
- **Sliding Window Median** ([7.3](sliding-window-median.md)) — the lazy-deletion validation family.
- **Interview follow-up:** "Why not delete from the heap when marking?" Heaps don't support arbitrary deletes cheaply — the marked-skip at pop is O(1) per stale entry, total O(n) extra. The [7.3](../ch07-heaps/sliding-window-median.md) "validate at pop" is the standard heap answer.
