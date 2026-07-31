# 10.17 Group Shifted Strings

> **Source:** [`src/main/kotlin/string/hashtable/GroupShiftedStrings.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/hashtable/GroupShiftedStrings.kt)
> **Pattern:** gap-sequence keys · **Core page**

## The Problem

Group strings that are **shifts** of each other (each char advanced by the same offset, wrapping).

- Constraints: strings ≤ 200; each ≤ 100 chars; lowercase.

## Examples

```
Input:  strings = ["abc","bcd","acef","xyz","az","ba","a","z"]
Output: [["abc","bcd","xyz"],["acef"],["az","ba"],["a","z"]]
```

## Intuition — the shift-class key is the sequence of gaps

Two strings are shifts of each other iff their **adjacent-character differences** (mod 26) match. "abc" → gaps (1,1); "bcd" → (1,1); "xyz" → (1,1) — same class. "az" → (25); "ba" → (25) (b→a wraps). The gap string is the map key:

```kotlin
private fun getKey(str: String): String = buildString {
    for (i in 1 until str.length) {
        append(((str[i] - str[i - 1] + 26) % 26).toString())
    }
}
// "abc" -> "11", "bcd" -> "11", "az" -> "25", "ba" -> "25"
```

**Why `+ 26) % 26`?** Differences can be negative ("az": 'z'−'a' = 25, fine; "za": 'a'−'z' = −25 → +26 → 1). The wrap makes the shift-invariant exact — the [9.2](../ch09-strings/group-anagrams.md) canonical-key idea with gaps instead of frequency counts.

**Why group by the gap string?** A shift changes *starting positions*, never internal gaps — the gap sequence is the shift-equivalence class invariant. The `computeIfAbsent(key) { mutableListOf() }.add(str)` is the grouping idiom ([9.2](../ch09-strings/group-anagrams.md) engine).

## Approach 1 — Pairwise shift comparison (O(n²))

Check each pair for shift-equality: correct, quadratic.

## Approach 2 — Gap-sequence keys (the repo's version, optimal)

```kotlin
class GroupShiftedStrings {
    /**
     * @param strings input strings
     * @return        groups of shift-equivalent strings
     */
    fun groupStrings(strings: Array<String>): List<List<String>> {
        val map = mutableMapOf<String, MutableList<String>>()

        for (str in strings) {
            val key = getKey(str)
            map.computeIfAbsent(key) { mutableListOf() }.add(str)
        }
        return map.values.toList()
    }

    private fun getKey(str: String): String = buildString {
        for (i in 1 until str.length) {
            append(((str[i] - str[i - 1] + 26) % 26).toString())
        }
    }
}
```

```java
import java.util.*;

public class GroupShiftedStrings {
    /**
     * @param strings input strings
     * @return        groups of shift-equivalent strings
     */
    public List<List<String>> groupStrings(String[] strings) {
        Map<String, List<String>> map = new HashMap<>();

        for (String s : strings) {
            String key = getKey(s);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(map.values());
    }

    private String getKey(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < s.length(); i++) {
            int gap = (s.charAt(i) - s.charAt(i - 1) + 26) % 26;
            sb.append(gap).append('#');
        }
        return sb.toString();
    }
}
```

```cpp
#include <string>
#include <unordered_map>
#include <vector>

class GroupShiftedStrings {
    std::string key(const std::string& s) {
        std::string k;
        for (int i = 1; i < (int)s.size(); i++) {
            int gap = (s[i] - s[i - 1] + 26) % 26;
            k += std::to_string(gap) + "#";
        }
        return k;
    }

public:
    /**
     * @param strings input strings
     * @return        groups of shift-equivalent strings
     */
    std::vector<std::vector<std::string>> groupStrings(std::vector<std::string>& strings) {
        std::unordered_map<std::string, std::vector<std::string>> map;

        for (const std::string& s : strings) {
            map[key(s)].push_back(s);
        }

        std::vector<std::vector<std::string>> result;
        for (auto& [_, group] : map) result.push_back(group);
        return result;
    }
};
```

```python
def group_strings(strings: list[str]) -> list[list[str]]:
    """
    @param strings: input strings
    @return:        groups of shift-equivalent strings
    """
    groups = {}

    for s in strings:
        gaps = tuple((ord(s[i]) - ord(s[i - 1])) % 26 for i in range(1, len(s)))
        groups.setdefault(gaps, []).append(s)

    return list(groups.values())
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param strings input strings
    /// @return        groups of shift-equivalent strings
    pub fn group_strings(strings: Vec<String>) -> Vec<Vec<String>> {
        let mut groups: HashMap<Vec<i32>, Vec<String>> = HashMap::new();

        for s in strings {
            let bytes = s.as_bytes();
            let gaps: Vec<i32> = (1..bytes.len())
                .map(|i| ((bytes[i] as i32 - bytes[i - 1] as i32) % 26 + 26) % 26)
                .collect();
            groups.entry(gaps).or_default().push(s);
        }

        groups.into_values().collect()
    }
}
```

## Dry run

**Input:** `strings = ["abc","bcd","acef","xyz","az","ba","a","z"]`.

```
"abc": gaps (1,1).   "bcd": (1,1).   "xyz": (1,1).   -> group key (1,1): [abc, bcd, xyz]
"acef": gaps (2,2,1). -> alone
"az": (25).  "ba": ('a'-'b' = -1, +26 -> 25).  -> group (25): [az, ba]
"a": empty gaps.  "z": empty gaps.  -> group (): [a, z]

Output: [[abc,bcd,xyz],[acef],[az,ba],[a,z]] ✓
```

The wrap (`% 26`) is the shift-invariant: "az" and "ba" are 25 apart in both directions — one via `z−a = 25`, the other via `a−b = −1 → 25` after the modulo. Single-char strings share the empty-gap key (any single char is a shift of any other). The key needs no length prefix here because gaps fully determine the class.

## Complexity

**Time.** One pass over all chars:

$$
T(n, L) = O(n \cdot L)
$$

**Space.** The key map:

$$
S(n, L) = O(n \cdot L)
$$

## Variants & follow-ups

- **Group Anagrams** ([9.2](../ch09-strings/group-anagrams.md)) — the same canonical-key idea with frequency vectors instead of gaps.
- **Interview follow-up:** "Why do gaps define the shift class but not the string?" A uniform shift changes every char by the same offset — differences between adjacent chars are untouched. The gap sequence is *invariant* under shift and *identifying* (equal gaps ⟺ some shift exists), which is exactly what a canonical key needs.
