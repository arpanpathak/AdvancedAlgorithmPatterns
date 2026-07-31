# 8.16 Remove All Adjacent Duplicates In String

> **Source:** [`src/main/kotlin/string/stack/RemoveAllAdjacentDuplicatesInString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/stack/RemoveAllAdjacentDuplicatesInString.kt)
> **Pattern:** stack-as-builder · **Core page**

## The Problem

Repeatedly remove **adjacent equal** pairs until none remain.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  s = "abbaca"   -> Output: "ca"   ("bb" removed -> "aaca" -> "aa" removed -> "ca")
```

## Intuition — the stack top is the previous char; a match pops

Scan left-to-right; the stack holds the current "compressed" prefix. Each char: if it equals the top, the pair collapses (pop); else push:

```kotlin
s.forEach { ch ->
    when {
        stack.isNotEmpty() && stack.last() == ch -> stack.removeLast()
        else -> stack.add(ch)
    }
}
return stack.joinToString("")
```

**Why is a single pass enough?** A removal can expose a *new* adjacent pair — the stack's pop-then-compare handles it automatically: after popping, the next char compares against the *new* top. The [8.3](daily-temperatures.md) stack-as-state pattern in its simplest form.

## Approach 1 — Repeated string replacement (O(n²))

Loop `while (true) replace adjacent pairs`: correct, slow.

## Approach 2 — Stack builder (the repo's version, optimal)

```kotlin
class RemoveAllAdjacentDuplicatesInString {
    /**
     * @param s input string
     * @return  string after removing adjacent equal pairs
     */
    fun removeDuplicates(s: String): String {
        val stack = ArrayDeque<Char>()

        s.forEach { ch ->
            when {
                stack.isNotEmpty() && stack.last() == ch -> stack.removeLast()
                else -> stack.add(ch)
            }
        }
        return stack.joinToString("")
    }
}
```

```java
import java.util.*;

public class RemoveAllAdjacentDuplicatesInString {
    /**
     * @param s input string
     * @return  string after removing adjacent equal pairs
     */
    public String removeDuplicates(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && stack.peek() == c) stack.pop();
            else stack.push(c);
        }

        StringBuilder sb = new StringBuilder();
        for (char c : stack) sb.append(c);
        return sb.reverse().toString();
    }
}
```

```cpp
#include <string>

class RemoveAllAdjacentDuplicatesInString {
public:
    /**
     * @param s input string
     * @return  string after removing adjacent equal pairs
     */
    std::string removeDuplicates(std::string s) {
        std::string stack;

        for (char c : s) {
            if (!stack.empty() && stack.back() == c) stack.pop_back();
            else stack.push_back(c);
        }
        return stack;
    }
};
```

```python
def remove_duplicates(s: str) -> str:
    """
    @param s: input string
    @return:  string after removing adjacent equal pairs
    """
    stack = []

    for ch in s:
        if stack and stack[-1] == ch:
            stack.pop()
        else:
            stack.append(ch)

    return "".join(stack)
```

```rust
impl Solution {
    /// @param s input string
    /// @return  string after removing adjacent equal pairs
    pub fn remove_duplicates(s: String) -> String {
        let mut stack: Vec<char> = Vec::new();

        for ch in s.chars() {
            if let Some(&top) = stack.last() {
                if top == ch { stack.pop(); continue; }
            }
            stack.push(ch);
        }

        stack.into_iter().collect()
    }
}
```

## Dry run

**Input:** `s = "abbaca"`.

```
'a': stack [a]
'b': stack [a,b]
'b': top == b -> pop.  stack [a]
'a': top == a -> pop.  stack []        (the new pair exposed by the bb removal)
'c': stack [c]
'a': stack [c,a]

Output: "ca" ✓
```

The cascade: removing `bb` exposes `aa`, which the next `a`'s pop removes — the stack's top-after-pop is the *new* neighbor, so cascades need no special handling. The `StringBuilder`/string stack is the output itself — no separate builder.

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

- **Remove All Adjacent Duplicates II** — the k-repeat generalization (counts on the stack).
- **Valid Parentheses** ([8.1](valid-parentheses.md)) — the matching-stack sibling.
- **Interview follow-up:** "Why does one pass handle cascades?" The stack invariant is "the current compressed prefix" — after a pop, the stack already reflects the post-removal state, so the next character compares against the correct neighbor. The cascade is *free*: it's just the next pop.
