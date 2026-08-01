# 3.43 Degree Of An Array

> **Source**: [`src/main/kotlin/array/hashtable/DegreeOfAnArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/DegreeOfAnArray.kt)
> **Pattern**: first-last-frequency maps · **Core page**

## The Problem

The smallest subarray whose degree (max frequency) equals the whole array's degree.

- Constraints: n ≤ 5×10⁴.

## Examples

```
Input:  nums = [1,2,2,3,1]     -> Output: 2  ([2,2])
Input:  nums = [1,2,2,3,1,4,2] -> Output: 6
```

## Intuition — the max-frequency value's span is the candidate

Track each value's first index, frequency, and last index; the degree is the max frequency; the answer is the min `last - first + 1` among degree-valued numbers:

```kotlin
val first = HashMap<Int, Int>()
val count = HashMap<Int, Int>()
var maxFreq = 0
var minLength = 0

for ((index, num) in nums.withIndex()) {
    first.putIfAbsent(num, index)
    val freq = (count[num] ?: 0) + 1
    count[num] = freq

    if (freq > maxFreq) {
        maxFreq = freq
        minLength = index - first[num]!! + 1
    } else if (freq == maxFreq) {
        minLength = minOf(minLength, index - first[num]!! + 1)
    }
}
return minLength
```

## Approach 1 — First/count/last maps (the repo's version, optimal)

```kotlin
class DegreeOfAnArray {
    /**
     * @param nums input array
     * @return     smallest subarray with the array's degree
     */
    fun findShortestSubArray(nums: IntArray): Int {
        val first = HashMap<Int, Int>()
        val count = HashMap<Int, Int>()
        var maxFreq = 0
        var minLength = 0

        for ((index, num) in nums.withIndex()) {
            first.putIfAbsent(num, index)
            val freq = (count[num] ?: 0) + 1
            count[num] = freq

            if (freq > maxFreq) {
                maxFreq = freq
                minLength = index - first[num]!! + 1
            } else if (freq == maxFreq) {
                minLength = minOf(minLength, index - first[num]!! + 1)
            }
        }
        return minLength
    }
}
```

```java
import java.util.*;

public class DegreeOfAnArray {
    /**
     * @param nums input array
     * @return     smallest subarray with the array's degree
     */
    public int findShortestSubArray(int[] nums) {
        Map<Integer, Integer> first = new HashMap<>();
        Map<Integer, Integer> count = new HashMap<>();
        int degree = 0, length = 0;

        for (int i = 0; i < nums.length; i++) {
            first.putIfAbsent(nums[i], i);
            int freq = count.getOrDefault(nums[i], 0) + 1;
            count.put(nums[i], freq);

            if (freq > degree) {
                degree = freq;
                length = i - first.get(nums[i]) + 1;
            } else if (freq == degree) {
                length = Math.min(length, i - first.get(nums[i]) + 1);
            }
        }
        return length;
    }
}
```

```cpp
#include <vector>
#include <unordered_map>
#include <algorithm>

class DegreeOfAnArray {
public:
    /**
     * @param nums input array
     * @return     smallest subarray with the array's degree
     */
    int findShortestSubArray(std::vector<int>& nums) {
        std::unordered_map<int, int> first, count;
        int degree = 0, length = 0;

        for (int i = 0; i < (int)nums.size(); i++) {
            if (!first.count(nums[i])) first[nums[i]] = i;
            int freq = ++count[nums[i]];

            if (freq > degree) {
                degree = freq;
                length = i - first[nums[i]] + 1;
            } else if (freq == degree) {
                length = std::min(length, i - first[nums[i]] + 1);
            }
        }
        return length;
    }
};
```

```python
def find_shortest_sub_array(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     smallest subarray with the array's degree
    """
    first, count = {}, {}
    degree = 0
    length = 0

    for i, num in enumerate(nums):
        first.setdefault(num, i)
        freq = count.get(num, 0) + 1
        count[num] = freq

        if freq > degree:
            degree = freq
            length = i - first[num] + 1
        elif freq == degree:
            length = min(length, i - first[num] + 1)

    return length
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums input array
    /// @return     smallest subarray with the array's degree
    pub fn find_shortest_sub_array(nums: Vec<i32>) -> i32 {
        let mut first: HashMap<i32, usize> = HashMap::new();
        let mut count: HashMap<i32, i32> = HashMap::new();
        let (mut degree, mut length) = (0, 0);

        for (i, &num) in nums.iter().enumerate() {
            first.entry(num).or_insert(i);
            let freq = *count.entry(num).or_insert(0) + 1;
            count.insert(num, freq);

            if freq > degree {
                degree = freq;
                length = i - first[&num] + 1;
            } else if freq == degree {
                length = length.min(i - first[&num] + 1);
            }
        }
        length as i32
    }
}
```

## Dry run

**Input:** `nums = [1,2,2,3,1]`.

```
i=0 (1): first 0, freq 1.  degree=1, len=1.
i=1 (2): first 1, freq 1.  no.
i=2 (2): freq 2 > 1 -> degree=2, len = 2-1+1 = 2.
i=3 (3): freq 1.
i=4 (1): freq 2 == degree -> len = min(2, 4-0+1=5) = 2.
Output: 2 ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Three maps:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does the degree tie need the min length?" Multiple values can share the degree — the answer is the shortest span among them; the `freq == degree` branch keeps the running min.
