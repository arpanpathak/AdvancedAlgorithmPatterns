# 8.11 Basic Calculator

> **Source:** [`src/main/kotlin/math/stack/BasicCalculator.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/stack/BasicCalculator.kt) (+ `BasicCalculator_I.kt`)
> **Pattern:** infix→postfix or the sign-stack · **Core page**

## The Problem

Evaluate a string with `+`, `-`, **parentheses**, and spaces (no `*`/`/`). Integers only.

- Constraints: $1 \le n \le 3 \times 10^5$.

## Examples

```
Input:  s = "1 + 1"       -> Output: 2
Input:  s = " 2-1 + 2 "   -> Output: 3
Input:  s = "(1+(4+5+2)-3)+(6+8)"  -> Output: 23
```

## Intuition — parentheses are a stack of signs

Without `*`/`/`, the only complexity is `-` and parentheses. Two framing ideas:

**The sign-stack view (the classic):** `+` flips nothing, `-` flips the sign, `(` *pushes* the current sign context, `)` pops it. A number's effective sign is `sign × currentOuterSign`:

```
stack of signs, default [1], currentSign = 1
'(' -> push currentSign
')' -> pop
'+' -> currentSign = stack.top
'-' -> currentSign = -stack.top
digit -> result += currentSign * number
```

**The repo's infix→postfix view:** convert to postfix ([8.6](evaluate-reverse-polish-notation.md) evaluates it), handling unary minus — more machinery, but the same `+`/`-` precedence (1) vs parentheses.

## Approach 1 — Sign stack (optimal, O(1) space)

```kotlin
class BasicCalculator {
    /**
     * @param s expression with + - ( ) and spaces
     * @return  the evaluated value
     */
    fun calculate(s: String): Int {
        var result = 0
        var number = 0
        var sign = 1
        val signStack = java.util.ArrayDeque<Int>().apply { push(1) }

        for (c in s) {
            when {
                c.isDigit() -> number = number * 10 + (c - '0')

                c == '+' -> {
                    result += sign * number
                    number = 0
                    sign = signStack.peek()
                }
                c == '-' -> {
                    result += sign * number
                    number = 0
                    sign = -signStack.peek()
                }
                c == '(' -> signStack.push(sign)
                c == ')' -> signStack.pop()
            }
        }
        return result + sign * number
    }
}
```

```java
import java.util.*;

public class BasicCalculator {
    /**
     * @param s expression with + - ( ) and spaces
     * @return  the evaluated value
     */
    public int calculate(String s) {
        int result = 0, number = 0, sign = 1;
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);

        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                number = number * 10 + (c - '0');
            } else if (c == '+') {
                result += sign * number;
                number = 0;
                sign = stack.peek();
            } else if (c == '-') {
                result += sign * number;
                number = 0;
                sign = -stack.peek();
            } else if (c == '(') {
                stack.push(sign);
            } else if (c == ')') {
                stack.pop();
            }
        }
        return result + sign * number;
    }
}
```

```cpp
#include <string>
#include <stack>

class BasicCalculator {
public:
    /**
     * @param s expression with + - ( ) and spaces
     * @return  the evaluated value
     */
    int calculate(std::string s) {
        int result = 0, number = 0, sign = 1;
        std::stack<int> signs;
        signs.push(1);

        for (char c : s) {
            if (std::isdigit(c)) {
                number = number * 10 + (c - '0');
            } else if (c == '+') {
                result += sign * number;
                number = 0;
                sign = signs.top();
            } else if (c == '-') {
                result += sign * number;
                number = 0;
                sign = -signs.top();
            } else if (c == '(') {
                signs.push(sign);
            } else if (c == ')') {
                signs.pop();
            }
        }
        return result + sign * number;
    }
};
```

```python
def calculate(s: str) -> int:
    """
    @param s: expression with + - ( ) and spaces
    @return:  the evaluated value
    """
    result = 0
    number = 0
    sign = 1
    signs = [1]

    for c in s:
        if c.isdigit():
            number = number * 10 + int(c)
        elif c == "+":
            result += sign * number
            number = 0
            sign = signs[-1]
        elif c == "-":
            result += sign * number
            number = 0
            sign = -signs[-1]
        elif c == "(":
            signs.append(sign)
        elif c == ")":
            signs.pop()

    return result + sign * number
```

```rust
impl Solution {
    /// @param s expression with + - ( ) and spaces
    /// @return  the evaluated value
    pub fn calculate(s: String) -> i32 {
        let mut result: i64 = 0;
        let mut number: i64 = 0;
        let mut sign: i64 = 1;
        let mut signs: Vec<i64> = vec![1];

        for c in s.chars() {
            if c.is_ascii_digit() {
                number = number * 10 + (c as i64 - '0' as i64);
            } else if c == '+' {
                result += sign * number;
                number = 0;
                sign = *signs.last().unwrap();
            } else if c == '-' {
                result += sign * number;
                number = 0;
                sign = -*signs.last().unwrap();
            } else if c == '(' {
                signs.push(sign);
            } else if c == ')' {
                signs.pop();
            }
        }
        (result + sign * number) as i32
    }
}
```

## Dry run

**Input:** `s = "(1+(4+5+2)-3)+(6+8)"`.

```
signs=[1], sign=1, result=0, number=0
'(':  push sign -> signs=[1,1]
'1': number=1.  '+': result += 1*1 = 1.  sign=1.
'(':  push -> signs=[1,1,1]
'4': number=4.  '+': result += 4 = 5.  ...
'5': result += 5 = 10.  '2': result += 2 = 12.  number=2
'-': result += 1*2 = 14.  sign = -1.  number=0
'3': number=3.  ')': pop -> signs=[1,1]
')': pop -> signs=[1]
'+': result += (-1)*3 = 11.  sign=1.  number=0
'(':  push -> [1,1]
'6': ... '8': result += 6+8 = 25.
')': pop.  number=8

Final: result + sign*number = 25 + 1*8 = 33? — WRONG, expected 23.
```

**Correction — the number must flush when a `)` pops:**

```
...'-': result += 1*2 = 12 + 2 = 14...  let me re-trace with the flush on ')':
At the inner "(4+5+2)": the '+' flushes each completed number: 4, then 5 -> result 1+4+5 = 10.
'2': number=2.  '-': result += 1*2 = 12.  sign=-1.  number=0.
'3': number=3.  ')': pop.  The ')' does NOT flush 3 — but the NEXT '+'
     flushes: result += (-1)*3 = 12-3 = 9.  sign=1.  number=0.
     Then '6','8': result += 6 + 8 = 23.  Final: 23 + 1*0 = 23 ✓
```

The trick: every `+`/`-` flushes the pending number *and* sets the next sign; `(` saves the current sign; `)` restores it. The final `result + sign*number` catches the last pending term. Parentheses only ever change the *sign* — the arithmetic is a straight left-to-right sum of signed numbers.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The sign stack (parenthesis depth):

$$
S(n) = O(d)
$$

## Variants & follow-ups

- **Basic Calculator II** ([8.10](basic-calculator-ii.md)) — no parentheses, adds `*`/`/` (the pending-term engine).
- **Basic Calculator III** ([8.12](basic-calculator-iii.md)) — both: parentheses AND `*`/`/` via recursion.
- **Interview follow-up:** "Why does the sign-stack store just signs and not values?" Only the *sign context* matters inside a parenthesis: `-(...)` flips everything inside, `+(...)` doesn't. Pushing the current `sign` on `(` and restoring on `)` is the complete state — no values on the stack at all.
