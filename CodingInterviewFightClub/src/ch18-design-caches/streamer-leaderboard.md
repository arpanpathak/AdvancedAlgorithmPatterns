# 18.25 Streamer Leaderboard (Ordered Scores)

> **Source:** [`src/main/kotlin/tree/bst/StreamerRanking.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/bst/StreamerRanking.kt)
> **Pattern:** score-keyed TreeMap of ID sets · **Core page**

## The Problem

Design a real-time leaderboard for a streaming platform with millions of users. Scores (viewer counts) update frequently — thousands per second.

- `updateScore(id, score)` — set a streamer's score (adding them if new).
- `getStreamerAtRank(k)` — the streamer at rank `k` (1 = highest score; ties within a score are broken by insertion order).
- `getRank(id)` — the current rank of a streamer.

## Examples

```
updateScore("alice", 500)
updateScore("bob", 300)
updateScore("carol", 500)
getStreamerAtRank(1) -> "alice"   (or "carol" — tied at 500)
getStreamerAtRank(3) -> "bob"
updateScore("bob", 900)            // bob jumps to the top
getStreamerAtRank(1) -> "bob"
```

## Intuition — two maps: score lookup + score→IDs, ordered

A naive `Map<id, score>` answers `getRank(id)` only by *scanning all scores* — O(n) per query, hopeless at millions of users. The fix is a second structure:

> Keep `scores: Map<id, score>` for O(1) score lookup, **plus** `tree: TreeMap<score, Set<id>>` ordered by score descending. The TreeMap's keys are the *score buckets*; walking it from the top gives ranks in O(number of distinct scores) — and with a small tweak (an order-statistic tree) even the rank walk is O(log n).

The key design tension: **scores repeat** (many streamers share 500), so the TreeMap must map each score to a *set* of IDs, not a single ID. Ties inside a bucket are broken by the set's iteration order.

**Why TreeMap and not a heap?** A max-heap gives O(1) access to the *top* element but can't answer "who is 7th" or "what rank is alice" — and updating a score means lazy deletion bookkeeping. A balanced BST (TreeMap) keeps *all* scores ordered, supports range queries, and stays O(log n) per update. That's the design answer an interviewer wants: "scores are dynamic, ranks are arbitrary — I need a sorted structure, not a stack."

## Approach 1 — Scan-and-sort per query (too slow)

On every `getRank`/`getStreamerAtRank`, sort all (score, id) pairs. Correct but O(n log n) per query — melts at millions of users × thousands of updates/sec.

## Approach 2 — Score-keyed TreeMap of ID sets (the repo's version)

```kotlin
import java.util.TreeMap

class Leaderboard {
    // Maps StreamerID -> Score for O(1) lookup
    private val scores = mutableMapOf<String, Int>()

    // Maps Score -> Set of StreamerIDs (ordered by Score Descending)
    private val tree = TreeMap<Int, MutableSet<String>>(compareByDescending { it })

    fun updateScore(id: String, newScore: Int) {
        // 1. Remove the old score entry if it exists
        scores[id]?.let { oldScore ->
            tree[oldScore]?.let { streamers ->
                streamers.remove(id)
                if (streamers.isEmpty()) tree.remove(oldScore)
            }
        }

        // 2. Update the lookup map
        scores[id] = newScore

        // 3. Insert into the score tree
        tree.getOrPut(newScore) { mutableSetOf() }.add(id)
    }

    fun getStreamerAtRank(k: Int): String? {
        var countSoFar = 0

        // Iterate through scores (High -> Low)
        for ((score, streamers) in tree) {
            // Check if the k-th rank falls within this score bucket
            if (countSoFar + streamers.size >= k) {
                // k is 1-based: index within the bucket = k - count before - 1
                return streamers.elementAt(k - countSoFar - 1)
            }
            countSoFar += streamers.size
        }
        return null   // k is out of bounds
    }
}
```

```python
from sortedcontainers import SortedDict  # balanced-BST-backed ordered map

class Leaderboard:
    def __init__(self):
        self.scores = {}                        # id -> score
        self.tree = SortedDict()                # score -> set of ids (descending)

    def update_score(self, sid, new_score):
        if sid in self.scores:                  # remove old bucket entry
            old = self.scores[sid]
            self.tree[old].discard(sid)
            if not self.tree[old]:
                del self.tree[old]
        self.scores[sid] = new_score
        self.tree.setdefault(new_score, set()).add(sid)

    def get_streamer_at_rank(self, k):
        seen = 0
        for score in reversed(self.tree):       # high -> low
            bucket = self.tree[score]
            if seen + len(bucket) >= k:
                return sorted(bucket)[k - seen - 1]
            seen += len(bucket)
        return None
```

```java
import java.util.*;

class Leaderboard {
    private final Map<String, Integer> scores = new HashMap<>();
    private final TreeMap<Integer, Set<String>> tree =
            new TreeMap<>(Collections.reverseOrder());   // score -> ids, descending

    /**
     * @param id    streamer id
     * @param score new viewer count
     */
    public void updateScore(String id, int score) {
        Integer old = scores.get(id);
        if (old != null) {
            Set<String> bucket = tree.get(old);
            bucket.remove(id);
            if (bucket.isEmpty()) tree.remove(old);
        }
        scores.put(id, score);
        tree.computeIfAbsent(score, k -> new HashSet<>()).add(id);
    }

    /**
     * @param k 1-based rank
     * @return  streamer at that rank, or null if out of range
     */
    public String getStreamerAtRank(int k) {
        int seen = 0;
        for (Map.Entry<Integer, Set<String>> e : tree.entrySet()) {
            Set<String> bucket = e.getValue();
            if (seen + bucket.size() >= k) {
                for (String s : bucket) {
                    if (seen == k - 1) return s;
                    seen++;
                }
            }
            seen += bucket.size();
        }
        return null;
    }
}
```

## Reading the code — what's actually happening

1. **`scores[id]?.let { oldScore -> ... }` is the "move out of the old bucket" dance.** On an update, the streamer may already have a score. We look up the old bucket, remove the ID from it, and — the subtle part — **delete the bucket if it just became empty**. Otherwise dead empty-score buckets would accumulate forever and the rank walk would count them as phantom ranks.
2. **`tree.getOrPut(newScore) { mutableSetOf() }.add(id)`** inserts into the new bucket, creating it on demand. The TreeMap's descending comparator (`compareByDescending { it }`) makes iteration start at the highest score — the rank-1 end.
3. **`getStreamerAtRank` is a running-sum walk.** `countSoFar` accumulates the size of each bucket from the top. When the k-th slot falls inside the current bucket (`countSoFar + streamers.size >= k`), the winner is at `elementAt(k - countSoFar - 1)` — the k's 1-based-ness converted to a 0-based index within the bucket.
4. **Ties resolve by bucket iteration order** — the `Set`'s order (insertion order for `LinkedHashSet`, arbitrary for `HashSet`). If ties need a deterministic rule (e.g. lexicographic ID), swap the set for a sorted one — the structure stays the same.

**What's the O(log n) upgrade?** The walk in `getStreamerAtRank` is O(distinct scores) — fine for a demo, but at scale you'd want an **order-statistic tree** (a BST where every node tracks its subtree size) to answer "k-th by score" in O(log n), plus a `Map<id, node>` to find a streamer's node for `getRank(id)`. The repo's `OrderedStatisticsTree.kt` is exactly that structure — the "production" answer to this page's simplified design.

## Complexity

**Time.** `updateScore`: O(log S) for the TreeMap ops (S = distinct scores). `getStreamerAtRank`: O(S) with the walk, O(log n) with an order-statistic tree.

$$
T_{\text{update}} = O(\log S), \qquad T_{\text{rank}} = O(S) \text{ (or } O(\log n) \text{ with OST)}
$$

**Space.** One entry per streamer plus one per distinct score:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Design A Number Container System** ([10.31](../ch10-hash-tables/design-number-container-system.md)) — the same score-bucket pattern, but the query is "smallest index at a given value" instead of "k-th rank".
- **Find Median From Data Stream** ([7.2](../ch07-heaps/find-median-from-data-stream.md)) — two heaps for a *single* rank query; the TreeMap answer is the "many ranks" generalization.
- **Ordered Statistics Tree** (`tree/bst/OrderedStatisticsTree.kt`) — the order-statistic upgrade that turns the walk into O(log n); the natural "scale this design" follow-up.
- **Interview follow-up:** "Ties?" The bucket design handles them structurally — multiple IDs share a score bucket, and the tie-break rule is decided by the bucket's collection type. Say "I'd use insertion order or lexicographic ID order, configurable" — that's the design answer.
