# 12.12 Combinations

> **Source**: [`src/main/kotlin/array/Combinatorics/Combinations.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/Combinatorics/Combinations.kt)
> **Pattern**: k-sized subset backtrack · **Core page**

## The Problem

All k-combinations of `1..n` (in lexicographic order).

- Constraints: 1 ≤ k ≤ n ≤ 20.

## Examples

```
Input:  n = 4, k = 2   -> Output: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]
```

## Intuition — the [12.1](subsets.md) backtrack with a size cap

Same for-loop recursion, stopping at `current.size == k` instead of collecting every prefix:

```kotlin
fun combine(start: Int, n: Int, k: Int, current: MutableList<Int>) {
    if (current.size == k) {
        result.add(current.toList())
        return
    }

    for (i in start..n) {
        current.add(i)
        combine(i + 1, n, k, current)
        current.removeLast()
    }
}
```

**Why `i + 1`?** Combinations are unordered — starting the next pick after the current one prevents duplicates and permutations. The [12.1](subsets.md) engine with a fixed size.

## Approach 1 — Size-capped backtrack (the repo's version, optimal)

```kotlin
class Combinations {
    /**
     * @param n upper bound
     * @param k combination size
     * @return  all k-combinations of 1..n
     */
    fun combine(n: Int, k: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        fun combine(start: Int, current: MutableList<Int>) {
            if (current.size == k) {
                result.add(current.toList())
                return
            }

            for (i in start..n) {
                current.add(i)
                combine(i + 1, current)
                current.removeLast()
            }
        }

        combine(1, mutableListOf())
        return result
    }
}
```

```java
import java.util.*;

public class Combinations {
    private int n, k;
    private List<List<Integer>> result = new ArrayList<>();

    private void backtrack(int start, List<Integer> current) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i <= n; i++) {
            current.add(i);
            backtrack(i + 1, current);
            current.remove(current.size() - 1);
        }
    }

    /**
     * @param n upper bound
     * @param k combination size
     * @return  all k-combinations of 1..n
     */
    public List<List<Integer>> combine(int n, int k) {
        this.n = n;
        this.k = k;
        result = new ArrayList<>();
        backtrack(1, new ArrayList<>());
        return result;
    }
}
```

```cpp
#include <vector>

class Combinations {
    int n, k;
    std::vector<std::vector<int>> result;

    void backtrack(int start, std::vector<int>& current) {
        if ((int)current.size() == k) {
            result.push_back(current);
            return;
        }

        for (int i = start; i <= n; i++) {
            current.push_back(i);
            backtrack(i + 1, current);
            current.pop_back();
        }
    }

public:
    /**
     * @param n upper bound
     * @param k combination size
     * @return  all k-combinations of 1..n
     */
    std::vector<std::vector<int>> combine(int n, int k) {
        this->n = n;
        this->k = k;
        backtrack(1, std::vector<int>());
        return result;
    }
};
```

```python
def combine(n: int, k: int) -> list[list[int]]:
    """
    @param n: upper bound
    @param k: combination size
    @return:  all k-combinations of 1..n
    """
    result = []

    def backtrack(start: int, current: list[int]) -> None:
        if len(current) == k:
            result.append(current[:])
            return

        for i in range(start, n + 1):
            current.append(i)
            backtrack(i + 1, current)
            current.pop()

    backtrack(1, [])
    return result
```

```rust
impl Solution {
    /// @param n upper bound
    /// @param k combination size
    /// @return  all k-combinations of 1..n
    pub fn combine(n: i32, k: i32) -> Vec<Vec<i32>> {
        let mut result = Vec::new();

        fn backtrack(n: i32, k: i32, start: i32, cur: &mut Vec<i32>, result: &mut Vec<Vec<i32>>) {
            if cur.len() == k as usize {
                result.push(cur.clone());
                return;
            }

            for i in start..=n {
                cur.push(i);
                backtrack(n, k, i + 1, cur, result);
                cur.pop();
            }
        }

        backtrack(n, k, 1, &mut Vec::new(), &mut result);
        result
    }
}
```

## Dry run

**Input:** `n = 4, k = 2`.

```
backtrack(1, []): i=1 -> [1] -> backtrack(2): i=2 -> [1,2] (add).  i=3 -> [1,3].  i=4 -> [1,4].
i=2 -> [2] -> [2,3], [2,4].  i=3 -> [3,4].  i=4 -> [4]? no more.
Output: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]] ✓
```

## Complexity

**Time.** C(n,k) leaves:

$$
T(n, k) = O(k \cdot C(n, k))
$$

**Space.** The recursion:

$$
S(n, k) = O(k)
$$

## Variants & follow-ups

- **Subsets** ([12.1](subsets.md)) — all sizes, no cap.
- **Combination Sum** — the sum-constrained variant.
- **Interview follow-up:** "Why does `i + 1` prevent duplicates?" A combination is orderless — forcing strictly increasing picks means each set is generated exactly once (in sorted order). The `start` parameter is the "no smaller elements" invariant.
