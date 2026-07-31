# 18.9 LRU Cache — The Repo's Seven Implementations

> **Sources:** [`src/main/kotlin/cache/`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/tree/main/src/main/kotlin/cache) — **seven** LRU implementations in one directory
> **Pattern:** variant consolidation page · **Core page**

## The Problem

[18.1](lru-cache.md) solved LRU with the classic `LinkedHashMap`. But the repo ships **seven** takes on the same cache. This page consolidates them — the duplicates are the syllabus: each one optimizes a different axis, and interviewers love asking "which would you ship?"

| File | Core idea | Space | Notes |
|---|---|---|---|
| `LRUCache.kt` | `LinkedHashMap` + remove-reinsert | O(n) | The [18.1](lru-cache.md) baseline; `get` re-`put`s to refresh recency |
| `LruCacheNobodyDoesItBetter.kt` | `LinkedHashMap` one-liners | O(n) | `get = cache.remove(key)?.also { cache[key] = it } ?: -1` — remove-reinsert in a single expression |
| `LRUCacheLinkedList.kt` | HashMap + real doubly-linked list | O(n) | Dummy head/tail; the textbook implementation |
| `LruCacheBruceLee.kt` | HashMap + doubly-linked list | O(n) | Same shape, `Node<K, V>` with nullables, `moveToHead`/`removeTail` |
| `LRUCacheBetter.kt` | Generic `LRUCacheLL<K, V>` | O(n) | The linked-list version generalized over key/value types |
| `LRUCleanAf.kt` | HashMap + head/tail **without dummies** | O(n) | Nullable head/tail; `addFirst` must handle the empty-list case |
| `LruCacheFuckYeah.kt` | HashMap + doubly-linked list | O(n) | The same with `require(capacity > 0)` hardening |

**The two families:**

**Family A — `LinkedHashMap` (2 files).** Access-order insertion *is* the recency list: `get` re-inserts to refresh, `put` removes-then-adds, eviction is `remove(keys.first())`. ~10 lines, O(1) amortized, perfect for production when the platform has `LinkedHashMap` (Kotlin/JVM, Java, Python's `OrderedDict`).

```kotlin
// LruCacheNobodyDoesItBetter.kt — the whole cache in two methods
class LruCacheNobodyDoesItBetter(private val capacity: Int) {
    private val cache = LinkedHashMap<Int, Int>(capacity)

    fun get(key: Int): Int = cache.remove(key)?.also {
        cache[key] = it                       // re-insert = mark Most Recently Used
    } ?: -1

    fun put(key: Int, value: Int) {
        when {
            cache.containsKey(key) -> cache.remove(key)
            cache.size == capacity -> cache.remove(cache.keys.first())   // evict LRU
        }
        cache[key] = value
    }
}
```

**Family B — HashMap + hand-rolled doubly-linked list (5 files).** The [18.1](lru-cache.md) interview answer when you must implement the list yourself: `HashMap<key, Node>` for O(1) lookup, a linked list for recency order, dummy head/tail so `addFirst`/`removeTail` never special-case. The five files differ only in nullability style and genericity.

```kotlin
// LRUCacheLinkedList.kt / LruCacheBruceLee.kt — the textbook shape
private class Node<K, V>(
    var key: K? = null,
    var value: V? = null,
    var prev: Node<K, V>? = null,
    var next: Node<K, V>? = null
)

private val cache = HashMap<K, Node<K, V>>()   // key -> node: O(1) lookup
private val head = Node<K, V>()                // dummy head: next is the MRU
private val tail = Node<K, V>()                // dummy tail: prev is the LRU

fun get(key: K): V? = cache[key]?.let {
    moveToHead(it)                             // touch = move to front
    it.value
}

fun put(key: K, value: V) {
    cache[key]?.let {
        it.value = value                       // update value
        moveToHead(it)                         // and refresh recency
        return
    }
    if (cache.size >= capacity) {
        val lru = removeTail()                 // evict the dummy tail's prev
        lru?.key?.let { cache.remove(it) }
    }
    val node = Node(key, value)
    cache[key] = node
    addToHead(node)
}
```

**Why so many duplicates?** Each file was a *different practice attempt* at the same problem — which is exactly the "duplicate files" the user asked about. The interview lesson: the algorithm is one idea (hash for lookup + list for order); the files are seven dialects of it. Knowing *why* they're all the same answer is worth more than memorizing any one.

## Approach 1 — LinkedHashMap (the repo's 2-file family)

Remove-reinsert on access, remove-first on eviction. Production-ready, minimal code.

## Approach 2 — Hand-rolled list (the repo's 5-file family)

HashMap + doubly-linked list with dummies. The interview-standard implementation when you can't rely on `LinkedHashMap`.

## The consolidation (one skeleton for all seven)

```kotlin
class LRU<K, V>(private val capacity: Int) {
    private class Node(val key: K, var value: V) {
        var prev: Node? = null
        var next: Node? = null
    }

    private val cache = HashMap<K, Node>()
    private val head = Node(null as K, null as V)   // dummy
    private val tail = Node(null as K, null as V)   // dummy

    init {
        head.next = tail
        tail.prev = head
    }

    fun get(key: K): V? = cache[key]?.also { moveToHead(it) }?.value

    fun put(key: K, value: V) {
        cache[key]?.let { it.value = value; moveToHead(it); return }
        if (cache.size == capacity) removeTail()?.key?.let { cache.remove(it) }
        Node(key, value).also { cache[key] = it; addToHead(it) }
    }

    private fun addToHead(node: Node) {
        node.next = head.next
        node.prev = head
        head.next?.prev = node
        head.next = node
    }

    private fun removeNode(node: Node) {
        node.prev?.next = node.next
        node.next?.prev = node.prev
    }

    private fun moveToHead(node: Node) { removeNode(node); addToHead(node) }

    private fun removeTail(): Node? = tail.prev?.takeIf { it != head }?.also { removeNode(it) }
}
```

## Dry run

**Input:** `capacity = 2`; ops `put(1,1), put(2,2), get(1), put(3,3), get(2)`.

```
put(1,1): cache={1}.  list: 1 (MRU).
put(2,2): cache={1,2}.  list: 2 -> 1.
get(1):   value 1.  moveToHead -> list: 1 -> 2.   (1 is now MRU)
put(3,3): size == 2 -> evict removeTail() = 2.  cache={1,3}.  list: 3 -> 1.
get(2):   not in cache -> -1 ✓   (2 was evicted: it was LRU after the get(1) touch)
```

The eviction is the point: `get(1)` made 1 the MRU, so 2 became the LRU and the `put(3,3)` evicts it. All seven repo files produce this exact behavior — the duplicates are dialects, not different semantics.

## Complexity

**Time.** Every op O(1) amortized:

$$
T = O(1) \text{ per operation}
$$

**Space.** The cache:

$$
S = O(capacity)
$$

## Variants & follow-ups

- **LRU Cache** ([18.1](lru-cache.md)) — the full page with the LinkedHashMap walkthrough.
- **LFU Cache** ([18.2](lfu-cache.md)) — frequency instead of recency: the repo's `LFUCache.kt`, `LFUCacheGigaCHAD.kt`, `LfuCacheNobodyDoesItBetter.kt` are the same "many dialects, one idea" story for LFU.
- **Thread-Safe Sharded LRU** ([18.3](thread-safe-lru-cache.md)) — the production upgrade: sharding + `ReentrantLock`.
- **Interview follow-up:** "Which of the seven would you ship?" `LinkedHashMap` (or Python's `OrderedDict`) — shorter, less bug surface, O(1). The hand-rolled list is for when the language lacks one or the interviewer wants the data-structure drill. Never mention all seven by name; say "the repo practiced it seven times — same algorithm, different sugar."
