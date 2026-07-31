# 7.9 Design Hit Counter

> **Source:** [`src/main/kotlin/queueu/dequeue/DesignHitCounter.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/queueu/dequeue/DesignHitCounter.kt)
> **Pattern:** monotone deque of timestamps · **Core page**

## The Problem

Design a hit counter: `hit(timestamp)` records a hit; `getHits(timestamp)` returns hits in the last 300 seconds (inclusive of the boundary).

- Constraints: timestamps are monotonically non-decreasing; ≤ 300 ops/s.

## Examples

```
hit(1); hit(2); hit(3); getHits(4) -> 3
getHits(300) -> 4   (all four hits are within [0, 300])
hit(301); getHits(301) -> 3   (hit(1) expired)
```

## Intuition — a FIFO queue where the *front* ages out

Hits arrive in time order, so the window is a **queue**: each new hit enqueues; `getHits` pops the front while it's older than 300 seconds. The answer is the queue's size:

```kotlin
class HitCounter {
    private val hits: Deque<Int> = LinkedList()

    fun hit(timestamp: Int) { hits.offer(timestamp) }

    fun getHits(timestamp: Int): Int {
        while (hits.isNotEmpty() && hits.peekFirst() <= timestamp - 300) {
            hits.pollFirst()                  // expired: older than the window
        }
        return hits.size
    }
}
```

**Why is the queue monotone?** Timestamps only increase, so the queue is sorted by construction — the front is always the oldest, and `peekFirst() <= timestamp - 300` is the exact expiry test. No sorting, no binary search needed.

**Why is it O(1) amortized?** Each timestamp is enqueued once and dequeued at most once — total pops across all calls ≤ total hits. The [18.x](../ch18-design-caches/pattern-primer.md) "amortized cleanup" idea in its purest form.

## Approach 1 — Count per second (O(300) per query, O(1) memory)

Ring buffer of 300 buckets summing the window: also O(1) per op, constant memory — the alternative interview answer.

## Approach 2 — FIFO queue with expiry pop (the repo's version, optimal)

```kotlin
import java.util.*

class HitCounter() {
    private val hits: Deque<Int> = LinkedList()

    /**
     * @param timestamp record a hit at this time
     */
    fun hit(timestamp: Int) {
        hits.offer(timestamp)
    }

    /**
     * @param timestamp query time
     * @return         hits in the last 300 seconds
     */
    fun getHits(timestamp: Int): Int {
        while (hits.isNotEmpty() && hits.peekFirst() <= timestamp - 300) {
            hits.pollFirst()
        }
        return hits.size
    }
}
```

```java
import java.util.*;

public class HitCounter {
    private final Deque<Integer> hits = new LinkedList<>();

    /**
     * @param timestamp record a hit at this time
     */
    public void hit(int timestamp) {
        hits.offer(timestamp);
    }

    /**
     * @param timestamp query time
     * @return         hits in the last 300 seconds
     */
    public int getHits(int timestamp) {
        while (!hits.isEmpty() && hits.peekFirst() <= timestamp - 300) {
            hits.pollFirst();
        }
        return hits.size();
    }
}
```

```cpp
#include <queue>

class HitCounter {
    std::queue<int> hits;

public:
    /**
     * @param timestamp record a hit at this time
     */
    void hit(int timestamp) { hits.push(timestamp); }

    /**
     * @param timestamp query time
     * @return         hits in the last 300 seconds
     */
    int getHits(int timestamp) {
        while (!hits.empty() && hits.front() <= timestamp - 300) hits.pop();
        return (int)hits.size();
    }
};
```

```python
from collections import deque

class HitCounter:
    """@param timestamp: record a hit at this time"""

    def __init__(self):
        self.hits = deque()

    def hit(self, timestamp: int) -> None:
        self.hits.append(timestamp)

    def get_hits(self, timestamp: int) -> int:
        while self.hits and self.hits[0] <= timestamp - 300:
            self.hits.popleft()              # expired: older than the window
        return len(self.hits)
```

```rust
use std::collections::VecDeque;

struct HitCounter {
    hits: VecDeque<i32>,
}

impl HitCounter {
    /// @param timestamp record a hit at this time
    fn hit(&mut self, timestamp: i32) {
        self.hits.push_back(timestamp);
    }

    /// @param timestamp query time
    /// @return         hits in the last 300 seconds
    fn get_hits(&mut self, timestamp: i32) -> i32 {
        while let Some(&front) = self.hits.front() {
            if front > timestamp - 300 { break; }
            self.hits.pop_front();           // expired
        }
        self.hits.len() as i32
    }
}
```

## Dry run

**Input:** `hit(1); hit(2); hit(3); getHits(4); getHits(300); hit(301); getHits(301)`.

```
hit(1), hit(2), hit(3): queue = [1,2,3]
getHits(4):   front 1 <= 4-300 = -296? no -> size 3 ✓
getHits(300): front 1 <= 0? no -> size 3.   (1, 2, 3 all within (0, 300])
hit(301): queue = [1,2,3,301]
getHits(301): front 1 <= 1? YES -> pop 1.   front 2 <= 1? no -> stop.  size 3 ✓
```

The expiry pop is the only "logic": `getHits(301)` pops `1` (age 300 ≥ 300) but keeps `2` (age 299 < 300). The `<= timestamp - 300` inclusive boundary is the exact "last 300 seconds" definition — `1` at timestamp 301 is exactly 300 seconds old, so it's out.

## Complexity

**Time.** O(1) amortized per op (each hit popped once):

$$
T = O(1) \text{ amortized}
$$

**Space.** The queue of in-window hits:

$$
S = O(300) \text{ worst} = O(1)
$$

## Variants & follow-ups

- **Number Of Recent Calls** (`queueu/dequeue/NumberOfRecentCalls.kt`) — the same window-queue, 3000 ms and returning the count.
- **Product Of Last K Numbers** (`queueu/dequeue/ProductOfLastKNumbers.kt`) — the prefix-product window sibling.
- **Interview follow-up:** "Why not a ring buffer of 300 buckets?" The bucket version is O(1) memory and O(1) per query with no amortization story — but it must *sum* 300 buckets per query. The queue trades worst-case memory for O(1)-amortized cleanup. Both are valid; the queue is the one-liner, the bucket is the constant-memory upgrade.
