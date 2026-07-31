# 14.2 Kth Largest Element In An Array

> **Source:** [`src/main/kotlin/quicksort/KThLargestElementInArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/quicksort/KThLargestElementInArray.kt)
> **Pattern:** randomized quickselect · **Core page**

## The Problem

Given an array `nums` and an integer `k`, return the **k-th largest** element (not the k-th distinct; `k` is 1-indexed).

- Constraints: $1 \le n \le 10^5$; values fit in `Int`.

## Examples

```
Input:  nums = [3,2,1,5,6,4], k = 2   -> Output: 5   (sorted: [1,2,3,4,5,6], 2nd largest)
Input:  nums = [3,2,3,1,2,4,5,5,6], k = 4 -> Output: 4
```

## Intuition — quicksort's partition, minus the "sort everything"

Sorting the whole array is $O(n \log n)$ — but the k-th largest needs only *one position* to be final. Quicksort's **partition** puts the pivot in its final position and tells you how many elements are on each side. That's everything needed:

- partition around a random pivot;
- if the pivot lands at the target position → done;
- if the pivot is too far left → the answer is in the *right* side; recurse there;
- if too far right → recurse into the left side.

Only one side is ever searched (versus quicksort's two), so the expected work is $n + n/2 + n/4 + \cdots = O(n)$.

**The random pivot is not optional.** With a fixed pivot (say the last element), a sorted or reverse-sorted input makes every partition unbalanced ($1$ and $n-1$) → $O(n^2)$. Randomization makes the expected split balanced — the average-case guarantee is *bought* by the coin flip. (Say this unprompted; it's the whole reason the repo's code calls `Random`.)

**The target index:** with `partition` putting smaller-or-equal elements on the left, the k-th **largest** sits at index `n - k` (0-indexed). The repo's `pivotIndex == nums.size - k` comparison is that translation — the classic off-by-one to be careful about. (`[3,2,1,5,6,4]`, k=2 → target index 4 → value 5 ✓.)

## Approach 1 — Sort and index (O(n log n))

`nums.sorted()[n - k]`: correct and dead simple — and exactly what quickselect beats when the array is big and k is arbitrary.

## Approach 2 — Randomized quickselect (the repo's version, optimal)

```kotlin
import kotlin.random.Random

class KThLargestElementInArray {
    /**
     * Partition nums[left..right] around a random pivot (<= left, > right).
     * @return the pivot's final index
     */
    fun partition(nums: IntArray, left: Int, right: Int): Int {
        // Randomly select pivot index and swap with the last element
        val pivotIndex = Random.nextInt(left, right + 1)
        nums[pivotIndex] = nums[right].also { nums[right] = nums[pivotIndex] }
        val pivot = nums[right]

        var i = left                                  // boundary of "<= pivot" region
        for (j in left until right) {
            if (nums[j] <= pivot) {
                nums[i] = nums[j].also { nums[j] = nums[i] }
                i++
            }
        }
        nums[i] = nums[right].also { nums[right] = nums[i] }   // pivot to its final spot
        return i
    }

    /**
     * @param nums input array
     * @param k    1-indexed rank (largest)
     * @return     the k-th largest element
     */
    fun findKthLargest(nums: IntArray, k: Int): Int {
        var left = 0
        var right = nums.size - 1
        val target = nums.size - k                   // index of the k-th largest

        while (left <= right) {
            val pivotIndex = partition(nums, left, right)
            when {
                pivotIndex == target -> return nums[pivotIndex]
                pivotIndex < target -> left = pivotIndex + 1     // answer in the right side
                else -> right = pivotIndex - 1                   // answer in the left side
            }
        }
        return -1                                    // unreachable for valid k
    }
}
```

```java
import java.util.Random;

public class KthLargestElement {
    private final Random random = new Random();

    /**
     * @param nums input array
     * @param k    1-indexed rank (largest)
     * @return     the k-th largest element
     */
    public int findKthLargest(int[] nums, int k) {
        int left = 0, right = nums.length - 1;
        int target = nums.length - k;                // index of the k-th largest

        while (left <= right) {
            int pivotIndex = partition(nums, left, right);
            if (pivotIndex == target) return nums[pivotIndex];
            if (pivotIndex < target) left = pivotIndex + 1;
            else right = pivotIndex - 1;
        }
        return -1;
    }

    private int partition(int[] nums, int left, int right) {
        int pivotIdx = left + random.nextInt(right - left + 1);
        swap(nums, pivotIdx, right);                 // random pivot to the end
        int pivot = nums[right];

        int i = left;
        for (int j = left; j < right; j++) {
            if (nums[j] <= pivot) swap(nums, i++, j);
        }
        swap(nums, i, right);                        // pivot to its final spot
        return i;
    }

    private void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }
}
```

```cpp
#include <cstdlib>
#include <vector>

class KthLargestElement {
    int partition(std::vector<int>& nums, int left, int right) {
        int pivotIdx = left + std::rand() % (right - left + 1);   // random pivot
        std::swap(nums[pivotIdx], nums[right]);
        int pivot = nums[right];

        int i = left;
        for (int j = left; j < right; j++) {
            if (nums[j] <= pivot) std::swap(nums[i++], nums[j]);
        }
        std::swap(nums[i], nums[right]);            // pivot to its final spot
        return i;
    }

public:
    /**
     * @param nums input array
     * @param k    1-indexed rank (largest)
     * @return     the k-th largest element
     */
    int findKthLargest(std::vector<int>& nums, int k) {
        int left = 0, right = nums.size() - 1;
        int target = nums.size() - k;               // index of the k-th largest

        while (left <= right) {
            int pivotIndex = partition(nums, left, right);
            if (pivotIndex == target) return nums[pivotIndex];
            if (pivotIndex < target) left = pivotIndex + 1;
            else right = pivotIndex - 1;
        }
        return -1;
    }
};
```

```python
import random

def find_kth_largest(nums: list[int], k: int) -> int:
    """
    @param nums: input array
    @param k:    1-indexed rank (largest)
    @return:     the k-th largest element
    """
    def partition(left: int, right: int) -> int:
        pivot_idx = random.randint(left, right)     # random pivot
        nums[pivot_idx], nums[right] = nums[right], nums[pivot_idx]
        pivot = nums[right]

        i = left
        for j in range(left, right):
            if nums[j] <= pivot:
                nums[i], nums[j] = nums[j], nums[i]
                i += 1
        nums[i], nums[right] = nums[right], nums[i]   # pivot to its final spot
        return i

    left, right = 0, len(nums) - 1
    target = len(nums) - k                          # index of the k-th largest

    while left <= right:
        pivot_index = partition(left, right)
        if pivot_index == target:
            return nums[pivot_index]
        if pivot_index < target:
            left = pivot_index + 1
        else:
            right = pivot_index - 1
    return -1
```

```rust
use rand::Rng;

impl Solution {
    /// @param nums input array
    /// @param k    1-indexed rank (largest)
    /// @return     the k-th largest element
    pub fn find_kth_largest(nums: Vec<i32>, k: i32) -> i32 {
        fn partition(nums: &mut Vec<i32>, left: usize, right: usize) -> usize {
            let pivot_idx = left + rand::thread_rng().gen_range(0..right - left + 1);
            nums.swap(pivot_idx, right);
            let pivot = nums[right];

            let mut i = left;
            for j in left..right {
                if nums[j] <= pivot {
                    nums.swap(i, j);
                    i += 1;
                }
            }
            nums.swap(i, right);                   // pivot to its final spot
            i
        }

        let mut nums = nums;
        let mut left = 0usize;
        let mut right = nums.len() - 1;
        let target = nums.len() - k as usize;      // index of the k-th largest

        while left <= right {
            let pivot_index = partition(&mut nums, left, right);
            if pivot_index == target { return nums[pivot_index]; }
            if pivot_index < target { left = pivot_index + 1; }
            else { right = pivot_index - 1; }
        }
        -1
    }
}
```

## Dry run

**Input:** `nums = [3,2,1,5,6,4]`, `k = 2` → `target = 4`.

```
partition(0,5): random pivot, say 4 (value 6):
  partition around 6: [3,2,1,5,4,6], pivot at index 5.
  pivotIndex 5 > target 4 -> right = 4.  (answer is in the LEFT side)
partition(0,4): random pivot, say 1 (value 2):
  partition around 2: [1,2,3,5,4], pivot at index 1.
  pivotIndex 1 < target 4 -> left = 2.  (answer is in the RIGHT side)
partition(2,4): random pivot, say 3 (value 5):
  partition around 5: [1,2,3,4,5], pivot at index 4.
  pivotIndex 4 == target 4 -> return 5 ✓
```

Note that only **one side** is explored at every level — `left`/`right` narrow like binary search, but the partition does real work per step. The expected total is $n + n/2 + n/4 + \cdots = O(n)$; the array is left partially sorted as a side effect (positions ≤ target are the k largest, unordered).

## Complexity

**Time.** Expected (random pivots), one side per level:

$$
T(n) = O(n) \text{ average}, \quad O(n^2) \text{ worst (adversarial pivots)}
$$

**Space.** In-place partition, iterative loop:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **K Closest Points To Origin** ([14.3](k-closest-points-to-origin.md)) — the identical loop with distance as the partition key.
- **Top K Frequent Elements** ([14.7](top-k-frequent-elements-quickselect.md)) — quickselect over *unique* elements keyed by frequency.
- **Heap version** ([7.1](../ch07-heaps/top-k-frequent-elements.md)) — $O(n \log k)$ guaranteed vs $O(n)$ average: the guaranteed-worst-case vs better-average trade-off, stated in one sentence.
- **Interview follow-up:** "Why randomize the pivot?" With a deterministic pivot, an adversarial (or just pre-sorted) input makes every partition maximally unbalanced → $O(n^2)$. Randomization makes the expected split balanced — the $O(n)$ average is *purchased* by the coin flip. This is the difference between quicksort-family algorithms that are "fine in practice" and ones that are "fine provably".
