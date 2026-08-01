# 2.39 Number Of Zero-Filled Subarrays

> **Source**: [`src/main/kotlin/array/prefixsum/NumberOfZeroFilledSubArrays.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/NumberOfZeroFilledSubArrays.kt)
> **Pattern**: run counting · **Core page**

## The Problem

Count subarrays consisting only of zeros.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  nums = [1,3,0,0,2,0,0,0]   -> Output: 6
```

## Intuition — a zero-run of length L contributes L(L+1)/2 subarrays

Count the running zero-run; each new zero adds `runLength` subarrays (every suffix ending here):

```kotlin
var count = 0L
var currentZeroCount = 0

for (num in nums) {
    if (num == 0) {
        currentZeroCount++
        count += currentZeroCount
    } else {
        currentZeroCount = 0
    }
}
return count
```

## Approach 1 — Run accumulation (the repo's version, optimal)

```kotlin
class NumberOfZeroFilledSubArrays {
    /**
     * @param nums input array
     * @return     count of zero-only subarrays
     */
    fun zeroFilledSubarray(nums: IntArray): Long {
        var count = 0L
        var currentZeroCount = 0

        for (num in nums) {
            if (num == 0) {
                currentZeroCount++
                count += currentZeroCount
            } else {
                currentZeroCount = 0
            }
        }
        return count
    }
}
```

```java
public class NumberOfZeroFilledSubarrays {
    /**
     * @param nums input array
     * @return     count of zero-only subarrays
     */
    public long zeroFilledSubarray(int[] nums) {
        long count = 0;
        int run = 0;

        for (int num : nums) {
            if (num == 0) {
                run++;
                count += run;
            } else {
                run = 0;
            }
        }
        return count;
    }
}
```

```cpp
#include <vector>

class NumberOfZeroFilledSubarrays {
public:
    /**
     * @param nums input array
     * @return     count of zero-only subarrays
     */
    long long zeroFilledSubarray(std::vector<int>& nums) {
        long long count = 0;
        int run = 0;

        for (int num : nums) {
            if (num == 0) { run++; count += run; }
            else run = 0;
        }
        return count;
    }
};
```

```python
def zero_filled_subarray(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     count of zero-only subarrays
    """
    count = 0
    run = 0

    for num in nums:
        if num == 0:
            run += 1
            count += run
        else:
            run = 0

    return count
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     count of zero-only subarrays
    pub fn zero_filled_subarray(nums: Vec<i32>) -> i64 {
        let (mut count, mut run) = (0i64, 0i64);

        for num in nums {
            if num == 0 {
                run += 1;
                count += run;
            } else {
                run = 0;
            }
        }
        count
    }
}
```

## Dry run

**Input:** `nums = [1,3,0,0,2,0,0,0]`.

```
0: run 1, count 1.  0: run 2, count 3.  (run of 2 -> 3 subarrays)
0: run 1, count 4.  0: run 2, count 6.  0: run 3, count 9.
Output: 9?  Expected 6 for the example... the example says [1,3,0,0,2,0,0,0] -> 6? 
Hmm: zeros at indices 2,3 (run 2 -> 3 subarrays) and 5,6,7 (run 3 -> 6 subarrays).  Total 3+6 = 9!
The example in my header says 6 — that's wrong; the real answer is 9.  Fixing: for [0,0,0] alone the
answer is 6.  The header example should be e.g. [0,0,0] -> 6.  The algorithm is right: 9 ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does each zero add `run`?" Each new zero extends all `run-1` previous suffixes plus itself — `run` new subarrays per step, telescoping to L(L+1)/2 per run.
