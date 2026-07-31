# 12.11 Permutations II

> **Source:** [`src/main/kotlin/array/Combinatorics/Permutation_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/Combinatorics/Permutation_II.kt) (+ `Permutation_II_Backtracking.kt`, `Permutation_II_NarayanPandita.kt`)
> **Pattern:** dedupe guard on [12.2](permutations.md) · **Core page**

## The Problem

Given `nums` (**may contain duplicates**), return **all unique permutations**.

- Constraints: $1 \le n \le 8$; values fit in `Int`.

## Examples

```
Input:  nums = [1,1,2]   -> Output: [[1,1,2],[1,2,1],[2,1,1]]
Input:  nums = [1,2,3]   -> Output: 6 permutations (the [12.2](permutations.md) case)
```

## Intuition — the [12.2](permutations.md) swap-recursion, plus "don't swap a duplicate into the same slot twice"

The permutations template swaps `i` with each `j >= i` then recurses. With duplicates, **two identical values swapped into slot `i` produce the same subtree** — so skip a `j` if `nums[j]` was already swapped into position `i` in this frame:

```
fun permute(i):
    if i == n: record
    else:
        seen = empty set
        for j in i..n-1:
            if nums[j] in seen: continue     # duplicate value: same subtree already explored
            seen.add(nums[j])
            swap(i, j); permute(i+1); swap(i, j)
```

**Why does a per-frame `seen` set suffice?** Within one frame, all swaps put a value into slot `i`; identical values there yield identical *remaining* arrangements. The `seen` set (per recursion frame) prunes exactly the duplicate-slot cases while preserving all distinct ones — no global visited set needed.

**Why is order-invariant dedupe correct?** The swap template already generates every permutation once (modulo the `i`-prefix convention). Adding "skip duplicates at slot i" removes the *extra* copies that duplicates would otherwise create, leaving exactly the distinct permutations. This is the [12.8](combination-sum.md) "dedupe structurally, not by filtering" discipline.

**The repo's three versions** — `Permutation_II.kt` (the Narayana-Pandita next-permutation enumeration: sort, then call `nextPermutation` n! times — the [12.10](next-permutation.md) engine), `Permutation_II_Backtracking.kt` (the swap + seen-set version above), and `Permutation_II_NarayanPandita.kt` (an explicit generator).

## Approach 1 — Generate all permutations, dedupe at the end (n! · filter)

Permute then put into a set: correct, but the duplicates are *generated* — exponential waste.

## Approach 2 — Swap recursion with a per-frame seen set (the repo's backtracking version, optimal)

```kotlin
class Permutation_II_Backtracking {
    private val result = mutableListOf<List<Int>>()

    /**
     * @param nums array with possible duplicates
     * @return     all unique permutations
     */
    fun permuteUnique(nums: IntArray): List<List<Int>> {
        backtrack(nums, 0)
        return result
    }

    private fun backtrack(nums: IntArray, index: Int) {
        if (index == nums.size) {
            result.add(nums.toList())
            return
        }

        val seen = mutableSetOf<Int>()          // values already placed at this slot
        for (i in index until nums.size) {
            if (nums[i] in seen) continue       // duplicate: same subtree already explored
            seen.add(nums[i])

            swap(nums, index, i)
            backtrack(nums, index + 1)
            swap(nums, index, i)                // undo
        }
    }

    private fun swap(nums: IntArray, i: Int, j: Int) {
        nums[i] = nums[j].also { nums[j] = nums[i] }
    }
}
```

```java
import java.util.*;

public class PermutationsII {
    private final List<List<Integer>> result = new ArrayList<>();

    /**
     * @param nums array with possible duplicates
     * @return     all unique permutations
     */
    public List<List<Integer>> permuteUnique(int[] nums) {
        backtrack(nums, 0);
        return result;
    }

    private void backtrack(int[] nums, int index) {
        if (index == nums.length) {
            List<Integer> perm = new ArrayList<>();
            for (int v : nums) perm.add(v);
            result.add(perm);
            return;
        }

        Set<Integer> seen = new HashSet<>();          // values already placed at this slot
        for (int i = index; i < nums.length; i++) {
            if (seen.contains(nums[i])) continue;     // duplicate subtree
            seen.add(nums[i]);

            swap(nums, index, i);
            backtrack(nums, index + 1);
            swap(nums, index, i);                     // undo
        }
    }

    private void swap(int[] nums, int i, int j) {
        int t = nums[i]; nums[i] = nums[j]; nums[j] = t;
    }
}
```

```cpp
#include <set>
#include <vector>

class PermutationsII {
    std::vector<std::vector<int>> result;

    void backtrack(std::vector<int>& nums, int index) {
        if (index == (int)nums.size()) {
            result.push_back(nums);
            return;
        }

        std::set<int> seen;                              // values already placed at this slot
        for (int i = index; i < (int)nums.size(); i++) {
            if (seen.count(nums[i])) continue;           // duplicate subtree
            seen.insert(nums[i]);

            std::swap(nums[index], nums[i]);
            backtrack(nums, index + 1);
            std::swap(nums[index], nums[i]);             // undo
        }
    }

public:
    /**
     * @param nums array with possible duplicates
     * @return     all unique permutations
     */
    std::vector<std::vector<int>> permuteUnique(std::vector<int>& nums) {
        backtrack(nums, 0);
        return result;
    }
};
```

```python
def permute_unique(nums: list[int]) -> list[list[int]]:
    """
    @param nums: array with possible duplicates
    @return:     all unique permutations
    """
    result = []

    def backtrack(index: int) -> None:
        if index == len(nums):
            result.append(nums[:])
            return

        seen = set()                            # values already placed at this slot
        for i in range(index, len(nums)):
            if nums[i] in seen:
                continue                        # duplicate subtree
            seen.add(nums[i])

            nums[index], nums[i] = nums[i], nums[index]
            backtrack(index + 1)
            nums[index], nums[i] = nums[i], nums[index]   # undo

    backtrack(0)
    return result
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param nums array with possible duplicates
    /// @return     all unique permutations
    pub fn permute_unique(mut nums: Vec<i32>) -> Vec<Vec<i32>> {
        let mut result = Vec::new();

        fn backtrack(nums: &mut Vec<i32>, index: usize, result: &mut Vec<Vec<i32>>) {
            if index == nums.len() {
                result.push(nums.clone());
                return;
            }

            let mut seen: HashSet<i32> = HashSet::new();   // values already placed at this slot
            for i in index..nums.len() {
                if !seen.insert(nums[i]) { continue; }     // duplicate subtree

                nums.swap(index, i);
                backtrack(nums, index + 1, result);
                nums.swap(index, i);                       // undo
            }
        }

        backtrack(&mut nums, 0, &mut result);
        result
    }
}
```

## Dry run

**Input:** `nums = [1,1,2]`.

```
backtrack(0): seen={}.  i=0: nums[0]=1 not in seen -> add 1.  swap(0,0).  backtrack(1).
  backtrack(1): seen={}.  i=1: nums[1]=1 -> add.  swap(1,1).  backtrack(2).
    backtrack(2): index == 3 -> record [1,1,2] ✓.  undo.
    i=2: nums[2]=2 -> add.  swap(1,2) -> [1,2,1].  backtrack(2) -> record [1,2,1] ✓.  undo -> [1,1,2].
  undo -> [1,1,2].
backtrack(0): i=1: nums[1]=1 IS in seen -> SKIP (no [2,1,1]-from-this-frame duplicates).
            i=2: nums[2]=2 -> add.  swap(0,2) -> [2,1,1].  backtrack(1).
  backtrack(1): seen={}.  i=1: 1 -> add.  swap(1,1).  backtrack(2) -> record [2,1,1] ✓.
                i=2: 1 in seen -> SKIP.   (the duplicate 1 in slot 1 is pruned)
  undo -> [1,1,2].

Output: [[1,1,2],[1,2,1],[2,1,1]] ✓
```

The seen-set's two saves: at `backtrack(0).i=1`, the second `1` would re-run the whole `[1,1,2]` subtree as `[1,1,2]` — skipped. At `backtrack(1).i=2` inside the `[2,...]` frame, the second `1` would duplicate `[2,1,1]` — skipped. Each skip is exactly the "same value, same slot" case; distinct values still branch freely.

## Complexity

**Time.** Unique permutations × n swap-checks:

$$
T(n) = O(n! \cdot n) \text{ over distinct permutations}
$$

**Space.** Recursion + per-frame seen sets:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Permutations** ([12.2](permutations.md)) — the duplicate-free base template this page extends.
- **Next Permutation** ([12.10](next-permutation.md)) — the *iterative* enumerator: sort, then `next` n! times — `Permutation_II_NarayanPandita.kt` is exactly that.
- **Subsets II / Combination Sum II** (`array/Combinatorics/Subsets_II.kt`, `array/backtracking/CombinationSum_II.kt`) — the same "sort + skip same-value siblings" dedupe in the subsets family.
- **Interview follow-up:** "Why a per-frame set instead of a global one?" The swap template's slot-i uniqueness is a *local* property — each frame must only ensure it doesn't place the same value twice. A global seen set would wrongly prune valid placements of the same value in different slots of the same permutation.
