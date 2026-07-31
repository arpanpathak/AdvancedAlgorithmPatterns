# 3.24 Interval List Intersections

> **Source**: [`src/main/kotlin/array/twopointer/IntervalListIntersection.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/IntervalListIntersection.kt)
> **Pattern**: two-pointer interval merge · **Core page**

## The Problem

The intersections of two sorted, non-overlapping interval lists.

- Constraints: lists sorted; n, m ≤ 10⁵.

## Examples

```
Input:  firstList = [[0,2],[5,10],[13,23],[24,25]], secondList = [[1,5],[8,12],[15,24],[25,26]]
Output: [[1,2],[5,5],[8,10],[15,23],[24,24],[25,25]]
```

## Intuition — overlap test + advance the earlier-ending interval

Two pointers walk the lists. The intersection is `[max(start1, start2), min(end1, end2)]` if valid; advance the interval that **ends first** (the other may still overlap the next):

```kotlin
while (i < firstList.size && j < secondList.size) {
    val startMax = maxOf(start1, start2)
    val endMin = minOf(end1, end2)

    if (startMax <= endMin) result.add(intArrayOf(startMax, endMin))

    if (end1 < end2) i++ else j++
}
```

**Why advance the earlier end?** The interval ending earlier can't intersect anything after the current partner — it's exhausted. The later-ending one survives for the next comparison. The [11.3](../ch11-greedy/meeting-rooms.md) interval two-pointer.

## Approach 1 — Nested scan (O(nm))

Check every pair: correct, slow.

## Approach 2 — Two-pointer overlap (the repo's version, optimal)

```kotlin
class IntervalListIntersection {
    /**
     * @param firstList  first interval list
     * @param secondList second interval list
     * @return           all intersections
     */
    fun intervalIntersection(firstList: Array<IntArray>, secondList: Array<IntArray>): Array<IntArray> {
        val result = mutableListOf<IntArray>()
        var (i, j) = 0 to 0

        while (i < firstList.size && j < secondList.size) {
            val (start1, end1) = firstList[i]
            val (start2, end2) = secondList[j]

            val startMax = maxOf(start1, start2)
            val endMin = minOf(end1, end2)

            if (startMax <= endMin) {
                result.add(intArrayOf(startMax, endMin))
            }

            if (end1 < end2) i++ else j++
        }
        return result.toTypedArray()
    }
}
```

```java
import java.util.*;

public class IntervalListIntersections {
    /**
     * @param firstList  first interval list
     * @param secondList second interval list
     * @return           all intersections
     */
    public int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
        List<int[]> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < firstList.length && j < secondList.length) {
            int lo = Math.max(firstList[i][0], secondList[j][0]);
            int hi = Math.min(firstList[i][1], secondList[j][1]);

            if (lo <= hi) result.add(new int[]{lo, hi});

            if (firstList[i][1] < secondList[j][1]) i++;
            else j++;
        }
        return result.toArray(new int[0][]);
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class IntervalListIntersections {
public:
    /**
     * @param firstList  first interval list
     * @param secondList second interval list
     * @return           all intersections
     */
    std::vector<std::vector<int>> intervalIntersection(std::vector<std::vector<int>>& firstList,
                                                       std::vector<std::vector<int>>& secondList) {
        std::vector<std::vector<int>> result;
        int i = 0, j = 0;

        while (i < (int)firstList.size() && j < (int)secondList.size()) {
            int lo = std::max(firstList[i][0], secondList[j][0]);
            int hi = std::min(firstList[i][1], secondList[j][1]);

            if (lo <= hi) result.push_back({lo, hi});

            if (firstList[i][1] < secondList[j][1]) i++;
            else j++;
        }
        return result;
    }
};
```

```python
def interval_intersection(first_list: list[list[int]], second_list: list[list[int]]) -> list[list[int]]:
    """
    @param first_list:  first interval list
    @param second_list: second interval list
    @return:            all intersections
    """
    result = []
    i = j = 0

    while i < len(first_list) and j < len(second_list):
        lo = max(first_list[i][0], second_list[j][0])
        hi = min(first_list[i][1], second_list[j][1])

        if lo <= hi:
            result.append([lo, hi])

        if first_list[i][1] < second_list[j][1]:
            i += 1
        else:
            j += 1

    return result
```

```rust
impl Solution {
    /// @param first_list  first interval list
    /// @param second_list second interval list
    /// @return            all intersections
    pub fn interval_intersection(first_list: Vec<Vec<i32>>, second_list: Vec<Vec<i32>>) -> Vec<Vec<i32>> {
        let mut result = Vec::new();
        let (mut i, mut j) = (0, 0);

        while i < first_list.len() && j < second_list.len() {
            let lo = first_list[i][0].max(second_list[j][0]);
            let hi = first_list[i][1].min(second_list[j][1]);

            if lo <= hi { result.push(vec![lo, hi]); }

            if first_list[i][1] < second_list[j][1] { i += 1; } else { j += 1; }
        }
        result
    }
}
```

## Dry run

**Input:** the example.

```
[0,2] vs [1,5]: lo=1, hi=2 -> [1,2].  end 2 < 5 -> i++.
[5,10] vs [1,5]: lo=5, hi=5 -> [5,5].  end 10 > 5 -> j++.
[5,10] vs [8,12]: lo=8, hi=10 -> [8,10].  i++.
[13,23] vs [8,12]: lo=13, hi=12 -> invalid.  j++.
[13,23] vs [15,24]: [15,23].  i++.
[24,25] vs [15,24]: [24,24].  j++.
[24,25] vs [25,26]: [25,25].  i++.
Output: [[1,2],[5,5],[8,10],[15,23],[24,24],[25,25]] ✓
```

## Complexity

**Time.** Each interval advanced once:

$$
T(n, m) = O(n + m)
$$

**Space.** The result:

$$
S(n, m) = O(n + m)
$$

## Variants & follow-ups

- **Meeting Rooms** ([11.3](../ch11-greedy/meeting-rooms.md)) — the interval-family ancestor.
- **Interview follow-up:** "Why advance by the earlier end?" The interval with the smaller end can't intersect any later partner — its overlap with the current one is its last chance. Advancing it keeps the other candidate alive; each step retires exactly one interval.
