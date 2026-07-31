# 8.10 Basic Calculator II

> **Source:** [`src/main/kotlin/math/stack/BasicCalculator_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/stack/BasicCalculator_II.kt) (+ `BasicCalculator_II_ShortCode.kt` — the compressed form below)
> **Pattern:** single-pass with a pending term · **Core page**

## The Problem

Evaluate a string expression with `+`, `-`, `*`, `/` (integer division) — **no parentheses**, standard operator precedence.

- Constraints: $1 \le n \le 3 \times 10^5$; expression is valid; fits in `Int`.

## Examples

```
Input:  s = "3+2*2"    -> Output: 7   (2*2 first!)
Input:  s = " 3/2 "    -> Output: 1   (integer division)
Input:  s = " 3+5 / 2" -> Output: 5
```

## Intuition — defer `+`/`-` terms; fold `*`/`/` into the *pending* term

Without parentheses, the only precedence rule is `*`/`/` bind tighter than `+`/`-`. So the single-pass state is:

- `lastNumber` — the term being built: `+`/`-` *reset* it, `*`/`/` *fold* into it;
- `result` — the sum of all *completed* terms;
- `operator` — the pending operator, applied when the next number ends.

```
scan digits into currentNumber
on a non-digit (or the end):
    apply `operator`:
        '+' -> result += lastNumber; lastNumber = currentNumber
        '-' -> result += lastNumber; lastNumber = -currentNumber
        '*' -> lastNumber *= currentNumber
        '/' -> lastNumber /= currentNumber
    operator = char; currentNumber = 0
return result + lastNumber
```

**Why does this avoid a stack?** A stack version pushes every term then sums them at the end. The pending-term version *banks* completed terms into `result` as it goes — the `*`/`/` fold happens inside `lastNumber` before banking. Same O(n), half the machinery.

## Approach 1 — Stack of signed terms

Push each number with its sign (flip the sign for `-`, multiply/divide the top for `*`/`/`), then sum the stack: correct, and the interview-standard answer.

## Approach 2 — Single-pass pending term (the repo's short code, optimal)

```kotlin
class BasicCalculator_II_ShortCode {
    /**
     * @param s expression with + - * / (no parentheses)
     * @return  the evaluated value
     */
    fun calculate(s: String): Int {
        var (currentNumber, result, lastNumber) = listOf(0, 0, 0)
        var operator = '+'

        s.forEachIndexed { i, char ->
            when {
                char.isDigit() -> currentNumber = currentNumber * 10 + (char - '0')

                // A non-digit (and not a space), or the last character: flush the term
                !char.isDigit() && char != ' ' || i == s.lastIndex -> {
                    when (operator) {
                        '+' -> { result += lastNumber; lastNumber = currentNumber }
                        '-' -> { result += lastNumber; lastNumber = -currentNumber }
                        '*' -> lastNumber *= currentNumber
                        '/' -> lastNumber /= currentNumber
                    }
                    operator = char
                    currentNumber = 0
                }
            }
        }
        return result + lastNumber
    }
}
```

```java
public class BasicCalculatorII {
    /**
     * @param s expression with + - * / (no parentheses)
     * @return  the evaluated value
     */
    public int calculate(String s) {
        int current = 0, result = 0, last = 0;
        char op = '+';

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                current = current * 10 + (c - '0');
            }
            if ((!Character.isDigit(c) && c != ' ') || i == s.length() - 1) {
                switch (op) {
                    case '+': result += last; last = current; break;
                    case '-': result += last; last = -current; break;
                    case '*': last *= current; break;
                    case '/': last /= current; break;
                }
                op = c;
                current = 0;
            }
        }
        return result + last;
    }
}
```

```cpp
#include <string>

class BasicCalculatorII {
public:
    /**
     * @param s expression with + - * / (no parentheses)
     * @return  the evaluated value
     */
    int calculate(std::string s) {
        int current = 0, result = 0, last = 0;
        char op = '+';

        for (int i = 0; i < (int)s.size(); i++) {
            char c = s[i];
            if (std::isdigit(c)) current = current * 10 + (c - '0');
            if ((!std::isdigit(c) && c != ' ') || i == (int)s.size() - 1) {
                switch (op) {
                    case '+': result += last; last = current; break;
                    case '-': result += last; last = -current; break;
                    case '*': last *= current; break;
                    case '/': last /= current; break;
                }
                op = c;
                current = 0;
            }
        }
        return result + last;
    }
};
```

```python
def calculate(s: str) -> int:
    """
    @param s: expression with + - * / (no parentheses)
    @return:  the evaluated value
    """
    current = result = last = 0
    op = "+"

    for i, c in enumerate(s):
        if c.isdigit():
            current = current * 10 + int(c)

        if (not c.isdigit() and c != " ") or i == len(s) - 1:
            if op == "+":
                result += last
                last = current
            elif op == "-":
                result += last
                last = -current
            elif op == "*":
                last *= current
            else:
                last = int(last / current)      # int() truncates toward zero
            op = c
            current = 0

    return result + last
```

```rust
impl Solution {
    /// @param s expression with + - * / (no parentheses)
    /// @return  the evaluated value
    pub fn calculate(s: String) -> i32 {
        let chars: Vec<char> = s.chars().collect();
        let (mut current, mut result, mut last) = (0i64, 0i64, 0i64);
        let mut op = '+';

        for (i, &c) in chars.iter().enumerate() {
            if c.is_ascii_digit() { current = current * 10 + (c as i64 - '0' as i64); }
            if (!c.is_ascii_digit() && c != ' ') || i == chars.len() - 1 {
                match op {
                    '+' => { result += last; last = current; }
                    '-' => { result += last; last = -current; }
                    '*' => last *= current,
                    '/' => last /= current,    // Rust integer division truncates toward zero
                    _ => {}
                }
                op = c;
                current = 0;
            }
        }
        (result + last) as i32
    }
}
```

## Dry run

**Input:** `s = "3+2*2"`.

```
current=0, result=0, last=0, op='+'
i=0 '3': current = 3.
i=1 '+': flush -> '+' : result += 0 (0); last = 3.  op='+', current=0.
i=2 '2': current = 2.
i=3 '*': flush -> '+': result += 3 (3); last = 2.  op='*', current=0.
i=4 '2': current = 2.
i=5 (last char '2'): flush -> '*': last *= 2 -> last = 4.  op='2' (irrelevant), current=0.

Output: result + last = 3 + 4 = 7 ✓
```

The precedence falls out of the state machine: `2*2` folds into `lastNumber` (4) *before* the `+` banks it — so the final `result + last` is `3 + 4`. A naive left-to-right would give `(3+2)*2 = 10`; the pending-term structure is exactly what enforces `*`/`/` first. `"3+5/2"` → the `/` folds `5/2 = 2` into `last`, result `3 + 2 = 5` ✓.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Five scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Evaluate Reverse Polish Notation** ([8.6](evaluate-reverse-polish-notation.md)) — the postfix sibling; the same term-accumulation idea without precedence.
- **Basic Calculator I / III** (`math/stack/BasicCalculator.kt`, `BasicCalculator_III.kt`) — parentheses add a recursion/stack layer over this engine.
- **Interview follow-up:** "Why `result + lastNumber` at the end and not just `result`?" `+`/`-` bank the *previous* term into `result` and set `lastNumber` to the current one — the very last term is still sitting in `lastNumber` when the loop ends. The final addition flushes it. The `|| i == lastIndex` condition is what makes the flush automatic; forgetting either is the classic off-by-one.
