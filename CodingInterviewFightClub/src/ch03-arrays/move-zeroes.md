# 3.3 Move Zeroes

> **Source:** [`src/main/kotlin/array/MoveZeroes.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/MoveZeroes.kt)
> **Pattern:** partition (write pointer, Dance 2) · **Core page — in-place two-pointer**

## The Problem

Given an integer array `nums`, move all `0`s to the **end** while keeping the relative order of the non-zero elements. Do it **in-place** with $O(1)$ extra space.

- Constraints: $1 \le n \le 10^4$.

## Examples

```
Input:  nums = [0, 1, 0, 3, 12]
Output: [1, 3, 12, 0, 0]

Input:  nums = [0]
Output: [0]
```

## Intuition — "keep" pointer, "read" pointer

This is the **partition** pattern from [3.0](pattern-primer.md) (Dance 2), the same skeleton as "move evens to the front" or quicksort's Lomuto partition:

- A **write pointer** marks where the next kept element should land.
- A **read pointer** scans the array.

For each element: if it's *kept* (non-zero), write it at the write pointer and advance both; if it's *discarded* (zero), skip it (only the read pointer moves). After the scan, every kept element is at the front, in order — so fill the tail with zeros.

Two phases, both $O(n)$, zero extra memory:

1. **Compact:** `write = 0`; for each `read`, if `nums[read] != 0` then `nums[write++] = nums[read]`.
2. **Zero-fill:** from `write` to the end, set `0`.

**Why the relative order is preserved:** the write pointer only ever *lags behind* the read pointer (it advances only on keeps), so kept elements are written in their original order at positions ≤ their original positions. Overwrites are safe because the write position always points at a slot that's either the read position or an already-consumed slot.

## Approach 1 — Copy to a new array

Build `[non-zeros...] + [zeros...]` and copy back: $O(n)$ time, $O(n)$ space. Works, but the problem's whole point is the in-place constraint.

## Approach 2 — Two-pass in-place (the repo's version, optimal)

```kotlin
/**
 * @param nums the array to modify in place
 * @return     Unit; nums is mutated so all zeros are moved to the end
 */
fun moveZeroes(nums: IntArray) {
    var nonZeroPointer = 0

    // Phase 1: move every non-zero element to the front, in order.
    for (i in nums.indices) {
        if (nums[i] != 0) {
            nums[nonZeroPointer++] = nums[i]
        }
    }

    // Phase 2: fill the remaining tail with zeros.
    while (nonZeroPointer < nums.size) {
        nums[nonZeroPointer++] = 0
    }
}
```

```java
public class MoveZeroes {
    /**
     * @param nums the array to modify in place
     *             (mutated so all zeros are moved to the end)
     */
    public void moveZeroes(int[] nums) {
        int write = 0;
        for (int read = 0; read < nums.length; read++) {
            if (nums[read] != 0) {
                nums[write++] = nums[read];       // compact kept elements
            }
        }
        while (write < nums.length) {
            nums[write++] = 0;                    // zero-fill the tail
        }
    }
}
```

```cpp
#include <vector>

class MoveZeroes {
public:
    /**
     * @param nums the array to modify in place
     *             (mutated so all zeros are moved to the end)
     */
    void moveZeroes(std::vector<int>& nums) {
        int write = 0;
        for (int read = 0; read < (int)nums.size(); read++) {
            if (nums[read] != 0) {
                nums[write++] = nums[read];
            }
        }
        while (write < (int)nums.size()) {
            nums[write++] = 0;
        }
    }
};
```

```python
def move_zeroes(nums: list[int]) -> None:
    """
    @param nums: the array to modify in place
                 (mutated so all zeros are moved to the end)
    """
    write = 0
    for read, value in enumerate(nums):
        if value != 0:
            nums[write] = value          # compact kept elements
            write += 1
    while write < len(nums):
        nums[write] = 0                  # zero-fill the tail
        write += 1
```

```rust
impl Solution {
    /// @param nums the array to modify in place
    ///             (mutated so all zeros are moved to the end)
    pub fn move_zeroes(nums: &mut Vec<i32>) {
        let mut write = 0;
        for read in 0..nums.len() {
            if nums[read] != 0 {
                nums[write] = nums[read];        // compact kept elements
                write += 1;
            }
        }
        while write < nums.len() {
            nums[write] = 0;                     // zero-fill the tail
            write += 1;
        }
    }
}
```

## Dry run

**Input:** `nums = [0, 1, 0, 3, 12]`

```
Phase 1 (compact):
read=0: nums[0]=0   -> skip
read=1: nums[1]=1   -> nums[0]=1, write=1      array: [1, 1, 0, 3, 12]
read=2: nums[2]=0   -> skip
read=3: nums[3]=3   -> nums[1]=3, write=2      array: [1, 3, 0, 3, 12]
read=4: nums[4]=12  -> nums[2]=12, write=3     array: [1, 3, 12, 3, 12]
Phase 2 (zero-fill):
write=3 -> nums[3]=0, write=4                  array: [1, 3, 12, 0, 12]
write=4 -> nums[4]=0, write=5                  array: [1, 3, 12, 0, 0]  ✓
```

Note how the overwrites only touch slots that have already been read (`write ≤ read` always holds), so no kept element is ever lost. The final array has the non-zeros in order followed by the zeros.

## Complexity

**Time.** Two linear passes:

$$
T(n) = O(n)
$$

**Space.** $O(1)$ — the entire point.

## Variants & follow-ups

- **Remove Element / Remove Duplicates From Sorted Array** (`src/main/kotlin/array/RemoveElement.kt`, `array/twopointer/RemoveDuplicateElementsFromSortedArray.kt`) — same write-pointer skeleton with a different predicate.
- **Interview follow-up:** "Do it in ONE pass with swaps instead of two passes." The swap version: `write = 0; for read: if nums[read] != 0 { swap(nums[read], nums[write]); write++ }` — same $O(n)$/$O(1)$, but it avoids re-writing the tail. The two-pass version above is simpler to prove correct; the swap version is marginally faster in practice. Say both.
- **Interview follow-up:** "Why can't we just sort by 'is zero'?" `Array.sort` with a zero-first comparator is $O(n \log n)$ and *not stable* (destroys relative order). The write-pointer pass is $O(n)$ and stable — that's the whole advantage.
