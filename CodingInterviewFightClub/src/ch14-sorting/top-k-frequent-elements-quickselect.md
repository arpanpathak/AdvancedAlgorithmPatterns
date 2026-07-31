# 14.7 Top K Frequent Elements (QuickSelect)

> **Source:** [`src/main/kotlin/quicksort/TopKFrequentElements.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/quicksort/TopKFrequentElements.kt)
> **Pattern:** quickselect on frequency · **Core page**

## The Problem

Given `nums` and `k`, return the `k` most frequent elements (any order; answer unique).

- Constraints: $1 \le n \le 10^5$; `k` in range.

## Examples

```
Input:  nums = [1,1,1,2,2,3], k = 2   -> Output: [1,2]
```

## Intuition — [7.1](../ch07-heaps/top-k-frequent-elements.md)'s question, quickselect's answer

The heap version ([7.1](../ch07-heaps/top-k-frequent-elements.md)) keeps a size-k min-heap: $O(n \log k)$, guaranteed. This page is the **quickselect version**: after counting frequencies, run the [14.2](kth-largest-element.md) loop over the *unique* values, keyed by **frequency**, until the k-th position is final. Then `copyOfRange(0, k)` — the first k unique values are the k most frequent.

**Why unique values?** Quickselect partitions *elements*, and the elements here are the distinct numbers (the frequency is the partition key, looked up from the map). Duplicates of the same value are one element — hence `map.keys.toIntArray()` first.

**The frequency-as-key partition:** the repo's `partition` compares `map[nums[i]] >= pivot` — partitioning by *frequency*, not value. Everything left of the pivot has frequency ≥ pivot's; after the loop, `uniqueNums[0..k)` are the k most frequent. (The `>=` here partitions descending; the [14.2](kth-largest-element.md) version used `<=` ascending — both are the same loop with the comparator flipped.)

**When to choose which version?** Heap = $O(n \log k)$ guaranteed, $O(k)$ extra space. Quickselect = $O(n)$ average, $O(1)$ extra space (beyond the map), but $O(n^2)$ worst. Interview answer: "heap for the guaranteed bound, quickselect when I want the better average and no extra heap" — and the [Chapter 7](../ch07-heaps/index.md) page already has the heap version, so this page is its mirror.

## Approach 1 — Heap of size k (see [7.1](../ch07-heaps/top-k-frequent-elements.md))

$O(n \log k)$ guaranteed, $O(k)$ space. The "safe" answer.

## Approach 2 — Quickselect on unique frequencies (the repo's version, optimal)

```kotlin
import kotlin.random.Random

class TopKFrequentElements {
    private val map = HashMap<Int, Int>()

    /**
     * @param nums input array
     * @param k    how many top-frequency elements to return
     * @return     the k most frequent elements (any order)
     */
    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        nums.forEach { map[it] = map.getOrPut(it) { 0 } + 1 }      // count frequencies

        val uniqueNums = map.keys.toIntArray()
        var start = 0
        var end = uniqueNums.size - 1

        while (start < end) {
            val partitionIndex = partition(uniqueNums, start, end)
            when {
                partitionIndex < k - 1 -> start = partitionIndex + 1
                partitionIndex > k - 1 -> end = partitionIndex - 1
                else -> break
            }
        }
        return uniqueNums.copyOfRange(0, k)                         // the k most frequent
    }

    // Randomized partition keyed by FREQUENCY (descending)
    private fun partition(nums: IntArray, start: Int, end: Int): Int {
        val randomIndex = Random.nextInt(start, end + 1)
        swap(nums, randomIndex, end)                                // random pivot to the end
        val pivot = map[nums[end]] ?: 0

        var partitionIndex = start
        for (i in start until end) {
            if ((map[nums[i]] ?: 0) >= pivot) {                     // high frequency first
                swap(nums, i, partitionIndex++)
            }
        }
        swap(nums, partitionIndex, end)
        return partitionIndex
    }

    private fun swap(nums: IntArray, i: Int, j: Int) {
        nums[i] = nums[j].also { nums[i] = it }
    }
}
```

```java
import java.util.*;

public class TopKFrequentElements {
    /**
     * @param nums input array
     * @param k    how many top-frequency elements to return
     * @return     the k most frequent elements (any order)
     */
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) freq.merge(x, 1, Integer::sum);          // count frequencies

        int[] unique = new int[freq.size()];
        int idx = 0;
        for (int key : freq.keySet()) unique[idx++] = key;

        int start = 0, end = unique.length - 1;
        while (start < end) {
            int pivotIndex = partition(unique, start, end, freq);
            if (pivotIndex < k - 1) start = pivotIndex + 1;
            else if (pivotIndex > k - 1) end = pivotIndex - 1;
            else break;
        }
        return Arrays.copyOf(unique, k);                            // the k most frequent
    }

    private int partition(int[] nums, int start, int end, Map<Integer, Integer> freq) {
        int pivotIdx = start + new Random().nextInt(end - start + 1);
        swap(nums, pivotIdx, end);                                  // random pivot to the end
        int pivot = freq.get(nums[end]);

        int i = start;
        for (int j = start; j < end; j++) {
            if (freq.get(nums[j]) >= pivot) swap(nums, i++, j);     // high frequency first
        }
        swap(nums, i, end);
        return i;
    }

    private void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }
}
```

```cpp
#include <cstdlib>
#include <unordered_map>
#include <vector>

class TopKFrequentElements {
    int partition(std::vector<int>& nums, int start, int end,
                  std::unordered_map<int, int>& freq) {
        int pivotIdx = start + std::rand() % (end - start + 1);     // random pivot
        std::swap(nums[pivotIdx], nums[end]);
        int pivot = freq[nums[end]];

        int i = start;
        for (int j = start; j < end; j++) {
            if (freq[nums[j]] >= pivot) std::swap(nums[i++], nums[j]);  // high frequency first
        }
        std::swap(nums[i], nums[end]);
        return i;
    }

public:
    /**
     * @param nums input array
     * @param k    how many top-frequency elements to return
     * @return     the k most frequent elements (any order)
     */
    std::vector<int> topKFrequent(std::vector<int>& nums, int k) {
        std::unordered_map<int, int> freq;
        for (int x : nums) freq[x]++;                                // count frequencies

        std::vector<int> unique;
        for (auto& [v, _] : freq) unique.push_back(v);

        int start = 0, end = unique.size() - 1;
        while (start < end) {
            int pivotIndex = partition(unique, start, end, freq);
            if (pivotIndex < k - 1) start = pivotIndex + 1;
            else if (pivotIndex > k - 1) end = pivotIndex - 1;
            else break;
        }
        return std::vector<int>(unique.begin(), unique.begin() + k);  // the k most frequent
    }
};
```

```python
import random

def top_k_frequent(nums: list[int], k: int) -> list[int]:
    """
    @param nums: input array
    @param k:    how many top-frequency elements to return
    @return:     the k most frequent elements (any order)
    """
    from collections import Counter

    freq = Counter(nums)                             # count frequencies
    unique = list(freq.keys())

    def partition(start: int, end: int) -> int:
        pivot_idx = random.randint(start, end)       # random pivot
        unique[pivot_idx], unique[end] = unique[end], unique[pivot_idx]
        pivot = freq[unique[end]]

        i = start
        for j in range(start, end):
            if freq[unique[j]] >= pivot:             # high frequency first
                unique[i], unique[j] = unique[j], unique[i]
                i += 1
        unique[i], unique[end] = unique[end], unique[i]
        return i

    start, end = 0, len(unique) - 1
    while start < end:
        pivot_index = partition(start, end)
        if pivot_index < k - 1:
            start = pivot_index + 1
        elif pivot_index > k - 1:
            end = pivot_index - 1
        else:
            break
    return unique[:k]
```

```rust
use rand::Rng;
use std::collections::HashMap;

impl Solution {
    /// @param nums input array
    /// @param k    how many top-frequency elements to return
    /// @return     the k most frequent elements (any order)
    pub fn top_k_frequent(nums: Vec<i32>, k: i32) -> Vec<i32> {
        let mut freq: HashMap<i32, i32> = HashMap::new();
        for x in nums { *freq.entry(x).or_insert(0) += 1; }        // count frequencies

        let mut unique: Vec<i32> = freq.keys().copied().collect();

        fn partition(unique: &mut Vec<i32>, start: usize, end: usize,
                     freq: &HashMap<i32, i32>) -> usize {
            let pivot_idx = start + rand::thread_rng().gen_range(0..end - start + 1);
            unique.swap(pivot_idx, end);
            let pivot = freq[&unique[end]];

            let mut i = start;
            for j in start..end {
                if freq[&unique[j]] >= pivot {        // high frequency first
                    unique.swap(i, j);
                    i += 1;
                }
            }
            unique.swap(i, end);
            i
        }

        let k = k as usize;
        let (mut start, mut end) = (0, unique.len() - 1);
        while start < end {
            let pivot_index = partition(&mut unique, start, end, &freq);
            if pivot_index < k - 1 { start = pivot_index + 1; }
            else if pivot_index > k - 1 { end = pivot_index - 1; }
            else { break; }
        }
        unique.truncate(k);                            // the k most frequent
        unique
    }
}
```

## Dry run

**Input:** `nums = [1,1,1,2,2,3]`, `k = 2`.

```
freq = {1:3, 2:2, 3:1}.  unique = [1,2,3].  target = k-1 = 1.

partition(0,2): random pivot, say 2 (value 3, freq 1):
  partition by freq >= 1: 1(3), 2(2), then pivot.  -> [1,2,3], pivotIndex=2.
  pivotIndex 2 > target 1 -> end = 1.
partition(0,1): subarray [1,2]; random pivot, say 1 (value 2, freq 2):
  freq[1]=3 >= 2 -> swap into left.  -> [1,2,3], pivotIndex=1.
  pivotIndex == target 1 -> break.

return unique[0..2) = [1,2] ✓
```

The pivot at `[1,2]` partition landed exactly on the k-th position: one comparison, and the first k elements are the two most frequent. The `>=` comparator (frequency descending) is what makes "first k" mean "most frequent".

## Complexity

**Time.** Counting $O(n)$ + quickselect $O(u)$ expected ($u$ = unique values):

$$
T(n) = O(n) \text{ average}, \quad O(n^2) \text{ worst}
$$

**Space.** Frequency map + unique array:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Heap version** ([7.1](../ch07-heaps/top-k-frequent-elements.md)) — $O(n \log k)$ guaranteed: the two answers to the same question, side by side.
- **Bucket-sort version** — frequencies are bounded by $n$: throw values into frequency buckets and walk from the top: $O(n)$, no randomization, no worst case.
- **Kth Largest Element** ([14.2](kth-largest-element.md)) — the same loop without the frequency map.
- **Interview follow-up:** "Why partition on *unique* values rather than the raw array?" Quickselect's partition puts *elements* in place; duplicated values are one element each in the "top k most frequent" question. The map dedupes first, then the partition key (frequency) is what orders them. Without the dedupe, `[1,1,1,...]` would partition a thousand copies of `1`.
