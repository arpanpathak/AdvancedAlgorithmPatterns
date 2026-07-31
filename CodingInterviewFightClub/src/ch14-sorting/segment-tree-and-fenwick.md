# 14.8 Range Query Structures: Segment Tree & Fenwick Tree

> **Sources:** [`src/main/kotlin/tree/segment/SegmentTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/segment/SegmentTree.kt) · `tree/segment/IterativeSegmentTree.kt` · `DynamicSegmentTree.kt` · `tree/fenwick/FenwickTree.kt` · `tree/fenwick/RangeSumQueryMutable.kt` · `RangeSumQuery2dMutable.kt`
> **Pattern:** the O(log n) range-sum engines · **Core page**

## The Problem (the structure)

Support **point updates** and **range-sum queries** on an array in O(log n): the backbone of [14.9](count-of-smaller-numbers-after-self.md), [7.8](../ch07-heaps/the-skyline-problem.md)'s sweep, and every "range with mutations" problem.

## The two structures

| | Segment Tree | Fenwick Tree (BIT) |
|---|---|---|
| Idea | binary tree over intervals; each node = a range's sum | binary-indexed array; `i += i & -i` walks ancestors |
| Build | O(n) | O(n) (or O(n log n) naive) |
| Range query | O(log n) (any interval) | O(log n) prefix-sum, range = prefix(r) − prefix(l−1) |
| Point update | O(log n) | O(log n) |
| Code size | ~40 lines (with lazy ~60) | ~15 lines |
| When | range *updates* (lazy), min/max, dynamic | pure point-update + prefix queries |

## The Fenwick (15 lines — the repo's `FenwickTree.kt`)

```kotlin
class FenwickTree(private val size: Int) {
    private val tree = IntArray(size + 1)

    fun update(index: Int, delta: Int) {          // 1-based index
        var i = index
        while (i <= size) {
            tree[i] += delta
            i += i and -i                          // next ancestor
        }
    }

    fun query(index: Int): Int {                   // prefix sum [1..index]
        var i = index
        var sum = 0
        while (i > 0) {
            sum += tree[i]
            i -= i and -i                          // parent
        }
        return sum
    }
}
```

**Why `i and -i`?** `i & -i` isolates the lowest set bit — the Fenwick's "how many elements this node covers" marker. Adding it moves to the next covering node; subtracting moves to the parent. The whole structure is that one bit trick.

## The Segment Tree (the repo's `SegmentTree.kt` shape)

```kotlin
class SegmentTree(private val arr: IntArray) {
    private val n = arr.size
    private val tree = LongArray(4 * n) { 0L }
    private val lazy = LongArray(4 * n) { 0L }

    init { build(0, n - 1, 0) }

    private fun build(left: Int, right: Int, node: Int) {
        if (left == right) {
            tree[node] = arr[left].toLong()
            return
        }
        val mid = (left + right) / 2
        build(left, mid, 2 * node + 1)
        build(mid + 1, right, 2 * node + 2)
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2]
    }

    fun update(left: Int, right: Int, node: Int, idx: Int, value: Long) {
        if (left == right) { tree[node] = value; return }
        val mid = (left + right) / 2
        if (idx <= mid) update(left, mid, 2 * node + 1, idx, value)
        else update(mid + 1, right, 2 * node + 2, idx, value)
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2]
    }

    fun query(left: Int, right: Int, node: Int, ql: Int, qr: Int): Long {
        if (qr < left || right < ql) return 0L          // no overlap
        if (ql <= left && right <= qr) return tree[node] // full cover
        val mid = (left + right) / 2
        return query(left, mid, 2 * node + 1, ql, qr) +
               query(mid + 1, right, 2 * node + 2, ql, qr)
    }
}
```

**Why `4n` nodes?** The worst-case tree for n leaves (non-power-of-two) needs < 4n storage — the safe upper bound. The repo sizes it to the next power of two (`2*pow2 - 1`); `4n` is the simpler equivalent.

**The lazy-extension** (the repo's `SegmentTree.kt` full form) adds a `lazy` array for **range updates**: a node's pending delta is stored, applied when visited — the "deferred work" idea that makes range-update O(log n). The 2-D variant (`RangeSumQuery2dMutable.kt`) nests the structure per row/col.

## Complexity

| Op | Segment Tree | Fenwick |
|---|---|---|
| Build | $O(n)$ | $O(n)$ |
| Point update | $O(\log n)$ | $O(\log n)$ |
| Range query | $O(\log n)$ | $O(\log n)$ (2 prefix sums) |
| Range update | $O(\log n)$ (lazy) | — (needs the trickier BIT-of-BIT) |
| Space | $O(n)$ | $O(n)$ |

## Variants & follow-ups

- **Count Of Smaller Numbers After Self** ([14.9](count-of-smaller-numbers-after-self.md)) — the Fenwick's signature application.
- **Rectangle Area II** — the segment tree in a sweep ([7.8](../ch07-heaps/the-skyline-problem.md) family; the repo's `RectangleArea_II_SegmentTree.kt`).
- **Interview follow-up:** "Fenwick or segment tree?" Fenwick when the operation is invertible (sum, xor) and queries are prefixes — 15 lines, faster constants. Segment tree when you need range *updates*, min/max, or non-invertible ops — more code, more power. Name the operation's properties first; the structure follows.

```python
class FenwickTree:
    def __init__(self, size: int):
        self.tree = [0] * (size + 1)

    def update(self, index: int, delta: int) -> None:
        i = index
        while i < len(self.tree):
            self.tree[i] += delta
            i += i & -i                    # next ancestor

    def query(self, index: int) -> int:
        total = 0
        i = index
        while i > 0:
            total += self.tree[i]
            i -= i & -i                    # parent
        return total


class SegmentTree:
    def __init__(self, arr: list[int]):
        self.n = len(arr)
        self.tree = [0] * (4 * self.n)
        self._build(arr, 0, self.n - 1, 0)

    def _build(self, arr, left, right, node):
        if left == right:
            self.tree[node] = arr[left]
            return
        mid = (left + right) // 2
        self._build(arr, left, mid, 2 * node + 1)
        self._build(arr, mid + 1, right, 2 * node + 2)
        self.tree[node] = self.tree[2 * node + 1] + self.tree[2 * node + 2]

    def query(self, ql: int, qr: int, left: int = 0, right: int = None, node: int = 0) -> int:
        if right is None:
            right = self.n - 1
        if qr < left or right < ql:
            return 0                        # no overlap
        if ql <= left and right <= qr:
            return self.tree[node]          # full cover
        mid = (left + right) // 2
        return (self.query(ql, qr, left, mid, 2 * node + 1) +
                self.query(ql, qr, mid + 1, right, 2 * node + 2))
```

```rust
struct FenwickTree {
    tree: Vec<i64>,
}

impl FenwickTree {
    fn new(size: usize) -> Self { Self { tree: vec![0; size + 1] } }

    fn update(&mut self, mut index: usize, delta: i64) {
        while index < self.tree.len() {
            self.tree[index] += delta;
            index += index & index.wrapping_neg();      // next ancestor
        }
    }

    fn query(&self, mut index: usize) -> i64 {
        let mut total = 0;
        while index > 0 {
            total += self.tree[index];
            index -= index & index.wrapping_neg();      // parent
        }
        total
    }
}

// Segment tree: same shape as the Kotlin version (4n array, recursive build/query).
// The query's three cases — no overlap (0), full cover (tree[node]),
// partial (recurse both children) — are the whole design.
```

## Dry run (Fenwick)

**Input:** `nums = [1,3,5]`, build with updates: `update(1,1), update(2,3), update(3,5)`.

```
tree after updates:
  update(1,1): i=1: tree[1]+=1; i=2: tree[2]+=1; i=4: tree[4]+=1.  (n=3 stops at 4)
  update(2,3): i=2: tree[2]+=3 (=4); i=4: tree[4]+=3 (=4)
  update(3,5): i=3: tree[3]+=5; i=4: tree[4]+=5 (=9)

query(3) [sum of 1..3]: i=3: total += tree[3] = 5.  i=2: total += tree[2] = 9.  i=0 stop.  -> 9 ✓
query(2) [sum of 1..2]: i=2: tree[2] = 4.  -> 4 ✓
range [2..3] = query(3) - query(1) = 9 - 1 = 8 ✓   (3 + 5)
```

The bit trick in action: `tree[2]` covers indices 1–2 (its lowest set bit is 2), `tree[3]` covers index 3 alone, `tree[4]` would cover 1–4. Prefix sums walk the covering chain; range sums subtract two prefixes. The segment tree answers `query(2,3)` directly in O(log n) via its overlap cases.

## Variants & follow-ups

- **Count Of Smaller Numbers After Self** ([14.9](count-of-smaller-numbers-after-self.md)) — the Fenwick as an ordered-statistics counter.
- **Range Sum Query Mutable / 2-D** (`tree/fenwick/RangeSumQueryMutable.kt`, `RangeSumQuery2dMutable.kt`) — the repo's LeetCode 307/308 implementations.
- **Interview follow-up:** "When is the segment tree's lazy array mandatory?" When updates are *ranges* (add v to [l, r]) — naively touching every leaf is O(n). Lazy defers a node's pending delta until a query forces it down: O(log n) updates, O(log n) queries, and the "defer work until needed" idea shows up across the whole book ([7.3](../ch07-heaps/sliding-window-median.md)'s lazy deletion, [18.3](../ch18-design-caches/peeking-iterator.md)'s lazy advance).
