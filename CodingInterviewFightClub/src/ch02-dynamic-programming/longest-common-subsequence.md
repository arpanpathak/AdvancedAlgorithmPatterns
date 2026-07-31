# 2.3 Longest Common Subsequence

> **Source:** `src/main/kotlin/google/GoogleCheatSheet_II.kt` (the LCS block) · `src/main/kotlin/string/dynamic_programming/`
> **Pattern:** 2D DP on string prefixes · **Core page**

## The Problem

Given two strings `s` (length $m$) and `t` (length $n$), return the length of the **longest common subsequence** — the longest sequence of characters that appears in *both* strings, in order, **not necessarily contiguously**.

- Constraints: $1 \le m, n \le 10^3$.

## Examples

```
s = "abcde", t = "ace"    -> 3   ("ace")
s = "abc",   t = "abc"    -> 3
s = "abc",   t = "def"    -> 0
s = "AGGTAB", t = "GXTXAYB" -> 4   ("GTAB")
```

## Intuition — "match or skip" on prefixes

The state is the classic prefix pair:

$$
dp[i][j] = \text{length of the LCS of } s[0..i) \text{ and } t[0..j)
$$

The recurrence comes from a clean case split on the last characters:

- **`s[i-1] == t[j-1]`:** this character *can* be the last of the LCS (any LCS of the prefixes can have it appended — matching it is never worse than skipping it, by the exchange argument that appending a common character only helps). So:

$$
dp[i][j] = dp[i-1][j-1] + 1
$$

- **`s[i-1] != t[j-1]`:** the last characters can't both be in the LCS, so the best is either the LCS of `s[0..i-1)` with `t[0..j)` or of `s[0..i)` with `t[0..j-1)`:

$$
dp[i][j] = \max(dp[i-1][j], \; dp[i][j-1])
$$

**Answer:** $dp[m][n]$. Base case: $dp[0][*] = dp[*][0] = 0$ (one string empty ⇒ LCS empty).

**Why "match" wins on equality:** suppose a best LCS of the prefixes doesn't use `s[i-1]`. Then it's an LCS of $s[0..i-1)$ and $t[0..j)$, length $\le dp[i-1][j-1] + 1$ — but appending the matched character to *some* LCS of $s[0..i-1), t[0..j-1)$ gives length $dp[i-1][j-1] + 1$, which is at least as good. So matching is optimal.

## Approach 1 — Brute force

Enumerate all $2^m$ subsequences of `s` and check containment in `t`. Exponential — this is the textbook "DP is the only way" problem.

## Approach 2 — Bottom-up DP (optimal)

```kotlin
/**
 * @param s the first string
 * @param t the second string
 * @return  the length of the longest common subsequence of s and t
 */
fun longestCommonSubsequence(s: String, t: String): Int {
    val dp = Array(s.length + 1) { IntArray(t.length + 1) }

    for (i in 1..s.length) {
        for (j in 1..t.length) {
            dp[i][j] = when {
                s[i - 1] == t[j - 1] -> dp[i - 1][j - 1] + 1          // extend the diagonal
                else                 -> maxOf(dp[i - 1][j], dp[i][j - 1]) // best of skipping one side
            }
        }
    }
    return dp[s.length][t.length]
}
```

```java
public class LongestCommonSubsequence {
    /**
     * @param s the first string
     * @param t the second string
     * @return  the length of the longest common subsequence of s and t
     */
    public int longestCommonSubsequence(String s, String t) {
        int m = s.length(), n = t.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }
}
```

```cpp
#include <string>
#include <vector>
#include <algorithm>

class LongestCommonSubsequence {
public:
    /**
     * @param s the first string
     * @param t the second string
     * @return  the length of the longest common subsequence of s and t
     */
    int longestCommonSubsequence(const std::string& s, const std::string& t) {
        int m = (int)s.size(), n = (int)t.size();
        std::vector<std::vector<int>> dp(m + 1, std::vector<int>(n + 1, 0));
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s[i - 1] == t[j - 1]) dp[i][j] = dp[i - 1][j - 1] + 1;
                else dp[i][j] = std::max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[m][n];
    }
};
```

```python
def longest_common_subsequence(s: str, t: str) -> int:
    """
    @param s: the first string
    @param t: the second string
    @return:  the length of the longest common subsequence of s and t
    """
    m, n = len(s), len(t)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if s[i - 1] == t[j - 1]:
                dp[i][j] = dp[i - 1][j - 1] + 1
            else:
                dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])
    return dp[m][n]
```

```rust
impl Solution {
    /// @param s the first string
    /// @param t the second string
    /// @return  the length of the longest common subsequence of s and t
    pub fn longest_common_subsequence(s: String, t: String) -> i32 {
        let (bs, bt) = (s.as_bytes(), t.as_bytes());
        let (m, n) = (bs.len(), bt.len());
        let mut dp = vec![vec![0i32; n + 1]; m + 1];
        for i in 1..=m {
            for j in 1..=n {
                dp[i][j] = if bs[i - 1] == bt[j - 1] {
                    dp[i - 1][j - 1] + 1
                } else {
                    dp[i - 1][j].max(dp[i][j - 1])
                };
            }
        }
        dp[m][n]
    }
}
```

## Dry run

**Input:** `s = "AGGTAB"`, `t = "GXTXAYB"`. The classic worked example. Fill the table:

```
      ""  G   X   T   X   A   Y   B
""     0   0   0   0   0   0   0   0
A      0   0   0   0   0   1   1   1
G      0   1   1   1   1   1   1   1
G      0   1   1   1   1   1   1   1
T      0   1   1   2   2   2   2   2
A      0   1   1   2   2   3   3   3
B      0   1   1   2   2   3   3   4
```

Trace the interesting cells:

```
i=1 (A):  j=5: 'A'=='A' -> dp[0][4]+1 = 1          (new match; extends from row 0)
i=4 (T):  j=3: 'T'=='T' -> dp[3][2]+1 = 1+1 = 2    ("GT")
          j=4: 'T' vs 'X' -> max(dp[3][4], dp[4][3]) = max(1, 2) = 2  (skip propagates the 2)
i=5 (A):  j=5: 'A'=='A' -> dp[4][4]+1 = 2+1 = 3    ("GTA")
i=6 (B):  j=7: 'B'=='B' -> dp[5][6]+1 = 3+1 = 4    ("GTAB")
```

Answer = `dp[6][7]` = **4**. Reading the diagonal back: matches at (1,5)→A, (4,3)→T, (5,5)→A... wait — reconstructing from the table (start at bottom-right, move up-left on matches): B(6,7), then A(5,5), then T(4,3), then G(2,1) → "GTAB" reversed. The path is the reconstruction story — walk it once by hand.

## Complexity

**Time.**

$$
T(m, n) = O(mn)
$$

**Space.** Full table $\Theta(mn)$; rolling to two rows gives $O(n)$ (each cell reads only `dp[i-1][j-1]`, `dp[i-1][j]`, `dp[i][j-1]`).

## Variants & follow-ups

- **[2.1](longest-common-substring.md)** — the contiguous twin; compare the two states ("ends exactly at" vs "best prefix pair") out loud in an interview.
- **[2.2](minimum-edit-distance.md)** — same table, but the "skip" branch becomes three explicit operations with +1 cost.
- **Delete Operations For Two Strings** (`src/main/kotlin/string/dynamic_programming/DeleteOperationsForTwoStrings.kt`) — `m + n - 2 * LCS`; the LCS is the "keep" set.
- **Interview follow-up:** "Print the LCS, not just its length." Backtrack from `dp[m][n]`: on a match move diagonally and emit; on unequal cells move to the larger neighbor. $O(m + n)$ after the table.
- **Interview follow-up:** "Is there a faster-than-$O(mn)$ method?" For *typical* strings, the Hunt–Szymanski algorithm runs in $O((r + n) \log n)$ where $r$ = number of matches — worth naming as the "when the alphabet is small / matches are sparse" variant.
