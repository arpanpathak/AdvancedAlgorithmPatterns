# 18.6 Design A Stack With Increment Operations

> **Source:** [`src/main/kotlin/stack/DesignAStackWithIncrementOperations.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/DesignAStackWithIncrementOperations.kt)
> **Pattern:** lazy increment array · **Core page**

## The Problem

Design a stack with `push(x)`, `pop()` and `increment(k, val)` — the last adds `val` to the **bottom k** elements. All operations target **O(1)**.

- Constraints: up to $10^5$ operations; `maxSize` ≤ $10^5$.

## Examples

```
CustomStack(3);  push(1); push(2); pop() -> 2;  push(2); push(3); push(4) (ignored, full);
increment(5,100); increment(2,100); pop() -> 103;  pop() -> 202;  pop() -> 201;  pop() -> -1
```

## Intuition — don't touch the bottom k elements; *record* the increment and apply it at pop

`increment(k, val)` naively adds `val` to `min(k, size)` elements — O(k) per call. The lazy trick: an **`increments` array parallel to the stack** where `increments[i]` = "the amount to add to the element at index i **when it's popped**". Then `increment(k, val)` is a single write:

```
increments[min(k, size) - 1] += val      # O(1) — the bottom k elements are all covered by this
```

**Why does one write cover k elements?** The *prefix* property: an increment at index `k-1` applies to everything below it — because the "carry" moves downward. On `pop()`, the popped element gets `increments[top]`, and the carry is **passed down**:

```
pop():
    value = stack.removeLast() + increments[index]
    if index > 0: increments[index - 1] += increments[index]   # carry to the element below
    increments[index] = 0                                      # reset
    return value
```

**Why is the carry correct?** `increment(k, v)` added `v` to elements `0..k-1`. When the element at `k-1` is popped, the elements below (`0..k-2`) still owe `v` — so the increment carries down one slot, where the next pop applies it. Each increment is paid once, at the pop of the highest covered element, then propagated — amortized O(1) per operation.

**The bottom-k semantics:** `increment(k, val)` with `k > size` covers the whole stack — the repo clamps with `min(k, stack.size)`. The "bottom" is index 0, so the write lands at index `clampedK - 1`.

## Approach 1 — Eager increment (O(k) per call)

Loop and add to the bottom k elements: correct, but a sequence of increments is $O(k \cdot n)$.

## Approach 2 — Lazy increment with carry (the repo's version, optimal)

```kotlin
class DesignAStackWithIncrementOperations(maxSize: Int) {
    var maxSize = maxSize
    var stack = ArrayDeque<Int>()
    var increments = IntArray(maxSize)

    /** @param x element to push (ignored when full) */
    fun push(x: Int) {
        if (stack.size < maxSize)
            stack.addLast(x)
    }

    /** @return the popped value (with any pending increments), or -1 when empty */
    fun pop(): Int {
        if (stack.isEmpty()) return -1

        val index = stack.size - 1
        val value = stack.removeLast() + increments[index]

        if (index > 0) {
            increments[index - 1] += increments[index]   // carry the increment to the element below
        }
        increments[index] = 0                            // reset after applying

        return value
    }

    /** @param k   apply to the bottom k elements
     *  @param val amount to add (lazily recorded) */
    fun increment(k: Int, `val`: Int) {
        val limit = minOf(k, stack.size) - 1
        if (limit >= 0) {
            increments[limit] += `val`                   // one write covers the bottom k
        }
    }
}
```

```java
public class CustomStack {
    private final int[] stack;
    private final int[] increments;
    private int top = -1;

    /** @param maxSize capacity */
    public CustomStack(int maxSize) {
        stack = new int[maxSize];
        increments = new int[maxSize];
    }

    /** @param x element to push (ignored when full) */
    public void push(int x) {
        if (top + 1 < stack.length) stack[++top] = x;
    }

    /** @return the popped value (with any pending increments), or -1 when empty */
    public int pop() {
        if (top == -1) return -1;
        int value = stack[top] + increments[top];
        if (top > 0) increments[top - 1] += increments[top];   // carry to the element below
        increments[top] = 0;                                   // reset
        top--;
        return value;
    }

    /** @param k apply to the bottom k elements  @param val amount to add */
    public void increment(int k, int val) {
        int limit = Math.min(k, top + 1) - 1;
        if (limit >= 0) increments[limit] += val;              // one write covers the bottom k
    }
}
```

```cpp
#include <vector>

class CustomStack {
    std::vector<int> stack;
    std::vector<int> increments;
    int top = -1;

public:
    /** @param maxSize capacity */
    CustomStack(int maxSize) : stack(maxSize), increments(maxSize) {}

    /** @param x element to push (ignored when full) */
    void push(int x) {
        if (top + 1 < (int)stack.size()) stack[++top] = x;
    }

    /** @return the popped value (with any pending increments), or -1 when empty */
    int pop() {
        if (top == -1) return -1;
        int value = stack[top] + increments[top];
        if (top > 0) increments[top - 1] += increments[top];   // carry to the element below
        increments[top] = 0;                                   // reset
        top--;
        return value;
    }

    /** @param k apply to the bottom k elements  @param val amount to add */
    void increment(int k, int val) {
        int limit = std::min(k, top + 1) - 1;
        if (limit >= 0) increments[limit] += val;              // one write covers the bottom k
    }
};
```

```python
class CustomStack:
    """@param max_size: capacity"""

    def __init__(self, max_size: int):
        self.stack = []
        self.inc = [0] * max_size
        self.max_size = max_size

    def push(self, x: int) -> None:
        """@param x: element to push (ignored when full)"""
        if len(self.stack) < self.max_size:
            self.stack.append(x)

    def pop(self) -> int:
        """@return: the popped value (with any pending increments), or -1 when empty"""
        if not self.stack:
            return -1
        i = len(self.stack) - 1
        value = self.stack.pop() + self.inc[i]
        if i > 0:
            self.inc[i - 1] += self.inc[i]      # carry the increment to the element below
        self.inc[i] = 0                         # reset after applying
        return value

    def increment(self, k: int, val: int) -> None:
        """@param k: apply to the bottom k elements  @param val: amount to add"""
        limit = min(k, len(self.stack)) - 1
        if limit >= 0:
            self.inc[limit] += val              # one write covers the bottom k
```

```rust
struct CustomStack {
    stack: Vec<i32>,
    inc: Vec<i32>,
    max_size: usize,
}

impl CustomStack {
    /// @param max_size capacity
    fn new(max_size: i32) -> Self {
        CustomStack { stack: Vec::new(), inc: vec![0; max_size as usize], max_size: max_size as usize }
    }

    /// @param x element to push (ignored when full)
    fn push(&mut self, x: i32) {
        if self.stack.len() < self.max_size {
            self.stack.push(x);
        }
    }

    /// @return the popped value (with any pending increments), or -1 when empty
    fn pop(&mut self) -> i32 {
        let Some(x) = self.stack.pop() else { return -1; };
        let i = self.stack.len();                    // index AFTER pop = position of x
        let value = x + self.inc[i];
        if i > 0 {
            self.inc[i - 1] += self.inc[i];          // carry the increment to the element below
        }
        self.inc[i] = 0;                             // reset after applying
        value
    }

    /// @param k apply to the bottom k elements  @param val amount to add
    fn increment(&mut self, k: i32, val: i32) {
        let limit = (k as usize).min(self.stack.len()).saturating_sub(1);
        self.inc[limit] += val;                      // one write covers the bottom k
    }
}
```

## Dry run

**Input:** the example sequence: `CustomStack(3)`.

```
push(1): stack=[1].  push(2): stack=[1,2].  pop(): i=1, value=2+inc[1]=2.  inc=[0,0,0].  -> 2
push(2): [1,2].  push(3): [1,2,3].  push(4): full -> ignored.
increment(5,100): limit = min(5,3)-1 = 2.  inc[2] += 100 -> inc=[0,0,100]
increment(2,100): limit = min(2,3)-1 = 1.  inc[1] += 100 -> inc=[0,100,100]
pop(): i=2, value=3+100=103.  carry: inc[1] += 100 -> inc=[0,200,0].  -> 103 ✓
pop(): i=1, value=2+200=202.  carry: inc[0] += 200 -> inc=[200,0,0].  -> 202 ✓
pop(): i=0, value=1+200=201.  -> 201 ✓
pop(): empty -> -1 ✓
```

The two increments (`100` to bottom 5 = all, `100` to bottom 2) stack up as `inc[1]=200` and `inc[2]=100` — and the carry chain pays them in the right order: the top element gets only its own `100`, the middle gets `100+100=200` via the carry, the bottom gets the carried `200`. Each increment is one write; each pop is one read plus one carry.

## Complexity

**Time.** O(1) per operation:

$$
T(n) = O(1) \text{ per operation}
$$

**Space.** The stack + increment array:

$$
S(n) = O(\text{maxSize})
$$

## Variants & follow-ups

- **Range-update / difference-array family** — the same "record the update at the boundary, resolve on read" idea as the difference array in range-sum problems.
- **LFU/LRU caches** ([18.1](lru-cache.md), [18.2](lfu-cache.md)) — deferred bookkeeping in another costume.
- **Interview follow-up:** "Why does one write to `increments[limit]` cover *all* bottom k elements?" The carry moves *downward* on every pop: an increment at index `k-1` applies to that element, then propagates to `k-2` on its pop, then `k-3`, etc. The prefix of k elements is covered by a single write plus the chain — so `increment` is O(1), and each carry is a constant-time step paid once per pop.
