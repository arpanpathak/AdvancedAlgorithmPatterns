# 7.7 Single Threaded CPU

> **Source:** [`src/main/kotlin/heap/SingleThreadedCPU.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/heap/SingleThreadedCPU.kt)
> **Pattern:** event + ready queues · **Core page**

## The Problem

A single-threaded CPU processes tasks `tasks[i] = [enqueueTime, processingTime]`. The CPU is **idle at time 0**, starts a task at the earliest moment one is available, and runs it to completion (no preemption). When multiple tasks are available, it picks the one with the **shortest processing time**; ties by **smallest original index**. Tasks that arrive while the CPU is busy *wait*. Return the order of task indices executed.

- Constraints: $1 \le n \le 10^5$; `enqueueTime` and `processingTime` up to $10^9$; distinct indices.

## Examples

```
Input:  tasks = [[1,2],[2,4],[3,2],[4,1]]
Output: [0,2,3,1]
        t=1 start task 0 (2); t=3 tasks 1,2 available, pick 2 (2); t=5 pick 3 (1); t=6 pick 1 (4)

Input:  tasks = [[7,10],[7,12],[7,5],[7,4],[7,2]]
Output: [4,3,2,0,1]
        (all arrive at t=7 — the CPU picks shortest first: 2,4,5,10,12)
```

## Intuition — the CPU is a greedy scheduler with two lists

The CPU's rule is a loop: *"if I'm idle and something is waiting, do the shortest waiting task; otherwise fast-forward time to the next arrival."* Two ordered collections mirror the two questions:

- **Which tasks have arrived?** Sort tasks by `enqueueTime` and keep a pointer — arrivals are a one-way door (time never moves backward), so each task crosses it exactly once. This is the [gate from 7.5](ipo.md).
- **Which waiting task is next?** A **min-heap** keyed on `(processingTime, index)` — the shortest waiting task is the root; ties fall to the smaller index for free. This is the ranking.

The loop body:

1. **Sweep** — while the next task's `enqueueTime <= time`, push it into the ready heap (it has arrived).
2. **Execute** — if the heap is non-empty, pop the root: run it (add `processingTime` to `time`), record its index.
3. **Idle jump** — if the heap is empty, no task is waiting; fast-forward `time` to the next arrival (the CPU does nothing between now and then).

**Why the idle jump is needed:** `enqueueTime` can be huge and gaps can be long. Naively incrementing `time` one unit at a time would be $O(\text{maxTime})$ — up to $10^9$ iterations. Jumping straight to the next arrival keeps the loop at $O(n)$ heap operations.

**The two-heap "event + ready" shape** (same skeleton as [7.5](ipo.md) and [7.6](meeting-rooms-iii.md)) is the recurring interview pattern: *arrivals sorted into a gate, priorities ranked by a heap, time advanced by events.* Once you see a scheduling problem with "pick the best among the arrived", you are looking at this loop.

## Approach 1 — Simulate second by second (too slow)

Walk `time` forward one unit, collecting arrivals and picking the shortest ready task: correct, but $O(\text{maxTime} + n)$ — and `enqueueTime` goes to $10^9$.

## Approach 2 — Event-driven heap (the repo's version, optimal)

```kotlin
import java.util.*

class SingleThreadedCPU {
    data class Task(val enqueueTime: Int, val processingTime: Int, val index: Int)

    /**
     * @param tasks tasks[i] = [enqueueTime, processingTime]
     * @return      order of task indices executed
     */
    fun getOrder(tasks: Array<IntArray>): IntArray {
        val allTasks = tasks.mapIndexed { i, (enq, proc) -> Task(enq, proc, i) }
            .sortedBy { it.enqueueTime }                       // gate: by arrival

        val pq = PriorityQueue(compareBy<Task> { it.processingTime }.thenBy { it.index })
        val result = mutableListOf<Int>()

        var time = 0
        var i = 0

        while (i < allTasks.size || pq.isNotEmpty()) {
            // Sweep: everything that has arrived by "now" joins the ready heap
            while (i < allTasks.size && allTasks[i].enqueueTime <= time) {
                pq.add(allTasks[i++])
            }

            if (pq.isNotEmpty()) {
                val task = pq.poll()                           // shortest waiting task
                time += task.processingTime                    // run to completion
                result.add(task.index)
            } else {
                time = allTasks[i].enqueueTime                 // idle: jump to next arrival
            }
        }
        return result.toIntArray()
    }
}
```

```java
import java.util.*;

public class SingleThreadedCPU {
    private record Task(int enqueueTime, int processingTime, int index) {}

    /**
     * @param tasks tasks[i] = [enqueueTime, processingTime]
     * @return      order of task indices executed
     */
    public int[] getOrder(int[][] tasks) {
        int n = tasks.length;
        Task[] all = new Task[n];
        for (int i = 0; i < n; i++) all[i] = new Task(tasks[i][0], tasks[i][1], i);
        Arrays.sort(all, Comparator.comparingInt(Task::enqueueTime));   // gate: by arrival

        PriorityQueue<Task> pq = new PriorityQueue<>(
            Comparator.comparingInt(Task::processingTime).thenComparingInt(Task::index));

        int[] result = new int[n];
        int written = 0;
        long time = 0;
        int i = 0;

        while (i < n || !pq.isEmpty()) {
            while (i < n && all[i].enqueueTime() <= time) {    // sweep arrivals
                pq.offer(all[i++]);
            }

            if (!pq.isEmpty()) {
                Task t = pq.poll();                            // shortest waiting task
                time += t.processingTime();
                result[written++] = t.index();
            } else {
                time = all[i].enqueueTime();                   // idle: jump to next arrival
            }
        }
        return result;
    }
}
```

```cpp
#include <algorithm>
#include <functional>
#include <queue>
#include <vector>

class SingleThreadedCPU {
    struct Task { int enqueueTime, processingTime, index; };

public:
    /**
     * @param tasks tasks[i] = [enqueueTime, processingTime]
     * @return      order of task indices executed
     */
    std::vector<int> getOrder(std::vector<std::vector<int>>& tasks) {
        int n = tasks.size();
        std::vector<Task> all;
        for (int i = 0; i < n; i++) all.push_back({tasks[i][0], tasks[i][1], i});
        std::sort(all.begin(), all.end(),
                  [](const Task& a, const Task& b) { return a.enqueueTime < b.enqueueTime; });

        auto cmp = [](const Task& a, const Task& b) {
            return a.processingTime > b.processingTime ||
                   (a.processingTime == b.processingTime && a.index > b.index);
        };
        std::priority_queue<Task, std::vector<Task>, decltype(cmp)> pq(cmp);

        std::vector<int> result;
        long long time = 0;
        int i = 0;

        while (i < n || !pq.empty()) {
            while (i < n && all[i].enqueueTime <= time) {    // sweep arrivals
                pq.push(all[i++]);
            }

            if (!pq.empty()) {
                Task t = pq.top(); pq.pop();                 // shortest waiting task
                time += t.processingTime;
                result.push_back(t.index);
            } else {
                time = all[i].enqueueTime;                   // idle: jump to next arrival
            }
        }
        return result;
    }
};
```

```python
import heapq

def get_order(tasks: list[list[int]]) -> list[int]:
    """
    @param tasks: tasks[i] = [enqueue_time, processing_time]
    @return:      order of task indices executed
    """
    all_tasks = sorted((enq, proc, i) for i, (enq, proc) in enumerate(tasks))
    ready = []                                         # (processingTime, index)
    result = []
    time = 0
    i = 0

    while i < len(all_tasks) or ready:
        while i < len(all_tasks) and all_tasks[i][0] <= time:   # sweep arrivals
            heapq.heappush(ready, (all_tasks[i][1], all_tasks[i][2]))
            i += 1

        if ready:
            proc, idx = heapq.heappop(ready)           # shortest waiting task
            time += proc
            result.append(idx)
        else:
            time = all_tasks[i][0]                     # idle: jump to next arrival
    return result
```

```rust
use std::cmp::Reverse;
use std::collections::BinaryHeap;

impl Solution {
    /// @param tasks tasks[i] = [enqueue_time, processing_time]
    /// @return      order of task indices executed
    pub fn get_order(tasks: Vec<Vec<i32>>) -> Vec<i32> {
        let mut all: Vec<(i64, i64, usize)> = tasks
            .iter()
            .enumerate()
            .map(|(i, t)| (t[0] as i64, t[1] as i64, i))
            .collect();
        all.sort();                                    // gate: by arrival

        // ready: min-heap on (processingTime, index); Reverse flips the tuple
        let mut ready: BinaryHeap<Reverse<(i64, usize)>> = BinaryHeap::new();
        let mut result = Vec::new();
        let mut time = 0i64;
        let mut i = 0;

        while i < all.len() || !ready.is_empty() {
            while i < all.len() && all[i].0 <= time {  // sweep arrivals
                ready.push(Reverse((all[i].1, all[i].2)));
                i += 1;
            }

            if let Some(Reverse((proc, idx))) = ready.pop() {
                time += proc;                          // shortest waiting task
                result.push(idx as i32);
            } else {
                time = all[i].0;                       // idle: jump to next arrival
            }
        }
        result
    }
}
```

## Dry run

**Input:** `tasks = [[1,2],[2,4],[3,2],[4,1]]` (task 0: enq 1, proc 2; task 1: 2,4; task 2: 3,2; task 3: 4,1).

```
sorted by enqueueTime: [task0(1,2), task1(2,4), task2(3,2), task3(4,1)]
time=0, i=0, ready={}

loop 1: sweep: task0.enq 1 <= 0? no.  ready empty -> time = 1 (jump to next arrival)
loop 2: sweep: task0.enq 1 <= 1 -> ready={(2,0)}, i=1. task1.enq 2 <= 1? no.
        ready non-empty -> pop (2,0): time = 1+2 = 3, result=[0]
loop 3: sweep: task1.enq 2 <= 3 -> ready={(4,1)}, i=2. task2.enq 3 <= 3 -> ready={(2,2),(4,1)}, i=3.
        pop (2,2) [shortest]: time = 3+2 = 5, result=[0,2]
loop 4: sweep: task3.enq 4 <= 5 -> ready={(1,3),(4,1)}, i=4.
        pop (1,3): time = 5+1 = 6, result=[0,2,3]
loop 5: sweep: i=4 done.  pop (4,1): time = 6+4 = 10, result=[0,2,3,1]
loop 6: i=4, ready empty -> stop.

Output: [0,2,3,1] ✓
```

Watch loop 3: tasks 1 and 2 are *both* waiting at t=3, and the heap picks task 2 — the shorter one — even though task 1 arrived first. The `(processingTime, index)` key is what encodes the problem's priority rule; a FIFO queue would give the wrong order `[0,1,...]`.

## Complexity

**Time.** Each task is sorted once, pushed once, popped once:

$$
T(n) = O(n \log n)
$$

**Space.** The sorted list and the ready heap:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **IPO** ([7.5](ipo.md)) and **Meeting Rooms III** ([7.6](meeting-rooms-iii.md)) — the same event-driven two-list loop with different gates and rankings; seeing all three side by side is the fastest way to internalize the shape.
- **CPU scheduling in the repo** (`src/main/kotlin/`) — `meeting-rooms` and `task` schedulers reuse this exact skeleton with swapped keys.
- **Interview follow-up:** "Why is the idle jump correct?" Between the moment the ready heap empties and the next `enqueueTime`, the CPU has nothing to do — no decision is being made, so no event matters. Jumping `time` to the next arrival skips only states where the answer can't change. That's what makes the loop $O(n)$ iterations instead of $O(\text{maxTime})$.
- **Interview follow-up:** "What changes if preemption is allowed?" The ready heap stays, but the running task must be re-inserted (or rescheduled) on every arrival — the classic "shortest remaining time first" variant. Mentioning that this page is the non-preemptive special case shows you know the family.
