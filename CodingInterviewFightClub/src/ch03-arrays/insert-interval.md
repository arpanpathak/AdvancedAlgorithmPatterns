# 3.5 Insert Interval

> **Source:** [`src/main/kotlin/array/InsertInterval.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/InsertInterval.kt)
> **Pattern:** three-phase sweep · **Core page — the "already sorted" twin of 3.4**

## The Problem

Given a **sorted, non-overlapping** list of intervals and a `newInterval`, insert `newInterval` (merging if it overlaps) and return the updated list — still sorted and non-overlapping.

- Constraints: $1 \le n \le 10^4$, intervals sorted by start, no overlaps.

## Examples

```
Input:  intervals = [[1, 3], [6, 9]], newInterval = [2, 5]
Output: [[1, 5], [6, 9]]

Input:  intervals = [[1, 2], [3, 5], [6, 7], [8, 10], [12, 16]], newInterval = [4, 8]
Output: [[1, 2], [3, 10], [12, 16]]

Input:  intervals = [], newInterval = [5, 7]
Output: [[5, 7]]
```

## Intuition — the three phases of the timeline

Because the input is *already sorted and disjoint*, the new interval's interaction with the timeline has exactly **three phases**:

1. **Before (no overlap):** intervals ending strictly before `newInterval` starts (`iv.end < new.start`) — copy them unchanged.
2. **During (overlap):** intervals whose start ≤ the running merged end — absorb them into `newInterval` by widening its bounds. This phase *grows* the merged interval to cover everything it touches.
3. **After (no overlap):** everything remaining — copy unchanged.

The two overlap conditions are asymmetric on purpose:

- Phase 1 test: `iv[1] < new[0]` (strict — touching at a point still counts as overlap for inclusive intervals).
- Phase 2 test: `iv[0] <= merged[1]` (the merged interval keeps growing, so the test is against the *running* end, not the original).

Why no sort? The input is already sorted, so a single forward pass suffices — $O(n)$, not $O(n \log n)$. (If you *did* sort + reuse [3.4](merge-intervals.md), you'd get the same result at higher cost — a classic "solve it the expensive way, then notice the structure" conversation.)

## Approach 1 — Append, re-sort, merge

Append `newInterval`, run [3.4](merge-intervals.md): $O(n \log n)$. Correct but wasteful.

## Approach 2 — Three-phase sweep (optimal)

```kotlin
/**
 * @param intervals   the sorted, non-overlapping intervals
 * @param newInterval the interval to insert
 * @return            the updated sorted, non-overlapping intervals
 */
fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
    val result = mutableListOf<IntArray>()
    var i = 0
    val n = intervals.size

    // Phase 1: intervals ending before newInterval starts — no overlap, copy.
    while (i < n && intervals[i][1] < newInterval[0]) {
        result.add(intervals[i])
        i++
    }

    // Phase 2: absorb every overlapping interval into the merged one.
    val mergedInterval = newInterval.copyOf()
    while (i < n && intervals[i][0] <= mergedInterval[1]) {
        mergedInterval[0] = minOf(mergedInterval[0], intervals[i][0])
        mergedInterval[1] = maxOf(mergedInterval[1], intervals[i][1])
        i++
    }
    result.add(mergedInterval)

    // Phase 3: intervals starting after the merged one ends — copy the rest.
    while (i < n) {
        result.add(intervals[i])
        i++
    }
    return result.toTypedArray()
}
```

```java
import java.util.*;

public class InsertInterval {
    /**
     * @param intervals   the sorted, non-overlapping intervals
     * @param newInterval the interval to insert
     * @return            the updated sorted, non-overlapping intervals
     */
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0, n = intervals.length;

        while (i < n && intervals[i][1] < newInterval[0]) {   // phase 1: before
            result.add(intervals[i]);
            i++;
        }

        int[] merged = newInterval.clone();
        while (i < n && intervals[i][0] <= merged[1]) {       // phase 2: overlap
            merged[0] = Math.min(merged[0], intervals[i][0]);
            merged[1] = Math.max(merged[1], intervals[i][1]);
            i++;
        }
        result.add(merged);

        while (i < n) {                                       // phase 3: after
            result.add(intervals[i]);
            i++;
        }
        return result.toArray(new int[0][]);
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class InsertInterval {
public:
    /**
     * @param intervals   the sorted, non-overlapping intervals
     * @param newInterval the interval to insert
     * @return            the updated sorted, non-overlapping intervals
     */
    std::vector<std::vector<int>> insert(
        const std::vector<std::vector<int>>& intervals,
        const std::vector<int>& newInterval) {
        std::vector<std::vector<int>> result;
        int i = 0, n = (int)intervals.size();

        while (i < n && intervals[i][1] < newInterval[0]) {   // phase 1: before
            result.push_back(intervals[i]);
            i++;
        }

        std::vector<int> merged = newInterval;
        while (i < n && intervals[i][0] <= merged[1]) {       // phase 2: overlap
            merged[0] = std::min(merged[0], intervals[i][0]);
            merged[1] = std::max(merged[1], intervals[i][1]);
            i++;
        }
        result.push_back(merged);

        while (i < n) {                                       // phase 3: after
            result.push_back(intervals[i]);
            i++;
        }
        return result;
    }
};
```

```python
def insert(intervals: list[list[int]], new_interval: list[int]) -> list[list[int]]:
    """
    @param intervals:    the sorted, non-overlapping intervals
    @param new_interval: the interval to insert
    @return:             the updated sorted, non-overlapping intervals
    """
    result: list[list[int]] = []
    i, n = 0, len(intervals)

    while i < n and intervals[i][1] < new_interval[0]:        # phase 1: before
        result.append(intervals[i])
        i += 1

    merged = list(new_interval)
    while i < n and intervals[i][0] <= merged[1]:             # phase 2: overlap
        merged[0] = min(merged[0], intervals[i][0])
        merged[1] = max(merged[1], intervals[i][1])
        i += 1
    result.append(merged)

    result.extend(intervals[i:])                              # phase 3: after
    return result
```

```rust
impl Solution {
    /// @param intervals    the sorted, non-overlapping intervals
    /// @param new_interval the interval to insert
    /// @return             the updated sorted, non-overlapping intervals
    pub fn insert(intervals: Vec<Vec<i32>>, new_interval: Vec<i32>) -> Vec<Vec<i32>> {
        let mut result: Vec<Vec<i32>> = Vec::new();
        let (mut i, n) = (0usize, intervals.len());

        while i < n && intervals[i][1] < new_interval[0] {    // phase 1: before
            result.push(intervals[i].clone());
            i += 1;
        }

        let mut merged = new_interval.clone();
        while i < n && intervals[i][0] <= merged[1] {         // phase 2: overlap
            merged[0] = merged[0].min(intervals[i][0]);
            merged[1] = merged[1].max(intervals[i][1]);
            i += 1;
        }
        result.push(merged);

        while i < n {                                         // phase 3: after
            result.push(intervals[i].clone());
            i += 1;
        }
        result
    }
}
```

## Dry run

**Input:** `intervals = [[1, 2], [3, 5], [6, 7], [8, 10], [12, 16]]`, `newInterval = [4, 8]`

```
Phase 1: intervals ending < 4:
  [1,2] ends 2 < 4 -> copy. i=1
  [3,5] ends 5 < 4? NO -> phase 1 ends.
Phase 2: absorb while start <= merged.end (merged starts [4,8]):
  [3,5]: 3 <= 8 -> merged = [min(4,3), max(8,5)] = [3,8]. i=2
  [6,7]: 6 <= 8 -> merged = [3, max(8,7)] = [3,8]. i=3
  [8,10]: 8 <= 8 -> merged = [3, max(8,10)] = [3,10]. i=4
  [12,16]: 12 <= 10? NO -> phase 2 ends.
  result += [3,10]
Phase 3: copy the rest:
  [12,16] -> copy.
Result: [[1,2], [3,10], [12,16]] ✓
```

Note how `merged` *grew* during phase 2 ([4,8] → [3,8] → [3,10]) — the phase-2 test must be against the running `merged[1]`, or the chain-absorption would stop too early. That's the one place everyone gets Insert Interval wrong.

**Edge cases:** empty input → all phases skip, result = `[newInterval]`. New interval before everything → phase 1 copies nothing, phase 2 absorbs nothing, phase 3 copies all. New interval after everything → phase 1 copies all, then the merged interval appends at the end.

## Complexity

**Time.** One forward pass:

$$
T(n) = O(n)
$$

**Space.** $O(n)$ for the output (plus the merged copy).

## Variants & follow-ups

- **[3.4](merge-intervals.md)** — the "everything overlaps, sort first" twin; this page is the "input already sorted" case.
- **Interview follow-up:** "What if the input intervals might overlap already?" Then the three-phase absorb isn't enough (an absorbed interval could overlap a *later* one that phase 3 would copy) — fall back to append + merge ([3.4](merge-intervals.md)), $O(n \log n)$.
- **Interview follow-up:** "What if newInterval itself is empty (start > end)?" In this problem it isn't, but the phase-2 loop's `<= merged[1]` test would just fail immediately and the degenerate interval would be inserted as-is — a good edge to raise unprompted.
