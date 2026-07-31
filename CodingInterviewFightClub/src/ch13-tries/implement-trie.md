# 13.1 Implement Trie (Prefix Tree)

> **Source:** [`src/main/kotlin/trie/AbstractTrie.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/trie/AbstractTrie.kt) (the repo's minimal version; the full `Trie` class lives in `SearchSuggestionSystem.kt`)
> **Pattern:** insert / search / startsWith · **Core page**

## The Problem

Implement a trie with three operations: `insert(word)`, `search(word)` (exact), and `startsWith(prefix)` (any word with that prefix).

- Constraints: up to $3 \times 10^4$ operations; lowercase letters; word length ≤ 10.

## Examples

```
Trie trie = new Trie();
trie.insert("apple");
trie.search("apple");    -> true
trie.search("app");      -> false   ("app" is a prefix, not a stored word)
trie.startsWith("app");  -> true
trie.insert("app");
trie.search("app");      -> true
```

## Intuition — a tree whose edges are characters

Each node holds a `children` map and an `isWord` flag. A word is a path: insert walks (creating as needed) and marks the final node; search walks and returns the final node's `isWord`; `startsWith` walks and returns "did the walk survive?" — **without** the `isWord` check.

The one-line distinctions to internalize:

- **search vs startsWith**: search ends with `?.isWord ?: false`; startsWith ends with `node != null`. The flag is literally the only difference.
- **The "`app` after inserting `apple`" case**: `search("app")` must be `false` — the node exists (it's a prefix of `apple`) but `isWord` is false. This is *the* test case for the flag.

**Why use a `Map` for children?** The repo stores `children = mutableMapOf<Char, TrieNode>()` — sparse-friendly. (An array of 26 slots is the denser alternative; see the [primer](pattern-primer.md).)

**The fold style:** the repo's `insert` uses `word.fold(root) { curr, char -> curr.children.getOrPut(char) { TrieNode() } }.isWord = true` — walking and creating in one expression, returning the final node. Elegant; the explicit-loop version below is the same thing spelled out.

## Approach 1 — Hash set of words + set of prefixes

Insert every word *and* every prefix into two sets: `startsWith` is O(1), but insert becomes $O(L^2)$ and space explodes. The trie does the same job with shared prefixes stored once.

## Approach 2 — The trie (the repo's version, optimal)

```kotlin
class Trie {
    data class TrieNode(
        var ch: Char = '*',
        var isWord: Boolean = false,
        var children: TreeMap<Char, TrieNode> = TreeMap()   // sorted children (repo style)
    )

    val root = TrieNode()

    /**
     * @param word word to insert
     */
    fun insert(word: String) {
        var currentNode = root
        word.forEach { ch ->
            currentNode = currentNode.children.getOrPut(ch) { TrieNode(ch) }  // walk or create
        }
        currentNode.isWord = true                            // mark the path as a word
    }

    /**
     * @param word word to look up
     * @return     true iff word was inserted exactly
     */
    fun search(word: String): Boolean {
        var currentNode = root
        word.forEach { ch ->
            currentNode = currentNode.children[ch] ?: return false    // missing char
        }
        return currentNode.isWord                            // exists as a WORD, not just a prefix
    }

    /**
     * @param prefix prefix to test
     * @return      true iff some inserted word starts with prefix
     */
    fun startsWith(prefix: String): Boolean {
        var currentNode = root
        prefix.forEach { ch ->
            currentNode = currentNode.children[ch] ?: return false
        }
        return true                                          // path exists; no isWord needed
    }
}
```

```java
public class Trie {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord;
    }

    private final TrieNode root = new TrieNode();

    /** @param word word to insert */
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) node.children[c - 'a'] = new TrieNode();
            node = node.children[c - 'a'];
        }
        node.isWord = true;                                  // mark the path as a word
    }

    /**
     * @param word word to look up
     * @return     true iff word was inserted exactly
     */
    public boolean search(String word) {
        TrieNode node = walk(word);
        return node != null && node.isWord;                  // exists as a WORD
    }

    /**
     * @param prefix prefix to test
     * @return      true iff some inserted word starts with prefix
     */
    public boolean startsWith(String prefix) {
        return walk(prefix) != null;                         // path exists; no isWord needed
    }

    private TrieNode walk(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            if (node.children[c - 'a'] == null) return null;
            node = node.children[c - 'a'];
        }
        return node;
    }
}
```

```cpp
#include <string>
#include <unordered_map>

class Trie {
    struct Node {
        std::unordered_map<char, Node*> children;
        bool isWord = false;
        ~Node() { for (auto& [c, n] : children) delete n; }
    };

    Node* root = new Node();

    Node* walk(const std::string& s) {
        Node* node = root;
        for (char c : s) {
            if (!node->children.count(c)) return nullptr;
            node = node->children[c];
        }
        return node;
    }

public:
    /** @param word word to insert */
    void insert(std::string word) {
        Node* node = root;
        for (char c : word) {
            if (!node->children.count(c)) node->children[c] = new Node();
            node = node->children[c];
        }
        node->isWord = true;                                 // mark the path as a word
    }

    /**
     * @param word word to look up
     * @return     true iff word was inserted exactly
     */
    bool search(std::string word) {
        Node* node = walk(word);
        return node && node->isWord;                         // exists as a WORD
    }

    /**
     * @param prefix prefix to test
     * @return      true iff some inserted word starts with prefix
     */
    bool startsWith(std::string prefix) {
        return walk(prefix) != nullptr;                      // path exists; no isWord needed
    }
};
```

```python
class Trie:
    """@param word: word to insert"""

    class _Node:
        def __init__(self):
            self.children = {}
            self.is_word = False

    def __init__(self):
        self.root = self._Node()

    def insert(self, word: str) -> None:
        node = self.root
        for c in word:
            if c not in node.children:
                node.children[c] = self._Node()
            node = node.children[c]
        node.is_word = True                      # mark the path as a word

    def search(self, word: str) -> bool:
        node = self._walk(word)
        return node is not None and node.is_word    # exists as a WORD

    def starts_with(self, prefix: str) -> bool:
        return self._walk(prefix) is not None       # path exists; no is_word needed

    def _walk(self, s: str):
        node = self.root
        for c in s:
            if c not in node.children:
                return None
            node = node.children[c]
        return node
```

```rust
use std::collections::HashMap;

struct Trie {
    root: TrieNode,
}

struct TrieNode {
    children: HashMap<char, TrieNode>,
    is_word: bool,
}

impl TrieNode {
    fn new() -> Self { TrieNode { children: HashMap::new(), is_word: false } }
}

impl Trie {
    fn new() -> Self { Trie { root: TrieNode::new() } }

    /// @param word word to insert
    fn insert(&mut self, word: String) {
        let mut node = &mut self.root;
        for c in word.chars() {
            node = node.children.entry(c).or_insert_with(TrieNode::new);
        }
        node.is_word = true;                         // mark the path as a word
    }

    /// @param word word to look up
    /// @return     true iff word was inserted exactly
    fn search(&self, word: String) -> bool {
        self.walk(&word).map_or(false, |n| n.is_word)    // exists as a WORD
    }

    /// @param prefix prefix to test
    /// @return      true iff some inserted word starts with prefix
    fn starts_with(&self, prefix: String) -> bool {
        self.walk(&prefix).is_some()                     // path exists; no is_word needed
    }

    fn walk(&self, s: &str) -> Option<&TrieNode> {
        let mut node = &self.root;
        for c in s.chars() {
            node = node.children.get(&c)?;
        }
        Some(node)
    }
}
```

## Dry run

**Input:** the example sequence.

```
insert("apple"): create a-p-p-l-e path, mark 'e' node isWord.

search("apple"):  walk a-p-p-l-e -> node exists, isWord=true -> true ✓
search("app"):    walk a-p-p -> node EXISTS (it's a prefix of apple) but isWord=false -> false ✓
startsWith("app"): walk a-p-p -> node exists -> true ✓   (no isWord check)

insert("app"):    walk a-p-p (already exists), mark 'p' node isWord=true.
search("app"):    walk a-p-p -> isWord=true now -> true ✓
```

The `search("app")` line is the whole lesson: *before* `insert("app")` the node exists but is not a word; *after* it is. The `isWord` flag — not node existence — is what answers "is this a stored word?".

## Complexity

**Time.** Each operation walks the word:

$$
T_{\text{insert/search/startsWith}}(L) = O(L)
$$

**Space.** One node per character in the stored words (prefixes shared):

$$
S = O(\text{total characters})
$$

## Variants & follow-ups

- **Design Add And Search Words** ([13.3](design-add-and-search-words.md)) — the same trie with a `.` wildcard: `search` becomes a branching DFS.
- **Count Words With A Given Prefix** ([13.4](count-words-with-a-given-prefix.md)) — insert gains a per-node counter so prefix queries return counts in $O(L)$.
- **Word Break** ([13.2](word-break.md)) — the trie as a *dictionary index*: "does this prefix match a word?" asked repeatedly by a DP.
- **Interview follow-up:** "Why is search O(L) regardless of how many words are stored?" The trie path for a word is *its own* characters — the walk touches exactly one node per character, never any other word's nodes. That's the property a hash set can't give prefix queries (it has no paths to walk).
