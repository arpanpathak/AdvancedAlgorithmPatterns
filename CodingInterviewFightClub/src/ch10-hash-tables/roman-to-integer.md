# 10.7 Roman To Integer

> **Source:** [`src/main/kotlin/hashtable/RomanToInteger.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/hashtable/RomanToInteger.kt)
> **Pattern:** right-to-left accumulation · **Core page**

## The Problem

Given a valid Roman numeral string, return its integer value. Roman numerals use `I(1) V(5) X(10) L(50) C(100) D(500) M(1000)`, with the *subtractive rule*: a smaller numeral before a larger one subtracts (`IV` = 4, `IX` = 9, `XL` = 40, ...).

- Constraints: $1 \le n \le 15$; valid Roman numerals in `[1, 3999]`.

## Examples

```
Input:  s = "III"       -> Output: 3
Input:  s = "LVIII"     -> Output: 58    (50 + 5 + 3)
Input:  s = "MCMXCIV"   -> Output: 1994  (1000 + 900 + 90 + 4)
```

## Intuition — "bigger before smaller adds; smaller before bigger subtracts"

The brute force is a table of six subtractive pairs (`IV`, `IX`, `XL`, `XC`, `CD`, `CM`) — correct but clunky. The elegant observation: **scan right to left**, and the rule becomes *local*:

- if the current numeral's value is **>=** the value to its right (the "previous" in a right-to-left scan), **add** it;
- otherwise (a smaller numeral before a larger one — the subtractive case) **subtract** it.

`"MCMXCIV"` right-to-left: `V(5) add`, `I(1) < 5 subtract -> 4`, `C(100) add`, `X(10) < 100 subtract`, `M(1000) add`, `C(100) < 1000 subtract`, `M(1000) add` — total `1000 - 100 + 1000 - 10 + 100 - 1 + 5 = 1994`. One pass, one comparison per character.

**Why right-to-left and not left-to-right?** The subtractive case is decided by the *right* neighbor (`IV`: the `I` is special because `V` follows). Scanning right to left, the deciding value is already known — it's the previous iteration's value. Left-to-right requires *looking ahead*, which is the same information but expressed as a peek. Both work; right-to-left is the canonical form because the rule reads naturally ("if this is smaller than what's after it").

**The value table is the hash map:** the character-to-value mapping `I->1, V->5, ...` is exactly a lookup table — the [value-to-state](pattern-primer.md) move, here in its simplest form.

## Approach 1 — Handle the six subtractive pairs explicitly

Walk left to right; if the current pair is one of `IV IX XL XC CD CM`, add the pair's value and skip two characters, else add the single character: correct, but six special cases and a look-ahead.

## Approach 2 — Right-to-left with prev-value comparison (the repo's version, optimal)

```kotlin
class RomanToInteger {
    /**
     * @param s a valid Roman numeral
     * @return  the integer value
     */
    fun romanToInt(s: String): Int {
        val romanMap = mapOf(
            'I' to 1, 'V' to 5, 'X' to 10, 'L' to 50,
            'C' to 100, 'D' to 500, 'M' to 1000
        )

        var sum = 0
        var prevVal = 0
        for (i in s.length - 1 downTo 0) {        // right to left
            val current = romanMap[s[i]]!!

            if (current >= prevVal) sum += current      // normal: add
            else sum -= current                         // subtractive: subtract

            prevVal = current
        }
        return sum
    }
}
```

```java
import java.util.*;

public class RomanToInteger {
    private static final Map<Character, Integer> VALUES = Map.of(
        'I', 1, 'V', 5, 'X', 10, 'L', 50, 'C', 100, 'D', 500, 'M', 1000);

    /**
     * @param s a valid Roman numeral
     * @return  the integer value
     */
    public int romanToInt(String s) {
        int sum = 0;
        int prev = 0;

        for (int i = s.length() - 1; i >= 0; i--) {    // right to left
            int cur = VALUES.get(s.charAt(i));
            if (cur >= prev) sum += cur;               // normal: add
            else sum -= cur;                           // subtractive: subtract
            prev = cur;
        }
        return sum;
    }
}
```

```cpp
#include <string>
#include <unordered_map>

class RomanToInteger {
    const std::unordered_map<char, int> VALUES = {
        {'I', 1}, {'V', 5}, {'X', 10}, {'L', 50},
        {'C', 100}, {'D', 500}, {'M', 1000}
    };

public:
    /**
     * @param s a valid Roman numeral
     * @return  the integer value
     */
    int romanToInt(std::string s) {
        int sum = 0;
        int prev = 0;

        for (int i = (int)s.size() - 1; i >= 0; i--) {  // right to left
            int cur = VALUES.at(s[i]);
            if (cur >= prev) sum += cur;               // normal: add
            else sum -= cur;                           // subtractive: subtract
            prev = cur;
        }
        return sum;
    }
};
```

```python
def roman_to_int(s: str) -> int:
    """
    @param s: a valid Roman numeral
    @return:  the integer value
    """
    values = {"I": 1, "V": 5, "X": 10, "L": 50, "C": 100, "D": 500, "M": 1000}

    total = 0
    prev = 0
    for c in reversed(s):            # right to left
        cur = values[c]
        if cur >= prev:
            total += cur             # normal: add
        else:
            total -= cur             # subtractive: subtract
        prev = cur
    return total
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param s a valid Roman numeral
    /// @return  the integer value
    pub fn roman_to_int(s: String) -> i32 {
        let values: HashMap<char, i32> = [
            ('I', 1), ('V', 5), ('X', 10), ('L', 50),
            ('C', 100), ('D', 500), ('M', 1000),
        ].into_iter().collect();

        let mut total = 0;
        let mut prev = 0;
        for c in s.chars().rev() {       // right to left
            let cur = values[&c];
            if cur >= prev { total += cur; }   // normal: add
            else { total -= cur; }             // subtractive: subtract
            prev = cur;
        }
        total
    }
}
```

## Dry run

**Input:** `s = "MCMXCIV"`.

```
values: M=1000 C=100 X=10 I=1 V=5

right to left:
'V' (5)   >= prev 0    -> sum = 5.        prev=5
'I' (1)   <  5         -> sum = 4.        prev=1   (subtractive: IV = 4)
'C' (100) >= 1         -> sum = 104.      prev=100
'X' (10)  <  100       -> sum = 94.       prev=10   (subtractive: XC = 90)
'M' (1000) >= 10       -> sum = 1094.     prev=1000
'C' (100) <  1000      -> sum = 994.      prev=100   (subtractive: CM = 900)
'M' (1000) >= 100      -> sum = 1994.     prev=1000

Output: 1994 ✓
```

Watch how each `I`/`X`/`C` in subtractive position flips its sign purely from the *previous* (right-hand) value — no pair table, no look-ahead. The single comparison `cur >= prev` encodes the entire subtractive rule.

## Complexity

**Time.** One pass over the string:

$$
T(n) = O(n)
$$

**Space.** The constant value table:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Integer To Roman** (`src/main/kotlin/hashtable/IntegerToRoman.kt`) — the reverse direction: greedily subtract from a descending value table (the "largest-fit" loop); the mirror of this page.
- **Integer To English Words** (`src/main/kotlin/array/hashtable/IntegerToEnglishWords.kt`) — the same table-driven decomposition at scale (thousands/millions tiers).
- **Excel Sheet Column Number** (`src/main/kotlin/string/ExcelSheetToColumnNumber.kt`) — a base-26 numeral system without the subtractive quirk: the "characters are values" idea without the sign flip.
- **Interview follow-up:** "Why does `cur >= prev` (not `>`) work for the add case?" Equal values can only repeat in a *non*-subtractive position (`"III"`: each `I` equals the previous, all add). The subtractive rule is strictly `smaller before larger`, so `>=` is exactly "not subtractive". A `>` would mis-evaluate any repeated run.
