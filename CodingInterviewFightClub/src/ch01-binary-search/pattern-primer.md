# 1.0 Pattern Primer — The Binary Search Theorem

Every problem in this chapter is a **search problem**, and search problems are solved by one of two weapons:

1. **Brute force:** look at every candidate. Cost is proportional to the *size of the candidate space*.
2. **Binary search:** exploit **structure** in the candidate space to discard *half* of it at each step. Cost is proportional to the *logarithm* of the size of the candidate space.

The structure we need is **monotonicity**. This page gives you the exact theorem, the two implementation templates used everywhere in this chapter, and the two traps that kill 90% of implementations.

## The theorem

> **Binary Search Theorem.** Let $P(x)$ be a predicate defined on an ordered domain $D$ (an array index range, an integer range $[L, R]$, whatever). If $P$ is *monotone* — i.e. $P(x) \Rightarrow P(x')$ for all $x' \ge x$ (once true, always true) — then there is a unique **boundary** $b^* = \min\{x \in D : P(x)\}$ and it can be found by halving in $O(\log |D|)$ predicate evaluations.

Equivalently, the domain splits into a "false prefix" followed by a "true suffix":

```
P(x):   F F F F F T T T T T T
              ^
              b* = first true
```

The *mirror* form (once true, always **false**, i.e. $P$ is anti-monotone) works identically — you just binary search for the **last true** instead of the first true. Both appear in this chapter.

**Why halving finds it:** binary search maintains an invariant "left is false, right is true" (or "left is outside, right is inside"). Each step tests the midpoint. If the midpoint is false, the boundary must lie strictly to its right, so we move `left` past it; if true, the boundary is at or to its left, so we move `right` onto it. Either way the interval $[left, right]$ **halves**, and by the identity $2^k \ge n \iff k \ge \log_2 n$, after $\lceil \log_2 n \rceil$ steps the interval collapses to the single point $b^*$. See [Reference §2](../reference/complexity.md) for the halving math.

## Template A — the "first true" search (lower bound)

The workhorse of this chapter. All of Koko, Capacity, First Bad Version, House Robber IV, Search Insert Position, and the rotated-array problems are this template wearing different costumes:

```kotlin
// Find the smallest x in [lo, hi) such that predicate(x) is true.
// Invariant: predicate(lo) == false (or lo is "too small"), predicate(hi) == true (or hi is "big enough").
fun lowerBound(lo: Int, hi: Int, predicate: (Int) -> Boolean): Int {
    var left = lo
    var right = hi            // right is EXCLUSIVE: candidate space is [left, right)
    while (left < right) {    // stop when the interval has exactly one element
        val mid = left + (right - left) / 2   // overflow-safe midpoint
        if (predicate(mid)) right = mid       // mid is true  -> boundary is at or left of mid
        else                left = mid + 1    // mid is false -> boundary is strictly right of mid
    }
    return left               // left == right == b*
}
```

Notes on the details — each one is load-bearing:

- **`right = hi` (exclusive).** This guarantees `right` always points at a *candidate* for the answer, and the loop condition `left < right` never drops the answer. If you used `right = hi - 1` you'd need `left <= right` and a completely different set of edge cases.
- **`left = mid + 1` vs `right = mid`.** This asymmetry is what makes the search *progress*: `mid` is never re-tested as false, so the interval strictly shrinks. If you wrote `left = mid` and `mid` stays false forever (e.g. `left = mid = left` when `right - left == 1`), you'd infinite-loop.
- **Overflow-safe midpoint.** `(left + right) / 2` can overflow for huge ranges (this exact bug lived in Java's standard library for **20 years** — see Chapter 1 intro). Always write `left + (right - left) / 2`.

## Template B — the exact-match search

When you're looking for an *exact* value (not a boundary), you use the classic three-way comparison:

```kotlin
fun exactMatch(arr: IntArray, target: Int): Int {
    var left = 0
    var right = arr.lastIndex          // INCLUSIVE here
    while (left <= right) {
        val mid = left + (right - left) / 2
        when {
            arr[mid] == target -> return mid
            arr[mid] < target  -> left = mid + 1
            else               -> right = mid - 1
        }
    }
    return -1                          // not found
}
```

Template B terminates because every branch either returns or shrinks the interval. The price of "inclusive" bounds is that `left == right` still needs a test — hence `<=`.

## When do you "binary search on the answer"?

Classic binary search searches an *array*. But 1.1, 1.2, 1.10 search a **range of integer answers** $[L, R]$ instead — the "array" is conceptual. The trigger is:

1. The answer is a number with a natural range (speed $[1, \max piles]$, capacity $[\max weight, \sum weights]$, capability $[\min, \max]$).
2. Feasibility is *monotone* in the answer: if speed $k$ works, speed $k+1$ works.
3. A cheap feasibility check $P(x)$ exists.

Then the answer is $\min\{x : P(x)\}$ — exactly Template A. The cost is $O(f \cdot \log(R - L))$ where $f$ is the cost of one feasibility check (see [Reference §6](../reference/complexity.md)).

## The five moves of this chapter

| Move | Looks like | Sections |
|---|---|---|
| Search over an answer range | "minimize X such that feasible(X)" | 1.1, 1.2, 1.10 |
| Find the first true / lower bound | "leftmost occurrence, insert position, first bad version" | 1.3, 1.8, 1.11, 1.18 |
| Search a rotated array | "sorted but rotated at a pivot" | 1.5, 1.16, 1.17 |
| Follow the slope | "peak / valley in an array" | 1.6, 1.7, 1.13, 1.20 |
| Search a derived structure | "prefix sums, partitions, index unrolling" | 1.4, 1.12, 1.14, 1.15, 1.21, 1.22 |

Every remaining page in this chapter is one of these five moves, stated in a costume. Learn the moves, not the costumes.
