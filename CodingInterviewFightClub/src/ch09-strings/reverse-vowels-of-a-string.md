# 9.34 Reverse Vowels Of A String

> **Source**: [`src/main/kotlin/string/ReverseVowelOfString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/ReverseVowelOfString.kt)
> **Pattern**: two-pointer vowel swap · **Core page**

## The Problem

Reverse only the vowels in `s`.

- Constraints: n ≤ 3×10⁵.

## Examples

```
Input:  s = "hello"   -> Output: "holle"
Input:  s = "leetcode" -> Output: "leotcede"
```

## Intuition — the [9.11](valid-palindrome.md) two-pointer, swapping vowels

```kotlin
val vowels = setOf('a', 'e', 'i', 'o', 'u')
val result = StringBuilder(s)
var (start, end) = Pair(0, s.lastIndex)

while (start < end) {
    if (s[start] in vowels && s[end] in vowels) {
        val tmp = result[start]
        result[start] = result[end]
        result[end] = tmp
        start++
        end--
    } else if (s[start] !in vowels) {
        start++
    } else {
        end--
    }
}
return result.toString()
```

## Approach 1 — Two-pointer swap (the repo's version, optimal)

```kotlin
class ReverseVowelOfString {
    /**
     * @param s input string
     * @return  vowels reversed
     */
    fun reverseVowels(s: String): String {
        val vowels = setOf('a', 'e', 'i', 'o', 'u')
        val result = StringBuilder(s)

        var (start, end) = Pair(0, s.lastIndex)

        while (start < end) {
            if (s[start] in vowels && s[end] in vowels) {
                val tmp = result[start]
                result[start] = result[end]
                result[end] = tmp
                start++
                end--
            } else if (s[start] !in vowels) {
                start++
            } else {
                end--
            }
        }
        return result.toString()
    }
}
```

```java
public class ReverseVowelsOfAString {
    private boolean isVowel(char c) {
        return "aeiouAEIOU".indexOf(c) != -1;
    }

    /**
     * @param s input string
     * @return  vowels reversed
     */
    public String reverseVowels(String s) {
        char[] chars = s.toCharArray();
        int left = 0, right = chars.length - 1;

        while (left < right) {
            if (isVowel(chars[left]) && isVowel(chars[right])) {
                char tmp = chars[left];
                chars[left] = chars[right];
                chars[right] = tmp;
                left++;
                right--;
            } else if (!isVowel(chars[left])) {
                left++;
            } else {
                right--;
            }
        }
        return new String(chars);
    }
}
```

```cpp
#include <string>

class ReverseVowelsOfAString {
    bool isVowel(char c) {
        c = std::tolower(c);
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }

public:
    /**
     * @param s input string
     * @return  vowels reversed
     */
    std::string reverseVowels(std::string s) {
        int left = 0, right = s.size() - 1;

        while (left < right) {
            if (isVowel(s[left]) && isVowel(s[right])) {
                std::swap(s[left], s[right]);
                left++;
                right--;
            } else if (!isVowel(s[left])) {
                left++;
            } else {
                right--;
            }
        }
        return s;
    }
};
```

```python
def reverse_vowels(s: str) -> str:
    """
    @param s: input string
    @return:  vowels reversed
    """
    vowels = set("aeiouAEIOU")
    result = list(s)
    left, right = 0, len(s) - 1

    while left < right:
        if result[left] in vowels and result[right] in vowels:
            result[left], result[right] = result[right], result[left]
            left += 1
            right -= 1
        elif result[left] not in vowels:
            left += 1
        else:
            right -= 1

    return "".join(result)
```

```rust
impl Solution {
    /// @param s input string
    /// @return  vowels reversed
    pub fn reverse_vowels(s: String) -> String {
        let mut chars: Vec<char> = s.chars().collect();
        let is_vowel = |c: char| matches!(c, 'a' | 'e' | 'i' | 'o' | 'u' | 'A' | 'E' | 'I' | 'O' | 'U');
        let (mut left, mut right) = (0, chars.len() - 1);

        while left < right {
            if is_vowel(chars[left]) && is_vowel(chars[right]) {
                chars.swap(left, right);
                left += 1;
                right -= 1;
            } else if !is_vowel(chars[left]) {
                left += 1;
            } else {
                right -= 1;
            }
        }
        chars.into_iter().collect()
    }
}
```

## Dry run

**Input:** `s = "hello"`.

```
h: skip left.  e(1) & o(4): swap -> "holle".  l(2): skip.  l(3): skip.
Output: "holle" ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The array:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Valid Palindrome** ([9.11](valid-palindrome.md)) — the two-pointer ancestor.
- **Interview follow-up:** "Why the three-branch walk?" Only swapping when both ends are vowels preserves the non-vowel positions; the skip branches advance toward the next vowel pair.
