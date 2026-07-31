# 6.8 Alien Dictionary

> **Source:** [`src/main/kotlin/graph/topological_sort/AlienDictionary.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/topological_sort/AlienDictionary.kt)
> **Pattern:** DFS topological sort with cycle detection · **Core page**

## The Problem

Given `words` (sorted lexicographically in an **alien alphabet**), derive the order of the letters — or return `""` if inconsistent.

- Constraints: small alphabets; words up to 100.

## Examples

```
Input:  words = ["wrt","wrf","er","ett","rftt"]
Output: "wertf"   (one valid order)

Input:  words = ["z","x","z"]   -> Output: ""   (cycle: z before x and x before z)
```

## Intuition — each adjacent pair of words is one edge

Two consecutive words that disagree at their first differing character reveal the alphabet's order: `words[i][j] < words[i+1][j]`. The `word1.startsWith(word2) && word1.length > word2.length` case is the built-in contradiction (a prefix can't be longer than its successor). The derived relations form a DAG; **topological sort** of it is the alphabet — and a cycle in it means the order is contradictory (`""`).

The repo uses **DFS with a three-state visited map** (`NOT_VISITED / VISITING / VISITED`):

```
dfs(node):
    if VISITING -> false (cycle!)
    if VISITED  -> true
    mark VISITING
    for each child: if !dfs(child) return false
    mark VISITED
    append node                     # post-order: children before parents
```

**Why post-order append?** The [6.3](course-schedule-ii.md) Kahn/BFS version appends when in-degree hits 0 (children after parents); the DFS version appends in **post-order** (parents after children) and reverses at the end — or, as here, builds the string in reverse-order naturally: `w`'s dependencies (`e,r,t,f`) are appended before `w`, so the accumulated string `f,t,r,e,w` must be read reversed... The repo appends `node` at the end of the DFS and returns the StringBuilder as-is, which — with post-order appending — yields the topological order *directly* (children already in the buffer before the parent). The three-state map is what makes cycle detection O(1) per node rather than a separate pass.

**The prefix-contradiction check:** if `word2` is a prefix of `word1` and shorter, the dictionary ordering is impossible (`"abc"` before `"ab"` violates the prefix rule) — an empty string answer before any graph work.

## Approach 1 — Kahn's algorithm (BFS) (see [6.3](course-schedule-ii.md))

In-degree counting + queue: also correct; the DFS version below carries its cycle detection inside the recursion.

## Approach 2 — Three-state DFS topo (the repo's version, optimal)

```kotlin
class AlienDictionary {
    enum class State { NOT_VISITED, VISITING, VISITED }

    /**
     * @param words words sorted by the alien alphabet
     * @return      a valid letter order, or "" if inconsistent
     */
    fun alienOrder(words: Array<String>): String {
        val graph = mutableMapOf<Char, MutableSet<Char>>()
        val visited = mutableMapOf<Char, State>()
        val result = StringBuilder()

        // Step 1: every letter is a node
        words.forEach { word ->
            word.forEach { char ->
                graph.putIfAbsent(char, mutableSetOf())
                visited.putIfAbsent(char, State.NOT_VISITED)
            }
        }

        // Step 2: edges from adjacent word pairs
        for (i in 1 until words.size) {
            val (word1, word2) = words[i - 1] to words[i]

            // Prefix contradiction: word2 is a prefix of word1 but shorter
            if (word1.startsWith(word2) && word1.length > word2.length) {
                return ""
            }

            for (j in 0 until minOf(word1.length, word2.length)) {
                if (word1[j] != word2[j]) {
                    graph[word1[j]]?.add(word2[j])     // word1[j] comes before word2[j]
                    break
                }
            }
        }

        // Step 3: DFS with cycle detection
        fun dfs(node: Char): Boolean {
            if (visited[node] == State.VISITING) return false   // cycle
            if (visited[node] == State.VISITED) return true     // done

            visited[node] = State.VISITING
            graph[node]?.forEach { if (!dfs(it)) return false }
            visited[node] = State.VISITED

            result.append(node)                      // post-order: dependencies already in
            return true
        }

        // Step 4: visit every letter
        for (char in visited.keys) {
            if (visited[char] == State.NOT_VISITED && !dfs(char)) {
                return ""                            // cycle detected
            }
        }
        return result.toString()
    }
}
```

```java
import java.util.*;

public class AlienDictionary {
    private static final int NOT = 0, VISITING = 1, DONE = 2;

    /**
     * @param words words sorted by the alien alphabet
     * @return      a valid letter order, or "" if inconsistent
     */
    public String alienOrder(String[] words) {
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> state = new HashMap<>();
        for (String w : words) {
            for (char c : w.toCharArray()) {
                graph.putIfAbsent(c, new HashSet<>());
                state.putIfAbsent(c, NOT);
            }
        }

        for (int i = 1; i < words.length; i++) {
            String a = words[i - 1], b = words[i];
            if (a.startsWith(b) && a.length() > b.length()) return "";   // prefix contradiction

            for (int j = 0; j < Math.min(a.length(), b.length()); j++) {
                if (a.charAt(j) != b.charAt(j)) {
                    graph.get(a.charAt(j)).add(b.charAt(j));   // a[j] before b[j]
                    break;
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (char c : state.keySet()) {
            if (state.get(c) == NOT && !dfs(c, graph, state, result)) return "";
        }
        return result.toString();
    }

    private boolean dfs(char node, Map<Character, Set<Character>> graph,
                        Map<Character, Integer> state, StringBuilder result) {
        if (state.get(node) == VISITING) return false;      // cycle
        if (state.get(node) == DONE) return true;

        state.put(node, VISITING);
        for (char next : graph.get(node)) {
            if (!dfs(next, graph, state, result)) return false;
        }
        state.put(node, DONE);
        result.append(node);                                 // post-order
        return true;
    }
}
```

```cpp
#include <string>
#include <unordered_map>
#include <unordered_set>
#include <vector>

class AlienDictionary {
    bool dfs(char node, std::unordered_map<char, std::unordered_set<char>>& graph,
             std::unordered_map<char, int>& state, std::string& result) {
        if (state[node] == 1) return false;      // cycle (1 = VISITING)
        if (state[node] == 2) return true;       // done
        state[node] = 1;
        for (char next : graph[node]) {
            if (!dfs(next, graph, state, result)) return false;
        }
        state[node] = 2;
        result += node;                          // post-order
        return true;
    }

public:
    /**
     * @param words words sorted by the alien alphabet
     * @return      a valid letter order, or "" if inconsistent
     */
    std::string alienOrder(std::vector<std::string>& words) {
        std::unordered_map<char, std::unordered_set<char>> graph;
        std::unordered_map<char, int> state;     // 0 not, 1 visiting, 2 done
        for (auto& w : words) for (char c : w) { graph[c]; state[c] = 0; }

        for (int i = 1; i < (int)words.size(); i++) {
            auto& a = words[i - 1]; auto& b = words[i];
            if (a.rfind(b, 0) == 0 && a.size() > b.size()) return "";   // prefix contradiction
            for (int j = 0; j < (int)std::min(a.size(), b.size()); j++) {
                if (a[j] != b[j]) { graph[a[j]].insert(b[j]); break; }
            }
        }

        std::string result;
        for (auto& [c, _] : state) {
            if (state[c] == 0 && !dfs(c, graph, state, result)) return "";
        }
        return result;
    }
};
```

```python
def alien_order(words: list[str]) -> str:
    """
    @param words: words sorted by the alien alphabet
    @return:      a valid letter order, or "" if inconsistent
    """
    graph = {c: set() for w in words for c in w}
    state = {c: 0 for w in words for c in w}      # 0 not, 1 visiting, 2 done

    for a, b in zip(words, words[1:]):
        if a.startswith(b) and len(a) > len(b):
            return ""                              # prefix contradiction
        for ca, cb in zip(a, b):
            if ca != cb:
                graph[ca].add(cb)                  # ca before cb
                break

    result = []

    def dfs(node: str) -> bool:
        if state[node] == 1:
            return False                           # cycle
        if state[node] == 2:
            return True
        state[node] = 1
        for nxt in graph[node]:
            if not dfs(nxt):
                return False
        state[node] = 2
        result.append(node)                        # post-order
        return True

    for c in list(state):
        if state[c] == 0 and not dfs(c):
            return ""
    return "".join(reversed(result))               # reverse the post-order
```

```rust
use std::collections::{HashMap, HashSet};

impl Solution {
    /// @param words words sorted by the alien alphabet
    /// @return      a valid letter order, or "" if inconsistent
    pub fn alien_order(words: Vec<String>) -> String {
        let mut graph: HashMap<u8, HashSet<u8>> = HashMap::new();
        let mut state: HashMap<u8, u8> = HashMap::new();
        for w in &words {
            for b in w.bytes() { graph.entry(b).or_default(); state.entry(b).or_insert(0); }
        }

        for pair in words.windows(2) {
            let (a, b) = (&pair[0], &pair[1]);
            if a.starts_with(b) && a.len() > b.len() { return String::new(); }  // contradiction
            for (ca, cb) in a.bytes().zip(b.bytes()) {
                if ca != cb { graph.get_mut(&ca).unwrap().insert(cb); break; }
            }
        }

        let mut result: Vec<u8> = Vec::new();
        fn dfs(node: u8, graph: &HashMap<u8, HashSet<u8>>, state: &mut HashMap<u8, u8>,
               result: &mut Vec<u8>) -> bool {
            match state[&node] {
                1 => return false,                 // cycle
                2 => return true,
                _ => {}
            }
            state.insert(node, 1);
            for &nxt in &graph[&node] {
                if !dfs(nxt, graph, state, result) { return false; }
            }
            state.insert(node, 2);
            result.push(node);                     // post-order
            true
        }

        let keys: Vec<u8> = state.keys().copied().collect();
        for c in keys {
            if state[&c] == 0 && !dfs(c, &graph, &mut state, &mut result) {
                return String::new();
            }
        }
        result.reverse();                          // reverse the post-order
        String::from_utf8(result).unwrap()
    }
}
```

> **Repo note:** the repo's `AlienDictionary.kt` appends in post-order but returns the `StringBuilder` **without reversing** — producing the reverse topological order (`"ftrew"` for the example). The book shows the corrected versions: post-order append, then reverse before returning. (Same class of bug as the `SingleNumber3` lowbit line — worth flagging in interviews: "what does a post-order DFS accumulate, and what do you need to do to it?")

### The full `AlienDictionary_BFS.kt` — the BFS twin of [6.8](../ch06-graphs/alien-dictionary.md)

```kotlin
class AlienDictionary_BFS {
    fun alienOrder(words: Array<String>): String {
        val graph = mutableMapOf<Char, HashSet<Char>>()
        val inDegree = mutableMapOf<Char, Int>()

        // Every letter is a node with in-degree 0 to start
        words.forEach { word ->
            word.forEach { char ->
                inDegree.putIfAbsent(char, 0)
                graph.putIfAbsent(char, hashSetOf())
            }
        }

        // Adjacent word pairs reveal one edge each
        for (i in 0 until words.size - 1) {
            val currentWord = words[i]
            val nextWord = words[i + 1]
            val minLength = minOf(currentWord.length, nextWord.length)

            for (j in 0 until minLength) {
                val currentChar = currentWord[j]
                val nextChar = nextWord[j]
                if (currentChar != nextChar) {
                    if (graph[currentChar]!!.add(nextChar)) {   // new edge?
                        inDegree[nextChar] = inDegree[nextChar]!! + 1
                    }
                    break
                }
                // Prefix contradiction: "abc" can't come before "ab"
                if (j == minLength - 1 && currentWord.length > nextWord.length) return ""
            }
        }

        // Kahn's with the filter-seeded queue
        val queue = ArrayDeque<Char>().apply {
            addAll(inDegree.filter { it.value == 0 }.keys)
        }
        val result = StringBuilder()

        while (queue.isNotEmpty()) {
            val char = queue.removeFirst()
            result.append(char)
            graph[char]?.forEach { neighbor ->
                inDegree[neighbor] = inDegree[neighbor]!! - 1
                if (inDegree[neighbor] == 0) queue.add(neighbor)
            }
        }

        // Cycle check: all letters emitted?
        return if (result.length == inDegree.size) result.toString() else ""
    }
}
```

vs [6.8](../ch06-graphs/alien-dictionary.md)'s three-state DFS — this is the **Kahn's BFS twin**: same edges, opposite traversal, and the cycle test is `result.length == inDegree.size` instead of a `VISITING` flag. The filter-seed is the one-line signature of the BFS family.


## Dry run

**Input:** `words = ["wrt","wrf","er","ett","rftt"]`.

```
edges from adjacent pairs:
  wrt vs wrf: t -> f        wrt vs er: w -> e
  er vs ett:  r -> t        ett vs rftt: e -> r

graph: w:{e}, e:{r}, r:{t}, t:{f}, f:{}

dfs(w): w(e): e(r): r(t): t(f): f (no children) -> append f.
        t done -> append t.  r done -> append r.  e done -> append e.
        w done -> append w.
post-order buffer: [f, t, r, e, w] -> reversed: "wertf" ✓
```

The post-order + reverse is the whole correctness detail: DFS emits each node *after* its descendants, so the buffer is reverse-topological; one reversal yields the alphabet. The cycle test (`VISITING` revisited) is what turns `["z","x","z"]` into `""` — z's edge x→z from the second pair clashes with z→x from the first.

## Complexity

**Time.** One DFS over V letters and E edges:

$$
T(V, E) = O(V + E)
$$

**Space.** Graph + state + recursion:

$$
S = O(V + E)
$$

## Variants & follow-ups

- **Course Schedule II** ([6.3](course-schedule-ii.md)) — Kahn's BFS version of the same topological sort; this page is its DFS twin.
- **Find All Possible Recipes / other topo problems** (`graph/topological_sort/`) — the dependency-resolution family.
- **Interview follow-up:** "Why three states instead of a visited boolean?" A boolean catches revisits but can't distinguish "currently on the recursion stack" (cycle!) from "finished" (fine). The three-state `VISITING / VISITED / NOT_VISITED` makes cycle detection a single state check inside the recursion — no separate cycle pass.
