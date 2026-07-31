# 3.9 Container With Most Water

> **Source:** [`src/main/kotlin/array/greedy/ContainerWithMostWater.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/greedy/ContainerWithMostWater.kt)
> **Pattern:** two pointers, move the shorter side · **Core page**

## The Problem

Given `height[i]` (wall heights at position i), find two lines that together with the x-axis form a container holding the **maximum water**: `min(height[a], height[b]) * (b - a)`.

- Constraints: $2 \le n \le 10^5$; heights fit in `Int`.

## Examples

```
Input:  height = [1,8,6,2,5,4,8,3,7]   -> Output: 49   (walls at 1 and 8: 7 * 7)
Input:  height = [1,1]                 -> Output: 1
```

## Intuition — the two-pointer *decision rule*: always move the shorter wall

Start with the widest container (left = 0, right = n-1). Its area is `min(h[l], h[r]) * (r - l)`. To beat it, the next container must be **taller** — narrowing always shrinks the width, so the only hope is a taller limiting wall. Therefore:

> **Move the pointer at the *shorter* wall** — the taller one is the current container's limiting factor, and keeping it gives the next container a chance to be taller-limited.

The repo's rule: `if (height[start] <= height[end]) start++ else end--`. Every move discards the side that can't be part of a better container *as the limiting wall* — the same "commit the decided side" geometry as [3.8](trapping-rain-water.md), mirrored.

**Why is discarding the shorter wall safe?** Any container formed with the current shorter wall and *any* inner wall is narrower (less width) and no taller (the shorter wall caps it) — so it's strictly worse than the current one. The shorter wall can never be part of an optimal answer with an interior partner; only the outer partner is worth keeping. The proof is the standard exchange: an optimal pair `(a, b)` is never discarded before both its pointers are visited.

## Approach 1 — All pairs (O(n^2))

For every `(i, j)` compute the area: correct, quadratic, and the baseline to beat.

## Approach 2 — Two pointers, move the shorter (the repo's version, optimal)

```kotlin
class ContainerWithMostWater {
    /**
     * @param height wall heights
     * @return      maximum water a pair of walls can hold
     */
    fun maxArea(height: IntArray): Int {
        var (start, end) = Pair(0, height.lastIndex)
        var maxWater = 0

        while (start < end) {
            maxWater = maxOf(maxWater, minOf(height[start], height[end]) * (end - start))

            if (height[start] <= height[end]) {
                start++                              // the shorter wall can't limit a better container
            } else {
                end--
            }
        }
        return maxWater
    }
}
```

```java
public class ContainerWithMostWater {
    /**
     * @param height wall heights
     * @return      maximum water a pair of walls can hold
     */
    public int maxArea(int[] height) {
        int start = 0, end = height.length - 1, best = 0;

        while (start < end) {
            best = Math.max(best, Math.min(height[start], height[end]) * (end - start));

            if (height[start] <= height[end]) start++;   // move the shorter wall
            else end--;
        }
        return best;
    }
}
```

```cpp
#include <vector>

class ContainerWithMostWater {
public:
    /**
     * @param height wall heights
     * @return      maximum water a pair of walls can hold
     */
    int maxArea(std::vector<int>& height) {
        int start = 0, end = height.size() - 1, best = 0;

        while (start < end) {
            best = std::max(best, std::min(height[start], height[end]) * (end - start));

            if (height[start] <= height[end]) start++;   // move the shorter wall
            else end--;
        }
        return best;
    }
};
```

```python
def max_area(height: list[int]) -> int:
    """
    @param height: wall heights
    @return:       maximum water a pair of walls can hold
    """
    start, end = 0, len(height) - 1
    best = 0

    while start < end:
        best = max(best, min(height[start], height[end]) * (end - start))

        if height[start] <= height[end]:
            start += 1                         # move the shorter wall
        else:
            end -= 1
    return best
```

```rust
impl Solution {
    /// @param height wall heights
    /// @return      maximum water a pair of walls can hold
    pub fn max_area(height: Vec<i32>) -> i32 {
        let (mut start, mut end) = (0usize, height.len() - 1);
        let mut best = 0;

        while start < end {
            best = best.max(height[start].min(height[end]) * (end - start) as i32);

            if height[start] <= height[end] { start += 1; }   // move the shorter wall
            else { end -= 1; }
        }
        best
    }
}
```

## Dry run

**Input:** `height = [1,8,6,2,5,4,8,3,7]`.

```
l=0 h=1, r=8 h=7: area = 1*8 = 8.   best=8.   1<=7 -> l=1
l=1 h=8, r=8 h=7: area = 7*7 = 49.  best=49.  8<=7? no -> r=7
l=1 h=8, r=7 h=3: area = 3*6 = 18.            r=6
l=1 h=8, r=6 h=8: area = 8*5 = 40.            8<=8 -> l=2
l=2 h=6, r=6 h=8: area = 6*4 = 24.            l=3
l=3 h=2, r=6 h=8: area = 2*3 = 6.             l=4
l=4 h=5, r=6 h=8: area = 5*2 = 10.            l=5
l=5 h=4, r=6 h=8: area = 4*1 = 4.             l=6
loop ends (l == r).

Output: 49 ✓   (the walls at indices 1 and 8)
```

The decision rule in action: from `l=1, r=8` (area 49), the left wall (8) is *not* moved — it's the taller side and the current best's key. Every subsequent container is narrower, so only walls tall enough to beat 49 could matter — none do.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Three variables:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Trapping Rain Water** ([3.8](trapping-rain-water.md)) — the per-cell sum version of the same two-pointer geometry.
- **Max Area Histogram** ([8.5](../ch08-stacks/largest-rectangle-in-histogram.md)) — maximize *rectangle* area (contiguous, all cells) instead of container water: the monotonic stack replaces the two-pointer.
- **Interview follow-up:** "Why can the shorter wall be discarded permanently?" Any container using that wall with an interior partner is both narrower and capped by that same shorter wall — strictly worse than the current one. So the optimal pair either includes the current *other* wall, or both are interior; the exchange argument shows the two-pointer never skips the optimal `(a, b)`.
