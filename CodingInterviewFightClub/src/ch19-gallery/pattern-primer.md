# 19.0 Pattern Primer: Same Algorithm, Different Costumes

> A chapter about *the same solution written many ways*.

## The observation that started this chapter

The Kotlin repo contains **~150 duplicate implementations**: seven LRU caches, four coin changes, four N-Queens, three Word Ladder IIs, three Eulerian-path problems, two sliding-window medians. From a "shipping code" view that's redundancy. From an *interview-prep* view it's a gift — each duplicate is a different **costume** of the same algorithm, and the costume changes what you can *say* about the problem.

## The three axes a variant can optimize

**1. Direction** — top-down vs bottom-up (TSP: [19.1](travelling-salesman-top-down.md)), forward vs backward BFS (Word Ladder II: [19.8](word-ladder-ii-family.md)). Same recurrence, opposite fills.

**2. State representation** — bitmask vs `BooleanArray` vs `Set<String>` (N-Queens: [19.4](n-queen-family.md)), indices-with-comparator vs values-with-counts (median: [19.10](sliding-window-median-treeset.md)). The representation *is* the correctness story — duplicates are the usual casualty.

**3. Ceremony level** — teaching → lean → idiomatic (LFU: [19.9](lfu-cache-family.md), short-code: [19.5](short-code-gallery.md)). The algorithm is ~15 essential lines; everything else is scaffolding. Reading the trio teaches you which is which.

## How to read a variant

1. **Find the invariant** the main page teaches (e.g., "evict the min-frequency bucket's first key").
2. **Ask what the variant changed** — direction, representation, or ceremony.
3. **Ask what that change costs** — TreeSet's exact removal buys duplicate-proofness at O(log k) vs the heap's O(1)-with-bookkeeping ([19.10](sliding-window-median-treeset.md)); the top-down TSP buys readability at the same $O(n^2 2^n)$ ([19.1](travelling-salesman-top-down.md)).

That three-step is also the interview answer to "can you do it differently?" — name the axis, show the change, state the tradeoff.
