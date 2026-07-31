# 9.17 Merge Strings Alternately

> **Source:** [`src/main/kotlin/string/MergeStringAlternatively.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/MergeStringAlternatively.kt)
> **Pattern:** interleaved zip · **Core page**

## The Problem

Merge two strings by alternating chars; append the longer string's remainder.

- Constraints: lengths ≤ 100.

## Examples

```
Input:  word1 = "abc", word2 = "pqr"   -> Output: "apbqcr"
Input:  word1 = "ab", word2 = "pqrs"   -> Output: "apbqrs"
```

## Intuition — one loop to the max length, guard each source

```kotlin
for (i in 0 until maxOf(word1.length, word2.length)) {
    if (i < word1.length) mergedString.append(word1[i])
    if (i < word2.length) mergedString.append(word2[i])
}
```

The per-index guards handle the unequal lengths — the tail appends naturally.

## Approach 1 — Two-pointer merge (the [4.7](../ch04-linked-lists/add-two-numbers.md) style)

`i`/`j` walkers + remainder append: equivalent.

## Approach 2 — Max-length loop (the repo's version, optimal)

```kotlin
class MergeStringAlternatively {
    /**
     * @param word1 first string
     * @param word2 second string
     * @return      alternating merge
     */
    fun mergeAlternately(word1: String, word2: String): String {
        val mergedString = StringBuilder()

        for (i in 0 until maxOf(word1.length, word2.length)) {
            if (i < word1.length) mergedString.append(word1[i])
            if (i < word2.length) mergedString.append(word2[i])
        }
        return mergedString.toString()
    }
}
```

```java
public class MergeStringsAlternately {
    /**
     * @param word1 first string
     * @param word2 second string
     * @return      alternating merge
     */
    public String mergeAlternately(String word1, String word2) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < Math.max(word1.length(), word2.length()); i++) {
            if (i < word1.length()) sb.append(word1.charAt(i));
            if (i < word2.length()) sb.append(word2.charAt(i));
        }
        return sb.toString();
    }
}
```

```cpp
#include <string>

class MergeStringsAlternately {
public:
    /**
     * @param word1 first string
     * @param word2 second string
     * @return      alternating merge
     */
    std::string mergeAlternately(std::string word1, std::string word2) {
        std::string result;
        int n = std::max(word1.size(), word2.size());

        for (int i = 0; i < n; i++) {
            if (i < (int)word1.size()) result += word1[i];
            if (i < (int)word2.size()) result += word2[i];
        }
        return result;
    }
};
```

```python
def merge_alternately(word1: str, word2: str) -> str:
    """
    @param word1: first string
    @param word2: second string
    @return:      alternating merge
    """
    return "".join(
        word1[i] if i < len(word1) else ""
        for i in range(len(word1))
    ) if False else "".join(
        (word1[i] if i < len(word1) else "") + (word2[i] if i < len(word2) else "")
        for i in range(max(len(word1), len(word2)))
    )
```

```rust
impl Solution {
    /// @param word1 first string
    /// @param word2 second string
    /// @return      alternating merge
    pub fn merge_alternately(word1: String, word2: String) -> String {
        let (b1, b2) = (word1.as_bytes(), word2.as_bytes());
        let n = b1.len().max(b2.len());
        let mut result = String::with_capacity(b1.len() + b2.len());

        for i in 0..n {
            if i < b1.len() { result.push(b1[i] as char); }
            if i < b2.len() { result.push(b2[i] as char); }
        }
        result
    }
}
```

## Dry run

**Input:** `word1 = "ab", word2 = "pqrs"`.

```
i=0: a, p.  i=1: b, q.  i=2: (word1 exhausted), r.  i=3: s.

Output: "apbqrs" ✓
```

## Complexity

**Time.** Max length:

$$
T(n) = O(n + m)
$$

**Space.** The builder:

$$
S(n) = O(n + m)
$$

## Variants & follow-ups

- **Interleaving String** ([2.22](../ch02-dynamic-programming/interleaving-string.md)) — the DP version deciding *if* a merge exists.
- **Interview follow-up:** "Why the max-length loop with guards instead of zip-then-append?" The guards make the tail handling implicit — no post-loop remainder append. Both are O(n+m); the guarded loop is the one-expression spelling.
