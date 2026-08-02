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

## Reading the code — what's actually happening

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

Why can we know the sign without multiplying? Because multiplication's sign obeys two dead-simple rules: **a zero anywhere makes the whole product 0**, and **each negative flips the sign**. Positive numbers are invisible to the sign, so the product's sign is determined entirely by *how many* negatives there are.

- **`if (it == 0) return 0` is the zero trapdoor.** The moment we see a zero, the product is zero regardless of everything else — no need to look further, return immediately. (In Kotlin, `return` inside `forEach` exits the whole function, which is exactly what we want.)
- **`if (it < 0) negativeCount++` tallies the sign-flippers.** Positives are skipped — they can't change the outcome. Each negative multiplies the running sign by −1.
- **The parity test decides the sign.** An *even* count of negatives (0, 2, 4, …) means the flips pair up and cancel → positive → `1`. An *odd* count leaves one flip unpaired → negative → `-1`. That's the `% 2 == 0` check: it asks "do the negatives cancel out?"
- **Why not just multiply?** The product can overflow a 32-bit int with a handful of large values. The parity approach needs no arithmetic at all — two counters' worth of state instead of a giant number.

Trace `[-1,-2,-3,-4,3,2,1]`: four negatives, zero zeros → `4 % 2 == 0` → `1` ✓. The actual product is `144` — positive, as predicted.

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
