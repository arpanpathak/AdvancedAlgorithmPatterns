# 8.6 Evaluate Reverse Polish Notation

> **Source:** [`src/main/kotlin/stack/EvaluateReversePolishNotation.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/EvaluateReversePolishNotation.kt)
> **Pattern:** stack arithmetic · **Core page**

## The Problem

Given an array of tokens in **Reverse Polish Notation** (operator *after* its two operands: `"3 4 +"` means `3 + 4`), evaluate it. Operators are `+ - * /`; division truncates **toward zero**; the expression is always valid.

- Constraints: $1 \le n \le 10^4$; tokens are integers or operators; intermediate values fit in 32-bit.

## Examples

```
Input:  tokens = ["2","1","+","3","*"]
Output: 9    ((2 + 1) * 3)

Input:  tokens = ["4","13","5","/","+"]
Output: 6    (4 + (13 / 5) = 4 + 2)

Input:  tokens = ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]
Output: 22   (the classic long one)
```

## Intuition — no parentheses, no precedence: the stack *is* the grammar

RPN's point: every operator immediately follows its two operands, so there is never ambiguity — no parentheses, no precedence rules. Evaluation is mechanical: walk the tokens; push numbers; when an operator arrives, **pop the two most recent numbers, compute, push the result**. Because of RPN's structure, those two pops are exactly the operator's operands — in order (`op2` was pushed after `op1`).

**The operand order trap:** the stack pops `op2` *first*. Subtraction and division are not commutative, so `op1 - op2` and `op1 / op2` are correct — swap them and every `-` and `/` token returns the wrong answer. (The repo's code does the two pops in exactly this order.)

**Truncation toward zero:** `13 / 5 = 2` and `-13 / 5 = -2` (not `-3`). Kotlin/Java/Rust/C++ integer division truncates toward zero natively; Python's `//` floors instead, so Python must convert with `int(a / b)` — the one real language gotcha on this page.

**Why does this work with a plain stack?** RPN is a post-order expression tree flattened into a linear token list (the [post-order](pattern-primer.md) from the tree chapter!). A stack evaluates any post-order expression with no backtracking — operands are pushed, subtrees reduce when their root operator arrives.

## Approach 1 — Shunting-yard + tree evaluation (overkill)

Parse to an AST and evaluate recursively: correct but pointless — RPN was *designed* to be stack-evaluated, and the AST is exactly what the stack reconstructs implicitly.

## Approach 2 — Stack evaluation (the repo's version, optimal)

```kotlin
class EvaluateReversePolishNotation {
    private val SYMBOLS = setOf("+", "-", "*", "/")

    /**
     * @param tokens RPN tokens (integers and operators)
     * @return       the evaluated result
     */
    fun evalRPN(tokens: Array<String>): Int {
        val stack = mutableListOf<Int>()
        for (token in tokens) {
            if (token in SYMBOLS) {
                val op2 = stack.removeLast()          // second operand was pushed later
                val op1 = stack.removeLast()
                stack.add(operate(token[0], op1, op2))
            } else {
                stack.add(token.toInt())
            }
        }
        return stack.removeLast()
    }

    fun operate(symbol: Char, a: Int, b: Int): Int {
        return when (symbol) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b                              // Kotlin: truncates toward zero
            else -> 0
        }
    }
}
```

```java
import java.util.*;

public class EvaluateReversePolishNotation {
    private static final Set<String> SYMBOLS = Set.of("+", "-", "*", "/");

    /**
     * @param tokens RPN tokens (integers and operators)
     * @return       the evaluated result
     */
    public int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String t : tokens) {
            if (SYMBOLS.contains(t)) {
                int op2 = stack.pop();                 // second operand was pushed later
                int op1 = stack.pop();
                stack.push(switch (t) {
                    case "+" -> op1 + op2;
                    case "-" -> op1 - op2;
                    case "*" -> op1 * op2;
                    default -> op1 / op2;              // Java: truncates toward zero
                });
            } else {
                stack.push(Integer.parseInt(t));
            }
        }
        return stack.pop();
    }
}
```

```cpp
#include <cctype>
#include <stack>
#include <string>
#include <vector>

class EvaluateReversePolishNotation {
public:
    /**
     * @param tokens RPN tokens (integers and operators)
     * @return       the evaluated result
     */
    int evalRPN(std::vector<std::string>& tokens) {
        std::stack<int> st;
        for (auto& t : tokens) {
            if (t.size() == 1 && !std::isdigit(t[0])) {     // an operator
                int op2 = st.top(); st.pop();               // second operand pushed later
                int op1 = st.top(); st.pop();
                if (t == "+") st.push(op1 + op2);
                else if (t == "-") st.push(op1 - op2);
                else if (t == "*") st.push(op1 * op2);
                else st.push(op1 / op2);                    // C++: truncates toward zero
            } else {
                st.push(std::stoi(t));
            }
        }
        return st.top();
    }
};
```

```python
def eval_rpn(tokens: list[str]) -> int:
    """
    @param tokens: RPN tokens (integers and operators)
    @return:       the evaluated result
    """
    stack = []
    for t in tokens:
        if t in "+-*/":
            op2 = stack.pop()                # second operand was pushed later
            op1 = stack.pop()
            if t == "+":
                stack.append(op1 + op2)
            elif t == "-":
                stack.append(op1 - op2)
            elif t == "*":
                stack.append(op1 * op2)
            else:
                stack.append(int(op1 / op2)) # int() truncates toward zero (// would floor)
        else:
            stack.append(int(t))
    return stack[0]
```

```rust
impl Solution {
    /// @param tokens RPN tokens (integers and operators)
    /// @return       the evaluated result
    pub fn eval_rpn(tokens: Vec<String>) -> i32 {
        let mut stack: Vec<i32> = Vec::new();
        for t in &tokens {
            match t.as_str() {
                "+" => { let b = stack.pop().unwrap(); let a = stack.pop().unwrap(); stack.push(a + b); }
                "-" => { let b = stack.pop().unwrap(); let a = stack.pop().unwrap(); stack.push(a - b); }
                "*" => { let b = stack.pop().unwrap(); let a = stack.pop().unwrap(); stack.push(a * b); }
                "/" => { let b = stack.pop().unwrap(); let a = stack.pop().unwrap(); stack.push(a / b); }
                _   => stack.push(t.parse().unwrap()),
            }
        }
        stack[0]
    }
}
```

## Dry run

**Input:** `tokens = ["4","13","5","/","+"]`.

```
stack = []
"4"  -> push 4.                   stack=[4]
"13" -> push 13.                  stack=[4,13]
"5"  -> push 5.                   stack=[4,13,5]
"/"  -> op2=5, op1=13 -> 13/5 = 2 (toward zero, not 2.6). push 2.   stack=[4,2]
"+"  -> op2=2, op1=4 -> 4+2 = 6.  push 6.                           stack=[6]
Result: 6 ✓
```

Now the negative-truncation case: `tokens = ["-13","5","/"]`:

```
"-13" -> push -13.   stack=[-13]
"5"   -> push 5.     stack=[-13,5]
"/"   -> op2=5, op1=-13 -> -13/5 = -2  (toward zero; Python's // would give -3!)
Result: -2
```

The operand order is visible in the first trace: `13/5` requires `op1=13` and `op2=5` — but `5` was popped *first*. Swap the order and the same tokens would compute `5/13 = 0`. Every `-` and `/` depends on the two pops.

## Complexity

**Time.** Each token pushed or popped a constant number of times:

$$
T(n) = O(n)
$$

**Space.** The stack holds at most all remaining operands:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Basic Calculator (infix with parentheses)** — the harder cousin: RPN *with* precedence, which needs the shunting-yard algorithm to convert infix to postfix — after which *this* page evaluates it.
- **Design A Stack With Increment Operations** (`src/main/kotlin/stack/DesignAStackWithIncrementOperations.kt`) — stack design with range updates; a different flavor of the same LIFO engine.
- **Exclusive Time Of Functions** (`src/main/kotlin/stack/ExclusiveTimeOfFunctions.kt`) — a stack of *function ids* tracking nested execution — the "stack of active contexts" idea pushed to concurrency.
- **Interview follow-up:** "Why does RPN need no parentheses?" Because the operator's position fixes its operands' identities: the two most recent pushes are always its two operands. The stack *is* the grammar — which is exactly why calculators and stack machines (JVM, PostScript) use postfix internally.
