# 2.34 Valid Palindrome III

> **Source**: [`src/main/kotlin/string/dynamic_programming/ValidPalindrome_III.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/dynamic_programming/ValidPalindrome_III.kt)
> **Pattern**: longest-palindromic-subsequence DP · **Core page**

## The Problem

Can `s` become a palindrome by deleting **at most k** chars?

- Constraints: n ≤ 1000.

## Examples

```
Input:  s = "abcdeca", k = 2   -> Output: true
Input:  s = "abbababa", k = 1  -> Output: true
```

## Intuition — deleting ≤ k ⟺ the LPS length ≥ n − k

The chars *kept* must form a palindrome — the maximum kept is the **longest palindromic subsequence**. If `LPS >= n - k`, deletions ≤ k suffice:

```kotlin
val dp = Array(n) { IntArray(n) { 0 } }

fun lps(start: Int, length: Int): Int {
    val end = start + length - 1
    return when {
        length in 0..1 -> length
        dp[start][end] != 0 -> dp[start][end]
        s[start] == s[end] -> lps(start + 1, length - 2) + 2
        else -> maxOf(lps(start + 1, length - 1), lps(start, length - 1))
    }
}
return lps(0, n) >= n - k
```

**Why the [9.5](../ch09-strings/longest-palindromic-substring.md) memo shape?** The LPS recurrence: matching ends extend by 2; otherwise take the better of dropping either end. The memo on `(start, length)` makes it O(n²) — the [2.3](longest-common-subsequence.md) DP's palindrome twin.

## Approach 1 — LPS memo (the repo's version, optimal)

```kotlin
class ValidPalindrome_III {
    /**
     * @param s input string
     * @param k max deletions
     * @return  true iff k deletions can make it a palindrome
     */
    fun isValidPalindrome(s: String, k: Int): Boolean {
        val dp = Array(s.length) { IntArray(s.length) { 0 } }

        fun lps(start: Int, length: Int): Int {
            val end = start + length - 1
            return when {
                length in 0..1 -> length
                dp[start][end] != 0 -> dp[start][end]
                s[start] == s[end] -> lps(start + 1, length - 2) + 2
                else -> maxOf(lps(start + 1, length - 1), lps(start, length - 1))
            }
        }

        return lps(0, s.length) >= s.length - k
    }
}
```

```java
public class ValidPalindromeIII {
    private int[][] memo;

    private int lps(String s, int i, int j) {
        if (i > j) return 0;
        if (i == j) return 1;
        if (memo[i][j] != 0) return memo[i][j];

        if (s.charAt(i) == s.charAt(j)) {
            return memo[i][j] = 2 + lps(s, i + 1, j - 1);
        }
        return memo[i][j] = Math.max(lps(s, i + 1, j), lps(s, i, j - 1));
    }

    /**
     * @param s input string
     * @param k max deletions
     * @return  true iff k deletions can make it a palindrome
     */
    public boolean isValidPalindrome(String s, int k) {
        memo = new int[s.length()][s.length()];
        return lps(s, 0, s.length() - 1) >= s.length() - k;
    }
}
```

```cpp
#include <string>
#include <vector>
#include <algorithm>

class ValidPalindromeIII {
public:
    /**
     * @param s input string
     * @param k max deletions
     * @return  true iff k deletions can make it a palindrome
     */
    bool isValidPalindrome(std::string s, int k) {
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
        return dp[0][n - 1] >= n - k;
    }
};
```

```python
def is_valid_palindrome(s: str, k: int) -> bool:
    """
    @param s: input string
    @param k: max deletions
    @return:  true iff k deletions can make it a palindrome
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

    return dp[0][n - 1] >= n - k
```

```rust
impl Solution {
    /// @param s input string
    /// @param k max deletions
    /// @return  true iff k deletions can make it a palindrome
    pub fn is_valid_palindrome(s: String, k: i32) -> bool {
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
        dp[0][n - 1] >= n as i32 - k
    }
}
```

## Dry run

**Input:** `s = "abcdeca", k = 2`.

```
LPS: "abcdeca" — the longest palindromic subsequence is "acdca"?  a-c-d-c-a: a(0),c(2),d(3),c(5),a(6) = 5.
n = 7.  7 - 5 = 2 <= k=2 -> true ✓  (delete b and e)
```

## Complexity

**Time.** O(n²) table:

$$
T(n) = O(n^2)
$$

**Space.** The table:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Valid Palindrome II** ([9.24](../ch09-strings/valid-palindrome-ii.md)) — the k=1 special case.
- **Longest Palindromic Subsequence** — the LPS engine this page wraps.
- **Interview follow-up:** "Why does LPS ≥ n−k decide it?" A palindrome needs some chars kept — keeping an LPS leaves exactly `n − LPS` deletions. If that's ≤ k the deletions fit; the LPS is the *maximum* keepable palindrome, so it's the best case.
