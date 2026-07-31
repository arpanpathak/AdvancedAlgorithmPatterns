# 7.2 Find Median From Data Stream

> **Source:** [`src/main/kotlin/heap/MedianFromRunningStream.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/heap/MedianFromRunningStream.kt)
> **Pattern:** two heaps (max + min) · **Core page**

## The Problem

Design a class that supports two operations on a **stream** of integers (numbers arrive one at a time, in any order):

- `addNum(num)` — add an integer;
- `findMedian()` — return the median of all numbers seen so far.

- Constraints: up to $5 \times 10^4$ calls; $-10^5 \le num \le 10^5$.

## Examples

```
addNum(1)   -> median of {1}        = 1
addNum(2)   -> median of {1,2}      = 1.5
addNum(3)   -> median of {1,2,3}    = 2
```

## Intuition — the median is where two sorted halves meet

The median splits the data into a **lower half** and an **upper half** of (nearly) equal size. If you could keep both halves sorted, the median is trivially computed from the two *boundary* elements. The heap trick: you don't need the halves fully sorted — only their **largest lower** and **smallest upper** elements. Those are exactly what a max-heap and a min-heap expose:

- **max-heap** holds the lower half → its root is the largest lower element;
- **min-heap** holds the upper half → its root is the smallest upper element.

Maintain one invariant: **the two halves differ in size by at most 1**, and the upper half is never *smaller* than the lower. Then:

- odd total → the extra element sits on top of the *upper* heap (in this repo's choreography) → that's the median;
- even total → the median is the average of the two roots.

**The addNum choreography** (the repo's version): push into the min-heap (upper half) first, then immediately move the min-heap's *smallest* into the max-heap — this guarantees everything in the upper half is *larger* than everything in the lower half even before the new element is placed. Then rebalance if the max-heap grew too big. Every operation is two or three $O(\log n)$ heap pushes/pops — no sorting, ever.

**Why the two-heap structure at all?** A sorted list answers `findMedian` in $O(1)$ but `addNum` costs $O(n)$ (insertion shift). A heap answers both in $O(\log n)$. For a stream of $10^5$ inserts, that's the difference between $10^5$ and $10^{10}$ operations.

## Approach 1 — Keep a sorted list

Insert each number into its sorted position (binary search + shift): `addNum` $O(n)$, `findMedian` $O(1)$. Fine for tiny inputs, hopeless for streams.

## Approach 2 — Two heaps (the repo's version, optimal)

```kotlin
import java.util.*

class MedianFromRunningStream {
    private val minHeap = PriorityQueue<Int>()                    // larger half (smallest on top)
    private val maxHeap = PriorityQueue<Int>(compareBy() { -it }) // smaller half (largest on top)

    /**
     * @param num integer to add to the stream
     */
    fun addNum(num: Int) {
        minHeap.offer(num)              // stage into the upper half
        maxHeap.offer(minHeap.poll())   // move its smallest into the lower half

        if (minHeap.size < maxHeap.size) {   // rebalance: upper half >= lower half in size
            minHeap.offer(maxHeap.poll())
        }
    }

    /**
     * @return the median of all numbers seen so far
     */
    fun findMedian(): Double {
        return if (minHeap.size > maxHeap.size) {
            minHeap.peek().toDouble()          // odd count -> the extra sits on the upper half
        } else {
            (minHeap.peek() + maxHeap.peek()) / 2.0   // even count -> average of the middles
        }
    }
}
```

```java
import java.util.*;

public class MedianFinder {
    private PriorityQueue<Integer> minHeap = new PriorityQueue<>();          // larger half
    private PriorityQueue<Integer> maxHeap =                                 // smaller half
        new PriorityQueue<>(Collections.reverseOrder());

    /**
     * @param num integer to add to the stream
     */
    public void addNum(int num) {
        minHeap.offer(num);                 // stage into the upper half
        maxHeap.offer(minHeap.poll());      // move its smallest into the lower half

        if (minHeap.size() < maxHeap.size()) {   // rebalance
            minHeap.offer(maxHeap.poll());
        }
    }

    /**
     * @return the median of all numbers seen so far
     */
    public double findMedian() {
        if (minHeap.size() > maxHeap.size()) {
            return minHeap.peek();               // odd count
        }
        return (minHeap.peek() + maxHeap.peek()) / 2.0;   // even count
    }
}
```

```cpp
#include <queue>
#include <vector>

class MedianFinder {
    std::priority_queue<int> maxHeap;                          // smaller half (largest on top)
    std::priority_queue<int, std::vector<int>, std::greater<int>> minHeap;  // larger half

public:
    /**
     * @param num integer to add to the stream
     */
    void addNum(int num) {
        minHeap.push(num);                  // stage into the upper half
        maxHeap.push(minHeap.top());        // move its smallest into the lower half
        minHeap.pop();

        if (minHeap.size() < maxHeap.size()) {   // rebalance
            minHeap.push(maxHeap.top());
            maxHeap.pop();
        }
    }

    /**
     * @return the median of all numbers seen so far
     */
    double findMedian() {
        if (minHeap.size() > maxHeap.size()) {
            return minHeap.top();                // odd count
        }
        return (minHeap.top() + maxHeap.top()) / 2.0;   // even count
    }
};
```

```python
import heapq

class MedianFinder:
    """@param num: integer to add to the stream"""

    def __init__(self):
        self.lower = []          # max-heap: negate values (largest lower on top)
        self.upper = []          # min-heap: smallest upper on top

    def add_num(self, num: int) -> None:
        heapq.heappush(self.upper, num)                    # stage into the upper half
        heapq.heappush(self.lower, -heapq.heappop(self.upper))   # its smallest -> lower half

        if len(self.upper) < len(self.lower):              # rebalance
            heapq.heappush(self.upper, -heapq.heappop(self.lower))

    def find_median(self) -> float:
        """@return: the median of all numbers seen so far"""
        if len(self.upper) > len(self.lower):
            return self.upper[0]                           # odd count
        return (-self.lower[0] + self.upper[0]) / 2.0      # even count -> average of middles
```

```rust
use std::cmp::Reverse;
use std::collections::BinaryHeap;

struct MedianFinder {
    lower: BinaryHeap<i32>,                    // max-heap: largest lower element on top
    upper: BinaryHeap<Reverse<i32>>,           // min-heap via Reverse: smallest upper on top
}

impl MedianFinder {
    fn new() -> Self {
        MedianFinder { lower: BinaryHeap::new(), upper: BinaryHeap::new() }
    }

    /// @param num integer to add to the stream
    fn add_num(&mut self, num: i32) {
        self.upper.push(Reverse(num));                       // stage into the upper half
        self.lower.push(self.upper.pop().unwrap().0);        // its smallest -> lower half

        if self.upper.len() < self.lower.len() {             // rebalance
            self.upper.push(Reverse(self.lower.pop().unwrap()));
        }
    }

    /// @return the median of all numbers seen so far
    fn find_median(&self) -> f64 {
        if self.upper.len() > self.lower.len() {
            self.upper.peek().unwrap().0 as f64              // odd count
        } else {
            (*self.lower.peek().unwrap() + self.upper.peek().unwrap().0) as f64 / 2.0
        }
    }
}
```

> **Python note:** Python's `heapq` is min-only, so the lower half stores **negated** values — `-x` in the heap, `-heap[0]` when read back. That's the same "negate to invert" trick as the Kotlin `compareBy { -it }`, just spelled out.

## Dry run

**Input:** the example sequence. (`min` = upper half, `max` = lower half.)

```
addNum(1):
  min.offer(1) -> min=[1]; max.offer(min.poll()=1) -> max=[1], min=[]
  min.size(0) < max.size(1) -> rebalance: min.offer(max.poll()=1) -> min=[1], max=[]
  findMedian: min.size(1) > max.size(0) -> 1.0 ✓

addNum(2):
  min.offer(2) -> min=[1,2]; max.offer(min.poll()=1) -> max=[1], min=[2]
  sizes equal (1 == 1) -> no rebalance
  findMedian: (2 + 1) / 2 = 1.5 ✓

addNum(3):
  min.offer(3) -> min=[2,3]; max.offer(min.poll()=2) -> max=[2,1], min=[3]
  min.size(1) < max.size(2) -> rebalance: min.offer(max.poll()=2) -> min=[2,3], max=[1]
  findMedian: min.size(2) > max.size(1) -> min.peek() = 2 ✓
```

The invariant is visible at the end: lower half `{1}` (max-heap root 1), upper half `{2,3}` (min-heap root 2). The two roots are the middle elements — exactly the two numbers a median averages or picks.

## Complexity

**Time.** Each `addNum` does a constant number of heap pushes/pops:

$$
T_{\text{add}}(n) = O(\log n), \qquad T_{\text{median}}(n) = O(1)
$$

**Space.** Every number lives in exactly one heap:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Sliding Window Median** ([7.3](sliding-window-median.md)) — the same two heaps plus *lazy deletion* for elements leaving the window: the direct next problem in this chapter.
- **Finding M K Average** (`src/main/kotlin/heap/FindingMKAverage.kt`) — *three* heaps (lowest k, middle window, largest k) tracking a running window's trimmed average.
- **Median of Two Sorted Arrays** (`src/main/kotlin/binarysearch/MedianOfTwoSortedArrays.kt`) — the *static* version: binary search on the cut instead of streaming heaps.
- **Interview follow-up:** "Why does the staging step (`min -> max`) keep the halves ordered?" The naive "put small in max, big in min" can violate ordering when the new number lands in the *wrong* half relative to existing roots. Staging through the upper heap forces the correct split: whatever leaves `min` is its *smallest*, which is provably ≥ everything already in `max`. One extra $O(\log n)$ op, and the invariant is structural instead of checked.
