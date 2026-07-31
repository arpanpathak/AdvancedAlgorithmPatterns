# 16.9 Divide Two Integers

> **Source:** [`src/main/kotlin/math/DivideTwoIntegers.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/DivideTwoIntegers.kt)
> **Pattern:** binary long division · **Core page**

## The Problem

`divide(dividend, divisor)` — integer division **without `*`, `/`, `%`**, truncated toward zero.

- Constraints: results fit in 32-bit; divisor ≠ 0.

## Examples

```
Input:  dividend = 10, divisor = 3   -> Output: 3
Input:  dividend = 7, divisor = -3   -> Output: -2
Input:  dividend = -2147483648, divisor = -1 -> Output: 2147483647 (clamped)
```

## Intuition — subtract doubling multiples, like long division in binary

`quotient` is the sum of powers of 2. For each step, find the **largest `multiple = divisor × 2^k`** that fits in the remainder, subtract it, and add `2^k` to the quotient — repeated doubling, the inverse of binary exponentiation ([16.8](pow-x-n.md)):

```kotlin
if (dividend == Int.MIN_VALUE && divisor == -1) return Int.MAX_VALUE   // overflow

val sign = if ((dividend < 0) xor (divisor < 0)) -1 else 1

var dividendL = Math.abs(dividend.toLong())
val divisorL = Math.abs(divisor.toLong())

var quotient = 0L

while (dividendL >= divisorL) {
    var tempDivisor = divisorL
    var multiple = 1L

    while (tempDivisor shl 1 <= dividendL) {      // double while it fits
        tempDivisor = tempDivisor shl 1
        multiple = multiple shl 1
    }

    dividendL -= tempDivisor
    quotient += multiple
}

return (sign * quotient).toInt()
```

**Why `Long` and the `MIN / -1` guard?** `abs(Int.MIN_VALUE)` overflows; the `toLong()` cast fixes the abs, and the `MIN / -1` case (result = 2³¹) is the only overflow — clamped to `MAX`.

**Why the inner doubling loop?** Each outer iteration removes the largest `divisor·2^k ≤ remainder`; the inner loop doubles until the next multiple would exceed. The quotient accumulates the `2^k`s — this is long division, reading the binary digits of the quotient.

## Approach 1 — Repeated subtraction (O(quotient))

Subtract divisor until negative: correct, quadratic worst case.

## Approach 2 — Doubling multiples (the repo's version, optimal)

```kotlin
class DivideTwoIntegers {
    /**
     * @param dividend numerator
     * @param divisor  denominator (nonzero)
     * @return         truncated quotient
     */
    fun divide(dividend: Int, divisor: Int): Int {
        if (dividend == Int.MIN_VALUE && divisor == -1) return Int.MAX_VALUE

        val sign = if ((dividend < 0) xor (divisor < 0)) -1 else 1

        var dividendL = Math.abs(dividend.toLong())
        val divisorL = Math.abs(divisor.toLong())

        var quotient = 0L

        while (dividendL >= divisorL) {
            var tempDivisor = divisorL
            var multiple = 1L

            while (tempDivisor shl 1 <= dividendL) {
                tempDivisor = tempDivisor shl 1
                multiple = multiple shl 1
            }

            dividendL -= tempDivisor
            quotient += multiple
        }
        return (sign * quotient).toInt()
    }
}
```

```java
public class DivideTwoIntegers {
    /**
     * @param dividend numerator
     * @param divisor  denominator (nonzero)
     * @return         truncated quotient
     */
    public int divide(int dividend, int divisor) {
        if (dividend == Integer.MIN_VALUE && divisor == -1) return Integer.MAX_VALUE;

        int sign = (dividend < 0) ^ (divisor < 0) ? -1 : 1;

        long dvd = Math.abs((long) dividend);
        long dvs = Math.abs((long) divisor);
        long quotient = 0;

        while (dvd >= dvs) {
            long temp = dvs, multiple = 1;

            while ((temp << 1) <= dvd) {
                temp <<= 1;
                multiple <<= 1;
            }
            dvd -= temp;
            quotient += multiple;
        }
        return (int) (sign * quotient);
    }
}
```

```cpp
#include <cstdlib>
#include <climits>

class DivideTwoIntegers {
public:
    /**
     * @param dividend numerator
     * @param divisor  denominator (nonzero)
     * @return         truncated quotient
     */
    int divide(int dividend, int divisor) {
        if (dividend == INT_MIN && divisor == -1) return INT_MAX;

        int sign = (dividend < 0) ^ (divisor < 0) ? -1 : 1;

        long long dvd = std::llabs((long long)dividend);
        long long dvs = std::llabs((long long)divisor);
        long long quotient = 0;

        while (dvd >= dvs) {
            long long temp = dvs, multiple = 1;

            while ((temp << 1) <= dvd) {
                temp <<= 1;
                multiple <<= 1;
            }
            dvd -= temp;
            quotient += multiple;
        }
        return (int)(sign * quotient);
    }
};
```

```python
def divide(dividend: int, divisor: int) -> int:
    """
    @param dividend: numerator
    @param divisor:  denominator (nonzero)
    @return:         truncated quotient
    """
    if dividend == -(2**31) and divisor == -1:
        return 2**31 - 1

    sign = -1 if (dividend < 0) ^ (divisor < 0) else 1

    dvd, dvs = abs(dividend), abs(divisor)
    quotient = 0

    while dvd >= dvs:
        temp, multiple = dvs, 1
        while (temp << 1) <= dvd:
            temp <<= 1
            multiple <<= 1
        dvd -= temp
        quotient += multiple

    return sign * quotient
```

```rust
impl Solution {
    /// @param dividend numerator
    /// @param divisor  denominator (nonzero)
    /// @return         truncated quotient
    pub fn divide(dividend: i32, divisor: i32) -> i32 {
        if dividend == i32::MIN && divisor == -1 { return i32::MAX; }

        let sign = if (dividend < 0) ^ (divisor < 0) { -1 } else { 1 };
        let mut dvd = (dividend as i64).abs();
        let dvs = (divisor as i64).abs();
        let mut quotient: i64 = 0;

        while dvd >= dvs {
            let mut temp = dvs;
            let mut multiple: i64 = 1;
            while (temp << 1) <= dvd {
                temp <<= 1;
                multiple <<= 1;
            }
            dvd -= temp;
            quotient += multiple;
        }
        (sign * quotient) as i32
    }
}
```

## Dry run

**Input:** `dividend = 10, divisor = 3`.

```
sign = +1.  dvd=10, dvs=3, quotient=0
outer: 10 >= 3:
  inner: temp=3, mult=1.  3<<1=6 <= 10 -> temp=6, mult=2.  6<<1=12 <= 10? no.
  dvd = 10-6 = 4.  quotient = 2.
outer: 4 >= 3:
  inner: temp=3, mult=1.  6 <= 4? no.
  dvd = 4-3 = 1.  quotient = 3.
outer: 1 >= 3? no.

Output: 3 ✓   (10 / 3 truncated)
```

The binary digits of the quotient: 10 = 6 + 3 + 1 → 2 + 1 = 3. Each outer iteration subtracts the largest `3·2^k` fitting the remainder; the inner doubling finds k. Negative case `7 / -3`: sign −1, same magnitudes → 2 → −2 ✓. `MIN / -1` clamps to MAX before any arithmetic.

## Complexity

**Time.** O(log dividend) doublings per step:

$$
T = O(\log^2 \text{dividend})
$$

**Space.** Scalars:

$$
S = O(1)
$$

## Variants & follow-ups

- **Pow(x, n)** ([16.8](pow-x-n.md)) — the doubling inverse; same bit-level machinery.
- **Interview follow-up:** "Why must the inner loop double the *divisor*, not the quotient?" The quotient's bits are discovered from the largest multiple fitting the remainder — doubling the divisor finds that multiple; the matching power of 2 is the quotient's bit. This is long division in base 2: subtract, shift, repeat.
