# 19.2 LRU Cache — The Hand-Rolled Linked-List Family

> **Sources:** [`src/main/kotlin/cache/LRUCacheLinkedList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/cache/LRUCacheLinkedList.kt) · `LruCacheBruceLee.kt` · `LRUCacheBetter.kt` · `LRUCleanAf.kt` · `LruCacheFuckYeah.kt`
> **Pattern:** variant gallery — the interview-standard implementation [18.1](../ch18-design-caches/lru-cache.md) only summarizes

## The gap this page fills

[18.9](../ch18-design-caches/lru-cache-variants.md) named the five hand-rolled files but showed only a skeleton. This page **captures the real code** — because "implement LRU with a doubly-linked list you wrote yourself" is *the* most common LRU follow-up, and the repo's `LRUCacheLinkedList.kt` is the cleanest spelling of it.

## The star: `LRUCacheLinkedList.kt` (complete)

```kotlin
class LRUCacheLinkedList(private val capacity: Int) {
    private class Node(val key: Int, var value: Int) {
        var prev: Node? = null
        var next: Node? = null
    }

    private val cache = HashMap<Int, Node>() // Hash map for O(1) access
    private val head = Node(-1, -1)          // Dummy head
    private val tail = Node(-1, -1)          // Dummy tail

    init {
        head.next = tail
        tail.prev = head
    }

    fun get(key: Int): Int {
        val node = cache[key] ?: return -1          // key not found
        moveToHead(node)                            // touch = most recently used
        return node.value
    }

    fun put(key: Int, value: Int) {
        val node = cache[key]
        if (node != null) {
            node.value = value                      // update value
            moveToHead(node)                        // and refresh recency
        } else {
            val newNode = Node(key, value)          // new node to the head
            cache[key] = newNode
            addToHead(newNode)

            if (cache.size > capacity) {            // evict the LRU
                val tailNode = removeTail()
                cache.remove(tailNode.key)
            }
        }
    }

    private fun addToHead(node: Node) {
        node.prev = head
        node.next = head.next
        head.next?.prev = node
        head.next = node
    }

    private fun removeNode(node: Node) {
        node.prev?.next = node.next
        node.next?.prev = node.prev
    }

    private fun moveToHead(node: Node) {
        removeNode(node)
        addToHead(node)
    }

    private fun removeTail(): Node {
        val tailNode = tail.prev!!                  // the dummy tail's prev is the LRU
        removeNode(tailNode)
        return tailNode
    }
}
```

**What makes it the canonical spelling:**

- **Dummy `head`/`tail` nodes** — `addToHead` and `removeTail` never touch null: `head.next` always exists, `tail.prev` always exists. The classic null-pointer-free linked-list discipline.
- **`get` = lookup + `moveToHead`** — the whole "recency" semantics in two lines; the HashMap gives the O(1) find, the list gives the O(1) touch.
- **`put` = the three cases** — update-in-place, insert-new, evict-when-full. The `cache.size > capacity` check (instead of `==`) is belt-and-suspenders safe.
- **`removeTail` returns the node** — so `cache.remove(tailNode.key)` has the key at hand; the dummy-tail design makes "find the LRU" a single field read.

## The siblings

**`LruCacheBruceLee.kt`** — generic `<K, V>` with *nullable* key/value on the dummy nodes; same shape, `moveToHead`/`removeTail` methods, plus a `require(capacity > 0)` guard. The differences are cosmetic — this is the same list machine.

**`LRUCacheBetter.kt`** — `LRUCacheLL<K, V>`: the same dummy-head/tail list with a `size` counter instead of `cache.size` checks.

**`LRUCleanAf.kt`** — the *no-dummy* variant: nullable `head`/`tail` fields. `addFirst` must handle the empty-list case explicitly:

```kotlin
private fun addFirst(node: CacheNode<Key, Val>) {
    node.next = head
    node.prev = null
    if (head != null) head!!.prev = node
    head = node
    if (tail == null) tail = head
}
```

This is the *contrast* implementation: it shows why the dummies exist. Every null check in `LRUCleanAf` is a case the dummy design eliminates. If an interviewer asks "how would you handle the empty list?" — this file is the answer.

**`LruCacheFuckYeah.kt`** — `LRUCacheFuckYeah<K, V>`: dummy nodes with `null as K` casts, `get = cache[key]?.also { moveToHead(it) }?.value` (the elvis-chain style), same list ops. The "fuck yeah" files in this repo are the "I've done this before, watch me" versions — same algorithm, maximal Kotlin idiom.

## The five files, one decision tree

```
"Which LRU would you ship?"
├─ LinkedHashMap available (JVM/Python)  -> LRUCache.kt / LruCacheNobodyDoesItBetter.kt  [18.1/18.9]
└─ Must hand-roll the list:
   ├─ Generic, any K/V                   -> LRUCacheBetter.kt / LruCacheFuckYeah.kt
   ├─ Dummy head/tail (recommended)      -> LRUCacheLinkedList.kt / LruCacheBruceLee.kt
   └─ No dummies (edge-case drill)       -> LRUCleanAf.kt
```

## Dry run

**Input:** `capacity = 2`; `put(1,1), put(2,2), get(1), put(3,3), get(2)`.

```
put(1,1): newNode 1.  list: head->1->tail.  cache={1}
put(2,2): newNode 2.  list: head->2->1->tail.  cache={1,2}
get(1):   cache[1] found.  moveToHead(1): remove 1, add 1 -> list: head->1->2->tail.  return 1
put(3,3): new.  list: head->3->1->2->tail.  size 3 > 2 -> removeTail() = 2.  cache={1,3}.  list: head->3->1->tail
get(2):   cache[2] == null -> -1 ✓
```

The `removeTail` after `put(3,3)` evicts 2 — it was the LRU because `get(1)` moved 1 to the front. The dummies keep every list op null-safe; the HashMap keeps every op O(1).

## Complexity

**Time.** Every operation is O(1) — HashMap lookup + constant list surgery:

$$
T = O(1) \text{ per operation}
$$

**Space.** One node per cached key:

$$
S = O(capacity)
$$

## Variants & follow-ups

- **LRU Cache** ([18.1](../ch18-design-caches/lru-cache.md)) / **LRU variants** ([18.9](../ch18-design-caches/lru-cache-variants.md)) — the LinkedHashMap family and the family map.
- **LFU Cache** ([18.2](../ch18-design-caches/lfu-cache.md)) — frequency buckets instead of a single list; the repo's `LFUCacheGigaCHAD.kt` and `LfuCacheNobodyDoesItBetter.kt` are its gallery entries.
- **Interview follow-up:** "Why the dummy head/tail?" With dummies, `addToHead`'s `head.next?.prev = node` never dereferences null and `removeTail` always has a victim — the list is *never empty* from the operations' perspective. That's the entire class of "empty-list" bugs eliminated before they exist.
