# 18.13 Moving Average From Data Stream

> **Source**: [`src/main/kotlin/stream/MovingAverageOfARunningStream.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stream/MovingAverageOfARunningStream.kt)
> **Pattern**: windowed queue with running sum · **Core page**

## The Problem

`next(val)` returns the average of the **last size** values.

- Constraints: size ≤ 10³; calls ≤ 10⁴.

## Examples

```
["MovingAverage","next","next","next","next"]
[[3],[1],[10],[3],[5]]
-> [null,1.0,5.5,4.66667,6.0]
```

## Intuition — a queue + running sum; evict when full

```kotlin
fun next(`val`: Int): Double {
    window.add(`val`)
    sum += `val`

    if (window.size > size) {
        sum -= window.removeFirst()
    }
    return sum.toDouble() / window.size
}
```

The running sum makes each step O(1) — the [7.9](../ch07-heaps/design-hit-counter.md) windowed-queue family with a sum payload.

## Approach 1 — Store all values, compute on demand (O(n) per call)

Keep a list, sum the last k: correct, slow.

## Approach 2 — Queue + running sum (the repo's version, optimal)

```kotlin
class MovingAverage(private val size: Int) {
    private val window = ArrayDeque<Int>()
    private var sum = 0

    /**
     * @param val new value
     * @return    average of the last `size` values
     */
    fun next(`val`: Int): Double {
        window.add(`val`)
        sum += `val`

        if (window.size > size) {
            sum -= window.removeFirst()
        }
        return sum.toDouble() / window.size
    }
}
```

```java
import java.util.*;

public class MovingAverage {
    private final Deque<Integer> window = new LinkedList<>();
    private final int size;
    private double sum = 0;

    public MovingAverage(int size) { this.size = size; }

    /**
     * @param val new value
     * @return    average of the last `size` values
     */
    public double next(int val) {
        window.offer(val);
        sum += val;

        if (window.size() > size) sum -= window.poll();
        return sum / window.size();
    }
}
```

```cpp
#include <queue>

class MovingAverage {
    std::queue<int> window;
    int size;
    double sum = 0;

public:
    MovingAverage(int size) : size(size) {}

    /**
     * @param val new value
     * @return    average of the last `size` values
     */
    double next(int val) {
        window.push(val);
        sum += val;

        if ((int)window.size() > size) {
            sum -= window.front();
            window.pop();
        }
        return sum / window.size();
    }
};
```

```python
from collections import deque

class MovingAverage:
    def __init__(self, size: int):
        self.size = size
        self.window = deque()
        self.sum = 0

    def next(self, val: int) -> float:
        self.window.append(val)
        self.sum += val

        if len(self.window) > self.size:
            self.sum -= self.window.popleft()

        return self.sum / len(self.window)
```

```rust
use std::collections::VecDeque;

struct MovingAverage {
    window: VecDeque<i32>,
    size: usize,
    sum: i64,
}

impl MovingAverage {
    fn new(size: i32) -> Self {
        Self { window: VecDeque::new(), size: size as usize, sum: 0 }
    }

    /// @param val new value
    /// @return    average of the last `size` values
    fn next(&mut self, val: i32) -> f64 {
        self.window.push_back(val);
        self.sum += val as i64;

        if self.window.len() > self.size {
            self.sum -= self.window.pop_front().unwrap() as i64;
        }
        self.sum as f64 / self.window.len() as f64
    }
}
```

## Dry run

**Input:** `size = 3`, `next(1); next(10); next(3); next(5)`.

```
next(1):  window [1].  sum 1.  avg 1.0.
next(10): [1,10].  sum 11.  avg 5.5.
next(3):  [1,10,3].  sum 14.  avg 4.666...
next(5):  add 5 -> sum 19.  size 4 > 3 -> evict 1 -> sum 18.  window [10,3,5].  avg 6.0 ✓
```

## Complexity

**Time.** O(1) per call:

$$
T = O(1)
$$

**Space.** The window:

$$
S = O(size)
$$

## Variants & follow-ups

- **Number Of Recent Calls** ([18.14](number-of-recent-calls.md)) — the same queue-window, counting instead of averaging.
- **Design Hit Counter** ([7.9](../ch07-heaps/design-hit-counter.md)) — the timestamp-window sibling.
- **Interview follow-up:** "Why a running sum instead of summing per query?" The sum makes each step O(1) — one add, one conditional subtract. Re-summing the window each call would be O(size); the running sum is the [15.x](../ch15-sliding-window/pattern-primer.md) window-sum trick in design form.
