# 3.37 4Sum

> **Source**: [`src/main/kotlin/array/twopointer/4Sum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/4Sum.kt)
> **Pattern**: k-sum recursion · **Core page**

## The Problem

All distinct quadruplets summing to `target`.

- Constraints: n ≤ 200.

## Examples

```
Input:  nums = [1,0,-1,0,-2,2], target = 0
Output: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]
```

## Intuition — the [3.5](three-sum.md) k-sum generalization

Sort; recurse k down to 2 with the two-pointer base case — dedupe by skipping equal neighbors:

```kotlin
nums.sort()
return kSum(nums, target.toLong(), 0, 4)

fun kSum(nums, target, start, k): List<List<Int>> {
    if (start == nums.size || nums[start] * k > target || target > nums.last() * k) return emptyList()
    if (k == 2) return twoSum(nums, target, start)

    val result = mutableListOf<List<Int>>()
    for (i in start until nums.size) {
        if (i > start && nums[i] == nums[i - 1]) continue
        for (sub in kSum(nums, target - nums[i], i + 1, k - 1)) {
            result.add(listOf(nums[i]) + sub)
        }
    }
    return result
}
```

**Why the pruning bounds?** `nums[start] * k > target` (even the smallest k-tuple exceeds) and `target > nums.last() * k` — the sorted order makes both decisive early exits.

## Approach 1 — kSum recursion (the repo's version, optimal)

```kotlin
class FourSum {
    /**
     * @param nums   input array
     * @param target target sum
     * @return       all quadruplets
     */
    fun fourSum(nums: IntArray, target: Int): List<List<Int>> {
        nums.sort()
        return kSum(nums, target.toLong(), 0, 4)
    }

    private fun kSum(nums: IntArray, target: Long, start: Int, k: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        if (start == nums.size || nums[start].toLong() * k > target ||
            target > nums[nums.size - 1].toLong() * k) return result

        if (k == 2) return twoSum(nums, target, start)

        for (i in start until nums.size) {
            if (i > start && nums[i] == nums[i - 1]) continue

            for (sub in kSum(nums, target - nums[i], i + 1, k - 1)) {
                result.add(listOf(nums[i]) + sub)
            }
        }
        return result
    }

    private fun twoSum(nums: IntArray, target: Long, start: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        var left = start
        var right = nums.size - 1

        while (left < right) {
            val sum = nums[left].toLong() + nums[right].toLong()
            when {
                sum < target -> left++
                sum > target -> right--
                else -> {
                    result.add(listOf(nums[left], nums[right]))
                    left++
                    right--
                    while (left < right && nums[left] == nums[left - 1]) left++
                    while (left < right && nums[right] == nums[right + 1]) right--
                }
            }
        }
        return result
    }
}
```

```java
import java.util.*;

public class FourSum {
    /**
     * @param nums   input array
     * @param target target sum
     * @return       all quadruplets
     */
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        return kSum(nums, target, 0, 4);
    }

    private List<List<Integer>> kSum(int[] nums, long target, int start, int k) {
        List<List<Integer>> result = new ArrayList<>();

        if (start == nums.length || nums[start] * (long) k > target ||
            target > nums[nums.length - 1] * (long) k) return result;

        if (k == 2) return twoSum(nums, target, start);

        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) continue;

            for (List<Integer> sub : kSum(nums, target - nums[i], i + 1, k - 1)) {
                List<Integer> list = new ArrayList<>(sub);
                list.add(0, nums[i]);
                result.add(list);
            }
        }
        return result;
    }

    private List<List<Integer>> twoSum(int[] nums, long target, int start) {
        List<List<Integer>> result = new ArrayList<>();
        int left = start, right = nums.length - 1;

        while (left < right) {
            long sum = (long) nums[left] + nums[right];
            if (sum < target) left++;
            else if (sum > target) right--;
            else {
                result.add(Arrays.asList(nums[left], nums[right]));
                left++;
                right--;
                while (left < right && nums[left] == nums[left - 1]) left++;
                while (left < right && nums[right] == nums[right + 1]) right--;
            }
        }
        return result;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class FourSum {
    std::vector<std::vector<int>> kSum(std::vector<int>& nums, long target, int start, int k) {
        std::vector<std::vector<int>> result;

        if (start == (int)nums.size() || nums[start] * (long)k > target ||
            target > nums.back() * (long)k) return result;

        if (k == 2) {
            int left = start, right = nums.size() - 1;
            while (left < right) {
                long sum = (long)nums[left] + nums[right];
                if (sum < target) left++;
                else if (sum > target) right--;
                else {
                    result.push_back({nums[left], nums[right]});
                    left++; right--;
                    while (left < right && nums[left] == nums[left - 1]) left++;
                    while (left < right && nums[right] == nums[right + 1]) right--;
                }
            }
            return result;
        }

        for (int i = start; i < (int)nums.size(); i++) {
            if (i > start && nums[i] == nums[i - 1]) continue;

            for (auto& sub : kSum(nums, target - nums[i], i + 1, k - 1)) {
                sub.insert(sub.begin(), nums[i]);
                result.push_back(sub);
            }
        }
        return result;
    }

public:
    /**
     * @param nums   input array
     * @param target target sum
     * @return       all quadruplets
     */
    std::vector<std::vector<int>> fourSum(std::vector<int>& nums, int target) {
        std::sort(nums.begin(), nums.end());
        return kSum(nums, target, 0, 4);
    }
};
```

```python
def four_sum(nums: list[int], target: int) -> list[list[int]]:
    """
    @param nums:   input array
    @param target: target sum
    @return:       all quadruplets
    """
    nums.sort()

    def k_sum(start: int, k: int, target: int) -> list[list[int]]:
        if start == len(nums) or nums[start] * k > target or target > nums[-1] * k:
            return []

        if k == 2:
            result = []
            left, right = start, len(nums) - 1
            while left < right:
                s = nums[left] + nums[right]
                if s < target:
                    left += 1
                elif s > target:
                    right -= 1
                else:
                    result.append([nums[left], nums[right]])
                    left += 1
                    right -= 1
                    while left < right and nums[left] == nums[left - 1]: left += 1
                    while left < right and nums[right] == nums[right + 1]: right -= 1
            return result

        result = []
        for i in range(start, len(nums)):
            if i > start and nums[i] == nums[i - 1]:
                continue
            for sub in k_sum(i + 1, k - 1, target - nums[i]):
                result.append([nums[i]] + sub)
        return result

    return k_sum(0, 4, target)
```

```rust
impl Solution {
    /// @param nums   input array
    /// @param target target sum
    /// @return       all quadruplets
    pub fn four_sum(mut nums: Vec<i32>, target: i32) -> Vec<Vec<i32>> {
        nums.sort_unstable();
        let target = target as i64;

        fn k_sum(nums: &Vec<i32>, target: i64, start: usize, k: i32) -> Vec<Vec<i32>> {
            if start == nums.len() || nums[start] as i64 * k as i64 > target
                || target > nums[nums.len() - 1] as i64 * k as i64 { return vec![]; }

            if k == 2 {
                let (mut left, mut right) = (start, nums.len() - 1);
                let mut result = Vec::new();
                while left < right {
                    let sum = nums[left] as i64 + nums[right] as i64;
                    if sum < target { left += 1; }
                    else if sum > target { right -= 1; }
                    else {
                        result.push(vec![nums[left], nums[right]]);
                        left += 1;
                        right -= 1;
                        while left < right && nums[left] == nums[left - 1] { left += 1; }
                        while left < right && nums[right] == nums[right + 1] { right -= 1; }
                    }
                }
                return result;
            }

            let mut result = Vec::new();
            for i in start..nums.len() {
                if i > start && nums[i] == nums[i - 1] { continue; }

                for mut sub in k_sum(nums, target - nums[i] as i64, i + 1, k - 1) {
                    sub.insert(0, nums[i]);
                    result.push(sub);
                }
            }
            result
        }

        k_sum(&nums, target, 0, 4)
    }
}
```

## Dry run

**Input:** `nums = [1,0,-1,0,-2,2]` (sorted: [-2,-1,0,0,1,2]), `target = 0`.

```
kSum(0, 4, 0): i=0 (-2): kSum(1, 3, 2): i=1 (-1): kSum(2, 2, 3): twoSum on [0,0,1,2] target 3:
  (0,2)? 0+2=2 no... (0,0,1,2): pairs summing 3: (1,2) -> [-2,-1,1,2] ✓
  i=2 (0): kSum(3, 2, 2): twoSum [0,1,2] target 2: (0,2) -> [-2,0,0,2] ✓
  i=3 (0): kSum(4, 2, 2): twoSum [1,2] target 2: none.
  i=4 (1): kSum(5, 2, 1): twoSum [2] target 1: none.
  i=1 (-1): kSum(2, 3, 1): i=2 (0): twoSum [0,1,2] target 1: (0,1)? 0+1=1 -> [-1,0,0,1] ✓
Output: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]] ✓
```

## Complexity

**Time.** O(n³):

$$
T(n) = O(n^3)
$$

**Space.** The result:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Three Sum** ([3.5](three-sum.md)) — the k=3 ancestor.
- **Interview follow-up:** "Why does kSum beat nested loops?" One generic recursion handles any k with the same dedupe/pruning logic — the [3.5](three-sum.md) machinery lifted to arbitrary arity.
