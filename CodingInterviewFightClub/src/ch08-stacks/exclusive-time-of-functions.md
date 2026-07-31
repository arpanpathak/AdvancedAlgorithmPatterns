# 8.15 Exclusive Time Of Functions

> **Source:** [`src/main/kotlin/stack/ExclusiveTimeOfFunctions.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/ExclusiveTimeOfFunctions.kt)
> **Pattern:** interval accounting with a stack · **Core page**

## The Problem

Each log `"id:start/end:time"`; compute each function's **exclusive** execution time.

- Constraints: logs sorted by time; ids < n ≤ 100.

## Examples

```
Input:  n = 2, logs = ["0:start:0","1:start:2","1:end:5","0:end:6"]
Output: [3,4]   (fn 0: [0,2) + [5,6] = 3; fn 1: [2,5] = 4)
```

## Intuition — a stack of running functions; intervals subtract

The stack holds *currently running* functions. On `start`, the previous top's interval (from `prevTime` to now) is credited to it, then the new function pushes. On `end`, the top's interval (inclusive!) is credited and it pops:

```kotlin
for (log in logs) {
    val (id, type, time) = log.split(":")
    val timestamp = time.toInt()

    when (type) {
        "start" -> {
            if (stack.isNotEmpty()) {
                result[stack.last()] += timestamp - prevTime   // prev fn ran [prevTime, timestamp)
            }
            stack.add(funcId)
            prevTime = timestamp
        }
        "end" -> {
            result[funcId] += timestamp - prevTime + 1        // INCLUSIVE end
            stack.removeLast()
            prevTime = timestamp + 1
        }
    }
}
```

**Why `+1` on end and `+1` on the next start's prevTime?** `start` intervals are half-open `[prev, now)`; `end` intervals are inclusive `[prev, now]`. The `prevTime` bookkeeping encodes both — the classic inclusive-end off-by-one.

**Why is the stack the right structure?** Functions nest (call stack) — the *most recent* start is the only one running; intervals credit the stack top. The [8.x](../ch08-stacks/pattern-primer.md) nesting contract.

## Approach 1 — Simulation with a stack (the repo's version, optimal)

```kotlin
class ExclusiveTimeOfFunctions {
    /**
     * @param n    function count
     * @param logs execution logs
     * @return     exclusive time per function
     */
    fun exclusiveTime(n: Int, logs: List<String>): IntArray {
        val result = IntArray(n)
        val stack = mutableListOf<Int>()
        var prevTime = 0

        for (log in logs) {
            val (id, type, time) = log.split(":")
            val funcId = id.toInt()
            val timestamp = time.toInt()

            when (type) {
                "start" -> {
                    if (stack.isNotEmpty()) {
                        result[stack.last()] += timestamp - prevTime
                    }
                    stack.add(funcId)
                    prevTime = timestamp
                }
                "end" -> {
                    result[funcId] += timestamp - prevTime + 1
                    stack.removeLast()
                    prevTime = timestamp + 1
                }
            }
        }
        return result
    }
}
```

```java
import java.util.*;

public class ExclusiveTimeOfFunctions {
    /**
     * @param n    function count
     * @param logs execution logs
     * @return     exclusive time per function
     */
    public int[] exclusiveTime(int n, List<String> logs) {
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        int prev = 0;

        for (String log : logs) {
            String[] parts = log.split(":");
            int id = Integer.parseInt(parts[0]);
            int time = Integer.parseInt(parts[2]);

            if (parts[1].equals("start")) {
                if (!stack.isEmpty()) result[stack.peek()] += time - prev;
                stack.push(id);
                prev = time;
            } else {
                result[id] += time - prev + 1;
                stack.pop();
                prev = time + 1;
            }
        }
        return result;
    }
}
```

```cpp
#include <vector>
#include <string>
#include <stack>

class ExclusiveTimeOfFunctions {
public:
    /**
     * @param n    function count
     * @param logs execution logs
     * @return     exclusive time per function
     */
    std::vector<int> exclusiveTime(int n, std::vector<std::string>& logs) {
        std::vector<int> result(n, 0);
        std::stack<int> stack;
        int prev = 0;

        for (const std::string& log : logs) {
            int firstColon = log.find(':');
            int lastColon = log.rfind(':');
            int id = std::stoi(log.substr(0, firstColon));
            bool isStart = log[firstColon + 1] == 's';
            int time = std::stoi(log.substr(lastColon + 1));

            if (isStart) {
                if (!stack.empty()) result[stack.top()] += time - prev;
                stack.push(id);
                prev = time;
            } else {
                result[id] += time - prev + 1;
                stack.pop();
                prev = time + 1;
            }
        }
        return result;
    }
};
```

```python
def exclusive_time(n: int, logs: list[str]) -> list[int]:
    """
    @param n:    function count
    @param logs: execution logs
    @return:     exclusive time per function
    """
    result = [0] * n
    stack = []
    prev = 0

    for log in logs:
        fid, typ, t = log.split(":")
        fid, t = int(fid), int(t)

        if typ == "start":
            if stack:
                result[stack[-1]] += t - prev
            stack.append(fid)
            prev = t
        else:
            result[fid] += t - prev + 1
            stack.pop()
            prev = t + 1

    return result
```

```rust
impl Solution {
    /// @param n    function count
    /// @param logs execution logs
    /// @return     exclusive time per function
    pub fn exclusive_time(n: i32, logs: Vec<String>) -> Vec<i32> {
        let mut result = vec![0; n as usize];
        let mut stack: Vec<i32> = Vec::new();
        let mut prev = 0;

        for log in &logs {
            let parts: Vec<&str> = log.split(':').collect();
            let id: i32 = parts[0].parse().unwrap();
            let is_start = parts[1] == "start";
            let time: i32 = parts[2].parse().unwrap();

            if is_start {
                if let Some(&top) = stack.last() {
                    result[top as usize] += time - prev;
                }
                stack.push(id);
                prev = time;
            } else {
                result[id as usize] += time - prev + 1;
                stack.pop();
                prev = time + 1;
            }
        }
        result
    }
}
```

## Dry run

**Input:** `n = 2, logs = ["0:start:0","1:start:2","1:end:5","0:end:6"]`.

```
prev=0, stack=[]
"0:start:0": stack empty.  push 0.  prev=0.
"1:start:2": top 0: result[0] += 2-0 = 2.  push 1.  prev=2.
"1:end:5":   result[1] += 5-2+1 = 4.  pop.  prev=6.
"0:end:6":   result[0] += 6-6+1 = 1.  pop.  prev=7.

Output: [3,4] ✓
```

The interval split is visible: fn 0 gets `[0,2)` = 2 (while fn 1 hadn't started) + `[6,6]` = 1 (its own final unit) = 3; fn 1 gets `[2,5]` = 4. The `prevTime` transitions (`= timestamp` after start, `= timestamp + 1` after end) make each unit counted exactly once.

## Complexity

**Time.** One pass over logs:

$$
T(L) = O(L)
$$

**Space.** The stack:

$$
S(L) = O(n)
$$

## Variants & follow-ups

- **Basic Calculator** ([8.11](basic-calculator.md)) — the nesting-stack family with a different payload.
- **Interview follow-up:** "Why inclusive end but exclusive start?" The logs define `start` as "begins at t" and `end` as "ends at t" — a function running [2,5] owns units 2,3,4,5 (4 units). The `+1` on end and the `+1` on `prevTime` after end are the same inclusive-exclusive convention in two places.
