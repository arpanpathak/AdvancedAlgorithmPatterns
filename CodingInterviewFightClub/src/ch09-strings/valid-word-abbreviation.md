# 9.32 Valid Word Abbreviation

> **Source**: [`src/main/kotlin/string/ValidWordAbbreviation.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/ValidWordAbbreviation.kt)
> **Pattern**: two-pointer expansion · **Core page**

## The Problem

Does `abbr` abbreviate `word` (digits = run lengths, no leading zeros)?

- Constraints: lengths ≤ 100.

## Examples

```
Input:  word = "internationalization", abbr = "i12iz4n"   -> true
Input:  word = "apple", abbr = "a2e"                       -> false
```

## Intuition — walk both; digits expand to skips

```kotlin
var i = 0
var j = 0

while (i < word.length && j < abbr.length) {
    if (abbr[j].isDigit()) {
        if (abbr[j] == '0') return false

        var count = 0
        while (j < abbr.length && abbr[j].isDigit()) {
            count = count * 10 + (abbr[j] - '0')
            j++
        }
        i += count
    } else {
        if (word[i] != abbr[j]) return false
        i++
        j++
    }
}
return i == word.length && j == abbr.length
```

## Approach 1 — Two-pointer expansion (the repo's version, optimal)

```kotlin
class ValidWordAbbreviation {
    /**
     * @param word full word
     * @param abbr abbreviation
     * @return     true iff valid
     */
    fun validWordAbbreviation(word: String, abbr: String): Boolean {
        var i = 0
        var j = 0

        while (i < word.length && j < abbr.length) {
            if (abbr[j].isDigit()) {
                if (abbr[j] == '0') return false

                var count = 0
                while (j < abbr.length && abbr[j].isDigit()) {
                    count = count * 10 + (abbr[j] - '0')
                    j++
                }
                i += count
            } else {
                if (word[i] != abbr[j]) return false
                i++
                j++
            }
        }
        return i == word.length && j == abbr.length
    }
}
```

```java
public class ValidWordAbbreviation {
    /**
     * @param word full word
     * @param abbr abbreviation
     * @return     true iff valid
     */
    public boolean validWordAbbreviation(String word, String abbr) {
        int i = 0, j = 0;

        while (i < word.length() && j < abbr.length()) {
            if (Character.isDigit(abbr.charAt(j))) {
                if (abbr.charAt(j) == '0') return false;

                int count = 0;
                while (j < abbr.length() && Character.isDigit(abbr.charAt(j))) {
                    count = count * 10 + (abbr.charAt(j) - '0');
                    j++;
                }
                i += count;
            } else {
                if (word.charAt(i) != abbr.charAt(j)) return false;
                i++;
                j++;
            }
        }
        return i == word.length() && j == abbr.length();
    }
}
```

```cpp
#include <string>
#include <cctype>

class ValidWordAbbreviation {
public:
    /**
     * @param word full word
     * @param abbr abbreviation
     * @return     true iff valid
     */
    bool validWordAbbreviation(std::string word, std::string abbr) {
        int i = 0, j = 0;

        while (i < (int)word.size() && j < (int)abbr.size()) {
            if (std::isdigit(abbr[j])) {
                if (abbr[j] == '0') return false;

                int count = 0;
                while (j < (int)abbr.size() && std::isdigit(abbr[j])) {
                    count = count * 10 + (abbr[j] - '0');
                    j++;
                }
                i += count;
            } else {
                if (word[i] != abbr[j]) return false;
                i++;
                j++;
            }
        }
        return i == (int)word.size() && j == (int)abbr.size();
    }
};
```

```python
def valid_word_abbreviation(word: str, abbr: str) -> bool:
    """
    @param word: full word
    @param abbr: abbreviation
    @return:     true iff valid
    """
    i = j = 0

    while i < len(word) and j < len(abbr):
        if abbr[j].isdigit():
            if abbr[j] == "0":
                return False

            count = 0
            while j < len(abbr) and abbr[j].isdigit():
                count = count * 10 + int(abbr[j])
                j += 1
            i += count
        else:
            if word[i] != abbr[j]:
                return False
            i += 1
            j += 1

    return i == len(word) and j == len(abbr)
```

```rust
impl Solution {
    /// @param word full word
    /// @param abbr abbreviation
    /// @return     true iff valid
    pub fn valid_word_abbreviation(word: String, abbr: String) -> bool {
        let w: Vec<char> = word.chars().collect();
        let a: Vec<char> = abbr.chars().collect();
        let (mut i, mut j) = (0, 0);

        while i < w.len() && j < a.len() {
            if a[j].is_ascii_digit() {
                if a[j] == '0' { return false; }

                let mut count = 0;
                while j < a.len() && a[j].is_ascii_digit() {
                    count = count * 10 + a[j] as i32 - '0' as i32;
                    j += 1;
                }
                i += count as usize;
            } else {
                if w[i] != a[j] { return false; }
                i += 1;
                j += 1;
            }
        }
        i == w.len() && j == a.len()
    }
}
```

## Dry run

**Input:** `word = "internationalization", abbr = "i12iz4n"`.

```
i: match i.  12: i += 12.  i: match.  z: mismatch?  word[1+12]= 'i'? 
"internationalization": i(0) + 12 = position 13 = 'i'?  i12iz4n: i, skip 12, i, z, skip 4, n.
word: i-n-t-e-r-n-a-t-i-o-n-a-l-i-z-a-t-i-o-n (20).  i(0), skip 12 -> 13='i', skip... 
word[13]='i' match 'i'.  'z': word[14]='z' ✓.  skip 4 -> 18, 'n': word[18]='n' ✓.
Output: true ✓
```

## Complexity

**Time.** One walk:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why reject leading zeros?" `"a02"` would be ambiguous (0 skips are meaningless) — the leading-zero check enforces the canonical form.
