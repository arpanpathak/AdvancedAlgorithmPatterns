# 12.12 Combinations

> **Source:** [`src/main/kotlin/array/Combinatorics/Combinations.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/Combinatorics/Combinations.kt)
> **Pattern:** start-index backtracking · **Core page**

## The Problem

All **combinations** of `k` numbers from `1..n` (order doesn't matter — each subset once).

- Constraints: $1 \le n \le 20$.

## Examples

```
Input:  n = 4, k = 2
Output: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]
```

## Intuition — the `start` index enforces order

The canonical [12.1](subsets.md) recursion, with `k` as the size limit and `start` forcing strictly increasing picks:

```kotlin
fun combine(start: Int, n: Int, k: Int, current: MutableList<Int> = mutableListOf()) {
    if (current.size == k) {
        result.add(current.toList())
        return
    }

    for (i in start..n) {
        current.add(i)
        combine(i + 1, n, k, current)     // next pick must be > i
        current.removeLast()              // undo
    }
}
combine(1, n, k)
```

**Why `i + 1` (not `start`)?** The `+1` makes picks strictly increasing — `[1,2]` yes, `[2,1]` never. That single constraint is what distinguishes combinations from permutations ([12.2](permutations.md)): order doesn't matter, so the recursion *imposes* an order.

**Why `current.size == k` as the base?** The recursion depth is exactly the combination size — unlike subsets (any size), the combination stops at `k`. The [12.0](pattern-primer.md) pick/undo contract with a size gate.

## Approach 1 — Generate all subsets, filter size k (O(2ⁿ))

All subsets then keep `size == k`: correct, wasteful.

## Approach 2 — Start-index backtracking (the repo's version, optimal)

```kotlin
class Combinations {
    /**
     * @param n range upper bound (1..n)
     * @param k combination size
     * @return  all k-combinations of 1..n
     */
    fun combine(n: Int, k: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        fun combine(start: Int, n: Int, k: Int, current: MutableList<Int> = mutableListOf()) {
            if (current.size == k) {
                result.add(current.toList())
                return
            }

            for (i in start..n) {
                current.add(i)
                combine(i + 1, n, k, current)
                current.removeLast()          // undo
            }
        }

        combine(1, n, k)
        return result
    }
}
```

```java
import java.util.*;

public class Combinations {
    /**
     * @param n range upper bound (1..n)
     * @param k combination size
     * @return  all k-combinations of 1..n
     */
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(1, n, k, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int start, int n, int k, List<Integer> cur, List<List<Integer>> result) {
        if (cur.size() == k) {
            result.add(new ArrayList<>(cur));
            return;
        }

        for (int i = start; i <= n; i++) {
            cur.add(i);
            backtrack(i + 1, n, k, cur, result);
            cur.remove(cur.size() - 1);       // undo
        }
    }
}
```

```cpp
#include <vector>

class Combinations {
    void backtrack(int start, int n, int k, std::vector<int>& cur,
                   std::vector<std::vector<int>>& result) {
        if ((int)cur.size() == k) {
            result.push_back(cur);
            return;
        }

        for (int i = start; i <= n; i++) {
            cur.push_back(i);
            backtrack(i + 1, n, k, cur, result);
            cur.pop_back();                   // undo
        }
    }

public:
    /**
     * @param n range upper bound (1..n)
     * @param k combination size
     * @return  all k-combinations of 1..n
     */
    std::vector<std::vector<int>> combine(int n, int k) {
        std::vector<std::vector<int>> result;
        std::vector<int> cur;
        backtrack(1, n, k, cur, result);
        return result;
    }
};
```

```python
def combine(n: int, k: int) -> list[list[int]]:
    """
    @param n: range upper bound (1..n)
    @param k: combination size
    @return:  all k-combinations of 1..n
    """
    result = []

    def backtrack(start: int, cur: list[int]) -> None:
        if len(cur) == k:
            result.append(cur[:])
            return

        for i in range(start, n + 1):
            cur.append(i)
            backtrack(i + 1, cur)       # next pick must be > i
            cur.pop()                   # undo

    backtrack(1, [])
    return result
```

```rust
impl Solution {
    /// @param n range upper bound (1..n)
    /// @param k combination size
    /// @return  all k-combinations of 1..n
    pub fn combine(n: i32, k: i32) -> Vec<Vec<i32>> {
        let mut result = Vec::new();

        fn backtrack(start: i32, n: i32, k: i32, cur: &mut Vec<i32>, result: &mut Vec<Vec<i32>>) {
            if cur.len() == k as usize {
                result.push(cur.clone());
                return;
            }
            for i in start..=n {
                cur.push(i);
                backtrack(i + 1, n, k, cur, result);   // next pick must be > i
                cur.pop();                             // undo
            }
        }

        backtrack(1, n, k, &mut Vec::new(), &mut result);
        result
    }
}
```

## Dry run

**Input:** `n = 4, k = 2`.

```
backtrack(1, []):
  i=1: cur=[1].  backtrack(2, [1]):
    i=2: cur=[1,2].  size==2 -> add [1,2].  undo -> [1]
    i=3: cur=[1,3] -> add [1,3]
    i=4: cur=[1,4] -> add [1,4]
  undo -> []
  i=2: cur=[2].  backtrack(3): i=3 -> [2,3].  i=4 -> [2,4].
  i=3: cur=[3].  backtrack(4): i=4 -> [3,4].
  i=4: cur=[4].  backtrack(5): no i -> nothing.

Output: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]] ✓  (C(4,2) = 6)
```

The `i + 1` start is the order-enforcer: after picking 2, only 3 and 4 are eligible — `[2,1]` can never form. The size-gate base case stops exactly at `k`; the undo pops restore each branch's state. `n=4,k=4` yields the single `[1,2,3,4]`; `k=1` yields the four singletons.

## Complexity

**Time.** One node per combination:

$$
T(n, k) = O\left(C(n, k) \cdot k\right)
$$

**Space.** Recursion depth + current list:

$$
S(n, k) = O(k)
$$

## Variants & follow-ups

- **Combination Sum** ([12.8](combination-sum.md)) — the same start-index machine with repeated picks (`i` instead of `i+1`) and a sum gate.
- **Permutations** ([12.2](permutations.md)) — without the start constraint: order matters.
- **Combination Sum III** ([12.13](combination-sum-iii.md)) — the k-size + fixed-sum twin.
- **Interview follow-up:** "Why does `i + 1` prevent duplicates?" The recursion only picks strictly increasing values, so each subset has exactly one representation — its sorted order. Any combination-tuple is generated once, in that order; the `start` index IS the dedupe.
