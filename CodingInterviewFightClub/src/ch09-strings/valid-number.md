# 9.31 Valid Number

> **Source**: [`src/main/kotlin/string/ValidNumber.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/ValidNumber.kt)
> **Pattern**: state-machine scan · **Core page**

## The Problem

Is `s` a valid decimal number (signs, digits, dot, e/E)?

- Constraints: n ≤ 20.

## Examples

```
Input:  "0"       -> true.  "e" -> false.  "." -> false.
Input:  "2e10"    -> true.  "1e" -> false.
```

## Intuition — track the four flags in one scan

```kotlin
var (hasNum, hasDot, hasE, hasDigitsAfterE) = listOf(false, false, false, false)
val str = s.trim()

for (i in str.indices) {
    val c = str[i]

    when {
        c.isDigit() -> { hasNum = true; hasDigitsAfterE = true }
        c == '.' -> {
            if (hasDot || hasE) return false
            hasDot = true
        }
        c == 'e' || c == 'E' -> {
            if (hasE || !hasNum) return false
            hasE = true
            hasDigitsAfterE = false
        }
        c == '+' || c == '-' -> {
            if (i > 0 && str[i - 1] != 'e' && str[i - 1] != 'E') return false
        }
        else -> return false
    }
}
return hasNum && hasDigitsAfterE
```

## Approach 1 — Flag scan (the repo's version, optimal)

```kotlin
class ValidNumber {
    /**
     * @param s input string
     * @return  true iff a valid number
     */
    fun isNumber(s: String): Boolean {
        var (hasNum, hasDot, hasE, hasDigitsAfterE) = listOf(false, false, false, false)

        val str = s.trim()
        for (i in str.indices) {
            val c = str[i]

            when {
                c.isDigit() -> {
                    hasNum = true
                    hasDigitsAfterE = true
                }
                c == '.' -> {
                    if (hasDot || hasE) return false
                    hasDot = true
                }
                c == 'e' || c == 'E' -> {
                    if (hasE || !hasNum) return false
                    hasE = true
                    hasDigitsAfterE = false
                }
                c == '+' || c == '-' -> {
                    if (i > 0 && str[i - 1] != 'e' && str[i - 1] != 'E') return false
                }
                else -> return false
            }
        }
        return hasNum && hasDigitsAfterE
    }
}
```

```java
public class ValidNumber {
    /**
     * @param s input string
     * @return  true iff a valid number
     */
    public boolean isNumber(String s) {
        boolean hasNum = false, hasDot = false, hasE = false, digitsAfterE = false;
        s = s.trim();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                hasNum = true;
                digitsAfterE = true;
            } else if (c == '.') {
                if (hasDot || hasE) return false;
                hasDot = true;
            } else if (c == 'e' || c == 'E') {
                if (hasE || !hasNum) return false;
                hasE = true;
                digitsAfterE = false;
            } else if (c == '+' || c == '-') {
                if (i > 0 && s.charAt(i - 1) != 'e' && s.charAt(i - 1) != 'E') return false;
            } else {
                return false;
            }
        }
        return hasNum && digitsAfterE;
    }
}
```

```cpp
#include <string>
#include <cctype>

class ValidNumber {
public:
    /**
     * @param s input string
     * @return  true iff a valid number
     */
    bool isNumber(std::string s) {
        bool hasNum = false, hasDot = false, hasE = false, digitsAfterE = false;

        size_t start = s.find_first_not_of(' ');
        size_t end = s.find_last_not_of(' ');
        if (start == std::string::npos) return false;
        s = s.substr(start, end - start + 1);

        for (size_t i = 0; i < s.size(); i++) {
            char c = s[i];

            if (std::isdigit(c)) { hasNum = true; digitsAfterE = true; }
            else if (c == '.') {
                if (hasDot || hasE) return false;
                hasDot = true;
            }
            else if (c == 'e' || c == 'E') {
                if (hasE || !hasNum) return false;
                hasE = true;
                digitsAfterE = false;
            }
            else if (c == '+' || c == '-') {
                if (i > 0 && s[i - 1] != 'e' && s[i - 1] != 'E') return false;
            }
            else return false;
        }
        return hasNum && digitsAfterE;
    }
};
```

```python
def is_number(s: str) -> bool:
    """
    @param s: input string
    @return:  true iff a valid number
    """
    has_num = has_dot = has_e = digits_after_e = False
    s = s.strip()

    for i, c in enumerate(s):
        if c.isdigit():
            has_num = True
            digits_after_e = True
        elif c == ".":
            if has_dot or has_e:
                return False
            has_dot = True
        elif c in "eE":
            if has_e or not has_num:
                return False
            has_e = True
            digits_after_e = False
        elif c in "+-":
            if i > 0 and s[i - 1] not in "eE":
                return False
        else:
            return False

    return has_num and digits_after_e
```

```rust
impl Solution {
    /// @param s input string
    /// @return  true iff a valid number
    pub fn is_number(s: String) -> bool {
        let s = s.trim();
        let mut has_num = false;
        let mut has_dot = false;
        let mut has_e = false;
        let mut digits_after_e = false;

        for (i, c) in s.chars().enumerate() {
            if c.is_ascii_digit() {
                has_num = true;
                digits_after_e = true;
            } else if c == '.' {
                if has_dot || has_e { return false; }
                has_dot = true;
            } else if c == 'e' || c == 'E' {
                if has_e || !has_num { return false; }
                has_e = true;
                digits_after_e = false;
            } else if c == '+' || c == '-' {
                if i > 0 && !matches!(s.chars().nth(i - 1), Some('e') | Some('E')) { return false; }
            } else {
                return false;
            }
        }
        has_num && digits_after_e
    }
}
```

## Dry run

**Input:** `"2e10"`.

```
2: hasNum, digitsAfterE.  e: hasE ok.  1,0: digitsAfterE.
Output: true ✓
Input: "1e": e: hasE ok.  end: digitsAfterE false -> false ✓
Input: ".": dot only.  hasNum false -> false ✓
```

## Complexity

**Time.** One scan:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **String To Integer** ([9.14](string-to-integer-atoi.md)) — the parsing sibling.
- **Interview follow-up:** "Why does `hasDigitsAfterE` reset?" `1e` is invalid — the exponent must have digits; the reset flag tracks exactly that.
