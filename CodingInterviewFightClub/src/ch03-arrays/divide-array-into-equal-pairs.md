# 3.47 Divide Array Into Equal Pairs

> **Source**: [`src/main/kotlin/array/hashtable/DivideArrayIntoEqualPairs.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/DivideArrayIntoEqualPairs.kt)
> **Pattern**: even-frequency test · **Core page**

## The Problem

Can the array be split into pairs of equal values?

- Constraints: n even.

## Examples

```
Input:  nums = [3,2,3,2,2,2]   -> Output: true
Input:  nums = [1,2,3,4]       -> Output: false
```

## Intuition — pairing needs every frequency even

```kotlin
val freqMap = mutableMapOf<Int, Int>()
for (num in nums) {
    freqMap[num] = freqMap.getOrDefault(num, 0) + 1
}
return freqMap.values.all { it % 2 == 0 }
```

## Approach 1 — Even-frequency test (the repo's version, optimal)

```kotlin
class DivideArrayIntoEqualPairs {
    /**
     * @param nums input array (even length)
     * @return     true iff pairable
     */
    fun divideArray(nums: IntArray): Boolean {
        val freqMap = mutableMapOf<Int, Int>()

        for (num in nums) {
            freqMap[num] = freqMap.getOrDefault(num, 0) + 1
        }
        return freqMap.values.all { it % 2 == 0 }
    }
}
```

```java
import java.util.*;

public class DivideArrayIntoEqualPairs {
    /**
     * @param nums input array (even length)
     * @return     true iff pairable
     */
    public boolean divideArray(int[] nums) {
        Set<Integer> unpaired = new HashSet<>();

        for (int num : nums) {
            if (!unpaired.add(num)) unpaired.remove(num);
        }
        return unpaired.isEmpty();
    }
}
```

```cpp
#include <vector>
#include <unordered_set>

class DivideArrayIntoEqualPairs {
public:
    /**
     * @param nums input array (even length)
     * @return     true iff pairable
     */
    bool divideArray(std::vector<int>& nums) {
        std::unordered_set<int> unpaired;

        for (int num : nums) {
            if (!unpaired.insert(num).second) unpaired.erase(num);
        }
        return unpaired.empty();
    }
};
```

```python
def divide_array(nums: list[int]) -> bool:
    """
    @param nums: input array (even length)
    @return:     true iff pairable
    """
    from collections import Counter
    return all(freq % 2 == 0 for freq in Counter(nums).values())
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param nums input array (even length)
    /// @return     true iff pairable
    pub fn divide_array(nums: Vec<i32>) -> bool {
        let mut unpaired: HashSet<i32> = HashSet::new();

        for num in nums {
            if !unpaired.insert(num) { unpaired.remove(&num); }
        }
        unpaired.is_empty()
    }
}
```

## Dry run

**Input:** `nums = [3,2,3,2,2,2]`.

```
freqs: 3→2, 2→4.  both even -> true ✓
Input: [1,2,3,4]: all 1 -> odd -> false ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The set:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does the toggling set work?" Each value toggles in/out per occurrence — the set holds the odd-count values; empty means all even.
