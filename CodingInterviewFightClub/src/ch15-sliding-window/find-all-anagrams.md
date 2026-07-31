# 15.11 Find All Anagrams In A String

> **Source:** [`src/main/kotlin/string/sliding_window/FindAllAnagrams.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/sliding_window/FindAllAnagrams.kt)
> **Pattern:** fixed-window frequency compare · **Core page**

## The Problem

Start indices of all `p`-anagram substrings in `s`.

- Constraints: lengths ≤ 3×10⁴.

## Examples

```
Input:  s = "cbaebabacd", p = "abc"   -> Output: [0,6]
Input:  s = "abab", p = "ab"          -> Output: [0,1,2]
```

## Examples — the counts of p match a window's counts

An anagram window has the **same character frequency vector** as `p`. Slide a fixed-size window, update its `IntArray(26)` counts, compare to `p`'s:

```kotlin
val pCount = IntArray(26)
val sCount = IntArray(26)

for (i in p.indices) {
    pCount[p[i] - 'a']++
    sCount[s[i] - 'a']++
}

for (i in p.length..s.length) {
    if (pCount.contentEquals(sCount)) result.add(i - p.length)

    if (i < s.length) {                    // slide: add next, remove first
        sCount[s[i] - 'a']++
        sCount[s[i - p.length] - 'a']--
    }
}
```

**Why the array-equality compare?** `contentEquals` on `IntArray(26)` is O(26) — a fixed cost per window, giving O(26n) total. The [9.2](../ch09-strings/group-anagrams.md)/[15.8](permutation-in-string.md) frequency-vector machinery.

**Why the slide with `i - p.length`?** The window is `[i - p.length, i)`; sliding adds `s[i]` and evicts `s[i - p.length]` — the [15.0](pattern-primer.md) fixed-window update, one add + one remove per step.

## Approach 1 — Check every substring (O(n·|p|·26))

Extract each window, count, compare: correct, slow.

## Approach 2 — Frequency compare + slide (the repo's version, optimal)

```kotlin
class FindAllAnagrams {
    /**
     * @param s haystack string
     * @param p anagram pattern
     * @return  start indices of p-anagram windows
     */
    fun findAnagrams(s: String, p: String): List<Int> {
        val result = mutableListOf<Int>()
        if (s.length < p.length) return result

        val pCount = IntArray(26)
        val sCount = IntArray(26)

        for (i in p.indices) {
            pCount[p[i] - 'a']++
            sCount[s[i] - 'a']++
        }

        for (i in p.length..s.length) {
            if (pCount.contentEquals(sCount)) result.add(i - p.length)

            if (i < s.length) {
                sCount[s[i] - 'a']++
                sCount[s[i - p.length] - 'a']--
            }
        }
        return result
    }
}
```

```java
import java.util.*;

public class FindAllAnagrams {
    /**
     * @param s haystack string
     * @param p anagram pattern
     * @return  start indices of p-anagram windows
     */
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) return result;

        int[] pCount = new int[26], sCount = new int[26];

        for (int i = 0; i < p.length(); i++) {
            pCount[p.charAt(i) - 'a']++;
            sCount[s.charAt(i) - 'a']++;
        }

        for (int i = p.length(); i <= s.length(); i++) {
            if (Arrays.equals(pCount, sCount)) result.add(i - p.length());

            if (i < s.length()) {
                sCount[s.charAt(i) - 'a']++;
                sCount[s.charAt(i - p.length()) - 'a']--;
            }
        }
        return result;
    }
}
```

```cpp
#include <vector>
#include <string>

class FindAllAnagrams {
public:
    /**
     * @param s haystack string
     * @param p anagram pattern
     * @return  start indices of p-anagram windows
     */
    std::vector<int> findAnagrams(std::string s, std::string p) {
        std::vector<int> result;
        if (s.size() < p.size()) return result;

        int pCount[26] = {0}, sCount[26] = {0};

        for (int i = 0; i < (int)p.size(); i++) {
            pCount[p[i] - 'a']++;
            sCount[s[i] - 'a']++;
        }

        for (int i = p.size(); i <= (int)s.size(); i++) {
            if (std::equal(pCount, pCount + 26, sCount)) result.push_back(i - p.size());

            if (i < (int)s.size()) {
                sCount[s[i] - 'a']++;
                sCount[s[i - p.size()] - 'a']--;
            }
        }
        return result;
    }
};
```

```python
def find_anagrams(s: str, p: str) -> list[int]:
    """
    @param s: haystack string
    @param p: anagram pattern
    @return:  start indices of p-anagram windows
    """
    result = []
    if len(s) < len(p):
        return result

    from collections import Counter
    p_count = Counter(p)
    window = Counter(s[:len(p)])

    for i in range(len(p), len(s) + 1):
        if window == p_count:
            result.append(i - len(p))

        if i < len(s):
            window[s[i]] += 1
            window[s[i - len(p)]] -= 1
            if window[s[i - len(p)]] == 0:
                del window[s[i - len(p)]]

    return result
```

```rust
impl Solution {
    /// @param s haystack string
    /// @param p anagram pattern
    /// @return  start indices of p-anagram windows
    pub fn find_anagrams(s: String, p: String) -> Vec<i32> {
        let (sb, pb) = (s.as_bytes(), p.as_bytes());
        let mut result = Vec::new();
        if sb.len() < pb.len() { return result; }

        let mut p_count = [0; 26];
        let mut w_count = [0; 26];

        for i in 0..pb.len() {
            p_count[(pb[i] - b'a') as usize] += 1;
            w_count[(sb[i] - b'a') as usize] += 1;
        }

        for i in pb.len()..=sb.len() {
            if p_count == w_count { result.push((i - pb.len()) as i32); }

            if i < sb.len() {
                w_count[(sb[i] - b'a') as usize] += 1;
                w_count[(sb[i - pb.len()] - b'a') as usize] -= 1;
            }
        }
        result
    }
}
```

## Dry run

**Input:** `s = "cbaebabacd"`, `p = "abc"`.

```
window [0,3) = "cba": counts {a:1,b:1,c:1} == p -> add 0.  slide: +'e'(1), -'c'(0)
window [1,4) = "bae": {a:1,b:1,e:1} != -> no.  slide: +'b' → {a:1,b:2,e:1}, -'b' → {a:1,b:1,e:1}
window [2,5) = "aeb": != .  slide: +'a'(2), -'a'(1) -> {a:1,b:1,e:1}
window [3,6) = "eba": != .  slide: +'b'(2), -'b'(1) -> {a:1,b:1,e:1}
window [4,7) = "bab": != .  slide: +'a'(2), -'e'(0) -> {a:2,b:2}... 
window [5,8) = "aba": != .  slide: +'c', -'a' -> {a:1,b:1,c:1}
window [6,9) = "bac": == p -> add 6.

Output: [0,6] ✓
```

The fixed window never re-counts — each slide is one `+` and one `-` on the 26-slot vector, and `contentEquals` is the O(26) anagram test. "abab"/"ab" → every window matches → [0,1,2] ✓.

## Complexity

**Time.** O(26) per window:

$$
T(n) = O(26n) = O(n)
$$

**Space.** Two count arrays:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Permutation In String** ([15.8](permutation-in-string.md)) — the boolean twin (any window is an anagram).
- **Group Anagrams** ([9.2](../ch09-strings/group-anagrams.md)) — the static frequency-vector grouping.
- **Interview follow-up:** "Why not a `formed`-counter like [15.2](minimum-window-substring.md)?" This window is *fixed-size* — the compare is always valid, no need to track how many chars matched. The formed-counter optimization shines for variable windows; the O(26) array compare is simpler and equally O(n) here.
