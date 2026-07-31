# 8.14 Online Stock Span

> **Source:** [`src/main/kotlin/stack/OnlineStockSpan.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/OnlineStockSpan.kt)
> **Pattern:** monotonic stack of (price, span) · **Core page**

## The Problem

`next(price)` returns how many **consecutive** previous days (incl. today) had price ≤ today's.

- Constraints: ≤ 10⁴ calls.

## Examples

```
["StockSpanner","next","next","next","next","next","next","next"]
[[],[100],[80],[60],[70],[60],[75],[85]]
-> [null,1,1,1,2,1,4,6]
```

## Intuition — pop the dominated days, absorb their spans

A day's span = 1 + the spans of all *previous* days it beats. A stack of `(price, span)` that's **strictly decreasing in price** — when a new price arrives, pop every cheaper top and add its span:

```kotlin
data class Stock(var price: Int, var spanDays: Int)

fun next(price: Int): Int {
    var spanDays = 1

    while (stack.isNotEmpty() && stack.last().price <= price) {
        spanDays += stack.removeLast().spanDays      // absorbed days
    }

    stack.add(Stock(price, spanDays))
    return spanDays
}
```

**Why does the pop absorb spans?** If today's price ≥ the top's, today also spans *everything the top spanned* (all those days were ≤ the top ≤ today). The span accumulates — the [8.3](daily-temperatures.md) "next greater" pattern inverted (≤ instead of >), with the span carried in the stack.

**Why can popped days be discarded?** Once a day is beaten, no *future* price can see it — any future price ≥ today's already covers today's whole span (which includes the popped days). The monotone stack stays minimal — the [8.0](pattern-primer.md) "discard dominated state" contract.

## Approach 1 — Scan back per query (O(n) per call)

Walk backwards while prices ≤: correct, quadratic total.

## Approach 2 — Monotonic stack with span carry (the repo's version, optimal)

```kotlin
class OnlineStockSpan {
    data class Stock(var price: Int, var spanDays: Int)

    private val stack = mutableListOf<Stock>()

    /**
     * @param price today's price
     * @return      consecutive days with price <= today's
     */
    fun next(price: Int): Int {
        var spanDays = 1

        while (stack.isNotEmpty() && stack.last().price <= price) {
            spanDays += stack.removeLast().spanDays
        }

        stack.add(Stock(price, spanDays))
        return spanDays
    }
}
```

```java
import java.util.*;

public class OnlineStockSpan {
    private final Deque<int[]> stack = new ArrayDeque<>();   // {price, span}

    /**
     * @param price today's price
     * @return      consecutive days with price <= today's
     */
    public int next(int price) {
        int span = 1;

        while (!stack.isEmpty() && stack.peek()[0] <= price) {
            span += stack.pop()[1];
        }

        stack.push(new int[]{price, span});
        return span;
    }
}
```

```cpp
#include <stack>
#include <utility>

class OnlineStockSpan {
    std::stack<std::pair<int, int>> stack;    // {price, span}

public:
    /**
     * @param price today's price
     * @return      consecutive days with price <= today's
     */
    int next(int price) {
        int span = 1;

        while (!stack.empty() && stack.top().first <= price) {
            span += stack.top().second;
            stack.pop();
        }

        stack.push({price, span});
        return span;
    }
};
```

```python
class StockSpanner:
    def __init__(self):
        self.stack = []                 # (price, span)

    def next(self, price: int) -> int:
        span = 1
        while self.stack and self.stack[-1][0] <= price:
            span += self.stack.pop()[1]     # absorbed days
        self.stack.append((price, span))
        return span
```

```rust
struct StockSpanner {
    stack: Vec<(i32, i32)>,             // (price, span)
}

impl StockSpanner {
    fn new() -> Self { Self { stack: Vec::new() } }

    /// @param price today's price
    /// @return      consecutive days with price <= today's
    fn next(&mut self, price: i32) -> i32 {
        let mut span = 1;
        while let Some(&(p, s)) = self.stack.last() {
            if p > price { break; }
            span += s;                  // absorbed days
            self.stack.pop();
        }
        self.stack.push((price, span));
        span
    }
}
```

## Dry run

**Input:** `[100, 80, 60, 70, 60, 75, 85]`.

```
100: stack [].  span=1.  push (100,1).            -> 1
80:  top 100 > 80.  span=1.  push (80,1).         -> 1
60:  top 80 > 60.  span=1.  push (60,1).          -> 1
70:  top 60 <= 70: span=1+1=2, pop.  top 80 > 70 stop.  push (70,2).   -> 2
60:  top 70 > 60.  span=1.  push (60,1).          -> 1
75:  top 60 <= 75: span=2, pop.  top 70 <= 75: span=2+2=4, pop.  top 80 > 75 stop.  push (75,4).  -> 4
85:  top 75 <= 85: span=1+4=5, pop.  top 80 <= 85: span=5+1=6, pop.  push (85,6).        -> 6
```

The span-absorption is visible at 75: it pops 60 (span 1) and 70 (span 2) → its own span 1+1+2 = 4, covering the 60, 70, and their sub-days. The stack stays decreasing: `[(80,1),(85,6)]` — the popped 100/75/70/60 are gone, dominated forever.

## Complexity

**Time.** Amortized O(1) per call:

$$
T = O(1) \text{ amortized}
$$

**Space.** The stack:

$$
S = O(n)
$$

## Variants & follow-ups

- **Daily Temperatures** ([8.3](daily-temperatures.md)) — the same monotone stack, next-greater (strict >) instead of ≤-absorption.
- **Largest Rectangle In Histogram** ([8.5](largest-rectangle-in-histogram.md)) — span-accumulation in its area form.
- **Interview follow-up:** "Why is the stack amortized O(1)?" Each entry is pushed once and popped at most once — total pops ≤ total pushes across all calls. The while-loop's total work is bounded by the number of `next` calls, not n² — the [7.x](../ch07-heaps/pattern-primer.md) amortization argument.
