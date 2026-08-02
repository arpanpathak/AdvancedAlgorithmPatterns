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

## Reading the code — what's actually happening

```kotlin
var i = 0
var j = 0
while (i < s.length && j < t.length) {
    if (s[i] == t[j]) { i++; j++ }
    else j++
}
return i == s.length
```

Think of it as two people walking down two lines of text: `i` points at the next character of `s` we still need to find, `j` sweeps through `t` once, left to right.

- **`j` is the hunter — it never goes backward.** It visits every character of `t` exactly once. The whole outer loop is really just `t`'s traversal; `i` only moves as a side effect.
- **`i` advances only on a match.** When `s[i] == t[j]`, we've found the next needed character in order, so `i` moves to the *next* needed character. The `j++` in the same branch means "this character of `t` is consumed" — it can't be reused for a later character of `s`, which is exactly what "subsequence" requires (order matters, no reuse).
- **On a mismatch, `j` alone advances** — we skip the irrelevant character of `t` and keep hunting.
- **The loop ends when either pointer runs out.** If `i` reached `s.length`, every character of `s` was found in order → `true`. If `j` ran out first, `t` ended while we were still missing characters → `i != s.length` → `false`.
- **Why greedy is safe:** matching `s[i]` to the *earliest* occurrence in `t` can never hurt. Any later occurrence would only leave fewer characters for the rest of `s` — so "take the first match" is provably optimal (a classic exchange argument).

Trace `s = "abc", t = "ahbgdc"`: `'a'` found at `t[0]`, `'b'` at `t[2]`, `'c'` at `t[5]` — three matches in order, `i = 3` → `true` ✓.

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
