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
