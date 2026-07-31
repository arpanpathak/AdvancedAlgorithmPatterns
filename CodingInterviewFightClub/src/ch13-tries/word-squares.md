# 13.6 Word Squares

> **Source:** [`src/main/kotlin/trie/WordSquare.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/trie/WordSquare.kt)
> **Pattern:** trie-indexed backtracking · **Core page**

## The Problem

Given a list of unique words of **equal length**, find all **word squares** — an $n \times n$ grid where the $i$-th row equals the $i$-th column (i.e., `square[i][j] == square[j][i]` for all `i, j`).

- Constraints: $1 \le$ words ≤ 1000; each word length ≤ 5.

## Examples

```
Input:  words = ["area","lead","wall","lady","ball"]
Output: [["ball","area","lead","lady"],
         ["wall","area","lead","lady"]]

        b a l l      w a l l
        a r e a      a r e a
        l e a d      l e a d
        l a d y      l a d y
```

## Intuition — fill row by row, and the *column* tells you the next candidate

A word square is symmetric: when you've chosen rows `0..k-1`, the next row (row `k`) is constrained — its **prefix must equal the k-th column of the already-chosen rows**. Specifically, the prefix of row `k` must match:

$$
\text{prefix}(k) = \text{square}[0][k] \cdot \text{square}[1][k] \cdots \text{square}[k-1][k]
$$

So the backtracking step is: build the required prefix from the *columns* of chosen rows, and the only valid candidates for row `k` are words **starting with that prefix**. That's precisely what the trie answers in $O(L)$ — which is why this problem is in the trie chapter.

**The trie must be built to answer "which words start with prefix P?"** — the standard trie returns words *under* a node, but listing full words per query is wasteful. The repo's trick: each node stores **`wordIndices`** — the indices of *all* words passing through that node. Then "candidates for prefix P" is `trieNode(P).wordIndices` — a direct list, no subtree DFS.

```
backtrack(square):
    if square.size == n: record (square complete — rows 0..n-1 chosen)
    prefix = column square.size of the chosen rows     # from the symmetry
    node = trieNode(prefix); if missing, return        # no word fits: prune
    for idx in node.wordIndices:                       # every word with this prefix
        square.add(words[idx])
        backtrack(square)
        square.removeLast()
```

**Why does checking the prefix *before* choosing prune so hard?** A wrong first row poisons every later row. By requiring row `k` to match the column prefix, each choice is *forced* into the set of words that can possibly complete the square — the tree shrinks from $n^k$ down to the actual square count.

**The base case is the whole square, not the row count:** `square.size == n` means all rows chosen — and by construction every row matches its column, so the square is valid. No final symmetry check needed; the invariant holds at every step.

## Approach 1 — Backtracking with prefix scan (O(nL) per candidate check)

Check candidates by scanning the whole word list for the prefix each time: correct, but the trie turns that scan into an $O(L)$ walk — the entire point of this page.

## Approach 2 — Trie-indexed backtracking (the repo's version, optimal)

```kotlin
class WordSquare {      // repo file name: WorkSquare
    class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        val wordIndices = mutableListOf<Int>()       // words passing through this node
    }

    /**
     * @param words unique words of equal length
     * @return      all word squares
     */
    fun wordSquares(words: Array<String>): List<List<String>> {
        val root = TrieNode()
        words.forEachIndexed { index, word ->
            var curr = root
            for (char in word) {
                curr = curr.children.getOrPut(char) { TrieNode() }
                curr.wordIndices.add(index)          // every prefix-node remembers this word
            }
        }

        val result = mutableListOf<List<String>>()
        val n = words[0].length

        fun backtrack(currentSquare: MutableList<String>) {
            if (currentSquare.size == n) {           // all rows chosen: a valid square
                result.add(ArrayList(currentSquare))
                return
            }

            // The required prefix for the next row = the current column of chosen rows
            val prefix = StringBuilder()
            for (i in 0 until currentSquare.size) {
                prefix.append(currentSquare[i][currentSquare.size])
            }

            // Candidates = words starting with that prefix
            val prefixString = prefix.toString()
            var node = root
            for (char in prefixString) {
                node = node.children[char] ?: return   // no word fits: prune
            }

            for (candidateIdx in node.wordIndices) {
                currentSquare.add(words[candidateIdx])
                backtrack(currentSquare)
                currentSquare.removeLast()             // undo
            }
        }

        for (word in words) {
            backtrack(mutableListOf(word))             // every word is a candidate first row
        }
        return result
    }
}
```

```java
import java.util.*;

public class WordSquares {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        List<Integer> wordIndices = new ArrayList<>();   // words passing through this node
    }

    private String[] words;

    /**
     * @param words unique words of equal length
     * @return      all word squares
     */
    public List<List<String>> wordSquares(String[] words) {
        this.words = words;
        TrieNode root = new TrieNode();
        for (int i = 0; i < words.length; i++) {          // build the index trie
            TrieNode node = root;
            for (char c : words[i].toCharArray()) {
                node = node.children.computeIfAbsent(c, k -> new TrieNode());
                node.wordIndices.add(i);                 // every prefix-node remembers this word
            }
        }

        List<List<String>> result = new ArrayList<>();
        for (String word : words) {
            backtrack(new ArrayList<>(List.of(word)), root, result);
        }
        return result;
    }

    private void backtrack(List<String> square, TrieNode root, List<List<String>> result) {
        int size = square.size();
        if (size == words[0].length()) {                 // all rows chosen: a valid square
            result.add(new ArrayList<>(square));
            return;
        }

        StringBuilder prefix = new StringBuilder();      // the column of chosen rows
        for (int i = 0; i < size; i++) prefix.append(square.get(i).charAt(size));

        TrieNode node = root;
        for (char c : prefix.toString().toCharArray()) {
            node = node.children.get(c);
            if (node == null) return;                    // no word fits: prune
        }

        for (int idx : node.wordIndices) {
            square.add(words[idx]);
            backtrack(square, root, result);
            square.remove(square.size() - 1);            // undo
        }
    }
}
```

```cpp
#include <string>
#include <unordered_map>
#include <vector>

class WordSquares {
    struct Node {
        std::unordered_map<char, Node*> children;
        std::vector<int> wordIndices;                    // words passing through this node
    };

    std::vector<std::string> words;

    void backtrack(std::vector<std::string>& square, Node* root,
                   std::vector<std::vector<std::string>>& result) {
        int size = square.size();
        if (size == (int)words[0].size()) {              // all rows chosen: a valid square
            result.push_back(square);
            return;
        }

        std::string prefix;                              // the column of chosen rows
        for (int i = 0; i < size; i++) prefix += square[i][size];

        Node* node = root;
        for (char c : prefix) {
            if (!node->children.count(c)) return;        // no word fits: prune
            node = node->children[c];
        }

        for (int idx : node->wordIndices) {
            square.push_back(words[idx]);
            backtrack(square, root, result);
            square.pop_back();                           // undo
        }
    }

public:
    /**
     * @param words unique words of equal length
     * @return      all word squares
     */
    std::vector<std::vector<std::string>> wordSquares(std::vector<std::string>& words) {
        this->words = words;
        Node* root = new Node();
        for (int i = 0; i < (int)words.size(); i++) {    // build the index trie
            Node* node = root;
            for (char c : words[i]) {
                if (!node->children.count(c)) node->children[c] = new Node();
                node = node->children[c];
                node->wordIndices.push_back(i);          // every prefix-node remembers this word
            }
        }

        std::vector<std::vector<std::string>> result;
        for (auto& word : words) {
            std::vector<std::string> square{word};
            backtrack(square, root, result);
        }
        return result;
    }
};
```

```python
def word_squares(words: list[str]) -> list[list[str]]:
    """
    @param words: unique words of equal length
    @return:      all word squares
    """
    trie = {}
    for idx, word in enumerate(words):
        node = trie
        for c in word:
            node = node.setdefault(c, {})
            node.setdefault("indices", []).append(idx)   # every prefix-node remembers this word

    n = len(words[0])
    result = []

    def backtrack(square: list[str]) -> None:
        if len(square) == n:                 # all rows chosen: a valid square
            result.append(square[:])
            return

        prefix = "".join(row[len(square)] for row in square)   # the column of chosen rows

        node = trie
        for c in prefix:
            if c not in node:
                return                       # no word fits: prune
            node = node[c]

        for idx in node.get("indices", []):
            square.append(words[idx])
            backtrack(square)
            square.pop()                     # undo

    for word in words:
        backtrack([word])
    return result
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param words unique words of equal length
    /// @return      all word squares
    pub fn word_squares(words: Vec<String>) -> Vec<Vec<String>> {
        let n = words[0].len();
        // trie: node id -> (char -> child id), plus word-end marker '#' -> indices list
        let mut trie: HashMap<usize, HashMap<u8, usize>> = HashMap::new();
        let mut ends: HashMap<usize, Vec<usize>> = HashMap::new();
        let mut nodes = 1usize;

        for (idx, w) in words.iter().enumerate() {
            let mut id = 0usize;
            for b in w.bytes() {
                if !trie.contains_key(&id) { trie.insert(id, HashMap::new()); }
                let next = *trie[&id].entry(b).or_insert_with(|| { nodes += 1; nodes - 1 });
                id = next;
                ends.entry(id).or_default().push(idx);     // every prefix-node remembers this word
            }
        }

        let mut result = Vec::new();
        let mut square: Vec<usize> = Vec::new();

        fn backtrack(square: &mut Vec<usize>, words: &Vec<String>, n: usize,
                     trie: &HashMap<usize, HashMap<u8, usize>>,
                     ends: &HashMap<usize, Vec<usize>>, result: &mut Vec<Vec<String>>) {
            if square.len() == n {                         // all rows chosen: a valid square
                result.push(square.iter().map(|&i| words[i].clone()).collect());
                return;
            }

            // the column of chosen rows = required prefix for the next row
            let mut id = 0usize;
            let mut ok = true;
            for &i in square.iter() {
                match trie.get(&id).and_then(|m| m.get(&words[i].as_bytes()[square.len()])) {
                    Some(&next) => id = next,
                    None => { ok = false; break; }
                }
            }
            if !ok { return; }                             // no word fits: prune

            for &idx in ends.get(&id).into_iter().flatten() {
                square.push(idx);
                backtrack(square, words, n, trie, ends, result);
                square.pop();                              // undo
            }
        }

        for i in 0..words.len() {
            square.push(i);
            backtrack(&mut square, &words, n, &trie, &ends, &mut result);
            square.pop();
        }
        result
    }
}
```

## Dry run

**Input:** `words = ["ball","area","lead","lady"]`, `n = 4`.

```
backtrack(["ball"]):  size 1 != 4.
  prefix = column 1 of chosen rows = "ball"[1] = "a".
  trieNode("a") exists -> wordIndices = [1] ("area" — the only word starting with 'a').
  add "area" -> ["ball","area"].  backtrack:
    prefix = square[0][2] + square[1][2] = 'l' + 'e' = "le".
    trieNode("le") -> wordIndices = [2] ("lead").
    add "lead" -> ["ball","area","lead"].  backtrack:
      prefix = square[0][3] + square[1][3] + square[2][3] = 'l' + 'a' + 'd' = "lad".
      trieNode("lad") -> wordIndices = [3] ("lady").
      add "lady" -> ["ball","area","lead","lady"].  size == 4 -> record ✓
      undo -> ["ball","area","lead"].
    undo -> ["ball","area"].
  undo -> ["ball"].
  (no other words start with "a" -> branch exhausted)
backtrack(["area"]): prefix = "r" — no word starts with 'r' -> prune immediately.
... etc.

result: [["ball","area","lead","lady"]] ✓  (plus the "wall" variant if "wall" is in the list)
```

The forced-choice structure is the lesson: once `"ball"` is the first row, the second row *must* start with `"a"` (the column), which leaves exactly `"area"` — and so on. Each step is a lookup, not a scan; the trie's `wordIndices` turn "which words match this prefix?" into a $O(1)$-ish list read.

## Complexity

**Time.** Trie build $O(n \cdot L)$; search is exponential in the square size but pruned to actual completions:

$$
T = O(n \cdot L + \text{backtracking tree size})
$$

**Space.** Trie + recursion:

$$
S = O(n \cdot L)
$$

## Variants & follow-ups

- **Word Break** ([13.2](word-break.md)) — the trie as an index for *one-dimensional* segmentation; this page is the two-dimensional version of the same idea.
- **Word Search II / Boggle** — DFS over a board with a trie pruning dictionary membership; the "trie accelerates the search" move again.
- **Search Suggestion System** ([13.5](search-suggestion-system.md)) — the collection mode; this page is the *index* mode (wordIndices instead of subtree DFS).
- **Interview follow-up:** "Why does the trie store indices instead of words?" At `wordIndices`-time the candidate list is used inside a hot backtracking loop; storing indices avoids copying/creating `String` objects per query and lets the caller index into the original array. Same data, cheaper access — a micro-decision that matters at $n = 1000$ with $L = 5$ where the tree is wide.
