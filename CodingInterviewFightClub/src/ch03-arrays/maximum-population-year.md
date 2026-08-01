# 3.51 Maximum Population Year

> **Source**: [`src/main/kotlin/array/sweepline/MaximumPopulationYear.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/sweepline/MaximumPopulationYear.kt)
> **Pattern**: difference map sweep · **Core page**

## The Problem

The **earliest** year with the max alive population (birth year ≤ year < death).

- Constraints: years in [1950, 2050].

## Examples

```
Input:  logs = [[1993,1999],[2000,2010]]   -> Output: 1993
```

## Intuition — the [11.21](../ch11-greedy/car-pooling.md) difference sweep over years

```kotlin
val deltas = mutableMapOf<Int, Int>()
var (minYear, maxYear) = Int.MAX_VALUE to Int.MIN_VALUE

for ((birth, death) in logs) {
    deltas[birth] = (deltas[birth] ?: 0) + 1
    deltas[death] = (deltas[death] ?: 0) - 1
    minYear = minOf(minYear, birth)
    maxYear = maxOf(maxYear, death)
}

var population = 0
var (maxPopulation, resultYear) = 0 to minYear
for (year in minYear..maxYear) {
    population += deltas[year] ?: 0
    if (population > maxPopulation) {
        maxPopulation = population
        resultYear = year
    }
}
return resultYear
```

## Approach 1 — Difference map (the repo's version, optimal)

```kotlin
class MaximumPopulationYear {
    /**
     * @param logs [birth, death] pairs
     * @return     earliest peak-population year
     */
    fun maximumPopulation(logs: Array<IntArray>): Int {
        val deltas = mutableMapOf<Int, Int>()
        var (minYear, maxYear) = Int.MAX_VALUE to Int.MIN_VALUE

        for ((birth, death) in logs) {
            deltas[birth] = (deltas[birth] ?: 0) + 1
            deltas[death] = (deltas[death] ?: 0) - 1
            minYear = minOf(minYear, birth)
            maxYear = maxOf(maxYear, death)
        }

        var population = 0
        var (maxPopulation, resultYear) = 0 to minYear

        for (year in minYear..maxYear) {
            population += deltas[year] ?: 0
            if (population > maxPopulation) {
                maxPopulation = population
                resultYear = year
            }
        }
        return resultYear
    }
}
```

```java
import java.util.*;

public class MaximumPopulationYear {
    /**
     * @param logs [birth, death] pairs
     * @return     earliest peak-population year
     */
    public int maximumPopulation(int[][] logs) {
        int[] delta = new int[2051];

        for (int[] log : logs) {
            delta[log[0]]++;
            delta[log[1]]--;
        }

        int population = 0, best = 0, year = 1950;
        for (int y = 1950; y <= 2050; y++) {
            population += delta[y];
            if (population > best) {
                best = population;
                year = y;
            }
        }
        return year;
    }
}
```

```cpp
#include <vector>
#include <array>

class MaximumPopulationYear {
public:
    /**
     * @param logs [birth, death] pairs
     * @return     earliest peak-population year
     */
    int maximumPopulation(std::vector<std::vector<int>>& logs) {
        std::array<int, 2051> delta{};

        for (auto& log : logs) {
            delta[log[0]]++;
            delta[log[1]]--;
        }

        int population = 0, best = 0, year = 1950;
        for (int y = 1950; y <= 2050; y++) {
            population += delta[y];
            if (population > best) { best = population; year = y; }
        }
        return year;
    }
};
```

```python
def maximum_population(logs: list[list[int]]) -> int:
    """
    @param logs: [birth, death] pairs
    @return:     earliest peak-population year
    """
    delta = [0] * 2051

    for birth, death in logs:
        delta[birth] += 1
        delta[death] -= 1

    population = best = 0
    year = 1950

    for y in range(1950, 2051):
        population += delta[y]
        if population > best:
            best = population
            year = y

    return year
```

```rust
impl Solution {
    /// @param logs [birth, death] pairs
    /// @return     earliest peak-population year
    pub fn maximum_population(logs: Vec<Vec<i32>>) -> i32 {
        let mut delta = vec![0i32; 2051];

        for log in &logs {
            delta[log[0] as usize] += 1;
            delta[log[1] as usize] -= 1;
        }

        let (mut population, mut best, mut year) = (0, 0, 1950);
        for y in 1950..=2050 {
            population += delta[y as usize];
            if population > best { best = population; year = y; }
        }
        year
    }
}
```

## Dry run

**Input:** `logs = [[1993,1999],[2000,2010]]`.

```
deltas: 1993:+1, 1999:-1, 2000:+1, 2010:-1.
sweep: 1993: 1 (best).  1994-1998: 1.  1999: 0.  2000: 1.
Output: 1993 ✓ (first year reaching the peak)
```

## Complexity

**Time.** Years range:

$$
T = O(101) = O(1)
$$

**Space.** The delta array:

$$
S = O(1)
$$

## Variants & follow-ups

- **Car Pooling** ([11.21](../ch11-greedy/car-pooling.md)) — the identical difference sweep.
- **Interview follow-up:** "Why `< death` (not ≤)?" A person alive in year y must satisfy `birth ≤ y < death` — the death year decrements *at* death, excluding it from the population.
