# 16.13 Power Of Two

> **Source**: [`src/main/kotlin/math/PowerOfTwo.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/PowerOfTwo.kt)
> **Pattern**: single-bit test · **Core page**

## The Problem

Is `n` a power of two?

- Constraints: 32-bit.

## Examples

```
Input:  n = 1   -> true.  n = 16 -> true.  n = 3  -> false.
```

## Intuition — a power of two has exactly one set bit

`n & (n-1)` clears the lowest set bit — zero for powers of two:

```kotlin
return n > 0 && (n and (n - 1)) == 0
```

**Why `n > 0`?** 0 and negatives: `0 & -1 = 0` would pass without the positivity guard; `Int.MIN_VALUE & (MIN-1) = 0` too — the guard excludes both. The [16.1](number-of-1-bits.md) bit-counting, as a test.

## Approach 1 — Loop division (O(log n))

Divide by 2 while even: correct, slower.

## Approach 2 — Single-bit test (the repo's version, optimal)

```kotlin
class PowerOfTwo {
    /**
     * @param n input integer
     * @return  true iff n is a power of two
     */
    fun isPowerOfTwo(n: Int): Boolean {
        return n > 0 && (n and (n - 1)) == 0
    }
}
```

```java
public class PowerOfTwo {
    /**
     * @param n input integer
     * @return  true iff n is a power of two
     */
    public boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
}
```

```cpp
class PowerOfTwo {
public:
    /**
     * @param n input integer
     * @return  true iff n is a power of two
     */
    bool isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
};
```

```python
def is_power_of_two(n: int) -> bool:
    """
    @param n: input integer
    @return:  true iff n is a power of two
    """
    return n > 0 and (n & (n - 1)) == 0
```

```rust
impl Solution {
    /// @param n input integer
    /// @return  true iff n is a power of two
    pub fn is_power_of_two(n: i32) -> bool {
        n > 0 && (n & (n - 1)) == 0
    }
}
```

## Reading the code — what's actually happening

Let's slow the one-liner down and watch what each piece does:

```kotlin
return n > 0 && (n and (n - 1)) == 0
```

1. **`n - 1` — borrow through the zeros.** Think of subtraction as \"find the lowest set bit, turn it off, and turn every zero below it on.\" For `n = 16 = 10000₂`, subtracting 1 borrows all the way across the four trailing zeros: `10000₂ - 1 = 01111₂`. For `n = 5 = 101₂`, it's `100₂` — the lowest set bit (bit 0) flips to 0 and nothing below it exists to flip.

2. **`n and (n - 1)` — keep the top, drop the lowest set bit.** AND keeps only bits that are 1 in *both* operands. The bits above the lowest set bit of `n` are untouched (they're 1 in `n` and still 1 in `n-1`). The lowest set bit itself is 0 in `n-1`, so it vanishes. For `16 & 15 = 10000₂ & 01111₂ = 0` — *everything* disappeared, because 16 had exactly one set bit.

3. **`== 0` — the single-bit test.** The AND result is zero exactly when `n` had only one set bit to begin with. That's the entire definition of a power of two (plus the special case `n = 1 = 2⁰`).

4. **`n > 0` — the guard.** Without it, `n = 0` would pass (`0 & -1 = 0`) and negative numbers would too (`-2147483648 & 2147483647 = 0`). Powers of two are positive by definition, so the guard is not a formality — it's what makes the predicate correct.

The whole trick collapses to one idea: **a power of two is a number with exactly one set bit, and `n & (n-1)` is the surgical way to ask \"was there only one?\"**

## Dry run

**Input:** `n = 16`.

```
16 & 15 = 0 -> true ✓
n = 3: 3 & 2 = 2 != 0 -> false ✓
n = 0: 0 > 0 false -> false ✓
```

## Complexity

**Time.** O(1):

$$
T = O(1)
$$

**Space.** O(1):

$$
S = O(1)
$$

## Variants & follow-ups

- **Number Of 1 Bits** ([16.1](number-of-1-bits.md)) — the counting ancestor.
- **Interview follow-up:** "Why `n & (n-1)`?" Subtracting 1 flips the lowest set bit and everything below it — `n & (n-1)` clears exactly that bit. Zero means n had only one set bit: a power of two.
