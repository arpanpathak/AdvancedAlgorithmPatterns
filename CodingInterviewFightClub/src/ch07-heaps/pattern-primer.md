# 7.0 Pattern Primer — The Lazy Sorted Structure

A **heap** is a binary tree stored in an array with one invariant: every node is ≤ (min-heap) or ≥ (max-heap) its children. That tiny rule gives two superpowers:

- `peek` — the extreme element, in $O(1)$;
- `push` / `pop` — insert or remove the extreme, in $O(\log n)$;

…with **no sorting cost elsewhere**. That's the whole pitch: *"sort, but only enough to always hand you the extreme."* When a problem keeps asking "which one is smallest/largest **right now**?", a heap is the data structure that was born to answer.

## The three moves

Almost every heap interview problem is one of these:

**1. Keep the top k.** Hold a *min-heap of size k* — for each element, push it, and if the heap exceeds size k, pop the smallest. What remains is the k *largest*; the heap root is the k-th largest. The trick is *inverting* the heap: a min-heap keeps the largest k because it evicts the smallest. Used by [7.1](top-k-frequent-elements.md).

**2. Split into two halves.** Keep a *max-heap* for the lower half and a *min-heap* for the upper half, always balanced to ±1 element. The two roots *are* the median(s). Every `add` is $O(\log n)$ — the median is never recomputed. Used by [7.2](find-median-from-data-stream.md) and [7.3](sliding-window-median.md).

**3. Process in priority order.** A scheduler/expander that always takes "the next most urgent thing": the CPU picks the shortest ready task ([7.7](single-threaded-cpu.md)), the rain-water boundary always floods from its lowest wall ([7.4](trapping-rain-water-ii.md)), the greedy IPO always takes the most profitable *affordable* project ([7.5](ipo.md)). The heap is the "always take the best available" loop made $O(\log n)$.

## The two data-structure reflexes

- **Priority queues are just heaps.** In Kotlin/Java, `PriorityQueue` *is* a min-heap by default. To get a max-heap, negate the comparator (`compareBy { -it }`) — the repo does exactly this in [7.2](find-median-from-data-stream.md).
- **"Remove an arbitrary element" is the enemy.** Heaps only pop the extreme efficiently. When a window slides and an *interior* element must leave ([7.3](sliding-window-median.md)), deleting it costs $O(n)$. The fix is **lazy deletion**: mark it dead (a `TreeMap` counter or a `HashMap`), and only physically remove it when it surfaces at the root. The heap stays correct; the dead entries just cost a little extra memory until popped.

## Complexity intuition

A heap of size $n$ costs $O(\log n)$ per push/pop and $O(n)$ to build (heapify). So:

- "keep the top k" over $n$ items: $O(n \log k)$ — better than sorting ($O(n \log n)$) whenever $k \ll n$;
- "two heaps" medians: $O(\log n)$ per operation — unbeatable for streams, since sorting each time is $O(n \log n)$ per query;
- "priority processing" loops: each iteration pops once and pushes $O(1)$ times, so $O((\text{iterations}) \log n)$ total.

The recurring interview question is *"why not just sort?"* — and the answer is usually "because the data changes, and a heap changes with it in $O(\log n)$ instead of $O(n \log n)$."
