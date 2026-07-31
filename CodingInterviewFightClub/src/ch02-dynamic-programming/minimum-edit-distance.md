# 2.2 Minimum Edit Distance

> **Source:** [`src/main/kotlin/string/dynamic_programming/EditDistance.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/dynamic_programming/EditDistance.kt)
> **Pattern:** 2D DP on string prefixes · **Core page**

## The Problem

Given two words `word1` (length $m$) and `word2` (length $n$), return the **minimum number of operations** to convert `word1` into `word2`. Allowed operations (each costs 1):

- **Insert** a character,
- **Delete** a character,
- **Replace** a character.

- Constraints: $0 \le m, n \le 500$.

## Examples

```
word1 = "horse", word2 = "ros"   -> 3
  horse -> rorse (replace 'h' with 'r')
  rorse -> rose   (delete 'r')
  rose  -> ros    (delete 'e')

word1 = "intention", word2 = "execution" -> 5
```

## Intuition — the three operations are three recursive subproblems

Classic state on prefixes:

$$
dp[i][j] = \text{min cost to convert } word1[0..i) \text{ into } word2[0..j)
$$

Split on the last characters:

- **Match (`word1[i-1] == word2[j-1]`):** no operation needed on the last characters; solve the prefixes: $dp[i][j] = dp[i-1][j-1]$.
- **Mismatch:** three options, each a different recursive subproblem, pay 1 and take the min:
  - **Replace:** `dp[i-1][j-1] + 1` — make the last chars equal, then solve the prefixes.
  - **Delete** from `word1`: `dp[i-1][j] + 1` — drop `word1`'s last char, now converting `word1[0..i-1)` to `word2[0..j)`.
  - **Insert** into `word1`: `dp[i][j-1] + 1` — insert `word2`'s last char at the end of `word1`, now converting `word1[0..i)` to `word2[0..j-1)`.

$$
dp[i][j] =
\begin{cases}
dp[i-1][j-1] & \text{match} \\[1mm]
1 + \min(dp[i-1][j-1],\; dp[i-1][j],\; dp[i][j-1]) & \text{mismatch}
\end{cases}
$$

**Base cases:** `dp[0][j] = j` (insert j chars), `dp[i][0] = i` (delete i chars).

**Why these three cover everything:** any edit script's last operation (in the optimal order) is one of these three, applied to the last character — insert, delete, or replace. So the optimal script = optimal prefix script + last operation. That's optimal substructure, stated cleanly.

## Approach 1 — Brute-force recursion

Recurse on every choice of operation. The recursion tree branches 3× per mismatch → up to $3^{\min(m,n)}$ leaves. Exponential — the memo table is what saves it.

## Approach 2 — Memoized recursion (the repo's style)

```kotlin
/**
 * @param word1 the source word
 * @param word2 the target word
 * @return      the minimum number of insert/delete/replace operations to convert word1 to word2
 */
fun minDistance(word1: String, word2: String): Int {
    val memo = mutableMapOf<String, Int>()

    /**
     * @param m the length of the word1 prefix under consideration
     * @param n the length of the word2 prefix under consideration
     * @return  min cost to convert word1[0..m) into word2[0..n)
     */
    fun solve(m: Int, n: Int): Int {
        val state = "$m.$n"
        memo[state]?.let { return it }

        return when {
            m == 0 -> n                                          // insert n chars
            n == 0 -> m                                          // delete m chars
            word1[m - 1] == word2[n - 1] -> solve(m - 1, n - 1)  // match, free
            else -> {
                val insert  = solve(m, n - 1)     // word1 gains word2[n-1]
                val delete  = solve(m - 1, n)     // word1 loses word1[m-1]
                val replace = solve(m - 1, n - 1) // both last chars replaced
                1 + minOf(insert, delete, replace)
            }
        }.also { memo[state] = it }
    }
    return solve(word1.length, word2.length)
}
```

> The repository's `EditDistance.kt` is exactly this shape (memoized recursion over `(m, n)` states). The `Map<String, Int>` key is a pragmatic stand-in for a 2D array; a `Array<IntArray>` with `-1` sentinels is faster and is what the bottom-up version uses below.

## Approach 3 — Bottom-up (optimal)

```kotlin
/**
 * @param word1 the source word
 * @param word2 the target word
 * @return      the minimum edit distance (insert/delete/replace, cost 1 each)
 */
fun minDistance(word1: String, word2: String): Int {
    val m = word1.length
    val n = word2.length
    val dp = Array(m + 1) { IntArray(n + 1) }

    for (i in 0..m) dp[i][0] = i     // delete i chars to reach ""
    for (j in 0..n) dp[0][j] = j     // insert j chars to reach word2 from ""

    for (i in 1..m) {
        for (j in 1..n) {
            dp[i][j] = if (word1[i - 1] == word2[j - 1]) {
                dp[i - 1][j - 1]                       // match: free
            } else {
                1 + minOf(
                    dp[i - 1][j - 1],   // replace
                    dp[i - 1][j],       // delete from word1
                    dp[i][j - 1]        // insert into word1
                )
            }
        }
    }
    return dp[m][n]
}
```

```java
public class EditDistance {
    /**
     * @param word1 the source word
     * @param word2 the target word
     * @return      the minimum edit distance (insert/delete/replace, cost 1 each)
     */
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                                    Math.min(dp[i - 1][j], dp[i][j - 1]));
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

class EditDistance {
public:
    /**
     * @param word1 the source word
     * @param word2 the target word
     * @return      the minimum edit distance (insert/delete/replace, cost 1 each)
     */
    int minDistance(const std::string& word1, const std::string& word2) {
        int m = (int)word1.size(), n = (int)word2.size();
        std::vector<std::vector<int>> dp(m + 1, std::vector<int>(n + 1, 0));
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1[i - 1] == word2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + std::min({dp[i - 1][j - 1], dp[i - 1][j], dp[i][j - 1]});
                }
            }
        }
        return dp[m][n];
    }
};
```

```python
def min_distance(word1: str, word2: str) -> int:
    """
    @param word1: the source word
    @param word2: the target word
    @return:      the minimum edit distance (insert/delete/replace, cost 1 each)
    """
    m, n = len(word1), len(word2)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    for i in range(m + 1):
        dp[i][0] = i
    for j in range(n + 1):
        dp[0][j] = j

    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if word1[i - 1] == word2[j - 1]:
                dp[i][j] = dp[i - 1][j - 1]
            else:
                dp[i][j] = 1 + min(
                    dp[i - 1][j - 1],   # replace
                    dp[i - 1][j],       # delete from word1
                    dp[i][j - 1]        # insert into word1
                )
    return dp[m][n]
```

```rust
impl Solution {
    /// @param word1 the source word
    /// @param word2 the target word
    /// @return      the minimum edit distance (insert/delete/replace, cost 1 each)
    pub fn min_distance(word1: String, word2: String) -> i32 {
        let (b1, b2) = (word1.as_bytes(), word2.as_bytes());
        let (m, n) = (b1.len(), b2.len());
        let mut dp = vec![vec![0i32; n + 1]; m + 1];
        for i in 0..=m { dp[i][0] = i as i32; }
        for j in 0..=n { dp[0][j] = j as i32; }

        for i in 1..=m {
            for j in 1..=n {
                dp[i][j] = if b1[i - 1] == b2[j - 1] {
                    dp[i - 1][j - 1]
                } else {
                    1 + dp[i - 1][j - 1].min(dp[i - 1][j]).min(dp[i][j - 1])
                };
            }
        }
        dp[m][n]
    }
}
```

### Approach 4 — The one-row "extreme optimization"

The notes' "extreme optimization" collapses the two rows into **one** — the only cell still needed from the previous row is the diagonal `dp[i-1][j-1]`, carried in a `prevDiagonal` variable that's saved *before* each cell is overwritten:

```kotlin
fun minDistance(s1: String, s2: String): Int {
    val m = s1.length
    val n = s2.length

    // Keep the shorter string as the row width
    if (m < n) return minDistance(s2, s1)

    val dp = IntArray(n + 1)
    for (j in 0..n) dp[j] = j                 // first row: s1 empty, j insertions

    for (i in 1..m) {
        var prevDiagonal = dp[0]              // dp[i-1][j-1] from the previous row
        dp[0] = i                             // first column: s2 empty, i deletions

        for (j in 1..n) {
            val temp = dp[j]                  // save current before overwriting
            dp[j] = when {
                s1[i - 1] == s2[j - 1] -> prevDiagonal
                else -> 1 + minOf(prevDiagonal, dp[j], dp[j - 1])
            }
            prevDiagonal = temp               // becomes the diagonal for the next j
        }
    }
    return dp[n]
}
```

`dp[j]` at the moment of the `minOf` is the *old* row's `dp[i-1][j]` (delete), `dp[j-1]` is this row's `dp[i][j-1]` (insert), and `prevDiagonal` is `dp[i-1][j-1]` (replace) — one array holding three rows' worth of information through careful timing. $O(n)$ space, same $O(mn)$ time.

## Dry run

**Input:** `word1 = "horse"`, `word2 = "ros"`. Fill the table:

```
      ""   r   o   s
""     0   1   2   3
h      1   1   2   3
o      2   2   1   2
r      3   2   2   2
s      4   3   3   2
e      5   4   4   3
```

Trace the interesting cells:

```
i=1 (h): j=1: 'h' vs 'r' mismatch -> 1 + min(dp[0][0]=0, dp[0][1]=1, dp[1][0]=1) = 1  (replace h->r)
i=2 (o): j=2: 'o'=='o' match -> dp[1][1] = 1
i=2 (o): j=3: 'o' vs 's' mismatch -> 1 + min(dp[1][2]=2, dp[1][3]=3, dp[2][2]=1) = 2
i=5 (e): j=3: 'e' vs 's' mismatch -> 1 + min(dp[4][2]=3, dp[4][3]=2, dp[5][2]=4) = 3
```

Answer = `dp[5][3]` = **3**. Reconstruction (bottom-right, follow the min):

```
(5,3) 'e' vs 's' -> delete 'e' (came from dp[4][3])
(4,3) 's' == 's' -> match, move diagonal (came from dp[3][2])
(3,2) 'r' vs 'o' -> replace 'r' with 'o' (came from dp[2][1])
(2,1) 'o' == 'o' -> match, diagonal (came from dp[1][0])
(1,0) -> delete 'h' (base case dp[1][0] = 1)
Script: delete h, match o, replace r->o, match s = 3 operations ✓
```

## Complexity

**Time.** $O(mn)$ — every cell computed once.

**Space.** Full table $\Theta(mn)$; the recurrence reads only the previous row, so **two rolling rows** give $O(n)$ space. (With a single row plus a saved diagonal, $O(n)$ is achievable too — the classic trick.)

## Variants & follow-ups

- **[2.3](longest-common-subsequence.md)** — LCS = edit distance where only insert/delete are allowed: $m + n - 2 \cdot \text{LCS}$.
- **Delete Operations For Two Strings** — exactly that identity, implemented in the repo.
- **Spell-check / diff** — the real-world systems built on this recurrence; saying "git diff's Myers algorithm is a variant of this" is a strong senior signal.
- **Interview follow-up:** "What if operations have different costs (replace = 2)?" The recurrence is unchanged — only the constants in the mismatch branch change. One line.
- **Interview follow-up:** "Print the edit script." Backtrack from `dp[m][n]` like the dry run: match → diagonal, else → the cell that produced the min, emitting the operation. $O(m + n)$ after the table.
