# 11.15 Max Profit Assigning Work

> **Source**: [`src/main/kotlin/greedy/MaxProfiAssigningWork.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/MaxProfiAssigningWork.kt)
> **Pattern**: sort + best-so-far sweep · **Core page**

## The Problem

Assign each worker a task they can do (difficulty ≤ ability) to maximize total profit.

- Constraints: tasks, workers ≤ 10⁴.

## Examples

```
Input:  difficulty = [2,4,6,8,10], profit = [10,20,30,40,50], worker = [4,5,6,7]
Output: 100   (worker 4 → task 2 (20); 5 → 2 (20); 6 → 3 (30); 7 → 3 (30))
```

## Intuition — sort tasks by difficulty, walk workers with a running best profit

For each worker (sorted ascending), the best task is the most profitable among all difficulties ≤ ability — a running max as tasks unlock:

```kotlin
val tasks = difficulty.zip(profit).map { Task(it.first, it.second) }.sortedBy { it.difficulty }
worker.sort()

var (taskIndex, maxProfit, currentMaxProfit) = listOf(0, 0, 0)

for (ability in worker) {
    while (taskIndex < tasks.size && tasks[taskIndex].difficulty <= ability) {
        currentMaxProfit = maxOf(currentMaxProfit, tasks[taskIndex].profit)
        taskIndex++
    }
    maxProfit += currentMaxProfit
}
return maxProfit
```

**Why the running best?** A worker can do *any* unlocked task — the best-so-far is the optimal pick. The monotone sweep (workers ascending, tasks ascending) makes each unlock permanent ([11.0](pattern-primer.md) two-pointer greedy).

## Approach 1 — For each worker, scan all tasks (O(W·T))

Find the best affordable: correct, slow.

## Approach 2 — Sorted sweep (the repo's version, optimal)

```kotlin
class MaxProfiAssigningWork {
    data class Task(val difficulty: Int, val profit: Int)

    /**
     * @param difficulty task difficulties
     * @param profit     task profits
     * @param worker     worker abilities
     * @return           max total profit
     */
    fun maxProfitAssignment(difficulty: IntArray, profit: IntArray, worker: IntArray): Int {
        val tasks = difficulty.zip(profit).map { Task(it.first, it.second) }.sortedBy { it.difficulty }
        worker.sort()

        var (taskIndex, maxProfit, currentMaxProfit) = listOf(0, 0, 0)

        for (ability in worker) {
            while (taskIndex < tasks.size && tasks[taskIndex].difficulty <= ability) {
                currentMaxProfit = maxOf(currentMaxProfit, tasks[taskIndex].profit)
                taskIndex++
            }
            maxProfit += currentMaxProfit
        }
        return maxProfit
    }
}
```

```java
import java.util.*;

public class MaxProfitAssigningWork {
    /**
     * @param difficulty task difficulties
     * @param profit     task profits
     * @param worker     worker abilities
     * @return           max total profit
     */
    public int maxProfitAssignment(int[] difficulty, int[] profit, int[] worker) {
        int n = difficulty.length;
        int[][] tasks = new int[n][2];
        for (int i = 0; i < n; i++) { tasks[i][0] = difficulty[i]; tasks[i][1] = profit[i]; }
        Arrays.sort(tasks, (a, b) -> a[0] - b[0]);
        Arrays.sort(worker);

        int idx = 0, best = 0, total = 0;

        for (int ability : worker) {
            while (idx < n && tasks[idx][0] <= ability) {
                best = Math.max(best, tasks[idx][1]);
                idx++;
            }
            total += best;
        }
        return total;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MaxProfitAssigningWork {
public:
    /**
     * @param difficulty task difficulties
     * @param profit     task profits
     * @param worker     worker abilities
     * @return           max total profit
     */
    int maxProfitAssignment(std::vector<int>& difficulty, std::vector<int>& profit,
                            std::vector<int>& worker) {
        int n = difficulty.size();
        std::vector<std::pair<int, int>> tasks;
        for (int i = 0; i < n; i++) tasks.push_back({difficulty[i], profit[i]});
        std::sort(tasks.begin(), tasks.end());
        std::sort(worker.begin(), worker.end());

        int idx = 0, best = 0, total = 0;
        for (int ability : worker) {
            while (idx < n && tasks[idx].first <= ability) {
                best = std::max(best, tasks[idx].second);
                idx++;
            }
            total += best;
        }
        return total;
    }
};
```

```python
def max_profit_assignment(difficulty: list[int], profit: list[int], worker: list[int]) -> int:
    """
    @param difficulty: task difficulties
    @param profit:     task profits
    @param worker:     worker abilities
    @return:           max total profit
    """
    tasks = sorted(zip(difficulty, profit))
    worker.sort()

    idx = best = total = 0
    for ability in worker:
        while idx < len(tasks) and tasks[idx][0] <= ability:
            best = max(best, tasks[idx][1])
            idx += 1
        total += best

    return total
```

```rust
impl Solution {
    /// @param difficulty task difficulties
    /// @param profit     task profits
    /// @param worker     worker abilities
    /// @return           max total profit
    pub fn max_profit_assignment(difficulty: Vec<i32>, profit: Vec<i32>, mut worker: Vec<i32>) -> i32 {
        let mut tasks: Vec<(i32, i32)> = difficulty.into_iter().zip(profit).collect();
        tasks.sort();
        worker.sort_unstable();

        let (mut idx, mut best, mut total) = (0, 0, 0);
        for ability in worker {
            while idx < tasks.len() && tasks[idx].0 <= ability {
                best = best.max(tasks[idx].1);
                idx += 1;
            }
            total += best;
        }
        total
    }
}
```

## Dry run

**Input:** `difficulty = [2,4,6,8,10], profit = [10,20,30,40,50], worker = [4,5,6,7]`.

```
tasks sorted: (2,10),(4,20),(6,30),(8,40),(10,50).  workers [4,5,6,7]
ability 4: unlock (2,10): best 10.  (4,20): best 20.  total += 20.
ability 5: no new tasks (6 > 5).  total += 20 = 40.
ability 6: unlock (6,30): best 30.  total += 30 = 70.
ability 7: no new.  total += 30 = 100.

Output: 100 ✓
```

The while-loop's monotonicity is the efficiency: task unlocks never regress, so each task is examined once across all workers. The `best` carries the optimal affordable profit; each worker adds it.

## Complexity

**Time.** Sorts + sweep:

$$
T = O((T + W) \log (T + W))
$$

**Space.** The task list:

$$
S = O(T)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why sort both sides?" The sweep needs both tasks and workers ascending so that each unlock is permanent and each worker's best is final. Sorting both is the two-pointer prerequisite; the result is one linear pass.
