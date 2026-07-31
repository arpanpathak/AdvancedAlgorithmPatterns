# 9.20 Is Subsequence

> **Source**: [`src/main/kotlin/string/IsSubsequence.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/IsSubsequence.kt)
> **Pattern**: two-pointer match · **Core page**

## The Problem

Is `s` a subsequence of `t` (chars in order, not necessarily contiguous)?

- Constraints: lengths ≤ 10⁵.

## Examples

```
Input:  s = "abc", t = "ahbgdc"   -> Output: true
Input:  s = "axc", t = "ahbgdc"   -> Output: false
```

## Intuition — advance s only on a match

```kotlin
var i = 0
var j = 0

while (i < s.length && j < t.length) {
    if (s[i] == t[j]) { i++; j++ }
    else j++
}
return i == s.length
```

## Approach 1 — Greedy two-pointer (the repo's version, optimal)

## Approach 2 — Index-map bisect (for many queries)

Precompute each char's positions; bisect for the next — O(|t| + |s| log |t|) per query.

```kotlin
class IsSubsequence {
    /**
     * @param s pattern
     * @param t haystack
     * @return  true iff s is a subsequence of t
     */
    fun isSubsequence(s: String, t: String): Boolean {
        var i = 0
        var j = 0

        while (i < s.length && j < t.length) {
            if (s[i] == t[j]) {
                i++
                j++
            } else {
                j++
            }
        }
        return i == s.length
    }
}
```

```java
public class IsSubsequence {
    /**
     * @param s pattern
     * @param t haystack
     * @return  true iff s is a subsequence of t
     */
    public boolean isSubsequence(String s, String t) {
        int i = 0;
        for (int j = 0; j < t.length() && i < s.length(); j++) {
            if (s.charAt(i) == t.charAt(j)) i++;
        }
        return i == s.length();
    }
}
```

```cpp
#include <string>

class IsSubsequence {
public:
    /**
     * @param s pattern
     * @param t haystack
     * @return  true iff s is a subsequence of t
     */
    bool isSubsequence(std::string s, std::string t) {
        int i = 0;
        for (int j = 0; j < (int)t.size() && i < (int)s.size(); j++) {
            if (s[i] == t[j]) i++;
        }
        return i == (int)s.size();
    }
};
```

```python
def is_subsequence(s: str, t: str) -> bool:
    """
    @param s: pattern
    @param t: haystack
    @return:  true iff s is a subsequence of t
    """
    i = 0
    for ch in t:
        if i < len(s) and s[i] == ch:
            i += 1

    return i == len(s)
```

```rust
impl Solution {
    /// @param s pattern
    /// @param t haystack
    /// @return  true iff s is a subsequence of t
    pub fn is_subsequence(s: String, t: String) -> bool {
        let (sb, tb) = (s.as_bytes(), t.as_bytes());
        let mut i = 0;

        for &ch in tb {
            if i < sb.len() && sb[i] == ch { i += 1; }
        }
        i == sb.len()
    }
}
```

## Dry run

**Input:** `s = "abc", t = "ahbgdc"`.

```
i=0: 'a' == 'a' -> i=1.  'h': no.  'b': match -> i=2.  'g': no.  'd': no.  'c': match -> i=3.
i == 3 == s.length -> true ✓
```

## Complexity

**Time.** One pass:

$$
T = O(|t|)
$$

**Space.** Constants:

$$
S = O(1)
$$

## Variants & follow-ups

- **Number Of Matching Subsequences** ([9.10](number-of-matching-subsequences.md)) — the multi-query upgrade (bucket the patterns).
- **Interview follow-up:** "Why is greedy correct?" The earliest match for each char is always safe — a later match can't be better since it leaves fewer chars for the rest. The greedy's "take the first" is the exchange-argument optimal.
