# 3.30 Rectangle Area

> **Source**: [`src/main/kotlin/math/geometry/RectangleArea.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/geometry/RectangleArea.kt)
> **Pattern**: inclusion-exclusion of areas · **Core page**

## The Problem

Total area covered by two axis-aligned rectangles.

- Constraints: coordinates within ±10⁴.

## Examples

```
Input:  ax1=-3, ay1=0, ax2=3, ay2=4, bx1=0, by1=-1, bx2=9, by2=2
Output: 45
```

## Intuition — sum the areas, subtract the overlap

`area = area1 + area2 - overlap`; the overlap is the clamped intersection `[max(x1), min(x2)] × [max(y1), min(y2)]`:

```kotlin
val h1 = abs(ax1 - ax2)
val w1 = abs(ay1 - ay2)
val h2 = abs(bx1 - bx2)
val w2 = abs(by1 - by2)

val h = minOf(ax2, bx2) - maxOf(ax1, bx1)
val w = minOf(ay2, by2) - maxOf(ay1, by1)

return h1 * w1 + h2 * w2 - maxOf(0, h) * maxOf(0, w)
```

**Why the `maxOf(0, ...)` clamps?** A negative overlap width means the rectangles don't intersect on that axis — clamping to 0 zeroes the overlap term. The [3.31](rectangle-overlap.md) test, in area form.

## Approach 1 — Clamped intersection (the repo's version, optimal)

```kotlin
class RectangleArea {
    /**
     * @return total covered area
     */
    fun computeArea(ax1: Int, ay1: Int, ax2: Int, ay2: Int, bx1: Int, by1: Int, bx2: Int, by2: Int): Int {
        val h1 = abs(ax1 - ax2)
        val w1 = abs(ay1 - ay2)
        val h2 = abs(bx1 - bx2)
        val w2 = abs(by1 - by2)

        val h = minOf(ax2, bx2) - maxOf(ax1, bx1)
        val w = minOf(ay2, by2) - maxOf(ay1, by1)

        return h1 * w1 + h2 * w2 - maxOf(0, h) * maxOf(0, w)
    }
}
```

```java
public class RectangleArea {
    /**
     * @return total covered area
     */
    public int computeArea(int ax1, int ay1, int ax2, int ay2, int bx1, int by1, int bx2, int by2) {
        int a = (ax2 - ax1) * (ay2 - ay1);
        int b = (bx2 - bx1) * (by2 - by1);

        int ox = Math.min(ax2, bx2) - Math.max(ax1, bx1);
        int oy = Math.min(ay2, by2) - Math.max(ay1, by1);

        int overlap = (ox > 0 && oy > 0) ? ox * oy : 0;
        return a + b - overlap;
    }
}
```

```cpp
#include <algorithm>

class RectangleArea {
public:
    /**
     * @return total covered area
     */
    int computeArea(int ax1, int ay1, int ax2, int ay2, int bx1, int by1, int bx2, int by2) {
        int a = (ax2 - ax1) * (ay2 - ay1);
        int b = (bx2 - bx1) * (by2 - by1);

        int ox = std::min(ax2, bx2) - std::max(ax1, bx1);
        int oy = std::min(ay2, by2) - std::max(ay1, by1);

        int overlap = (ox > 0 && oy > 0) ? ox * oy : 0;
        return a + b - overlap;
    }
};
```

```python
def compute_area(ax1: int, ay1: int, ax2: int, ay2: int,
                 bx1: int, by1: int, bx2: int, by2: int) -> int:
    """
    @return: total covered area
    """
    a = (ax2 - ax1) * (ay2 - ay1)
    b = (bx2 - bx1) * (by2 - by1)

    ox = min(ax2, bx2) - max(ax1, bx1)
    oy = min(ay2, by2) - max(ay1, by1)

    return a + b - (ox * oy if ox > 0 and oy > 0 else 0)
```

```rust
impl Solution {
    /// @return total covered area
    pub fn compute_area(ax1: i32, ay1: i32, ax2: i32, ay2: i32,
                        bx1: i32, by1: i32, bx2: i32, by2: i32) -> i32 {
        let a = (ax2 - ax1) * (ay2 - ay1);
        let b = (bx2 - bx1) * (by2 - by1);

        let ox = ax2.min(bx2) - ax1.max(bx1);
        let oy = ay2.min(by2) - ay1.max(by1);

        let overlap = if ox > 0 && oy > 0 { ox * oy } else { 0 };
        a + b - overlap
    }
}
```

## Dry run

**Input:** the example.

```
a = 6*4 = 24.  b = 9*3 = 27.
ox = min(3,9) - max(-3,0) = 3 - 0 = 3.  oy = min(4,2) - max(0,-1) = 2 - 0 = 2.
overlap = 6.  total = 24 + 27 - 6 = 45 ✓
```

## Complexity

**Time.** O(1):

$$
T = O(1)
$$

**Space.** O(1):

$$
S = O(1)
$$

## Variants & follow-ups

- **Rectangle Overlap** ([3.31](rectangle-overlap.md)) — the boolean test this page's clamp embodies.
- **Interview follow-up:** "Why is clamping the overlap safe?" Non-intersecting rectangles give a negative (or zero) clamp — `max(0, ox*oy)` drops the overlap term, leaving the plain sum. The clamp IS the overlap test.
