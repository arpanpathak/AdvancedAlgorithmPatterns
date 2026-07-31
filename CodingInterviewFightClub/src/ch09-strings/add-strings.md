# 9.13 Add Strings

> **Source:** [`src/main/kotlin/math/AddStrings.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/AddStrings.kt)
> **Pattern:** digit-wise addition without conversion · **Core page**

## The Problem

Add two **non-negative integer strings** (`num1`, `num2`) without converting to integers (they can be huge).

- Constraints: $1 \le$ length ≤ 10⁴; digits only.

## Examples

```
Input:  num1 = "11", num2 = "123"   -> Output: "134"
Input:  num1 = "456", num2 = "77"   -> Output: "533"
```

## Intuition — the [4.7](../ch04-linked-lists/add-two-numbers.md) carry loop, on strings

Identical to the linked-list addition — right-to-left, `% 10` / `/ 10` carry — except digits come from `char - '0'` and the result is a `StringBuilder` (built reversed, then reversed back):

```kotlin
var (i, j) = num1.length - 1 to num2.length - 1
var carry = 0
val sb = StringBuilder()

while (i >= 0 || j >= 0 || carry != 0) {
    val digit1 = if (i >= 0) num1[i--] - '0' else 0
    val digit2 = if (j >= 0) num2[j--] - '0' else 0
    val sum = digit1 + digit2 + carry
    carry = sum / 10
    sb.append(sum % 10)
}
return sb.reverse().toString()
```

**Why `char - '0'`?** `'5' - '0'` = 5 — the ASCII-offset digit extraction, the string-side twin of `node.val`. The `if (i >= 0)` elvis gives missing digits the value 0.

**Why build reversed then `reverse()`?** Digits are appended least-significant first; the string must be most-significant first. One final reversal fixes the order — the same "accumulate backward, flip once" pattern as [4.7](../ch04-linked-lists/add-two-numbers.md)'s dummy-head (there the order is native, here it isn't).

## Approach 1 — `BigInteger` / `toLong()` (overflow!)

Convert and add: breaks on 10⁴-digit inputs — the problem's hidden constraint.

## Approach 2 — Digit-wise carry (the repo's version, optimal)

```kotlin
class AddStrings {
    /**
     * @param num1 first number as a string
     * @param num2 second number as a string
     * @return     their sum as a string
     */
    fun addStrings(num1: String, num2: String): String {
        val sb = StringBuilder()
        var (i, j) = num1.length - 1 to num2.length - 1
        var carry = 0

        while (i >= 0 || j >= 0 || carry != 0) {
            val digit1 = if (i >= 0) num1[i--] - '0' else 0
            val digit2 = if (j >= 0) num2[j--] - '0' else 0

            val sum = digit1 + digit2 + carry
            carry = sum / 10
            sb.append(sum % 10)
        }
        return sb.reverse().toString()
    }
}
```

```java
public class AddStrings {
    /**
     * @param num1 first number as a string
     * @param num2 second number as a string
     * @return     their sum as a string
     */
    public String addStrings(String num1, String num2) {
        StringBuilder sb = new StringBuilder();
        int i = num1.length() - 1, j = num2.length() - 1, carry = 0;

        while (i >= 0 || j >= 0 || carry != 0) {
            int d1 = i >= 0 ? num1.charAt(i--) - '0' : 0;
            int d2 = j >= 0 ? num2.charAt(j--) - '0' : 0;
            int sum = d1 + d2 + carry;
            carry = sum / 10;
            sb.append(sum % 10);
        }
        return sb.reverse().toString();
    }
}
```

```cpp
#include <string>
#include <algorithm>

class AddStrings {
public:
    /**
     * @param num1 first number as a string
     * @param num2 second number as a string
     * @return     their sum as a string
     */
    std::string addStrings(std::string num1, std::string num2) {
        std::string result;
        int i = num1.size() - 1, j = num2.size() - 1, carry = 0;

        while (i >= 0 || j >= 0 || carry) {
            int d1 = i >= 0 ? num1[i--] - '0' : 0;
            int d2 = j >= 0 ? num2[j--] - '0' : 0;
            int sum = d1 + d2 + carry;
            carry = sum / 10;
            result += (char)('0' + sum % 10);
        }
        std::reverse(result.begin(), result.end());
        return result;
    }
};
```

```python
def add_strings(num1: str, num2: str) -> str:
    """
    @param num1: first number as a string
    @param num2: second number as a string
    @return:     their sum as a string
    """
    i, j = len(num1) - 1, len(num2) - 1
    carry = 0
    result = []

    while i >= 0 or j >= 0 or carry:
        d1 = int(num1[i]) if i >= 0 else 0
        d2 = int(num2[j]) if j >= 0 else 0
        total = d1 + d2 + carry
        carry = total // 10
        result.append(str(total % 10))
        i -= 1
        j -= 1

    return "".join(reversed(result))
```

```rust
impl Solution {
    /// @param num1 first number as a string
    /// @param num2 second number as a string
    /// @return     their sum as a string
    pub fn add_strings(num1: String, num2: String) -> String {
        let (b1, b2) = (num1.as_bytes(), num2.as_bytes());
        let (mut i, mut j) = (b1.len() as i32 - 1, b2.len() as i32 - 1);
        let mut carry = 0u8;
        let mut result = String::new();

        while i >= 0 || j >= 0 || carry > 0 {
            let d1 = if i >= 0 { b1[i as usize] - b'0' } else { 0 };
            let d2 = if j >= 0 { b2[j as usize] - b'0' } else { 0 };
            let sum = d1 + d2 + carry;
            carry = sum / 10;
            result.push((b'0' + sum % 10) as char);
            i -= 1;
            j -= 1;
        }
        result.chars().rev().collect()
    }
}
```

## Dry run

**Input:** `num1 = "456"`, `num2 = "77"`.

```
i=2 ('6'), j=1 ('7'), carry=0: sum = 6+7+0 = 13.  carry=1.  sb="3".  i=1, j=0
i=1 ('5'), j=0 ('7'), carry=1: sum = 5+7+1 = 13.  carry=1.  sb="33".  i=0, j=-1
i=0 ('4'), j<0,     carry=1: sum = 4+0+1 = 5.   carry=0.  sb="335".  i=-1
i<0, j<0, carry=0 -> stop.  reverse: "533" ✓
```

The carry propagation through the unequal lengths is the whole story: the `if (j >= 0)` elvis gives the missing tens-digit a 0, and the final `carry = 1` (from 13) rolls into the hundreds. `"11" + "123"` → `"134"` the same way. `BigInteger` would break at 10⁴ digits; this loop never does.

## Complexity

**Time.** One pass over the longer string:

$$
T(n) = O(\max(|num1|, |num2|))
$$

**Space.** The result builder:

$$
S = O(\max(|num1|, |num2|))
$$

## Variants & follow-ups

- **Add Two Numbers** ([4.7](../ch04-linked-lists/add-two-numbers.md)) — the linked-list twin; identical carry loop.
- **Multiply Strings** (`math/MultiplyStrings.kt`) — the multiplication upgrade: per-digit products into a running array.
- **Interview follow-up:** "Why is `char - '0'` the right extraction?" Digits in ASCII are contiguous — `'0'` is 48, `'9'` is 57, so `'5' - '0' = 5`. It's the zero-cost integer conversion that makes string math feasible at 10⁴ digits.
