# 14.8 Sort Colors

> **Source:** [`src/main/kotlin/array/sorting/SortColors.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/sorting/SortColors.kt)
> **Pattern:** Dutch National Flag · **Core page**

## The Problem

Sort an array containing only `0, 1, 2` (colors) **in one pass**, in place, without counting.

- Constraints: $1 \le n \le 300$; values are 0, 1, or 2.

## Examples

```
Input:  nums = [2,0,2,1,1,0]   -> Output: [0,0,1,1,2,2]
```

## Intuition — three regions via three pointers; the middle pointer walks

The array is partitioned into `[0s] [1s] [unknown] [2s]` maintained by three pointers:

- `low` — the boundary after the 0s;
- `high` — the boundary before the 2s;
- `i` — the scanning pointer through the unknown region.

```
while (i <= high):
    nums[i] == 0 -> swap(i, low); i++; low++     # move a 0 into the left region
    nums[i] == 1 -> i++                          # already home: just advance
    nums[i] == 2 -> swap(i, high); high--        # move a 2 into the right region (i stays)
```

**Why does `i` stay put on a 2-swap?** The element swapped *in* from `high` is unexamined — it could be a 0 (needing another swap) or a 2. Only `low`-swaps (which bring a 1 or... wait, a `low`-swap brings the old `nums[low]`, which was a 1 or 0 — actually it brings whatever was at `low`, which by invariant is a 1) — so `i` can advance. The 2-swap is the only one that doesn't advance `i`.

**Why is this "one pass"?** Every element is examined once; the swaps are O(1). The [14.0](pattern-primer.md) theme inverted: instead of sorting-then-scanning, a structure (three regions) makes a single pass sort.

## Approach 1 — Count then rewrite (two passes)

Count 0s/1s/2s, then overwrite: O(n) and simple — but the problem forbids counting ("one pass without counting" in spirit).

## Approach 2 — Dutch National Flag (the repo's version, optimal)

```kotlin
class SortColors {
    fun swap(i: Int, j: Int, nums: IntArray) {
        nums[i] = nums[j].also { nums[j] = nums[i] }
    }

    /**
     * @param nums array of 0s, 1s, 2s (sorted in place, one pass)
     */
    fun sortColors(nums: IntArray): Unit {
        var (low, high) = 0 to nums.size - 1
        var i = 0

        while (i <= high) {
            when (nums[i]) {
                0 -> swap(i++, low++, nums)   // move the 0 into the left region
                1 -> i++                      // already home
                2 -> swap(i, high--, nums)    // move the 2 right; i stays (incoming is unexamined)
            }
        }
    }
}
```

```java
public class SortColors {
    /**
     * @param nums array of 0s, 1s, 2s (sorted in place, one pass)
     */
    public void sortColors(int[] nums) {
        int low = 0, high = nums.length - 1, i = 0;

        while (i <= high) {
            if (nums[i] == 0) {
                swap(nums, i++, low++);       // move the 0 into the left region
            } else if (nums[i] == 1) {
                i++;                          // already home
            } else {
                swap(nums, i, high--);        // move the 2 right; i stays
            }
        }
    }

    private void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }
}
```

```cpp
#include <vector>

class SortColors {
public:
    /**
     * @param nums array of 0s, 1s, 2s (sorted in place, one pass)
     */
    void sortColors(std::vector<int>& nums) {
        int low = 0, high = nums.size() - 1, i = 0;

        while (i <= high) {
            if (nums[i] == 0) {
                std::swap(nums[i++], nums[low++]);   // move the 0 into the left region
            } else if (nums[i] == 1) {
                i++;                                 // already home
            } else {
                std::swap(nums[i], nums[high--]);    // move the 2 right; i stays
            }
        }
    }
};
```

```python
def sort_colors(nums: list[int]) -> None:
    """
    @param nums: array of 0s, 1s, 2s (sorted in place, one pass)
    """
    low, high, i = 0, len(nums) - 1, 0

    while i <= high:
        if nums[i] == 0:
            nums[i], nums[low] = nums[low], nums[i]   # move the 0 into the left region
            i += 1
            low += 1
        elif nums[i] == 1:
            i += 1                                    # already home
        else:
            nums[i], nums[high] = nums[high], nums[i] # move the 2 right; i stays
            high -= 1
```

```rust
impl Solution {
    /// @param nums array of 0s, 1s, 2s (sorted in place, one pass)
    pub fn sort_colors(nums: &mut Vec<i32>) {
        let (mut low, mut high, mut i) = (0usize, nums.len() - 1, 0usize);

        while i <= high {
            match nums[i] {
                0 => { nums.swap(i, low); i += 1; low += 1; }    // 0 into the left region
                1 => { i += 1; }                                 // already home
                _ => { nums.swap(i, high); high -= 1; }          // 2 right; i stays
            }
        }
    }
}
```

## Dry run

**Input:** `nums = [2,0,2,1,1,0]`.

```
low=0, high=5, i=0
i=0 (2): swap(0,5) -> [0,0,2,1,1,2].  high=4.  i stays 0.
i=0 (0): swap(0,0) -> unchanged.  i=1, low=1.
i=1 (0): swap(1,1) -> unchanged.  i=2, low=2.
i=2 (2): swap(2,4) -> [0,0,1,1,2,2].  high=3.  i stays 2.
i=2 (1): i=3.
i=3 (1): i=4.  i(4) > high(3) -> stop.

Output: [0,0,1,1,2,2] ✓
```

The 2-swap at `i=2` is the one that *doesn't* advance `i`: the incoming element (a `1` from `high`) is unexamined and must be processed. The `0`-swaps always advance because they bring a `1` (by the region invariant). Three regions stay contiguous the whole time.

## Complexity

**Time.** Each element examined once:

$$
T(n) = O(n)
$$

**Space.** Three pointers:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Partition family** — the Dutch National Flag is "three-way partition": the [14.2](kth-largest-element.md) partition generalized to two boundaries.
- **Move Zeroes** ([3.3](../ch03-arrays/move-zeroes.md)) — the two-region special case (0s vs non-0s): one boundary instead of two.
- **Interview follow-up:** "Why can't the 2-swap advance `i`?" The element swapped in from `high` has never been examined — it might be a 0 (needing a left swap) or another 2. Advancing `i` would skip it, breaking the invariant. The 0-swap *can* advance because `nums[low]` is always a 1 by the region invariant — the swapped-in element is already in its final region.
