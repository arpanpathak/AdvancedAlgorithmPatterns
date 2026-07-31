# 3.35 Remove Duplicates From Sorted Array II

> **Source**: [`src/main/kotlin/array/twopointer/RemoveDuplicateElementsFromSortedArray_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/RemoveDuplicateElementsFromSortedArray_II.kt)
> **Pattern**: write-pointer with a run counter · **Core page**

## The Problem

Remove duplicates **in place**, allowing at most **two** of each; return the new length.

- Constraints: n ≤ 3×10⁴.

## Examples

```
Input:  nums = [1,1,1,2,2,3]   -> Output: 5  ([1,1,2,2,3])
```

## Intuition — the [3.18](remove-duplicates-from-sorted-array.md) write pointer with a count

Track the run length; write a value only if its run ≤ 2:

```kotlin
var index = 0
var count = 1

for (i in 1..nums.lastIndex) {
    if (nums[i] == nums[i - 1]) count++ else count = 1

    if (count <= 2) {
        index++
        nums[index] = nums[i]
    }
}
return index + 1
```

**Why reset the count on a new value?** The run-length is per-value — a fresh value always gets written (count 1), and only the third+ occurrence of a run is skipped.

## Approach 1 — Count-gated write pointer (the repo's version, optimal)

```kotlin
class RemoveDuplicateElementsFromSortedArray_II {
    /**
     * @param nums sorted array (mutated)
     * @return     new length with <= 2 of each value
     */
    fun removeDuplicates(nums: IntArray): Int {
        var index = 0
        var count = 1

        for (i in 1..nums.lastIndex) {
            if (nums[i] == nums[i - 1]) {
                count++
            } else {
                count = 1
            }

            if (count <= 2) {
                index++
                nums[index] = nums[i]
            }
        }
        return index + 1
    }
}
```

```java
public class RemoveDuplicatesFromSortedArrayII {
    /**
     * @param nums sorted array (mutated)
     * @return     new length with <= 2 of each value
     */
    public int removeDuplicates(int[] nums) {
        int index = 0, count = 1;

        for (int i = 1; i < nums.length; i++) {
            count = nums[i] == nums[i - 1] ? count + 1 : 1;

            if (count <= 2) {
                nums[++index] = nums[i];
            }
        }
        return index + 1;
    }
}
```

```cpp
#include <vector>

class RemoveDuplicatesFromSortedArrayII {
public:
    /**
     * @param nums sorted array (mutated)
     * @return     new length with <= 2 of each value
     */
    int removeDuplicates(std::vector<int>& nums) {
        int index = 0, count = 1;

        for (int i = 1; i < (int)nums.size(); i++) {
            count = nums[i] == nums[i - 1] ? count + 1 : 1;

            if (count <= 2) nums[++index] = nums[i];
        }
        return index + 1;
    }
};
```

```python
def remove_duplicates(nums: list[int]) -> int:
    """
    @param nums: sorted array (mutated)
    @return:     new length with <= 2 of each value
    """
    index = 0
    count = 1

    for i in range(1, len(nums)):
        count = count + 1 if nums[i] == nums[i - 1] else 1

        if count <= 2:
            index += 1
            nums[index] = nums[i]

    return index + 1
```

```rust
impl Solution {
    /// @param nums sorted array (mutated)
    /// @return     new length with <= 2 of each value
    pub fn remove_duplicates(nums: &mut Vec<i32>) -> i32 {
        let mut index = 0;
        let mut count = 1;

        for i in 1..nums.len() {
            count = if nums[i] == nums[i - 1] { count + 1 } else { 1 };

            if count <= 2 {
                index += 1;
                nums[index] = nums[i];
            }
        }
        index as i32 + 1
    }
}
```

## Dry run

**Input:** `nums = [1,1,1,2,2,3]`.

```
index=0, count=1.
i=1 (1): count=2.  <=2 -> nums[1]=1.  index=1.
i=2 (1): count=3.  >2 -> skip.
i=3 (2): count=1.  nums[2]=2.  index=2.
i=4 (2): count=2.  nums[3]=2.  index=3.
i=5 (3): count=1.  nums[4]=3.  index=4.

Output: 5, prefix [1,1,2,2,3] ✓
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

- **Remove Duplicates I** ([3.18](remove-duplicates-from-sorted-array.md)) — the at-most-one ancestor.
- **Interview follow-up:** "How would you generalize to 'at most k'?" Replace the `count <= 2` gate with `count <= k` — the counter approach scales to any k without changing the structure.
