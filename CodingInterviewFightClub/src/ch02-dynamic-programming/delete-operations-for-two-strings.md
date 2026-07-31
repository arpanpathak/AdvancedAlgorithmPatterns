# 2.24 Delete Operations For Two Strings

> **Source:** [`src/main/kotlin/string/dynamic_programming/DeleteOperationsForTwoStrings.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/dynamic_programming/DeleteOperationsForTwoStrings.kt)
> **Pattern:** LCS → deletions · **Core page**

## The Problem

Minimum deletions to make `word1` and `word2` equal (delete from either).

- Constraints: lengths ≤ 500.

## Examples

```
Input:  word1 = "sea", word2 = "eat"   -> Output: 2   (delete 's' and 't': both become "ea")
Input:  word1 = "leetcode", word2 = "etco" -> Output: 4
```

## Intuition — delete everything but the LCS

The strings become equal iff we keep a **common subsequence** and delete the rest. Keep the *longest* such subsequence → minimal deletions:

$$
\text{answer} = |w1| + |w2| - 2 \cdot \text{LCS}(w1, w2)
$$

The repo's DP computes the LCS directly ([2.3](longest-common-subsequence.md) engine):

```kotlin
val dp = Array(word1.length + 1) { IntArray(word2.length + 1) }

for (i in 1..word1.length) {
    for (j in 1..word2.length) {
        dp[i][j] = when {
            word1[i - 1] == word2[j - 1] -> dp[i - 1][j - 1] + 1   // extend the LCS
            else -> maxOf(dp[i - 1][j], dp[i][j - 1])              // skip one char
        }
    }
}
return word1.length + word2.length - 2 * dp[word1.length][word2.length]
```

**Why `+1` on match, `max` on mismatch?** The LCS recurrence: a matching pair extends the best LCS of the prefixes; a mismatch keeps the best of dropping either string's last char. The [2.2](minimum-edit-distance.md) table machinery, with deletions-only as the cost model.

**Why `2 * LCS`?** Each kept LCS char is a char *not deleted* from both strings — the deletions are `|w1| − LCS` plus `|w2| − LCS`.

## Approach 1 — DP over edits (the direct 2-row DP)

`dp[i][j]` = min deletions; same table, different semantics — equivalent to LCS minus the algebra.

## Approach 2 — LCS then subtract (the repo's version, optimal)

```kotlin
class DeleteOperationsForTwoStrings {
    /**
     * @param word1 first string
     * @param word2 second string
     * @return      minimum deletions to make them equal
     */
    fun minDistance(word1: String, word2: String): Int {
        val dp = Array(word1.length + 1) { IntArray(word2.length + 1) }   // LCS lengths

        for (i in 1..word1.length) {
            for (j in 1..word2.length) {
                dp[i][j] = when {
                    word1[i - 1] == word2[j - 1] -> dp[i - 1][j - 1] + 1
                    else -> maxOf(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }

        return word1.length + word2.length - 2 * dp[word1.length][word2.length]
    }
}
```

```java
public class DeleteOperationsForTwoStrings {
    /**
     * @param word1 first string
     * @param word2 second string
     * @return      minimum deletions to make them equal
     */
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];          // LCS lengths

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return m + n - 2 * dp[m][n];
    }
}
```

```cpp
#include <string>
#include <vector>
#include <algorithm>

class DeleteOperationsForTwoStrings {
public:
    /**
     * @param word1 first string
     * @param word2 second string
     * @return      minimum deletions to make them equal
     */
    int minDistance(std::string word1, std::string word2) {
        int m = word1.size(), n = word2.size();
        std::vector<std::vector<int>> dp(m + 1, std::vector<int>(n + 1, 0));   // LCS lengths

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1[i - 1] == word2[j - 1]) dp[i][j] = dp[i - 1][j - 1] + 1;
                else dp[i][j] = std::max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return m + n - 2 * dp[m][n];
    }
};
```

```python
def min_distance(word1: str, word2: str) -> int:
    """
    @param word1: first string
    @param word2: second string
    @return:      minimum deletions to make them equal
    """
    m, n = len(word1), len(word2)
    dp = [[0] * (n + 1) for _ in range(m + 1)]   # LCS lengths

    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if word1[i - 1] == word2[j - 1]:
                dp[i][j] = dp[i - 1][j - 1] + 1
            else:
                dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])

    return m + n - 2 * dp[m][n]
```

```rust
impl Solution {
    /// @param word1 first string
    /// @param word2 second string
    /// @return      minimum deletions to make them equal
    pub fn min_distance(word1: String, word2: String) -> i32 {
        let (b1, b2) = (word1.as_bytes(), word2.as_bytes());
        let (m, n) = (b1.len(), b2.len());
        let mut dp = vec![vec![0; n + 1]; m + 1];   // LCS lengths

        for i in 1..=m {
            for j in 1..=n {
                if b1[i - 1] == b2[j - 1] {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = dp[i - 1][j].max(dp[i][j - 1]);
                }
            }
        }
        (m + n - 2 * dp[m][n]) as i32
    }
}
```

## Dry run

**Input:** `word1 = "sea"`, `word2 = "eat"`.

```
LCS table:
    ""  e  a  t
""   0  0  0  0
s    0  0  0  0
e    0  0  0  0   <- wait, trace properly:
i=1 's': j=1 'e': no -> max(0,0)=0.  j=2 'a': 0.  j=3 't': 0.
i=2 'e': j=1 'e': match -> dp[1][0]+1 = 1.  j=2 'a': max(1,0)=1.  j=3 't': max(1,1)=1.
i=3 'a': j=1 'e': max(1,0)=1.  j=2 'a': match -> dp[2][1]+1 = 2.  j=3 't': max(2,1)=2.

LCS("sea","eat") = 2 ("ea").
answer = 3 + 3 - 2*2 = 2 ✓
```

The LCS table says the longest common subsequence is "ea" (length 2) — keep it, delete 's' from "sea" and 't' from "eat": 2 deletions total. The `m + n − 2·LCS` algebra turns the LCS computation into the answer; the same table with a `delete`-cost recurrence gives the same number directly.

## Complexity

**Time.** Table fill:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** The table (2-row reduces to O(n)):

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Longest Common Subsequence** ([2.3](longest-common-subsequence.md)) — the engine this page wraps.
- **Edit Distance** ([2.2](minimum-edit-distance.md)) — insert/delete/replace: the 3-op generalization.
- **Interview follow-up:** "Why is LCS the right frame and not a direct deletion DP?" The strings become equal by *keeping* a common subsequence — maximizing what's kept minimizes what's deleted. The LCS is the shared skeleton; deletions are everything else. Naming that identity is the whole insight.
