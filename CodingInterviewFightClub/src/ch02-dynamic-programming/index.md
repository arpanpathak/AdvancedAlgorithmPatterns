# Chapter 2 — Dynamic Programming

> **Source:** `src/main/kotlin/dynamic_programming/`, `src/main/kotlin/array/dp/`, `src/main/kotlin/graph/dp/` (and friends)
>
> **Master idea:** DP is *organized recursion* — the same subproblems, computed once, reused many times. Every DP in this book is: **state → recurrence → base case → answer**, and the "aha" is always discovering the right state.
>
> **Prerequisites:** the recurrence math in [Reference §4](../reference/complexity.md).

## Problems at a glance

| # | Problem | State & recurrence essence | Complexity | Page |
|---|---------|---------------------------|------------|------|
| 2.1 | Longest Common Substring | `dp[i][j]` = length of common substring *ending* at `(i,j)` | $O(mn)$ | [→](longest-common-substring.md) |
| 2.2 | Minimum Edit Distance | `dp[i][j]` = edit distance of prefixes; min of insert/delete/replace | $O(mn)$ | [→](minimum-edit-distance.md) |
| 2.3 | Longest Common Subsequence | `dp[i][j]` = LCS of prefixes; match or skip | $O(mn)$ | [→](longest-common-subsequence.md) |
| 2.4 | 0/1 Knapsack | `dp[c]` = max value with capacity `c`; pick or skip | $O(nW)$ | [→](zero-one-knapsack.md) |
| 2.5 | Unbounded Knapsack | same, but `dp[c]` reuses the current item | $O(nW)$ | [→](unbounded-knapsack.md) |
| 2.6 | Partition Equal Subset Sum | subset-sum reachability; `dp[s]` boolean | $O(nS)$ | [→](partition-equal-subset-sum.md) |
| 2.7 | Maximum Product Subarray | track max AND min (sign flips!) | $O(n)$ | [→](maximum-product-subarray.md) |
| 2.8 | Frog Jump | set of reachable jumps per stone | $O(n^2)$ | [→](frog-jump.md) |
| 2.9 | Super Egg Drop | `dp[k][m]` = floors coverable with k eggs, m moves | $O(k \log f)$ | [→](super-egg-drop.md) |
| 2.10 | Minimum Cost To Cut A Stick | interval DP: `dp[i][j]` over sorted cut points | $O(n^3)$ | [→](minimum-cost-to-cut-a-stick.md) |
| 2.11 | Minimum Cost To Merge Stones | interval DP with K-way grouping | $O(n^3)$ | [→](minimum-cost-to-merge-stones.md) |
| 2.12 | Closest Subsequence Sum | meet-in-the-middle (see [1.22](../ch01-binary-search/closest-subsequence-sum.md)) | $O(2^{n/2} \log 2^{n/2})$ | [→](../ch01-binary-search/closest-subsequence-sum.md) |
| 2.13 | Maximum Profit In Job Scheduling | sort + `dp[i]` = max profit up to job i | $O(n \log n)$ | [→](maximum-profit-in-job-scheduling.md) |

| 2.14 | Count Ways To Pick K Coins Divisible By M | memoized (idx, k, rem) | $O(nkm)$ | [→](count-ways-to-pick-k-coins-divisible-by-m.md) |
| 2.15 | Maximal Square | min-of-three DP | $O(mn)$ | [→](maximal-square.md) |
| 2.16 | Coin Change | unbounded-knapsack minimization | $O(AC)$ | [→](coin-change.md) |
| 2.17 | House Robber | include/exclude two-variable DP | $O(n)$ | [→](house-robber.md) |
| 2.18 | Maximum Subarray | Kadane best-ending-here | $O(n)$ | [→](maximum-subarray.md) |
| 2.19 | Longest Increasing Subsequence | dp over all previous | $O(n^2)$ | [→](longest-increasing-subsequence.md) |
| 2.20 | Burst Balloons | interval DP + sentinels | $O(n^3)$ | [→](burst-balloons.md) |
| 2.21 | Target Sum | (index, sum) memo | $O(nS)$ | [→](target-sum.md) |
| 2.22 | Interleaving String | (i, j) matching memo | $O(mn)$ | [→](interleaving-string.md) |
| 2.23 | Regular Expression Matching | (i, j) memo with `*` | $O(mn)$ | [→](regular-expression-matching.md) |
| 2.24 | Delete Operations For Two Strings | LCS → deletions | $O(mn)$ | [→](delete-operations-for-two-strings.md) |
| 2.25 | Cherry Pickup | two-walker DP | $O(n^3)$ | [→](cherry-pickup.md) |
| 2.26 | Racecar | (pos, speed) DFS | $O(win·sp)$ | [→](racecar.md) |
| 2.27 | Min Taps To Water Garden | interval covering greedy | $O(n)$ | [→](minimum-number-of-taps.md) |
| 2.28 | Shortest Common Supersequence | LCS + backtrace | $O(mn)$ | [→](shortest-common-supersequence.md) |
| 2.29 | Min Cost Climbing Stairs | two-step DP | $O(n)$ | [→](min-cost-climbing-stairs.md) |
| 2.30 | Coin Change II | unbounded-knapsack count | $O(ca)$ | [→](coin-change-ii.md) |
| 2.31 | Unique Paths | grid DP / combinatorics | $O(mn)$ | [→](unique-paths.md) |
| 2.32 | Unique Paths II | obstacle-zeroed DP | $O(mn)$ | [→](unique-paths-ii.md) |
| 2.33 | Palindrome Partitioning II | palindrome table + min cuts | $O(n^2)$ | [→](palindrome-partitioning-ii.md) |
| 2.34 | Valid Palindrome III | LPS DP | $O(n^2)$ | [→](valid-palindrome-iii.md) |
| 2.35 | Longest Palindromic Subsequence | LPS memo / LCS(s, rev) | $O(n^2)$ | [→](longest-palindromic-subsequence.md) |
## Reading order

2.2 and 2.3 first — they're the *ur-examples* of the state-shape `dp[i][j]`. Then 2.1 (same table, different recurrence), then the knapsack family (2.4–2.6) which is the most frequently re-appearing pattern in real interviews, then the interval DPs (2.10, 2.11), then the gyms (2.8, 2.9, 2.13). End with 2.12 which is the *anti-DP* — it proves you know when **not** to reach for a DP table.
