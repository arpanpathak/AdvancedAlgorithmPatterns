# 9.29 Palindrome Number

> **Source**: [`src/main/kotlin/numbers/PalindromeNumber.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/numbers/PalindromeNumber.kt)
> **Pattern**: reverse-half comparison · **Core page**

## The Problem

Is `x` the same read backward?

- Constraints: 32-bit.

## Examples

```
Input:  x = 121   -> true.  x = -121 -> false.  x = 10 -> false.
```

## Intuition — rebuild the reverse; compare

```kotlin
var (reduced, sum) = listOf(x, 0, 1)

while (reduced > 0) {
    sum = sum * 10 + reduced % 10
    reduced /= 10
}
return sum == x
```

## Approach 1 — Full reverse (the repo's version)

## Approach 2 — Half reverse (optimal, no overflow)

Reverse only the lower half until `reversed >= x`; compare `x == reversed || x == reversed / 10`.

```kotlin
class PalindromeNumber {
    /**
     * @param x input integer
     * @return  true iff palindrome
     */
    fun isPalindrome(x: Int): Boolean {
        if (x < 0 || (x % 10 == 0 && x != 0)) return false

        var revertedNumber = 0
        var num = x

        while (num > revertedNumber) {
            revertedNumber = revertedNumber * 10 + num % 10
            num /= 10
        }
        return num == revertedNumber || num == revertedNumber / 10
    }
}
```

```java
public class PalindromeNumber {
    /**
     * @param x input integer
     * @return  true iff palindrome
     */
    public boolean isPalindrome(int x) {
        if (x < 0 || (x % 10 == 0 && x != 0)) return false;

        int reversed = 0;
        while (x > reversed) {
            reversed = reversed * 10 + x % 10;
            x /= 10;
        }
        return x == reversed || x == reversed / 10;
    }
}
```

```cpp
class PalindromeNumber {
public:
    /**
     * @param x input integer
     * @return  true iff palindrome
     */
    bool isPalindrome(int x) {
        if (x < 0 || (x % 10 == 0 && x != 0)) return false;

        int reversed = 0;
        while (x > reversed) {
            reversed = reversed * 10 + x % 10;
            x /= 10;
        }
        return x == reversed || x == reversed / 10;
    }
};
```

```python
def is_palindrome(x: int) -> bool:
    """
    @param x: input integer
    @return:  true iff palindrome
    """
    if x < 0 or (x % 10 == 0 and x != 0):
        return False

    reversed_num = 0
    while x > reversed_num:
        reversed_num = reversed_num * 10 + x % 10
        x //= 10

    return x == reversed_num or x == reversed_num // 10
```

```rust
impl Solution {
    /// @param x input integer
    /// @return  true iff palindrome
    pub fn is_palindrome(x: i32) -> bool {
        if x < 0 || (x % 10 == 0 && x != 0) { return false; }

        let mut reversed = 0;
        let mut num = x;

        while num > reversed {
            reversed = reversed * 10 + num % 10;
            num /= 10;
        }
        num == reversed || num == reversed / 10
    }
}
```

## Dry run

**Input:** `x = 121`.

```
reversed=0, num=121.  num > rev: rev=1, num=12.  12 > 1: rev=12, num=1.  1 > 12? no.
num == reversed / 10? 1 == 1 -> true ✓
Input: x = 10: x % 10 == 0 && x != 0 -> false ✓
```

## Complexity

**Time.** Half the digits:

$$
T = O(\log x)
$$

**Space.** Constants:

$$
S = O(1)
$$

## Variants & follow-ups

- **Reverse Integer** ([3.26](../ch03-arrays/reverse-integer.md)) — the overflow-aware reverse.
- **Interview follow-up:** "Why the half-reverse?" The full reverse can overflow — reversing half avoids it, and the `num == reversed/10` branch handles odd digit counts.
