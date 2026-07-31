# 3.26 Reverse Integer

> **Source**: [`src/main/kotlin/math/ReverseInteger.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/ReverseInteger.kt)
> **Pattern**: digit extraction with overflow pre-check · **Core page**

## The Problem

Reverse a 32-bit int's digits; return 0 on overflow.

- Constraints: 32-bit.

## Examples

```
Input:  123  -> Output: 321
Input:  -123 -> Output: -321
Input:  120  -> Output: 21
Input:  1534236469 -> Output: 0 (overflow)
```

## Intuition — build the reversed number, check overflow *before* multiplying

`reversed = reversed * 10 + digit`; the pre-check `reversed > (MAX - digit) / 10` detects the overflow before it happens — the [9.14](../ch09-strings/string-to-integer-atoi.md) guard:

```kotlin
var reversed: Long = 0
val sign = if (x < 0) -1 else 1
var num: Long = abs(x.toLong())

while (num != 0L) {
    if (reversed > (Int.MAX_VALUE - num % 10) / 10) {
        return 0
    }
    reversed = reversed * 10L + num % 10
    num = num / 10
}
return (sign * reversed.toInt())
```

**Why Long for the intermediate?** `abs(Int.MIN_VALUE)` overflows — the Long cast fixes the sign handling; the explicit pre-check additionally keeps the *reversal* itself overflow-safe.

## Approach 1 — String reverse + parse (fragile)

`reversed.toString().reversed()`: fails on overflow silently.

## Approach 2 — Pre-checked digit build (the repo's version, optimal)

```kotlin
class ReverseInteger {
    /**
     * @param x input integer
     * @return  reversed digits, or 0 on overflow
     */
    fun reverse(x: Int): Int {
        var reversed: Long = 0
        val sign = if (x < 0) -1 else 1
        var num: Long = abs(x.toLong())

        while (num != 0L) {
            if (reversed > (Int.MAX_VALUE - num % 10) / 10) {
                return 0
            }
            reversed = reversed * 10L + num % 10
            num = num / 10
        }
        return (sign * reversed.toInt())
    }
}
```

```java
public class ReverseInteger {
    /**
     * @param x input integer
     * @return  reversed digits, or 0 on overflow
     */
    public int reverse(int x) {
        long reversed = 0;

        while (x != 0) {
            reversed = reversed * 10 + x % 10;
            x /= 10;

            if (reversed > Integer.MAX_VALUE || reversed < Integer.MIN_VALUE) return 0;
        }
        return (int) reversed;
    }
}
```

```cpp
#include <climits>

class ReverseInteger {
public:
    /**
     * @param x input integer
     * @return  reversed digits, or 0 on overflow
     */
    int reverse(int x) {
        long reversed = 0;

        while (x != 0) {
            reversed = reversed * 10 + x % 10;
            x /= 10;

            if (reversed > INT_MAX || reversed < INT_MIN) return 0;
        }
        return (int) reversed;
    }
};
```

```python
def reverse(x: int) -> int:
    """
    @param x: input integer
    @return:  reversed digits, or 0 on overflow
    """
    sign = -1 if x < 0 else 1
    num = abs(x)
    reversed_num = 0

    while num:
        reversed_num = reversed_num * 10 + num % 10
        num //= 10

    result = sign * reversed_num
    return result if -(2**31) <= result < 2**31 else 0
```

```rust
impl Solution {
    /// @param x input integer
    /// @return  reversed digits, or 0 on overflow
    pub fn reverse(x: i32) -> i32 {
        let sign = if x < 0 { -1 } else { 1 };
        let mut num = (x as i64).abs();
        let mut reversed: i64 = 0;

        while num != 0 {
            reversed = reversed * 10 + num % 10;
            num /= 10;
        }

        reversed *= sign;
        if reversed < i32::MIN as i64 || reversed > i32::MAX as i64 { 0 } else { reversed as i32 }
    }
}
```

## Dry run

**Input:** `x = 1534236469`.

```
digits reversed: 9646324351 > Int.MAX (2147483647) -> the pre-check fires -> 0 ✓
Input: -123: num=123.  reversed: 3 -> 32 -> 321.  sign -1 -> -321 ✓
```

## Complexity

**Time.** Digit count:

$$
T = O(\log x)
$$

**Space.** Constants:

$$
S = O(1)
$$

## Variants & follow-ups

- **String To Integer (atoi)** ([9.14](../ch09-strings/string-to-integer-atoi.md)) — the same overflow pre-check in a parser.
- **Interview follow-up:** "Why check before the multiply?" `reversed * 10 + digit` computed first would overflow before inspection — the pre-check compares the *about-to-multiply* state against the safe bound, the only place the overflow is visible.
