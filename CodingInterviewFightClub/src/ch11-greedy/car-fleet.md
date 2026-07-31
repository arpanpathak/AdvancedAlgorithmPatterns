# 11.5 Car Fleet

> **Source:** [`src/main/kotlin/greedy/CarFleet.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/CarFleet.kt)
> **Pattern:** sort by position + ETA sweep · **Core page**

## The Problem

`n` cars drive toward `target` at `position[i]` (all distinct) with `speed[i]`. A car **never passes** another — it catches up and forms a **fleet** that moves at the slower car's speed. Return the number of fleets that arrive at `target`.

- Constraints: $1 \le n \le 10^5$; `0 <= position[i] < target <= 10^6`.

## Examples

```
Input:  target = 12, position = [10,8,0,5,3], speed = [2,4,1,1,3]
Output: 3   (fleets: {10}, {8,5,3} at speed 1, {0} — see trace)

Input:  target = 10, position = [3], speed = [3]
Output: 1
```

## Intuition — "who catches whom" is decided by *arrival times*

Each car, alone, would reach the target at time `ETA(i) = (target - position[i]) / speed[i]` (the repo's `t = s / v` comment). A faster car *behind* a slower car will catch it before the target **iff its ETA is smaller** — and once caught, both arrive together at the *slower* car's ETA. So:

- sort cars by **position descending** (closest to target first);
- walk that order, tracking the **slowest ETA seen so far** (the fleet leader's arrival time);
- each car with ETA **larger** than the current leader's ETA starts a **new fleet** (it can't catch the fleet ahead — it would arrive later even alone);
- a car with ETA **smaller or equal** merges into the fleet ahead (it catches it — same fleet, one count).

The answer is the number of times the running "slowest ETA" increases. **A car is a fleet leader iff its ETA is greater than every car ahead of it** — the greedy sweep counts exactly those records.

**Why sort by position, not ETA?** A car can only merge with the fleet *in front of it*. Position order defines "in front"; the ETA comparison decides "merge or not". The fleet structure is positional — hence the sort key.

**Floating-point equality is safe here** because the merge condition is `>` (strictly later = new fleet); a car with equal ETA arrives at the same moment, so it merges. No epsilon needed.

## Approach 1 — Simulate all cars pairwise (too slow)

For each pair, compute catch-up time and simulate merges: $O(n^2)$.

## Approach 2 — Sort + ETA sweep (the repo's version, optimal)

```kotlin
class CarFleet {
    data class Car(val position: Double, val eta: Double)

    /**
     * @param target   destination distance
     * @param position position[i] of car i
     * @param speed    speed[i] of car i
     * @return         number of fleets reaching the target
     */
    fun carFleet(target: Int, position: IntArray, speed: IntArray): Int {
        var (fleets, n) = listOf(0, position.size)
        val cars = mutableListOf<Car>()

        // t = s / v  (time to cover the remaining distance)
        position.forEachIndexed { i, pos ->
            cars.add(Car(pos.toDouble(), (target - pos).toDouble() / speed[i].toDouble()))
        }

        // Sort by position descending: the car closest to target leads its fleet.
        cars.sortBy { -it.position }

        var currentSlowestEta = 0.0
        cars.forEach { car ->
            // ETA larger than the current fleet leader -> catches nothing: new fleet
            if (car.eta > currentSlowestEta) {
                fleets++
                currentSlowestEta = car.eta
            }
        }
        return fleets
    }
}
```

```java
import java.util.*;

public class CarFleet {
    /**
     * @param target   destination distance
     * @param position position[i] of car i
     * @param speed    speed[i] of car i
     * @return         number of fleets reaching the target
     */
    public int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;
        double[][] cars = new double[n][2];       // {position, time to reach target}
        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            cars[i][1] = (double) (target - position[i]) / speed[i];
        }
        Arrays.sort(cars, (a, b) -> Double.compare(b[0], a[0]));   // position descending

        int fleets = 0;
        double slowest = 0;
        for (double[] car : cars) {
            if (car[1] > slowest) {               // later than the fleet ahead: new fleet
                fleets++;
                slowest = car[1];
            }
        }
        return fleets;
    }
}
```

```cpp
#include <algorithm>
#include <vector>

class CarFleet {
public:
    /**
     * @param target   destination distance
     * @param position position[i] of car i
     * @param speed    speed[i] of car i
     * @return         number of fleets reaching the target
     */
    int carFleet(int target, std::vector<int>& position, std::vector<int>& speed) {
        int n = position.size();
        std::vector<std::pair<int, double>> cars;        // {position, time}
        for (int i = 0; i < n; i++) {
            cars.push_back({position[i], (double)(target - position[i]) / speed[i]});
        }
        std::sort(cars.begin(), cars.end(),              // position descending
                  [](const auto& a, const auto& b) { return a.first > b.first; });

        int fleets = 0;
        double slowest = 0;
        for (auto& [_, eta] : cars) {
            if (eta > slowest) {                         // later than the fleet ahead: new fleet
                fleets++;
                slowest = eta;
            }
        }
        return fleets;
    }
};
```

```python
def car_fleet(target: int, position: list[int], speed: list[int]) -> int:
    """
    @param target:   destination distance
    @param position: position[i] of car i
    @param speed:    speed[i] of car i
    @return:         number of fleets reaching the target
    """
    cars = sorted(zip(position, speed), reverse=True)    # position descending
    fleets = 0
    slowest = 0.0

    for pos, spd in cars:
        eta = (target - pos) / spd
        if eta > slowest:                                # later than the fleet ahead: new fleet
            fleets += 1
            slowest = eta
    return fleets
```

```rust
impl Solution {
    /// @param target   destination distance
    /// @param position position[i] of car i
    /// @param speed    speed[i] of car i
    /// @return         number of fleets reaching the target
    pub fn car_fleet(target: i32, position: Vec<i32>, speed: Vec<i32>) -> i32 {
        let mut cars: Vec<(i32, f64)> = position.iter().zip(speed.iter())
            .map(|(&p, &s)| (p, (target - p) as f64 / s as f64))
            .collect();
        cars.sort_by(|a, b| b.0.cmp(&a.0));              // position descending

        let mut fleets = 0;
        let mut slowest = 0.0f64;
        for (_, eta) in cars {
            if eta > slowest {                           // later than the fleet ahead: new fleet
                fleets += 1;
                slowest = eta;
            }
        }
        fleets
    }
}
```

## Dry run

**Input:** `target = 12`, `position = [10,8,0,5,3]`, `speed = [2,4,1,1,3]`.

```
ETAs (12 - pos) / speed:  car@10: 2/2=1, car@8: 4/4=1, car@5: 7/1=7, car@3: 9/3=3, car@0: 12/1=12

cars sorted by position descending: (10,1), (8,1), (5,7), (3,3), (0,12)

fleets=0, slowest=0
(10,1):  1 > 0  -> fleet!  fleets=1, slowest=1
(8,1):   1 > 1? no -> merges into the fleet ahead (same arrival time 1).   fleets=1
(5,7):   7 > 1  -> fleet!  fleets=2, slowest=7
(3,3):   3 > 7? no -> catches the (5) fleet, arriving at 7 together.       fleets=2
(0,12):  12 > 7 -> fleet!  fleets=3, slowest=12

Output: 3 ✓
```

The two merge lines are the physical intuition: car@8 catches car@10 *immediately* (same ETA), and car@3 is slower than the fleet at 5 — it catches *it* (moving at the fleet's slower speed), not the other way around. A car becomes a leader only when it's faster than everything ahead — which is exactly the "new record in the ETA sweep" condition.

## Complexity

**Time.** Sort dominates:

$$
T(n) = O(n \log n)
$$

**Space.** The car list:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Car Fleet II** — *collision times* (when fleets form) instead of arrival counts: a monotonic stack over ETAs, the [Chapter 8](../ch08-stacks/index.md) engine wearing a physics costume.
- **Maximum Profit Assigning Work** (`src/main/kotlin/greedy/MaxProfiAssigningWork.kt`) — the same "sort two axes, sweep one" shape.
- **Interview follow-up:** "Why is a car's own speed irrelevant once it merges?" The fleet moves at the *slowest* member's speed — the leader's ETA — so after the merge decision, the faster car's speed is never consulted again. That's why the sweep only tracks `slowest` (the fleet leader's ETA), not every car's.
