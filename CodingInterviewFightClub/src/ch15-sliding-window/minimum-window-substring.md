# 15.2 Minimum Window Substring

> **Source:** [`src/main/kotlin/sliding_window/MinimumWindowSubstring.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sliding_window/MinimumWindowSubstring.kt)
> **Pattern:** two maps + formed-count · **Core page**

## The Problem

Given strings `s` and `t`, return the **minimum window substring** of `s` containing *every* character of `t` (with the same multiplicities), or `""` if none.

- Constraints: $1 \le n, m \le 10^5$; uppercase and lowercase letters.

## Examples

```
Input:  s = "ADOBECODEBANC", t = "ABC"   -> Output: "BANC"
Input:  s = "a", t = "a"                 -> Output: "a"
Input:  s = "a", t = "aa"                -> Output: ""   (needs two a's)
```

## Intuition — "does the window cover t?" is a `formed` counter

The window `[left, right]` must contain each target character at least as many times as in `t`. Two maps track this:

- `targetMap[c]` = how many of `c` t needs;
- `windowMap[c]` = how many of `c` the window has.

The key optimization — **`formed`**: the number of *distinct* characters whose window count has reached its target. A window is valid iff `formed == targetMap.size`. Why this beats checking all maps per window: `formed` increments only when a character's count *crosses* its target (once per character), so each window validity test is $O(1)$ instead of $O(|\Sigma|)$.

**The expand-then-shrink rhythm:**

```
for right in s.indices:
    add s[right] to windowMap; update formed
    while formed == targetMap.size:              # window is valid: try to shrink
        record the window if it's the shortest so far
        remove s[left] from windowMap; update formed (may drop below); left++
```

The `while` shrinks as far as possible while staying valid — every valid window is examined, and the shortest is kept. The `formed` decrement on removal is the subtle line: removing a character *below* its target is the only way `formed` drops.

## Approach 1 — For each start, scan for a valid end (O(n^2))

For every `left`, extend `right` until the window covers `t`: $O(n^2)$ map checks.

## Approach 2 — Shrink-until-valid with formed (the repo's version, optimal)

```kotlin
fun minWindow(s: String, t: String): String {
    if (s.length < t.length) return ""

    val targetMap = t.groupingBy { it }.eachCount()
    val windowMap = mutableMapOf<Char, Int>()

    var left = 0
    var formed = 0
    var minLen = Int.MAX_VALUE
    var bestRange = 0..-1                    // empty range until a window is found

    for (right in s.indices) {
        val char = s[right]
        windowMap[char] = windowMap.getOrDefault(char, 0) + 1

        // Only increment 'formed' when frequency exactly matches target
        if (windowMap[char] == targetMap[char]) {
            formed++
        }

        // Shrink from left: expand until valid, then shrink until invalid
        while (formed == targetMap.size) {
            if (right - left + 1 < minLen) {
                minLen = right - left + 1
                bestRange = left..right
            }

            val leftChar = s[left]
            // If the char we are removing was essential, decrement formed
            if (windowMap[leftChar] == targetMap[leftChar]) {
                formed--
            }
            windowMap[leftChar] = windowMap[leftChar]!! - 1
            left++
        }
    }
    return s.substring(bestRange)
}
```

```java
import java.util.*;

public class MinimumWindowSubstring {
    /**
     * @param s source string
     * @param t target characters
     * @return  minimum window of s containing all of t
     */
    public String minWindow(String s, String t) {
        Map<Character, Integer> target = new HashMap<>();
        for (char c : t.toCharArray()) target.merge(c, 1, Integer::sum);

        Map<Character, Integer> window = new HashMap<>();
        int left = 0, formed = 0, minLen = Integer.MAX_VALUE, bestL = -1, bestR = -1;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            window.merge(c, 1, Integer::sum);
            if (window.get(c).equals(target.get(c))) formed++;   // crossed the target

            while (formed == target.size()) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    bestL = left; bestR = right;
                }
                char out = s.charAt(left);
                if (window.get(out).equals(target.get(out))) formed--;   // fell below
                window.merge(out, -1, Integer::sum);
                left++;
            }
        }
        return bestL == -1 ? "" : s.substring(bestL, bestR + 1);
    }
}
```

```cpp
#include <string>
#include <unordered_map>

class MinimumWindowSubstring {
public:
    /**
     * @param s source string
     * @param t target characters
     * @return  minimum window of s containing all of t
     */
    std::string minWindow(std::string s, std::string t) {
        std::unordered_map<char, int> target;
        for (char c : t) target[c]++;

        std::unordered_map<char, int> window;
        int left = 0, formed = 0, minLen = INT_MAX, bestL = -1, bestR = -1;

        for (int right = 0; right < (int)s.size(); right++) {
            char c = s[right];
            window[c]++;
            if (window[c] == target[c]) formed++;       // crossed the target

            while (formed == (int)target.size()) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    bestL = left; bestR = right;
                }
                char out = s[left];
                if (window[out] == target[out]) formed--;   // fell below
                window[out]--;
                left++;
            }
        }
        return bestL == -1 ? "" : s.substr(bestL, minLen);
    }
};
```

```python
def min_window(s: str, t: str) -> str:
    """
    @param s: source string
    @param t: target characters
    @return:  minimum window of s containing all of t
    """
    from collections import Counter

    target = Counter(t)
    window = Counter()
    left = 0
    formed = 0
    best = (0, float("inf"))

    for right, c in enumerate(s):
        window[c] += 1
        if window[c] == target[c]:           # crossed the target
            formed += 1

        while formed == len(target):         # window is valid: try to shrink
            if right - left < best[1] - best[0]:
                best = (left, right)
            out = s[left]
            if window[out] == target[out]:   # fell below
                formed -= 1
            window[out] -= 1
            left += 1

    l, r = best
    return "" if r == float("inf") else s[l:r + 1]
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param s source string
    /// @param t target characters
    /// @return  minimum window of s containing all of t
    pub fn min_window(s: String, t: String) -> String {
        let sb = s.as_bytes();
        let tb = t.as_bytes();

        let mut target: HashMap<u8, i32> = HashMap::new();
        for &b in tb { *target.entry(b).or_insert(0) += 1; }

        let mut window: HashMap<u8, i32> = HashMap::new();
        let (mut left, mut formed) = (0usize, 0);
        let mut best: Option<(usize, usize)> = None;

        for right in 0..sb.len() {
            *window.entry(sb[right]).or_insert(0) += 1;
            if window[&sb[right]] == target.get(&sb[right]).copied().unwrap_or(-1) {
                formed += 1;                          // crossed the target
            }

            while formed == target.len() {            // window is valid: try to shrink
                if best.map_or(true, |(l, r)| right - left < r - l) {
                    best = Some((left, right));
                }
                let out = sb[left];
                if window[&out] == target.get(&out).copied().unwrap_or(-1) {
                    formed -= 1;                      // fell below
                }
                *window.entry(out).or_insert(0) -= 1;
                left += 1;
            }
        }

        match best {
            Some((l, r)) => s[l..=r].to_string(),
            None => String::new(),
        }
    }
}
```


## Dry run

**Input:** `s = "ADOBECODEBANC"`, `t = "ABC"`.

```
target = {A:1, B:1, C:1}; formed counts distinct chars at their targets.

right 5 'C': window has A,B,C each >= target -> formed=3 -> VALID [0,5]="ADOBEC" (len 6)
  record best=(0,5).  shrink: remove s[0]='A' -> window{A}=0 < target -> formed=2.  left=1.
right 9 'B': window{B}=2 (no crossing; still >= target).  right 10 'A': window{A}=1 -> formed=3.
  VALID [1,10] len 10 (longer, skip).  shrink:
    remove 'D'(1->2), 'O'(2->3): no formed change (not in target).
    remove s[3]='B': window{B}=1 (still == target) -> no formed change!  left=4.
    remove 'E'(4->5): no change.  remove s[5]='C': window{C}=0 < target -> formed=2.  left=6.
right 12 'C': window{C}=1 -> formed=3 -> VALID [6,12] len 7 (skip, longer).  shrink:
    remove 'O'(6->7), 'D'(7->8), 'E'(8->9): no formed change.
    record [9,12]="BANC" (len 4) -> 4 < 6 -> best=(9,12).
    remove s[9]='B': window{B}=0 < target -> formed=2.  left=10.  stop.

best = (9,12) -> "BANC" ✓
```

The per-shrink recording is the subtlety: the while loop records *every* valid window it passes through while shrinking, so the true minimum (appearing mid-shrink, not at the top of the expansion) is captured. The `formed` bookkeeping is exact — a character's count crossing its target (up or down) is the only event that changes it.

## Complexity

**Time.** Each character enters and leaves the window once; map ops are $O(1)$:

$$
T(n) = O(n)
$$

**Space.** Two maps of at most $|\Sigma|$ entries:

$$
S(n) = O(|\Sigma|) \subseteq O(n)
$$

## Variants & follow-ups

- **Longest Repeating Character Replacement** ([15.3](longest-repeating-character-replacement.md)) — the same shrink-until-valid shape with a *different* validity condition.
- **Permutation In String / Find All Anagrams** — the *fixed-size* version: windows of exactly `|t|` compared to the target map (no shrink, just slide).
- **Longest Substring Without Repeating Characters** ([15.1](longest-substring-without-repeating-characters.md)) — the last-index teleport version, where validity is local.
- **Interview follow-up:** "Why does `formed` only change on a *crossing*, not on every add/remove?" The window's validity is "every target char is covered" — a per-character boolean. `formed` counts how many of those booleans are true, and a boolean flips only when a count crosses its target (0->target or target->0). Counting crossings keeps each validity update $O(1)$ instead of $O(|\Sigma|)$.
