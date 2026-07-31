# 18.21 My Calendar II

> **Source**: [`src/main/kotlin/tree/segment/MyCalendar_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/segment/MyCalendar_II.kt)
> **Pattern**: segment tree with lazy propagation · **Core page**

## The Problem

`book(start, end)` — true if the interval can be added **without triple-booking** (two overlaps allowed; the third rejects).

- Constraints: ≤ 1000 bookings; times ≤ 10⁹.

## Examples

```
["MyCalendarTwo","book","book","book","book","book","book"]
[[],[10,20],[50,60],[10,40],[5,15],[5,10],[25,55]]
-> [null,true,true,true,false,true,true]
```

## Intuition — the [18.11](my-calendar.md) overlap counter, generalized to "max 2"

Each booking adds +1 to its range; a booking is legal iff no point in it already has **2** bookings. The repo's segment tree with lazy propagation answers "range max" and applies "range add" in O(log 10⁹):

```kotlin
fun book(startTime: Int, endTime: Int): Boolean {
    val end = endTime - 1

    return when (query(root, minTime, maxTime, startTime, end)) {
        in 2..Int.MAX_VALUE -> false      // a point already double-booked
        else -> {
            lazyUpdate(root, minTime, maxTime, startTime, end, 1)
            true
        }
    }
}
```

**Why query-then-update?** The legality test is "is the max over the range < 2?" — a range-max query. Only if it passes does the range-add apply. The two operations are the segment tree's stock in trade ([14.8](../ch14-sorting/segment-tree-and-fenwick.md) engine).

**Why lazy propagation?** Times go to 10⁹ — a full segment tree is 4×10⁹ nodes. Lazy updates only materialize visited ranges: O(log 10⁹) per book with implicit node creation.

## Approach 1 — Two-level sweep (the simpler O(n²) version)

Keep a list of single bookings and a list of double-booking ranges; a new booking overlaps double if it intersects any double range. O(n²) per book — fine at n ≤ 1000, the segment tree is the scale-up.

## Approach 2 — Lazy segment tree (the repo's version, optimal)

```kotlin
class MyCalendarTwo() {
    private class Node {
        var peakBookings = 0
        var lazyIncrement = 0
        var leftChild: Node? = null
        var rightChild: Node? = null
    }

    private val root = Node()
    private val minTime = 0
    private val maxTime = 1_000_000_000

    /**
     * @param startTime booking start
     * @param endTime   booking end (exclusive)
     * @return          true if no triple-booking results
     */
    fun book(startTime: Int, endTime: Int): Boolean {
        val end = endTime - 1

        return when (query(root, minTime, maxTime, startTime, end)) {
            in 2..Int.MAX_VALUE -> false
            else -> {
                lazyUpdate(root, minTime, maxTime, startTime, end, 1)
                true
            }
        }
    }

    private fun query(node: Node, rangeStart: Int, rangeEnd: Int,
                      queryLeft: Int, queryRight: Int): Int {
        if (queryLeft <= rangeStart && rangeEnd <= queryRight) {
            return node.peakBookings
        }

        pushDown(node)
        val mid = rangeStart + (rangeEnd - rangeStart) / 2
        var max = 0

        if (queryLeft <= mid) {
            node.leftChild?.let { max = maxOf(max, query(it, rangeStart, mid, queryLeft, queryRight)) }
        }
        if (queryRight > mid) {
            node.rightChild?.let { max = maxOf(max, query(it, mid + 1, rangeEnd, queryLeft, queryRight)) }
        }
        return max
    }

    private fun lazyUpdate(node: Node, rangeStart: Int, rangeEnd: Int,
                           queryLeft: Int, queryRight: Int, value: Int) {
        if (queryLeft <= rangeStart && rangeEnd <= queryRight) {
            node.peakBookings += value
            node.lazyIncrement += value
            return
        }

        pushDown(node)
        val mid = rangeStart + (rangeEnd - rangeStart) / 2

        if (queryLeft <= mid) {
            if (node.leftChild == null) node.leftChild = Node()
            node.leftChild?.let { lazyUpdate(it, rangeStart, mid, queryLeft, queryRight, value) }
        }
        if (queryRight > mid) {
            if (node.rightChild == null) node.rightChild = Node()
            node.rightChild?.let { lazyUpdate(it, mid + 1, rangeEnd, queryLeft, queryRight, value) }
        }

        node.peakBookings = maxOf(
            node.leftChild?.peakBookings ?: 0,
            node.rightChild?.peakBookings ?: 0
        )
    }

    private fun pushDown(node: Node) {
        if (node.lazyIncrement == 0) return

        if (node.leftChild == null) node.leftChild = Node()
        if (node.rightChild == null) node.rightChild = Node()

        node.leftChild!!.peakBookings += node.lazyIncrement
        node.leftChild!!.lazyIncrement += node.lazyIncrement
        node.rightChild!!.peakBookings += node.lazyIncrement
        node.rightChild!!.lazyIncrement += node.lazyIncrement

        node.lazyIncrement = 0
    }
}
```

```java
public class MyCalendarTwo {
    private static class Node {
        int peak = 0, lazy = 0;
        Node left, right;
    }

    private final Node root = new Node();
    private final int MIN = 0, MAX = 1_000_000_000;

    /**
     * @param startTime booking start
     * @param endTime   booking end (exclusive)
     * @return          true if no triple-booking results
     */
    public boolean book(int startTime, int endTime) {
        int end = endTime - 1;

        if (query(root, MIN, MAX, startTime, end) >= 2) return false;

        update(root, MIN, MAX, startTime, end, 1);
        return true;
    }

    private int query(Node node, int lo, int hi, int ql, int qr) {
        if (ql <= lo && hi <= qr) return node.peak;
        push(node);
        int mid = lo + (hi - lo) / 2, best = 0;
        if (ql <= mid && node.left != null) best = Math.max(best, query(node.left, lo, mid, ql, qr));
        if (qr > mid && node.right != null) best = Math.max(best, query(node.right, mid + 1, hi, ql, qr));
        return best;
    }

    private void update(Node node, int lo, int hi, int ql, int qr, int v) {
        if (ql <= lo && hi <= qr) {
            node.peak += v;
            node.lazy += v;
            return;
        }
        push(node);
        int mid = lo + (hi - lo) / 2;
        if (ql <= mid) { if (node.left == null) node.left = new Node(); update(node.left, lo, mid, ql, qr, v); }
        if (qr > mid) { if (node.right == null) node.right = new Node(); update(node.right, mid + 1, hi, ql, qr, v); }
        node.peak = Math.max(node.left == null ? 0 : node.left.peak,
                             node.right == null ? 0 : node.right.peak);
    }

    private void push(Node node) {
        if (node.lazy == 0) return;
        if (node.left == null) node.left = new Node();
        if (node.right == null) node.right = new Node();
        node.left.peak += node.lazy;  node.left.lazy += node.lazy;
        node.right.peak += node.lazy; node.right.lazy += node.lazy;
        node.lazy = 0;
    }
}
```

```cpp
class MyCalendarTwo {
    struct Node {
        int peak = 0, lazy = 0;
        Node* left = nullptr;
        Node* right = nullptr;
    };

    Node* root = new Node();
    const int MIN = 0, MAX = 1e9;

    void push(Node* node) {
        if (!node->lazy) return;
        if (!node->left) node->left = new Node();
        if (!node->right) node->right = new Node();
        node->left->peak += node->lazy;  node->left->lazy += node->lazy;
        node->right->peak += node->lazy; node->right->lazy += node->lazy;
        node->lazy = 0;
    }

    int query(Node* node, int lo, int hi, int ql, int qr) {
        if (ql <= lo && hi <= qr) return node->peak;
        push(node);
        int mid = lo + (hi - lo) / 2, best = 0;
        if (ql <= mid && node->left) best = std::max(best, query(node->left, lo, mid, ql, qr));
        if (qr > mid && node->right) best = std::max(best, query(node->right, mid + 1, hi, ql, qr));
        return best;
    }

    void update(Node* node, int lo, int hi, int ql, int qr, int v) {
        if (ql <= lo && hi <= qr) { node->peak += v; node->lazy += v; return; }
        push(node);
        int mid = lo + (hi - lo) / 2;
        if (ql <= mid) { if (!node->left) node->left = new Node(); update(node->left, lo, mid, ql, qr, v); }
        if (qr > mid) { if (!node->right) node->right = new Node(); update(node->right, mid + 1, hi, ql, qr, v); }
        node->peak = std::max(node->left ? node->left->peak : 0,
                              node->right ? node->right->peak : 0);
    }

public:
    /**
     * @param startTime booking start
     * @param endTime   booking end (exclusive)
     * @return          true if no triple-booking results
     */
    bool book(int startTime, int endTime) {
        int end = endTime - 1;
        if (query(root, MIN, MAX, startTime, end) >= 2) return false;
        update(root, MIN, MAX, startTime, end, 1);
        return true;
    }
};
```

```python
class Node:
    __slots__ = ("peak", "lazy", "left", "right")
    def __init__(self):
        self.peak = 0
        self.lazy = 0
        self.left = None
        self.right = None


class MyCalendarTwo:
    def __init__(self):
        self.root = Node()
        self.MIN, self.MAX = 0, 1_000_000_000

    def book(self, startTime: int, endTime: int) -> bool:
        end = endTime - 1

        if self._query(self.root, self.MIN, self.MAX, startTime, end) >= 2:
            return False

        self._update(self.root, self.MIN, self.MAX, startTime, end, 1)
        return True

    def _push(self, node: Node) -> None:
        if node.lazy == 0:
            return
        if not node.left:
            node.left = Node()
        if not node.right:
            node.right = Node()
        node.left.peak += node.lazy
        node.left.lazy += node.lazy
        node.right.peak += node.lazy
        node.right.lazy += node.lazy
        node.lazy = 0

    def _query(self, node: Node, lo: int, hi: int, ql: int, qr: int) -> int:
        if ql <= lo and hi <= qr:
            return node.peak
        self._push(node)
        mid = lo + (hi - lo) // 2
        best = 0
        if ql <= mid and node.left:
            best = max(best, self._query(node.left, lo, mid, ql, qr))
        if qr > mid and node.right:
            best = max(best, self._query(node.right, mid + 1, hi, ql, qr))
        return best

    def _update(self, node: Node, lo: int, hi: int, ql: int, qr: int, v: int) -> None:
        if ql <= lo and hi <= qr:
            node.peak += v
            node.lazy += v
            return
        self._push(node)
        mid = lo + (hi - lo) // 2
        if ql <= mid:
            if not node.left:
                node.left = Node()
            self._update(node.left, lo, mid, ql, qr, v)
        if qr > mid:
            if not node.right:
                node.right = Node()
            self._update(node.right, mid + 1, hi, ql, qr, v)
        node.peak = max(node.left.peak if node.left else 0,
                        node.right.peak if node.right else 0)
```

```rust
// Rust has no implicit-node segment trees in std — the two-level sweep
// (bookings list + double-booked ranges list) is the idiomatic port:

struct MyCalendarTwo {
    bookings: Vec<(i32, i32)>,
    overlaps: Vec<(i32, i32)>,
}

impl MyCalendarTwo {
    fn new() -> Self { Self { bookings: Vec::new(), overlaps: Vec::new() } }

    /// @param startTime booking start
    /// @param endTime   booking end (exclusive)
    /// @return          true if no triple-booking results
    fn book(&mut self, start: i32, end: i32) -> bool {
        for &(os, oe) in &self.overlaps {
            if start < oe && os < end { return false; }   // triple!
        }
        for &(bs, be) in &self.bookings {
            let s = start.max(bs);
            let e = end.min(be);
            if s < e { self.overlaps.push((s, e)); }      // new double-booking
        }
        self.bookings.push((start, end));
        true
    }
}
```

## Dry run

**Input:** the example.

```
book(10,20): overlaps empty -> ok.  overlaps += [10,20]? no — [10,20] is the booking itself.
  bookings: [(10,20)].  overlaps: [].
book(50,60): ok.  bookings: [(10,20),(50,60)].
book(10,40): overlaps empty -> ok.  new double range with (10,20): [10,20].
  bookings + (10,40).  overlaps: [(10,20)].
book(5,15): overlaps [(10,20)]: 5<20 && 10<15 -> TRUE -> reject → false ✓
book(5,10): overlaps [(10,20)]: 5<20 && 10<10? no -> ok.  double with (10,20): [10,10]? empty.
  double with (10,40): [10,10]? empty.  overlaps stays.  -> true.
book(25,55): overlaps [(10,20)]: no.  double with (10,40): [25,40].  with (50,60): [50,55].
  -> true.
```

The segment tree's `query >= 2` performs the same test in O(log 10⁹) — range-max says "any point already at 2?" The lazy push materializes children only when touched, keeping the implicit tree small.

## Complexity

**Time.** O(log 10⁹) per book:

$$
T = O(\log U)
$$

**Space.** Visited tree nodes:

$$
S = O(n \log U)
$$

## Variants & follow-ups

- **My Calendar** ([18.11](my-calendar.md)) — the no-overlap ancestor (range-max vs 1).
- **Segment Tree** ([14.8](../ch14-sorting/segment-tree-and-fenwick.md)) — the lazy engine.
- **Interview follow-up:** "Why is a segment tree better than the two-level sweep?" At 1000 bookings both work; the sweep is O(n²) per book, the tree O(log 10⁹). The tree also generalizes to My Calendar III (k-level with k ≥ 3) unchanged — the `>= 2` test becomes `>= k`.
