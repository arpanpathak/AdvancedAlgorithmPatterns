# 10.10 First Missing Positive

> **Source:** [`src/main/kotlin/array/hashtable/FirstMissingPositive.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/FirstMissingPositive.kt) (+ `array/hashtable/FindMissingPositive.kt`)
> **Pattern:** index-as-memo marking · **Core page**

## The Problem

Given `nums`, return the **smallest positive integer missing** from it — in **O(n) time and O(1) space** (no sets, no sorts).

- Constraints: $1 \le n \le 5 \times 10^5$; values fit in `Int`.

## Examples

```
Input:  nums = [1,2,0]       -> Output: 3
Input:  nums = [3,4,-1,1]    -> Output: 2
Input:  nums = [7,8,9,11,12] -> Output: 1
```

## Intuition — the answer is in [1, n+1], and the *array itself* can be the presence set

The smallest missing positive is at most `n + 1` (if all of 1..n are present). So "is value `v` present?" can be encoded in `nums` itself: **make `nums[v - 1]` negative** as the mark. Three passes:

1. **Sanitize:** replace non-positive values with `n + 1` (a harmless placeholder — they can't be answers).
2. **Mark:** for each `v = |nums[i]|` in `1..n`, set `nums[v - 1] = -|nums[v - 1]|` — one mark per value, negatives ignored (already visited).
3. **Scan:** the first `i` with `nums[i] > 0` means value `i + 1` was never marked → answer. If none, `n + 1`.

**Why the sign as a mark?** Values are already stored in the array — the *sign bit* is free real estate. `nums[v-1] < 0 ⟺ v was seen`. The `abs()` at read time handles double-marks (the same `v` appearing twice). This is the [3.12](../ch03-arrays/set-matrix-zeroes.md) marker-lane idea applied to a 1-D array.

**Why sanitize first?** A negative `nums[i]` would otherwise be ambiguous — is it a mark or data? Replacing all non-positives with `n+1` (which can never be a valid mark index) keeps negatives exclusively for marking.

**Why `abs` before checking the range?** Marking makes some cells negative; reading `nums[i]` raw would misread marks as data. `|nums[i]|` is the *original* value, and only values in `1..n` map to valid indices.

## Approach 1 — Hash set (O(n) space)

Insert everything, then scan 1..n for the first miss: correct, but violates the O(1)-space constraint.

## Approach 2 — Index-marking in place (the repo's version, optimal)

```kotlin
class FirstMissingPositive {
    /**
     * @param nums input array
     * @return     smallest positive integer missing from nums
     */
    fun firstMissingPositive(nums: IntArray): Int {
        val n = nums.size

        // Step 1: replace negatives and zeros with a placeholder > n
        for (i in nums.indices) {
            if (nums[i] <= 0) nums[i] = n + 1
        }

        // Step 2: mark presence by making nums[value - 1] negative
        for (i in nums.indices) {
            val num = kotlin.math.abs(nums[i])
            if (num in 1..n) {
                val idx = num - 1
                if (nums[idx] > 0) {
                    nums[idx] = -nums[idx]
                }
            }
        }

        // Step 3: first positive cell means its value was never seen
        for (i in nums.indices) {
            if (nums[i] > 0) return i + 1
        }
        return n + 1
    }
}
```

```java
public class FirstMissingPositive {
    /**
     * @param nums input array
     * @return     smallest positive integer missing from nums
     */
    public int firstMissingPositive(int[] nums) {
        int n = nums.length;

        for (int i = 0; i < n; i++)                        // sanitize non-positives
            if (nums[i] <= 0) nums[i] = n + 1;

        for (int i = 0; i < n; i++) {                      // mark presence via the sign
            int v = Math.abs(nums[i]);
            if (v >= 1 && v <= n && nums[v - 1] > 0) {
                nums[v - 1] = -nums[v - 1];
            }
        }

        for (int i = 0; i < n; i++)                        // first unmarked cell
            if (nums[i] > 0) return i + 1;
        return n + 1;
    }
}
```

```cpp
#include <vector>
#include <cstdlib>

class FirstMissingPositive {
public:
    /**
     * @param nums input array
     * @return     smallest positive integer missing from nums
     */
    int firstMissingPositive(std::vector<int>& nums) {
        int n = nums.size();

        for (int i = 0; i < n; i++)                        // sanitize non-positives
            if (nums[i] <= 0) nums[i] = n + 1;

        for (int i = 0; i < n; i++) {                      // mark presence via the sign
            int v = std::abs(nums[i]);
            if (v >= 1 && v <= n && nums[v - 1] > 0) {
                nums[v - 1] = -nums[v - 1];
            }
        }

        for (int i = 0; i < n; i++)                        // first unmarked cell
            if (nums[i] > 0) return i + 1;
        return n + 1;
    }
};
```

```python
def first_missing_positive(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     smallest positive integer missing from nums
    """
    n = len(nums)

    for i in range(n):                       # sanitize non-positives
        if nums[i] <= 0:
            nums[i] = n + 1

    for i in range(n):                       # mark presence via the sign
        v = abs(nums[i])
        if 1 <= v <= n and nums[v - 1] > 0:
            nums[v - 1] = -nums[v - 1]

    for i in range(n):                       # first unmarked cell
        if nums[i] > 0:
            return i + 1
    return n + 1
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     smallest positive integer missing from nums
    pub fn first_missing_positive(nums: &mut Vec<i32>) -> i32 {
        let n = nums.len();

        for v in nums.iter_mut() {                       // sanitize non-positives
            if *v <= 0 { *v = n as i32 + 1; }
        }

        for i in 0..n {                                  // mark presence via the sign
            let v = nums[i].abs() as usize;
            if v >= 1 && v <= n && nums[v - 1] > 0 {
                nums[v - 1] = -nums[v - 1];
            }
        }

        for i in 0..n {                                  // first unmarked cell
            if nums[i] > 0 { return i as i32 + 1; }
        }
        n as i32 + 1
    }
}
```

## Dry run

**Input:** `nums = [3,4,-1,1]`.

```
n = 4
sanitize: [3, 4, 5, 1]      (-1 -> n+1 = 5)

mark:
i=0: v=|3|=3 -> idx 2: nums[2]=5 > 0 -> nums[2] = -5.  nums = [3,4,-5,1]
i=1: v=|4|=4 -> idx 3: nums[3]=1 > 0 -> nums[3] = -1.  nums = [3,4,-5,-1]
i=2: v=|-5|=5 -> not in 1..4 -> skip.
i=3: v=|-1|=1 -> idx 0: nums[0]=3 > 0 -> nums[0] = -3.  nums = [-3,4,-5,-1]

scan: i=0: -3 < 0 marked (1 present).  i=1: 4 > 0 UNMARKED -> answer 2 ✓
```

The sign bits decode cleanly: `1` marked at index 0, `3` at index 2, `4` at index 3 — index 1 was never marked because value 2 was never seen. The sanitize step's `5` lives outside `1..n`, so it can never be misread as a mark target.

## Complexity

**Time.** Three passes:

$$
T(n) = O(n)
$$

**Space.** In place:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Find All Numbers Disappeared In An Array** (`array/hashtable/`) — the same sign-marking collecting *all* missing values.
- **Missing Ranges / Set Mismatch** (`array/MissingRanges.kt`, `array/hashtable/SetMismatch.kt`) — more "missing value" detection, each with its own trick.
- **Interview follow-up:** "Why is the answer guaranteed to be ≤ n+1?" Among 1..n+1, only n values can be present — pigeonhole: at least one of 1..n+1 is missing. And every candidate outside 1..n is irrelevant (it's never *the* first missing positive).
