# 17.13 Minimum Genetic Mutations

> **Source:** [`src/main/kotlin/graph/MinimumGeneticMutations.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/MinimumGeneticMutations.kt)
> **Pattern:** 4-neighbor BFS with a bank set · **Core page**

## The Problem

A gene string is 8 chars from `A,C,G,T`. A valid mutation changes **one char** to a bank-valid string. Min mutations from `startGene` to `endGene`, or -1.

- Constraints: bank size ≤ 10; all strings length 8.

## Examples

```
Input:  start = "AACCGGTT", end = "AAACGGTA", bank = ["AACCGGTA","AACCGCTA","AAACGGTA"]
Output: 2   ("AACCGGTT" -> "AACCGGTA" -> "AAACGGTA")
```

## Intuition — it's Word Ladder ([6.1](../ch06-graphs/word-ladder.md)) with 4 letters

Each gene is a node; an edge connects genes differing in one position. BFS from `start` counting levels = mutations. The neighbor generator tries all `4 × 8` one-char changes and keeps the ones in the bank:

```kotlin
val bases = listOf('A','C','G','T')
val geneBank = bank.toMutableSet()
if (endGene !in geneBank) return -1

val queue = ArrayDeque<String>().apply { add(startGene) }
var mutations = 0

fun getNeighbors(gene: String): List<String> {
    val neighbors = mutableListOf<String>()
    val arr = gene.toCharArray()
    for (i in arr.indices) {
        for (base in bases) {
            if (base != gene[i]) {
                arr[i] = base
                neighbors.add(String(arr))
            }
        }
        arr[i] = gene[i]                    // restore
    }
    return neighbors
}
// BFS: for each level, mutate every queued gene; check bank membership;
// remove used genes from the bank (dedupe by construction)
```

**Why remove used genes from the bank?** The bank doubles as the visited set — a gene removed from `geneBank` can't be re-visited (BFS reaches each gene at its shortest distance first). One structure serves both roles, exactly like [6.1](../ch06-graphs/word-ladder.md)'s `wordSet` pruning.

**Why `if (endGene !in geneBank) return -1` up front?** If the target isn't a valid mutation, no path exists — the check prunes the BFS before it starts.

## Approach 1 — BFS over the bank with a distance map (the [6.1](../ch06-graphs/word-ladder.md) recipe)

Queue + distances, explicit visited set: correct — this page's algorithm with a map instead of bank-removal.

## Approach 2 — BFS with bank-as-visited (the repo's version, optimal)

```kotlin
class MinimumGeneticMutations {
    /**
     * @param startGene starting gene string
     * @param endGene   target gene string
     * @param bank      valid gene strings
     * @return          min mutations, or -1
     */
    fun minMutation(startGene: String, endGene: String, bank: Array<String>): Int {
        val bases = listOf('A', 'C', 'G', 'T')
        val geneBank = bank.toMutableSet()
        if (endGene !in geneBank) return -1

        val queue = ArrayDeque<String>().apply { add(startGene) }
        var mutations = 0

        fun getNeighbors(gene: String): List<String> {
            val neighbors = mutableListOf<String>()
            val arr = gene.toCharArray()

            for (i in arr.indices) {
                for (base in bases) {
                    if (base != gene[i]) {
                        arr[i] = base
                        neighbors.add(String(arr))
                    }
                }
                arr[i] = gene[i]                 // restore
            }
            return neighbors
        }

        while (queue.isNotEmpty()) {
            mutations++
            repeat(queue.size) {                 // one level = one mutation
                val gene = queue.removeFirst()

                for (next in getNeighbors(gene)) {
                    if (next in geneBank) {      // valid mutation, not yet used
                        if (next == endGene) return mutations
                        geneBank.remove(next)    // visited
                        queue.add(next)
                    }
                }
            }
        }
        return -1
    }
}
```

```java
import java.util.*;

public class MinimumGeneticMutations {
    /**
     * @param startGene starting gene string
     * @param endGene   target gene string
     * @param bank      valid gene strings
     * @return          min mutations, or -1
     */
    public int minMutation(String startGene, String endGene, String[] bank) {
        char[] bases = {'A', 'C', 'G', 'T'};
        Set<String> geneBank = new HashSet<>(Arrays.asList(bank));
        if (!geneBank.contains(endGene)) return -1;

        Queue<String> queue = new LinkedList<>();
        queue.offer(startGene);
        int mutations = 0;

        while (!queue.isEmpty()) {
            mutations++;
            int size = queue.size();             // one level = one mutation

            for (int s = 0; s < size; s++) {
                String gene = queue.poll();
                char[] arr = gene.toCharArray();

                for (int i = 0; i < 8; i++) {
                    for (char base : bases) {
                        if (base != arr[i]) {
                            char saved = arr[i];
                            arr[i] = base;
                            String next = new String(arr);
                            arr[i] = saved;      // restore

                            if (geneBank.remove(next)) {   // visited on removal
                                if (next.equals(endGene)) return mutations;
                                queue.offer(next);
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }
}
```

```cpp
#include <queue>
#include <string>
#include <unordered_set>
#include <vector>

class MinimumGeneticMutations {
public:
    /**
     * @param startGene starting gene string
     * @param endGene   target gene string
     * @param bank      valid gene strings
     * @return          min mutations, or -1
     */
    int minMutation(std::string startGene, std::string endGene, std::vector<std::string>& bank) {
        const char bases[4] = {'A', 'C', 'G', 'T'};
        std::unordered_set<std::string> geneBank(bank.begin(), bank.end());
        if (!geneBank.count(endGene)) return -1;

        std::queue<std::string> queue;
        queue.push(startGene);
        int mutations = 0;

        while (!queue.empty()) {
            mutations++;
            int size = queue.size();             // one level = one mutation

            for (int s = 0; s < size; s++) {
                std::string gene = queue.front(); queue.pop();

                for (int i = 0; i < 8; i++) {
                    for (char base : bases) {
                        if (base != gene[i]) {
                            char saved = gene[i];
                            gene[i] = base;
                            std::string next = gene;
                            gene[i] = saved;     // restore

                            if (geneBank.erase(next)) {   // visited on removal
                                if (next == endGene) return mutations;
                                queue.push(next);
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }
};
```

```python
from collections import deque

def min_mutation(start_gene: str, end_gene: str, bank: list[str]) -> int:
    """
    @param start_gene: starting gene string
    @param end_gene:   target gene string
    @param bank:       valid gene strings
    @return:           min mutations, or -1
    """
    bases = "ACGT"
    gene_bank = set(bank)
    if end_gene not in gene_bank:
        return -1

    queue = deque([start_gene])
    mutations = 0

    while queue:
        mutations += 1
        for _ in range(len(queue)):          # one level = one mutation
            gene = queue.popleft()

            for i in range(8):
                for base in bases:
                    if base != gene[i]:
                        nxt = gene[:i] + base + gene[i + 1:]
                        if nxt in gene_bank:   # valid mutation, not yet used
                            if nxt == end_gene:
                                return mutations
                            gene_bank.remove(nxt)   # visited on removal
                            queue.append(nxt)

    return -1
```

```rust
use std::collections::{HashSet, VecDeque};

impl Solution {
    /// @param start_gene starting gene string
    /// @param end_gene   target gene string
    /// @param bank       valid gene strings
    /// @return           min mutations, or -1
    pub fn min_mutation(start_gene: String, end_gene: String, bank: Vec<String>) -> i32 {
        let bases = ['A', 'C', 'G', 'T'];
        let mut gene_bank: HashSet<String> = bank.into_iter().collect();
        if !gene_bank.contains(&end_gene) { return -1; }

        let mut queue = VecDeque::new();
        queue.push_back(start_gene);
        let mut mutations = 0;

        while let Some(gene) = queue.pop_front() {
            mutations += 1;
            let size = queue.len();          // (level-fenced in the Kotlin version; same idea)

            let mut arr: Vec<char> = gene.chars().collect();
            for i in 0..8 {
                for &base in &bases {
                    if base != arr[i] {
                        let saved = arr[i];
                        arr[i] = base;
                        let nxt: String = arr.iter().collect();
                        arr[i] = saved;      // restore

                        if gene_bank.remove(&nxt) {   // visited on removal
                            if nxt == end_gene { return mutations; }
                            queue.push_back(nxt);
                        }
                    }
                }
            }
        }
        -1
    }
}
```

## Dry run

**Input:** `start = "AACCGGTT"`, `end = "AAACGGTA"`, `bank = ["AACCGGTA","AACCGCTA","AAACGGTA"]`.

```
geneBank = {AACCGGTA, AACCGCTA, AAACGGTA}.  end in bank ✓.  queue=[AACCGGTT]

level 1 (1 mutation): neighbors of AACCGGTT with one change:
   position 2: A->A? same.  C->A: "AAACGGTT" not in bank.  ...
   position 7: T->A: "AACCGGTA" IN bank.  not end.  remove.  enqueue.  queue=[AACCGGTA]

level 2 (2 mutations): neighbors of AACCGGTA:
   position 6: G->A: "AACCGGAA" no.  position 2: C->A: "AAACGGTA" IN bank == end -> return 2 ✓
```

The BFS fence (one level = one mutation) plus the bank-removal visited-set: the first level finds `AACCGGTA` (the only 1-char change in the bank); the second finds `AAACGGTA` at exactly 2. A longer bank would fan out more, but the removal keeps each gene's first-visit distance minimal.

## Complexity

**Time.** 4 × 8 neighbors per gene, bank lookup O(1):

$$
T(n) = O(32 \cdot n) = O(n)
$$

**Space.** The bank + queue:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Word Ladder** ([6.1](../ch06-graphs/word-ladder.md)) — the 26-letter sibling; same BFS, bigger alphabet.
- **Bus Routes** ([17.12](bus-routes.md)) — the two-layer BFS that models a cost per *vehicle* instead of per *edit*.
- **Interview follow-up:** "Why is the bank both the dictionary and the visited set?" BFS visits each gene at its shortest distance; the first time a mutation lands on a bank gene, that *is* the minimum distance. Removing it from the bank prevents later, longer re-visits — the [6.1](../ch06-graphs/word-ladder.md) pruning in its minimal form.
