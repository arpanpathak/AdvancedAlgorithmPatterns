# 8.12 Basic Calculator III

> **Source:** [`src/main/kotlin/math/stack/BasicCalculator_III.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/stack/BasicCalculator_III.kt)
> **Pattern:** recursive descent with a shared index · **Core page**

## The Problem

Evaluate a string with `+`, `-`, `*`, `/`, **and parentheses** — the full calculator.

- Constraints: $1 \le n \le 10^5$; integers, spaces allowed.

## Examples

```
Input:  s = "2*(5+5*2)/3+(6/2+8)"   -> Output: 21
Input:  s = " 3+5 / 2 "             -> Output: 5
```

## Intuition — `(` is a function call; `)` returns

The [8.10](basic-calculator-ii.md) pending-term machine handles `*`/`/`; parentheses need *nesting*. Recursive descent gives it for free: when the scan hits `(`, recurse (the sub-expression returns its value); when it hits `)`, return the accumulated value:

```kotlin
var index = 0

fun evaluate(): Int {
    val stack = ArrayDeque<Int>()
    var num = 0
    var op = '+'

    while (index < s.length) {
        val c = s[index++]

        when {
            c.isDigit() -> num = num * 10 + (c - '0')
            c == '(' -> num = evaluate()          // the sub-expression's value
            c == ')' -> break                     // return to the caller
            c != ' ' -> {
                when (op) {
                    '+' -> stack.addLast(num)
                    '-' -> stack.addLast(-num)
                    '*' -> stack.addLast(stack.removeLast() * num)
                    '/' -> stack.addLast(stack.removeLast() / num)
                }
                op = c
                num = 0
            }
        }
    }

    // flush the pending term, sum the stack (like 8.10's result + lastNumber)
    return stack.sum()  // (in the repo: the accumulated stack value)
}
```

**Why a shared `index`?** The recursion must resume *where the sub-expression ended* — a single member `index` carries the scan position across calls (the [5.6](../ch05-trees/serialize-and-deserialize-binary-tree.md) deserializer's index-pointer pattern).

**Why `(` → `num = evaluate()`?** A parenthesized expression *is* a number (a term). Assigning the recursion's result to `num` lets the surrounding operator apply to it like any digit-built number — `2*(...)` folds via the `*` branch.

## Approach 1 — Two-stack shunting yard

Operator/operand stacks with precedence: correct, twice the bookkeeping.

## Approach 2 — Recursive descent (the repo's version, optimal)

```kotlin
class BasicCalculator_III {
    private var index = 0

    /**
     * @param s expression with + - * / ( ) and spaces
     * @return  the evaluated value
     */
    fun calculate(s: String): Int {
        index = 0
        return evaluate(s)
    }

    private fun evaluate(s: String): Int {
        val stack = ArrayDeque<Int>()
        var num = 0
        var op = '+'

        while (index < s.length) {
            val c = s[index++]

            when {
                c.isDigit() -> num = num * 10 + (c - '0')
                c == '(' -> num = evaluate(s)     // sub-expression -> a term
                c == ')' -> break                 // done with this level
                c != ' ' -> {
                    when (op) {
                        '+' -> stack.addLast(num)
                        '-' -> stack.addLast(-num)
                        '*' -> stack.addLast(stack.removeLast() * num)
                        '/' -> stack.addLast(stack.removeLast() / num)
                    }
                    op = c
                    num = 0
                }
            }
        }

        // The pending term follows the last operator (8.10's result + lastNumber)
        return when (op) {
            '+' -> stack.sum() + num
            '-' -> stack.sum() - num
            '*' -> stack.sum() * num
            else -> stack.sum() / num
        }
    }
}
```

```java
public class BasicCalculatorIII {
    private int index = 0;

    /**
     * @param s expression with + - * / ( ) and spaces
     * @return  the evaluated value
     */
    public int calculate(String s) {
        index = 0;
        return evaluate(s);
    }

    private int evaluate(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        int num = 0;
        char op = '+';

        while (index < s.length()) {
            char c = s.charAt(index++);

            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == '(') {
                num = evaluate(s);                 // sub-expression -> a term
            } else if (c == ')') {
                break;                             // done with this level
            } else if (c != ' ') {
                switch (op) {
                    case '+': stack.push(num); break;
                    case '-': stack.push(-num); break;
                    case '*': stack.push(stack.pop() * num); break;
                    case '/': stack.push(stack.pop() / num); break;
                }
                op = c;
                num = 0;
            }
        }

        int result = 0;
        switch (op) {
            case '+': while (!stack.isEmpty()) result += stack.pop(); result += num; break;
            case '-': while (!stack.isEmpty()) result += stack.pop(); result -= num; break;
            case '*': result = 1; while (!stack.isEmpty()) result *= stack.pop(); result *= num; break;
            default:  while (!stack.isEmpty()) result += stack.pop(); result /= num;
        }
        return result;
    }
}
```

```cpp
#include <string>
#include <stack>

class BasicCalculatorIII {
    int index = 0;

    int evaluate(const std::string& s) {
        std::stack<int> stack;
        int num = 0;
        char op = '+';

        while (index < (int)s.size()) {
            char c = s[index++];

            if (std::isdigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == '(') {
                num = evaluate(s);                 // sub-expression -> a term
            } else if (c == ')') {
                break;                             // done with this level
            } else if (c != ' ') {
                switch (op) {
                    case '+': stack.push(num); break;
                    case '-': stack.push(-num); break;
                    case '*': { int t = stack.top(); stack.pop(); stack.push(t * num); break; }
                    case '/': { int t = stack.top(); stack.pop(); stack.push(t / num); break; }
                }
                op = c;
                num = 0;
            }
        }

        int result = 0;
        if (op == '*') result = 1;
        while (!stack.empty()) { result += stack.top(); stack.pop(); }

        switch (op) {
            case '+': return result + num;
            case '-': return result - num;
            case '*': return result * num;
            default:  return result / num;
        }
    }

public:
    /**
     * @param s expression with + - * / ( ) and spaces
     * @return  the evaluated value
     */
    int calculate(std::string s) {
        index = 0;
        return evaluate(s);
    }
};
```

```python
def calculate(s: str) -> int:
    """
    @param s: expression with + - * / ( ) and spaces
    @return:  the evaluated value
    """
    index = 0

    def evaluate() -> int:
        nonlocal index
        stack = []
        num = 0
        op = "+"

        while index < len(s):
            c = s[index]
            index += 1

            if c.isdigit():
                num = num * 10 + int(c)
            elif c == "(":
                num = evaluate()            # sub-expression -> a term
            elif c == ")":
                break
            elif c != " ":
                if op == "+": stack.append(num)
                elif op == "-": stack.append(-num)
                elif op == "*": stack.append(stack.pop() * num)
                else: stack.append(int(stack.pop() / num))
                op = c
                num = 0

        # flush the pending term
        if op == "+": stack.append(num)
        elif op == "-": stack.append(-num)
        elif op == "*": stack.append(stack.pop() * num)
        else: stack.append(int(stack.pop() / num))

        return sum(stack)

    return evaluate()
```

```rust
impl Solution {
    /// @param s expression with + - * / ( ) and spaces
    /// @return  the evaluated value
    pub fn calculate(s: String) -> i32 {
        let chars: Vec<char> = s.chars().collect();
        let mut index = 0usize;

        fn evaluate(chars: &Vec<char>, index: &mut usize) -> i64 {
            let mut stack: Vec<i64> = Vec::new();
            let mut num: i64 = 0;
            let mut op = '+';

            while *index < chars.len() {
                let c = chars[*index];
                *index += 1;

                if c.is_ascii_digit() {
                    num = num * 10 + (c as i64 - '0' as i64);
                } else if c == '(' {
                    num = evaluate(chars, index);      // sub-expression -> a term
                } else if c == ')' {
                    break;
                } else if c != ' ' {
                    match op {
                        '+' => stack.push(num),
                        '-' => stack.push(-num),
                        '*' => { let t = stack.pop().unwrap(); stack.push(t * num); }
                        '/' => { let t = stack.pop().unwrap(); stack.push(t / num); }
                        _ => {}
                    }
                    op = c;
                    num = 0;
                }
            }
            match op {
                '+' => stack.push(num),
                '-' => stack.push(-num),
                '*' => { let t = stack.pop().unwrap(); stack.push(t * num); }
                '/' => { let t = stack.pop().unwrap(); stack.push(t / num); }
                _ => {}
            }
            stack.iter().sum()
        }

        evaluate(&chars, &mut index) as i32
    }
}
```

## Dry run

**Input:** `s = "2*(5+5*2)/3+(6/2+8)"`.

```
evaluate() top level: num=0, op='+'
'2': num=2.  '*': push 2.  op='*'.  num=0
'(': num = evaluate()            <-- sub-expression "5+5*2"
      inside: '5': num=5.  '+': push 5.  op='+'.  num=0
              '5': num=5.  '*': push 5.  op='*'.  num=0
              '2': num=2.  ')': break.  flush '*': push 5*2=10.
              stack = [5,10].  return 15.
   num=15.  '/': stack.push(2 * 15) = 30.  op='/'.  num=0
'3': num=3.  '+': stack.push(30 / 3) = 10.  op='+'.  num=0
'(': evaluate()                  <-- sub-expression "6/2+8"
      '6': num=6.  '/': push 6.  op='/'.  num=0
      '2': num=2.  '+': push 6/2 = 3.  op='+'.  num=0
      '8': num=8.  end/')': flush '+': push 8.  stack=[3,8].  return 11.
   num=11.  end: flush '+': push 11.
   stack = [10, 11].  sum = 21 ✓
```

The recursion boundary is the `'('`/`')'` pair: the inner `evaluate` returns a *number* (`15`, `11`) that the outer level treats like any digit-built operand. The shared `index` resumes the outer scan exactly after the `)`. `2*(15)/3 + 11 = 10 + 11 = 21` ✓.

## Complexity

**Time.** Each char consumed once:

$$
T(n) = O(n)
$$

**Space.** Recursion + operand stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Basic Calculator** ([8.11](basic-calculator.md)) — no `*`/`/`: the sign-stack special case.
- **Basic Calculator II** ([8.10](basic-calculator-ii.md)) — no parentheses: the pending-term special case.
- **Evaluate Reverse Polish Notation** ([8.6](evaluate-reverse-polish-notation.md)) — the postfix consumer; BasicCalculator.kt's infix→postfix pipeline feeds it.
- **Interview follow-up:** "Why is the shared `index` essential?" Each `evaluate()` must resume the *outer* scan at the exact position after its `)` — a local index would reset and re-scan the sub-expression forever. The member/closure index is the [5.6](../ch05-trees/serialize-and-deserialize-binary-tree.md) deserializer trick: one cursor, shared by all recursion frames.
