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

## Reading the code — what's actually happening

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

Think of `currentZeroCount` as **the length of the current zero-run**, and notice what happens when a run grows from `L` to `L + 1` zeros: the *new* subarrays that end at this newest zero are exactly `L + 1` — the new zero by itself, plus the `L` suffixes that extend the previous run. So adding the run length to `count` at every step telescopes into the formula `L(L+1)/2` per run without ever computing it directly.

- **`currentZeroCount++` grows the run.** Each consecutive zero extends the current run of zeros.
- **`count += currentZeroCount` banks the new subarrays.** When the run length is `L`, the subarrays ending *here* are: `[0]`, `[0,0]`, …, the whole run — exactly `L` of them. Adding `L` per step accumulates `1 + 2 + … + L = L(L+1)/2` for the completed run. That's why a run of 3 contributes 6 subarrays.
- **`currentZeroCount = 0` resets at the first non-zero.** A non-zero breaks the run — subarrays can't cross it, so the counter restarts from scratch for the next zero block.
- **Why `count` is `Long`:** a run of length $10^5$ contributes ~$5 \times 10^9$ subarrays, which overflows `Int` — the widening is mandatory, not defensive.

Trace `[1,3,0,0,2,0,0,0]`: run of 2 at indices 2–3 → adds 1 then 2 → 3 subarrays; run of 3 at indices 5–7 → adds 1, 2, 3 → 6 subarrays. Total **9**, not 6 — the two runs are independent because the `2` in between resets the counter.

## Dry run

**Input:** `nums = [1,3,0,0,2,0,0,0]`.

```
run of 2 zeros -> 1 + 2 = 3 subarrays ([0]x2 positions, [0,0])
run of 3 zeros -> 1 + 2 + 3 = 6 subarrays
Total = 3 + 6 = 9 ✓   (for [0,0,0] alone the answer is 6)
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
