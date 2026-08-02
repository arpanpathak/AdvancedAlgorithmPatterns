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

## Reading the code — what's actually happening

```kotlin
if (x < 0 || (x % 10 == 0 && x != 0)) return false
var revertedNumber = 0
var num = x
while (num > revertedNumber) {
    revertedNumber = revertedNumber * 10 + num % 10
    num /= 10
}
return num == revertedNumber || num == revertedNumber / 10
```

The core idea: **peel digits off the right end of `x` and stack them into `revertedNumber` — but only go halfway.** If `x` is a palindrome, the reversed right half equals the left half.

- **The two early returns are edge-case sentinels.** `x < 0` can't be a palindrome (the minus sign has no mirror). `x % 10 == 0 && x != 0` kills numbers ending in zero like `10`, `100` — a palindrome can't end in 0 unless it *is* 0, because its first digit would also have to be 0.
- **The loop condition `num > revertedNumber` is the "stop at the middle" meter.** Each iteration transfers the last digit of `num` onto the end of `revertedNumber`: `revertedNumber = revertedNumber * 10 + num % 10` shifts the reversed part up a digit and appends the new one; `num /= 10` trims the digit we just stole. The loop stops when `revertedNumber` catches up to (or passes) `num` — meaning we've reversed at least half the digits.
- **Two comparison branches handle even vs. odd digit counts.** For `x = 1221` (even): `num = 12`, `revertedNumber = 12` when the loop stops → `num == revertedNumber` → `true`. For `x = 121` (odd): the middle digit `1` lands in `revertedNumber` (`num = 1`, `revertedNumber = 12`), so we compare `num == revertedNumber / 10` → `1 == 1` → `true`. Dropping the extra middle digit is what `/ 10` does.
- **Why half-reverse at all?** Reversing the entire number could overflow 32 bits (`x = 2147483647` reversed is `7463847412`); stopping halfway keeps `revertedNumber` comfortably small.

Trace `x = 121`: `num=121, rev=0` → `121 > 0`: `rev = 1, num = 12` → `12 > 1`: `rev = 12, num = 1` → `1 > 12`? no → `num == rev/10` → `1 == 1` → `true` ✓.

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
