# 7.5 IPO (Maximize Capital)

> **Source:** [`src/main/kotlin/heap/IPO.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/heap/IPO.kt)
> **Pattern:** greedy + max-profit heap · **Core page**

## The Problem

You start with `w` capital and can do at most `k` projects. Project `i` needs `capital[i]` to start and yields `profit[i]`. You may do projects in any order (each once); doing one adds its profit to your capital. Return the maximum capital after at most `k` projects.

- Constraints: $1 \le k \le 10^5$; $0 \le w \le 10^9$; $n \le 10^5$.

## Examples

```
Input:  k = 2, w = 0, profits = [1,2,3], capital = [0,1,1]
Output: 4   (project 0 needs 0 -> capital 1; project 1 or 2 needs 1 -> capital 3 or 4; max = 4)

Input:  k = 3, w = 0, profits = [1,2,3], capital = [0,1,2]
Output: 6   (0 -> 1 -> 2: capital 0 -> 1 -> 3 -> 6)
```

## Intuition — "affordability is a gate, profit is a ranking"

At every step you may start **any project you can afford** — so the decision splits cleanly in two:

1. **Which projects are affordable?** A project becomes available the moment `capital <= currentCapital`. Since `currentCapital` only grows, the affordable set only *grows* — sort projects by capital and slide a pointer forward as capital increases. Each project crosses the gate exactly once.
2. **Among affordable ones, which is best?** The most *profitable*. A **max-heap of profits** of all currently-affordable projects gives it in $O(\log n)$.

The loop: for each of `k` rounds — pour every newly-affordable project into the profit max-heap, then take the heap's root. If the heap is empty, no project is affordable and the remaining rounds are useless — stop.

**Why greedy and why is it optimal?** The decision "do the most profitable affordable project now" looks local, but it's globally optimal: doing *any other* affordable project would leave you with no more capital and remove a project from the pool — it can never unlock more future projects than the most-profitable choice, and its profit is no larger. (A formal exchange argument: swap any optimal solution's first project with the max-profit affordable one — the result is still valid and no worse.)

**The two-structure split is the pattern** — sort handles *feasibility* (a static gate), heap handles *priority* (a dynamic ranking). Whenever a problem says "pick the best among those that qualify, where qualifying grows over time", this is the shape.

## Approach 1 — Sort by profit, check affordability (wrong!)

Doing the most profitable project *overall* first is tempting — but the most profitable may be unaffordable until you earn elsewhere. A pure profit sort fails whenever cheap-but-low-profit projects are prerequisites for big ones. The capital gate must come first.

## Approach 2 — Capital-sorted pointer + profit max-heap (the repo's version, optimal)

```kotlin
import java.util.*

class IPO {
    data class Project(val capital: Int, val profit: Int)

    /**
     * @param k       max number of projects
     * @param w       starting capital
     * @param profits profit[i] for project i
     * @param capital capital[i] required by project i
     * @return        maximum capital after at most k projects
     */
    fun findMaximizedCapital(k: Int, w: Int, profits: IntArray, capital: IntArray): Int {
        val projects = capital.indices
            .map { Project(capital[it], profits[it]) }
            .sortedBy { it.capital }                     // gate: sorted by affordability

        val maxHeap = PriorityQueue<Int>(compareByDescending { it })   // ranking: by profit

        var currentCapital = w
        var i = 0

        repeat(k) {
            // Pour every newly-affordable project into the heap
            while (i < projects.size && projects[i].capital <= currentCapital)
                maxHeap.offer(projects[i++].profit)

            // Take the most profitable affordable one
            maxHeap.poll()?.let { currentCapital += it } ?: return currentCapital
        }
        return currentCapital
    }
}
```

```java
import java.util.*;

public class IPO {
    /**
     * @param k       max number of projects
     * @param w       starting capital
     * @param profits profit[i] for project i
     * @param capital capital[i] required by project i
     * @return        maximum capital after at most k projects
     */
    public int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        int n = profits.length;
        int[][] projects = new int[n][2];               // {capital, profit}
        for (int i = 0; i < n; i++) { projects[i][0] = capital[i]; projects[i][1] = profits[i]; }
        Arrays.sort(projects, Comparator.comparingInt(p -> p[0]));   // gate: by capital

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        int current = w, i = 0;

        while (k-- > 0) {
            while (i < n && projects[i][0] <= current) {
                maxHeap.offer(projects[i++][1]);        // newly affordable -> heap
            }
            if (maxHeap.isEmpty()) break;               // nothing affordable: stop
            current += maxHeap.poll();                  // most profitable affordable
        }
        return current;
    }
}
```

```cpp
#include <algorithm>
#include <functional>
#include <queue>
#include <vector>

class IPO {
public:
    /**
     * @param k       max number of projects
     * @param w       starting capital
     * @param profits profit[i] for project i
     * @param capital capital[i] required by project i
     * @return        maximum capital after at most k projects
     */
    int findMaximizedCapital(int k, int w, std::vector<int>& profits, std::vector<int>& capital) {
        int n = profits.size();
        std::vector<std::pair<int,int>> projects;       // {capital, profit}
        for (int i = 0; i < n; i++) projects.push_back({capital[i], profits[i]});
        std::sort(projects.begin(), projects.end());    // gate: by capital

        std::priority_queue<int> maxHeap;               // ranking: by profit
        int current = w, i = 0;

        while (k-- > 0) {
            while (i < n && projects[i].first <= current) {
                maxHeap.push(projects[i++].second);     // newly affordable -> heap
            }
            if (maxHeap.empty()) break;                 // nothing affordable: stop
            current += maxHeap.top();
            maxHeap.pop();
        }
        return current;
    }
};
```

```python
import heapq

def find_maximized_capital(k: int, w: int, profits: list[int], capital: list[int]) -> int:
    """
    @param k:       max number of projects
    @param w:       starting capital
    @param profits: profit[i] for project i
    @param capital: capital[i] required by project i
    @return:        maximum capital after at most k projects
    """
    projects = sorted(zip(capital, profits))        # gate: sorted by capital
    max_heap = []                                   # ranking: by profit (negated)

    current = w
    i = 0
    for _ in range(k):
        while i < len(projects) and projects[i][0] <= current:
            heapq.heappush(max_heap, -projects[i][1])   # newly affordable -> heap
            i += 1
        if not max_heap:
            break                                   # nothing affordable: stop
        current += -heapq.heappop(max_heap)         # most profitable affordable
    return current
```

```rust
use std::cmp::Reverse;
use std::collections::BinaryHeap;

impl Solution {
    /// @param k       max number of projects
    /// @param w       starting capital
    /// @param profits profit[i] for project i
    /// @param capital capital[i] required by project i
    /// @return        maximum capital after at most k projects
    pub fn find_maximized_capital(k: i32, w: i32, profits: Vec<i32>, capital: Vec<i32>) -> i32 {
        let mut projects: Vec<(i32, i32)> = capital.into_iter().zip(profits).collect();
        projects.sort();                            // gate: by capital

        // BinaryHeap is a max-heap -> max profit on top
        let mut max_heap: BinaryHeap<i32> = BinaryHeap::new();
        let mut current = w;
        let mut i = 0;

        for _ in 0..k {
            while i < projects.len() && projects[i].0 <= current {
                max_heap.push(projects[i].1);       // newly affordable -> heap
                i += 1;
            }
            match max_heap.pop() {
                Some(profit) => current += profit,  // most profitable affordable
                None => break,                      // nothing affordable: stop
            }
        }
        current
    }
}
```

## Dry run

**Input:** `k = 2`, `w = 0`, `profits = [1,2,3]`, `capital = [0,1,1]`.

```
projects sorted by capital: [(0,1), (1,2), (1,3)]     (capital, profit)

Round 1 (k=1): current=0
  pour: projects[0].capital 0 <= 0 -> heap={1}, i=1
        projects[1].capital 1 <= 0? no
  take max profit 1 -> current = 0 + 1 = 1

Round 2 (k=2): current=1
  pour: projects[1].capital 1 <= 1 -> heap={2}, i=2
        projects[2].capital 1 <= 1 -> heap={2,3}, i=3
  take max profit 3 -> current = 1 + 3 = 4

k exhausted -> return 4 ✓
```

Notice the moment of the design: in round 2, *both* remaining projects became affordable the instant capital hit 1 — the heap then picked the better one (3 over 2) in $O(\log n)$. A profit-first sort would have started with project "3", but it needs capital 1 which did not exist yet — the gate-first order is what makes the heap's choice valid.

## Complexity

**Time.** Each project is sorted once and pushed/popped at most once:

$$
T(n, k) = O(n \log n + k \log n)
$$

**Space.** The sorted list plus the heap:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Single Threaded CPU** ([7.7](single-threaded-cpu.md)) — the exact same two-phase shape: events sorted by time (the gate), ready queue keyed on processing time (the ranking). Once you see it, it is the same problem wearing a clock.
- **Course Schedule III / task scheduling with deadlines** — "affordability" becomes "fits before the deadline"; the greedy swaps out the *worst* instead of taking the best.
- **Meeting Rooms III** ([7.6](meeting-rooms-iii.md)) — the twin structure: *availability* is the gate (a heap of free rooms), *urgency* is the ranking (a heap of busy rooms).
- **Interview follow-up:** "Why is a heap needed if projects are sorted?" The sorted order is static — it cannot know that new projects became affordable *after* a capital increase mid-loop. The heap is the dynamic "best among the currently unlocked" set; the pointer feeds it. Removing either half breaks the algorithm: without the sort you would scan everything each round ($O(kn)$); without the heap you would re-scan for the max each round.
