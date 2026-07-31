# 12.3 Generate Parentheses

> **Source:** [`src/main/kotlin/string/backtracking/GenerateParantheses.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/backtracking/GenerateParantheses.kt)
> **Pattern:** balance-constrained · **Core page**

## The Problem

Given `n` pairs of parentheses, generate **all** combinations of well-formed parentheses.

- Constraints: $1 \le n \le 8$.

## Examples

```
Input:  n = 3
Output: ["((()))","(()())","(())()","()(())","()()()"]   (all 5 valid strings)
```

## Intuition — two counters encode the entire constraint

A string of `(` and `)` is well-formed iff, scanning left to right, **`(` never exceeds** ... more precisely: at every prefix, `close <= open` (never more `)` than `(` so far), and at the end `open == close`. Backtracking with two counters captures this directly:

- state = `(open, close)` — how many of each remain *to place*;
- `complete` when both are 0;
- a `(` is always legal while `open > 0`;
- a `)` is legal only when `close > open` — i.e., more `)` remain than `(` — which guarantees no prefix ever closes a pair that was never opened.

The pruning is the whole problem: the `close > open` guard is the well-formedness invariant, checked before every branch instead of after. Every invalid string is never *built* — the tree is pruned at the first impossible `)`.

**Why state "remaining" instead of "placed"?** The repo counts down (`generate(open-1, close)`); the invariant becomes "`)` legal iff `close > open`" — a slightly cleaner comparison than the count-up version's "`placed.close < placed.open`". Either works; be consistent, because the off-by-one confusion between "remaining" and "placed" is the classic bug.

**The StringBuilder undo:** appending and `setLength(len-1)`/`deleteCharAt` is the in-place path mutation — same undo contract as the [Subsets](subsets.md) list, applied to a string builder.

## Approach 1 — Generate all $2^{2n}$ strings, filter valid (too slow)

All $4^n$ binary strings, keep the balanced ones: works at $n = 3$, explodes at $n = 8$ ($2^{16} = 65536$ vs the real answer $\binom{16}{8}/9 = 1430$).

## Approach 2 — Balance-pruned backtracking (the repo's version, optimal)

```kotlin
class GenerateParantheses {
    /**
     * @param n number of parentheses pairs
     * @return  all well-formed strings of n pairs
     */
    fun generateParenthesis(n: Int): List<String> {
        val result = mutableListOf<String>()
        generate(n, n, StringBuilder(), result)
        return result
    }

    fun generate(open: Int, close: Int, current: StringBuilder, result: MutableList<String>) {
        if (open == 0 && close == 0) {                     // all placed: complete
            result.add(current.toString())
            return
        }

        if (open > 0) {                                    // a '(' is always legal
            current.append("(")
            generate(open - 1, close, current, result)
            current.setLength(current.length - 1)          // undo
        }

        if (close > open) {                                // ')' only while a '(' is open
            current.append(")")
            generate(open, close - 1, current, result)
            current.deleteCharAt(current.length - 1)       // undo
        }
    }
}
```

```java
import java.util.*;

public class GenerateParentheses {
    /**
     * @param n number of parentheses pairs
     * @return  all well-formed strings of n pairs
     */
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(n, n, new StringBuilder(), result);
        return result;
    }

    private void backtrack(int open, int close, StringBuilder sb, List<String> result) {
        if (open == 0 && close == 0) {                     // all placed: complete
            result.add(sb.toString());
            return;
        }

        if (open > 0) {                                    // a '(' is always legal
            sb.append('(');
            backtrack(open - 1, close, sb, result);
            sb.setLength(sb.length() - 1);                 // undo
        }

        if (close > open) {                                // ')' only while a '(' is open
            sb.append(')');
            backtrack(open, close - 1, sb, result);
            sb.setLength(sb.length() - 1);                 // undo
        }
    }
}
```

```cpp
#include <string>
#include <vector>

class GenerateParentheses {
    void backtrack(int open, int close, std::string& cur, std::vector<std::string>& result) {
        if (open == 0 && close == 0) {                     // all placed: complete
            result.push_back(cur);
            return;
        }

        if (open > 0) {                                    // a '(' is always legal
            cur.push_back('(');
            backtrack(open - 1, close, cur, result);
            cur.pop_back();                                // undo
        }

        if (close > open) {                                // ')' only while a '(' is open
            cur.push_back(')');
            backtrack(open, close - 1, cur, result);
            cur.pop_back();                                // undo
        }
    }

public:
    /**
     * @param n number of parentheses pairs
     * @return  all well-formed strings of n pairs
     */
    std::vector<std::string> generateParenthesis(int n) {
        std::vector<std::string> result;
        std::string cur;
        backtrack(n, n, cur, result);
        return result;
    }
};
```

```python
def generate_parenthesis(n: int) -> list[str]:
    """
    @param n: number of parentheses pairs
    @return:  all well-formed strings of n pairs
    """
    result = []
    current = []

    def backtrack(open: int, close: int) -> None:
        if open == 0 and close == 0:             # all placed: complete
            result.append("".join(current))
            return

        if open > 0:                             # a '(' is always legal
            current.append("(")
            backtrack(open - 1, close)
            current.pop()                        # undo

        if close > open:                         # ')' only while a '(' is open
            current.append(")")
            backtrack(open, close - 1)
            current.pop()                        # undo

    backtrack(n, n)
    return result
```

```rust
impl Solution {
    /// @param n number of parentheses pairs
    /// @return  all well-formed strings of n pairs
    pub fn generate_parenthesis(n: i32) -> Vec<String> {
        let mut result = Vec::new();
        let mut current = String::new();

        fn backtrack(open: i32, close: i32, current: &mut String, result: &mut Vec<String>) {
            if open == 0 && close == 0 {             // all placed: complete
                result.push(current.clone());
                return;
            }

            if open > 0 {                            // a '(' is always legal
                current.push('(');
                backtrack(open - 1, close, current, result);
                current.pop();                       // undo
            }

            if close > open {                        // ')' only while a '(' is open
                current.push(')');
                backtrack(open, close - 1, current, result);
                current.pop();                       // undo
            }
        }

        backtrack(n, n, &mut current, &mut result);
        result
    }
}
```

## Dry run

**Input:** `n = 2`.

```
backtrack(open=2, close=2):
  '(' legal: append "(".  backtrack(1, 2):
    '(' legal: append -> "((".  backtrack(0, 2):
      '(' not legal (open=0).  ')' legal (2>0): append -> "(()".  backtrack(0, 1):
        '(' no.  ')' legal (1>0): append -> "(())".  backtrack(0,0): record "(())".
        undo -> "(()".
      undo -> "((".
    undo -> "(".
    ')' legal (2>1): append -> "()".  backtrack(1, 1):
      '(' legal: append -> "()(".  backtrack(0, 1):
        ')' legal: append -> "()()".  record.
      undo -> "()".
      ')' legal (1>1)? no.  return.
    undo -> "(".
  undo -> "".
  ')' legal (2>2)? no.  return.

result: ["(())", "()()"] ✓
```

The guard `close > open` is doing all the work: at every `)` the invariant "never more `)` than `(` so far" holds by construction. Invalid strings like `")("` never even start — the first character can only be `(`.

## Complexity

**Time.** Exactly the Catalan number of outputs, times $O(n)$ copy each:

$$
T(n) = O\left(n \cdot \binom{2n}{n}\right)
$$

**Space.** Recursion depth plus output:

$$
S(n) = O\left(n \cdot \binom{2n}{n}\right)
$$

## Variants & follow-ups

- **Valid Parentheses** ([8.1](../ch08-stacks/valid-parentheses.md)) — the *checking* direction; this page is its generation inverse.
- **Minimum Add To Make Parentheses Valid** (`src/main/kotlin/stack/MinimumAddtoMakeParenthesesValid.kt`) — the counting version: unmatched `)` and leftover `(` counted greedily, no recursion.
- **Unique Binary Search Trees II / Catalan-family** — the same Catalan numbers counted or generated; seeing the connection is a nice "aha" for interviews.
- **Interview follow-up:** "Why does `close > open` (remaining) equal `close < open` (placed)?" With the repo's count-down state, `close > open` means more `)` remain than `(` remain — i.e., fewer `)` placed than `(` placed — exactly the well-formed prefix condition. The comparison flips sign with the counting direction; confusing the two is the classic off-by-one.
