# Chapter 11 — Greedy

> **Source:** `src/main/kotlin/greedy/`
>
> **Master idea:** a greedy algorithm makes the **locally optimal choice at every step** — and is *correct* only when the local choice can be proven globally optimal. This chapter's problems fall into three moves: *reach/frontier tracking*, *interval scheduling by sorting*, and *deferred decisions with a heap*.
>
> **Prerequisites:** sorting, the heap from [Chapter 7](../ch07-heaps/index.md), and a habit of asking "but does greedy actually work here?" — the answer is never obvious, it's proven.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 11.1 | Jump Game | reachable-frontier tracking | $O(n)$ | [→](jump-game.md) |
| 11.2 | Jump Game II | frontier + jump count | $O(n)$ | [→](jump-game-ii.md) |
| 11.3 | Meeting Rooms | sort + adjacency check | $O(n \log n)$ | [→](meeting-rooms.md) |
| 11.4 | Meeting Rooms II | sort + min-heap of end times | $O(n \log n)$ | [→](meeting-rooms-ii.md) |
| 11.5 | Car Fleet | sort by position + ETA sweep | $O(n \log n)$ | [→](car-fleet.md) |
| 11.6 | Task Scheduler | frequency math | $O(n)$ | [→](task-scheduler.md) |
| 11.7 | Minimum Number Of Refueling Stops | max-heap "time travel" | $O(n \log n)$ | [→](minimum-number-of-refueling-stops.md) |

| 11.8 | Best Time To Buy And Sell Stock II | greedy on price differences | $O(n)$ | [→](best-time-to-buy-and-sell-stock-ii.md) |
| 11.9 | Non-Overlapping Intervals | greedy by earliest finish | $O(n log n)$ | [→](non-overlapping-intervals.md) |
| 11.10 | Reorganize String | max-heap + cooldown window | $O(n log n)$ | [→](reorganize-string.md) |
| 11.11 | Minimum Number Of Arrows To Burst Balloons | greedy by earliest end | $O(n log n)$ | [→](minimum-number-of-arrows-to-burst-balloons.md) |
| 11.12 | Can Place Flowers | greedy plant-and-mark | $O(n)$ | [→](can-place-flowers.md) |
| 11.13 | Destroying Asteroids | sort + accumulate | $O(n log n)$ | [→](destroying-asteroids.md) |
| 11.14 | Employee Free Time | flatten + merge + gaps | $O(N log N)$ | [→](employee-free-time.md) |
| 11.15 | Max Profit Assigning Work | sorted sweep | $O((T+W) log)$ | [→](max-profit-assigning-work.md) |
| 11.21 | Car Pooling | sweep-line occupancy | $O(t+L)$ | [→](car-pooling.md) |
| 11.22 | Meeting Scheduler | two-pointer overlap | $O(s log s)$ | [→](meeting-scheduler.md) |
| 11.23 | Count Collisions On A Road | boundary exclusion | $O(n)$ | [→](count-collisions-on-a-road.md) |
| 11.24 | Partition Labels | last-occurrence partition | $O(n)$ | [→](partition-labels.md) |
| 11.25 | Break A Palindrome | first-non-a flip | $O(n)$ | [→](break-a-palindrome.md) |
| 11.26 | Max Chunks To Make Sorted II | prefix-max/suffix-min | $O(n)$ | [→](max-chunks-to-make-sorted-ii.md) |
| 11.27 | Maximum Value Of An Ordered Triplet II | running max/diff | $O(n)$ | [→](maximum-value-of-an-ordered-triplet-ii.md) |
| 11.28 | Reschedule Meetings For Max Free Time | gap window sum | $O(n)$ | [→](reschedule-meetings-for-maximum-free-time.md) |
## The rest of the greedy/ directory

`src/main/kotlin/greedy/` also holds: Destroying Asteroids (greedy by size), Jump Game variants, Maximum Profit Assigning Work (sorted pointers), Minimum Time To Make Rope Colorful (keep the max per run), Minimum Deletions To Make String Balanced, Minimum Replacement To Sort The Array, Reschedule Meetings For Maximum Free Time, Maximum Value Of An Ordered Triplet II, Max Chunks To Make Sorted II, MInimum Cost Homecoming Of A Robot, and Task Scheduler neighbors. The interval-family problems connect to `src/main/kotlin/interval/` and the scheduling problems to [7.5](../ch07-heaps/ipo.md)/[7.6](../ch07-heaps/meeting-rooms-iii.md) from the heap chapter.

New pages are appended to the table above as they're written.
