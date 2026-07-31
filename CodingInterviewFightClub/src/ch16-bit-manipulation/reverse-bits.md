# 16.2 Reverse Bits

> **Source:** [`src/main/kotlin/bitset/ReverseBits.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/bitset/ReverseBits.kt)
> **Pattern:** bit-by-bit rebuild · **Core page**

## The Problem

Reverse the bits of a given 32-bit unsigned integer — the bit at position `i` moves to position `31 - i`.

- Constraints: `n` is a 32-bit unsigned integer.

## Examples

```
Input:  n = 43261596 (00000010100101000001111010011100)
Output: 964176192   (00111001011110000010100101000000)
```

## Intuition — extract the last bit, shift it into the result

The rebuild loop:

```
result = 0
for i in 0..31:
    bit = n and 1            # the next bit to place (from the low end)
    result = (result shl 1) or bit     # make room and place it
    n = n ushr 1             # drop the consumed bit
```

After 32 iterations, `n`'s bit 0 has been placed into `result`'s bit 31, bit 1 into bit 30, etc. — a full reversal. The order of operations matters: **shift the result first, then OR** — otherwise the new bit overwrites instead of appending.

**Why `ushr` (unsigned shift) and not `shr`?** `shr` on a negative number fills the top bit with 1s (sign extension), which would inject phantom bits. `ushr` always fills with 0 — the only correct choice when treating the int as a bit string. (Java/Kotlin's `int` is signed; the "unsigned" in the problem is a *bit-level* concern.)

**Why `n and 1`?** Isolating the lowest bit with a mask is the extraction idiom — the [16.0](pattern-primer.md) toolbox's "read one bit" move.

## Approach 1 — Convert to string, reverse, parse

Work in a string/binary representation: correct but slow and misses the point — bit reversal is a *shift* problem.

## Approach 2 — Bit-by-bit rebuild (the repo's version, optimal)

```kotlin
class ReverseBits {
    /**
     * @param n unsigned 32-bit integer
     * @return  its bits reversed
     */
    fun reverseBits(n: Int): Int {
        var num = n
        var result = 0

        for (i in 0 until 32) {
            val bit = num and 1                 // extract the last bit
            result = (result shl 1) or bit      // shift left and add the bit
            num = num ushr 1                    // unsigned right shift
        }
        return result
    }
}
```

```java
public class ReverseBits {
    /**
     * @param n unsigned 32-bit integer
     * @return  its bits reversed
     */
    public int reverseBits(int n) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            result = (result << 1) | (n & 1);   // shift left and add the lowest bit
            n >>>= 1;                           // unsigned right shift
        }
        return result;
    }
}
```

```cpp
class ReverseBits {
public:
    /**
     * @param n unsigned 32-bit integer
     * @return  its bits reversed
     */
    uint32_t reverseBits(uint32_t n) {
        uint32_t result = 0;
        for (int i = 0; i < 32; i++) {
            result = (result << 1) | (n & 1);   // shift left and add the lowest bit
            n >>= 1;
        }
        return result;
    }
};
```

```python
def reverse_bits(n: int) -> int:
    """
    @param n: unsigned 32-bit integer
    @return:  its bits reversed
    """
    result = 0
    for _ in range(32):
        result = (result << 1) | (n & 1)   # shift left and add the lowest bit
        n >>= 1                            # unsigned shift (Python ints are infinite)
    return result
```

```rust
impl Solution {
    /// @param n unsigned 32-bit integer
    /// @return  its bits reversed
    pub fn reverse_bits(mut n: u32) -> u32 {
        let mut result: u32 = 0;
        for _ in 0..32 {
            result = (result << 1) | (n & 1);   // shift left and add the lowest bit
            n >>= 1;
        }
        result
    }
}
```

## Dry run

**Input:** `n = 4` (binary `...000100` in 32 bits).

```
result = 0
i=0: bit = 0 -> result = 0.        n = 2 (10)
i=1: bit = 0 -> result = 0.        n = 1 (1)
i=2: bit = 1 -> result = (0<<1)|1 = 1.   n = 0
i=3..31: bit = 0 -> result = result << 1 each time (1 -> 2 -> 4 -> ... -> 2^28 at i=31)

result = 2^28 = 268435456 ✓   (100 in 32-bit reversed is the bit-28 set)
```

The build is visible at i=2: the lone set bit of `4` is placed at position 0, then each subsequent iteration shifts it *up* — landing at bit 28 after the full 32, exactly the reversal of bit 2.

## Complexity

**Time.** Fixed 32 iterations:

$$
T(n) = O(32) = O(1)
$$

**Space.** Two variables:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Number Of 1 Bits** ([16.1](number-of-1-bits.md)) — the read-only half of this loop.
- **Power Of Two / bit masks** — the mask idiom (`n and 1`) is the universal "extract the lowest bit" move.
- **Interview follow-up:** "Why must the shift be unsigned?" In Java/Kotlin, `int` is signed: `>>` propagates the sign bit (1s flood in from the top), corrupting the bit string; `>>>`/`ushr` always fills 0s. The moment you treat an int as a *sequence of bits* rather than a number, every right shift must be unsigned.
