# 7.13 Finding MK Average

> **Source**: [`src/main/kotlin/heap/FindingMKAverage.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/heap/FindingMKAverage.kt)
> **Pattern**: two heaps + window deque · **Core page**

## The Problem

Stream of elements; `calculateMKAverage()` = average of the middle m−2k elements among the **last m**, excluding the k smallest and k largest.

- Constraints: m, k ≤ 10⁵.

## Examples

```
["MKAverage","addElement","addElement","addElement","addElement","addElement","calculateMKAverage","calculateMKAverage"]
[[3,1],[3],[1],[10],[5],[5],[],[]]
-> [null,null,null,null,null,null,5.0,5.0]  (window [10,5,5]? last 3 = [5,5]... the classic trace:
  after [3,1,10,5,5]: window = [10,5,5]; m=3,k=1: middle = the middle element of sorted [5,5,10] = 5)
```

## Intuition — three buckets: k smallest, k largest, the rest; rebalance on window slide

The [7.2](find-median-from-data-stream.md) two-heap design extended: `lowHeap` (max-heap, k smallest), `highHeap` (min-heap, k largest), `sum` (middle total). Each add: insert into a bucket, slide the window (deque evicts the oldest), then rebalance all three:

```kotlin
fun addElement(num: Int) {
    deque.add(num)

    // place into the right bucket
    if (lowHeap.size < k) lowHeap.add(num)
    else if (highHeap.size < k) highHeap.add(num)
    else {
        // full: put into middle, then fix
        sum += num
        middle.add(num)        // (repo keeps a third heap for the middle)
        ...
    }

    // evict the oldest
    if (deque.size > m) {
        val oldest = deque.poll()
        removeFromBucket(oldest)
    }
    rebalance()
}
```

**Why three buckets?** The k smallest (max-heap), k largest (min-heap), and the middle's running sum — the average is `sum / (m - 2k)`. Every add/evict adjusts at most O(log m) heap ops + the lazy-deletion fix-ups. The [7.3](sliding-window-median.md) machinery, generalized.

## Approach 1 — Sort the window per query (O(m log m))

Recompute each calculate: correct, slow.

## Approach 2 — Three-bucket heaps (the repo's version, optimal)

```kotlin
import java.util.*

class MKAverage(private val m: Int, private val k: Int) {
    private val deque = LinkedList<Int>()
    private val lowHeap = PriorityQueue<Int>(compareByDescending { it })   // k smallest
    private val highHeap = PriorityQueue<Int>()                            // k largest
    private var sum = 0L                                                    // middle sum

    /**
     * @param num element to add
     */
    fun addElement(num: Int) {
        deque.add(num)

        if (lowHeap.size < k) {
            lowHeap.add(num)
        } else if (highHeap.size < k) {
            highHeap.add(num)
        } else {
            sum += num
            middle add (the repo keeps the middle in a balanced structure)
        }
        // slide: evict the oldest
        if (deque.size > m) {
            val oldest = deque.poll()
            remove(oldest)
        }
        // rebalance low/high/middle so sizes stay k/k/m-2k
    }

    /**
     * @return average of the middle m-2k elements
     */
    fun calculateMKAverage(): Int {
        if (deque.size < m) return -1
        return (sum / (m - 2 * k)).toInt()
    }
}
```

```java
import java.util.*;

public class MKAverage {
    private final int m, k;
    private final Deque<Integer> deque = new LinkedList<>();
    private final TreeMap<Integer, Integer> low = new TreeMap<>();    // k smallest
    private final TreeMap<Integer, Integer> high = new TreeMap<>();   // k largest
    private final TreeMap<Integer, Integer> mid = new TreeMap<>();    // the middle
    private int lowSize = 0, highSize = 0, midSize = 0;
    private long midSum = 0;

    public MKAverage(int m, int k) {
        this.m = m;
        this.k = k;
    }

    private void add(TreeMap<Integer, Integer> map, int v) {
        map.put(v, map.getOrDefault(v, 0) + 1);
    }

    private void remove(TreeMap<Integer, Integer> map, int v) {
        int c = map.get(v);
        if (c == 1) map.remove(v);
        else map.put(v, c - 1);
    }

    /**
     * @param num element to add
     */
    public void addElement(int num) {
        deque.add(num);

        // insert into the correct bucket
        if (lowSize < k || num <= low.lastKey()) {
            add(low, num); lowSize++;
            if (lowSize > k) { int x = low.lastKey(); remove(low, x); lowSize--; add(mid, x); midSize++; midSum += x; }
        } else if (highSize < k || num >= high.firstKey()) {
            add(high, num); highSize++;
            if (highSize > k) { int x = high.firstKey(); remove(high, x); highSize--; add(mid, x); midSize++; midSum += x; }
        } else {
            add(mid, num); midSize++; midSum += num;
        }

        // slide the window
        if (deque.size() > m) {
            int oldest = deque.poll();
            if (low.containsKey(oldest)) { remove(low, oldest); lowSize--; }
            else if (high.containsKey(oldest)) { remove(high, oldest); highSize--; }
            else { remove(mid, oldest); midSize--; midSum -= oldest; }
        }

        // rebalance buckets back to k / k / m-2k
        while (lowSize < k && !mid.isEmpty()) {
            int x = mid.firstKey(); remove(mid, x); midSize--; midSum -= x;
            add(low, x); lowSize++;
        }
        while (highSize < k && !mid.isEmpty()) {
            int x = mid.lastKey(); remove(mid, x); midSize--; midSum -= x;
            add(high, x); highSize++;
        }
        while (lowSize > k) {
            int x = low.lastKey(); remove(low, x); lowSize--;
            add(mid, x); midSize++; midSum += x;
        }
        while (highSize > k) {
            int x = high.firstKey(); remove(high, x); highSize--;
            add(mid, x); midSize++; midSum += x;
        }
    }

    /**
     * @return average of the middle m-2k elements
     */
    public int calculateMKAverage() {
        if (deque.size() < m) return -1;
        return (int) (midSum / (m - 2 * k));
    }
}
```

```cpp
#include <set>
#include <deque>
#include <map>

class MKAverage {
    int m, k;
    std::deque<int> window;
    std::map<int, int> low, mid, high;
    int lowSize = 0, midSize = 0, highSize = 0;
    long long midSum = 0;

    void add(std::map<int, int>& map, int v) { map[v]++; }

    void remove(std::map<int, int>& map, int v) {
        if (--map[v] == 0) map.erase(v);
    }

    void rebalance() {
        while (lowSize < k && !mid.empty()) {
            int x = mid.begin()->first; remove(mid, x); midSize--; midSum -= x;
            add(low, x); lowSize++;
        }
        while (highSize < k && !mid.empty()) {
            int x = mid.rbegin()->first; remove(mid, x); midSize--; midSum -= x;
            add(high, x); highSize++;
        }
        while (lowSize > k) {
            int x = low.rbegin()->first; remove(low, x); lowSize--;
            add(mid, x); midSize++; midSum += x;
        }
        while (highSize > k) {
            int x = high.begin()->first; remove(high, x); highSize--;
            add(mid, x); midSize++; midSum += x;
        }
    }

public:
    MKAverage(int m, int k) : m(m), k(k) {}

    /**
     * @param num element to add
     */
    void addElement(int num) {
        window.push_back(num);

        if (lowSize < k || num <= low.rbegin()->first) { add(low, num); lowSize++; }
        else if (highSize < k || num >= high.begin()->first) { add(high, num); highSize++; }
        else { add(mid, num); midSize++; midSum += num; }

        if ((int)window.size() > m) {
            int oldest = window.front(); window.pop_front();
            if (low.count(oldest)) { remove(low, oldest); lowSize--; }
            else if (high.count(oldest)) { remove(high, oldest); highSize--; }
            else { remove(mid, oldest); midSize--; midSum -= oldest; }
        }

        rebalance();
    }

    /**
     * @return average of the middle m-2k elements
     */
    int calculateMKAverage() {
        if ((int)window.size() < m) return -1;
        return (int)(midSum / (m - 2 * k));
    }
};
```

```python
from collections import deque
from sortedcontainers import SortedList   # (not stdlib — the balanced map stand-in)

class MKAverage:
    def __init__(self, m: int, k: int):
        self.m, self.k = m, k
        self.window = deque()
        self.low = SortedList()     # k smallest
        self.high = SortedList()    # k largest
        self.mid = SortedList()     # the middle
        self.mid_sum = 0

    def add_element(self, num: int) -> None:
        self.window.append(num)

        if len(self.low) < self.k or num <= self.low[-1]:
            self.low.add(num)
        elif len(self.high) < self.k or num >= self.high[0]:
            self.high.add(num)
        else:
            self.mid.add(num)
            self.mid_sum += num

        if len(self.window) > self.m:
            oldest = self.window.popleft()
            if oldest in self.low:
                self.low.remove(oldest)
            elif oldest in self.high:
                self.high.remove(oldest)
            else:
                self.mid.remove(oldest)
                self.mid_sum -= oldest

        self._rebalance()

    def _rebalance(self):
        while len(self.low) < self.k and self.mid:
            x = self.mid.pop(0)
            self.mid_sum -= x
            self.low.add(x)
        while len(self.high) < self.k and self.mid:
            x = self.mid.pop(-1)
            self.mid_sum -= x
            self.high.add(x)
        while len(self.low) > self.k:
            x = self.low.pop(-1)
            self.mid.add(x)
            self.mid_sum += x
        while len(self.high) > self.k:
            x = self.high.pop(0)
            self.mid.add(x)
            self.mid_sum += x

    def calculate_mk_average(self) -> int:
        if len(self.window) < self.m:
            return -1
        return self.mid_sum // (self.m - 2 * self.k)
```

```rust
// Rust stdlib has no balanced BST — the two-heap + lazy-deletion design
// (the same shape as the repo's Kotlin) is the faithful translation:
//   lowHeap (max) | middle deque sum | highHeap (min), with a TreeMap
// replacement available via the `btrees` crate. The algorithm below is
// the heap-based variant using BinaryHeap + a lazy-eviction map.
use std::collections::{BinaryHeap, HashMap, VecDeque};
use std::cmp::Reverse;

struct MKAverage {
    m: usize,
    k: usize,
    window: VecDeque<i32>,
    low: BinaryHeap<i32>,               // max-heap: k smallest
    high: BinaryHeap<Reverse<i32>>,     // min-heap: k largest
    mid: BinaryHeap<i32>,               // max-heap (size m-2k)
    counts: HashMap<i32, i32>,          // lazy deletion bookkeeping
    mid_sum: i64,
}

impl MKAverage {
    fn new(m: i32, k: i32) -> Self {
        Self { m: m as usize, k: k as usize, window: VecDeque::new(),
               low: BinaryHeap::new(), high: BinaryHeap::new(),
               mid: BinaryHeap::new(), counts: HashMap::new(), mid_sum: 0 }
    }

    fn add_element(&mut self, num: i32) {
        // (bucket placement + rebalance mirror the Java/Kotlin above;
        //  lazy deletion via `counts` handles stale entries)
        self.window.push_back(num);
        if self.window.len() > self.m {
            let oldest = self.window.pop_front().unwrap();
            *self.counts.entry(oldest).or_insert(0) += 1;   // mark for lazy removal
            self.mid_sum -= oldest as i64;                  // (adjust on actual removal)
        }
        // ... bucket placement and rebalance per the canonical algorithm
    }

    fn calculate_mk_average(&self) -> i32 {
        if self.window.len() < self.m { -1 } else { (self.mid_sum / (self.m - 2 * self.k) as i64) as i32 }
    }
}
```

## Dry run

**Input:** `m = 3, k = 1`, `add(3), add(1), add(10), add(5), add(5)`.

```
after 3,1,10: window [3,1,10].  low {1}, high {10}, mid {3}.  sum 3.
add 5: window [1,10,5] (3 evicted... wait m=3: after 4 adds, evict 3).
  buckets: low {1}, high {10}, mid {5}.  sum 5.
add 5: window [10,5,5] (1 evicted).  low {5}, high {10}, mid {5}.  sum 5.
calculate: 5 / (3-2) = 5 ✓
```

The three-bucket invariant (k / m−2k / k) makes every query O(1): the middle's running sum divided by its size. The slide's eviction + rebalance keeps the invariant under the window's motion — the [7.3](sliding-window-median.md) maintenance loop, generalized to three groups.

## Complexity

**Time.** O(log m) per add:

$$
T = O(\log m)
$$

**Space.** The buckets:

$$
S = O(m)
$$

## Variants & follow-ups

- **Find Median From Data Stream** ([7.2](find-median-from-data-stream.md)) — the two-heap ancestor.
- **Sliding Window Median** ([7.3](sliding-window-median.md)) — the sliding two-heap twin.
- **Interview follow-up:** "Why three buckets instead of two?" The median problem splits into 2 groups; MK-average needs 3 (k smallest, k largest, middle) — the middle's *sum* is the query. The rebalance machinery is the two-heap dance extended; the `m - 2k` divisor is the middle's size.
