# 2.35 Longest Palindromic Subsequence

> **Source**: [`src/main/kotlin/string/dynamic_programming/LongestPalindromicSubsequence.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/dynamic_programming/LongestPalindromicSubsequence.kt)
> **Pattern**: LPS memo / LCS with reverse · **Core page**

## The Problem

The longest **subsequence** (not substring!) that is a palindrome.

- Constraints: n ≤ 1000.

## Examples

```
Input:  s = "bbbab"   -> Output: 4   ("bbbb")
Input:  s = "cbbd"    -> Output: 2
```

## Intuition — the recurrence: match the ends or drop one

`lps(start, end)`: ends equal → 2 + inner; else the max of dropping either end:

```kotlin
fun lps(start: Int, length: Int): Int {
    if (length <= 1) return length
    val end = start + length - 1

    return when {
        dp[start][end] != 0 -> dp[start][end]
        s[start] == s[end] -> 2 + lps(start + 1, length - 2)
        else -> maxOf(lps(start + 1, length - 1), lps(start, length - 1))
    }.also { dp[start][end] = it }
}
```

**Why the [2.3](longest-common-subsequence.md) shape?** LPS(s) = LCS(s, reverse(s)) — the same recurrence family; the memoized `(start, length)` version computes it directly. The [9.5](../ch09-strings/longest-palindromic-substring.md) substring twin, without the contiguity constraint.

## Approach 1 — Memoized LPS (the repo's version, optimal)

## Approach 2 — LCS(s, rev(s)) table — the equivalence proof

```kotlin
class LongestPalindromicSubsequence {
    /**
     * @param s input string
     * @return  longest palindromic subsequence length
     */
    fun longestPalindromeSubseq(s: String): Int {
        val dp = Array(s.length) { IntArray(s.length) }

        fun lps(start: Int, length: Int): Int {
            if (length <= 1) return length
            val end = start + length - 1

            return when {
                dp[start][end] != 0 -> dp[start][end]
                s[start] == s[end] -> 2 + lps(start + 1, length - 2)
                else -> maxOf(lps(start + 1, length - 1), lps(start, length - 1))
            }.also { dp[start][end] = it }
        }

        return lps(0, s.length)
    }
}
```

```java
public class LongestPalindromicSubsequence {
    private int[][] memo;

    private int lps(String s, int i, int j) {
        if (i > j) return 0;
        if (i == j) return 1;
        if (memo[i][j] != 0) return memo[i][j];

        if (s.charAt(i) == s.charAt(j)) return memo[i][j] = 2 + lps(s, i + 1, j - 1);
        return memo[i][j] = Math.max(lps(s, i + 1, j), lps(s, i, j - 1));
    }

    /**
     * @param s input string
     * @return  longest palindromic subsequence length
     */
    public int longestPalindromeSubseq(String s) {
        memo = new int[s.length()][s.length()];
        return lps(s, 0, s.length() - 1);
    }
}
```

```cpp
#include <string>
#include <vector>
#include <algorithm>

class LongestPalindromicSubsequence {
public:
    /**
     * @param s input string
     * @return  longest palindromic subsequence length
     */
    int longestPalindromeSubseq(std::string s) {
        int n = s.size();
        std::vector<std::vector<int>> dp(n, std::vector<int>(n, 0));

        for (int len = 1; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                if (len == 1) dp[i][j] = 1;
                else if (s[i] == s[j]) dp[i][j] = 2 + dp[i + 1][j - 1];
                else dp[i][j] = std::max(dp[i + 1][j], dp[i][j - 1]);
            }
        }
        return dp[0][n - 1];
    }
};
```

```python
def longest_palindrome_subseq(s: str) -> int:
    """
    @param s: input string
    @return:  longest palindromic subsequence length
    """
    n = len(s)
    dp = [[0] * n for _ in range(n)]

    for length in range(1, n + 1):
        for i in range(n - length + 1):
            j = i + length - 1
            if length == 1:
                dp[i][j] = 1
            elif s[i] == s[j]:
                dp[i][j] = 2 + dp[i + 1][j - 1]
            else:
                dp[i][j] = max(dp[i + 1][j], dp[i][j - 1])

    return dp[0][n - 1]
```

```rust
impl Solution {
    /// @param s input string
    /// @return  longest palindromic subsequence length
    pub fn longest_palindrome_subseq(s: String) -> i32 {
        let bytes: Vec<char> = s.chars().collect();
        let n = bytes.len();
        let mut dp = vec![vec![0; n]; n];

        for length in 1..=n {
            for i in 0..=(n - length) {
                let j = i + length - 1;
                dp[i][j] = if length == 1 { 1 }
                    else if bytes[i] == bytes[j] { 2 + dp[i + 1][j - 1] }
                    else { dp[i + 1][j].max(dp[i][j - 1]) };
            }
        }
        dp[0][n - 1]
    }
}
```

## Dry run

**Input:** `s = "bbbab"`.

```
len 1: all 1.  len 2: "bb": 2.  "bb": 2.  "ba": max(1,1)=1.  "ab": 1.
len 3: "bbb": ends b==b -> 2 + dp[1][1]=1 -> 3.  "bba": b!=a -> max(dp[1][2]=2, dp[0][1]=2)=2.
  "bab": b==b -> 2 + dp[1][1]=1 -> 3.
len 4: "bbba": b!=a -> max(dp[1][3]=3, dp[0][2]=3)=3.  "bbab": b==b -> 2 + dp[1][2]=2 -> 4.
len 5: "bbbab": b==b -> 2 + dp[1][3]=3 -> 5?  Wait the answer for "bbbab" is 4 ("bbbb")!
  dp[1][3] = "bba" = 2?  Let me recompute: dp[1][3] is indices 1..3 = "bba" -> len 3: b==a? no
  -> max(dp[2][3]="ba"=1, dp[1][2]="bb"=2) = 2.  So dp[0][4] = 2 + 2 = 4 ✓  ("bbbb")
Output: 4 ✓
```

## Complexity

**Time.** O(n²):

$$
T(n) = O(n^2)
$$

**Space.** The table:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Valid Palindrome III** ([2.34](valid-palindrome-iii.md)) — the LPS test `>= n - k`.
- **Longest Palindromic Substring** ([9.5](../ch09-strings/longest-palindromic-substring.md)) — contiguity makes it O(n²) center-expansion instead.
- **Interview follow-up:** "Why does LPS equal LCS(s, rev(s))?" A palindrome read backward is itself — the longest common subsequence between s and its reverse picks exactly the chars of a palindromic subsequence, in mirrored positions.
