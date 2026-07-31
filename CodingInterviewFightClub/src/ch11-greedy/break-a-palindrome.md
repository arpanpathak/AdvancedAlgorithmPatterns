# 11.25 Break A Palindrome

> **Source**: [`src/main/kotlin/string/greedy/BreakAPalindrome.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/greedy/BreakAPalindrome.kt)
> **Pattern**: first-non-'a' flip · **Core page**

## The Problem

Change **one** char so the string stops being a palindrome; lexicographically smallest result, or "".

- Constraints: n ≥ 1.

## Examples

```
Input:  palindrome = "abccba"   -> Output: "aaccba"
Input:  palindrome = "a"        -> Output: ""
```

## Intuition — flip the first non-'a' in the first half to 'a'; else the last char to 'b'

A palindrome is broken by changing one char. The lexicographically smallest break: make the **leftmost** char (before the middle) 'a' if it isn't; if all are 'a', the last char becomes 'b':

```kotlin
if (palindrome.length == 1) return ""

val arr = palindrome.toCharArray()
for (i in 0 until palindrome.length / 2) {
    if (arr[i] != 'a') {
        arr[i] = 'a'
        return String(arr)
    }
}
arr[arr.lastIndex] = 'b'
return String(arr)
```

**Why the first half only?** Changing a mirrored pair's *left* member is lexicographically best (earliest position). The middle char is never touched (it doesn't affect the mirror).

**Why the fallback to 'b'?** An all-'a' palindrome (e.g. "aaa") can only grow — changing the *last* char to 'b' is the smallest increase. Length 1 is impossible (any change keeps it a palindrome... changing 'a' → 'b' makes "b", still a palindrome).

## Approach 1 — First-non-a greedy (the repo's version, optimal)

```kotlin
class BreakAPalindrome {
    /**
     * @param palindrome input palindrome
     * @return           lexicographically smallest non-palindrome, or ""
     */
    fun breakPalindrome(palindrome: String): String {
        if (palindrome.length == 1) return ""

        val arr = palindrome.toCharArray()

        for (i in 0 until palindrome.length / 2) {
            if (arr[i] != 'a') {
                arr[i] = 'a'
                return String(arr)
            }
        }
        arr[arr.lastIndex] = 'b'
        return String(arr)
    }
}
```

```java
public class BreakAPalindrome {
    /**
     * @param palindrome input palindrome
     * @return           lexicographically smallest non-palindrome, or ""
     */
    public String breakPalindrome(String palindrome) {
        if (palindrome.length() == 1) return "";

        char[] arr = palindrome.toCharArray();

        for (int i = 0; i < arr.length / 2; i++) {
            if (arr[i] != 'a') {
                arr[i] = 'a';
                return new String(arr);
            }
        }
        arr[arr.length - 1] = 'b';
        return new String(arr);
    }
}
```

```cpp
#include <string>

class BreakAPalindrome {
public:
    /**
     * @param palindrome input palindrome
     * @return           lexicographically smallest non-palindrome, or ""
     */
    std::string breakPalindrome(std::string palindrome) {
        if (palindrome.size() == 1) return "";

        for (int i = 0; i < (int)palindrome.size() / 2; i++) {
            if (palindrome[i] != 'a') {
                palindrome[i] = 'a';
                return palindrome;
            }
        }
        palindrome[palindrome.size() - 1] = 'b';
        return palindrome;
    }
};
```

```python
def break_palindrome(palindrome: str) -> str:
    """
    @param palindrome: input palindrome
    @return:           lexicographically smallest non-palindrome, or ""
    """
    if len(palindrome) == 1:
        return ""

    arr = list(palindrome)
    for i in range(len(arr) // 2):
        if arr[i] != "a":
            arr[i] = "a"
            return "".join(arr)

    arr[-1] = "b"
    return "".join(arr)
```

```rust
impl Solution {
    /// @param palindrome input palindrome
    /// @return           lexicographically smallest non-palindrome, or ""
    pub fn break_palindrome(palindrome: String) -> String {
        if palindrome.len() == 1 { return String::new(); }

        let mut chars: Vec<char> = palindrome.chars().collect();

        for i in 0..chars.len() / 2 {
            if chars[i] != 'a' {
                chars[i] = 'a';
                return chars.into_iter().collect();
            }
        }

        let n = chars.len();
        chars[n - 1] = 'b';
        chars.into_iter().collect()
    }
}
```

## Dry run

**Input:** `palindrome = "abccba"`.

```
half = 3.  i=0: 'a' -> skip.  i=1: 'b' != 'a' -> arr[1]='a'.  return "aaccba" ✓
Input: "aaa": all 'a' in the half -> fallback: last -> "aab" ✓ (smallest: "aab")
```

## Complexity

**Time.** Half scan:

$$
T(n) = O(n)
$$

**Space.** The array:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why is the fallback the last char → 'b'?" An all-'a' palindrome can only be *increased* — increasing the rightmost position is the smallest increase ("aaa" → "aab"). The first-half 'a'-flip is the *decrease* case, which beats any increase lexicographically.
