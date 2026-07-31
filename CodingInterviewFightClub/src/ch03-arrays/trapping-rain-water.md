# 3.8 Trapping Rain Water

> **Source:** [`src/main/kotlin/array/twopointer/TrappingRainWater.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/TrappingRainWater.kt)
> **Pattern:** two pointers with running maxima · **Core page**

## The Problem

Given `height[i]` (bar heights), compute how much water the terrain can trap after rain — every cell's water is limited by the **min of the tallest bar to its left and right**.

- Constraints: $1 \le n \le 2 \times 10^4$; heights fit in `Int`.

## Examples

```
Input:  height = [0,1,0,2,1,0,1,3,2,1,2,1]   -> Output: 6
Input:  height = [4,2,0,3,2,5]               -> Output: 9
```

## Intuition — water at cell i = min(leftMax, rightMax) - height[i]

The per-cell formula is the whole problem:

$$
\text{water}[i] = \max(0,\; \min(\text{leftMax}[i], \text{rightMax}[i]) - \text{height}[i])
$$

Where `leftMax[i]` = tallest bar at or left of i, `rightMax[i]` = tallest at or right of i. Three flavors:

1. **Precompute both arrays** (the repo's `trap`): two passes fill `leftMax` and `rightMax`, one pass sums — $O(n)$ time, $O(n)$ space.
2. **The two-pointer version** (the repo's `trapConstantSpace`): walk from both ends, keeping the *current* left/right maxima. At each step, the side with the **shorter wall** is the one whose water is decided — because its limiting factor (the shorter maximum) is already known. $O(n)$ time, $O(1)$ space — the clean version.
3. **Monotonic stack** — the stack-based alternative (bar-indices, water fills valleys when a taller bar appears).

**Why does the shorter side's water get decided immediately?** If `height[left] <= height[right]`, then whatever happens between them, the right side has a bar at least `height[right]` tall — so the water at `left` is limited only by `leftMax` (already known). The two-pointer "commit the decided side" rhythm is the same one from [3.9](container-with-most-water.md).

## Approach 1 — Precompute left/right maxima (O(n) space)

The direct formula: fill `leftMax`, fill `rightMax`, sum. Correct and easy to explain; the space is the only waste.

## Approach 2 — Two-pointer with running maxima (the repo's constant-space version, optimal)

```kotlin
class TrappingRainWater {
    /**
     * @param height bar heights
     * @return      total water trapped
     */
    fun trapConstantSpace(height: IntArray): Int {
        var (left, right, leftMax, rightMax, waterTrapped) = listOf(0, height.lastIndex, 0, 0, 0)

        while (left <= right) {
            if (height[left] <= height[right]) {
                waterTrapped += (leftMax - height[left]).coerceAtLeast(0)  // decided by leftMax
                leftMax = maxOf(leftMax, height[left])
                left++
            } else {
                waterTrapped += (rightMax - height[right]).coerceAtLeast(0) // decided by rightMax
                rightMax = maxOf(rightMax, height[right])
                right--
            }
        }
        return waterTrapped
    }
}
```

```java
public class TrappingRainWater {
    /**
     * @param height bar heights
     * @return      total water trapped
     */
    public int trap(int[] height) {
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0, water = 0;

        while (left <= right) {
            if (height[left] <= height[right]) {
                water += Math.max(0, leftMax - height[left]);
                leftMax = Math.max(leftMax, height[left]);
                left++;
            } else {
                water += Math.max(0, rightMax - height[right]);
                rightMax = Math.max(rightMax, height[right]);
                right--;
            }
        }
        return water;
    }
}
```

```cpp
#include <vector>

class TrappingRainWater {
public:
    /**
     * @param height bar heights
     * @return      total water trapped
     */
    int trap(std::vector<int>& height) {
        int left = 0, right = height.size() - 1;
        int leftMax = 0, rightMax = 0, water = 0;

        while (left <= right) {
            if (height[left] <= height[right]) {
                water += std::max(0, leftMax - height[left]);
                leftMax = std::max(leftMax, height[left]);
                left++;
            } else {
                water += std::max(0, rightMax - height[right]);
                rightMax = std::max(rightMax, height[right]);
                right--;
            }
        }
        return water;
    }
};
```

```python
def trap(height: list[int]) -> int:
    """
    @param height: bar heights
    @return:       total water trapped
    """
    left, right = 0, len(height) - 1
    left_max = right_max = 0
    water = 0

    while left <= right:
        if height[left] <= height[right]:
            water += max(0, left_max - height[left])   # decided by leftMax
            left_max = max(left_max, height[left])
            left += 1
        else:
            water += max(0, right_max - height[right]) # decided by rightMax
            right_max = max(right_max, height[right])
            right -= 1
    return water
```

```rust
impl Solution {
    /// @param height bar heights
    /// @return      total water trapped
    pub fn trap(height: Vec<i32>) -> i32 {
        let (mut left, mut right) = (0usize, height.len() - 1);
        let (mut left_max, mut right_max, mut water) = (0, 0, 0);

        while left <= right {
            if height[left] <= height[right] {
                water += (left_max - height[left]).max(0);   // decided by leftMax
                left_max = left_max.max(height[left]);
                left += 1;
            } else {
                water += (right_max - height[right]).max(0); // decided by rightMax
                right_max = right_max.max(height[right]);
                right -= 1;
            }
        }
        water
    }
}
```

## Dry run

**Input:** `height = [0,1,0,2,1,0,1,3,2,1,2,1]`.

```
left=0 right=11 leftMax=0 rightMax=0 water=0
l0: 0<=1 -> water+=max(0,0-0)=0.  leftMax=0.  left=1
l1: 1<=1 -> +=0.  leftMax=1.  left=2
l2: 0<=1 -> +=1-0=1.  water=1.  left=3
l3: 2<=1? NO -> +=max(0,0-1)=0.  rightMax=1.  right=10
l3: 2<=2 -> +=max(0,1-2)=0.  leftMax=2.  left=4
l4: 1<=2 -> +=2-1=1.  water=2.  left=5
l5: 0<=2 -> +=2-0=2.  water=4.  left=6
l6: 1<=2 -> +=2-1=1.  water=5.  left=7
l7: 3<=2? NO -> +=max(0,1-2)=0.  rightMax=2.  right=9
l7: 3<=1? NO -> +=max(0,2-1)=1.  water=6.  right=8
l7: 3<=2? NO -> +=max(0,2-2)=0.  right=7
l7: 3<=3 -> +=max(0,3-3)=0.  left=8.  loop ends (8 > 7).

Output: 6 ✓
```

The "commit the decided side" rhythm: every `else` branch resolved the *right* cell against `rightMax` — the left side was taller, so the right side's water is already final. Each cell is processed exactly once, and the running maxima replace the two precomputed arrays entirely.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Four variables:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Container With Most Water** ([3.9](container-with-most-water.md)) — the mirror problem: instead of *summing* per-cell water, *maximize* the min-height × width; the two-pointer rule is the same geometry.
- **Trapping Rain Water II** ([7.4](../ch07-heaps/trapping-rain-water-ii.md)) — the 2-D version: a min-heap boundary expansion replaces the left/right scan.
- **The precompute version** (`trap` in the repo file) — `leftMax`/`rightMax` arrays then sum: the O(n)-space form that's easier to prove and the natural first answer.
- **Interview follow-up:** "Why does the shorter side's water get decided without knowing the far side's full profile?" The `height[left] <= height[right]` test guarantees a wall at least `height[right]` tall somewhere to the right — so `rightMax` can only be ≥ `height[right]`, and the limiting factor for `left` is purely `leftMax`. The far side's exact profile is irrelevant; the inequality is all the information needed.
