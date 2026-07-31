# 2.27 Minimum Number Of Taps To Water The Garden

> **Source:** [`src/main/kotlin/array/greedy/MinimumNumberOfTapsToWaterGarden.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/greedy/MinimumNumberOfTapsToWaterGarden.kt)
> **Pattern:** interval covering via jump-game greedy · **Core page**

## The Problem

Taps at positions 0..n, each waters `[i - ranges[i], i + ranges[i]]`. Min taps to cover `[0, n]`.

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  n = 5, ranges = [3,4,1,1,0,0]   -> Output: 1   (tap 1 covers 0..5)
Input:  n = 3, ranges = [0,0,0,0]       -> Output: -1
```

## Intuition — the [11.2](../ch11-greedy/jump-game-ii.md) greedy, on coverage intervals

Each tap is an interval `[left, right]`. Track `maxReach[left] = max right` — the farthest any tap starting at `left` reaches. Then the **minimum number of intervals covering [0, n]** is the jump-game-II frontier sweep:

```kotlin
val maxReach = IntArray(n + 1) { it }
for (i in 0..n) {
    val left = maxOf(0, i - ranges[i])
    val right = minOf(n, i + ranges[i])
    maxReach[left] = maxOf(maxReach[left], right)
}

var taps = 0
var currEnd = 0
var farthest = 0

for (i in 0 until n) {
    farthest = maxOf(farthest, maxReach[i])
    if (i == currEnd) {            // reached this frontier's end: must take a tap
        taps++
        currEnd = farthest
        if (currEnd >= n) return taps
        if (currEnd <= i) return -1   // stuck: a gap
    }
}
return -1
```

**Why is this the minimum?** At each frontier boundary, taking the tap that extends coverage farthest is optimal (exchange argument — any other choice covers no more). The [11.2](../ch11-greedy/jump-game-ii.md) layer logic: `taps++` exactly when the current coverage ends.

**Why `maxReach[left]` and not a sorted interval list?** All taps with the same left bound: only the farthest right matters (it dominates). The array collapses the intervals into the jump-game's "max jump from position i".

## Approach 1 — DP over positions (O(n²))

`dp[i]` = min taps to cover [0, i]: correct, slower.

## Approach 2 — Frontier greedy (the repo's version, optimal)

```kotlin
class MinimumNumberOfTapsToWaterGarden {
    /**
     * @param n      garden length
     * @param ranges tap ranges
     * @return       min taps, or -1
     */
    fun minTaps(n: Int, ranges: IntArray): Int {
        val maxReach = IntArray(n + 1) { it }

        for (i in 0..n) {
            val left = maxOf(0, i - ranges[i])
            val right = minOf(n, i + ranges[i])
            maxReach[left] = maxOf(maxReach[left], right)
        }

        var taps = 0
        var currEnd = 0
        var farthest = 0

        for (i in 0 until n) {
            farthest = maxOf(farthest, maxReach[i])

            if (i == currEnd) {
                taps++
                currEnd = farthest
                if (currEnd >= n) return taps
                if (currEnd <= i) return -1
            }
        }
        return -1
    }
}
```

```java
public class MinimumNumberOfTapsToWaterGarden {
    /**
     * @param n      garden length
     * @param ranges tap ranges
     * @return       min taps, or -1
     */
    public int minTaps(int n, int[] ranges) {
        int[] maxReach = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            int left = Math.max(0, i - ranges[i]);
            int right = Math.min(n, i + ranges[i]);
            maxReach[left] = Math.max(maxReach[left], right);
        }

        int taps = 0, currEnd = 0, farthest = 0;
        for (int i = 0; i < n; i++) {
            farthest = Math.max(farthest, maxReach[i]);

            if (i == currEnd) {
                taps++;
                currEnd = farthest;
                if (currEnd >= n) return taps;
                if (currEnd <= i) return -1;
            }
        }
        return -1;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MinimumNumberOfTapsToWaterGarden {
public:
    /**
     * @param n      garden length
     * @param ranges tap ranges
     * @return       min taps, or -1
     */
    int minTaps(int n, std::vector<int>& ranges) {
        std::vector<int> maxReach(n + 1);

        for (int i = 0; i <= n; i++) {
            int left = std::max(0, i - ranges[i]);
            int right = std::min(n, i + ranges[i]);
            maxReach[left] = std::max(maxReach[left], right);
        }

        int taps = 0, currEnd = 0, farthest = 0;
        for (int i = 0; i < n; i++) {
            farthest = std::max(farthest, maxReach[i]);

            if (i == currEnd) {
                taps++;
                currEnd = farthest;
                if (currEnd >= n) return taps;
                if (currEnd <= i) return -1;
            }
        }
        return -1;
    }
};
```

```python
def min_taps(n: int, ranges: list[int]) -> int:
    """
    @param n:      garden length
    @param ranges: tap ranges
    @return:       min taps, or -1
    """
    max_reach = [0] * (n + 1)
    for i, r in enumerate(ranges):
        left = max(0, i - r)
        right = min(n, i + r)
        max_reach[left] = max(max_reach[left], right)

    taps = curr_end = farthest = 0
    for i in range(n):
        farthest = max(farthest, max_reach[i])

        if i == curr_end:
            taps += 1
            curr_end = farthest
            if curr_end >= n:
                return taps
            if curr_end <= i:
                return -1
    return -1
```

```rust
impl Solution {
    /// @param n      garden length
    /// @param ranges tap ranges
    /// @return       min taps, or -1
    pub fn min_taps(n: i32, ranges: Vec<i32>) -> i32 {
        let n = n as usize;
        let mut max_reach = vec![0usize; n + 1];

        for i in 0..=n {
            let left = i.saturating_sub(ranges[i] as usize);
            let right = (i + ranges[i] as usize).min(n);
            max_reach[left] = max_reach[left].max(right);
        }

        let (mut taps, mut curr_end, mut farthest) = (0, 0, 0);
        for i in 0..n {
            farthest = farthest.max(max_reach[i]);

            if i == curr_end {
                taps += 1;
                curr_end = farthest;
                if curr_end >= n { return taps; }
                if curr_end <= i { return -1; }
            }
        }
        -1
    }
}
```

## Dry run

**Input:** `n = 5, ranges = [3,4,1,1,0,0]`.

```
maxReach: tap0: [0,3] -> maxReach[0]=3.  tap1: [0,5] -> maxReach[0]=5.  tap2: [1,3] -> maxReach[1]=3.
          tap3: [2,4] -> maxReach[2]=4.  tap4: [4,4].  tap5: [5,5].
maxReach = [5,3,4,4,4,5]

i=0: farthest = 5.  i == currEnd (0): taps=1, currEnd=5 >= n -> return 1 ✓
```

One tap (tap 1, range 4) covers [0, 5] — the greedy finds it instantly. The failing case `ranges = [0,0,0,0]`: maxReach = [0,1,2,3]; i=0: taps=1, currEnd=0, `currEnd <= i` → -1 ✓ (no coverage can start). The frontier sweep is the jump-game engine with `maxReach` as the jump array.

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** The reach array:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Jump Game II** ([11.2](../ch11-greedy/jump-game-ii.md)) — the engine this page reuses.
- **Video Stitching** (`array/greedy/VideoStitching.kt`) — the same interval-covering greedy on video clips.
- **Interview follow-up:** "Why does collapsing by left-bound work?" For all taps starting at `left`, the one with the max right *dominates* the rest — any solution using a dominated tap can swap in the dominant one without losing coverage. The array form is the [11.2](../ch11-greedy/jump-game-ii.md) jump table; the frontier count is the min-cover count.
