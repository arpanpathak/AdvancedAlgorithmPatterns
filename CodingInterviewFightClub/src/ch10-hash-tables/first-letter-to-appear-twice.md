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
