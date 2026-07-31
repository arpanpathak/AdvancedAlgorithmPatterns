# 13.7 Design Auto Complete System

> **Source:** [`src/main/kotlin/trie/AutoCompleteSystem.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/trie/AutoCompleteSystem.kt)
> **Pattern:** hotness-ranked suggestions · **Core page**

## The Problem

Design an autocomplete system. Given a history of `sentences` with usage `times`, an `input(c)` stream builds a prefix; for each character, return the **top 3 sentences by hotness** (times used) that start with the current prefix (ties: lexicographically). Inputting `'#'` commits the current sentence (incrementing its hotness) and resets the buffer.

- Constraints: ≤ 100 sentences; input streams up to $10^4$ characters.

## Examples

```
AutocompleteSystem(["i love you","island","ironman","i love leetcode"], [5,3,2,2]);
input('i')  -> ["i love you","island","i love leetcode"]   (hotness 5,3,2)
input(' ')  -> ["i love you","i love leetcode"]            (only two start with "i ")
input('a')  -> []                                          (nothing starts with "i a")
input('#')  -> commits "i a" with hotness 1, resets
```

## Intuition — the [13.5](search-suggestion-system.md) trie, with *scores* instead of plain words

This is the Search Suggestion System's structure with two upgrades:

1. **Scores at word nodes** — each sentence's node carries `hotness` (incremented every time it's committed — the repo's `append` does `hotness += addedHotness`). The prefix walk lands on the node, and the subtree collection gathers `(sentence, hotness)` pairs instead of bare strings.
2. **Ranking, not lexicographic order** — after collection, `sortedByDescending { hotness }` and `take(3)`. Ties fall to the `TreeMap`-sorted traversal order (lexicographic) — which is why the repo's `TreeMap` children matter again.

**The stateful `input`:** the stream is incremental — the system must remember the prefix typed so far (`prefix` StringBuilder) and commit it on `'#'` (`trie.append(sentence)` to bump hotness, then reset). This is a *design* problem: the algorithm is 13.5; the class state is the extra requirement.

**Why collect-then-rank?** The naive alternative — store `(sentence, hotness)` in a map keyed by prefix — duplicates every sentence under every prefix. The trie keeps one node per shared prefix, and the *collection* phase gathers the (few) candidates on demand. At ≤ 100 sentences, collection cost is trivial; the trie's win is clean structure, not raw speed.

**The commit increment** — `'#'` ends a stream with the sentence typed so far; `append(sentence)` walks the trie creating nodes if needed and `hotness += 1`. This is how "the system learns": previously-unseen sentences get a node; seen ones get a hotter score. The repo's `append` takes a `hotness` parameter defaulting to 1, so the initial build can pass `times[i]`.

## Approach 1 — Hash map of prefix -> sorted sentence lists (also correct)

Maintain `Map<prefix, List<(sentence, hotness)>>`, rebuilt on every commit: correct but stores every sentence under every prefix ($O(L^2)$ space) and needs full re-sorting per query.

## Approach 2 — Trie with hotness + rank-on-collect (the repo's version, optimal)

```kotlin
import java.util.*

class AutoCompleteTrie {
    data class TrieNode(
        var ch: Char = '*',
        var hotness: Int = 0,                       // total usage of the sentence ending here
        var isSentence: Boolean = false,
        var children: TreeMap<Char, TrieNode> = TreeMap()
    )
    data class SearchResultItem(var data: String, val hotness: Int)

    val root = TrieNode()

    /**
     * @param word    sentence to record
     * @param hotness additional usage count (default 1 per commit)
     */
    fun append(word: String, hotness: Int = 1) {
        var currentNode = root
        word.forEach { ch ->
            currentNode = currentNode.children.getOrPut(ch) { TrieNode(ch) }
        }
        currentNode.isSentence = true
        currentNode.hotness += hotness             // accumulate usage
    }

    /**
     * @param word  current prefix
     * @param limit how many suggestions to return
     * @return      top suggestions ranked by hotness (ties: lexicographic)
     */
    fun rank(word: String, limit: Int = 3): List<String> {
        val results = mutableListOf<SearchResultItem>()
        var currentNode = root

        word.forEach { ch -> currentNode = currentNode.children[ch] ?: return results.map { it.data } }
        collectWords(currentNode, word, results)   // gather (sentence, hotness) from the subtree

        return results
            .sortedByDescending { it.hotness }     // rank by hotness
            .take(limit)                           // top 3
            .map { it.data }
    }

    fun collectWords(node: TrieNode?, prefix: String, results: MutableList<SearchResultItem>) {
        if (node == null) return
        if (node.isSentence) results.add(SearchResultItem(data = prefix, hotness = node.hotness))

        for ((ch, childNode) in node.children) {   // TreeMap: lexicographic tie-break for free
            collectWords(childNode, prefix + ch, results)
        }
    }
}

class AutocompleteSystem(sentences: Array<String>, times: IntArray) {
    var prefix = StringBuilder()
    var trie = AutoCompleteTrie()

    init {
        sentences.forEachIndexed { index, word -> trie.append(word, times[index]) }
    }

    /**
     * @param c next typed character ('#' commits and resets)
     * @return  top-3 suggestions for the prefix built so far
     */
    fun input(c: Char): List<String> {
        if (c == '#') {                            // commit the sentence and reset
            val sentence = prefix.toString()
            trie.append(sentence)                  // hotness +1
            prefix = StringBuilder()
            return emptyList()
        }

        prefix.append(c)
        return trie.rank(prefix.toString())        // suggest for the growing prefix
    }
}
```

```java
import java.util.*;

public class AutocompleteSystem {
    private static class TrieNode {
        Map<Character, TrieNode> children = new TreeMap<>();   // sorted: lexicographic tie-break
        int hotness;
        boolean isSentence;
    }

    private final TrieNode root = new TrieNode();
    private final StringBuilder prefix = new StringBuilder();

    /** @param sentences history sentences @param times usage counts */
    public AutocompleteSystem(String[] sentences, int[] times) {
        for (int i = 0; i < sentences.length; i++) append(sentences[i], times[i]);
    }

    private void append(String word, int hotness) {
        TrieNode node = root;
        for (char c : word.toCharArray()) node = node.children.computeIfAbsent(c, k -> new TrieNode());
        node.isSentence = true;
        node.hotness += hotness;                             // accumulate usage
    }

    /**
     * @param c next typed character ('#' commits and resets)
     * @return  top-3 suggestions for the prefix built so far
     */
    public List<String> input(char c) {
        if (c == '#') {                                      // commit the sentence and reset
            append(prefix.toString(), 1);
            prefix.setLength(0);
            return List.of();
        }

        prefix.append(c);
        List<Map.Entry<String, Integer>> candidates = new ArrayList<>();

        TrieNode node = root;
        for (char ch : prefix.toString().toCharArray()) {
            node = node.children.get(ch);
            if (node == null) return List.of();              // no sentences with this prefix
        }
        collect(node, prefix.toString(), candidates);        // gather (sentence, hotness)

        candidates.sort((a, b) -> b.getValue() != a.getValue()
                ? b.getValue() - a.getValue()                // hotness descending
                : a.getKey().compareTo(b.getKey()));         // lexicographic tie-break
        return candidates.stream().limit(3).map(Map.Entry::getKey).toList();
    }

    private void collect(TrieNode node, String sentence, List<Map.Entry<String, Integer>> out) {
        if (node.isSentence) out.add(Map.entry(sentence, node.hotness));
        for (Map.Entry<Character, TrieNode> e : node.children.entrySet()) {
            collect(e.getValue(), sentence + e.getKey(), out);
        }
    }
}
```

```cpp
#include <map>
#include <string>
#include <vector>

class AutocompleteSystem {
    struct Node {
        std::map<char, Node*> children;                    // sorted: lexicographic tie-break
        int hotness = 0;
        bool isSentence = false;
    };

    Node* root = new Node();
    std::string prefix;

    void append(const std::string& word, int hotness) {
        Node* node = root;
        for (char c : word) {
            if (!node->children.count(c)) node->children[c] = new Node();
            node = node->children[c];
        }
        node->isSentence = true;
        node->hotness += hotness;                          // accumulate usage
    }

    void collect(Node* node, const std::string& sentence,
                 std::vector<std::pair<std::string, int>>& out) {
        if (node->isSentence) out.push_back({sentence, node->hotness});
        for (auto& [ch, child] : node->children) {
            collect(child, sentence + ch, out);
        }
    }

public:
    /** @param sentences history sentences @param times usage counts */
    AutocompleteSystem(std::vector<std::string>& sentences, std::vector<int>& times) {
        for (int i = 0; i < (int)sentences.size(); i++) append(sentences[i], times[i]);
    }

    /**
     * @param c next typed character ('#' commits and resets)
     * @return  top-3 suggestions for the prefix built so far
     */
    std::vector<std::string> input(char c) {
        if (c == '#') {                                    // commit the sentence and reset
            append(prefix, 1);
            prefix.clear();
            return {};
        }

        prefix += c;
        Node* node = root;
        for (char ch : prefix) {
            if (!node->children.count(ch)) return {};      // no sentences with this prefix
            node = node->children[ch];
        }

        std::vector<std::pair<std::string, int>> candidates;
        collect(node, prefix, candidates);

        std::sort(candidates.begin(), candidates.end(),
                  [](const auto& a, const auto& b) {
                      if (a.second != b.second) return a.second > b.second;  // hotness desc
                      return a.first < b.first;            // lexicographic tie-break
                  });

        std::vector<std::string> result;
        for (int i = 0; i < 3 && i < (int)candidates.size(); i++) result.push_back(candidates[i].first);
        return result;
    }
};
```

```python
class AutocompleteSystem:
    """@param sentences: history sentences  @param times: usage counts"""

    def __init__(self, sentences: list[str], times: list[int]):
        self.trie = {}
        self.prefix = ""
        for sentence, t in zip(sentences, times):
            self._append(sentence, t)

    def _append(self, sentence: str, hotness: int) -> None:
        node = self.trie
        for c in sentence:
            node = node.setdefault(c, {})
        node.setdefault("count", 0)
        node["count"] += hotness               # accumulate usage
        node["is_sentence"] = True

    def _collect(self, node: dict, sentence: str) -> list[tuple[str, int]]:
        out = []
        if node.get("is_sentence"):
            out.append((sentence, node["count"]))
        for c, child in node.items():
            if c not in ("count", "is_sentence"):
                out.extend(self._collect(child, sentence + c))
        return out

    def input(self, c: str) -> list[str]:
        """@param c: next typed character ('#' commits and resets)"""
        if c == "#":                           # commit the sentence and reset
            self._append(self.prefix, 1)
            self.prefix = ""
            return []

        self.prefix += c
        node = self.trie
        for ch in self.prefix:
            if ch not in node:
                return []                      # no sentences with this prefix
            node = node[ch]

        candidates = self._collect(node, self.prefix)
        candidates.sort(key=lambda x: (-x[1], x[0]))   # hotness desc, lexicographic tie-break
        return [s for s, _ in candidates[:3]]
```

```rust
use std::collections::BTreeMap;

struct AutocompleteSystem {
    nodes: Vec<BTreeMap<char, usize>>,
    ends: Vec<(i32, bool)>,      // (hotness, is_sentence) per node
    prefix: String,
}

impl AutocompleteSystem {
    /// @param sentences history sentences  @param times usage counts
    fn new(sentences: Vec<String>, times: Vec<i32>) -> Self {
        let mut sys = AutocompleteSystem {
            nodes: vec![BTreeMap::new()],
            ends: vec![(0, false)],
            prefix: String::new(),
        };
        for (s, t) in sentences.into_iter().zip(times) {
            sys.append(&s, t);
        }
        sys
    }

    fn append(&mut self, word: &str, hotness: i32) {
        let mut id = 0usize;
        for c in word.chars() {
            if !self.nodes[id].contains_key(&c) {
                self.nodes[id].insert(c, self.nodes.len());
                self.nodes.push(BTreeMap::new());
                self.ends.push((0, false));
            }
            id = self.nodes[id][&c];
        }
        self.ends[id].0 += hotness;            // accumulate usage
        self.ends[id].1 = true;
    }

    fn collect(&self, id: usize, sentence: &str, out: &mut Vec<(String, i32)>) {
        let (h, is_sentence) = self.ends[id];
        if is_sentence { out.push((sentence.to_string(), h)); }
        for (&c, &child) in &self.nodes[id] {
            let mut next = sentence.to_string();
            next.push(c);
            self.collect(child, &next, out);
        }
    }

    /// @param c next typed character ('#' commits and resets)
    /// @return  top-3 suggestions for the prefix built so far
    fn input(&mut self, c: char) -> Vec<String> {
        if c == '#' {                          // commit the sentence and reset
            let s = self.prefix.clone();
            self.append(&s, 1);
            self.prefix.clear();
            return Vec::new();
        }

        self.prefix.push(c);
        let mut id = 0usize;
        for ch in self.prefix.chars() {
            match self.nodes[id].get(&ch) {
                Some(&next) => id = next,
                None => return Vec::new(),     // no sentences with this prefix
            }
        }

        let mut candidates = Vec::new();
        self.collect(id, &self.prefix, &mut candidates);
        candidates.sort_by(|a, b| b.1.cmp(&a.1).then(a.0.cmp(&b.0)));  // hotness desc, lexicographic
        candidates.into_iter().take(3).map(|(s, _)| s).collect()
    }
}
```

## Dry run

**Input:** the example session.

```
build: append("i love you", 5), append("island", 3), append("ironman", 2), append("i love leetcode", 2)

input('i'):
  walk i-node; collect subtree:
    "i love you"(5), "i love leetcode"(2), "island"(3), "ironman"(2)
  rank: hotness desc -> ["i love you"(5), "island"(3), "i love leetcode"(2)]  ✓

input(' '):  prefix "i ":
  walk i -> ' '; collect: "i love you"(5), "i love leetcode"(2)
  -> ["i love you", "i love leetcode"]  ✓

input('a'):  prefix "i a": walk i -> ' ' -> 'a'? no child -> []  ✓

input('#'):  commit "i a": append("i a", 1) -> new nodes created, hotness 1.  prefix reset.
  -> []  ✓
```

The commit step is the "learning": `"i a"` was never in the history, but after `'#'` it has a trie path with hotness 1 — so a future `input('i')` + `input(' ')` + `input('a')` stream would rank it (tied at 1, after any hotter matches). The system's state is entirely in the trie.

## Complexity

**Time.** `input` = walk $O(L)$ + collect $O(\text{matching sentences})$ + sort $O(k \log k)$:

$$
T_{\text{input}}(L, k) = O(L + k \log k), \quad k \le \text{sentence count}
$$

**Space.** The trie:

$$
S = O(\text{total characters})
$$

## Variants & follow-ups

- **Search Suggestion System** ([13.5](search-suggestion-system.md)) — this page without hotness: ranking is pure lexicographic, so the TreeMap traversal alone suffices.
- **AutoCompleteSystemWithHeap** (`src/main/kotlin/trie/AutoCompleteSystemWithHeap.kt`) — the repo's alternative: a heap per prefix-node for the top-k; same ideas, different ranking machinery.
- **Count Words With A Given Prefix** ([13.4](count-words-with-a-given-prefix.md)) — counts instead of collections; the "score per node" family.
- **Interview follow-up:** "Why does the commit increment the node's hotness instead of storing a separate count map?" The hotness lives *at the sentence's node* — the same place `isSentence` lives — so collection reads it in $O(1)$ per sentence. A separate `Map<sentence, count>` would duplicate the association and need a join during ranking. The node *is* the sentence's record.
