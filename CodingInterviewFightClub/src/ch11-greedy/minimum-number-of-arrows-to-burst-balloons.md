# 11.11 Minimum Number Of Arrows To Burst Balloons

> **Source:** [`src/main/kotlin/array/greedy/MInimumNumberOfArrowsRequiredToBurstBallons.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/greedy/MInimumNumberOfArrowsRequiredToBurstBallons.kt)
> **Pattern:** greedy by earliest end · **Core page**

## The Problem

Given `points[i] = [x_start, x_end]` (balloon x-spans), an arrow shot at coordinate `x` bursts every balloon with `x_start <= x <= x_end`. Return the **minimum arrows** to burst all balloons.

- Constraints: $1 \le n \le 10^5$; coordinates fit in `Int`.

## Examples

```
Input:  points = [[10,16],[2,8],[1,6],[7,12]]   -> Output: 2
Input:  points = [[1,2],[3,4],[5,6],[7,8]]     -> Output: 4
```

## Intuition — one arrow per *overlap cluster*; sort by end and greedily merge

Balloons that share a common x are burst by one arrow — so the answer is the minimum number of **overlap groups**. The greedy ([11.9](non-overlapping-intervals.md)'s twin, reversed): sort by **end**; hold the current cluster's right edge; every balloon whose start is *within* the cluster joins it; a balloon starting after the edge closes the cluster and needs a new arrow:

```
sort by end; arrows = 1; clusterEnd = points[0].end
for point in points:
    if clusterEnd < point.start:      # this balloon is beyond the current cluster
        arrows++
        clusterEnd = point.end        # new cluster starts here
return arrows
```

**Why sort by end (not start)?** The [11.3](meeting-rooms.md) discipline: the earliest-ending balloon *forces* where the arrow can go — any arrow bursting it must be at `x <= its end`, and choosing exactly its end maximizes how many others join. Sorting by end lets each cluster greedily absorb everything reachable.

**Why `clusterEnd < point.start` (strict) and not `<=`?** The spans are *closed* intervals — an arrow at the shared boundary `x = 5` bursts both `[1,6]` and `[6,10]`. Overlap requires `start <= clusterEnd`; only a strict gap (`clusterEnd < start`) forces a new arrow. This is the same inclusive-overlap subtlety as [11.3](meeting-rooms.md) (`start >= end` for "can attend").

**The repo's `firstEnd` tracking** — `clusterEnd` in the code above; each new arrow resets it to the new cluster's end. `arrows = 1` handles the always-at-least-one-arrow base case.

## Approach 1 — Merge intervals, count clusters (over-engineering)

Merge overlapping spans then count: correct, but the merge is exactly the greedy in disguise.

## Approach 2 — Sort by end + cluster scan (the repo's version, optimal)

```kotlin
class Solution {
    /**
     * @param points balloon spans [x_start, x_end]
     * @return       minimum arrows to burst all balloons
     */
    fun findMinArrowShots(points: Array<IntArray>): Int {
        if (points.isEmpty()) return 0

        points.sortBy { it[1] }                      // sort by end

        var arrows = 1                               // at least one arrow is required
        var clusterEnd = points[0][1]

        for (point in points) {
            // Check if there is no overlap with the current cluster
            if (clusterEnd < point[0]) {
                arrows++                             // this balloon starts a new cluster
                clusterEnd = point[1]
            }
        }
        return arrows
    }
}
```

```java
import java.util.*;

public class MinimumNumberOfArrowsToBurstBalloons {
    /**
     * @param points balloon spans [x_start, x_end]
     * @return       minimum arrows to burst all balloons
     */
    public int findMinArrowShots(int[][] points) {
        Arrays.sort(points, Comparator.comparingInt(a -> a[1]));   // sort by end

        int arrows = 1;                              // at least one arrow is required
        int clusterEnd = points[0][1];

        for (int[] p : points) {
            if (clusterEnd < p[0]) {                 // no overlap with the current cluster
                arrows++;
                clusterEnd = p[1];
            }
        }
        return arrows;
    }
}
```

```cpp
#include <algorithm>
#include <vector>

class MinimumNumberOfArrowsToBurstBalloons {
public:
    /**
     * @param points balloon spans [x_start, x_end]
     * @return       minimum arrows to burst all balloons
     */
    int findMinArrowShots(std::vector<std::vector<int>>& points) {
        std::sort(points.begin(), points.end(), [](auto& a, auto& b) { return a[1] < b[1]; });

        int arrows = 1;                              // at least one arrow is required
        int clusterEnd = points[0][1];

        for (auto& p : points) {
            if (clusterEnd < p[0]) {                 // no overlap with the current cluster
                arrows++;
                clusterEnd = p[1];
            }
        }
        return arrows;
    }
};
```

```python
def find_min_arrow_shots(points: list[list[int]]) -> int:
    """
    @param points: balloon spans [x_start, x_end]
    @return:       minimum arrows to burst all balloons
    """
    if not points:
        return 0

    points.sort(key=lambda p: p[1])          # sort by end

    arrows = 1                               # at least one arrow is required
    cluster_end = points[0][1]

    for start, end in points:
        if cluster_end < start:              # no overlap with the current cluster
            arrows += 1
            cluster_end = end
    return arrows
```

```rust
impl Solution {
    /// @param points balloon spans [x_start, x_end]
    /// @return       minimum arrows to burst all balloons
    pub fn find_min_arrow_shots(mut points: Vec<Vec<i32>>) -> i32 {
        points.sort_by_key(|p| p[1]);        // sort by end

        let mut arrows = 1;                  // at least one arrow is required
        let mut cluster_end = points[0][1];

        for p in &points {
            if cluster_end < p[0] {          // no overlap with the current cluster
                arrows += 1;
                cluster_end = p[1];
            }
        }
        arrows
    }
}
```

## Dry run

**Input:** `points = [[10,16],[2,8],[1,6],[7,12]]`.

```
sorted by end: [[1,6],[2,8],[7,12],[10,16]].  arrows=1, clusterEnd=6

[1,6]:   6 < 1? no -> stays in the cluster.      (arrow at x=6 bursts it)
[2,8]:   6 < 2? no -> stays.                     (x=6 is inside [2,8])
[7,12]:  6 < 7? YES -> arrows=2.  clusterEnd=12. (new cluster: arrow at x=12)
[10,16]: 12 < 10? no -> stays.

Output: 2 ✓   (arrows at x=6 and x=12)
```

The cluster logic: the first three balloons all contain `x = 6` (spans `[1,6]`, `[2,8]`, and... `[7,12]` does NOT contain 6 — wait, `[7,12]` starts at 7 > 6, so it leaves the cluster. Correct — that's exactly the `6 < 7` trigger.) Arrow 1 at x=6 bursts `[1,6]` and `[2,8]`; arrow 2 at x=12 bursts `[7,12]` and `[10,16]`. The inclusive-boundary check (`clusterEnd < start`, not `<=`) is what lets `[2,8]` share the x=6 arrow with `[1,6]`.

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

- **Non-Overlapping Intervals** ([11.9](non-overlapping-intervals.md)) — the *dual*: count removals to eliminate overlap vs. this page's count of overlap clusters. Same sort-by-end greedy, opposite question.
- **Merge Intervals** ([3.4](../ch03-arrays/merge-intervals.md)) — merging the clusters instead of counting them.
- **Interview follow-up:** "Why is the arrow always placed at the cluster's end?" The earliest-ending balloon in a cluster *forces* an arrow at `x <= its end`; placing it exactly at the end maximizes the span covered (every balloon containing `x = end` joins). Any other position covers a subset — the greedy is optimal by exchange.
