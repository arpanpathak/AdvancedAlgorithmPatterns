# 3.15 Find Pivot Index

> **Source:** [`src/main/kotlin/array/prefixsum/FindPivotIndex.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/FindPivotIndex.kt)
> **Pattern:** running prefix vs total · **Core page**

## The Problem

Return the **leftmost index** where the sum left of it equals the sum right of it, or `-1`.

- Constraints: $1 \le n \le 10^4$; values fit in `Int`.

## Examples

```
Input:  nums = [1,7,3,6,5,6]   -> Output: 3   (left 1+7+3 = 11; right 5+6 = 11)
Input:  nums = [1,2,3]         -> Output: -1
```

## Intuition — at index i, the right sum is `total - prefix - nums[i]`

One pass with a running prefix:

```
total = sum(nums); prefix = 0
for i in indices:
    if prefix == total - prefix - nums[i]: return i    # left sum == right sum
    prefix += nums[i]
return -1
```

**Why this single equation?** The left sum at `i` is `prefix`; the right sum is everything else: `total - prefix - nums[i]` (subtract the left part *and* the pivot itself). Equality is the pivot test — no second array, no two-pointer.

**Why is the leftmost found automatically?** The scan is left-to-right; the first `i` satisfying the equation is returned immediately.

## Approach 1 — Prefix/suffix arrays (O(n) space)

Precompute left and right sums, compare: correct, wasteful.

## Approach 2 — Running prefix vs total (the repo's version, optimal)

```kotlin
class FindPivotIndex {
    /**
     * @param nums input array
     * @return     leftmost pivot index, or -1
     */
    fun pivotIndex(nums: IntArray): Int {
        val totalSum = nums.sum()
        var prefixSum = 0

        for (i in nums.indices) {
            if (prefixSum == totalSum - prefixSum - nums[i]) return i
            prefixSum += nums[i]
        }
        return -1
    }
}
```

```java
public class FindPivotIndex {
    /**
     * @param nums input array
     * @return     leftmost pivot index, or -1
     */
    public int pivotIndex(int[] nums) {
        int total = 0;
        for (int v : nums) total += v;

        int prefix = 0;
        for (int i = 0; i < nums.length; i++) {
            if (prefix == total - prefix - nums[i]) return i;
            prefix += nums[i];
        }
        return -1;
    }
}
```

```cpp
#include <numeric>
#include <vector>

class FindPivotIndex {
public:
    /**
     * @param nums input array
     * @return     leftmost pivot index, or -1
     */
    int pivotIndex(std::vector<int>& nums) {
        int total = std::accumulate(nums.begin(), nums.end(), 0);

        int prefix = 0;
        for (int i = 0; i < (int)nums.size(); i++) {
            if (prefix == total - prefix - nums[i]) return i;
            prefix += nums[i];
        }
        return -1;
    }
};
```

```python
def pivot_index(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     leftmost pivot index, or -1
    """
    total = sum(nums)
    prefix = 0

    for i, num in enumerate(nums):
        if prefix == total - prefix - num:
            return i
        prefix += num
    return -1
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     leftmost pivot index, or -1
    pub fn pivot_index(nums: Vec<i32>) -> i32 {
        let total: i32 = nums.iter().sum();
        let mut prefix = 0;

        for (i, &num) in nums.iter().enumerate() {
            if prefix == total - prefix - num { return i as i32; }
            prefix += num;
        }
        -1
    }
}
```

## Dry run

**Input:** `nums = [1,7,3,6,5,6]`.

```
total = 28, prefix = 0
i=0 (1):  0 == 28 - 0 - 1 = 27? no.  prefix = 1
i=1 (7):  1 == 28 - 1 - 7 = 20? no.  prefix = 8
i=2 (3):  8 == 28 - 8 - 3 = 17? no.  prefix = 11
i=3 (6):  11 == 28 - 11 - 6 = 11? YES -> return 3 ✓
```

The single equation does everything: at i=3, the left sum (1+7+3 = 11) equals the right sum (5+6 = 11) — the pivot itself (6) is excluded by the subtraction. `nums = [1,2,3]`: total 6; i=0: 0 vs 5; i=1: 1 vs 3; i=2: 3 vs 0 — never equal → -1.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Two scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Find The Highest Altitude** (`array/prefixsum/FIndTheHighestAltitute.kt`) — the prefix-sum family's running-max sibling.
- **Contiguous Array / Subarray Sum Equals K** ([10.8](../ch10-hash-tables/subarray-sum-equals-k.md)) — the prefix-sum family's map-based members.
- **Interview follow-up:** "Why no need for the right-sum array?" The right sum is derived: `total - prefix - nums[i]`. One formula replaces a whole second pass — the "total minus what I've seen" idiom at the heart of every prefix-sum problem.
