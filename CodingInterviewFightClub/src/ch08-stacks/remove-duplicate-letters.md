# 8.19 Remove Duplicate Letters

> **Source:** [`src/main/kotlin/stack/RemoveDuplicateLetters.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/RemoveDuplicateLetters.kt)
> **Pattern:** monotonic stack with last-occurrence guards · **Core page**

## The Problem

The **smallest lexicographic** string using each letter once, preserving order.

- Constraints: n ≤ 10⁴; lowercase.

## Examples

```
Input:  s = "bcabc"   -> Output: "abc"
Input:  s = "cbacdcbc" -> Output: "acdb"
```

## Intuition — a monotonic stack that pops only when the letter can still appear later

Keep the result stack lexicographically smallest: on each char, pop bigger stack tops *if* they appear later (`lastIndex[top] > i`), and skip chars already placed:

```kotlin
val lastIndex = IntArray(26) { -1 }
val inStack = BooleanArray(26)
val stack = ArrayDeque<Char>()

for (i in s.indices) lastIndex[s[i] - 'a'] = i

for (i in s.indices) {
    val c = s[i]
    if (inStack[c - 'a']) continue                  // already placed: skip

    while (stack.isNotEmpty() && stack.last() > c && lastIndex[stack.last() - 'a'] > i) {
        inStack[stack.removeLast() - 'a'] = false   // pop: it appears again later
    }

    stack.add(c)
    inStack[c - 'a'] = true
}
return stack.joinToString("")
```

**Why the `lastIndex > i` guard?** Popping is only legal if the popped letter *re-appears* later — otherwise it's lost forever. The guard is what makes the greedy safe; the stack is monotone increasing, the [8.5](largest-rectangle-in-histogram.md) monotonic-stack discipline with a data-dependency.

**Why `inStack`?** A letter already in the result can't be re-added — duplicates skip. The boolean mirrors the stack's contents ([8.4](next-greater-element-ii.md) visited-map style).

## Approach 1 — Greedy pick-min each round (O(26n))

Repeatedly find the smallest char whose prefix is removable: correct, slow.

## Approach 2 — Monotonic stack + lastIndex (the repo's version, optimal)

```kotlin
class RemoveDuplicateLetters {
    /**
     * @param s input string
     * @return  smallest lexicographic result with each letter once
     */
    fun removeDuplicateLetters(s: String): String {
        val lastIndex = IntArray(26) { -1 }
        val inStack = BooleanArray(26)
        val stack = ArrayDeque<Char>()

        for (i in s.indices) lastIndex[s[i] - 'a'] = i

        for (i in s.indices) {
            val c = s[i]

            if (inStack[c - 'a']) continue

            while (stack.isNotEmpty() && stack.last() > c && lastIndex[stack.last() - 'a'] > i) {
                inStack[stack.removeLast() - 'a'] = false
            }

            stack.add(c)
            inStack[c - 'a'] = true
        }
        return stack.joinToString("")
    }
}
```

```java
import java.util.*;

public class RemoveDuplicateLetters {
    /**
     * @param s input string
     * @return  smallest lexicographic result with each letter once
     */
    public String removeDuplicateLetters(String s) {
        int[] last = new int[26];
        boolean[] placed = new boolean[26];
        Deque<Character> stack = new ArrayDeque<>();

        for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (placed[c - 'a']) continue;

            while (!stack.isEmpty() && stack.peek() > c && last[stack.peek() - 'a'] > i) {
                placed[stack.pop() - 'a'] = false;
            }

            stack.push(c);
            placed[c - 'a'] = true;
        }

        StringBuilder sb = new StringBuilder();
        for (char c : stack) sb.append(c);
        return sb.reverse().toString();
    }
}
```

```cpp
#include <string>
#include <vector>

class RemoveDuplicateLetters {
public:
    /**
     * @param s input string
     * @return  smallest lexicographic result with each letter once
     */
    std::string removeDuplicateLetters(std::string s) {
        std::vector<int> last(26, -1);
        std::vector<bool> placed(26, false);
        std::string stack;

        for (int i = 0; i < (int)s.size(); i++) last[s[i] - 'a'] = i;

        for (int i = 0; i < (int)s.size(); i++) {
            char c = s[i];
            if (placed[c - 'a']) continue;

            while (!stack.empty() && stack.back() > c && last[stack.back() - 'a'] > i) {
                placed[stack.back() - 'a'] = false;
                stack.pop_back();
            }

            stack.push_back(c);
            placed[c - 'a'] = true;
        }
        return stack;
    }
};
```

```python
def remove_duplicate_letters(s: str) -> str:
    """
    @param s: input string
    @return:  smallest lexicographic result with each letter once
    """
    last = {ch: i for i, ch in enumerate(s)}
    placed = set()
    stack = []

    for i, ch in enumerate(s):
        if ch in placed:
            continue

        while stack and stack[-1] > ch and last[stack[-1]] > i:
            placed.remove(stack.pop())

        stack.append(ch)
        placed.add(ch)

    return "".join(stack)
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param s input string
    /// @return  smallest lexicographic result with each letter once
    pub fn remove_duplicate_letters(s: String) -> String {
        let bytes: Vec<char> = s.chars().collect();
        let mut last = std::collections::HashMap::new();
        for (i, &ch) in bytes.iter().enumerate() { last.insert(ch, i); }

        let mut placed: HashSet<char> = HashSet::new();
        let mut stack: Vec<char> = Vec::new();

        for (i, &ch) in bytes.iter().enumerate() {
            if placed.contains(&ch) { continue; }

            while let Some(&top) = stack.last() {
                if top < ch || last[&top] < i { break; }
                placed.remove(&top);
                stack.pop();
            }

            stack.push(ch);
            placed.insert(ch);
        }
        stack.into_iter().collect()
    }
}
```

## Dry run

**Input:** `s = "bcabc"`.

```
last: b=3, c=4, a=2
i=0 'b': push.  stack [b].  placed {b}
i=1 'c': top b < c -> no pop.  push.  [b,c].  {b,c}
i=2 'a': top c > a && last[c]=4 > 2 -> pop c.  top b > a && last[b]=3 > 2 -> pop b.
  stack [].  push a.  [a].  {a}
i=3 'b': top a < b -> push.  [a,b].  {a,b}
i=4 'c': push.  [a,b,c].

Output: "abc" ✓
```

The pops at i=2 are the algorithm's heart: both `c` and `b` are bigger than `a` *and* re-appear later (indices 4, 3) — popping them makes the result start with `a`, the lexicographic win. The `lastIndex > i` guard is what permits the pops; without it, "b" would be lost.

## Complexity

**Time.** Each char pushed/popped once:

$$
T(n) = O(n)
$$

**Space.** Stack + flags:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Smallest Subsequence Of Distinct Characters** — the identical problem (duplicate file).
- **Monotonic stack family** ([8.3](daily-temperatures.md), [8.5](largest-rectangle-in-histogram.md)) — the pop-when-dominated engine.
- **Interview follow-up:** "Why is the pop safe only with `lastIndex > i`?" Popping removes a letter from the result — if it never appears again, the result becomes impossible. The last-occurrence table answers "can I afford to defer it?" in O(1); that deferral is exactly what buys the lexicographic minimum.
