# 19.9 LFU Cache — The Three Implementations, Captured

> **Sources:** [`src/main/kotlin/cache/LFUCache.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/cache/LFUCache.kt) · `LFUCacheGigaCHAD.kt` · `LfuCacheNobodyDoesItBetter.kt`
> **Pattern:** variant gallery — frequency buckets vs counting maps ([18.2](../ch18-design-caches/lfu-cache.md) documents the canonical bucket version)

## The family map

| File | Core structure | Eviction |
|---|---|---|
| `LFUCache.kt` | `freq` map + `lists` of buckets + `vals` map | min-frequency bucket's LRU node |
| `LfuCacheNobodyDoesItBetter.kt` | counting maps + frequency counter | `minFreq` lookup + per-freq queue |
| `LFUCacheGigaCHAD.kt` | the same idea, named aggressively | — |

## The canonical: `LFUCache.kt` (what [18.2](../ch18-design-caches/lfu-cache.md) documents)

The three-map design: `vals[key]` for values, `freq[key]` for frequencies, `lists[f]` for the LRU-ordered members of each frequency:

```kotlin
// sketch of the canonical three-map LFU (LFUCache.kt)
// vals: HashMap<Int, Int>          — key -> value
// freq: HashMap<Int, Int>          — key -> frequency
// lists: HashMap<Int, LinkedHashSet<Int>> — frequency -> keys (insertion order = LRU within freq)
// minFreq: Int                     — the eviction target
//
// get: if key absent -> -1; else freq[key]++, move key between buckets, return value
// put: if key present -> update value + frequency; if full -> evict lists[minFreq].first()
```

The `LinkedHashSet` per frequency is the "LRU within LFU" tiebreak: the set's insertion order is recency, so eviction = `first()` of the min bucket. [18.2](../ch18-design-caches/lfu-cache.md) walks this in full.

## The leaner: `LfuCacheNobodyDoesItBetter.kt`

Same semantics with a `Counter`-style map and a `minFreq` that's recomputed lazily — fewer structures, more map arithmetic:

```kotlin
// sketch of the leaner shape (LfuCacheNobodyDoesItBetter.kt)
// keyToFreq: HashMap<Int, Int>
// freqToKeys: HashMap<Int, LinkedHashSet<Int>>
// minFreq tracking via "if the min bucket emptied, minFreq++"
// eviction: freqToKeys[minFreq]!!.first() — the least-recently-used key at the lowest frequency
```

**What's cool:** it drops the `vals` map (the value rides in a `Pair` or a node) and keeps only the frequency bookkeeping — the minimal structure that still satisfies "evict the least frequent, tie-break by recency."

## The bravado: `LFUCacheGigaCHAD.kt`

Same three-map skeleton, maximal idiom — `getOrPut`, `withDefault`, chained elvis. The "GigaCHAD" files in this repo are the "I've internalized this" versions: same algorithm, zero ceremony. Reading `LFUCache.kt` (teaching) → `LfuCacheNobodyDoesItBetter.kt` (lean) → `LFUCacheGigaCHAD.kt` (idiom) is the three-stage mastery progression.

## The one invariant all three share

> **`minFreq` is only meaningful if every bucket's recency is maintained** — the eviction picks `freqToKeys[minFreq].first()`, so the *first* key inserted into a frequency bucket must be the one to evict. The `LinkedHashSet` (or a queue) is what guarantees that; a plain `HashSet` would make eviction arbitrary and break the LFU contract.

That invariant is the page's whole correctness story: frequency decides the *bucket*, insertion order decides the *victim*.

## Dry run

**Input:** `capacity = 2`; `put(1,1), put(2,2), get(1), put(3,3), get(2), get(3), put(4,4)`.

```
put(1,1): freq 1 bucket: [1].  minFreq=1.
put(2,2): freq 1 bucket: [1,2].  minFreq=1.
get(1):   1 -> freq 2 bucket: [1].  freq 1 bucket: [2].  return 1.
put(3,3): full -> evict freq 1 first = 2.  insert 3 at freq 1: [3].  minFreq=1.
get(2):   -1 ✓  (2 was the least-frequent AND least-recently-used)
get(3):   3 -> freq 2: [3].  freq 1 bucket empty -> minFreq=2.
put(4,4): full -> evict freq 2 first = 1 (1 is at freq 2, 3 is at freq 2 [3]... eviction = 1).  insert 4.
```

The eviction ladder: `2` goes first (freq 1, least recent), then `1` (freq 2, but *older* in its bucket than 3). The `minFreq` re-baselines when a bucket empties — the subtle bookkeeping all three files implement, just with different amounts of sugar.

## Complexity

**Time.** All ops O(1) amortized (map lookups + set moves):

$$
T = O(1) \text{ per operation}
$$

**Space.** One entry per cached key across the maps:

$$
S = O(capacity)
$$

## Variants & follow-ups

- **LFU Cache** ([18.2](../ch18-design-caches/lfu-cache.md)) — the canonical walkthrough.
- **LRU families** ([18.9](../ch18-design-caches/lru-cache-variants.md), [19.2](lru-cache-linked-list-family.md)) — recency-only cousins; LFU adds the frequency dimension.
- **Interview follow-up:** "Why can't a single priority queue do LFU?" A PQ can pop the min frequency in O(log n), but *updating* a key's frequency requires a delete-key on the heap (O(n) without index tracking). The bucket maps make frequency moves O(1) — the classic "lazy vs eager" data-structure tradeoff, and the reason LFU's canonical form is three maps, not one heap.
