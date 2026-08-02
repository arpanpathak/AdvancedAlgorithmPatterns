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

## Reading the code — what's actually happening

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

Imagine a string of balloons where a "run" is a group of the same color sitting together. The rule says adjacent balloons must differ — so within each run, **all but one balloon must go**. The question is *which one to keep*, and the answer is obvious: keep the most expensive one to remove, delete the rest. That's the whole problem.

- **`colors[i] == colors[i - 1]` detects that we're inside a run.** Consecutive same-colored balloons are the conflict; different colors are already fine and skipped.
- **`minTime += minOf(neededTime[i], neededTime[i - 1])` removes the cheaper of the two.** Two same-colored neighbors can't both stay — removing the cheaper one is locally optimal, and since runs are processed left to right, this greedily peels off every non-survivor.
- **`neededTime[i] = maxOf(...)` carries the survivor forward.** After deleting one of the pair, the *other* one is still there — and it might conflict with the *next* balloon if the run continues. By writing the max (the survivor's cost) into `neededTime[i]`, the next iteration compares against the run's champion so far, not a balloon that's already been removed. This is the "running best" trick: one array slot is repurposed as the run's memory.
- **Why is this optimal?** In a run of `k` balloons, exactly `k - 1` must be deleted, and the cheapest possible choice is to keep the single most expensive one — the greedy removes every balloon except the max of the run, paying `sum - max`, which is the minimum possible.

Trace `colors = "abaac", neededTime = [1,2,3,4,5]`: only the run `a,a` at indices 2–3 conflicts → pay `min(4,3)=3`, keep cost 4 → total 3 ✓.

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
