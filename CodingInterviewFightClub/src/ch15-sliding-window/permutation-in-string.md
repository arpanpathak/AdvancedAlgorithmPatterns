# 15.8 Permutation In String

> **Source:** [`src/main/kotlin/string/hashtable/PermutationsInString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/hashtable/PermutationsInString.kt)
> **Pattern:** fixed-size anagram window · **Core page**

## The Problem

Given `s1` and `s2`, return `true` if `s2` contains a **permutation of `s1`** as a substring.

- Constraints: $1 \le n, m \le 10^4$; lowercase letters.

## Examples

```
Input:  s1 = "ab", s2 = "eidbaooo"   -> Output: true   (the window "ba" at index 2)
Input:  s1 = "ab", s2 = "eidboaoo"   -> Output: false
```

## Intuition — a permutation of `s1` is any window of length `|s1|` with matching letter counts

"Permutation" is the anagram relation — [9.1](../ch09-strings/valid-anagram.md) said it: same multiset of letters. So the question becomes: **does any fixed-size window of `s2` (length `|s1|`) have the same frequency array as `s1`?** That's the [15.4](maximum-average-subarray.md) fixed-size window machinery with a frequency-array comparison instead of a sum:

```
targetFreq = count(s1); windowFreq = zeros(26)
for i in s2.indices:
    windowFreq[s2[i]]++
    if i >= s1.length: windowFreq[s2[i - s1.length]]--     # leave the window
    if targetFreq == windowFreq: return true
return false
```

**Why the fixed size?** Any permutation has exactly `|s1|` characters — so the window never shrinks or grows; the left pointer is arithmetic (`i - s1.length`), the same add-left/remove-right rhythm as [15.4](maximum-average-subarray.md).

**Why compare the whole 26-array?** `targetFreq.contentEquals(windowFreq)` — an O(26) comparison per window = O(26·n) total, fine at these constraints. (The `formed`-counter optimization from [15.2](minimum-window-substring.md) makes it O(26 + n) — same family, and a natural follow-up.)

**The `i >= s1.length` off-by-one:** the remove happens only after the window has grown past size `|s1|` — keeping `windowFreq` exactly the current `|s1|`-length window.

## Approach 1 — Generate all permutations (n! dead end)

Enumeration explodes; the counting-window is the point.

## Approach 2 — Fixed-size frequency window (the repo's version, optimal)

```kotlin
class PermutationsInString {
    /**
     * @param s1 the pattern (letters to match)
     * @param s2 the string to search
     * @return   true iff s2 has a window that is a permutation of s1
     */
    fun checkInclusion(s1: String, s2: String): Boolean {
        val targetFreq = IntArray(26)
        val windowFreq = IntArray(26)

        for (c in s1) targetFreq[c - 'a']++

        for (i in s2.indices) {
            windowFreq[s2[i] - 'a']++
            if (i >= s1.length) {
                windowFreq[s2[i - s1.length] - 'a']--   // leave the window
            }
            if (targetFreq.contentEquals(windowFreq))   // anagram window found
                return true
        }
        return false
    }
}
```

```java
public class PermutationInString {
    /**
     * @param s1 the pattern (letters to match)
     * @param s2 the string to search
     * @return   true iff s2 has a window that is a permutation of s1
     */
    public boolean checkInclusion(String s1, String s2) {
        int[] target = new int[26];
        int[] window = new int[26];

        for (char c : s1.toCharArray()) target[c - 'a']++;

        for (int i = 0; i < s2.length(); i++) {
            window[s2.charAt(i) - 'a']++;
            if (i >= s1.length()) {
                window[s2.charAt(i - s1.length()) - 'a']--;   // leave the window
            }
            if (Arrays.equals(target, window)) return true;   // anagram window found
        }
        return false;
    }
}
```

```cpp
#include <array>
#include <string>

class PermutationInString {
public:
    /**
     * @param s1 the pattern (letters to match)
     * @param s2 the string to search
     * @return   true iff s2 has a window that is a permutation of s1
     */
    bool checkInclusion(std::string s1, std::string s2) {
        std::array<int, 26> target{}; 
        std::array<int, 26> window{};
        for (char c : s1) target[c - 'a']++;

        for (int i = 0; i < (int)s2.size(); i++) {
            window[s2[i] - 'a']++;
            if (i >= (int)s1.size()) {
                window[s2[i - s1.size()] - 'a']--;   // leave the window
            }
            if (target == window) return true;       // anagram window found
        }
        return false;
    }
};
```

```python
def check_inclusion(s1: str, s2: str) -> bool:
    """
    @param s1: the pattern (letters to match)
    @param s2: the string to search
    @return:   true iff s2 has a window that is a permutation of s1
    """
    target = [0] * 26
    window = [0] * 26

    for c in s1:
        target[ord(c) - ord("a")] += 1

    for i, c in enumerate(s2):
        window[ord(c) - ord("a")] += 1
        if i >= len(s1):
            window[ord(s2[i - len(s1)]) - ord("a")] -= 1    # leave the window
        if window == target:                                # anagram window found
            return True
    return False
```

```rust
impl Solution {
    /// @param s1 the pattern (letters to match)
    /// @param s2 the string to search
    /// @return   true iff s2 has a window that is a permutation of s1
    pub fn check_inclusion(s1: String, s2: String) -> bool {
        let b1 = s1.as_bytes();
        let b2 = s2.as_bytes();
        if b2.len() < b1.len() { return false; }

        let mut target = [0i32; 26];
        let mut window = [0i32; 26];
        for &b in b1 { target[(b - b'a') as usize] += 1; }

        for i in 0..b2.len() {
            window[(b2[i] - b'a') as usize] += 1;
            if i >= b1.len() {
                window[(b2[i - b1.len()] - b'a') as usize] -= 1;   // leave the window
            }
            if window == target { return true; }                   // anagram window found
        }
        false
    }
}
```

## Dry run

**Input:** `s1 = "ab"`, `s2 = "eidbaooo"`.

```
target = [a:1, b:1]

i=0 'e': window[e]=1.  i < 2 -> no remove.  window != target.
i=1 'i': window=[e,i].  no remove.  != target.
i=2 'd': window=[e,i,d].  i >= 2 -> remove s2[0]='e' -> window=[i,d].  != target.
i=3 'b': window=[i,d,b].  remove s2[1]='i' -> [d,b].  != target.
i=4 'a': window=[d,b,a].  remove s2[2]='d' -> [b,a].  == target ✓  (window "ba" at index 3)

Output: true ✓
```

The window stays exactly `|s1|` long the whole time: the remove at `i - s1.length` fires only once the window is full, and the comparison at each step checks the *current* window. "ba" (indices 3-4) is a permutation of "ab" — the frequency arrays are equal even though the letters are swapped.

## Complexity

**Time.** O(n) windows × O(26) comparison:

$$
T(n, m) = O(26n) = O(n)
$$

**Space.** Two frequency arrays:

$$
S = O(26) = O(1)
$$

## Variants & follow-ups

- **Find All Anagrams In A String** (`string/sliding_window/FindAllAnagrams.kt`) — the same window, but *collecting* every start index instead of returning a boolean.
- **Minimum Window Substring** ([15.2](minimum-window-substring.md)) — the variable-size cousin: coverage instead of exact equality, with the `formed` counter.
- **Valid Anagram** ([9.1](../ch09-strings/valid-anagram.md)) — the static (single-window) version of the same frequency comparison.
- **Interview follow-up:** "Why is this O(n) rather than O(n·|s1|)?" The window slides by add-left/remove-right — O(1) per step — and the comparison is a fixed 26-slot array equality. No substring is ever *built*; the frequency arrays are the entire state. (The `formed`-counter upgrade from [15.2](minimum-window-substring.md) can push the comparison to O(1) too.)
