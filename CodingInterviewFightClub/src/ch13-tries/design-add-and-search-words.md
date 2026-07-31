# 13.3 Design Add And Search Words

> **Source:** [`src/main/kotlin/trie/DesignAddAndSearchWordDataStructure.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/trie/DesignAddAndSearchWordDataStructure.kt)
> **Pattern:** wildcard `.` search · **Core page**

## The Problem

Design a data structure with `addWord(word)` and `search(word)` where `word` may contain **`.` wildcards** — a `.` matches any letter.

- Constraints: up to $10^4$ calls; word length ≤ 25; lowercase letters and `.` in queries.

## Examples

```
WordDictionary wd = new WordDictionary();
wd.addWord("bad"); wd.addWord("dad"); wd.addWord("mad");
wd.search("pad") -> false
wd.search("bad") -> true
wd.search(".ad") -> true   (bad / dad / mad all match)
wd.search("b..") -> true   (bad)
```

## Intuition — the `.` turns a walk into a branch

[13.1](implement-trie.md)'s search is a deterministic walk: at each character there's exactly one child to follow. A `.` breaks that: *any* child could be the next step, so the walk must try **all of them** — search becomes a **DFS that branches at wildcards**.

```
dfs(index, node):
    if index == len: return node.isWord
    if word[index] == '.':
        return any(dfs(index + 1, child) for child in node.children.values)   # branch
    else:
        child = node.children[word[index]] ?: return false
        return dfs(index + 1, child)                                          # walk
```

The non-wildcard path is the plain trie walk; the wildcard path is the one line that makes this problem different from [13.1](implement-trie.md). Everything else (node anatomy, `isWord`, insert) is identical.

**Why is the worst case exponential but acceptable?** With $k$ wildcards, each branches over up to 26 children — $26^k$ leaves worst case. In practice words are short (≤ 25), wildcards are few, and the trie prunes dead branches (a `.` at a leaf node has no children to try). The problem's constraints are sized so the pruning keeps it fast.

**The recursion carries the index and node** — not the whole word — so each call is $O(1)$ state plus the branch factor. The `index == word.length` base case checks `isWord`, not node existence: `.a` matching `"ba"` ends at the node after `b`... wait — `.a` at index 1 checks node for 'a', then `index == 2` → `isWord`. Correct.

## Approach 1 — Hash map of words by length + wildcard expansion (also works)

Store `Map<Int, List<String>>` and, for a query with wildcards, generate all $26^k$ expansions and check membership: exponential blowup *per query*. The trie shares the cost across queries and prunes naturally.

## Approach 2 — Trie with branching wildcard search (the repo's version, optimal)

```kotlin
class DesignAddAndSearchWordDataStructure {
    class WordDictionary {
        private data class TrieNode(
            val children: MutableMap<Char, TrieNode> = mutableMapOf(),
            var isEnd: Boolean = false
        )

        private val root = TrieNode()

        /**
         * @param word word to add (lowercase)
         */
        fun addWord(word: String) {
            var current = root
            for (ch in word) {
                current = current.children.getOrPut(ch) { TrieNode() }
            }
            current.isEnd = true
        }

        /**
         * @param word pattern to match ('.' matches any letter)
         * @return    true iff some added word matches the pattern
         */
        fun search(word: String): Boolean {
            fun dfs(index: Int, node: TrieNode): Boolean {
                when {
                    index == word.length -> return node.isEnd       // whole pattern consumed
                    word[index] == '.' -> {                          // wildcard: branch!
                        return node.children.values.any { child ->
                            dfs(index + 1, child)
                        }
                    }
                    else -> {
                        val nextNode = node.children[word[index]] ?: return false
                        return dfs(index + 1, nextNode)              // normal character: walk
                    }
                }
            }
            return dfs(0, root)
        }
    }
}
```

```java
import java.util.*;

public class WordDictionary {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
    }

    private final TrieNode root = new TrieNode();

    /** @param word word to add (lowercase) */
    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
    }

    /**
     * @param word pattern to match ('.' matches any letter)
     * @return     true iff some added word matches the pattern
     */
    public boolean search(String word) {
        return dfs(0, root, word);
    }

    private boolean dfs(int index, TrieNode node, String word) {
        if (index == word.length()) return node.isEnd;      // whole pattern consumed

        char c = word.charAt(index);
        if (c == '.') {                                     // wildcard: branch!
            for (TrieNode child : node.children.values()) {
                if (dfs(index + 1, child, word)) return true;
            }
            return false;
        }
        TrieNode next = node.children.get(c);
        return next != null && dfs(index + 1, next, word);  // normal character: walk
    }
}
```

```cpp
#include <string>
#include <unordered_map>

class WordDictionary {
    struct Node {
        std::unordered_map<char, Node*> children;
        bool isEnd = false;
    };

    Node* root = new Node();

    bool dfs(int index, const std::string& word, Node* node) {
        if (index == (int)word.size()) return node->isEnd;  // whole pattern consumed

        char c = word[index];
        if (c == '.') {                                     // wildcard: branch!
            for (auto& [ch, child] : node->children) {
                if (dfs(index + 1, word, child)) return true;
            }
            return false;
        }
        if (!node->children.count(c)) return false;
        return dfs(index + 1, word, node->children[c]);     // normal character: walk
    }

public:
    /** @param word word to add (lowercase) */
    void addWord(std::string word) {
        Node* node = root;
        for (char c : word) {
            if (!node->children.count(c)) node->children[c] = new Node();
            node = node->children[c];
        }
        node->isEnd = true;
    }

    /**
     * @param word pattern to match ('.' matches any letter)
     * @return     true iff some added word matches the pattern
     */
    bool search(std::string word) {
        return dfs(0, word, root);
    }
};
```

```python
class WordDictionary:
    """@param word: word to add (lowercase)"""

    class _Node:
        def __init__(self):
            self.children = {}
            self.is_end = False

    def __init__(self):
        self.root = self._Node()

    def add_word(self, word: str) -> None:
        node = self.root
        for c in word:
            if c not in node.children:
                node.children[c] = self._Node()
            node = node.children[c]
        node.is_end = True

    def search(self, word: str) -> bool:
        """@param word: pattern to match ('.' matches any letter)"""

        def dfs(i: int, node: "WordDictionary._Node") -> bool:
            if i == len(word):
                return node.is_end              # whole pattern consumed
            c = word[i]
            if c == ".":                        # wildcard: branch!
                return any(dfs(i + 1, child) for child in node.children.values())
            child = node.children.get(c)
            return child is not None and dfs(i + 1, child)   # normal character: walk

        return dfs(0, self.root)
```

```rust
use std::collections::HashMap;

struct WordDictionary {
    root: Node,
}

struct Node {
    children: HashMap<char, Node>,
    is_end: bool,
}

impl Node {
    fn new() -> Self { Node { children: HashMap::new(), is_end: false } }
}

impl WordDictionary {
    fn new() -> Self { WordDictionary { root: Node::new() } }

    /// @param word word to add (lowercase)
    fn add_word(&mut self, word: String) {
        let mut node = &mut self.root;
        for c in word.chars() {
            node = node.children.entry(c).or_insert_with(Node::new);
        }
        node.is_end = true;
    }

    /// @param word pattern to match ('.' matches any letter)
    /// @return     true iff some added word matches the pattern
    fn search(&self, word: String) -> bool {
        fn dfs(chars: &[char], node: &Node) -> bool {
            match chars {
                [] => node.is_end,                          // whole pattern consumed
                [c, rest @ ..] => {
                    if *c == '.' {                          // wildcard: branch!
                        node.children.values().any(|child| dfs(rest, child))
                    } else {
                        node.children.get(c).map_or(false, |child| dfs(rest, child))
                    }
                }
            }
        }
        let chars: Vec<char> = word.chars().collect();
        dfs(&chars, &self.root)
    }
}
```


## Dry run

**Input:** `addWord("bad")`, `addWord("dad")`, `addWord("mad")`, then the queries.

```
search("bad"):  walk b -> a -> d; index == 3 == len -> d-node.isEnd = true ✓
search(".ad"):  index 0 = '.': branch over root children {b, d, m}:
                  dfs(1, b-node): 'a' -> dfs(2, a-node): 'd' -> dfs(3, d-node): isEnd(true) -> true ✓
search("b.."):  index 0 = 'b': walk to b-node.
                  index 1 = '.': branch over b-node children {a}:
                    dfs(2, a-node): index 2 = '.': branch over a-node children {d}:
                      dfs(3, d-node): index == 3 == len -> isEnd(true) -> true ✓
search("pad"):  index 0 = 'p': walk to p-node? root has no 'p' child -> false ✓
```

The `.ad` trace shows the branching in action: one wildcard tries every root child, and the recursion descends each candidate path. `"b.."` shows *two* consecutive wildcards nesting: each one multiplies the possible paths, and the trie structure is what keeps the fan-out bounded by actual children (a `.` at a leaf has zero children to try — the search dies immediately).

## Complexity

**Time.** Insert is $O(L)$; search is $O(26^w)$ worst case where $w$ = wildcard count (each `.` branches ≤ 26 ways):

$$
T_{\text{add}} = O(L), \qquad T_{\text{search}} = O(26^w) \text{ worst, small in practice}
$$

**Space.** Trie nodes:

$$
S = O(\text{total characters})
$$

## Variants & follow-ups

- **Implement Trie** ([13.1](implement-trie.md)) — this page without the wildcard: search is the deterministic walk.
- **Word Search II** — a board + a dictionary: DFS the board, walking the *trie* to prune; the trie is the accelerator for "which dictionary words touch this path".
- **Search Suggestion System** ([13.5](search-suggestion-system.md)) — the wildcard removed, replaced by "collect and rank completions".
- **Interview follow-up:** "Why is the `.` branch a `return any(...)` and not a loop with a `found` flag?" The recursion either finds a complete match (`true` propagates) or exhausts all children (`false`). `any` is the "did any child path succeed?" fold — the same logic as an explicit loop, one line shorter. The base case (`index == length -> isEnd`) is what stops the recursion from treating `.` at the pattern's end as "anything can follow".
