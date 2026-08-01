# 3.48 Maximum Distance In Arrays

> **Source**: [`src/main/kotlin/array/greedy/MaximumDistanceInArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/greedy/MaximumDistanceInArray.kt)
> **Pattern**: global min/max tracking · **Core page**

## The Problem

Max `|a[i] - b[j]|` where a and b are from **different** arrays (each sorted).

- Constraints: arrays ≤ 10⁵.

## Examples

```
Input:  arrays = [[1,2,3],[4,5],[1,2,3]]   -> Output: 4
```

## Intuition — the max distance is global-max minus global-min from different arrays

Track the best min/max *so far*; for each new array, its extremes pair with the previous extremes:

```kotlin
var minValue = arrays[0].first()
var maxValue = arrays[0].last()
var maxDistance = 0

for (i in 1 until arrays.size) {
    val currentMin = arrays[i].first()
    val currentMax = arrays[i].last()

    maxDistance = maxOf(
        maxDistance,
        abs(currentMax - minValue),   // this max vs an earlier min
        abs(maxValue - currentMin)    // this min vs an earlier max
    )

    minValue = minOf(minValue, currentMin)
    maxValue = maxOf(maxValue, currentMax)
}
return maxDistance
```

## Approach 1 — Running extremes (the repo's version, optimal)

```kotlin
class MaximumDistanceInArray {
    /**
     * @param arrays sorted arrays
     * @return      max distance between two arrays
     */
    fun maxDistance(arrays: List<IntArray>): Int {
        var minValue = arrays[0].first()
        var maxValue = arrays[0].last()
        var maxDistance = 0

        for (i in 1 until arrays.size) {
            val currentMin = arrays[i].first()
            val currentMax = arrays[i].last()

            maxDistance = maxOf(
                maxDistance,
                abs(currentMax - minValue),
                abs(maxValue - currentMin)
            )

            minValue = minOf(minValue, currentMin)
            maxValue = maxOf(maxValue, currentMax)
        }
        return maxDistance
    }
}
```

```java
public class MaximumDistanceInArrays {
    /**
     * @param arrays sorted arrays
     * @return      max distance between two arrays
     */
    public int maxDistance(List<List<Integer>> arrays) {
        int min = arrays.get(0).get(0);
        int max = arrays.get(0).get(arrays.get(0).size() - 1);
        int best = 0;

        for (int i = 1; i < arrays.size(); i++) {
            int curMin = arrays.get(i).get(0);
            int curMax = arrays.get(i).get(arrays.get(i).size() - 1);

            best = Math.max(best, Math.max(curMax - min, max - curMin));

            min = Math.min(min, curMin);
            max = Math.max(max, curMax);
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <cstdlib>

class MaximumDistanceInArrays {
public:
    /**
     * @param arrays sorted arrays
     * @return      max distance between two arrays
     */
    int maxDistance(std::vector<std::vector<int>>& arrays) {
        int min = arrays[0].front();
        int max = arrays[0].back();
        int best = 0;

        for (int i = 1; i < (int)arrays.size(); i++) {
            int curMin = arrays[i].front();
            int curMax = arrays[i].back();

            best = std::max(best, std::max(std::abs(curMax - min), std::abs(max - curMin)));

            min = std::min(min, curMin);
            max = std::max(max, curMax);
        }
        return best;
    }
};
```

```python
def max_distance(arrays: list[list[int]]) -> int:
    """
    @param arrays: sorted arrays
    @return:       max distance between two arrays
    """
    min_value = arrays[0][0]
    max_value = arrays[0][-1]
    best = 0

    for arr in arrays[1:]:
        cur_min, cur_max = arr[0], arr[-1]

        best = max(best, cur_max - min_value, max_value - cur_min)

        min_value = min(min_value, cur_min)
        max_value = max(max_value, cur_max)

    return best
```

```rust
impl Solution {
    /// @param arrays sorted arrays
    /// @return      max distance between two arrays
    pub fn max_distance(arrays: Vec<Vec<i32>>) -> i32 {
        let mut min_value = arrays[0][0];
        let mut max_value = *arrays[0].last().unwrap();
        let mut best = 0;

        for arr in arrays.iter().skip(1) {
            let (cur_min, cur_max) = (arr[0], *arr.last().unwrap());

            best = best.max((cur_max - min_value).abs().max((max_value - cur_min).abs()));

            min_value = min_value.min(cur_min);
            max_value = max_value.max(cur_max);
        }
        best
    }
}
```

## Dry run

**Input:** `arrays = [[1,2,3],[4,5],[1,2,3]]`.

```
min=1, max=3.  [4,5]: best = max(5-1=4, 3-4=1) = 4.  min=1, max=5.
[1,2,3]: best = max(3-1=2, 5-1=4) = 4.
Output: 4 ✓
```

## Complexity

**Time.** One pass:

$$
T(k) = O(k)
$$

**Space.** Constants:

$$
S(k) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why must the extremes come from different arrays?" The min/max are tracked *before* the current array's update — pairing the current extremes with past ones guarantees distinct sources.
