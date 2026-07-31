# 9.3 Isomorphic Strings

> **Source:** [`src/main/kotlin/string/IsomorphicString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/IsomorphicString.kt)
> **Pattern:** pattern encoding · **Core page**

## The Problem

Given two strings `s` and `t`, return `true` if they are **isomorphic** — the characters in `s` can be replaced to get `t`, with every occurrence of a character mapping to the same character, and *no two characters mapping to the same target* (a one-to-one mapping).

- Constraints: $1 \le n \le 5 \times 10^4$; printable ASCII characters.

## Examples

```
Input:  s = "egg", t = "add"        -> Output: true    (e->a, g->d)
Input:  s = "foo", t = "bar"        -> Output: false   (o would map to both o and r)
Input:  s = "paper", t = "title"    -> Output: true
Input:  s = "ab",   t = "aa"        -> Output: false   (two different chars -> same target)
```

## Intuition — isomorphism is equality of *patterns*, not content

"Can `s` be transformed into `t` by a consistent substitution?" is a question about the **shape** of each string: which positions share a character. The trick that turns it into string equality: **encode each string by the first-occurrence position of each character**.

`s = "egg"` encodes to `0 1 1` (e first seen at 0, g at 1, g at 1). `t = "add"` encodes to `0 1 1` — same pattern, isomorphic. `s = "foo"` encodes to `0 1 1`; `t = "bar"` encodes to `0 1 2` — different patterns, not isomorphic. The mapping question became a *string equality* question: the [encode-then-equate](pattern-primer.md) move from the primer.

**Why does encoding catch the "two-to-one" case?** `s = "ab"` → `0 1`; `t = "aa"` → `0 0`. Different encodings. The one-to-one requirement (bijection) is exactly what "both strings encode identically" enforces: if `a -> x` and `b -> x` were allowed, `s`'s encoding would differ from `t`'s at that position.

The alternative (and equally common) approach is a **two-way mapping**: `mapST[c]` and `mapTS[c]`, checking both directions during one pass. The encoding version needs only *one* map per string — the repo's style.

## Approach 1 — Two synchronized maps (one pass, direct)

`mapS[t[i]]` and `mapT[s[i]]` checked/assigned simultaneously: correct and $O(n)$, but two maps and two consistency checks per character. The encoding below is the same idea packaged as "build canonical forms, compare".

## Approach 2 — First-occurrence encoding (the repo's version, optimal)

```kotlin
class IsomorphicString {
    /**
     * @param s input string
     * @return  first-occurrence encoding, e.g. "egg" -> "0 1 1 "
     */
    fun encode(s: String): String {
        val map = mutableMapOf<Char, Int>()
        val sb = StringBuilder()
        var code = 0

        for (c in s) {
            if (c !in map) map[c] = code++      // first sighting gets a fresh code
            sb.append(map[c]).append(" ")       // every later sighting reuses it
        }
        return sb.toString()
    }

    /**
     * @param s first string
     * @param t second string
     * @return  true iff the strings have identical substitution patterns
     */
    fun isIsomorphic(s: String, t: String): Boolean {
        return encode(s) == encode(t)
    }
}
```

```java
import java.util.*;

public class IsomorphicStrings {
    /** @param s input string */
    private String encode(String s) {
        Map<Character, Integer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        int code = 0;

        for (char c : s.toCharArray()) {
            if (!map.containsKey(c)) map.put(c, code++);   // first sighting gets a fresh code
            sb.append(map.get(c)).append(' ');
        }
        return sb.toString();
    }

    /**
     * @param s first string
     * @param t second string
     * @return  true iff the strings have identical substitution patterns
     */
    public boolean isIsomorphic(String s, String t) {
        return encode(s).equals(encode(t));
    }
}
```

```cpp
#include <string>
#include <unordered_map>

class IsomorphicStrings {
    /** @param s input string */
    std::string encode(const std::string& s) {
        std::unordered_map<char, int> map;
        std::string out;
        int code = 0;

        for (char c : s) {
            if (!map.count(c)) map[c] = code++;            // first sighting gets a fresh code
            out += std::to_string(map[c]) + " ";
        }
        return out;
    }

public:
    /**
     * @param s first string
     * @param t second string
     * @return  true iff the strings have identical substitution patterns
     */
    bool isIsomorphic(std::string s, std::string t) {
        return encode(s) == encode(t);
    }
};
```

```python
def is_isomorphic(s: str, t: str) -> bool:
    """
    @param s: first string
    @param t: second string
    @return:  true iff the strings have identical substitution patterns
    """

    def encode(x: str) -> str:
        first = {}
        parts = []
        for c in x:
            if c not in first:
                first[c] = len(first)      # first sighting gets a fresh code
            parts.append(str(first[c]))
        return " ".join(parts)

    return encode(s) == encode(t)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param s first string
    /// @param t second string
    /// @return  true iff the strings have identical substitution patterns
    pub fn is_isomorphic(s: String, t: String) -> bool {
        fn encode(x: &str) -> String {
            let mut first: HashMap<char, usize> = HashMap::new();
            let mut out = Vec::new();
            for c in x.chars() {
                let code = *first.entry(c).or_insert_with(|| first.len());  // fresh code on first sighting
                out.push(code.to_string());
            }
            out.join(" ")
        }
        encode(&s) == encode(&t)
    }
}
```

## Dry run

**Input:** `s = "paper"`, `t = "title"`.

```
encode("paper"):
  'p' new -> code 0.  encode so far: "0"
  'a' new -> code 1.                     "0 1"
  'p' seen -> code 0.                    "0 1 0"
  'e' new -> code 2.                     "0 1 0 2"
  'r' new -> code 3.                     "0 1 0 2 3"
  -> "0 1 0 2 3"

encode("title"):
  't' new -> 0.                          "0"
  'i' new -> 1.                          "0 1"
  't' seen -> 0.                         "0 1 0"
  'l' new -> 2.                          "0 1 0 2"
  'e' new -> 3.                          "0 1 0 2 3"
  -> "0 1 0 2 3"

Encodings equal -> true ✓   (the pattern "a b a c d" matches "a b a c d")
```

The failure case `s = "ab"`, `t = "aa"`: encodings are `"0 1"` vs `"0 0"` — different. The position-based codes make the *bijection* requirement structural: two distinct characters in `s` must have produced two distinct codes, and `t`'s same-position codes must match.

## Complexity

**Time.** One pass per string, $O(1)$ map operations:

$$
T(n) = O(n)
$$

**Space.** Two maps of distinct characters, plus the encoded strings:

$$
S(n) = O(\Sigma) \subseteq O(n)
$$

## Variants & follow-ups

- **Word Pattern** — the same isomorphism check with *words* as the units: `"abba"` vs `["dog","cat","cat","dog"]`; encode both sides (pattern → codes, words → codes) and compare.
- **Valid Anagram** ([9.1](valid-anagram.md)) — anagram ignores order, isomorphism preserves it positionally — a good pair to state side by side in an interview.
- **Find And Replace Pattern / Word Square** (`src/main/kotlin/string/backtracking/WordSquare.kt`) — grouping words by their canonical pattern is this encoding as a hash key, exactly like [9.2](group-anagrams.md) does for counts.
- **Interview follow-up:** "Why is one map per string enough?" A single map per string encodes *that* string's repetition structure. Two strings are isomorphic iff their repetition structures are identical — so comparing the two encodings checks the bijection without ever materializing the `s -> t` map itself.
