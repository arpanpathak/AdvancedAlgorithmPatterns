# 9.1 Valid Anagram

> **Source:** [`src/main/kotlin/string/ValidAnagram.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/ValidAnagram.kt)
> **Pattern:** counter array (+1 / -1) · **Core page**

## The Problem

Given two strings `s` and `t`, return `true` if `t` is an **anagram** of `s` — the same characters, same frequencies, any order.

- Constraints: $1 \le n, m \le 5 \times 10^4$; lowercase English letters only.

## Examples

```
Input:  s = "anagram", t = "nagaram"   -> Output: true
Input:  s = "rat",     t = "car"       -> Output: false
```

## Intuition — anagrams are *multiset* equality

"Anagram" is a statement about **character frequencies**, not positions. Two strings are anagrams iff their frequency vectors are identical. The counting is where the cleverness lives:

**The +1 / -1 trick.** Instead of building a frequency map for `s` and a separate one for `t` and comparing them, use *one* array: add 1 for every character of `s`, subtract 1 for every character of `t`. If the strings are anagrams, every count returns to zero; if not, some character's net count is non-zero. One pass, one array, no comparison step.

The length check (`s.length != t.length`) is a free early exit — different lengths can never be anagrams.

**Why `int[26]` and not a `HashMap`?** The alphabet is fixed and small (lowercase → 26). An array indexed by `c - 'a'` gives $O(1)$ access with zero hashing overhead and constant memory. Mentioning "this only works because the alphabet is bounded" is the depth signal — for a general alphabet you'd reach for a map.

## Approach 1 — Sort both strings, compare

`sort(s) == sort(t)`: $O(n \log n)$ time, and it obscures the frequency insight. Correct, but the counter version below is both faster and more instructive.

## Approach 2 — The +1 / -1 counter (the repo's version, optimal)

```kotlin
class ValidAnagram {
    /**
     * @param s first string
     * @param t second string
     * @return  true iff t is an anagram of s
     */
    fun isAnagram(s: String, t: String): Boolean {
        if (s.length != t.length) return false

        val charCount = IntArray(26)

        for (i in s.indices) {
            charCount[s[i] - 'a']++          // s contributes +1
            charCount[t[i] - 'a']--          // t contributes -1
        }

        // Check if all counts are zero
        for (count in charCount) {
            if (count != 0) return false
        }
        return true
    }
}
```

```java
public class ValidAnagram {
    /**
     * @param s first string
     * @param t second string
     * @return  true iff t is an anagram of s
     */
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;

        int[] count = new int[26];
        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i) - 'a']++;      // s contributes +1
            count[t.charAt(i) - 'a']--;      // t contributes -1
        }
        for (int c : count) if (c != 0) return false;
        return true;
    }
}
```

```cpp
#include <string>

class ValidAnagram {
public:
    /**
     * @param s first string
     * @param t second string
     * @return  true iff t is an anagram of s
     */
    bool isAnagram(std::string s, std::string t) {
        if (s.size() != t.size()) return false;

        int count[26] = {0};
        for (int i = 0; i < (int)s.size(); i++) {
            count[s[i] - 'a']++;             // s contributes +1
            count[t[i] - 'a']--;             // t contributes -1
        }
        for (int c : count) if (c != 0) return false;
        return true;
    }
};
```

```python
def is_anagram(s: str, t: str) -> bool:
    """
    @param s: first string
    @param t: second string
    @return:  true iff t is an anagram of s
    """
    if len(s) != len(t):
        return False

    count = [0] * 26
    for i in range(len(s)):
        count[ord(s[i]) - ord('a')] += 1     # s contributes +1
        count[ord(t[i]) - ord('a')] -= 1     # t contributes -1
    return all(c == 0 for c in count)
```

```rust
impl Solution {
    /// @param s first string
    /// @param t second string
    /// @return  true iff t is an anagram of s
    pub fn is_anagram(s: String, t: String) -> bool {
        if s.len() != t.len() { return false; }

        let mut count = [0i32; 26];
        for (a, b) in s.bytes().zip(t.bytes()) {
            count[(a - b'a') as usize] += 1;     // s contributes +1
            count[(b - b'a') as usize] -= 1;     // t contributes -1
        }
        count.iter().all(|&c| c == 0)
    }
}
```

## Dry run

**Input:** `s = "anagram"`, `t = "nagaram"`.

```
count = [0]*26
i=0: s[0]='a' +1 -> count[a]=1;   t[0]='n' -1 -> count[n]=-1
i=1: s[1]='n' +1 -> count[n]=0;   t[1]='a' -1 -> count[a]=0
i=2: s[2]='a' +1 -> count[a]=1;   t[2]='g' -1 -> count[g]=-1
i=3: s[3]='g' +1 -> count[g]=0;   t[3]='a' -1 -> count[a]=0
i=4: s[4]='r' +1 -> count[r]=1;   t[4]='r' -1 -> count[r]=0
i=5: s[5]='a' +1 -> count[a]=1;   t[5]='a' -1 -> count[a]=0
i=6: s[6]='m' +1 -> count[m]=1;   t[6]='m' -1 -> count[m]=0
final: all counts 0 -> true ✓
```

Now the failing case `s = "rat"`, `t = "car"`: `count[r]` ends at +1 (no `r` in `t`) — the non-zero check catches it. Every anagram class leaves exactly the zero vector; every non-anagram leaves at least one non-zero entry.

## Complexity

**Time.** One pass, plus a fixed 26-element check:

$$
T(n) = O(n)
$$

**Space.** The counter is a constant-size array:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Group Anagrams** ([9.2](group-anagrams.md)) — the same frequency vector, promoted from "checker" to "hash key".
- **Find All Anagrams In A String** (`src/main/kotlin/string/`) — a *sliding window* over the counter: maintain the window's counts and compare to the target's as the window moves.
- **Is Subsequence / Count Words Which Are Subsequences** (`src/main/kotlin/string/IsSubsequence.kt`) — the opposite question: order *does* matter, so a greedy pointer scan replaces the counter entirely.
- **Interview follow-up:** "Why is a `Map` needed for a general alphabet?" `int[26]` bakes in the lowercase assumption. With Unicode or mixed case, the alphabet is unbounded — a `HashMap<Char, Int>` keeps the same algorithm with $O(\Sigma)$ space instead of $O(1)$. State both, use the array.
