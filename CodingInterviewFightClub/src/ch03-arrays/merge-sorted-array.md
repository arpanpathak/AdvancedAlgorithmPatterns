# 3.11 Merge Sorted Array

> **Source:** [`src/main/kotlin/array/MergeSortedArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/MergeSortedArray.kt)
> **Pattern:** reverse two-pointer merge · **Core page**

## The Problem

Merge `nums2` (length `n`) into `nums1` (length `m + n`, first `m` elements valid, rest zeroed) in place, sorted.

- Constraints: $0 \le m, n \le 200$; both arrays sorted.

## Examples

```
Input:  nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
Output: [1,2,2,3,5,6]
```

## Intuition — merge from the *back*, where the empty space is

The standard merge ([4.3](../ch04-linked-lists/merge-two-sorted-lists.md)) walks from the front — but that would overwrite `nums1`'s valid prefix. The trick: **fill from the end** (`m+n-1` downward), where `nums1` has guaranteed empty slots. Compare the last valid elements of each side and place the larger one:

```
x = m - 1, y = n - 1, ptr = m + n - 1
while x >= 0 and y >= 0:
    nums1[ptr--] = max(nums1[x], nums2[y])   # put the larger tail element
while y >= 0:                                # nums2 leftovers (nums1's are already home)
    nums1[ptr--] = nums2[y--]
```

**Why is the `nums1` leftover loop unnecessary?** If `x` runs out first, the remaining `nums1` elements are already in their final sorted positions at the front. If `y` runs out first, `nums2` is fully placed — done. Only `nums2`'s leftovers need copying.

**Why does this never clobber?** `ptr` starts at the last (empty) slot and only writes positions that are either empty or already consumed — the classic "write where you've already read" safety of reverse-direction merges. The repo's version has the first loop stop at `y > 0` (a hair early) and relies on the leftover loop; the canonical `y >= 0` form below is equivalent and cleaner.

## Approach 1 — Copy and sort (O((m+n) log(m+n)))

`System.arraycopy` then sort: trivial, but ignores the sorted inputs.

## Approach 2 — Reverse merge (the repo's version, optimal)

```kotlin
class MergeSortedArray {
    /**
     * @param nums1 destination (length m+n, first m valid)
     * @param m     valid length of nums1
     * @param nums2 source (sorted)
     * @param n     length of nums2
     */
    fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int) {
        var (x, y, ptr) = listOf(m - 1, n - 1, m + n - 1)

        // Fill from the back: no valid nums1 element is ever overwritten
        while (x >= 0 && y >= 0) {
            nums1[ptr--] = if (nums1[x] > nums2[y]) nums1[x--] else nums2[y--]
        }

        // nums2 leftovers (nums1's leftovers are already in place)
        while (y >= 0) {
            nums1[ptr--] = nums2[y--]
        }
    }
}
```

```java
public class MergeSortedArray {
    /**
     * @param nums1 destination (length m+n, first m valid)
     * @param m     valid length of nums1
     * @param nums2 source (sorted)
     * @param n     length of nums2
     */
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int x = m - 1, y = n - 1, ptr = m + n - 1;

        while (x >= 0 && y >= 0) {                       // fill from the back
            nums1[ptr--] = nums1[x] > nums2[y] ? nums1[x--] : nums2[y--];
        }
        while (y >= 0) {                                 // nums2 leftovers
            nums1[ptr--] = nums2[y--];
        }
    }
}
```

```cpp
#include <vector>

class MergeSortedArray {
public:
    /**
     * @param nums1 destination (length m+n, first m valid)
     * @param m     valid length of nums1
     * @param nums2 source (sorted)
     * @param n     length of nums2
     */
    void merge(std::vector<int>& nums1, int m, std::vector<int>& nums2, int n) {
        int x = m - 1, y = n - 1, ptr = m + n - 1;

        while (x >= 0 && y >= 0) {                       // fill from the back
            nums1[ptr--] = nums1[x] > nums2[y] ? nums1[x--] : nums2[y--];
        }
        while (y >= 0) {                                 // nums2 leftovers
            nums1[ptr--] = nums2[y--];
        }
    }
};
```

```python
def merge(nums1: list[int], m: int, nums2: list[int], n: int) -> None:
    """
    @param nums1: destination (length m+n, first m valid)
    @param m:     valid length of nums1
    @param nums2: source (sorted)
    @param n:     length of nums2
    """
    x, y, ptr = m - 1, n - 1, m + n - 1

    while x >= 0 and y >= 0:                       # fill from the back
        if nums1[x] > nums2[y]:
            nums1[ptr] = nums1[x]
            x -= 1
        else:
            nums1[ptr] = nums2[y]
            y -= 1
        ptr -= 1

    while y >= 0:                                  # nums2 leftovers
        nums1[ptr] = nums2[y]
        y -= 1
        ptr -= 1
```

```rust
impl Solution {
    /// @param nums1 destination (length m+n, first m valid)
    /// @param m     valid length of nums1
    /// @param nums2 source (sorted)
    /// @param n     length of nums2
    pub fn merge(nums1: &mut Vec<i32>, m: i32, nums2: &mut Vec<i32>, n: i32) {
        let (mut x, mut y, mut ptr) = (m as usize, n as usize, (m + n) as usize);

        while x > 0 && y > 0 {
            if nums1[x - 1] > nums2[y - 1] {       // fill from the back
                nums1[ptr - 1] = nums1[x - 1];
                x -= 1;
            } else {
                nums1[ptr - 1] = nums2[y - 1];
                y -= 1;
            }
            ptr -= 1;
        }
        while y > 0 {                              // nums2 leftovers
            nums1[ptr - 1] = nums2[y - 1];
            y -= 1;
            ptr -= 1;
        }
    }
}
```

## Dry run

**Input:** `nums1 = [1,2,3,0,0,0]`, `m = 3`, `nums2 = [2,5,6]`, `n = 3`.

```
x=2, y=2, ptr=5
nums1[2]=3 vs nums2[2]=6 -> 6.  nums1[5]=6.  ptr=4, y=1
nums1[2]=3 vs nums2[1]=5 -> 5.  nums1[4]=5.  ptr=3, y=0
nums1[2]=3 vs nums2[0]=2 -> 3.  nums1[3]=3.  ptr=2, x=1
nums1[1]=2 vs nums2[0]=2 -> 2.  nums1[2]=2.  ptr=1, y=-1
y < 0 -> stop.  nums1's leftover (1) is already home.

nums1 = [1,2,2,3,5,6] ✓
```

The reverse direction does all the work: the tail writes land in `nums1`'s guaranteed-empty slots (`ptr` from 5 down), and the tie at `3 vs 2` sends `nums2`'s 2 — order is preserved because each side's pointer only moves after its element is placed.

## Complexity

**Time.** Linear in both arrays:

$$
T(m, n) = O(m + n)
$$

**Space.** In place:

$$
S(m, n) = O(1)
$$

## Variants & follow-ups

- **Merge Two Sorted Lists** ([4.3](../ch04-linked-lists/merge-two-sorted-lists.md)) — the linked-list twin; there the "empty space" problem doesn't exist (new nodes), here it dictates the reverse direction.
- **Merge Intervals** ([3.4](merge-intervals.md)) — "merge" in the overlap sense: sort + adjacency, the [11.9](../ch11-greedy/non-overlapping-intervals.md) family.
- **Interview follow-up:** "Why merge from the back instead of the front?" A front merge writes `nums1[0]` (valid data) before reading it — the classic overwrite bug. Backward writes only target slots that are empty or already consumed, so no data is ever lost.
