# 9.15 Text Justification

> **Source:** [`src/main/kotlin/simulation/TextJustification.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/simulation/TextJustification.kt)
> **Pattern:** greedy line-packing + space distribution · **Core page**

## The Problem

Format words into full-width lines: justify all but the last line (evenly spaced); the last is left-justified.

- Constraints: $1 \le$ words; maxWidth ≤ 100.

## Examples

```
Input:  words = ["This","is","an","example","of","text","justification."], maxWidth = 16
Output: ["This    is    an",
         "example  of text",
         "justification.  "]
```

## Intuition — pack greedily, then two spacing rules

Two passes per line: **pack** words until the next won't fit, then **distribute** the spare spaces — evenly between words for full lines, trailing for the last line:

```
pack:  currentLine = []; currentLength = 0
       if currentLength + word.length + currentLine.size > maxWidth:   # +1 space per gap
           emit justifyLine(currentLine, ...)
       add word

justifyLine(words, currentLength, maxWidth):
    spaces = maxWidth - currentLength           # to distribute
    gaps = words.size - 1
    if gaps == 0: pad the single word's right
    base = spaces / gaps; extra = spaces % gaps
    join words with base spaces, first `extra` gaps get +1

justifyLastLine(words, maxWidth):
    words joined with single spaces, right-padded to maxWidth
```

**Why `currentLine.size` in the fit check?** The gap count is words−1 — adding one space per *existing* word approximates the gaps; the exact test is `length + word.length + (currentLine.size)` (the existing gaps). The off-by-one is what the pack condition encodes.

**Why `spaces / gaps` + `%`?** Even distribution puts `base` spaces per gap; the remainder goes to the *leftmost* gaps (the standard left-weighted justification).

## Approach 1 — Greedy pack + two justify functions (the repo's version, optimal)

```kotlin
class TextJustification {
    /**
     * @param words    words to format
     * @param maxWidth line width
     * @return         justified lines
     */
    fun fullJustify(words: Array<String>, maxWidth: Int): List<String> {
        val result = mutableListOf<String>()
        var currentLine = mutableListOf<String>()
        var currentLength = 0

        for (word in words) {
            if (currentLength + word.length + currentLine.size > maxWidth) {
                result.add(justifyLine(currentLine, currentLength, maxWidth))
                currentLine = mutableListOf()
                currentLength = 0
            }
            currentLine.add(word)
            currentLength += word.length
        }

        result.add(justifyLastLine(currentLine, maxWidth))
        return result
    }

    private fun justifyLine(words: List<String>, currentLength: Int, maxWidth: Int): String {
        val spaces = maxWidth - currentLength
        val gaps = words.size - 1

        if (gaps == 0) return words[0] + " ".repeat(spaces)

        val base = spaces / gaps
        val extra = spaces % gaps

        return buildString {
            for (i in words.indices) {
                append(words[i])
                if (i < gaps) {
                    append(" ".repeat(base + if (i < extra) 1 else 0))
                }
            }
        }
    }

    private fun justifyLastLine(words: List<String>, maxWidth: Int): String {
        val joined = words.joinToString(" ")
        return joined + " ".repeat(maxWidth - joined.length)
    }
}
```

```java
import java.util.*;

public class TextJustification {
    /**
     * @param words    words to format
     * @param maxWidth line width
     * @return         justified lines
     */
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        List<String> line = new ArrayList<>();
        int length = 0;

        for (String word : words) {
            if (length + word.length() + line.size() > maxWidth) {
                result.add(justify(line, length, maxWidth, false));
                line.clear();
                length = 0;
            }
            line.add(word);
            length += word.length();
        }
        result.add(justify(line, length, maxWidth, true));
        return result;
    }

    private String justify(List<String> words, int len, int max, boolean last) {
        int spaces = max - len;
        int gaps = words.size() - 1;

        if (last || gaps == 0) {
            String joined = String.join(" ", words);
            while (joined.length() < max) joined += " ";
            return joined;
        }

        int base = spaces / gaps, extra = spaces % gaps;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            sb.append(words.get(i));
            if (i < gaps) {
                for (int s = 0; s < base + (i < extra ? 1 : 0); s++) sb.append(' ');
            }
        }
        return sb.toString();
    }
}
```

```cpp
#include <string>
#include <vector>

class TextJustification {
    std::string justify(const std::vector<std::string>& words, int len, int max, bool last) {
        int spaces = max - len;
        int gaps = (int)words.size() - 1;

        if (last || gaps == 0) {
            std::string joined;
            for (int i = 0; i < (int)words.size(); i++) {
                if (i) joined += ' ';
                joined += words[i];
            }
            joined.append(max - joined.size(), ' ');
            return joined;
        }

        int base = spaces / gaps, extra = spaces % gaps;
        std::string result;
        for (int i = 0; i < (int)words.size(); i++) {
            result += words[i];
            if (i < gaps) result.append(base + (i < extra ? 1 : 0), ' ');
        }
        return result;
    }

public:
    /**
     * @param words    words to format
     * @param maxWidth line width
     * @return         justified lines
     */
    std::vector<std::string> fullJustify(std::vector<std::string>& words, int maxWidth) {
        std::vector<std::string> result;
        std::vector<std::string> line;
        int length = 0;

        for (const std::string& word : words) {
            if (length + (int)word.size() + (int)line.size() > maxWidth) {
                result.push_back(justify(line, length, maxWidth, false));
                line.clear();
                length = 0;
            }
            line.push_back(word);
            length += word.size();
        }
        result.push_back(justify(line, length, maxWidth, true));
        return result;
    }
};
```

```python
def full_justify(words: list[str], max_width: int) -> list[str]:
    """
    @param words:    words to format
    @param max_width: line width
    @return:          justified lines
    """
    def justify(words, length, last=False):
        spaces = max_width - length
        gaps = len(words) - 1

        if last or gaps == 0:
            joined = " ".join(words)
            return joined + " " * (max_width - len(joined))

        base, extra = divmod(spaces, gaps)
        result = []
        for i, word in enumerate(words):
            result.append(word)
            if i < gaps:
                result.append(" " * (base + (1 if i < extra else 0)))
        return "".join(result)

    result = []
    line, length = [], 0

    for word in words:
        if length + len(word) + len(line) > max_width:
            result.append(justify(line, length))
            line, length = [], 0
        line.append(word)
        length += len(word)

    result.append(justify(line, length, last=True))
    return result
```

```rust
impl Solution {
    /// @param words    words to format
    /// @param max_width line width
    /// @return         justified lines
    pub fn full_justify(words: Vec<String>, max_width: i32) -> Vec<String> {
        let mut result = Vec::new();
        let mut line: Vec<String> = Vec::new();
        let mut length = 0i32;

        for word in &words {
            if length + word.len() as i32 + line.len() as i32 > max_width {
                result.push(Self::justify(&line, length, max_width, false));
                line.clear();
                length = 0;
            }
            line.push(word.clone());
            length += word.len() as i32;
        }
        result.push(Self::justify(&line, length, max_width, true));
        result
    }

    fn justify(words: &[String], len: i32, max: i32, last: bool) -> String {
        let spaces = max - len;
        let gaps = words.len() as i32 - 1;

        if last || gaps == 0 {
            let joined = words.join(" ");
            return format!("{:<width$}", joined, width = max as usize);
        }

        let (base, extra) = (spaces / gaps, spaces % gaps);
        let mut s = String::new();
        for (i, w) in words.iter().enumerate() {
            s.push_str(w);
            if (i as i32) < gaps {
                s.push_str(&" ".repeat((base + if (i as i32) < extra { 1 } else { 0 }) as usize));
            }
        }
        s
    }
}
```

## Dry run

**Input:** `words = ["This","is","an","example","of","text","justification."]`, `maxWidth = 16`.

```
pack: "This" (4) -> "is" (4+2+1=7) -> "an" (7+2+2=11) -> "example": 11+7+3=21 > 16 -> emit [This,is,an]
      justify: length=7, spaces=9, gaps=2.  base=4, extra=1.
      "This" + 5 spaces + "is" + 4 spaces + "an" = "This    is    an" ✓

pack: "example"(7) -> "of" (7+2+1=10) -> "text" (10+4+2=16) -> "justification.": 16+14+3 > 16 -> emit
      justify: length=11, spaces=5, gaps=2.  base=2, extra=1.
      "example  of text" ✓

last: "justification." + pad to 16 = "justification.  " ✓
```

The pack condition's `+ currentLine.size` is the gap-space accounting: it rejects "example" because adding it (7 chars) plus the 2 existing gap-spaces exceeds 16. The `divmod` distribution (`base` + extra to the left gaps) is the even-justification rule; the last line drops to single spaces + trailing pad.

## Complexity

**Time.** Each word touched once per phase:

$$
T(n, w) = O(n \cdot w)
$$

**Space.** The output:

$$
S(n, w) = O(n \cdot w)
$$

## Variants & follow-ups

- **Longest Substring / word-wrap family** — the greedy line-packing with a different cost.
- **Interview follow-up:** "Why is the last line left-justified?" The spec: no word can be split, and the final line has no *following* line to align with — so single spaces + right pad. The `justifyLine`/`justifyLastLine` split is the two rules made explicit; merging them into one function with a `last` flag is the cleaner refactor.
