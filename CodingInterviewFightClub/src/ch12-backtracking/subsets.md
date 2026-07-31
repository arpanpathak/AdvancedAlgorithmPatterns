# 12.1 Subsets

> **Source:** [`src/main/kotlin/array/Combinatorics/Subsets.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/Combinatorics/Subsets.kt)
> **Pattern:** positional include/exclude · **Core page**

## The Problem

Given an array `nums` of distinct integers, return **all possible subsets** (the power set).

- Constraints: $1 \le n \le 10$; distinct elements.

## Examples

```
Input:  nums = [1,2,3]
Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]   (all 8 subsets, any order)
```

## Intuition — each element is a *decision*, and every decision sequence is a subset

The power set has exactly $2^n$ members — one per "in or out" decision per element. Backtracking enumerates them by walking positions: at index `start`, the *current subset* either keeps `nums[i]` or not. The repo's version generates every subset as a *prefix* of the decision tree:

```
backtrack(start):
    record the current subset (it's complete as-is)
    for i in start..n-1:
        add nums[i]
        backtrack(i + 1)      # all subsets that INCLUDE nums[i]
        remove nums[i]        # undo, then try including nums[i+1] instead
```

**Why record at *every* node?** Every node of the recursion tree *is* a valid subset — the empty set at the root, then one element, then extensions. Recording at the node (not just the leaves) is what captures the "any length" nature of subsets. Contrast with permutations, where only the *leaves* (length n) are answers.

**Why `i + 1` and not `start + 1`?** The loop picks elements in increasing index order, so a subset is built by *appending*; `backtrack(i+1)` means "next choices come only after the chosen element." This is what guarantees each subset appears exactly once (no reorderings like `[2,1]` and `[1,2]`).

**The undo is the whole algorithm:** `removeAt(last)` restores the shared `currentSubset` list for the next iteration. Forget the undo and every branch pollutes the next — the classic bug that turns correct-looking code into garbage.

## Approach 1 — Iterative doubling

Start with `[[]]`; for each element, append copies of every existing subset plus the element: $O(n \cdot 2^n)$, same as backtracking, arguably simpler. The backtracking version is the one that generalizes (to permutations, partitions, constrained subsets) — which is why interviews want it.

## Approach 2 — Positional backtracking (the repo's version, optimal)

```kotlin
class Subsets {
    /**
     * @param nums array of distinct integers
     * @return     every subset of nums (the power set)
     */
    fun subsets(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val currentSubset = mutableListOf<Int>()

        fun backtrack(start: Int) {
            result.add(ArrayList(currentSubset))     // record this node: it IS a subset

            for (i in start until nums.size) {
                currentSubset.add(nums[i])           // include nums[i]
                backtrack(i + 1)                     // all subsets containing it
                currentSubset.removeAt(currentSubset.size - 1)   // undo
            }
        }

        backtrack(0)
        return result
    }
}
```

```java
import java.util.*;

public class Subsets {
    /**
     * @param nums array of distinct integers
     * @return     every subset of nums (the power set)
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(0, nums, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int start, int[] nums, List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));        // record this node: it IS a subset

        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);                    // include nums[i]
            backtrack(i + 1, nums, current, result);
            current.remove(current.size() - 1);      // undo
        }
    }
}
```

```cpp
#include <vector>

class Subsets {
    void backtrack(int start, std::vector<int>& nums, std::vector<int>& cur,
                   std::vector<std::vector<int>>& result) {
        result.push_back(cur);                       // record this node: it IS a subset

        for (int i = start; i < (int)nums.size(); i++) {
            cur.push_back(nums[i]);                  // include nums[i]
            backtrack(i + 1, nums, cur, result);
            cur.pop_back();                          // undo
        }
    }

public:
    /**
     * @param nums array of distinct integers
     * @return     every subset of nums (the power set)
     */
    std::vector<std::vector<int>> subsets(std::vector<int>& nums) {
        std::vector<std::vector<int>> result;
        std::vector<int> cur;
        backtrack(0, nums, cur, result);
        return result;
    }
};
```

```python
def subsets(nums: list[int]) -> list[list[int]]:
    """
    @param nums: array of distinct integers
    @return:     every subset of nums (the power set)
    """
    result = []
    current = []

    def backtrack(start: int) -> None:
        result.append(current[:])              # record this node: it IS a subset

        for i in range(start, len(nums)):
            current.append(nums[i])            # include nums[i]
            backtrack(i + 1)
            current.pop()                      # undo

    backtrack(0)
    return result
```

```rust
impl Solution {
    /// @param nums array of distinct integers
    /// @return     every subset of nums (the power set)
    pub fn subsets(nums: Vec<i32>) -> Vec<Vec<i32>> {
        let mut result = Vec::new();
        let mut current = Vec::new();

        fn backtrack(start: usize, nums: &Vec<i32>, current: &mut Vec<i32>, result: &mut Vec<Vec<i32>>) {
            result.push(current.clone());      // record this node: it IS a subset

            for i in start..nums.len() {
                current.push(nums[i]);         // include nums[i]
                backtrack(i + 1, nums, current, result);
                current.pop();                 // undo
            }
        }

        backtrack(0, &nums, &mut current, &mut result);
        result
    }
}
```

## Dry run

**Input:** `nums = [1,2]`.

```
backtrack(0): record [].
  i=0: add 1 -> [1].  backtrack(1): record [1].
    i=1: add 2 -> [1,2].  backtrack(2): record [1,2].
      i=2 loop empty.  return.
    remove 2 -> [1].  loop ends.  return.
  remove 1 -> [].  loop ends.
  i=1: add 2 -> [2].  backtrack(2): record [2].
    i=2 loop empty.  return.
  remove 2 -> [].  loop ends.

result: [[], [1], [1,2], [2]] ✓  (all 4 subsets)
```

The `i + 1` rule is what keeps `[2,1]` out: after choosing `1`, the only further choices are indices *after* index 0 — so `2` can follow `1`, but `1` can never follow `2`. Each subset is built in increasing index order, exactly once.

## Complexity

**Time.** $2^n$ subsets, each copied at record time ($O(n)$):

$$
T(n) = O(n \cdot 2^n)
$$

**Space.** The result plus the recursion depth:

$$
S(n) = O(n \cdot 2^n)
$$

## Variants & follow-ups

- **Subsets II** (`src/main/kotlin/array/Combinatorics/Subsets_II.kt`) — with duplicates: sort, and skip a candidate `i` when it equals `nums[i-1]` and `i > start`. One extra line; the classic "dedupe by sorted pruning" move.
- **Combinations** (`src/main/kotlin/array/Combinatorics/Combinations.kt`) — subsets of a fixed size `k`: the same template with a `size == k` guard at record time.
- **Permutations** ([12.2](permutations.md)) — the ordering sibling: recording moves from every node to the leaves, and the "choose" becomes a swap.
- **Interview follow-up:** "Why record at every node for subsets but only at leaves for permutations?" A subset is *any* prefix of the decision path (all lengths are answers); a permutation is only complete when every position is fixed. The shape of the output dictates where the record goes — saying that shows you see the template, not just the problem.
