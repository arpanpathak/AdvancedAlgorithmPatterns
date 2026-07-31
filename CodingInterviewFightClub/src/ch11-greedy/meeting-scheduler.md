# 11.22 Meeting Scheduler

> **Source**: [`src/main/kotlin/array/sorting/MeetingScheduler.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/sorting/MeetingScheduler.kt)
> **Pattern**: two-pointer interval overlap · **Core page**

## The Problem

The **earliest** common free slot of length ≥ `duration` from two schedules.

- Constraints: slots sorted per person; ≤ 10⁵.

## Examples

```
Input:  slots1 = [[10,50],[60,120],[140,210]], slots2 = [[0,15],[60,70]], duration = 8
Output: [60,68]
```

## Intuition — the [3.24](../ch03-arrays/interval-list-intersections.md) overlap, taking the first fit

Walk both slot lists; the overlap `[max(start), min(end)]` is a candidate — if it's ≥ duration, return it; else advance the earlier-ending slot:

```kotlin
while (firstIdx < slots1.size && secondIdx < slots2.size) {
    val maxStart = maxOf(slots1[firstIdx][0], slots2[secondIdx][0])
    val minEnd = minOf(slots1[firstIdx][1], slots2[secondIdx][1])

    if (minEnd - maxStart >= duration) {
        return listOf(maxStart, maxStart + duration)
    }

    if (slots1[firstIdx][1] < slots2[secondIdx][1]) firstIdx++
    else secondIdx++
}
return emptyList()
```

**Why sorted order makes the first fit the earliest?** The slots are processed in chronological order — the first overlap long enough IS the earliest answer. The [3.24](../ch03-arrays/interval-list-intersections.md) machinery with a duration filter.

## Approach 1 — All pairs + sort (O(nm log))

Check every pair, sort by start: correct, slow.

## Approach 2 — Two-pointer overlap (the repo's version, optimal)

```kotlin
class MeetingScheduler {
    /**
     * @param slots1   person 1's free slots
     * @param slots2   person 2's free slots
     * @param duration meeting length
     * @return         earliest common slot, or []
     */
    fun minAvailableDuration(slots1: Array<IntArray>, slots2: Array<IntArray>, duration: Int): List<Int> {
        slots1.sortBy { it[0] }
        slots2.sortBy { it[0] }

        var firstIdx = 0
        var secondIdx = 0

        while (firstIdx < slots1.size && secondIdx < slots2.size) {
            val maxStart = maxOf(slots1[firstIdx][0], slots2[secondIdx][0])
            val minEnd = minOf(slots1[firstIdx][1], slots2[secondIdx][1])

            if (minEnd - maxStart >= duration) {
                return listOf(maxStart, maxStart + duration)
            }

            when {
                slots1[firstIdx][1] < slots2[secondIdx][1] -> firstIdx++
                else -> secondIdx++
            }
        }
        return emptyList()
    }
}
```

```java
import java.util.*;

public class MeetingScheduler {
    /**
     * @param slots1   person 1's free slots
     * @param slots2   person 2's free slots
     * @param duration meeting length
     * @return         earliest common slot, or []
     */
    public List<Integer> minAvailableDuration(int[][] slots1, int[][] slots2, int duration) {
        Arrays.sort(slots1, (a, b) -> a[0] - b[0]);
        Arrays.sort(slots2, (a, b) -> a[0] - b[0]);

        int i = 0, j = 0;
        while (i < slots1.length && j < slots2.length) {
            int start = Math.max(slots1[i][0], slots2[j][0]);
            int end = Math.min(slots1[i][1], slots2[j][1]);

            if (end - start >= duration) return Arrays.asList(start, start + duration);

            if (slots1[i][1] < slots2[j][1]) i++;
            else j++;
        }
        return Collections.emptyList();
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MeetingScheduler {
public:
    /**
     * @param slots1   person 1's free slots
     * @param slots2   person 2's free slots
     * @param duration meeting length
     * @return         earliest common slot, or []
     */
    std::vector<int> minAvailableDuration(std::vector<std::vector<int>>& slots1,
                                          std::vector<std::vector<int>>& slots2, int duration) {
        std::sort(slots1.begin(), slots1.end());
        std::sort(slots2.begin(), slots2.end());

        int i = 0, j = 0;
        while (i < (int)slots1.size() && j < (int)slots2.size()) {
            int start = std::max(slots1[i][0], slots2[j][0]);
            int end = std::min(slots1[i][1], slots2[j][1]);

            if (end - start >= duration) return {start, start + duration};

            if (slots1[i][1] < slots2[j][1]) i++;
            else j++;
        }
        return {};
    }
};
```

```python
def min_available_duration(slots1: list[list[int]], slots2: list[list[int]], duration: int) -> list[int]:
    """
    @param slots1:   person 1's free slots
    @param slots2:   person 2's free slots
    @param duration: meeting length
    @return:         earliest common slot, or []
    """
    slots1.sort()
    slots2.sort()

    i = j = 0
    while i < len(slots1) and j < len(slots2):
        start = max(slots1[i][0], slots2[j][0])
        end = min(slots1[i][1], slots2[j][1])

        if end - start >= duration:
            return [start, start + duration]

        if slots1[i][1] < slots2[j][1]:
            i += 1
        else:
            j += 1

    return []
```

```rust
impl Solution {
    /// @param slots1   person 1's free slots
    /// @param slots2   person 2's free slots
    /// @param duration meeting length
    /// @return         earliest common slot, or []
    pub fn min_available_duration(mut slots1: Vec<Vec<i32>>, mut slots2: Vec<Vec<i32>>, duration: i32) -> Vec<i32> {
        slots1.sort();
        slots2.sort();

        let (mut i, mut j) = (0, 0);
        while i < slots1.len() && j < slots2.len() {
            let start = slots1[i][0].max(slots2[j][0]);
            let end = slots1[i][1].min(slots2[j][1]);

            if end - start >= duration { return vec![start, start + duration]; }

            if slots1[i][1] < slots2[j][1] { i += 1; } else { j += 1; }
        }
        vec![]
    }
}
```

## Dry run

**Input:** the example.

```
slots sorted.  [10,50] vs [0,15]: overlap [10,15] len 5 < 8.  50 > 15 -> j++.
[10,50] vs [60,70]: overlap [60,50] invalid.  50 < 70 -> i++.
[60,120] vs [60,70]: overlap [60,70] len 10 >= 8 -> return [60,68] ✓
```

## Complexity

**Time.** Sorts + walk:

$$
T = O(s_1 \log s_1 + s_2 \log s_2)
$$

**Space.** O(1) (or O(s) for the sort):

$$
S = O(1)
$$

## Variants & follow-ups

- **Interval List Intersections** ([3.24](../ch03-arrays/interval-list-intersections.md)) — the exact machinery, all overlaps instead of the first fit.
- **Interview follow-up:** "Why does the first fit give the earliest?" The pointer walk visits overlaps in chronological order — skipping a too-short overlap and advancing the earlier-ending slot never misses an earlier valid one (its start would have been even later).
