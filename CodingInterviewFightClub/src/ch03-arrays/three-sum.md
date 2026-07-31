# 3.2 Three Sum

> **Source:** [`src/main/kotlin/array/twopointer/ThreeSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/ThreeSum.kt)
> **Pattern:** sort + converge · **Core page — "sort, then reduce to Two Sum"**

## The Problem

Given an integer array `nums`, return **all** triples `[nums[i], nums[j], nums[k]]` with `i < j < k` (distinct indices) such that `nums[i] + nums[j] + nums[k] == 0`. The solution set must contain **no duplicates** (triples are unordered — `[-1, 0, 1]` and `[0, 1, -1]` are the same triple).

- Constraints: $3 \le n \le 3000$.

## Examples

```
Input:  nums = [-1, 0, 1, 2, -1, -4]
Output: [[-1, -1, 2], [-1, 0, 1]]

Input:  nums = [0, 1, 1]
Output: []            (0+1+1 = 2, no zero triple)

Input:  nums = [0, 0, 0]
Output: [[0, 0, 0]]
```

## Intuition — freeze one number, then dance

The move: **sort, then for each pivot `i`, solve "Two Sum == -nums[i]" on the suffix with converging pointers** ([3.1](two-sum-ii.md)).

Why sorting first? The two-pointer dance needs sorted order. Sorting costs $O(n \log n)$ once, then each of the $n$ pivots runs an $O(n)$ dance → $O(n^2)$ total, which is optimal (the output itself can be $O(n^2)$).

The subtle part is **deduplication**. Three sources of duplicate triples:

1. **Same first element:** after processing pivot `i`, skip all subsequent indices with `nums[i] == nums[i-1]` — any triple starting with the same value was already enumerated.
2. **Same second/third elements:** inside the dance, after recording a triple `(nums[i], nums[start], nums[end])`, skip the runs of equal `nums[start]` and `nums[end]` before moving both pointers.
3. **The `i < j < k` index rule** is automatically satisfied by construction (`i < start < end`).

The repo's version keeps a `Set<List<Int>>` as a belt-and-suspenders safety net — with the skip discipline it's redundant, but harmless.

## Approach 1 — Brute force

Triple loop over all $\binom{n}{3}$ index triples, dedupe with a set: $O(n^3)$ time, and for $n = 3000$ that's $2.7 \times 10^{10}$ — too slow.

## Approach 2 — Sort + two pointers (optimal)

```kotlin
/**
 * @param nums the input array (may contain duplicates)
 * @return     all distinct triples summing to zero
 */
fun threeSum(nums: IntArray): List<List<Int>> {
    val result = mutableSetOf<List<Int>>()   // set = safety net for duplicates
    nums.sort()

    for (i in nums.indices) {
        // Skip duplicate pivots: same first value -> same triples.
        if (i > 0 && nums[i] == nums[i - 1]) continue

        var start = i + 1
        var end = nums.lastIndex

        while (start < end) {
            val sum = nums[start] + nums[end] + nums[i]
            when {
                sum == 0 -> {
                    result.add(listOf(nums[i], nums[start], nums[end]))

                    // Skip duplicate second/third values.
                    while (start < end && nums[start] == nums[start + 1]) start++
                    while (start < end && nums[end] == nums[end - 1]) end--

                    start++
                    end--
                }
                sum > 0 -> end--       // need a smaller sum
                else    -> start++     // need a bigger sum
            }
        }
    }
    return result.toList()
}
```

```java
import java.util.*;

public class ThreeSum {
    /**
     * @param nums the input array (may contain duplicates)
     * @return     all distinct triples summing to zero
     */
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;   // skip duplicate pivots
            int start = i + 1, end = nums.length - 1;

            while (start < end) {
                int sum = nums[i] + nums[start] + nums[end];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[start], nums[end]));
                    while (start < end && nums[start] == nums[start + 1]) start++;
                    while (start < end && nums[end] == nums[end - 1]) end--;
                    start++;
                    end--;
                } else if (sum > 0) {
                    end--;
                } else {
                    start++;
                }
            }
        }
        return result;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class ThreeSum {
public:
    /**
     * @param nums the input array (may contain duplicates)
     * @return     all distinct triples summing to zero
     */
    std::vector<std::vector<int>> threeSum(std::vector<int>& nums) {
        std::sort(nums.begin(), nums.end());
        std::vector<std::vector<int>> result;

        for (int i = 0; i < (int)nums.size(); i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;   // skip duplicate pivots
            int start = i + 1, end = (int)nums.size() - 1;

            while (start < end) {
                int sum = nums[i] + nums[start] + nums[end];
                if (sum == 0) {
                    result.push_back({nums[i], nums[start], nums[end]});
                    while (start < end && nums[start] == nums[start + 1]) start++;
                    while (start < end && nums[end] == nums[end - 1]) end--;
                    start++;
                    end--;
                } else if (sum > 0) {
                    end--;
                } else {
                    start++;
                }
            }
        }
        return result;
    }
};
```

```python
def three_sum(nums: list[int]) -> list[list[int]]:
    """
    @param nums: the input array (may contain duplicates)
    @return:     all distinct triples summing to zero
    """
    nums.sort()
    result: list[list[int]] = []

    for i in range(len(nums)):
        if i > 0 and nums[i] == nums[i - 1]:
            continue                              # skip duplicate pivots
        start, end = i + 1, len(nums) - 1
        while start < end:
            s = nums[i] + nums[start] + nums[end]
            if s == 0:
                result.append([nums[i], nums[start], nums[end]])
                while start < end and nums[start] == nums[start + 1]:
                    start += 1                    # skip duplicate second values
                while start < end and nums[end] == nums[end - 1]:
                    end -= 1                      # skip duplicate third values
                start += 1
                end -= 1
            elif s > 0:
                end -= 1                          # need a smaller sum
            else:
                start += 1                        # need a bigger sum
    return result
```

```rust
impl Solution {
    /// @param nums the input array (may contain duplicates)
    /// @return     all distinct triples summing to zero
    pub fn three_sum(mut nums: Vec<i32>) -> Vec<Vec<i32>> {
        nums.sort_unstable();
        let mut result: Vec<Vec<i32>> = Vec::new();

        for i in 0..nums.len() {
            if i > 0 && nums[i] == nums[i - 1] {
                continue;                              // skip duplicate pivots
            }
            let (mut start, mut end) = (i + 1, nums.len() - 1);
            while start < end {
                let sum = nums[i] + nums[start] + nums[end];
                if sum == 0 {
                    result.push(vec![nums[i], nums[start], nums[end]]);
                    while start < end && nums[start] == nums[start + 1] { start += 1; }
                    while start < end && nums[end] == nums[end - 1] { end -= 1; }
                    start += 1;
                    end -= 1;
                } else if sum > 0 {
                    end -= 1;                          // need a smaller sum
                } else {
                    start += 1;                        // need a bigger sum
                }
            }
        }
        result
    }
}
```

## Dry run

**Input:** `nums = [-1, 0, 1, 2, -1, -4]`. After sorting: `[-4, -1, -1, 0, 1, 2]`.

```
pivot i=0 (nums[0] = -4):  target = 4
  start=1 end=5: -4 + -1 + 2 = -3 < 0 -> start=2
  start=2 end=5: -4 + -1 + 2 = -3 < 0 -> start=3
  start=3 end=5: -4 + 0 + 2 = -2 < 0  -> start=4
  start=4 end=5: -4 + 1 + 2 = -1 < 0  -> start=5  (start == end, exit)
pivot i=1 (nums[1] = -1):  target = 1
  start=2 end=5: -1 + -1 + 2 = 0  == 0 -> add [-1, -1, 2]
      skip: nums[2]==nums[3]? -1 == -1 -> start=3; nums[5]==nums[4]? 2 vs 1 no
      start=4 end=4 -> exit inner
pivot i=2 (nums[2] = -1):  DUPLICATE of nums[1] -> continue (skip!)
pivot i=3 (nums[3] = 0):   target = 0
  start=4 end=5: 0 + 1 + 2 = 3 > 0 -> end=4  (exit)
pivot i=4 (nums[4] = 1):   target = -1
  start=5: only one element, exit
pivot i=5: nothing after
Result: [[-1, -1, 2]]  — wait, what about [-1, 0, 1]?
```

Let me re-trace pivot i=1 more carefully: sorted = `[-4, -1, -1, 0, 1, 2]`, i=1 (value -1), start=2 (value -1), end=5 (value 2):

```
sum = -1 + -1 + 2 = 0 -> record [-1, -1, 2]
  skip: start(2) == start+1(3)? nums[2]=-1, nums[3]=0 -> NO
  skip: end(5) == end-1(4)? nums[5]=2, nums[4]=1 -> NO
  start=3, end=4
sum = -1 + 0 + 1 = 0 -> record [-1, 0, 1] ✓
  skip: start(3) == 4? 0 vs 1 NO; end(4) == 3? 1 vs 0 NO
  start=4, end=3 -> exit
```

So pivot i=1 finds BOTH triples: `[-1, -1, 2]` and `[-1, 0, 1]`. The duplicate-skip at `i=2` (second -1) is exactly what prevents a duplicate `[-1, 0, 1]` from appearing twice. Result: `[[-1, -1, 2], [-1, 0, 1]]` ✓ — matching the expected output.

**Edge case:** `nums = [0, 0, 0]` → pivot i=0: sum=0 → record `[0,0,0]`, start/end skip runs, done. Pivots i=1, i=2 skipped as duplicates. Output `[[0,0,0]]` ✓.

## Complexity

**Time.** Sort $O(n \log n)$ + one $O(n)$ dance per pivot:

$$
T(n) = O(n \log n) + O(n^2) = O(n^2)
$$

**Space.** $O(1)$ auxiliary (the output list doesn't count), or $O(n)$ with the set-based dedupe.

## Variants & follow-ups

- **3Sum Closest** (`src/main/kotlin/array/twopointer/ThreeSumClosest.kt`) — same dance, but track the *closest* sum instead of exact zeros; no dedup needed.
- **4Sum** (`src/main/kotlin/array/twopointer/4Sum.kt`) — nest the dance twice ($O(n^3)$), same duplicate-skip discipline on both pivots.
- **Two Sum II** ([3.1](two-sum-ii.md)) — the inner dance this whole page is built on.
- **Interview follow-up:** "Why `nums[i] == nums[i-1]` and not `nums[i] == nums[i+1]`?" Checking the *previous* element skips a value only *after* its first occurrence has been processed; checking the *next* would skip it before processing, losing valid triples.
- **Interview follow-up:** "What if the target is `k` instead of 0?" Nothing changes structurally — just compare `sum` against `k` (or target = `k - nums[i]` in the two-sum phase).
