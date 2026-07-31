# 8.2 Min Stack

> **Source:** [`src/main/kotlin/stack/MinStack.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/MinStack.kt)
> **Pattern:** dual-stack design · **Core page**

## The Problem

Design a stack that supports `push`, `pop`, `top`, and **`getMin`** — all in **$O(1)$** time.

- Constraints: up to $3 \times 10^4$ operations; $-2^{31} \le val \le 2^{31} - 1$.

## Examples

```
push(-2), push(0), push(-3)
getMin() -> -3
pop()               (pops -3)
top()    -> 0
getMin() -> -2
```

## Intuition — the minimum is a *history*, so it deserves its own stack

`getMin()` in $O(1)$ is the crux. Scanning the main stack for the minimum is $O(n)$; keeping a single cached `min` value breaks on `pop` (what was the minimum *before* the popped value was pushed?). The fix: **the running minimum is itself a stack**. Every time you push a value, push the *current global minimum onto a second stack*; every time you pop, pop the second stack too. The two stacks move in lockstep — `minStack.top` is always "the minimum of everything currently on the main stack".

Two equivalent bookkeeping styles:

1. **Push min every time** — `minStack` always has the same height as `stack`; `getMin` just reads its top. Wasteful when minima don't change (pushing 5 then 5 then 5 repeats 5 three times).
2. **Push only when it changes** (the repo's version) — push `val` onto `minStack` only if `val <= minStack.top`; on `pop`, pop `minStack` only if the popped value *was* that minimum. `minStack` can be shorter than `stack`, but its top is still exactly the current minimum. This is the interview-grade version: less memory, same $O(1)$.

The subtle correctness point in style 2: **why `<=` and not `<`?** Duplicates. If two equal values equal the current minimum are pushed, both must land on `minStack` — otherwise the first `pop` removes the only copy and the true minimum is lost. `<=` keeps one copy per duplicate.

## Approach 1 — Scan for the min on demand (too slow)

`getMin` walks the whole stack: $O(n)$ per call, up to $O(n^2)$ total. The problem's $O(1)$ requirement exists precisely to forbid this.

## Approach 2 — Parallel min stack (the repo's version, optimal)

```kotlin
class MinStack() {
    private val stack = ArrayDeque<Int>()
    private val minStack = ArrayDeque<Int>()       // history of running minimums

    /**
     * @param `val` value to push
     */
    fun push(`val`: Int) {
        stack.add(`val`)
        if (minStack.isEmpty() || `val` <= minStack.last()) {   // new minimum (or duplicate)
            minStack.add(`val`)
        }
    }

    fun pop() {
        if (stack.isNotEmpty()) {
            val poppedValue = stack.removeLast()
            if (poppedValue == minStack.last()) {   // did the minimum itself leave?
                minStack.removeLast()
            }
        }
    }

    fun top(): Int {
        return stack.last()
    }

    fun getMin(): Int {
        return minStack.last()
    }
}
```

```java
import java.util.*;

public class MinStack {
    private Deque<Integer> stack = new ArrayDeque<>();
    private Deque<Integer> minStack = new ArrayDeque<>();

    /** @param val value to push */
    public void push(int val) {
        stack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);                    // new minimum (or duplicate)
        }
    }

    public void pop() {
        if (!stack.isEmpty() && stack.peek().equals(minStack.peek())) {
            minStack.pop();                        // the minimum itself left
        }
        stack.pop();
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}
```

```cpp
#include <stack>

class MinStack {
    std::stack<int> st;
    std::stack<int> minSt;                         // history of running minimums

public:
    /** @param val value to push */
    void push(int val) {
        st.push(val);
        if (minSt.empty() || val <= minSt.top()) {
            minSt.push(val);                       // new minimum (or duplicate)
        }
    }

    void pop() {
        if (!st.empty() && st.top() == minSt.top()) {
            minSt.pop();                           // the minimum itself left
        }
        st.pop();
    }

    int top() { return st.top(); }
    int getMin() { return minSt.top(); }
};
```

```python
class MinStack:
    """@param val: value to push"""

    def __init__(self):
        self.stack = []
        self.min_stack = []                      # history of running minimums

    def push(self, val: int) -> None:
        self.stack.append(val)
        if not self.min_stack or val <= self.min_stack[-1]:
            self.min_stack.append(val)           # new minimum (or duplicate)

    def pop(self) -> None:
        if self.stack.pop() == self.min_stack[-1]:
            self.min_stack.pop()                 # the minimum itself left

    def top(self) -> int:
        return self.stack[-1]

    def get_min(self) -> int:
        return self.min_stack[-1]
```

```rust
struct MinStack {
    stack: Vec<i32>,
    min_stack: Vec<i32>,                         // history of running minimums
}

impl MinStack {
    fn new() -> Self {
        MinStack { stack: Vec::new(), min_stack: Vec::new() }
    }

    /// @param val value to push
    fn push(&mut self, val: i32) {
        self.stack.push(val);
        if self.min_stack.is_empty() || val <= *self.min_stack.last().unwrap() {
            self.min_stack.push(val);            // new minimum (or duplicate)
        }
    }

    fn pop(&mut self) {
        if let (Some(v), Some(&m)) = (self.stack.pop(), self.min_stack.last()) {
            if v == m {
                self.min_stack.pop();            // the minimum itself left
            }
        }
    }

    fn top(&self) -> i32 {
        *self.stack.last().unwrap()
    }

    fn get_min(&self) -> i32 {
        *self.min_stack.last().unwrap()
    }
}
```

## Dry run

**Input:** the example sequence.

```
push(-2): stack=[-2],        minStack empty -> push.  minStack=[-2]
push(0):  stack=[-2,0],      0 <= -2? no.             minStack=[-2]
push(-3): stack=[-2,0,-3],   -3 <= -2 -> push.        minStack=[-2,-3]
getMin() -> minStack.top = -3 ✓
pop():    pops -3; -3 == minStack.top(-3) -> pop minStack.  stack=[-2,0], minStack=[-2]
top()    -> 0 ✓
getMin() -> -2 ✓
```

Now the duplicate case — the reason for `<=`:

```
push(1): stack=[1],        minStack=[1]
push(1): stack=[1,1],      1 <= 1 -> push.            minStack=[1,1]
pop():   pops 1; 1 == minStack.top(1) -> pop.         stack=[1], minStack=[1]
getMin() -> 1 ✓   (with `<` instead of `<=`, the second 1 would never have been recorded,
                   and this pop would have emptied minStack — getMin would crash)
```

## Complexity

**Time.** Every operation is $O(1)$ (amortized for the stacks' growth):

$$
T_{\text{push/pop/top/getMin}}(n) = O(1)
$$

**Space.** Two stacks:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **MinStackShort** (`src/main/kotlin/stack/MinStackShort.kt`) — a one-stack trick storing `2*val - min` encoded values; the repo's compact alternative.
- **Design A Stack With Increment Operations** (`src/main/kotlin/stack/DesignAStackWithIncrementOperations.kt`) — a lazy `inc[]` array plus a final pass; the same "sidecar state" reflex applied to range updates.
- **Max Stack / monotonic variants** — the same dual-stack skeleton with `>` instead of `<=`.
- **Interview follow-up:** "Can you do it with one stack?" Yes — encode `2*val - currentMin` on push and decode on pop (the repo's `MinStackShort`), trading a little overflow care for one less structure. Worth mentioning as the O(1)-space curiosity, with the dual-stack as the primary answer.
