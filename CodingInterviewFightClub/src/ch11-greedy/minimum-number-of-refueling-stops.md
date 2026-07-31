# 11.7 Minimum Number Of Refueling Stops

> **Source:** [`src/main/kotlin/greedy/MinimumNumberOfRefuelingStops.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/MinimumNumberOfRefuelingStops.kt)
> **Pattern:** max-heap "time travel" · **Core page**

## The Problem

A car starts at position 0 with `startFuel` liters, and wants to reach `target`. There are `stations[i] = [position, fuel]` along the way. Each unit of distance consumes one liter. Return the **minimum number of refueling stops**, or `-1` if unreachable. (You may refuel at most once per station.)

- Constraints: $1 \le n \le 500$; `0 < startFuel, target <= 10^9`.

## Examples

```
Input:  target = 100, startFuel = 10, stations = [[10,60],[20,30],[30,30],[60,40]]
Output: 2    (stop at 10 (60) and 60 (40): 10 + 60 = 70 -> reaches 100)

Input:  target = 100, startFuel = 1, stations = [[10,100]]
Output: -1   (can't even reach the first station)
```

## Intuition — don't choose *when you pass*, choose *when you're stuck*

The tempting greedy — "always stop at the station with the most fuel" — is wrong: you might burn fuel to reach a distant giant tank while a nearby station would have sufficed. The right move is the **deferred decision** from the [primer](pattern-primer.md):

> **Never stop anywhere — but *remember* every station you pass.** When you run out of fuel, "time-travel" back to the most generous station you've passed and retroactively stop there.

Data structure: a **max-heap of fuel** from every station you've passed. The loop:

1. Drive as far as `currentFuel` allows, pouring every reachable station's fuel into the heap (you *could* have stopped there).
2. If the car runs out before the target: if the heap is empty, no station can save you → `-1`. Otherwise pop the *largest* fuel — retroactively refuel at that station — `stops++`, keep driving.
3. Repeat until `currentFuel >= target`.

**Why is "largest fuel when stuck" optimal?** Whenever the car stalls, it must have stopped at *some* previously-passed station; choosing the one with the most fuel maximizes the distance gained per stop — and since all passed stations are equally "reachable" (they were within the fuel we had), the choice among them is pure fuel size. This is a staying-ahead argument: greedy's fuel after `k` stops is ≥ any other strategy's fuel after `k` stops, so greedy minimizes stops.

**Why does the heap "time travel" work?** Deferring the decision doesn't change feasibility: any station you pass *with the fuel you eventually have* is a station you could have stopped at — the heap records exactly the set of stations reachable by *some* prefix of stops, and popping in fuel order is the optimal ordering of those stops. The repo's comments call it "time travel" — the decision is made at the moment of *need*, not the moment of *passing*.

## Approach 1 — DP over stations (O(n^2))

`dp[i] = max fuel after stopping at station i` with `dp[j] + fuel` transitions: correct, quadratic, and it computes way more than "just the count" needs.

## Approach 2 — Max-heap deferred decisions (the repo's version, optimal)

```kotlin
import java.util.*

/**
 * @param target      destination distance
 * @param startFuel   initial fuel
 * @param stations    stations[i] = [position, fuel]
 * @return            minimum refueling stops, or -1 if unreachable
 */
fun minRefuelStops(target: Int, startFuel: Int, stations: Array<IntArray>): Int {
    // Step 1: Max-Heap stores the "best decisions we could have made"
    val maxHeap = PriorityQueue<Int>(compareByDescending { it })

    var currentFuel = startFuel
    var stops = 0
    var i = 0
    val n = stations.size

    // Step 2: Continuous simulation
    while (currentFuel < target) {
        // Add all stations reachable with the fuel we've already "committed"
        while (i < n && stations[i][0] <= currentFuel) {
            maxHeap.offer(stations[i][1])
            i++
        }

        // If we run out of fuel and have no more "backtrack" options
        if (maxHeap.isEmpty()) return -1

        // "Time Travel": Refuel at the best station we passed but didn't stop at
        currentFuel += maxHeap.poll()
        stops++
    }
    return stops
}
```

```java
import java.util.*;

public class MinimumNumberOfRefuelingStops {
    /**
     * @param target      destination distance
     * @param startFuel   initial fuel
     * @param stations    stations[i] = [position, fuel]
     * @return            minimum refueling stops, or -1 if unreachable
     */
    public int minRefuelStops(int target, int startFuel, int[][] stations) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

        int fuel = startFuel, stops = 0, i = 0, n = stations.length;

        while (fuel < target) {
            while (i < n && stations[i][0] <= fuel) {   // reachable stations join the heap
                maxHeap.offer(stations[i][1]);
                i++;
            }
            if (maxHeap.isEmpty()) return -1;           // stuck with no saved options

            fuel += maxHeap.poll();                     // retroactively take the best station
            stops++;
        }
        return stops;
    }
}
```

```cpp
#include <functional>
#include <queue>
#include <vector>

class MinimumNumberOfRefuelingStops {
public:
    /**
     * @param target      destination distance
     * @param startFuel   initial fuel
     * @param stations    stations[i] = [position, fuel]
     * @return            minimum refueling stops, or -1 if unreachable
     */
    int minRefuelStops(int target, int startFuel, std::vector<std::vector<int>>& stations) {
        std::priority_queue<int> maxHeap;               // fuels of reachable stations

        int fuel = startFuel, stops = 0, i = 0, n = stations.size();

        while (fuel < target) {
            while (i < n && stations[i][0] <= fuel) {   // reachable stations join the heap
                maxHeap.push(stations[i][1]);
                i++;
            }
            if (maxHeap.empty()) return -1;             // stuck with no saved options

            fuel += maxHeap.top(); maxHeap.pop();       // retroactively take the best station
            stops++;
        }
        return stops;
    }
};
```

```python
import heapq

def min_refuel_stops(target: int, start_fuel: int, stations: list[list[int]]) -> int:
    """
    @param target:      destination distance
    @param start_fuel:  initial fuel
    @param stations:    stations[i] = [position, fuel]
    @return:            minimum refueling stops, or -1 if unreachable
    """
    max_heap = []                      # fuels of reachable stations (negated for max)
    fuel = start_fuel
    stops = 0
    i = 0

    while fuel < target:
        while i < len(stations) and stations[i][0] <= fuel:   # reachable stations join the heap
            heapq.heappush(max_heap, -stations[i][1])
            i += 1
        if not max_heap:
            return -1                  # stuck with no saved options

        fuel += -heapq.heappop(max_heap)    # retroactively take the best station
        stops += 1
    return stops
```

```rust
use std::cmp::Reverse;
use std::collections::BinaryHeap;

impl Solution {
    /// @param target      destination distance
    /// @param start_fuel  initial fuel
    /// @param stations    stations[i] = [position, fuel]
    /// @return            minimum refueling stops, or -1 if unreachable
    pub fn min_refuel_stops(target: i32, start_fuel: i32, stations: Vec<Vec<i32>>) -> i32 {
        // BinaryHeap is a max-heap; Reverse flips it to a min-heap on (position, -fuel)
        let mut heap: BinaryHeap<Reverse<(i32, i32)>> = BinaryHeap::new();
        let mut fuel = start_fuel;
        let mut stops = 0;
        let mut i = 0;

        while fuel < target {
            while i < stations.len() && stations[i][0] <= fuel {
                heap.push(Reverse((stations[i][1], stations[i][0])));   // key on fuel
                i += 1;
            }
            let Some(Reverse((best_fuel, _))) = heap.pop() else { return -1; };
            fuel += best_fuel;           // retroactively take the best station
            stops += 1;
        }
        stops
    }
}
```

## Dry run

**Input:** `target = 100`, `startFuel = 10`, `stations = [[10,60],[20,30],[30,30],[60,40]]`.

```
fuel=10, heap=[], stops=0

fuel < 100:
  stations with pos <= 10: [10,60] -> heap={60}, i=1
  heap non-empty -> pop 60.  fuel = 10+60 = 70, stops=1
fuel=70 < 100:
  stations with pos <= 70: [20,30], [30,30], [60,40] -> heap={40,30,30}, i=4
  pop 40 -> fuel = 70+40 = 110, stops=2
fuel=110 >= 100 -> return 2 ✓
```

The "time travel" is visible at the second stall: the car *passed* stations 20, 30, and 60 without stopping, and the moment it runs low it retroactively picks the *best* of them (40). A naive "stop at the first reachable station" greedy would have stopped at 20 (30 fuel) and needed a third stop.

## Complexity

**Time.** Each station pushed and popped at most once:

$$
T(n) = O(n \log n)
$$

**Space.** The heap:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Jump Game II** ([11.2](jump-game-ii.md)) — the same "how far with k resources" skeleton, where every jump's reach is *known up front* (no choice) — so the frontier is plain greedy. This page is its "choose where to spend" upgrade.
- **Maximum Number Of Refueling Stops / gas-station circuits** — the circular variant; the "deferred choice" heap is replaced by a running deficit argument.
- **IPO** ([7.5](../ch07-heaps/ipo.md)) — the exact same "affordable = reachable, best = heap" shape with capital instead of fuel: the gate is affordability, the ranking is profit. Seeing the two pages side by side is the fastest way to internalize the pattern.
- **Interview follow-up:** "Why is deferring the decision safe?" Because "I passed station X" is *monotone* — once reachable, it stays reachable with any future fuel. The heap's membership never shrinks, so choosing later can only add options, never remove them. That monotonicity is what turns the decision into a pure "pick the best when forced" loop.
