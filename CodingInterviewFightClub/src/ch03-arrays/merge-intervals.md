# 3.4 Merge Intervals

> **Source:** [`src/main/kotlin/array/MergeIntervals.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/MergeIntervals.kt)
> **Pattern:** sort + linear sweep (Dance 4) · **Core page — the interval template**

## The Problem

Given an array of intervals `[start, end]` (inclusive), merge all overlapping intervals and return the merged intervals (non-overlapping, covering the union).

- Constraints: $1 \le n \le 10^4$.

## Examples

```
Input:  intervals = [[1, 3], [2, 6], [8, 10], [15, 18]]
Output: [[1, 6], [8, 10], [15, 18]]
Explanation: [1,3] and [2,6] overlap -> [1,6].

Input:  intervals = [[1, 4], [4, 5]]
Output: [[1, 5]]
Explanation: touching at 4 counts as overlapping (inclusive bounds).
```

## Intuition — sort, then one pass with a "current" interval

Overlapping intervals are a *contiguous* structure once you sort: if the intervals are sorted by start, then any chain of overlaps forms a run, and merging is a single forward sweep:

- Sort by start.
- Keep a "current" merged interval.
- For each next interval:
  - If it **starts after** the current one ends (`next.start > current.end`): no overlap — flush current, start a new one.
  - Otherwise (**overlap**): extend current's end to `max(current.end, next.end)`.

**Why one pass suffices:** after sorting, "current" is the interval with the earliest start among the not-yet-flushed ones. Any overlap must be with it (an interval can't skip over it to overlap something further right without touching it), so the greedy merge is complete.

**Why `max` and not `next.end`:** a later interval can have an *earlier* end (e.g. `[1, 10]` then `[2, 3]`) — the merged end is the max of everything seen in the run.

The repo's version does the merge **in-place** (mutating the sorted array, then truncating) — a space-optimal variant worth noting.

## Approach 1 — Brute force

For each interval, check all others for overlap; union repeatedly: $O(n^2)$ and fiddly (merging can cascade). The sorted sweep is the expected answer.

## Approach 2 — Sort + sweep (optimal)

```kotlin
/**
 * @param intervals the intervals to merge, each [start, end] inclusive
 * @return          the merged non-overlapping intervals
 */
fun merge(intervals: Array<IntArray>): Array<IntArray> {
    intervals.sortWith(compareBy { it[0] })   // sort by start
    var index = 0

    for (i in 1..intervals.lastIndex) {
        if (intervals[i][0] > intervals[index][1]) {
            // No overlap: flush the current merged interval and start a new one.
            intervals[++index] = intervals[i]
        } else {
            // Overlap: extend the current interval's end.
            intervals[index][1] = maxOf(intervals[index][1], intervals[i][1])
        }
    }
    return intervals.copyOfRange(0, index + 1)
}
```

```java
import java.util.*;

public class MergeIntervals {
    /**
     * @param intervals the intervals to merge, each [start, end] inclusive
     * @return          the merged non-overlapping intervals
     */
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);   // sort by start
        int index = 0;

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] > intervals[index][1]) {
                intervals[++index] = intervals[i];       // flush + start new
            } else {
                intervals[index][1] = Math.max(intervals[index][1], intervals[i][1]);
            }
        }
        return Arrays.copyOf(intervals, index + 1);
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MergeIntervals {
public:
    /**
     * @param intervals the intervals to merge, each [start, end] inclusive
     * @return          the merged non-overlapping intervals
     */
    std::vector<std::vector<int>> merge(std::vector<std::vector<int>>& intervals) {
        std::sort(intervals.begin(), intervals.end());   // sorts by start
        int index = 0;

        for (int i = 1; i < (int)intervals.size(); i++) {
            if (intervals[i][0] > intervals[index][1]) {
                intervals[++index] = intervals[i];       // flush + start new
            } else {
                intervals[index][1] = std::max(intervals[index][1], intervals[i][1]);
            }
        }
        intervals.resize(index + 1);
        return intervals;
    }
};
```

```python
def merge(intervals: list[list[int]]) -> list[list[int]]:
    """
    @param intervals: the intervals to merge, each [start, end] inclusive
    @return:          the merged non-overlapping intervals
    """
    intervals.sort(key=lambda iv: iv[0])        # sort by start
    result: list[list[int]] = []
    for start, end in intervals:
        if not result or start > result[-1][1]:
            result.append([start, end])         # no overlap: flush + start new
        else:
            result[-1][1] = max(result[-1][1], end)   # overlap: extend
    return result
```

```rust
impl Solution {
    /// @param intervals the intervals to merge, each [start, end] inclusive
    /// @return          the merged non-overlapping intervals
    pub fn merge(mut intervals: Vec<Vec<i32>>) -> Vec<Vec<i32>> {
        intervals.sort();                        // sorts by start
        let mut result: Vec<Vec<i32>> = Vec::new();

        for iv in intervals {
            if let Some(last) = result.last_mut() {
                if iv[0] > last[1] {
                    result.push(iv);             // no overlap: flush + start new
                } else {
                    last[1] = last[1].max(iv[1]); // overlap: extend
                }
            } else {
                result.push(iv);
            }
        }
        result
    }
}
```

## Dry run

**Input:** `intervals = [[1, 3], [2, 6], [8, 10], [15, 18]]`

```
sorted: same (already sorted)
index=0  current=[1,3]
i=1: [2,6] 2 > 3? NO -> extend -> current=[1,6]
i=2: [8,10] 8 > 6? YES -> flush [1,6], current=[8,10]
i=3: [15,18] 15 > 10? YES -> flush [8,10], current=[15,18]
result: [[1,6], [8,10], [15,18]] ✓
```

**Input:** `intervals = [[1, 4], [4, 5]]`

```
sorted: same
index=0  current=[1,4]
i=1: [4,5] 4 > 4? NO (inclusive bounds: touching counts as overlap) -> extend -> [1,5]
result: [[1,5]] ✓
```

**Input:** `intervals = [[1, 4], [0, 2], [3, 5]]` (unsorted)

```
sorted: [[0,2], [1,4], [3,5]]
index=0 current=[0,2]
i=1: [1,4] 1 > 2? NO -> extend -> [0,4]
i=2: [3,5] 3 > 4? NO -> extend -> [0,5]
result: [[0,5]] ✓   (the whole chain merges)
```

## Complexity

**Time.** Sorting dominates:

$$
T(n) = O(n \log n)
$$

**Space.** $O(1)$ extra (in-place merge; the output reuses the input) or $O(n)$ for the copy-based version.

## Variants & follow-ups

- **[3.5](insert-interval.md)** — one new interval into a *sorted* list; no sorting needed, three-phase sweep.
- **Interval List Intersections** (`src/main/kotlin/array/twopointer/IntervalListIntersection.kt`) — the two-pointer version of this page.
- **Non-overlapping Intervals / Meeting Rooms** — the *counting* twins: "how many intervals must be removed" uses a greedy earliest-finish rule on the same sorted sweep.
- **Skyline Problem** (`src/main/kotlin/tree/bst/SkylineProblem.kt`) — the same sweep idea, but with *events* (start/end) and a multiset of heights.
- **Interview follow-up:** "What if intervals are half-open `[start, end)`?" Only the comparison changes: overlap iff `next.start < current.end` (touching at the boundary no longer merges). One character, say it deliberately.
- **Interview follow-up:** "Why sort by start and not by end?" The sweep decision ("flush or extend") is correct exactly when the un-flushed current interval has the earliest start; end-time sorting breaks that invariant.
