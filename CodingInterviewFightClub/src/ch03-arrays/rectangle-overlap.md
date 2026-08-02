# 3.31 Rectangle Overlap

> **Source**: [`src/main/kotlin/math/geometry/RectangleOverlap.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/geometry/RectangleOverlap.kt)
> **Pattern**: axis-separation test · **Core page**

## The Problem

Do two axis-aligned rectangles overlap (positive area)?

- Constraints: integer coords.

## Examples

```
Input:  rec1 = [0,0,2,2], rec2 = [1,1,3,3]   -> Output: true
Input:  rec1 = [0,0,1,1], rec2 = [1,0,2,1]   -> Output: false (touching = no overlap)
```

## Intuition — overlap iff NOT separated on either axis

Two rectangles overlap iff their x-intervals and y-intervals both intersect with positive length. The separation test is cleaner:

```kotlin
val xOverlap = aX1 < bX2 && bX1 < aX2    // A not fully left of B, B not fully left of A
val yOverlap = aY1 < bY2 && bY1 < aY2
return xOverlap && yOverlap
```

**Why strict `<`?** Touching edges (e.g. `aX2 == bX1`) give zero overlap area — the strict inequality excludes edge-touching. The problem defines overlap as positive area.

## Approach 1 — The [3.30](rectangle-area.md) clamp test

`ox > 0 && oy > 0`: same idea via the intersection dimensions.

## Approach 2 — Separation check (the repo's version, optimal)

```kotlin
class RectangleOverlap {
    /**
     * @param rec1 [x1, y1, x2, y2]
     * @param rec2 [x1, y1, x2, y2]
     * @return     true iff positive-area overlap
     */
    fun isRectangleOverlap(rec1: IntArray, rec2: IntArray): Boolean {
        val (aX1, aY1, aX2, aY2) = rec1
        val (bX1, bY1, bX2, bY2) = rec2

        val xOverlap = aX1 < bX2 && bX1 < aX2
        val yOverlap = aY1 < bY2 && bY1 < aY2

        return xOverlap && yOverlap
    }
}
```

```java
public class RectangleOverlap {
    /**
     * @param rec1 [x1, y1, x2, y2]
     * @param rec2 [x1, y1, x2, y2]
     * @return     true iff positive-area overlap
     */
    public boolean isRectangleOverlap(int[] rec1, int[] rec2) {
        return rec1[0] < rec2[2] && rec2[0] < rec1[2] &&
               rec1[1] < rec2[3] && rec2[1] < rec1[3];
    }
}
```

```cpp
#include <vector>

class RectangleOverlap {
public:
    /**
     * @param rec1 [x1, y1, x2, y2]
     * @param rec2 [x1, y1, x2, y2]
     * @return     true iff positive-area overlap
     */
    bool isRectangleOverlap(std::vector<int>& rec1, std::vector<int>& rec2) {
        return rec1[0] < rec2[2] && rec2[0] < rec1[2] &&
               rec1[1] < rec2[3] && rec2[1] < rec1[3];
    }
};
```

```python
def is_rectangle_overlap(rec1: list[int], rec2: list[int]) -> bool:
    """
    @param rec1: [x1, y1, x2, y2]
    @param rec2: [x1, y1, x2, y2]
    @return:     true iff positive-area overlap
    """
    return rec1[0] < rec2[2] and rec2[0] < rec1[2] and \
           rec1[1] < rec2[3] and rec2[1] < rec1[3]
```

```rust
impl Solution {
    /// @param rec1 [x1, y1, x2, y2]
    /// @param rec2 [x1, y1, x2, y2]
    /// @return     true iff positive-area overlap
    pub fn is_rectangle_overlap(rec1: Vec<i32>, rec2: Vec<i32>) -> bool {
        rec1[0] < rec2[2] && rec2[0] < rec1[2] &&
        rec1[1] < rec2[3] && rec2[1] < rec1[3]
    }
}
```

## Reading the code — what's actually happening

```kotlin
val xOverlap = aX1 < bX2 && bX1 < aX2
val yOverlap = aY1 < bY2 && bY1 < aY2
return xOverlap && yOverlap
```

Decompose the problem: two rectangles overlap in the plane **iff their shadows overlap on the x-axis AND their shadows overlap on the y-axis**. Each shadow is just a 1-D interval, and 1-D interval overlap has a famously simple test.

- **`aX1 < bX2` — A doesn't start *past* B's right edge.** If A's left edge were at or beyond B's right edge (`aX1 >= bX2`), A would be entirely to the right of B — no x-overlap.
- **`bX1 < aX2` — B doesn't start *past* A's right edge.** Symmetric: if B's left edge is at or beyond A's right edge, B is entirely to the right of A.
- **Both must hold → the intervals interleave.** If neither rectangle is entirely on one side of the other, their x-intervals must overlap with positive length. Same logic on the y-axis for vertical overlap.
- **The strict `<` is the "positive area" rule.** When `aX2 == bX1` (B's left edge exactly touches A's right edge), the x-overlap would be zero-width — the strict comparison correctly rejects it as "no overlap". Same for touching corners. The problem explicitly defines overlap as *positive area*, so equality never counts.
- **Why not compute the intersection rectangle?** The clamp-based twin ([3.30](rectangle-area.md)) computes `ox = min(aX2,bX2) - max(aX1,bX1)` and checks `ox > 0 && oy > 0`. This version skips the arithmetic and tests the separation conditions directly — same answer, four comparisons instead of six operations. De Morgan's law is the bridge: "overlap ⟺ NOT (A left of B OR B left of A OR A below B OR B below A)".

Trace `rec1 = [0,0,1,1], rec2 = [1,0,2,1]`: `0 < 2` ✓ but `1 < 1` ✗ → x-overlap false → overall `false` — the rectangles only touch along the line `x=1`, which has zero area.

## Dry run

**Input:** `rec1 = [0,0,2,2], rec2 = [1,1,3,3]`.

```
x: 0 < 3 ✓ && 1 < 2 ✓.  y: 0 < 3 ✓ && 1 < 2 ✓.
Output: true ✓

Input: [0,0,1,1] vs [1,0,2,1]: x: 0 < 2 ✓ && 1 < 1? false.  Output: false ✓
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

- **Rectangle Area** ([3.30](rectangle-area.md)) — the quantitative twin.
- **Interview follow-up:** "Why does this work for any orientation?" The four comparisons are the two axis-separations: A left of B (`aX2 <= bX1`), B left of A, A below B, B below A. Overlap ⟺ none holds — De Morgan on the separation conditions.
