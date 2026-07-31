# 12.8 Combination Sum

> **Source:** [`src/main/kotlin/array/backtracking/CombinationSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/backtracking/CombinationSum.kt)
> **Pattern:** include/exclude with repetition · **Core page**

## The Problem

Given `candidates` (distinct, positive) and a `target`, return **all unique combinations** where the candidates sum to `target`. Each candidate may be used **unlimited times**; order doesn't matter (`[2,2,3]` and `[3,2,2]` are the same).

- Constraints: $1 \le$ candidates ≤ 30; target ≤ 40.

## Examples

```
Input:  candidates = [2,3,6,7], target = 7
Output: [[2,2,3],[7]]
```

## Intuition — the [12.1](subsets.md) template with *unbounded* inclusion

The decision at each candidate is "how many copies?" — and the include/exclude recursion from [12.1](subsets.md) generalizes with one change: after **including** a candidate, the recursion **stays on the same index** (repetition allowed), while the exclude branch advances:

```
findCombinations(current, start, remaining):
    remaining == 0          -> record (a valid combination)
    remaining > 0 && start < size:
        include candidates[start]: recurse(start, remaining - candidates[start])   # same index!
        removeLast
        exclude: recurse(start + 1, remaining)                                      # next index
```

**Why "same index" on include?** The unlimited-repetition rule — once `2` is chosen, `2` may be chosen again. The exclude branch is what eventually moves on. (Contrast [12.1](subsets.md)'s `i + 1` — the no-repetition rule.)

**Why no duplicate combinations?** The `start` parameter only ever advances — a combination is built in non-decreasing candidate order, so `[3,2,2]` can never be produced once `[2,2,3]` exists. Order-irrelevance is enforced structurally, the same trick as [12.1](subsets.md).

**The `remaining` arithmetic is the pruning:** the `remaining > 0` guard plus the `remaining == 0` base case kill any branch whose partial sum overshoots or hits — no separate sum check, no visited set.

## Approach 1 — Generate all subsets, filter by sum (2^n · n)

Enumerate every subset with repetition then keep those summing to target: wasteful, and duplicates abound.

## Approach 2 — Include/exclude with same-index recursion (the repo's version, optimal)

```kotlin
class CombinationSum {
    /**
     * @param candidates distinct positive numbers (unlimited use each)
     * @param target     sum to reach
     * @return           all combinations summing to target
     */
    fun combinationSum(candidates: IntArray, target: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        fun findCombinations(current: MutableList<Int>, start: Int, remaining: Int) {
            when {
                remaining == 0 -> result.add(ArrayList(current))          // valid combination
                remaining > 0 && start < candidates.size -> {
                    current.add(candidates[start])
                    findCombinations(current, start, remaining - candidates[start])  // include: same index
                    current.removeLast()
                    findCombinations(current, start + 1, remaining)                  // exclude: next index
                }
            }
        }

        findCombinations(mutableListOf(), 0, target)
        return result
    }
}
```

```java
import java.util.*;

public class CombinationSum {
    /**
     * @param candidates distinct positive numbers (unlimited use each)
     * @param target     sum to reach
     * @return           all combinations summing to target
     */
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        dfs(0, target, new ArrayList<>(), candidates, result);
        return result;
    }

    private void dfs(int start, int remaining, List<Integer> current,
                     int[] candidates, List<List<Integer>> result) {
        if (remaining == 0) { result.add(new ArrayList<>(current)); return; }   // valid
        if (remaining < 0 || start >= candidates.length) return;

        current.add(candidates[start]);
        dfs(start, remaining - candidates[start], current, candidates, result); // include: same index
        current.remove(current.size() - 1);
        dfs(start + 1, remaining, current, candidates, result);                 // exclude: next index
    }
}
```

```cpp
#include <vector>

class CombinationSum {
    void dfs(int start, int remaining, std::vector<int>& cur,
             std::vector<int>& candidates, std::vector<std::vector<int>>& result) {
        if (remaining == 0) { result.push_back(cur); return; }     // valid combination
        if (remaining < 0 || start >= (int)candidates.size()) return;

        cur.push_back(candidates[start]);
        dfs(start, remaining - candidates[start], cur, candidates, result);   // include: same index
        cur.pop_back();
        dfs(start + 1, remaining, cur, candidates, result);                   // exclude: next index
    }

public:
    /**
     * @param candidates distinct positive numbers (unlimited use each)
     * @param target     sum to reach
     * @return           all combinations summing to target
     */
    std::vector<std::vector<int>> combinationSum(std::vector<int>& candidates, int target) {
        std::vector<std::vector<int>> result;
        std::vector<int> cur;
        dfs(0, target, cur, candidates, result);
        return result;
    }
};
```

```python
def combination_sum(candidates: list[int], target: int) -> list[list[int]]:
    """
    @param candidates: distinct positive numbers (unlimited use each)
    @param target:     sum to reach
    @return:           all combinations summing to target
    """
    result = []
    current = []

    def dfs(start: int, remaining: int) -> None:
        if remaining == 0:
            result.append(current[:])            # valid combination
            return
        if remaining < 0 or start >= len(candidates):
            return

        current.append(candidates[start])
        dfs(start, remaining - candidates[start])   # include: same index (repetition allowed)
        current.pop()
        dfs(start + 1, remaining)                   # exclude: next index

    dfs(0, target)
    return result
```

```rust
impl Solution {
    /// @param candidates distinct positive numbers (unlimited use each)
    /// @param target     sum to reach
    /// @return           all combinations summing to target
    pub fn combination_sum(candidates: Vec<i32>, target: i32) -> Vec<Vec<i32>> {
        let mut result = Vec::new();
        let mut current = Vec::new();

        fn dfs(start: usize, remaining: i32, candidates: &Vec<i32>,
               current: &mut Vec<i32>, result: &mut Vec<Vec<i32>>) {
            if remaining == 0 {
                result.push(current.clone());            // valid combination
                return;
            }
            if remaining < 0 || start >= candidates.len() { return; }

            current.push(candidates[start]);
            dfs(start, remaining - candidates[start], candidates, current, result);  // include
            current.pop();
            dfs(start + 1, remaining, candidates, current, result);                  // exclude
        }

        dfs(0, target, &candidates, &mut current, &mut result);
        result
    }
}
```

## Dry run

**Input:** `candidates = [2,3,6,7]`, `target = 7`.

```
dfs([], start=0, rem=7):
  include 2: [2] rem 5.
    include 2: [2,2] rem 3.
      include 2: [2,2,2] rem 1.
        include 2: rem -1 -> dead.  exclude -> start 1:
          include 3: [2,2,2,3] rem -2 dead.  exclude -> 6, 7: dead.
      exclude 2: include 3: [2,2,3] rem 0 -> RECORD [2,2,3] ✓
        exclude 3 -> 6, 7: dead (rem < 0).
      exclude: include 6: [2,6] rem -1 dead.  include 7: [2,7] dead.
    exclude 2: include 3: [3] rem 4 -> include 3: [3,3] rem 1 -> include 3: [3,3,3] dead...
                include 6: [3,6] dead.  include 7: [3,7] dead.
              exclude 3: include 6: [6] rem 1 -> include 6: [6,6] dead.  include 7: [6,7] dead.
                            exclude 6: include 7: [7] rem 0 -> RECORD [7] ✓

result: [[2,2,3],[7]] ✓
```

The `remaining < 0` guard is the pruning engine: the branch `[2,2,2]` with rem 1 *tries* 2 (rem -1, dies instantly) before excluding to 3 — overshoot branches are killed at depth, never explored further. The `start` monotonicity keeps `[3,2,2]` impossible.

## Complexity

**Time.** Exponential in the answer size (bounded by the combinations of candidates summing to target):

$$
T(n, t) = O(t^{t/\min}) \text{ worst}, \text{ pruned heavily}
$$

**Space.** Recursion depth + output:

$$
S = O(t) + O(\text{output})
$$

## Variants & follow-ups

- **Combination Sum II** (`array/backtracking/CombinationSum_II.kt`) — *no repetition* + duplicate candidates: sort first, and skip `i` when `nums[i] == nums[i-1] && i > start` — the [12.1](subsets.md)-style dedupe guard.
- **Combination Sum III** (`array/backtracking/CombinationSum3.kt`) — exactly k elements from 1..9: the same template with a size limit.
- **Interview follow-up:** "Why does 'same index on include, next index on exclude' produce no duplicates?" Every combination is emitted in non-decreasing candidate order — the include branch can re-pick the current candidate, the exclude branch permanently abandons it. No other ordering is ever produced, so `[3,2,2]`-style permutations are structurally impossible.
