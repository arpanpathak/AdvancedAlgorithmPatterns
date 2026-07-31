# 10.15 Max Points On A Line

> **Source:** [`src/main/kotlin/math/geometry/MaxPointsOnALine.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/geometry/MaxPointsOnALine.kt)
> **Pattern:** slope frequency map · **Core page**

## The Problem

The **maximum number of points** that lie on the same straight line.

- Constraints: $1 \le n \le 300$; points unique.

## Examples

```
Input:  points = [[1,1],[2,2],[3,3]]              -> Output: 3
Input:  points = [[1,1],[3,2],[5,3],[4,1],[2,3],[1,4]]  -> Output: 4
```

## Intuition — fix one point, group the rest by slope

Three points are collinear iff their slopes from a fixed anchor are equal. For each anchor `i`, count how many other points share each slope — the max count + 1 (the anchor) is the answer:

```kotlin
for (i in indices) {
    var samePoints = 1                    // duplicates of the anchor
    val slopeMap = mutableMapOf<Double, Int>()

    for (j in i + 1 until size) {
        val dx = points[j][0] - points[i][0]
        val dy = points[j][1] - points[i][1]

        if (dx == 0 && dy == 0) { samePoints++; continue }

        val slope = when {
            dx == 0 -> Double.POSITIVE_INFINITY   // vertical
            dy == 0 -> 0.0                        // horizontal
            else -> dy.toDouble() / dx
        }
        slopeMap[slope] = slopeMap.getOrDefault(slope, 0) + 1
        maxPoints = maxOf(maxPoints, slopeMap[slope]!! + samePoints)
    }
}
```

**Why per-anchor and not a global map?** Slopes are relative to the anchor — two points collinear with anchor A aren't necessarily with anchor B. Each anchor's own map keeps the frame fixed.

**Why is `Double` slope risky but OK here?** Floating-point division can collide for nearly-equal-but-distinct slopes. At n ≤ 300 the risk is small; the *rigorous* version reduces the slope to `(dy/g, dx/g)` normalized with signs ([10.0](pattern-primer.md) note). The repo takes the pragmatic double route — worth naming the tradeoff.

**Why handle vertical/horizontal separately?** `dx == 0` → division by zero: the `+∞` sentinel represents vertical lines, the only slope doubles can't express.

## Approach 1 — Check every triple (O(n³))

Three-point collinearity via cross products: correct, cubic.

## Approach 2 — Slope map per anchor (the repo's version, optimal)

```kotlin
class MaxPointsOnALine {
    /**
     * @param points point coordinates
     * @return       max points on a single line
     */
    fun maxPoints(points: Array<IntArray>): Int {
        if (points.size <= 2) return points.size

        var maxPoints = 1

        for (i in points.indices) {
            var samePoints = 1                          // duplicates of the anchor
            val slopeMap = mutableMapOf<Double, Int>()

            for (j in i + 1 until points.size) {
                val dx = points[j][0] - points[i][0]
                val dy = points[j][1] - points[i][1]

                if (dx == 0 && dy == 0) {
                    samePoints++
                    maxPoints = maxOf(maxPoints, samePoints)
                    continue
                }

                val slope = when {
                    dx == 0 -> Double.POSITIVE_INFINITY // vertical line
                    dy == 0 -> 0.0                      // horizontal line
                    else -> dy.toDouble() / dx
                }

                slopeMap[slope] = slopeMap.getOrDefault(slope, 0) + 1
                maxPoints = maxOf(maxPoints, slopeMap[slope]!! + samePoints)
            }
        }
        return maxPoints
    }
}
```

```java
import java.util.*;

public class MaxPointsOnALine {
    /**
     * @param points point coordinates
     * @return       max points on a single line
     */
    public int maxPoints(int[][] points) {
        if (points.length <= 2) return points.length;

        int best = 1;
        for (int i = 0; i < points.length; i++) {
            int same = 1;
            Map<Double, Integer> slopes = new HashMap<>();

            for (int j = i + 1; j < points.length; j++) {
                int dx = points[j][0] - points[i][0];
                int dy = points[j][1] - points[i][1];

                if (dx == 0 && dy == 0) { same++; best = Math.max(best, same); continue; }

                double slope;
                if (dx == 0) slope = Double.POSITIVE_INFINITY;
                else if (dy == 0) slope = 0.0;
                else slope = (double) dy / dx;

                slopes.put(slope, slopes.getOrDefault(slope, 0) + 1);
                best = Math.max(best, slopes.get(slope) + same);
            }
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <unordered_map>
#include <limits>

class MaxPointsOnALine {
public:
    /**
     * @param points point coordinates
     * @return       max points on a single line
     */
    int maxPoints(std::vector<std::vector<int>>& points) {
        if (points.size() <= 2) return (int)points.size();

        int best = 1;
        for (int i = 0; i < (int)points.size(); i++) {
            int same = 1;
            std::unordered_map<double, int> slopes;

            for (int j = i + 1; j < (int)points.size(); j++) {
                int dx = points[j][0] - points[i][0];
                int dy = points[j][1] - points[i][1];

                if (dx == 0 && dy == 0) { same++; best = std::max(best, same); continue; }

                double slope;
                if (dx == 0) slope = std::numeric_limits<double>::infinity();
                else if (dy == 0) slope = 0.0;
                else slope = (double)dy / dx;

                slopes[slope]++;
                best = std::max(best, slopes[slope] + same);
            }
        }
        return best;
    }
};
```

```python
def max_points(points: list[list[int]]) -> int:
    """
    @param points: point coordinates
    @return:       max points on a single line
    """
    if len(points) <= 2:
        return len(points)

    best = 1
    for i, (x1, y1) in enumerate(points):
        same = 1
        slopes = {}

        for x2, y2 in points[i + 1:]:
            dx, dy = x2 - x1, y2 - y1

            if dx == 0 and dy == 0:
                same += 1
                best = max(best, same)
                continue

            if dx == 0:
                slope = float("inf")
            elif dy == 0:
                slope = 0.0
            else:
                slope = dy / dx

            slopes[slope] = slopes.get(slope, 0) + 1
            best = max(best, slopes[slope] + same)

    return best
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param points point coordinates
    /// @return       max points on a single line
    pub fn max_points(points: Vec<Vec<i32>>) -> i32 {
        if points.len() <= 2 { return points.len() as i32; }

        let mut best = 1;
        for i in 0..points.len() {
            let mut same = 1;
            let mut slopes: HashMap<i64, i32> = HashMap::new();

            for j in (i + 1)..points.len() {
                let (dx, dy) = (points[j][0] - points[i][0], points[j][1] - points[i][1]);

                if dx == 0 && dy == 0 { same += 1; best = best.max(same); continue; }

                // normalized slope key: dy/g, dx/g (avoids float collisions)
                let g = gcd(dx.abs(), dy.abs());
                let key = (dy / g * 20001 + dx / g) as i64;
                *slopes.entry(key).or_insert(0) += 1;
                best = best.max(slopes[&key] + same);
            }
        }
        best
    }
}

fn gcd(mut a: i32, mut b: i32) -> i32 {
    while b != 0 { let t = a % b; a = b; b = t; }
    a
}
```

## Dry run

**Input:** `points = [[1,1],[2,2],[3,3]]`.

```
i=0 (1,1): same=1, slopes={}
  j=1 (2,2): dx=1, dy=1 -> slope 1.0.  slopes={1.0:1}.  best = max(1, 1+1) = 2
  j=2 (3,3): dx=2, dy=2 -> slope 1.0.  slopes={1.0:2}.  best = max(2, 2+1) = 3
i=1 (2,2): same=1, slopes={}
  j=2 (3,3): slope 1.0 -> slopes={1.0:1}.  best = 3
i=2: no j.

Output: 3 ✓
```

The anchor loop's reuse: anchor (1,1) sees both other points at slope 1.0 → 3 collinear. Each anchor's map is fresh — the slope frame resets because collinearity is anchor-relative. Vertical lines get `+∞`; duplicates (`samePoints`) add to every candidate line through the anchor.

## Complexity

**Time.** All pairs:

$$
T(n) = O(n^2)
$$

**Space.** One slope map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Convex Hull** ([3.17](../ch03-arrays/convex-hull.md)) — the collinearity primitive (`cross == 0`) in hull construction.
- **Line Reflection / Rectangle Overlap** (`math/geometry/`) — the geometry family.
- **Interview follow-up:** "Why is the `dy/dx` double risky?" Slopes like `1/3` and `2/6` are equal but `0.3333...` vs `0.3333...` may hash differently under floating point. The exact fix: reduce `(dy/g, dx/g)` (g = gcd) with a canonical sign — the Rust snippet's integer key. Name the tradeoff: doubles are simpler, rationals are exact.
