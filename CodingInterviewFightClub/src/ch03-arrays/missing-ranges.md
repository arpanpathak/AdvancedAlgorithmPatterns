# 3.29 Missing Ranges

> **Source**: [`src/main/kotlin/array/MissingRanges.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/MissingRanges.kt)
> **Pattern**: gap scanning · **Core page**

## The Problem

The ranges missing from `nums` within `[lower, upper]`.

- Constraints: n ≤ 1000; sorted nums.

## Examples

```
Input:  nums = [0,1,3,50,75], lower = 0, upper = 99
Output: [[2,2],[4,49],[51,74],[76,99]]
```

## Intuition — a pointer walks the expected range; each num jumps it

`currentRangePointer` starts at `lower`; each `num` closes the gap `[pointer, num-1]` if non-empty, then the pointer jumps to `num + 1`:

```kotlin
var currentRangePointer = lower

for (num in nums) {
    if (num > currentRangePointer)
        result.add(listOf(currentRangePointer, num - 1))
    currentRangePointer = num + 1
}

if (currentRangePointer <= upper) {
    result.add(listOf(currentRangePointer, upper))
}
```

## Approach 1 — Gap scan (the repo's version, optimal)

```kotlin
class MissingRanges {
    /**
     * @param nums  sorted numbers
     * @param lower lower bound
     * @param upper upper bound
     * @return      missing ranges
     */
    fun findMissingRanges(nums: IntArray, lower: Int, upper: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        var currentRangePointer = lower

        for (num in nums) {
            if (num > currentRangePointer)
                result.add(listOf(currentRangePointer, num - 1))
            currentRangePointer = num + 1
        }

        if (currentRangePointer <= upper) {
            result.add(listOf(currentRangePointer, upper))
        }
        return result
    }
}
```

```java
import java.util.*;

public class MissingRanges {
    /**
     * @param nums  sorted numbers
     * @param lower lower bound
     * @param upper upper bound
     * @return      missing ranges
     */
    public List<List<Integer>> findMissingRanges(int[] nums, int lower, int upper) {
        List<List<Integer>> result = new ArrayList<>();
        long next = lower;

        for (int num : nums) {
            if (num > next) result.add(Arrays.asList((int) next, num - 1));
            next = (long) num + 1;
        }

        if (next <= upper) result.add(Arrays.asList((int) next, upper));
        return result;
    }
}
```

```cpp
#include <vector>

class MissingRanges {
public:
    /**
     * @param nums  sorted numbers
     * @param lower lower bound
     * @param upper upper bound
     * @return      missing ranges
     */
    std::vector<std::vector<int>> findMissingRanges(std::vector<int>& nums, int lower, int upper) {
        std::vector<std::vector<int>> result;
        long next = lower;

        for (int num : nums) {
            if (num > next) result.push_back({(int)next, num - 1});
            next = (long)num + 1;
        }

        if (next <= upper) result.push_back({(int)next, upper});
        return result;
    }
};
```

```python
def find_missing_ranges(nums: list[int], lower: int, upper: int) -> list[list[int]]:
    """
    @param nums:  sorted numbers
    @param lower: lower bound
    @param upper: upper bound
    @return:      missing ranges
    """
    result = []
    pointer = lower

    for num in nums:
        if num > pointer:
            result.append([pointer, num - 1])
        pointer = num + 1

    if pointer <= upper:
        result.append([pointer, upper])

    return result
```

```rust
impl Solution {
    /// @param nums  sorted numbers
    /// @param lower lower bound
    /// @param upper upper bound
    /// @return      missing ranges
    pub fn find_missing_ranges(nums: Vec<i32>, lower: i32, upper: i32) -> Vec<Vec<i32>> {
        let mut result = Vec::new();
        let mut pointer = lower as i64;

        for &num in &nums {
            if num as i64 > pointer {
                result.push(vec![pointer as i32, num - 1]);
            }
            pointer = num as i64 + 1;
        }

        if pointer <= upper as i64 {
            result.push(vec![pointer as i32, upper]);
        }
        result
    }
}
```

## Dry run

**Input:** `nums = [0,1,3,50,75], lower = 0, upper = 99`.

```
pointer=0.  0: num == pointer -> no gap.  pointer=1.
1: no gap.  pointer=2.
3: gap [2,2].  pointer=4.
50: gap [4,49].  pointer=51.
75: gap [51,74].  pointer=76.
end: 76 <= 99 -> [76,99].

Output: [[2,2],[4,49],[51,74],[76,99]] ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The result:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Summary Ranges** — the inverse (compress present ranges).
- **Interview follow-up:** "Why `num + 1` as the next pointer?" A present num occupies itself — the next possible missing value is `num + 1`. The pointer is the "next expected value", and each gap is `[pointer, num-1]`.
