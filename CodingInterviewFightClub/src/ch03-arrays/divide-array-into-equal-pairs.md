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

## Reading the code — what's actually happening

```kotlin
val freqMap = mutableMapOf<Int, Int>()
for (num in nums) {
    freqMap[num] = freqMap.getOrDefault(num, 0) + 1
}
return freqMap.values.all { it % 2 == 0 }
```

Think about what "split into equal pairs" actually demands: every value must appear an **even** number of times. Three `2`s and one `3` can never be paired up — one of each would be left over. So the whole problem reduces to a frequency parity check.

- **The `for` loop counts occurrences.** `getOrDefault(num, 0) + 1` means "read the current count (0 if unseen), add one, write back". After the pass, `freqMap` holds `value → count` for every distinct number.
- **`values.all { it % 2 == 0 }` is the verdict.** It asks every count: "are you even?" The moment any count is odd, `all` short-circuits to `false`. This is both the check and the proof — an even count means those copies can be grouped into pairs with none left over.
- **The Java/C++/Rust variants use a toggling set instead of a frequency map.** Same logic, cleverer encoding: add a value when first seen, remove it when seen again. Each occurrence flips the value's presence, so the set ends up holding exactly the values with *odd* counts — empty set ⟺ all even. No counting needed at all; the set's size is the answer's fingerprint.

Trace `nums = [3,2,3,2,2,2]`: counts are `3→2`, `2→4` — both even → `true` ✓. For `[1,2,3,4]`: every count is 1 (odd) → `false` ✓.

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
