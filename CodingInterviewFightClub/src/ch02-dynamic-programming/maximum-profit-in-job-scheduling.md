# 2.13 Maximum Profit In Job Scheduling

> **Source:** [`src/main/kotlin/dynamic_programming/MaximumProfitInJobScheduling.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/dynamic_programming/MaximumProfitInJobScheduling.kt)
> **Pattern:** sort + binary-search DP · **Gym page — DP meets this book's Chapter 1**

## The Problem

You have `n` jobs, each with a `startTime`, an `endTime`, and a `profit`. You can schedule **non-overlapping** jobs (a job's start must be ≥ the previous job's end) to maximize total profit. Return the max profit.

- Constraints: $1 \le n \le 5 \times 10^4$.

## Examples

```
startTime = [1, 2, 3, 3], endTime = [3, 4, 5, 6], profit = [50, 10, 40, 70]
Output: 120
Explanation: job 1 (1→3, 50) + job 3 (3→5, 40) + job 4 (3→6, 70)? No — jobs 3 and 4 both start at 3.
             Best: job 1 (50) + job 4 (70) = 120 (1→3 then 3→6). Or job 1 + job 3 = 90.
             Actually job 1 (50) + job 3 (40) + ... job 3 ends at 5, nothing after. 120 it is.

startTime = [1, 2, 3, 4, 6], endTime = [3, 5, 10, 6, 9], profit = [20, 20, 100, 70, 60]
Output: 150
Explanation: job 4 (4→6, 70) + job 5 (6→9, 60) = 130; job 2 (2→5,20) + job 5 = 80...
             job 1 (20) + job 2 (20) + job 5 (60) = 100; job 3 alone = 100;
             job 2 (2→5, 20) + job 4 (4→6, 70)? overlap. 150 = job 1 (20) + job 4 (70) + job 5 (60) = 150 ✓
```

## Intuition — "skip or take" with a binary search for the predecessor

This is a *weighted* interval scheduling — the classic DP that every real "resource scheduling" system uses. The recipe:

1. **Sort jobs by end time.** Now "consider the first i jobs" is a meaningful prefix, and "the last job that doesn't overlap job i" is a **contiguous prefix** of the sorted list (all jobs with `end <= start_i` come before some point).
2. **DP on the prefix:**

$$
dp[i] = \max\!\big(\underbrace{dp[i-1]}_{\text{skip job } i}, \underbrace{profit_i + dp[p(i)]}_{\text{take job } i}\big)
$$

where $p(i)$ = index of the **last job before i with `end <= start_i`** — found by **binary search** on the sorted-by-end list (Chapter 1's lower-bound machinery from [1.0](../ch01-binary-search/pattern-primer.md)).

**Why $dp[p(i)]$ and not "the best non-overlapping anything"?** Any valid schedule ending before `start_i` is, by the end-time ordering, a schedule among the first `p(i)` jobs — and the best such schedule is exactly `dp[p(i)]` by induction. So "take job i" reduces to "best schedule that finishes before job i starts" — one binary search + one lookup.

**Why sorting by end (not start)?** The prefix property "jobs ≤ some point all end before a deadline" is only true in end-time order. Sorting by start breaks it (a late-starting job can end early).

## Approach 1 — Brute force

Try all $2^n$ subsets, check pairwise non-overlap. $2^{50000}$ — the prompt itself is the joke.

## Approach 2 — Sort + binary-search DP (optimal)

```kotlin
/**
 * @param startTime the start time of each job
 * @param endTime   the end time of each job
 * @param profit    the profit of each job
 * @return          the maximum profit from non-overlapping jobs
 */
fun jobScheduling(startTime: IntArray, endTime: IntArray, profit: IntArray): Int {
    // Bundle jobs and sort by END time — this gives the "prefix" structure.
    val jobs = startTime.indices
        .map { Job(startTime[it], endTime[it], profit[it]) }
        .sortedBy { it.end }

    val dp = IntArray(jobs.size)
    dp[0] = jobs[0].profit

    for (i in 1 until jobs.size) {
        dp[i] = dp[i - 1]                       // skip job i

        val prevJobIndex = findLastNonOverlappingJob(jobs, i)   // binary search
        val currentProfit = jobs[i].profit + if (prevJobIndex != -1) dp[prevJobIndex] else 0

        dp[i] = maxOf(dp[i], currentProfit)     // take job i (if better)
    }
    return dp.last()
}

/**
 * @param jobs        the jobs sorted by end time
 * @param currentIndex the index of the job being scheduled
 * @return            the index of the last job with end <= jobs[currentIndex].start, or -1
 */
private fun findLastNonOverlappingJob(jobs: List<Job>, currentIndex: Int): Int {
    val currentJob = jobs[currentIndex]
    var low = 0
    var high = currentIndex - 1
    var best = -1

    while (low <= high) {                     // Template: last index with jobs[mid].end <= start
        val mid = (low + high) / 2
        if (jobs[mid].end <= currentJob.start) {
            best = mid
            low = mid + 1                     // candidate works; try a later one
        } else {
            high = mid - 1
        }
    }
    return best
}
```

```java
import java.util.*;

public class MaximumProfitInJobScheduling {
    /**
     * @param startTime the start time of each job
     * @param endTime   the end time of each job
     * @param profit    the profit of each job
     * @return          the maximum profit from non-overlapping jobs
     */
    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) jobs[i] = new int[]{startTime[i], endTime[i], profit[i]};
        Arrays.sort(jobs, (a, b) -> a[1] - b[1]);        // sort by end time

        int[] dp = new int[n];
        dp[0] = jobs[0][2];
        for (int i = 1; i < n; i++) {
            dp[i] = dp[i - 1];                            // skip job i
            int prev = findLastNonOverlapping(jobs, i);   // binary search
            dp[i] = Math.max(dp[i], jobs[i][2] + (prev == -1 ? 0 : dp[prev]));
        }
        return dp[n - 1];
    }

    /**
     * @param jobs the jobs sorted by end time
     * @param i    the index of the job being scheduled
     * @return     the last index with jobs[idx].end <= jobs[i].start, or -1
     */
    private int findLastNonOverlapping(int[][] jobs, int i) {
        int lo = 0, hi = i - 1, best = -1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            if (jobs[mid][1] <= jobs[i][0]) { best = mid; lo = mid + 1; }
            else hi = mid - 1;
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MaximumProfitInJobScheduling {
public:
    /**
     * @param startTime the start time of each job
     * @param endTime   the end time of each job
     * @param profit    the profit of each job
     * @return          the maximum profit from non-overlapping jobs
     */
    int jobScheduling(const std::vector<int>& startTime,
                      const std::vector<int>& endTime,
                      const std::vector<int>& profit) {
        int n = (int)startTime.size();
        std::vector<std::array<int, 3>> jobs(n);
        for (int i = 0; i < n; i++) jobs[i] = {startTime[i], endTime[i], profit[i]};
        std::sort(jobs.begin(), jobs.end(), [](const auto& a, const auto& b) { return a[1] < b[1]; });

        std::vector<int> dp(n);
        dp[0] = jobs[0][2];
        for (int i = 1; i < n; i++) {
            dp[i] = dp[i - 1];                              // skip job i
            int prev = findLastNonOverlapping(jobs, i);     // binary search
            dp[i] = std::max(dp[i], jobs[i][2] + (prev == -1 ? 0 : dp[prev]));
        }
        return dp[n - 1];
    }

private:
    /**
     * @param jobs the jobs sorted by end time
     * @param i    the index of the job being scheduled
     * @return     the last index with jobs[idx][1] <= jobs[i][0], or -1
     */
    int findLastNonOverlapping(const std::vector<std::array<int, 3>>& jobs, int i) {
        int lo = 0, hi = i - 1, best = -1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            if (jobs[mid][1] <= jobs[i][0]) { best = mid; lo = mid + 1; }
            else hi = mid - 1;
        }
        return best;
    }
};
```

```python
def job_scheduling(start_time: list[int], end_time: list[int], profit: list[int]) -> int:
    """
    @param start_time: the start time of each job
    @param end_time:   the end time of each job
    @param profit:     the profit of each job
    @return:           the maximum profit from non-overlapping jobs
    """
    jobs = sorted(zip(start_time, end_time, profit), key=lambda j: j[1])   # sort by end
    n = len(jobs)
    dp = [0] * n
    dp[0] = jobs[0][2]

    def last_non_overlapping(i: int) -> int:
        """
        @param i: the index of the job being scheduled
        @return:  the last index with jobs[idx][1] <= jobs[i][0], or -1
        """
        lo, hi, best = 0, i - 1, -1
        while lo <= hi:
            mid = (lo + hi) // 2
            if jobs[mid][1] <= jobs[i][0]:
                best = mid
                lo = mid + 1
            else:
                hi = mid - 1
        return best

    for i in range(1, n):
        dp[i] = dp[i - 1]                                   # skip job i
        prev = last_non_overlapping(i)
        dp[i] = max(dp[i], jobs[i][2] + (dp[prev] if prev != -1 else 0))
    return dp[-1]
```

```rust
impl Solution {
    /// @param start_time the start time of each job
    /// @param end_time   the end time of each job
    /// @param profit     the profit of each job
    /// @return           the maximum profit from non-overlapping jobs
    pub fn job_scheduling(start_time: Vec<i32>, end_time: Vec<i32>, profit: Vec<i32>) -> i32 {
        let n = start_time.len();
        let mut jobs: Vec<(i32, i32, i32)> = (0..n).map(|i| (start_time[i], end_time[i], profit[i])).collect();
        jobs.sort_by_key(|j| j.1);                           // sort by end time

        let mut dp = vec![0i32; n];
        dp[0] = jobs[0].2;
        for i in 1..n {
            dp[i] = dp[i - 1];                               // skip job i
            // binary search: last job with end <= start of job i
            let (mut lo, mut hi, mut best) = (0usize, i, usize::MAX);
            while lo < hi {
                let mid = (lo + hi) / 2;
                if jobs[mid].1 <= jobs[i].0 {
                    best = mid;
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
            let prev = if best == usize::MAX { 0 } else { dp[best] };
            dp[i] = dp[i].max(jobs[i].2 + prev);
        }
        dp[n - 1]
    }
}
```

## Dry run

**Input:** `startTime = [1,2,3,3]`, `endTime = [3,4,5,6]`, `profit = [50,10,40,70]`.

**Sort by end:** jobs become `[1→3:50]`, `[2→4:10]`, `[3→5:40]`, `[3→6:70]`.

```
i=0: dp[0] = 50                                        (take job 0)
i=1: skip -> dp[1] = 50
     findLastNonOverlapping(1): jobs[0].end=3 <= 2? NO -> best=-1
     take -> 10 + 0 = 10. dp[1] = max(50, 10) = 50
i=2: skip -> dp[2] = 50
     findLastNonOverlapping(2): jobs[1].end=4 <= 3? NO; jobs[0].end=3 <= 3? YES -> best=0
     take -> 40 + dp[0] = 40 + 50 = 90. dp[2] = max(50, 90) = 90
i=3: skip -> dp[3] = 90
     findLastNonOverlapping(3): jobs[2].end=5<=3?NO; jobs[1].end=4<=3?NO; jobs[0].end=3<=3?YES -> best=0
     take -> 70 + dp[0] = 70 + 50 = 120. dp[3] = max(90, 120) = 120
Answer: 120 ✓  (job 0 then job 3: [1→3] + [3→6])
```

The binary search at `i=3` found `jobs[0]` (end 3 ≤ start 3 — sharing an instant is allowed) in 2 probes instead of scanning 3 jobs. With $n = 5 \times 10^4$, that's the difference between $O(n^2)$ (2.5 billion) and $O(n \log n)$ (~800k).

## Complexity

**Time.** Sorting $O(n \log n)$ + one binary search per job:

$$
T(n) = O(n \log n)
$$

**Space.** $O(n)$ (jobs + dp).

## Variants & follow-ups

- **Weighted interval scheduling** — this exact problem is the textbook example; the unweighted version is greedy (earliest finish time), the weighted one *needs* DP. Saying why (weight makes "more jobs" ≠ "more value") is a classic probe.
- **Interview follow-up:** "What if jobs can share boundaries (end == start)?" The code already allows it (`end <= start`); if instead they couldn't, flip to `end < start` — one character.
- **Interview follow-up:** "Reconstruct the schedule." Store the *choice*: if `dp[i]` came from `take`, walk back via `p(i)`; else via `i-1`. $O(n)$ reconstruction after the table.
