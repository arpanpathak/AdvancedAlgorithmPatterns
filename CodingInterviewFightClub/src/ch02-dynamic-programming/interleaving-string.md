# 2.22 Interleaving String

> **Source:** [`src/main/kotlin/string/dynamic_programming/InterleavingString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/dynamic_programming/InterleavingString.kt)
> **Pattern:** (i, j) matching DP · **Core page**

## The Problem

Is `s3` formed by **interleaving** `s1` and `s2` (both's orders preserved, characters interleaved)?

- Constraints: lengths ≤ 100 each.

## Examples

```
Input:  s1 = "aabcc", s2 = "dbbca", s3 = "aadbbcbcac"  -> Output: true
Input:  s1 = "aabcc", s2 = "dbbca", s3 = "aadbbbaccc"  -> Output: false
```

## Intuition — the state is (how much of s1 used, how much of s2 used)

`s3[i + j]` must be matched by either `s1[i]` or `s2[j]` — a two-source merge decision:

```
dfs(i, j):   i+j == len(s3) -> true (all consumed)
             match s1[i] == s3[i+j] -> try dfs(i+1, j)
             match s2[j] == s3[i+j] -> try dfs(i, j+1)
             memoize on (i, j)
```

**Why `(i, j)` and not an index into s3?** `s3`'s position is *derived*: `k = i + j`. Two pointers into the sources fully describe the state — the classic two-sequence DP ([2.2](minimum-edit-distance.md), [2.19](longest-increasing-subsequence.md) family) with a single merged target.

**Why the length check first?** `|s1| + |s2| != |s3|` can never interleave — the cheap reject before any DP.

**Why memoize?** The same `(i, j)` is reached via different merge orders (`s1 then s2` vs `s2 then s1` at the same prefix) — the [2.0](pattern-primer.md) overlapping-subproblems signature. `memo[i to j] = result` collapses the exponential recursion to O(m·n).

## Approach 1 — Brute force all merge orders (exponential)

Try every interleaving: correct, combinatorial blow-up.

## Approach 2 — Memoized (i, j) matching (the repo's version, optimal)

```kotlin
class InterleavingString {
    /**
     * @param s1 first source string
     * @param s2 second source string
     * @param s3 target interleaved string
     * @return   true iff s3 interleaves s1 and s2
     */
    fun isInterleave(s1: String, s2: String, s3: String): Boolean {
        if (s1.length + s2.length != s3.length) return false

        val memo = mutableMapOf<Pair<Int, Int>, Boolean>()

        fun dfs(i: Int, j: Int): Boolean {
            if (i == s1.length && j == s2.length) return true
            if (memo.containsKey(i to j)) return memo[i to j]!!

            val k = i + j
            var result = false

            if (i < s1.length && s1[i] == s3[k] && dfs(i + 1, j)) {
                result = true
            }
            if (j < s2.length && s2[j] == s3[k] && dfs(i, j + 1)) {
                result = true
            }

            memo[i to j] = result
            return result
        }

        return dfs(0, 0)
    }
}
```

```java
import java.util.*;

public class InterleavingString {
    /**
     * @param s1 first source string
     * @param s2 second source string
     * @param s3 target interleaved string
     * @return   true iff s3 interleaves s1 and s2
     */
    public boolean isInterleave(String s1, String s2, String s3) {
        if (s1.length() + s2.length() != s3.length()) return false;

        Map<Integer, Boolean> memo = new HashMap<>();       // key: i * (n+1) + j
        return dfs(s1, s2, s3, 0, 0, memo);
    }

    private boolean dfs(String s1, String s2, String s3, int i, int j, Map<Integer, Boolean> memo) {
        if (i == s1.length() && j == s2.length()) return true;

        int key = i * (s2.length() + 1) + j;
        if (memo.containsKey(key)) return memo.get(key);

        int k = i + j;
        boolean ok = (i < s1.length() && s1.charAt(i) == s3.charAt(k) && dfs(s1, s2, s3, i + 1, j, memo))
                  || (j < s2.length() && s2.charAt(j) == s3.charAt(k) && dfs(s1, s2, s3, i, j + 1, memo));
        memo.put(key, ok);
        return ok;
    }
}
```

```cpp
#include <string>
#include <vector>

class InterleavingString {
public:
    /**
     * @param s1 first source string
     * @param s2 second source string
     * @param s3 target interleaved string
     * @return   true iff s3 interleaves s1 and s2
     */
    bool isInterleave(std::string s1, std::string s2, std::string s3) {
        int m = s1.size(), n = s2.size();
        if (m + n != (int)s3.size()) return false;

        std::vector<std::vector<int>> memo(m + 1, std::vector<int>(n + 1, -1));

        std::function<bool(int, int)> dfs = [&](int i, int j) -> bool {
            if (i == m && j == n) return true;
            if (memo[i][j] != -1) return memo[i][j];

            int k = i + j;
            bool ok = (i < m && s1[i] == s3[k] && dfs(i + 1, j))
                   || (j < n && s2[j] == s3[k] && dfs(i, j + 1));
            return memo[i][j] = ok;
        };

        return dfs(0, 0);
    }
};
```

```python
def is_interleave(s1: str, s2: str, s3: str) -> bool:
    """
    @param s1: first source string
    @param s2: second source string
    @param s3: target interleaved string
    @return:   true iff s3 interleaves s1 and s2
    """
    if len(s1) + len(s2) != len(s3):
        return False

    from functools import lru_cache

    @lru_cache(None)
    def dfs(i: int, j: int) -> bool:
        if i == len(s1) and j == len(s2):
            return True
        k = i + j
        return (i < len(s1) and s1[i] == s3[k] and dfs(i + 1, j)) or \
               (j < len(s2) and s2[j] == s3[k] and dfs(i, j + 1))

    return dfs(0, 0)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param s1 first source string
    /// @param s2 second source string
    /// @param s3 target interleaved string
    /// @return   true iff s3 interleaves s1 and s2
    pub fn is_interleave(s1: String, s2: String, s3: String) -> bool {
        if s1.len() + s2.len() != s3.len() { return false; }

        let (b1, b2, b3) = (s1.as_bytes(), s2.as_bytes(), s3.as_bytes());
        let mut memo: HashMap<(usize, usize), bool> = HashMap::new();

        fn dfs(b1: &[u8], b2: &[u8], b3: &[u8], i: usize, j: usize,
               memo: &mut HashMap<(usize, usize), bool>) -> bool {
            if i == b1.len() && j == b2.len() { return true; }
            if let Some(&v) = memo.get(&(i, j)) { return v; }

            let k = i + j;
            let ok = (i < b1.len() && b1[i] == b3[k] && dfs(b1, b2, b3, i + 1, j, memo))
                  || (j < b2.len() && b2[j] == b3[k] && dfs(b1, b2, b3, i, j + 1, memo));
            memo.insert((i, j), ok);
            ok
        }

        dfs(b1, b2, b3, 0, 0, &mut memo)
    }
}
```

## Dry run

**Input:** `s1 = "aabcc"`, `s2 = "dbbca"`, `s3 = "aadbbcbcac"` → true.

```
dfs(0,0): k=0.  s1[0]='a' == s3[0]='a' -> dfs(1,0)
dfs(1,0): k=1.  s1[1]='a' == s3[1]='a' -> dfs(2,0)
dfs(2,0): k=2.  s1[2]='b' vs s3[2]='d' no.  s2[0]='d' == 'd' -> dfs(2,1)
dfs(2,1): k=3.  s2[1]='b' == s3[3]='b' -> dfs(2,2)
dfs(2,2): k=4.  s2[2]='b' == s3[4]='b' -> dfs(2,3)
dfs(2,3): k=5.  s1[2]='b' == s3[5]='c'? no.  s2[3]='c' == 'c' -> dfs(2,4)
dfs(2,4): k=6.  s1[2]='b' == s3[6]='b' -> dfs(3,4)
dfs(3,4): k=7.  s1[3]='c' == s3[7]='c' -> dfs(4,4)
dfs(4,4): k=8.  s1[4]='c' == s3[8]='a'? no.  s2[4]='a' == 'a' -> dfs(4,5)
dfs(4,5): k=9.  s1[4]='c' == s3[9]='c' -> dfs(5,5) -> true ✓

s3 = "aadbbbaccc": the second 'b' at index 4 can't be matched when s2 is exhausted at index 3
                   and s1's next is 'c' -> dfs paths fail at (2,3)/... -> false ✓
```

The merge-order decisions are visible: `a a` from s1, then `d b b c` from s2, then `b c a c` alternating — every step is one source matching the next `s3` char. The memo catches the same `(i,j)` reached via different orders — the recursion tree's overlapping subtrees collapse.

## Complexity

**Time.** States (i, j) × O(1):

$$
T(m, n) = O(m \cdot n)
$$

**Space.** The memo:

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Edit Distance** ([2.2](minimum-edit-distance.md)) — the two-sequence DP sibling with edit costs.
- **Word Break** ([13.2](../ch13-tries/word-break.md)) — the single-sequence matching DP.
- **Interview follow-up:** "Why is `k = i + j` free?" The total consumed is always `i + j` characters — s3's position is determined, not chosen. That's what makes `(i, j)` a complete state; a third index would be redundant and a third dimension would be a bug.
