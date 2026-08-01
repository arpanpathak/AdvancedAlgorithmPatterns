# 9.27 Multiply Strings

> **Source**: [`src/main/kotlin/math/MultiplyStrings.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/MultiplyStrings.kt)
> **Pattern**: digit-by-digit multiplication · **Core page**

## The Problem

Multiply two non-negative integer strings without big-int types.

- Constraints: lengths ≤ 200.

## Examples

```
Input:  num1 = "123", num2 = "456"   -> Output: "56088"
```

## Intuition — the schoolbook algorithm into an int array

`num1[i] × num2[j]` lands at positions `i+j` and `i+j+1`:

```kotlin
val result = IntArray(num1.length + num2.length)

for (i in num1.indices.reversed()) {
    for (j in num2.indices.reversed()) {
        val product = (num1[i] - '0') * (num2[j] - '0')
        val sum = product + result[i + j + 1]

        result[i + j + 1] = sum % 10
        result[i + j] += sum / 10
    }
}

return result.joinToString("").trimStart('0').ifEmpty { "0" }
```

## Approach 1 — Int-array accumulation (the repo's version, optimal)

```kotlin
class MultiplyStrings {
    /**
     * @param num1 first number
     * @param num2 second number
     * @return     product string
     */
    fun multiply(num1: String, num2: String): String {
        if (num1 == "0" || num2 == "0") return "0"

        val result = IntArray(num1.length + num2.length)

        for (i in num1.indices.reversed()) {
            for (j in num2.indices.reversed()) {
                val product = (num1[i] - '0') * (num2[j] - '0')
                val sum = product + result[i + j + 1]

                result[i + j + 1] = sum % 10
                result[i + j] += sum / 10
            }
        }

        return result.joinToString("").trimStart('0').ifEmpty { "0" }
    }
}
```

```java
public class MultiplyStrings {
    /**
     * @param num1 first number
     * @param num2 second number
     * @return     product string
     */
    public String multiply(String num1, String num2) {
        if (num1.equals("0") || num2.equals("0")) return "0";

        int[] result = new int[num1.length() + num2.length()];

        for (int i = num1.length() - 1; i >= 0; i--) {
            for (int j = num2.length() - 1; j >= 0; j--) {
                int product = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
                int sum = product + result[i + j + 1];

                result[i + j + 1] = sum % 10;
                result[i + j] += sum / 10;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int d : result) {
            if (!(sb.length() == 0 && d == 0)) sb.append(d);
        }
        return sb.length() == 0 ? "0" : sb.toString();
    }
}
```

```cpp
#include <string>
#include <vector>

class MultiplyStrings {
public:
    /**
     * @param num1 first number
     * @param num2 second number
     * @return     product string
     */
    std::string multiply(std::string num1, std::string num2) {
        if (num1 == "0" || num2 == "0") return "0";

        std::vector<int> result(num1.size() + num2.size(), 0);

        for (int i = num1.size() - 1; i >= 0; i--) {
            for (int j = num2.size() - 1; j >= 0; j--) {
                int product = (num1[i] - '0') * (num2[j] - '0');
                int sum = product + result[i + j + 1];

                result[i + j + 1] = sum % 10;
                result[i + j] += sum / 10;
            }
        }

        std::string out;
        bool started = false;
        for (int d : result) {
            if (d != 0) started = true;
            if (started) out += char('0' + d);
        }
        return out.empty() ? "0" : out;
    }
};
```

```python
def multiply(num1: str, num2: str) -> str:
    """
    @param num1: first number
    @param num2: second number
    @return:     product string
    """
    if num1 == "0" or num2 == "0":
        return "0"

    result = [0] * (len(num1) + len(num2))

    for i in range(len(num1) - 1, -1, -1):
        for j in range(len(num2) - 1, -1, -1):
            product = int(num1[i]) * int(num2[j])
            total = product + result[i + j + 1]

            result[i + j + 1] = total % 10
            result[i + j] += total // 10

    out = "".join(map(str, result)).lstrip("0")
    return out or "0"
```

```rust
impl Solution {
    /// @param num1 first number
    /// @param num2 second number
    /// @return     product string
    pub fn multiply(num1: String, num2: String) -> String {
        if num1 == "0" || num2 == "0" { return "0".to_string(); }

        let a: Vec<u32> = num1.chars().map(|c| c.to_digit(10).unwrap()).collect();
        let b: Vec<u32> = num2.chars().map(|c| c.to_digit(10).unwrap()).collect();
        let mut result = vec![0u32; a.len() + b.len()];

        for i in (0..a.len()).rev() {
            for j in (0..b.len()).rev() {
                let product = a[i] * b[j];
                let sum = product + result[i + j + 1];

                result[i + j + 1] = sum % 10;
                result[i + j] += sum / 10;
            }
        }

        let out: String = result.iter().map(|d| char::from_digit(*d, 10).unwrap()).collect();
        let trimmed = out.trim_start_matches('0');
        if trimmed.is_empty() { "0".to_string() } else { trimmed.to_string() }
    }
}
```

## Dry run

**Input:** `"123" × "456"`.

```
i=2 (3): j=2 (6): p=18, s=18+0=18 -> result[5]=8, result[4]=1.  j=1 (5): 15+1=16 -> result[4]=6, result[3]=1.
  j=0 (4): 12+1=13 -> result[3]=3, result[2]=1.
i=1 (2): j=2: 12+1=13 -> result[4]=3, result[3]=1.  j=1: 10+3=13 -> result[3]=3, result[2]=1.
  j=0: 8+1=9 -> result[2]=9.
i=0 (1): j=2: 6+3=9 -> result[3]=9.  j=1: 5+9=14 -> result[2]=4, result[1]=1.  j=0: 4+1=5 -> result[1]=5.
result: [0,5,6,0,8,8] -> "56088" ✓
```

## Complexity

**Time.** Digit product pairs:

$$
T(n, m) = O(n \cdot m)
$$

**Space.** The array:

$$
S(n, m) = O(n + m)
$$

## Variants & follow-ups

- **Add Strings** ([9.x](add-strings.md)) — the addition engine.
- **Interview follow-up:** "Why do the positions land at i+j / i+j+1?" Multiplying the i-th digit of A by the j-th of B gives 10^(i+j) weight — the two slots are the tens and units of the local product.
