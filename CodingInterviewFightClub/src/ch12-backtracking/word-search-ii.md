# 12.14 Word Search II

> **Source**: [`src/main/kotlin/grid/search/WordSearch_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/search/WordSearch_II.kt) (a stub in the repo — the canonical Trie + DFS below)
> **Pattern**: trie-pruned grid DFS · **Core page**

## The Problem

Find **all** words from `words` on the board (4-connected, each cell once).

- Constraints: board ≤ 12×12; words ≤ 3×10⁴.

## Examples

```
Input:  board = [["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]],
        words = ["oath","pea","eat","rain"]
Output: ["eat","oath"]
```

## Intuition — a trie of the words prunes the DFS

The [12.x](../ch12-backtracking/pattern-primer.md) single-word DFS explodes per word (n words × 4^len). Build a **trie** of all words and run one DFS that walks the trie — a path that leaves the trie's prefix set is dead:

```kotlin
fun findWords(board: Array<CharArray>, words: Array<String>): List<String> {
    val root = TrieNode()
    for (word in words) insert(word, root)

    val result = mutableListOf<String>()

    fun dfs(r: Int, c: Int, node: TrieNode, path: String) {
        if (r < 0 || c < 0 || r >= m || c >= n) return

        val ch = board[r][c]
        if (ch == '#') return                       // visited
        val next = node.children[ch] ?: return       // trie prunes

        if (next.isWord) { result.add(path + ch); next.isWord = false }   // dedupe

        board[r][c] = '#'
        for ((dr, dc) in dirs) dfs(r + dr, c + dc, next, path + ch)
        board[r][c] = ch
    }

    for (r in 0 until m) for (c in 0 until n) dfs(r, c, root, "")
    return result
}
```

**Why the trie?** The shared prefixes collapse the search: all words sharing a prefix explore that prefix's cells once. The `node.children[ch] ?: return` is the prune — the [13.0](../ch13-tries/pattern-primer.md) engine glued to the [12.x](../ch12-backtracking/pattern-primer.md) DFS.

**Why `next.isWord = false` after finding?** Duplicate words in `words` or multiple paths to the same word — marking consumed dedupes the result.

## Approach 1 — Per-word DFS (word-search × N)

Run the single-word search per word: correct, O(N × 4^L) — too slow.

## Approach 2 — Trie-pruned DFS (the canonical, optimal)

```kotlin
class WordSearch_II {
    private class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        var isWord = false
    }

    /**
     * @param board letter grid
     * @param words dictionary
     * @return      all words found on the board
     */
    fun findWords(board: Array<CharArray>, words: Array<String>): List<String> {
        val root = TrieNode()
        for (word in words) {
            var node = root
            for (ch in word) node = node.children.getOrPut(ch) { TrieNode() }
            node.isWord = true
        }

        val m = board.size
        val n = board[0].size
        val dirs = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
        val result = mutableListOf<String>()

        fun dfs(r: Int, c: Int, node: TrieNode, path: String) {
            if (r < 0 || c < 0 || r >= m || c >= n) return

            val ch = board[r][c]
            if (ch == '#') return

            val next = node.children[ch] ?: return

            if (next.isWord) {
                result.add(path + ch)
                next.isWord = false
            }

            board[r][c] = '#'
            for ((dr, dc) in dirs) dfs(r + dr, c + dc, next, path + ch)
            board[r][c] = ch
        }

        for (r in 0 until m) for (c in 0 until n) dfs(r, c, root, "")
        return result
    }
}
```

```java
import java.util.*;

public class WordSearchII {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        String word = null;
    }

    private int m, n;
    private int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private void dfs(char[][] board, int r, int c, TrieNode node, List<String> result) {
        if (r < 0 || c < 0 || r >= m || c >= n) return;

        char ch = board[r][c];
        if (ch == '#') return;

        TrieNode next = node.children.get(ch);
        if (next == null) return;

        if (next.word != null) {
            result.add(next.word);
            next.word = null;
        }

        board[r][c] = '#';
        for (int[] d : dirs) dfs(board, r + d[0], c + d[1], next, result);
        board[r][c] = ch;
    }

    /**
     * @param board letter grid
     * @param words dictionary
     * @return      all words found on the board
     */
    public List<String> findWords(char[][] board, String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode node = root;
            for (char c : w.toCharArray()) {
                node.children.putIfAbsent(c, new TrieNode());
                node = node.children.get(c);
            }
            node.word = w;
        }

        m = board.length;
        n = board[0].length;
        List<String> result = new ArrayList<>();

        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++)
                dfs(board, r, c, root, result);
        return result;
    }
}
```

```cpp
#include <vector>
#include <string>
#include <unordered_map>

class WordSearchII {
    struct TrieNode {
        std::unordered_map<char, TrieNode*> children;
        std::string word;
    };

    int m, n;
    int dirs[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    void dfs(std::vector<std::vector<char>>& board, int r, int c,
             TrieNode* node, std::vector<std::string>& result) {
        if (r < 0 || c < 0 || r >= m || c >= n) return;

        char ch = board[r][c];
        if (ch == '#') return;

        if (!node->children.count(ch)) return;
        TrieNode* next = node->children[ch];

        if (!next->word.empty()) {
            result.push_back(next->word);
            next->word.clear();
        }

        board[r][c] = '#';
        for (auto& d : dirs) dfs(board, r + d[0], c + d[1], next, result);
        board[r][c] = ch;
    }

public:
    /**
     * @param board letter grid
     * @param words dictionary
     * @return      all words found on the board
     */
    std::vector<std::string> findWords(std::vector<std::vector<char>>& board, std::vector<std::string>& words) {
        TrieNode* root = new TrieNode();
        for (auto& w : words) {
            TrieNode* node = root;
            for (char c : w) {
                if (!node->children.count(c)) node->children[c] = new TrieNode();
                node = node->children[c];
            }
            node->word = w;
        }

        m = board.size();
        n = board[0].size();
        std::vector<std::string> result;

        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++)
                dfs(board, r, c, root, result);
        return result;
    }
};
```

```python
class TrieNode:
    def __init__(self):
        self.children = {}
        self.word = None


def find_words(board: list[list[str]], words: list[str]) -> list[str]:
    """
    @param board: letter grid
    @param words: dictionary
    @return:      all words found on the board
    """
    root = TrieNode()
    for w in words:
        node = root
        for ch in w:
            node = node.children.setdefault(ch, TrieNode())
        node.word = w

    m, n = len(board), len(board[0])
    dirs = ((1, 0), (-1, 0), (0, 1), (0, -1))
    result = []

    def dfs(r, c, node, path):
        if not (0 <= r < m and 0 <= c < n):
            return

        ch = board[r][c]
        if ch == "#":
            return

        nxt = node.children.get(ch)
        if nxt is None:
            return

        if nxt.word:
            result.append(path + ch)
            nxt.word = None

        board[r][c] = "#"
        for dr, dc in dirs:
            dfs(r + dr, c + dc, nxt, path + ch)
        board[r][c] = ch

    for r in range(m):
        for c in range(n):
            dfs(r, c, root, "")

    return result
```

```rust
use std::collections::HashMap;

struct TrieNode {
    children: HashMap<char, TrieNode>,
    word: Option<String>,
}

impl TrieNode {
    fn new() -> Self { Self { children: HashMap::new(), word: None } }
}

impl Solution {
    /// @param board letter grid
    /// @param words dictionary
    /// @return      all words found on the board
    pub fn find_words(board: Vec<Vec<char>>, words: Vec<String>) -> Vec<String> {
        let mut root = TrieNode::new();
        for w in &words {
            let mut node = &mut root;
            for ch in w.chars() {
                node = node.children.entry(ch).or_insert_with(TrieNode::new);
            }
            node.word = Some(w.clone());
        }

        let (m, n) = (board.len(), board[0].len());
        let dirs = [(1, 0), (-1, 0), (0, 1), (0, -1)];
        let mut result = Vec::new();

        fn dfs(board: &mut Vec<Vec<char>>, r: i32, c: i32, node: &mut TrieNode,
               m: i32, n: i32, dirs: &[(i32, i32)], result: &mut Vec<String>, path: &mut String) {
            if r < 0 || c < 0 || r >= m || c >= n { return; }
            let ch = board[r as usize][c as usize];
            if ch == '#' { return; }

            let Some(next) = node.children.get_mut(&ch) else { return; };

            if let Some(word) = next.word.take() {
                result.push(word);
            }

            board[r as usize][c as usize] = '#';
            path.push(ch);
            for (dr, dc) in dirs {
                dfs(board, r + dr, c + dc, next, m, n, dirs, result, path);
            }
            path.pop();
            board[r as usize][c as usize] = ch;
        }

        let mut board = board;
        for r in 0..m {
            for c in 0..n {
                dfs(&mut board, r as i32, c as i32, &mut root, m as i32, n as i32,
                    &dirs, &mut result, &mut String::new());
            }
        }
        result
    }
}
```

## Dry run

**Input:** the example board, `words = ["oath","pea","eat","rain"]`.

```
trie: oath, pea, eat, rain.
DFS from (0,0) 'o': children has 'o'? oath's o yes.  walk o-a-t-h -> "oath" found ✓.
DFS from (1,0) 'e': children has 'e'? eat's e yes.  e-a-t -> "eat" found ✓.
"pea": p not on any reachable path... p at (1,1)? board has no 'p' -> never explored ✓.
"rain": r at (2,1): r-a-i-n? (2,1) r -> (1,1) a? 't' no -> pruned ✓.

Output: ["oath","eat"] ✓
```

The trie's prefix prune kills whole search branches: "rai" paths die at the first mismatch, instead of exploring 4^L cell combinations per word.

## Complexity

**Time.** Cells × trie depth (amortized):

$$
T = O(m \cdot n \cdot 4 \cdot L)
$$

**Space.** The trie:

$$
S = O(\text{total word length})
$$

## Variants & follow-ups

- **Word Search** — the single-word ancestor.
- **Implement Trie** ([13.1](../ch13-tries/implement-trie.md)) — the prefix engine.
- **Interview follow-up:** "Why is the trie the right data structure?" The shared prefix structure means one DFS explores all words at once — the prune `children[ch] ?: return` skips every path not in the dictionary. The `#` mark handles the "each cell once" constraint.
