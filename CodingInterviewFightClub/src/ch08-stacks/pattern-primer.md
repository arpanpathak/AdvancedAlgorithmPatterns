# 8.0 Pattern Primer — LIFO, FIFO, and the Monotonic Stack

A **stack** is LIFO: the element you push *last* is the one you pop *first*. A **queue** is FIFO: the element you push *first* leaves *first*. That one-word difference drives everything — a stack keeps the *most recent* frontier, a queue keeps the *oldest*. (Trees and graphs already used both: DFS's recursion stack vs BFS's queue from [Chapters 5](../ch05-trees/index.md) and [6](../ch06-graphs/index.md).)

This chapter's problems fall into three families:

## Move 1 — Match pairs

Some problems are literally "the thing I just saw decides the thing before it." Valid parentheses ([8.1](valid-parentheses.md)) and RPN arithmetic ([8.6](evaluate-reverse-polish-notation.md)) both push an operand and pop it the moment its *partner* arrives. The stack's LIFO order is the whole algorithm: the most recently pushed opener is the one a closer must match.

## Move 2 — Carry state down

A stack can store *sidecars* — extra information parallel to the data. [Min Stack](min-stack.md) keeps a second stack of running minimums: each push records "the minimum among everything below me too." Design problems ([8.2](min-stack.md), and the repo's Flatten Nested List Iterator, Stack With Increment Operations) are almost always this: the trick is what you store *alongside* the value.

## Move 3 — The monotonic stack (the big one)

The pattern that makes stacks a chapter of their own:

> **Keep the stack sorted** by repeatedly popping elements that violate the order *before* pushing the new one.

```kotlin
for (x in items) {
    while (stack.isNotEmpty() && violatesOrder(stack.last(), x)) stack.removeLast()
    stack.addLast(x)
}
```

Each element is pushed once and popped at most once — so the whole sweep is **amortized $O(n)$**, even though there's a `while` inside the loop. Three canonical uses:

- **Next greater/smaller element** ([8.3](daily-temperatures.md), [8.4](next-greater-element-ii.md)): while `x` is greater than the stack top, the top's *next greater* is `x` — resolve it, pop. The stack holds unresolved candidates, in decreasing order.
- **Span / visibility** (Online Stock Span, Visible People, Buildings With An Ocean View): the pop count *is* the answer — count how many elements each newcomer resolves.
- **Largest rectangle** ([8.5](largest-rectangle-in-histogram.md)): pop gives the height, the distance to the new smaller bar gives the width. The hard one — see its page for why the stack holds *indices*, not values.

**The reflex to build:** the moment the problem says "for each element, find the nearest element that is larger/smaller", reach for a monotonic stack, not nested loops. The nested loop is $O(n^2)$; the stack is $O(n)$ because the `while` only ever pops things already pushed.

## Complexity intuition

Every element is pushed once and popped once across the whole run → $O(n)$ amortized per problem, $O(n)$ space for the stack. The "trick" questions are about *what to store* (indices vs values — [8.5](largest-rectangle-in-histogram.md) is the classic trap) and *what order to maintain* (increasing vs decreasing — deciding by "which side is the question on").
