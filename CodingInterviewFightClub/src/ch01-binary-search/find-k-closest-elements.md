# 1.4 Find K Closest Elements

> **Source:** [`src/main/kotlin/binarysearch/FindKClosestElements.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/FindKClosestElements.kt)
> **Pattern:** binary search on the *start index* · **Core page** (the heap version is a famous follow-up)

## The Problem

Given a **sorted** array `arr`, an integer `k`, and an integer `x`, return the `k` elements of `arr` that are **closest** to `x` (by absolute difference), in **sorted order**.

Ties are broken toward the **smaller** value: if two candidates are equally close, prefer `arr[i]` over `arr[i + k]` (the left one).

- Constraints: $1 \le k \le n \le 10^4$, sorted ascending.

## Examples

```
Input:  arr = [1, 2, 3, 4, 5], k = 4, x = 3
Output: [1, 2, 3, 4]
Explanation: distances from 3: 1→2, 2→1, 3→0, 4→1, 5→2. The 4 smallest are 1,2,3,4.

Input:  arr = [1, 2, 3, 4, 5], k = 4, x = -1
Output: [1, 2, 3, 4]
Explanation: everything is to the right; the 4 smallest values are closest.

Input:  arr = [1, 1, 1, 10, 10, 10], k = 1, x = 9
Output: [10]
Explanation: |1−9| = 8 vs |10−9| = 1 → the single 10 is closest.
```

## Intuition — why binary search can find a *window start*

Most people reach for a heap or a sort. Both are correct and worth knowing (Approach 2 below), but there is a sharper structure hiding here: **the answer is always a contiguous window of `k` elements in the sorted array.**

Why? Suppose the answer contained `arr[i]` but not `arr[j]` with `i < j` while `j` lies strictly between `i` and the window's other members. Swapping would only move the window *toward* the cluster — formally, for any three sorted values $a \le b \le c$ we have $|b - x| \le \max(|a - x|, |c - x|)$, so an interior element can never be farther than an exterior one. Hence the optimal set is a contiguous block.

So the problem reduces to: **find the leftmost index `s` of the optimal window** `arr[s .. s+k)`. That's a search over `n - k + 1` possible starts — and the score of a window is **monotone enough** to binary search:

Let $W_s = \text{arr}[s..s+k)$. Moving the window right by one drops `arr[s]` and adds `arr[s+k]`. If `arr[s+k]` is *closer* to `x` than `arr[s]`, the window improves by shifting right; otherwise it doesn't. So the predicate "window starting at `s` is not worse than any window starting to its right" is monotone in `s`, and the standard halving finds the best start in $O(\log(n-k))$ window comparisons — each comparison $O(1)$. Total: **$O(\log(n-k))$**, beating the heap's $O(n \log k)$ and the sort's $O(n \log n)$.

## Approach 1 — Binary search on the window start (optimal)

```kotlin
/**
 * @param arr the sorted array to search in
 * @param k   the number of closest elements to return
 * @param x   the target value to be close to
 * @return    the k closest elements, in sorted order
 */
fun findClosestElements(arr: IntArray, k: Int, x: Int): List<Int> {
    // The optimal window starts somewhere in [0, arr.size - k].
    var (left, right) = 0 to arr.size - k

    while (left < right) {
        val mid = left + (right - left) / 2
        // Compare the left edge of window(mid) with the element just past its right edge.
        // If x - arr[mid] > arr[mid + k] - x, then arr[mid + k] is closer to x than
        // arr[mid] is, so shifting the window right improves it -> move left past mid.
        when {
            x - arr[mid] > arr[mid + k] - x -> left = mid + 1
            else                             -> right = mid
        }
    }
    return arr.toList().subList(left, left + k)
}
```

```java
import java.util.*;

public class FindKClosestElements {
    /**
     * @param arr the sorted array to search in
     * @param k   the number of closest elements to return
     * @param x   the target value to be close to
     * @return    the k closest elements, in sorted order
     */
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        int left = 0, right = arr.length - k;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (x - arr[mid] > arr[mid + k] - x) {
                left = mid + 1;          // right edge is closer -> shift window right
            } else {
                right = mid;             // keep window at or left of mid
            }
        }
        List<Integer> result = new ArrayList<>();
        for (int i = left; i < left + k; i++) result.add(arr[i]);
        return result;
    }
}
```

```cpp
#include <vector>

class FindKClosestElements {
public:
    /**
     * @param arr the sorted array to search in
     * @param k   the number of closest elements to return
     * @param x   the target value to be close to
     * @return    the k closest elements, in sorted order
     */
    std::vector<int> findClosestElements(const std::vector<int>& arr, int k, int x) {
        int left = 0, right = (int)arr.size() - k;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (x - arr[mid] > arr[mid + k] - x) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return std::vector<int>(arr.begin() + left, arr.begin() + left + k);
    }
};
```

```python
def find_closest_elements(arr: list[int], k: int, x: int) -> list[int]:
    """
    @param arr: the sorted array to search in
    @param k:   the number of closest elements to return
    @param x:   the target value to be close to
    @return:    the k closest elements, in sorted order
    """
    left, right = 0, len(arr) - k
    while left < right:
        mid = left + (right - left) // 2
        # arr[mid + k] closer to x than arr[mid]? Then shift the window right.
        if x - arr[mid] > arr[mid + k] - x:
            left = mid + 1
        else:
            right = mid
    return arr[left:left + k]
```

```rust
impl Solution {
    /// @param arr the sorted array to search in
    /// @param k   the number of closest elements to return
    /// @param x   the target value to be close to
    /// @return    the k closest elements, in sorted order
    pub fn find_closest_elements(arr: Vec<i32>, k: i32, x: i32) -> Vec<i32> {
        let k = k as usize;
        let (mut left, mut right) = (0usize, arr.len() - k);
        while left < right {
            let mid = left + (right - left) / 2;
            if x - arr[mid] > arr[mid + k] - x {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        arr[left..left + k].to_vec()
    }
}
```

## Approach 2 — Max-heap of size k (the classic follow-up)

Works on **unsorted** arrays too. Keep a heap of the *k best so far*; when it exceeds k, evict the worst. "Worst" = largest distance, ties broken toward the larger value (so that the smaller value survives ties — exactly the tie rule).

```kotlin
/**
 * @param arr the array (sorted or not) to search in
 * @param k   the number of closest elements to return
 * @param x   the target value to be close to
 * @return    the k closest elements, in sorted order
 */
fun findClosestElementsHeap(arr: IntArray, k: Int, x: Int): List<Int> {
    // Max-heap on (distance from x, value): comparator returns positive when b is "bigger" = worse.
    val priorityQueue = PriorityQueue<Int> { a, b ->
        val diffA = abs(a - x)
        val diffB = abs(b - x)
        if (diffA == diffB) b - a else diffB - diffA
    }
    for (element in arr) {
        priorityQueue.offer(element)
        if (priorityQueue.size > k) priorityQueue.poll()   // evict the worst
    }
    return priorityQueue.sorted()
}
```

```java
import java.util.*;

public class FindKClosestElementsHeap {
    /**
     * @param arr the array (sorted or not) to search in
     * @param k   the number of closest elements to return
     * @param x   the target value to be close to
     * @return    the k closest elements, in sorted order
     */
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> {
            int da = Math.abs(a - x), db = Math.abs(b - x);
            return da == db ? b - a : db - da;   // max-heap on (distance, value)
        });
        for (int element : arr) {
            pq.offer(element);
            if (pq.size() > k) pq.poll();
        }
        List<Integer> result = new ArrayList<>(pq);
        Collections.sort(result);
        return result;
    }
}
```

```cpp
#include <vector>
#include <queue>
#include <algorithm>

class FindKClosestElementsHeap {
public:
    /**
     * @param arr the array (sorted or not) to search in
     * @param k   the number of closest elements to return
     * @param x   the target value to be close to
     * @return    the k closest elements, in sorted order
     */
    std::vector<int> findClosestElements(const std::vector<int>& arr, int k, int x) {
        auto worse = [x](int a, int b) {
            int da = std::abs(a - x), db = std::abs(b - x);
            return da == db ? a < b : da < db;   // max-heap: "less" means "worse" on top
        };
        std::priority_queue<int, std::vector<int>, decltype(worse)> pq(worse);
        for (int element : arr) {
            pq.push(element);
            if ((int)pq.size() > k) pq.pop();
        }
        std::vector<int> result;
        while (!pq.empty()) { result.push_back(pq.top()); pq.pop(); }
        std::sort(result.begin(), result.end());
        return result;
    }
};
```

```python
def find_closest_elements_heap(arr: list[int], k: int, x: int) -> list[int]:
    """
    @param arr: the array (sorted or not) to search in
    @param k:   the number of closest elements to return
    @param x:   the target value to be close to
    @return:    the k closest elements, in sorted order
    """
    import heapq
    heap: list[tuple[int, int]] = []            # max-heap keyed by (-distance, -value)
    for element in arr:
        heapq.heappush(heap, (-abs(element - x), -element))
        if len(heap) > k:
            heapq.heappop(heap)                 # evict the worst
    return sorted(-neg_value for _, neg_value in heap)
```

```rust
use std::collections::BinaryHeap;
use std::cmp::Ordering;

impl Solution {
    /// @param arr the array (sorted or not) to search in
    /// @param k   the number of closest elements to return
    /// @param x   the target value to be close to
    /// @return    the k closest elements, in sorted order
    pub fn find_closest_elements_heap(arr: Vec<i32>, k: i32, x: i32) -> Vec<i32> {
        // Reverse wrapper: BinaryHeap pops the LARGEST (distance, value), which is the worst.
        #[derive(Eq, PartialEq)]
        struct Elem(i32, i32);
        impl Ord for Elem {
            fn cmp(&self, other: &Self) -> Ordering {
                (self.0, self.1).cmp(&(other.0, other.1))
            }
        }
        impl PartialOrd for Elem { fn partial_cmp(&self, other: &Self) -> Option<Ordering> { Some(self.cmp(other)) } }

        let mut heap = BinaryHeap::new();
        for &element in &arr {
            heap.push(Elem((element - x).abs(), element));
            if heap.len() > k as usize {
                heap.pop();
            }
        }
        let mut result: Vec<i32> = heap.into_iter().map(|e| e.1).collect();
        result.sort_unstable();
        result
    }
}
```

**Heap complexity:** $O(n \log k)$ time ($n$ pushes, at most $n$ pops), $O(k)$ space. **Binary search complexity:** $O(\log(n-k))$ time, $O(1)$ extra space (plus the $O(k)$ output). When `arr` is sorted — as the problem guarantees — the binary search is strictly better; when `arr` isn't sorted, the heap is the weapon.

## Dry run (binary search approach)

**Input:** `arr = [1, 2, 3, 4, 5]`, `k = 4`, `x = 3`. Start window index space: $[0, 1]$ (only 2 possible starts):

```
left=0  right=1  mid=0
  x - arr[0] = 3 - 1 = 2
  arr[0+4] - x = arr[4] - 3 = 5 - 3 = 2
  2 > 2 is FALSE  -> right = 0
left=0  right=0  -> return arr[0..4] = [1, 2, 3, 4] ✓
```

Tie rule in action: window `[1,2,3,4]` (distances 2,1,0,1) ties window `[2,3,4,5]` (distances 1,0,1,2) on total distance; the tie-break prefers the smaller left edge, which is exactly what the `>` (strict) comparison in the code does.

**Input:** `arr = [1, 2, 3, 4, 5]`, `k = 4`, `x = -1`. Window start space $[0, 1]$:

```
left=0  right=1  mid=0
  x - arr[0] = -1 - 1 = -2
  arr[4] - x = 5 - (-1) = 6
  -2 > 6 is FALSE -> right = 0
return [1, 2, 3, 4] ✓   (leftmost window wins)
```

**Input:** `arr = [1,1,1,10,10,10]`, `k = 1`, `x = 9`. Window start space $[0, 5]$:

```
left=0  right=5  mid=2
  x - arr[2] = 9 - 1 = 8
  arr[3] - x = 10 - 9 = 1
  8 > 1 TRUE -> left = 3
left=3  right=5  mid=4
  x - arr[4] = 9 - 10 = -1
  arr[5] - x = 10 - 9 = 1
  -1 > 1 FALSE -> right = 4
left=3  right=4  mid=3
  x - arr[3] = 9 - 10 = -1
  arr[4] - x = 10 - 9 = 1
  -1 > 1 FALSE -> right = 3
return arr[3..4] = [10] ✓
```

## Complexity

**Binary search approach.** The window-start space has $n - k + 1$ candidates; halving needs $\lceil \log_2(n-k+1) \rceil$ comparisons, each $O(1)$:

$$
T(n) = O(\log(n - k)), \qquad S(n) = O(k) \text{ (the output)}
$$

**Heap approach.** Each of the $n$ elements is pushed once ($O(\log k)$) and popped at most once ($O(\log k)$):

$$
T(n) = O(n \log k), \qquad S(n) = O(k)
$$

## Variants & follow-ups

- **K Closest Points to Origin** (`src/main/kotlin/heap/`) — same heap pattern, Euclidean distance instead of absolute difference; "sorted" doesn't apply, so the heap (or quickselect) is the answer.
- **Interview follow-up:** "Prove the answer is a contiguous window." Use the ordering lemma: for sorted $a \le b \le c$ and any $x$, $|b - x| \le \max(|a - x|, |c - x|)$ — an interior element can't be the farthest. Therefore the optimal $k$-set has no "holes".
- **Interview follow-up:** "Why `>` and not `>=`?" The strict comparison sends equal-distance cases to `right = mid`, biasing the search left — exactly the required smaller-value tie-break.
