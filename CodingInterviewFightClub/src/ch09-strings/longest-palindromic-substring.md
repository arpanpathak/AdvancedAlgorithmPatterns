# 9.4 Longest Palindromic Substring

> **Source:** [`src/main/kotlin/string/LongestPalidnromicSubstring.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/LongestPalidnromicSubstring.kt)
> **Pattern:** expand around center · **Core page**

## The Problem

Given a string `s`, return the **longest palindromic substring** in `s` (if several, any one).

- Constraints: $1 \le n \le 1000$; printable ASCII characters.

## Examples

```
Input:  s = "babad"    -> Output: "bab"  (or "aba" — both length 3)
Input:  s = "cbbd"     -> Output: "bb"
Input:  s = "a"        -> Output: "a"
```

## Intuition — every palindrome has a center

Every palindrome is *symmetric around a center* — and there are exactly **$2n - 1$** possible centers in a string of length $n$:

- the $n$ **characters** (odd-length palindromes: `"aba"` centered at `'b'`);
- the $n - 1$ **gaps** between characters (even-length palindromes: `"abba"` centered between the two `b`s).

For each center, **expand outward** while the mirrored characters match; the expanded length is the palindrome at that center. Take the max over all centers.

**Why is this $O(n^2)$ acceptable?** $n \le 1000$ means $2n \approx 2000$ centers, each expanding at most $n$ steps → about $2 \times 10^6$ character comparisons — instant. The DP alternative is also $O(n^2)$ time but needs $O(n^2)$ memory; expansion is $O(1)$ memory. At $n = 1000$ the memory difference is the deciding factor between the two.

**Why not try every substring and check?** That's $O(n^3)$ (choose start/end, verify). The center formulation removes the "verify" factor: expanding *from* the center verifies while building.

The repo's `start = i - (len - 1) / 2` bookkeeping deserves attention: it converts the palindrome *length* back into the substring's *start index* — the one fiddly arithmetic on this page.

## Approach 1 — DP table (palindrome[i][j])

`pal[i][j] = (s[i] == s[j]) && (j - i < 2 || pal[i+1][j-1])`, tracking the longest `true` span: $O(n^2)$ time **and** $O(n^2)$ space. The classic alternative; expansion beats it on memory with identical time.

## Approach 2 — Expand around every center (the repo's version, optimal)

```kotlin
class LongestPalidnromicSubstring {
    /**
     * @param s input string
     * @return  a longest palindromic substring
     */
    fun longestPalindrome(s: String): String {
        if (s.isEmpty()) return ""

        var start = 0
        var maxLength = 1

        // Helper function to expand around the center
        fun expandAroundCenter(left: Int, right: Int): Int {
            var l = left
            var r = right
            while (l >= 0 && r < s.length && s[l] == s[r]) {
                l--
                r++
            }
            return r - l - 1                 // length of the palindrome found
        }

        for (i in 0 until s.length) {
            val len1 = expandAroundCenter(i, i)        // odd: center is s[i]
            val len2 = expandAroundCenter(i, i + 1)    // even: center between s[i], s[i+1]

            val len = maxOf(len1, len2)
            if (len > maxLength) {
                maxLength = len
                start = i - (len - 1) / 2              // convert length back to start index
            }
        }

        return s.substring(start, start + maxLength)
    }
}
```

```java
public class LongestPalindromicSubstring {
    private int lo = 0, maxLen = 0;

    /**
     * @param s input string
     * @return  a longest palindromic substring
     */
    public String longestPalindrome(String s) {
        if (s.length() < 2) return s;

        for (int i = 0; i < s.length(); i++) {
            expand(s, i, i);         // odd length: center is s[i]
            expand(s, i, i + 1);     // even length: center between s[i], s[i+1]
        }
        return s.substring(lo, lo + maxLen);
    }

    private void expand(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        if (right - left - 1 > maxLen) {          // length of the palindrome found
            maxLen = right - left - 1;
            lo = left + 1;
        }
    }
}
```

```cpp
#include <string>

class LongestPalindromicSubstring {
    int lo = 0, maxLen = 0;

    void expand(const std::string& s, int left, int right) {
        while (left >= 0 && right < (int)s.size() && s[left] == s[right]) {
            left--;
            right++;
        }
        if (right - left - 1 > maxLen) {          // length of the palindrome found
            maxLen = right - left - 1;
            lo = left + 1;
        }
    }

public:
    /**
     * @param s input string
     * @return  a longest palindromic substring
     */
    std::string longestPalindrome(std::string s) {
        if (s.size() < 2) return s;

        for (int i = 0; i < (int)s.size(); i++) {
            expand(s, i, i);         // odd length
            expand(s, i, i + 1);     // even length
        }
        return s.substr(lo, maxLen);
    }
};
```

```python
def longest_palindrome(s: str) -> str:
    """
    @param s: input string
    @return:  a longest palindromic substring
    """
    def expand(left: int, right: int) -> tuple[int, int]:
        while left >= 0 and right < len(s) and s[left] == s[right]:
            left -= 1
            right += 1
        return left + 1, right - left - 1      # (start, length) of the palindrome

    start = best = 0
    for i in range(len(s)):
        for l, length in (expand(i, i), expand(i, i + 1)):   # odd and even centers
            if length > best:
                best = length
                start = l
    return s[start:start + best]
```

```rust
impl Solution {
    /// @param s input string
    /// @return  a longest palindromic substring
    pub fn longest_palindrome(s: String) -> String {
        let bytes = s.as_bytes();
        let mut start = 0usize;
        let mut max_len = 0usize;

        fn expand(bytes: &[u8], mut l: isize, mut r: isize) -> (usize, usize) {
            while l >= 0 && (r as usize) < bytes.len() && bytes[l as usize] == bytes[r as usize] {
                l -= 1;
                r += 1;
            }
            (l as usize + 1, (r - l - 1) as usize)      // (start, length)
        }

        for i in 0..bytes.len() {
            for (l, len) in [expand(bytes, i as isize, i as isize),
                             expand(bytes, i as isize, i as isize + 1)] {
                if len > max_len { max_len = len; start = l; }
            }
        }
        s[start..start + max_len].to_string()
    }
}
```

## Dry run

**Input:** `s = "babad"`.

```
centers for odd length (i, i) and even length (i, i+1):

i=0 'b': odd:  expand(0,0): 'b'='b' -> l=-1,r=1 -> len 1.   even: expand(0,1): 'b' vs 'a' -> 0.
          maxLen=1, start=0
i=1 'a': odd:  expand(1,1): 'a'='a' -> l=0,r=2: 'b'='b' -> l=-1,r=3 -> len 3.
          len 3 > 1 -> maxLen=3, start = 1 - (3-1)/2 = 0.      ("bab")
          even: expand(1,2): 'a' vs 'b' -> 0.
i=2 'b': odd:  'b','a','b' -> len 3. not > 3.
          even: expand(2,3): 'b' vs 'a' -> 0.
i=3 'a': odd:  len 1. even: expand(3,4): 'a' vs 'd' -> 0.
i=4 'd': odd:  len 1. even: out of bounds -> 0.

Answer: s[0..3] = "bab" ✓   (the mirror-symmetric "aba" centered at i=2 is equally valid)
```

The bookkeeping line `start = i - (len-1)/2`: at `i=1` with `len=3`, the palindrome `"bab"` spans `0..2` — and `1 - 1 = 0` recovers that start. For an even palindrome (`len=4`, center between `i=2` and `i=3`), `i - (4-1)/2 = 2 - 1 = 1` also lands correctly. The integer division is doing exactly the "half-length left of center" arithmetic.

## Complexity

**Time.** $2n - 1$ centers, each expanding at most $n$ steps:

$$
T(n) = O(n^2)
$$

**Space.** No auxiliary structures beyond the answer:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Manacher's Algorithm** — the linear-time palindrome finder ($O(n)$); interviews rarely demand it, but naming it as the asymptotically-faster cousin of this page is a strong signal.
- **Longest Palindromic Subsequence** (`src/main/kotlin/string/dynamic_programming/LongestPalindromicSubsequence.kt`) — the *subsequence* version (deletions allowed) is a real DP: this page's "contiguous + symmetric" becomes "select a palindromic subset".
- **Palindrome Partitioning II** (`src/main/kotlin/string/dynamic_programming/PalindromePartitioning_II.kt`) — uses the same "is `s[i..j]` a palindrome?" facts, precomputed by expansion, then a separate DP for the minimum cuts.
- **Valid Palindrome / Valid Palindrome II** (`src/main/kotlin/string/ValidPalindrome.kt`, `ValidPalindrome_II.kt`) — the *checking* direction of the same symmetry idea, with two pointers from the ends.
- **Interview follow-up:** "Why not try all $n^2$ substrings?" Verifying each is $O(n)$ → $O(n^3)$. Expansion verifies *while* growing from the center, so the total work is the sum of the $2n-1$ expansions — $O(n^2)$ — with $O(1)$ memory, beating the DP table on space.
