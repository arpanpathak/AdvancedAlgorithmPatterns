# 10.20 Unique Length-3 Palindromic Subsequences

> **Source**: [`src/main/kotlin/string/hashtable/UniqueLength3PalindromicSubsequence.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/hashtable/UniqueLength3PalindromicSubsequence.kt)
> **Pattern**: first/last positions + middle set · **Core page**

## The Problem

Count **distinct** length-3 palindromic subsequences (`aba`, `aaa` shapes).

- Constraints: n ≤ 10⁵; lowercase.

## Examples

```
Input:  s = "aabca"   -> Output: 3   ("aba","aaa","aca")
Input:  s = "adc"     -> Output: 0
```

## Intuition — a palindrome is `c ... c`; count the distinct middles

A length-3 palindrome is determined by its outer char `c` and a middle char from between `c`'s **first and last** occurrences:

```kotlin
val charPositions = mutableMapOf<Char, MutableList<Int>>()
s.forEachIndexed { index, char ->
    charPositions.computeIfAbsent(char) { mutableListOf() }.add(index)
}

var count = 0
for ((char, positions) in charPositions) {
    val start = positions.first()
    val end = positions.last()
    if (end - start > 1) {
        count += s.substring(start + 1, end).toSet().size    // distinct middles
    }
}
```

**Why first/last?** Any `c...c` palindrome uses the *outermost* `c`s for maximal span — the middle set between them is a superset of any inner pair's. One outer pair per char captures all possible middles.

**Why `.toSet().size`?** The palindrome is `c + middle + c`; distinctness counts each middle char once. The set dedupes.

## Approach 1 — Enumerate all triples (O(n³))

Check every i<j<k: correct, absurd.

## Approach 2 — First/last + middle sets (the repo's version, optimal)

```kotlin
class UniqueLength3PalindromicSubsequence {
    /**
     * @param s input string
     * @return  count of distinct length-3 palindromes
     */
    fun countPalindromicSubsequence(s: String): Int {
        val charPositions = mutableMapOf<Char, MutableList<Int>>()

        s.forEachIndexed { index, char ->
            charPositions.computeIfAbsent(char) { mutableListOf() }.add(index)
        }

        var count = 0

        for ((char, positions) in charPositions) {
            val start = positions.first()
            val end = positions.last()

            if (end - start > 1) {
                count += s.substring(start + 1, end).toSet().size
            }
        }
        return count
    }
}
```

```java
import java.util.*;

public class UniqueLength3PalindromicSubsequences {
    /**
     * @param s input string
     * @return  count of distinct length-3 palindromes
     */
    public int countPalindromicSubsequence(String s) {
        int[] first = new int[26], last = new int[26];
        Arrays.fill(first, -1);

        for (int i = 0; i < s.length(); i++) {
            int idx = s.charAt(i) - 'a';
            if (first[idx] == -1) first[idx] = i;
            last[idx] = i;
        }

        int count = 0;
        for (int c = 0; c < 26; c++) {
            if (first[c] == -1) continue;

            Set<Character> middles = new HashSet<>();
            for (int i = first[c] + 1; i < last[c]; i++) {
                middles.add(s.charAt(i));
            }
            count += middles.size();
        }
        return count;
    }
}
```

```cpp
#include <string>
#include <vector>
#include <unordered_set>

class UniqueLength3PalindromicSubsequences {
public:
    /**
     * @param s input string
     * @return  count of distinct length-3 palindromes
     */
    int countPalindromicSubsequence(std::string s) {
        std::vector<int> first(26, -1), last(26, -1);

        for (int i = 0; i < (int)s.size(); i++) {
            int idx = s[i] - 'a';
            if (first[idx] == -1) first[idx] = i;
            last[idx] = i;
        }

        int count = 0;
        for (int c = 0; c < 26; c++) {
            if (first[c] == -1) continue;

            std::unordered_set<char> middles;
            for (int i = first[c] + 1; i < last[c]; i++) middles.insert(s[i]);
            count += middles.size();
        }
        return count;
    }
};
```

```python
def count_palindromic_subsequence(s: str) -> int:
    """
    @param s: input string
    @return:  count of distinct length-3 palindromes
    """
    first, last = {}, {}
    for i, ch in enumerate(s):
        first.setdefault(ch, i)
        last[ch] = i

    count = 0
    for ch in first:
        if last[ch] - first[ch] > 1:
            count += len(set(s[first[ch] + 1:last[ch]]))

    return count
```

```rust
use std::collections::{HashMap, HashSet};

impl Solution {
    /// @param s input string
    /// @return  count of distinct length-3 palindromes
    pub fn count_palindromic_subsequence(s: String) -> i32 {
        let bytes: Vec<char> = s.chars().collect();
        let mut first: HashMap<char, usize> = HashMap::new();
        let mut last: HashMap<char, usize> = HashMap::new();

        for (i, &ch) in bytes.iter().enumerate() {
            first.entry(ch).or_insert(i);
            last.insert(ch, i);
        }

        let mut count = 0;
        for (&ch, &f) in &first {
            let l = last[&ch];
            if l > f + 1 {
                let middles: HashSet<&char> = bytes[f + 1..l].iter().collect();
                count += middles.len();
            }
        }
        count as i32
    }
}
```

## Dry run

**Input:** `s = "aabca"`.

```
positions: a:[0,1,4], b:[2], c:[3]
'a': start 0, end 4.  middles = {a, b, c} (from "abc") -> 3.
'b': start == end -> skip.  'c': skip.

Output: 3 ✓  ("aaa" via (0,1,4), "aba" via (0,2,4), "aca" via (0,3,4))
```

The outer-pair insight collapses the problem: each char's first/last span determines *all* possible palindromes with that outer char, and the set of middles counts them distinctly. `"adc"`: no char appears twice → no spans → 0 ✓.

## Complexity

**Time.** Positions + span scans:

$$
T(n) = O(26 \cdot n) = O(n)
$$

**Space.** Position maps:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Count Palindromic Substrings** — the continuous palindromes family.
- **Interview follow-up:** "Why is one outer pair per char enough?" The outermost occurrence pair encloses every middle candidate any inner pair could — using `first`/`last` maximizes the middle set, and distinctness is per-middle-char, so the outer pair dominates all inner ones.
