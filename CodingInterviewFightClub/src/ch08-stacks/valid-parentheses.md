# 8.1 Valid Parentheses

> **Source:** [`src/main/kotlin/stack/ValidParentheses.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/ValidParentheses.kt)
> **Pattern:** stack matching · **Core page**

## The Problem

Given a string `s` containing `()[]{}`, return `true` if it is **valid**: brackets close in the correct order and every opener has a closer.

- Constraints: $1 \le n \le 10^4$.

## Examples

```
Input:  s = "()[]{}"      -> Output: true
Input:  s = "(]"          -> Output: false   (mismatched pair)
Input:  s = "([)]"        -> Output: false   (nested wrong: [ closes inside ())
Input:  s = "([])"        -> Output: true    (proper nesting)
```

## Intuition — the most recent opener must be the first to close

Read left to right. When you see an opener, you don't know yet whether it's valid — its closer could be far ahead. When you see a closer, the only opener it can match is the **most recent still-unclosed one**: nesting guarantees `([...])` closes `)` before `]`, so the closer matches exactly what was pushed *last*. That's LIFO — a stack is not a suggestion here, it's the definition.

Three failure modes, each caught by a different check:

1. **Closer with empty stack** — no opener to match (`")"`).
2. **Mismatch** — the closer doesn't match the top opener (`"(]"`).
3. **Leftover openers at the end** — unclosed at the end (`"(()"`).

The whole algorithm: push openers; on a closer, pop-and-compare or fail; at the end, `stack.isEmpty()`.

## Approach 1 — The stack (the repo's version, optimal)

```kotlin
class ValidParentheses {
    /**
     * @param s input string of bracket characters
     * @return  true iff every opener is matched in correct nesting order
     */
    fun isValid(s: String): Boolean {
        val stack = mutableListOf<Char>()
        val openingBraces = listOf('(', '[', '{')

        for (ch in s) {
            if (ch in openingBraces) {
                stack.add(ch)                    // opener: remember it, decide later
                continue
            }

            when {
                stack.isEmpty() -> return false                       // closer with no opener
                ch == ')' -> if (stack.last() == '(') stack.removeLast() else return false
                ch == '}' -> if (stack.last() == '{') stack.removeLast() else return false
                ch == ']' -> if (stack.last() == '[') stack.removeLast() else return false
            }
        }

        return stack.isEmpty()                   // any leftover opener is unmatched
    }
}
```

```java
import java.util.*;

public class ValidParentheses {
    /**
     * @param s input string of bracket characters
     * @return  true iff every opener is matched in correct nesting order
     */
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);                                       // opener: remember it
            } else {
                if (stack.isEmpty()) return false;                   // closer with no opener
                char top = stack.pop();
                if ((c == ')' && top != '(') ||
                    (c == ']' && top != '[') ||
                    (c == '}' && top != '{')) return false;          // mismatch
            }
        }
        return stack.isEmpty();                                      // any leftover opener?
    }
}
```

```cpp
#include <stack>
#include <string>

class ValidParentheses {
public:
    /**
     * @param s input string of bracket characters
     * @return  true iff every opener is matched in correct nesting order
     */
    bool isValid(std::string s) {
        std::stack<char> st;

        for (char c : s) {
            if (c == '(' || c == '[' || c == '{') {
                st.push(c);                                          // opener: remember it
            } else {
                if (st.empty()) return false;                        // closer with no opener
                char top = st.top(); st.pop();
                if ((c == ')' && top != '(') ||
                    (c == ']' && top != '[') ||
                    (c == '}' && top != '{')) return false;          // mismatch
            }
        }
        return st.empty();                                           // any leftover opener?
    }
};
```

```python
def is_valid(s: str) -> bool:
    """
    @param s: input string of bracket characters
    @return:  true iff every opener is matched in correct nesting order
    """
    stack = []
    pairs = {")": "(", "]": "[", "}": "{"}

    for ch in s:
        if ch in pairs:                      # closer
            if not stack or stack.pop() != pairs[ch]:
                return False
        else:
            stack.append(ch)                 # opener: remember it
    return not stack
```

```rust
impl Solution {
    /// @param s input string of bracket characters
    /// @return  true iff every opener is matched in correct nesting order
    pub fn is_valid(s: String) -> bool {
        let mut stack = Vec::new();
        for c in s.chars() {
            match c {
                '(' | '[' | '{' => stack.push(c),              // opener: remember it
                _ => {
                    let Some(top) = stack.pop() else { return false };  // closer, empty stack
                    if (c == ')' && top != '(')
                        || (c == ']' && top != '[')
                        || (c == '}' && top != '{') {
                        return false;                          // mismatch
                    }
                }
            }
        }
        stack.is_empty()                                       // any leftover opener?
    }
}
```

## Dry run

**Input:** `s = "([)]"` — the classic false case.

```
stack = []
'(' opener -> push.   stack = ['(']
'[' opener -> push.   stack = ['(', '[']
')' closer -> top '[' != '(' -> return false ✓
```

Now `s = "([])"`:

```
stack = []
'(' -> push.    stack = ['(']
'[' -> push.    stack = ['(', '[']
']' -> top '[' matches -> pop.   stack = ['(']
')' -> top '(' matches -> pop.   stack = []
end -> stack.isEmpty() -> true ✓
```

The decisive difference: in `([)]`, the `)` tries to match the most recent opener `[` — and fails. The stack's LIFO order *is* the nesting structure; no other data shape captures it.

## Complexity

**Time.** One pass, each character pushed/popped at most once:

$$
T(n) = O(n)
$$

**Space.** At most one opener per character on the stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Minimum Remove To Make Valid Parentheses** (`src/main/kotlin/stack/MinimumRemoveToMakeValidParentheses.kt`) — the same matcher, but instead of failing you *mark* the offending closers (empty stack) and leftover openers, then rebuild without them.
- **Longest Valid Parentheses** (`src/main/kotlin/stack/LongestValidParanthesis.kt`) — a stack of *indices* with a sentinel; the length between unmatched positions is the answer.
- **Generate Parentheses / all balanced strings** — the counting version (never let `)` exceed `(`) — the stack matcher inverted into a generator.
- **Interview follow-up:** "Why not just count openers and closers?" Counts miss *ordering*: `([)]` has equal counts of each bracket but is invalid. The stack is what detects that the closer matched the *wrong* opener. (A single-type version, just `()`, *can* be done with a counter — that's exactly the boundary of when the stack is needed.)
