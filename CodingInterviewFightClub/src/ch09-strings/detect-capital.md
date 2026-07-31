# 9.19 Detect Capital

> **Source**: [`src/main/kotlin/string/DetectCapital.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/DetectCapital.kt)
> **Pattern**: capital-count rules · **Core page**

## The Problem

Is the capitalization "correct": all caps, all lower, or Title-case?

- Constraints: n ≤ 100.

## Examples

```
Input:  "USA"   -> true.  "leetcode" -> true.  "Google" -> true.
Input:  "FlaG"  -> false.
```

## Intuition — count capitals; three allowed shapes

```kotlin
var capitals = 0
for (char in word) if (char.isUpperCase()) capitals++

return capitals == word.length ||          // ALL caps
       capitals == 0 ||                    // all lower
       (capitals == 1 && word[0].isUpperCase())   // Title case
```

## Approach 1 — Count-then-test (the repo's version, optimal)

```kotlin
class DetectCapital {
    /**
     * @param word input word
     * @return     true iff capitalization is correct
     */
    fun detectCapitalUse(word: String): Boolean {
        var capitals = 0

        for (char in word) {
            if (char.isUpperCase()) capitals++
        }

        return capitals == word.length ||
                capitals == 0 ||
                (capitals == 1 && word[0].isUpperCase())
    }
}
```

```java
public class DetectCapital {
    /**
     * @param word input word
     * @return     true iff capitalization is correct
     */
    public boolean detectCapitalUse(String word) {
        int caps = 0;
        for (char c : word.toCharArray()) if (Character.isUpperCase(c)) caps++;

        return caps == word.length() || caps == 0 ||
               (caps == 1 && Character.isUpperCase(word.charAt(0)));
    }
}
```

```cpp
#include <string>
#include <cctype>

class DetectCapital {
public:
    /**
     * @param word input word
     * @return     true iff capitalization is correct
     */
    bool detectCapitalUse(std::string word) {
        int caps = 0;
        for (char c : word) if (std::isupper(c)) caps++;

        return caps == (int)word.size() || caps == 0 ||
               (caps == 1 && std::isupper(word[0]));
    }
};
```

```python
def detect_capital_use(word: str) -> bool:
    """
    @param word: input word
    @return:     true iff capitalization is correct
    """
    caps = sum(1 for ch in word if ch.isupper())

    return caps == len(word) or caps == 0 or (caps == 1 and word[0].isupper())
```

```rust
impl Solution {
    /// @param word input word
    /// @return     true iff capitalization is correct
    pub fn detect_capital_use(word: String) -> bool {
        let caps = word.chars().filter(|c| c.is_uppercase()).count();

        caps == word.len() || caps == 0 ||
            (caps == 1 && word.chars().next().unwrap().is_uppercase())
    }
}
```

## Dry run

**Input:** `"FlaG"`.

```
caps = 2.  len 4.  caps == 0? no.  caps == 1? no.
Output: false ✓
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

- **Interview follow-up:** "Why are exactly three shapes allowed?" The rules define: every letter capital, no letter capital, or only the first. Any mix (e.g. first+third) violates all three — the count test is exact.
