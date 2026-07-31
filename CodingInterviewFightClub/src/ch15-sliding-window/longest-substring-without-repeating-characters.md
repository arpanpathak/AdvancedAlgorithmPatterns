# 15.1 Longest Substring Without Repeating Characters

> **Source:** [`src/main/kotlin/sliding_window/LongestSubstringWithoutRepeatingCharacter.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sliding_window/LongestSubstringWithoutRepeatingCharacter.kt)
> **Pattern:** last-index map · **Core page**

## The Problem

Given a string `s`, return the length of the **longest substring without repeating characters**.

- Constraints: $0 \le n \le 5 \times 10^4$; printable ASCII.

## Examples

```
Input:  s = "abcabcbb"   -> Output: 3   ("abc")
Input:  s = "bbbbb"      -> Output: 1   ("b")
Input:  s = "pwwkew"     -> Output: 3   ("wke")
```

## Intuition — a repeat *teleports* the window start

Two pointers bound the current no-repeat window `[left, right]`. As `right` advances, the only thing that can invalidate the window is a character already inside it. The clever part: **we don't shrink one step at a time — we jump `left` directly past the previous occurrence.**

```
lastIndex[c] = the most recent position of c (else -1)
for right in s.indices:
    left = max(left, lastIndex[s[right]] + 1)   # repeat -> jump past it
    max = max(max, right - left + 1)
    lastIndex[s[right]] = right                 # remember for the future
```

**Why `max` on the left update?** The stored `lastIndex` may point *behind* the current window (that occurrence already left). `max(left, last+1)` prevents the window from ever expanding backward — the "teleport" only moves forward.

**Why the jump instead of a while-loop?** The condition "no repeats" is fully determined by the last occurrence — no other state matters. So the shrink is a single `max` instead of a loop. This is the fastest variant of the shrink-until-valid template ([15.0](pattern-primer.md)): the invalidator is *local*, so the fix is direct.

**The `IntArray(256)`** — one slot per ASCII value (the repo uses `s[i].code` as the index). For lowercase-only, `int[26]` with `c - 'a'` works; the 256-array handles any ASCII without a map.

## Approach 1 — Brute force all substrings (O(n^3))

Check every substring for repeats: the "why we need windows" baseline.

## Approach 2 — Last-index jump (the repo's version, optimal)

```kotlin
class LongestSubstringWithoutRepeatingCharacter {
    /**
     * @param s input string
     * @return  length of the longest substring with no repeated characters
     */
    fun lengthOfLongestSubstring(s: String): Int {
        val lastIndex = IntArray(256) { -1 }       // last position of each ASCII char
        var max = 0
        var windowStart = 0

        for (i in s.indices) {
            windowStart = maxOf(windowStart, lastIndex[s[i].code] + 1)   // jump past the repeat
            max = maxOf(max, i - windowStart + 1)
            lastIndex[s[i].code] = i               // remember for the future
        }
        return max
    }
}
```

```java
public class LongestSubstringWithoutRepeatingCharacters {
    /**
     * @param s input string
     * @return  length of the longest substring with no repeated characters
     */
    public int lengthOfLongestSubstring(String s) {
        int[] lastIndex = new int[256];
        Arrays.fill(lastIndex, -1);
        int max = 0, start = 0;

        for (int i = 0; i < s.length(); i++) {
            start = Math.max(start, lastIndex[s.charAt(i)] + 1);   // jump past the repeat
            max = Math.max(max, i - start + 1);
            lastIndex[s.charAt(i)] = i;            // remember for the future
        }
        return max;
    }
}
```

```cpp
#include <string>
#include <vector>

class LongestSubstringWithoutRepeatingCharacters {
public:
    /**
     * @param s input string
     * @return  length of the longest substring with no repeated characters
     */
    int lengthOfLongestSubstring(std::string s) {
        std::vector<int> lastIndex(256, -1);
        int max = 0, start = 0;

        for (int i = 0; i < (int)s.size(); i++) {
            start = std::max(start, lastIndex[(unsigned char)s[i]] + 1);   // jump past the repeat
            max = std::max(max, i - start + 1);
            lastIndex[(unsigned char)s[i]] = i;    // remember for the future
        }
        return max;
    }
};
```

```python
def length_of_longest_substring(s: str) -> int:
    """
    @param s: input string
    @return:  length of the longest substring with no repeated characters
    """
    last_index = {}
    start = 0
    best = 0

    for i, c in enumerate(s):
        if c in last_index:
            start = max(start, last_index[c] + 1)   # jump past the repeat
        best = max(best, i - start + 1)
        last_index[c] = i                           # remember for the future
    return best
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param s input string
    /// @return  length of the longest substring with no repeated characters
    pub fn length_of_longest_substring(s: String) -> i32 {
        let mut last_index: HashMap<u8, usize> = HashMap::new();
        let mut start = 0usize;
        let mut best = 0;

        for (i, b) in s.bytes().enumerate() {
            if let Some(&prev) = last_index.get(&b) {
                start = start.max(prev + 1);        // jump past the repeat
            }
            best = best.max(i - start + 1);
            last_index.insert(b, i);                // remember for the future
        }
        best as i32
    }
}
```

## Dry run

**Input:** `s = "pwwkew"`.

```
lastIndex = all -1, start = 0, max = 0

i=0 'p': start = max(0, -1+1) = 0.  max = max(0, 0-0+1) = 1.  last['p']=0
i=1 'w': start = 0.                  max = 2.  last['w']=1
i=2 'w': last['w']=1 -> start = max(0, 2) = 2.  max = max(2, 0+1)=2.  last['w']=2
i=3 'k': start = 2.                  max = max(2, 2) = 2.  last['k']=3
i=4 'e': start = 2.                  max = max(2, 3) = 3.  last['e']=4
i=5 'w': last['w']=2 -> start = max(2, 3) = 3.  max = max(3, 3) = 3.

Output: 3 ✓   ("wke")
```

The teleport at i=5 is the whole trick: `'w'` last appeared at index 2, so the window start jumps from 2 to 3 — past the duplicate — in one `max` instead of a shrink loop. The window `[3,5] = "wke"` is valid and length 3.

## Complexity

**Time.** One pass, O(1) per character:

$$
T(n) = O(n)
$$

**Space.** The last-index array/map (bounded):

$$
S(n) = O(|\Sigma|) \subseteq O(n)
$$

## Variants & follow-ups

- **Longest Substring With At Most K Distinct Characters** — the same window with a *count* of distinct characters instead of last-index jumps; the shrink becomes a while-loop.
- **Contains Duplicate II** ([10.2](../ch10-hash-tables/contains-duplicate-ii.md)) — the same last-index map, asked as a boolean instead of a window length.
- **Minimum Window Substring** ([15.2](minimum-window-substring.md)) — the "coverage" version of the same two-pointer idea.
- **Interview follow-up:** "Why does the teleport not skip valid windows?" The jump moves `left` to just past the *previous* occurrence of the repeated character — any window that stayed behind would still contain that duplicate, so it was invalid anyway. Jumping to the first *valid* start is exact, not approximate.
