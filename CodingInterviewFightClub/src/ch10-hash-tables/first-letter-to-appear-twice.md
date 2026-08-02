# 10.30 First Letter To Appear Twice

> **Source**: [`src/main/kotlin/bitset/FirstLetterToAppearTwice.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/bitset/FirstLetterToAppearTwice.kt)
> **Pattern**: bitmask duplicate detection · **Core page**

## The Problem

The first letter that appears twice (guaranteed to exist).

- Constraints: n ≤ 1000; lowercase.

## Examples

```
Input:  s = "abccbaacz"   -> Output: "c"
```

## Intuition — the 26-bit mask; a second occurrence flips the bit back to a seen state

```kotlin
var bits = 0

for (ch in s) {
    val bit = 1 shl (ch - 'a')

    if (bits and bit != 0) return ch
    bits = bits or bit
}
return ' '
```

## Approach 1 — Bitmask (the repo's version, optimal)

```kotlin
class FirstLetterToAppearTwice {
    /**
     * @param s input string
     * @return  first repeated letter
     */
    fun repeatedCharacter(s: String): Char {
        var bits = 0

        for (ch in s) {
            val bit = 1 shl (ch - 'a')

            if (bits and bit != 0) return ch
            bits = bits or bit
        }
        return ' '
    }
}
```

```java
public class FirstLetterToAppearTwice {
    /**
     * @param s input string
     * @return  first repeated letter
     */
    public char repeatedCharacter(String s) {
        int bits = 0;

        for (char c : s.toCharArray()) {
            int bit = 1 << (c - 'a');

            if ((bits & bit) != 0) return c;
            bits |= bit;
        }
        return ' ';
    }
}
```

```cpp
#include <string>

class FirstLetterToAppearTwice {
public:
    /**
     * @param s input string
     * @return  first repeated letter
     */
    char repeatedCharacter(std::string s) {
        int bits = 0;

        for (char c : s) {
            int bit = 1 << (c - 'a');

            if (bits & bit) return c;
            bits |= bit;
        }
        return ' ';
    }
};
```

```python
def repeated_character(s: str) -> str:
    """
    @param s: input string
    @return:  first repeated letter
    """
    seen = 0

    for ch in s:
        bit = 1 << (ord(ch) - ord("a"))

        if seen & bit:
            return ch
        seen |= bit

    return " "
```

```rust
impl Solution {
    /// @param s input string
    /// @return  first repeated letter
    pub fn repeated_character(s: String) -> char {
        let mut bits: u32 = 0;

        for c in s.chars() {
            let bit = 1u32 << (c as u32 - 'a' as u32);

            if bits & bit != 0 { return c; }
            bits |= bit;
        }
        ' '
    }
}
```

## Reading the code — what's actually happening

```kotlin
var bits = 0
for (ch in s) {
    val bit = 1 shl (ch - 'a')
    if (bits and bit != 0) return ch
    bits = bits or bit
}
return ' '
```

The insight: there are only 26 lowercase letters, and an `Int` has 32 bits — so we can encode "have I seen this letter?" as **one bit per letter** and check membership with a single AND. No hash map, no allocation.

- **`ch - 'a'` maps each letter to a slot 0–25.** `'a'` → 0, `'b'` → 1, …, `'z'` → 25.
- **`1 shl (ch - 'a')` builds that letter's "identity card"** — an integer with exactly one bit set, at the letter's slot. For `'c'` that's `1 << 2 = 4` (`...000100`).
- **`bits and bit != 0` is the "seen before?" test.** `bits` has bit `k` set iff that letter has appeared earlier in the scan. If `bits` already contains our letter's bit, the AND is non-zero → *second* occurrence → return immediately. This is the first-repeated detection.
- **`bits = bits or bit` records the sighting.** If the letter is new, OR-ing its bit into `bits` marks it as seen for future characters. Duplicates leave `bits` unchanged (the bit was already there) — that's fine, because the very first duplicate triggers the early return.
- **Why a bitmask and not a set?** A `HashSet<Char>` does the same job, but the mask is a single `Int`: O(1) membership via one CPU instruction, zero heap allocation, and it shows fluency with bit-level tricks (see [ch16](../ch16-bit-manipulation/pattern-primer.md)).

Trace `"abccbaacz"`: `a` → bit 0 set; `b` → bit 1 set; `c` → bit 2 set; next `c` → `bits and bit2 != 0` → return `'c'` ✓.

## Dry run

**Input:** `s = "abccbaacz"`.

```
a: bit0.  b: bit1.  c: bit2.  c: bit2 already -> return 'c' ✓
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

- **Interview follow-up:** "Why a bitmask instead of a set?" 26 lowercase letters fit in an int — membership is an AND, O(1) with zero allocation.
