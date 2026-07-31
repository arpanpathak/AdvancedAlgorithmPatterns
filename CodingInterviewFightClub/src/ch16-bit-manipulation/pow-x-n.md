# 16.8 Pow(x, n)

> **Source:** [`src/main/kotlin/math/pow.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/pow.kt)
> **Pattern:** binary exponentiation · **Core page**

## The Problem

Implement `pow(x, n)` computing `xⁿ` — in **O(log n) time** (n can be negative; `Int.MIN_VALUE` overflows `-n`).

- Constraints: $-2^{31} \le n \le 2^{31}-1$; x is a double.

## Examples

```
Input:  x = 2.0, n = 10   -> Output: 1024.0
Input:  x = 2.1, n = 3    -> Output: 9.26100
Input:  x = 2.0, n = -2   -> Output: 0.25
```

## Intuition — halve the exponent, square the base: the [16.0](pattern-primer.md) identity

The exponent's binary representation tells the story: `x¹⁰ = x⁸ · x²` — exactly the bits of 10 (`1010`). **Binary exponentiation** squares the base and halves the exponent, multiplying in the base whenever the exponent is odd:

$$
x^n = \begin{cases} (x^{n/2})^2 & n \text{ even} \\ x \cdot (x^{n/2})^2 & n \text{ odd} \end{cases}
$$

The repo ships both spellings — recursive and iterative — in one file:

```kotlin
class pow {
    fun myPow(x: Double, n: Int): Double {
        fun pow(x: Double, n: Long): Double {
            if (n == 0L) return 1.0
            val half = pow(x, n / 2)
            return when (n % 2) {
                0L -> half * half          // even: square the half
                else -> x * half * half    // odd: one extra factor
            }
        }
        return if (n >= 0) pow(x, n.toLong()) else 1 / pow(x, n.toLong())
    }

    fun myPowIterative(x: Double, n: Int): Double {
        var base = x
        var exponent = n.toLong()
        var result = 1.0

        if (exponent < 0) { base = 1 / base; exponent = -exponent }

        while (exponent > 0) {
            if (exponent % 2 == 1L) result *= base   // this bit is set: multiply in base
            base *= base                             // square the base for the next bit
            exponent /= 2
        }
        return result
    }
}
```

**Why `Long` and not `Int`?** `n = Int.MIN_VALUE` makes `-n` overflow back to `Int.MIN_VALUE` — the infinite-loop/1.0 bug. Converting to `Long` first gives `-n` the full 64-bit range. This is the [1.x](../ch01-binary-search/pattern-primer.md)-style overflow hygiene that interviewers check for.

**Why negative exponent = reciprocal?** `x⁻ⁿ = 1/xⁿ` — one inversion handles the whole sign case, then the same binary exponentiation runs.

## Approach 1 — Loop n times (O(n))

`for (i in 1..n) result *= x`: correct, linear — and obviously not what 10⁹ exponents want.

## Approach 2 — Binary exponentiation (the repo's version, optimal)

```kotlin
fun myPow(x: Double, n: Int): Double {
    var base = x
    var exponent = n.toLong()          // Long: Int.MIN_VALUE safe
    var result = 1.0

    if (exponent < 0) { base = 1 / base; exponent = -exponent }

    while (exponent > 0) {
        if (exponent % 2 == 1L) result *= base
        base *= base
        exponent /= 2
    }
    return result
}
```

```java
public class Pow {
    /**
     * @param x base
     * @param n exponent (may be negative)
     * @return  x raised to n
     */
    public double myPow(double x, int n) {
        long exp = n;                              // Long: Int.MIN_VALUE safe
        if (exp < 0) { x = 1 / x; exp = -exp; }

        double result = 1.0;
        while (exp > 0) {
            if (exp % 2 == 1) result *= x;         // this bit is set
            x *= x;                                // square for the next bit
            exp /= 2;
        }
        return result;
    }
}
```

```cpp
class Pow {
public:
    /**
     * @param x base
     * @param n exponent (may be negative)
     * @return  x raised to n
     */
    double myPow(double x, int n) {
        long long exp = n;                         // Long: INT_MIN safe
        if (exp < 0) { x = 1 / x; exp = -exp; }

        double result = 1.0;
        while (exp > 0) {
            if (exp % 2 == 1) result *= x;         // this bit is set
            x *= x;                                // square for the next bit
            exp /= 2;
        }
        return result;
    }
};
```

```python
def my_pow(x: float, n: int) -> float:
    """
    @param x: base
    @param n: exponent (may be negative)
    @return:  x raised to n
    """
    if n < 0:
        x = 1 / x
        n = -n

    result = 1.0
    while n > 0:
        if n % 2 == 1:
            result *= x          # this bit is set
        x *= x                   # square for the next bit
        n //= 2
    return result
```

```rust
impl Solution {
    /// @param x base
    /// @param n exponent (may be negative)
    /// @return  x raised to n
    pub fn my_pow(x: f64, n: i32) -> f64 {
        let mut base = x;
        let mut exp = n as i64;                // i64: i32::MIN safe
        if exp < 0 { base = 1.0 / base; exp = -exp; }

        let mut result = 1.0;
        while exp > 0 {
            if exp % 2 == 1 { result *= base; }   // this bit is set
            base *= base;                         // square for the next bit
            exp /= 2;
        }
        result
    }
}
```

## Dry run

**Input:** `x = 2.0`, `n = 10` (binary `1010`).

```
exp = 10, base = 2.0, result = 1.0

exp=10 (even): base = 4.0.   exp = 5
exp=5  (odd):  result = 1 * 4 = 4.  base = 16.   exp = 2
exp=2  (even): base = 256.  exp = 1
exp=1  (odd):  result = 4 * 256 = 1024.  base = 65536.  exp = 0

Output: 1024.0 ✓
```

The bit-reading is visible: 10 = 8 + 2, and the result multiplies in the base at exactly the bit-positions set in the exponent (the `4` at the `2²`-bit and `256` at the `2³`-bit → `4 × 256 = 1024`). Negative case: `x=2, n=-2` → base becomes 0.5, exp 2 → `0.5² = 0.25` ✓.

## Complexity

**Time.** Halving the exponent each step:

$$
T(n) = O(\log n)
$$

**Space.** A few scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Reverse Bits** ([16.2](reverse-bits.md)) — the same bit-by-bit machine, reading bits instead of exponents.
- **Matrix Exponentiation** — the linear-recurrence upgrade: the same halving, with matrix multiplication as `base *= base` (Fibonacci in O(log n)).
- **Interview follow-up:** "Why does the iterative version need `exp % 2 == 1` before squaring?" Each iteration processes one bit of the exponent, least-significant first: `result` absorbs the base iff that bit is 1; then the base squares to represent the *next* bit's place value. The order (check bit, square, halve) is the whole algorithm — swap the square and the check and you get `x^(n/2)` instead of `x^n`.
