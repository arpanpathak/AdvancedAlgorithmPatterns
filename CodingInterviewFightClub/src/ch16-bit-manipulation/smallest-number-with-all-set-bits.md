# 16.7 Smallest Number With All Set Bits

> **Source:** [`src/main/kotlin/bitset/SmallestNumberWithAllSetBits.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/bitset/SmallestNumberWithAllSetBits.kt)
> **Pattern:** msb -> all-ones · **Core page**

## The Problem

Given a positive integer `n`, return the **smallest number ≥ n whose binary representation has all bits set** (of the form $2^k - 1$).

- Constraints: $1 \le n \le 10^9$.

## Examples

```
Input:  n = 5   (101)  -> Output: 7   (111)
Input:  n = 10  (1010) -> Output: 15  (1111)
Input:  n = 7   (111)  -> Output: 7   (already all set)
```

## Intuition — find the MSB position, then fill everything below it

The all-set numbers are $1, 3, 7, 15, \ldots = 2^k - 1$. For a given `n`, the smallest all-set number ≥ `n` is: **take `n`'s most significant bit position `k`, and return `2^k - 1`** — unless `n` is itself of that form.

The repo's loop finds the msb:

```
var msb = 0
while (1 shl msb <= n) msb++     # msb ends as the position above n's top bit
return (1 shl msb) - 1
```

The loop stops when `2^msb > n`, so `2^msb - 1 >= n` and `2^(msb-1) - 1 < n` — the result is the smallest all-set number ≥ n. **Why is `2^msb - 1` always ≥ n?** `n` has at most `msb` bits, so `n < 2^msb`, hence `n <= 2^msb - 1`. And `2^(msb-1) - 1` (all set with one fewer bit) is `< n` by the loop condition — so the result is minimal.

**The edge case** `n <= 1` → `1` (the loop would also handle it, but the repo short-circuits).

**The `1 shl msb - 1` precedence note:** in Kotlin, `shl` binds tighter than `-`, so `1 shl msb - 1` = `(1 shl msb) - 1` — the all-ones mask with `msb` low bits set. The [16.0](pattern-primer.md) identity `(1 << k) - 1` strikes again.

## Approach 1 — Increment and check (O(answer - n))

Walk up until the number is all-set: correct, but can take up to $2^k$ steps.

## Approach 2 — msb -> all-ones (the repo's version, optimal)

```kotlin
class SmallestNumberWithAllSetBits {
    /**
     * @param n positive integer
     * @return  smallest number >= n whose bits are all set
     */
    fun smallestNumber(n: Int): Int {
        if (n <= 1) return 1

        // Find the bit position of the most significant set bit
        var msb = 0
        while (1 shl msb <= n) {
            msb++
        }

        // All bits set up to msb position: (1 << msb) - 1
        return (1 shl msb) - 1
    }
}
```

```java
public class SmallestNumberWithAllSetBits {
    /**
     * @param n positive integer
     * @return  smallest number >= n whose bits are all set
     */
    public int smallestNumber(int n) {
        if (n <= 1) return 1;

        int msb = 0;
        while ((1 << msb) <= n) msb++;        // position above n's top bit

        return (1 << msb) - 1;                // all bits set up to that position
    }
}
```

```cpp
class SmallestNumberWithAllSetBits {
public:
    /**
     * @param n positive integer
     * @return  smallest number >= n whose bits are all set
     */
    int smallestNumber(int n) {
        if (n <= 1) return 1;

        int msb = 0;
        while ((1 << msb) <= n) msb++;        // position above n's top bit

        return (1 << msb) - 1;                // all bits set up to that position
    }
};
```

```python
def smallest_number(n: int) -> int:
    """
    @param n: positive integer
    @return:  smallest number >= n whose bits are all set
    """
    if n <= 1:
        return 1

    msb = 0
    while (1 << msb) <= n:
        msb += 1                        # position above n's top bit
    return (1 << msb) - 1               # all bits set up to that position
```

```rust
impl Solution {
    /// @param n positive integer
    /// @return  smallest number >= n whose bits are all set
    pub fn smallest_number(n: i32) -> i32 {
        if n <= 1 { return 1; }

        let mut msb = 0u32;
        while (1i64 << msb) <= n as i64 { msb += 1; }   // position above n's top bit
        (1i64 << msb) as i32 - 1                        // all bits set up to that position
    }
}
```

## Reading the code — what's actually happening

```kotlin
var msb = 0
while (1 shl msb <= n) {
    msb++
}
return (1 shl msb) - 1
```

- **`msb` counts bit positions, starting at 0.** Each loop iteration asks "is `2^msb` still ≤ `n`?" — i.e., "does `n` still reach this high?" The loop keeps climbing while the answer is yes.
- **The loop exits one *above* `n`'s top bit.** For `n = 5 = 101₂`: `1 ≤ 5` (msb=1), `2 ≤ 5` (msb=2), `4 ≤ 5` (msb=3), `8 ≤ 5`? No — stop. `msb = 3`, meaning `n` occupies at most bits 0..2 and `2³ = 8` is the first power of two strictly above `n`.
- **`(1 shl msb) - 1` turns "one past the top" into "all ones up to the top".** `1 << 3 = 1000₂`; subtracting 1 borrows through the three zeros → `0111₂ = 7`. That's the all-set number with exactly as many bits as `n` needs. And it's *minimal*: the all-set number with one fewer bit, `2² - 1 = 3`, failed the loop condition (`4 ≤ 5` was true, so the loop kept going past it) — meaning `3 < n`. Nothing smaller than `7` can be both ≥ `5` and all-set.
- **The edge case `n <= 1` → `1`** short-circuits the trivial input; the loop would also terminate correctly there, but the guard makes the intent explicit.

The whole method is one idea: **"find the bit-length of `n`, then return the number made of that many 1s."** The loop is just a hand-rolled way of measuring bit-length.

## Dry run

**Input:** `n = 5` (binary 101).

```
msb = 0: 1 << 0 = 1 <= 5 -> msb=1
        1 << 1 = 2 <= 5 -> msb=2
        1 << 2 = 4 <= 5 -> msb=3
        1 << 3 = 8 <= 5? NO -> stop.  msb = 3
return (1 << 3) - 1 = 8 - 1 = 7 ✓   (111)
```

Now `n = 7` (111): the loop runs `1,2,4 <= 7` then `8 <= 7?` no → `msb = 3` → `8 - 1 = 7` ✓ — already all-set, the answer is itself. The minimality argument: `2^2 - 1 = 3 < 5`, so nothing with fewer bits can work; `2^3 - 1 = 7 >= 5` is the first all-set number at or above `n`.

## Complexity

**Time.** One shift per bit:

$$
T(n) = O(\log n)
$$

**Space.** Constant:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Power Of Two** — `n > 0 && (n & (n-1)) == 0`: the same msb thinking, inverted (exactly one bit set).
- **Number Of Steps To Reduce A Number In Binary Representation To One** (`src/main/kotlin/bitset/`) — binary-string manipulation with the same bit-level mindset.
- **Interview follow-up:** "Why is the result `2^msb - 1` and not something involving `n`'s own bits?" All-set numbers form the ladder $1, 3, 7, 15, \ldots$; `n` sits strictly between two rungs, and the upper rung is determined entirely by its bit-length. The msb scan finds the rung in $O(\log n)$ — the bit-length *is* the answer.
