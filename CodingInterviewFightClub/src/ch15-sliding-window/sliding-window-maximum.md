# 15.6 Sliding Window Maximum

> **Source:** [`src/main/kotlin/sliding_window/SlidingWindowMaximum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sliding_window/SlidingWindowMaximum.kt)
> **Pattern:** monotonic deque · **Core page**

## The Problem

Given `nums` and a window size `k`, return an array of the **maximum of each** length-k window (windows overlap; `n - k + 1` outputs).

- Constraints: $1 \le k \le n \le 10^5$; values fit in `Int`.

## Examples

```
Input:  nums = [1,3,-1,-3,5,3,6,7], k = 3
Output: [3,3,5,5,6,7]
```

## Intuition — a deque of *candidates*, with the max always at the front

A fixed window needs "the max" per position. A heap is $O(\log k)$ per step; the **monotonic deque** makes it $O(1)$ amortized by maintaining *only the indices that could possibly be a future max*:

- **The deque holds indices with strictly decreasing values** — front is the current max.
- **Expiry:** before inserting, pop the front if it has left the window (`deque.first <= i - k`).
- **Dominance:** before inserting, pop the back while its value ≤ the new value — an older, smaller element can *never* be a max again while this bigger, newer one is in the window (it expires first *and* is smaller).

Then `nums[deque.first()]` is the window's max, reported once the window is full.

**Why pop the back at all?** A new element `nums[i]` stays in the window longer than anything behind it (it's the newest). If it's also ≥ them, every popped element is dominated on both axes (value and expiry) — keeping them would waste deque space and complicate the front. This is the [monotonic stack](../ch08-stacks/index.md) idea with an expiry rule added.

**Why store indices, not values?** Expiry is a *time* test (`i - k`); values don't carry their position. Index storage makes both the expiry check and the value comparison trivial (`nums[deque.last()]`).

## Approach 1 — Heap of the window (O(n log k))

Re-heapify per window or use a lazy-deletion heap (the [7.3](../ch07-heaps/sliding-window-median.md) machinery): correct, $O(n \log k)$.

## Approach 2 — Monotonic deque (the repo's version, optimal)

```kotlin
class SlidingWindowMaximum {
    /**
     * @param nums input array
     * @param k    window size
     * @return     max of each length-k window
     */
    fun maxSlidingWindow(nums: IntArray, k: Int): IntArray {
        if (nums.isEmpty()) return intArrayOf()

        val result = mutableListOf<Int>()
        val deque = ArrayDeque<Int>()              // indices, decreasing values

        nums.forEachIndexed { i, num ->
            // Remove elements outside the current window
            if (deque.isNotEmpty() && deque.first() <= i - k) deque.removeFirst()

            // Remove smaller elements from the back; keep max at front
            while (deque.isNotEmpty() && nums[deque.last()] <= num) {
                deque.removeLast()
            }
            deque.addLast(i)

            // Once the first k elements are processed, the front is the window's max
            if (i >= k - 1) result.add(nums[deque.first()])
        }
        return result.toIntArray()
    }
}
```

```java
import java.util.*;

public class SlidingWindowMaximum {
    /**
     * @param nums input array
     * @param k    window size
     * @return     max of each length-k window
     */
    public int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> deque = new ArrayDeque<>();   // indices, decreasing values
        int[] result = new int[nums.length - k + 1];
        int idx = 0;

        for (int i = 0; i < nums.length; i++) {
            while (!deque.isEmpty() && deque.peekFirst() <= i - k) deque.pollFirst();  // expiry
            while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) deque.pollLast();  // dominance
            deque.offerLast(i);

            if (i >= k - 1) result[idx++] = nums[deque.peekFirst()];   // window's max
        }
        return result;
    }
}
```

```cpp
#include <deque>
#include <vector>

class SlidingWindowMaximum {
public:
    /**
     * @param nums input array
     * @param k    window size
     * @return     max of each length-k window
     */
    std::vector<int> maxSlidingWindow(std::vector<int>& nums, int k) {
        std::deque<int> dq;                        // indices, decreasing values
        std::vector<int> result;

        for (int i = 0; i < (int)nums.size(); i++) {
            if (!dq.empty() && dq.front() <= i - k) dq.pop_front();    // expiry
            while (!dq.empty() && nums[dq.back()] <= nums[i]) dq.pop_back();  // dominance
            dq.push_back(i);

            if (i >= k - 1) result.push_back(nums[dq.front()]);        // window's max
        }
        return result;
    }
};
```

```python
from collections import deque

def max_sliding_window(nums: list[int], k: int) -> list[int]:
    """
    @param nums: input array
    @param k:    window size
    @return:     max of each length-k window
    """
    dq = deque()                       # indices, decreasing values
    result = []

    for i, num in enumerate(nums):
        if dq and dq[0] <= i - k:      # expiry: front left the window
            dq.popleft()
        while dq and nums[dq[-1]] <= num:   # dominance: older smaller can never be max
            dq.pop()
        dq.append(i)

        if i >= k - 1:
            result.append(nums[dq[0]])  # window's max
    return result
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param nums input array
    /// @param k    window size
    /// @return     max of each length-k window
    pub fn max_sliding_window(nums: Vec<i32>, k: i32) -> Vec<i32> {
        let k = k as usize;
        let mut dq: VecDeque<usize> = VecDeque::new();   // indices, decreasing values
        let mut result = Vec::new();

        for i in 0..nums.len() {
            if dq.front().map_or(false, |&f| f <= i.saturating_sub(k)) {
                dq.pop_front();                          // expiry
            }
            while dq.back().map_or(false, |&b| nums[b] <= nums[i]) {
                dq.pop_back();                           // dominance
            }
            dq.push_back(i);

            if i + 1 >= k {
                result.push(nums[dq[0]]);                // window's max
            }
        }
        result
    }
}
```

## Dry run

**Input:** `nums = [1,3,-1,-3,5,3,6,7]`, `k = 3`.

```
i=0 (1): dq=[] -> add 0.              dq=[0]
i=1 (3): 3 >= nums[0]=1 -> pop 0.  dq=[1]
i=2 (-1): -1 < nums[1]=3 -> keep.  add 2.   dq=[1,2].  full -> max = nums[1] = 3 ✓
i=3 (-3): add 3.  dq=[1,2,3].  full -> max = 3 ✓
i=4 (5): pop 3 (-3), pop 2 (-1), pop 1 (3) -> all <= 5.  dq=[4].  full -> max = 5 ✓
i=5 (3): 3 < 5 -> add.  dq=[4,5].  full -> max = 5 ✓
i=6 (6): pop 5 (3), pop 4 (5).  dq=[6].  full -> max = 6 ✓
i=7 (7): pop 6.  dq=[7].  full -> max = 7 ✓

Output: [3,3,5,5,6,7] ✓
```

The dominance pops at i=4 are the engine: `3`, `-1`, `-3` all die because the new `5` is both bigger and longer-lived — none of them can ever be a max again. Each element is pushed once and popped once, which is the $O(n)$ amortization.

## Complexity

**Time.** Each index pushed and popped once:

$$
T(n) = O(n)
$$

**Space.** The deque (≤ k entries):

$$
S(n) = O(k)
$$

## Variants & follow-ups

- **Longest Continuous Subarray With Absolute Difference <= Limit** (`src/main/kotlin/sliding_window/`) — needs both the window max *and* min → **two** monotonic deques, one for each extreme.
- **Sliding Window Median** ([7.3](../ch07-heaps/sliding-window-median.md)) — same window, different statistic (median): two heaps + lazy deletion instead of a deque.
- **Interview follow-up:** "Why do dominance pops preserve the answer?" An element is only popped when a *newer, larger-or-equal* element enters — that new element is in every window the old one could be in (it expires later) and is at least as large. So the popped element was never going to be reported as a window max; the deque only ever drops provably-useless candidates.
