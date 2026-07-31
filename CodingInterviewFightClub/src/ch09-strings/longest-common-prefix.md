# 9.5 Longest Common Prefix

> **Source:** [`src/main/kotlin/string/LongestCommonPrefix.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/LongestCommonPrefix.kt)
> **Pattern:** vertical scan · **Core page**

## The Problem

Given an array of strings, return the **longest common prefix** shared by all of them (or `""` if none).

- Constraints: $1 \le n \le 200$; $0 \le L \le 200$; lowercase letters.

## Examples

```
Input:  strs = ["flower","flow","flight"]   -> Output: "fl"
Input:  strs = ["dog","racecar","car"]      -> Output: ""   (no common first letter)
Input:  strs = ["", "a"]                    -> Output: ""   (empty string short-circuits)
```

## Intuition — a prefix is shared *position by position*

A prefix is the *longest* prefix of all strings, which means: it must be a prefix of **every** string, so its first character must equal every string's first character, its second must equal every string's second, and so on. The prefix stops at the first position where *any* string differs — or where *any* string runs out.

**The vertical scan** (the repo's version): use the first string as the reference. For each position `i` in it, compare `strs[0][i]` against every other string's character at `i`. The first mismatch (or a string shorter than `i`) ends the prefix. This reads characters *column by column* — vertical — and stops at the earliest disagreement.

**Why stop early?** The common prefix can only shrink as you inspect more strings; the moment one string disagrees, no longer prefix can exist. The vertical scan exploits this by stopping at the *first* bad column, whereas a horizontal scan (compare whole strings pairwise) may do wasted work on strings that already agree for a long prefix.

**Why `i >= strs[j].length` is a separate condition?** A shorter string ends the prefix even if all characters so far matched — its entire length is the most it can share.

## Approach 1 — Horizontal scan (pairwise reduce)

`prefix = LCP(prefix, strs[i])` for each word, trimming the prefix down each time: $O(n \cdot L)$ with a smaller constant — but it can re-scan characters the vertical version skipped.

## Approach 2 — Vertical scan (the repo's version, optimal)

```kotlin
class LongestCommonPrefix {
    /**
     * @param strs array of strings
     * @return     longest prefix shared by all of them, "" if none
     */
    fun longestCommonPrefix(strs: Array<String>): String {
        if (strs.isEmpty()) return ""

        // Use the first string as the reference
        for (i in strs[0].indices) {
            val char = strs[0][i]
            // Compare this character with the corresponding character in all other strings
            for (j in 1 until strs.size) {
                // If the current string is shorter or the characters don't match, return the prefix so far
                if (i >= strs[j].length || strs[j][i] != char) {
                    return strs[0].substring(0, i)
                }
            }
        }
        return strs[0]          // no mismatch found: the entire first string is the prefix
    }
}
```

```java
public class LongestCommonPrefix {
    /**
     * @param strs array of strings
     * @return     longest prefix shared by all of them, "" if none
     */
    public String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) return "";

        for (int i = 0; i < strs[0].length(); i++) {
            char c = strs[0].charAt(i);
            for (int j = 1; j < strs.length; j++) {
                if (i >= strs[j].length() || strs[j].charAt(i) != c) {
                    return strs[0].substring(0, i);
                }
            }
        }
        return strs[0];
    }
}
```

```cpp
#include <string>
#include <vector>

class LongestCommonPrefix {
public:
    /**
     * @param strs array of strings
     * @return     longest prefix shared by all of them, "" if none
     */
    std::string longestCommonPrefix(std::vector<std::string>& strs) {
        if (strs.empty()) return "";

        for (int i = 0; i < (int)strs[0].size(); i++) {
            char c = strs[0][i];
            for (int j = 1; j < (int)strs.size(); j++) {
                if (i >= (int)strs[j].size() || strs[j][i] != c) {
                    return strs[0].substr(0, i);
                }
            }
        }
        return strs[0];
    }
};
```

```python
def longest_common_prefix(strs: list[str]) -> str:
    """
    @param strs: array of strings
    @return:     longest prefix shared by all of them, "" if none
    """
    if not strs:
        return ""

    for i, c in enumerate(strs[0]):          # vertical: column by column
        for word in strs[1:]:
            if i >= len(word) or word[i] != c:
                return strs[0][:i]           # first disagreement ends the prefix
    return strs[0]
```

```rust
impl Solution {
    /// @param strs array of strings
    /// @return     longest prefix shared by all of them, "" if none
    pub fn longest_common_prefix(strs: Vec<String>) -> String {
        if strs.is_empty() { return String::new(); }

        let first = strs[0].as_bytes();
        for i in 0..first.len() {
            let c = first[i];
            for word in &strs[1..] {
                let b = word.as_bytes();
                if i >= b.len() || b[i] != c {
                    return strs[0][..i].to_string();    // first disagreement ends the prefix
                }
            }
        }
        strs[0].clone()
    }
}
```

## Dry run

**Input:** `strs = ["flower","flow","flight"]`.

```
reference = "flower"

i=0 'f': 'f' == strs[1][0] 'f'? yes.  'f' == strs[2][0] 'f'? yes.   prefix "f"
i=1 'l': 'l' == "flow"[1] 'l'? yes.    'l' == "flight"[1] 'l'? yes.  prefix "fl"
i=2 'o': 'o' == "flow"[2] 'o'? yes.    'o' == "flight"[2] 'i'? NO -> return "fl" ✓
```

The second example fails at the very first column: `"dog"` vs `"racecar"` — `'d' != 'r'` at `i=0`, so the scan returns `""` after comparing a single character. Early stopping is why the vertical scan rarely touches all $n \cdot L$ characters: it stops at the *shortest* common prefix in the data.

## Complexity

**Time.** Worst case every column of every string (e.g., all strings identical):

$$
T(n, L) = O(n \cdot L)
$$

**Space.** Only the answer substring:

$$
S(n, L) = O(1)
$$

## Variants & follow-ups

- **Sorting-based shortcut** — sort the strings and compare only the *first and last*: identical prefixes force identical first/last; $O(nL \log n)$ but a famous one-liner.
- **Trie version** (`src/main/kotlin/trie/`) — build a trie of all strings; the longest common prefix is the longest path from the root with a single child. The "structured" answer when the interviewer asks for a data-structure approach.
- **Merge String Alternatively / Apply Substitutions** (`src/main/kotlin/string/MergeStringAlternatively.kt`) — zipper-style string building; the same "positional comparison" spirit.
- **Interview follow-up:** "Horizontal vs vertical?" Horizontal (pairwise reduce) never revisits a *whole* string that fully matched, but it can re-check early characters of later strings. Vertical stops at the first disagreement — which is the entire prefix, so it does minimal work when the prefix is short. At $L \le 200$ the difference is noise; state both, implement vertical.
