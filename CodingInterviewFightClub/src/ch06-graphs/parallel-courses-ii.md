# 6.24 Parallel Courses II

> **Source**: [`src/main/kotlin/google/SemesterScheduler.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/google/SemesterScheduler.kt) (+ `CourseWithSemesterConstraint.kt`, `MinimumTimeToFinishBuildByKWorkers.kt` — the greedy-fails note)
> **Pattern**: bitmask DP over taken courses · **Core page**

## The Problem

n courses with prerequisites; take **at most k per semester**. Min semesters.

- Constraints: n ≤ 15 (bitmask!), k ≤ n.

## Examples

```
Input:  n = 4, dependencies = [[2,1],[3,1],[1,4]], k = 2
Output: 3   (sem 1: 2,3; sem 2: 1; sem 3: 4)
```

## Intuition — the state is a bitmask of finished courses; pick any valid subset ≤ k

With n ≤ 15, the finished set fits in an `Int`/`Long`. DP over masks: `minSemesters(finished)` = 1 + min over all valid next subsets:

```kotlin
val allFinished = (1L shl n) - 1

fun minSemesters(finished: Long): Int = cache.getOrPut(finished) {
    when {
        finished == allFinished -> 0
        else -> {
            var availableMask = 0L
            for (i in 0 until n) {
                val isFinished = (finished and (1L shl i)) != 0L
                val prereqsMet = (finished and requirements[i]) == requirements[i]
                if (!isFinished && prereqsMet) availableMask = availableMask or (1L shl i)
            }

            val possibleCount = availableMask.countOneBits()

            when {
                possibleCount <= k -> 1 + minSemesters(finished or availableMask)   // take all
                else -> {
                    // enumerate subsets of availableMask of size <= k, take the best
                    1 + min over valid subsets of minSemesters(finished or subset)
                }
            }
        }
    }
}
```

**Why bitmask?** "Which courses are done" is the complete state — a `Long` bitmask makes it hashable and O(1)-comparable. The [17.4](../ch17-advanced-graphs/travelling-salesman-held-karp.md) bitmask-DP discipline ([ch17](../ch17-advanced-graphs/pattern-primer.md) style).

**Why enumerate subsets when `availableCount > k`?** The semester cap forces a choice of *which* available courses to take — the optimal subset isn't greedy (taking the most is NOT always best — that's the repo's `MinimumTimeToFinishBuildByKWorkers` comment: "greedy BFS won't work, NP-complete DP required").

## Approach 1 — Greedy BFS per semester (wrong!)

Take any k available each round: fails — the repo's own `minTime` notes it.

## Approach 2 — Bitmask DP (the repo's `SemesterScheduler`, optimal)

```kotlin
class SemesterScheduler(val n: Int, val k: Int, val requirements: LongArray) {
    private val cache = mutableMapOf<Long, Int>()
    private val allFinished = (1L shl n) - 1

    /**
     * @param finished bitmask of completed courses
     * @return        min semesters to finish the rest
     */
    fun minSemesters(finished: Long = 0L): Int = cache.getOrPut(finished) {
        when {
            finished == allFinished -> 0
            else -> {
                var availableMask = 0L
                for (i in 0 until n) {
                    val isFinished = (finished and (1L shl i)) != 0L
                    val prereqsMet = (finished and requirements[i]) == requirements[i]
                    if (!isFinished && prereqsMet) {
                        availableMask = availableMask or (1L shl i)
                    }
                }

                val possibleCount = availableMask.countOneBits()

                when {
                    possibleCount <= k -> 1 + minSemesters(finished or availableMask)
                    else -> {
                        var best = Int.MAX_VALUE
                        var subset = availableMask
                        while (subset > 0) {          // enumerate submasks
                            if (subset.countOneBits() <= k) {
                                best = minOf(best, 1 + minSemesters(finished or subset))
                            }
                            subset = (subset - 1) and availableMask
                        }
                        best
                    }
                }
            }
        }
    }
}
```

```java
import java.util.*;

public class ParallelCoursesII {
    private Map<Integer, Integer> memo = new HashMap<>();
    private int n, k;
    private int[] prereqs;

    private int solve(int finished) {
        if (finished == (1 << n) - 1) return 0;
        if (memo.containsKey(finished)) return memo.get(finished);

        int available = 0;
        for (int i = 0; i < n; i++) {
            if ((finished & (1 << i)) == 0 && (finished & prereqs[i]) == prereqs[i]) {
                available |= (1 << i);
            }
        }

        if (Integer.bitCount(available) <= k) {
            return memo.put(finished, 1 + solve(finished | available));
        }

        int best = Integer.MAX_VALUE;
        for (int sub = available; sub > 0; sub = (sub - 1) & available) {
            if (Integer.bitCount(sub) <= k) {
                best = Math.min(best, 1 + solve(finished | sub));
            }
        }
        return memo.put(finished, best);
    }

    /**
     * @param n            course count
     * @param dependencies prerequisite pairs
     * @param k            semester cap
     * @return             min semesters
     */
    public int minNumberOfSemesters(int n, int[][] dependencies, int k) {
        this.n = n;
        this.k = k;
        prereqs = new int[n];

        for (int[] d : dependencies) {
            prereqs[d[1] - 1] |= (1 << (d[0] - 1));    // bit i = course i's prerequisite set
        }
        return solve(0);
    }
}
```

```cpp
#include <vector>
#include <unordered_map>

class ParallelCoursesII {
    std::unordered_map<int, int> memo;
    int n, k;
    std::vector<int> prereqs;

    int solve(int finished) {
        if (finished == (1 << n) - 1) return 0;
        if (memo.count(finished)) return memo[finished];

        int available = 0;
        for (int i = 0; i < n; i++) {
            if ((finished & (1 << i)) == 0 && (finished & prereqs[i]) == prereqs[i]) {
                available |= (1 << i);
            }
        }

        if (__builtin_popcount(available) <= k) {
            return memo[finished] = 1 + solve(finished | available);
        }

        int best = INT_MAX;
        for (int sub = available; sub > 0; sub = (sub - 1) & available) {
            if (__builtin_popcount(sub) <= k) {
                best = std::min(best, 1 + solve(finished | sub));
            }
        }
        return memo[finished] = best;
    }

public:
    /**
     * @param n            course count
     * @param dependencies prerequisite pairs
     * @param k            semester cap
     * @return             min semesters
     */
    int minNumberOfSemesters(int n, std::vector<std::vector<int>>& dependencies, int k) {
        this->n = n;
        this->k = k;
        prereqs.assign(n, 0);

        for (auto& d : dependencies) {
            prereqs[d[1] - 1] |= (1 << (d[0] - 1));
        }
        return solve(0);
    }
};
```

```python
from functools import lru_cache

def min_number_of_semesters(n: int, dependencies: list[list[int]], k: int) -> int:
    """
    @param n:            course count
    @param dependencies: prerequisite pairs
    @param k:            semester cap
    @return:             min semesters
    """
    prereqs = [0] * n
    for pre, course in dependencies:
        prereqs[course - 1] |= 1 << (pre - 1)

    all_finished = (1 << n) - 1

    @lru_cache(None)
    def solve(finished: int) -> int:
        if finished == all_finished:
            return 0

        available = 0
        for i in range(n):
            if (finished & (1 << i)) == 0 and (finished & prereqs[i]) == prereqs[i]:
                available |= 1 << i

        if available.bit_count() <= k:
            return 1 + solve(finished | available)

        best = float("inf")
        sub = available
        while sub:
            if sub.bit_count() <= k:
                best = min(best, 1 + solve(finished | sub))
            sub = (sub - 1) & available
        return best

    return solve(0)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param n            course count
    /// @param dependencies prerequisite pairs
    /// @param k            semester cap
    /// @return             min semesters
    pub fn min_number_of_semesters(n: i32, dependencies: Vec<Vec<i32>>, k: i32) -> i32 {
        let n = n as usize;
        let mut prereqs = vec![0u32; n];
        for d in dependencies {
            prereqs[d[1] as usize - 1] |= 1 << (d[0] - 1);
        }

        let all = (1u32 << n) - 1;
        let mut memo: HashMap<u32, i32> = HashMap::new();

        fn solve(finished: u32, prereqs: &Vec<u32>, k: usize, n: usize,
                 all: u32, memo: &mut HashMap<u32, i32>) -> i32 {
            if finished == all { return 0; }
            if let Some(&v) = memo.get(&finished) { return v; }

            let mut available = 0u32;
            for i in 0..n {
                if finished & (1 << i) == 0 && finished & prereqs[i] == prereqs[i] {
                    available |= 1 << i;
                }
            }

            let result = if available.count_ones() as usize <= k {
                1 + solve(finished | available, prereqs, k, n, all, memo)
            } else {
                let mut best = i32::MAX;
                let mut sub = available;
                while sub > 0 {
                    if sub.count_ones() as usize <= k {
                        best = best.min(1 + solve(finished | sub, prereqs, k, n, all, memo));
                    }
                    sub = (sub - 1) & available;
                }
                best
            };
            memo.insert(finished, result);
            result
        }

        solve(0, &prereqs, k as usize, n, all, &mut memo)
    }
}
```

## Dry run

**Input:** `n = 4, dependencies = [[2,1],[3,1],[1,4]], k = 2`.

```
prereqs: course 1 needs {2,3}, course 4 needs {1}.
solve(0): available = {2,3} (bits 1,2).  count 2 <= k=2 -> take both:
  1 + solve({2,3}).
solve({2,3}): available = {1} (prereqs met).  count 1 <= 2 -> take:
  1 + solve({2,3,1}).
solve({2,3,1}): available = {4}.  take: 1 + solve(all) = 1.
total: 3 ✓
```

The `availableCount <= k` shortcut (take everything) is the DP's easy branch; the hard branch enumerates submasks when the cap binds. The repo's `MinimumTimeToFinishBuildByKWorkers` is the same problem with the greedy-fails warning — the submask enumeration is the NP-hard core.

## Complexity

**Time.** 2ⁿ masks × submask enumeration:

$$
T(n) = O(3^n)
$$

**Space.** The memo:

$$
S(n) = O(2^n)
$$

## Variants & follow-ups

- **Course Schedule II** ([6.3](course-schedule-ii.md)) — the no-cap Kahn's ancestor.
- **Travelling Salesman** ([17.4](../ch17-advanced-graphs/travelling-salesman-held-karp.md)) — the bitmask-DP state idiom shared.
- **Interview follow-up:** "Why is greedy wrong here?" Taking the *most* available courses can strand a critical prerequisite chain (the repo's comment: "greedy BFS won't work"). The subset enumeration explores the actual tradeoff — which available courses to defer — the NP-hard part the bitmask makes feasible at n ≤ 15.
