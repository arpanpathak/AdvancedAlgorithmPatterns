# Chapter 7 — Heaps & Priority Queues

> **Source:** `src/main/kotlin/heap/`
>
> **Master idea:** a heap is a *lazy sorted structure* — it answers "what's the smallest/largest?" in $O(\log n)$ without keeping the whole collection sorted. Nearly every heap problem is one of three moves: *keep the top k*, *split into two halves*, or *process in priority order*.
>
> **Prerequisites:** arrays, the BFS/level ideas from [Chapter 5](../ch05-trees/index.md), and a willingness to read "sort, but only enough" as the answer.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 7.1 | Top K Frequent Elements | min-heap of size k | $O(n \log k)$ | [→](top-k-frequent-elements.md) |
| 7.2 | Find Median From Data Stream | two heaps (max + min) | $O(\log n)$ / add | [→](find-median-from-data-stream.md) |
| 7.3 | Sliding Window Median | dual heap + lazy deletion | $O(n \log k)$ | [→](sliding-window-median.md) |
| 7.4 | Trapping Rain Water II | min-heap boundary expansion | $O(mn \log(mn))$ | [→](trapping-rain-water-ii.md) |
| 7.5 | IPO (Maximize Capital) | greedy + max-profit heap | $O((n+k) \log n)$ | [→](ipo.md) |
| 7.6 | Meeting Rooms III | busy/available heaps | $O(m \log n)$ | [→](meeting-rooms-iii.md) |
| 7.7 | Single Threaded CPU | event + ready queues | $O(n \log n)$ | [→](single-threaded-cpu.md) |

| 7.8 | The Skyline Problem | sweep line + height multiset | $O(n log n)$ | [→](the-skyline-problem.md) |
## The rest of the heap/ directory

`src/main/kotlin/heap/` also holds: `DualBalancedHeap.kt` (an alternate sliding-window median), `FindingMKAverage.kt` (three-heap window stats), `FindKClosestElements.kt`, `FindScoreOfAnArrayAfterMarkingAllElements.kt`, `LongestHappyString.kt` (greedy with a max-heap of character counts), and `MedianFromRunningStream.kt` variants. The general-purpose heap lives in `src/main/kotlin/` too — `sliding_window/`, `quicksort/`, and `greedy/` all import the same `PriorityQueue` idiom.

New pages are appended to the table above as they're written.
