# 6.1 Word Ladder

> **Source:** [`src/main/kotlin/graph/WordLadder.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/WordLadder.kt)
> **Pattern:** BFS on an implicit graph · **Core page**

## The Problem

Given `beginWord`, `endWord`, and a `wordList`, return the length of the **shortest transformation sequence** from `beginWord` to `endWord` — where each step changes exactly one letter, and every intermediate word (including the end) must be in `wordList`. Return `0` if impossible.

- Constraints: $1 \le n \le 5000$; all words the same length $L \le 10$; lowercase letters only.

## Examples

```
Input:  beginWord = "hit", endWord = "cog",
        wordList  = ["hot","dot","dog","lot","log","cog"]
Output: 5   (hit -> hot -> dot -> dog -> cog, five words in the chain)

Input:  beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log"]
Output: 0   (cog isn't in the list, so it's unreachable)
```

## Intuition — the graph is hiding in the rule

The problem never mentions graphs, but "words connected if they differ by one letter" *is* an edge rule. Each word is a **vertex**; two vertices are connected when they differ in exactly one position. The shortest transformation is then the **shortest path** between two vertices — and since every edge costs one step, that's unweighted shortest path, i.e. **BFS**.

Two ways to build it:

1. **Explicitly** — compute all pairs differing by one letter: $O(n^2 \cdot L)$ — fine for small $n$, death at $n = 5000$.
2. **Implicitly** — never build the graph at all; for a given word, *generate* its neighbors by trying all $26$ letters at each of the $L$ positions and checking membership in a `HashSet`. That's $O(26 \cdot L)$ per word instead of $O(n \cdot L)$ — and $n$ is usually much bigger than $26 \cdot L$.

This "implicit graph" move (from the [primer](pattern-primer.md)) is the whole lesson: when the edge rule is cheap to evaluate, don't materialize the graph.

**The state tuple:** the queue carries `(word, level)` because the *level* is the answer. BFS's "first time a vertex is seen is the shortest distance" property means the moment `endWord` is generated, the level it lands on is the minimum — no second pass needed.

**The `wordSet.remove(...)` trick:** instead of a separate `visited` set, delete each word from the set when enqueued. Same effect, one less structure, and it also guarantees we never re-queue a word.

## Approach 1 — Explicit graph, then BFS

Precompute `adj[w] = all words differing in one letter`, then BFS. $O(n^2 L)$ time, $O(n^2)$ space. Correct — and the exact candidate to *avoid* in the interview, since it explodes at $n = 5000$.

## Approach 2 — Implicit neighbor generation (the repo's version, optimal)

```kotlin
import java.util.*

class WordLadder {
    /**
     * @param beginWord starting word of the chain
     * @param endWord   target word
     * @param wordList  dictionary of allowed intermediate words
     * @return          shortest chain length (words in the sequence), 0 if impossible
     */
    fun ladderLength(beginWord: String, endWord: String, wordList: List<String>): Int {
        if (endWord !in wordList) return 0                     // unreachable end

        val wordSet = wordList.toHashSet()                     // O(1) membership
        val queue: Queue<Pair<String, Int>> = LinkedList()     // state = (word, level)
        queue.offer(beginWord to 1)

        while (queue.isNotEmpty()) {
            val (currentWord, level) = queue.poll()

            for (i in currentWord.indices) {
                val originalChar = currentWord[i]
                for (ch in 'a'..'z') {                         // generate neighbors lazily
                    val newWord = currentWord.substring(0, i) + ch + currentWord.substring(i + 1)

                    if (newWord == endWord) return level + 1   // first sighting = shortest

                    if (newWord in wordSet) {
                        wordSet.remove(newWord)                // visited == removed
                        queue.offer(newWord to level + 1)
                    }
                }
            }
        }
        return 0
    }
}
```

```java
import java.util.*;

public class WordLadder {
    /**
     * @param beginWord starting word of the chain
     * @param endWord   target word
     * @param wordList  dictionary of allowed intermediate words
     * @return          shortest chain length (words in the sequence), 0 if impossible
     */
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        Queue<Map.Entry<String, Integer>> queue = new LinkedList<>();
        queue.offer(new AbstractMap.SimpleEntry<>(beginWord, 1));

        while (!queue.isEmpty()) {
            Map.Entry<String, Integer> state = queue.poll();
            String current = state.getKey();
            int level = state.getValue();

            char[] chars = current.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char original = chars[i];
                for (char c = 'a'; c <= 'z'; c++) {
                    chars[i] = c;
                    String next = new String(chars);

                    if (next.equals(endWord)) return level + 1;
                    if (wordSet.remove(next)) {
                        queue.offer(new AbstractMap.SimpleEntry<>(next, level + 1));
                    }
                }
                chars[i] = original;
            }
        }
        return 0;
    }
}
```

```cpp
#include <queue>
#include <string>
#include <unordered_set>
#include <vector>

class WordLadder {
public:
    /**
     * @param beginWord starting word of the chain
     * @param endWord   target word
     * @param wordList  dictionary of allowed intermediate words
     * @return          shortest chain length (words in the sequence), 0 if impossible
     */
    int ladderLength(std::string beginWord, std::string endWord, std::vector<std::string>& wordList) {
        std::unordered_set<std::string> wordSet(wordList.begin(), wordList.end());
        if (!wordSet.count(endWord)) return 0;

        std::queue<std::pair<std::string, int>> q;
        q.push({beginWord, 1});

        while (!q.empty()) {
            auto [word, level] = q.front();
            q.pop();

            for (int i = 0; i < (int)word.size(); i++) {
                char original = word[i];
                for (char c = 'a'; c <= 'z'; c++) {
                    word[i] = c;
                    if (word == endWord) return level + 1;
                    if (wordSet.erase(word)) {
                        q.push({word, level + 1});
                    }
                }
                word[i] = original;
            }
        }
        return 0;
    }
};
```

```python
from collections import deque

def ladder_length(begin_word: str, end_word: str, word_list: list[str]) -> int:
    """
    @param begin_word: starting word of the chain
    @param end_word:   target word
    @param word_list:  dictionary of allowed intermediate words
    @return:           shortest chain length (words in the sequence), 0 if impossible
    """
    word_set = set(word_list)
    if end_word not in word_set:
        return 0

    queue = deque([(begin_word, 1)])          # state = (word, level)
    while queue:
        word, level = queue.popleft()

        for i in range(len(word)):
            for c in "abcdefghijklmnopqrstuvwxyz":
                next_word = word[:i] + c + word[i + 1:]
                if next_word == end_word:
                    return level + 1
                if next_word in word_set:
                    word_set.remove(next_word)  # visited == removed
                    queue.append((next_word, level + 1))
    return 0
```

```rust
use std::collections::{HashSet, VecDeque};

impl Solution {
    /// @param begin_word starting word of the chain
    /// @param end_word   target word
    /// @param word_list  dictionary of allowed intermediate words
    /// @return           shortest chain length (words in the sequence), 0 if impossible
    pub fn ladder_length(begin_word: String, end_word: String, word_list: Vec<String>) -> i32 {
        let mut word_set: HashSet<String> = word_list.into_iter().collect();
        if !word_set.contains(&end_word) {
            return 0;
        }

        let mut queue = VecDeque::new();
        queue.push_back((begin_word, 1));

        while let Some((word, level)) = queue.pop_front() {
            let bytes = word.as_bytes();
            for i in 0..bytes.len() {
                for c in b'a'..=b'z' {
                    let mut next = word.clone();
                    unsafe { next.as_bytes_mut()[i] = c; }
                    if next == end_word {
                        return level + 1;
                    }
                    if word_set.remove(&next) {
                        queue.push_back((next, level + 1));
                    }
                }
            }
        }
        0
    }
}
```

> **Rust note:** mutating a byte inside a `String` is safe here only because `c` is always a lowercase ASCII letter — replacing one byte with another ASCII byte can't break UTF-8. The `unsafe` block documents exactly why.

## Dry run

**Input:** `beginWord = "hit"`, `endWord = "cog"`, `wordList = ["hot","dot","dog","lot","log","cog"]`

```
queue: [(hit,1)]                     wordSet = {hot,dot,dog,lot,log,cog}
pop (hit,1):
  try 26 letters at each of 3 positions -> "hot" found in set
  remove "hot", push (hot,2)         wordSet = {dot,dog,lot,log,cog}
pop (hot,2):
  neighbors: "dot" (in set) -> push (dot,3); "lot" (in set) -> push (lot,3)
  also tries "hot" itself and others; "cog"? no (2-letter diff from hot)
  wordSet = {dog,log,cog}
pop (dot,3):
  neighbor "dog" -> push (dog,4)     wordSet = {log,cog}
pop (lot,3):
  neighbor "log" -> push (log,4)     wordSet = {cog}
pop (dog,4):
  neighbor "cog" == endWord -> return 4 + 1 = 5 ✓
```

Note the two *valid* chains (`hit→hot→dot→dog→cog` and `hit→hot→lot→log→cog`) are both length 5; BFS explores them level by level and returns the first completion. If `cog` were absent from the set, the queue would drain and the function returns `0`.

## Complexity

**Time.** Each word generates at most $26 \cdot L$ neighbors; each generated word costs $O(1)$ set lookups (amortized, with $O(L)$ for the string build):

$$
T(n, L) = O(n \cdot 26 \cdot L) = O(n \cdot L)
$$

**Space.** The set plus the queue hold at most one copy of every word:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Word Ladder II** (`src/main/kotlin/graph/WordLadder_II.kt`, `WordLadder_II_clean.kt`) — *all* shortest paths, not just the length: BFS to record each word's level, then DFS backtracking that only walks level-decreasing edges.
- **Bidirectional BFS** — run BFS from both ends and stop when the two frontiers meet: worst case still $O(n \cdot L)$, but typically explores a much smaller frontier. The classic "how do we make it faster?" follow-up.
- **Minimum Genetic Mutations** (`src/main/kotlin/graph/MinimumGeneticMutations.kt`) — identical engine, DNA alphabet of 4 instead of 26, and `start` need not be in the bank.
- **Bus Routes** (`src/main/kotlin/graph/BusRoutes.kt`) — BFS over a *two-layer* graph (buses ↔ stops); the trick is choosing which layer to walk.
- **Interview follow-up:** "Why BFS and not DFS?" DFS would find *a* path but not necessarily the shortest — and with cycles it needs extra bookkeeping. BFS's level property gives the minimum for free, which is exactly what "shortest transformation" asks for.
