# 2.33 Palindrome Partitioning II

> **Source**: [`src/main/kotlin/string/dynamic_programming/PalindromePartitioning_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/dynamic_programming/PalindromePartitioning_II.kt)
> **Pattern**: palindrome-table + min-cut DP · **Core page**

## The Problem

Min cuts to partition `s` into palindromes.

- Constraints: n ≤ 2000.

## Examples

```
Input:  s = "aab"   -> Output: 1   ("aa" | "b")
Input:  s = "a"     -> Output: 0
```

## Intuition — precompute palindromes, then the min-cut DP

`isPalindrome[i][j]` via the [9.5](../ch09-strings/longest-palindromic-substring.md) expansion or DP; `minCuts[end] = min(cuts before a palindrome suffix) + 1`:

```kotlin
val isPalindrome = Array(length) { BooleanArray(length) }
val minCuts = IntArray(length) { 0 }

for (end in 0 until length) {
    var currentMinCuts = end        // worst case: cut after every char

    for (start in 0..end) {
        if (s[start] == s[end] && (end - start <= 2 || isPalindrome[start + 1][end - 1])) {
            isPalindrome[start][end] = true

            currentMinCuts = if (start == 0) 0        // the whole prefix is a palindrome
                             else minOf(currentMinCuts, minCuts[start - 1] + 1)
        }
    }
    minCuts[end] = currentMinCuts
}
return minCuts[length - 1]
```

**Why interleave the two tables?** The palindrome test for `[start, end]` needs only shorter palindromes — computable on the fly in the same loop. The cut DP then reads `minCuts[start-1] + 1` (one cut before a palindrome suffix).

## Approach 1 — Interleaved palindrome + cut DP (the repo's version, optimal)

```kotlin
class PalindromePartitioning_II {
    /**
     * @param s input string
     * @return  minimum palindrome cuts
     */
    fun minCut(s: String): Int {
        val length = s.length
        val isPalindrome = Array(length) { BooleanArray(length) { false } }
        val minCuts = IntArray(length) { 0 }

        for (end in 0 until length) {
            var currentMinCuts = end

            for (start in 0..end) {
                if (s[start] == s[end] && (end - start <= 2 || isPalindrome[start + 1][end - 1])) {
                    isPalindrome[start][end] = true

                    currentMinCuts = if (start == 0) 0
                                     else minOf(currentMinCuts, minCuts[start - 1] + 1)
                }
            }
            minCuts[end] = currentMinCuts
        }
        return minCuts[length - 1]
    }
}
```

```java
public class PalindromePartitioningII {
    /**
     * @param s input string
     * @return  minimum palindrome cuts
     */
    public int minCut(String s) {
        int n = s.length();
        boolean[][] pal = new boolean[n][n];
        int[] cuts = new int[n];

        for (int end = 0; end < n; end++) {
            int best = end;

            for (int start = 0; start <= end; start++) {
                if (s.charAt(start) == s.charAt(end)
                        && (end - start <= 2 || pal[start + 1][end - 1])) {
                    pal[start][end] = true;
                    best = start == 0 ? 0 : Math.min(best, cuts[start - 1] + 1);
                }
            }
            cuts[end] = best;
        }
        return cuts[n - 1];
    }
}
```

```cpp
#include <string>
#include <vector>
#include <algorithm>

class PalindromePartitioningII {
public:
    /**
     * @param s input string
     * @return  minimum palindrome cuts
     */
    int minCut(std::string s) {
        int n = s.size();
        std::vector<std::vector<bool>> pal(n, std::vector<bool>(n, false));
        std::vector<int> cuts(n, 0);

        for (int end = 0; end < n; end++) {
            int best = end;

            for (int start = 0; start <= end; start++) {
                if (s[start] == s[end] && (end - start <= 2 || pal[start + 1][end - 1])) {
                    pal[start][end] = true;
                    best = start == 0 ? 0 : std::min(best, cuts[start - 1] + 1);
                }
            }
            cuts[end] = best;
        }
        return cuts[n - 1];
    }
};
```

```python
def min_cut(s: str) -> int:
    """
    @param s: input string
    @return:  minimum palindrome cuts
    """
    n = len(s)
    is_pal = [[False] * n for _ in range(n)]
    cuts = [0] * n

    for end in range(n):
        best = end

        for start in range(end + 1):
            if s[start] == s[end] and (end - start <= 2 or is_pal[start + 1][end - 1]):
                is_pal[start][end] = True
                best = 0 if start == 0 else min(best, cuts[start - 1] + 1)

        cuts[end] = best

    return cuts[-1]
```

```rust
impl Solution {
    /// @param s input string
    /// @return  minimum palindrome cuts
    pub fn min_cut(s: String) -> i32 {
        let bytes: Vec<char> = s.chars().collect();
        let n = bytes.len();
        let mut is_pal = vec![vec![false; n]; n];
        let mut cuts = vec![0; n];

        for end in 0..n {
            let mut best = end;

            for start in 0..=end {
                if bytes[start] == bytes[end]
                    && (end - start <= 2 || is_pal[start + 1][end - 1]) {
                    is_pal[start][end] = true;
                    best = if start == 0 { 0 } else { best.min(cuts[start - 1] + 1) };
                }
            }
            cuts[end] = best;
        }
        cuts[n - 1] as i32
    }
}
```

## Dry run

**Input:** `s = "aab"`.

```
end=0 'a': start 0: 'a'=='a' -> pal[0][0]=true.  start==0 -> best=0.  cuts[0]=0.
end=1 'a': start 0: 'a'=='a' && 1-0<=2 -> pal[0][1]=true.  best=0.  start 1: pal[1][1].  cuts[1]=0.
end=2 'b': start 0: 'a'!='b' no.  start 1: 'a'!='b' no.  start 2: pal[2][2] -> best = cuts[1]+1 = 1.
  cuts[2]=1.
Output: 1 ✓
```

## Complexity

**Time.** O(n²) cells:

$$
T(n) = O(n^2)
$$

**Space.** Two tables:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Palindrome Partitioning** ([12.5](../ch12-backtracking/palindrome-partitioning.md)) — enumerate all (I), this page minimizes (II).
- **Interview follow-up:** "Why `end - start <= 2` in the palindrome test?" Length-1 and length-2 substrings are palindromes by inspection (no interior to check) — the base case of the recurrence `pal[start][end] = chars match && pal[start+1][end-1]`.
