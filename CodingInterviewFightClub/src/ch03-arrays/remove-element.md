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

## Reading the code — what's actually happening

```kotlin
var i = 0
for (j in nums.indices) {
    if (nums[j] != `val`) {
        nums[i++] = nums[j]
    }
}
return i
```

The trick is the **two-role pointer**: `j` reads the whole array (it's the "scanner"), while `i` marks where the next *kept* element should be written (it's the "writer"). They start together but `i` only moves when we keep something, so the kept elements compact to the front.

- **`j` is the read pointer — it visits every index exactly once.** Each element is examined exactly once and classified: keep it (≠ val) or drop it (= val).
- **`i` is the write pointer — it advances only on kept elements.** When `nums[j] != val`, we copy that value to position `i` and advance. When `nums[j] == val`, `i` stays put — the next kept element will overwrite this slot.
- **Why is overwriting safe?** `i` never exceeds `j` (the writer can't get ahead of the reader), so writing to `nums[i]` can only touch positions the scanner has *already passed*. We never clobber an unread element — the "in-place" guarantee holds without any auxiliary array.
- **The returned `i` is the new length.** Since the first `i` positions hold all kept elements in their original relative order, `i` is exactly the number of survivors — and the problem only requires the prefix to be correct, which it is.

Trace `nums = [3,2,2,3], val = 3`: `j=0` (3): drop, `i=0`; `j=1` (2): keep → `nums[0]=2`, `i=1`; `j=2` (2): keep → `nums[1]=2`, `i=2`; `j=3` (3): drop. Return `2` — and `nums = [2,2,...]` ✓.

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
