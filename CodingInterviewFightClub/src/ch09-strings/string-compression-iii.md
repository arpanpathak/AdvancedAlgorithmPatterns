# 9.25 String Compression III

> **Source**: [`src/main/kotlin/string/StringCompression_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/StringCompression_II.kt)
> **Pattern**: run-length with a 9-cap · **Core page**

## The Problem

Compress `word` as `char+count` pairs, count **capped at 9** (runs split into chunks).

- Constraints: n ≤ 2×10⁵; lowercase.

## Examples

```
Input:  word = "abcde"        -> Output: "1a1b1c1d1e"
Input:  word = "aaaaaaaaaaaaa" -> Output: "9a4a"   (13 a's -> 9 + 4)
```

## Intuition — count the run, emit in 9-chunks

The [9.21](string-compression.md) run-length, with the count split into 9-capped digits:

```kotlin
while (i < word.length) {
    val ch = word[i]
    var count = 0

    while (i < word.length && word[i] == ch && count < 9) {
        i++
        count++
    }

    compressed.append(count).append(ch)
}
```

**Why the 9-cap?** The problem's format forbids multi-digit counts — a 13-run becomes `9a4a`. The inner loop stops at 9 and lets the outer loop resume the same char.

## Approach 1 — 9-capped run-length (the repo's version, optimal)

```kotlin
class StringCompression_II {
    /**
     * @param word input string
     * @return     9-capped run-length encoding
     */
    fun compressedString(word: String): String {
        val compressed = StringBuilder()
        var i = 0

        while (i < word.length) {
            val ch = word[i]
            var count = 0

            while (i < word.length && word[i] == ch && count < 9) {
                i++
                count++
            }

            compressed.append(count).append(ch)
        }
        return compressed.toString()
    }
}
```

```java
public class StringCompressionIII {
    /**
     * @param word input string
     * @return     9-capped run-length encoding
     */
    public String compressedString(String word) {
        StringBuilder sb = new StringBuilder();
        int i = 0;

        while (i < word.length()) {
            char ch = word.charAt(i);
            int count = 0;

            while (i < word.length() && word.charAt(i) == ch && count < 9) {
                i++;
                count++;
            }

            sb.append(count).append(ch);
        }
        return sb.toString();
    }
}
```

```cpp
#include <string>

class StringCompressionIII {
public:
    /**
     * @param word input string
     * @return     9-capped run-length encoding
     */
    std::string compressedString(std::string word) {
        std::string result;
        int i = 0;

        while (i < (int)word.size()) {
            char ch = word[i];
            int count = 0;

            while (i < (int)word.size() && word[i] == ch && count < 9) {
                i++;
                count++;
            }

            result += std::to_string(count);
            result += ch;
        }
        return result;
    }
};
```

```python
def compressed_string(word: str) -> str:
    """
    @param word: input string
    @return:     9-capped run-length encoding
    """
    result = []
    i = 0

    while i < len(word):
        ch = word[i]
        count = 0

        while i < len(word) and word[i] == ch and count < 9:
            i += 1
            count += 1

        result.append(f"{count}{ch}")

    return "".join(result)
```

```rust
impl Solution {
    /// @param word input string
    /// @return     9-capped run-length encoding
    pub fn compressed_string(word: String) -> String {
        let bytes: Vec<char> = word.chars().collect();
        let mut result = String::new();
        let mut i = 0;

        while i < bytes.len() {
            let ch = bytes[i];
            let mut count = 0;

            while i < bytes.len() && bytes[i] == ch && count < 9 {
                i += 1;
                count += 1;
            }

            result.push_str(&count.to_string());
            result.push(ch);
        }
        result
    }
}
```

## Dry run

**Input:** `word = "aaaaaaaaaaaaa"` (13 a's).

```
i=0 'a': count to 9 (i=9).  emit "9a".  outer resumes at i=9.
count 4 (i=13).  emit "4a".
Output: "9a4a" ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The output:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **String Compression** ([9.21](string-compression.md)) — the in-place multi-digit ancestor.
- **Interview follow-up:** "Why does the 9-cap need the outer loop to resume?" The same char's run continues past a 9-chunk — the outer loop restarts the counting at the current position, naturally producing consecutive `9a`/`4a` pairs.
