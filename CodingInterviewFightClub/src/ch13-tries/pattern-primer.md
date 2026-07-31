# 13.0 Pattern Primer — The Prefix Structure

A **trie** (prefix tree) is a tree where each *edge* is a character and each *node* represents a prefix — the path from the root spells it out. Words that share a prefix share the path: `"cat"`, `"car"`, and `"cart"` all live under `c -> a`. The payoff:

- **insert** / **search** / **startsWith** all cost $O(L)$ — the word length — *independent of how many words are stored*;
- prefix queries ("what words start with `ca`?") need no scanning — just walk to the prefix's node and read its subtree.

Compare with a hash set: `startsWith` would need a full scan ($O(nL)$), because a hash set indexes *whole words*, not prefixes. That's the entire niche: **tries are the data structure for prefix structure.**

## The node anatomy

```kotlin
class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()   // or a fixed 26-slot array
    var isWord = false                               // is the path root->here a stored word?
    // optional per-problem cargo:
    var prefixCount = 0                              // [13.4] words passing through
    val wordIndices = mutableListOf<Int>()           // [13.6] words whose prefix this is
    var hotness = 0                                  // [13.7] ranking scores
}
```

**`isWord` is what separates a prefix from a word.** `"car"` being stored doesn't make `"ca"` a word — only nodes marked `isWord` count. Forgetting the flag (or conflating "node exists" with "word exists") is the classic trie bug, especially with prefixes of longer words (`insert("cart")` then `search("car")` must return `false`).

**Children as `Map` vs array:** a `Map<Char, TrieNode>` is space-efficient for sparse alphabets (the repo's style); a 26-slot array is faster for dense lowercase alphabets at higher memory cost. Say both, use the map.

## The moves

**Insert — the fold.** Walk the path, `getOrPut` every character, mark `isWord` at the end. (The repo's `AbstractTrie` does this with a `fold` — one line.)

**Search — the nullable walk.** Walk the path; if any character is missing, `false`; at the end, return `isWord`. **StartsWith** is the same walk without the `isWord` check — that one-character difference is the whole "prefix vs word" distinction.

**The wildcard** ([13.3](design-add-and-search-words.md)) — a `.` character means "any child": the walk becomes a DFS that branches over all children at that position. Exponential worst case, tiny in practice.

**Subtree collection** ([13.5](search-suggestion-system.md), [13.7](design-autocomplete-system.md)) — after walking to the prefix node, DFS its subtree collecting every `isWord` path. The answers are *ranked* by adding per-node scores ([13.4](count-words-with-a-given-prefix.md) counts as it inserts; [13.7](design-autocomplete-system.md) accumulates hotness).

**Trie as an index** ([13.2](word-break.md), [13.6](word-squares.md)) — the trie answers "which words match this prefix?" in $O(L)$ instead of scanning a dictionary; the surrounding algorithm (DP or backtracking) then queries it per position. This is the *advanced* move: the trie is not the solution, it's the accelerator.

## Complexity intuition

$O(L)$ per basic operation, $O(\text{total characters})$ space (shared prefixes amortize). Searches that must *enumerate* a subtree cost the subtree size on top ($O(k)$ for k completions). The recurring interview claim: "insert and search are $O(L)$ *regardless of $n$*" — that's the sentence that justifies the trie.
