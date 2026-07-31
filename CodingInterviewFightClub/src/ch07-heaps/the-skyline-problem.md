# 7.8 The Skyline Problem

> **Source:** [`src/main/kotlin/tree/bst/SkylineProblem.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bst/SkylineProblem.kt)
> **Pattern:** sweep line + multiset of heights · **Core page**

## The Problem

Given `buildings[i] = [left, right, height]`, return the **skyline** — the list of `[x, height]` key points where the outline changes.

- Constraints: $1 \le n \le 10^4$; coordinates fit in `Int`.

## Examples

```
Input:  buildings = [[2,9,10],[3,7,15],[5,12,12],[15,20,10],[19,24,8]]
Output: [[2,10],[3,15],[7,12],[12,0],[15,10],[20,8],[24,0]]
```

## Intuition — flatten every building into two *events*, then sweep the horizon

The sweep-line move: each building `[l, r, h]` becomes two **critical points** — a *start* at `l` (height +h) and an *end* at `r` (height -h, sign-flipped so starts sort before ends at the same x). The active heights live in a **max-multiset** (a `TreeMap` of height → count, descending):

```
for (x, h) in sorted points:
    if h < 0: insert -h                      # a building starts here
    else:     remove h (one occurrence)      # a building ends here
    currentMax = max active height
    if currentMax != previousHeight:         # the outline changed
        emit [x, currentMax]
```

**Why the TreeMap with counts, not a plain multiset?** `TreeMap(reverseOrder())` gives the max in O(1) (`firstKey`), and counts handle *duplicate heights* — two buildings of height 10 overlapping: the end of one shouldn't remove the other's 10. The count decrement (`dec()`, remove when 1) is the [7.3](sliding-window-median.md) lazy-deletion bookkeeping in miniature.

**Why does sorting starts before ends at the same x matter?** At x where a building ends and another starts, the *start* must be processed first — otherwise the skyline dips to a lower height for one point. The sign trick (start = -h, end = +h, ascending sort) makes that automatic: `-h` sorts before `+h`.

**The `{0: 1}` seed** — the ground: height 0 is always "active", so an empty skyline strip returns to 0 instead of vanishing. The emit condition `currentMax != previousHeight` is what collapses flat stretches (no output between changes).

## Approach 1 — Merge intervals per height (too slow)

For each distinct height, merge its intervals: $O(h \cdot n \log n)$.

## Approach 2 — Sweep line + height multiset (the repo's version, optimal)

```kotlin
import java.util.*

class SkylineProblem {
    /**
     * @param buildings [left, right, height]
     * @return         skyline key points [[x, height], ...]
     */
    fun getSkyline(buildings: Array<IntArray>): List<List<Int>> {
        val points = buildings.flatMap {
            listOf(Pair(it[0], -it[2]), Pair(it[1], it[2]))   // start = negative, end = positive
        }.sortedWith(compareBy({ it.first }, { it.second }))

        val heightMap = TreeMap<Int, Int>(reverseOrder()).also { it[0] = 1 }  // max-multiset
        val result = mutableListOf<List<Int>>()
        var previousHeight = 0

        for ((x, height) in points) {
            // Start of a building
            if (height < 0) {
                heightMap[-height] = (heightMap[-height] ?: 0) + 1
            } else if (heightMap[height] == 1) {
                heightMap.remove(height)                    // last occurrence ends
            } else {
                heightMap[height] = heightMap[height]?.dec()
            }

            val currentHeight = heightMap.firstKey()        // max active height
            if (currentHeight != previousHeight) {          // the outline changed
                result.add(listOf(x, currentHeight))
                previousHeight = currentHeight
            }
        }
        return result
    }
}
```

```java
import java.util.*;

public class TheSkylineProblem {
    /**
     * @param buildings [left, right, height]
     * @return         skyline key points [[x, height], ...]
     */
    public List<List<Integer>> getSkyline(int[][] buildings) {
        List<int[]> points = new ArrayList<>();
        for (int[] b : buildings) {
            points.add(new int[]{b[0], -b[2]});             // start = negative
            points.add(new int[]{b[1], b[2]});              // end = positive
        }
        points.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);

        // Max-heap of active heights with lazy deletion via counts
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        Map<Integer, Integer> counts = new HashMap<>();
        maxHeap.add(0); counts.put(0, 1);

        List<List<Integer>> result = new ArrayList<>();
        int prev = 0;

        for (int[] p : points) {
            int x = p[0], h = p[1];
            if (h < 0) {                                    // start
                maxHeap.add(-h);
                counts.merge(-h, 1, Integer::sum);
            } else {                                        // end: lazy-delete one occurrence
                counts.merge(h, -1, Integer::sum);
                while (!maxHeap.isEmpty() && counts.getOrDefault(maxHeap.peek(), 0) == 0) {
                    maxHeap.poll();
                }
            }

            int current = maxHeap.peek();
            if (current != prev) {
                result.add(List.of(x, current));
                prev = current;
            }
        }
        return result;
    }
}
```

```cpp
#include <map>
#include <set>
#include <vector>

class TheSkylineProblem {
public:
    /**
     * @param buildings [left, right, height]
     * @return         skyline key points [[x, height], ...]
     */
    std::vector<std::vector<int>> getSkyline(std::vector<std::vector<int>>& buildings) {
        std::vector<std::pair<int, int>> points;
        for (auto& b : buildings) {
            points.push_back({b[0], -b[2]});                // start = negative
            points.push_back({b[1], b[2]});                 // end = positive
        }
        std::sort(points.begin(), points.end());

        std::multiset<int> heights = {0};                   // active heights (max at rbegin)
        std::vector<std::vector<int>> result;
        int prev = 0;

        for (auto [x, h] : points) {
            if (h < 0) heights.insert(-h);                  // start
            else heights.erase(heights.find(h));            // end: remove one occurrence

            int current = *heights.rbegin();
            if (current != prev) {
                result.push_back({x, current});
                prev = current;
            }
        }
        return result;
    }
};
```

```python
from collections import defaultdict
import heapq

def get_skyline(buildings: list[list[int]]) -> list[list[int]]:
    """
    @param buildings: [left, right, height]
    @return:          skyline key points [[x, height], ...]
    """
    points = []
    for l, r, h in buildings:
        points.append((l, -h))     # start = negative
        points.append((r, h))      # end = positive
    points.sort()

    max_heap = [0]                 # active heights (max at top); 0 = ground
    counts = defaultdict(int)      # height -> occurrences (lazy deletion)
    counts[0] = 1
    result = []
    prev = 0

    for x, h in points:
        if h < 0:                  # start
            heapq.heappush(max_heap, h)          # h is negative; Python's heap is min -> store -h
            counts[-h] += 1
        else:                      # end: lazy-delete one occurrence
            counts[h] -= 1
            while max_heap and counts[-max_heap[0]] == 0:
                heapq.heappop(max_heap)

        current = -max_heap[0]
        if current != prev:        # the outline changed
            result.append([x, current])
            prev = current
    return result
```

```rust
use std::collections::BTreeMap;

impl Solution {
    /// @param buildings [left, right, height]
    /// @return          skyline key points [[x, height], ...]
    pub fn get_skyline(buildings: Vec<Vec<i32>>) -> Vec<Vec<i32>> {
        let mut points: Vec<(i32, i32)> = Vec::new();
        for b in &buildings {
            points.push((b[0], -b[2]));     // start = negative
            points.push((b[1], b[2]));      // end = positive
        }
        points.sort();

        let mut heights: BTreeMap<i32, i32> = BTreeMap::new();   // height -> count (max at last)
        heights.insert(0, 1);               // the ground
        let mut result = Vec::new();
        let mut prev = 0;

        for (x, h) in points {
            if h < 0 {
                *heights.entry(-h).or_insert(0) += 1;           // start
            } else {
                let entry = heights.entry(h).or_insert(0);
                *entry -= 1;                                    // end
                if *entry == 0 { heights.remove(&h); }
            }

            let current = *heights.last_key_value().unwrap().0; // max active height
            if current != prev {                                // the outline changed
                result.push(vec![x, current]);
                prev = current;
            }
        }
        result
    }
}
```

## Dry run

**Input:** `buildings = [[2,9,10],[3,7,15],[5,12,12],[15,20,10],[19,24,8]]`.

```
points (sorted): (2,-10),(3,-15),(5,-12),(7,15),(9,10),(12,12),(15,-10),(19,-8),(20,10),(24,8)
heights = {0:1}, prev = 0

(2,-10): add 10.        {15? no: {10:1, 0:1}}.  max=10 != 0 -> emit [2,10].  prev=10
(3,-15): add 15.        {15:1,10:1,0:1}.  max=15 != 10 -> emit [3,15].  prev=15
(5,-12): add 12.        max=15 == prev -> no emit.
(7,15):  end: remove 15.  {12:1,10:1,0:1}.  max=12 != 15 -> emit [7,12].  prev=12
(9,10):  end: remove 10.  {12:1,0:1}.  max=12 == prev -> no emit.
(12,12): end: remove 12.  {0:1}.  max=0 != 12 -> emit [12,0].  prev=0
(15,-10): add 10.       max=10 != 0 -> emit [15,10].  prev=10
(19,-8): add 8.         max=10 == prev -> no emit.
(20,10): end: remove 10.  {8:1,0:1}.  max=8 != 10 -> emit [20,8].  prev=8
(24,8):  end: remove 8.   {0:1}.  max=0 != 8 -> emit [24,0].

Output: [[2,10],[3,15],[7,12],[12,0],[15,10],[20,8],[24,0]] ✓
```

The `currentMax != prev` test is what compresses flat runs: from x=5 to x=7 the max stays 15 (the 12-height building is hidden behind the 15), so no point is emitted — only the *changes* become key points. The ground seed `{0:1}` is what produces `[12,0]` and `[24,0]` when the last building ends.

## Complexity

**Time.** Sort + O(1) per event:

$$
T(n) = O(n \log n)
$$

**Space.** The active-height structure:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Meeting Rooms II sweep-line** ([11.4](../ch11-greedy/meeting-rooms-ii.md)) — the same event-flatten + running-sum idea with `+1/-1` instead of heights.
- **Count Of Smaller Numbers After Self** (`tree/fenwick/CountOfSmallerNumberAfterSelf.kt`) — the Fenwick-tree sibling for order-statistics queries.
- **Interview follow-up:** "Why the sign trick instead of a separate sort key?" Sorting `(-h, +h)` ascending makes a *start* process before an *end* at the same x — the skyline jumps up instead of dipping. A custom comparator could do it too; the sign trick bakes the rule into the data so the sort stays a plain `sort`.
