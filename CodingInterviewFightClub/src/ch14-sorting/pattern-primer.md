# 14.0 Pattern Primer — Sort as Preprocessing

**Sorting is never the goal** — it's the preprocessing that makes a problem's structure visible. After sorting, three things become cheap:

1. **Adjacency** — "next in order" is `arr[i+1]` (the interval family from [11.3](../ch11-greedy/meeting-rooms.md), H-Index).
2. **Extremes** — min/max are at the ends (kth-largest, top-k).
3. **Ordered sequences** — LIS-style structure emerges on a sorted axis (Russian Doll Envelopes).

And once you've sorted, the follow-up scan is usually $O(n)$ — so the sort *is* the complexity. Comparison sorts cost $O(n \log n)$; that's the floor for any "reorder by a rule" problem.

## The two engines of this chapter

**Merge sort** ([14.1](merge-sort.md)) — the *divide-and-conquer* workhorse: split, sort each half, merge two sorted halves. It's the template for "inversion count", "merge k sorted", and external sorts, and its recursion tree is the proof that comparison sorting can't beat $O(n \log n)$.

**Quickselect** ([14.2](kth-largest-element.md), [14.3](k-closest-points-to-origin.md), [14.7](top-k-frequent-elements-quickselect.md)) — quicksort's partition *without* sorting the whole array:

> Partition around a random pivot; if the pivot lands on the k-th position, you're done; else recurse (or loop) into only the side that contains k.

Average $O(n)$ — better than sorting ($O(n \log n)$) whenever you need "the k-th something", not the full order. The heap answer ([Chapter 7](../ch07-heaps/index.md)) is $O(n \log k)$; quickselect is $O(n)$ average but $O(n^2)$ worst. The interview contrast: *heap = guaranteed worst case, quickselect = better average, no extra space.*

## Custom comparators

The "sorted" of a problem is often *not* natural order. [Largest Number](largest-number.md) sorts by `"ab" vs "ba"` string concatenation; [Russian Doll Envelopes](russian-doll-envelopes.md) sorts by width *ascending* and height *descending* — a deliberate comparator that turns the 2-D nesting problem into 1-D LIS. The reflex: **ask "what order makes the answer visible?", then write the comparator that produces it.** The comparator is the problem statement translated into a boolean.

## Complexity intuition

- Comparison sorts: $\Theta(n \log n)$ — and that's a *lower bound*, not a choice.
- Quickselect: $O(n)$ average, $O(n^2)$ worst (bad pivots); the randomization is what makes the average hold.
- Post-sort scans: $O(n)$ — never the bottleneck after the sort.
- Counting/bucket sorts: $O(n + \text{range})$ — when the *values* are bounded (the H-Index counting variant), they beat comparison sorts. Mentioning the non-comparison alternative is the depth signal.
