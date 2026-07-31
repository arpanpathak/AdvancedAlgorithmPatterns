# 19.8 Word Ladder II — The Three Implementations, Captured

> **Sources:** [`src/main/kotlin/graph/WordLadder_II_FinalCutPro.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/WordLadder_II_FinalCutPro.kt) · `WordLadder_II.kt` · `WordLadder_II_clean.kt` (the [6.13](../ch06-graphs/word-ladder-ii.md) page documents the clean one)
> **Pattern:** variant gallery — forward vs backward BFS

## The family map

| File | BFS direction | Path reconstruction |
|---|---|---|
| `WordLadder_II_clean.kt` | **from the end** (distances from `endWord`) | DFS over edges built toward the end |
| `WordLadder_II_FinalCutPro.kt` | **from the begin** (distances from `beginWord`) | DFS over edges built toward... also built during BFS |
| `WordLadder_II.kt` | forward, full-path BFS | paths stored in the queue (memory-heavy) |

## The star: `WordLadder_II_FinalCutPro.kt`

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

## The contrast: `WordLadder_II.kt` (paths in the queue)

The naive (and memory-explosive) version stores *paths* in the BFS queue — every level multiplies the copies:

```kotlin
// sketch of the memory-heavy shape (WordLadder_II.kt)
// queue of List<String> paths; each expansion clones the path and appends the neighbor.
// Correct, and fine for small word lists — but a dense graph copies O(paths × words)
// per level, which is why the adjacency versions above exist.
```

The three files together are the classic interview progression: naive-path-BFS → correct-but-heavy → adjacency + reconstruction. If an interviewer asks "your BFS stores too much — how do we fix it?", the answer is the `FinalCutPro` refactor: BFS builds the *graph*, DFS walks it.

## Dry run

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

## Complexity

**Time.** BFS over neighbor generation + DFS over paths:

$$
T(n, L) = O(26 \cdot L \cdot n + \text{paths})
$$

**Space.** adjacency + distance maps (+ paths in output):

$$
S = O(n \cdot L + \text{paths})
$$

## Variants & follow-ups

- **Word Ladder II** ([6.13](../ch06-graphs/word-ladder-ii.md)) — the backward-BFS page; this page's forward version is its mirror twin.
- **Word Ladder** ([6.1](../ch06-graphs/word-ladder.md)) — the distance-only base.
- **Interview follow-up:** "Backward vs forward BFS — which and why?" Backward (6.13) can prune with a *bidirectional* early-exit more naturally; forward (this page) reads more directly. The `== dist + 1` admission rule is mandatory in **both** directions — it's the "keep all shortest branches" rule, not a direction choice.
