# 11.4 Meeting Rooms II

> **Source:** [`src/main/kotlin/greedy/MeetingRooms_II_greedy.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/MeetingRooms_II_greedy.kt)
> **Pattern:** sort + min-heap of end times · **Core page**

## The Problem

Given meeting intervals `intervals[i] = [start, end]` (half-open), return the **minimum number of conference rooms** required.

- Constraints: $0 \le n \le 10^4$; `0 <= start < end <= 10^6`.

## Examples

```
Input:  intervals = [[0,30],[5,10],[15,20]]   -> Output: 2   ([0,30] plus one of the others)
Input:  intervals = [[7,10],[2,4]]            -> Output: 1   (disjoint — one room suffices)
```

## Intuition — the peak number of simultaneous meetings

Minimum rooms = **maximum simultaneous overlap**. The question: as meetings start and end over time, what's the largest number alive at once? Two classic engines:

1. **The min-heap of end times** (the repo's `MeetingRooms_II_greedy.kt`): sort by start; keep a heap of the *end times of ongoing meetings*. For each meeting: if the earliest ending meeting is already over (`start >= heap.top`), **reuse its room** (pop it); then push the new meeting's end. The heap size *is* the current room count; its maximum over the run is the answer. Each "pop + push" is one room being reused, so the heap never shrinks permanently — the final size equals the peak.
2. **The two-pointer sweep**: collect all starts and all ends separately, sort both; walk the timeline — a start bumps the counter, an end lowers it; the peak is the answer. $O(n \log n)$ too, no heap.

**Why does "reuse the earliest-free room" work?** Among all currently-busy rooms, the one ending *earliest* becomes free soonest. If even that one is still busy when the next meeting starts, *every* room is busy — a new room is forced. If it's free, reusing it is optimal: any other room is busy even longer, so reusing this one never blocks a future meeting that reusing another wouldn't. (The greedy "always pick the earliest available" is optimal by exchange.)

**Why `start >= heap.peek()` (not `>`)?** Half-open intervals: a meeting ending exactly at `start` frees its room for this one — `>=` captures that, the same `<`-vs-`<=` subtlety as [11.3](meeting-rooms.md).

## Approach 1 — For each interval, scan all rooms (too slow)

Maintain a list of room-until-times and scan for the first free room: $O(n^2)$ worst case.

## Approach 2 — Sort + min-heap of end times (the repo's version, optimal)

```kotlin
import java.util.*

class MeetingRooms_II_greedy {
    /**
     * @param intervals intervals[i] = [start, end], half-open
     * @return          minimum number of rooms needed
     */
    fun minMeetingRooms(intervals: Array<IntArray>): Int {
        intervals.sortBy { it[0] }

        val heap = PriorityQueue<Int>()              // end times of ongoing meetings

        for ((start, end) in intervals) {
            // If the current meeting starts after the earliest ending meeting, reuse the room
            if (heap.isNotEmpty() && start >= heap.peek()) {
                heap.poll()                          // earliest-ending room is free: reuse
            }
            heap.offer(end)                          // this meeting occupies a room
        }
        return heap.size                             // peak occupancy = rooms needed
    }
}
```

```java
import java.util.*;

public class MeetingRoomsII {
    /**
     * @param intervals intervals[i] = [start, end], half-open
     * @return          minimum number of rooms needed
     */
    public int minMeetingRooms(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

        PriorityQueue<Integer> heap = new PriorityQueue<>();   // end times of ongoing meetings

        for (int[] m : intervals) {
            if (!heap.isEmpty() && m[0] >= heap.peek()) {
                heap.poll();                        // earliest-ending room is free: reuse
            }
            heap.offer(m[1]);                       // this meeting occupies a room
        }
        return heap.size();                         // peak occupancy = rooms needed
    }
}
```

```cpp
#include <algorithm>
#include <functional>
#include <queue>
#include <vector>

class MeetingRoomsII {
public:
    /**
     * @param intervals intervals[i] = [start, end], half-open
     * @return          minimum number of rooms needed
     */
    int minMeetingRooms(std::vector<std::vector<int>>& intervals) {
        std::sort(intervals.begin(), intervals.end());

        std::priority_queue<int, std::vector<int>, std::greater<int>> heap;  // end times

        for (auto& m : intervals) {
            if (!heap.empty() && m[0] >= heap.top()) {
                heap.pop();                         // earliest-ending room is free: reuse
            }
            heap.push(m[1]);                        // this meeting occupies a room
        }
        return heap.size();                         // peak occupancy = rooms needed
    }
};
```

```python
import heapq

def min_meeting_rooms(intervals: list[list[int]]) -> int:
    """
    @param intervals: intervals[i] = [start, end], half-open
    @return:          minimum number of rooms needed
    """
    intervals.sort()                                 # by start
    heap = []                                        # end times of ongoing meetings

    for start, end in intervals:
        if heap and start >= heap[0]:                # earliest-ending room is free: reuse
            heapq.heappop(heap)
        heapq.heappush(heap, end)                    # this meeting occupies a room
    return len(heap)                                 # peak occupancy = rooms needed
```

```rust
use std::cmp::Reverse;
use std::collections::BinaryHeap;

impl Solution {
    /// @param intervals intervals[i] = [start, end], half-open
    /// @return          minimum number of rooms needed
    pub fn min_meeting_rooms(mut intervals: Vec<Vec<i32>>) -> i32 {
        intervals.sort();                            // by start

        let mut heap: BinaryHeap<Reverse<i32>> = BinaryHeap::new();  // end times (min-heap)

        for m in intervals {
            if let Some(&Reverse(earliest_end)) = heap.peek() {
                if m[0] >= earliest_end {            // earliest-ending room is free: reuse
                    heap.pop();
                }
            }
            heap.push(Reverse(m[1]));                // this meeting occupies a room
        }
        heap.len() as i32                            // peak occupancy = rooms needed
    }
}
```

### The sweep-line version (the notes' event-flatten alternative)

The notes' *Interval Partitioning* flavor flattens every interval into `(start, +1)` and `(end, -1)` events, sorts them (ends before starts at the same time), and tracks the running overlap — the peak is the minimum room count:

```kotlin
fun minRooms(intervals: Array<IntArray>): Int {
    // Flatten into events; the +1/-1 sign makes "end before start" at equal times automatic
    val events = intervals.flatMap { listOf(it[0] to 1, it[1] to -1) }
        .sortedWith(compareBy({ it.first }, { it.second }))

    var maxRooms = 0
    var current = 0
    for ((_, type) in events) {
        current += type
        maxRooms = maxOf(maxRooms, current)
    }
    return maxRooms
}
```

Same $O(n \log n)$ and $O(n)$ space as the min-heap ([Approach 2](#approach-2--sort--min-heap-of-end-times-the-repos-version-optimal)); the difference is *what* carries the state — a running counter instead of a heap of end times. Both are valid answers; the sweep-line is the [7.8](../ch07-heaps/the-skyline-problem.md) skyline's skeleton in miniature (events + a running aggregate), which makes it a nice segue if the interviewer pivots.

## Dry run

**Input:** `intervals = [[0,30],[5,10],[15,20]]`.

```
sorted: [[0,30],[5,10],[15,20]]
heap = []

[0,30]: heap empty -> push 30.        heap=[30]     (rooms in use: 1)
[5,10]: peek 30; 5 >= 30? no -> push 10.  heap=[10,30]   (2)
[15,20]: peek 10; 15 >= 10? yes -> pop 10 (room free!); push 20.  heap=[20,30]  (2)

heap.size = 2 -> Output: 2 ✓   (room 1 hosts [0,30]; room 2 hosts [5,10] then [15,20])
```

The reuse is visible at `[15,20]`: the room that ended at 10 is popped and immediately re-occupied. Notice the heap *size* never drops below the current occupancy — each pop is matched by a push, so the final size equals the peak over time.

## Complexity

**Time.** Sort, then $O(\log n)$ heap ops per interval:

$$
T(n) = O(n \log n)
$$

**Space.** The heap holds at most all overlapping meetings:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Meeting Rooms** ([11.3](meeting-rooms.md)) — the boolean version; the answer is `minMeetingRooms <= 1`.
- **Meeting Rooms III** ([7.6](../ch07-heaps/meeting-rooms-iii.md)) — the full scheduler: rooms have *identities* and meetings can be *delayed*; two heaps (busy + free) instead of one.
- **Two-pointer sweep version** — collect starts and ends, sort each, walk both: same $O(n \log n)$, no heap. A favorite "same answer, different tool" interview comparison.
- **Interview follow-up:** "Why is the heap's size exactly the answer?" Each push represents a meeting occupying a room; each pop reuses a room. The count of `(pushes - pops)` at any moment is the simultaneous occupancy, and the *final* size (after all pops are matched with pushes) equals the peak — because a room is only ever popped to be re-pushed, the heap never permanently shrinks.
