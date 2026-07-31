# 10.19 Determine If Two Strings Are Close

> **Source:** [`src/main/kotlin/string/hashtable/DetermineIfStringsAreClose.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/hashtable/DetermineIfStringsAreClose.kt)
> **Pattern:** char-set + frequency-multiset equality · **Core page**

## The Problem

"Close" if one string can become the other via: swap any two chars, or transform all occurrences of one char into another.

- Constraints: lengths ≤ 10⁵.

## Examples

```
Input:  word1 = "abc", word2 = "bca"   -> Output: true  (swaps suffice)
Input:  word1 = "cabbba", word2 = "abbccc" -> Output: true (transform a->c, b->a...)
Input:  word1 = "uau", word2 = "ssx"   -> Output: false (different char sets)
```

## Intuition — the two operations preserve exactly two invariants

- **Swapping** preserves the multiset of frequencies and the char set;
- **Transforming** permutes frequencies among chars but keeps the char set and the frequency multiset.

So "close" ⟺ **same char set** AND **same frequency multiset**:

```kotlin
return when {
    freq1.keys != freq2.keys -> false        // different characters
    freq1.values.sorted() != freq2.values.sorted() -> false   // different frequency counts
    else -> true
}
```

**Why `keys` equality?** A transform can only map existing chars to existing chars — if word1 has `u` and word2 has `s`, no sequence of transforms introduces `s`. The char sets must match.

**Why sorted values?** Transforms rearrange *which* char has each count — the counts themselves (as a multiset) are invariant. Sorting both value-lists compares the multisets.

## Approach 1 — The two-invariant test (the repo's version, optimal)

```kotlin
class DetermineIfStringsAreClose {
    /**
     * @param word1 first string
     * @param word2 second string
     * @return      true iff the strings are close
     */
    fun closeStrings(word1: String, word2: String): Boolean {
        if (word1.length != word2.length) return false

        val freq1 = mutableMapOf<Char, Int>().apply {
            word1.forEach { ch -> this[ch] = this.getOrDefault(ch, 0) + 1 }
        }
        val freq2 = mutableMapOf<Char, Int>().apply {
            word2.forEach { ch -> this[ch] = this.getOrDefault(ch, 0) + 1 }
        }

        return when {
            freq1.keys != freq2.keys -> false
            freq1.values.sorted() != freq2.values.sorted() -> false
            else -> true
        }
    }
}
```

```java
import java.util.*;

public class DetermineIfTwoStringsAreClose {
    /**
     * @param word1 first string
     * @param word2 second string
     * @return      true iff the strings are close
     */
    public boolean closeStrings(String word1, String word2) {
        int[] a = new int[26], b = new int[26];
        for (char c : word1.toCharArray()) a[c - 'a']++;
        for (char c : word2.toCharArray()) b[c - 'a']++;

        for (int i = 0; i < 26; i++) {
            if ((a[i] == 0) != (b[i] == 0)) return false;      // char sets differ
        }

        Arrays.sort(a);
        Arrays.sort(b);
        return Arrays.equals(a, b);                            // frequency multisets match
    }
}
```

```cpp
#include <string>
#include <array>
#include <algorithm>

class DetermineIfTwoStringsAreClose {
public:
    /**
     * @param word1 first string
     * @param word2 second string
     * @return      true iff the strings are close
     */
    bool closeStrings(std::string word1, std::string word2) {
        std::array<int, 26> a{}, b{};
        for (char c : word1) a[c - 'a']++;
        for (char c : word2) b[c - 'a']++;

        for (int i = 0; i < 26; i++) {
            if ((a[i] == 0) != (b[i] == 0)) return false;      // char sets differ
        }

        std::sort(a.begin(), a.end());
        std::sort(b.begin(), b.end());
        return a == b;                                         // frequency multisets match
    }
};
```

```python
def close_strings(word1: str, word2: str) -> bool:
    """
    @param word1: first string
    @param word2: second string
    @return:      true iff the strings are close
    """
    if len(word1) != len(word2):
        return False

    from collections import Counter
    f1, f2 = Counter(word1), Counter(word2)

    return set(f1.keys()) == set(f2.keys()) and sorted(f1.values()) == sorted(f2.values())
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param word1 first string
    /// @param word2 second string
    /// @return      true iff the strings are close
    pub fn close_strings(word1: String, word2: String) -> bool {
        let mut f1: HashMap<char, i32> = HashMap::new();
        let mut f2: HashMap<char, i32> = HashMap::new();
        for c in word1.chars() { *f1.entry(c).or_insert(0) += 1; }
        for c in word2.chars() { *f2.entry(c).or_insert(0) += 1; }

        if f1.keys().collect::<std::collections::HashSet<_>>()
            != f2.keys().collect::<std::collections::HashSet<_>>() { return false; }

        let mut v1: Vec<i32> = f1.values().copied().collect();
        let mut v2: Vec<i32> = f2.values().copied().collect();
        v1.sort_unstable();
        v2.sort_unstable();
        v1 == v2
    }
}
```

## Dry run

**Input:** `word1 = "cabbba", word2 = "abbccc"`.

```
freq1: c:1, a:2, b:3.  freq2: a:1, b:2, c:3.
keys: {c,a,b} == {a,b,c} ✓
values sorted: [1,2,3] == [1,2,3] ✓

Output: true ✓   (transform: a->c? no — the classic: c->a, b->c, a->b... the multiset 1,2,3
maps onto itself)

Input: "uau" vs "ssx": keys {u,a} != {s,x} -> false ✓
```

The two invariants are both necessary and sufficient: the char set must match (no new chars), and the frequency counts as a multiset must match (transforms permute counts among chars). Swaps are the degenerate case where both match trivially.

## Complexity

**Time.** Counting + sorting 26 values:

$$
T(n) = O(n)
$$

**Space.** Count arrays:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Group Anagrams** ([9.2](../ch09-strings/group-anagrams.md)) — frequency vectors in a grouping role.
- **Interview follow-up:** "Why are these two invariants sufficient?" Operation 2 (transform) can realize any permutation of counts among the shared chars — the symmetric group acts on the frequency assignment. Equal char sets + equal count-multisets means the permutation exists; the operations generate exactly that group.
