# 2.19 Longest Increasing Subsequence

> **Source:** [`src/main/kotlin/array/dp/LongestIncreasingSubsequence.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/LongestIncreasingSubsequence.kt) (+ `tree/bst/LongestIncreasingSubsequence.kt` — a TreeSet variant)
> **Pattern:** `dp[i]` = best ending at i · **Core page**

## The Problem

Given `nums`, return the length of the **longest strictly increasing subsequence** (not necessarily contiguous).

- Constraints: $1 \le n \le 2500$; values fit in `Int`.

## Examples

```
Input:  nums = [10,9,2,5,3,7,101,18]   -> Output: 4   ([2,3,7,101])
Input:  nums = [0,1,0,3,2,3]           -> Output: 4   ([0,1,2,3])
```

## Intuition — `dp[i]` = the longest increasing subsequence *ending at i*

The contiguous cousin ([2.18](maximum-subarray.md)) needs only the previous answer; LIS needs **every previous index** because an increasing subsequence may skip elements:

$$
dp[i] = 1 + \max_{j < i,\; nums[j] < nums[i]} dp[j]
$$

**Why scan all `j < i`?** The subsequence ending at `i` can extend any *earlier* subsequence whose last value is smaller — the best one isn't necessarily the immediately previous index. That's the O(n²) loop: for each `i`, scan `j in 0..i-1`, and if `nums[j] < nums[i]`, consider `dp[j] + 1`.

**The `dp` init to 1** — every element alone is a valid increasing subsequence of length 1; the `maxOrNull() ?: 1` on the return handles the size-1 array.

**The O(n log n) upgrade** — a "tails" array + binary search ([1.0](../ch01-binary-search/pattern-primer.md)'s theorem): `tails[k]` = the smallest tail of an increasing subsequence of length k. The TreeSet variant (`tree/bst/LongestIncreasingSubsequence.kt`) implements the same idea with a balanced tree. Mention it in the interview; implement the O(n²) first.

## Approach 1 — LCS trick (O(n^2) with more machinery)

Sort a copy and take the LCS with the original — correct but with duplicates issues; the direct DP is cleaner.

## Approach 2 — `dp[i]` over all previous (the repo's version)

```kotlin
class LongestIncreasingSubsequence {
    /**
     * @param nums input array
     * @return     length of the longest strictly increasing subsequence
     */
    fun lengthOfLIS(nums: IntArray): Int {
        if (nums.isEmpty()) return 0

        val dp = IntArray(nums.size) { 1 }     // every element is length-1 by itself

        for (i in 1 until nums.size) {
            for (j in 0 until i) {
                if (nums[i] > nums[j]) {       // can extend the subsequence ending at j
                    dp[i] = maxOf(dp[i], dp[j] + 1)
                }
            }
        }
        return dp.maxOrNull() ?: 1
    }
}
```

```java
public class LongestIncreasingSubsequence {
    /**
     * @param nums input array
     * @return     length of the longest strictly increasing subsequence
     */
    public int lengthOfLIS(int[] nums) {
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);                        // every element is length-1 by itself
        int best = 1;

        for (int i = 1; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) dp[i] = Math.max(dp[i], dp[j] + 1);
            }
            best = Math.max(best, dp[i]);
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class LongestIncreasingSubsequence {
public:
    /**
     * @param nums input array
     * @return     length of the longest strictly increasing subsequence
     */
    int lengthOfLIS(std::vector<int>& nums) {
        std::vector<int> dp(nums.size(), 1);       // every element is length-1 by itself
        int best = 1;

        for (int i = 1; i < (int)nums.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) dp[i] = std::max(dp[i], dp[j] + 1);
            }
            best = std::max(best, dp[i]);
        }
        return best;
    }
};
```

```python
def length_of_lis(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     length of the longest strictly increasing subsequence
    """
    dp = [1] * len(nums)             # every element is length-1 by itself

    for i in range(1, len(nums)):
        for j in range(i):
            if nums[i] > nums[j]:
                dp[i] = max(dp[i], dp[j] + 1)
    return max(dp)
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     length of the longest strictly increasing subsequence
    pub fn length_of_lis(nums: Vec<i32>) -> i32 {
        let mut dp = vec![1; nums.len()];    // every element is length-1 by itself

        for i in 1..nums.len() {
            for j in 0..i {
                if nums[i] > nums[j] {
                    dp[i] = dp[i].max(dp[j] + 1);
                }
            }
        }
        dp.into_iter().max().unwrap()
    }
}
```

## Dry run

**Input:** `nums = [10,9,2,5,3,7,101,18]`.

```
dp = [1,1,1,1,1,1,1,1]

i=1 (9):  j=0: 9 > 10? no -> dp[1]=1
i=2 (2):  j=0,1: 2 > 10/9? no -> dp[2]=1
i=3 (5):  j=2: 5 > 2 -> dp[3]=2
i=4 (3):  j=2: 3 > 2 -> dp[4]=2   (j=3: 3 > 5? no)
i=5 (7):  j=2: 7>2 -> 2;  j=3: 7>5 -> dp[3]+1=3;  j=4: 7>3 -> dp[4]+1=3.  dp[5]=3
i=6 (101): best extension = dp[5]+1 = 4.  dp[6]=4
i=7 (18): j=5: 18>7 -> dp[5]+1=4.  dp[7]=4

max(dp) = 4 ✓   ([2,3,7,101] or [2,5,7,101] or [2,3,7,18])
```

The all-previous scan is what makes it non-contiguous: at i=5 (7), the best extension is `dp[3]` (ending at 5, from `[2,5]`) or `dp[4]` (ending at 3, from `[2,3]`) — not the immediately-previous index. Every `j < i` with a smaller value is a candidate chain.

## Complexity

**Time.** All pairs:

$$
T(n) = O(n^2)
$$

**Space.** The dp array:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Russian Doll Envelopes** ([14.6](../ch14-sorting/russian-doll-envelopes.md)) — LIS in 2-D: sort by one dimension, LIS on the other.
- **Minimum Number Of Removals To Make Mountain Array** (`tree/bst/MinimumNumberOfRemovalsToMakeMountainArray.kt`) — LIS + LDS around a peak.
- **Longest Increasing Sequence In A Matrix** (`array/dp/LongestIncreasingSequenceInAMatrix.kt`) — the grid version: DFS + memo.
- **Interview follow-up:** "Can you do O(n log n)?" Yes — the *tails* array: `tails[k]` = smallest tail of a length-k subsequence; binary search the insertion point ([1.0](../ch01-binary-search/pattern-primer.md)). Same length, different bookkeeping; the TreeSet repo variant is that idea in a balanced tree.
