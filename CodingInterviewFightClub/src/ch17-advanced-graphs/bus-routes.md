# 17.12 Bus Routes

> **Source:** [`src/main/kotlin/graph/BusRoutes.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/BusRoutes.kt)
> **Pattern:** stop→bus two-layer BFS · **Core page**

## The Problem

Given `routes[i]` (the stops bus `i` visits), the fewest **buses** to ride from `source` to `target` (stops are shared between buses).

- Constraints: $1 \le$ stops; up to 10⁵ stops.

## Examples

```
Input:  routes = [[1,2,7],[3,6,7]], source = 1, target = 6
Output: 2   (bus 0 to stop 7, then bus 1 to stop 6)
```

## Intuition — the graph's nodes are *buses*, not stops

Riding bus 0 then bus 1 = an edge between buses that share a stop. So build **stop → list of buses** passing through it, then BFS over buses:

```
graph: stop -> [buses through it]
visitedBuses = set; visitedStops = set
queue: (source, 0)
while queue not empty:
    (stop, count) = poll
    if stop == target: return count
    for bus in graph[stop]:
        if bus not visited:
            visitedBuses.add(bus)
            for stop in routes[bus]:
                if stop not visitedStops:
                    queue.offer((stop, count + 1))
                    visitedStops.add(stop)
return -1
```

**Why the two visited sets?** `visitedBuses` prevents re-riding a bus (infinite loop via shared stops); `visitedStops` prevents re-enqueueing a stop from another bus at a worse-or-equal count. The `(stop, busCount)` state is the [6.1](../ch06-graphs/word-ladder.md) BFS with a payload.

**Why is "fewest buses" not "fewest stops"?** The cost is per *bus boarded* — riding a bus through 10 stops costs 1. The BFS counts bus-boards, which is exactly the `busCount + 1` when moving to a new bus's stops.

## Approach 1 — BFS over stops (wrong cost)

Stop-to-stop BFS counts *stops*, not buses — the classic mis-modeling.

## Approach 2 — Stop→bus BFS (the repo's version, optimal)

```kotlin
import java.util.*

class BusRoutes {
    data class Node(val stop: Int, val busCount: Int)

    /**
     * @param routes bus -> stops
     * @param source start stop
     * @param target target stop
     * @return      fewest buses, or -1
     */
    fun numBusesToDestination(routes: Array<IntArray>, source: Int, target: Int): Int {
        if (source == target) return 0

        val graph = mutableMapOf<Int, MutableList<Int>>()   // stop -> buses through it

        for (bus in routes.indices) {
            for (stop in routes[bus]) {
                graph.getOrPut(stop) { mutableListOf() }.add(bus)
            }
        }

        val queue: Queue<Node> = LinkedList()
        val visitedBuses = mutableSetOf<Int>()
        val visitedStops = mutableSetOf<Int>()

        queue.offer(Node(source, 0))
        visitedStops.add(source)

        while (queue.isNotEmpty()) {
            val (currentStop, busCount) = queue.poll()

            if (currentStop == target) return busCount

            graph[currentStop]?.let { buses ->
                for (bus in buses) {
                    if (bus !in visitedBuses) {
                        visitedBuses.add(bus)

                        for (stop in routes[bus]) {
                            if (stop !in visitedStops) {
                                queue.offer(Node(stop, busCount + 1))
                                visitedStops.add(stop)
                            }
                        }
                    }
                }
            }
        }
        return -1
    }
}
```

```java
import java.util.*;

public class BusRoutes {
    /**
     * @param routes bus -> stops
     * @param source start stop
     * @param target target stop
     * @return      fewest buses, or -1
     */
    public int numBusesToDestination(int[][] routes, int source, int target) {
        if (source == target) return 0;

        Map<Integer, List<Integer>> graph = new HashMap<>();   // stop -> buses through it
        for (int bus = 0; bus < routes.length; bus++) {
            for (int stop : routes[bus]) {
                graph.computeIfAbsent(stop, k -> new ArrayList<>()).add(bus);
            }
        }

        Queue<int[]> queue = new LinkedList<>();               // {stop, busCount}
        Set<Integer> visitedBuses = new HashSet<>();
        Set<Integer> visitedStops = new HashSet<>();
        queue.offer(new int[]{source, 0});
        visitedStops.add(source);

        while (!queue.isEmpty()) {
            int[] top = queue.poll();
            int stop = top[0], count = top[1];

            if (stop == target) return count;

            for (int bus : graph.getOrDefault(stop, List.of())) {
                if (visitedBuses.add(bus)) {                   // first ride of this bus
                    for (int s : routes[bus]) {
                        if (visitedStops.add(s)) {
                            queue.offer(new int[]{s, count + 1});
                        }
                    }
                }
            }
        }
        return -1;
    }
}
```

```cpp
#include <queue>
#include <unordered_map>
#include <unordered_set>
#include <vector>

class BusRoutes {
public:
    /**
     * @param routes bus -> stops
     * @param source start stop
     * @param target target stop
     * @return      fewest buses, or -1
     */
    int numBusesToDestination(std::vector<std::vector<int>>& routes, int source, int target) {
        if (source == target) return 0;

        std::unordered_map<int, std::vector<int>> graph;      // stop -> buses through it
        for (int bus = 0; bus < (int)routes.size(); bus++) {
            for (int stop : routes[bus]) graph[stop].push_back(bus);
        }

        std::queue<std::pair<int, int>> queue;                // {stop, busCount}
        std::unordered_set<int> visitedBuses, visitedStops;
        queue.push({source, 0});
        visitedStops.insert(source);

        while (!queue.empty()) {
            auto [stop, count] = queue.front(); queue.pop();

            if (stop == target) return count;

            for (int bus : graph[stop]) {
                if (visitedBuses.insert(bus).second) {        // first ride of this bus
                    for (int s : routes[bus]) {
                        if (visitedStops.insert(s).second) {
                            queue.push({s, count + 1});
                        }
                    }
                }
            }
        }
        return -1;
    }
};
```

```python
from collections import deque

def num_buses_to_destination(routes: list[list[int]], source: int, target: int) -> int:
    """
    @param routes: bus -> stops
    @param source: start stop
    @param target: target stop
    @return:       fewest buses, or -1
    """
    if source == target:
        return 0

    graph = {}                                   # stop -> buses through it
    for bus, stops in enumerate(routes):
        for stop in stops:
            graph.setdefault(stop, []).append(bus)

    queue = deque([(source, 0)])
    visited_buses = set()
    visited_stops = {source}

    while queue:
        stop, count = queue.popleft()

        if stop == target:
            return count

        for bus in graph.get(stop, []):
            if bus not in visited_buses:
                visited_buses.add(bus)
                for s in routes[bus]:
                    if s not in visited_stops:
                        visited_stops.add(s)
                        queue.append((s, count + 1))

    return -1
```

```rust
use std::collections::{HashMap, HashSet, VecDeque};

impl Solution {
    /// @param routes bus -> stops
    /// @param source start stop
    /// @param target target stop
    /// @return      fewest buses, or -1
    pub fn num_buses_to_destination(routes: Vec<Vec<i32>>, source: i32, target: i32) -> i32 {
        if source == target { return 0; }

        let mut graph: HashMap<i32, Vec<usize>> = HashMap::new();   // stop -> buses
        for (bus, stops) in routes.iter().enumerate() {
            for &stop in stops { graph.entry(stop).or_default().push(bus); }
        }

        let mut queue = VecDeque::new();
        let mut visited_buses = HashSet::new();
        let mut visited_stops = HashSet::new();
        queue.push_back((source, 0));
        visited_stops.insert(source);

        while let Some((stop, count)) = queue.pop_front() {
            if stop == target { return count; }

            if let Some(buses) = graph.get(&stop) {
                for &bus in buses {
                    if visited_buses.insert(bus) {                  // first ride of this bus
                        for &s in &routes[bus] {
                            if visited_stops.insert(s) {
                                queue.push_back((s, count + 1));
                            }
                        }
                    }
                }
            }
        }
        -1
    }
}
```

## Dry run

**Input:** `routes = [[1,2,7],[3,6,7]]`, `source = 1`, `target = 6`.

```
graph: 1->[0], 2->[0], 7->[0,1], 3->[1], 6->[1]

queue=[(1,0)].  visitedStops={1}, visitedBuses={}
poll (1,0): not target.  buses at 1: [0].  bus 0 new -> visitedBuses={0}.
   stops of bus 0: 1(seen), 2(new) -> enqueue (2,1); 7(new) -> enqueue (7,1).  visitedStops={1,2,7}
poll (2,1): buses at 2: [0] visited -> skip.
poll (7,1): buses at 7: [0] visited; [1] new -> visitedBuses={0,1}.
   stops of bus 1: 3(new) -> (3,2); 6(new) -> (6,2); 7(seen).  visitedStops={1,2,7,3,6}
poll (3,2): buses [1] visited.
poll (6,2): == target -> return 2 ✓
```

The two layers are explicit: stop 7 is the *transfer* where bus 0's stops end and bus 1's stops begin — the BFS hops buses there (`busCount` 1 → 2). Re-enqueueing stop 7 from bus 1 is prevented by `visitedStops`, and re-riding bus 0 by `visitedBuses` — both guards are needed for the O(stops + buses) bound.

## Complexity

**Time.** Each bus and each stop processed once:

$$
T(B, S) = O(B \cdot S)
$$

**Space.** Graph + queues:

$$
S(B, S) = O(B \cdot S)
$$

## Variants & follow-ups

- **Word Ladder** ([6.1](../ch06-graphs/word-ladder.md)) — the same "layer" BFS with adjacency-lists over words.
- **Minimum Genetic Mutations** ([17.13](minimum-genetic-mutations.md)) — the compact sibling: 4-neighbor generation instead of route tables.
- **Interview follow-up:** "Why are the graph's nodes buses and not stops?" The cost unit is *buses boarded* — an edge exists between any two buses sharing a stop, and riding a bus through its stops is free. Modeling stops as nodes counts stops (wrong); modeling buses as nodes counts boards (right). Naming the cost unit is the whole problem.
