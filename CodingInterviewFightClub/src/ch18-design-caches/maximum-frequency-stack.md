# 18.17 Maximum Frequency Stack

> **Source**: [`src/main/kotlin/hashtable/MaximumFrequencyStack.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/hashtable/MaximumFrequencyStack.kt)
> **Pattern**: frequency → group stacks · **Core page**

## The Problem

`push(val)` and `pop()` removing the **most frequent** element (ties → most recent).

- Constraints: ≤ 2×10⁴ ops.

## Examples

```
["FreqStack","push","push","push","push","push","push","pop","pop","pop","pop"]
[[],[5],[7],[5],[7],[4],[5],[],[],[],[]]
-> [null,null,null,null,null,null,null,5,7,5,4]
```

## Intuition — each frequency gets its own stack; pop from the max frequency's

```kotlin
val freq = mutableMapOf<Int, Int>()
val groups = mutableMapOf<Int, ArrayDeque<Int>>()   // frequency -> stack
var maxFreq = 0

fun push(`val`: Int) {
    val count = (freq[`val`] ?: 0) + 1
    freq[`val`] = count
    maxFreq = maxOf(maxFreq, count)

    groups.getOrPut(count) { ArrayDeque() }.add(`val`)
}

fun pop(): Int {
    val value = groups[maxFreq]!!.removeLast()      // most frequent, most recent
    freq[value] = freq[value]!! - 1

    if (groups[maxFreq]!!.isEmpty()) maxFreq--      // no more at this frequency
    return value
}
```

**Why per-frequency stacks?** The pop rule "most frequent, then most recent" is *exactly* "the top of the max-frequency stack". Each push lands in its frequency's stack — recency is preserved per frequency; the max-frequency pointer selects the winner.

**Why `maxFreq--` on empty?** When the max group drains, the next max is `maxFreq - 1` (frequencies decrement by 1). The [18.2](lfu-cache.md) min-counter idea, mirrored.

## Approach 1 — Priority queue of (freq, time, val)

PQ with composite keys: correct, O(log n) per op.

## Approach 2 — Frequency stacks (the repo's version, optimal)

```kotlin
class FreqStack() {
    val freq = mutableMapOf<Int, Int>()
    val groups = mutableMapOf<Int, ArrayDeque<Int>>()
    var maxFreq = 0

    /**
     * @param val value to push
     */
    fun push(`val`: Int) {
        val count = (freq[`val`] ?: 0) + 1
        freq[`val`] = count
        if (count > maxFreq) maxFreq = count

        if (count !in groups) groups[count] = ArrayDeque()
        groups[count]?.add(`val`)
    }

    /**
     * @return the most frequent (most recent) element
     */
    fun pop(): Int {
        val value = groups[maxFreq]!!.removeLast()
        freq[value] = freq[value]!! - 1

        if (groups[maxFreq]!!.isEmpty()) maxFreq--
        return value
    }
}
```

```java
import java.util.*;

public class FreqStack {
    private final Map<Integer, Integer> freq = new HashMap<>();
    private final Map<Integer, Deque<Integer>> groups = new HashMap<>();
    private int maxFreq = 0;

    /**
     * @param val value to push
     */
    public void push(int val) {
        int count = freq.getOrDefault(val, 0) + 1;
        freq.put(val, count);
        maxFreq = Math.max(maxFreq, count);

        groups.computeIfAbsent(count, k -> new ArrayDeque<>()).push(val);
    }

    /**
     * @return the most frequent (most recent) element
     */
    public int pop() {
        int value = groups.get(maxFreq).pop();
        freq.put(value, freq.get(value) - 1);

        if (groups.get(maxFreq).isEmpty()) maxFreq--;
        return value;
    }
}
```

```cpp
#include <unordered_map>
#include <stack>

class FreqStack {
    std::unordered_map<int, int> freq;
    std::unordered_map<int, std::stack<int>> groups;
    int maxFreq = 0;

public:
    /**
     * @param val value to push
     */
    void push(int val) {
        int count = ++freq[val];
        maxFreq = std::max(maxFreq, count);
        groups[count].push(val);
    }

    /**
     * @return the most frequent (most recent) element
     */
    int pop() {
        int value = groups[maxFreq].top();
        groups[maxFreq].pop();
        freq[value]--;

        if (groups[maxFreq].empty()) maxFreq--;
        return value;
    }
};
```

```python
from collections import defaultdict, deque

class FreqStack:
    def __init__(self):
        self.freq = defaultdict(int)
        self.groups = defaultdict(deque)
        self.max_freq = 0

    def push(self, val: int) -> None:
        self.freq[val] += 1
        count = self.freq[val]
        self.max_freq = max(self.max_freq, count)
        self.groups[count].append(val)

    def pop(self) -> int:
        value = self.groups[self.max_freq].pop()
        self.freq[value] -= 1

        if not self.groups[self.max_freq]:
            self.max_freq -= 1
        return value
```

```rust
use std::collections::HashMap;

struct FreqStack {
    freq: HashMap<i32, i32>,
    groups: HashMap<i32, Vec<i32>>,
    max_freq: i32,
}

impl FreqStack {
    fn new() -> Self {
        Self { freq: HashMap::new(), groups: HashMap::new(), max_freq: 0 }
    }

    /// @param val value to push
    fn push(&mut self, val: i32) {
        let count = self.freq.entry(val).or_insert(0);
        *count += 1;
        self.max_freq = self.max_freq.max(*count);
        self.groups.entry(*count).or_default().push(val);
    }

    /// @return the most frequent (most recent) element
    fn pop(&mut self) -> i32 {
        let value = self.groups.get_mut(&self.max_freq).unwrap().pop().unwrap();

        let f = self.freq.get_mut(&value).unwrap();
        *f -= 1;

        if self.groups.get(&self.max_freq).unwrap().is_empty() {
            self.max_freq -= 1;
        }
        value
    }
}
```

## Dry run

**Input:** `push(5), push(7), push(5), push(7), push(4), push(5)`.

```
push 5: freq 1.  groups[1]: [5].  max 1.
push 7: freq 1.  groups[1]: [5,7].  max 1.
push 5: freq 2.  groups[2]: [5].  max 2.
push 7: freq 2.  groups[2]: [5,7].  max 2.
push 4: freq 1.  groups[1]: [5,7,4].  max 2.
push 5: freq 3.  groups[3]: [5].  max 3.

pop: groups[3] -> 5.  freq 5->2.  groups[3] empty -> max 2.
pop: groups[2] -> 7 (most recent of the freq-2 stack).  freq 7->1.  max 2.
pop: groups[2] -> 5.  freq 5->1.  empty -> max 1.
pop: groups[1] -> 4 (most recent).  freq 4->0.

Output: [5,7,5,4] ✓
```

The tie-break falls out of the per-frequency stacks: at freq 2, `[5,7]` pops 7 first (later push). The max-frequency decrement tracks the current champion — no heap, no time stamps.

## Complexity

**Time.** O(1) per op:

$$
T = O(1)
$$

**Space.** The maps:

$$
S = O(n)
$$

## Variants & follow-ups

- **LFU Cache** ([18.2](lfu-cache.md)) — the frequency-bucket family with capacity eviction.
- **Interview follow-up:** "Why do frequency stacks beat a priority queue?" The composite key (freq desc, time desc) needs O(log n) per op; the bucket stacks make both dimensions O(1) — frequency via the map index, recency via the stack top. The LFU design's frequency-bucket structure in its simplest form.
