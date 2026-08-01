# 3.38 Three Sum Closest

> **Source**: [`src/main/kotlin/array/twopointer/ThreeSumClosest.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/ThreeSumClosest.kt)
> **Pattern**: two-pointer closest · **Core page**

## The Problem

The 3-sum **closest** to `target`.

- Constraints: n ≤ 500.

## Examples

```
Input:  nums = [-1,2,1,-4], target = 1   -> Output: 2  (-1+2+1)
```

## Intuition — the [3.5](three-sum.md) two-pointer, tracking the closest

```kotlin
nums.sort()
var closestSum = nums[0] + nums[1] + nums[2]

for (i in 0 until nums.size - 2) {
    var left = i + 1
    var right = nums.size - 1

    while (left < right) {
        val sum = nums[i] + nums[left] + nums[right]
        if (abs(sum - target) < abs(closestSum - target)) closestSum = sum

        when {
            sum < target -> left++
            sum > target -> right--
            else -> return sum
        }
    }
}
return closestSum
```

## Approach 1 — Two-pointer closest (the repo's version, optimal)

```kotlin
class ThreeSumClosest {
    /**
     * @param nums   input array
     * @param target target sum
     * @return       closest 3-sum
     */
    fun threeSumClosest(nums: IntArray, target: Int): Int {
        nums.sort()
        var closestSum = nums[0] + nums[1] + nums[2]

        for (i in 0 until nums.size - 2) {
            var left = i + 1
            var right = nums.size - 1

            while (left < right) {
                val sum = nums[i] + nums[left] + nums[right]
                if (abs(sum - target) < abs(closestSum - target)) closestSum = sum

                when {
                    sum < target -> left++
                    sum > target -> right--
                    else -> return sum
                }
            }
        }
        return closestSum
    }
}
```

```java
public class ThreeSumClosest {
    /**
     * @param nums   input array
     * @param target target sum
     * @return       closest 3-sum
     */
    public int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int closest = nums[0] + nums[1] + nums[2];

        for (int i = 0; i < nums.length - 2; i++) {
            int left = i + 1, right = nums.length - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (Math.abs(sum - target) < Math.abs(closest - target)) closest = sum;

                if (sum < target) left++;
                else if (sum > target) right--;
                else return sum;
            }
        }
        return closest;
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <cstdlib>

class ThreeSumClosest {
public:
    /**
     * @param nums   input array
     * @param target target sum
     * @return       closest 3-sum
     */
    int threeSumClosest(std::vector<int>& nums, int target) {
        std::sort(nums.begin(), nums.end());
        int closest = nums[0] + nums[1] + nums[2];

        for (int i = 0; i < (int)nums.size() - 2; i++) {
            int left = i + 1, right = nums.size() - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (std::abs(sum - target) < std::abs(closest - target)) closest = sum;

                if (sum < target) left++;
                else if (sum > target) right--;
                else return sum;
            }
        }
        return closest;
    }
};
```

```python
def three_sum_closest(nums: list[int], target: int) -> int:
    """
    @param nums:   input array
    @param target: target sum
    @return:       closest 3-sum
    """
    nums.sort()
    closest = sum(nums[:3])

    for i in range(len(nums) - 2):
        left, right = i + 1, len(nums) - 1

        while left < right:
            total = nums[i] + nums[left] + nums[right]
            if abs(total - target) < abs(closest - target):
                closest = total

            if total < target:
                left += 1
            elif total > target:
                right -= 1
            else:
                return total

    return closest
```

```rust
impl Solution {
    /// @param nums   input array
    /// @param target target sum
    /// @return       closest 3-sum
    pub fn three_sum_closest(mut nums: Vec<i32>, target: i32) -> i32 {
        nums.sort_unstable();
        let mut closest = nums[0] + nums[1] + nums[2];

        for i in 0..nums.len() - 2 {
            let (mut left, mut right) = (i + 1, nums.len() - 1);

            while left < right {
                let sum = nums[i] + nums[left] + nums[right];
                if (sum - target).abs() < (closest - target).abs() { closest = sum; }

                if sum < target { left += 1; }
                else if sum > target { right -= 1; }
                else { return sum; }
            }
        }
        closest
    }
}
```

## Dry run

**Input:** `nums = [-1,2,1,-4]` (sorted [-4,-1,1,2]), `target = 1`.

```
closest = -4-1+1 = -4.
i=0 (-4): l=1(-1), r=3(2): sum -3.  |−4| > |−3|? closer -> closest=-3.  <1 -> l=2(1): sum -1 -> closest=-1.  <1 -> l=3? done.
i=1 (-1): l=2(1), r=3(2): sum 2.  |2-1|=1 < |-1-1|=2 -> closest=2.  >1 -> r=2 done.
i=2 (1): done.
Output: 2 ✓
```

## Complexity

**Time.** O(n²):

$$
T(n) = O(n^2)
$$

**Space.** In place:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Three Sum** ([3.5](three-sum.md)) — exact vs closest.
- **Interview follow-up:** "Why does the two-pointer still work for 'closest'?" The sorted walk visits sums monotonically near the target — each pointer move is a strictly better-or-equal approximation direction.
