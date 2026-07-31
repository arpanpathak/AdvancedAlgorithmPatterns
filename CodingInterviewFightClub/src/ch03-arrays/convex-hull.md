# 3.17 Convex Hull (Erect The Fence)

> **Source:** [`src/main/kotlin/math/geometry/ConvexHull.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/geometry/ConvexHull.kt) (+ `ErectTheFence_ConvexHull.kt`)
> **Pattern:** Andrew's monotone chain · **Core page**

## The Problem

Return the points on the **convex hull** (all fence posts enclosing every tree).

- Constraints: $1 \le n \le 3000$; points may be collinear (hull includes them).

## Examples

```
Input:  points = [[1,1],[2,2],[2,0],[2,4],[3,3],[4,2]]
Output: [[1,1],[2,0],[3,3],[2,4],[4,2]]   (the enclosing fence)
```

## Intuition — sort, build the lower hull left→right, the upper hull right→left

Andrew's monotone chain: sort by `(x, y)`, then sweep once building the lower hull (keep points that turn **counter-clockwise**; pop on clockwise turns), sweep reversed for the upper hull, union both:

```kotlin
fun cross(a: Point, b: Point, c: Point): Int =
    (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)   // >0 CCW, <0 CW, =0 collinear

fun buildHalfHull(points: List<Point>): List<Point> {
    val hull = ArrayDeque<Point>()
    for (point in points) {
        while (hull.size >= 2 && cross(hull[hull.size - 2], hull.last(), point) < 0) {
            hull.removeLast()          // clockwise turn: the middle point is inside
        }
        hull.addLast(point)
    }
    return hull.toList()
}
```

**Why the cross product?** `cross(a, b, c) > 0` means the turn `a → b → c` is counter-clockwise. A hull must turn CCW only — any CW turn means `b` is *inside* the hull and gets popped. The sign of one integer decides inclusion: the [3.x](../ch03-arrays/pattern-primer.md) geometry primitive.

**Why two passes?** One direction builds the bottom chain, the reversed sweep the top chain; the union is the full hull. The repo's `(lower + upper).toSet()` dedupes the shared endpoints — with collinear points allowed (the `<= 3` early return), the hull is exactly the fence posts.

## Approach 1 — Jarvis march (gift wrapping, O(n·h))

Repeatedly pick the next point with the leftmost turn: correct, O(nh) worst case.

## Approach 2 — Andrew's monotone chain (the repo's version, optimal)

```kotlin
import kotlin.collections.ArrayDeque

data class Point(val x: Int, val y: Int)

class ConvexHull {
    /**
     * @param trees point coordinates
     * @return      the convex hull (fence posts)
     */
    fun outerTrees(trees: Array<IntArray>): Array<IntArray> {
        if (trees.size <= 3) return trees

        val points = trees.map { Point(it[0], it[1]) }.sortedWith(compareBy({ it.x }, { it.y }))

        val lower = buildHalfHull(points)
        val upper = buildHalfHull(points.reversed())

        return (lower + upper).toSet().map { intArrayOf(it.x, it.y) }.toTypedArray()
    }

    private fun buildHalfHull(points: List<Point>): List<Point> {
        val hull = ArrayDeque<Point>()
        for (point in points) {
            while (hull.size >= 2 && cross(hull[hull.size - 2], hull.last(), point) < 0) {
                hull.removeLast()
            }
            hull.addLast(point)
        }
        return hull.toList()
    }

    private fun cross(a: Point, b: Point, c: Point): Int =
        (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
}
```

```java
import java.util.*;

public class ConvexHull {
    static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }

    private static int cross(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    private static List<Point> half(List<Point> points) {
        List<Point> hull = new ArrayList<>();
        for (Point p : points) {
            while (hull.size() >= 2 && cross(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p) < 0) {
                hull.remove(hull.size() - 1);
            }
            hull.add(p);
        }
        return hull;
    }

    /**
     * @param trees point coordinates
     * @return      the convex hull (fence posts)
     */
    public int[][] outerTrees(int[][] trees) {
        if (trees.length <= 3) return trees;

        List<Point> points = new ArrayList<>();
        for (int[] t : trees) points.add(new Point(t[0], t[1]));
        points.sort((a, b) -> a.x != b.x ? a.x - b.x : a.y - b.y);

        List<Point> lower = half(points);
        Collections.reverse(points);
        List<Point> upper = half(points);

        Set<Point> seen = new HashSet<>(lower);
        seen.addAll(upper);

        int[][] result = new int[seen.size()][2];
        int i = 0;
        for (Point p : seen) { result[i][0] = p.x; result[i][1] = p.y; i++; }
        return result;
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <set>

class ConvexHull {
    struct Point { int x, y; };

    static long long cross(const Point& a, const Point& b, const Point& c) {
        return (long long)(b.x - a.x) * (c.y - a.y) - (long long)(b.y - a.y) * (c.x - a.x);
    }

    static std::vector<Point> half(std::vector<Point>& points) {
        std::vector<Point> hull;
        for (const auto& p : points) {
            while (hull.size() >= 2 && cross(hull[hull.size() - 2], hull.back(), p) < 0) {
                hull.pop_back();
            }
            hull.push_back(p);
        }
        return hull;
    }

public:
    /**
     * @param trees point coordinates
     * @return      the convex hull (fence posts)
     */
    std::vector<std::vector<int>> outerTrees(std::vector<std::vector<int>>& trees) {
        if (trees.size() <= 3) return trees;

        std::vector<Point> points;
        for (auto& t : trees) points.push_back({t[0], t[1]});
        std::sort(points.begin(), points.end(), [](const Point& a, const Point& b) {
            return a.x != b.x ? a.x < b.x : a.y < b.y;
        });

        auto lower = half(points);
        std::reverse(points.begin(), points.end());
        auto upper = half(points);

        std::set<std::pair<int, int>> seen;
        for (auto& p : lower) seen.insert({p.x, p.y});
        for (auto& p : upper) seen.insert({p.x, p.y});

        std::vector<std::vector<int>> result;
        for (auto& [x, y] : seen) result.push_back({x, y});
        return result;
    }
};
```

```python
def outer_trees(trees: list[list[int]]) -> list[list[int]]:
    """
    @param trees: point coordinates
    @return:      the convex hull (fence posts)
    """
    def cross(a, b, c):
        return (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0])

    def half(points):
        hull = []
        for p in points:
            while len(hull) >= 2 and cross(hull[-2], hull[-1], p) < 0:
                hull.pop()              # clockwise turn: inside
            hull.append(p)
        return hull

    if len(trees) <= 3:
        return trees

    points = sorted(trees)              # by x, then y
    lower = half(points)
    upper = half(points[::-1])

    return list({tuple(p) for p in lower + upper})
```

```rust
impl Solution {
    /// @param trees point coordinates
    /// @return      the convex hull (fence posts)
    pub fn outer_trees(mut trees: Vec<Vec<i32>>) -> Vec<Vec<i32>> {
        if trees.len() <= 3 { return trees; }

        trees.sort();
        let cross = |a: &Vec<i32>, b: &Vec<i32>, c: &Vec<i32>| {
            (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0])
        };

        let half = |points: &Vec<Vec<i32>>| -> Vec<Vec<i32>> {
            let mut hull: Vec<Vec<i32>> = Vec::new();
            for p in points {
                while hull.len() >= 2 && cross(&hull[hull.len() - 2], &hull[hull.len() - 1], p) < 0 {
                    hull.pop();
                }
                hull.push(p.clone());
            }
            hull
        };

        let lower = half(&trees);
        let mut rev = trees.clone();
        rev.reverse();
        let upper = half(&rev);

        let mut seen = std::collections::HashSet::new();
        for p in lower.iter().chain(upper.iter()) { seen.insert(p.clone()); }
        seen.into_iter().collect()
    }
}
```

## Dry run

**Input:** `trees = [[1,1],[2,2],[2,0],[2,4],[3,3],[4,2]]`.

```
sorted: (1,1), (2,0), (2,2), (2,4), (3,3), (4,2)

LOWER (left→right):
(1,1),(2,0) push.  (2,2): cross((1,1),(2,0),(2,2)) = (1)(2)-(0)(1) = 2 > 0 CCW -> push.  [(1,1),(2,0),(2,2)]
(2,4): cross((2,0),(2,2),(2,4)) = (0)(4)-(2)(0) = 0 -> not < 0 -> push.  [(1,1),(2,0),(2,2),(2,4)]
(3,3): cross((2,2),(2,4),(3,3)) = (0)(1)-(2)(1) = -2 < 0 CW -> pop (2,4).
       cross((2,0),(2,2),(3,3)) = (0)(3)-(2)(1) = -2 < 0 -> pop (2,2).  [(1,1),(2,0)]
       cross((1,1),(2,0),(3,3)) = (1)(2)-(-1)(2) = 4 > 0 -> push (3,3).  [(1,1),(2,0),(3,3)]
(4,2): cross((2,0),(3,3),(4,2)) = (1)(2)-(3)(2) = -4 < 0 -> pop (3,3).  [(1,1),(2,0)]
       cross((1,1),(2,0),(4,2)) = (1)(1)-(-1)(3) = 4 > 0 -> push.  [(1,1),(2,0),(4,2)]

lower = [(1,1),(2,0),(4,2)].   upper (reversed sweep) = [(4,2),(3,3),(2,4),(1,1)].

union = {(1,1),(2,0),(4,2),(3,3),(2,4)} = the 5 fence posts ✓
```

The CW pops are the whole algorithm: `(2,2)` and `(2,4)` are *inside* the hull (the fence cuts across), so the cross-product's negative sign evicts them. Collinear points (cross = 0) survive because the pop is `< 0`, not `<= 0` — the repo keeps fence posts on the boundary.

## Complexity

**Time.** Sort + two sweeps:

$$
T(n) = O(n \log n)
$$

**Space.** The hull lists:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Max Points On A Line** ([10.15](../ch10-hash-tables/max-points-on-a-line.md)) — the collinearity primitive (`cross == 0`) in a counting role.
- **Rectangle Overlap / Rectangle Area** (`math/geometry/`) — the geometry family's axis-aligned members.
- **Interview follow-up:** "Why keep collinear points (pop on `< 0` not `<= 0`)?" The problem asks for *all* fence posts — boundary points are on the hull. With `<= 0` they'd be dropped (the classic "convex hull of all points" variant wants them kept). The strict inequality is the problem statement in one character.
