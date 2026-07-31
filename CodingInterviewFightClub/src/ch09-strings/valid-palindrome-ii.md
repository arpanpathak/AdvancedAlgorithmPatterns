# 9.24 Valid Palindrome II

> **Source**: [`src/main/kotlin/string/ValidPalindrome_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/ValidPalindrome_II.kt)
> **Pattern**: skip-one palindrome check · **Core page**

## The Problem

Can `s` become a palindrome by deleting **at most one** char?

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  s = "abca"   -> Output: true   (delete 'c' or 'b')
Input:  s = "abc"    -> Output: false
```

## Intuition — two-pointer; on mismatch, try skipping either side

The classic [9.11](valid-palindrome.md) two-pointer; the first mismatch offers exactly two fixes — skip the left or skip the right:

```kotlin
var left = 0
var right = s.lastIndex

while (left < right) {
    if (s[left] != s[right]) {
        return isPalindrome(s, left, right - 1) || isPalindrome(s, left + 1, right)
    }
    left++
    right--
}
return true
```

**Why only two options?** Deleting *one* char at the first mismatch: either the left or the right. If neither fix yields a palindrome, no single deletion works — one check per side, O(n).

## Approach 1 — Two-pointer with skip-try (the repo's version, optimal)

```kotlin
class ValidPalindrome_II {
    /**
     * @param s input string
     * @return  true iff one deletion can make it a palindrome
     */
    fun validPalindrome(s: String): Boolean {
        var left = 0
        var right = s.lastIndex

        while (left < right) {
            if (s[left] != s[right]) {
                return isPalindrome(s, left, right - 1) || isPalindrome(s, left + 1, right)
            }
            left++
            right--
        }
        return true
    }

    private fun isPalindrome(s: String, left: Int, right: Int): Boolean {
        var l = left
        var r = right
        while (l < r) {
            if (s[l] != s[r]) return false
            l++
            r--
        }
        return true
    }
}
```

```java
public class ValidPalindromeII {
    private boolean isPalindrome(String s, int l, int r) {
        while (l < r) {
            if (s.charAt(l++) != s.charAt(r--)) return false;
        }
        return true;
    }

    /**
     * @param s input string
     * @return  true iff one deletion can make it a palindrome
     */
    public boolean validPalindrome(String s) {
        int left = 0, right = s.length() - 1;

        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return isPalindrome(s, left + 1, right) || isPalindrome(s, left, right - 1);
            }
            left++;
            right--;
        }
        return true;
    }
}
```

```cpp
#include <string>

class ValidPalindromeII {
    bool isPalindrome(std::string& s, int l, int r) {
        while (l < r) {
            if (s[l++] != s[r--]) return false;
        }
        return true;
    }

public:
    /**
     * @param s input string
     * @return  true iff one deletion can make it a palindrome
     */
    bool validPalindrome(std::string s) {
        int left = 0, right = s.size() - 1;

        while (left < right) {
            if (s[left] != s[right]) {
                return isPalindrome(s, left + 1, right) || isPalindrome(s, left, right - 1);
            }
            left++;
            right--;
        }
        return true;
    }
};
```

```python
def valid_palindrome(s: str) -> bool:
    """
    @param s: input string
    @return:  true iff one deletion can make it a palindrome
    """
    def is_pal(l: int, r: int) -> bool:
        while l < r:
            if s[l] != s[r]:
                return False
            l += 1
            r -= 1
        return True

    left, right = 0, len(s) - 1
    while left < right:
        if s[left] != s[right]:
            return is_pal(left + 1, right) or is_pal(left, right - 1)
        left += 1
        right -= 1

    return True
```

```rust
impl Solution {
    /// @param s input string
    /// @return  true iff one deletion can make it a palindrome
    pub fn valid_palindrome(s: String) -> bool {
        let bytes: Vec<char> = s.chars().collect();

        fn is_pal(bytes: &Vec<char>, mut l: i32, mut r: i32) -> bool {
            while l < r {
                if bytes[l as usize] != bytes[r as usize] { return false; }
                l += 1;
                r -= 1;
            }
            true
        }

        let (mut left, mut right) = (0i32, bytes.len() as i32 - 1);
        while left < right {
            if bytes[left as usize] != bytes[right as usize] {
                return is_pal(&bytes, left + 1, right) || is_pal(&bytes, left, right - 1);
            }
            left += 1;
            right -= 1;
        }
        true
    }
}
```

## Dry run

**Input:** `s = "abca"`.

```
left=0 'a', right=3 'a' match.  left=1 'b', right=2 'c' MISMATCH.
try skip right: isPal("abc"? 1..1: "b"-> true) -> true ✓
```

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Valid Palindrome** ([9.11](valid-palindrome.md)) — the no-deletion ancestor.
- **Valid Palindrome III** ([2.34](../ch02-dynamic-programming/valid-palindrome-iii.md)) — k deletions (DP).
- **Interview follow-up:** "Why check only at the first mismatch?" Before it, the string is symmetric — deletions there mirror to both sides and can't help. The first mismatch localizes the problem to exactly two candidate deletions.
