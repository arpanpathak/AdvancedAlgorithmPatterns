# 10.3 Longest Consecutive Sequence

> **Source:** [`src/main/kotlin/array/hashtable/LongestConsecutiveSequence.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/LongestConsecutiveSequence.kt)
> **Pattern:** set + run-start detection · **Core page**

## The Problem

Given an unsorted array of integers, return the length of the longest **consecutive elements** sequence (e.g., `[100,4,200,1,3,2]` contains `1,2,3,4`). The algorithm must run in **$O(n)$**.

- Constraints: $0 \le n \le 10^5$; $-10^9 \le nums[i] \le 10^9$.

## Examples

```
Input:  nums = [100,4,200,1,3,2]
Output: 4    (the run 1,2,3,4)

Input:  nums = [0,3,7,2,5,8,4,6,0,1]
Output: 9    (0..8, duplicates ignored)
```

## Intuition — "only extend runs from their *start*"

The $O(n)$ constraint kills the obvious answers: sorting is $O(n \log n)$, and a per-element extension walk is $O(n^2)$. The trick has two parts:

1. **A set for $O(1)$ membership** — load all values into a `HashSet`. "Is `x + 1` present?" is now $O(1)$.
2. **Only start a run at its beginning** — the key idea: when walking forward from `x` (checking `x+1`, `x+2`, ...), do it **only if `x - 1` is NOT in the set**. If `x - 1` exists, `x` is in the middle of a run — that run will be (or already was) counted when we start from its true first element.

Why is this $O(n)$ and not $O(n^2)$? Each element is a run-start only once, and each *extension* step moves an element from "unseen" to "consumed by a run" exactly once. The total extension work across all runs is $O(n)$ — the same amortization argument as the monotonic stack: the inner `while` only ever visits elements that no other run will visit.

**Why does membership decide the start?** The run containing `x` has a minimal element `m`. Walking forward from `m` discovers the whole run. Walking from any non-minimal element would rediscover a suffix of it — wasted work. The `x - 1 in set` check is a cheap filter that makes every element walk at most once: either it's a run start (walks its whole run) or it's skipped.

## Approach 1 — Sort and scan (O(n log n))

Sort, then one pass measuring run lengths: correct and simple, but violates the $O(n)$ requirement and doesn't exercise the insight.

## Approach 2 — Set + run-start detection (the repo's version, optimal)

```kotlin
class LongestConsecutiveSequence {
    /**
     * @param nums unsorted integer array
     * @return     length of the longest run of consecutive integers
     */
    fun longestConsecutive(nums: IntArray): Int {
        val set = nums.toSet()
        var maxLength = 0

        for (num in nums) {
            // Check if num - 1 is NOT in the set.
            // This condition ensures that num is the start of a consecutive sequence.
            if (num - 1 in set) continue

            var currentNum = num
            var currentLength = 1
            while (currentNum + 1 in set) {      // extend the run forward
                currentNum = currentNum + 1
                currentLength++
            }
            maxLength = maxOf(maxLength, currentLength)
        }
        return maxLength
    }
}
```

```java
import java.util.*;

public class LongestConsecutiveSequence {
    /**
     * @param nums unsorted integer array
     * @return     length of the longest run of consecutive integers
     */
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int x : nums) set.add(x);

        int maxLength = 0;
        for (int x : nums) {
            if (set.contains(x - 1)) continue;   // x is mid-run: skip, its start will count it

            int length = 1;
            while (set.contains(x + length)) length++;   // extend the run forward
            maxLength = Math.max(maxLength, length);
        }
        return maxLength;
    }
}
```

```cpp
#include <unordered_set>
#include <vector>

class LongestConsecutiveSequence {
public:
    /**
     * @param nums unsorted integer array
     * @return     length of the longest run of consecutive integers
     */
    int longestConsecutive(std::vector<int>& nums) {
        std::unordered_set<int> set(nums.begin(), nums.end());

        int maxLength = 0;
        for (int x : nums) {
            if (set.count(x - 1)) continue;      // x is mid-run: skip

            int length = 1;
            while (set.count(x + length)) length++;    // extend the run forward
            maxLength = std::max(maxLength, length);
        }
        return maxLength;
    }
};
```

```python
def longest_consecutive(nums: list[int]) -> int:
    """
    @param nums: unsorted integer array
    @return:     length of the longest run of consecutive integers
    """
    s = set(nums)
    max_length = 0

    for x in nums:
        if x - 1 in s:                  # x is mid-run: skip, its start will count it
            continue

        length = 1
        while x + length in s:          # extend the run forward
            length += 1
        max_length = max(max_length, length)
    return max_length
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param nums unsorted integer array
    /// @return     length of the longest run of consecutive integers
    pub fn longest_consecutive(nums: Vec<i32>) -> i32 {
        let set: HashSet<i32> = nums.iter().copied().collect();
        let mut max_length = 0;

        for &x in &nums {
            if set.contains(&(x - 1)) { continue; }   // x is mid-run: skip

            let mut length = 1;
            while set.contains(&(x + length)) {       // extend the run forward
                length += 1;
            }
            max_length = max_length.max(length);
        }
        max_length
    }
}
```

## Dry run

**Input:** `nums = [100,4,200,1,3,2]`.

```
set = {100, 4, 200, 1, 3, 2}

x=100:  99 in set? no -> start: 101? no.  length 1.   max=1
x=4:    3 in set? YES -> skip (4 is mid-run)
x=200:  199 in set? no -> start: 201? no. length 1.   max=1
x=1:    0 in set? no -> start: 2? yes, 3? yes, 4? yes, 5? no.  length 4.  max=4
x=3:    2 in set? yes -> skip
x=2:    1 in set? yes -> skip

Output: 4 ✓
```

Count the membership probes: the run `1,2,3,4` is walked exactly once (from 1); every other element either starts a length-1 run or is skipped by one check. Total work is linear even though there's a `while` inside the loop — the amortization is the whole point of the `x-1 in set` gate.

## Complexity

**Time.** Each element: one gate check, and at most one walk *as part of its run's start*:

$$
T(n) = O(n)
$$

**Space.** The set:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Find Missing Positive / First Missing Positive** (`src/main/kotlin/array/hashtable/FindMissingPositive.kt`) — "smallest positive integer absent" is a run-detection question on a *permutation-indexed* array; the set version of this page works, the O(1)-space version uses index-swapping.
- **Longest Consecutive (with union-find)** — the disjoint-set flavor: `union(x, x+1)` for every present pair, tracking component sizes. Same $O(n)$; a good "solve it two ways" follow-up.
- **Interview follow-up:** "Why is this $O(n)$ and not $O(n^2)$?" Because the inner `while` only executes for *run starts*, and every element belongs to exactly one run — so the total number of `while` iterations across the whole loop equals the number of elements, not $n$ times $n$. The `x - 1 in set` filter is what guarantees each element is walked at most once.
