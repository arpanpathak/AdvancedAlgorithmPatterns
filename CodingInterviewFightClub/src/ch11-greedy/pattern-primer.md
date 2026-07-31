# 11.0 Pattern Primer — The Local Choice, Defended

**Greedy** = at every step, make the choice that looks best *right now*, and never revisit it. It's the simplest algorithm family to *write* and the easiest to *get wrong* — because a locally optimal choice is only safe if the problem has a structure that makes it globally optimal. The interview question is never "what's the greedy step?" but "**why does the greedy step work?**"

## The three moves

**1. Reach / frontier tracking** — ["can I get there at all?"](jump-game.md) and ["how few steps?"](jump-game-ii.md). Maintain the *farthest point reachable so far*; extend it greedily at every index. Correctness comes from a **staying-ahead argument**: if some optimal strategy reaches `r`, ours reaches at least `r` after every step, so we're never worse.

**2. Interval scheduling by sorting** — [Meeting Rooms](meeting-rooms.md)/[II](meeting-rooms-ii.md). Sort by start time, then the *adjacent* comparisons decide everything: no overlap check needs non-neighbors (a meeting overlaps *any* meeting iff it overlaps the sorted neighbors). The greedy structure is "process in chronological order"; the proof is that any overlap is visible at a sorted boundary.

**3. Deferred decisions with a heap** — [Refueling Stops](minimum-number-of-refueling-stops.md) and Task Scheduler's formula. Instead of choosing *when you pass a station*, you *record* it and choose *when you're stuck* — a **max-heap of "choices I could have made"**. This is greedy with a memory: you always pick the best un-taken option when forced. The "time travel" phrasing in the repo's comments is exactly right — you defer the decision until the moment it matters.

## When is greedy correct? The two proof shapes

Interviewers want *one* of these, stated in a sentence:

- **Exchange argument**: "any optimal solution can be transformed, step by step, into the greedy one without losing quality." (Task Scheduler, Meeting Rooms.)
- **Staying ahead**: "after each step, greedy's partial state is at least as good as any other strategy's." (Jump Game's reachable frontier.)

And the red flag: **greedy fails when a later decision can invalidate an earlier one.** Classic counterexamples: knapsack (greedy by value fails), coin change with non-canonical denominations, most DP problems. If you can construct *any* two-step counterexample, the problem isn't greedy — reach for [Chapter 2](../ch02-dynamic-programming/index.md) instead.

## Complexity intuition

Greedy is almost always a **sort + single pass**: $O(n \log n)$ for the sort (intervals, cars, stations), then $O(n)$ or $O(\log n)$-per-step work (heaps). The pure-scan ones (jumps) are $O(n)$. The proof, not the algorithm, is what costs interview time — budget your explanation accordingly.
