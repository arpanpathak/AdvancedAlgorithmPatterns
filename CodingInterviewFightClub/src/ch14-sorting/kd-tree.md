# 14.10 KD-Tree (K-Nearest Neighbors in 2D)

> **Source:** [`src/main/kotlin/geo/kdtree/KDTreeExample.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/geo/kdtree/KDTreeExample.kt)
> **Pattern:** axis-alternating BST over points · **Core page**

## The Problem

Given a set of 2-D points, support:

- `insert(point)` — add a point;
- `findKNearestNeighbors(query, k)` — return the `k` points closest to `query` (by Euclidean distance).

The naive approach — scan all points, keep the k closest — is O(n) per query. A **kd-tree** (k-dimensional tree) organizes points so that a nearest-neighbor search can *prune whole regions* of the space, giving average-case $O(\log n)$ behavior.

## Examples

```
points: (2,3), (5,4), (9,6), (4,7), (8,1), (7,2)
query: (5,5), k = 2 -> the two nearest are (5,4) [dist 1] and (4,7) [dist √5 ≈ 2.24]
```

## Intuition — a binary search tree, but the "key" alternates between x and y

A BST works because keys are 1-D and ordered. A point has two coordinates — so a kd-tree makes the ordering *alternate by depth*:

> At depth 0, split on x: points with smaller x go left, larger x go right. At depth 1, split on **y** instead. Depth 2 back to x, and so on. Each node stores one point; the tree recursively partitions the plane into axis-aligned half-planes.

Why alternate axes? Splitting always on x would give a degenerate tree when points share x-values, and the rectangles each node "owns" would get long and thin — bad for pruning. Alternating keeps the cells roughly square, which is what makes the search efficient.

**How does search prune?** At each node, compute the distance to the node's point (update the k-best heap if it's closer than the current k-th). Then decide which child to descend into: the one whose side of the splitting line the query is on. The key question is whether we must also visit the *other* side. If the distance from the query to the splitting line is **larger than the current k-th best distance**, then every point on the other side is farther than the k-th best — the whole half-plane can be *discarded* without looking at it. That's the pruning that turns "check everything" into "check a few".

## Approach 1 — Brute force with a max-heap (O(n) per query)

Scan all points, keep a max-heap of the k closest: correct, simple, and the right answer for small n. The kd-tree is worth it when n is large and queries are many.

## Approach 2 — KD-tree with nearest-neighbor search (the repo's version)

```kotlin
import java.util.PriorityQueue

data class Point(val x: Double, val y: Double)

// Max-heap neighbor: the largest distance sits on top, so we can evict it
data class Neighbor(val point: Point, val distanceSq: Double) : Comparable<Neighbor> {
    override fun compareTo(other: Neighbor): Int = other.distanceSq.compareTo(this.distanceSq)
}

data class KDTreeNode(
    val point: Point,
    val depth: Int,
    var left: KDTreeNode? = null,
    var right: KDTreeNode? = null
)

class KDTree {
    private var root: KDTreeNode? = null
    private val K = 2   // 2-D

    fun insert(point: Point) {
        root = insert(root, point, 0)
    }

    private fun insert(node: KDTreeNode?, newPoint: Point, depth: Int): KDTreeNode {
        if (node == null) return KDTreeNode(newPoint, depth)

        val axis = depth % K
        val shouldGoLeft = when (axis) {
            0 -> newPoint.x < node.point.x
            1 -> newPoint.y < node.point.y
            else -> throw IllegalStateException("kd-tree is only 2D")
        }

        return node.apply {
            when (shouldGoLeft) {
                true -> left = insert(left, newPoint, depth + 1)
                false -> right = insert(right, newPoint, depth + 1)
            }
        }
    }

    fun findKNearestNeighbors(query: Point, k: Int): List<Point> {
        if (root == null || k <= 0) return emptyList()

        val heap = PriorityQueue<Neighbor>(k)   // max-heap of the k best so far

        fun search(node: KDTreeNode?) {
            node ?: return

            val axis = node.depth % K
            val distanceSq = distanceSq(query, node.point)

            // 1. Update the k-best heap
            if (heap.size < k) {
                heap.add(Neighbor(node.point, distanceSq))
            } else if (distanceSq < heap.peek().distanceSq) {
                heap.poll()
                heap.add(Neighbor(node.point, distanceSq))
            }

            // 2. Decide which side to search first (the side containing the query)
            val queryLess = when (axis) {
                0 -> query.x < node.point.x
                else -> query.y < node.point.y
            }
            val (near, far) = if (queryLess) node.left to node.right else node.right to node.left
            search(near)

            // 3. Prune: only search the far side if the splitting line is
            //    closer than the current k-th best distance
            val axisDistanceSq = when (axis) {
                0 -> (query.x - node.point.x).let { it * it }
                else -> (query.y - node.point.y).let { it * it }
            }
            if (heap.size < k || axisDistanceSq < heap.peek().distanceSq) {
                search(far)
            }
        }

        search(root)
        return heap.sortedBy { it.distanceSq }.map { it.point }
    }
}

fun distanceSq(p1: Point, p2: Point): Double {
    val dx = p1.x - p2.x
    val dy = p1.y - p2.y
    return dx * dx + dy * dy
}
```

```python
import heapq

def distance_sq(p1, p2):
    return (p1[0] - p2[0]) ** 2 + (p1[1] - p2[1]) ** 2

class KDTree:
    def __init__(self):
        self.root = None

    def insert(self, point, depth=0):
        if self.root is None:
            self.root = (point, depth, None, None)
            return
        node, d, _, _ = self.root
        axis = d % 2
        if point[axis] < node[axis]:
            self._insert_left(self.root, point, d + 1)
        else:
            self._insert_right(self.root, point, d + 1)

    # (simplified recursive insert — see the repo for the full version)
    def _insert_left(self, node, point, depth):
        if node[2] is None:
            node[2] = (point, depth, None, None)
        else:
            axis = depth % 2
            if point[axis] < node[2][0][axis]:
                self._insert_left(node[2], point, depth + 1)
            else:
                self._insert_right(node[2], point, depth + 1)

    def _insert_right(self, node, point, depth):
        if node[3] is None:
            node[3] = (point, depth, None, None)
        else:
            axis = depth % 2
            if point[axis] < node[3][0][axis]:
                self._insert_left(node[3], point, depth + 1)
            else:
                self._insert_right(node[3], point, depth + 1)

    def find_k_nearest(self, query, k):
        best = []   # max-heap via negative distances
        def search(node):
            if node is None:
                return
            point, depth, left, right = node
            d = distance_sq(query, point)
            if len(best) < k:
                heapq.heappush(best, (-d, point))
            elif d < -best[0][0]:
                heapq.heapreplace(best, (-d, point))

            axis = depth % 2
            near, far = (left, right) if query[axis] < point[axis] else (right, left)
            search(near)

            split_dist = (query[axis] - point[axis]) ** 2
            if len(best) < k or split_dist < -best[0][0]:
                search(far)

        search(self.root)
        return [p for _, p in sorted(best, key=lambda t: -t[0])]
```

```java
import java.util.*;

class KDTree {
    static class Node {
        double[] p; int depth; Node left, right;
        Node(double[] p, int depth) { this.p = p; this.depth = depth; }
    }

    private Node root;

    public void insert(double[] point) { root = insert(root, point, 0); }

    private Node insert(Node node, double[] p, int depth) {
        if (node == null) return new Node(p, depth);
        int axis = depth % 2;
        if (p[axis] < node.p[axis]) node.left = insert(node.left, p, depth + 1);
        else node.right = insert(node.right, p, depth + 1);
        return node;
    }

    /**
     * @param query the query point [x, y]
     * @param k     number of neighbors to return
     * @return      the k nearest points, nearest first
     */
    public List<double[]> findKNearest(double[] query, int k) {
        PriorityQueue<double[]> heap = new PriorityQueue<>(
            (a, b) -> Double.compare(distSq(b, query), distSq(a, query)));  // max-heap
        search(root, query, k, heap);
        List<double[]> out = new ArrayList<>(heap);
        out.sort(Comparator.comparingDouble(a -> distSq(a, query)));
        return out;
    }

    private void search(Node node, double[] q, int k, PriorityQueue<double[]> heap) {
        if (node == null) return;
        double d = distSq(node.p, q);
        if (heap.size() < k) heap.add(node.p);
        else if (d < distSq(heap.peek(), q)) { heap.poll(); heap.add(node.p); }

        int axis = node.depth % 2;
        boolean goLeft = q[axis] < node.p[axis];
        search(goLeft ? node.left : node.right, q, k, heap);

        double split = (q[axis] - node.p[axis]);
        if (heap.size() < k || split * split < distSq(heap.peek(), q)) {
            search(goLeft ? node.right : node.left, q, k, heap);
        }
    }

    private static double distSq(double[] a, double[] b) {
        double dx = a[0] - b[0], dy = a[1] - b[1];
        return dx * dx + dy * dy;
    }
}
```

## Reading the code — what's actually happening

- **`insert` is a BST insert with a rotating key.** `axis = depth % 2` picks the coordinate to compare at this level (x at even depth, y at odd). New points descend left/right exactly like a binary search tree — but the comparison key changes every level. Note the recursion carries `depth + 1` so each node knows its own axis.
- **The search heap is a *max*-heap of the k best.** `Neighbor`'s `compareTo` is inverted (`other.distanceSq.compareTo(this.distanceSq)`), so the *largest* distance sits on top. That makes the eviction test trivial: a new point joins the top-k iff `distanceSq < heap.peek().distanceSq` — i.e., it's closer than the current *worst* of the best k.
- **`search(near)` descends toward the query first.** The near child is the one on the query's side of the split. Searching near-first is what builds a good heap early — a tight k-th distance makes the far-side pruning aggressive.
- **The pruning test is the whole point of the structure.** `axisDistanceSq` is the squared distance from the query to the splitting line. If that's already ≥ the k-th best distance, **no point on the far side can possibly beat the current top-k** — every far-side point is at least `axisDistanceSq` away, and the heap's worst is closer. Skipping `search(far)` is where the O(n) scan becomes a near-O(log n) search.
- **The `heap.size < k` guard** in the pruning test keeps correctness during the warm-up phase: until we've found k points, we must visit both sides (there's no "k-th best" to prune against yet).

## Dry run

**Input:** points `(2,3), (5,4), (9,6), (4,7), (8,1), (7,2)`, query `(5,5)`, k = 2.

```
Insert (2,3) at depth 0 (axis x).
Insert (5,4): 5 >= 2 -> right subtree, depth 1 (axis y).
Insert (9,6): 9 >= 2 -> right, 6 >= 4 -> right of (5,4), depth 2 (axis x).
Insert (4,7): 4 >= 2 -> right; 7 >= 4 -> right of (5,4)... (4,7) is to the LEFT of (5,4) on y? 7 >= 4 -> right.
   then depth 2 (axis x): 4 < 9 -> left of (9,6).
... (tree shape depends on insertion order)

Search (5,5), k=2:
  Visit (2,3): d² = 13. heap = [(2,3)].
  Go right (query.x 5 >= 2). Visit (5,4): d² = 1. heap = [(2,3)|d13, (5,4)|d1] (max-heap, 13 on top).
  Query (5,5) vs (5,4) axis y: 5 >= 4 -> right. Visit (9,6): d² = 17. 17 < 13? No -> skip.
    Prune far side of (5,4): axis y, split dist = (5-4)² = 1 < 13 -> must search left of (5,4)...
  Eventually the heap settles on (5,4) [d²=1] and (4,7) [d²=5].
Output: [(5,4), (4,7)] ✓
```

The pruning moments are where the tree pays off: entire subtrees get skipped whenever their side of a splitting line is farther than the current 2nd-best distance.

## Complexity

**Time.** Balanced case: insertion $O(\log n)$; nearest-neighbor $O(\log n)$ average, $O(n)$ worst (degenerate/unbalanced tree — e.g., points inserted in sorted order).

$$
T_{\text{insert}} = O(\log n), \qquad T_{\text{search}} = O(\log n)\ \text{avg}
$$

**Space.** One node per point:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **K Closest Points To Origin** ([14.3](k-closest-points-to-origin.md)) — the static version: no insertions, so a heap or quickselect over all points beats building a tree.
- **Range queries / kd-tree variants** — counting points inside a rectangle uses the same alternating-split structure with the same pruning idea, on both axes.
- **The repo's fuller `KDTreeExample.kt`** — includes the `Rectangle`-based bounding-box checks used to prune range queries; the nearest-neighbor core above is the interview-essential subset.
- **Interview follow-up:** "What if points arrive sorted by x?" The tree degenerates into a chain (all inserts go right), and search becomes O(n). The fix is a **balanced** variant (median-splitting during build, or a scapegoat/randomized kd-tree) — worth naming even if you don't implement it.
