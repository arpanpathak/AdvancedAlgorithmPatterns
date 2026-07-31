# 11.14 Employee Free Time

> **Source**: [`src/main/kotlin/sorting/EmployeeFreeTime.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sorting/EmployeeFreeTime.kt)
> **Pattern**: flatten + merge + gaps · **Core page**

## The Problem

Given every employee's busy intervals (sorted, non-overlapping per employee), the **free** intervals common to all.

- Constraints: total intervals ≤ 10⁵.

## Examples

```
Input:  schedule = [[[1,2],[5,6]],[[1,3]],[[4,10]]]
Output: [[3,4]]
```

## Intuition — merge all intervals; the gaps between merged blocks are free

Flatten every employee's intervals, sort by start, then the standard [11.3](meeting-rooms.md) merge — the *gaps* between merged blocks are the free time:

```kotlin
val allIntervals = schedule.flatten().sortedBy { it.start }
val freeTime = arrayListOf<Interval>()
var prevEnd = allIntervals[0].end

for (i in 1 until allIntervals.size) {
    val (start, end) = allIntervals[i].start to allIntervals[i].end

    if (start > prevEnd) freeTime.add(Interval(prevEnd, start))   // a gap!
    prevEnd = maxOf(prevEnd, end)
}
```

**Why merge first?** Free time exists only between the *union* of busy intervals — overlapping busy blocks from different employees leave no free space. The merge's running `prevEnd` finds the union's gaps in one pass.

## Approach 1 — Sort + merge + gaps (the repo's version, optimal)

```kotlin
class EmployeeFreeTime {
    class Interval(var start: Int, var end: Int)

    /**
     * @param schedule per-employee busy intervals
     * @return         common free intervals
     */
    fun employeeFreeTime(schedule: ArrayList<ArrayList<Interval>>): ArrayList<Interval> {
        val allIntervals = schedule.flatten().sortedBy { it.start }
        val freeTime = arrayListOf<Interval>()

        var prevEnd = allIntervals[0].end

        for (i in 1 until allIntervals.size) {
            val (start, end) = allIntervals[i].start to allIntervals[i].end

            if (start > prevEnd) freeTime.add(Interval(prevEnd, start))
            prevEnd = maxOf(prevEnd, end)
        }
        return freeTime
    }
}
```

```java
import java.util.*;

public class EmployeeFreeTime {
    /**
     * @param schedule per-employee busy intervals
     * @return         common free intervals
     */
    public List<int[]> employeeFreeTime(List<List<int[]>> schedule) {
        List<int[]> all = new ArrayList<>();
        for (List<int[]> emp : schedule) all.addAll(emp);
        all.sort((a, b) -> a[0] - b[0]);

        List<int[]> free = new ArrayList<>();
        int prevEnd = all.get(0)[1];

        for (int i = 1; i < all.size(); i++) {
            int start = all.get(i)[0], end = all.get(i)[1];

            if (start > prevEnd) free.add(new int[]{prevEnd, start});
            prevEnd = Math.max(prevEnd, end);
        }
        return free;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class EmployeeFreeTime {
public:
    /**
     * @param schedule per-employee busy intervals
     * @return         common free intervals
     */
    std::vector<std::vector<int>> employeeFreeTime(std::vector<std::vector<std::vector<int>>>& schedule) {
        std::vector<std::vector<int>> all;
        for (auto& emp : schedule)
            for (auto& iv : emp) all.push_back(iv);

        std::sort(all.begin(), all.end(), [](auto& a, auto& b) { return a[0] < b[0]; });

        std::vector<std::vector<int>> free;
        int prevEnd = all[0][1];

        for (int i = 1; i < (int)all.size(); i++) {
            int start = all[i][0], end = all[i][1];

            if (start > prevEnd) free.push_back({prevEnd, start});
            prevEnd = std::max(prevEnd, end);
        }
        return free;
    }
};
```

```python
def employee_free_time(schedule: list[list[list[int]]]) -> list[list[int]]:
    """
    @param schedule: per-employee busy intervals
    @return:         common free intervals
    """
    all_intervals = sorted(iv for emp in schedule for iv in emp)
    free = []

    prev_end = all_intervals[0][1]
    for start, end in all_intervals[1:]:
        if start > prev_end:
            free.append([prev_end, start])
        prev_end = max(prev_end, end)

    return free
```

```rust
impl Solution {
    /// @param schedule per-employee busy intervals
    /// @return         common free intervals
    pub fn employee_free_time(schedule: Vec<Vec<Vec<i32>>>) -> Vec<Vec<i32>> {
        let mut all: Vec<Vec<i32>> = schedule.into_iter().flatten().collect();
        all.sort_by_key(|iv| iv[0]);

        let mut free = Vec::new();
        let mut prev_end = all[0][1];

        for iv in all.iter().skip(1) {
            if iv[0] > prev_end { free.push(vec![prev_end, iv[0]]); }
            prev_end = prev_end.max(iv[1]);
        }
        free
    }
}
```

## Dry run

**Input:** `schedule = [[[1,2],[5,6]],[[1,3]],[[4,10]]]`.

```
flatten + sort: [1,2],[1,3],[4,10],[5,6]
prevEnd = 2
[1,3]: start 1 <= 2 (overlap).  prevEnd = 3.
[4,10]: start 4 > 3 -> FREE [3,4].  prevEnd = 10.
[5,6]: start 5 <= 10.  prevEnd = 10.

Output: [[3,4]] ✓
```

The merge absorbs overlaps; the gap test `start > prevEnd` fires exactly when the union has a hole. The per-employee sorted property isn't needed — the global sort does all the work.

## Complexity

**Time.** Sort + scan:

$$
T(N) = O(N \log N)
$$

**Space.** The flattened list:

$$
S(N) = O(N)
$$

## Variants & follow-ups

- **Meeting Rooms** ([11.3](meeting-rooms.md)) / **Non-Overlapping Intervals** ([11.9](non-overlapping-intervals.md)) — the interval-sort family.
- **Interview follow-up:** "Why flatten before sorting?" The per-employee lists are already sorted, but merging k sorted lists (k-way) needs a heap; flattening + one sort is simpler and equally O(N log N) — the [7.11](../ch07-heaps/merge-k-sorted-lists.md) tradeoff, here in favor of simplicity.
