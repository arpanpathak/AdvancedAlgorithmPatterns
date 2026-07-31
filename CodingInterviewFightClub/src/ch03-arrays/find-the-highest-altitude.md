# 3.23 Find The Highest Altitude

> **Source:** [`src/main/kotlin/array/prefixsum/FIndTheHighestAltitute.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/FIndTheHighestAltitute.kt)
> **Pattern:** running prefix max · **Core page**

## The Problem

A biker starts at altitude 0; `gain[i]` is the altitude change. The **highest altitude** reached.

- Constraints: n ≤ 100; gains fit in `Int`.

## Examples

```
Input:  gain = [-5,1,5,0,-7]   -> Output: 1   (altitudes: 0,-5,-4,1,1,-6)
Input:  gain = [-4,-3,-2,-1,4,3,2]  -> Output: 0   (never above start)
```

## Intuition — track the running sum, keep the max

Altitude after `i` segments is the prefix sum. Track the running total and the maximum it ever reached:

```kotlin
var (currentAltitude, highestAltitude) = Pair(0, 0)

for (i in 0 until gain.size) {
    currentAltitude += gain[i]
    highestAltitude = maxOf(currentAltitude, highestAltitude)
}
return highestAltitude
```

**Why start `highestAltitude = 0`?** The starting point counts — altitude 0 is always reached, so the answer is at least 0.

## Approach 1 — Prefix array then max

Build all altitudes, take the max: correct, O(n) extra space.

## Approach 2 — Running prefix max (the repo's version, optimal)

```kotlin
class FIndTheHighestAltitute {
    /**
     * @param gain altitude changes
     * @return     highest altitude reached
     */
    fun largestAltitude(gain: IntArray): Int {
        var (currentAltitude, highestAltitude) = Pair(0, 0)

        for (i in 0 until gain.size) {
            currentAltitude += gain[i]
            highestAltitude = maxOf(currentAltitude, highestAltitude)
        }
        return highestAltitude
    }
}
```

```java
public class FindTheHighestAltitude {
    /**
     * @param gain altitude changes
     * @return     highest altitude reached
     */
    public int largestAltitude(int[] gain) {
        int current = 0, highest = 0;

        for (int g : gain) {
            current += g;
            highest = Math.max(highest, current);
        }
        return highest;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class FindTheHighestAltitude {
public:
    /**
     * @param gain altitude changes
     * @return     highest altitude reached
     */
    int largestAltitude(std::vector<int>& gain) {
        int current = 0, highest = 0;

        for (int g : gain) {
            current += g;
            highest = std::max(highest, current);
        }
        return highest;
    }
};
```

```python
def largest_altitude(gain: list[int]) -> int:
    """
    @param gain: altitude changes
    @return:     highest altitude reached
    """
    current = highest = 0
    for g in gain:
        current += g
        highest = max(highest, current)
    return highest
```

```rust
impl Solution {
    /// @param gain altitude changes
    /// @return     highest altitude reached
    pub fn largest_altitude(gain: Vec<i32>) -> i32 {
        let (mut current, mut highest) = (0, 0);
        for g in gain {
            current += g;
            highest = highest.max(current);
        }
        highest
    }
}
```

## Dry run

**Input:** `gain = [-5,1,5,0,-7]`.

```
current=0, highest=0
-5: current=-5.  highest=0.
+1: current=-4.  0.  +5: current=1.  highest=1.
+0: current=1.  1.  -7: current=-6.  1.

Output: 1 ✓   (altitudes 0,-5,-4,1,1,-6; the peak is 1)
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Two scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Find Pivot Index** ([3.15](find-pivot-index.md)) — the prefix-sum family's balance test.
- **Running Sum** — the simplest prefix-sum member.
- **Interview follow-up:** "Why is 0 the initial max?" The starting altitude counts as a reached altitude — `gain = [-4,-3,-2,-1,4,3,2]` never rises above 0, and the answer is 0, not the max of negative dips.
