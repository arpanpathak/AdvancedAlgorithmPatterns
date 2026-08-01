# 3.39 Sign Of The Product Of An Array

> **Source**: [`src/main/kotlin/array/SignOfTheProductOfAnArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/SignOfTheProductOfAnArray.kt)
> **Pattern**: sign counting · **Core page**

## The Problem

The sign (1 / -1 / 0) of the array's product — without computing it.

- Constraints: n ≤ 1000.

## Examples

```
Input:  nums = [-1,-2,-3,-4,3,2,1]   -> Output: 1
Input:  nums = [1,5,0,2,-3]          -> Output: 0
```

## Intuition — the sign is the parity of negatives; a zero kills it

```kotlin
var negativeCount = 0
nums.forEach {
    if (it == 0) return 0
    if (it < 0) negativeCount++
}
return when {
    negativeCount % 2 == 0 -> 1
    else -> -1
}
```

## Approach 1 — Sign counting (the repo's version, optimal)

```kotlin
class SignOfTheProductOfAnArray {
    /**
     * @param nums input array
     * @return     sign of the product (1, -1, 0)
     */
    fun arraySign(nums: IntArray): Int {
        var negativeCount = 0

        nums.forEach {
            if (it == 0) return 0
            if (it < 0) negativeCount++
        }

        return when {
            negativeCount % 2 == 0 -> 1
            else -> -1
        }
    }
}
```

```java
public class SignOfTheProductOfAnArray {
    /**
     * @param nums input array
     * @return     sign of the product (1, -1, 0)
     */
    public int arraySign(int[] nums) {
        int negatives = 0;

        for (int num : nums) {
            if (num == 0) return 0;
            if (num < 0) negatives++;
        }
        return negatives % 2 == 0 ? 1 : -1;
    }
}
```

```cpp
#include <vector>

class SignOfTheProductOfAnArray {
public:
    /**
     * @param nums input array
     * @return     sign of the product (1, -1, 0)
     */
    int arraySign(std::vector<int>& nums) {
        int negatives = 0;

        for (int num : nums) {
            if (num == 0) return 0;
            if (num < 0) negatives++;
        }
        return negatives % 2 == 0 ? 1 : -1;
    }
};
```

```python
def array_sign(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     sign of the product (1, -1, 0)
    """
    negatives = 0

    for num in nums:
        if num == 0:
            return 0
        if num < 0:
            negatives += 1

    return 1 if negatives % 2 == 0 else -1
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     sign of the product (1, -1, 0)
    pub fn array_sign(nums: Vec<i32>) -> i32 {
        let negatives = nums.iter().filter(|&&n| n < 0).count();
        if nums.contains(&0) { 0 } else if negatives % 2 == 0 { 1 } else { -1 }
    }
}
```

## Dry run

**Input:** `[-1,-2,-3,-4,3,2,1]`.

```
negatives: 4 (even).  Output: 1 ✓
Input: [1,5,0,2,-3]: 0 -> 0 ✓
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

- **Interview follow-up:** "Why not multiply?" The product overflows — the sign needs only the parity of negatives and the zero presence.
