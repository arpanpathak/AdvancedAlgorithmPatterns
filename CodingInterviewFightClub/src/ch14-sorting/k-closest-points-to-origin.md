# 14.3 K Closest Points To Origin

> **Source:** [`src/main/kotlin/quicksort/KClosestPointsToOrigin.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/quicksort/KClosestPointsToOrigin.kt)
> **Pattern:** quickselect on distance · **Core page**

## The Problem

Given `points[i] = [x, y]`, return the `k` closest points to the origin `(0,0)` (Euclidean distance, any order).

- Constraints: $1 \le k \le n \le 10^4$.

## Examples

```
Input:  points = [[1,3],[-2,2]], k = 1
Output: [[-2,2]]     (distance sqrt(8) vs sqrt(5))

Input:  points = [[3,3],[5,-1],[-2,4]], k = 2
Output: [[3,3],[-2,4]]   (distances sqrt(18), sqrt(26), sqrt(20) — two smallest)
```

## Intuition — the [14.2](kth-largest-element.md) loop, re-keyed

The k *closest* points are the k *smallest* by distance — so this is the same quickselect skeleton, with the partition key being **squared distance** (`x² + y²`, no sqrt needed since comparison order is preserved) and the target being index `k - 1`.

The repo's loop narrows until the k-th point is at its final position, then returns `points.copyOfRange(0, K)` — the first k elements are *guaranteed* to be the k closest (all ≤ pivot ≤ the rest) even though their internal order is arbitrary.

**Why squared distance?** `sqrt` is monotonic — `d1 < d2 ⟺ d1² < d2²` — and computing the square avoids floating point entirely. Using `x*x + y*y` as the comparator key is a micro-optimization that also dodges precision concerns (the values fit in Int at these bounds).

**Why quickselect over sorting?** Same argument as [14.2](kth-largest-element.md): only the position `k-1` must be final, so one-sided partition ($O(n)$ expected) beats a full sort ($O(n \log n)$). The heap alternative from [Chapter 7](../ch07-heaps/index.md) ($O(n \log k)$) is the guaranteed-worst-case cousin.

## Approach 1 — Sort by distance, take k

`points.sortedBy { it[0]*it[0] + it[1]*it[1] }.take(k)`: $O(n \log n)$ — correct, and the "simple first" answer to improve.

## Approach 2 — Quickselect on squared distance (the repo's version, optimal)

```kotlin
import kotlin.random.Random

class KClosestPointsToOrigin {
    /**
     * @param points points[i] = [x, y]
     * @param k      how many closest to return
     * @return       the k closest points to the origin (any order)
     */
    fun kClosest(points: Array<IntArray>, K: Int): Array<IntArray> {
        var (start, end) = Pair(0, points.size - 1)

        // Partition until the k-th point is in its final position
        while (start < end) {
            val pivotIndex = quickSort(points, start, end)
            when {
                pivotIndex < K - 1 -> start = pivotIndex + 1     // answer to the right
                pivotIndex > K - 1 -> end = pivotIndex - 1       // answer to the left
                else -> break                                    // k-th is final
            }
        }
        return points.copyOfRange(0, K)                          // the k closest
    }

    private fun quickSort(points: Array<IntArray>, start: Int, end: Int): Int {
        var partitionIndex = start
        val randomIndex = Random.nextInt(start, end + 1)
        swap(points, end, randomIndex)                           // random pivot to the end

        val pivotDistance = calculateDistance(points[end])

        for (i in start until end) {
            if (calculateDistance(points[i]) <= pivotDistance) {
                swap(points, i, partitionIndex++)
            }
        }
        swap(points, partitionIndex, end)
        return partitionIndex
    }

    private fun swap(points: Array<IntArray>, i: Int, j: Int) {
        points[j] = points[i].also { points[i] = points[j] }
    }

    private fun calculateDistance(point: IntArray): Int {
        return point[0] * point[0] + point[1] * point[1]         // squared: no sqrt needed
    }
}
```

```java
import java.util.*;

public class KClosestPointsToOrigin {
    /**
     * @param points points[i] = [x, y]
     * @param k      how many closest to return
     * @return       the k closest points to the origin (any order)
     */
    public int[][] kClosest(int[][] points, int k) {
        int start = 0, end = points.length - 1;

        while (start < end) {
            int pivotIndex = partition(points, start, end);
            if (pivotIndex < k - 1) start = pivotIndex + 1;
            else if (pivotIndex > k - 1) end = pivotIndex - 1;
            else break;
        }
        return Arrays.copyOf(points, k);                         // the k closest
    }

    private int partition(int[][] points, int start, int end) {
        int pivotIdx = start + new Random().nextInt(end - start + 1);
        swap(points, pivotIdx, end);                             // random pivot to the end
        int pivotDist = dist(points[end]);

        int i = start;
        for (int j = start; j < end; j++) {
            if (dist(points[j]) <= pivotDist) swap(points, i++, j);
        }
        swap(points, i, end);
        return i;
    }

    private int dist(int[] p) { return p[0] * p[0] + p[1] * p[1]; }   // squared: no sqrt

    private void swap(int[][] a, int i, int j) {
        int[] t = a[i]; a[i] = a[j]; a[j] = t;
    }
}
```

```cpp
#include <cstdlib>
#include <vector>

class KClosestPointsToOrigin {
    int dist(const std::vector<int>& p) { return p[0] * p[0] + p[1] * p[1]; }   // squared

    int partition(std::vector<std::vector<int>>& pts, int start, int end) {
        int pivotIdx = start + std::rand() % (end - start + 1);   // random pivot
        std::swap(pts[pivotIdx], pts[end]);
        int pivotDist = dist(pts[end]);

        int i = start;
        for (int j = start; j < end; j++) {
            if (dist(pts[j]) <= pivotDist) std::swap(pts[i++], pts[j]);
        }
        std::swap(pts[i], pts[end]);
        return i;
    }

public:
    /**
     * @param points points[i] = [x, y]
     * @param k      how many closest to return
     * @return       the k closest points to the origin (any order)
     */
    std::vector<std::vector<int>> kClosest(std::vector<std::vector<int>>& points, int k) {
        int start = 0, end = points.size() - 1;

        while (start < end) {
            int pivotIndex = partition(points, start, end);
            if (pivotIndex < k - 1) start = pivotIndex + 1;
            else if (pivotIndex > k - 1) end = pivotIndex - 1;
            else break;
        }
        return std::vector<std::vector<int>>(points.begin(), points.begin() + k);
    }
};
```

```python
import random

def k_closest(points: list[list[int]], k: int) -> list[list[int]]:
    """
    @param points: points[i] = [x, y]
    @param k:      how many closest to return
    @return:       the k closest points to the origin (any order)
    """
    def dist(p):
        return p[0] * p[0] + p[1] * p[1]        # squared: no sqrt needed

    def partition(start: int, end: int) -> int:
        pivot_idx = random.randint(start, end)   # random pivot
        points[pivot_idx], points[end] = points[end], points[pivot_idx]
        pivot_dist = dist(points[end])

        i = start
        for j in range(start, end):
            if dist(points[j]) <= pivot_dist:
                points[i], points[j] = points[j], points[i]
                i += 1
        points[i], points[end] = points[end], points[i]
        return i

    start, end = 0, len(points) - 1
    while start < end:
        pivot_index = partition(start, end)
        if pivot_index < k - 1:
            start = pivot_index + 1
        elif pivot_index > k - 1:
            end = pivot_index - 1
        else:
            break
    return points[:k]
```

```rust
use rand::Rng;

impl Solution {
    /// @param points points[i] = [x, y]
    /// @param k      how many closest to return
    /// @return       the k closest points to the origin (any order)
    pub fn k_closest(points: Vec<Vec<i32>>, k: i32) -> Vec<Vec<i32>> {
        fn dist(p: &Vec<i32>) -> i32 { p[0] * p[0] + p[1] * p[1] }   // squared

        fn partition(points: &mut Vec<Vec<i32>>, start: usize, end: usize) -> usize {
            let pivot_idx = start + rand::thread_rng().gen_range(0..end - start + 1);
            points.swap(pivot_idx, end);
            let pivot_dist = dist(&points[end]);

            let mut i = start;
            for j in start..end {
                if dist(&points[j]) <= pivot_dist {
                    points.swap(i, j);
                    i += 1;
                }
            }
            points.swap(i, end);
            i
        }

        let mut points = points;
        let k = k as usize;
        let (mut start, mut end) = (0, points.len() - 1);

        while start < end {
            let pivot_index = partition(&mut points, start, end);
            if pivot_index < k - 1 { start = pivot_index + 1; }
            else if pivot_index > k - 1 { end = pivot_index - 1; }
            else { break; }
        }
        points.truncate(k);                       // the k closest
        points
    }
}
```

## Dry run

**Input:** `points = [[3,3],[5,-1],[-2,4]]`, `k = 2`.

```
distances: [3,3]->18, [5,-1]->26, [-2,4]->20.  target index = k-1 = 1.

partition(0,2): random pivot, say index 1 ([5,-1], dist 26):
  partition by 26: all ≤ 26 -> [3,3],[5,-1],[-2,4], pivot at index 2.
  pivotIndex 2 > target 1 -> end = 1.
partition(0,1): random pivot, say index 0 ([3,3], dist 18):
  partition by 18: [3,3],[5,-1], pivot at index 0.
  pivotIndex 0 < target 1 -> start = 1.
  (start=1, end=1: loop ends)

return points[0..2) = [[3,3],[5,-1]]? 
```

Wait — that gives `[[3,3],[5,-1]]` but the expected output is `[[3,3],[-2,4]]` (distances 18 and 20; `[5,-1]` is 26). Let me recheck. After partition(0,1) with pivot [3,3] (dist 18): partition by 18 — elements ≤ 18: [3,3] only → i ends at 1 after placing [3,3] at index 0... wait: start=0,end=1. pivot dist 18. j=0: points[0]=[3,3], dist 18 ≤ 18 → swap(i=0,j=0), i=1. j=1: [5,-1] dist 26 ≤ 18? no. Then swap(i=1, end=1): pivot goes to index 1. So array: [[3,3],[5,-1]]. pivotIndex=1 == target → break (in the `else break` branch... actually the while loop: start=0,end=1, pivotIndex=1. pivotIndex > k-1? 1 > 1 no. pivotIndex < 1? no. else break. Loop ends with start=0, end=1. points[0..2) = [[3,3],[5,-1]].

Hmm, that's WRONG — [5,-1] (26) should not be in the top-2; [-2,4] (20) should. Let me re-trace the FIRST partition. partition(0,2) with pivot [5,-1] (26): j=0: [3,3] 18 ≤ 26 → swap(0,0), i=1. j=1: skip (j is pivot). j=2: [-2,4] 20 ≤ 26 → swap(i=1, j=2): array becomes [[3,3],[-2,4],[5,-1]], i=2. Then swap(i=2, end=2) (pivot already at end) → [[3,3],[-2,4],[5,-1]], pivotIndex=2.

So after partition 1: [[3,3],[-2,4],[5,-1]], pivotIndex=2 > 1 → end=1. GOOD.
partition(0,1): start=0, end=1. random pivot from {0,1}: say 0 ([3,3], dist 18). Swap(0,1): [[5,-1],[-2,4]]... wait no — current array is [[3,3],[-2,4],[5,-1]], subarray [0..1] = [[3,3],[-2,4]]. pivot_idx = rand(0,1). Say 0 ([3,3], 18). swap(0,1): [[-2,4],[3,3],[5,-1]]. pivot = [3,3] (18). j=0: [-2,4] dist 20 ≤ 18? NO. So i stays 0. swap(i=0, end=1): [[3,3],[-2,4],[5,-1]] — pivot [3,3] goes to index 0! pivotIndex = 0 < target 1 → start = 1. Loop: start=1, end=1 → ends.

return points[0..2) = [[3,3],[-2,4]] ✓ CORRECT!

My scratch trace had an error: after partition(0,1), the pivot lands at index 0 (not 1), because [-2,4] (20) is NOT ≤ 18, so nothing moves left of the pivot. Let me redo: partition by pivot 18: [-2,4] has dist 20 > 18, so it does NOT move to the left region; i stays at 0; pivot swaps to index 0. So [3,3] ends at index 0, [-2,4] at index 1, and the k=2 answer [[3,3],[-2,4]] ✓.

Good — the algorithm is correct; my scratch slipped. In the dry run I write, I must be careful. Let me write the final part-2 with a CORRECT trace:

```
partition(0,2): pivot [5,-1] (26): partition -> [[3,3],[-2,4],[5,-1]], pivotIndex=2 > 1 -> end=1
partition(0,1): subarray [[3,3],[-2,4]]; random pivot [3,3] (18):
  [-2,4] (20) > 18 -> stays right; pivot swaps to index 0.
  array: [[3,3],[-2,4],[5,-1]], pivotIndex=0 < 1 -> start=1. loop ends.
return points[0..2) = [[3,3],[-2,4]] ✓
```

Let me write the final content.<｜end▁of▁thinking｜>I caught another scratch slip in my `k-closest` trace (the second partition's pivot actually lands at index 0, not 1, giving the correct `[[3,3],[-2,4]]`). Writing the final content with the verified trace:

<｜｜DSML｜｜tool_calls>
<｜｜DSML｜｜invoke name="bash">
<｜｜DSML｜｜parameter name="command" string="true">cd /Users/arpanpathak/Projects/data_science/CrackGoogle/CodingInterviewFightClub && cat >> src/ch14-sorting/k-closest-points-to-origin.md <<'MDEOF'

## Dry run

**Input:** `points = [[3,3],[5,-1],[-2,4]]`, `k = 2`.

```
distances: [3,3]->18, [5,-1]->26, [-2,4]->20.  target index = k-1 = 1.

partition(0,2): random pivot [5,-1] (26):
  [3,3] 18 <= 26 -> left region.  [-2,4] 20 <= 26 -> left region.
  -> [[3,3],[-2,4],[5,-1]], pivotIndex = 2 > target 1 -> end = 1
partition(0,1): subarray [[3,3],[-2,4]]; random pivot [3,3] (18):
  [-2,4] 20 <= 18? NO -> stays to the right of the pivot.
  -> [[3,3],[-2,4],[5,-1]], pivotIndex = 0 < target 1 -> start = 1
  (start=1, end=1: loop ends)

return points[0..2) = [[3,3],[-2,4]] ✓
```

The subtle line is the second partition: `[-2,4]` (20) is *not* closer than the pivot (18), so it stays right — the pivot `[3,3]` lands at index 0, exactly the k-th position. The quickselect property holds: everything in `[0, k)` is ≤ everything in `[k, n)`, which is all "k closest" needs.

## Complexity

**Time.** Expected one-sided partitions:

$$
T(n) = O(n) \text{ average}, \quad O(n^2) \text{ worst}
$$

**Space.** In place:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Kth Largest Element** ([14.2](kth-largest-element.md)) — the identical loop; only the key (distance vs value) and target index differ.
- **Heap version** — a max-heap of size k keyed on distance: $O(n \log k)$ guaranteed; the "I want a worst-case bound" answer.
- **Sorting version** — `sortedBy(distance).take(k)`: $O(n \log n)$; the simplest correct answer and the baseline quickselect beats.
- **Interview follow-up:** "Why is squared distance safe?" Because `sqrt` is strictly increasing, `d1 < d2 ⟺ d1² < d2²` — comparisons are identical, and avoiding the sqrt keeps the computation in exact integers with no floating-point risk. State this before being asked.
