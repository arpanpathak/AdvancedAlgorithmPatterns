# 8.9 Longest Valid Parentheses

> **Source:** [`src/main/kotlin/stack/LongestValidParanthesis.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/LongestValidParanthesis.kt)
> **Pattern:** stack of *indices* with a base · **Core page**

## The Problem

Given a string of `(` and `)`, return the length of the **longest valid (well-formed) parentheses substring**.

- Constraints: $1 \le n \le 3 \times 10^4$; only parentheses.

## Examples

```
Input:  s = "(()"     -> Output: 2   (the substring "()")
Input:  s = ")()())"  -> Output: 4   (the substring "()()")
```

## Intuition — store *indices* on the stack, keep a base index for broken chains

The [8.1](valid-parentheses.md) matching stack answers "is the whole string valid?"; here a substring may be valid while the whole isn't. The stack must track **where matches begin** — so it holds *indices*, not characters, and keeps a sentinel `-1` as the "base" before any valid chain:

```
stack = [-1]                 # base: the position before the current chain
for i in s.indices:
    if s[i] == '(': stack.push(i)
    else:
        stack.pop()          # match the last '(' (or remove the base)
        if stack.empty(): stack.push(i)      # unmatched ')': new base at i
        else: maxLen = max(maxLen, i - stack.last())
```

**Why `-1` initially and `i` on unmatched `')'`?** A valid chain is a *contiguous* block; its length is `end - base`. The base is the index just before the block starts. When a `')'` finds no `'('` to match (stack empties), the chain *breaks* — the current `i` becomes the new base, and length measurement restarts. The `-1` sentinel makes the very first chain measure from index 0.

**Why does popping a `'('` and reading `i - stack.last()` give the length?** After matching, the stack top is the *last unmatched index* — either the chain's base or an earlier `'('` that starts an enclosing valid block. `i - top` is the distance: exactly the matched segment's length. This is the [8.7](remove-k-digits.md)/[8.3](daily-temperatures.md) "stack holds indices for spans" idiom.

## Approach 1 — DP (also O(n))

`dp[i]` = longest valid ending at i, with `dp[i] = 2 + dp[i-1] + dp[i - dp[i-1] - 2]` on `')'`: correct, more bookkeeping.

## Approach 2 — Stack of indices (the repo's version, optimal)

```kotlin
class LongestValidParanthesis {
    /**
     * @param s parentheses string
     * @return  length of the longest valid substring
     */
    fun longestValidParentheses(s: String): Int {
        val stack = ArrayDeque<Int>()
        stack.addLast(-1)                 // base before the first chain
        var maxLen = 0

        for (i in s.indices) {
            if (s[i] == '(') {
                stack.addLast(i)          // push the index of '('
            } else {
                stack.removeLast()        // match the last '(' (or drop the base)

                if (stack.isEmpty()) {
                    stack.addLast(i)      // unmatched ')': new base, chain broken
                } else {
                    maxLen = maxOf(maxLen, i - stack.last())
                }
            }
        }
        return maxLen
    }
}
```

```java
import java.util.*;

public class LongestValidParentheses {
    /**
     * @param s parentheses string
     * @return  length of the longest valid substring
     */
    public int longestValidParentheses(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1);                            // base before the first chain
        int max = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);                     // push the index of '('
            } else {
                stack.pop();                       // match the last '(' (or drop the base)

                if (stack.isEmpty()) {
                    stack.push(i);                 // unmatched ')': new base
                } else {
                    max = Math.max(max, i - stack.peek());
                }
            }
        }
        return max;
    }
}
```

```cpp
#include <stack>
#include <string>

class LongestValidParentheses {
public:
    /**
     * @param s parentheses string
     * @return  length of the longest valid substring
     */
    int longestValidParentheses(std::string s) {
        std::stack<int> st;
        st.push(-1);                               // base before the first chain
        int max = 0;

        for (int i = 0; i < (int)s.size(); i++) {
            if (s[i] == '(') {
                st.push(i);                        // push the index of '('
            } else {
                st.pop();                          // match the last '(' (or drop the base)

                if (st.empty()) {
                    st.push(i);                    // unmatched ')': new base
                } else {
                    max = std::max(max, i - st.top());
                }
            }
        }
        return max;
    }
};
```

```python
def longest_valid_parentheses(s: str) -> int:
    """
    @param s: parentheses string
    @return:  length of the longest valid substring
    """
    stack = [-1]                     # base before the first chain
    max_len = 0

    for i, c in enumerate(s):
        if c == "(":
            stack.append(i)          # push the index of '('
        else:
            stack.pop()              # match the last '(' (or drop the base)

            if not stack:
                stack.append(i)      # unmatched ')': new base, chain broken
            else:
                max_len = max(max_len, i - stack[-1])
    return max_len
```

```rust
impl Solution {
    /// @param s parentheses string
    /// @return  length of the longest valid substring
    pub fn longest_valid_parentheses(s: String) -> i32 {
        let mut stack: Vec<i32> = vec![-1];      // base before the first chain
        let mut max_len = 0;

        for (i, c) in s.bytes().enumerate() {
            if c == b'(' {
                stack.push(i as i32);            // push the index of '('
            } else {
                stack.pop();                     // match the last '(' (or drop the base)

                if stack.is_empty() {
                    stack.push(i as i32);        // unmatched ')': new base
                } else {
                    max_len = max_len.max(i as i32 - stack[stack.len() - 1]);
                }
            }
        }
        max_len
    }
}
```

## Dry run

**Input:** `s = ")()())"`.

```
stack = [-1], maxLen = 0
i=0 ')': pop -> empty.  push 0.  stack=[0].           (base reset: chain broken before index 0)
i=1 '(': push 1.  stack=[0,1]
i=2 ')': pop 1.  top=0 -> maxLen = max(0, 2-0) = 2.   ("()" at 1..2)
i=3 '(': push 3.  stack=[0,3]
i=4 ')': pop 3.  top=0 -> maxLen = max(2, 4-0) = 4.   ("()()" at 1..4)
i=5 ')': pop 0 -> empty.  push 5.  stack=[5].         (final ')' breaks the chain)

Output: 4 ✓
```

The base-index mechanics: `i=0`'s unmatched `')'` makes index 0 the base, so the chain starting at 1 measures `2 - 0 = 2` and `4 - 0 = 4` — the base *precedes* the whole valid block. The final `')'` pops the base and re-seeds at 5, correctly ending the measurement. `"(()"` gives 2 the same way: `(` push 0, `(` push 1, `)` pop 1 → `2 - 0 = 2`.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Valid Parentheses** ([8.1](valid-parentheses.md)) — the whole-string checker; this page's stack stores indices *and* a base to handle broken chains.
- **Minimum Add To Make Parentheses Valid** (`stack/MinimumAddtoMakeParenthesesValid.kt`) — count the unmatched: the base-reset logic in counting form.
- **Longest Valid Parentheses (DP)** — the `dp[i]` alternative: `dp[i] = 2 + dp[i-1] + dp[i - dp[i-1] - 2]` when `s[i] == ')'` and its match exists.
- **Interview follow-up:** "Why indices on the stack instead of characters?" A character stack can verify matching but can't *measure distance* — the length of a matched segment is `i - stack.top()`, which requires the base index. The `-1`/reset base is what makes the measurement restart correctly after broken chains.
