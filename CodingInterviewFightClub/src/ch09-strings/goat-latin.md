# 9.18 Goat Latin

> **Source**: [`src/main/kotlin/string/GoatLatin.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/GoatLatin.kt)
> **Pattern**: word transform · **Core page**

## The Problem

Transform each word: vowel-start → append "ma"; consonant-start → move the first letter to the end + "ma"; append `a` × (index+1).

- Constraints: words ≤ 150; lowercase/uppercase.

## Examples

```
Input:  sentence = "I speak Goat Latin"   -> Output: "Imaa peaksmaaa oatGmaaaa atinLmaaaaa"
```

## Intuition — split, transform, rejoin with the per-word `a` count

```kotlin
return sentence.split(" ").mapIndexed { index, word ->
    if (word[0] in vowels) {
        word + "ma" + "a".repeat(index + 1)
    } else {
        word.substring(1) + word[0] + "ma" + "a".repeat(index + 1)
    }
}.joinToString(" ")
```

The index IS the `a`-count — one pass, no bookkeeping.

## Approach 1 — Transform map (the repo's version, optimal)

```kotlin
class GoatLatin {
    /**
     * @param sentence input sentence
     * @return         goat-latin transform
     */
    fun toGoatLatin(sentence: String): String {
        val vowels = setOf('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U')

        return sentence.split(" ").mapIndexed { index, word ->
            if (word[0] in vowels) {
                word + "ma" + "a".repeat(index + 1)
            } else {
                word.substring(1) + word[0] + "ma" + "a".repeat(index + 1)
            }
        }.joinToString(" ")
    }
}
```

```java
public class GoatLatin {
    private static final String VOWELS = "aeiouAEIOU";

    /**
     * @param sentence input sentence
     * @return         goat-latin transform
     */
    public String toGoatLatin(String sentence) {
        String[] words = sentence.split(" ");

        for (int i = 0; i < words.length; i++) {
            String w = words[i];

            if (VOWELS.indexOf(w.charAt(0)) >= 0) {
                words[i] = w + "ma";
            } else {
                words[i] = w.substring(1) + w.charAt(0) + "ma";
            }

            StringBuilder a = new StringBuilder();
            for (int j = 0; j <= i; j++) a.append('a');
            words[i] += a;
        }
        return String.join(" ", words);
    }
}
```

```cpp
#include <string>
#include <vector>
#include <sstream>

class GoatLatin {
public:
    /**
     * @param sentence input sentence
     * @return         goat-latin transform
     */
    std::string toGoatLatin(std::string sentence) {
        std::istringstream iss(sentence);
        std::vector<std::string> words;
        std::string word;
        while (iss >> word) words.push_back(word);

        auto isVowel = [](char c) {
            c = std::tolower(c);
            return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
        };

        for (int i = 0; i < (int)words.size(); i++) {
            std::string w = words[i];
            if (!isVowel(w[0])) w = w.substr(1) + w[0];

            words[i] = w + "ma" + std::string(i + 1, 'a');
        }

        std::string result;
        for (int i = 0; i < (int)words.size(); i++) {
            if (i) result += ' ';
            result += words[i];
        }
        return result;
    }
};
```

```python
def to_goat_latin(sentence: str) -> str:
    """
    @param sentence: input sentence
    @return:         goat-latin transform
    """
    vowels = set("aeiouAEIOU")

    return " ".join(
        (word if word[0] in vowels else word[1:] + word[0]) + "ma" + "a" * (i + 1)
        for i, word in enumerate(sentence.split())
    )
```

```rust
impl Solution {
    /// @param sentence input sentence
    /// @return         goat-latin transform
    pub fn to_goat_latin(sentence: String) -> String {
        let is_vowel = |c: char| matches!(c.to_ascii_lowercase(), 'a' | 'e' | 'i' | 'o' | 'u');

        sentence
            .split_whitespace()
            .enumerate()
            .map(|(i, word)| {
                let mut w = word.to_string();
                if !is_vowel(w.chars().next().unwrap()) {
                    let first = w.remove(0);
                    w.push(first);
                }
                w.push_str("ma");
                w.push_str(&"a".repeat(i + 1));
                w
            })
            .collect::<Vec<_>>()
            .join(" ")
    }
}
```

## Dry run

**Input:** `"I speak Goat Latin"`.

```
"I": vowel -> "I" + "ma" + "a" = "Imaa"
"speak": consonant -> "peaks" + "ma" + "aa" = "peaksmaaa"
"Goat": -> "oatG" + "ma" + "aaa" = "oatGmaaaa"
"Latin": -> "atinL" + "ma" + "aaaa" = "atinLmaaaaa"
Output: "Imaa peaksmaaa oatGmaaaa atinLmaaaaa" ✓
```

## Complexity

**Time.** Each char touched once:

$$
T(n) = O(n)
$$

**Space.** The output:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does the `a`-count use the word index?" The rule is `a` repeated `wordIndex + 1` times — the 0-based enumerate index IS the repeat count, no separate counter needed.
