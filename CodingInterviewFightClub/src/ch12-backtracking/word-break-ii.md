# 12.15 Word Break II

> **Source**: [`src/main/kotlin/string/backtracking/WordBreak_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/backtracking/WordBreak_II.kt)
> **Pattern**: memoized sentence enumeration · **Core page**

## The Problem

All ways to break `s` into dictionary words (space-separated sentences).

- Constraints: length ≤ 20; words ≤ 1000.

## Examples

```
Input:  s = "catsanddog", wordDict = ["cat","cats","and","sand","dog"]
Output: ["cats and dog","cat sand dog"]
```

## Intuition — the [13.2](../ch13-tries/word-break.md) DP's witness: backtrack the splits

At each index, try every dictionary word matching the prefix; recurse on the rest; concatenate:

```kotlin
fun backtrack(start: Int, current: StringBuilder) {
    if (start == s.length) {
        result.add(current.toString().trim())
        return
    }

    for (end in start + 1..s.length) {
        val word = s.substring(start, end)
        if (word in wordSet) {
            current.append("$word ")
            backtrack(end, current)
            current.setLength(current.length - word.length - 1)
        }
    }
}
```

**Why try all matching prefixes?** Every dictionary word that matches the current position is a candidate split — the recursion enumerates all segmentations. The [13.2](../ch13-tries/word-break.md) feasibility check, turned into a witness generator.

## Approach 1 — Naive recursion (the repo's version; exponential without memo)

## Approach 2 — Memoized sentence DP (optimal for repeated suffixes)

`memo[i]` = sentences for `s[i..]`, computed once — the [2.0](../ch02-dynamic-programming/pattern-primer.md) memo pattern.

```kotlin
class WordBreak_II {
    /**
     * @param s        input string
     * @param wordDict dictionary
     * @return         all valid segmentations
     */
    fun wordBreak(s: String, wordDict: List<String?>?): List<String> {
        val set = wordDict?.toSet() ?: emptySet()
        val result = mutableListOf<String>()

        fun permute(i: Int, current: StringBuilder) {
            if (i == s.length) {
                result.add(current.toString().trim())
                return
            }

            for (end in i + 1..s.length) {
                val word = s.substring(i, end)
                if (word in set) {
                    current.append(word).append(' ')
                    permute(end, current)
                    current.setLength(current.length - word.length - 1)
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
    private Map<Integer, List<String>> memo = new HashMap<>();

    private List<String> solve(String s, Set<String> set, int start) {
        if (memo.containsKey(start)) return memo.get(start);
        if (start == s.length()) return Arrays.asList("");

        List<String> result = new ArrayList<>();
        for (int end = start + 1; end <= s.length(); end++) {
            String word = s.substring(start, end);
            if (set.contains(word)) {
                for (String suffix : solve(s, set, end)) {
                    result.add(word + (suffix.isEmpty() ? "" : " " + suffix));
                }
            }
        }
        memo.put(start, result);
        return result;
    }

    /**
     * @param s        input string
     * @param wordDict dictionary
     * @return         all valid segmentations
     */
    public List<String> wordBreak(String s, List<String> wordDict) {
        return solve(s, new HashSet<>(wordDict), 0);
    }
}
```

```cpp
#include <string>
#include <vector>
#include <unordered_set>

class WordBreakII {
public:
    /**
     * @param s        input string
     * @param wordDict dictionary
     * @return         all valid segmentations
     */
    std::vector<std::string> wordBreak(std::string s, std::vector<std::string>& wordDict) {
        std::unordered_set<std::string> set(wordDict.begin(), wordDict.end());
        std::unordered_map<int, std::vector<std::string>> memo;

        std::function<std::vector<std::string>(int)> solve = [&](int start) {
            if (memo.count(start)) return memo[start];
            if (start == (int)s.size()) return std::vector<std::string>{""};

            std::vector<std::string> result;
            for (int end = start + 1; end <= (int)s.size(); end++) {
                std::string word = s.substr(start, end - start);
                if (set.count(word)) {
                    for (auto& suffix : solve(end)) {
                        result.push_back(word + (suffix.empty() ? "" : " " + suffix));
                    }
                }
            }
            return memo[start] = result;
        };

        return solve(0);
    }
};
```

```python
from functools import lru_cache

def word_break(s: str, word_dict: list[str]) -> list[str]:
    """
    @param s:        input string
    @param word_dict: dictionary
    @return:         all valid segmentations
    """
    word_set = set(word_dict)

    @lru_cache(None)
    def solve(start: int) -> list[str]:
        if start == len(s):
            return [""]

        sentences = []
        for end in range(start + 1, len(s) + 1):
            word = s[start:end]
            if word in word_set:
                for suffix in solve(end):
                    sentences.append(word + ("" if not suffix else " " + suffix))

        return sentences

    return solve(0)
```

```rust
use std::collections::{HashMap, HashSet};

impl Solution {
    /// @param s        input string
    /// @param word_dict dictionary
    /// @return         all valid segmentations
    pub fn word_break(s: String, word_dict: Vec<String>) -> Vec<String> {
        let set: HashSet<&str> = word_dict.iter().map(|w| w.as_str()).collect();
        let bytes: Vec<char> = s.chars().collect();

        fn solve(bytes: &Vec<char>, set: &HashSet<&str>, start: usize,
                 memo: &mut HashMap<usize, Vec<String>>) -> Vec<String> {
            if let Some(v) = memo.get(&start) { return v.clone(); }
            if start == bytes.len() { return vec![String::new()]; }

            let mut result = Vec::new();
            let mut word = String::new();

            for end in start..bytes.len() {
                word.push(bytes[end]);
                if set.contains(word.as_str()) {
                    for suffix in solve(bytes, set, end + 1, memo) {
                        if suffix.is_empty() { result.push(word.clone()); }
                        else { result.push(format!("{} {}", word, suffix)); }
                    }
                }
            }
            memo.insert(start, result.clone());
            result
        }

        solve(&bytes, &set, 0, &mut HashMap::new())
    }
}
```

## Dry run

**Input:** `s = "catsanddog", wordDict = ["cat","cats","and","sand","dog"]`.

```
start 0: "cat" in set -> recurse(3).  "cats" in set -> recurse(4).
  (3): "sand" -> (7): "dog" -> (10): [""] -> "sand dog".  => "cat sand dog"
  (4): "and" -> (7): "dog" -> => "cats and dog"
Output: ["cats and dog","cat sand dog"] ✓
```

## Complexity

**Time.** Exponential worst (all segmentations), memoized:

$$
T = O(2^n)
$$

**Space.** Memo + output:

$$
S = O(2^n)
$$

## Variants & follow-ups

- **Word Break** ([13.2](../ch13-tries/word-break.md)) — the feasibility ancestor.
- **Interview follow-up:** "Why is memoization optional here?" The output itself can be exponential — the memo saves recomputation of *shared suffixes*, which is real but bounded by the output size. For length ≤ 20 the naive recursion is fine; the memo is the professional version.
