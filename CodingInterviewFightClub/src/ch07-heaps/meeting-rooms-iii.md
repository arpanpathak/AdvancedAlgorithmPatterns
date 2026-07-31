# 7.6 Meeting Rooms III

> **Source:** [`src/main/kotlin/heap/MeetingRoom_III.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/heap/MeetingRoom_III.kt)
> **Pattern:** busy/available heaps · **Core page**

## The Problem

You have `n` rooms numbered `0..n-1`. Meetings arrive as `[start, end]` (half-open: `end` exclusive). Assign each meeting to a room: if a free room exists, use the **lowest-numbered** one; otherwise **delay** the meeting until the earliest-freed room and hold it there (keeping its original duration). Return the room that hosted the **most** meetings (ties: smallest index).

- Constraints: $1 \le n \le 100$; $1 \le meetings.length \le 10^5$; `0 <= start < end <= 10^9`.

## Examples

```
Input:  n = 2, meetings = [[0,10],[1,5],[2,7],[3,4]]
Output: 0   (room 0 hosts [0,10] and the delayed [10,11]; room 1 hosts [1,5] and [5,10] — tie, so 0)

Input:  n = 3, meetings = [[1,20],[2,10],[3,5],[4,9],[6,8]]
Output: 1
```

## Intuition — two heaps, one for "who is free", one for "who frees next"

Every meeting needs one decision: **is there a free room, and if so which?** Two pools of rooms change over time, so each pool becomes a heap:

- **availableRooms** (min-heap of indices) — rooms currently idle. The *lowest-numbered* free room is its root: the tie-break rule is free.
- **busyRooms** (min-heap of `(endTime, index)`) — rooms in use, ordered by when they free up. The room that frees *earliest* is its root: the delay fallback is free.

The flow per meeting (in start-time order):

1. **Release**: while `busyRooms.peek().endTime <= start`, move that room back to `availableRooms`. (Half-open intervals make `<=` correct.)
2. **Assign**: if `availableRooms` is non-empty, pop the lowest index, count it, and put it into `busyRooms` with `endTime = start + duration`.
3. **Delay**: otherwise, pop the earliest-freed room, count it, and re-insert it with `endTime = earliestEnd + duration` — the meeting takes over the moment the room is free, *delayed* by however long it had to wait.

**Why does delaying preserve duration but shift the start?** A delayed meeting keeps its original length but begins when its room is available — that is the problem's contract, and it is the reason step 3 uses `earliest.endTime + duration` instead of the original `end`. The classic trap: the *start* moves, the *duration* does not.

**Why sort meetings first?** The release step must know "what time is it now" — and "now" is each meeting's start. Processing out of start-time order would break the release logic, so `meetings.sortBy { start }` is the very first move.

## Approach 1 — Brute force: scan rooms per meeting

For each meeting, scan all n rooms for the first free one, or the earliest end: $O(m \cdot n)$. Fine for small n, but at $n = 100$ and $m = 10^5$ it is $10^7$ — workable, yet the heap version is $O(m \log n)$ and is the interview answer.

## Approach 2 — Two heaps (the repo's version, optimal)

```kotlin
import java.util.*

class MeetingRoom_III {
    data class Room(val endTime: Long, val index: Int)

    /**
     * @param n        number of rooms (0..n-1)
     * @param meetings meetings[i] = [start, end]
     * @return         the room that hosted the most meetings (smallest index on ties)
     */
    fun mostBooked(n: Int, meetings: Array<IntArray>): Int {
        // Sort meetings by start time
        meetings.sortWith(compareBy { it[0] })

        val roomUsage = IntArray(n)
        val busyRooms = PriorityQueue<Room>(compareBy({ it.endTime }, { it.index }))
        val availableRooms = PriorityQueue<Int>()

        // Initialize available rooms
        for (i in 0 until n) availableRooms.add(i)

        for ((start, end) in meetings.map { it[0].toLong() to it[1].toLong() }) {
            val duration = end - start

            // Free up any rooms that are now available
            while (busyRooms.isNotEmpty() && busyRooms.peek().endTime <= start) {
                availableRooms.add(busyRooms.poll().index)
            }

            if (availableRooms.isNotEmpty()) {
                // If a room is available, use it
                val room = availableRooms.poll()
                busyRooms.add(Room(start + duration, room))
                roomUsage[room]++
            } else {
                // If all rooms are busy, use the one that will become available first
                val earliestRoom = busyRooms.poll()
                // The new end time is the earliest available time plus the duration
                busyRooms.add(Room(earliestRoom.endTime + duration, earliestRoom.index))
                roomUsage[earliestRoom.index]++
            }
        }

        // Find the room with maximum usage (if tied, return the smallest index)
        var maxUsage = -1
        var result = -1
        for (i in 0 until n) {
            if (roomUsage[i] > maxUsage) {
                maxUsage = roomUsage[i]
                result = i
            }
        }
        return result
    }
}
```

```java
import java.util.*;

public class MeetingRoomsIII {
    private record Busy(long endTime, int index) {}

    /**
     * @param n        number of rooms (0..n-1)
     * @param meetings meetings[i] = [start, end]
     * @return         the room that hosted the most meetings (smallest index on ties)
     */
    public int mostBooked(int n, int[][] meetings) {
        Arrays.sort(meetings, Comparator.comparingInt(m -> m[0]));

        int[] usage = new int[n];
        PriorityQueue<Busy> busy = new PriorityQueue<>(
            Comparator.comparingLong(Busy::endTime).thenComparingInt(Busy::index));
        PriorityQueue<Integer> free = new PriorityQueue<>();
        for (int i = 0; i < n; i++) free.offer(i);

        for (int[] m : meetings) {
            long start = m[0], duration = m[1] - m[0];

            while (!busy.isEmpty() && busy.peek().endTime() <= start) {
                free.offer(busy.poll().index());            // release freed rooms
            }

            if (!free.isEmpty()) {
                int room = free.poll();                     // lowest-numbered free room
                busy.offer(new Busy(start + duration, room));
                usage[room]++;
            } else {
                Busy earliest = busy.poll();                // delay into earliest-freed room
                busy.offer(new Busy(earliest.endTime() + duration, earliest.index()));
                usage[earliest.index()]++;
            }
        }

        int max = -1, result = -1;
        for (int i = 0; i < n; i++) {
            if (usage[i] > max) { max = usage[i]; result = i; }
        }
        return result;
    }
}
```

```cpp
#include <algorithm>
#include <functional>
#include <queue>
#include <vector>

class MeetingRoomsIII {
    struct Busy { long long endTime; int index; };
    struct BusyCmp { bool operator()(const Busy& a, const Busy& b) const {
        return a.endTime > b.endTime || (a.endTime == b.endTime && a.index > b.index);
    }};

public:
    /**
     * @param n        number of rooms (0..n-1)
     * @param meetings meetings[i] = [start, end]
     * @return         the room that hosted the most meetings (smallest index on ties)
     */
    int mostBooked(int n, std::vector<std::vector<int>>& meetings) {
        std::sort(meetings.begin(), meetings.end());

        std::vector<int> usage(n, 0);
        std::priority_queue<Busy, std::vector<Busy>, BusyCmp> busy;
        std::priority_queue<int, std::vector<int>, std::greater<int>> free;
        for (int i = 0; i < n; i++) free.push(i);

        for (auto& m : meetings) {
            long long start = m[0], duration = m[1] - m[0];

            while (!busy.empty() && busy.top().endTime <= start) {
                free.push(busy.top().index);                // release freed rooms
                busy.pop();
            }

            if (!free.empty()) {
                int room = free.top(); free.pop();          // lowest-numbered free room
                busy.push({start + duration, room});
                usage[room]++;
            } else {
                Busy earliest = busy.top(); busy.pop();     // delay into earliest-freed room
                busy.push({earliest.endTime + duration, earliest.index});
                usage[earliest.index]++;
            }
        }

        int max = -1, result = -1;
        for (int i = 0; i < n; i++) {
            if (usage[i] > max) { max = usage[i]; result = i; }
        }
        return result;
    }
};
```

```python
import heapq

def most_booked(n: int, meetings: list[list[int]]) -> int:
    """
    @param n:        number of rooms (0..n-1)
    @param meetings: meetings[i] = [start, end]
    @return:         the room that hosted the most meetings (smallest index on ties)
    """
    meetings.sort()                                    # by start time
    usage = [0] * n
    busy = []                                          # (endTime, index)
    free = list(range(n))
    heapq.heapify(free)

    for start, end in meetings:
        duration = end - start

        while busy and busy[0][0] <= start:            # release freed rooms
            heapq.heappush(free, heapq.heappop(busy)[1])

        if free:
            room = heapq.heappop(free)                 # lowest-numbered free room
            heapq.heappush(busy, (start + duration, room))
            usage[room] += 1
        else:
            earliest_end, room = heapq.heappop(busy)   # delay into earliest-freed room
            heapq.heappush(busy, (earliest_end + duration, room))
            usage[room] += 1

    return usage.index(max(usage))
```

```rust
use std::cmp::Reverse;
use std::collections::BinaryHeap;

impl Solution {
    /// @param n        number of rooms (0..n-1)
    /// @param meetings meetings[i] = [start, end]
    /// @return         the room that hosted the most meetings (smallest index on ties)
    pub fn most_booked(n: i32, meetings: Vec<Vec<i32>>) -> i32 {
        let mut meetings = meetings;
        meetings.sort();

        let n = n as usize;
        let mut usage = vec![0i32; n];
        // busy: min-heap on (endTime, index); Reverse flips the whole tuple
        let mut busy: BinaryHeap<Reverse<(i64, usize)>> = BinaryHeap::new();
        // free: min-heap of indices
        let mut free: BinaryHeap<Reverse<usize>> = (0..n).map(Reverse).collect();

        for m in meetings {
            let (start, end) = (m[0] as i64, m[1] as i64);
            let duration = end - start;

            while let Some(&Reverse((end_time, idx))) = busy.peek() {
                if end_time > start { break; }
                busy.pop();
                free.push(Reverse(idx));                       // release freed rooms
            }

            if let Some(Reverse(room)) = free.pop() {
                busy.push(Reverse((start + duration, room)));  // lowest-numbered free room
                usage[room] += 1;
            } else {
                let Reverse((end_time, room)) = busy.pop().unwrap();
                busy.push(Reverse((end_time + duration, room)));   // delay, keep duration
                usage[room] += 1;
            }
        }

        // max usage, smallest index on ties
        let mut max_usage = -1;
        let mut result = 0;
        for (i, &u) in usage.iter().enumerate() {
            if u > max_usage { max_usage = u; result = i; }
        }
        result as i32
    }
}
```

## Dry run

**Input:** `n = 2`, `meetings = [[0,10],[1,5],[2,7],[3,4]]`.

```
meetings sorted: [0,10], [1,5], [2,7], [3,4]
free = {0,1}, busy = {}

[0,10] dur=10: release: busy empty.
  free non-empty -> room 0.  busy={(10,0)},     usage[0]=1
[1,5]  dur=4:  release: (10,0) end 10 <= 1? no.
  free non-empty -> room 1.  busy={(5,1),(10,0)},  usage[1]=1
[2,7]  dur=5:  release: (5,1) end 5 <= 2? no.
  free empty -> delay: pop (5,1); new end = 5 + 5 = 10.
                busy={(10,0),(10,1)},           usage[1]=2
[3,4]  dur=1:  release: peek (10,0): 10 <= 3? no.
  free empty -> delay: pop (10,0) (tie with (10,1), smaller index wins);
                new end = 10 + 1 = 11.
                busy={(10,1),(11,0)},           usage[0]=2

usage = [2, 2] -> tie -> return 0 ✓
```

The two subtle lines: `[2,7]` is delayed to `[5,10]` — its *start* shifted from 2 to 5 but its *duration* stayed 5 — and `[3,4]` is delayed into room **0** (the `(endTime, index)` comparator breaks the tie by index), which is exactly why the answer is 0 and not 1.

## Complexity

**Time.** Each meeting does a constant number of heap ops on heaps of size n:

$$
T(m, n) = O(m \log n)
$$

(plus $O(m \log m)$ for the initial sort).

**Space.** Both heaps and the usage array:

$$
S(m, n) = O(n)
$$

## Variants & follow-ups

- **Single Threaded CPU** ([7.7](single-threaded-cpu.md)) — the mirror image: instead of rooms racing for meetings, a CPU races through tasks; the "who frees next" heap becomes the ready queue.
- **IPO** ([7.5](ipo.md)) — the same "sorted gate + priority ranking" shape, with affordability as the gate.
- **Meeting Rooms II** — the "minimum number of rooms" version: one heap (end times) counting the peak overlap, no delay logic. This page is its full scheduling cousin.
- **Interview follow-up:** "Why `<= start` in the release condition?" Meetings are half-open `[start, end)` — a room ending exactly at `start` is free for the new meeting. Using `<` would leave that room "busy" for one meeting and shift all later delays, changing the answer. Half-open semantics drive the whole comparison.
