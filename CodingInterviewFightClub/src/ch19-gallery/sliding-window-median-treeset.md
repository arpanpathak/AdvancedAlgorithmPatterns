# 19.10 Sliding Window Median — The TreeSet Alternative

> **Source:** [`src/main/kotlin/tree/SlidingWindowMedianTreeSet.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/SlidingWindowMedianTreeSet.kt)
> **Pattern:** variant gallery — index-based TreeSets vs [7.3](../ch07-heaps/sliding-window-median.md)'s lazy-deletion heaps

## The problem (recap)

For every window of size `k` in `nums`, find the median. [7.3](../ch07-heaps/sliding-window-median.md) solves it with two heaps + lazy deletion. This file solves it with **two TreeSets ordered by (value, index)** — a genuinely different data structure with the same O(n log k) bound.

## The implementation

```kotlin
class SlidingWindowMedianTreeSet {
    // Index-based comparison to handle duplicates:
    // two entries with equal values are ordered by their array index
    private val lower = TreeSet<Int> { a, b ->
        if (nums[a] != nums[b]) nums[a].compareTo(nums[b])
        else a.compareTo(b)
    }

    private val upper = TreeSet<Int> { a, b ->
        if (nums[a] != nums[b]) nums[a].compareTo(nums[b])
        else a.compareTo(b)
    }

    private lateinit var nums: IntArray
    private val removalQueue = ArrayDeque<Int>()

    fun medianSlidingWindow(nums: IntArray, k: Int): DoubleArray {
        this.nums = nums
        val result = DoubleArray(nums.size - k + 1)

        nums.indices.forEach { i ->
            // ... the standard two-multiset slide:
            // add nums[i] (balanced into lower/upper), evict the leaving index,
            // rebalance, then read the median from lower.last()/upper.first()
        }
        return result
    }
}
```

**What makes it cool:**

- **Indices, not values, in the sets.** The comparator is `(value, index)` — so *duplicates never collide*. Two `3`s at indices 5 and 9 are distinct TreeSet entries, ordered by index. The heap version's [7.3](../ch07-heaps/sliding-window-median.md) lazy-deletion counter (the `counts` map) exists precisely to work around duplicate ambiguity; the TreeSet comparator eliminates the problem structurally.
- **`lower.last()` / `upper.first()` are the medians** — a balanced pair of TreeSets gives both middle elements in O(1) reads; each add/remove is O(log k).
- **Removal is exact** — `remove(leavingIndex)` targets the precise entry; no stale entries accumulate, no prune-then-rebalance pass. The price: the comparator reads `nums[a]`/`nums[b]` — the array must be stable (it is; only the window slides).

## TreeSet vs two-heaps, in one table

| | [7.3](../ch07-heaps/sliding-window-median.md) two-heaps + lazy deletion | This page: two TreeSets |
|---|---|---|
| Duplicates | `counts` map + prune + rebalance | comparator `(value, index)` — none needed |
| Removal | mark-dead, lazy prune | exact `remove(index)` |
| Median read | `maxHeap.peek()/minHeap.peek()` (after balance) | `lower.last()/upper.first()` |
| Extra state | `counts`, `toBeRemoved` tracking | just the two sets + removalQueue |
| Code size | ~40 lines of bookkeeping | ~20 lines + comparator |
| Rare quirk | stale entries must be flushed before peek | comparator closure over `nums` |

**When to reach for TreeSet:** when the language has a balanced tree (Java `TreeSet`, C++ `set`/`multiset`) and the problem involves *duplicates* — the index-based comparator is the cleanest duplicate-handling trick in the book. When the language only has heaps (Python's `heapq`), the lazy-deletion heap version ([7.3](../ch07-heaps/sliding-window-median.md)) is the portable answer.

## Dry run

**Input:** `nums = [1,3,-1,-3,5,3,6,7]`, `k = 3`.

```
window [1,3,-1]: lower=[-1,1], upper=[3].  median = lower.last() = 1.  -> 1.0
window [3,-1,-3]: remove index 0 (1).  add -3.  lower=[-3,-1], upper=[3].  median = -1.  -> -1.0
window [-1,-3,5]: add 5 -> lower=[-3,-1], upper=[3,5].  median = -1.  -> -1.0
window [-3,5,3]: add 3 -> lower=[-3,3], upper=[5]... balanced -> median = 3.  -> 3.0
window [5,3,6]: median 5.  -> 5.0
window [3,6,7]: median 6.  -> 6.0

Output: [1.0,-1.0,-1.0,3.0,5.0,6.0] ✓
```

The `(value, index)` ordering matters in `[3,5,3]`: the two `3`s are distinct entries, and the sliding eviction removes exactly the *leaving index* — no ambiguity about which `3` to drop.

## Complexity

**Time.** Each add/remove is O(log k):

$$
T(n, k) = O(n \log k)
$$

**Space.** Two sets of size k:

$$
S(n, k) = O(k)
$$

## Variants & follow-ups

- **Sliding Window Median** ([7.3](../ch07-heaps/sliding-window-median.md)) — the two-heap page; this file is the "what if I use a balanced tree instead?" answer.
- **Find Median From Data Stream** ([7.2](../ch07-heaps/find-median-from-data-stream.md)) — no sliding (no removals) — the TreeSet's exact-removal advantage is moot there, and the two-heap version shines.
- **Interview follow-up:** "Why does the heap version need a counts map but the TreeSet doesn't?" Heaps store *values* — two equal values are indistinguishable, so deletions must be marked and pruned. TreeSets can store *indices* with a `(value, index)` comparator — each entry is unique, so deletion is a plain `remove`. The entire lazy-deletion machinery is a workaround for heap-duplicate ambiguity.
