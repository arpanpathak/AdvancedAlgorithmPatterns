# 11.9 Non-Overlapping Intervals

> **Source:** [`src/main/kotlin/array/greedy/NonOverlappingIntervals.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/greedy/NonOverlappingIntervals.kt)
> **Pattern:** greedy by earliest finish · **Core page**

## The Problem

Given `intervals[i] = [start, end]`, return the **minimum number of intervals to remove** so the rest are non-overlapping.

- Constraints: $1 \le n \le 10^5$; `start < end`.

## Examples

```
Input:  intervals = [[1,2],[2,3],[3,4],[1,3]]   -> Output: 1   (drop [1,3])
Input:  intervals = [[1,2],[1,2],[1,2]]         -> Output: 2
```

## Intuition — the [11.3](meeting-rooms.md) sorted-adjacency, with a *removal* decision

Sort by start; scan adjacent pairs. When `next.start < prev.end` — an overlap — **one of them must go**; keep the one that *ends earlier* (it leaves more room for the future), and remember its end as the new "previous":

```
sort by start; prevEnd = intervals[0].end; removed = 0
for i in 1..n-1:
    if intervals[i].start < prevEnd:          # overlap: remove one
        removed++
        prevEnd = min(prevEnd, intervals[i].end)   # keep the earlier-ending interval
    else:
        prevEnd = intervals[i].end
return removed
```

**Why keep the earlier end?** This is the classic **interval scheduling greedy** — "always keep the job that finishes earliest" — because an earlier finish can't hurt future intervals and strictly helps. When two overlap, the later-ending one is a strict superset of scheduling conflicts: dropping *it* (keeping the earlier end) is never worse. Exchange argument: the optimal solution can always be rearranged to include the earliest-finishing interval.

**Why is the count just "overlaps found"?** Each overlap detected in the sorted scan forces exactly one removal, and the greedy choice guarantees each removal is optimal — so the number of greedy removals equals the minimum. (The answer is also `n - maxNonOverlapping`, the interval-scheduling duality from the notes.)

## Approach 1 — Longest non-overlapping subset (DP / sweep)

Compute the maximum set of non-overlapping intervals and subtract from n: correct, more machinery.

## Approach 2 — Greedy keep-earliest-end (the repo's version, optimal)

```kotlin
class NonOverlappingIntervals {
    /**
     * @param intervals [start, end] pairs
     * @return          minimum intervals to remove for a non-overlapping set
     */
    fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
        intervals.sortWith(compareBy { it[0] })           // sort by start

        var count = 0
        var prevEnd = intervals[0][1]

        for (i in 1 until intervals.size) {
            val interval = intervals[i]

            if (interval[0] < prevEnd) {                  // overlap
                count++
                prevEnd = minOf(prevEnd, interval[1])     // keep the earlier-ending one
            } else {
                prevEnd = interval[1]
            }
        }
        return count
    }
}
```

```java
import java.util.*;

public class NonOverlappingIntervals {
    /**
     * @param intervals [start, end] pairs
     * @return          minimum intervals to remove for a non-overlapping set
     */
    public int eraseOverlapIntervals(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));   // sort by start

        int removed = 0;
        int prevEnd = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < prevEnd) {              // overlap
                removed++;
                prevEnd = Math.min(prevEnd, intervals[i][1]);   // keep the earlier-ending one
            } else {
                prevEnd = intervals[i][1];
            }
        }
        return removed;
    }
}
```

```cpp
#include <algorithm>
#include <vector>

class NonOverlappingIntervals {
public:
    /**
     * @param intervals [start, end] pairs
     * @return          minimum intervals to remove for a non-overlapping set
     */
    int eraseOverlapIntervals(std::vector<std::vector<int>>& intervals) {
        std::sort(intervals.begin(), intervals.end());            // sort by start

        int removed = 0;
        int prevEnd = intervals[0][1];

        for (int i = 1; i < (int)intervals.size(); i++) {
            if (intervals[i][0] < prevEnd) {              // overlap
                removed++;
                prevEnd = std::min(prevEnd, intervals[i][1]);     // keep the earlier-ending one
            } else {
                prevEnd = intervals[i][1];
            }
        }
        return removed;
    }
};
```

```python
def erase_overlap_intervals(intervals: list[list[int]]) -> int:
    """
    @param intervals: [start, end] pairs
    @return:          minimum intervals to remove for a non-overlapping set
    """
    intervals.sort()                            # sort by start

    removed = 0
    prev_end = intervals[0][1]

    for i in range(1, len(intervals)):
        if intervals[i][0] < prev_end:          # overlap
            removed += 1
            prev_end = min(prev_end, intervals[i][1])   # keep the earlier-ending one
        else:
            prev_end = intervals[i][1]
    return removed
```

```rust
impl Solution {
    /// @param intervals [start, end] pairs
    /// @return          minimum intervals to remove for a non-overlapping set
    pub fn erase_overlap_intervals(mut intervals: Vec<Vec<i32>>) -> i32 {
        intervals.sort();                        // sort by start

        let mut removed = 0;
        let mut prev_end = intervals[0][1];

        for i in 1..intervals.len() {
            if intervals[i][0] < prev_end {      // overlap
                removed += 1;
                prev_end = prev_end.min(intervals[i][1]);   // keep the earlier-ending one
            } else {
                prev_end = intervals[i][1];
            }
        }
        removed
    }
}
```

## Dry run

**Input:** `intervals = [[1,2],[2,3],[3,4],[1,3]]`.

```
sorted by start: [[1,2],[1,3],[2,3],[3,4]].  prevEnd=2, removed=0

i=1 [1,3]: 1 < 2 -> overlap -> removed=1.  prevEnd = min(2, 3) = 2.   (keep [1,2], drop [1,3])
i=2 [2,3]: 2 < 2? no -> prevEnd = 3.
i=3 [3,4]: 3 < 3? no -> prevEnd = 4.

Output: 1 ✓   (drop [1,3]; {[1,2],[2,3],[3,4]} is clean)
```

The greedy's pivotal moment is `i=1`: `[1,2]` and `[1,3]` overlap, and the earlier-ending `[1,2]` is kept — its end (2) becomes the fence, and `[2,3]` slides right past it. Had we kept `[1,3]` instead, `[2,3]` would have collided too — one removal becomes two.

## Complexity

**Time.** Sort dominates:

$$
T(n) = O(n \log n)
$$

**Space.** In-place sort:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Meeting Rooms / II** ([11.3](meeting-rooms.md), [11.4](meeting-rooms-ii.md)) — the boolean and *count* versions of the same adjacency scan; this page is the "remove" version.
- **Weighted Interval Scheduling** ([2.13](../ch02-dynamic-programming/maximum-profit-in-job-scheduling.md)) — when intervals carry *weights*, the greedy breaks and DP takes over — the [11.0](pattern-primer.md) "greedy fails when value matters" boundary.
- **Interview follow-up:** "Why does keeping the earlier end never hurt?" An earlier end is a strict superset of scheduling freedom — any interval that fits after the later end also fits after the earlier one, and intervals that fit between only exist for the earlier end. So the exchange argument swaps any optimal removal for the greedy one without increasing the count.
