# 8.18 Minimum Remove To Make Valid Parentheses

> **Source:** [`src/main/kotlin/stack/MinimumRemoveToMakeValidParentheses.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/MinimumRemoveToMakeValidParentheses.kt)
> **Pattern:** mark-then-filter · **Core page**

## The Problem

Remove the **fewest** parentheses so the result is valid (may contain other chars).

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  s = "lee(t(c)o)de)"   -> Output: "lee(t(c)o)de"
Input:  s = "a)b(c)d"         -> Output: "ab(c)d"
Input:  s = "))(("           -> Output: ""
```

## Intuition — one pass finds bad indices; a second pass filters

Two passes:

1. **Mark** — a stack of `(` indices; a `)` with an empty stack is *bad*; at the end, leftover `(` indices are bad;
2. **Filter** — rebuild the string skipping the marked indices.

```kotlin
val stack = mutableListOf<Int>()
val toRemove = mutableSetOf<Int>()

for (i in s.indices) {
    when {
        s[i] == '(' -> stack.add(i)
        s[i] == ')' && stack.isNotEmpty() -> stack.removeLast()
        s[i] == ')' && stack.isEmpty() -> toRemove.add(i)
    }
}
toRemove.addAll(stack)     // unmatched opens

return s.filterIndexed { i, _ -> i !in toRemove }
```

**Why indices (not chars)?** Duplicate parens are indistinguishable — the *positions* are what need removal. The stack stores indices; the set marks them; the rebuild skips — the [8.1](valid-parentheses.md) stack with positional output.

**Why is this minimal?** Every marked paren is *provably* un-matchable; removing exactly them yields a valid string, and any valid string must remove at least those.

## Approach 1 — Two-pass with a stack (the repo's version, optimal)

```kotlin
class MinimumRemoveToMakeValidParentheses {
    /**
     * @param s input string
     * @return  minimal-removal valid string
     */
    fun minRemoveToMakeValid(s: String): String {
        val stack = mutableListOf<Int>()
        val toRemove = mutableSetOf<Int>()

        for (i in s.indices) {
            when {
                s[i] == '(' -> stack.add(i)
                s[i] == ')' && stack.isNotEmpty() -> stack.removeLast()
                s[i] == ')' && stack.isEmpty() -> toRemove.add(i)
                else -> continue
            }
        }
        toRemove.addAll(stack)     // unmatched opens

        return s.filterIndexed { i, _ -> i !in toRemove }
    }
}
```

```java
import java.util.*;

public class MinimumRemoveToMakeValidParentheses {
    /**
     * @param s input string
     * @return  minimal-removal valid string
     */
    public String minRemoveToMakeValid(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        Set<Integer> remove = new HashSet<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') stack.push(i);
            else if (c == ')') {
                if (stack.isEmpty()) remove.add(i);
                else stack.pop();
            }
        }
        remove.addAll(stack);     // unmatched opens

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (!remove.contains(i)) sb.append(s.charAt(i));
        }
        return sb.toString();
    }
}
```

```cpp
#include <string>
#include <stack>
#include <unordered_set>

class MinimumRemoveToMakeValidParentheses {
public:
    /**
     * @param s input string
     * @return  minimal-removal valid string
     */
    std::string minRemoveToMakeValid(std::string s) {
        std::stack<int> stack;
        std::unordered_set<int> remove;

        for (int i = 0; i < (int)s.size(); i++) {
            if (s[i] == '(') stack.push(i);
            else if (s[i] == ')') {
                if (stack.empty()) remove.insert(i);
                else stack.pop();
            }
        }
        while (!stack.empty()) { remove.insert(stack.top()); stack.pop(); }

        std::string result;
        for (int i = 0; i < (int)s.size(); i++) {
            if (!remove.count(i)) result += s[i];
        }
        return result;
    }
};
```

```python
def min_remove_to_make_valid(s: str) -> str:
    """
    @param s: input string
    @return:  minimal-removal valid string
    """
    stack = []
    to_remove = set()

    for i, ch in enumerate(s):
        if ch == "(":
            stack.append(i)
        elif ch == ")":
            if stack:
                stack.pop()
            else:
                to_remove.add(i)

    to_remove.update(stack)          # unmatched opens

    return "".join(ch for i, ch in enumerate(s) if i not in to_remove)
```

```rust
impl Solution {
    /// @param s input string
    /// @return  minimal-removal valid string
    pub fn min_remove_to_make_valid(s: String) -> String {
        let bytes: Vec<char> = s.chars().collect();
        let mut stack: Vec<usize> = Vec::new();
        let mut remove: std::collections::HashSet<usize> = std::collections::HashSet::new();

        for (i, &ch) in bytes.iter().enumerate() {
            match ch {
                '(' => stack.push(i),
                ')' => {
                    if let Some(_) = stack.pop() { }
                    else { remove.insert(i); }
                }
                _ => {}
            }
        }
        remove.extend(stack);        // unmatched opens

        bytes.iter().enumerate()
            .filter(|(i, _)| !remove.contains(i))
            .map(|(_, &c)| c)
            .collect()
    }
}
```

## Dry run

**Input:** `s = "lee(t(c)o)de)"`.

```
i=10 ')': stack empty -> toRemove {10}.
leftover opens: none.

filter: skip index 10 -> "lee(t(c)o)de" ✓

Input: "))((": ')':0 remove.  ')':1 remove.  '(':2 stack.  '(':3 stack.
toRemove {0,1} + {2,3} -> "" ✓
```

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** Stack + set:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Minimum Add To Make Valid** ([8.17](minimum-add-to-make-parentheses-valid.md)) — the add-counter twin.
- **Valid Parentheses** ([8.1](valid-parentheses.md)) — the matching base.
- **Interview follow-up:** "Why positions and not a char-stack?" Removing needs *where*; the index stack + set marks exactly the bad parens, and the filter pass rebuilds deterministically. A char-stack would lose the positions the removal requires.
