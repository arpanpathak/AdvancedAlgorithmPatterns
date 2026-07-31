# 12.13 Combination Sum III

> **Source:** [`src/main/kotlin/array/backtracking/CombinationSum3.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/backtracking/CombinationSum3.kt)
> **Pattern:** k-size + fixed-sum backtracking · **Core page**

## The Problem

All combinations of `k` distinct numbers from `1..9` that sum to `n` (each used at most once).

- Constraints: $1 \le k \le 9$; $1 \le n \le 60$.

## Examples

```
Input:  k = 3, n = 7   -> Output: [[1,2,4]]
Input:  k = 3, n = 9   -> Output: [[1,2,6],[1,3,5],[2,3,4]]
```

## Intuition — [12.12](combinations.md) with a sum gate

The combinations machine with `k` as the size limit and `remaining` as the sum check — both gates in the base case:

```kotlin
fun dfs(k, remaining, start, result, curr = mutableListOf()) {
    if (curr.size == k && remaining == 0) {
        result.add(curr.toList())
        return
    }

    for (i in start..9) {
        if (remaining - i < 0) break        // prune: too big
        curr.add(i)
        dfs(k, remaining - i, i + 1, result, curr)
        curr.removeLast()                   // undo
    }
}
```

**Why `remaining - i < 0 → break`?** The candidates are ascending — once `i` exceeds the remaining sum, every later `i` does too. The `break` is the [12.0](pattern-primer.md) pruning: skip the whole tail instead of testing each.

**Why `i + 1`?** Each number used once — the [12.12](combinations.md) increasing-order constraint (no `[1,1]`-style repeats, which is what distinguishes this from [12.8](combination-sum.md)'s unlimited picks).

## Approach 1 — Combinations then filter by sum

Generate C(9,k), keep sum == n: correct, wasteful.

## Approach 2 — Size + sum gates with pruning (the repo's version, optimal)

```kotlin
class CombinationSum3 {
    /**
     * @param k how many numbers
     * @param n target sum
     * @return  all k distinct 1..9 numbers summing to n
     */
    fun combinationSum3(k: Int, n: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        dfs(k, n, 1, result)
        return result
    }

    fun dfs(
        k: Int,
        remaining: Int,
        start: Int,
        result: MutableList<List<Int>>,
        curr: MutableList<Int> = mutableListOf(),
    ) {
        if (curr.size == k && remaining == 0) {
            result.add(curr.toList())
            return
        }

        for (i in start..9) {
            if (remaining - i < 0) break        // prune: too big
            curr.add(i)
            dfs(k, remaining - i, i + 1, result, curr)
            curr.removeLast()                   // undo
        }
    }
}
```

```java
import java.util.*;

public class CombinationSumIII {
    /**
     * @param k how many numbers
     * @param n target sum
     * @return  all k distinct 1..9 numbers summing to n
     */
    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();
        dfs(k, n, 1, new ArrayList<>(), result);
        return result;
    }

    private void dfs(int k, int remaining, int start, List<Integer> cur, List<List<Integer>> result) {
        if (cur.size() == k && remaining == 0) {
            result.add(new ArrayList<>(cur));
            return;
        }

        for (int i = start; i <= 9; i++) {
            if (remaining - i < 0) break;       // prune: too big
            cur.add(i);
            dfs(k, remaining - i, i + 1, cur, result);
            cur.remove(cur.size() - 1);         // undo
        }
    }
}
```

```cpp
#include <vector>

class CombinationSumIII {
    void dfs(int k, int remaining, int start, std::vector<int>& cur,
             std::vector<std::vector<int>>& result) {
        if ((int)cur.size() == k && remaining == 0) {
            result.push_back(cur);
            return;
        }

        for (int i = start; i <= 9; i++) {
            if (remaining - i < 0) break;       // prune: too big
            cur.push_back(i);
            dfs(k, remaining - i, i + 1, cur, result);
            cur.pop_back();                     // undo
        }
    }

public:
    /**
     * @param k how many numbers
     * @param n target sum
     * @return  all k distinct 1..9 numbers summing to n
     */
    std::vector<std::vector<int>> combinationSum3(int k, int n) {
        std::vector<std::vector<int>> result;
        std::vector<int> cur;
        dfs(k, n, 1, cur, result);
        return result;
    }
};
```

```python
def combination_sum3(k: int, n: int) -> list[list[int]]:
    """
    @param k: how many numbers
    @param n: target sum
    @return:  all k distinct 1..9 numbers summing to n
    """
    result = []

    def dfs(start: int, remaining: int, cur: list[int]) -> None:
        if len(cur) == k and remaining == 0:
            result.append(cur[:])
            return

        for i in range(start, 10):
            if remaining - i < 0:
                break                       # prune: too big
            cur.append(i)
            dfs(i + 1, remaining - i, cur)
            cur.pop()                       # undo

    dfs(1, n, [])
    return result
```

```rust
impl Solution {
    /// @param k how many numbers
    /// @param n target sum
    /// @return  all k distinct 1..9 numbers summing to n
    pub fn combination_sum3(k: i32, n: i32) -> Vec<Vec<i32>> {
        let mut result = Vec::new();

        fn dfs(k: i32, remaining: i32, start: i32, cur: &mut Vec<i32>, result: &mut Vec<Vec<i32>>) {
            if cur.len() == k as usize && remaining == 0 {
                result.push(cur.clone());
                return;
            }
            for i in start..=9 {
                if remaining - i < 0 { break; }    // prune: too big
                cur.push(i);
                dfs(k, remaining - i, i + 1, cur, result);
                cur.pop();                         // undo
            }
        }

        dfs(k, n, 1, &mut Vec::new(), &mut result);
        result
    }
}
```

## Dry run

**Input:** `k = 3, n = 7`.

```
dfs(1, 7, []):
  i=1: remaining 7-1=6.  cur=[1].  dfs(2, 6):
    i=2: rem 4.  cur=[1,2].  dfs(3, 4):
      i=3: rem 1.  cur=[1,2,3].  dfs(4, 1):
        i=4: rem 1-4<0 -> break (prune!).  size 3 but remaining 1 != 0 -> nothing.
      i=4: rem 0.  cur=[1,2,4].  dfs(5, 0):
        i=5: 0-5<0 break.  size 3, remaining 0 -> add [1,2,4] ✓
      i=5: rem -1 -> break.
  ... i=3 at top: [1,3]: 7-1-3=3: i=4 rem -1 break... nothing completes.
  i=2 at top: [2]: rem 5: i=3 -> rem 2: i=4 rem -2 break; size 2.  nothing (need size 3).
  ...

Output: [[1,2,4]] ✓
```

The two gates are both needed: `curr.size == k` (exactly k numbers) AND `remaining == 0` (exact sum). The `break` prune cuts the `[1,2,3,...]` tail the moment the sum would go negative — candidates ascend, so the rest are all too big. `k=3, n=9` → `[[1,2,6],[1,3,5],[2,3,4]]` the same way.

## Complexity

**Time.** Bounded by C(9, k):

$$
T(k) = O(C(9, k))
$$

**Space.** Recursion depth:

$$
S(k) = O(k)
$$

## Variants & follow-ups

- **Combination Sum** ([12.8](combination-sum.md)) — unlimited picks (`i` not `i+1`), any size.
- **Combinations** ([12.12](combinations.md)) — the size gate without the sum gate.
- **Interview follow-up:** "Why `break` and not `continue`?" The candidates are strictly increasing — once `i` exceeds `remaining`, every later `i` is larger still. `break` exits the whole loop; `continue` would wastefully test them all. The pruning is valid exactly because of the increasing order.
