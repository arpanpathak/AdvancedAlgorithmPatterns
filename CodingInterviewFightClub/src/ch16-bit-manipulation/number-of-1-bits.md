# 16.1 Number Of 1 Bits

> **Source:** [`src/main/kotlin/bitset/NumberOfOneBits.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/bitset/NumberOfOneBits.kt)
> **Pattern:** `n & (n-1)` popcount · **Core page**

## The Problem

Return the **number of set bits** (the Hamming weight / popcount) of an unsigned integer `n`.

- Constraints: `n` is a 32-bit unsigned integer.

## Examples

```
Input:  n = 11  (binary 1011)   -> Output: 3
Input:  n = 128 (10000000)      -> Output: 1
```

## Intuition — each `n & (n-1)` removes exactly one set bit

The identity:

> `n & (n - 1)` clears the **lowest set bit** of `n`, leaving everything else intact.

So counting set bits is: repeatedly apply `n = n and (n - 1)` and count — the loop runs *exactly* `popcount(n)` times, not 32 times. Example: `12 = 1100` → `1100 & 1011 = 1000` (one bit cleared) → `1000 & 0111 = 0` — two iterations for two set bits.

**Why does `n-1` work?** Subtracting 1 borrows through the trailing zeros and flips the lowest set bit to 0 (and the zeros below it to 1). AND-ing with the original keeps the higher bits, zeros out the flipped region — net effect: the lowest set bit disappears.

**The naive alternative** — check all 32 bits with `(n shr i) and 1` — is fine but always runs 32 times; the identity version runs only as many times as there are set bits. Both are O(32); the identity is the one interviews expect you to know.

## Approach 1 — Check every bit (O(32))

Loop 32 times, `count += n and 1; n ushr= 1`. Simple, constant-time, never faster than needed.

## Approach 2 — `n & (n-1)` counting (the repo's version, optimal)

```kotlin
class NumberOfOneBits {
    /**
     * @param n unsigned 32-bit integer
     * @return  the number of set bits in n
     */
    fun hammingWeight(n: Int): Int {
        var number = n
        var count = 0

        while (number > 0) {
            number = number and (number - 1)   // clear the lowest set bit
            count++
        }
        return count
    }
}
```

```java
public class NumberOfOneBits {
    /**
     * @param n unsigned 32-bit integer
     * @return  the number of set bits in n
     */
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1);                      // clear the lowest set bit
            count++;
        }
        return count;
    }
}
```

```cpp
class NumberOfOneBits {
public:
    /**
     * @param n unsigned 32-bit integer
     * @return  the number of set bits in n
     */
    int hammingWeight(uint32_t n) {
        int count = 0;
        while (n) {
            n &= (n - 1);                      // clear the lowest set bit
            count++;
        }
        return count;
    }
};
```

```python
def hamming_weight(n: int) -> int:
    """
    @param n: unsigned 32-bit integer
    @return:  the number of set bits in n
    """
    count = 0
    while n:
        n &= n - 1                  # clear the lowest set bit
        count += 1
    return count
```

```rust
impl Solution {
    /// @param n unsigned 32-bit integer
    /// @return  the number of set bits in n
    pub fn hamming_weight(mut n: u32) -> i32 {
        let mut count = 0;
        while n != 0 {
            n &= n - 1;             // clear the lowest set bit
            count += 1;
        }
        count
    }
}
```

## Reading the code — what's actually happening

```kotlin
var number = n
var count = 0
while (number > 0) {
    number = number and (number - 1)   // clear the lowest set bit
    count++
}
return count
```

- **`number` is a working copy** — we don't want to destroy the caller's `n`, so we mutate a local.
- **The loop condition `number > 0` is the whole efficiency story.** Each iteration *removes exactly one set bit*, so the loop runs once per set bit — never a fixed 32 times. If the number is sparse (say `1000₂`), the loop runs once and stops.
- **`number and (number - 1)` is the surgical strike.** Subtracting 1 turns the lowest set bit into 0 (borrowing through the zeros below it); AND-ing with the original keeps everything above that bit intact and zeroes out the flipped region. Net effect: exactly one set bit disappears, nothing else moves. `1011₂ & 1010₂ = 1010₂` — the low 1 is gone, the upper `10` is untouched.
- **`count++` records the removal.** Since every removal corresponds to one set bit that used to be there, `count` at the end *is* the popcount. The loop count equals the answer — that's the elegant part worth saying out loud in an interview.

Trace `n = 11 = 1011₂`: remove low bit → `1010₂` (count 1) → `1000₂` (count 2) → `0000₂` (count 3) → loop exits. Three iterations, three set bits, done — the two zero bits were never even looked at.

## Dry run

**Input:** `n = 11` (binary `1011`).

```
n=1011 (11), count=0
  n and (n-1) = 1011 & 1010 = 1010 (10).  count=1
  n and (n-1) = 1010 & 1001 = 1000 (8).   count=2
  n and (n-1) = 1000 & 0111 = 0000 (0).   count=3
n=0 -> stop.  Output: 3 ✓
```

Each iteration removes exactly one set bit — the loop ran 3 times for 3 set bits, never touching the two zero bits. That's the efficiency claim: $O(\text{set bits})$ iterations instead of a fixed 32.

## Complexity

**Time.** One iteration per set bit (≤ 32):

$$
T(n) = O(\text{popcount}(n)) \subseteq O(1)
$$

**Space.** Two variables:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Counting Bits** — popcount for *all* numbers 0..n: the DP `dp[i] = dp[i shr 1] + (i and 1)` reuses this per number.
- **Reverse Bits** ([16.2](reverse-bits.md)) — the same bit-level toolbox in rebuild mode.
- **Power Of Two** — `n > 0 && n and (n-1) == 0`: popcount == 1, the identity as a boolean.
- **Interview follow-up:** "Why not just count with a 32-iteration loop?" Both are O(32) worst case; the `n & (n-1)` version's loop count equals the *answer*, which is both faster on sparse numbers and the "known identity" signal. If you can quote the identity, you've shown fluency — that's the depth bar for bit problems.
