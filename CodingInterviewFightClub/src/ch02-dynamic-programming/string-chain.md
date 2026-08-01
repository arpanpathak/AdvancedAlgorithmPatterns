# 2.41 String Chain

> **Source**: [`src/main/kotlin/string/dynamic_programming/LongestStringChain.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/dynamic_programming/LongestStringChain.kt)
> **Pattern**: sorted-length DP · **Core page**

## The Problem

The longest chain where each word adds one character to the previous (any position).

- Constraints: words ≤ 1000; length ≤ 16.

## Examples

```
Input:  words = ["a","b","ba","bca","bda","bdca"]   -> Output: 4  (a, ba, bda, bdca)
```

## Intuition — sort by length; each word extends its best (word minus one char)

```kotlin
words.sortBy { it.length }
val dp = mutableMapOf<String, Int>()
var maxLen = 1

for (word in words) {
    dp[word] = 1
    for (i in word.indices) {
        val prev = word.removeRange(i, i + 1)
        dp[word] = maxOf(dp[word]!!, (dp[prev] ?: 0) + 1)
    }
    maxLen = maxOf(maxLen, dp[word]!!)
}
return maxLen
```

**Why sort by length?** A predecessor is strictly shorter — processing lengths ascending makes `dp[prev]` final when used. The [2.19](longest-increasing-subsequence.md) chain DP over words.

## Approach 1 — Sorted-length DP (the repo's version, optimal)

```kotlin
class LongestStringChain {
    /**
     * @param words input words
     * @return      longest chain length
     */
    fun longestStrChain(words: Array<String>): Int {
        words.sortBy { it.length }
        val dp = mutableMapOf<String, Int>()
        var maxLen = 1

        for (word in words) {
            dp[word] = 1
            for (i in word.indices) {
                val prev = word.removeRange(i, i + 1)
                dp[word] = maxOf(dp[word]!!, (dp[prev] ?: 0) + 1)
            }
            maxLen = maxOf(maxLen, dp[word]!!)
        }
        return maxLen
    }
}
```

```java
import java.util.*;

public class LongestStringChain {
    /**
     * @param words input words
     * @return      longest chain length
     */
    public int longestStrChain(String[] words) {
        Arrays.sort(words, (a, b) -> a.length() - b.length());
        Map<String, Integer> dp = new HashMap<>();
        int best = 1;

        for (String word : words) {
            int cur = 1;

            for (int i = 0; i < word.length(); i++) {
                String prev = word.substring(0, i) + word.substring(i + 1);
                cur = Math.max(cur, dp.getOrDefault(prev, 0) + 1);
            }
            dp.put(word, cur);
            best = Math.max(best, cur);
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <string>
#include <unordered_map>
#include <algorithm>

class LongestStringChain {
public:
    /**
     * @param words input words
     * @return      longest chain length
     */
    int longestStrChain(std::vector<std::string>& words) {
        std::sort(words.begin(), words.end(),
                  [](auto& a, auto& b) { return a.size() < b.size(); });

        std::unordered_map<std::string, int> dp;
        int best = 1;

        for (auto& word : words) {
            int cur = 1;

            for (int i = 0; i < (int)word.size(); i++) {
                std::string prev = word.substr(0, i) + word.substr(i + 1);
                if (dp.count(prev)) cur = std::max(cur, dp[prev] + 1);
            }
            dp[word] = cur;
            best = std::max(best, cur);
        }
        return best;
    }
};
```

```python
def longest_str_chain(words: list[str]) -> int:
    """
    @param words: input words
    @return:      longest chain length
    """
    dp = {}
    best = 1

    for word in sorted(words, key=len):
        cur = 1
        for i in range(len(word)):
            prev = word[:i] + word[i + 1:]
            cur = max(cur, dp.get(prev, 0) + 1)
        dp[word] = cur
        best = max(best, cur)

    return best
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param words input words
    /// @return      longest chain length
    pub fn longest_str_chain(mut words: Vec<String>) -> i32 {
        words.sort_by_key(|w| w.len());
        let mut dp: HashMap<String, i32> = HashMap::new();
        let mut best = 1;

        for word in &words {
            let mut cur = 1;
            let bytes: Vec<char> = word.chars().collect();

            for i in 0..bytes.len() {
                let prev: String = bytes[..i].iter().chain(&bytes[i + 1..]).collect();
                cur = cur.max(dp.get(&prev).copied().unwrap_or(0) + 1);
            }
            dp.insert(word.clone(), cur);
            best = best.max(cur);
        }
        best
    }
}
```

## Dry run

**Input:** `words = ["a","b","ba","bca","bda","bdca"]`.

```
sorted: a, b, ba, bca, bda, bdca.
a: prev "" -> 1.  b: 1.  ba: prev a -> 2 (or b -> 2).
bca: prev ba -> 3 (ca? no).  bda: prev ba -> 3.
bdca: prev bca -> 4 (bda -> 4 too).
Output: 4 ✓
```

## Complexity

**Time.** Words × length:

$$
T(n, L) = O(n \cdot L^2)
$$

**Space.** The map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Longest Increasing Subsequence** ([2.19](longest-increasing-subsequence.md)) — the numeric ancestor.
- **Interview follow-up:** "Why must predecessors be strictly shorter?" A chain step adds exactly one char — the length order guarantees each word's predecessors were processed, so their dp values are final.
