# 9.8 Find The Index Of The First Occurrence (Rabin-Karp)

> **Source:** [`src/main/kotlin/string/pattern_matching/FindTheIndexofTheFirstOccurrenceIna String_RabinKarp.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/pattern_matching/FindTheIndexofTheFirstOccurrenceIna%20String_RabinKarp.kt)
> **Pattern:** rolling hash · **Core page**

## The Problem

Given `haystack` and `needle`, return the index of the first occurrence of `needle` in `haystack`, or `-1`.

- Constraints: $1 \le n, m \le 10^4$; lowercase letters.

## Examples

```
Input:  haystack = "sadbutsad", needle = "sad"   -> Output: 0
Input:  haystack = "leetcode", needle = "leeto"  -> Output: -1
```

## Intuition — compare *hashes* instead of substrings

Naively, each window comparison is O(m) → O(n·m) total. **Rabin-Karp** hashes the window in O(1) per slide and only *confirms* with a real comparison on a hash match:

1. Hash `needle` and the first window of `haystack` (both length m) — base 26, mod P:
   `hash = (hash * base + charValue(c)) % mod`.
2. Slide: remove the leftmost character's contribution (`window - c_old * base^(m-1)`), add the new one (`window * base + c_new`) — both O(1).
3. On hash equality, **verify with `matches()`** (the hash collision guard) and return the index.

**Why base-26 and a large prime mod?** The string is a base-26 number; the mod keeps it bounded. Collisions are possible (two different strings, same hash) — hence the confirmation pass. With a large prime, false matches are rare, so the expected cost is O(n + m).

**The `power` factor** — `base^(m-1)` is precomputed once (multiplying `power * base` for the first m-1 chars) and used to strip the leaving character. The modular-arithmetic hygiene (`+ mod` before `% mod`) keeps the subtraction non-negative.

**The repository's `matches()`** does the final O(m) verification — the "hash says yes, check the real thing" step that makes the algorithm *correct* rather than *probabilistic*.

## Approach 1 — Sliding window with direct comparison (O(n·m))

Compare each window character-by-character: simple, worst-case quadratic.

## Approach 2 — Rolling hash with confirmation (the repo's version, optimal)

```kotlin
class FindTheIndexOfTheFirstOccurrenceInString_RabinKarp {
    /**
     * @param haystack the string to search in
     * @param needle   the pattern to find
     * @return         first index of needle in haystack, or -1
     */
    fun strStr(haystack: String, needle: String): Int {
        if (needle.isEmpty()) return 0
        if (haystack.length < needle.length) return -1

        val base = 26
        val mod = 1_000_000_007
        val m = needle.length
        var targetHash = 0L
        var windowHash = 0L
        var power = 1L

        // Precompute needle hash and initial window hash
        for (i in 0 until m) {
            targetHash = (targetHash * base + charValue(needle[i])) % mod
            windowHash = (windowHash * base + charValue(haystack[i])) % mod
            if (i < m - 1) power = (power * base) % mod
        }

        // Early check for a match at index 0
        if (windowHash == targetHash && matches(haystack, needle, 0)) {
            return 0
        }

        // Slide the window and update the hash in O(1)
        for (i in m until haystack.length) {
            // Remove the leftmost character and add the new one
            windowHash = (windowHash - charValue(haystack[i - m]) * power % mod + mod) % mod
            windowHash = (windowHash * base + charValue(haystack[i])) % mod

            val startIndex = i - m + 1
            if (windowHash == targetHash && matches(haystack, needle, startIndex)) {
                return startIndex
            }
        }
        return -1
    }

    private fun charValue(c: Char): Int = c - 'a'

    private fun matches(haystack: String, needle: String, start: Int): Boolean {
        for (j in needle.indices) {
            if (haystack[start + j] != needle[j]) return false
        }
        return true
    }
}
```

```java
public class FindTheIndexOfTheFirstOccurrence {
    /**
     * @param haystack the string to search in
     * @param needle   the pattern to find
     * @return         first index of needle in haystack, or -1
     */
    public int strStr(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        if (haystack.length() < needle.length()) return -1;

        int base = 26;
        long mod = 1_000_000_007L;
        int m = needle.length();
        long target = 0, window = 0, power = 1;

        for (int i = 0; i < m; i++) {
            target = (target * base + charValue(needle.charAt(i))) % mod;
            window = (window * base + charValue(haystack.charAt(i))) % mod;
            if (i < m - 1) power = power * base % mod;
        }

        if (window == target && matches(haystack, needle, 0)) return 0;

        for (int i = m; i < haystack.length(); i++) {
            window = (window - charValue(haystack.charAt(i - m)) * power % mod + mod) % mod;
            window = (window * base + charValue(haystack.charAt(i))) % mod;

            int start = i - m + 1;
            if (window == target && matches(haystack, needle, start)) return start;
        }
        return -1;
    }

    private int charValue(char c) { return c - 'a'; }

    private boolean matches(String haystack, String needle, int start) {
        for (int j = 0; j < needle.length(); j++) {
            if (haystack.charAt(start + j) != needle.charAt(j)) return false;
        }
        return true;
    }
}
```

```cpp
#include <string>

class FindTheIndexOfTheFirstOccurrence {
    int charValue(char c) { return c - 'a'; }

    bool matches(const std::string& haystack, const std::string& needle, int start) {
        for (int j = 0; j < (int)needle.size(); j++) {
            if (haystack[start + j] != needle[j]) return false;
        }
        return true;
    }

public:
    /**
     * @param haystack the string to search in
     * @param needle   the pattern to find
     * @return         first index of needle in haystack, or -1
     */
    int strStr(std::string haystack, std::string needle) {
        if (needle.empty()) return 0;
        if (haystack.size() < needle.size()) return -1;

        const long long base = 26, mod = 1'000'000'007LL;
        int m = needle.size();
        long long target = 0, window = 0, power = 1;

        for (int i = 0; i < m; i++) {
            target = (target * base + charValue(needle[i])) % mod;
            window = (window * base + charValue(haystack[i])) % mod;
            if (i < m - 1) power = power * base % mod;
        }

        if (window == target && matches(haystack, needle, 0)) return 0;

        for (int i = m; i < (int)haystack.size(); i++) {
            window = (window - charValue(haystack[i - m]) * power % mod + mod) % mod;
            window = (window * base + charValue(haystack[i])) % mod;

            int start = i - m + 1;
            if (window == target && matches(haystack, needle, start)) return start;
        }
        return -1;
    }
};
```

```python
def str_str(haystack: str, needle: str) -> int:
    """
    @param haystack: the string to search in
    @param needle:   the pattern to find
    @return:         first index of needle in haystack, or -1
    """
    if not needle:
        return 0
    if len(haystack) < len(needle):
        return -1

    base, mod = 26, 1_000_000_007
    m = len(needle)

    target = 0
    window = 0
    power = 1
    for i in range(m):
        target = (target * base + ord(needle[i]) - ord("a")) % mod
        window = (window * base + ord(haystack[i]) - ord("a")) % mod
        if i < m - 1:
            power = power * base % mod

    def matches(start: int) -> bool:
        return haystack[start:start + m] == needle     # confirm (collision guard)

    if window == target and matches(0):
        return 0

    for i in range(m, len(haystack)):
        window = (window - (ord(haystack[i - m]) - ord("a")) * power % mod + mod) % mod
        window = (window * base + ord(haystack[i]) - ord("a")) % mod

        start = i - m + 1
        if window == target and matches(start):
            return start
    return -1
```

```rust
impl Solution {
    /// @param haystack the string to search in
    /// @param needle   the pattern to find
    /// @return         first index of needle in haystack, or -1
    pub fn str_str(haystack: String, needle: String) -> i32 {
        let h = haystack.as_bytes();
        let n = needle.as_bytes();
        if n.is_empty() { return 0; }
        if h.len() < n.len() { return -1; }

        const BASE: i64 = 26;
        const MOD: i64 = 1_000_000_007;
        let m = n.len();
        let (mut target, mut window, mut power) = (0i64, 0i64, 1i64);

        for i in 0..m {
            target = (target * BASE + (n[i] - b'a') as i64) % MOD;
            window = (window * BASE + (h[i] - b'a') as i64) % MOD;
            if i < m - 1 { power = power * BASE % MOD; }
        }

        let matches = |start: usize| h[start..start + m] == n;   // confirm (collision guard)

        if window == target && matches(0) { return 0; }

        for i in m..h.len() {
            window = (window - (h[i - m] - b'a') as i64 * power % MOD + MOD) % MOD;
            window = (window * BASE + (h[i] - b'a') as i64) % MOD;

            let start = i - m + 1;
            if window == target && matches(start) { return start as i32; }
        }
        -1
    }
}
```

## Dry run

**Input:** `haystack = "sadbutsad"`, `needle = "sad"` (`s=18, a=0, d=3`, base 26).

```
target hash = ((18)*26 + 0)*26 + 3 = 468*26 + 3 = 12171
window hash at index 0 ("sad") = 12171.  window == target && matches("sad" == "sad") -> return 0 ✓

Input: haystack = "leetcode", needle = "leeto":
  target = hash("leeto"); every window hash differs -> loop exhausts -> -1 ✓
```

The rolling update at work (conceptually): sliding from `"sad"` to `"adb"` subtracts `s * 26^2`, multiplies by 26, adds `b` — three O(1) modular ops replacing an O(m) comparison. The `matches()` call on hash equality is what makes the occasional collision harmless.

## Complexity

**Time.** Expected linear (hash ops + rare confirmations):

$$
T(n, m) = O(n + m) \text{ expected}, \quad O(n \cdot m) \text{ worst (collisions)}
$$

**Space.** A few scalars:

$$
S(n, m) = O(1)
$$

## Variants & follow-ups

- **The naive/KMP versions** (`string/pattern_matching/`) — the plain window comparison and the KMP automaton; Rabin-Karp is the "compare hashes" middle ground.
- **Repeated DNA Sequences / rolling-hash family** — the same sliding hash for "find any m-length repeat" problems.
- **Interview follow-up:** "Why is the confirmation pass necessary?" Hash collisions are possible (two strings can share a base-26 residue mod P). Without `matches()`, a false hash match would return a wrong index. With it, correctness is exact — the hash only *filters*; the comparison *decides*.
