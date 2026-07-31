# 3.0 Pattern Primer — Two Pointers & The Sorted-Array Dance

The single most repeated trick in array interviews is the **two-pointer scan**: two indices, one invariant, one loop, $O(n)$. This page names the four canonical dances; every two-pointer problem in this book is one of them in costume.

## Dance 1 — Converge (opposite ends)

Sorted array, find a pair with a given property. Start `left = 0`, `right = n-1`. The sum `s = arr[left] + arr[right]` is *monotone in the pointers*: moving `left` right increases `s` (values are sorted), moving `right` left decreases it. So:

```
if s == target: found
if s < target:  left++      (need a bigger sum)
if s > target:  right--     (need a smaller sum)
```

Each step discards **one whole pointer line** — if `s < target`, no pair `(left, anything < right)` can work, so the entire row of `left` dies. That's why the loop is $O(n)$ and not $O(n^2)$. Examples: [3.1](two-sum-ii.md), [3.2](three-sum.md), Trapping Rain Water, Container With Most Water.

## Dance 2 — Partition (write pointer)

Move all elements satisfying a predicate to the front, in place. Keep a `write` pointer; scan with `read`:

```
for read in 0..n-1:
    if pred(arr[read]):
        arr[write] = arr[read]; write++
```

One pass, $O(n)$, stable order of the kept elements preserved. Examples: [3.3](move-zeroes.md), Remove Duplicates From Sorted Array, the *first* phase of quicksort's Lomuto partition.

## Dance 3 — Sliding window (same direction)

A window `[lo, hi)` that only ever moves forward. Extend `hi` to include elements, shrink `lo` when the window becomes invalid. Total work per element is $O(1)$ (each index enters and leaves once), so a "nested-looking" loop is actually $O(n)$. That's the whole trick of sliding-window problems (see the `sliding_window/` directory and Chapter 13).

## Dance 4 — Sweep (sorted events)

For interval problems, sort by one endpoint and scan once, merging/deciding as you go. The sorted order makes "what's the current state?" answerable with a single variable. Examples: [3.4](merge-intervals.md), [3.5](insert-interval.md), the Skyline problem (`tree/bst/SkylineProblem.kt`).

## The two questions to ask before dancing

1. **Is the array sorted?** If yes, convergence pointers are available. If not, sorting it first is often the right *preprocessing* (as in [3.2](three-sum.md): $O(n \log n)$ sort, then $O(n)$ per pivot).
2. **Can the answer be found by a monotone scan?** If a single forward pass with a moving boundary suffices (Dance 2/3), there's no need for binary search or heaps.

## Complexity intuition (the sum that saves you)

Dance 1's cost argument is the *same* geometric-series idea from [Reference §3](../reference/complexity.md): at each step exactly one of the two pointers moves, the two pointers never cross, so at most $n$ steps total — regardless of the fact that the loop looks like it could explore $n^2$ pairs. **The loop is linear because every discarded candidate is discarded forever.**
