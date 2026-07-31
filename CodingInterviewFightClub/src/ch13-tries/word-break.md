# 13.2 Word Break

> **Source:** [`src/main/kotlin/trie/WordBreak_I.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/trie/WordBreak_I.kt)
> **Pattern:** trie + memoized DFS · **Core page**

## The Problem

Given a string `s` and a dictionary `wordDict`, return `true` if `s` can be **segmented** into a space-separated sequence of dictionary words (each word usable any number of times).

- Constraints: $1 \le n \le 300$; $1 \le$ dict size $\le 1000$; lowercase letters.

## Examples

```
Input:  s = "leetcode", wordDict = ["leet","code"]      -> Output: true   (leet | code)
Input:  s = "applepenapple", wordDict = ["apple","pen"] -> Output: true
Input:  s = "catsandog", wordDict = ["cats","dog","sand","and","cat"] -> Output: false
```

## Intuition — "can `s[i..]` be segmented?" is a recursive question

Define `canSegment(i)` = "the suffix `s[i..]` is segmentable." Then:

- base case: `canSegment(n) = true` (empty suffix trivially segments);
- otherwise: `canSegment(i) = true` iff there is some word `w` matching `s[i..]` **and** `canSegment(i + w.length)`.

That's the DP structure from [Chapter 2](../ch02-dynamic-programming/index.md) — the "who starts here" recursion. The two implementation choices:

1. **Hash set of the dictionary** — for each position, try every dictionary word as a prefix: $O(n \cdot \text{dictSize} \cdot L)$.
2. **Trie** (the repo's version) — walk the trie from position `i` *character by character*: every `isWord` node hit is a candidate word ending at some `j`, then recurse `canSegment(j+1)`. The trie *prunes* the dictionary scan — a dead character branch stops the walk immediately instead of testing words pointlessly.

**Why memoize?** `canSegment(i)` can be reached from many prefixes (`"cat"` and `"cats"` both lead to position 3 in the example above). Without memoization the recursion is exponential; with a `dp[i]` map it's polynomial — the standard "DFS + memo" pattern.

**Why does the trie help?** The inner loop walks `s[j]` through the trie; when `s[j]` has no trie child, `break` — no dictionary word can start at `i` through that path. The trie turns "is this prefix a dictionary word?" into an $O(L)$ walk instead of a scan of 1000 words.

## Approach 1 — DP with a hash set

`dp[i] = any word w in dict with s.startsWith(w, i) && dp[i + w.length]`: $O(n \cdot d \cdot L)$. Simple, correct — the trie version below is the "make the inner test faster" upgrade.

## Approach 2 — Trie + memoized DFS (the repo's version, optimal)

```kotlin
class WordBreak_I {
    data class TrieNode(
        val children: MutableMap<Char, TrieNode> = mutableMapOf(),
        var endOfWord: Boolean = false
    )

    /**
     * @param s       string to segment
     * @param wordDict dictionary of allowed words
     * @return        true iff s can be split into dictionary words
     */
    fun wordBreak(s: String, wordDict: List<String>): Boolean {
        val root = TrieNode()

        // Build the Trie from the word dictionary
        wordDict.forEach { word ->
            var node = root
            word.forEach { char ->
                node = node.children.getOrPut(char) { TrieNode() }
            }
            node.endOfWord = true
        }

        // dp[i] = can s[i..] be segmented? (memo)
        val dp = mutableMapOf<Int, Boolean>().apply { this[s.length] = true }  // empty suffix: yes

        fun canSegment(i: Int): Boolean {
            dp[i]?.let { return it }                     // memoized

            var node = root
            for (j in i until s.length) {
                node = node.children[s[j]] ?: break      // no word can start here through this char

                if (node.endOfWord && canSegment(j + 1)) {   // found a word; is the rest segmentable?
                    return true.also { dp[i] = it }
                }
            }
            return false.also { dp[i] = it }
        }

        return canSegment(0)
    }
}
```

```java
import java.util.*;

public class WordBreak {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean endOfWord;
    }

    /**
     * @param s       string to segment
     * @param wordDict dictionary of allowed words
     * @return        true iff s can be split into dictionary words
     */
    public boolean wordBreak(String s, List<String> wordDict) {
        TrieNode root = new TrieNode();
        for (String w : wordDict) {                        // build the trie
            TrieNode node = root;
            for (char c : w.toCharArray()) {
                node.children.computeIfAbsent(c, k -> new TrieNode());
                node = node.children.get(c);
            }
            node.endOfWord = true;
        }

        Map<Integer, Boolean> dp = new HashMap<>();
        dp.put(s.length(), true);                          // empty suffix: yes

        Deque<Integer> stack = new ArrayDeque<>();         // iterative memoized DFS
        // (recursive version mirrors the Kotlin below; iterative avoids deep stacks)
        return canSegment(0, s, root, dp);
    }

    private boolean canSegment(int i, String s, TrieNode root, Map<Integer, Boolean> dp) {
        if (dp.containsKey(i)) return dp.get(i);           // memoized

        TrieNode node = root;
        for (int j = i; j < s.length(); j++) {
            node = node.children.get(s.charAt(j));
            if (node == null) break;                       // no word can start here
            if (node.endOfWord && canSegment(j + 1, s, root, dp)) {
                dp.put(i, true);
                return true;
            }
        }
        dp.put(i, false);
        return false;
    }
}
```

```cpp
#include <string>
#include <unordered_map>
#include <vector>

class WordBreak {
    struct Node {
        std::unordered_map<char, Node*> children;
        bool endOfWord = false;
    };

    bool canSegment(int i, const std::string& s, Node* root,
                    std::vector<int>& dp) {
        if (dp[i] != -1) return dp[i];                     // memoized

        Node* node = root;
        for (int j = i; j < (int)s.size(); j++) {
            if (!node->children.count(s[j])) break;        // no word can start here
            node = node->children[s[j]];
            if (node->endOfWord && canSegment(j + 1, s, root, dp)) {
                dp[i] = 1;
                return true;
            }
        }
        dp[i] = 0;
        return false;
    }

public:
    /**
     * @param s       string to segment
     * @param wordDict dictionary of allowed words
     * @return        true iff s can be split into dictionary words
     */
    bool wordBreak(std::string s, std::vector<std::string>& wordDict) {
        Node* root = new Node();
        for (auto& w : wordDict) {                         // build the trie
            Node* node = root;
            for (char c : w) {
                if (!node->children.count(c)) node->children[c] = new Node();
                node = node->children[c];
            }
            node->endOfWord = true;
        }

        std::vector<int> dp(s.size() + 1, -1);
        dp[s.size()] = 1;                                  // empty suffix: yes
        return canSegment(0, s, root, dp);
    }
};
```

```python
def word_break(s: str, word_dict: list[str]) -> bool:
    """
    @param s:        string to segment
    @param word_dict: dictionary of allowed words
    @return:         true iff s can be split into dictionary words
    """
    trie = {}
    for word in word_dict:                       # build the trie
        node = trie
        for c in word:
            node = node.setdefault(c, {})
        node["#"] = True                         # word-end marker

    from functools import lru_cache

    @lru_cache(None)
    def can_segment(i: int) -> bool:
        if i == len(s):
            return True                          # empty suffix: yes
        node = trie
        for j in range(i, len(s)):
            if s[j] not in node:
                break                            # no word can start here
            node = node[s[j]]
            if "#" in node and can_segment(j + 1):
                return True
        return False

    return can_segment(0)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param s        string to segment
    /// @param word_dict dictionary of allowed words
    /// @return         true iff s can be split into dictionary words
    pub fn word_break(s: String, word_dict: Vec<String>) -> bool {
        let bytes = s.as_bytes();
        // trie as nested maps: node -> (char -> child, "#" -> word end)
        let mut trie: HashMap<usize, HashMap<u8, usize>> = HashMap::new();
        let mut nodes = 1;                         // node 0 = root
        for w in word_dict {
            let mut id = 0;
            for b in w.bytes() {
                if !trie.contains_key(&id) { trie.insert(id, HashMap::new()); }
                let next = *trie[&id].entry(b).or_insert_with(|| { nodes += 1; nodes - 1 });
                id = next;
            }
            trie.entry(id).or_default().insert(b'#', 0);   // word-end marker
        }

        fn dfs(i: usize, bytes: &[u8], trie: &HashMap<usize, HashMap<u8, usize>>,
               memo: &mut Vec<Option<bool>>) -> bool {
            if i == bytes.len() { return true; }        // empty suffix: yes
            if let Some(v) = memo[i] { return v; }

            let mut id = 0;
            let mut result = false;
            for j in i..bytes.len() {
                match trie.get(&id).and_then(|m| m.get(&bytes[j])) {
                    None => break,                       // no word can start here
                    Some(&next) => id = next,
                }
                if trie.get(&id).map_or(false, |m| m.contains_key(&b'#'))
                    && dfs(j + 1, bytes, trie, memo) {
                    result = true;
                    break;
                }
            }
            memo[i] = Some(result);
            result
        }

        let mut memo = vec![None; bytes.len() + 1];
        dfs(0, &bytes, &trie, &mut memo)
    }
}
```

## Dry run

**Input:** `s = "catsandog"`, `wordDict = ["cats","dog","sand","and","cat"]`.

```
canSegment(0) "catsandog":
  walk trie: c -> a -> t -> s (endOfWord: "cats"!) -> canSegment(4) "andog":
    walk: a -> n -> d (endOfWord: "and"!) -> canSegment(7) "og":
      walk: o (no child 'o' after... 'o' at root? no) -> break -> false
    walk: d (from 4): 'd' child of root? no -> break -> false
    -> false
  continue walk from 0: c -> a -> t (endOfWord: "cat"!) -> canSegment(3) "sandog":
    walk: s -> a -> n -> d (endOfWord: "sand"!) -> canSegment(7) "og": false (as before)
    walk: d (from 3): no -> break -> false
    -> false
  -> false

Result: false ✓   (no segmentation covers "og" — the dictionary lacks any word for it)
```

The interesting pruning: `canSegment(4)` and `canSegment(7)` are reached from *multiple* parents (via `"cats"`/`"cat"` and `"sand"`/`"and"`) — the memo makes those repeated calls $O(1)$ instead of re-walking. Without it, this tiny example already revisits the same suffixes several times.

## Complexity

**Time.** $n$ positions × up to $n$ trie steps each:

$$
T(n, L) = O(n^2)
$$

(plus $O(\text{dict chars})$ to build the trie).

**Space.** Trie + memo:

$$
S = O(\text{dict chars} + n)
$$

## Variants & follow-ups

- **Word Break II** (`src/main/kotlin/string/backtracking/WordBreak_II.kt`) — *enumerate all* segmentations instead of the boolean: same trie walk, but `canSegment` collects sentences (backtracking over the same positions).
- **Word Squares** ([13.6](word-squares.md)) — the trie as a prefix index driving a backtracking search over *rows* of a square.
- **Partition Equal Subset Sum / other DP** — the "DFS + memo over positions" skeleton is the same; the trie is the dictionary-specific accelerator.
- **Interview follow-up:** "Why does the trie beat scanning the dictionary at every position?" A dictionary scan tests each word against `s[i..]` — up to $d$ *prefix tests* per position. The trie walk tests characters once: a mismatch at character `k` kills *all* dictionary words sharing that prefix, which is exactly the shared-prefix pruning tries exist for.
