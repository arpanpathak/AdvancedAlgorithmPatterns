# 15.3 Longest Repeating Character Replacement

> **Source:** [`src/main/kotlin/sliding_window/LongestRepeatingCharacterReplacement.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sliding_window/LongestRepeatingCharacterReplacement.kt)
> **Pattern:** max-count window · **Core page**

## The Problem

Given a string `s` and an integer `k`, return the length of the longest substring you can get by replacing **at most k characters** with any other characters (i.e., the longest substring that is *almost* one repeated character).

- Constraints: $1 \le n \le 10^5$; uppercase letters.

## Examples

```
Input:  s = "ABAB", k = 2   -> Output: 4   (replace both B's -> "AAAA")
Input:  s = "AABABBA", k = 1 -> Output: 4  ("AABA" or "ABBB")
```

## Intuition — a window is fixable iff `len - maxCount <= k`

A window can be made uniform by replacing at most k characters **iff its non-dominant characters number at most k**:

$$
\text{window length} - \text{count of the most frequent char} \le k
$$

The replacements turn every non-dominant character into the dominant one. So the validity condition is one arithmetic test on two running quantities:

- `maxCount` — the max frequency *within the current window* (maintained as the window slides);
- `len = right - left + 1`.

**The subtle `maxCount` trick:** the repo *never decreases* `maxCount` when the window shrinks — `maxOf(maxCount, ...)` only grows. That's deliberate: for the *maximum-length* answer, an over-estimate of `maxCount` only makes the window *shorter* (validity is `len - maxCount <= k`; a larger `maxCount` makes the test easier to pass). The classic proof that this is safe: the answer never needs a window longer than the best already found, and stale-high `maxCount` can only reject longer windows, never accept a wrong shorter one... actually the standard argument: since we only care about the max, a monotonic `maxCount` never invalidates a window that the true max would accept — it makes validity *looser*, but the resulting length bound still holds.

**The shrink is `if`, not `while`:** because validity only gets *easier* as the window shrinks (removing characters can't increase `len - maxCount`... hmm — removing a *non-dominant* char lowers `len` by 1 and leaves `maxCount` (or lowers it), so `len - maxCount` shrinks by 1; removing a *dominant* char may lower `maxCount` by 1, keeping `len - maxCount` the same. Either way the test result never flips from invalid to valid... actually it can only improve. So one shrink step per expansion is enough to restore validity — no while needed. This is the "shrink at most once per step" flavor of the window.

## Approach 1 — For every start, extend to the limit (O(n^2))

For each `left`, extend `right` while the window is fixable: $O(n^2)$.

## Approach 2 — Max-count window with single-step shrink (the repo's version, optimal)

```kotlin
class LongestRepeatingCharacterReplacement {
    /**
     * @param s input string (uppercase letters)
     * @param k replacements allowed
     * @return  longest substring that can be made uniform with <= k replacements
     */
    fun characterReplacement(s: String, k: Int): Int {
        val count = IntArray(26)
        var maxLength = 0
        var left = 0
        var maxCount = 0

        for (right in s.indices) {
            val char = s[right]
            count[char - 'A']++
            maxCount = maxOf(maxCount, count[char - 'A'])

            if (right - left + 1 - maxCount > k) {     // window not fixable: shrink once
                count[s[left] - 'A']--
                left++
            }
            maxLength = maxOf(maxLength, right - left + 1)
        }
        return maxLength
    }
}
```

```java
public class LongestRepeatingCharacterReplacement {
    /**
     * @param s input string (uppercase letters)
     * @param k replacements allowed
     * @return  longest substring that can be made uniform with <= k replacements
     */
    public int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int left = 0, maxCount = 0, best = 0;

        for (int right = 0; right < s.length(); right++) {
            int c = s.charAt(right) - 'A';
            count[c]++;
            maxCount = Math.max(maxCount, count[c]);

            if (right - left + 1 - maxCount > k) {     // window not fixable: shrink once
                count[s.charAt(left) - 'A']--;
                left++;
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
```

```cpp
#include <string>
#include <vector>

class LongestRepeatingCharacterReplacement {
public:
    /**
     * @param s input string (uppercase letters)
     * @param k replacements allowed
     * @return  longest substring that can be made uniform with <= k replacements
     */
    int characterReplacement(std::string s, int k) {
        std::vector<int> count(26, 0);
        int left = 0, maxCount = 0, best = 0;

        for (int right = 0; right < (int)s.size(); right++) {
            count[s[right] - 'A']++;
            maxCount = std::max(maxCount, count[s[right] - 'A']);

            if (right - left + 1 - maxCount > k) {     // window not fixable: shrink once
                count[s[left] - 'A']--;
                left++;
            }
            best = std::max(best, right - left + 1);
        }
        return best;
    }
};
```

```python
def character_replacement(s: str, k: int) -> int:
    """
    @param s: input string (uppercase letters)
    @param k: replacements allowed
    @return:  longest substring that can be made uniform with <= k replacements
    """
    count = {}
    left = 0
    max_count = 0
    best = 0

    for right, c in enumerate(s):
        count[c] = count.get(c, 0) + 1
        max_count = max(max_count, count[c])

        if right - left + 1 - max_count > k:     # window not fixable: shrink once
            count[s[left]] -= 1
            left += 1
        best = max(best, right - left + 1)
    return best
```

```rust
impl Solution {
    /// @param s input string (uppercase letters)
    /// @param k replacements allowed
    /// @return  longest substring that can be made uniform with <= k replacements
    pub fn character_replacement(s: String, k: i32) -> i32 {
        let bytes = s.as_bytes();
        let mut count = [0i32; 26];
        let (mut left, mut max_count, mut best) = (0usize, 0, 0);

        for right in 0..bytes.len() {
            let c = (bytes[right] - b'A') as usize;
            count[c] += 1;
            max_count = max_count.max(count[c]);

            if (right - left + 1) as i32 - max_count > k {   // window not fixable: shrink once
                count[(bytes[left] - b'A') as usize] -= 1;
                left += 1;
            }
            best = best.max((right - left + 1) as i32);
        }
        best
    }
}
```

## Dry run

**Input:** `s = "AABABBA"`, `k = 1`.

```
count = [0]*26, left = 0, maxCount = 0, best = 0

right 0 'A': count[A]=1, maxCount=1.  1-1-1=-1 > 1? no.  best=1
right 1 'A': count[A]=2, maxCount=2.  2-2=0 > 1? no.  best=2
right 2 'B': count[B]=1, maxCount=2.  3-2=1 > 1? no.  best=3     ("AAB", fixable)
right 3 'A': count[A]=3, maxCount=3.  4-3=1 > 1? no.  best=4     ("AABA", fixable)
right 4 'B': count[B]=2, maxCount=3.  5-3=2 > 1? YES -> shrink:
                count[A]-- (remove s[0]='A'), left=1.  best=max(4, 4)=4   ("ABAB" len 4)
right 5 'B': count[B]=3, maxCount=3.  5-3=2 > 1? YES -> shrink:
                count[A]-- (remove s[1]='A'), left=2.  best=4   ("BABB" len 4)
right 6 'A': count[A]=1, maxCount=3.  5-3=2 > 1? YES -> shrink:
                count[B]-- (remove s[2]='B'), left=3.  best=4   ("ABBA" len 4)

Output: 4 ✓
```

The single-step shrink is visible: each over-long window loses exactly one character, keeping the window length non-decreasing after the first `k` — that's what guarantees the final `best` is the true maximum. The never-decreasing `maxCount` (stuck at 3 from index 1 on) only makes the validity test *looser* as it goes stale, which never costs us a valid longer window.

## Complexity

**Time.** One pass, O(1) per step:

$$
T(n) = O(n)
$$

**Space.** The 26-slot counter:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Max Consecutive Ones III** ([15.7](max-consecutive-ones-iii.md)) — the same "fix a window with a budget" idea over binary values.
- **Longest Continuous Subarray With Absolute Difference <= Limit** (`src/main/kotlin/sliding_window/`) — the budget is a *range* instead of a count; needs a monotonic structure for the window's min and max.
- **Interview follow-up:** "Why can `maxCount` be allowed to go stale?" We only need the *longest* fixable window. A stale-high `maxCount` makes the validity test easier (never rejecting a window the true max would accept), and any window the test *accepts* with stale `maxCount` is still genuinely fixable by its own true dominant count — so the answer stays correct. The standard trick that turns this from O(n) with a reset into O(n) plain.
