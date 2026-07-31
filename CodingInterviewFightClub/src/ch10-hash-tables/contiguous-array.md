# 10.24 Contiguous Array

> **Source**: [`src/main/kotlin/array/prefixsum/ContiguousArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/ContiguousArray.kt)
> **Pattern**: prefix-sum map with first-occurrence · **Core page**

## The Problem

The longest subarray with **equal** 0s and 1s.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  nums = [0,1]        -> Output: 2
Input:  nums = [0,1,0]      -> Output: 2   ([0,1] or [1,0])
```

## Intuition — treat 0 as −1; a repeated prefix sum marks a balanced window

Map 0→−1, 1→+1. A subarray is balanced iff its prefix sums are equal — `sum[i] == sum[j]` means `nums[i+1..j]` sums to 0. Store the **first** occurrence of each sum:

```kotlin
val map = mutableMapOf<Int, Int>()
var sum = 0
var maxLen = 0
map[0] = -1        // the empty prefix: sum 0 at index -1

for (i in nums.indices) {
    sum += if (nums[i] == 0) -1 else 1

    if (map.containsKey(sum)) {
        maxLen = maxOf(maxLen, i - map[sum]!!)   // balanced window between the two
    } else {
        map[sum] = i                              // first occurrence
    }
}
return maxLen
```

**Why keep the first occurrence?** The longest window for a repeated sum is from the *earliest* occurrence — later ones only shorten it. The [10.8](subarray-sum-equals-k.md) prefix-frequency map, keeping min-index instead of count.

## Approach 1 — Brute force windows (O(n²))

Check every subarray: correct, slow.

## Approach 2 — Prefix-sum first-occurrence (the repo's version, optimal)

```kotlin
class ContiguousArray {
    /**
     * @param nums binary array
     * @return     longest balanced subarray length
     */
    fun findMaxLength(nums: IntArray): Int {
        val map = mutableMapOf<Int, Int>()
        var sum = 0
        var maxLen = 0
        map[0] = -1

        for (i in nums.indices) {
            sum += if (nums[i] == 0) -1 else 1

            if (map.containsKey(sum)) {
                maxLen = maxOf(maxLen, i - map[sum]!!)
            } else {
                map[sum] = i
            }
        }
        return maxLen
    }
}
```

```java
import java.util.*;

public class ContiguousArray {
    /**
     * @param nums binary array
     * @return     longest balanced subarray length
     */
    public int findMaxLength(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, -1);
        int sum = 0, best = 0;

        for (int i = 0; i < nums.length; i++) {
            sum += nums[i] == 0 ? -1 : 1;

            if (map.containsKey(sum)) {
                best = Math.max(best, i - map.get(sum));
            } else {
                map.put(sum, i);
            }
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <unordered_map>

class ContiguousArray {
public:
    /**
     * @param nums binary array
     * @return     longest balanced subarray length
     */
    int findMaxLength(std::vector<int>& nums) {
        std::unordered_map<int, int> map;
        map[0] = -1;
        int sum = 0, best = 0;

        for (int i = 0; i < (int)nums.size(); i++) {
            sum += nums[i] == 0 ? -1 : 1;

            if (map.count(sum)) best = std::max(best, i - map[sum]);
            else map[sum] = i;
        }
        return best;
    }
};
```

```python
def find_max_length(nums: list[int]) -> int:
    """
    @param nums: binary array
    @return:     longest balanced subarray length
    """
    first = {0: -1}
    total = best = 0

    for i, num in enumerate(nums):
        total += -1 if num == 0 else 1

        if total in first:
            best = max(best, i - first[total])
        else:
            first[total] = i

    return best
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums binary array
    /// @return     longest balanced subarray length
    pub fn find_max_length(nums: Vec<i32>) -> i32 {
        let mut first: HashMap<i32, i32> = HashMap::new();
        first.insert(0, -1);
        let (mut sum, mut best) = (0, 0);

        for (i, &num) in nums.iter().enumerate() {
            sum += if num == 0 { -1 } else { 1 };

            if let Some(&j) = first.get(&sum) {
                best = best.max(i as i32 - j);
            } else {
                first.insert(sum, i as i32);
            }
        }
        best
    }
}
```

## Dry run

**Input:** `nums = [0,1,0]`.

```
sum=0, map {0:-1}
i=0 (0): sum=-1.  not in map -> map[-1]=0.
i=1 (1): sum=0.  in map at -1 -> best = 1-(-1) = 2.
i=2 (0): sum=-1.  in map at 0 -> best = max(2, 2-0) = 2.

Output: 2 ✓
```

The repeated sum −1 at indices 0 and 2 brackets the balanced window `[1,0]`. The `map[0] = -1` sentinel makes whole-prefix windows (like `[0,1]`) measurable.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Subarray Sum Equals K** ([10.8](subarray-sum-equals-k.md)) — the frequency-map cousin.
- **Interview follow-up:** "Why the −1/+1 encoding?" Equal counts ⟺ sum 0 under the encoding — the problem becomes "longest zero-sum subarray", which the prefix-sum repetition test answers in one pass.
