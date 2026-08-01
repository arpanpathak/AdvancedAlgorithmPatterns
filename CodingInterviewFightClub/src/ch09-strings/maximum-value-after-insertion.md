# 9.36 Maximum Value After Insertion

> **Source**: [`src/main/kotlin/string/MaximumValueAfterInsertion.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/MaximumValueAfterInsertion.kt)
> **Pattern**: insertion position scan · **Core page**

## The Problem

Insert digit `x` into `n` (a string, possibly negative) to maximize the value.

- Constraints: n ≤ 10⁵ digits.

## Examples

```
Input:  n = "99", x = 9    -> Output: "999"
Input:  n = "-13", x = 2   -> Output: "-123"
```

## Intuition — insert before the first digit smaller (positive) / larger (negative)

```kotlin
val isNegative = n[0] == '-'
val xChar = x.digitToChar()

val insertIndex = when (isNegative) {
    true -> (1 until n.length).firstOrNull { n[it] > xChar }
    false -> n.indices.firstOrNull { n[it] < xChar }
} ?: n.length

return n.substring(0, insertIndex) + xChar + n.substring(insertIndex)
```

## Approach 1 — Position scan (the repo's version, optimal)

```kotlin
class MaximumValueAfterInsertion {
    /**
     * @param n number string
     * @param x digit to insert
     * @return  maximized string
     */
    fun maxValue(n: String, x: Int): String {
        val isNegative = n[0] == '-'
        val xChar = x.digitToChar()

        val insertIndex = when (isNegative) {
            true -> (1 until n.length).firstOrNull { n[it] > xChar }
            false -> n.indices.firstOrNull { n[it] < xChar }
        } ?: n.length

        return n.substring(0, insertIndex) + xChar + n.substring(insertIndex)
    }
}
```

```java
public class MaximumValueAfterInsertion {
    /**
     * @param n number string
     * @param x digit to insert
     * @return  maximized string
     */
    public String maxValue(String n, int x) {
        boolean negative = n.charAt(0) == '-';
        char xc = (char) ('0' + x);

        int index = n.length();
        int start = negative ? 1 : 0;

        for (int i = start; i < n.length(); i++) {
            boolean better = negative ? n.charAt(i) > xc : n.charAt(i) < xc;
            if (better) {
                index = i;
                break;
            }
        }

        return n.substring(0, index) + xc + n.substring(index);
    }
}
```

```cpp
#include <string>

class MaximumValueAfterInsertion {
public:
    /**
     * @param n number string
     * @param x digit to insert
     * @return  maximized string
     */
    std::string maxValue(std::string n, int x) {
        bool negative = n[0] == '-';
        char xc = '0' + x;

        int index = n.size();
        int start = negative ? 1 : 0;

        for (int i = start; i < (int)n.size(); i++) {
            bool better = negative ? n[i] > xc : n[i] < xc;
            if (better) { index = i; break; }
        }

        return n.substr(0, index) + xc + n.substr(index);
    }
};
```

```python
def max_value(n: str, x: int) -> str:
    """
    @param n: number string
    @param x: digit to insert
    @return:  maximized string
    """
    negative = n[0] == "-"
    xc = str(x)
    start = 1 if negative else 0

    for i in range(start, len(n)):
        if (n[i] > xc) if negative else (n[i] < xc):
            return n[:i] + xc + n[i:]

    return n + xc
```

```rust
impl Solution {
    /// @param n number string
    /// @param x digit to insert
    /// @return  maximized string
    pub fn max_value(n: String, x: i32) -> String {
        let negative = n.starts_with('-');
        let xc = char::from_digit(x as u32, 10).unwrap();
        let start = if negative { 1 } else { 0 };

        for (i, c) in n.chars().enumerate().skip(start) {
            let better = if negative { c > xc } else { c < xc };
            if better {
                return format!("{}{}{}", &n[..i], xc, &n[i..]);
            }
        }
        format!("{}{}", n, xc)
    }
}
```

## Dry run

**Input:** `n = "-13", x = 2`.

```
negative.  scan from 1: '1' > '2'? no.  '3' > '2'? yes -> index 2.
"-1" + "2" + "3" = "-123" ✓
```

## Complexity

**Time.** One scan:

$$
T(n) = O(n)
$$

**Space.** The result:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does the sign flip the comparison?" For positive numbers, inserting a bigger digit earlier is better (999 vs 9999 style) — for negatives, the smaller magnitude wins, so insert before the first *larger* digit.
