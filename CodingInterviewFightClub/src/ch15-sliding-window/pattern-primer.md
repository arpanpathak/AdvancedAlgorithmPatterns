# 15.0 Pattern Primer — The Moving Window

A sliding window is **two pointers that delimit a contiguous range**, plus a **running state** that's updated in $O(1)$ per step instead of recomputed per range. The whole art is deciding *when the left pointer moves*:

## Fixed-size windows

The window is always exactly `k` long: `right` advances every step, and `left = right - k + 1` is *derived*, never chosen. The state update is **add the entering element, remove the leaving one** — the running sum of [15.4](maximum-average-subarray.md) is the archetype. No condition, no shrink logic; the left pointer is arithmetic.

## Variable-size (shrink-until-valid) windows

Here `left` moves *conditionally* — the window must satisfy a property, and the answer is the *largest* (or *smallest*) valid window:

```
for right in 0..n-1:
    state.add(s[right])                        # expand
    while not valid(state):                    # the window broke the rule
        state.remove(s[left]); left++          # shrink from the left
    answer = max(answer, right - left + 1)
```

The **monotonicity requirement**: shrinking must eventually restore validity (validity is monotone — a sub-window of a valid window is valid). If the property isn't monotone, this template breaks. Used by [15.2](minimum-window-substring.md), [15.3](longest-repeating-character-replacement.md), [15.5](minimum-size-subarray-sum.md), [15.7](max-consecutive-ones-iii.md).

## The "last index" map trick

[Longest Substring Without Repeating Characters](longest-substring-without-repeating-characters.md) stores `char -> last position`; a repeat pulls `left` *directly* past the previous occurrence (the [10.2](../ch10-hash-tables/contains-duplicate-ii.md) value-to-state move) — no while loop needed, because the condition is "no repeats", which is fully determined by the last sighting.

## The monotonic deque

[Sliding Window Maximum](sliding-window-maximum.md) needs "max of the current window" in $O(1)$ per step. The deque keeps **indices with decreasing values** — the front is the current max; expired indices pop from the front, smaller values pop from the back before inserting. It's the [monotonic stack](../ch08-stacks/index.md) idea turned into a two-ended structure with an expiry condition.

## Complexity intuition

Every element enters the window once and leaves once → **$O(n)$ total** regardless of the inner while loops. The running state is $O(1)$ per update (a sum, a count, a deque operation). The interview tell: *"contiguous subarray/substring" + "largest/smallest satisfying a condition"* → sliding window, not nested loops. The nested loop is $O(n^2)$; the window is $O(n)$ because no element is processed twice.
