# 9.6 Reverse Words In A String

> **Source:** [`src/main/kotlin/string/ReverseWordsInString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/ReverseWordsInString.kt)
> **Pattern:** split + two pointers · **Core page**

## The Problem

Given a string `s`, return the string with its **words in reverse order** — words are maximal runs of non-space characters, separated by one or more spaces. Remove all leading/trailing/multiple spaces; a single space separates the output words.

- Constraints: $1 \le n \le 10^4$; printable ASCII.

## Examples

```
Input:  s = "the sky is blue"        -> Output: "blue is sky the"
Input:  s = "  hello world  "        -> Output: "world hello"   (spaces trimmed)
Input:  s = "a good   example"       -> Output: "example good a"
```

## Intuition — reverse the *words*, not the characters

Two layers of "reverse" get confused here:

1. Reversing the **characters** of the whole string (`"the sky"` -> `"yks eht"`) — wrong.
2. Reversing the **order of words** (`"the sky"` -> `"sky the"`) — right.

The clean decomposition: **tokenize into words, then reverse the token list**. The repo does exactly this: split on spaces, filter the empty tokens (which is how split represents runs of multiple spaces), then reverse the word list with two pointers (the [swap dance from Chapter 3](../ch03-arrays/index.md)), then join with single spaces.

**Why filter empty tokens?** `"  hello world  ".split(" ")` in Kotlin yields `["", "", "hello", "world", "", ""]` — the extra spaces become empty strings. Filtering them is what removes the leading/trailing/multiple-space noise in one stroke. (Java's `split(" ")` with regex has different behavior — the repo's `.trim()`-free approach relies on this filtering; the alternative idiom is `split("\\s+")`.)

**Why two-pointer reverse instead of `reversed()`?** Either works; the two-pointer swap is the *explicit* version of the same idea and matches the "reverse array in place" lesson from Chapter 3. It also works on lists in any language.

## Approach 1 — Character-level two passes (in-place flavor)

Reverse the whole string, then reverse each word individually: $O(n)$ time, $O(1)$ extra space. The classic C-style solution — elegant, but fiddly with spaces. The split version below trades a little memory for clarity.

## Approach 2 — Split, reverse, join (the repo's version, optimal)

```kotlin
class ReverseWordsInString {
    /**
     * @param s input string with words separated by spaces
     * @return  the words in reverse order, single-spaced
     */
    fun reverseWords(s: String): String {
        val words = s.split(" ").filter { it.isNotEmpty() }.toMutableList()

        // Two pointers over the word list
        var (start, end) = Pair(0, words.lastIndex)
        while (start < end) {
            words[end] = words[start].also { words[start] = words[end] }
            start++
            end--
        }
        return words.joinToString(separator = " ").trim()
    }
}
```

```java
public class ReverseWordsInAString {
    /**
     * @param s input string with words separated by spaces
     * @return  the words in reverse order, single-spaced
     */
    public String reverseWords(String s) {
        String[] words = s.trim().split("\\s+");       // regex: one or more spaces
        StringBuilder sb = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {  // walk the word list backward
            sb.append(words[i]);
            if (i > 0) sb.append(' ');
        }
        return sb.toString();
    }
}
```

```cpp
#include <algorithm>
#include <sstream>
#include <string>
#include <vector>

class ReverseWordsInAString {
public:
    /**
     * @param s input string with words separated by spaces
     * @return  the words in reverse order, single-spaced
     */
    std::string reverseWords(std::string s) {
        std::istringstream in(s);                    // tokenizes on whitespace
        std::vector<std::string> words;
        std::string word;
        while (in >> word) words.push_back(word);

        std::reverse(words.begin(), words.end());    // reverse the word list

        std::string result;
        for (int i = 0; i < (int)words.size(); i++) {
            if (i > 0) result += ' ';
            result += words[i];
        }
        return result;
    }
};
```

```python
def reverse_words(s: str) -> str:
    """
    @param s: input string with words separated by spaces
    @return:  the words in reverse order, single-spaced
    """
    return " ".join(s.split()[::-1])       # split() with no args splits on any whitespace
```

```rust
impl Solution {
    /// @param s input string with words separated by spaces
    /// @return  the words in reverse order, single-spaced
    pub fn reverse_words(s: String) -> String {
        s.split_whitespace()
            .rev()
            .collect::<Vec<_>>()
            .join(" ")
    }
}
```

## Dry run

**Input:** `s = "a good   example"` (three spaces between "good" and "example").

```
split(" "):  ["a", "good", "", "", "example"]
filter non-empty: ["a", "good", "example"]
two-pointer reverse:
  swap positions 0 and 2 -> ["example", "good", "a"]
join with single spaces: "example good a" ✓
```

The three spaces became two empty tokens, which the filter dropped — that single `filter { it.isNotEmpty() }` handles both the multiple-space runs and the leading/trailing spaces in `"  hello world  "` (whose split yields `["", "", "hello", "world", "", ""]` → filtered to `["hello","world"]` → `"world hello"`).

## Complexity

**Time.** Split, filter, reverse, join — each $O(n)$:

$$
T(n) = O(n)
$$

**Space.** The token list:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Reverse Words In A String III** — reverse the *characters within each word* but keep word order: the same split, with each token reversed instead of the list.
- **In-place character version** — reverse the whole string, then reverse each word: $O(1)$ extra space. Mention it as the memory-optimal alternative when the interviewer bans extra arrays.
- **Validate IP Address** ([9.7](validate-ip-address.md)) — the same "split into segments" step, used for validation instead of reordering.
- **Interview follow-up:** "Why does `split(" ")` produce empty strings?" Because `" "` is a literal delimiter: every occurrence splits, so consecutive delimiters yield empty tokens (and leading/trailing ones do too). That behavior is exactly what makes the filter line necessary — and exactly what `split("\\s+")` (regex) or `split_whitespace()` (Rust) fold into a single step.
