# 17.2 Maximum Bipartite Matching

> **Source:** [`src/main/kotlin/graph/flow_network/MaximumBipartileJobMatching.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/flow_network/MaximumBipartileJobMatching.kt)
> **Pattern:** Kuhn's augmenting path · **Core page**

## The Problem

Given `numJobs` jobs, `numWorkers` workers, and an adjacency list of *compatible* (job, worker) pairs, find the **maximum matching**: assign as many jobs as possible, each worker taking at most one job.

- Constraints: small-to-medium bipartite graphs.

## Examples

```
jobs 0..2, workers 0..2
edges: (0,0), (0,1), (1,0), (2,1)
Max matching = 2   (e.g., job 0 -> worker 0, job 2 -> worker 1; job 1 unmatched)
```

## Intuition — "can this job be matched?" = "can we shuffle the existing matches?"

For each job, try to find a worker. The core recursion `canMatch`:

- if worker is **free** → take it;
- if worker is **taken by another job** → try to *reassign that job elsewhere* (recursively); if that succeeds, the worker is freed and this job takes it.

This is the **augmenting path** of the [17.0](pattern-primer.md) engine, in matching clothing: each recursion is "follow the current match and see if the displaced job can find a new home." The `visited` array per job prevents infinite loops (a job never re-probes the same worker twice in one attempt).

**Why does trying every job once (in any order) suffice?** When `canMatch(job)` succeeds, the matching grew by one; when it fails, no augmenting path exists *from that job* — and since the matching only grows, retrying earlier jobs isn't needed. This is Kuhn's algorithm: $O(V \cdot E)$ worst case (V attempts × E edges each).

**The `match` array is the state:** `match[worker] = job` (or -1). The recursion *commits* a reassignment only when the whole chain succeeds (`match[worker] = job` inside the success branch) — no undo bookkeeping needed, because a failed attempt leaves the match array untouched.

## Approach 1 — Max flow reduction

Model jobs/workers as a flow network (source -> jobs -> workers -> sink, capacity 1) and run [17.1](max-flow-edmonds-karp.md): correct, heavier machinery than needed.

## Approach 2 — Kuhn's augmenting path (the repo's version, optimal for bipartite)

```kotlin
class BipartiteMatching(private val numJobs: Int, private val numWorkers: Int) {
    // Adjacency list: Job index -> List of compatible Worker indices
    private val adj = Array(numJobs) { mutableListOf<Int>() }

    fun addEdge(job: Int, worker: Int) {
        adj[job].add(worker)
    }

    /**
     * @return maximum number of jobs that can be matched
     */
    fun maxMatching(): Int {
        // match[worker] = job assigned to them, -1 if free
        val match = IntArray(numWorkers) { -1 }
        var result = 0

        for (job in 0 until numJobs) {
            // For each job, try to find a worker using a fresh visited array
            val visited = BooleanArray(numWorkers)
            if (canMatch(job, visited, match)) {
                result++
            }
        }
        return result
    }

    // Try to find a free worker for `job`, possibly by reassigning others
    private fun canMatch(job: Int, visited: BooleanArray, match: IntArray): Boolean {
        for (worker in adj[job]) {
            if (!visited[worker]) {
                visited[worker] = true

                // If worker is free OR the job currently holding the worker can move
                if (match[worker] < 0 || canMatch(match[worker], visited, match)) {
                    match[worker] = job
                    return true
                }
            }
        }
        return false
    }
}
```

```java
import java.util.*;

public class BipartiteMatching {
    private final List<List<Integer>> adj = new ArrayList<>();
    private int[] match;

    /** @param numJobs jobs @param numWorkers workers */
    public BipartiteMatching(int numJobs, int numWorkers) {
        for (int i = 0; i < numJobs; i++) adj.add(new ArrayList<>());
        match = new int[numWorkers];
        Arrays.fill(match, -1);
    }

    public void addEdge(int job, int worker) { adj.get(job).add(worker); }

    /**
     * @return maximum number of jobs that can be matched
     */
    public int maxMatching() {
        int result = 0;
        for (int job = 0; job < adj.size(); job++) {
            boolean[] visited = new boolean[match.length];
            if (canMatch(job, visited)) result++;
        }
        return result;
    }

    private boolean canMatch(int job, boolean[] visited) {
        for (int worker : adj.get(job)) {
            if (!visited[worker]) {
                visited[worker] = true;
                // free worker, or the incumbent job can be reassigned
                if (match[worker] == -1 || canMatch(match[worker], visited)) {
                    match[worker] = job;
                    return true;
                }
            }
        }
        return false;
    }
}
```

```cpp
#include <vector>

class BipartiteMatching {
    std::vector<std::vector<int>> adj;      // job -> compatible workers
    std::vector<int> match;                 // worker -> job, -1 if free

    bool canMatch(int job, std::vector<bool>& visited) {
        for (int worker : adj[job]) {
            if (!visited[worker]) {
                visited[worker] = true;
                // free worker, or the incumbent job can be reassigned
                if (match[worker] == -1 || canMatch(match[worker], visited)) {
                    match[worker] = job;
                    return true;
                }
            }
        }
        return false;
    }

public:
    /** @param numJobs jobs @param numWorkers workers */
    BipartiteMatching(int numJobs, int numWorkers)
        : adj(numJobs), match(numWorkers, -1) {}

    void addEdge(int job, int worker) { adj[job].push_back(worker); }

    /**
     * @return maximum number of jobs that can be matched
     */
    int maxMatching() {
        int result = 0;
        for (int job = 0; job < (int)adj.size(); job++) {
            std::vector<bool> visited(match.size(), false);
            if (canMatch(job, visited)) result++;
        }
        return result;
    }
};
```

```python
class BipartiteMatching:
    """@param num_jobs: jobs  @param num_workers: workers"""

    def __init__(self, num_jobs: int, num_workers: int):
        self.adj = [[] for _ in range(num_jobs)]
        self.match = [-1] * num_workers

    def add_edge(self, job: int, worker: int) -> None:
        self.adj[job].append(worker)

    def max_matching(self) -> int:
        """@return: maximum number of jobs that can be matched"""
        result = 0
        for job in range(len(self.adj)):
            visited = [False] * len(self.match)   # fresh per job attempt
            if self._can_match(job, visited):
                result += 1
        return result

    def _can_match(self, job: int, visited: list[bool]) -> bool:
        for worker in self.adj[job]:
            if not visited[worker]:
                visited[worker] = True
                # free worker, or the incumbent job can be reassigned
                if self.match[worker] == -1 or self._can_match(self.match[worker], visited):
                    self.match[worker] = job
                    return True
        return False
```

```rust
impl Solution {
    /// @param adj     job -> compatible workers
    /// @param workers number of workers
    /// @return        maximum number of jobs that can be matched
    pub fn max_matching(adj: Vec<Vec<usize>>, workers: usize) -> i32 {
        fn can_match(job: usize, adj: &Vec<Vec<usize>>, visited: &mut Vec<bool>, match: &mut Vec<i32>) -> bool {
            for &worker in &adj[job] {
                if !visited[worker] {
                    visited[worker] = true;
                    // free worker, or the incumbent job can be reassigned
                    if match[worker] == -1 || can_match(match[worker] as usize, adj, visited, match) {
                        match[worker] = job as i32;
                        return true;
                    }
                }
            }
            false
        }

        let mut match = vec![-1i32; workers];
        let mut result = 0;
        for job in 0..adj.len() {
            let mut visited = vec![false; workers];
            if can_match(job, &adj, &mut visited, &mut match) {
                result += 1;
            }
        }
        result
    }
}
```

## Dry run

**Input:** jobs {0,1,2}, workers {0,1,2}; edges `(0,0), (0,1), (1,0), (2,1)`.

```
job 0: try worker 0 (free) -> match[0]=0.  matching = 1.
job 1: try worker 0 (taken by job 0) -> visited[0]=true; recurse canMatch(job 0):
         job 0's other worker: 1 (free) -> match[1]=0 (job 0 moves to worker 1).
       -> match[0]=1.  matching = 2.
job 2: try worker 1 (taken by job 0) -> visited[1]=true; recurse canMatch(job 0):
         job 0's workers: 0 (taken by job 1) -> visited[0]=true; recurse canMatch(job 1):
           job 1's workers: 0 only, visited -> false.
         1 (visited) -> false.  -> false.
       try worker 2? job 2 has no edge to 2.  -> false.
matching = 2 ✓
```

The chain reaction at job 2 is the algorithm's heart: worker 1 is occupied, so the search tries to *move* the incumbent (job 0) — which needs job 1's worker, which has no alternative — and the whole chain fails, so no reassignment is committed. A failed `canMatch` leaves `match` untouched; only a fully successful chain writes.

## Complexity

**Time.** V job attempts × E edges each:

$$
T(V, E) = O(VE)
$$

**Space.** The match array + recursion:

$$
S = O(V)
$$

## Variants & follow-ups

- **Max Flow formulation** — add source/sink with unit capacities and run [17.1](max-flow-edmonds-karp.md): the same answer, heavier machinery (useful when *weights* enter, i.e., max-weight matching).
- **Course Schedule-style conflicts** — bipartite matching is the "assign without conflicts" engine behind scheduling, seating, and pairing problems.
- **Interview follow-up:** "Why does one attempt per job suffice?" If `canMatch(job)` fails, no augmenting path starts at `job` — and since later matches only *reassign*, never un-match, a failed job can never become matchable later. Hence a single pass over jobs (with fresh `visited` per attempt) finds the maximum.
