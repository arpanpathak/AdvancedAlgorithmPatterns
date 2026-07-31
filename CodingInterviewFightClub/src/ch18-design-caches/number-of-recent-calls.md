# 18.14 Number Of Recent Calls

> **Source**: [`src/main/kotlin/queueu/dequeue/NumberOfRecentCalls.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/queueu/dequeue/NumberOfRecentCalls.kt)
> **Pattern**: monotone time-window queue · **Core page**

## The Problem

`ping(t)` — how many pings in `[t-3000, t]`.

- Constraints: t strictly increasing; ≤ 10⁴ calls.

## Examples

```
["RecentCounter","ping","ping","ping","ping"]
[[],[1],[100],[3001],[3002]]
-> [null,1,2,3,3]
```

## Intuition — evict pings older than the window at each call

```kotlin
fun ping(t: Int): Int {
    while (deque.isNotEmpty() && (t - deque.first()) > TIME_WINDOW_MS) {
        deque.removeFirst()          // expired
    }
    deque.addLast(t)
    return deque.size
}
```

The queue holds in-window timestamps — its size *is* the answer. The [7.9](../ch07-heaps/design-hit-counter.md) expiry-pop, 3000 ms, inclusive boundary.

## Approach 1 — Binary search over stored times (O(log n))

Store all pings, bisect the window: correct, log per call.

## Approach 2 — Expiry queue (the repo's version, optimal)

```kotlin
class NumberOfRecentCalls {
    val deque = LinkedList<Int>()
    val TIME_WINDOW_MS = 3000

    /**
     * @param t ping time (strictly increasing)
     * @return  pings in the last 3000 ms
     */
    fun ping(t: Int): Int {
        while (deque.isNotEmpty() && (t - deque.first()) > TIME_WINDOW_MS) {
            deque.removeFirst()
        }
        deque.addLast(t)
        return deque.size
    }
}
```

```java
import java.util.*;

public class NumberOfRecentCalls {
    private final Deque<Integer> deque = new LinkedList<>();

    /**
     * @param t ping time (strictly increasing)
     * @return  pings in the last 3000 ms
     */
    public int ping(int t) {
        while (!deque.isEmpty() && deque.peekFirst() < t - 3000) deque.pollFirst();
        deque.offerLast(t);
        return deque.size();
    }
}
```

```cpp
#include <queue>

class NumberOfRecentCalls {
    std::queue<int> q;

public:
    /**
     * @param t ping time (strictly increasing)
     * @return  pings in the last 3000 ms
     */
    int ping(int t) {
        while (!q.empty() && q.front() < t - 3000) q.pop();
        q.push(t);
        return (int)q.size();
    }
};
```

```python
from collections import deque

class RecentCounter:
    def __init__(self):
        self.pings = deque()

    def ping(self, t: int) -> int:
        while self.pings and self.pings[0] < t - 3000:
            self.pings.popleft()
        self.pings.append(t)
        return len(self.pings)
```

```rust
use std::collections::VecDeque;

struct RecentCounter {
    pings: VecDeque<i32>,
}

impl RecentCounter {
    fn new() -> Self { Self { pings: VecDeque::new() } }

    /// @param t ping time (strictly increasing)
    /// @return  pings in the last 3000 ms
    fn ping(&mut self, t: i32) -> i32 {
        while let Some(&front) = self.pings.front() {
            if front >= t - 3000 { break; }
            self.pings.pop_front();
        }
        self.pings.push_back(t);
        self.pings.len() as i32
    }
}
```

## Dry run

**Input:** `ping(1); ping(100); ping(3001); ping(3002)`.

```
ping(1):   queue [].  add 1.  size 1.
ping(100): 1 >= 100-3000? yes -> keep.  [1,100].  2.
ping(3001): front 1 >= 1? 1 >= 3001-3000 = 1 YES -> keep.  [1,100,3001].  3.
ping(3002): front 1 >= 2? no -> pop 1.  front 100 >= 2 yes.  [100,3001,3002].  3.

Output: [1,2,3,3] ✓
```

The inclusive `>= t-3000` boundary is what keeps ping(1) alive at t=3001 (age exactly 3000) — the `>` in the repo's version vs `<` in the Java/C++ versions must agree: `t - first > 3000` ⟺ `first < t - 3000`; both keep the exactly-3000-old ping.

## Complexity

**Time.** O(1) amortized:

$$
T = O(1) \text{ amortized}
$$

**Space.** In-window pings:

$$
S = O(3000) = O(1)
$$

## Variants & follow-ups

- **Design Hit Counter** ([7.9](../ch07-heaps/design-hit-counter.md)) — the same window, hits instead of pings.
- **Moving Average** ([18.13](moving-average.md)) — the sum-carrying window sibling.
- **Interview follow-up:** "Why is the queue sorted?" Timestamps are strictly increasing, so the front is always the oldest — one `peekFirst` decides expiry. The monotone input is what makes the O(1) amortized pop correct.
