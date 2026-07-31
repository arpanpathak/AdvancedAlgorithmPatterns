# 15.12 Maximum Number Of Vowels In A Substring Of Given Length

> **Source**: [`src/main/kotlin/string/sliding_window/MaximumNumberofVowelsinSubstringofGivenLength.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/sliding_window/MaximumNumberofVowelsinSubstringofGivenLength.kt)
> **Pattern**: fixed-window vowel count · **Core page**

## The Problem

Max vowels in any length-k substring.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  s = "abciiidef", k = 3   -> Output: 3   ("iii")
Input:  s = "aeiou", k = 2       -> Output: 2
```

## Intuition — the fixed window from [15.8](permutation-in-string.md), counting vowels

Slide a k-window; add the entering char's vowel-ness, subtract the leaving one's:

```kotlin
for (i in 0 until s.length) {
    if (s[i] in vowels) vowelWindowCount++

    if (i >= k - 1) {                    // a full window
        maxCount = maxOf(maxCount, vowelWindowCount)
        if (s[i - k + 1] in vowels) vowelWindowCount--    // evict the leaving char
    }
}
```

**Why the eviction at `i - k + 1`?** The window is `[i-k+1, i]`; when it slides, the char leaving is `s[i-k+1]` — its vowel-ness is subtracted after recording. The [15.0](pattern-primer.md) add-then-evict rhythm, vowel-ness as the 0/1 payload.

## Approach 1 — Count each window (O(nk))

Extract and count: correct, slow.

## Approach 2 — Sliding vowel counter (the repo's version, optimal)

```kotlin
class MaximumNumberofVowelsinSubstringofGivenLength {
    /**
     * @param s input string
     * @param k window length
     * @return  max vowels in any k-substring
     */
    fun maxVowels(s: String, k: Int): Int {
        var (vowelWindowCount, maxCount) = Pair(0, 0)
        val vowels = setOf('a', 'e', 'i', 'o', 'u')

        for (i in 0 until s.length) {
            if (s[i] in vowels) vowelWindowCount++

            if (i >= k - 1) {
                maxCount = maxOf(maxCount, vowelWindowCount)
                if (s[i - k + 1] in vowels) vowelWindowCount--
            }
        }
        return maxCount
    }
}
```

```java
public class MaximumNumberOfVowelsInASubstringOfGivenLength {
    private static final String VOWELS = "aeiou";

    /**
     * @param s input string
     * @param k window length
     * @return  max vowels in any k-substring
     */
    public int maxVowels(String s, int k) {
        int count = 0, best = 0;

        for (int i = 0; i < s.length(); i++) {
            if (VOWELS.indexOf(s.charAt(i)) >= 0) count++;

            if (i >= k - 1) {
                best = Math.max(best, count);
                if (VOWELS.indexOf(s.charAt(i - k + 1)) >= 0) count--;
            }
        }
        return best;
    }
}
```

```cpp
#include <string>
#include <algorithm>

class MaximumNumberOfVowelsInASubstringOfGivenLength {
public:
    /**
     * @param s input string
     * @param k window length
     * @return  max vowels in any k-substring
     */
    int maxVowels(std::string s, int k) {
        auto isVowel = [](char c) { return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u'; };

        int count = 0, best = 0;
        for (int i = 0; i < (int)s.size(); i++) {
            if (isVowel(s[i])) count++;

            if (i >= k - 1) {
                best = std::max(best, count);
                if (isVowel(s[i - k + 1])) count--;
            }
        }
        return best;
    }
};
```

```python
def max_vowels(s: str, k: int) -> int:
    """
    @param s: input string
    @param k: window length
    @return:  max vowels in any k-substring
    """
    vowels = set("aeiou")
    count = best = 0

    for i, ch in enumerate(s):
        if ch in vowels:
            count += 1

        if i >= k - 1:
            best = max(best, count)
            if s[i - k + 1] in vowels:
                count -= 1

    return best
```

```rust
impl Solution {
    /// @param s input string
    /// @param k window length
    /// @return  max vowels in any k-substring
    pub fn max_vowels(s: String, k: i32) -> i32 {
        let is_vowel = |c: char| matches!(c, 'a' | 'e' | 'i' | 'o' | 'u');
        let bytes: Vec<char> = s.chars().collect();
        let k = k as usize;

        let mut count = 0;
        let mut best = 0;
        for i in 0..bytes.len() {
            if is_vowel(bytes[i]) { count += 1; }

            if i + 1 >= k {
                best = best.max(count);
                if is_vowel(bytes[i + 1 - k]) { count -= 1; }
            }
        }
        best
    }
}
```

## Dry run

**Input:** `s = "abciiidef", k = 3`.

```
i=0 'a': count=1.  i=1 'b': 1.  i=2 'i': 2.  window [0,3] "abi": best=2.  evict 'a' -> 1.
i=3 'i': 2.  window "bii": best=2.  evict 'b' -> 2.
i=4 'i': 3.  window "iii": best=3.  evict 'i' -> 2.
i=5 'd': 2.  best=3.  evict 'i' -> 1.  i=6 'e': 2.  evict 'i' -> 1.  i=7 'f': 1.

Output: 3 ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Max Consecutive Ones III** ([15.7](max-consecutive-ones-iii.md)) — the same fixed-window counting family.
- **Interview follow-up:** "Why evict after recording, not before?" The window is full when `i >= k-1` — recording first captures the just-completed window; evicting then prepares the next. The order is the [15.0](pattern-primer.md) contract.
