# 18.11 My Calendar

> **Source:** [`src/main/kotlin/tree/bst/MyCalendar.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bst/MyCalendar.kt) (+ `tree/segment/MyCalendar_II.kt` — the double-booking twin)
> **Pattern:** TreeMap floor/ceiling · **Core page**

## The Problem

`book(start, end)` — schedule an interval; reject if it **overlaps any existing booking**.

- Constraints: ≤ 10³ calls; intervals are `[start, end)` half-open.

## Examples

```
["MyCalendar","book","book","book"]
[[],[10,20],[15,25],[20,30]]
-> [null,true,false,true]   (10-20 ok; 15-25 overlaps; 20-30 touches, no overlap)
```

## Intuition — only two intervals can overlap a new one: the neighbors

With intervals keyed by start, the only potential conflicts are the **immediate predecessor and successor** — any earlier interval is before both, any later starts after. Two lookups decide:

```kotlin
class MyCalendar() {
    private val calender = TreeMap<Int, Int>()      // start -> end

    fun book(startTime: Int, endTime: Int): Boolean {
        val prev = calender.floorEntry(startTime)    // latest start <= ours
        val next = calender.ceilingEntry(startTime)  // earliest start >= ours

        val hasAnOverlapWithPrev = prev?.let { startTime < it.value } ?: false
        val hasAnOverlapWithNext = next?.let { it.key < endTime } ?: false

        return when {
            hasAnOverlapWithPrev || hasAnOverlapWithNext -> false
            else -> {
                calender[startTime] = endTime
                true
            }
        }
    }
}
```

**Why only the two neighbors?** Sort the intervals by start. A new `[s, e)` can only collide with the interval whose start is the largest `≤ s` (it could still be running) and the one whose start is the smallest `≥ s` (it could start before `e`). Everything else is fully left or fully right of the window — the [11.x](../ch11-greedy/pattern-primer.md) "adjacency-only conflicts" insight for intervals.

**Why half-open `[start, end)` matters?** `20-30` after `10-20` is legal: `floorEntry(20).value = 20`, and `20 < 20` is false → no prev overlap. The `startTime < it.value` (not `<=`) is the half-open boundary encoded.

## Approach 1 — Scan all bookings (O(n) per book)

Check every interval: correct, linear per query.

## Approach 2 — TreeMap floor/ceiling (the repo's version, optimal)

```kotlin
import java.util.*

class MyCalendar() {
    private val calender = TreeMap<Int, Int>()      // start -> end

    /**
     * @param startTime interval start (inclusive)
     * @param endTime   interval end (exclusive)
     * @return          true iff booked without overlap
     */
    fun book(startTime: Int, endTime: Int): Boolean {
        val prev = calender.floorEntry(startTime)
        val next = calender.ceilingEntry(startTime)

        val hasAnOverlapWithPrev = prev?.let { startTime < it.value } ?: false
        val hasAnOverlapWithNext = next?.let { it.key < endTime } ?: false

        return when {
            hasAnOverlapWithPrev || hasAnOverlapWithNext -> false
            else -> {
                calender[startTime] = endTime
                true
            }
        }
    }
}
```

```java
import java.util.*;

public class MyCalendar {
    private final TreeMap<Integer, Integer> cal = new TreeMap<>();

    /**
     * @param startTime interval start (inclusive)
     * @param endTime   interval end (exclusive)
     * @return          true iff booked without overlap
     */
    public boolean book(int startTime, int endTime) {
        Map.Entry<Integer, Integer> prev = cal.floorEntry(startTime);
        Map.Entry<Integer, Integer> next = cal.ceilingEntry(startTime);

        if (prev != null && startTime < prev.getValue()) return false;
        if (next != null && next.getKey() < endTime) return false;

        cal.put(startTime, endTime);
        return true;
    }
}
```

```cpp
#include <map>

class MyCalendar {
    std::map<int, int> cal;

public:
    /**
     * @param startTime interval start (inclusive)
     * @param endTime   interval end (exclusive)
     * @return          true iff booked without overlap
     */
    bool book(int startTime, int endTime) {
        auto it = cal.upper_bound(startTime);       // first start > ours

        if (it != cal.end() && it->first < endTime) return false;   // next overlaps
        if (it != cal.begin()) {
            auto prev = std::prev(it);              // last start <= ours
            if (startTime < prev->second) return false;             // prev overlaps
        }

        cal[startTime] = endTime;
        return true;
    }
};
```

```python
from bisect import bisect_left


class MyCalendar:
    """start-time sorted list of (start, end)"""

    def __init__(self):
        self.bookings = []

    def book(self, start_time: int, end_time: int) -> bool:
        i = bisect_left(self.bookings, (start_time, end_time))

        if i > 0 and start_time < self.bookings[i - 1][1]:
            return False                       # prev overlaps
        if i < len(self.bookings) and self.bookings[i][0] < end_time:
            return False                       # next overlaps

        self.bookings.insert(i, (start_time, end_time))
        return True
```

```rust
use std::collections::BTreeMap;

struct MyCalendar {
    cal: BTreeMap<i32, i32>,
}

impl MyCalendar {
    fn new() -> Self { Self { cal: BTreeMap::new() } }

    /// @param start_time interval start (inclusive)
    /// @param end_time   interval end (exclusive)
    /// @return           true iff booked without overlap
    fn book(&mut self, start_time: i32, end_time: i32) -> bool {
        if let Some((_, &prev_end)) = self.cal.range(..=start_time).next_back() {
            if start_time < prev_end { return false; }      // prev overlaps
        }
        if let Some((&next_start, _)) = self.cal.range(start_time..).next() {
            if next_start < end_time { return false; }      // next overlaps
        }

        self.cal.insert(start_time, end_time);
        true
    }
}
```

## Dry run

**Input:** `book(10,20); book(15,25); book(20,30)`.

```
book(10,20): floor(10)=none.  ceiling(10)=none.  insert {10:20}.  -> true
book(15,25): floor(15)=(10,20): 15 < 20 -> OVERLAP.  -> false ✓
book(20,30): floor(20)=(10,20): 20 < 20? no (half-open!).  ceiling(20)=none.  insert.  -> true ✓
```

The two neighbor checks cover every conflict: `[15,25)` collides only with its floor `[10,20)`; `[20,30)` *touches* the floor at exactly 20 — legal under `[start, end)` semantics, so the `startTime < it.value` strict comparison admits it. Later, `book(12,18)` would fail on the floor `(10,20)` and `book(5,8)` on nothing — the neighbor logic scales to any insertion order.

## Complexity

**Time.** Two O(log n) lookups:

$$
T(n) = O(\log n)
$$

**Space.** One entry per booking:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **My Calendar II** (`tree/segment/MyCalendar_II.kt`) — allow *one* double-booking: the same map with an overlap-count structure.
- **Non-Overlapping Intervals** ([11.9](../ch11-greedy/non-overlapping-intervals.md)) — the greedy sort version for static sets.
- **Meeting Rooms** ([11.3](../ch11-greedy/meeting-rooms.md)) — the "can all attend" static twin.
- **Interview follow-up:** "Why floor/ceiling and not scanning?" A sorted-by-start map makes the conflict check *local*: any interval overlapping `[s, e)` must either contain `s` (its start ≤ s, end > s — the floor) or start inside the window (start < e — the ceiling). Everything else is disjoint by the sorted order — two O(log n) lookups replace an O(n) scan.
