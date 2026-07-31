# 9.10 Find The Index Of The First Occurrence (KMP)

> **Source:** [`src/main/kotlin/string/pattern_matching/FindTheIndexofTheFirstOccurrenceIna String.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/pattern_matching/FindTheIndexofTheFirstOccurrenceIna%20String.kt)
> **Pattern:** KMP with the LPS array · **Core page**

## The Problem

Given `haystack` and `needle`, return the index of the first occurrence of `needle`, or `-1` — in **worst-case O(n + m)** (the guarantee [9.8](find-the-index-of-the-first-occurrence.md)'s naive version lacks).

- Constraints: $1 \le n, m \le 10^4$; lowercase letters.

## Examples

```
Input:  haystack = "sadbutsad", needle = "sad"   -> Output: 0
Input:  haystack = "aabaabaafa", needle = "aabaaf" -> Output: 3
```

## Intuition — never re-match what the pattern already told you

The naive matcher backtracks `j` to 0 on every mismatch — re-comparing already-seen characters. **KMP's insight:** after a partial match, the *pattern itself* knows how much of itself is already satisfied. The **LPS (Longest Prefix which is also Suffix) array** encodes that: `lps[i]` = the longest proper prefix of `needle[0..i]` that is also a suffix.

On a mismatch, instead of resetting `j = 0`, jump `j = lps[j - 1]` — the matched prefix so far already contains a suffix equal to a prefix of the pattern. The text pointer `i` **never moves backward**; only `j` (the pattern pointer) retreats.

**Building the LPS is self-matching:** `buildLPS` runs the same "if equal, extend; else fall back" logic on the pattern against itself — `lps` doubles as both the structure and its own construction proof. The [9.8](find-the-index-of-the-first-occurrence.md) Rabin-Karp page compares hashes; KMP compares characters with *guaranteed* linear time — no collisions, no mod.

**Why worst-case linear?** `i` advances on every match or start-mismatch; `j` can only fall back as far as it rose (each `lps[j-1]` jump consumes a previous rise). Both pointers are amortized O(n + m).

## Approach 1 — Naive / Rabin-Karp (see [9.8](find-the-index-of-the-first-occurrence.md))

Naive is O(n·m) worst; Rabin-Karp is expected linear but probabilistic-ish (needs the confirmation pass).

## Approach 2 — KMP with the LPS array (the repo's version, optimal)

```kotlin
class `FindTheIndexofTheFirstOccurrenceIna String` {
    /**
     * @param haystack the string to search in
     * @param needle   the pattern to find
     * @return         first index of needle in haystack, or -1
     */
    fun strStr(haystack: String, needle: String): Int {
        if (needle.isEmpty()) return 0

        val (m, n) = needle.length to haystack.length
        val lps = buildLPS(needle)

        var (i, j) = 0 to 0          // i walks haystack, j walks needle

        while (i < n) {
            when {
                haystack[i] == needle[j] -> {      // characters match
                    i++; j++
                }
                j > 0 -> j = lps[j - 1]            // mismatch after a partial match: fall back
                else -> i++                        // mismatch at the start: advance text only
            }

            if (j == m) return i - j               // the whole pattern matched
        }
        return -1
    }

    // LPS: longest proper prefix of needle[0..i] that is also a suffix
    fun buildLPS(needle: String): IntArray {
        val lps = IntArray(needle.length)
        var (i, j) = 0 to 1

        while (j < needle.length) {
            when {
                needle[j] == needle[i] -> lps[j++] = ++i   // extend the prefix-suffix
                i != 0 -> i = lps[i - 1]                    // fall back like the search loop
                else -> lps[j++] = 0                        // no prefix-suffix at j
            }
        }
        return lps
    }
}
```

```java
public class FindTheIndexOfTheFirstOccurrenceKmp {
    /**
     * @param haystack the string to search in
     * @param needle   the pattern to find
     * @return         first index of needle in haystack, or -1
     */
    public int strStr(String haystack, String needle) {
        if (needle.isEmpty()) return 0;

        int[] lps = buildLPS(needle);
        int i = 0, j = 0;

        while (i < haystack.length()) {
            if (haystack.charAt(i) == needle.charAt(j)) { i++; j++; }
            else if (j > 0) j = lps[j - 1];              // fall back
            else i++;                                    // mismatch at the start

            if (j == needle.length()) return i - j;      // pattern found
        }
        return -1;
    }

    private int[] buildLPS(String needle) {
        int[] lps = new int[needle.length()];
        int i = 0, j = 1;
        while (j < needle.length()) {
            if (needle.charAt(j) == needle.charAt(i)) lps[j++] = ++i;
            else if (i != 0) i = lps[i - 1];
            else lps[j++] = 0;
        }
        return lps;
    }
}
```

```cpp
#include <string>
#include <vector>

class FindTheIndexOfTheFirstOccurrenceKmp {
    std::vector<int> buildLPS(const std::string& needle) {
        std::vector<int> lps(needle.size(), 0);
        int len = 0, i = 1;
        while (i < (int)needle.size()) {
            if (needle[i] == needle[len]) lps[i++] = ++len;
            else if (len != 0) len = lps[len - 1];
            else lps[i++] = 0;
        }
        return lps;
    }

public:
    /**
     * @param haystack the string to search in
     * @param needle   the pattern to find
     * @return         first index of needle in haystack, or -1
     */
    int strStr(std::string haystack, std::string needle) {
        if (needle.empty()) return 0;

        auto lps = buildLPS(needle);
        int i = 0, j = 0;
        while (i < (int)haystack.size()) {
            if (haystack[i] == needle[j]) { i++; j++; }
            else if (j > 0) j = lps[j - 1];              // fall back
            else i++;                                    // mismatch at the start

            if (j == (int)needle.size()) return i - j;   // pattern found
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

    def build_lps(p: str) -> list[int]:
        lps = [0] * len(p)
        i, j = 0, 1
        while j < len(p):
            if p[j] == p[i]:
                i += 1
                lps[j] = i
                j += 1
            elif i != 0:
                i = lps[i - 1]
            else:
                lps[j] = 0
                j += 1
        return lps

    lps = build_lps(needle)
    i = j = 0
    while i < len(haystack):
        if haystack[i] == needle[j]:
            i += 1
            j += 1
        elif j > 0:
            j = lps[j - 1]              # fall back
        else:
            i += 1                      # mismatch at the start

        if j == len(needle):
            return i - j                # pattern found
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

        let mut lps = vec![0usize; n.len()];
        let (mut i, mut j) = (0usize, 1usize);
        while j < n.len() {                          // build LPS
            if n[j] == n[i] { i += 1; lps[j] = i; j += 1; }
            else if i != 0 { i = lps[i - 1]; }
            else { j += 1; }
        }

        let (mut i, mut j) = (0usize, 0usize);
        while i < h.len() {
            if h[i] == n[j] { i += 1; j += 1; }
            else if j > 0 { j = lps[j - 1]; }        // fall back
            else { i += 1; }                         // mismatch at the start

            if j == n.len() { return (i - j) as i32; }  // pattern found
        }
        -1
    }
}
```

## Dry run

**Input:** `haystack = "aabaabaafa"`, `needle = "aabaaf"`.

```
buildLPS("aabaaf"): a a b a a f
  lps = [0,1,0,1,2,0]   (the "aa" suffix of the first four chars has length 2)

search:
i=0..4: a,a,b,a,a match (j=5).  i=5: h[5]='b' vs n[5]='f' MISMATCH.
  j = lps[4] = 2.   (the matched prefix "aabaa" has suffix "aa" = prefix of needle)
i=5: h[5]='b' vs n[2]='b' match -> i=6,j=3.  h[6]='a' vs n[3]='a' -> i=7,j=4.
h[7]='a' vs n[4]='a' -> i=8,j=5.  h[8]='f' vs n[5]='f' -> i=9,j=6 == m -> return 9-6=3 ✓
```

The mismatch at `i=5` is the KMP moment: instead of restarting at `j=0` (and re-reading `aaba`), the LPS jump to `j=2` resumes at the `'b'` already aligned — the text pointer never retreats. Total work: one pass over the haystack plus one over the pattern.

## Complexity

**Time.** Both pointers advance monotonically-ish:

$$
T(n, m) = O(n + m) \quad \text{worst case — guaranteed}
$$

**Space.** The LPS array:

$$
S(m) = O(m)
$$

## Variants & follow-ups

- **Rabin-Karp version** ([9.8](find-the-index-of-the-first-occurrence.md)) — expected linear via rolling hashes; KMP is the *guaranteed* linear character-based answer. The two pages side by side are the "compare the two big pattern-matching ideas" interview moment.
- **Repeated String Match / strStr family** — the same search with rotations and repetitions.
- **Interview follow-up:** "Why can `i` never move backward?" Every mismatch either advances `i` (start mismatch) or shrinks `j` using the LPS — which only ever returns to a position the pattern already matched. So the text is scanned once, and the pattern's retries are bounded by its own prefix structure. That's the amortized linearity.
