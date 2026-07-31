# 11.21 Car Pooling

> **Source**: [`src/main/kotlin/simulation/CarPooling.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/simulation/CarPooling.kt)
> **Pattern**: sweep-line occupancy · **Core page**

## The Problem

Can a car with `capacity` seats serve all trips (`[passengers, from, to]`)?

- Constraints: trips ≤ 1000; locations ≤ 1000.

## Examples

```
Input:  trips = [[2,1,5],[3,3,7]], capacity = 4   -> Output: false (peak 5 at mile 3)
Input:  trips = [[2,1,5],[3,3,7]], capacity = 5   -> Output: true
```

## Intuition — a difference array over locations; the prefix is the occupancy

`locations[from] += passengers` (board), `locations[to] -= passengers` (alight). The running prefix at each mile is the simultaneous passengers:

```kotlin
val locations = IntArray(1001)
for ((passengers, from, to) in trips) {
    locations[from] += passengers
    locations[to] -= passengers
}

for (p in locations) {
    capacity -= p
    if (capacity < 0) return false
}
return true
```

**Why the difference array?** Boarding/alighting are point events — the [3.32](../ch03-arrays/zero-array-transformation.md) sweep-line: prefix sums materialize the occupancy curve; any dip below 0 fails.

## Approach 1 — Sort events and sweep (O(t log t))

Event list (from +p, to −p) sorted by mile: equally valid.

## Approach 2 — Difference array (the repo's version, optimal)

```kotlin
class CarPooling {
    /**
     * @param trips    [passengers, from, to]
     * @param capacity seat count
     * @return         true iff all trips fit
     */
    fun carPooling(trips: Array<IntArray>, capacity: Int): Boolean {
        var capacity = capacity
        val locations = IntArray(1001)

        for (trip in trips) {
            val (passengers, from, to) = trip
            locations[from] += passengers
            locations[to] -= passengers
        }

        for (p in locations) {
            capacity -= p
            if (capacity < 0) return false
        }
        return true
    }
}
```

```java
public class CarPooling {
    /**
     * @param trips    [passengers, from, to]
     * @param capacity seat count
     * @return         true iff all trips fit
     */
    public boolean carPooling(int[][] trips, int capacity) {
        int[] diff = new int[1001];

        for (int[] trip : trips) {
            diff[trip[1]] += trip[0];
            diff[trip[2]] -= trip[0];
        }

        int onBoard = 0;
        for (int p : diff) {
            onBoard += p;
            if (onBoard > capacity) return false;
        }
        return true;
    }
}
```

```cpp
#include <vector>

class CarPooling {
public:
    /**
     * @param trips    [passengers, from, to]
     * @param capacity seat count
     * @return         true iff all trips fit
     */
    bool carPooling(std::vector<std::vector<int>>& trips, int capacity) {
        std::vector<int> diff(1001, 0);

        for (auto& trip : trips) {
            diff[trip[1]] += trip[0];
            diff[trip[2]] -= trip[0];
        }

        int onBoard = 0;
        for (int p : diff) {
            onBoard += p;
            if (onBoard > capacity) return false;
        }
        return true;
    }
};
```

```python
def car_pooling(trips: list[list[int]], capacity: int) -> bool:
    """
    @param trips:    [passengers, from, to]
    @param capacity: seat count
    @return:         true iff all trips fit
    """
    diff = [0] * 1001

    for passengers, fr, to in trips:
        diff[fr] += passengers
        diff[to] -= passengers

    on_board = 0
    for p in diff:
        on_board += p
        if on_board > capacity:
            return False
    return True
```

```rust
impl Solution {
    /// @param trips    [passengers, from, to]
    /// @param capacity seat count
    /// @return         true iff all trips fit
    pub fn car_pooling(trips: Vec<Vec<i32>>, capacity: i32) -> bool {
        let mut diff = vec![0; 1001];

        for trip in &trips {
            diff[trip[1] as usize] += trip[0];
            diff[trip[2] as usize] -= trip[0];
        }

        let mut on_board = 0;
        for p in diff {
            on_board += p;
            if on_board > capacity { return false; }
        }
        true
    }
}
```

## Dry run

**Input:** `trips = [[2,1,5],[3,3,7]], capacity = 4`.

```
diff: [1]+=2, [5]-=2, [3]+=3, [7]-=3.
prefix: mile 1: 2.  2: 2.  3: 5 > 4 -> false ✓
```

## Complexity

**Time.** Trips + 1001:

$$
T = O(t + L)
$$

**Space.** The diff array:

$$
S = O(L)
$$

## Variants & follow-ups

- **Zero Array Transformation** ([3.32](../ch03-arrays/zero-array-transformation.md)) — the identical diff-array machinery.
- **Interview follow-up:** "Why does alighting at `to` (not `to+1`) matter?" Passengers leave *at* the destination mile — `diff[to] -= passengers` makes the occupancy drop exactly there, matching the "peak occupancy" definition.
