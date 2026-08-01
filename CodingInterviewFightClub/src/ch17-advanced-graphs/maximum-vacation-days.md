# 17.18 Maximum Vacation Days

> **Source**: [`src/main/kotlin/graph/dp/MaximumVacationDays.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/dp/MaximumVacationDays.kt)
> **Pattern**: week-by-week DP over cities · **Core page**

## The Problem

Max vacation days over `k` weeks: each week stay in a city (or fly along flights) and collect `days[city][week]`; start at city 0.

- Constraints: cities ≤ 100; weeks ≤ 20.

## Examples

```
Input:  flights = [[0,1,1],[1,0,1],[1,1,0]], days = [[1,3,1],[6,0,3],[3,3,3]]
Output: 12
```

## Intuition — dp[city][week] = best days ending in city at week; relax from the previous week

```kotlin
data class State(val city: Int, val week: Int)
val cache = mutableMapOf<State, Int>()

fun solve(city: Int, week: Int): Int = cache.getOrPut(State(city, week)) {
    if (week == numWeeks) 0
    else {
        var best = 0
        // stay or fly from city to any neighbor
        for (next in 0 until numCities) {
            if (city == next || flights[city][next] == 1) {
                best = maxOf(best, days[next][week] + solve(next, week + 1))
            }
        }
        best
    }
}
return solve(0, 0)
```

## Approach 1 — Memoized week DP (the repo's version, optimal)

```kotlin
class MaximumVacationDays {
    /**
     * @param flights adjacency matrix
     * @param days    days[city][week]
     * @return        max vacation days
     */
    fun maxVacationDays(flights: Array<IntArray>, days: Array<IntArray>): Int {
        val numCities = flights.size
        val numWeeks = days[0].size

        data class State(val city: Int, val week: Int)
        val cache = mutableMapOf<State, Int>()

        fun solve(city: Int, week: Int): Int = cache.getOrPut(State(city, week)) {
            if (week == numWeeks) 0
            else {
                var best = 0

                for (next in 0 until numCities) {
                    if (city == next || flights[city][next] == 1) {
                        best = maxOf(best, days[next][week] + solve(next, week + 1))
                    }
                }
                best
            }
        }

        return solve(0, 0)
    }
}
```

```java
import java.util.*;

public class MaximumVacationDays {
    private int[][] flights, days;
    private int[][] memo;

    private int solve(int city, int week) {
        if (week == days[0].length) return 0;
        if (memo[city][week] != -1) return memo[city][week];

        int best = 0;
        for (int next = 0; next < flights.length; next++) {
            if (city == next || flights[city][next] == 1) {
                best = Math.max(best, days[next][week] + solve(next, week + 1));
            }
        }
        return memo[city][week] = best;
    }

    /**
     * @param flights adjacency matrix
     * @param days    days[city][week]
     * @return        max vacation days
     */
    public int maxVacationDays(int[][] flights, int[][] days) {
        this.flights = flights;
        this.days = days;
        memo = new int[flights.length][days[0].length];
        for (int[] row : memo) Arrays.fill(row, -1);
        return solve(0, 0);
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MaximumVacationDays {
    int solve(int city, int week, std::vector<std::vector<int>>& flights,
              std::vector<std::vector<int>>& days, std::vector<std::vector<int>>& memo) {
        if (week == (int)days[0].size()) return 0;
        if (memo[city][week] != -1) return memo[city][week];

        int best = 0;
        for (int next = 0; next < (int)flights.size(); next++) {
            if (city == next || flights[city][next]) {
                best = std::max(best, days[next][week] + solve(next, week + 1, flights, days, memo));
            }
        }
        return memo[city][week] = best;
    }

public:
    /**
     * @param flights adjacency matrix
     * @param days    days[city][week]
     * @return        max vacation days
     */
    int maxVacationDays(std::vector<std::vector<int>>& flights, std::vector<std::vector<int>>& days) {
        int cities = flights.size(), weeks = days[0].size();
        std::vector<std::vector<int>> memo(cities, std::vector<int>(weeks, -1));
        return solve(0, 0, flights, days, memo);
    }
};
```

```python
def max_vacation_days(flights: list[list[int]], days: list[list[int]]) -> int:
    """
    @param flights: adjacency matrix
    @param days:    days[city][week]
    @return:        max vacation days
    """
    cities, weeks = len(flights), len(days[0])
    from functools import lru_cache

    @lru_cache(None)
    def solve(city: int, week: int) -> int:
        if week == weeks:
            return 0

        best = 0
        for nxt in range(cities):
            if city == nxt or flights[city][nxt]:
                best = max(best, days[nxt][week] + solve(nxt, week + 1))

        return best

    return solve(0, 0)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param flights adjacency matrix
    /// @param days    days[city][week]
    /// @return        max vacation days
    pub fn max_vacation_days(flights: Vec<Vec<i32>>, days: Vec<Vec<i32>>) -> i32 {
        let (cities, weeks) = (flights.len(), days[0].len());
        let mut memo = HashMap::new();

        fn solve(city: usize, week: usize, flights: &Vec<Vec<i32>>, days: &Vec<Vec<i32>>,
                 memo: &mut HashMap<(usize, usize), i32>) -> i32 {
            if week == days[0].len() { return 0; }
            if let Some(&v) = memo.get(&(city, week)) { return v; }

            let mut best = 0;
            for nxt in 0..flights.len() {
                if city == nxt || flights[city][nxt] == 1 {
                    best = best.max(days[nxt][week] + solve(nxt, week + 1, flights, days, memo));
                }
            }
            memo.insert((city, week), best);
            best
        }

        solve(0, 0, &flights, &days, &mut memo)
    }
}
```

## Dry run

**Input:** the example.

```
solve(0,0): week 0 cities: stay 0 (days[0][0]=1), fly 1 (days[1][0]=6), fly 2 (days[2][0]=3).
  best path: 1 (6) -> week 1: stay 1 (0)? days[1][1]=0; fly 2 (days[2][1]=3) -> week 2: stay 2 (3): total 6+3+3=12.
Output: 12 ✓
```

## Complexity

**Time.** Cities² × weeks:

$$
T(c, w) = O(c^2 \cdot w)
$$

**Space.** The memo:

$$
S(c, w) = O(c \cdot w)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why the stay-or-fly union in the relax loop?" `city == next` covers staying (the identity edge) — one loop handles both without special cases.
