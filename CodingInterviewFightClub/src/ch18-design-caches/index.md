# Chapter 18 — Design & Caches

> **Source:** `src/main/kotlin/cache/`, `src/main/kotlin/design/`, and the `stack/` + `probability/` design files
>
> **Master idea:** design questions test *data-structure composition*: which structures combine to meet the stated complexity? The classic answers: **hash map + linked list** (LRU), **three maps + a min-counter** (LFU), **hash map + array swap-remove** (O(1) random access), and **stateful iterators** (buffers and stacks that make traversal lazy).
>
> **Prerequisites:** hash maps ([Chapter 10](../ch10-hash-tables/index.md)), linked lists ([Chapter 4](../ch04-linked-lists/index.md)), heaps/queues ([Chapter 7](../ch07-heaps/index.md)), and stacks ([Chapter 8](../ch08-stacks/index.md)) — every design here composes those.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 18.1 | LRU Cache | LinkedHashMap, access-order | O(1) per op | [→](lru-cache.md) |
| 18.2 | LFU Cache | 3 maps + min-frequency | O(1) per op | [→](lfu-cache.md) |
| 18.3 | Thread-Safe Sharded LRU | sharding + locks | O(1) amortized | [→](thread-safe-lru-cache.md) |
| 18.4 | Peeking Iterator | one-element buffer | O(1) per op | [→](peeking-iterator.md) |
| 18.5 | Flatten Nested List Iterator | stack of nested lists | O(1) amortized | [→](flatten-nested-list-iterator.md) |
| 18.6 | Design A Stack With Increment Operations | lazy increment array | O(1) per op | [→](design-a-stack-with-increment-operations.md) |
| 18.7 | Insert Delete GetRandom O(1) | map + list swap-remove | O(1) per op | [→](insert-delete-getrandom.md) |

| 18.8 | Weighted Reservoir Sampling | A-Res randomized keys | $O(N log k)$ | [→](weighted-reservoir-sampling.md) |
| 18.9 | LRU Cache — The Repo's Seven Implementations | variant consolidation | $O(1)$ | [→](lru-cache-variants.md) |
| 18.11 | My Calendar | TreeMap floor/ceiling | $O(log n)$ | [→](my-calendar.md) |
| 18.12 | BST Iterator | left-spine stack | $O(1)$ amortized | [→](bst-iterator.md) |
| 18.13 | Moving Average | windowed queue + sum | $O(1)$ | [→](moving-average.md) |
| 18.14 | Number Of Recent Calls | expiry queue | $O(1)$ amortized | [→](number-of-recent-calls.md) |
| 18.15 | Product Of Last K Numbers | prefix products + zero-reset | $O(1)$ | [→](product-of-last-k-numbers.md) |
| 18.16 | Design Circular Queue | ring buffer | $O(1)$ | [→](design-circular-queue.md) |
| 18.17 | Maximum Frequency Stack | frequency stacks | $O(1)$ | [→](maximum-frequency-stack.md) |
| 18.18 | Range Sum Query 2D Immutable | 2-D prefix sums | $O(1)$ query | [→](range-sum-query-2d-immutable.md) |
| 18.19 | Convert BST To DLL | inorder threading | $O(n)$ | [→](convert-bst-to-sorted-doubly-linked-list.md) |
| 18.20 | Design TicTacToe | signed line counters | $O(1)$ | [→](design-tic-tac-toe.md) |
## The rest of the design-related directories

`cache/` holds many LRU/LFU flavors (`LRUCacheLinkedList.kt`, `LRUCacheBetter.kt`, the `LruCacheNobodyDoesItBetter.kt` family, `LFUCacheGigaCHAD.kt`, ...) — this chapter documents the canonical structures. `design/` adds `SelfDoubtSimulation.kt`; `stack/` holds the nested-list iterator and increment-stack; `probability/` holds the O(1) random-access set.

New pages are appended to the table above as they're written.
