# 12.2 Permutations

> **Source:** [`src/main/kotlin/array/Combinatorics/Permutations.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/Combinatorics/Permutations.kt)
> **Pattern:** swap-based · **Core page**

## The Problem

Given an array `nums` of distinct integers, return **all possible permutations** (orderings).

- Constraints: $1 \le n \le 6$; distinct elements.

## Examples

```
Input:  nums = [1,2,3]
Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]   (all 6, any order)
```

## Intuition — fix a position, recurse on the rest

Subsets ([12.1](subsets.md)) builds by *appending*; permutations need *orderings*, and the classic trick builds them in place:

> At recursion depth `start`, decide **who occupies position `start`**: swap each candidate into that slot, recurse on `start + 1`, swap back.

```
permute(start):
    if start == last index: record the whole array (one permutation!) and return
    for i in start..last:
        swap(nums[start], nums[i])     # nums[i] gets the start position
        permute(start + 1)             # fix the rest
        swap(nums[start], nums[i])     # undo (the backtrack)
```

**Why is a swap the right "choose"?** Position `start` must hold each remaining element exactly once. Swapping `nums[i]` into `nums[start]` *is* "choosing nums[i] for this position"; the recursion fixes the rest; the second swap restores the array so the next candidate starts from the same base state.

**Why no `visited` set?** The swap bookkeeping implicitly partitions the array: everything *before* `start` is fixed, everything from `start` on is still available. No element is ever chosen twice because the available set shrinks by one at every depth.

**The copy at the leaf:** only at `start == lastIndex` is the array a complete permutation — so the leaf copies it (`nums.copyOf().toList()`). Copying at the leaf (not the node) is the mirror of Subsets recording at every node: permutations have a fixed length.

## Approach 1 — Path building with a used-set (also correct)

Maintain `path` + a `used: BooleanArray`; at each position try every unused element: same $O(n!)$, easier to read, but it needs the extra set and copies at the leaves. The swap version is the "in-place" flavor interviews like for its elegance.

## Approach 2 — Swap-based backtracking (the repo's version, optimal)

```kotlin
class Permutations {
    /**
     * @param nums array of distinct integers
     * @return     every permutation of nums
     */
    fun permute(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        fun permute(nums: IntArray, start: Int) {
            if (start == nums.lastIndex) {                 // one element left: a complete permutation
                result.add(nums.copyOf().toList())
                return
            }

            for (i in start..nums.lastIndex) {
                nums[start] = nums[i].also { nums[i] = nums[start] }   // swap: choose for position start
                permute(nums, start + 1)                               // fix the rest
                nums[start] = nums[i].also { nums[i] = nums[start] }   // swap back: undo
            }
        }

        permute(nums, 0)
        return result
    }
}
```

```java
import java.util.*;

public class Permutations {
    /**
     * @param nums array of distinct integers
     * @return     every permutation of nums
     */
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(0, nums, result);
        return result;
    }

    private void backtrack(int start, int[] nums, List<List<Integer>> result) {
        if (start == nums.length - 1) {                    // one element left: complete
            List<Integer> perm = new ArrayList<>();
            for (int x : nums) perm.add(x);
            result.add(perm);
            return;
        }

        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);                          // choose for position start
            backtrack(start + 1, nums, result);            // fix the rest
            swap(nums, start, i);                          // swap back: undo
        }
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i]; nums[i] = nums[j]; nums[j] = tmp;
    }
}
```

```cpp
#include <vector>

class Permutations {
    void backtrack(int start, std::vector<int>& nums, std::vector<std::vector<int>>& result) {
        if (start == (int)nums.size() - 1) {               // one element left: complete
            result.push_back(nums);
            return;
        }

        for (int i = start; i < (int)nums.size(); i++) {
            std::swap(nums[start], nums[i]);               // choose for position start
            backtrack(start + 1, nums, result);            // fix the rest
            std::swap(nums[start], nums[i]);               // swap back: undo
        }
    }

public:
    /**
     * @param nums array of distinct integers
     * @return     every permutation of nums
     */
    std::vector<std::vector<int>> permute(std::vector<int>& nums) {
        std::vector<std::vector<int>> result;
        backtrack(0, nums, result);
        return result;
    }
};
```

```python
def permute(nums: list[int]) -> list[list[int]]:
    """
    @param nums: array of distinct integers
    @return:     every permutation of nums
    """
    result = []

    def backtrack(start: int) -> None:
        if start == len(nums) - 1:           # one element left: a complete permutation
            result.append(nums[:])
            return

        for i in range(start, len(nums)):
            nums[start], nums[i] = nums[i], nums[start]   # swap: choose for position start
            backtrack(start + 1)                          # fix the rest
            nums[start], nums[i] = nums[i], nums[start]   # swap back: undo

    backtrack(0)
    return result
```

```rust
impl Solution {
    /// @param nums array of distinct integers
    /// @return     every permutation of nums
    pub fn permute(nums: Vec<i32>) -> Vec<Vec<i32>> {
        let mut nums = nums;
        let mut result = Vec::new();

        fn backtrack(start: usize, nums: &mut Vec<i32>, result: &mut Vec<Vec<i32>>) {
            if start == nums.len() - 1 {               // one element left: a complete permutation
                result.push(nums.clone());
                return;
            }
            for i in start..nums.len() {
                nums.swap(start, i);                   // swap: choose for position start
                backtrack(start + 1, nums, result);    // fix the rest
                nums.swap(start, i);                   // swap back: undo
            }
        }

        backtrack(0, &mut nums, &mut result);
        result
    }
}
```

## Dry run

**Input:** `nums = [1,2,3]`.

```
permute(0):
  i=0: swap(0,0) -> [1,2,3].  permute(1):
         i=1: swap(1,1) -> [1,2,3].  permute(2): start==2 -> record [1,2,3].
              swap(1,1) back.
         i=2: swap(1,2) -> [1,3,2].  permute(2): record [1,3,2].
              swap(1,2) back -> [1,2,3].
    swap(0,0) back.
  i=1: swap(0,1) -> [2,1,3].  permute(1):
         i=1: swap(1,1) -> record [2,1,3].
         i=2: swap(1,2) -> [2,3,1] -> record.
    swap(0,1) back -> [1,2,3].
  i=2: swap(0,2) -> [3,2,1].  permute(1):
         i=1: record [3,2,1].
         i=2: swap -> [3,1,2] -> record.
    swap(0,2) back -> [1,2,3].

result: [1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,2,1], [3,1,2] ✓  (all 6)
```

Every `swap back` restores the array to exactly the state the caller expects — that's the undo contract. Break any single swap-back and the next iteration starts from a corrupted array, silently dropping or duplicating permutations.

## Complexity

**Time.** $n!$ permutations, each copied at the leaf:

$$
T(n) = O(n \cdot n!)
$$

**Space.** The result plus recursion depth:

$$
S(n) = O(n \cdot n!)
$$

## Variants & follow-ups

- **Permutations II** (`src/main/kotlin/array/Combinatorics/Permutation_II_Backtracking.kt`) — with duplicates: sort first, skip a swap target when `nums[i] == nums[i-1]` and `i` wasn't used — the dedupe pruning for swap-based backtracking.
- **Next Permutation / Narayana-Pandita** (`src/main/kotlin/array/Combinatorics/NextPermutation.kt`) — the *iteration* version: one specific next ordering per call, no recursion. The "lexicographic successor" that turns enumeration into a walk.
- **Permutation Sequence (k-th)** — count-by-factorials instead of enumerating: the "we don't need all of them" optimization that comes up when the output is too big.
- **Interview follow-up:** "Why does the swap version need no `visited` set?" The array is partitioned by `start`: indices `< start` are fixed, `>= start` are available. Swapping a candidate *out* of the available region and swapping it back is exactly "mark used / unmark" — the partition *is* the used-set, stored for free in the array.
