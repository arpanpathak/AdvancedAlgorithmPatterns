# 12.10 Next Permutation

> **Source:** [`src/main/kotlin/array/Combinatorics/NextPermutation.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/Combinatorics/NextPermutation.kt)
> **Pattern:** Narayana-Pandita · **Core page**

## The Problem

Rearrange `nums` into the **next lexicographically greater permutation** in place (wrap to the smallest when already maximal).

- Constraints: $1 \le n \le 100$; values fit in `Int`.

## Examples

```
Input:  nums = [1,2,3]   -> Output: [1,3,2]
Input:  nums = [3,2,1]   -> Output: [1,2,3]   (already maximal: wrap)
Input:  nums = [1,1,5]   -> Output: [1,5,1]
```

## Intuition — find the first "descent" from the right, then swap and reverse

The lexicographic successor has a famous three-step recipe (Narayana-Pandita):

1. **Find the pivot:** scan right-to-left for the first `nums[i] < nums[i+1]` — the rightmost place where the suffix stops being descending. Everything after the pivot is a *maximal* suffix (descending), which is why the next permutation must touch the pivot.
2. **Swap with the successor:** in the descending suffix, find the smallest element **greater than the pivot** (right-to-left scan stops at the first hit, since the suffix is descending) and swap them. This makes the prefix *just barely* larger.
3. **Reverse the suffix:** the suffix was descending (maximal); reversing it makes it ascending (minimal) — the *smallest* arrangement with the new prefix, which is exactly "next".

**Why "descending suffix" is the key invariant:** a permutation is maximal within its prefix-suffix split iff the suffix is descending. The first ascent-from-the-right is where the ordering can increase; everything after it must be reset to minimal.

**The repo's pivot handling** — `index++` then `pivot = nums[index - 1]`: the loop leaves `index` at the *first element of the descending suffix* (or -1), so `pivot` is `nums[index - 1]` and the suffix to fix is `[index..end]`. Slightly terse; the standard spelling (below) keeps `pivot` directly.

## Approach 1 — Generate all permutations (n! and wrong direction)

Enumerating permutations to find the successor is factorial — the O(n) three-step is the intended answer.

## Approach 2 — Narayana-Pandita in place (the repo's version, optimal)

```kotlin
class NextPermutation {
    fun swap(nums: IntArray, i: Int, j: Int) {
        nums[j] = nums[i].also { nums[i] = nums[j] }
    }

    fun reverse(nums: IntArray, startIndex: Int) {
        var (left, right) = Pair(startIndex, nums.lastIndex)
        while (left < right) {
            swap(nums, left++, right--)
        }
    }

    /**
     * @param nums array to advance to its next permutation (in place)
     * @return     false if the array was maximal (wrapped to the smallest)
     */
    fun nextPermutation(nums: IntArray): Boolean {
        var index = nums.lastIndex - 1

        // Step 1: find the rightmost ascent (pivot): nums[index] < nums[index + 1]
        while (index >= 0 && nums[index] >= nums[index + 1]) {
            index--
        }

        // Already maximal: wrap to the smallest permutation
        if (index < 0) {
            reverse(nums, 0)
            return false
        }

        // Step 2: swap the pivot with the smallest larger element in the descending suffix
        index++
        val pivot = nums[index - 1]
        var indexToSwap = index
        while (indexToSwap < nums.size - 1 && nums[indexToSwap + 1] > pivot) {
            indexToSwap++
        }
        swap(nums, index - 1, indexToSwap)

        // Step 3: reverse the suffix (descending -> ascending = minimal)
        reverse(nums, index)
        return true
    }
}
```

```java
public class NextPermutation {
    /**
     * @param nums array to advance to its next permutation (in place)
     */
    public void nextPermutation(int[] nums) {
        int pivot = -1;
        for (int i = nums.length - 2; i >= 0; i--) {         // find the rightmost ascent
            if (nums[i] < nums[i + 1]) { pivot = i; break; }
        }

        if (pivot < 0) {                                     // maximal: wrap
            reverse(nums, 0);
            return;
        }

        int swapIdx = nums.length - 1;
        while (nums[swapIdx] <= nums[pivot]) swapIdx--;      // smallest larger element
        swap(nums, pivot, swapIdx);

        reverse(nums, pivot + 1);                            // suffix descending -> ascending
    }

    private void reverse(int[] nums, int start) {
        int l = start, r = nums.length - 1;
        while (l < r) swap(nums, l++, r--);
    }

    private void swap(int[] nums, int i, int j) {
        int t = nums[i]; nums[i] = nums[j]; nums[j] = t;
    }
}
```

```cpp
#include <algorithm>
#include <vector>

class NextPermutation {
public:
    /**
     * @param nums array to advance to its next permutation (in place)
     */
    void nextPermutation(std::vector<int>& nums) {
        int pivot = -1;
        for (int i = nums.size() - 2; i >= 0; i--) {         // find the rightmost ascent
            if (nums[i] < nums[i + 1]) { pivot = i; break; }
        }

        if (pivot < 0) {                                     // maximal: wrap
            std::reverse(nums.begin(), nums.end());
            return;
        }

        int swapIdx = nums.size() - 1;
        while (nums[swapIdx] <= nums[pivot]) swapIdx--;      // smallest larger element
        std::swap(nums[pivot], nums[swapIdx]);

        std::reverse(nums.begin() + pivot + 1, nums.end());  // suffix ascending
    }
};
```

```python
def next_permutation(nums: list[int]) -> None:
    """
    @param nums: array to advance to its next permutation (in place)
    """
    pivot = -1
    for i in range(len(nums) - 2, -1, -1):         # find the rightmost ascent
        if nums[i] < nums[i + 1]:
            pivot = i
            break

    if pivot < 0:                                  # maximal: wrap
        nums.reverse()
        return

    swap_idx = len(nums) - 1
    while nums[swap_idx] <= nums[pivot]:           # smallest larger element
        swap_idx -= 1
    nums[pivot], nums[swap_idx] = nums[swap_idx], nums[pivot]

    nums[pivot + 1:] = reversed(nums[pivot + 1:])  # suffix ascending
```

```rust
impl Solution {
    /// @param nums array to advance to its next permutation (in place)
    pub fn next_permutation(nums: &mut Vec<i32>) {
        let mut pivot = None;
        for i in (0..nums.len() - 1).rev() {           // find the rightmost ascent
            if nums[i] < nums[i + 1] { pivot = Some(i); break; }
        }

        let Some(pivot) = pivot else {                 // maximal: wrap
            nums.reverse();
            return;
        };

        let mut swap_idx = nums.len() - 1;
        while nums[swap_idx] <= nums[pivot] { swap_idx -= 1; }   // smallest larger element
        nums.swap(pivot, swap_idx);

        nums[pivot + 1..].reverse();                   // suffix ascending
    }
}
```

## Dry run

**Input:** `nums = [1,2,3]`.

```
pivot scan: i=1: 2 < 3 -> pivot = 1.          (the rightmost ascent)
swap: smallest element > nums[1]=2 in the suffix [3] -> index 2.  swap(1,2) -> [1,3,2].
reverse suffix from index 2: [2..2] unchanged.

Output: [1,3,2] ✓
```

Now `nums = [3,2,1]`: no `i` with `nums[i] < nums[i+1]` → pivot = -1 → reverse whole array → `[1,2,3]` (the wrap). And `[1,1,5]`: pivot = 1 (`1 < 5`); smallest element > 1 in the suffix — scanning from the right, `5 > 1` → swap(1,2) → `[1,5,1]` ✓.

## Complexity

**Time.** Linear scans + a reverse:

$$
T(n) = O(n)
$$

**Space.** In place:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Permutations** ([12.2](permutations.md)) — the *iteration* counterpart: `nextPermutation` repeated `n!` times enumerates every ordering in lexicographic order.
- **Permutations II (Narayana-Pandita)** (`array/Combinatorics/Permutation_II_NarayanPandita.kt`) — the duplicate-safe enumeration: `next` skips repeated values structurally.
- **Next Greater Element III** (`array/Combinatorics/NextGreaterElement_III.kt`) — the same algorithm on the *digits of a number*.
- **Interview follow-up:** "Why must the suffix after the pivot be reversed rather than sorted?" The suffix is *descending* (maximal) by construction of the pivot scan — reversing it is a sort, in O(n) instead of O(n log n). The swap makes the prefix minimally larger; the reverse makes the suffix minimally arranged; together they're the exact successor.
