# 9.2 Group Anagrams

> **Source:** [`src/main/kotlin/string/GroupAnagrams.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/GroupAnagrams.kt)
> **Pattern:** frequency-vector key · **Core page**

## The Problem

Given an array of strings, group the **anagrams** together. The answer may be in any order.

- Constraints: $1 \le n \le 10^4$; $0 \le L \le 100$ (word length); lowercase letters only.

## Examples

```
Input:  strs = ["eat","tea","tan","ate","nat","bat"]
Output: [["bat"],["nat","tan"],["ate","eat","tea"]]

Input:  strs = [""]        -> Output: [[""]]
Input:  strs = ["a"]       -> Output: [["a"]]
```

## Intuition — "same multiset" needs a canonical key

Two words are anagrams iff they share a *canonical form* — something identical for exactly the anagram class. Two classic choices:

1. **Sorted word** — `"eat"` and `"tea"` both canonicalize to `"aet"`. Simple, but sorting each word costs $O(L \log L)$.
2. **Frequency vector** — the `int[26]` count from [9.1](valid-anagram.md), used as a *key*: `"eat"` and `"tea"` both produce `{a:1, e:1, t:1}`. Counting is $O(L)$ per word — faster than sorting — and the repo uses exactly this.

The data structure is a **map from canonical form to word list**: for each word, compute its frequency vector, look up (or create) the bucket, append. What the [primer](pattern-primer.md) calls the *encode-then-equate* move: an equivalence question becomes a *hash lookup* question.

**Why a `List<Int>` key and not a `String`?** In Kotlin the repo builds `count.toList()` — a 26-element vector — and uses it directly as the map key (lists have structural equality). Java/C++/Python below use the stringified counts (`"1#0#..."`) or the sorted word; any canonical form that is equal exactly for anagram classes works.

**The early trap:** using the *set of characters* instead of the counts — `"aab"` and `"abb"` have the same character set `{a,b}` but are not anagrams. The frequency vector distinguishes them; a set does not. Say this distinction unprompted.

## Approach 1 — Sort each word, group by sorted form

`map[sorted(word)] += word`: $O(n \cdot L \log L)$ total. Simpler to read; the frequency-vector version trades the log for a constant.

## Approach 2 — Frequency-vector keys (the repo's version, optimal)

```kotlin
class GroupAnagrams {
    /**
     * @param strs array of words
     * @return     words grouped by anagram class
     */
    fun groupAnagrams(strs: Array<String>): List<List<String>> {
        val anagramMap = mutableMapOf<List<Int>, MutableList<String>>()  // frequency vector -> bucket

        for (word in strs) {
            val count = IntArray(26)
            word.forEach { count[it - 'a']++ }           // canonical form: the counts
            anagramMap.getOrPut(count.toList()) { mutableListOf() }.add(word)
        }
        return anagramMap.values.toList()
    }
}
```

```java
import java.util.*;

public class GroupAnagrams {
    /**
     * @param strs array of words
     * @return     words grouped by anagram class
     */
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();

        for (String word : strs) {
            int[] count = new int[26];
            for (char c : word.toCharArray()) count[c - 'a']++;

            StringBuilder key = new StringBuilder();     // canonical form: "1#2#0#..."
            for (int c : count) key.append(c).append('#');
            map.computeIfAbsent(key.toString(), k -> new ArrayList<>()).add(word);
        }
        return new ArrayList<>(map.values());
    }
}
```

```cpp
#include <string>
#include <unordered_map>
#include <vector>

class GroupAnagrams {
public:
    /**
     * @param strs array of words
     * @return     words grouped by anagram class
     */
    std::vector<std::vector<std::string>> groupAnagrams(std::vector<std::string>& strs) {
        std::unordered_map<std::string, std::vector<std::string>> map;

        for (auto& word : strs) {
            int count[26] = {0};
            for (char c : word) count[c - 'a']++;

            std::string key;                             // canonical form: counts concatenated
            for (int c : count) key += std::to_string(c) + "#";
            map[key].push_back(word);
        }

        std::vector<std::vector<std::string>> result;
        for (auto& [_, bucket] : map) result.push_back(bucket);
        return result;
    }
};
```

```python
def group_anagrams(strs: list[str]) -> list[list[str]]:
    """
    @param strs: array of words
    @return:     words grouped by anagram class
    """
    buckets: dict[tuple[int, ...], list[str]] = {}
    for word in strs:
        count = [0] * 26
        for c in word:
            count[ord(c) - ord('a')] += 1
        key = tuple(count)                     # canonical form: the counts
        buckets.setdefault(key, []).append(word)
    return list(buckets.values())
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param strs array of words
    /// @return     words grouped by anagram class
    pub fn group_anagrams(strs: Vec<String>) -> Vec<Vec<String>> {
        let mut buckets: HashMap<[i32; 26], Vec<String>> = HashMap::new();

        for word in strs {
            let mut count = [0i32; 26];
            for b in word.bytes() {
                count[(b - b'a') as usize] += 1;
            }
            buckets.entry(count).or_default().push(word);   // canonical form: the counts
        }
        buckets.into_values().collect()
    }
}
```

### 1. `GroupAnagrams.kt` — the `List<Int>` key

[9.2](../ch09-strings/group-anagrams.md) uses a frequency-String key (`"1#2#0#..."`). This file uses the **frequency list itself** as the key — `count.toList()` — relying on `List<Int>`'s structural equality:

```kotlin
class GroupAnagrams {
    fun groupAnagrams(strs: Array<String>): List<List<String>> {
        val anagramMap = mutableMapOf<List<Int>, MutableList<String>>()  // List<Int> as the key!
        for (word in strs) {
            val count = IntArray(26)
            word.forEach { count[it - 'a']++ }
            anagramMap.getOrPut(count.toList()) { mutableListOf() }.add(word)
        }
        return anagramMap.values.toList()
    }
}
```

**What's cool:** no string serialization — the `IntArray` is converted to a `List<Int>` whose `equals`/`hashCode` are structural (contents, not identity). `getOrPut(count.toList()) { ... }` folds create-and-add into one call. The [9.2](../ch09-strings/group-anagrams.md) String-key is debuggable (printable); this key is zero-encoding.


## Dry run

**Input:** `strs = ["eat","tea","tan","ate","nat","bat"]`.

```
"eat": count = {a:1,e:1,t:1} -> key -> bucket["eat"]
"tea": count = {a:1,e:1,t:1} -> same key -> bucket["eat","tea"]
"tan": count = {a:1,n:1,t:1} -> new key -> bucket["tan"]
"ate": count = {a:1,e:1,t:1} -> same as eat -> bucket["eat","tea","ate"]
"nat": count = {a:1,n:1,t:1} -> same as tan -> bucket["tan","nat"]
"bat": count = {a:1,b:1,t:1} -> new key -> bucket["bat"]

buckets: {"a1e1t1": ["eat","tea","ate"], "a1n1t1": ["tan","nat"], "a1b1t1": ["bat"]}
Output: [["eat","tea","ate"],["tan","nat"],["bat"]] ✓
```

The key insight in action: `"eat"` and `"tea"` never needed to be *compared* — they both produced the identical 26-count vector, so the hash map did the grouping. And `"tan"` vs `"bat"`: both length-3 with `a`,`t`, but different third letters — different vectors, different buckets.

## Complexity

**Time.** Each word counted once ($O(L)$) and hashed:

$$
T(n, L) = O(n \cdot L)
$$

**Space.** The map holds every word:

$$
S(n, L) = O(n \cdot L)
$$

## Variants & follow-ups

- **Valid Anagram** ([9.1](valid-anagram.md)) — the single-pair case; this page is the "many pairs at once" generalization.
- **Count Words With A Given Prefix** (`src/main/kotlin/string/CountWordsWithAGivenPrefix.kt`) — grouping by a *prefix* instead of a frequency class: a trie (`src/main/kotlin/trie/`) is the structured version of the same bucket idea.
- **Find Duplicate File In System / grouping by content** — any "group by canonical form" problem is this skeleton.
- **Interview follow-up:** "Why is the frequency vector better than sorting each word?" Sorting is $O(L \log L)$ per word; counting is $O(L)$. Over $10^4$ words of length 100 that is a constant-factor (but real) difference — and counting also makes the "what exactly defines an anagram?" reasoning explicit. For very short words the sort is simpler; say both.
