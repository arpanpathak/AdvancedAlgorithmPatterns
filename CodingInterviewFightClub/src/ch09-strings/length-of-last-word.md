# 9.16 Length Of Last Word

> **Source:** [`src/main/kotlin/string/LengthOfLastWord.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/LengthOfLastWord.kt)
> **Pattern:** backward scan · **Core page**

## The Problem

The length of the last word (words separated by spaces; trailing spaces possible).

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  s = "Hello World"       -> Output: 5
Input:  s = "   fly me   to   the moon  "  -> Output: 4
```

## Intuition — skip trailing spaces, then count non-spaces

Scanning backward avoids splitting:

```kotlin
var i = s.length - 1
var len = 0

while (i >= 0 && s[i] == ' ') i--     // skip trailing spaces
while (i >= 0 && s[i] != ' ') {
    len++
    i--
}
return len
```

## Approach 1 — Split and filter

`split(" ").filter{it.isNotEmpty()}.last().length`: fine, allocates.

## Approach 2 — Backward scan (the repo's version, optimal)

```kotlin
class LengthOfLastWord {
    /**
     * @param s input string
     * @return  length of the last word
     */
    fun lengthOfLastWord(s: String): Int {
        var i = s.length - 1
        var len = 0

        while (i >= 0 && s[i] == ' ') i--
        while (i >= 0 && s[i] != ' ') {
            len++
            i--
        }
        return len
    }
}
```

```java
public class LengthOfLastWord {
    /**
     * @param s input string
     * @return  length of the last word
     */
    public int lengthOfLastWord(String s) {
        int i = s.length() - 1, len = 0;

        while (i >= 0 && s.charAt(i) == ' ') i--;
        while (i >= 0 && s.charAt(i) != ' ') { len++; i--; }
        return len;
    }
}
```

```cpp
#include <string>

class LengthOfLastWord {
public:
    /**
     * @param s input string
     * @return  length of the last word
     */
    int lengthOfLastWord(std::string s) {
        int i = s.size() - 1, len = 0;

        while (i >= 0 && s[i] == ' ') i--;
        while (i >= 0 && s[i] != ' ') { len++; i--; }
        return len;
    }
};
```

```python
def length_of_last_word(s: str) -> int:
    """
    @param s: input string
    @return:  length of the last word
    """
    i, length = len(s) - 1, 0

    while i >= 0 and s[i] == " ":
        i -= 1
    while i >= 0 and s[i] != " ":
        length += 1
        i -= 1

    return length
```

```rust
impl Solution {
    /// @param s input string
    /// @return  length of the last word
    pub fn length_of_last_word(s: String) -> i32 {
        let bytes = s.as_bytes();
        let mut i = bytes.len() as i32 - 1;
        let mut len = 0;

        while i >= 0 && bytes[i as usize] == b' ' { i -= 1; }
        while i >= 0 && bytes[i as usize] != b' ' { len += 1; i -= 1; }
        len
    }
}
```

## Reading the code — what's actually happening

```kotlin
var i = s.length - 1
var len = 0
while (i >= 0 && s[i] == ' ') i--     // skip trailing spaces
while (i >= 0 && s[i] != ' ') {
    len++
    i--
}
return len
```

Walk the string **backward** — the last word is at the end, so we start there and stop as soon as we're done. No splitting, no list allocation.

- **`i` starts at the last character** (`s.length - 1`), and the first `while` scoots it leftward over any trailing spaces. For `"   fly me   to   the moon  "` it skips two spaces and lands on `'n'`. The `i >= 0` guard keeps us from running off the front of the string (e.g., input of all spaces).
- **The second `while` counts non-space characters** — that's the word itself. `len` increments for each letter, `i` marches left, and the loop dies the moment it hits a space (the word's left boundary) or the start of the string.
- **The `i >= 0` in the second loop matters** for inputs like `"moon"` — no leading space exists, so without the guard we'd read `s[-1]` and crash.
- **Why not split?** `s.split(" ").filter{...}.last()` allocates a list of every word just to throw away all but one. The backward scan touches only the trailing spaces and the last word — O(len of last word + trailing spaces), never the whole string's worth of tokens.

For `"   fly me   to   the moon  "`: skip 2 spaces → count `m,o,o,n` = 4 → hit the space before `moon` → return `4` ✓.

## Dry run

**Input:** `s = "   fly me   to   the moon  "`.

```
skip trailing: i lands on 'n'.  count 'moon' = 4.  stop at the space.

Output: 4 ✓
```

## Complexity

**Time.** Last word only:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Reverse Words In A String** ([9.6](reverse-words-in-a-string.md)) — the split-filter family.
- **Interview follow-up:** "Why backward?" The answer needs only the *last* word — backward scanning stops at it without touching the rest of the string.
