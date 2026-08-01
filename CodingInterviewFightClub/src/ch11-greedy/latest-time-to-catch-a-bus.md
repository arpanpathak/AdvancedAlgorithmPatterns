# 11.34 Latest Time To Catch A Bus

> **Source**: [`src/main/kotlin/simulation/LatestTimeToCatchBus.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/simulation/LatestTimeToCatchBus.kt)
> **Pattern**: greedy passenger fitting · **Core page**

## The Problem

The latest time to arrive at a stop to catch a bus (must not equal an existing passenger's time; each bus holds `capacity`).

- Constraints: buses ≤ 10⁵.

## Examples

```
Input:  buses = [10,20], passengers = [2,17,18,19], capacity = 2
Output: 16
```

## Intuition — fit passengers greedily; the last slot's predecessor decides

```kotlin
busses.sort()
passengers.sort()

var lastPassengerIdx = 0
var lastBusPassengerCount = 0

busses.forEach { busTime ->
    lastBusPassengerCount = 0

    while (lastPassengerIdx < passengers.size &&
           lastBusPassengerCount < capacity &&
           passengers[lastPassengerIdx] <= busTime) {
        lastPassengerIdx++
        lastBusPassengerCount++
    }
}

// latest time = last bus's time (or the passenger before the last slot), minus 1 if taken
```

## Approach 1 — Greedy fit + back-off (the repo's version)

```kotlin
class LatestTimeToCatchBus {
    /**
     * @param buses      bus times
     * @param passengers passenger times
     * @param capacity   seats per bus
     * @return           latest catch time
     */
    fun latestTimeCatchTheBus(buses: IntArray, passengers: IntArray, capacity: Int): Int {
        buses.sort()
        passengers.sort()

        var lastPassengerIdx = 0
        var lastBusPassengerCount = 0

        for (busTime in buses) {
            lastBusPassengerCount = 0

            while (lastPassengerIdx < passengers.size &&
                   lastBusPassengerCount < capacity &&
                   passengers[lastPassengerIdx] <= busTime) {
                lastPassengerIdx++
                lastBusPassengerCount++
            }
        }

        // the last bus's last slot
        var latestTime = if (lastBusPassengerCount < capacity) {
            buses.last()
        } else {
            passengers[lastPassengerIdx - 1] - 1
        }

        // avoid collision with existing passengers
        var idx = lastPassengerIdx - 1
        while (idx >= 0 && passengers[idx] == latestTime) {
            latestTime--
            idx--
        }

        return latestTime
    }
}
```

```java
import java.util.*;

public class LatestTimeToCatchABus {
    /**
     * @param buses      bus times
     * @param passengers passenger times
     * @param capacity   seats per bus
     * @return           latest catch time
     */
    public int latestTimeCatchTheBus(int[] buses, int[] passengers, int capacity) {
        Arrays.sort(buses);
        Arrays.sort(passengers);

        int idx = 0, count = 0;

        for (int bus : buses) {
            count = 0;

            while (idx < passengers.length && count < capacity && passengers[idx] <= bus) {
                idx++;
                count++;
            }
        }

        int latest = count < capacity ? buses[buses.length - 1] : passengers[idx - 1] - 1;

        int i = idx - 1;
        while (i >= 0 && passengers[i] == latest) {
            latest--;
            i--;
        }
        return latest;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class LatestTimeToCatchABus {
public:
    /**
     * @param buses      bus times
     * @param passengers passenger times
     * @param capacity   seats per bus
     * @return           latest catch time
     */
    int latestTimeCatchTheBus(std::vector<int>& buses, std::vector<int>& passengers, int capacity) {
        std::sort(buses.begin(), buses.end());
        std::sort(passengers.begin(), passengers.end());

        int idx = 0, count = 0;

        for (int bus : buses) {
            count = 0;

            while (idx < (int)passengers.size() && count < capacity && passengers[idx] <= bus) {
                idx++;
                count++;
            }
        }

        int latest = count < capacity ? buses.back() : passengers[idx - 1] - 1;

        int i = idx - 1;
        while (i >= 0 && passengers[i] == latest) {
            latest--;
            i--;
        }
        return latest;
    }
};
```

```python
def latest_time_catch_the_bus(buses: list[int], passengers: list[int], capacity: int) -> int:
    """
    @param buses:      bus times
    @param passengers: passenger times
    @param capacity:   seats per bus
    @return:           latest catch time
    """
    buses.sort()
    passengers.sort()

    idx = 0
    count = 0

    for bus in buses:
        count = 0

        while idx < len(passengers) and count < capacity and passengers[idx] <= bus:
            idx += 1
            count += 1

    latest = buses[-1] if count < capacity else passengers[idx - 1] - 1

    i = idx - 1
    while i >= 0 and passengers[i] == latest:
        latest -= 1
        i -= 1

    return latest
```

```rust
impl Solution {
    /// @param buses      bus times
    /// @param passengers passenger times
    /// @param capacity   seats per bus
    /// @return           latest catch time
    pub fn latest_time_catch_the_bus(mut buses: Vec<i32>, mut passengers: Vec<i32>, capacity: i32) -> i32 {
        buses.sort_unstable();
        passengers.sort_unstable();

        let (mut idx, mut count) = (0, 0);

        for &bus in &buses {
            count = 0;

            while idx < passengers.len() && count < capacity && passengers[idx] <= bus {
                idx += 1;
                count += 1;
            }
        }

        let mut latest = if count < capacity {
            *buses.last().unwrap()
        } else {
            passengers[idx - 1] - 1
        };

        let mut i = idx as i32 - 1;
        while i >= 0 && passengers[i as usize] == latest {
            latest -= 1;
            i -= 1;
        }
        latest
    }
}
```

## Dry run

**Input:** the example.

```
bus 10: fills 2 with 2,17 -> idx 2.  bus 20: fills 2 with 18,19 -> idx 4, count 2.
count == capacity -> latest = 19 - 1 = 18.  18 in passengers -> 17? 17 in passengers -> 16 ✓
Output: 16
```

## Complexity

**Time.** Two pointers:

$$
T = O(b + p)
$$

**Space.** In place:

$$
S = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why walk back from the candidate?" The latest time must not collide with an existing passenger — the back-off loop decrements until the first free minute.
