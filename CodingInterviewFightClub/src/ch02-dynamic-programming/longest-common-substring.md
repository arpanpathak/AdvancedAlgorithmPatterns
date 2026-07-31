# 2.1 Longest Common Substring

> **Source:** [`src/main/kotlin/array/dp/LongestCommonSubarray.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/array/dp/LongestCommonSubarray.kt)
> **Pattern:** 2D DP on string prefixes · **Core page**

## The Problem

Given two strings (arrays) `s1` of length $m$ and `s2` of length $n$, return the length of the longest **contiguous** substring common to both.

- Constraints: $1 \le m, n \le 10^3$.

## Examples

```
s1 = "abcde", s2 = "abfce"   -> 2   ("ab")
s1 = "abcd",  s2 = "bc"      -> 2   ("bc")
s1 = "abc",   s2 = "def"     -> 0
```

## Intuition — the state must remember *continuity*

Contiguity is the whole difficulty. If we used the LCS state ("best common subsequence of the first i / first j characters"), a *discontiguous* match would be allowed — wrong. The fix is a sharper state:

$$
dp[i][j] = \text{length of the longest common substring that ENDS exactly at } s1[i-1] \text{ and } s2[j-1]
$$

With "ends exactly at", the recurrence is brutally simple:

$$
dp[i][j] =
\begin{cases}
dp[i-1][j-1] + 1 & s1[i-1] = s2[j-1] \\[1mm]
0 & \text{otherwise}
\end{cases}
$$

If the last characters match, extend the diagonal streak by 1 (the previous common substring *ending* at `(i-1, j-1)` can be extended); if they don't, no common substring can end here — reset to 0. The answer is the **max over all cells**:

$$
\text{answer} = \max_{i,j} dp[i][j]
$$

**Why the max?** The best common substring could end anywhere in both strings; every cell records the streak ending at that pair, so the global maximum over the table is the longest contiguous match.

## Approach 1 — Brute force

For each of the $O(m^2)$ substrings of `s1`, check membership in `s2`: $O(m^2 n)$ (or $O(m^2)$ with a suffix automaton — overkill here). Exponential-ish blowup in practice; the DP is the expected answer.

## Approach 2 — Bottom-up DP (optimal)

```kotlin
/**
 * @param s1 the first string (or array)
 * @param s2 the second string (or array)
 * @return   the length of the longest contiguous substring common to both
 */
fun longestCommonSubstring(s1: String, s2: String): Int {
    val m = s1.length
    val n = s2.length
    val dp = Array(m + 1) { IntArray(n + 1) }   // row 0 and col 0 are the empty-string padding
    var maxLen = 0

    for (i in 1..m) {
        for (j in 1..n) {
            if (s1[i - 1] == s2[j - 1]) {
                // Extend the diagonal streak: the substring ending at (i-1, j-1) + this char.
                dp[i][j] = dp[i - 1][j - 1] + 1
                maxLen = maxOf(maxLen, dp[i][j])
            } else {
                // Characters differ -> no common substring can END here. Reset.
                dp[i][j] = 0
            }
        }
    }
    return maxLen
}
```

```java
public class LongestCommonSubstring {
    /**
     * @param s1 the first string (or array)
     * @param s2 the second string (or array)
     * @return   the length of the longest contiguous substring common to both
     */
    public int longestCommonSubstring(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        int maxLen = 0;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    maxLen = Math.max(maxLen, dp[i][j]);
                } else {
                    dp[i][j] = 0;
                }
            }
        }
        return maxLen;
    }
}
```

```cpp
#include <string>
#include <vector>
#include <algorithm>

class LongestCommonSubstring {
public:
    /**
     * @param s1 the first string
     * @param s2 the second string
     * @return   the length of the longest contiguous substring common to both
     */
    int longestCommonSubstring(const std::string& s1, const std::string& s2) {
        int m = (int)s1.size(), n = (int)s2.size();
        std::vector<std::vector<int>> dp(m + 1, std::vector<int>(n + 1, 0));
        int maxLen = 0;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1[i - 1] == s2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    maxLen = std::max(maxLen, dp[i][j]);
                }
            }
        }
        return maxLen;
    }
};
```

```python
def longest_common_substring(s1: str, s2: str) -> int:
    """
    @param s1: the first string
    @param s2: the second string
    @return:   the length of the longest contiguous substring common to both
    """
    m, n = len(s1), len(s2)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    max_len = 0
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if s1[i - 1] == s2[j - 1]:
                dp[i][j] = dp[i - 1][j - 1] + 1
                max_len = max(max_len, dp[i][j])
            else:
                dp[i][j] = 0
    return max_len
```

```rust
impl Solution {
    /// @param s1 the first string
    /// @param s2 the second string
    /// @return   the length of the longest contiguous substring common to both
    pub fn longest_common_substring(s1: String, s2: String) -> i32 {
        let (b1, b2) = (s1.as_bytes(), s2.as_bytes());
        let (m, n) = (b1.len(), b2.len());
        let mut dp = vec![vec![0i32; n + 1]; m + 1];
        let mut max_len = 0i32;
        for i in 1..=m {
            for j in 1..=n {
                if b1[i - 1] == b2[j - 1] {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    max_len = max_len.max(dp[i][j]);
                }
            }
        }
        max_len
    }
}
```

> **Note on the repository file:** the repo's `LongestCommonSubarray.kt` carries both a memoized-recursion attempt and the tabular `findLength_tabular` shown here. The tabular version is the one to study — the memoized version on that file has scratch-state issues (it compares `nums1[i]` with itself and recurses `i + 1` instead of `i - 1`), a good reminder that a broken top-down attempt should be discarded in favor of the clean bottom-up table. We present the clean version above.

## Dry run

**Input:** `s1 = "abcde"`, `s2 = "abfce"`. Fill the table (row by row; only the diagonal cells matter):

```
      ""   a   b   f   c   e
""     0   0   0   0   0   0
a      0   1   0   0   0   0
b      0   0   2   0   0   0
c      0   0   0   0   1   0
d      0   0   0   0   0   0
e      0   0   0   0   0   1
```

Trace the interesting cells:

```
i=1,j=1: 'a'=='a' -> dp=dp[0][0]+1=1        (streak "a")
i=2,j=2: 'b'=='b' -> dp=dp[1][1]+1=2        (streak "ab" — extended diagonally)
i=3,j=3: 'c' vs 'f' -> 0                    (streak broken at the mismatch)
i=3,j=4: 'c'=='c' -> dp=dp[2][3]+1=0+1=1    (new streak "c")
i=5,j=5: 'e'=='e' -> dp=dp[4][4]+1=0+1=1    (new streak "e")
```

Max over the table = **2** ("ab"). The crucial detail: the "c" match at `(3,4)` does **not** extend to 3, because the substring ending at `(2,3)` was 0 — the mismatch at `f` broke the diagonal. That's contiguity enforced by the recurrence itself.

## Complexity

**Time.** Every cell is computed once with $O(1)$ work:

$$
T(m, n) = O(mn)
$$

**Space.** The full table is $\Theta(mn)$; but each cell only reads `dp[i-1][j-1]`, so a **rolling array of size 2** (or even 1 with careful overwrite order) reduces space to $O(\min(m, n))$.

## Variants & follow-ups

- **[2.3](longest-common-subsequence.md)** — drop the "ends exactly at" sharpening and the same table computes the *discontiguous* longest common subsequence. The contrast between the two states is the single best interview teaching moment in this chapter.
- **[2.2](minimum-edit-distance.md)** — same table shape, three-way recurrence.
- **Interview follow-up:** "Return the substring, not just the length." Track the `(i, j)` of the maximum cell; the substring is `s1[i - maxLen .. i)`. One extra variable.
- **Interview follow-up:** "Can you do it with suffix automata / rolling hash + binary search?" For huge strings, binary search on the length $L$ + rolling-hash equality check gives $O((m+n)\log \min(m,n))$ — a genuinely different tradeoff worth mentioning after the DP.
