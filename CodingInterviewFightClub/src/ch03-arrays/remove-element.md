# 3.36 Remove Element

> **Source**: [`src/main/kotlin/array/RemoveElement.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/RemoveElement.kt)
> **Pattern**: write-pointer filter · **Core page**

## The Problem

Remove all occurrences of `val` in place; return the new length.

- Constraints: n ≤ 100.

## Examples

```
Input:  nums = [3,2,2,3], val = 3   -> Output: 2 ([2,2])
```

## Intuition — the [3.18](remove-duplicates-from-sorted-array.md) write pointer, filtering

```kotlin
var i = 0
for (j in nums.indices) {
    if (nums[j] != `val`) {
        nums[i++] = nums[j]
    }
}
return i
```

## Approach 1 — Write-pointer filter (the repo's version, optimal)

```kotlin
class RemoveElement {
    /**
     * @param nums array (mutated)
     * @param val  value to remove
     * @return     new length
     */
    fun removeElement(nums: IntArray, `val`: Int): Int {
        var i = 0

        for (j in nums.indices) {
            if (nums[j] != `val`) {
                nums[i++] = nums[j]
            }
        }
        return i
    }
}
```

```java
public class RemoveElement {
    /**
     * @param nums array (mutated)
     * @param val  value to remove
     * @return     new length
     */
    public int removeElement(int[] nums, int val) {
        int i = 0;
        for (int j = 0; j < nums.length; j++) {
            if (nums[j] != val) nums[i++] = nums[j];
        }
        return i;
    }
}
```

```cpp
#include <vector>

class RemoveElement {
public:
    /**
     * @param nums array (mutated)
     * @param val  value to remove
     * @return     new length
     */
    int removeElement(std::vector<int>& nums, int val) {
        int i = 0;
        for (int j = 0; j < (int)nums.size(); j++) {
            if (nums[j] != val) nums[i++] = nums[j];
        }
        return i;
    }
};
```

```python
def remove_element(nums: list[int], val: int) -> int:
    """
    @param nums: array (mutated)
    @param val:  value to remove
    @return:     new length
    """
    i = 0
    for j, num in enumerate(nums):
        if num != val:
            nums[i] = num
            i += 1
    return i
```

```rust
impl Solution {
    /// @param nums array (mutated)
    /// @param val  value to remove
    /// @return     new length
    pub fn remove_element(nums: &mut Vec<i32>, val: i32) -> i32 {
        let mut i = 0;
        for j in 0..nums.len() {
            if nums[j] != val { nums[i] = nums[j]; i += 1; }
        }
        i as i32
    }
}
```

## Dry run

**Input:** `nums = [3,2,2,3], val = 3`.

```
j=0 (3): skip.  j=1 (2): nums[0]=2.  j=2 (2): nums[1]=2.  j=3 (3): skip.
Output: 2, nums = [2,2,...] ✓
```

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

- **Remove Duplicates From Sorted Array** ([3.18](remove-duplicates-from-sorted-array.md)) — the identical write-pointer.
- **Interview follow-up:** "Why is the write pointer safe?" It never exceeds the read pointer — overwriting earlier slots can't clobber unread input.
