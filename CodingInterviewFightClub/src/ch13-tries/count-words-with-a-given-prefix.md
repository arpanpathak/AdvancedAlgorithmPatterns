# 13.4 Count Words With A Given Prefix

> **Source:** [`src/main/kotlin/trie/CountWordsWithAGivenPrefix_Trie.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/trie/CountWordsWithAGivenPrefix_Trie.kt)
> **Pattern:** prefix-count per node · **Core page**

## The Problem

Given an array of words and a prefix, return the **number of words** that have that prefix (each word counted once).

- Constraints: $1 \le n \le 100$; word lengths ≤ 100.

## Examples

```
Input:  words = ["pay","attention","practice","attend"], prefix = "at"
Output: 2    (attention, attend)

Input:  words = ["leetcode","win","loops","success"], prefix = "code"
Output: 0
```

## Intuition — count words as they pass *through* each node

The naive answer counts by scanning all words and checking `startsWith` — $O(n \cdot L)$ per query. The trie upgrade is a per-node **`prefixCount`**: every time a word is inserted, **increment the counter on every node along its path** (including the word's final node). Then:

- node for prefix `"at"` has `prefixCount` = "how many words pass through the path a-t";
- a query is just: walk to the prefix's node, read `prefixCount` — $O(L)$ per query, *zero* scanning.

**Why does counting on insert work?** "A word has prefix P" ⟺ "the word's path passes through P's node." Incrementing every path node at insert time means each word contributes +1 to exactly the nodes of *its own* prefixes — so `prefixCount` at a node is literally the number of stored words with that prefix. The count is computed once, shared by all future queries.

**The self-prefix subtlety:** a word counts for its *own* full length too — the repo increments on the final character's node as well (`current = current.children.getOrPut(...)` then `current.prefixCount++`). So `prefixCount("pay")` on the word "pay" returns 1, not 0. (LeetCode 2185 counts each word that *starts with* the prefix — same thing.)

**Why not just store the count in a map at query time?** You *could* scan once and tally prefix counts into a `Map<String, Int>` — $O(n \cdot L)$ once, then O(1) queries. For a *fixed* word set that's fine; the trie version wins when words are added incrementally (each insert maintains all prefix counts for free) or when the follow-up asks for autocomplete (the next pages).

## Approach 1 — Scan and check each word (O(nL) per query)

`words.count { it.startsWith(prefix) }`: simple, and the right answer at small scale — the trie's whole advantage is repeated queries.

## Approach 2 — Trie with per-node counts (the repo's version, optimal)

```kotlin
class CountWordsWithAGivenPrefix_Trie {
    data class TrieNode(
        val ch: Char = '_',
        val children: MutableMap<Char, TrieNode> = mutableMapOf(),
        var prefixCount: Int = 0                        // words passing through this node
    )

    var root = TrieNode()

    /**
     * @param words  word list
     * @param prefix prefix to count
     * @return       number of words with the given prefix
     */
    fun prefixCount(words: Array<String>, prefix: String): Int {
        buildTrie(words, root)
        return findPrefixNode(prefix)?.prefixCount ?: 0
    }

    private fun buildTrie(words: Array<String>, root: TrieNode) {
        for (word in words) {
            var current = root
            for (ch in word) {
                current = current.children.getOrPut(ch) { TrieNode(ch) }
                current.prefixCount++                   // this word passes through here
            }
        }
    }

    private fun findPrefixNode(prefix: String): TrieNode? {
        var current = root
        for (ch in prefix) {
            current = current.children[ch] ?: return null   // prefix absent
        }
        return current
    }
}
```

```java
import java.util.*;

public class CountWordsWithAGivenPrefix {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        int prefixCount = 0;                            // words passing through this node
    }

    /**
     * @param words  word list
     * @param prefix prefix to count
     * @return       number of words with the given prefix
     */
    public int prefixCount(String[] words, String prefix) {
        TrieNode root = new TrieNode();

        for (String word : words) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                node = node.children.computeIfAbsent(c, k -> new TrieNode());
                node.prefixCount++;                     // this word passes through here
            }
        }

        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return 0;                 // prefix absent
        }
        return node.prefixCount;
    }
}
```

```cpp
#include <string>
#include <unordered_map>
#include <vector>

class CountWordsWithAGivenPrefix {
    struct Node {
        std::unordered_map<char, Node*> children;
        int prefixCount = 0;                            // words passing through this node
    };

public:
    /**
     * @param words  word list
     * @param prefix prefix to count
     * @return       number of words with the given prefix
     */
    int prefixCount(std::vector<std::string>& words, std::string prefix) {
        Node* root = new Node();

        for (auto& word : words) {
            Node* node = root;
            for (char c : word) {
                if (!node->children.count(c)) node->children[c] = new Node();
                node = node->children[c];
                node->prefixCount++;                    // this word passes through here
            }
        }

        Node* node = root;
        for (char c : prefix) {
            if (!node->children.count(c)) return 0;     // prefix absent
            node = node->children[c];
        }
        return node->prefixCount;
    }
};
```

```python
def prefix_count(words: list[str], prefix: str) -> int:
    """
    @param words:  word list
    @param prefix: prefix to count
    @return:       number of words with the given prefix
    """
    trie = {}
    for word in words:                       # build the trie with per-node counts
        node = trie
        for c in word:
            if c not in node:
                node[c] = {}
            node = node[c]
            node["count"] = node.get("count", 0) + 1   # this word passes through here

    node = trie
    for c in prefix:
        if c not in node:
            return 0                         # prefix absent
        node = node[c]
    return node.get("count", 0)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param words  word list
    /// @param prefix prefix to count
    /// @return       number of words with the given prefix
    pub fn prefix_count(words: Vec<String>, prefix: String) -> i32 {
        // trie: node id -> (char -> child id, count of words through this node)
        let mut trie: HashMap<usize, HashMap<u8, usize>> = HashMap::new();
        let mut counts: HashMap<usize, i32> = HashMap::new();
        let mut nodes = 1usize;

        for w in words {
            let mut id = 0usize;
            for b in w.bytes() {
                if !trie.contains_key(&id) { trie.insert(id, HashMap::new()); }
                let next = *trie[&id].entry(b).or_insert_with(|| { nodes += 1; nodes - 1 });
                id = next;
                *counts.entry(id).or_insert(0) += 1;      // this word passes through here
            }
        }

        let mut id = 0usize;
        for b in prefix.bytes() {
            match trie.get(&id).and_then(|m| m.get(&b)) {
                Some(&next) => id = next,
                None => return 0,                          // prefix absent
            }
        }
        *counts.get(&id).unwrap_or(&0)
    }
}
```

### 3. `CountWordsWithAGivenPrefix_Trie_FP.kt` — the trie, functionally

[13.4](../ch13-tries/count-words-with-a-given-prefix.md) documents the imperative trie; this file builds the **same trie with `fold`/`getOrPut`** — insertion as a single `fold` expression:

```kotlin
// sketch of the FP trie (CountWordsWithAGivenPrefix_Trie_FP.kt)
// class TrieNode(val children: MutableMap<Char, TrieNode> = mutableMapOf(), var count: Int = 0)
// insert(word): word.fold(root) { node, c -> node.children.getOrPut(c) { TrieNode() } }
//     .also { it.count++ }            — the whole insertion is a fold + also
// countPrefix(prefix): prefix.fold(root) { node, c -> node.children[c] ?: return 0 }
//     .let { it.count }
```

**What's cool:** `fold` over the word *is* the descent — each character steps down a level, `getOrPut` creates missing nodes; `?:"` returns early on a missing prefix. The imperative version (loop + if-null-create) and this are the same walk; the FP version states it as a single expression chain.


## Dry run

**Input:** `words = ["pay","attention","practice","attend"]`, `prefix = "at"`.

```
buildTrie (prefixCount incremented on every node visited):
  "pay":     p(1) -> a(1) -> y(1)
  "attention": a(1) -> t(1) -> t(1) -> e(1) -> n(1) -> t(1) -> i(1) -> o(1) -> n(1)
  "practice": p(2) -> r(1) -> a(2) -> c(1) -> t(2) -> i(2) -> c(1) -> e(1)
  "attend":  a(2) -> t(2) -> t(2) -> e(2) -> n(2) -> d(1)

query "at": walk a(2) -> t(2).  prefixCount = 2 ✓  (attention, attend)

query "code": walk c? root has p,a only -> null -> 0 ✓
```

The magic is the two `t`s: `"attention"` and `"attend"` share the `a -> t -> t` path, so the node at the end of `"at"` counted **both** during their inserts. The count is precomputed; the query is a walk and a read.

## Complexity

**Time.** Build is $O(\text{total chars})$; each query walks the prefix:

$$
T_{\text{build}} = O(\text{total chars}), \qquad T_{\text{query}}(L) = O(L)
$$

**Space.** The trie:

$$
S = O(\text{total chars})
$$

## Variants & follow-ups

- **Search Suggestion System** ([13.5](search-suggestion-system.md)) — the count becomes *collection*: instead of a number, gather the words under a prefix node.
- **Design Autocomplete** ([13.7](design-autocomplete-system.md)) — the count becomes a *hotness score* that queries rank by.
- **Longest Common Prefix** (`src/main/kotlin/trie/LongestCommonPrefix.kt`) — the trie's answer to [9.5](../ch09-strings/longest-common-prefix.md): the longest path from the root where every node has a single child.
- **Interview follow-up:** "Why increment during insert rather than computing at query time?" Incrementing during insert makes each word pay its share *once*; a query-time computation would re-scan all words per query ($O(nL)$ each). The insert-time cost is amortized across all future queries — the "pay once, read forever" pattern that makes tries competitive.
