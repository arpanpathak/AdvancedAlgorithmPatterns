# 3.33 Rectangle Area II

> **Source**: [`src/main/kotlin/math/geometry/RectangleArea_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/geometry/RectangleArea_II.kt)
> **Pattern**: coordinate compression sweep · **Core page**

## The Problem

The **union** area of many axis-aligned rectangles (overlaps counted once).

- Constraints: ≤ 200 rectangles; coords ≤ 10⁹.

## Examples

```
Input:  rectangles = [[0,0,2,2],[1,0,2,3],[1,0,3,1]]
Output: 6
```

## Intuition — compress the X coordinates; each vertical strip's covered height sums

Collect every distinct x; each strip `[x[i], x[i+1]]` has a width and a covered height (the union of y-intervals of rectangles spanning the strip):

```kotlin
val xCoords = rectangles.flatMap { listOf(it[0], it[2]) }.distinct().sorted()

var totalArea = 0L

for (i in 0 until xCoords.size - 1) {
    val width = (xCoords[i + 1] - xCoords[i]).toLong()
    if (width == 0L) continue

    // collect the y-ranges of rectangles covering this strip
    val yIntervals = mutableListOf<Pair<Int, Int>>()
    for (rect in rectangles) {
        if (rect[0] <= xCoords[i] && xCoords[i + 1] <= rect[2]) {
            yIntervals.add(rect[1] to rect[3])
        }
    }

    val coveredHeight = mergeAndSumY(yIntervals)     // union of y intervals
    totalArea += width * coveredHeight
}
```

**Why compress?** Coordinates reach 10⁹ — sweeping every integer x is impossible. The ≤ 2n distinct x values define strips inside which coverage is constant ([3.31](rectangle-overlap.md) union in strip form).

**Why merge the y-intervals per strip?** A strip's covered height is the *union* of the rectangles' y-ranges spanning it — overlapping y-ranges merge ([11.3](../ch11-greedy/meeting-rooms.md) merge machinery).

## Approach 1 — Coordinate compression + per-strip merge (the repo's version, optimal)

```kotlin
class RectangleArea_II {
    /**
     * @param rectangles [x1, y1, x2, y2] list
     * @return          union area mod 1e9+7
     */
    fun rectangleArea(rectangles: Array<IntArray>): Int {
        val MOD = 1_000_000_007L

        val xCoords = rectangles.flatMap { listOf(it[0], it[2]) }.distinct().sorted()

        var totalArea = 0L

        for (i in 0 until xCoords.size - 1) {
            val width = (xCoords[i + 1] - xCoords[i]).toLong()
            if (width == 0L) continue

            val yIntervals = mutableListOf<Pair<Int, Int>>()
            for (rect in rectangles) {
                if (rect[0] <= xCoords[i] && xCoords[i + 1] <= rect[2]) {
                    yIntervals.add(rect[1] to rect[3])
                }
            }

            if (yIntervals.isEmpty()) continue

            var coveredHeight = 0L
            var currentBottom = -1
            var currentTop = -1

            for ((y1, y2) in yIntervals.sortedBy { it.first }) {
                if (y1 > currentTop) {           // new disjoint interval
                    coveredHeight += (currentTop - currentBottom)
                    currentBottom = y1
                    currentTop = y2
                } else {
                    currentTop = maxOf(currentTop, y2)
                }
            }
            coveredHeight += (currentTop - currentBottom)

            totalArea = (totalArea + width * coveredHeight) % MOD
        }
        return totalArea.toInt()
    }
}
```

```java
import java.util.*;

public class RectangleAreaII {
    /**
     * @param rectangles [x1, y1, x2, y2] list
     * @return          union area mod 1e9+7
     */
    public int rectangleArea(int[][] rectangles) {
        long MOD = 1_000_000_007L;

        List<Integer> xs = new ArrayList<>();
        for (int[] r : rectangles) { xs.add(r[0]); xs.add(r[2]); }
        Collections.sort(xs);

        long total = 0;
        for (int i = 0; i < xs.size() - 1; i++) {
            int x1 = xs.get(i), x2 = xs.get(i + 1);
            if (x1 == x2) continue;

            List<int[]> ys = new ArrayList<>();
            for (int[] r : rectangles) {
                if (r[0] <= x1 && x2 <= r[2]) ys.add(new int[]{r[1], r[3]});
            }
            if (ys.isEmpty()) continue;

            ys.sort((a, b) -> a[0] - b[0]);
            long height = 0, bottom = -1, top = -1;

            for (int[] y : ys) {
                if (y[0] > top) {
                    height += top - bottom;
                    bottom = y[0];
                    top = y[1];
                } else {
                    top = Math.max(top, y[1]);
                }
            }
            height += top - bottom;

            total = (total + (long) (x2 - x1) * height) % MOD;
        }
        return (int) total;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class RectangleAreaII {
public:
    /**
     * @param rectangles [x1, y1, x2, y2] list
     * @return          union area mod 1e9+7
     */
    int rectangleArea(std::vector<std::vector<int>>& rectangles) {
        long long MOD = 1e9 + 7;

        std::vector<int> xs;
        for (auto& r : rectangles) { xs.push_back(r[0]); xs.push_back(r[2]); }
        std::sort(xs.begin(), xs.end());

        long long total = 0;
        for (int i = 0; i < (int)xs.size() - 1; i++) {
            if (xs[i] == xs[i + 1]) continue;

            std::vector<std::pair<int, int>> ys;
            for (auto& r : rectangles) {
                if (r[0] <= xs[i] && xs[i + 1] <= r[2]) ys.push_back({r[1], r[3]});
            }
            if (ys.empty()) continue;

            std::sort(ys.begin(), ys.end());
            long long height = 0, bottom = -1, top = -1;

            for (auto& [y1, y2] : ys) {
                if (y1 > top) { height += top - bottom; bottom = y1; top = y2; }
                else top = std::max(top, y2);
            }
            height += top - bottom;

            total = (total + (long long)(xs[i + 1] - xs[i]) * height) % MOD;
        }
        return (int)total;
    }
};
```

```python
def rectangle_area(rectangles: list[list[int]]) -> int:
    """
    @param rectangles: [x1, y1, x2, y2] list
    @return:           union area mod 1e9+7
    """
    MOD = 10**9 + 7

    xs = sorted({x for rect in rectangles for x in (rect[0], rect[2])})
    total = 0

    for i in range(len(xs) - 1):
        x1, x2 = xs[i], xs[i + 1]
        if x1 == x2:
            continue

        ys = sorted(
            (rect[1], rect[3]) for rect in rectangles
            if rect[0] <= x1 and x2 <= rect[2]
        )
        if not ys:
            continue

        height = 0
        bottom = top = -1
        for y1, y2 in ys:
            if y1 > top:
                height += top - bottom
                bottom, top = y1, y2
            else:
                top = max(top, y2)
        height += top - bottom

        total = (total + (x2 - x1) * height) % MOD

    return total
```

```rust
impl Solution {
    /// @param rectangles [x1, y1, x2, y2] list
    /// @return          union area mod 1e9+7
    pub fn rectangle_area(rectangles: Vec<Vec<i32>>) -> i32 {
        let mut xs: Vec<i32> = rectangles.iter()
            .flat_map(|r| vec![r[0], r[2]])
            .collect();
        xs.sort_unstable();
        xs.dedup();

        let mut total: i64 = 0;
        for w in xs.windows(2) {
            let (x1, x2) = (w[0], w[1]);
            if x1 == x2 { continue; }

            let mut ys: Vec<(i32, i32)> = rectangles.iter()
                .filter(|r| r[0] <= x1 && x2 <= r[2])
                .map(|r| (r[1], r[3]))
                .collect();
            if ys.is_empty() { continue; }
            ys.sort();

            let mut height: i64 = 0;
            let (mut bottom, mut top) = (-1, -1);
            for (y1, y2) in ys {
                if y1 > top {
                    height += (top - bottom) as i64;
                    bottom = y1;
                    top = y2;
                } else {
                    top = top.max(y2);
                }
            }
            height += (top - bottom) as i64;

            total = (total + (x2 - x1) as i64 * height) % 1_000_000_007;
        }
        total as i32
    }
}
```

## Dry run

**Input:** `rectangles = [[0,0,2,2],[1,0,2,3],[1,0,3,1]]`.

```
xs = [0,1,2,3].
strip [0,1]: rects covering: [0,0,2,2].  ys [(0,2)].  height 2.  area 1*2 = 2.
strip [1,2]: rects: all three.  ys [(0,2),(0,3),(0,1)] -> merge: [(0,3)].  height 3.  area 3.
strip [2,3]: rects: [1,0,3,1].  ys [(0,1)].  height 1.  area 1.
total = 2 + 3 + 1 = 6 ✓
```

The compression is the whole win: 4 strips instead of 10⁹ x-values. Each strip's y-merge is the [11.3](../ch11-greedy/meeting-rooms.md) union — overlapping ranges collapse into one covered span.

## Complexity

**Time.** Strips × rectangles:

$$
T(r) = O(r^2 \log r)
$$

**Space.** Coordinates + intervals:

$$
S(r) = O(r)
$$

## Variants & follow-ups

- **Rectangle Area** ([3.30](rectangle-area.md)) — the two-rectangle special case (no compression needed).
- **Interview follow-up:** "Why is the per-strip coverage constant?" Between two consecutive distinct x-values, no rectangle edge crosses — every rectangle either fully spans the strip or misses it. Constant coverage per strip makes width × merged-height exact.
