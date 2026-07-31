# 10.5 First Unique Character

> **Source:** [`src/main/kotlin/hashtable/FirstUniqueCharacter.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/hashtable/FirstUniqueCharacter.kt)
> **Pattern:** frequency map · **Core page**

## The Problem

Given a string `s`, return the index of the **first non-repeating character**, or `-1` if none exists.

- Constraints: $1 \le n \le 10^5$; lowercase English letters only.

## Examples

```
Input:  s = "leetcode"   -> Output: 0   ('l' is first and unique)
Input:  s = "loveleetcode" -> Output: 2  ('v')
Input:  s = "aabb"       -> Output: -1  (every character repeats)
```

## Intuition — two questions need two passes

"First character whose count is 1" requires two facts about each character: **how often does it occur** (a frequency map), and **where is its first position** (its index). The clean decomposition:

1. **Pass 1 — count:** walk `s`, tally every character's frequency.
2. **Pass 2 — find:** walk `s` again, left to right, and return the first index whose character's count is exactly `1`.

The second pass returning the *first* such index is what makes it "first unique", not "any unique" — order matters, so the second walk is over the string (not the map).

**The frequency map vs `int[26]`:** lowercase-only means an `IntArray(26)` works (the [9.1](../ch09-strings/valid-anagram.md) reflex) — $O(1)$ space, no hashing. The repo uses `groupingBy { it }.eachCount()`, the Kotlin idiomatic frequency map, which generalizes to any alphabet.

**Why two passes and not one?** A character's uniqueness isn't known until its *last* occurrence is seen — the count isn't complete until the end of the string. A single pass could guess "unique" at first sighting and be wronged by a later duplicate (e.g., `"aab"` — `'a'` looks unique at index 0 but repeats at index 1). The two-pass shape is the honest cost of "needs global knowledge, answered positionally."

## Approach 1 — Count on the fly with a deque (clever but overkill)

A queue of "maybe-unique" characters with a `Map<Char, Boolean>` of "already duplicated": $O(n)$ and single-pass, but more moving parts than the problem needs. The two-pass version is simpler to reason about and equally fast.

## Approach 2 — Two passes with a frequency map (the repo's version, optimal)

```kotlin
class FirstUniqueCharacter {
    /**
     * @param s input string
     * @return  index of the first character that appears exactly once, -1 if none
     */
    fun firstUniqChar(s: String): Int {
        val count = s.groupingBy { it }.eachCount()    // frequency map
        return s.indexOfFirst { count[it] == 1 }       // first index with count 1
    }
}
```

```java
import java.util.*;

public class FirstUniqueCharacter {
    /**
     * @param s input string
     * @return  index of the first character that appears exactly once, -1 if none
     */
    public int firstUniqChar(String s) {
        int[] count = new int[26];                     // lowercase alphabet
        for (char c : s.toCharArray()) count[c - 'a']++;

        for (int i = 0; i < s.length(); i++) {
            if (count[s.charAt(i) - 'a'] == 1) return i;   // first index with count 1
        }
        return -1;
    }
}
```

```cpp
#include <string>
#include <vector>

class FirstUniqueCharacter {
public:
    /**
     * @param s input string
     * @return  index of the first character that appears exactly once, -1 if none
     */
    int firstUniqChar(std::string s) {
        int count[26] = {0};                           // lowercase alphabet
        for (char c : s) count[c - 'a']++;

        for (int i = 0; i < (int)s.size(); i++) {
            if (count[s[i] - 'a'] == 1) return i;      // first index with count 1
        }
        return -1;
    }
};
```

```python
def first_uniq_char(s: str) -> int:
    """
    @param s: input string
    @return:  index of the first character that appears exactly once, -1 if none
    """
    from collections import Counter

    count = Counter(s)                       # frequency map
    for i, c in enumerate(s):
        if count[c] == 1:                    # first index with count 1
            return i
    return -1
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param s input string
    /// @return  index of the first character that appears exactly once, -1 if none
    pub fn first_uniq_char(s: String) -> i32 {
        let mut count: HashMap<char, i32> = HashMap::new();
        for c in s.chars() {
            *count.entry(c).or_insert(0) += 1;      // frequency map
        }

        for (i, c) in s.chars().enumerate() {
            if count[&c] == 1 {                     // first index with count 1
                return i as i32;
            }
        }
        -1
    }
}
```

## Dry run

**Input:** `s = "loveleetcode"`.

```
Pass 1 — counts:
  l:2, o:2, v:1, e:4, t:1, c:1, d:1

Pass 2 — first index with count == 1:
  i=0 'l' count 2 -> no
  i=1 'o' count 2 -> no
  i=2 'v' count 1 -> return 2 ✓
```

The two-pass necessity is visible: `'l'` at index 0 *looks* unique at first glance — but its count only settles at 2 after the whole string is seen. Any single-pass "first unique" guess would have returned 0 for `"love..."` and been wrong.

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** The frequency map (bounded by alphabet size):

$$
S(n) = O(|\Sigma|) \subseteq O(n)
$$

## Variants & follow-ups

- **First Unique Character In A Stream** — the streaming version: a deque of "candidate uniques" + a duplicated-flag map; the "single-pass" machinery this page deliberately avoids.
- **Unique Number Of Occurrences** (`src/main/kotlin/array/hashtable/UniqueNumberOfOccurences.kt`) — same counting pass, but the *counts* become a set for a different question.
- **Group Anagrams** ([9.2](../ch09-strings/group-anagrams.md)) — the count map promoted to a grouping key.
- **Interview follow-up:** "Why two passes instead of one?" Because "unique" is a global fact (depends on the whole string) while "first" is a local one (depends on position). The frequency pass computes the global facts; the index pass applies them positionally. A one-pass version must keep *both* structures rolling (the deque approach) — more code for the same $O(n)$.
