# 2.40 Subarray Product Less Than K

> **Source**: [`src/main/kotlin/array/prefixsum/SubArrayProductLessThanK.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/SubArrayProductLessThanK.kt)
> **Pattern**: sliding product window · **Core page**

## The Problem

Count subarrays whose product is `< k`.

- Constraints: n ≤ 3×10⁴; k ≤ 10⁶.

## Examples

```
Input:  nums = [10,5,2,6], k = 100   -> Output: 8
```

## Intuition — the sliding window: each right adds `right - left + 1` subarrays

Products grow — a window ending at `right` contributes all its suffixes:

```kotlin
if (k <= 1) return 0

var (count, product, left) = listOf(0, 1, 0)

for (right in nums.indices) {
    product *= nums[right]

    while (product >= k) {
        product /= nums[left]
        left++
    }

    count += right - left + 1
}
return count
```

**Why the early `k <= 1`?** With k ≤ 1 no positive product is < k — the while-loop would never terminate productively. The [15.x](../ch15-sliding-window/pattern-primer.md) variable window with a product payload.

## Approach 1 — Sliding product window (the repo's version, optimal)

```kotlin
class SubArrayProductLessThanK {
    /**
     * @param nums input array
     * @param k    bound (exclusive)
     * @return     count of subarrays with product < k
     */
    fun numSubarrayProductLessThanK(nums: IntArray, k: Int): Int {
        if (k <= 1) return 0

        var (count, product, left) = listOf(0, 1, 0)

        for (right in nums.indices) {
            product *= nums[right]

            while (product >= k) {
                product /= nums[left]
                left++
            }

            count += right - left + 1
        }
        return count
    }
}
```

```java
public class SubarrayProductLessThanK {
    /**
     * @param nums input array
     * @param k    bound (exclusive)
     * @return     count of subarrays with product < k
     */
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) return 0;

        int count = 0, product = 1, left = 0;

        for (int right = 0; right < nums.length; right++) {
            product *= nums[right];

            while (product >= k) product /= nums[left++];

            count += right - left + 1;
        }
        return count;
    }
}
```

```cpp
#include <vector>

class SubarrayProductLessThanK {
public:
    /**
     * @param nums input array
     * @param k    bound (exclusive)
     * @return     count of subarrays with product < k
     */
    int numSubarrayProductLessThanK(std::vector<int>& nums, int k) {
        if (k <= 1) return 0;

        int count = 0, product = 1, left = 0;

        for (int right = 0; right < (int)nums.size(); right++) {
            product *= nums[right];

            while (product >= k) product /= nums[left++];

            count += right - left + 1;
        }
        return count;
    }
};
```

```python
def num_subarray_product_less_than_k(nums: list[int], k: int) -> int:
    """
    @param nums: input array
    @param k:    bound (exclusive)
    @return:     count of subarrays with product < k
    """
    if k <= 1:
        return 0

    count = product = left = 0

    for right, num in enumerate(nums):
        product *= num

        while product >= k:
            product //= nums[left]
            left += 1

        count += right - left + 1

    return count
```

```rust
impl Solution {
    /// @param nums input array
    /// @param k    bound (exclusive)
    /// @return     count of subarrays with product < k
    pub fn num_subarray_product_less_than_k(nums: Vec<i32>, k: i32) -> i32 {
        if k <= 1 { return 0; }

        let (mut count, mut product, mut left) = (0, 1, 0);

        for right in 0..nums.len() {
            product *= nums[right];

            while product >= k {
                product /= nums[left];
                left += 1;
            }

            count += right - left + 1;
        }
        count as i32
    }
}
```

## Dry run

**Input:** `nums = [10,5,2,6], k = 100`.

```
r=0 (10): p=10 < 100.  count += 1 (1).
r=1 (5): p=50.  count += 2 (3): [5],[10,5].
r=2 (2): p=100 >= 100 -> p/=10, left=1: p=10.  count += 2 (5): [2],[5,2].
r=3 (6): p=60.  count += 3 (8): [6],[2,6],[5,2,6].
Output: 8 ✓
```

## Complexity

**Time.** Amortized O(n):

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does each right add `right - left + 1`?" Every subarray ending at `right` with product < k starts at some index ≥ left — exactly `right - left + 1` valid starts.
