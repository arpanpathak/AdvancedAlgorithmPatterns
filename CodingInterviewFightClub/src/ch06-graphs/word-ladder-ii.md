# 6.13 Word Ladder II

> **Source:** [`src/main/kotlin/graph/WordLadder_II_clean.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/WordLadder_II_clean.kt) (+ `WordLadder_II.kt`, `WordLadder_II_FinalCutPro.kt`)
> **Pattern:** BFS distances + DFS reconstruction · **Core page**

## The Problem

Like [6.1](word-ladder.md), but return **ALL shortest transformation sequences** from `beginWord` to `endWord` (one letter changed per step, intermediates in `wordList`).

- Constraints: $1 \le$ wordList ≤ 500; words same length.

## Examples

```
Input:  beginWord = "hit", endWord = "cog",
        wordList = ["hot","dot","dog","lot","log","cog"]
Output: [["hit","hot","dot","dog","cog"],
         ["hit","hot","lot","log","cog"]]
```

## Intuition — BFS gives the *distances*, DFS over the distance-decreasing edges gives the *paths*

Two problems hide in one: (1) how long is the shortest path? (2) what are all the paths of that length? The clean separation:

1. **BFS from `endWord`** (or begin) computes `distMap[word]` = shortest distance from the end — while also **pruning the graph** to only the edges that can appear on a shortest path (the repo's `buildPrunedGraph`).
2. **DFS from `beginWord`**, moving only to neighbors with `dist = currentDist - 1` (strictly *closer to the end*), appending each word to the path. Every path that reaches `endWord` is shortest by construction — the distance monotonicity guarantees it.

**Why DFS on the pruned graph and not BFS-backtracking?** BFS would revisit whole layers; the distance-decreasing DFS walks *each valid path once*. The repo's `getNeighbors` generates the 26-letter mutations and keeps only those in the word set — the same [6.1](word-ladder.md) neighbor machinery, reused for both phases.

**Why is `foundEnd` tracked?** If BFS never reaches `endWord`, the DFS would walk forever — the pruned graph's `foundEnd` flag short-circuits to `[]` before any path search.

## Approach 1 — BFS storing full paths (memory explosion)

Queue of *paths* instead of words: every path is copied at every step — exponential memory on dense graphs.

## Approach 2 — BFS distances + DFS reconstruction (the repo's clean version, optimal)

```kotlin
class WordLadder_II_clean {

    private data class GraphData(
        val graph: Map<String, Set<String>>,
        val distMap: Map<String, Int>,
        val foundEnd: Boolean
    )

    /**
     * @param beginWord start word
     * @param endWord   target word
     * @param wordList  intermediate words
     * @return          all shortest transformation sequences
     */
    fun findLadders(beginWord: String, endWord: String, wordList: List<String>): List<List<String>> {
        val wordSet = wordList.toSet()
        if (endWord !in wordSet) return emptyList()

        val graphData = buildPrunedGraph(beginWord, endWord, wordSet)
        if (!graphData.foundEnd) return emptyList()

        val result = mutableListOf<List<String>>()
        reconstructPaths(
            current = beginWord, endWord = endWord,
            graph = graphData.graph, path = mutableListOf(), result = result
        )
        return result
    }

    private fun getNeighbors(word: String, wordSet: Set<String>, endWord: String): List<String> = buildList {
        val chars = word.toCharArray()
        for (i in word.indices) {
            val originalChar = chars[i]
            for (c in 'a'..'z') {
                if (c == originalChar) continue
                chars[i] = c
                val newWord = String(chars)
                chars[i] = originalChar
                if (newWord == endWord || newWord in wordSet) add(newWord)
            }
        }
    }

    // BFS from the END: distMap[w] = distance from w to endWord
    private fun buildPrunedGraph(beginWord: String, endWord: String, wordSet: Set<String>): GraphData {
        val distMap = mutableMapOf<String, Int>()
        val graph = mutableMapOf<String, MutableSet<String>>()
        val queue = ArrayDeque<String>()
        var foundEnd = false

        distMap[endWord] = 0
        queue.addLast(endWord)

        while (queue.isNotEmpty()) {
            val word = queue.removeFirst()
            for (neighbor in getNeighbors(word, wordSet, endWord)) {
                if (neighbor !in distMap) {            // BFS: first visit = shortest
                    distMap[neighbor] = distMap[word]!! + 1
                    graph.getOrPut(neighbor) { mutableSetOf() }.add(word)   // edge toward end
                    queue.addLast(neighbor)
                    if (neighbor == beginWord) foundEnd = true
                }
            }
        }
        return GraphData(graph, distMap, foundEnd)
    }

    // DFS walking strictly closer to the end: every completed path is shortest
    private fun reconstructPaths(
        current: String, endWord: String, graph: Map<String, Set<String>>,
        path: MutableList<String>, result: MutableList<List<String>>
    ) {
        path.add(current)
        if (current == endWord) {
            result.add(ArrayList(path))
        } else {
            graph[current]?.forEach { next ->          // edges built toward endWord
                reconstructPaths(next, endWord, graph, path, result)
            }
        }
        path.removeLast()                              // undo (the [12.0] contract)
    }
}
```

```python
from collections import deque

def find_ladders(begin_word: str, end_word: str, word_list: list[str]) -> list[list[str]]:
    """
    @param begin_word: start word
    @param end_word:   target word
    @param word_list:  intermediate words
    @return:           all shortest transformation sequences
    """
    word_set = set(word_list)
    if end_word not in word_set:
        return []

    def neighbors(word: str) -> list[str]:
        result = []
        for i in range(len(word)):
            for c in "abcdefghijklmnopqrstuvwxyz":
                if c == word[i]:
                    continue
                nxt = word[:i] + c + word[i+1:]
                if nxt in word_set:
                    result.append(nxt)
        return result

    # BFS from the end: dist[w] = distance from w to endWord; graph keeps edges toward the end
    dist = {end_word: 0}
    graph = {}
    queue = deque([end_word])
    found = False

    while queue:
        word = queue.popleft()
        for nxt in neighbors(word):
            if nxt not in dist:                 # BFS: first visit = shortest
                dist[nxt] = dist[word] + 1
                graph.setdefault(nxt, []).append(word)
                queue.append(nxt)
                if nxt == begin_word:
                    found = True

    if not found:
        return []

    result = []

    def dfs(word: str, path: list[str]) -> None:
        if word == end_word:
            result.append(path[:])
            return
        for nxt in graph.get(word, []):         # edges built toward endWord
            path.append(nxt)
            dfs(nxt, path)
            path.pop()                          # undo

    dfs(begin_word, [begin_word])
    return result
```

```rust
use std::collections::{HashMap, HashSet, VecDeque};

impl Solution {
    /// @param begin_word start word
    /// @param end_word   target word
    /// @param word_list  intermediate words
    /// @return           all shortest transformation sequences
    pub fn find_ladders(begin_word: String, end_word: String, word_list: Vec<String>) -> Vec<Vec<String>> {
        let words: HashSet<String> = word_list.into_iter().collect();
        if !words.contains(&end_word) { return vec![]; }

        let neighbors = |w: &String| -> Vec<String> {
            let mut out = Vec::new();
            let bytes = w.as_bytes();
            for i in 0..bytes.len() {
                for c in b'a'..=b'z' {
                    if c == bytes[i] { continue; }
                    let mut nb = bytes.to_vec();
                    nb[i] = c;
                    let s = String::from_utf8(nb).unwrap();
                    if words.contains(&s) { out.push(s); }
                }
            }
            out
        };

        let mut dist: HashMap<String, i32> = HashMap::new();
        let mut graph: HashMap<String, Vec<String>> = HashMap::new();
        let mut queue = VecDeque::new();
        dist.insert(end_word.clone(), 0);
        queue.push_back(end_word.clone());
        let mut found = false;

        while let Some(word) = queue.pop_front() {
            for nxt in neighbors(&word) {
                if !dist.contains_key(&nxt) {            // BFS: first visit = shortest
                    dist.insert(nxt.clone(), dist[&word] + 1);
                    graph.entry(nxt.clone()).or_default().push(word.clone());
                    queue.push_back(nxt.clone());
                    if nxt == begin_word { found = true; }
                }
            }
        }

        if !found { return vec![]; }

        let mut result = Vec::new();
        let mut path = vec![begin_word.clone()];
        fn dfs(word: String, end: &String, graph: &HashMap<String, Vec<String>>,
               path: &mut Vec<String>, result: &mut Vec<Vec<String>>) {
            if &word == end { result.push(path.clone()); return; }
            if let Some(nexts) = graph.get(&word) {
                for nxt in nexts {
                    path.push(nxt.clone());
                    dfs(nxt.clone(), end, graph, path, result);
                    path.pop();                          // undo
                }
            }
        }
        dfs(begin_word, &end_word, &graph, &mut path, &mut result);
        result
    }
}
```

> **Sources:** [`src/main/kotlin/graph/WordLadder_II_FinalCutPro.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/WordLadder_II_FinalCutPro.kt) · `WordLadder_II.kt` · `WordLadder_II_clean.kt` (the [6.13](../ch06-graphs/word-ladder-ii.md) page documents the clean one)
> **Pattern:** variant gallery — forward vs backward BFS

### The family map

| File | BFS direction | Path reconstruction |
|---|---|---|
| `WordLadder_II_clean.kt` | **from the end** (distances from `endWord`) | DFS over edges built toward the end |
| `WordLadder_II_FinalCutPro.kt` | **from the begin** (distances from `beginWord`) | DFS over edges built toward... also built during BFS |
| `WordLadder_II.kt` | forward, full-path BFS | paths stored in the queue (memory-heavy) |

### The star: `WordLadder_II_FinalCutPro.kt`

The forward version with a subtle adjacency rule — it adds edge `next → curr` *even when `next` was already visited at the same distance*, which is what keeps *all* shortest paths (not just one):

```kotlin
class WordLadder_II_FinalCutPro {
    fun findLadders(beginWord: String, endWord: String, wordList: List<String>): List<List<String>> {
        val wordSet = wordList.toSet()
        if (endWord !in wordSet) return emptyList()

        val adj = mutableMapOf<String, MutableList<String>>()
        val distance = mutableMapOf<String, Int>().apply { put(beginWord, 0) }
        val queue = ArrayDeque<String>().apply { add(beginWord) }
        var found = false

        fun getNeighbors(curr: String): List<String> {
            val neighbors = mutableListOf<String>()
            for (i in curr.indices) {
                for (char in 'a'..'z') {
                    if (char == curr[i]) continue
                    val next = curr.substring(0, i) + char + curr.substring(i + 1)
                    if (next in wordSet) neighbors.add(next)
                }
            }
            return neighbors
        }

        // Phase 1: BFS
        while (queue.isNotEmpty() && !found) {
            repeat(queue.size) {
                val curr = queue.removeFirst()
                val currDist = distance[curr]!!

                for (next in getNeighbors(curr)) {
                    if (distance[next] == null || distance[next] == currDist + 1) {
                        adj.getOrPut(next) { mutableListOf() }.add(curr)

                        if (distance[next] == null) {
                            distance[next] = currDist + 1
                            queue.add(next)
                        }
                        if (next == endWord) found = true
                    }
                }
            }
        }

        // Phase 2: DFS reconstruction over the adjacency (paths toward beginWord)
        val result = mutableListOf<List<String>>()
        fun dfs(node: String, path: MutableList<String>) {
            if (node == beginWord) {
                result.add(path.reversed())
                return
            }
            adj[node]?.forEach { prev ->
                path.add(prev)
                dfs(prev, path)
                path.removeLast()
            }
        }

        if (found) {
            dfs(endWord, mutableListOf(endWord))
        }
        return result
    }
}
```

**What makes it cool — and correct:**

- **`distance[next] == null || distance[next] == currDist + 1`** — the second disjunct is the whole point. It admits a word into the adjacency even if *another* BFS branch already reached it at the same distance. That's what produces *both* `hit→hot→dot→dog→cog` and `hit→hot→lot→log→cog` — a plain "only first visit" BFS would drop the second branch.
- **The edge direction is `next → curr`** (the predecessor) — so the DFS runs *backward from `endWord`* and the final `path.reversed()` restores the forward order. Same post-order discipline as [6.13](../ch06-graphs/word-ladder-ii.md)'s backward-BFS, mirrored.
- **`found` gates the DFS** — no reconstruction when the target is unreachable.
- **The `repeat(queue.size)` fence** — each BFS level is one distance; the `!found` loop guard stops the moment `endWord` is discovered at its shortest depth.

### The contrast: `WordLadder_II.kt` (paths in the queue)

The naive (and memory-explosive) version stores *paths* in the BFS queue — every level multiplies the copies:

```kotlin
// sketch of the memory-heavy shape (WordLadder_II.kt)
// queue of List<String> paths; each expansion clones the path and appends the neighbor.
// Correct, and fine for small word lists — but a dense graph copies O(paths × words)
// per level, which is why the adjacency versions above exist.
```

The three files together are the classic interview progression: naive-path-BFS → correct-but-heavy → adjacency + reconstruction. If an interviewer asks "your BFS stores too much — how do we fix it?", the answer is the `FinalCutPro` refactor: BFS builds the *graph*, DFS walks it.

### Dry run

**Input:** `beginWord = "hit"`, `endWord = "cog"`, `wordList = [hot,dot,dog,lot,log,cog]`.

```
BFS from "hit": distance hit=0.
  level 1: neighbors of hit: hot.  dist[hot]=1.  adj[hot] += hit.
  level 2: neighbors of hot: dot, lot.  dist=2.  adj[dot] += hot; adj[lot] += hot.
  level 3: dot -> dog (dist 3, adj[dog]+=dot); lot -> log (dist 3, adj[log]+=lot).
           dog -> cog? dog's neighbors: dot(seen), log(seen), cog -> dist 4.  adj[cog]+=dog.
           log -> cog: dist 4 already -> `== currDist+1` admits: adj[cog]+=log.  found=true.

adj: hot:[hit], dot:[hot], lot:[hot], dog:[dot], log:[lot], cog:[dog, log]

DFS from cog: cog -> dog -> dot -> hot -> hit  = [hit,hot,dot,dog,cog] ✓
             cog -> log -> lot -> hot -> hit  = [hit,hot,lot,log,cog] ✓
```

The `== currDist + 1` admission at `cog` is the crux: `dog` reaches it first (creating `adj[cog]+=dog`), and `log` reaches it *at the same distance* (creating `adj[cog]+=log`) — without that clause, the second shortest path would silently vanish.


## Dry run

**Input:** `beginWord = "hit"`, `endWord = "cog"`, `wordList = ["hot","dot","dog","lot","log","cog"]`.

```
BFS from "cog" (dist from the end):
  cog:0.  neighbors in set: dog, log -> dist 1.
  dog:1 -> dot -> dist 2.   log:1 -> lot -> dist 2.
  dot:2 -> hot -> dist 3.   lot:2 -> hot (already at 3).
  hot:3 -> hit -> dist 4.   foundEnd = true.

graph (edges toward the end): hit->{hot}, hot->{dot, lot}, dot->{dog}, lot->{log}, dog->{cog}, log->{cog}

DFS from "hit":
  hit -> hot -> dot -> dog -> cog      -> path [hit,hot,dot,dog,cog] ✓
  hit -> hot -> lot -> log -> cog      -> path [hit,hot,lot,log,cog] ✓

Output: both shortest sequences (length 5) ✓
```

The distance monotonicity is the guarantee: every DFS edge moves from distance `d` to `d - 1` toward `cog`, so any path that lands on `cog` has length exactly 4 — shortest by construction. The same word is reachable via `dot` and `lot` (both dist 2 from `hot`), which is what forks the two answers.

## Complexity

**Time.** BFS over neighbors + DFS over paths:

$$
T(n, L) = O(26 \cdot L \cdot n + \text{paths})
$$

**Space.** dist, graph, and the recursion:

$$
S = O(n \cdot L + \text{paths} \cdot L)
$$

## Variants & follow-ups

- **Word Ladder** ([6.1](word-ladder.md)) — the distance-only version: same BFS, no reconstruction.
- **Minimum Genetic Mutations** (`graph/MinimumGeneticMutations.kt`) — the same one-char-diff BFS over 4-letter genes.
- **Interview follow-up:** "Why BFS from the *end*?" Either direction works for distances; building `graph` as "edges toward the end" makes the DFS's distance check implicit — each node's stored neighbors are *already* the closer ones. That's the pruned-graph trick that keeps path search linear in the output size.
