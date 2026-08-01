# 11.31 Minimum Time To Make Rope Colorful

> **Source**: [`src/main/kotlin/greedy/MinimumTimeToMakeRopeColorful.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/MinimumTimeToMakeRopeColorful.kt)
> **Pattern**: run-max pruning · **Core page**

## The Problem

Remove balloons so adjacent ones differ — min total removal time.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  colors = "abaac", neededTime = [1,2,3,4,5]   -> Output: 3
```

## Intuition — in each same-color run, keep the most expensive, remove the rest

```kotlin
var minTime = 0

for (i in 1 until neededTime.size) {
    if (colors[i] == colors[i - 1]) {
        minTime += minOf(neededTime[i], neededTime[i - 1])
        neededTime[i] = maxOf(neededTime[i], neededTime[i - 1])
    }
}
return minTime
```

**Why the max-carry?** The survivor of a run is its max cost — carrying it forward makes the next comparison against the run's best.

## Approach 1 — Run-max pruning (the repo's version, optimal)

```kotlin
class MinimumTimeToMakeRopeColorful {
    /**
     * @param colors      balloon colors
     * @param neededTime  removal times
     * @return            min total removal time
     */
    fun minCost(colors: String, neededTime: IntArray): Int {
        var minTime = 0

        for (i in 1 until neededTime.size) {
            if (colors[i] == colors[i - 1]) {
                minTime += minOf(neededTime[i], neededTime[i - 1])
                neededTime[i] = maxOf(neededTime[i], neededTime[i - 1])
            }
        }
        return minTime
    }
}
```

```java
public class MinimumTimeToMakeRopeColorful {
    /**
     * @param colors      balloon colors
     * @param neededTime  removal times
     * @return            min total removal time
     */
    public int minCost(String colors, int[] neededTime) {
        int total = 0;

        for (int i = 1; i < neededTime.length; i++) {
            if (colors.charAt(i) == colors.charAt(i - 1)) {
                total += Math.min(neededTime[i], neededTime[i - 1]);
                neededTime[i] = Math.max(neededTime[i], neededTime[i - 1]);
            }
        }
        return total;
    }
}
```

```cpp
#include <string>
#include <vector>
#include <algorithm>

class MinimumTimeToMakeRopeColorful {
public:
    /**
     * @param colors      balloon colors
     * @param neededTime  removal times
     * @return            min total removal time
     */
    int minCost(std::string colors, std::vector<int>& neededTime) {
        int total = 0;

        for (int i = 1; i < (int)neededTime.size(); i++) {
            if (colors[i] == colors[i - 1]) {
                total += std::min(neededTime[i], neededTime[i - 1]);
                neededTime[i] = std::max(neededTime[i], neededTime[i - 1]);
            }
        }
        return total;
    }
};
```

```python
def min_cost(colors: str, needed_time: list[int]) -> int:
    """
    @param colors:      balloon colors
    @param needed_time: removal times
    @return:            min total removal time
    """
    total = 0

    for i in range(1, len(needed_time)):
        if colors[i] == colors[i - 1]:
            total += min(needed_time[i], needed_time[i - 1])
            needed_time[i] = max(needed_time[i], needed_time[i - 1])

    return total
```

```rust
impl Solution {
    /// @param colors      balloon colors
    /// @param needed_time removal times
    /// @return            min total removal time
    pub fn min_cost(colors: String, needed_time: Vec<i32>) -> i32 {
        let c: Vec<char> = colors.chars().collect();
        let mut needed = needed_time;
        let mut total = 0;

        for i in 1..needed.len() {
            if c[i] == c[i - 1] {
                total += needed[i].min(needed[i - 1]);
                needed[i] = needed[i].max(needed[i - 1]);
            }
        }
        total
    }
}
```

## Dry run

**Input:** `colors = "abaac", neededTime = [1,2,3,4,5]`.

```
i=1: b != a.  i=2: a != b.  i=3: a == a: total += min(4,3)=3; needed[3]=4.
i=4: c != a.
Output: 3 ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** In place:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why keep the max?" The run needs exactly one survivor — the cheapest to remove are everything except the most expensive; the carried max tracks the survivor without extra state.
