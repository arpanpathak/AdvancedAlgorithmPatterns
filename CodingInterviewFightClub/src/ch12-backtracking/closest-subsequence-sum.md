# 12.19 Closest Subsequence Sum

> **Source**: [`src/main/kotlin/array/Combinatorics/ClosestSubsequenceSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/Combinatorics/ClosestSubsequenceSum.kt)
> **Pattern**: meet-in-the-middle subset sums · **Core page**

## The Problem

The subsequence sum closest to `goal` (|sum − goal| minimal).

- Constraints: n ≤ 40 (too big for 2^n).

## Examples

```
Input:  nums = [5,-7,8], goal = 0   -> Output: 0   (5 + (-7) + 8 = 6? no — 5-7 = -2? |−2| = 2... 
  Actually the answer is 0 via the empty subset? |0−0| = 0? The closest is 5? |5|? no goal is 0:
  sums: 0 (diff 0)? |0-0|=0 ✓ -> 0
```

## Intuition — enumerate both halves; bisect the complements

Split nums in half; generate all subset sums per half; for each left sum, binary search the right sum nearest `goal - leftSum`:

```kotlin
fun generateSubsets(nums: IntArray, start: Int, end: Int): List<Int> {
    val sums = mutableListOf<Int>()
    val count = end - start

    for (mask in 0 until (1 shl count)) {
        var sum = 0
        for (i in 0 until count) {
            if ((mask and (1 shl i)) != 0) sum += nums[start + i]
        }
        sums.add(sum)
    }
    return sums
}

val left = generateSubsets(nums, 0, n / 2).toMutableSet().toList()
val right = generateSubsets(nums, n / 2, n).sorted()

var best = Int.MAX_VALUE
for (l in left) {
    val target = goal - l
    val idx = right.binarySearch(target).let { if (it >= 0) it else -it - 1 }

    for (c in listOf(idx - 1, idx, idx + 1)) {
        if (c in right.indices) best = minOf(best, abs(goal - (l + right[c])))
    }
}
return best
```

**Why halves?** 2^40 is impossible; 2×2^20 with a bisect is trivial — the [2.42](../ch02-dynamic-programming/partition-array-into-two-arrays.md) trick, repurposed for nearest-sum.

## Approach 1 — Meet-in-the-middle (the repo's version, optimal)

```kotlin
class ClosestSubsequenceSum {
    /**
     * @param nums input array
     * @param goal target sum
     * @return     min |subsequence sum - goal|
     */
    fun minAbsDifference(nums: IntArray, goal: Int): Int {
        val n = nums.size

        fun generateSubsets(start: Int, end: Int): List<Int> {
            val sums = mutableListOf<Int>()
            val count = end - start

            for (mask in 0 until (1 shl count)) {
                var sum = 0
                for (i in 0 until count) {
                    if ((mask and (1 shl i)) != 0) sum += nums[start + i]
                }
                sums.add(sum)
            }
            return sums
        }

        val left = generateSubsets(0, n / 2).toMutableSet().toList()
        val right = generateSubsets(n / 2, n).sorted()

        var best = Int.MAX_VALUE

        for (l in left) {
            val target = goal - l
            val idx = right.binarySearch(target).let { if (it >= 0) it else -it - 1 }

            for (c in listOf(idx - 1, idx, idx + 1)) {
                if (c in right.indices) {
                    best = minOf(best, abs(goal - (l + right[c])))
                }
            }
        }
        return best
    }
}
```

```java
import java.util.*;

public class ClosestSubsequenceSum {
    private List<Integer> generate(int[] nums, int start, int end) {
        List<Integer> sums = new ArrayList<>();
        int count = end - start;

        for (int mask = 0; mask < (1 << count); mask++) {
            int sum = 0;
            for (int i = 0; i < count; i++) {
                if ((mask & (1 << i)) != 0) sum += nums[start + i];
            }
            sums.add(sum);
        }
        return sums;
    }

    /**
     * @param nums input array
     * @param goal target sum
     * @return     min |subsequence sum - goal|
     */
    public int minAbsDifference(int[] nums, int goal) {
        int n = nums.length;
        List<Integer> left = new ArrayList<>(new HashSet<>(generate(nums, 0, n / 2)));
        List<Integer> right = generate(nums, n / 2, n);
        Collections.sort(right);

        int best = Integer.MAX_VALUE;
        for (int l : left) {
            int target = goal - l;
            int idx = Collections.binarySearch(right, target);
            if (idx < 0) idx = -idx - 1;

            for (int c = idx - 1; c <= idx + 1; c++) {
                if (c >= 0 && c < right.size()) {
                    best = Math.min(best, Math.abs(goal - (l + right.get(c))));
                }
            }
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <cstdlib>

class ClosestSubsequenceSum {
    std::vector<int> generate(std::vector<int>& nums, int start, int end) {
        std::vector<int> sums;
        int count = end - start;

        for (int mask = 0; mask < (1 << count); mask++) {
            int sum = 0;
            for (int i = 0; i < count; i++) {
                if (mask & (1 << i)) sum += nums[start + i];
            }
            sums.push_back(sum);
        }
        return sums;
    }

public:
    /**
     * @param nums input array
     * @param goal target sum
     * @return     min |subsequence sum - goal|
     */
    int minAbsDifference(std::vector<int>& nums, int goal) {
        int n = nums.size();
        auto left = generate(nums, 0, n / 2);
        auto right = generate(nums, n / 2, n);
        std::sort(right.begin(), right.end());

        int best = INT_MAX;
        for (int l : left) {
            int target = goal - l;
            int idx = std::lower_bound(right.begin(), right.end(), target) - right.begin();

            for (int c = idx - 1; c <= idx + 1; c++) {
                if (c >= 0 && c < (int)right.size()) {
                    best = std::min(best, std::abs(goal - (l + right[c])));
                }
            }
        }
        return best;
    }
};
```

```python
from bisect import bisect_left

def min_abs_difference(nums: list[int], goal: int) -> int:
    """
    @param nums: input array
    @param goal: target sum
    @return:     min |subsequence sum - goal|
    """
    def generate(start: int, end: int) -> list[int]:
        sums = []
        count = end - start

        for mask in range(1 << count):
            total = 0
            for i in range(count):
                if mask & (1 << i):
                    total += nums[start + i]
            sums.append(total)

        return sums

    left = set(generate(0, len(nums) // 2))
    right = sorted(generate(len(nums) // 2, len(nums)))

    best = float("inf")
    for l in left:
        target = goal - l
        idx = bisect_left(right, target)

        for c in (idx - 1, idx, idx + 1):
            if 0 <= c < len(right):
                best = min(best, abs(goal - (l + right[c])))

    return best
```

```rust
impl Solution {
    /// @param nums input array
    /// @param goal target sum
    /// @return     min |subsequence sum - goal|
    pub fn min_abs_difference(nums: Vec<i32>, goal: i32) -> i32 {
        fn generate(nums: &Vec<i32>, start: usize, end: usize) -> Vec<i32> {
            let count = end - start;
            let mut sums = Vec::new();

            for mask in 0..(1 << count) {
                let mut sum = 0;
                for i in 0..count {
                    if mask & (1 << i) != 0 { sum += nums[start + i]; }
                }
                sums.push(sum);
            }
            sums
        }

        let n = nums.len();
        let left: std::collections::HashSet<i32> = generate(&nums, 0, n / 2).into_iter().collect();
        let mut right = generate(&nums, n / 2, n);
        right.sort_unstable();

        let mut best = i32::MAX;
        for &l in &left {
            let target = goal - l;
            let idx = right.partition_point(|&x| x < target);

            for &c in [idx.checked_sub(1).unwrap_or(0), idx, (idx + 1).min(right.len())].iter() {
                if c < right.len() {
                    best = best.min((goal - (l + right[c])).abs());
                }
            }
        }
        best
    }
}
```

## Dry run

**Input:** `nums = [5,-7,8], goal = 0`.

```
left half [5]: sums {0, 5}.  right half [-7,8]: sums {0, -7, 8, 1} sorted [-7,0,1,8].
l=0: target 0 -> idx 1 (0): |0-0| = 0.
Output: 0 ✓
```

## Complexity

**Time.** 2^(n/2) log:

$$
T(n) = O(2^{n/2} \log 2^{n/2})
$$

**Space.** The sums:

$$
S(n) = O(2^{n/2})
$$

## Variants & follow-ups

- **Partition Array Into Two Arrays** ([2.42](../ch02-dynamic-programming/partition-array-into-two-arrays.md)) — the same split-and-bisect.
- **Interview follow-up:** "Why must the halves be deduped/sorted?" The left dedupes (a set) to cut the loop; the right sorts for the bisect — both halves of the meet-in-the-middle contract.
