# 3.18 Remove Duplicates From Sorted Array

> **Source:** [`src/main/kotlin/array/twopointer/RemoveDuplicateElementsFromSortedArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/RemoveDuplicateElementsFromSortedArray.kt)
> **Pattern:** write-pointer dedupe · **Core page**

## The Problem

Remove duplicates **in place** from a sorted array; return the new length (the prefix holds unique values).

- Constraints: $1 \le n \le 3 \times 10^4$; sorted.

## Examples

```
Input:  nums = [0,0,1,1,1,2,2,3,3,4]   -> Output: 5, nums = [0,1,2,3,4,...]
```

## Intuition — a write pointer that only advances on change

One read scan, one write pointer: `write` marks where the next *unique* value lands. A value is unique iff it differs from the last written one:

```kotlin
var index = 0
for (i in 1..nums.lastIndex) {
    if (nums[index] != nums[i]) {      // new value
        nums[++index] = nums[i]        // write it next
    }
}
return index + 1
```

**Why compare with `nums[index]`?** `index` always points at the last written unique value — the "frontier". `nums[i]` is new iff it differs from that frontier; equal values are skipped silently. The [3.1](two-sum-ii.md) two-pointer discipline with one pointer doing double duty.

**Why `++index`?** The new value goes *after* the last written one; pre-increment advances the frontier and writes in one expression. The returned length is `index + 1` (the frontier is 0-based).

## Approach 1 — Copy to a new array (O(n) space)

Collect uniques: correct, violates in-place.

## Approach 2 — Write-pointer dedupe (the repo's version, optimal)

```kotlin
class RemoveDuplicateElementsFromSortedArray {
    /**
     * @param nums sorted array (modified in place)
     * @return     new length with unique prefix
     */
    fun removeDuplicates(nums: IntArray): Int {
        var index = 0

        for (i in 1..nums.lastIndex) {
            if (nums[index] != nums[i]) {
                nums[++index] = nums[i]
            }
        }
        return index + 1
    }
}
```

```java
public class RemoveDuplicatesFromSortedArray {
    /**
     * @param nums sorted array (modified in place)
     * @return     new length with unique prefix
     */
    public int removeDuplicates(int[] nums) {
        int write = 0;

        for (int i = 1; i < nums.length; i++) {
            if (nums[write] != nums[i]) {
                nums[++write] = nums[i];
            }
        }
        return write + 1;
    }
}
```

```cpp
#include <vector>

class RemoveDuplicatesFromSortedArray {
public:
    /**
     * @param nums sorted array (modified in place)
     * @return     new length with unique prefix
     */
    int removeDuplicates(std::vector<int>& nums) {
        int write = 0;

        for (int i = 1; i < (int)nums.size(); i++) {
            if (nums[write] != nums[i]) {
                nums[++write] = nums[i];
            }
        }
        return write + 1;
    }
};
```

```python
def remove_duplicates(nums: list[int]) -> int:
    """
    @param nums: sorted array (modified in place)
    @return:     new length with unique prefix
    """
    write = 0

    for i in range(1, len(nums)):
        if nums[write] != nums[i]:
            write += 1
            nums[write] = nums[i]

    return write + 1
```

```rust
impl Solution {
    /// @param nums sorted array (modified in place)
    /// @return     new length with unique prefix
    pub fn remove_duplicates(nums: &mut Vec<i32>) -> i32 {
        let mut write = 0;

        for i in 1..nums.len() {
            if nums[write] != nums[i] {
                write += 1;
                nums[write] = nums[i];
            }
        }
        write as i32 + 1
    }
}
```

## Dry run

**Input:** `nums = [0,0,1,1,1,2,2,3,3,4]`.

```
write=0 (nums[0]=0)
i=1 (0): nums[0]==0 -> skip.
i=2 (1): 0 != 1 -> nums[1]=1.  write=1.
i=3 (1): 1 == 1 -> skip.  i=4 (1): skip.
i=5 (2): 1 != 2 -> nums[2]=2.  write=2.
i=6 (2): skip.
i=7 (3): 2 != 3 -> nums[3]=3.  write=3.
i=8 (3): skip.
i=9 (4): 3 != 4 -> nums[4]=4.  write=4.

return write+1 = 5.  prefix = [0,1,2,3,4] ✓
```

The frontier comparison is the whole trick: every new value is detected by `nums[write] != nums[i]`, and the skipped duplicates never disturb the written prefix. The array's tail holds stale values — the contract only guarantees the first `write+1` entries.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** In place:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Remove Duplicates From Sorted Array II** (`array/twopointer/RemoveDuplicateElementsFromSortedArray_II.kt`) — allow two: the frontier becomes a `count` check.
- **Move Zeroes** — the write-pointer family's zero-collecting member.
- **Interview follow-up:** "Why `nums[write]` and not a separate `last` variable?" The write pointer *is* the last written value's home — no extra state. The pre-increment `++write` writes the new value and advances in one step, and the sorted input is what makes the frontier comparison sufficient.
