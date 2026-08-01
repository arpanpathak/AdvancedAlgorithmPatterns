# 11.28 Reschedule Meetings For Maximum Free Time

> **Source**: [`src/main/kotlin/greedy/RescheduleMeetingsforMaximumFreeTime_I.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/RescheduleMeetingsforMaximumFreeTime_I.kt)
> **Pattern**: gap window sum · **Core page**

## The Problem

`eventTime` day with meetings `[startTime[i], endTime[i]]`; by **moving** up to k meetings (each to another day), maximize the longest **contiguous free time**.

- Constraints: meetings ≤ 10⁵.

## Examples

```
Input:  eventTime = 5, k = 1, startTime = [1,3], endTime = [2,5]? — a sample: gaps [1, 1, 0]
Output: 2   (move the middle meeting, joining two gaps)
```

## Intuition — the gaps between meetings are the free time; moving k meetings joins k+1 gaps

Compute the gaps (before the first, between meetings, after the last). Moving k meetings frees their *positions*, joining `k+1` consecutive gaps into one run — the max **window sum** over k+1 gaps:

```kotlin
val gaps = mutableListOf<Int>()
gaps.add(startTime[0])
for (i in 1 until n) gaps.add(startTime[i] - endTime[i - 1])
gaps.add(eventTime - endTime[n - 1])

var windowSum = 0
for (i in 0..k) windowSum += gaps[i]

var maxFree = windowSum
for (i in k + 1 until gaps.size) {
    windowSum += gaps[i] - gaps[i - k - 1]
    maxFree = maxOf(maxFree, windowSum)
}
return maxFree
```

**Why the k+1 window?** A moved meeting contributes its entire gap; moving k meetings empties k gaps' worth of positions — but the freed run spans k+1 original gaps (the k removed + the one they sat in). The sliding window over the gap array is the whole optimization.

## Approach 1 — Gap window sum (the repo's version, optimal)

```kotlin
class RescheduleMeetingsforMaximumFreeTime_I {
    /**
     * @param eventTime day length
     * @param k         movable meetings
     * @param startTime meeting starts
     * @param endTime   meeting ends
     * @return          max contiguous free time
     */
    fun maxFreeTime(eventTime: Int, k: Int, startTime: IntArray, endTime: IntArray): Int {
        val n = startTime.size
        val gaps = mutableListOf<Int>()

        gaps.add(startTime[0])
        for (i in 1 until n) {
            gaps.add(startTime[i] - endTime[i - 1])
        }
        gaps.add(eventTime - endTime[n - 1])

        var windowSum = 0
        for (i in 0..k) windowSum += gaps[i]

        var maxFree = windowSum
        for (i in k + 1 until gaps.size) {
            windowSum += gaps[i] - gaps[i - k - 1]
            maxFree = maxOf(maxFree, windowSum)
        }
        return maxFree
    }
}
```

```java
public class RescheduleMeetingsForMaximumFreeTime {
    /**
     * @param eventTime day length
     * @param k         movable meetings
     * @param startTime meeting starts
     * @param endTime   meeting ends
     * @return          max contiguous free time
     */
    public int maxFreeTime(int eventTime, int k, int[] startTime, int[] endTime) {
        int n = startTime.length;
        int[] gaps = new int[n + 1];

        gaps[0] = startTime[0];
        for (int i = 1; i < n; i++) gaps[i] = startTime[i] - endTime[i - 1];
        gaps[n] = eventTime - endTime[n - 1];

        int window = 0;
        for (int i = 0; i <= k; i++) window += gaps[i];

        int best = window;
        for (int i = k + 1; i < gaps.length; i++) {
            window += gaps[i] - gaps[i - k - 1];
            best = Math.max(best, window);
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class RescheduleMeetingsForMaximumFreeTime {
public:
    /**
     * @param eventTime day length
     * @param k         movable meetings
     * @param startTime meeting starts
     * @param endTime   meeting ends
     * @return          max contiguous free time
     */
    int maxFreeTime(int eventTime, int k, std::vector<int>& startTime, std::vector<int>& endTime) {
        int n = startTime.size();
        std::vector<int> gaps(n + 1);

        gaps[0] = startTime[0];
        for (int i = 1; i < n; i++) gaps[i] = startTime[i] - endTime[i - 1];
        gaps[n] = eventTime - endTime[n - 1];

        int window = 0;
        for (int i = 0; i <= k; i++) window += gaps[i];

        int best = window;
        for (int i = k + 1; i < (int)gaps.size(); i++) {
            window += gaps[i] - gaps[i - k - 1];
            best = std::max(best, window);
        }
        return best;
    }
};
```

```python
def max_free_time(event_time: int, k: int, start_time: list[int], end_time: list[int]) -> int:
    """
    @param event_time: day length
    @param k:          movable meetings
    @param start_time: meeting starts
    @param end_time:   meeting ends
    @return:           max contiguous free time
    """
    gaps = [start_time[0]]
    for i in range(1, len(start_time)):
        gaps.append(start_time[i] - end_time[i - 1])
    gaps.append(event_time - end_time[-1])

    window = sum(gaps[:k + 1])
    best = window

    for i in range(k + 1, len(gaps)):
        window += gaps[i] - gaps[i - k - 1]
        best = max(best, window)

    return best
```

```rust
impl Solution {
    /// @param event_time day length
    /// @param k          movable meetings
    /// @param start_time meeting starts
    /// @param end_time   meeting ends
    /// @return           max contiguous free time
    pub fn max_free_time(event_time: i32, k: usize, start_time: Vec<i32>, end_time: Vec<i32>) -> i32 {
        let n = start_time.len();
        let mut gaps = Vec::with_capacity(n + 1);

        gaps.push(start_time[0]);
        for i in 1..n { gaps.push(start_time[i] - end_time[i - 1]); }
        gaps.push(event_time - end_time[n - 1]);

        let mut window: i32 = gaps[..=k].iter().sum();
        let mut best = window;

        for i in (k + 1)..gaps.len() {
            window += gaps[i] - gaps[i - k - 1];
            best = best.max(window);
        }
        best
    }
}
```

## Dry run

**Input:** `eventTime = 5, k = 1, startTime = [1,3], endTime = [2,4]`.

```
gaps: [1, 1, 1]  (before [1,2], between 2-3, after 4-5).
window of k+1=2: 1+1 = 2.  slide: 1+1 = 2.  best 2 ✓
(move either meeting away, joining two gaps into a 2-unit run)
```

## Complexity

**Time.** Gaps + window:

$$
T(n) = O(n)
$$

**Space.** The gap array:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why k+1 gaps per window?" Moving k meetings empties k of their original gap-spans; the freed contiguous run is the union of those k gaps plus the gap they occupied between — k+1 total. The [15.x](../ch15-sliding-window/pattern-primer.md) fixed-window sum, on gaps.
