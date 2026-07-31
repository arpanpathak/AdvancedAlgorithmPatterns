# 11.3 Meeting Rooms

> **Source:** [`src/main/kotlin/greedy/MeetingRooms.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/MeetingRooms.kt)
> **Pattern:** sort + adjacency check · **Core page**

## The Problem

Given an array of meeting time intervals `intervals[i] = [start, end]` (half-open: `end` exclusive), return `true` if a person could attend **all** meetings — i.e., no two intervals overlap.

- Constraints: $0 \le n \le 10^4$; `0 <= start < end <= 10^6`.

## Examples

```
Input:  intervals = [[0,30],[5,10],[15,20]]   -> Output: false  ([0,30] swallows the others)
Input:  intervals = [[7,10],[2,4]]            -> Output: true   (disjoint)
```

## Intuition — overlaps only happen *between sorted neighbors*

Checking "does any pair overlap?" naively is $O(n^2)$. The sorted insight: **sort by start time; then any overlap is visible between two adjacent intervals.** If `A` and `B` are sorted by start and don't overlap, then `A` ends before `B` begins — and anything after `B` starts even later, so `A` can't overlap them either. Therefore:

- no adjacent pair overlaps  ⟺  no pair at all overlaps.

The check becomes one pass: `intervals[i+1].start < intervals[i].end` → overlap → `false`.

**Why half-open matters:** `[0,10]` and `[10,20]` do *not* overlap — a meeting ending at 10 and one starting at 10 can both be attended. The overlap condition is strictly `<` (`next.start < current.end`), not `<=`.

**The greedy shape:** this is the "sort, then the sorted structure answers everything" move — the cheapest possible preprocessing (one sort) that turns a quadratic pairwise question into a linear scan. It's also the feasibility half of the [interval family](pattern-primer.md): [11.4](meeting-rooms-ii.md) asks for the *count* of overlaps instead of the boolean.

## Approach 1 — Check all pairs (too slow)

For every pair of intervals, test overlap: $O(n^2)$.

## Approach 2 — Sort + adjacent check (the repo's version, optimal)

```kotlin
class MeetingRooms {
    /**
     * @param intervals intervals[i] = [start, end], half-open
     * @return          true iff no two intervals overlap
     */
    fun canAttendMeetings(intervals: Array<IntArray>): Boolean {
        intervals.sortBy { it[0] }                     // sort by start time

        for (i in 0 until intervals.size - 1) {
            if (intervals[i + 1][0] < intervals[i][1]) {   // next starts before this ends
                return false
            }
        }
        return true
    }
}
```

```java
import java.util.*;

public class MeetingRooms {
    /**
     * @param intervals intervals[i] = [start, end], half-open
     * @return          true iff no two intervals overlap
     */
    public boolean canAttendMeetings(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));   // sort by start

        for (int i = 0; i < intervals.length - 1; i++) {
            if (intervals[i + 1][0] < intervals[i][1]) {   // next starts before this ends
                return false;
            }
        }
        return true;
    }
}
```

```cpp
#include <algorithm>
#include <vector>

class MeetingRooms {
public:
    /**
     * @param intervals intervals[i] = [start, end], half-open
     * @return          true iff no two intervals overlap
     */
    bool canAttendMeetings(std::vector<std::vector<int>>& intervals) {
        std::sort(intervals.begin(), intervals.end());           // sort by start

        for (int i = 0; i < (int)intervals.size() - 1; i++) {
            if (intervals[i + 1][0] < intervals[i][1]) {         // next starts before this ends
                return false;
            }
        }
        return true;
    }
};
```

```python
def can_attend_meetings(intervals: list[list[int]]) -> bool:
    """
    @param intervals: intervals[i] = [start, end], half-open
    @return:          true iff no two intervals overlap
    """
    intervals.sort()                             # sort by start

    for i in range(len(intervals) - 1):
        if intervals[i + 1][0] < intervals[i][1]:   # next starts before this ends
            return False
    return True
```

```rust
impl Solution {
    /// @param intervals intervals[i] = [start, end], half-open
    /// @return          true iff no two intervals overlap
    pub fn can_attend_meetings(mut intervals: Vec<Vec<i32>>) -> bool {
        intervals.sort();                        // sort by start

        for w in intervals.windows(2) {
            if w[1][0] < w[0][1] {               // next starts before this ends
                return false;
            }
        }
        true
    }
}
```

## Dry run

**Input:** `intervals = [[0,30],[5,10],[15,20]]`.

```
sorted: [[0,30],[5,10],[15,20]]
i=0: next.start 5 < current.end 30 -> OVERLAP -> return false ✓
```

Now `intervals = [[7,10],[2,4]]`:

```
sorted: [[2,4],[7,10]]
i=0: next.start 7 < current.end 4? no -> continue
no overlap -> return true ✓
```

The sorted-neighbor argument in action: after sorting, `[0,30]` sits right next to `[5,10]`, and the overlap is caught at distance one. In the failing trace, no pair check beyond the adjacent one is ever needed — that's the entire saving.

## Complexity

**Time.** Sort dominates:

$$
T(n) = O(n \log n)
$$

**Space.** In-place sort, constant extra:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Meeting Rooms II** ([11.4](meeting-rooms-ii.md)) — same sort, but *count* the overlap instead of returning false; the "boolean" version becomes a "how many" version.
- **Merge Intervals** ([3.4](../ch03-arrays/merge-intervals.md)) — the same sorted structure, but overlapping neighbors get *merged* instead of rejected.
- **Insert Interval** ([3.5](../ch03-arrays/insert-interval.md)) — one interval inserted into a sorted set; the adjacency logic reused.
- **Interview follow-up:** "Why does sorting by start make the pairwise check collapse to adjacent?" If two non-adjacent intervals overlapped, every interval between them (sorted by start) would overlap at least one of them too — so an overlap always shows up at an adjacent pair. The sort is the preprocessing that makes the check local.
