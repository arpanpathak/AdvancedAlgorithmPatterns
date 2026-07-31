# 6.3 Course Schedule II

> **Source:** [`src/main/kotlin/graph/topological_sort/CourseSchedule_II_BFS.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/topological_sort/CourseSchedule_II_BFS.kt)
> **Pattern:** Kahn's topological sort · **Core page**

## The Problem

There are `numCourses` courses labeled `0..numCourses-1`. You're given `prerequisites[i] = [course, preReq]`, meaning `preReq` must be taken *before* `course`. Return any valid ordering of all courses, or an **empty array** if it's impossible (a cycle exists).

- Constraints: $1 \le numCourses \le 2000$; no duplicate prerequisites.

## Examples

```
Input:  numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
Output: [0,1,2,3] or [0,2,1,3]        (0 before 1&2, 1&2 before 3 — both valid)

Input:  numCourses = 2, prerequisites = [[1,0]]
Output: [0,1]

Input:  numCourses = 2, prerequisites = [[0,1],[1,0]]     (cycle)
Output: []
```

## Intuition — "who has nothing pending?" is a queue

Model courses as a **directed graph**: edge `preReq -> course` ("prereq unlocks course"). A valid ordering is a **topological order** — every edge points *forward* in the output. A topological order exists iff the graph is a DAG, so this one problem asks two things at once: *find the order* and *detect the cycle*.

**Kahn's algorithm** is the BFS-flavored engine:

1. Compute `inDegree` for every vertex (number of edges pointing at it).
2. Any vertex with `inDegree == 0` has no remaining prerequisites — it's *ready*. Seed a queue with all of them.
3. Pop a ready vertex, append it to the result, and "unlock" its outgoing edges: decrement each neighbor's `inDegree`; the moment one hits `0`, enqueue it.

The result accumulates exactly one topological order. At the end, if we processed **fewer than `numCourses`** vertices, some cycle never had an in-degree-0 member — return `[]`. That single comparison is the entire cycle detector.

**Why is the output a valid order?** Every vertex enters the result only after all its *prerequisites* were already popped (that's what `inDegree == 0` means at pop time). So every edge `preReq -> course` has `preReq` earlier in the result. Invariant held, no extra proof needed.

## Approach 1 — DFS with 3-color marking

Mark each vertex white/gray/black while DFS-ing; a gray back-edge means a cycle; append on finishing. $O(V+E)$ — correct, but the bookkeeping (three states, "is this gray?") is fiddlier than Kahn's and the code says less about *why* it works.

## Approach 2 — Kahn's algorithm (the repo's version, optimal)

```kotlin
class CourseSchedule_II_BFS {
    /**
     * @param numCourses    total number of courses
     * @param prerequisites pairs [course, preReq]: preReq must come before course
     * @return              any valid course order, or an empty array if a cycle exists
     */
    fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
        val graph = Array<MutableList<Int>>(numCourses) { mutableListOf() }
        val inDegree = IntArray(numCourses)
        val result = mutableListOf<Int>()

        // Build the graph and calculate in-degrees
        prerequisites.forEach { (course, preReq) ->
            graph[preReq].add(course)
            inDegree[course]++
        }

        // Initialize queue with courses having no prerequisites
        val queue = ArrayDeque<Int>().apply {
            inDegree.indices.filter { inDegree[it] == 0 }.forEach { add(it) }
        }

        // Perform BFS (Kahn's Algorithm)
        while (queue.isNotEmpty()) {
            queue.removeFirst().also {
                result.add(it)
                graph[it].forEach { neighbor ->
                    if (--inDegree[neighbor] == 0) queue.add(neighbor)
                }
            }
        }

        return if (result.size == numCourses) result.toIntArray() else intArrayOf()
    }
}
```

```java
import java.util.*;

public class CourseScheduleII {
    /**
     * @param numCourses    total number of courses
     * @param prerequisites pairs [course, preReq]: preReq must come before course
     * @return              any valid course order, or an empty array if a cycle exists
     */
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
        int[] inDegree = new int[numCourses];

        for (int[] pre : prerequisites) {
            graph.get(pre[1]).add(pre[0]);
            inDegree[pre[0]]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }

        int[] result = new int[numCourses];
        int written = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            result[written++] = course;
            for (int next : graph.get(course)) {
                if (--inDegree[next] == 0) queue.offer(next);
            }
        }

        return written == numCourses ? result : new int[0];
    }
}
```

```cpp
#include <queue>
#include <vector>

class CourseScheduleII {
public:
    /**
     * @param numCourses    total number of courses
     * @param prerequisites pairs [course, preReq]: preReq must come before course
     * @return              any valid course order, or an empty array if a cycle exists
     */
    std::vector<int> findOrder(int numCourses, std::vector<std::vector<int>>& prerequisites) {
        std::vector<std::vector<int>> graph(numCourses);
        std::vector<int> inDegree(numCourses, 0);

        for (auto& pre : prerequisites) {
            graph[pre[1]].push_back(pre[0]);
            inDegree[pre[0]]++;
        }

        std::queue<int> q;
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) q.push(i);
        }

        std::vector<int> result;
        while (!q.empty()) {
            int course = q.front();
            q.pop();
            result.push_back(course);
            for (int next : graph[course]) {
                if (--inDegree[next] == 0) q.push(next);
            }
        }

        return result.size() == (size_t)numCourses ? result : std::vector<int>{};
    }
};
```

```python
from collections import deque

def find_order(num_courses: int, prerequisites: list[list[int]]) -> list[int]:
    """
    @param num_courses:    total number of courses
    @param prerequisites:  pairs [course, pre_req]: pre_req must come before course
    @return:               any valid course order, or an empty array if a cycle exists
    """
    graph = [[] for _ in range(num_courses)]
    in_degree = [0] * num_courses

    for course, pre_req in prerequisites:
        graph[pre_req].append(course)
        in_degree[course] += 1

    queue = deque(i for i in range(num_courses) if in_degree[i] == 0)
    result = []

    while queue:
        course = queue.popleft()
        result.append(course)
        for next_course in graph[course]:
            in_degree[next_course] -= 1
            if in_degree[next_course] == 0:
                queue.append(next_course)

    return result if len(result) == num_courses else []
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param num_courses    total number of courses
    /// @param prerequisites  pairs [course, pre_req]: pre_req must come before course
    /// @return               any valid course order, or an empty array if a cycle exists
    pub fn find_order(num_courses: i32, prerequisites: Vec<Vec<i32>>) -> Vec<i32> {
        let n = num_courses as usize;
        let mut graph = vec![Vec::new(); n];
        let mut in_degree = vec![0i32; n];

        for pre in prerequisites {
            graph[pre[1] as usize].push(pre[0] as usize);
            in_degree[pre[0] as usize] += 1;
        }

        let mut queue: VecDeque<usize> = (0..n).filter(|&i| in_degree[i] == 0).collect();
        let mut result = Vec::new();

        while let Some(course) = queue.pop_front() {
            result.push(course as i32);
            for &next in &graph[course] {
                in_degree[next] -= 1;
                if in_degree[next] == 0 {
                    queue.push_back(next);
                }
            }
        }

        if result.len() == n { result } else { Vec::new() }
    }
}
```

## Dry run

**Input:** `numCourses = 4`, `prerequisites = [[1,0],[2,0],[3,1],[3,2]]`

```
graph: 0 -> [1,2], 1 -> [3], 2 -> [3], 3 -> []
inDegree: [0, 1, 1, 2]
queue: [0]

pop 0   -> result=[0]; unlock 1 (deg 1->0, enqueue), unlock 2 (deg 1->0, enqueue)
queue: [1,2]
pop 1   -> result=[0,1]; unlock 3 (deg 2->1, not ready)
queue: [2]
pop 2   -> result=[0,1,2]; unlock 3 (deg 1->0, enqueue)
queue: [3]
pop 3   -> result=[0,1,2,3]; no neighbors
queue: []

result.size == 4 == numCourses -> return [0,1,2,3] ✓   (valid: every prereq precedes its course)
```

With `prerequisites = [[0,1],[1,0]]`: both in-degrees are 1, the queue starts empty, `result` stays empty, `0 != 2` → return `[]`. The cycle detector is literally "did we run out of ready vertices?"

## Complexity

**Time.** Each vertex dequeued once; each edge examined once (when its source is popped):

$$
T(V, E) = O(V + E)
$$

**Space.** Graph, in-degree array, queue, result:

$$
S(V, E) = O(V + E)
$$

## Variants & follow-ups

- **Course Schedule I** (`src/main/kotlin/graph/`) — the same engine minus the output: just return whether `result.size == numCourses`.
- **Parallel Courses** (`src/main/kotlin/graph/dp/ParallelCourses_II.kt`) — topological order with *levels*: Kahn's queue processes one level at a time (the fence from [5.2](../ch05-trees/binary-tree-level-order-traversal.md)); minimum semesters = number of levels.
- **Alien Dictionary** — topological sort where the graph is *derived*: compare adjacent words to infer letter precedence edges, then Kahn's.
- **DFS variant** (`src/main/kotlin/graph/topological_sort/`) — 3-color marking with finish-time appending; same $O(V+E)$, different flavor. The repo ships both.
- **Interview follow-up:** "Why is the empty result the *only* failure signal?" A DAG with $V$ vertices always lets Kahn's process all $V$ (every vertex eventually has all prerequisites popped). Only a cycle — where every member has a prerequisite inside the cycle — strands vertices at in-degree ≥ 1 forever. So `count == V` ⟺ no cycle. One comparison, complete answer.
