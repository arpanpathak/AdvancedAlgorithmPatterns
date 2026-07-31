# 1.22 Closest Subsequence Sum

> **Source:** [`src/main/kotlin/binarysearch/ClosestSebsequenceSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/ClosestSebsequenceSum.kt)
> **Pattern:** meet-in-the-middle + binary search · **The gym boss**

## The Problem

Given an array `nums` (length up to 40, values may be negative) and a target `goal`, find the **minimum absolute difference** between `goal` and the sum of **any subsequence** of `nums` (the empty subsequence is allowed, sum 0).

- Constraints: $1 \le n \le 40$, $-10^7 \le nums[i] \le 10^7$, $-10^9 \le goal \le 10^9$.

## Examples

```
Input:  nums = [5, -7, 8], goal = 0
Output: 0
Explanation: 5 + (-7) + 8 = 6? no... subsequence {-7, 8} = 1, {5, -7} = -2,
             {5, -7, 8} = 6, {5, 8} = 13... but {5, -7, 8} sums to 6; {-7, 8} = 1;
             the empty set = 0 exactly! |0 - 0| = 0 ✓

Input:  nums = [1, 2, 3], goal = -7
Output: 7
Explanation: every subsequence sums to something in {0,1,2,3,3,4,5,6};
             the closest to -7 is 0 -> |−7 − 0| = 7.
```

## Intuition — why 40 is the magic number

There are $2^{40} \approx 10^{12}$ subsequences — enumerating all of them is impossible. But $2^{20} \approx 10^6$ is *very* doable. The trick: **split the array in half, enumerate both halves, and combine**.

Let $L$ = all subsequence sums of the left half ($2^{20}$ of them) and $R$ = the same for the right half. Every subsequence of `nums` is `l + r` for some $l \in L, r \in R$ (take any subsequence, split its chosen indices by half). So:

$$
\min_{l \in L, r \in R} |\,l + r - goal\,| = \min_{l \in L} \min_{r \in R} |\,r - (goal - l)\,|
$$

For a fixed $l$, the best $r$ is the one **closest to $(goal - l)$** — and if we sort $R$, "closest" is found by **binary search** for the insertion point of $(goal - l)$, checking the two neighbors (the candidate just below and just above). That's the whole algorithm:

1. Enumerate all $2^{n/2}$ sums of each half (DFS subset enumeration).
2. Sort the right half's sums.
3. For each left sum, binary search the right half for the best complement; track the global minimum.

**Why it's called meet-in-the-middle:** the exponential blowup is halved by meeting in the middle — $2^{n/2} + 2^{n/2} \log 2^{n/2}$ instead of $2^n$. For $n = 40$: $10^6 + 10^6 \times 20 \approx 2 \times 10^7$ operations versus $10^{12}$.

## Approach 1 — Enumerate everything

DFS all $2^n$ subsets, track the closest sum. $2^{40} = 10^{12}$ — a supercomputer's job. The whole point of the problem is avoiding this.

## Approach 2 — Meet in the middle + binary search (optimal)

```kotlin
/**
 * @param nums the array of (possibly negative) integers
 * @param goal the target sum to get close to
 * @return     the minimum possible |subsequenceSum - goal|
 */
fun minAbsDifference(nums: IntArray, goal: Int): Int {
    val leftSums = mutableListOf<Int>()
    val rightSums = mutableListOf<Int>()
    val n = nums.size
    val mid = n / 2

    /**
     * @param start       first index to consider (inclusive)
     * @param end         stopping index (exclusive)
     * @param currentSum  the running subsequence sum
     * @param result      the list that collects all subset sums of this half
     */
    fun dfs(start: Int, end: Int, currentSum: Int, result: MutableList<Int>) {
        if (start == end) {
            result.add(currentSum)
            return
        }
        dfs(start + 1, end, currentSum, result)                 // exclude nums[start]
        dfs(start + 1, end, currentSum + nums[start], result)   // include nums[start]
    }

    dfs(0, mid, 0, leftSums)
    dfs(mid, n, 0, rightSums)

    rightSums.sort()
    var minDiff = Int.MAX_VALUE

    for (sumLeft in leftSums) {
        val target = goal - sumLeft
        val idx = rightSums.binarySearch(target)
        if (idx >= 0) return 0                      // exact match -> can't do better

        val insertPoint = -idx - 1
        // The best r is one of the two neighbors of the insertion point.
        if (insertPoint < rightSums.size)
            minDiff = minOf(minDiff, abs(sumLeft + rightSums[insertPoint] - goal))
        if (insertPoint > 0)
            minDiff = minOf(minDiff, abs(sumLeft + rightSums[insertPoint - 1] - goal))
    }
    return minDiff
}
```

```java
import java.util.*;

public class ClosestSubsequenceSum {
    /**
     * @param nums the array of (possibly negative) integers
     * @param goal the target sum to get close to
     * @return     the minimum possible |subsequenceSum - goal|
     */
    public int minAbsDifference(int[] nums, int goal) {
        List<Integer> left = new ArrayList<>(), right = new ArrayList<>();
        int n = nums.length, mid = n / 2;
        dfs(nums, 0, mid, 0, left);
        dfs(nums, mid, n, 0, right);
        Collections.sort(right);

        int best = Integer.MAX_VALUE;
        for (int l : left) {
            int target = goal - l;
            int idx = Collections.binarySearch(right, target);
            if (idx >= 0) return 0;
            int insert = -idx - 1;
            if (insert < right.size()) best = Math.min(best, Math.abs(l + right.get(insert) - goal));
            if (insert > 0)            best = Math.min(best, Math.abs(l + right.get(insert - 1) - goal));
        }
        return best;
    }

    /**
     * @param nums    the input array
     * @param start   inclusive start index
     * @param end     exclusive end index
     * @param sum     running subsequence sum
     * @param result  collector for all subset sums of [start, end)
     */
    private void dfs(int[] nums, int start, int end, int sum, List<Integer> result) {
        if (start == end) { result.add(sum); return; }
        dfs(nums, start + 1, end, sum, result);
        dfs(nums, start + 1, end, sum + nums[start], result);
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <cstdlib>
#include <climits>

class ClosestSubsequenceSum {
public:
    /**
     * @param nums the array of (possibly negative) integers
     * @param goal the target sum to get close to
     * @return     the minimum possible |subsequenceSum - goal|
     */
    int minAbsDifference(const std::vector<int>& nums, int goal) {
        int n = (int)nums.size(), mid = n / 2;
        std::vector<long long> left, right;
        dfs(nums, 0, mid, 0, left);
        dfs(nums, mid, n, 0, right);
        std::sort(right.begin(), right.end());

        long long best = LLONG_MAX;
        for (long long l : left) {
            long long target = (long long)goal - l;
            auto it = std::lower_bound(right.begin(), right.end(), target);
            if (it != right.end()) best = std::min(best, std::llabs(l + *it - goal));
            if (it != right.begin()) best = std::min(best, std::llabs(l + *(it - 1) - goal));
        }
        return (int)best;
    }

private:
    /**
     * @param nums   the input array
     * @param start  inclusive start index
     * @param end    exclusive end index
     * @param sum    running subsequence sum
     * @param result collector for all subset sums of [start, end)
     */
    void dfs(const std::vector<int>& nums, int start, int end, long long sum, std::vector<long long>& result) {
        if (start == end) { result.push_back(sum); return; }
        dfs(nums, start + 1, end, sum, result);
        dfs(nums, start + 1, end, sum + nums[start], result);
    }
};
```

```python
from bisect import bisect_left

def min_abs_difference(nums: list[int], goal: int) -> int:
    """
    @param nums: the array of (possibly negative) integers
    @param goal: the target sum to get close to
    @return:     the minimum possible |subsequenceSum - goal|
    """
    n = len(nums)
    mid = n // 2

    def dfs(start: int, end: int, current: int, result: list[int]) -> None:
        """
        @param start:   inclusive start index
        @param end:     exclusive end index
        @param current: running subsequence sum
        @param result:  collector for all subset sums of [start, end)
        """
        if start == end:
            result.append(current)
            return
        dfs(start + 1, end, current, result)                 # exclude nums[start]
        dfs(start + 1, end, current + nums[start], result)   # include nums[start]

    left, right = [], []
    dfs(0, mid, 0, left)
    dfs(mid, n, 0, right)
    right.sort()

    best = float("inf")
    for l in left:
        target = goal - l
        idx = bisect_left(right, target)                     # insertion point
        if idx < len(right):
            best = min(best, abs(l + right[idx] - goal))     # candidate above
        if idx > 0:
            best = min(best, abs(l + right[idx - 1] - goal)) # candidate below
    return best
```

```rust
impl Solution {
    /// @param nums the array of (possibly negative) integers
    /// @param goal the target sum to get close to
    /// @return     the minimum possible |subsequenceSum - goal|
    pub fn min_abs_difference(nums: Vec<i32>, goal: i32) -> i32 {
        let n = nums.len();
        let mid = n / 2;
        let mut left = Vec::new();
        let mut right = Vec::new();

        fn dfs(nums: &[i32], start: usize, end: usize, current: i64, result: &mut Vec<i64>) {
            if start == end {
                result.push(current);
                return;
            }
            dfs(nums, start + 1, end, current, result);
            dfs(nums, start + 1, end, current + nums[start] as i64, result);
        }

        dfs(&nums, 0, mid, 0, &mut left);
        dfs(&nums, mid, n, 0, &mut right);
        right.sort_unstable();

        let mut best = i64::MAX;
        for &l in &left {
            let target = goal as i64 - l;
            match right.binary_search(&target) {
                Ok(_) => return 0,
                Err(insert) => {
                    if insert < right.len() {
                        best = best.min((l + right[insert] - goal as i64).abs());
                    }
                    if insert > 0 {
                        best = best.min((l + right[insert - 1] - goal as i64).abs());
                    }
                }
            }
        }
        best as i32
    }
}
```

## Dry run

**Input:** `nums = [5, -7, 8]`, `goal = 0`. Split at `mid = 1`: left half `[5]`, right half `[-7, 8]`.

**Step 1 — enumerate both halves (DFS order):**

```
left  (subsets of [5]):   exclude -> 0      include -> 5        => L = [0, 5]
right (subsets of [-7,8]): []->0, [-7]->-7, [8]->8, [-7,8]->1   => R = [0, -7, 8, 1]
```

**Step 2 — sort R:** `[-7, 0, 1, 8]`

**Step 3 — for each l, binary search for `goal - l`:**

| l | target = 0 − l | insertion point in R | candidates | best diff |
|---|---|---|---|---|
| 0 | 0 | exact hit (0) | 0 | **0 → return** |

The empty-left + exact-right hit finds `0` immediately. For an input without an exact hit, the two-neighbor check is what saves you — e.g. `l = 5` would search `target = -5` in R, land between `-7` and `0`, and check `|5 + (-7) - 0| = 2` and `|5 + 0 - 0| = 5`, taking the 2.

**Why checking only two neighbors is enough:** in a sorted list, the element minimizing $|r - t|$ is either the largest element ≤ t or the smallest element ≥ t — the two elements straddling the insertion point. Any element further away is *strictly* farther from t than one of these two (convexity of the absolute value on the line).

## Complexity

**Enumeration:** each half has $2^{n/2}$ subsets → $O(2^{n/2})$ time and space per half.

**Sorting R:** $O(2^{n/2} \log 2^{n/2})$.

**Query phase:** $|L| = 2^{n/2}$ binary searches, each $O(\log 2^{n/2})$:

$$
T(n) = O\!\left(2^{n/2} \log 2^{n/2}\right), \qquad S(n) = O\!\left(2^{n/2}\right)
$$

For $n = 40$: $\approx 10^6 \times 20 = 2 \times 10^7$ operations and ~$10^6$ integers of memory (~8 MB as `i64`) — comfortably feasible, where $2^{40}$ is not.

## Variants & follow-ups

- **Partition Equal Subset Sum / Subset Sum** (`src/main/kotlin/dynamic_programming/`) — the *counting/feasibility* cousin; when `n` is small but values are huge (no DP table possible), meet-in-the-middle is the tool.
- **Interview follow-up:** "Why can't DP solve this?" DP over sums needs a table of size $O(n \cdot \text{range})$, and with $\pm 10^7$ values the range is $2 \times 10^8$ per element — the table is astronomically large. Meet-in-the-middle is the *memory-bound* answer.
- **Interview follow-up:** "Improve the constant." Deduplicate both sum lists (many subsets produce equal sums), and early-exit on `0`. Both are one-liners that make the wall-clock time dramatically better on real inputs.
- **Interview follow-up:** "What if `n = 60`?" $2^{30} \approx 10^9$ — too big. You'd need a smarter structure (e.g. splitting into thirds, or pruning with branch-and-bound) — a good place to say *"the meet-in-the-middle tradeoff breaks; here's what I'd try next."*
