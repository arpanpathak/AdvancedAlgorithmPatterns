# 7.1 Top K Frequent Elements

> **Source:** [`src/main/kotlin/heap/TopKFrequentElements.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/heap/TopKFrequentElements.kt)
> **Pattern:** min-heap of size k · **Core page**

## The Problem

Given an integer array `nums` and an integer `k`, return the `k` **most frequent** elements (any order). The answer is guaranteed unique.

- Constraints: $1 \le n \le 10^5$; $-10^4 \le nums[i] \le 10^4$; $k$ in range.

## Examples

```
Input:  nums = [1,1,1,2,2,3], k = 2
Output: [1,2]          (1 appears 3×, 2 appears 2×, 3 appears 1×)

Input:  nums = [1], k = 1
Output: [1]
```

## Intuition — "count first, then keep the top k"

Two independent phases:

1. **Count** — one pass over `nums` into a frequency map: `freq[num] = occurrences`. $O(n)$.
2. **Select the top k** — the interesting half. The naive way sorts *all* $u$ unique elements by frequency: $O(u \log u)$. But we only need the k largest — and the [keep-the-top-k move](pattern-primer.md) from the primer does it with a **min-heap of size k**.

**Why a min-heap and not a max-heap?** The goal is to *evict* the smallest-frequency element whenever the heap exceeds k. A min-heap's root is exactly the element to evict — `poll()` removes the least-frequent candidate. After processing all unique elements, the heap holds the k largest frequencies. A max-heap would instead hand you the *largest* on every pop, which is useless for eviction.

The comparator is the subtle part: the heap orders by `freqMap[a] - freqMap[b]`, *not* by the raw value. The heap contains values; their *priority* is their frequency.

## Approach 1 — Sort everything

Build the frequency map, sort the unique keys by frequency descending, take the first k: $O(u \log u)$. Simple, correct — and exactly what the heap version beats when $k \ll u$.

## Approach 2 — Min-heap of size k (the repo's version, optimal)

```kotlin
import java.util.*

class TopKFrequentElements {
    /**
     * @param nums input array
     * @param k    how many top-frequency elements to return
     * @return     the k most frequent elements
     */
    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        val freqMap = mutableMapOf<Int, Int>()

        // Phase 1: count frequencies
        nums.forEach { freqMap[it] = freqMap.getOrPut(it) { 0 } + 1 }

        // Phase 2: min-heap that keeps the k largest frequencies
        //          (ordered by frequency, not by value)
        val minHeap = PriorityQueue<Int> { a, b -> freqMap[a]!! - freqMap[b]!! }

        for (num in freqMap.keys) {
            minHeap.offer(num)
            if (minHeap.size > k) {
                minHeap.poll()          // evict the least frequent
            }
        }

        return minHeap.toIntArray()
    }
}
```

```java
import java.util.*;

public class TopKFrequentElements {
    /**
     * @param nums input array
     * @param k    how many top-frequency elements to return
     * @return     the k most frequent elements
     */
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) freq.merge(num, 1, Integer::sum);   // Phase 1: count

        PriorityQueue<Integer> minHeap = new PriorityQueue<>(
            (a, b) -> freq.get(a) - freq.get(b));                 // ordered by frequency

        for (int num : freq.keySet()) {                           // Phase 2: keep top k
            minHeap.offer(num);
            if (minHeap.size() > k) minHeap.poll();               // evict the least frequent
        }

        int[] result = new int[minHeap.size()];
        int i = 0;
        for (int num : minHeap) result[i++] = num;
        return result;
    }
}
```

```cpp
#include <queue>
#include <unordered_map>
#include <vector>

class TopKFrequentElements {
public:
    /**
     * @param nums input array
     * @param k    how many top-frequency elements to return
     * @return     the k most frequent elements
     */
    std::vector<int> topKFrequent(std::vector<int>& nums, int k) {
        std::unordered_map<int, int> freq;
        for (int num : nums) freq[num]++;                        // Phase 1: count

        // min-heap of (frequency, value): ordered by frequency
        auto cmp = [&](int a, int b) { return freq[a] > freq[b]; };
        std::priority_queue<int, std::vector<int>, decltype(cmp)> minHeap(cmp);

        for (auto& [num, _] : freq) {                            // Phase 2: keep top k
            minHeap.push(num);
            if ((int)minHeap.size() > k) minHeap.pop();          // evict the least frequent
        }

        std::vector<int> result;
        while (!minHeap.empty()) { result.push_back(minHeap.top()); minHeap.pop(); }
        return result;
    }
};
```

```python
import heapq

def top_k_frequent(nums: list[int], k: int) -> list[int]:
    """
    @param nums: input array
    @param k:    how many top-frequency elements to return
    @return:     the k most frequent elements
    """
    freq = {}
    for num in nums:                       # Phase 1: count
        freq[num] = freq.get(num, 0) + 1

    # Phase 2: min-heap of size k, ordered by frequency
    min_heap = []
    for num, count in freq.items():
        heapq.heappush(min_heap, (count, num))
        if len(min_heap) > k:
            heapq.heappop(min_heap)        # evict the least frequent
    return [num for _, num in min_heap]
```

```rust
use std::cmp::Reverse;
use std::collections::{BinaryHeap, HashMap};

impl Solution {
    /// @param nums input array
    /// @param k    how many top-frequency elements to return
    /// @return     the k most frequent elements
    pub fn top_k_frequent(nums: Vec<i32>, k: i32) -> Vec<i32> {
        let mut freq: HashMap<i32, i32> = HashMap::new();
        for num in nums {                                  // Phase 1: count
            *freq.entry(num).or_insert(0) += 1;
        }

        // Phase 2: min-heap of size k.
        // BinaryHeap is a max-heap; Reverse makes (count, num) sort ascending.
        let mut heap: BinaryHeap<(Reverse<i32>, i32)> = BinaryHeap::new();
        for (num, count) in freq {
            heap.push((Reverse(count), num));
            if heap.len() > k as usize {
                heap.pop();                                // evict the least frequent
            }
        }
        heap.into_iter().map(|(_, num)| num).collect()
    }
}
```

> **Rust note:** `BinaryHeap` is a max-heap, so the tuple is wrapped in `Reverse` to make `(count, num)` behave as a min-heap keyed on frequency — the same "negate to invert" reflex as the Kotlin `compareBy { -it }` idiom elsewhere in this chapter.

## Dry run

**Input:** `nums = [1,1,1,2,2,3]`, `k = 2`.

```
Phase 1: freq = {1:3, 2:2, 3:1}
Phase 2: minHeap ordered by frequency (root = smallest freq):
  offer 1 -> heap [1]
  offer 2 -> heap [1,2]
  offer 3 -> heap [1,2,3]; size 3 > k=2 -> poll() removes 3 (freq 1, the least frequent)
Result: heap = {1, 2}  (frequencies 3 and 2 — the top 2) ✓
```

Watch the eviction: `3` had the smallest frequency, so it's exactly what the min-heap root is — `poll()` removes it. If we'd used a max-heap, `poll()` would have removed `1` (the most frequent!) and the answer would be wrong.

## Complexity

**Time.** Counting is one pass; each of the $u$ unique keys gets at most one push and one pop on a heap of size $k$:

$$
T(n, u, k) = O(n) + O(u \log k)
$$

**Space.** The frequency map plus the heap:

$$
S(n, u, k) = O(u + k)
$$

## Variants & follow-ups

- **Bucket sort version** — frequencies are bounded by $n$, so drop elements into frequency buckets and walk buckets from the top: $O(n)$ time, no heap. The "can we do better than $O(\log k)$?" follow-up.
- **QuickSelect version** — partition the unique elements by frequency, recurse into the side containing the k-th: expected $O(u)$, worst $O(u^2)$.
- **K Closest Points To Origin** — the identical skeleton: replace "frequency" with "squared distance" and the comparator changes, nothing else. The repo's `FindKClosestElements.kt` and `quicksort/TopKFrequentElements.kt` show the same pattern twice.
- **Interview follow-up:** "Why not sort?" Because k is usually tiny ($k = 2$ here) while $u$ can be $10^5$ — the heap touch-each-once costs $O(u \log k)$ and never pays for ordering elements that will be evicted anyway.
