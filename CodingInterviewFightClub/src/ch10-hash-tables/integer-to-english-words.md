# 10.14 Integer To English Words

> **Source:** [`src/main/kotlin/array/hashtable/IntegerToEnglishWords.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/IntegerToEnglishWords.kt)
> **Pattern:** digit-table recursion over 1000-blocks · **Core page**

## The Problem

Convert a non-negative integer to its English words representation.

- Constraints: $0 \le num \le 2^{31} - 1$.

## Examples

```
Input:  num = 123        -> Output: "One Hundred Twenty Three"
Input:  num = 1234567    -> Output: "One Million Two Hundred Thirty Four Thousand Five Hundred Sixty Seven"
Input:  num = 0          -> Output: "Zero"
```

## Intuition — every 1000-block repeats the same ≤999 converter

English groups digits in thousands: `num = 123 | 456 | 789` → "123 Million 456 Thousand 789". So:

1. **The `dfs(num)` helper** converts any `num < 1000` — "X Hundred YZ" with the `lessThan20` / `tens` tables;
2. **The main loop** peels `num % 1000` blocks, tagging each with `Thousand / Million / Billion`.

```kotlin
private val lessThan20 = arrayOf("", "One", ..., "Nineteen")
private val tens = arrayOf("", "", "Twenty", ..., "Ninety")
private val thousands = arrayOf("", "Thousand", "Million", "Billion")

fun numberToWords(num: Int): String {
    if (num == 0) return "Zero"

    var n = num; var result = StringBuilder(); var i = 0
    while (n > 0) {
        if (n % 1000 != 0) {
            result.insert(0, "${dfs(n % 1000)} ${thousands[i]} ")
        }
        n /= 1000; i++
    }
    return result.toString().trim()
}

fun dfs(num: Int): String = when {
    num == 0 -> ""
    num < 20 -> lessThan20[num]
    num < 100 -> "${tens[num / 10]} ${lessThan20[num % 10]}".trim()
    else -> "${lessThan20[num / 100]} Hundred ${dfs(num % 100)}".trim()
}
```

**Why `result.insert(0, ...)`?** The loop peels blocks least-significant-first (`% 1000`), but English reads most-significant-first — inserting at the front assembles the string in the right order without a final reversal. The `n % 1000 != 0` guard skips empty blocks ("1,000,000" needs no "Zero Thousand").

**Why is `num < 20` special?** 11–19 are irregular words ("Eleven", not "Ten One") — a table entry instead of composition. The `tens` table handles 20–90; `Hundred` composition handles the top.

## Approach 1 — Iterate with `% 1000` blocks (the repo's version, optimal)

```kotlin
class IntegerToEnglishWords {
    private val lessThan20 = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    )
    private val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty",
        "Sixty", "Seventy", "Eighty", "Ninety")
    private val thousands = arrayOf("", "Thousand", "Million", "Billion")

    /**
     * @param num non-negative integer
     * @return    English words representation
     */
    fun numberToWords(num: Int): String {
        if (num == 0) return "Zero"

        var n = num
        var result = StringBuilder()
        var i = 0

        while (n > 0) {
            if (n % 1000 != 0) {
                result.insert(0, "${dfs(n % 1000)} ${thousands[i]} ")
            }
            n /= 1000
            i++
        }
        return result.toString().trim()
    }

    private fun dfs(num: Int): String {
        if (num == 0) return ""
        if (num < 20) return lessThan20[num]
        if (num < 100) return "${tens[num / 10]} ${lessThan20[num % 10]}".trim()
        return "${lessThan20[num / 100]} Hundred ${dfs(num % 100)}".trim()
    }
}
```

```java
public class IntegerToEnglishWords {
    private static final String[] LESS20 = {"", "One", "Two", "Three", "Four", "Five", "Six",
        "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
        "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    private static final String[] TENS = {"", "", "Twenty", "Thirty", "Forty", "Fifty",
        "Sixty", "Seventy", "Eighty", "Ninety"};
    private static final String[] THOUSANDS = {"", "Thousand", "Million", "Billion"};

    /**
     * @param num non-negative integer
     * @return    English words representation
     */
    public String numberToWords(int num) {
        if (num == 0) return "Zero";

        StringBuilder result = new StringBuilder();
        int i = 0;
        while (num > 0) {
            if (num % 1000 != 0) {
                result.insert(0, dfs(num % 1000) + " " + THOUSANDS[i] + " ");
            }
            num /= 1000;
            i++;
        }
        return result.toString().trim();
    }

    private String dfs(int num) {
        if (num == 0) return "";
        if (num < 20) return LESS20[num];
        if (num < 100) return (TENS[num / 10] + " " + LESS20[num % 10]).trim();
        return (LESS20[num / 100] + " Hundred " + dfs(num % 100)).trim();
    }
}
```

```cpp
#include <string>

class IntegerToEnglishWords {
    const char* less20[20] = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
        "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
        "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    const char* tens[10] = {"", "", "Twenty", "Thirty", "Forty", "Fifty",
        "Sixty", "Seventy", "Eighty", "Ninety"};
    const char* thousands[4] = {"", "Thousand", "Million", "Billion"};

    std::string dfs(int num) {
        if (num == 0) return "";
        if (num < 20) return less20[num];
        if (num < 100) return std::string(tens[num / 10]) + " " + less20[num % 10];
        return std::string(less20[num / 100]) + " Hundred " + dfs(num % 100);
    }

public:
    /**
     * @param num non-negative integer
     * @return    English words representation
     */
    std::string numberToWords(int num) {
        if (num == 0) return "Zero";

        std::string result;
        int i = 0;
        while (num > 0) {
            if (num % 1000 != 0) {
                result = dfs(num % 1000) + " " + thousands[i] + " " + result;
            }
            num /= 1000;
            i++;
        }
        // trim the trailing space
        while (!result.empty() && result.back() == ' ') result.pop_back();
        return result;
    }
};
```

```python
LESS20 = ["", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
          "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
          "Seventeen", "Eighteen", "Nineteen"]
TENS = ["", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"]
THOUSANDS = ["", "Thousand", "Million", "Billion"]


def number_to_words(num: int) -> str:
    """
    @param num: non-negative integer
    @return:    English words representation
    """
    def dfs(n: int) -> str:
        if n == 0:
            return ""
        if n < 20:
            return LESS20[n]
        if n < 100:
            return (TENS[n // 10] + " " + LESS20[n % 10]).strip()
        return (LESS20[n // 100] + " Hundred " + dfs(n % 100)).strip()

    if num == 0:
        return "Zero"

    result = []
    i = 0
    while num > 0:
        if num % 1000 != 0:
            result.insert(0, dfs(num % 1000) + " " + THOUSANDS[i])
        num //= 1000
        i += 1

    return " ".join(result).strip()
```

```rust
impl Solution {
    /// @param num non-negative integer
    /// @return    English words representation
    pub fn number_to_words(num: i32) -> String {
        const LESS20: [&str; 20] = ["", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen"];
        const TENS: [&str; 10] = ["", "", "Twenty", "Thirty", "Forty", "Fifty",
            "Sixty", "Seventy", "Eighty", "Ninety"];
        const THOUSANDS: [&str; 4] = ["", "Thousand", "Million", "Billion"];

        fn dfs(n: i32, less20: &[&str; 20], tens: &[&str; 10]) -> String {
            if n == 0 { return String::new(); }
            if n < 20 { return less20[n as usize].to_string(); }
            if n < 100 {
                return format!("{} {}", tens[(n / 10) as usize], less20[(n % 10) as usize]).trim().to_string();
            }
            format!("{} Hundred {}", less20[(n / 100) as usize], dfs(n % 100, less20, tens))
                .trim().to_string()
        }

        if num == 0 { return "Zero".to_string(); }

        let mut result = String::new();
        let mut n = num;
        let mut i = 0;
        while n > 0 {
            if n % 1000 != 0 {
                result = format!("{} {} {}", dfs(n % 1000, &LESS20, &TENS), THOUSANDS[i], result)
                    .trim().to_string();
            }
            n /= 1000;
            i += 1;
        }
        result
    }
}
```

## Dry run

**Input:** `num = 1234567`.

```
n = 1234567, i = 0
block 7:    n % 1000 = 567 -> dfs(567) = "Five Hundred Sixty Seven".  insert "Five Hundred Sixty Seven " (thousands[0] = "")
            n = 1234, i = 1
block 8:    n % 1000 = 234 -> dfs(234) = "Two Hundred Thirty Four".   insert "Two Hundred Thirty Four Thousand "
            n = 1, i = 2
block 9:    n % 1000 = 1   -> dfs(1) = "One".                          insert "One Million "

result.trim() = "One Million Two Hundred Thirty Four Thousand Five Hundred Sixty Seven" ✓
```

The `insert(0, ...)` front-assembly is the whole ordering trick: blocks are peeled least-significant-first, but each `insert(0)` places them most-significant-first — "One Million" lands at the front, "567" at the back. The `n % 1000 != 0` guard skips the middle "000" block in numbers like 1,000,123.

## Complexity

**Time.** Four blocks max, O(1) each:

$$
T = O(1)
$$

**Space.** The result string:

$$
S = O(1)
$$

## Variants & follow-ups

- **Roman To Integer / Integer To Roman** ([10.7](roman-to-integer.md), `hashtable/IntegerToRoman.kt`) — the numeral-conversion family this page's table-driven recursion joins.
- **Valid Number** (`string/ValidNumber.kt`) — the parsing inverse: decide if a string is a number.
- **Interview follow-up:** "Why three tables?" `lessThan20` covers the irregular teens; `tens` covers 20–90 compositions; `thousands` tags the blocks. The recursion's base cases map 1:1 to the tables — the table sizes *are* the English grammar.
