# 13.8 Word Break II

> **Source:** [`src/main/kotlin/string/backtracking/WordBreak_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/backtracking/WordBreak_II.kt)
> **Pattern:** prefix backtracking · **Core page**

## The Problem

Given `s` and a `wordDict`, return **all sentences** formed by segmenting `s` into dictionary words.

- Constraints: $1 \le |s| \le 20$; dictionary words non-empty.

## Examples

```
Input:  s = "catsanddog", wordDict = ["cat","cats","and","sand","dog"]
Output: ["cats and dog", "cat sand dog"]
```

## Intuition — the [13.2](word-break.md) DP with *paths* instead of a boolean

Word Break I asks "can it be segmented?" — a boolean DP. Word Break II asks *how* — the same prefix idea, but **backtracking records every split**. At each index, try every dictionary prefix; on a hit, recurse from the prefix's end and carry the accumulated sentence:

```kotlin
fun permute(i: Int, current: StringBuilder) {
    if (i == s.length) { result.add(current.toString().trim()); return }

    for (j in i until s.length) {
        val prefix = s.substring(i, j + 1)
        if (set.contains(prefix)) {
            val originalLength = current.length
            current.append(prefix).append(" ")
            permute(j + 1, current)
            current.setLength(originalLength)     // undo
        }
    }
}
```

**Why backtracking and not DP-with-reconstruction?** The [13.2](word-break.md) `dp[i]` table can be extended with parent-pointers, but at |s| ≤ 20 the plain prefix-recursion is simpler and enumerates exactly the valid segmentations. The `setLength(originalLength)` restore is the [12.0](../ch12-backtracking/pattern-primer.md) undo contract on a `StringBuilder`.

**Why the `.trim()` at the end?** Every append adds a trailing space; the accumulated sentence is trimmed once when complete. (The repo's `current` builder is the path; the trim is the finalization.)

## Approach 1 — DP reachability + reconstruction (the [13.2](word-break.md) upgrade)

`dp[i]` boolean plus lists of prefixes per index, then DFS over the back-edges: also correct, more machinery at this size.

## Approach 2 — Prefix backtracking (the repo's version, optimal)

```kotlin
class WordBreak_II {
    /**
     * @param s        the string to segment
     * @param wordDict dictionary words
     * @return         all sentences formed by valid segmentations
     */
    fun wordBreak(s: String, wordDict: List<String?>?): List<String> {
        val set = wordDict?.toSet() ?: emptySet()
        val result = mutableListOf<String>()

        fun permute(i: Int, current: StringBuilder) {
            if (i == s.length) {
                result.add(current.toString().trim())
                return
            }

            for (j in i until s.length) {
                val prefix = s.substring(i, j + 1)
                if (set.contains(prefix)) {
                    val originalLength = current.length
                    current.append(prefix).append(" ")
                    permute(j + 1, current)
                    current.setLength(originalLength)   // undo
                }
            }
        }

        permute(0, StringBuilder())
        return result
    }
}
```

```java
import java.util.*;

public class WordBreakII {
    /**
     * @param s        the string to segment
     * @param wordDict dictionary words
     * @return         all sentences formed by valid segmentations
     */
    public List<String> wordBreak(String s, List<String> wordDict) {
        Set<String> set = new HashSet<>(wordDict);
        List<String> result = new ArrayList<>();
        backtrack(s, 0, new StringBuilder(), set, result);
        return result;
    }

    private void backtrack(String s, int i, StringBuilder cur, Set<String> set, List<String> result) {
        if (i == s.length()) {
            result.add(cur.toString().trim());
            return;
        }

        for (int j = i; j < s.length(); j++) {
            String prefix = s.substring(i, j + 1);
            if (set.contains(prefix)) {
                int len = cur.length();
                cur.append(prefix).append(' ');
                backtrack(s, j + 1, cur, set, result);
                cur.setLength(len);                       // undo
            }
        }
    }
}
```

```cpp
#include <string>
#include <unordered_set>
#include <vector>

class WordBreakII {
    void backtrack(const std::string& s, int i, std::string cur,
                   const std::unordered_set<std::string>& set,
                   std::vector<std::string>& result) {
        if (i == (int)s.size()) {
            result.push_back(cur.substr(0, cur.size() - 1));   // drop the trailing space
            return;
        }

        for (int j = i; j < (int)s.size(); j++) {
            std::string prefix = s.substr(i, j - i + 1);
            if (set.count(prefix)) {
                backtrack(s, j + 1, cur + prefix + " ", set, result);
            }
        }
    }

public:
    /**
     * @param s        the string to segment
     * @param wordDict dictionary words
     * @return         all sentences formed by valid segmentations
     */
    std::vector<std::string> wordBreak(std::string s, std::vector<std::string>& wordDict) {
        std::unordered_set<std::string> set(wordDict.begin(), wordDict.end());
        std::vector<std::string> result;
        backtrack(s, 0, "", set, result);
        return result;
    }
};
```

```python
def word_break(s: str, word_dict: list[str]) -> list[str]:
    """
    @param s:        the string to segment
    @param word_dict: dictionary words
    @return:         all sentences formed by valid segmentations
    """
    words = set(word_dict)
    result = []

    def backtrack(i: int, cur: list[str]) -> None:
        if i == len(s):
            result.append(" ".join(cur))
            return

        for j in range(i, len(s)):
            prefix = s[i:j + 1]
            if prefix in words:
                cur.append(prefix)
                backtrack(j + 1, cur)
                cur.pop()                       # undo

    backtrack(0, [])
    return result
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param s        the string to segment
    /// @param word_dict dictionary words
    /// @return         all sentences formed by valid segmentations
    pub fn word_break(s: String, word_dict: Vec<String>) -> Vec<String> {
        let words: HashSet<&str> = word_dict.iter().map(|w| w.as_str()).collect();
        let mut result = Vec::new();
        let mut cur = Vec::new();

        fn backtrack(s: &str, i: usize, words: &HashSet<&str>,
                     cur: &mut Vec<String>, result: &mut Vec<String>) {
            if i == s.len() {
                result.push(cur.join(" "));
                return;
            }
            for j in (i + 1)..=s.len() {
                let prefix = &s[i..j];
                if words.contains(prefix) {
                    cur.push(prefix.to_string());
                    backtrack(s, j, words, cur, result);
                    cur.pop();                          // undo
                }
            }
        }

        backtrack(&s, 0, &words, &mut cur, &mut result);
        result
    }
}
```

## Dry run

**Input:** `s = "catsanddog"`, `wordDict = ["cat","cats","and","sand","dog"]`.

```
backtrack(0, ""): prefixes: "c","ca","cat" ✓ -> append "cat ".  backtrack(3, "cat ").
  backtrack(3): "s","sa","san","sand" ✓ -> append "sand ".  backtrack(7, "cat sand ").
    backtrack(7): "d","do","dog" ✓ -> append "dog ".  backtrack(10, ...): i == 10 -> add "cat sand dog" ✓.
  undo "sand ".  "sands" ✗, "sandsa" ✗ ... no more at index 3.
undo "cat ".  "cats" ✓ -> append "cats ".  backtrack(4, "cats ").
  backtrack(4): "a","an","and" ✓ -> append "and ".  backtrack(7, "cats and ").
    backtrack(7): "dog" ✓ -> add "cats and dog" ✓.

Output: ["cats and dog", "cat sand dog"] ✓
```

The two valid segmentations diverge at index 3 (`cat` vs `cats`) and reconverge at 7 (`dog`). The `setLength(originalLength)`/`pop()` undo is what lets the recursion explore both branches from the same prefix state — the [12.0](../ch12-backtracking/pattern-primer.md) contract on a mutable path.

## Complexity

**Time.** Exponential in the number of splits (bounded by valid segmentations):

$$
T = O(\text{number of valid sentences} \times |s|)
$$

**Space.** The path + result:

$$
S = O(|s| + \text{output})
$$

## Variants & follow-ups

- **Word Break** ([13.2](word-break.md)) — the boolean DP this page turns into enumeration.
- **Palindrome Partitioning** ([12.5](../ch12-backtracking/palindrome-partitioning.md)) — the same prefix-backtracking shape, with palindrome checks instead of dictionary lookups.
- **Interview follow-up:** "When would DP beat backtracking here?" At |s| > 30 with dense dictionaries, the same segmentations recur via different splits — a memo `map[i] = sentences for s[i..]` collapses them. The repo's plain backtracking is the right call at |s| ≤ 20; name the memo as the scale-up.
