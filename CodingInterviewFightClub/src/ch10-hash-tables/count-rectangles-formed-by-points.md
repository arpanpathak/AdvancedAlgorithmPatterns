# 10.9 Count Rectangles Formed By Points

> **Source:** the *Coding Interview Fight Club* notes (`countRectangles` — Computational Geometry section); the `geo/` folder holds the quadtree/kd-tree extensions
> **Pattern:** diagonal pairing + set lookup · **Core page**

## The Problem

Given a list of 2-D points, count the number of **axis-aligned rectangles** whose four corners are among the points. (Variant: *tilted* rectangles via the midpoint technique.)

- Constraints: small-to-medium point sets; coordinates fit in `Int`.

## Examples

```
Input:  points = [[0,0],[1,1],[1,0],[0,1]]   -> Output: 1   (the unit square)
Input:  points = [[0,0],[2,2],[2,0],[0,2],[1,1]] -> Output: 1   (plus an interior point, useless)
```

## Intuition — every rectangle has two diagonals; check the corners a diagonal implies

A rectangle is uniquely determined by **any diagonal** (a pair of opposite corners). So: pick every pair of points as a *potential diagonal* — if they differ in both coordinates — and check whether the **other two corners** exist in a set:

```
for i < j:
    (x1, y1), (x2, y2) = points[i], points[j]
    if x1 == x2 or y1 == y2: continue            # same row/column: not a diagonal
    if (x1, y2) in set and (x2, y1) in set: count++
return count / 2                                 # each rectangle counted once per diagonal
```

**Why divide by 2?** Each rectangle has *two* diagonals, and the pair loop visits both — counting each rectangle twice. Dividing is cheaper than deduplicating.

**Why a set?** The membership check is the whole inner loop — a `HashSet` of `(x, y)` pairs makes it O(1). This is the [10.1](two-sum.md) "hash the thing you'll query" move.

**The tilted variant (midpoint technique):** axis-alignment isn't required. Two segments are the diagonals of *some* rectangle iff they have the **same midpoint and the same squared length**. So: hash every pair's `(midpoint, sqLength)` key; a key shared by k diagonals yields $\binom{k}{2}$ rectangles. Handles rotated rectangles with the same set-lookup spirit, one dimension up.

## Approach 1 — Brute force quadruples (O(n^4))

For every 4-point subset, check rectangle-ness: correct, hopeless at n = 100.

## Approach 2 — Diagonal pairing + set lookup (the notes' version, optimal)

```kotlin
/**
 * @param points list of (x, y)
 * @return       number of axis-aligned rectangles
 */
fun countRectangles(points: List<IntArray>): Int {
    val pointSet = points.map { (x, y) -> x to y }.toSet()
    var count = 0

    for (i in points.indices) {
        val (x1, y1) = points[i]
        for (j in i + 1 until points.size) {
            val (x2, y2) = points[j]

            if (x1 == x2 || y1 == y2) continue        // not a diagonal

            // The other two corners, formed by mixing the coordinates
            if ((x1 to y2) in pointSet && (x2 to y1) in pointSet) {
                count++
            }
        }
    }
    return count / 2                                  // each rectangle counted twice
}
```

```java
import java.util.*;

public class CountRectangles {
    /**
     * @param points list of (x, y)
     * @return       number of axis-aligned rectangles
     */
    public int countRectangles(int[][] points) {
        Set<String> set = new HashSet<>();
        for (int[] p : points) set.add(p[0] + "," + p[1]);

        int count = 0;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                int x1 = points[i][0], y1 = points[i][1];
                int x2 = points[j][0], y2 = points[j][1];

                if (x1 == x2 || y1 == y2) continue;   // not a diagonal

                if (set.contains(x1 + "," + y2) && set.contains(x2 + "," + y1)) {
                    count++;                           // the other two corners exist
                }
            }
        }
        return count / 2;                              // each rectangle counted twice
    }
}
```

```cpp
#include <set>
#include <utility>
#include <vector>

class CountRectangles {
public:
    /**
     * @param points list of (x, y)
     * @return       number of axis-aligned rectangles
     */
    int countRectangles(std::vector<std::vector<int>>& points) {
        std::set<std::pair<int, int>> set;
        for (auto& p : points) set.insert({p[0], p[1]});

        int count = 0;
        for (int i = 0; i < (int)points.size(); i++) {
            for (int j = i + 1; j < (int)points.size(); j++) {
                int x1 = points[i][0], y1 = points[i][1];
                int x2 = points[j][0], y2 = points[j][1];

                if (x1 == x2 || y1 == y2) continue;   // not a diagonal

                if (set.count({x1, y2}) && set.count({x2, y1})) {
                    count++;                           // the other two corners exist
                }
            }
        }
        return count / 2;                              // each rectangle counted twice
    }
};
```

```python
def count_rectangles(points: list[list[int]]) -> int:
    """
    @param points: list of (x, y)
    @return:       number of axis-aligned rectangles
    """
    point_set = {(x, y) for x, y in points}
    count = 0

    for i in range(len(points)):
        x1, y1 = points[i]
        for j in range(i + 1, len(points)):
            x2, y2 = points[j]

            if x1 == x2 or y1 == y2:
                continue                     # not a diagonal

            if (x1, y2) in point_set and (x2, y1) in point_set:
                count += 1                   # the other two corners exist
    return count // 2                        # each rectangle counted twice
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param points list of (x, y)
    /// @return       number of axis-aligned rectangles
    pub fn count_rectangles(points: Vec<Vec<i32>>) -> i32 {
        let set: HashSet<(i32, i32)> = points.iter().map(|p| (p[0], p[1])).collect();
        let mut count = 0;

        for i in 0..points.len() {
            for j in (i + 1)..points.len() {
                let (x1, y1) = (points[i][0], points[i][1]);
                let (x2, y2) = (points[j][0], points[j][1]);

                if x1 == x2 || y1 == y2 { continue; }          // not a diagonal

                if set.contains(&(x1, y2)) && set.contains(&(x2, y1)) {
                    count += 1;                                // the other two corners exist
                }
            }
        }
        count / 2                                              // each rectangle counted twice
    }
}
```

## Dry run

**Input:** `points = [[0,0],[1,1],[1,0],[0,1]]`.

```
set = {(0,0),(1,1),(1,0),(0,1)}

(0,0) with (1,1): differ in both coords.  (0,1) in set? YES.  (1,0) in set? YES -> count=1
(0,0) with (1,0): same row -> skip.   (0,0) with (0,1): same col -> skip.
(1,1) with (1,0): same row -> skip.   (1,1) with (0,1): same col -> skip.
(1,0) with (0,1): differ.  (1,1) in set? YES.  (0,0) in set? YES -> count=2

Output: 2 / 2 = 1 ✓
```

The single rectangle is counted once via the `(0,0)-(1,1)` diagonal and once via `(1,0)-(0,1)` — the `/ 2` is exactly the two-diagonals-per-rectangle symmetry. The same-row/column pairs are skipped by the diagonal condition.

## Complexity

**Time.** Pair loop with O(1) set lookups:

$$
T(n) = O(n^2)
$$

**Space.** The point set:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **The tilted variant** — hash `(midpoint, squared length)` per pair; a key with k diagonals yields `k*(k-1)/2` rectangles. Same "hash the property, count the collisions" skeleton, one dimension up.
- **Valid Sudoku / longest-consecutive** ([10.7](valid-sudoku.md), [10.5](longest-consecutive-sequence.md)) — the "put it in a set, then query membership" family this page belongs to.
- **Interview follow-up:** "Why is checking *one* diagonal sufficient?" A rectangle's four corners determine two diagonals, and either diagonal determines the other two corners by coordinate mixing — the pair loop's `(x1,y2)`/`(x2,y1)` check reconstructs the rectangle from one diagonal uniquely. The `/2` is the only redundancy cost.
