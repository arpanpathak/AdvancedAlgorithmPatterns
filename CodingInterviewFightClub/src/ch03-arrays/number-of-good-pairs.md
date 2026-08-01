# 3.45 Number Of Good Pairs

> **Source**: [`src/main/kotlin/array/hashtable/NumberOfGoodPairs.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/NumberOfGoodPairs.kt)
> **Pattern**: running-frequency sum · **Core page**

## The Problem

Count `(i, j)` with `i < j` and `nums[i] == nums[j]`.

- Constraints: n ≤ 100.

## Examples

```
Input:  nums = [1,2,3,1,1,3]   -> Output: 4
```

## Intuition — each new occurrence pairs with every previous one

```kotlin
var goodPairs = 0
val counts = mutableMapOf<Int, Int>()

for (num in nums) {
    val count = counts.getOrDefault(num, 0)
    goodPairs += count
    counts[num] = count + 1
}
```

## Approach 1 — Running-frequency sum (the repo's version, optimal)

```kotlin
class NumberOfGoodPairs {
    /**
     * @param nums input array
     * @return     number of equal pairs
     */
    fun numIdenticalPairs(nums: IntArray): Int {
        var goodPairs = 0
        val counts = mutableMapOf<Int, Int>()

        for (num in nums) {
            val count = counts.getOrDefault(num, 0)
            goodPairs += count
            counts[num] = count + 1
        }
        return goodPairs
    }
}
```

```java
import java.util.*;

public class NumberOfGoodPairs {
    /**
     * @param nums input array
     * @return     number of equal pairs
     */
    public int numIdenticalPairs(int[] nums) {
        int pairs = 0;
        Map<Integer, Integer> counts = new HashMap<>();

        for (int num : nums) {
            int count = counts.getOrDefault(num, 0);
            pairs += count;
            counts.put(num, count + 1);
        }
        return pairs;
    }
}
```

```cpp
#include <vector>
#include <unordered_map>

class NumberOfGoodPairs {
public:
    /**
     * @param nums input array
     * @return     number of equal pairs
     */
    int numIdenticalPairs(std::vector<int>& nums) {
        int pairs = 0;
        std::unordered_map<int, int> counts;

        for (int num : nums) {
            pairs += counts[num];
            counts[num]++;
        }
        return pairs;
    }
};
```

```python
def num_identical_pairs(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     number of equal pairs
    """
    pairs = 0
    counts = {}

    for num in nums:
        pairs += counts.get(num, 0)
        counts[num] = counts.get(num, 0) + 1

    return pairs
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums input array
    /// @return     number of equal pairs
    pub fn num_identical_pairs(nums: Vec<i32>) -> i32 {
        let mut counts: HashMap<i32, i32> = HashMap::new();
        let mut pairs = 0;

        for num in nums {
            let c = counts.entry(num).or_insert(0);
            pairs += *c;
            *c += 1;
        }
        pairs
    }
}
```

## Dry run

**Input:** `nums = [1,2,3,1,1,3]`.

```
1: 0 pairs, count 1.  2: 0.  3: 0.
1: 1 pair, count 2.  1: 2 pairs, count 3.  3: 1 pair, count 2.
Output: 1+2+1 = 4 ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The counts:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why is the running sum exact?" Each occurrence of value v creates pairs with every prior v — adding the current count each time totals exactly C(freq, 2) without computing combinations.
