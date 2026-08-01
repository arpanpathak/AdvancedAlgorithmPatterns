# 9.35 Excel Sheet Column Number

> **Source**: [`src/main/kotlin/string/ExcelSheetToColumnNumber.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/ExcelSheetToColumnNumber.kt)
> **Pattern**: base-26 decoding · **Core page**

## The Problem

"AB" → 28 (A=1, B=2, ... base-26 without zero).

- Constraints: n ≤ 7.

## Examples

```
Input:  columnTitle = "AB"   -> Output: 28
Input:  columnTitle = "ZY"   -> Output: 701
```

## Intuition — Horner's rule in base 26

```kotlin
var base = 1
var sum = 0

for (i in columnTitle.length - 1 downTo 0) {
    sum += (columnTitle[i] - 'A' + 1) * base
    base *= 26
}
return sum
```

## Approach 1 — Horner decode (the repo's version, optimal)

## Approach 2 — Forward Horner (cleaner)

`sum = sum * 26 + (c - 'A' + 1)` left to right.

```kotlin
class ExcelSheetToColumnNumber {
    /**
     * @param columnTitle column title
     * @return            column number
     */
    fun titleToNumber(columnTitle: String): Int {
        var result = 0

        for (ch in columnTitle) {
            result = result * 26 + (ch - 'A' + 1)
        }
        return result
    }
}
```

```java
public class ExcelSheetColumnNumber {
    /**
     * @param columnTitle column title
     * @return            column number
     */
    public int titleToNumber(String columnTitle) {
        int result = 0;

        for (char c : columnTitle.toCharArray()) {
            result = result * 26 + (c - 'A' + 1);
        }
        return result;
    }
}
```

```cpp
#include <string>

class ExcelSheetColumnNumber {
public:
    /**
     * @param columnTitle column title
     * @return            column number
     */
    int titleToNumber(std::string columnTitle) {
        int result = 0;

        for (char c : columnTitle) {
            result = result * 26 + (c - 'A' + 1);
        }
        return result;
    }
};
```

```python
def title_to_number(column_title: str) -> int:
    """
    @param column_title: column title
    @return:             column number
    """
    result = 0

    for ch in column_title:
        result = result * 26 + (ord(ch) - ord("A") + 1)

    return result
```

```rust
impl Solution {
    /// @param column_title column title
    /// @return             column number
    pub fn title_to_number(column_title: String) -> i32 {
        column_title.bytes().fold(0, |acc, b| acc * 26 + (b - b'A' + 1) as i32)
    }
}
```

## Dry run

**Input:** `columnTitle = "AB"`.

```
A: 0*26+1 = 1.  B: 1*26+2 = 28.
Output: 28 ✓
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

- **Excel Sheet Column Title** — the inverse (encode).
- **Interview follow-up:** "Why is this not plain base-26?" There's no zero digit — 'A' is 1, not 0. Horner still works because each position contributes (c−A+1) at its weight.
