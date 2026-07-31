# 9.22 Custom Sort String

> **Source**: [`src/main/kotlin/string/sorting/CustomSortString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/sorting/CustomSortString.kt) (+ `CustomSortString_Linear.kt`)
> **Pattern**: custom-order sorting · **Core page**

## The Problem

Sort `s` by the order of `order` (chars in `order` first, in that order; the rest in any order).

- Constraints: lengths ≤ 200; chars unique in `order`.

## Examples

```
Input:  order = "cba", s = "abcd"   -> Output: "cbad"
```

## Intuition — a rank map, then sort by rank

```kotlin
val orderMap = mutableMapOf<Char, Int>()
order.forEachIndexed { index, ch -> orderMap[ch] = index }

return s.toCharArray().sortedBy { ch -> orderMap[ch] ?: Int.MAX_VALUE }
    .joinToString("")
```

Unmentioned chars sort last (`Int.MAX_VALUE`). The `_Linear` variant counts frequencies for O(n) — the [10.19](../ch10-hash-tables/determine-if-two-strings-are-close.md) frequency-map discipline.

## Approach 1 — Rank-map sort (the repo's version)

## Approach 2 — Frequency linear (the `_Linear` file, optimal)

Count `s`'s chars; emit order's chars first (count times), then the rest.

```kotlin
class CustomSortString {
    /**
     * @param order custom character order
     * @param s     string to sort
     * @return      s sorted by order
     */
    fun customSortString(order: String, s: String): String {
        val orderMap = mutableMapOf<Char, Int>()
        order.forEachIndexed { index, ch -> orderMap[ch] = index }

        return s.toCharArray().sortedBy { ch -> orderMap[ch] ?: Int.MAX_VALUE }
            .joinToString("")
    }
}
```

```java
public class CustomSortString {
    /**
     * @param order custom character order
     * @param s     string to sort
     * @return      s sorted by order
     */
    public String customSortString(String order, String s) {
        int[] rank = new int[26];
        for (int i = 0; i < order.length(); i++) rank[order.charAt(i) - 'a'] = i;

        int[] count = new int[26];
        for (char c : s.toCharArray()) count[c - 'a']++;

        StringBuilder sb = new StringBuilder();
        for (char c : order.toCharArray()) {
            while (count[c - 'a']-- > 0) sb.append(c);
        }
        for (char c = 'a'; c <= 'z'; c++) {
            while (count[c - 'a']-- > 0) sb.append(c);
        }
        return sb.toString();
    }
}
```

```cpp
#include <string>
#include <array>

class CustomSortString {
public:
    /**
     * @param order custom character order
     * @param s     string to sort
     * @return      s sorted by order
     */
    std::string customSortString(std::string order, std::string s) {
        std::array<int, 26> count{};
        for (char c : s) count[c - 'a']++;

        std::string result;
        for (char c : order) while (count[c - 'a']-- > 0) result += c;
        for (char c = 'a'; c <= 'z'; c++) while (count[c - 'a']-- > 0) result += c;
        return result;
    }
};
```

```python
def custom_sort_string(order: str, s: str) -> str:
    """
    @param order: custom character order
    @param s:     string to sort
    @return:      s sorted by order
    """
    rank = {ch: i for i, ch in enumerate(order)}
    return "".join(sorted(s, key=lambda ch: rank.get(ch, 26)))
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param order custom character order
    /// @param s     string to sort
    /// @return      s sorted by order
    pub fn custom_sort_string(order: String, s: String) -> String {
        let rank: HashMap<char, usize> = order.chars().enumerate().map(|(i, c)| (c, i)).collect();

        let mut chars: Vec<char> = s.chars().collect();
        chars.sort_by_key(|c| rank.get(c).copied().unwrap_or(26));
        chars.into_iter().collect()
    }
}
```

## Dry run

**Input:** `order = "cba", s = "abcd"`.

```
ranks: c=0, b=1, a=2.  others -> MAX.
sort "abcd" by rank: c(0), b(1), a(2), d(MAX) -> "cbad" ✓
```

## Complexity

**Time.** Sort O(n log n) or count O(n):

$$
T(n) = O(n \log n) \quad \text{or} \quad O(n)
$$

**Space.** Map/counts:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why `Int.MAX_VALUE` for unmentioned chars?" They must appear after all ordered chars but in *any* relative order — the sentinel rank puts them last while preserving stability (Java/C++ sort stability keeps their original order).
