# 7.3 Sliding Window Median

> **Source:** [`src/main/kotlin/heap/SlidingWindowMedian.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/heap/SlidingWindowMedian.kt)
> **Pattern:** dual heap + lazy deletion · **Core page**

## The Problem

Given an array `nums` and a window size `k`, return an array of the **medians of every window** of size `k` as it slides from left to right.

- Constraints: $1 \le k \le n \le 10^5$; $-2^{31} \le nums[i] \le 2^{31} - 1$.

## Examples

```
Input:  nums = [1,3,-1,-3,5,3,6,7], k = 3
Output: [1, -1, -1, 3, 5, 6]
        window [1,3,-1]  -> median 1
        window [3,-1,-3] -> median -1
        window [-1,-3,5] -> median -1
        window [-3,5,3]  -> median 3
        window [5,3,6]   -> median 5
        window [3,6,7]   -> median 6
```

## Intuition — the hard part is removal

The two-heap median structure from [7.2](find-median-from-data-stream.md) gives the median of a *static* set. Here the set **changes every step**: one element enters, one leaves. Adding is the same as before — but **removing an arbitrary element from a heap is the enemy** (the [primer's second reflex](pattern-primer.md)): a heap only pops its root efficiently, and the leaving element is usually buried somewhere inside.

Two honest options:

1. **Direct removal** — `heap.remove(x)` scans the heap's internal array to find `x`, then re-heapifies: $O(k)$ per removal, $O(n \cdot k)$ total. Correct and simple; fine for small windows. (This is what the repo's code does.)
2. **Lazy deletion** — never remove eagerly at all. Keep a `TreeMap` (or `HashMap`) counter of elements *marked dead*: when an element leaves the window, just increment its dead-count. The heaps stay as-is. Before *peeking* at a root, loop: while the root is marked dead, pop it and decrement the count. Dead elements cost a little memory until they surface — but every heap operation stays $O(\log k)$.

The window flow per step: `add(new)` -> `mark dead(old)` -> `prune()` (flush dead roots) -> **`balance()` again** (pruning can unbalance the halves!) -> `getMedian()`.

**Why a counter and not a `Set` of dead values?** Two windows may contain equal values from different positions; removing by value would kill both copies. The counter records *multiplicities*, so marking "one copy" dead decrements instead of erasing both.

## Approach 1 — Sort every window

For each of the $n-k+1$ windows, copy, sort, take the middle: $O((n-k+1) \cdot k \log k)$. Correct, and the interview answer *not* to give at $k = 10^5$.

## Approach 2 — Dual heaps with direct removal (the repo's version)

```kotlin
import java.util.*

class SlidingWindowMedian {
    private val minHeap = PriorityQueue<Double>()                  // larger half (smallest on top)
    private val maxHeap = PriorityQueue<Double>(compareBy { -it }) // smaller half (largest on top)
    private val delayedRemoval = TreeMap<Double, Int>()            // lazy-deletion counter (see below)

    private fun balanceHeaps() {
        if (maxHeap.size > minHeap.size + 1) {                     // keep sizes within 1
            minHeap.add(maxHeap.poll())
        } else if (minHeap.size > maxHeap.size) {
            maxHeap.add(minHeap.poll())
        }
    }

    private fun add(num: Int) {
        if (maxHeap.isEmpty() || num <= maxHeap.peek()) {          // route to the right half
            maxHeap.add(num.toDouble())
        } else {
            minHeap.add(num.toDouble())
        }
        balanceHeaps()
    }

    private fun remove(num: Int) {
        if (num <= maxHeap.peek()) {                               // which half holds it?
            maxHeap.remove(num.toDouble())                         // O(k) linear scan
        } else {
            minHeap.remove(num.toDouble())
        }
        balanceHeaps()
    }

    private fun getMedian(): Double {
        return if (maxHeap.size == minHeap.size) {
            (maxHeap.peek() + minHeap.peek()) / 2.0
        } else {
            maxHeap.peek()                                         // odd window -> max-heap root
        }
    }

    /**
     * @param nums the input array
     * @param k    sliding window size
     * @return     the median of every window of size k
     */
    fun medianSlidingWindow(nums: IntArray, k: Int): DoubleArray {
        val result = DoubleArray(nums.size - k + 1)

        for (i in nums.indices) {
            add(nums[i])                                           // 1. new element enters
            if (i >= k) remove(nums[i - k])                        // 2. old element leaves
            if (i >= k - 1) result[i - k + 1] = getMedian()        // 3. window full -> record
        }
        return result
    }
}
```

> **Repo note — the honest complexity story:** `PriorityQueue.remove(x)` is a *linear scan* ($O(k)$), so the direct version costs $O(n \cdot k)$ worst case — fine for the small `k` it targets, but it would not pass LeetCode's $k = 10^5$ limits. The `delayedRemoval` TreeMap field the repo declares is exactly the **lazy deletion** mechanism that fixes this; it's wired up properly below.

## Approach 3 — Dual heaps with lazy deletion (the O(n log k) fix)

```kotlin
class SlidingWindowMedianLazy {
    private val minHeap = PriorityQueue<Double>()
    private val maxHeap = PriorityQueue<Double>(compareBy { -it })
    private val deadCount = TreeMap<Double, Int>()          // value -> dead multiplicity

    private fun balance() {
        if (maxHeap.size > minHeap.size + 1) minHeap.add(maxHeap.poll())
        if (minHeap.size > maxHeap.size) maxHeap.add(minHeap.poll())
    }

    private fun prune() {                                   // flush dead roots from both heaps
        while (true) {
            val root = maxHeap.peek() ?: return
            val c = deadCount.getOrDefault(root, 0)
            if (c == 0) break
            maxHeap.poll(); if (c == 1) deadCount.remove(root) else deadCount[root] = c - 1
        }
        while (true) {
            val root = minHeap.peek() ?: return
            val c = deadCount.getOrDefault(root, 0)
            if (c == 0) break
            minHeap.poll(); if (c == 1) deadCount.remove(root) else deadCount[root] = c - 1
        }
    }

    /**
     * @param nums the input array
     * @param k    sliding window size
     * @return     the median of every window of size k
     */
    fun medianSlidingWindow(nums: IntArray, k: Int): DoubleArray {
        val result = DoubleArray(nums.size - k + 1)

        for (i in nums.indices) {
            // 1. add and route
            if (maxHeap.isEmpty() || nums[i].toDouble() <= maxHeap.peek()) {
                maxHeap.add(nums[i].toDouble())
            } else {
                minHeap.add(nums[i].toDouble())
            }
            balance()

            // 2. mark the leaving element dead (never remove eagerly)
            if (i >= k) {
                val old = nums[i - k].toDouble()
                deadCount[old] = deadCount.getOrDefault(old, 0) + 1
            }

            // 3. flush dead roots, then rebalance (pruning shifts sizes!)
            prune()
            balance()

            // 4. record the median once the window is full
            if (i >= k - 1) {
                result[i - k + 1] = if (maxHeap.size == minHeap.size)
                    (maxHeap.peek() + minHeap.peek()) / 2.0
                else maxHeap.peek()
            }
        }
        return result
    }
}
```

```java
import java.util.*;

public class SlidingWindowMedianLazy {
    private PriorityQueue<Double> minHeap = new PriorityQueue<>();   // larger half
    private PriorityQueue<Double> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    private TreeMap<Double, Integer> dead = new TreeMap<>();         // lazy-deletion counter

    private void balance() {
        if (maxHeap.size() > minHeap.size() + 1) minHeap.offer(maxHeap.poll());
        if (minHeap.size() > maxHeap.size()) maxHeap.offer(minHeap.poll());
    }

    private void prune() {
        while (!maxHeap.isEmpty() && dead.getOrDefault(maxHeap.peek(), 0) > 0) {
            double x = maxHeap.poll();
            int c = dead.get(x);
            if (c == 1) dead.remove(x); else dead.put(x, c - 1);
        }
        while (!minHeap.isEmpty() && dead.getOrDefault(minHeap.peek(), 0) > 0) {
            double x = minHeap.poll();
            int c = dead.get(x);
            if (c == 1) dead.remove(x); else dead.put(x, c - 1);
        }
    }

    /**
     * @param nums the input array
     * @param k    sliding window size
     * @return     the median of every window of size k
     */
    public double[] medianSlidingWindow(int[] nums, int k) {
        double[] result = new double[nums.length - k + 1];

        for (int i = 0; i < nums.length; i++) {
            if (maxHeap.isEmpty() || nums[i] <= maxHeap.peek()) maxHeap.offer((double) nums[i]);
            else minHeap.offer((double) nums[i]);
            balance();

            if (i >= k) dead.merge((double) nums[i - k], 1, Integer::sum);  // mark dead

            prune();
            balance();                                        // pruning shifts sizes!

            if (i >= k - 1) {
                result[i - k + 1] = maxHeap.size() == minHeap.size()
                    ? (maxHeap.peek() + minHeap.peek()) / 2.0
                    : maxHeap.peek();
            }
        }
        return result;
    }
}
```

```cpp
#include <functional>
#include <queue>
#include <unordered_map>
#include <vector>

class SlidingWindowMedianLazy {
    std::priority_queue<double> maxHeap;                    // smaller half (largest on top)
    std::priority_queue<double, std::vector<double>, std::greater<double>> minHeap;
    std::unordered_map<double, int> dead;                   // lazy-deletion counter

    void balance() {
        if (maxHeap.size() > minHeap.size() + 1) { minHeap.push(maxHeap.top()); maxHeap.pop(); }
        if (minHeap.size() > maxHeap.size()) { maxHeap.push(minHeap.top()); minHeap.pop(); }
    }

    void prune() {
        while (!maxHeap.empty() && dead[maxHeap.top()] > 0) { dead[maxHeap.top()]--; maxHeap.pop(); }
        while (!minHeap.empty() && dead[minHeap.top()] > 0) { dead[minHeap.top()]--; minHeap.pop(); }
    }

public:
    /**
     * @param nums the input array
     * @param k    sliding window size
     * @return     the median of every window of size k
     */
    std::vector<double> medianSlidingWindow(std::vector<int>& nums, int k) {
        std::vector<double> result(nums.size() - k + 1);

        for (int i = 0; i < (int)nums.size(); i++) {
            if (maxHeap.empty() || nums[i] <= maxHeap.top()) maxHeap.push(nums[i]);
            else minHeap.push(nums[i]);
            balance();

            if (i >= k) dead[nums[i - k]]++;                 // mark dead

            prune();
            balance();                                       // pruning shifts sizes!

            if (i >= k - 1) {
                result[i - k + 1] = maxHeap.size() == minHeap.size()
                    ? (maxHeap.top() + minHeap.top()) / 2.0
                    : maxHeap.top();
            }
        }
        return result;
    }
};
```

```python
import heapq

def median_sliding_window(nums: list[int], k: int) -> list[float]:
    """
    @param nums: the input array
    @param k:    sliding window size
    @return:     the median of every window of size k
    """
    lower = []          # max-heap (negated values)
    upper = []          # min-heap
    dead = {}           # value -> dead multiplicity

    def prune() -> None:
        while lower and -lower[0] in dead and dead[-lower[0]] > 0:
            x = -heapq.heappop(lower)
            dead[x] -= 1
            if dead[x] == 0:
                del dead[x]
        while upper and upper[0] in dead and dead[upper[0]] > 0:
            x = heapq.heappop(upper)
            dead[x] -= 1
            if dead[x] == 0:
                del dead[x]

    def balance() -> None:
        if len(lower) > len(upper) + 1:
            heapq.heappush(upper, -heapq.heappop(lower))
        if len(upper) > len(lower):
            heapq.heappush(lower, -heapq.heappop(upper))

    result = []
    for i, num in enumerate(nums):
        if not lower or num <= -lower[0]:
            heapq.heappush(lower, -num)
        else:
            heapq.heappush(upper, num)
        balance()

        if i >= k:                                   # mark the leaving element dead
            old = nums[i - k]
            dead[old] = dead.get(old, 0) + 1

        prune()
        balance()                                    # pruning shifts sizes!

        if i >= k - 1:
            result.append(-lower[0] if len(lower) > len(upper)
                          else (-lower[0] + upper[0]) / 2.0)
    return result
```

```rust
use std::cmp::Reverse;
use std::collections::{BinaryHeap, HashMap};

impl Solution {
    /// @param nums the input array
    /// @param k    sliding window size
    /// @return     the median of every window of size k
    pub fn median_sliding_window(nums: Vec<i32>, k: i32) -> Vec<f64> {
        let mut lower: BinaryHeap<i64> = BinaryHeap::new();        // smaller half
        let mut upper: BinaryHeap<Reverse<i64>> = BinaryHeap::new();
        let mut dead: HashMap<i64, i32> = HashMap::new();          // lazy-deletion counter
        let k = k as usize;

        fn prune(lower: &mut BinaryHeap<i64>, upper: &mut BinaryHeap<Reverse<i64>>, dead: &mut HashMap<i64, i32>) {
            while let Some(&x) = lower.peek() {
                let e = dead.entry(x).or_insert(0);
                if *e == 0 { break; }
                *e -= 1; lower.pop();
            }
            while let Some(&Reverse(x)) = upper.peek() {
                let e = dead.entry(x).or_insert(0);
                if *e == 0 { break; }
                *e -= 1; upper.pop();
            }
        }

        fn balance(lower: &mut BinaryHeap<i64>, upper: &mut BinaryHeap<Reverse<i64>>) {
            if lower.len() > upper.len() + 1 { upper.push(Reverse(lower.pop().unwrap())); }
            if upper.len() > lower.len() { lower.push(upper.pop().unwrap().0); }
        }

        let mut result = Vec::new();
        for (i, &num) in nums.iter().enumerate() {
            let x = num as i64;
            if lower.is_empty() || x <= *lower.peek().unwrap() { lower.push(x); }
            else { upper.push(Reverse(x)); }
            balance(&mut lower, &mut upper);

            if i >= k {                                            // mark dead
                *dead.entry(nums[i - k] as i64).or_insert(0) += 1;
            }

            prune(&mut lower, &mut upper, &mut dead);
            balance(&mut lower, &mut upper);                       // pruning shifts sizes!

            if i >= k - 1 {
                result.push(if lower.len() > upper.len() {
                    *lower.peek().unwrap() as f64
                } else {
                    (*lower.peek().unwrap() + upper.peek().unwrap().0) as f64 / 2.0
                });
            }
        }
        result
    }
}
```

> **Source:** [`src/main/kotlin/tree/SlidingWindowMedianTreeSet.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/SlidingWindowMedianTreeSet.kt)
> **Pattern:** variant gallery — index-based TreeSets vs [7.3](../ch07-heaps/sliding-window-median.md)'s lazy-deletion heaps

### The problem (recap)

For every window of size `k` in `nums`, find the median. [7.3](../ch07-heaps/sliding-window-median.md) solves it with two heaps + lazy deletion. This file solves it with **two TreeSets ordered by (value, index)** — a genuinely different data structure with the same O(n log k) bound.

### The implementation

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

### TreeSet vs two-heaps, in one table

| | [7.3](../ch07-heaps/sliding-window-median.md) two-heaps + lazy deletion | This page: two TreeSets |
|---|---|---|
| Duplicates | `counts` map + prune + rebalance | comparator `(value, index)` — none needed |
| Removal | mark-dead, lazy prune | exact `remove(index)` |
| Median read | `maxHeap.peek()/minHeap.peek()` (after balance) | `lower.last()/upper.first()` |
| Extra state | `counts`, `toBeRemoved` tracking | just the two sets + removalQueue |
| Code size | ~40 lines of bookkeeping | ~20 lines + comparator |
| Rare quirk | stale entries must be flushed before peek | comparator closure over `nums` |

**When to reach for TreeSet:** when the language has a balanced tree (Java `TreeSet`, C++ `set`/`multiset`) and the problem involves *duplicates* — the index-based comparator is the cleanest duplicate-handling trick in the book. When the language only has heaps (Python's `heapq`), the lazy-deletion heap version ([7.3](../ch07-heaps/sliding-window-median.md)) is the portable answer.

### Dry run

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


## Dry run

**Input:** `nums = [1,3,-1,-3,5,3,6,7]`, `k = 3`. (`L` = lower/max-heap, `U` = upper/min-heap, dead values in `{}`.)

```
i=0, add 1:  L empty -> L=[1].                              no removal, window not full
i=1, add 3:  3 <= L.root 1? no -> U=[3].  L=[1], U=[3]
i=2, add -1: -1 <= 1? yes -> L=[1,-1].
             median: L.size(2) > U.size(1) -> L.root = 1 ✓    window [1,3,-1]

i=3, add -3: -3 <= 1? yes -> L=[1,-1,-3]; balance: L too big -> move 1 to U.
             L=[-1,-3], U=[1,3].
             mark dead nums[0]=1 -> dead={1}
             prune: U.root 1 dead -> pop.  L=[-1,-3], U=[3]
             balance: sizes ok.
             median: L.size(2) > U.size(1) -> L.root = -1 ✓   window [3,-1,-3]

i=4, add 5:  5 <= -1? no -> U=[3,5].   L=[-1,-3], U=[3,5]
             mark dead nums[1]=3 -> dead={3}
             prune: U.root 3 dead -> pop.  L=[-1,-3], U=[5]
             median: L.size(2) > U.size(1) -> L.root = -1 ✓   window [-1,-3,5]

i=5, add 3:  3 <= -1? no -> U=[3,5].   L=[-1,-3], U=[3,5]
             mark dead nums[2]=-1 -> dead={-1}
             prune: L.root -1 dead -> pop.  L=[-3], U=[3,5]
             balance: U.size(2) > L.size(1) -> move 3 to L.  L=[-3,3], U=[5]
             median: L.size(2) > U.size(1) -> L.root = 3 ✓    window [-3,5,3]

i=6, add 6:  6 <= 3? no -> U=[5,6].   L=[-3,3], U=[5,6]
             mark dead nums[3]=-3 -> dead={-3}
             prune: L.root -3 dead -> pop.  L=[3], U=[5,6]
             balance: move 5 to L.  L=[3,5], U=[6]
             median: L.root = 5 ✓                              window [5,3,6]

i=7, add 7:  7 <= 5? no -> U=[6,7].   L=[3,5], U=[6,7]
             mark dead nums[4]=5 -> dead={5}
             prune: L.root 5 dead -> pop.  L=[3], U=[6,7]
             balance: move 6 to L.  L=[3,6], U=[7]
             median: L.root = 6 ✓                              window [3,6,7]

Output: [1, -1, -1, 3, 5, 6] ✓
```

The line worth staring at is `i=5`: after the dead `-1` is pruned from the *lower* heap, the halves are `L=[-3]`, `U=[3,5]` — unbalanced. The second `balance()` moves `3` down, and *then* the median is right. Skipping that rebalance is the classic bug in this problem.

## Complexity

**Time.** Each element is pushed once, popped at most once, and marked dead once; prune pops each dead element exactly once in total:

$$
T(n, k) = O(n \log k)
$$

(The repo's direct-removal variant instead costs $O(n \cdot k)$, since `PriorityQueue.remove` is a linear scan.)

**Space.** Both heaps hold live + dead elements; the counter holds dead values:

$$
S(n, k) = O(n)
$$

## Variants & follow-ups

- **Finding M K Average** (`src/main/kotlin/heap/FindingMKAverage.kt`) — *three* heaps (lowest k, middle window, largest k) with the same lazy-deletion discipline; the median is the "k=0" special case.
- **Dual Balanced Heap** (`src/main/kotlin/heap/DualBalancedHeap.kt`) — the repo's alternate take on the same two-heap structure.
- **Sliding Window Maximum** — the *max* version of this problem; a deque (monotonic queue) does it in $O(n)$ total, which is the classic "why is a deque better here?" comparison.
- **Interview follow-up:** "Why rebalance *after* pruning and not before?" Pruning removes dead roots — which can shrink one half enough to violate the ±1 invariant. If you only balanced at `add` time, the median would read a stale, lopsided structure. The two `balance()` calls are not redundant; they guard different events (insertion vs. eviction).
