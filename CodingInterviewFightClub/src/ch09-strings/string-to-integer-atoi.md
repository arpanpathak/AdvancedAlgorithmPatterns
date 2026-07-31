# 9.14 String To Integer (atoi)

> **Source:** [`src/main/kotlin/math/StringtoIntegerAtoi.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/StringtoIntegerAtoi.kt)
> **Pattern:** scanner with overflow guards · **Core page**

## The Problem

Parse `s` to an int: skip spaces, optional sign, digits; clamp to `Int` range.

- Constraints: $1 \le n \le 200$; printable chars.

## Examples

```
Input:  s = "   -42"      -> Output: -42
Input:  s = "4193 with words" -> Output: 4193
Input:  s = "words and 987"  -> Output: 0
Input:  s = "-91283472332" -> Output: -2147483648 (clamped)
```

## Intuition — four phases, one overflow pre-check

Trim spaces → read sign → accumulate digits → clamp. The overflow guard checks **before** multiplying:

```kotlin
while (i < s.length && s[i] == ' ') i++            // phase 1: spaces

if (i < s.length && s[i] in "+-") {                // phase 2: sign
    sign = if (s[i] == '-') -1 else 1
    i++
}

while (i < s.length && s[i].isDigit()) {           // phase 3: digits
    val digit = s[i] - '0'

    // phase 4: overflow pre-check
    if (number > Int.MAX_VALUE / 10 ||
        (number == Int.MAX_VALUE / 10 && digit > Int.MAX_VALUE % 10)) {
        return if (sign == 1) Int.MAX_VALUE else Int.MIN_VALUE
    }
    number = number * 10 + digit
    i++
}
return sign * number
```

**Why the two-condition pre-check?** `number * 10 + digit` can overflow before the result is inspected. The guard `number > MAX/10` (or `== MAX/10 && digit > 7`) detects the overflow *before* it happens — the [1.x](../ch01-binary-search/pattern-primer.md) "check before arithmetic" discipline.

**Why stop at the first non-digit?** The problem's grammar: digits accumulate until a non-digit ends the number — trailing words are ignored, but a leading non-digit yields 0.

## Approach 1 — Regex match (compact, slow)

`Regex("^\\s*([+-]?\\d+)").find(s)`: works, but the hand-rolled scanner is the interview answer.

## Approach 2 — Phase scanner with pre-check (the repo's version, optimal)

```kotlin
class StringtoIntegerAtoi {
    /**
     * @param s input string
     * @return  parsed integer (clamped)
     */
    fun myAtoi(s: String): Int {
        var sign = 1
        var number = 0
        var i = 0

        while (i < s.length && s[i] == ' ') i++

        if (i < s.length && s[i] in "+-") {
            sign = if (s[i] == '-') -1 else 1
            i++
        }

        while (i < s.length && s[i].isDigit()) {
            val digit = s[i] - '0'

            if (number > Int.MAX_VALUE / 10 ||
                (number == Int.MAX_VALUE / 10 && digit > Int.MAX_VALUE % 10)) {
                return if (sign == 1) Int.MAX_VALUE else Int.MIN_VALUE
            }
            number = number * 10 + digit
            i++
        }
        return sign * number
    }
}
```

```java
public class StringToIntegerAtoi {
    /**
     * @param s input string
     * @return  parsed integer (clamped)
     */
    public int myAtoi(String s) {
        int sign = 1, number = 0, i = 0;

        while (i < s.length() && s.charAt(i) == ' ') i++;

        if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
            sign = s.charAt(i) == '-' ? -1 : 1;
            i++;
        }

        while (i < s.length() && Character.isDigit(s.charAt(i))) {
            int digit = s.charAt(i) - '0';

            if (number > Integer.MAX_VALUE / 10 ||
                (number == Integer.MAX_VALUE / 10 && digit > Integer.MAX_VALUE % 10)) {
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
            number = number * 10 + digit;
            i++;
        }
        return sign * number;
    }
}
```

```cpp
#include <string>
#include <climits>

class StringToIntegerAtoi {
public:
    /**
     * @param s input string
     * @return  parsed integer (clamped)
     */
    int myAtoi(std::string s) {
        int sign = 1, number = 0, i = 0;

        while (i < (int)s.size() && s[i] == ' ') i++;

        if (i < (int)s.size() && (s[i] == '+' || s[i] == '-')) {
            sign = s[i] == '-' ? -1 : 1;
            i++;
        }

        while (i < (int)s.size() && std::isdigit(s[i])) {
            int digit = s[i] - '0';

            if (number > INT_MAX / 10 ||
                (number == INT_MAX / 10 && digit > INT_MAX % 10)) {
                return sign == 1 ? INT_MAX : INT_MIN;
            }
            number = number * 10 + digit;
            i++;
        }
        return sign * number;
    }
};
```

```python
def my_atoi(s: str) -> int:
    """
    @param s: input string
    @return:  parsed integer (clamped)
    """
    INT_MAX, INT_MIN = 2**31 - 1, -(2**31)

    i, n = 0, len(s)
    while i < n and s[i] == " ":
        i += 1

    sign = 1
    if i < n and s[i] in "+-":
        sign = -1 if s[i] == "-" else 1
        i += 1

    number = 0
    while i < n and s[i].isdigit():
        digit = int(s[i])

        if number > INT_MAX // 10 or (number == INT_MAX // 10 and digit > INT_MAX % 10):
            return INT_MAX if sign == 1 else INT_MIN
        number = number * 10 + digit
        i += 1

    return sign * number
```

```rust
impl Solution {
    /// @param s input string
    /// @return  parsed integer (clamped)
    pub fn my_atoi(s: String) -> i32 {
        let bytes = s.as_bytes();
        let mut i = 0usize;
        let n = bytes.len();

        while i < n && bytes[i] == b' ' { i += 1; }

        let mut sign = 1i64;
        if i < n && (bytes[i] == b'+' || bytes[i] == b'-') {
            sign = if bytes[i] == b'-' { -1 } else { 1 };
            i += 1;
        }

        let mut number: i64 = 0;
        while i < n && bytes[i].is_ascii_digit() {
            let digit = (bytes[i] - b'0') as i64;

            if number > i32::MAX as i64 / 10
                || (number == i32::MAX as i64 / 10 && digit > 7) {
                return if sign == 1 { i32::MAX } else { i32::MIN };
            }
            number = number * 10 + digit;
            i += 1;
        }
        (sign * number) as i32
    }
}
```

## Dry run

**Input:** `s = "  -91283472332"`.

```
spaces skipped: i=2.  sign: '-' -> sign=-1.  i=3.
digits:
  9: number=0, 0 > MAX/10? no.  (0 == 214748364 && 9 > 7) -> YES -> clamp!
  return sign == 1 ? MAX : MIN = MIN = -2147483648 ✓
```

The clamp fires on the first digit: `number == MAX/10` (0) and `digit > MAX%10` (9 > 7) — the number *would* be `0*10+9 = 9` which fits, but the pre-check is conservative at the boundary... actually for 0 it's `0 > 214748364`? no, `number == 214748364`? no (0). So it proceeds: number=9. Next 1: 9 → 91. ... until number = 912834723: `912834723 == 214748364`? no (it's larger) → `number > MAX/10` → clamp. The clamp triggers correctly at the first overflowing digit. `"4193 with words"`: digits 4193 then space stops → 4193 ✓.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Valid Number** (`string/ValidNumber.kt`) — the parsing inverse (decide, don't convert).
- **Add Strings** ([9.13](add-strings.md)) — digit extraction without conversion.
- **Interview follow-up:** "Why check `number > MAX/10` *before* multiplying?" `number * 10 + digit` computed first would already have overflowed — the wrapped value could pass any post-hoc check. The pre-check compares the *about-to-multiply* state against the safe bound; it's the only place the overflow is visible.
