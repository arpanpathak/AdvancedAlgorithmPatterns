# 9.28 Add Binary

> **Source**: [`src/main/kotlin/math/binary/AddBinary.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/binary/AddBinary.kt)
> **Pattern**: carry walk · **Core page**

## The Problem

Add two binary strings.

- Constraints: lengths ≤ 10⁴.

## Examples

```
Input:  a = "11", b = "1"   -> Output: "100"
```

## Intuition — the [9.x](add-strings.md) carry loop over bits

```kotlin
val result = StringBuilder()
var (i, j) = a.lastIndex to b.lastIndex
var carry = 0

while (i >= 0 || j >= 0 || carry > 0) {
    var sum = carry
    if (i >= 0) sum += a[i--] - '0'
    if (j >= 0) sum += b[j--] - '0'

    result.append(sum % 2)
    carry = sum / 2
}
return result.reverse().toString()
```

## Approach 1 — Carry walk (the repo's version, optimal)

```kotlin
class AddBinary {
    /**
     * @param a first binary string
     * @param b second binary string
     * @return  sum binary string
     */
    fun addBinary(a: String, b: String): String {
        val result = StringBuilder()
        var (i, j) = a.lastIndex to b.lastIndex
        var carry = 0

        while (i >= 0 || j >= 0 || carry > 0) {
            var sum = carry
            if (i >= 0) sum += a[i--] - '0'
            if (j >= 0) sum += b[j--] - '0'

            result.append(sum % 2)
            carry = sum / 2
        }
        return result.reverse().toString()
    }
}
```

```java
public class AddBinary {
    /**
     * @param a first binary string
     * @param b second binary string
     * @return  sum binary string
     */
    public String addBinary(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1, carry = 0;

        while (i >= 0 || j >= 0 || carry > 0) {
            int sum = carry;
            if (i >= 0) sum += a.charAt(i--) - '0';
            if (j >= 0) sum += b.charAt(j--) - '0';

            sb.append(sum % 2);
            carry = sum / 2;
        }
        return sb.reverse().toString();
    }
}
```

```cpp
#include <string>
#include <algorithm>

class AddBinary {
public:
    /**
     * @param a first binary string
     * @param b second binary string
     * @return  sum binary string
     */
    std::string addBinary(std::string a, std::string b) {
        std::string result;
        int i = a.size() - 1, j = b.size() - 1, carry = 0;

        while (i >= 0 || j >= 0 || carry) {
            int sum = carry;
            if (i >= 0) sum += a[i--] - '0';
            if (j >= 0) sum += b[j--] - '0';

            result += char('0' + sum % 2);
            carry = sum / 2;
        }
        std::reverse(result.begin(), result.end());
        return result;
    }
};
```

```python
def add_binary(a: str, b: str) -> str:
    """
    @param a: first binary string
    @param b: second binary string
    @return:  sum binary string
    """
    result = []
    i, j = len(a) - 1, len(b) - 1
    carry = 0

    while i >= 0 or j >= 0 or carry:
        total = carry
        if i >= 0:
            total += int(a[i])
            i -= 1
        if j >= 0:
            total += int(b[j])
            j -= 1

        result.append(str(total % 2))
        carry = total // 2

    return "".join(reversed(result))
```

```rust
impl Solution {
    /// @param a first binary string
    /// @param b second binary string
    /// @return  sum binary string
    pub fn add_binary(a: String, b: String) -> String {
        let a: Vec<char> = a.chars().collect();
        let b: Vec<char> = b.chars().collect();
        let (mut i, mut j, mut carry) = (a.len() as i32 - 1, b.len() as i32 - 1, 0);
        let mut result = Vec::new();

        while i >= 0 || j >= 0 || carry > 0 {
            let mut sum = carry;
            if i >= 0 { sum += a[i as usize] as i32 - '0' as i32; i -= 1; }
            if j >= 0 { sum += b[j as usize] as i32 - '0' as i32; j -= 1; }

            result.push(char::from_digit((sum % 2) as u32, 10).unwrap());
            carry = sum / 2;
        }

        result.into_iter().rev().collect()
    }
}
```

## Dry run

**Input:** `a = "11", b = "1"`.

```
i=1, j=0: sum = 0+1+1 = 2 -> append 0, carry 1.  i=0: sum = 1+1 = 2 -> append 0, carry 1.
carry: sum = 1 -> append 1.  "001" reversed -> "100" ✓
```

## Complexity

**Time.** Max length:

$$
T(n, m) = O(\max(n, m))
$$

**Space.** The result:

$$
S(n, m) = O(\max(n, m))
$$

## Variants & follow-ups

- **Add Strings** — the decimal twin.
- **Interview follow-up:** "Why the `carry > 0` in the loop condition?" A final carry (e.g. 1+1=10) needs an extra digit — the condition keeps the loop alive one more round.
