# 13.5 Search Suggestion System

> **Source:** [`src/main/kotlin/trie/SearchSuggestionSystem.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/trie/SearchSuggestionSystem.kt)
> **Pattern:** trie + subtree collection · **Core page**

## The Problem

Given `products` (a product-name catalog) and a `searchWord`, return for **each prefix** of the search word (from length 1 up) the **three lexicographically smallest** products matching that prefix.

- Constraints: $1 \le n \le 1000$; product lengths ≤ 30; searchWord length ≤ 1000.

## Examples

```
Input:  products = ["mobile","mouse","moneypot","monitor","mousepad"], searchWord = "mouse"
Output: [
  ["mobile","moneypot","monitor"],   ("m")
  ["mobile","moneypot","monitor"],   ("mo")
  ["mouse","mousepad"],              ("mou")
  ["mouse","mousepad"],              ("mous")
  ["mouse","mousepad"]               ("mouse")
]
```

## Intuition — walk to the prefix, then *collect* its subtree

The trie gives prefix matching for free: for prefix `P`, walk `P` to its node, then **DFS the node's subtree collecting every word** (the `isWord` paths). Sort the collected words, take 3. And since the search word's prefixes are *nested* (`"m"`, `"mo"`, `"mou"`...), each query reuses the same walk prefix — but the repo's version re-walks per prefix, which is fine at this scale.

**The collection walk** (`collectWords`): from the prefix node, DFS down; whenever `isWord`, emit the accumulated path string; continue into every child (the repo's `TreeMap` children give *sorted* traversal, which is why the results come out lexicographically ordered even before the `.sorted()` — using a `TreeMap` is a quiet detail that makes the sort nearly free).

**Why `take(3)` after collecting everything?** The subtree may hold hundreds of products. Collecting *all* then taking 3 is simple and correct; a production version would stop the DFS after 3 hits — but the "collect all, sort, slice" version is the interview-appropriate shape at $n \le 1000$.

**The repeat work:** `searchWord` has $L$ prefixes, each query re-walking from the root costs $O(L)$ — total $O(L^2)$ for the walks plus collection. Acceptable; the "walk once, descend incrementally" optimization is the follow-up.

## Approach 1 — Sort once, filter per prefix (also O(nL log n))

Sort `products` once, then for each prefix binary-search the first match and scan the next 3: elegant, no trie. The trie version below is the "structured" answer — and it generalizes to [13.6](word-squares.md)/[13.7](design-autocomplete-system.md).

## Approach 2 — Trie walk + collect + slice (the repo's version, optimal)

```kotlin
import java.lang.StringBuilder
import java.util.*

class Trie {
    data class TrieNode(
        var ch: Char = '*',
        var isWord: Boolean = false,
        var children: TreeMap<Char, TrieNode> = TreeMap()   // sorted: lexicographic order for free
    )

    val root = TrieNode()

    fun insert(word: String) {
        var currentNode = root
        word.forEach { ch -> currentNode = currentNode.children.getOrPut(ch) { TrieNode(ch) } }
        currentNode.isWord = true
    }

    /**
     * @param word prefix to complete
     * @return     all stored words with this prefix (lexicographically sorted)
     */
    fun search(word: String): List<String> {
        val results = mutableListOf<String>()
        var currentNode = root

        word.forEach { ch -> currentNode = currentNode.children[ch] ?: return results }
        collectWords(currentNode, word, results)          // subtree DFS
        return results
    }

    fun collectWords(node: TrieNode?, prefix: String, results: MutableList<String>) {
        if (node == null) return
        if (node.isWord) results.add(prefix)              // this path is a word

        for ((ch, childNode) in node.children) {
            collectWords(childNode, prefix + ch, results)
        }
    }
}

class SearchSuggestionSystem {
    /**
     * @param products  product catalog
     * @param searchWord typed character by character
     * @return          top-3 lexicographic matches for each prefix of searchWord
     */
    fun suggestedProducts(products: Array<String>, searchWord: String): List<List<String>> {
        val trie = Trie()
        val result = mutableListOf<List<String>>()
        val prefixSearchString = StringBuilder()

        products.forEach { trie.insert(it) }

        searchWord.forEach { ch ->
            prefixSearchString.append(ch)
            result.add(trie.search(prefixSearchString.toString()).take(3))   // top 3
        }
        return result
    }
}
```

```java
import java.util.*;

public class SearchSuggestionSystem {
    private static class TrieNode {
        Map<Character, TrieNode> children = new TreeMap<>();   // sorted: lexicographic order
        boolean isWord;
    }

    private final TrieNode root = new TrieNode();

    private void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) node = node.children.computeIfAbsent(c, k -> new TrieNode());
        node.isWord = true;
    }

    private List<String> search(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return results;
        }
        collect(node, prefix, results);                      // subtree DFS
        return results;
    }

    private void collect(TrieNode node, String prefix, List<String> results) {
        if (node.isWord) results.add(prefix);                // this path is a word
        for (Map.Entry<Character, TrieNode> e : node.children.entrySet()) {
            collect(e.getValue(), prefix + e.getKey(), results);
        }
    }

    /**
     * @param products   product catalog
     * @param searchWord typed character by character
     * @return           top-3 lexicographic matches for each prefix of searchWord
     */
    public List<List<String>> suggestedProducts(String[] products, String searchWord) {
        for (String p : products) insert(p);

        List<List<String>> result = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        for (char c : searchWord.toCharArray()) {
            prefix.append(c);
            result.add(search(prefix.toString()).stream().limit(3).toList());
        }
        return result;
    }
}
```

```cpp
#include <map>
#include <string>
#include <vector>

class SearchSuggestionSystem {
    struct Node {
        std::map<char, Node*> children;                    // sorted: lexicographic order
        bool isWord = false;
    };

    Node* root = new Node();

    void insert(const std::string& word) {
        Node* node = root;
        for (char c : word) {
            if (!node->children.count(c)) node->children[c] = new Node();
            node = node->children[c];
        }
        node->isWord = true;
    }

    void collect(Node* node, const std::string& prefix, std::vector<std::string>& out) {
        if (node->isWord) out.push_back(prefix);            // this path is a word
        for (auto& [ch, child] : node->children) {
            collect(child, prefix + ch, out);
        }
    }

    std::vector<std::string> search(const std::string& prefix) {
        Node* node = root;
        for (char c : prefix) {
            if (!node->children.count(c)) return {};
            node = node->children[c];
        }
        std::vector<std::string> out;
        collect(node, prefix, out);                         // subtree DFS
        return out;
    }

public:
    /**
     * @param products   product catalog
     * @param searchWord typed character by character
     * @return           top-3 lexicographic matches for each prefix of searchWord
     */
    std::vector<std::vector<std::string>> suggestedProducts(std::vector<std::string>& products,
                                                            std::string searchWord) {
        for (auto& p : products) insert(p);

        std::vector<std::vector<std::string>> result;
        std::string prefix;
        for (char c : searchWord) {
            prefix += c;
            auto all = search(prefix);
            if (all.size() > 3) all.resize(3);              // top 3
            result.push_back(all);
        }
        return result;
    }
};
```

```python
def suggested_products(products: list[str], search_word: str) -> list[list[str]]:
    """
    @param products:   product catalog
    @param search_word: typed character by character
    @return:           top-3 lexicographic matches for each prefix of search_word
    """
    trie = {}
    for word in products:                    # build the trie
        node = trie
        for c in word:
            node = node.setdefault(c, {})
        node["#"] = True                     # word-end marker

    def collect(node, prefix: str) -> list[str]:
        words = []
        if "#" in node:
            words.append(prefix)             # this path is a word
        for c, child in node.items():
            if c != "#":
                words.extend(collect(child, prefix + c))
        return words

    result = []
    prefix = ""
    node = trie
    for c in search_word:
        prefix += c
        node = node.get(c, {})               # if missing, node becomes empty -> no matches
        result.append(collect(node, prefix)[:3])    # top 3
    return result
```

```rust
use std::collections::BTreeMap;

impl Solution {
    /// @param products   product catalog
    /// @param search_word typed character by character
    /// @return           top-3 lexicographic matches for each prefix of search_word
    pub fn suggested_products(products: Vec<String>, search_word: String) -> Vec<Vec<String>> {
        let mut trie = Trie::new();
        for p in &products { trie.insert(p); }

        let mut result = Vec::new();
        let mut node_id = 0usize;
        let mut prefix = String::new();

        for c in search_word.chars() {
            prefix.push(c);
            match trie.step(node_id, c) {
                Some(next) => {
                    node_id = next;
                    let mut all = Vec::new();
                    trie.collect(node_id, &prefix, &mut all);
                    all.truncate(3);                     // top 3
                    result.push(all);
                }
                None => {
                    node_id = usize::MAX;                // prefix dead: no matches from here on
                    result.push(Vec::new());
                }
            }
        }
        result
    }
}

struct Trie {
    // node id -> (char -> child id), with a sorted map for lexicographic order
    nodes: Vec<BTreeMap<char, usize>>,
    word_end: Vec<bool>,
}

impl Trie {
    fn new() -> Self {
        Trie { nodes: vec![BTreeMap::new()], word_end: vec![false] }
    }

    fn insert(&mut self, word: &str) {
        let mut id = 0;
        for c in word.chars() {
            if !self.nodes[id].contains_key(&c) {
                self.nodes[id].insert(c, self.nodes.len());
                self.nodes.push(BTreeMap::new());
                self.word_end.push(false);
            }
            id = self.nodes[id][&c];
        }
        self.word_end[id] = true;
    }

    fn step(&self, id: usize, c: char) -> Option<usize> {
        self.nodes.get(id)?.get(&c).copied()
    }

    fn collect(&self, id: usize, prefix: &str, out: &mut Vec<String>) {
        if self.word_end[id] { out.push(prefix.to_string()); }   // this path is a word
        for (c, &child) in &self.nodes[id] {
            let mut next = prefix.to_string();
            next.push(*c);
            self.collect(child, &next, out);
        }
    }
}
```

## Dry run

**Input:** `products = ["mobile","mouse","moneypot","monitor","mousepad"]`, `searchWord = "mouse"`.

```
trie built; TreeMap children keep each node's children sorted ('m' has children 'o' only...).

prefix "m": walk to m-node; collect subtree -> [mobile, moneypot, monitor] (sorted by TreeMap
            traversal) -> take 3 -> ["mobile","moneypot","monitor"] ✓
prefix "mo": walk m->o; collect -> [mobile, moneypot, monitor] -> same three ✓
prefix "mou": walk m->o->u; collect -> [mouse, mousepad] -> take 3 (only 2) ✓
prefix "mous": -> [mouse, mousepad] ✓
prefix "mouse": -> [mouse, mousepad] ✓
```

The TreeMap detail is doing visible work: at the `"m"` node, the children `o` (only child) lead down to a subtree containing `mobile`/`moneypot`/`monitor` — and the sorted traversal emits them in lexicographic order without an explicit sort. If `products` had `"ma..."` and `"mo..."`, the `m`-node's sorted children would order them automatically.

## Complexity

**Time.** Per prefix: walk $O(L)$ + subtree collect $O(\text{matching words})$:

$$
T(L, k) = O(L^2 + L \cdot k) \text{ over all prefixes}
$$

**Space.** The trie:

$$
S = O(\text{total characters})
$$

## Variants & follow-ups

- **Count Words With A Given Prefix** ([13.4](count-words-with-a-given-prefix.md)) — counts instead of collections: the same walk, a number instead of a subtree DFS.
- **Design Autocomplete** ([13.7](design-autocomplete-system.md)) — the collection is ranked by *hotness* instead of lexicographic order; scores live at the word nodes.
- **Word Squares** ([13.6](word-squares.md)) — the "which words start with this prefix?" query gets asked inside a backtracking loop — the collection becomes an *index* (`wordIndices`) for instant candidate lists.
- **Interview follow-up:** "Why use a TreeMap for children?" The lexicographic requirement means we need *sorted* traversal. A `TreeMap` gives sorted iteration for free, so the collected results are already ordered — the `.take(3)` slices a sorted list instead of sorting it. With a `HashMap` the collect would need an explicit `.sorted()`.
