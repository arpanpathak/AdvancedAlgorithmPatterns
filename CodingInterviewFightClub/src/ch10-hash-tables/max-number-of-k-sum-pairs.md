# 10.21 Max Number Of K-Sum Pairs

> **Source**: [`src/main/kotlin/array/hashtable/MaxNUmWithKSumPairs.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/MaxNUmWithKSumPairs.kt)
> **Pattern**: complement counting map · **Core page**

## The Problem

Max operations: pick two numbers summing to `k`, remove them.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  nums = [1,2,3,4], k = 5   -> Output: 2   ((1,4),(2,3))
Input:  nums = [3,1,3,4,3], k = 6 -> Output: 1
```

## Intuition — the [3.1](../ch03-arrays/two-sum-ii.md) map, consuming matches

For each num: if its complement `k - num` is in the map (unused), consume one and count; else stash `num`:

```kotlin
val map = mutableMapOf<Int, Int>()
var count = 0

for (num in nums) {
    val remainingSum = k - num
    when {
        map.getOrDefault(remainingSum, 0) > 0 -> {
            count++
            map[remainingSum] = map[remainingSum]!! - 1    // consume the complement
        }
        else -> map[num] = map.getOrDefault(num, 0) + 1     // stash for later
    }
}
return count
```

**Why a count-map and not a set?** Duplicates matter — `[3,3,3]` with k=6 can pair once; a set would lose the second 3. The count map tracks *unused* availability ([10.8](subarray-sum-equals-k.md) frequency-map discipline).

## Approach 1 — Sort + two pointers (O(n log n))

Sort, pair from the ends: equally valid, needs the sort.

## Approach 2 — Complement count map (the repo's version, optimal)

```kotlin
class MaxNUmWithKSumPairs {
    /**
     * @param nums input array
     * @param k    target sum
     * @return     max number of pairs
     */
    fun maxOperations(nums: IntArray, k: Int): Int {
        val map = mutableMapOf<Int, Int>()
        var count = 0

        for (num in nums) {
            val remainingSum = k - num
            when {
                map.getOrDefault(remainingSum, 0) > 0 -> {
                    count++
                    map[remainingSum] = map[remainingSum]!! - 1
                }
                else -> map[num] = map.getOrDefault(num, 0) + 1
            }
        }
        return count
    }
}
```

```java
import java.util.*;

public class MaxNumberOfKSumPairs {
    /**
     * @param nums input array
     * @param k    target sum
     * @return     max number of pairs
     */
    public int maxOperations(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        int count = 0;

        for (int num : nums) {
            int complement = k - num;
            if (map.getOrDefault(complement, 0) > 0) {
                count++;
                map.put(complement, map.get(complement) - 1);    // consume
            } else {
                map.put(num, map.getOrDefault(num, 0) + 1);      // stash
            }
        }
        return count;
    }
}
```

```cpp
#include <vector>
#include <unordered_map>

class MaxNumberOfKSumPairs {
public:
    /**
     * @param nums input array
     * @param k    target sum
     * @return     max number of pairs
     */
    int maxOperations(std::vector<int>& nums, int k) {
        std::unordered_map<int, int> map;
        int count = 0;

        for (int num : nums) {
            int complement = k - num;
            if (map[complement] > 0) {
                count++;
                map[complement]--;          // consume
            } else {
                map[num]++;                 // stash
            }
        }
        return count;
    }
};
```

```python
def max_operations(nums: list[int], k: int) -> int:
    """
    @param nums: input array
    @param k:    target sum
    @return:     max number of pairs
    """
    counts = {}
    pairs = 0

    for num in nums:
        complement = k - num
        if counts.get(complement, 0) > 0:
            pairs += 1
            counts[complement] -= 1        # consume
        else:
            counts[num] = counts.get(num, 0) + 1   # stash

    return pairs
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums input array
    /// @param k    target sum
    /// @return     max number of pairs
    pub fn max_operations(nums: Vec<i32>, k: i32) -> i32 {
        let mut counts: HashMap<i32, i32> = HashMap::new();
        let mut pairs = 0;

        for num in nums {
            let complement = k - num;
            if counts.get(&complement).copied().unwrap_or(0) > 0 {
                pairs += 1;
                *counts.get_mut(&complement).unwrap() -= 1;    // consume
            } else {
                *counts.entry(num).or_insert(0) += 1;          // stash
            }
        }
        pairs
    }
}
```

## Dry run

**Input:** `nums = [3,1,3,4,3], k = 6`.

```
3: complement 3? map empty -> stash {3:1}
1: complement 5? no -> stash {3:1, 1:1}
3: complement 3? yes (1 left) -> pairs=1, consume -> {3:0, 1:1}
4: complement 2? no -> stash {3:0, 1:1, 4:1}
3: complement 3? 0 -> stash {3:1, ...}

Output: 1 ✓
```

The consume/stash asymmetry is the pairing logic: a num that finds its complement *uses it up* (the pair is complete and removed); otherwise the num waits for a future complement. The count map's decrement is the "removal" — duplicates handled by counts.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The count map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Two Sum** ([3.1](../ch03-arrays/two-sum-ii.md)) — the ancestor: one pair, set-based.
- **Interview follow-up:** "Why a count-map instead of a set?" Pairs consume *multiplicities* — `[3,3,3]` k=6 has one pair, which a set (allowing at most one) would still get right, but `[1,1,2,2]` k=3 needs counts to allow the second (1,2). The decrement models removal exactly.
