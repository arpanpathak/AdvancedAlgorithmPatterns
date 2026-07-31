# 9.11 Valid Palindrome

> **Source:** [`src/main/kotlin/string/ValidPalindrome.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/ValidPalindrome.kt) (+ `string/ValidPalindrome_II.kt`)
> **Pattern:** two pointers with a filter predicate · **Core page**

## The Problem

Given a string, determine if it is a palindrome considering **only alphanumeric characters** and ignoring case.

- Constraints: $1 \le n \le 2 \times 10^5$; printable ASCII.

## Examples

```
Input:  s = "A man, a plan, a canal: Panama"   -> Output: true
Input:  s = "race a car"                       -> Output: false
```

## Intuition — two pointers that *skip* non-alphanumerics

The [3.1](../ch03-arrays/two-sum-ii.md) two-pointer shape, with the "advance" rules extended: on each side, skip any character that isn't a letter or digit, then compare the survivors case-insensitively. The repo's `isAlpha` predicate drives a 4-way `when`:

```
left = 0, right = last
while left < right:
    if both alphanumeric:
        if they differ (case-insensitively): return false
        left++; right--
    else if right is not alphanumeric: right--      # skip the invalid one
    else if left is not alphanumeric: left++        # skip the invalid one
    else: left++; right--
return true
```

**Why skip *before* comparing?** The valid characters are the only ones that must mirror — punctuation and spaces are noise. The two-pointer skip is the [9.6](reverse-words-in-a-string.md) "filter as you go" discipline, avoiding a pre-filter pass.

**Why case-insensitive?** `'A'` and `'a'` are the same letter for palindrome purposes — the `lowercaseChar()` comparison normalizes both sides. The repo's `isAlpha` uses `Character.isAlphabetic || Character.isDigit` — alphanumeric means letters *and* digits (`"0P"` is not a palindrome: `0 ≠ p`).

## Approach 1 — Filter, reverse, compare (O(n) space)

`filter(isAlphanumeric).lowercase()` then compare with its reverse: simple, but builds a second string.

## Approach 2 — Two-pointer with skip (the repo's version, optimal)

```kotlin
class ValidPalindrome {
    /**
     * @param s input string
     * @return  true iff the alphanumerics mirror ignoring case
     */
    fun isPalindrome(s: String): Boolean {
        var left = 0
        var right = s.lastIndex
        val isAlpha = { ch: Char -> Character.isAlphabetic(ch.code) || Character.isDigit(ch.code) }

        while (left < right) {
            when {
                // Both are valid characters: compare them
                isAlpha(s[left]) && isAlpha(s[right])
                        && s[left].lowercaseChar() != s[right].lowercaseChar() -> return false

                // One side is invalid: skip it
                isAlpha(s[left]) && !isAlpha(s[right]) -> right--
                isAlpha(s[right]) && !isAlpha(s[left]) -> left++

                // Both valid and equal: move inward
                else -> {
                    left++
                    right--
                }
            }
        }
        return true
    }
}
```

```java
public class ValidPalindrome {
    /**
     * @param s input string
     * @return  true iff the alphanumerics mirror ignoring case
     */
    public boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;

        while (left < right) {
            char l = s.charAt(left), r = s.charAt(right);
            if (!Character.isLetterOrDigit(l)) { left++; }          // skip invalid
            else if (!Character.isLetterOrDigit(r)) { right--; }    // skip invalid
            else if (Character.toLowerCase(l) != Character.toLowerCase(r)) {
                return false;                                       // mismatch
            } else {
                left++; right--;
            }
        }
        return true;
    }
}
```

```cpp
#include <string>
#include <cctype>

class ValidPalindrome {
public:
    /**
     * @param s input string
     * @return  true iff the alphanumerics mirror ignoring case
     */
    bool isPalindrome(std::string s) {
        int left = 0, right = s.size() - 1;

        while (left < right) {
            if (!std::isalnum(s[left])) { left++; }                 // skip invalid
            else if (!std::isalnum(s[right])) { right--; }          // skip invalid
            else if (std::tolower(s[left]) != std::tolower(s[right])) {
                return false;                                       // mismatch
            } else {
                left++; right--;
            }
        }
        return true;
    }
};
```

```python
def is_palindrome(s: str) -> bool:
    """
    @param s: input string
    @return:  true iff the alphanumerics mirror ignoring case
    """
    left, right = 0, len(s) - 1

    while left < right:
        if not s[left].isalnum():
            left += 1                     # skip invalid
        elif not s[right].isalnum():
            right -= 1                    # skip invalid
        elif s[left].lower() != s[right].lower():
            return False                  # mismatch
        else:
            left += 1
            right -= 1
    return True
```

```rust
impl Solution {
    /// @param s input string
    /// @return  true iff the alphanumerics mirror ignoring case
    pub fn is_palindrome(s: String) -> bool {
        let chars: Vec<char> = s.chars().collect();
        let (mut left, mut right) = (0usize, chars.len().saturating_sub(1));

        while left < right {
            if !chars[left].is_alphanumeric() { left += 1; }          // skip invalid
            else if !chars[right].is_alphanumeric() { right -= 1; }   // skip invalid
            else if chars[left].to_lowercase().next() != chars[right].to_lowercase().next() {
                return false;                                         // mismatch
            } else {
                left += 1;
                right -= 1;
            }
        }
        true
    }
}
```

## Dry run

**Input:** `s = "A man, a plan, a canal: Panama"`.

```
left=0 'A', right=23 'a'  -> both alnum; 'a' == 'a' -> move in.
left=1 ' ', right=22 'm'  -> left is not alnum -> left++ (skip the space).
left=2 'm', right=22 'm'  -> equal -> move in.
left=3 'a', right=21 'a'  -> equal.
left=4 'n', right=20 'n'  -> equal.
... (the punctuation gets skipped on whichever side holds it)
left=12 'c', right=12 'c' -> equal -> left=13, right=11 -> loop ends.

Output: true ✓
```

The skip rules interleave naturally: when `left` hits a space or comma, it advances alone; when `right` does, it retreats alone; only when both sides hold alphanumerics is there a comparison. `"race a car"` fails at the `'e'` vs `'c'` comparison (`race` vs `rac` are not mirrors).

## Complexity

**Time.** One pass, O(1) per character:

$$
T(n) = O(n)
$$

**Space.** Two pointers:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Valid Palindrome II** (`string/ValidPalindrome_II.kt`) — one deletion allowed: on the first mismatch, check the two substrings skipping either side.
- **Longest Palindromic Substring** ([9.4](longest-palindromic-substring.md)) — the expand-around-center upgrade of the same mirror idea.
- **Reverse Vowels Of A String** (`string/ReverseVowelOfString.kt`) — two pointers with a vowel predicate: the same skip-and-swap rhythm.
- **Interview follow-up:** "Why skip in both directions instead of pre-filtering?" Pre-filtering copies the string (O(n) space). The two-pointer skip does the filtering *during* the comparison — the same O(n) time with O(1) space, and the case-normalization happens at the comparison site.
