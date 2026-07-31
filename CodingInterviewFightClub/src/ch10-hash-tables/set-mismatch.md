# 10.25 Set Mismatch

> **Source**: [`src/main/kotlin/array/hashtable/SetMismatch.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/SetMismatch.kt)
> **Pattern**: sum-arithmetic detection · **Core page**

## The Problem

An array 1..n with one duplicated (replacing one missing). Return `[duplicate, missing]`.

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  nums = [1,2,2,4]   -> Output: [2,3]
```

## Intuition — find the duplicate by set; the missing falls out of sums

`expectedSum = n(n+1)/2`; `actualSum` includes the duplicate in place of the missing:

```kotlin
val seen = mutableSetOf<Int>()
var duplicate = -1
var actualSum = 0

for (num in nums) {
    if (!seen.add(num)) duplicate = num
    actualSum += num
}

val expectedSum = n * (n + 1) / 2
val missing = expectedSum - (actualSum - duplicate)

return intArrayOf(duplicate, missing)
```

**Why the arithmetic?** `actualSum - duplicate` = the sum of the *distinct* elements = `expectedSum - missing`. One subtraction recovers the missing value — the [3.18](../ch03-arrays/remove-duplicates-from-sorted-array.md) set + the [10.8](subarray-sum-equals-k.md) sum identity.

## Approach 1 — Count array (O(n) space)

Frequency table, scan for 2 and 0: the straightforward version.

## Approach 2 — Set + sum arithmetic (the repo's version, optimal)

```kotlin
class SetMismatch {
    /**
     * @param nums array with one duplicate
     * @return     [duplicate, missing]
     */
    fun findErrorNums(nums: IntArray): IntArray {
        val seen = mutableSetOf<Int>()
        var duplicate = -1
        var actualSum = 0
        val n = nums.size

        for (num in nums) {
            if (!seen.add(num)) {
                duplicate = num
            }
            actualSum += num
        }

        val expectedSum = n * (n + 1) / 2
        val missing = expectedSum - (actualSum - duplicate)

        return intArrayOf(duplicate, missing)
    }
}
```

```java
public class SetMismatch {
    /**
     * @param nums array with one duplicate
     * @return     [duplicate, missing]
     */
    public int[] findErrorNums(int[] nums) {
        int n = nums.length;
        boolean[] seen = new boolean[n + 1];
        int duplicate = 0;
        long actual = 0;

        for (int num : nums) {
            if (seen[num]) duplicate = num;
            seen[num] = true;
            actual += num;
        }

        long expected = (long) n * (n + 1) / 2;
        int missing = (int) (expected - (actual - duplicate));
        return new int[]{duplicate, missing};
    }
}
```

```cpp
#include <vector>

class SetMismatch {
public:
    /**
     * @param nums array with one duplicate
     * @return     [duplicate, missing]
     */
    std::vector<int> findErrorNums(std::vector<int>& nums) {
        int n = nums.size();
        std::vector<bool> seen(n + 1, false);
        int duplicate = 0;
        long long actual = 0;

        for (int num : nums) {
            if (seen[num]) duplicate = num;
            seen[num] = true;
            actual += num;
        }

        long long expected = (long long)n * (n + 1) / 2;
        return {duplicate, (int)(expected - (actual - duplicate))};
    }
};
```

```python
def find_error_nums(nums: list[int]) -> list[int]:
    """
    @param nums: array with one duplicate
    @return:     [duplicate, missing]
    """
    seen = set()
    duplicate = -1
    actual_sum = 0

    for num in nums:
        if num in seen:
            duplicate = num
        seen.add(num)
        actual_sum += num

    expected = len(nums) * (len(nums) + 1) // 2
    missing = expected - (actual_sum - duplicate)

    return [duplicate, missing]
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param nums array with one duplicate
    /// @return     [duplicate, missing]
    pub fn find_error_nums(nums: Vec<i32>) -> Vec<i32> {
        let n = nums.len() as i64;
        let mut seen: HashSet<i32> = HashSet::new();
        let mut duplicate = -1;
        let mut actual_sum: i64 = 0;

        for &num in &nums {
            if !seen.insert(num) { duplicate = num; }
            actual_sum += num as i64;
        }

        let expected = n * (n + 1) / 2;
        let missing = expected - (actual_sum - duplicate as i64);
        vec![duplicate, missing as i32]
    }
}
```

## Dry run

**Input:** `nums = [1,2,2,4]`.

```
seen: 1, 2 (dup at the second 2), 4.  actualSum = 9.
expected = 10.  missing = 10 - (9 - 2) = 3.
Output: [2,3] ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The set:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Find The Duplicate Number** ([4.5](../ch04-linked-lists/find-the-duplicate-number.md)) — the cycle version.
- **First Missing Positive** ([10.10](first-missing-positive.md)) — the index-marking family.
- **Interview follow-up:** "Why does `expected - (actual - duplicate)` recover the missing?" The actual sum contains the duplicate once too many and the missing zero times — subtracting the duplicate restores the *distinct* sum, whose deficit from expected is exactly the missing value.
