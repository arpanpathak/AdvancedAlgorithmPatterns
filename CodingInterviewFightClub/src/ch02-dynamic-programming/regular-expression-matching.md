# 2.23 Regular Expression Matching

> **Source:** [`src/main/kotlin/string/dynamic_programming/RegularExpressionMatching.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/dynamic_programming/RegularExpressionMatching.kt)
> **Pattern:** (i, j) memo with `*` backtracking · **Core page**

## The Problem

Full-match `s` against pattern `p` where `.` matches any char and `*` repeats the **preceding** char zero or more times.

- Constraints: lengths ≤ 20 (the classic hard).

## Examples

```
Input:  s = "aa", p = "a"    -> Output: false
Input:  s = "aa", p = "a*"   -> Output: true
Input:  s = "ab", p = ".*"   -> Output: true
Input:  s = "aab", p = "c*a*b" -> Output: true
```

## Intuition — the `*` makes it a two-choice decision

At `(i, j)` the current chars either match (`.` or equal), and the *next* pattern char decides:

- `p[j+1] == '*'` → **two ways**: skip the `X*` group entirely (`dfs(i, j+2)`) or consume one char if it matches (`dfs(i+1, j)` — stay on the `*`);
- otherwise a plain match → `dfs(i+1, j+1)`;
- mismatch → false.

```kotlin
fun dfs(s: String, p: String, i: Int, j: Int): Boolean {
    val match = (i < s.length) && (s[i] == p[j] || p[j] == '.')
    return when {
        i >= s.length && j >= p.length -> true
        j >= p.length -> false
        j + 1 < p.length && p[j + 1] == '*' ->
            dfs(s, p, i, j + 2) || (match && dfs(s, p, i + 1, j))
        match -> dfs(s, p, i + 1, j + 1)
        else -> false
    }
}
```

**Why `dfs(i, j+2)` OR `dfs(i+1, j)`?** The `X*` group can match *zero* (skip both chars) or *one-or-more* (consume one `s` char, stay on the `*` — the loop is the recursion). Either path succeeding means the whole match succeeds — the [2.0](pattern-primer.md) "OR over choices" DP signature.

**Why the state is `(i, j)`?** The pattern position and string position fully determine the remainder; the same `(i, j)` recurs via different `*`-consumption paths. The repo's `"$i.$j"` string key memos it — the [2.22](interleaving-string.md) two-pointer state idiom.

## Approach 1 — Brute force backtracking (exponential)

Try all `*`-consumptions: correct, blows up on `a*a*a*...` patterns.

## Approach 2 — Memoized (i, j) DFS (the repo's version, optimal)

```kotlin
class RegularExpressionMatching {
    private val dp = mutableMapOf<String, Boolean>()

    /**
     * @param s input string
     * @param p pattern with . and *
     * @return  true iff s fully matches p
     */
    fun isMatch(s: String, p: String): Boolean {
        return dfs(s, p, 0, 0)
    }

    private fun dfs(s: String, p: String, i: Int, j: Int): Boolean {
        val state = "$i.$j"
        return when {
            dp.contains(state) -> dp[state]!!
            i >= s.length && j >= p.length -> true
            j >= p.length -> false
            else -> {
                val match = (i < s.length) && (s[i] == p[j] || p[j] == '.')

                when {
                    j + 1 < p.length && p[j + 1] == '*'
                        -> dfs(s, p, i, j + 2) || (match && dfs(s, p, i + 1, j))
                    match -> dfs(s, p, i + 1, j + 1)
                    else -> false
                }
            }
        }.also { dp[state] = it }
    }
}
```

```java
import java.util.*;

public class RegularExpressionMatching {
    private Map<String, Boolean> memo = new HashMap<>();

    /**
     * @param s input string
     * @param p pattern with . and *
     * @return  true iff s fully matches p
     */
    public boolean isMatch(String s, String p) {
        return dfs(s, p, 0, 0);
    }

    private boolean dfs(String s, String p, int i, int j) {
        String key = i + "." + j;
        if (memo.containsKey(key)) return memo.get(key);
        if (i >= s.length() && j >= p.length()) return true;
        if (j >= p.length()) return false;

        boolean match = i < s.length() && (s.charAt(i) == p.charAt(j) || p.charAt(j) == '.');

        boolean result;
        if (j + 1 < p.length() && p.charAt(j + 1) == '*') {
            result = dfs(s, p, i, j + 2) || (match && dfs(s, p, i + 1, j));
        } else if (match) {
            result = dfs(s, p, i + 1, j + 1);
        } else {
            result = false;
        }
        memo.put(key, result);
        return result;
    }
}
```

```cpp
#include <string>
#include <unordered_map>

class RegularExpressionMatching {
    std::unordered_map<std::string, bool> memo;

    bool dfs(const std::string& s, const std::string& p, int i, int j) {
        std::string key = std::to_string(i) + "." + std::to_string(j);
        if (memo.count(key)) return memo[key];
        if (i >= (int)s.size() && j >= (int)p.size()) return true;
        if (j >= (int)p.size()) return false;

        bool match = i < (int)s.size() && (s[i] == p[j] || p[j] == '.');

        bool result;
        if (j + 1 < (int)p.size() && p[j + 1] == '*') {
            result = dfs(s, p, i, j + 2) || (match && dfs(s, p, i + 1, j));
        } else if (match) {
            result = dfs(s, p, i + 1, j + 1);
        } else {
            result = false;
        }
        memo[key] = result;
        return result;
    }

public:
    /**
     * @param s input string
     * @param p pattern with . and *
     * @return  true iff s fully matches p
     */
    bool isMatch(std::string s, std::string p) {
        return dfs(s, p, 0, 0);
    }
};
```

```python
def is_match(s: str, p: str) -> bool:
    """
    @param s: input string
    @param p: pattern with . and *
    @return:  true iff s fully matches p
    """
    from functools import lru_cache

    @lru_cache(None)
    def dfs(i: int, j: int) -> bool:
        if i >= len(s) and j >= len(p):
            return True
        if j >= len(p):
            return False

        match = i < len(s) and (s[i] == p[j] or p[j] == ".")

        if j + 1 < len(p) and p[j + 1] == "*":
            return dfs(i, j + 2) or (match and dfs(i + 1, j))
        if match:
            return dfs(i + 1, j + 1)
        return False

    return dfs(0, 0)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param s input string
    /// @param p pattern with . and *
    /// @return  true iff s fully matches p
    pub fn is_match(s: String, p: String) -> bool {
        let (b, q) = (s.as_bytes(), p.as_bytes());
        let mut memo: HashMap<(usize, usize), bool> = HashMap::new();

        fn dfs(b: &[u8], q: &[u8], i: usize, j: usize,
               memo: &mut HashMap<(usize, usize), bool>) -> bool {
            if let Some(&v) = memo.get(&(i, j)) { return v; }
            if i >= b.len() && j >= q.len() { return true; }
            if j >= q.len() { return false; }

            let matched = i < b.len() && (b[i] == q[j] || q[j] == b'.');

            let result = if j + 1 < q.len() && q[j + 1] == b'*' {
                dfs(b, q, i, j + 2, memo) || (matched && dfs(b, q, i + 1, j, memo))
            } else if matched {
                dfs(b, q, i + 1, j + 1, memo)
            } else {
                false
            };
            memo.insert((i, j), result);
            result
        }

        dfs(b, q, 0, 0, &mut memo)
    }
}
```

## Dry run

**Input:** `s = "aab"`, `p = "c*a*b"`.

```
dfs(0,0): p[0]='c', p[1]='*' -> skip: dfs(0,2) OR match('a'=='c'? no)
  dfs(0,2): p[2]='a', p[3]='*' -> skip: dfs(0,4) OR (match 'a'=='a' && dfs(1,2))
    dfs(0,4): p[4]='b', no '*'.  match 'a'=='b'? no -> false
    dfs(1,2): p[2]='a', p[3]='*' -> skip: dfs(1,4) OR (match && dfs(2,2))
      dfs(1,4): 'a'=='b'? no -> false
      dfs(2,2): match 'a'=='a' && dfs(3,2):
        dfs(3,2): i>=len(3), p='a*...' j=2: match? i>=len -> no.  skip: dfs(3,4): 'b'=='b' && dfs(4,4): both done -> true!
  (true propagates up) -> dfs(2,2) true -> dfs(1,2) true -> dfs(0,2) true

Output: true ✓
```

The `*`-decision tree: `c*` matches zero c's, `a*` matches two a's, `b` matches b. The `dfs(i, j+2)` (skip) and `dfs(i+1, j)` (consume) branches explore exactly the "how many times does the star repeat?" question — the memo collapses the repeated states (`(2,2)` reached via different consumption counts).

## Complexity

**Time.** States (i, j):

$$
T(m, n) = O(m \cdot n)
$$

**Space.** The memo:

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Wildcard Matching** (`string/dynamic_programming/WildCardMatching.kt`) — `*` matches *any* run: a different recurrence.
- **Interleaving String** ([2.22](interleaving-string.md)) — the same (i, j) two-pointer memo family.
- **Interview follow-up:** "Why is `dfs(i, j+2)` before `dfs(i+1, j)`?" The skip branch (zero matches) must be tried even when `match` is false — `c*` with no c's. The OR short-circuits: if zero works, don't explore consumption. The order also keeps the base cases (empty s, empty p) reachable first.
