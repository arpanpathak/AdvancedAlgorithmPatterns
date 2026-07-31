# 10.2 Contains Duplicate II

> **Source:** [`src/main/kotlin/array/hashtable/ContainsDuplicate_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/ContainsDuplicate_II.kt)
> **Pattern:** value -> last index · **Core page**

## The Problem

Given an integer array `nums` and an integer `k`, return `true` if there exist two **distinct** indices `i` and `j` with `nums[i] == nums[j]` and `|i - j| <= k`.

- Constraints: $1 \le n \le 10^5$; $0 \le k \le 10^5$.

## Examples

```
Input:  nums = [1,2,3,1], k = 3    -> Output: true   (the two 1s are 3 apart)
Input:  nums = [1,0,1,1], k = 1    -> Output: true   (adjacent 1s)
Input:  nums = [1,2,3,1,2,3], k = 2 -> Output: false  (nearest equal pair is 3 apart)
```

## Intuition — for each value, remember only its *most recent* occurrence

For each index `i`, the question is: *"did this exact value appear within the last k positions?"* That's a **sliding-window membership test** with a twist — the window is by *position*, and we need the *distance* to the previous occurrence.

The map stores `value -> index of its most recent occurrence`. For each `nums[i]`:

- if it was seen before at `prev` and `i - prev <= k` → answer is `true`;
- regardless, **update** `map[nums[i]] = i` — the current index becomes the new "most recent".

**Why store only the most recent index?** Any occurrence older than the most recent is *farther away* than it is. If `i - mostRecent > k`, then `i - anyOlder >= i - mostRecent > k` too — an older occurrence can never satisfy the distance bound if the newest one can't. The "nearest duplicate" is always the most recent one, so one slot per value is enough. (This is the same "keep the best-so-far" compression as the [Chapter 3](../ch03-arrays/index.md) two-pointer minimums.)

**The `k`-window alternative:** a sliding `HashSet` of size `k` (add, and when `i > k`, remove `nums[i-k-1]`) also works and is the more *literal* translation of "window of size k". The map version is one structure and stores strictly less when values repeat.

## Approach 1 — Check all pairs (too slow)

For each value, compare all pairs of its occurrences: $O(n^2)$ in the worst case (all values equal).

## Approach 2 — Value-to-last-index map (the repo's version, optimal)

```kotlin
class ContainsDuplicate_II {
    /**
     * @param nums input array
     * @param k    max allowed distance between equal values
     * @return     true iff some value repeats within distance k
     */
    fun containsNearbyDuplicate(nums: IntArray, k: Int): Boolean {
        val map = mutableMapOf<Int, Int>()          // value -> most recent index

        for (i in nums.indices) {
            val previousIndex = map[nums[i]]

            previousIndex?.let {
                if (i - it <= k) return true        // within distance k
            }
            map[nums[i]] = i                        // refresh the most recent index
        }
        return false
    }
}
```

```java
import java.util.*;

public class ContainsDuplicateII {
    /**
     * @param nums input array
     * @param k    max allowed distance between equal values
     * @return     true iff some value repeats within distance k
     */
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();    // value -> most recent index

        for (int i = 0; i < nums.length; i++) {
            Integer prev = map.get(nums[i]);
            if (prev != null && i - prev <= k) return true;
            map.put(nums[i], i);
        }
        return false;
    }
}
```

```cpp
#include <unordered_map>
#include <vector>

class ContainsDuplicateII {
public:
    /**
     * @param nums input array
     * @param k    max allowed distance between equal values
     * @return     true iff some value repeats within distance k
     */
    bool containsNearbyDuplicate(std::vector<int>& nums, int k) {
        std::unordered_map<int, int> map;               // value -> most recent index

        for (int i = 0; i < (int)nums.size(); i++) {
            if (map.count(nums[i]) && i - map[nums[i]] <= k) return true;
            map[nums[i]] = i;
        }
        return false;
    }
};
```

```python
def contains_nearby_duplicate(nums: list[int], k: int) -> bool:
    """
    @param nums: input array
    @param k:    max allowed distance between equal values
    @return:     true iff some value repeats within distance k
    """
    last = {}                                    # value -> most recent index
    for i, num in enumerate(nums):
        if num in last and i - last[num] <= k:
            return True
        last[num] = i
    return False
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums input array
    /// @param k    max allowed distance between equal values
    /// @return     true iff some value repeats within distance k
    pub fn contains_nearby_duplicate(nums: Vec<i32>, k: i32) -> bool {
        let mut last: HashMap<i32, usize> = HashMap::new();   // value -> most recent index

        for (i, &num) in nums.iter().enumerate() {
            if let Some(&prev) = last.get(&num) {
                if i - prev <= k as usize { return true; }
            }
            last.insert(num, i);
        }
        false
    }
}
```

## Dry run

**Input:** `nums = [1,0,1,1]`, `k = 1`.

```
map = {}
i=0 (1): not seen.  store 1 -> 0.      map={1:0}
i=1 (0): not seen.  store 0 -> 1.      map={1:0, 0:1}
i=2 (1): seen at 0: 2-0 = 2 > k=1 -> no.  refresh: 1 -> 2.   map={1:2, 0:1}
i=3 (1): seen at 2: 3-2 = 1 <= k=1 -> return true ✓
```

The refresh at `i=2` is the whole trick: the duplicate at index 0 is now *irrelevant* — index 2 is closer to any future 1. The stale `1 -> 0` entry is overwritten, so the distance check always runs against the nearest candidate.

## Complexity

**Time.** One pass, $O(1)$ map ops:

$$
T(n) = O(n)
$$

**Space.** One entry per distinct value:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Contains Duplicate I / III** — I is "any duplicate at all" (a plain set); III is "distance AND value-difference bounds" (a sorted structure like a `TreeSet` of window values — the $O(\log k)$ per-op version of this page).
- **Longest Substring Without Repeating Characters** (`src/main/kotlin/string/sliding_window/`) — the same "value -> last index" map, but tracking the *window's left boundary* instead of returning early.
- **Degree Of An Array** (`src/main/kotlin/array/hashtable/DegreeOfAnArray.kt`) — value -> first/last occurrence maps; the "state per value" move extended to two slots per value.
- **Interview follow-up:** "Why does overwriting with the latest index never lose the answer?" Distance to the *nearest* duplicate is always via the *most recent* occurrence — any older occurrence is strictly farther. So the single-slot-per-value compression is exact, not approximate.
