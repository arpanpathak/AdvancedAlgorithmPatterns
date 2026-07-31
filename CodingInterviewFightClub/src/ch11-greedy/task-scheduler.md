# 11.6 Task Scheduler

> **Source:** [`src/main/kotlin/greedy/TaskScheduler.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/TaskScheduler.kt)
> **Pattern:** frequency math · **Core page**

## The Problem

Given a char array `tasks` and an integer `n`, each task takes one unit of time, and two **identical** tasks must be separated by at least `n` units of time (you may insert *idle* slots). Return the **minimum total time** (tasks + idles).

- Constraints: $1 \le n \le 100$; up to $10^4$ tasks; uppercase letters only.

## Examples

```
Input:  tasks = ["A","A","A","B","B","B"], n = 2
Output: 8   (A B idle A B idle A B)

Input:  tasks = ["A","C","A","B","D","B"], n = 1
Output: 6   (no idles needed — the order just avoids equal neighbors)
```

## Intuition — the most frequent task *dictates the frame*

The constraint bites hardest on the **most frequent task** — call its frequency `maxFreq` and let `maxCount` be how many tasks tie for that maximum. Think of scheduling as filling frames:

- The `maxFreq` occurrences of the top task must be spread out. Between the first `maxFreq - 1` of them there must be at least `n` other slots — a frame of `n + 1` units each.
- The last occurrence needs no trailing gap.
- The other `maxCount`-tied tasks can fill the *last* frame's slot; all other tasks fill in anywhere.

So the *minimum length forced by the top task* is:

$$
(\text{maxFreq} - 1) \times (n + 1) + \text{maxCount}
$$

The **greedy part**: everything else fits into this frame *without* adding time, *if* the frame is big enough. When it isn't (many distinct tasks), the total is just `tasks.size` — every slot is real work, no idles. The answer is the max of the two:

$$
\text{answer} = \max(\text{tasks.size},\; (\text{maxFreq} - 1)(n + 1) + \text{maxCount})
$$

**Why can all other tasks fit without extra time?** The frame has `(maxFreq - 1)` gaps of `n` slots each — `(maxFreq-1) * n` idle slots waiting to be filled — plus the `maxCount` slots at the end. Any task with frequency `<= maxFreq` can be distributed one per gap (a pigeonhole argument: `freq - 1 <= maxFreq - 1` occurrences fit into the `maxFreq - 1` gaps). This is where "greedy is a proof" shows up: the construction always exists, so the lower bound is achieved.

**The intuition dump for interviews:** "the rarest tasks are free — they hide in the idle slots the most frequent task creates." State that sentence, then write the formula.

## Approach 1 — Simulate with a max-heap + cooldown queue

Classic heap simulation: schedule the highest-frequency available task each tick, push it into a cooldown queue for `n` ticks, count every tick (including idles). Correct and general, but $O(\text{time} \cdot \log k)$ — the formula below is the closed form this simulation converges to.

```kotlin
import java.util.*

// 1. The schedulable unit in the max-heap
data class Task(val name: Char, var remainingCount: Int)

// 2. A task waiting for a specific time slot to become available
data class Cooldown(val task: Task, val availableTime: Int)

fun leastInterval(tasks: CharArray, n: Int): Int {
    // Frequency map -> max-heap of tasks, prioritized by remaining count
    val freqMap = tasks.groupingBy { it }.eachCount()
    val maxHeap = PriorityQueue<Task> { t1, t2 -> t2.remainingCount - t1.remainingCount }
    maxHeap.addAll(freqMap.map { (name, count) -> Task(name, count) })

    val queue: Queue<Cooldown> = LinkedList()
    var time = 0

    while (maxHeap.isNotEmpty() || queue.isNotEmpty()) {
        time++                                 // advance time (idles count)

        if (maxHeap.isNotEmpty()) {
            val currentTask = maxHeap.poll()   // greedily pick the highest-priority task
            currentTask.remainingCount--

            if (currentTask.remainingCount > 0) {
                // Cool down: this task can't run again until time + n
                queue.offer(Cooldown(currentTask, time + n))
            }
        }

        // Release tasks whose cooldown has expired
        if (queue.isNotEmpty() && queue.peek().availableTime == time) {
            maxHeap.add(queue.poll().task)
        }
    }
    return time
}
```

The simulation's `time++` counts **every tick, including idle ones** — that's the answer. It's correct and general (handles any cooldown `n`), but $O(\text{time} \cdot \log k)$: the closed-form formula below is what this loop converges to. The [11.10](reorganize-string.md) page is this exact engine for the `k = 1` rearrangement case.

## Approach 2 — The frequency formula (the repo's version, optimal)

```kotlin
class TaskScheduler {
    /**
     * @param tasks task types (each takes one unit)
     * @param n     minimum separation between identical tasks
     * @return      minimum total time (tasks + idle)
     */
    fun leastInterval(tasks: CharArray, n: Int): Int {
        val freq = tasks.toList().groupingBy { it }.eachCount().values
        val maxFreq = freq.maxOrNull() ?: 0
        val maxCount = freq.count { it == maxFreq }      // how many tasks tie for the max

        return maxOf(tasks.size, (maxFreq - 1) * (n + 1) + maxCount)
    }
}
```

```java
public class TaskScheduler {
    /**
     * @param tasks task types (each takes one unit)
     * @param n     minimum separation between identical tasks
     * @return      minimum total time (tasks + idle)
     */
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char c : tasks) freq[c - 'A']++;

        int maxFreq = 0, maxCount = 0;
        for (int f : freq) maxFreq = Math.max(maxFreq, f);
        for (int f : freq) if (f == maxFreq) maxCount++;

        return Math.max(tasks.length, (maxFreq - 1) * (n + 1) + maxCount);
    }
}
```

```cpp
#include <algorithm>
#include <string>
#include <vector>

class TaskScheduler {
public:
    /**
     * @param tasks task types (each takes one unit)
     * @param n     minimum separation between identical tasks
     * @return      minimum total time (tasks + idle)
     */
    int leastInterval(std::vector<char>& tasks, int n) {
        int freq[26] = {0};
        for (char c : tasks) freq[c - 'A']++;

        int maxFreq = *std::max_element(freq, freq + 26);
        int maxCount = 0;
        for (int f : freq) if (f == maxFreq) maxCount++;

        return std::max((int)tasks.size(), (maxFreq - 1) * (n + 1) + maxCount);
    }
};
```

```python
def least_interval(tasks: list[str], n: int) -> int:
    """
    @param tasks: task types (each takes one unit)
    @param n:     minimum separation between identical tasks
    @return:      minimum total time (tasks + idle)
    """
    from collections import Counter

    freq = Counter(tasks).values()
    max_freq = max(freq)
    max_count = sum(1 for f in freq if f == max_freq)

    return max(len(tasks), (max_freq - 1) * (n + 1) + max_count)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param tasks task types (each takes one unit)
    /// @param n     minimum separation between identical tasks
    /// @return      minimum total time (tasks + idle)
    pub fn least_interval(tasks: Vec<char>, n: i32) -> i32 {
        let mut freq: HashMap<char, i32> = HashMap::new();
        for c in tasks.iter() {
            *freq.entry(*c).or_insert(0) += 1;
        }

        let max_freq = *freq.values().max().unwrap();
        let max_count = freq.values().filter(|&&f| f == max_freq).count() as i32;

        (tasks.len() as i32).max((max_freq - 1) * (n + 1) + max_count)
    }
}
```

## Dry run

**Input:** `tasks = ["A","A","A","B","B","B"]`, `n = 2`.

```
freq = {A:3, B:3}; maxFreq = 3; maxCount = 2 (A and B both hit 3)
frame formula: (3 - 1) * (2 + 1) + 2 = 2 * 3 + 2 = 8
tasks.size = 6
answer = max(6, 8) = 8 ✓   (A B idle A B idle A B — two idles forced)
```

Now `tasks = ["A","C","A","B","D","B"]`, `n = 1`:

```
freq = {A:2, B:2, C:1, D:1}; maxFreq = 2; maxCount = 2
frame formula: (2 - 1) * (1 + 1) + 2 = 1 * 2 + 2 = 4
tasks.size = 6
answer = max(6, 4) = 6 ✓   (A B C A B D — no idles; the frame is smaller than the work)
```

The two branches of the `max` are both visible: when the top task's frame forces idles (8), and when the workload alone dominates (6). The formula computes "the cheapest schedule the bottleneck task allows" and the `max` with `tasks.size` guards against the frame being smaller than the actual work.

## Complexity

**Time.** One counting pass + fixed-size scans:

$$
T(n) = O(n)
$$

**Space.** The frequency map (bounded by alphabet):

$$
S(n) = O(|\Sigma|) \subseteq O(n)
$$

## Variants & follow-ups

- **Task Scheduler II** — tasks are a *sequence* you must respect (not a multiset): the same cooling constraint with order, solved with a per-task "next allowed time" map.
- **Minimum Number Of Refueling Stops** ([11.7](minimum-number-of-refueling-stops.md)) — the same "bottleneck dictates the frame" logic but with a heap choosing *where* to spend resources.
- **Interview follow-up:** "Why `maxCount` at the end of the formula and not `1`?" All tasks tied for the maximum frequency get one slot in the *final* frame — `A B A B` needs both A and B scheduled at the end, not just one. Dropping `maxCount` to 1 undercounts whenever two tasks share the top frequency (like the `A/B` example above: 7 instead of 8).
