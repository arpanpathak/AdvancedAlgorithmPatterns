# 12.12 Subsets II

> **Source**: [`src/main/kotlin/array/Combinatorics/Subsets_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/Combinatorics/Subsets_II.kt)
> **Pattern**: sorted skip-duplicates subset · **Core page**

## The Problem

All **distinct** subsets (nums may have duplicates).

- Constraints: n ≤ 15.

## Examples

```
Input:  nums = [1,2,2]   -> Output: [[],[1],[1,2],[1,2,2],[2],[2,2]]
```

## Intuition — the [12.1](subsets.md) backtrack with a skip rule

Sort first. In the for-loop, skip a value equal to its predecessor unless it's the *first* at this level — that dedupes identical subsets:

```kotlin
fun backtrack(start: Int) {
    result.add(current.toList())

    for (i in start until nums.size) {
        if (i > start && nums[i - 1] == nums[i]) continue    // skip duplicates at this level

        current.add(nums[i])
        backtrack(i + 1)
        current.removeLast()
    }
}
```

**Why `i > start`?** The duplicate must be skipped only among *siblings* — `nums[start]` itself may equal `nums[start-1]` but must still be taken (it's a new position in the subset). The `i > start` guard is the exact sibling test.

## Approach 1 — Set-based dedupe (the lazy way)

Generate all subsets into a set: correct, wasteful.

## Approach 2 — Sorted skip rule (the repo's version, optimal)

```kotlin
class Subsets_II {
    /**
     * @param nums array with possible duplicates
     * @return     all distinct subsets
     */
    fun subsetsWithDup(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val current = mutableListOf<Int>()
        nums.sort()

        fun backtrack(start: Int) {
            result.add(current.toList())

            for (i in start until nums.size) {
                if (i > start && nums[i - 1] == nums[i]) continue

                current.add(nums[i])
                backtrack(i + 1)
                current.removeLast()
            }
        }

        backtrack(0)
        return result
    }
}
```

```java
import java.util.*;

public class SubsetsII {
    /**
     * @param nums array with possible duplicates
     * @return     all distinct subsets
     */
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();

        backtrack(result, new ArrayList<>(), nums, 0);
        return result;
    }

    private void backtrack(List<List<Integer>> result, List<Integer> cur,
                           int[] nums, int start) {
        result.add(new ArrayList<>(cur));

        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) continue;

            cur.add(nums[i]);
            backtrack(result, cur, nums, i + 1);
            cur.remove(cur.size() - 1);
        }
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class SubsetsII {
public:
    /**
     * @param nums array with possible duplicates
     * @return     all distinct subsets
     */
    std::vector<std::vector<int>> subsetsWithDup(std::vector<int>& nums) {
        std::sort(nums.begin(), nums.end());
        std::vector<std::vector<int>> result;

        std::vector<int> cur;
        std::function<void(int)> backtrack = [&](int start) {
            result.push_back(cur);

            for (int i = start; i < (int)nums.size(); i++) {
                if (i > start && nums[i] == nums[i - 1]) continue;

                cur.push_back(nums[i]);
                backtrack(i + 1);
                cur.pop_back();
            }
        };

        backtrack(0);
        return result;
    }
};
```

```python
def subsets_with_dup(nums: list[int]) -> list[list[int]]:
    """
    @param nums: array with possible duplicates
    @return:     all distinct subsets
    """
    nums.sort()
    result = []

    def backtrack(start: int, current: list[int]) -> None:
        result.append(current[:])

        for i in range(start, len(nums)):
            if i > start and nums[i] == nums[i - 1]:
                continue

            current.append(nums[i])
            backtrack(i + 1, current)
            current.pop()

    backtrack(0, [])
    return result
```

```rust
impl Solution {
    /// @param nums array with possible duplicates
    /// @return     all distinct subsets
    pub fn subsets_with_dup(mut nums: Vec<i32>) -> Vec<Vec<i32>> {
        nums.sort_unstable();
        let mut result = Vec::new();

        fn backtrack(nums: &Vec<i32>, start: usize, cur: &mut Vec<i32>, result: &mut Vec<Vec<i32>>) {
            result.push(cur.clone());

            for i in start..nums.len() {
                if i > start && nums[i] == nums[i - 1] { continue; }

                cur.push(nums[i]);
                backtrack(nums, i + 1, cur, result);
                cur.pop();
            }
        }

        backtrack(&nums, 0, &mut Vec::new(), &mut result);
        result
    }
}
```

## Dry run

**Input:** `nums = [1,2,2]` (sorted).

```
backtrack(0): add [].  i=0 (1): take 1 -> backtrack(1): add [1].
    i=1 (2): take -> backtrack(2): add [1,2].  i=2 (2): i>1 && 2==2 -> skip.
    i=2 (2): i>1 && 2==2 -> skip.
  i=1 (2): take 2 -> backtrack(2): add [2].  i=2: skip.
  i=2 (2): i>0 && 2==2? nums[1]==nums[2] and i=2>0 -> SKIP.  (avoids the duplicate [2])
Output: [[],[1],[1,2],[2],[2,2]] ✓
```

## Complexity

**Time.** 2ⁿ subsets:

$$
T(n) = O(2^n)
$$

**Space.** The recursion + result:

$$
S(n) = O(2^n)
$$

## Variants & follow-ups

- **Subsets** ([12.1](subsets.md)) — the no-duplicates ancestor.
- **Combination Sum II** — the same skip rule in a sum context.
- **Interview follow-up:** "Why does sorting make the skip exact?" Equal values become adjacent — the sibling test `nums[i-1] == nums[i]` detects "we already branched with this value at this level". The `i > start` guard keeps the first occurrence.
