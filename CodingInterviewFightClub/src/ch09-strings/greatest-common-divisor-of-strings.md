# 9.30 Greatest Common Divisor Of Strings

> **Source**: [`src/main/kotlin/string/GreatestCommonDivisorOfStrings.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/GreatestCommonDivisorOfStrings.kt)
> **Pattern**: string division · **Core page**

## The Problem

The largest string dividing both `str1` and `str2` (by repetition).

- Constraints: lengths ≤ 1000.

## Examples

```
Input:  str1 = "ABCABC", str2 = "ABC"   -> Output: "ABC"
Input:  str1 = "ABABAB", str2 = "ABAB"  -> Output: "AB"
```

## Intuition — if `str1 + str2 != str2 + str1`, no common divisor; else the gcd length

```kotlin
fun gcd(a: String, b: String): String = when {
    a == b -> a
    a > b -> gcd(a - b, b)
    else -> gcd(a, b - a)
}

fun gcdOfStrings(str1: String, str2: String): String {
    if (str1 + str2 != str2 + str1) return ""

    return str1.substring(0, gcdOf(str1.length, str2.length))
}
```

**Why the concatenation test?** If a common divisor X exists, both strings are repetitions of X — so `str1 + str2` and `str2 + str1` are the same repetition pattern. The gcd length then slices the answer.

## Approach 1 — Concatenation test + numeric gcd (the repo's version, optimal)

```kotlin
class GreatestCommonDivisorOfStrings {
    /**
     * @param str1 first string
     * @param str2 second string
     * @return     largest common divisor string
     */
    fun gcdOfStrings(str1: String, str2: String): String {
        if (str1 + str2 != str2 + str1) return ""

        fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

        return str1.substring(0, gcd(str1.length, str2.length))
    }
}
```

```java
public class GreatestCommonDivisorOfStrings {
    /**
     * @param str1 first string
     * @param str2 second string
     * @return     largest common divisor string
     */
    public String gcdOfStrings(String str1, String str2) {
        if (!(str1 + str2).equals(str2 + str1)) return "";

        int g = gcd(str1.length(), str2.length());
        return str1.substring(0, g);
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
```

```cpp
#include <string>
#include <numeric>

class GreatestCommonDivisorOfStrings {
public:
    /**
     * @param str1 first string
     * @param str2 second string
     * @return     largest common divisor string
     */
    std::string gcdOfStrings(std::string str1, std::string str2) {
        if (str1 + str2 != str2 + str1) return "";

        return str1.substr(0, std::gcd(str1.size(), str2.size()));
    }
};
```

```python
import math

def gcd_of_strings(str1: str, str2: str) -> str:
    """
    @param str1: first string
    @param str2: second string
    @return:     largest common divisor string
    """
    if str1 + str2 != str2 + str1:
        return ""

    return str1[: math.gcd(len(str1), len(str2))]
```

```rust
impl Solution {
    /// @param str1 first string
    /// @param str2 second string
    /// @return     largest common divisor string
    pub fn gcd_of_strings(str1: String, str2: String) -> String {
        let concat = format!("{}{}", str1, str2);
        let concat2 = format!("{}{}", str2, str1);
        if concat != concat2 { return String::new(); }

        fn gcd(a: usize, b: usize) -> usize {
            if b == 0 { a } else { gcd(b, a % b) }
        }

        str1[..gcd(str1.len(), str2.len())].to_string()
    }
}
```

## Dry run

**Input:** `str1 = "ABABAB", str2 = "ABAB"`.

```
"ABABABABAB" == "ABABABABAB" ✓.  gcd(6,4) = 2.  str1[0..2] = "AB" ✓
Input: "ABCABC" + "ABC" vs "ABC" + "ABCABC": both "ABCABCABC" ✓.  gcd(6,3)=3 -> "ABC" ✓
Input: str1="LEET", str2="CODE": "LEETCODE" != "CODELEET" -> "" ✓
```

## Complexity

**Time.** gcd of lengths + concat:

$$
T = O(n + m)
$$

**Space.** The result:

$$
S = O(n + m)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does the concatenation equality decide existence?" A shared period X forces both strings to be X-repetitions — concatenating in either order yields the same repeated pattern iff such an X exists.
