# 2.42 Partition Array Into Two Arrays To Minimize Sum Difference

> **Source**: [`src/main/kotlin/array/dp/PartitionArrayIntoTwoArrayToMinimuzeSumDifference.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/PartitionArrayIntoTwoArrayToMinimuzeSumDifference.kt)
> **Pattern**: meet-in-the-middle subset sums · **Core page**

## The Problem

Split `nums` (length 2n) into two **equal-size** arrays minimizing the absolute sum difference.

- Constraints: n ≤ 15 (so 2n ≤ 30).

## Examples

```
Input:  nums = [3,9,7,3]   -> Output: 2  ([3,9] vs [7,3]: 12 vs 10)
```

## Intuition — enumerate half-sums by size; bisect the other half

The [12.x](../ch12-backtracking/pattern-primer.md) meet-in-the-middle: enumerate all size-k subset sums of each half. For each left size-k sum, the ideal right partner is `(total/2) - leftSum` — a binary search over the right's size-(n-k) sums:

```kotlin
val n = nums.size
val totalSum = nums.sum()
val halfSize = n / 2

val leftSubsets = generateSubsetsBySize(nums, 0, halfSize)
val rightSubsets = generateSubsetsBySize(nums, halfSize, n)

// for each size k on the left, binary search the right's size-(n-k) sums
```

**Why meet-in-the-middle?** Full subset enumeration is 2^30 (too big); half-and-half is 2×2^15 with a bisect — the classic NP-hard trick. Each side's sums are bucketed by size so the equal-size constraint holds.

## Approach 1 — Meet-in-the-middle (the repo's version, optimal)

```kotlin
class PartitionArrayIntoTwoArrayToMinimuzeSumDifference {
    /**
     * @param nums even-length array
     * @return     min equal-size partition difference
     */
    fun minimumDifference(nums: IntArray): Int {
        val n = nums.size
        val totalSum = nums.sum()
        val halfSize = n / 2

        fun generateSubsetsBySize(start: Int, end: Int): Array<MutableList<Int>> {
            val subsets = Array(halfSize + 1) { mutableListOf<Int>() }
            val count = end - start

            for (mask in 0 until (1 shl count)) {
                var sum = 0
                var size = 0
                for (i in 0 until count) {
                    if ((mask and (1 shl i)) != 0) {
                        sum += nums[start + i]
                        size++
                    }
                }
                subsets[size].add(sum)
            }
            return subsets
        }

        val leftSubsets = generateSubsetsBySize(0, halfSize)
        val rightSubsets = generateSubsetsBySize(halfSize, n)
        rightSubsets.forEach { it.sort() }

        var minDiff = Int.MAX_VALUE

        for (k in 0..halfSize) {
            val leftSums = leftSubsets[k]
            val rightSums = rightSubsets[halfSize - k]

            for (leftSum in leftSums) {
                val target = (totalSum / 2) - leftSum

                val idx = rightSums.binarySearch(target).let { if (it >= 0) it else -it - 1 }

                for (candidate in listOf(idx - 1, idx, idx + 1)) {
                    if (candidate in rightSums.indices) {
                        val diff = abs(totalSum - 2 * (leftSum + rightSums[candidate]))
                        minDiff = minOf(minDiff, diff)
                    }
                }
            }
        }
        return minDiff
    }
}
```

```java
import java.util.*;

public class PartitionArrayIntoTwoArrays {
    private int[] nums;
    private int half;

    private List<Integer>[] sumsBySize(int start, int end) {
        List<Integer>[] result = new List[half + 1];
        for (int i = 0; i <= half; i++) result[i] = new ArrayList<>();
        int count = end - start;

        for (int mask = 0; mask < (1 << count); mask++) {
            int sum = 0, size = 0;
            for (int i = 0; i < count; i++) {
                if ((mask & (1 << i)) != 0) { sum += nums[start + i]; size++; }
            }
            result[size].add(sum);
        }
        return result;
    }

    /**
     * @param nums even-length array
     * @return     min equal-size partition difference
     */
    public int minimumDifference(int[] nums) {
        this.nums = nums;
        int n = nums.length;
        half = n / 2;
        int total = 0;
        for (int num : nums) total += num;

        List<Integer>[] left = sumsBySize(0, half);
        List<Integer>[] right = sumsBySize(half, n);
        for (List<Integer> list : right) Collections.sort(list);

        int best = Integer.MAX_VALUE;
        for (int k = 0; k <= half; k++) {
            List<Integer> leftSums = left[k];
            List<Integer> rightSums = right[half - k];

            for (int ls : leftSums) {
                int target = total / 2 - ls;
                int idx = Collections.binarySearch(rightSums, target);
                if (idx < 0) idx = -idx - 1;

                for (int c = idx - 1; c <= idx + 1; c++) {
                    if (c >= 0 && c < rightSums.size()) {
                        best = Math.min(best, Math.abs(total - 2 * (ls + rightSums.get(c))));
                    }
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

class PartitionArrayIntoTwoArrays {
    std::vector<std::vector<int>> sumsBySize(std::vector<int>& nums, int start, int end, int half) {
        std::vector<std::vector<int>> result(half + 1);
        int count = end - start;

        for (int mask = 0; mask < (1 << count); mask++) {
            int sum = 0, size = 0;
            for (int i = 0; i < count; i++) {
                if (mask & (1 << i)) { sum += nums[start + i]; size++; }
            }
            result[size].push_back(sum);
        }
        return result;
    }

public:
    /**
     * @param nums even-length array
     * @return     min equal-size partition difference
     */
    int minimumDifference(std::vector<int>& nums) {
        int n = nums.size(), half = n / 2, total = 0;
        for (int num : nums) total += num;

        auto left = sumsBySize(nums, 0, half, half);
        auto right = sumsBySize(nums, half, n, half);
        for (auto& list : right) std::sort(list.begin(), list.end());

        int best = INT_MAX;
        for (int k = 0; k <= half; k++) {
            for (int ls : left[k]) {
                int target = total / 2 - ls;
                auto& rs = right[half - k];
                int idx = std::lower_bound(rs.begin(), rs.end(), target) - rs.begin();

                for (int c = idx - 1; c <= idx + 1; c++) {
                    if (c >= 0 && c < (int)rs.size()) {
                        best = std::min(best, std::abs(total - 2 * (ls + rs[c])));
                    }
                }
            }
        }
        return best;
    }
};
```

```python
def minimum_difference(nums: list[int]) -> int:
    """
    @param nums: even-length array
    @return:     min equal-size partition difference
    """
    n = len(nums)
    half = n // 2
    total = sum(nums)

    def sums_by_size(start: int, end: int) -> list[list[int]]:
        subsets = [[] for _ in range(half + 1)]
        count = end - start

        for mask in range(1 << count):
            s = size = 0
            for i in range(count):
                if mask & (1 << i):
                    s += nums[start + i]
                    size += 1
            subsets[size].append(s)
        return subsets

    left = sums_by_size(0, half)
    right = sums_by_size(half, n)
    for lst in right:
        lst.sort()

    best = float("inf")
    for k in range(half + 1):
        for ls in left[k]:
            target = total // 2 - ls
            rs = right[half - k]
            idx = bisect_left(rs, target)

            for c in (idx - 1, idx, idx + 1):
                if 0 <= c < len(rs):
                    best = min(best, abs(total - 2 * (ls + rs[c])))

    return best
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums even-length array
    /// @return     min equal-size partition difference
    pub fn minimum_difference(nums: Vec<i32>) -> i32 {
        let n = nums.len();
        let half = n / 2;
        let total: i32 = nums.iter().sum();

        // bucket subsets by size
        let mut left: HashMap<usize, Vec<i32>> = HashMap::new();
        let mut right: HashMap<usize, Vec<i32>> = HashMap::new();

        for (mask, map, start) in [(0, &mut left, 0usize), (0, &mut right, half)] {
            for m in 0..(1 << half) {
                let (mut sum, mut size) = (0, 0);
                for i in 0..half {
                    if m & (1 << i) != 0 { sum += nums[start + i]; size += 1; }
                }
                map.entry(size).or_default().push(sum);
            }
        }

        for (_, v) in right.iter_mut() { v.sort_unstable(); }

        let mut best = i32::MAX;
        for k in 0..=half {
            for &ls in &left[&k] {
                let target = total / 2 - ls;
                let rs = &right[&(half - k)];
                let idx = rs.partition_point(|&x| x < target);

                for &c in [idx.checked_sub(1).unwrap_or(0), idx, (idx + 1).min(rs.len())].iter() {
                    if c < rs.len() {
                        best = best.min((total - 2 * (ls + rs[c])).abs());
                    }
                }
            }
        }
        best
    }
}
```

## Dry run

**Input:** `nums = [3,9,7,3]`.

```
total 22, half 2.
left subsets by size: k0: [0].  k1: [3,9].  k2: [3+9=12].
right: k0: [0].  k1: [7,3].  k2: [7+3=10].
k=1: left [3,9] vs right size 1 [3,7]:
  ls=3: target 11-3=8.  rs sorted [3,7]: bisect 8 -> idx 2 -> candidates 7: |22-2*10|=2.
  ls=9: target 2: candidate 3: |22-2*12|=2.
best = 2 ✓
```

## Complexity

**Time.** 2^(n/2) per half:

$$
T(n) = O(2^{n/2} \cdot \log 2^{n/2})
$$

**Space.** The buckets:

$$
S(n) = O(2^{n/2})
$$

## Variants & follow-ups

- **Partition Equal Subset Sum** ([2.x](../ch02-dynamic-programming/pattern-primer.md)) — the equal-sum special case.
- **Interview follow-up:** "Why the size buckets?" The partition requires equal-size arrays — pairing left's size-k sums with right's size-(n-k) sums enforces it; the bisect makes the pairing O(log) per candidate.
