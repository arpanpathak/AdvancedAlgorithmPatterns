# 10.1 Two Sum

> **Source:** the repo's sibling is [`src/main/kotlin/array/twopointer/TwoSum_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/TwoSum_II.kt) (the *sorted* variant, covered as [3.1](../ch03-arrays/two-sum-ii.md)); this page is the classic *unsorted* hash-map version.
> **Pattern:** complement lookup · **Core page**

## The Problem

Given an array of integers `nums` and a target `target`, return the **indices** of the two numbers that add up to `target`. Exactly one solution exists; you may not use the same element twice.

- Constraints: $2 \le n \le 10^4$; $-10^9 \le nums[i], target \le 10^9$.

## Examples

```
Input:  nums = [2,7,11,15], target = 9
Output: [0,1]        (2 + 7 = 9)

Input:  nums = [3,2,4], target = 6
Output: [1,2]        (2 + 4, NOT [0,0] — can't reuse the same 3)
```

## Intuition — "does my complement exist already?"

For each element `x`, the partner it needs is `target - x`. The naive way finds that partner by scanning the rest of the array — $O(n^2)$. The hash-map move: **remember every value you've already seen, keyed by value, mapped to its index.** Then for each `x`, one lookup answers "have I already passed my complement?":

- if `target - x` is in the map → the pair is `(map[target - x], current)` — done;
- otherwise, store `x -> i` and move on.

**Why does "already seen" suffice?** Every pair consists of a *later* element and an *earlier* one. When the later element is processed, its complement is already in the map — so every pair is found exactly when its second element is visited. No need to look ahead.

**Why this beats sorting + two pointers here?** Sorting destroys the original indices (the answer needs them). [3.1](../ch03-arrays/two-sum-ii.md) works only when the input is *already sorted*. The map version works on unsorted input and returns original indices — which is why it's the answer for the classic statement of this problem.

## Approach 1 — Nested loops (too slow)

For each pair, check the sum: $O(n^2)$ time, $O(1)$ space. The first thing to reject.

## Approach 2 — One-pass complement lookup (optimal)

```kotlin
/**
 * @param nums   array of integers (exactly one solution exists)
 * @param target desired sum
 * @return       indices of the two elements summing to target
 */
fun twoSum(nums: IntArray, target: Int): IntArray {
    val seen = mutableMapOf<Int, Int>()          // value -> index (already visited)

    for (i in nums.indices) {
        val complement = target - nums[i]
        seen[complement]?.let { return intArrayOf(it, i) }   // partner was seen earlier
        seen[nums[i]] = i
    }
    return intArrayOf()                          // unreachable: solution guaranteed
}
```

```java
import java.util.*;

public class TwoSum {
    /**
     * @param nums   array of integers (exactly one solution exists)
     * @param target desired sum
     * @return       indices of the two elements summing to target
     */
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();   // value -> index

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            seen.put(nums[i], i);
        }
        return new int[0];
    }
}
```

```cpp
#include <unordered_map>
#include <vector>

class TwoSum {
public:
    /**
     * @param nums   array of integers (exactly one solution exists)
     * @param target desired sum
     * @return       indices of the two elements summing to target
     */
    std::vector<int> twoSum(std::vector<int>& nums, int target) {
        std::unordered_map<int, int> seen;               // value -> index

        for (int i = 0; i < (int)nums.size(); i++) {
            int complement = target - nums[i];
            if (seen.count(complement)) {
                return {seen[complement], i};
            }
            seen[nums[i]] = i;
        }
        return {};
    }
};
```

```python
def two_sum(nums: list[int], target: int) -> list[int]:
    """
    @param nums:   array of integers (exactly one solution exists)
    @param target: desired sum
    @return:       indices of the two elements summing to target
    """
    seen = {}                                  # value -> index (already visited)

    for i, num in enumerate(nums):
        complement = target - num
        if complement in seen:
            return [seen[complement], i]
        seen[num] = i
    return []
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums   array of integers (exactly one solution exists)
    /// @param target desired sum
    /// @return       indices of the two elements summing to target
    pub fn two_sum(nums: Vec<i32>, target: i32) -> Vec<i32> {
        let mut seen: HashMap<i32, usize> = HashMap::new();   // value -> index

        for (i, &num) in nums.iter().enumerate() {
            let complement = target - num;
            if let Some(&j) = seen.get(&complement) {
                return vec![j as i32, i as i32];
            }
            seen.insert(num, i);
        }
        vec![]
    }
}
```

## Dry run

**Input:** `nums = [3,2,4]`, `target = 6`.

```
seen = {}
i=0 (3): complement = 6-3 = 3. 3 in seen? no.  store 3 -> 0.  seen={3:0}
i=1 (2): complement = 4.       4 in seen? no.  store 2 -> 1.  seen={3:0, 2:1}
i=2 (4): complement = 2.       2 in seen? YES at index 1 -> return [1,2] ✓
```

The trap this exposes: the *element itself* is not the partner — `3 + 3` would need the same index twice, which the "store after checking" order forbids. Checking the complement *before* storing the current value is what prevents `[0,0]`.

## Complexity

**Time.** One pass, $O(1)$ map ops per element:

$$
T(n) = O(n)
$$

**Space.** At most one entry per distinct value:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Two Sum II (Sorted)** ([3.1](../ch03-arrays/two-sum-ii.md), repo `array/twopointer/TwoSum_II.kt`) — sorted input: two pointers, $O(1)$ space. Choosing between the two pages *based on whether the input is sorted* is the one-sentence interview answer.
- **Three Sum** ([3.2](../ch03-arrays/three-sum.md)) — sort + two pointers on the *pair* sum; the map-based "complement of the complement" for k-sums explodes combinatorially, which is why the sorted version wins there.
- **Contains Duplicate II** ([10.2](contains-duplicate-ii.md)) — the same map, but the *distance* becomes the question instead of the pair itself.
- **Interview follow-up:** "Why not sort and binary search?" Sorting reorders the array, losing the original indices the answer must return — and $O(n)$ with a map beats $O(n \log n)$ with a sort anyway. The map is both faster *and* preserves position.
