# 9.9 Number Of Matching Subsequences

> **Source:** [`src/main/kotlin/string/CountNumberOfWordsWhichAreSubSequence.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/CountNumberOfWordsWhichAreSubSequence.kt)
> **Pattern:** 26 buckets of word-states · **Core page**

## The Problem

Given a string `s` and a list of `words`, count how many words are a **subsequence** of `s`.

- Constraints: $1 \le |s|, \text{words} \le 10^4$; lowercase letters.

## Examples

```
Input:  s = "abcde", words = ["a","bb","acd","ace"]
Output: 3   ("a", "acd", "ace" — "bb" needs two b's)
```

## Intuition — instead of scanning the word list per character, let the words wait in buckets

The naive check (two-pointer per word, [9.10's cousin](is-subsequence-family)) is $O(\text{words} \cdot |s|)$. The bucket trick flips the loops: **for each character of `s`, advance every word that's currently waiting for it** — no re-scans:

1. **Bucket every word by its first character**: 26 queues, each holding `(wordIndex, charIndex)` states.
2. **Walk `s`:** for character `c`, pop the `c`-bucket's queue (the words whose next needed char is `c`), advance each by one; if the word is complete, count it; else push it into the bucket of its *next* needed character.
3. Each character of `s` is processed once; each word state moves through at most `|word|` buckets.

**Why is this O(|s| + total word length)?** Every `(word, charIndex)` state is enqueued and dequeued once per character of its word. The main loop touches only the bucket for the current char — the work is proportional to the *matches*, not to the word list size.

**Why queues (FIFO) and not sets?** Two words may need the same character at the same position (e.g., `"acd"` and `"ace"` both wait for `'a'`); the queue preserves the per-word `charIndex` state, and the `repeat(currentQueue.size)` snapshot in the repo ensures we only advance words that were waiting *before* this character — not ones we just pushed.

**The subsequence check is implicit:** a word is a subsequence iff its characters can be matched in order — the bucket machine literally advances the required character order along `s`, and a word completing means all its characters were found in order.

## Approach 1 — Two-pointer per word (O(words · |s|))

For each word, walk `s` matching characters: correct, quadratic on worst cases.

## Approach 2 — 26-bucket state machine (the repo's version, optimal)

```kotlin
import java.util.ArrayDeque

class CountNumberOfWordsWhichAreSubSequence {

    private data class WordState(val wordIndex: Int, val charIndex: Int)

    /**
     * @param s     the string to match against
     * @param words candidate words
     * @return      number of words that are subsequences of s
     */
    fun numMatchingSubseq(s: String, words: List<String>): Int {
        val waiting: Array<ArrayDeque<WordState>> = Array(26) { ArrayDeque<WordState>() }

        // Bucket each word by its first character
        words.forEachIndexed { wordIndex, word ->
            if (word.isNotEmpty()) {
                val firstChar = word[0]
                waiting[firstChar - 'a'].addLast(WordState(wordIndex, 0))
            }
        }

        var count = 0

        for (c in s) {
            val bucketIndex = c - 'a'
            val currentQueue = waiting[bucketIndex]

            repeat(currentQueue.size) {            // only words waiting BEFORE this char
                val state = currentQueue.removeFirst()

                val wordIndex = state.wordIndex
                val nextCharIndex = state.charIndex + 1
                val word = words[wordIndex]

                if (nextCharIndex == word.length) {
                    count++                        // word fully matched
                } else {
                    val nextChar = word[nextCharIndex]
                    waiting[nextChar - 'a'].addLast(WordState(wordIndex, nextCharIndex))
                }
            }
        }
        return count
    }
}
```

```java
import java.util.*;

public class NumberOfMatchingSubsequences {
    private record State(int wordIndex, int charIndex) {}

    /**
     * @param s     the string to match against
     * @param words candidate words
     * @return      number of words that are subsequences of s
     */
    public int numMatchingSubseq(String s, String[] words) {
        @SuppressWarnings("unchecked")
        Deque<State>[] waiting = new ArrayDeque[26];
        for (int i = 0; i < 26; i++) waiting[i] = new ArrayDeque<>();

        for (int i = 0; i < words.length; i++) {     // bucket by first character
            if (!words[i].isEmpty()) waiting[words[i].charAt(0) - 'a'].add(new State(i, 0));
        }

        int count = 0;
        for (char c : s.toCharArray()) {
            Deque<State> bucket = waiting[c - 'a'];
            int size = bucket.size();                // snapshot: only words waiting BEFORE this char
            for (int k = 0; k < size; k++) {
                State st = bucket.poll();
                int nextIdx = st.charIndex() + 1;
                if (nextIdx == words[st.wordIndex()].length()) {
                    count++;                         // word fully matched
                } else {
                    char next = words[st.wordIndex()].charAt(nextIdx);
                    waiting[next - 'a'].add(new State(st.wordIndex(), nextIdx));
                }
            }
        }
        return count;
    }
}
```

```cpp
#include <queue>
#include <string>
#include <vector>

class NumberOfMatchingSubsequences {
    struct State { int wordIndex, charIndex; };

public:
    /**
     * @param s     the string to match against
     * @param words candidate words
     * @return      number of words that are subsequences of s
     */
    int numMatchingSubseq(std::string s, std::vector<std::string>& words) {
        std::vector<std::queue<State>> waiting(26);

        for (int i = 0; i < (int)words.size(); i++) {   // bucket by first character
            if (!words[i].empty()) waiting[words[i][0] - 'a'].push({i, 0});
        }

        int count = 0;
        for (char c : s) {
            auto& bucket = waiting[c - 'a'];
            int size = bucket.size();                   // snapshot
            for (int k = 0; k < size; k++) {
                State st = bucket.front(); bucket.pop();
                int nextIdx = st.charIndex + 1;
                if (nextIdx == (int)words[st.wordIndex].size()) {
                    count++;                            // word fully matched
                } else {
                    char next = words[st.wordIndex][nextIdx];
                    waiting[next - 'a'].push({st.wordIndex, nextIdx});
                }
            }
        }
        return count;
    }
};
```

```python
from collections import defaultdict, deque

def num_matching_subseq(s: str, words: list[str]) -> int:
    """
    @param s:     the string to match against
    @param words: candidate words
    @return:      number of words that are subsequences of s
    """
    waiting = defaultdict(list)                # char -> list of (word_idx, char_idx)

    for wi, w in enumerate(words):             # bucket by first character
        if w:
            waiting[w[0]].append((wi, 0))

    count = 0
    for c in s:
        bucket = waiting[c]                    # words waiting for this char
        waiting[c] = []
        for wi, ci in bucket:
            next_idx = ci + 1
            if next_idx == len(words[wi]):
                count += 1                     # word fully matched
            else:
                waiting[words[wi][next_idx]].append((wi, next_idx))
    return count
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param s     the string to match against
    /// @param words candidate words
    /// @return      number of words that are subsequences of s
    pub fn num_matching_subseq(s: String, words: Vec<String>) -> i32 {
        let mut waiting: Vec<VecDeque<(usize, usize)>> = (0..26).map(|_| VecDeque::new()).collect();

        for (wi, w) in words.iter().enumerate() {       // bucket by first character
            if let Some(&first) = w.as_bytes().first() {
                waiting[(first - b'a') as usize].push_back((wi, 0));
            }
        }

        let mut count = 0;
        for &c in s.as_bytes() {
            let bucket = waiting[(c - b'a') as usize].clone();   // snapshot
            waiting[(c - b'a') as usize].clear();
            for (wi, ci) in bucket {
                let next_idx = ci + 1;
                if next_idx == words[wi].len() {
                    count += 1;                              // word fully matched
                } else {
                    let next = words[wi].as_bytes()[next_idx];
                    waiting[(next - b'a') as usize].push_back((wi, next_idx));
                }
            }
        }
        count
    }
}
```

## Dry run

**Input:** `s = "abcde"`, `words = ["a","bb","acd","ace"]`.

```
waiting: 'a'->[(0,0)], 'b'->[(1,0)], 'a'->[(2,0)], 'a'->[(3,0)]

c='a': bucket [(0,0),(2,0),(3,0)]:
  (0,0): next_idx 1 == len("a")=1 -> count=1
  (2,0): next 'c' -> waiting['c'] += [(2,1)]
  (3,0): next 'c' -> waiting['c'] += [(3,1)]
c='b': bucket [(1,0)]: next 'b' -> waiting['b'] += [(1,1)]
c='c': bucket [(2,1),(3,1)]: next 'd' -> waiting['d'] += [(2,2)];  next 'e' -> waiting['e'] += [(3,2)]
c='d': bucket [(2,2)]: next_idx 3 == len("acd")=3 -> count=2
c='e': bucket [(3,2)]: next_idx 3 == len("ace")=3 -> count=3

Output: 3 ✓   ("bb" stays stuck waiting for a second 'b' that never comes)
```

The state machine's economy is visible: every `(word, charIndex)` advances exactly once per matched character — `"acd"` rides through buckets `a→c→d` across four characters of `s`, and `"bb"` waits forever in the `'b'` bucket after its first `'b'` was consumed at c='b' with no second `'b'` in sight.

## Complexity

**Time.** Each state moves once; `s` processed once:

$$
T(n, w) = O(n + \text{total word length})
$$

**Space.** The 26 queues hold all states:

$$
S = O(\text{total word length})
$$

## Variants & follow-ups

- **Is Subsequence** (`string/IsSubsequence.kt`) — the single-word version: the two-pointer scan this page generalizes.
- **Longest Word In Dictionary / word-chain family** — bucket-style processing of word lists.
- **Interview follow-up:** "Why snapshot the bucket size before processing?" Words advanced by the current character get pushed into *other* buckets — but a word pushed into the *same* bucket (e.g., one that needs `'a'` twice, like `"aa"`) must wait for the *next* `'a'`, not re-process now. The snapshot (`repeat(currentQueue.size)`) is what enforces that.
