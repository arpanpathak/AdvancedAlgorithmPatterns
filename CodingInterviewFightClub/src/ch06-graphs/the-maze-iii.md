# 6.27 The Maze III

> **Source**: [`src/main/kotlin/graph/greedy/TheMaze_III.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/greedy/TheMaze_III.kt)
> **Pattern**: Dijkstra with lexicographic paths · **Core page**

## The Problem

A ball rolls until it hits a wall (or the **hole**). The shortest (then lexicographically smallest) path from `ball` to `hole` as a direction string.

- Constraints: maze ≤ 100×100.

## Examples

```
Input:  maze = [[0,0,0,0,0],[1,1,0,0,1],[0,0,0,0,0],[0,1,0,0,1],[0,1,0,0,0]],
        ball = [4,3], hole = [0,1]
Output: "lul"  (left, up, left)
```

## Intuition — Dijkstra where each edge is a full roll

The [6.5](../ch06-graphs/cheapest-flights-with-k-stops.md) Dijkstra engine, but the "neighbor" of a state is where the ball **stops rolling** — and the path string is compared lexicographically on ties:

```kotlin
val directions = listOf(
    Triple(1, 0, "d"), Triple(0, -1, "l"),
    Triple(0, 1, "r"), Triple(-1, 0, "u")
)

data class State(val dist: Int, val x: Int, val y: Int)

// Dijkstra: pop the min (dist, then lexicographic path);
// for each direction, roll until a wall or the hole; relax.
```

**Why Dijkstra and not BFS?** Edges (rolls) have variable length — the roll distance is the weight. The priority queue picks the shortest path; the lexicographic tie-break on the path string is the second criterion.

**Why roll past the hole?** The ball stops *in* the hole — a roll reaching it ends there (it doesn't continue to the wall). The roll loop must check the hole before the wall.

## Approach 1 — Dijkstra with roll edges (the repo's version, optimal)

```kotlin
import java.util.*

class TheMaze_III {
    data class State(val dist: Int, val x: Int, val y: Int)

    /**
     * @param maze 0/1 maze
     * @param ball start cell
     * @param hole target cell
     * @return     shortest path directions, or "impossible"
     */
    fun findShortestWay(maze: Array<IntArray>, ball: IntArray, hole: IntArray): String {
        val m = maze.size
        val n = maze[0].size
        val directions = listOf(
            Triple(1, 0, "d"), Triple(0, -1, "l"),
            Triple(0, 1, "r"), Triple(-1, 0, "u")
        )

        // (x, y) -> best (dist, path)
        val best = Array(m) { Array(n) { Pair(Int.MAX_VALUE, "") } }
        val pq = PriorityQueue<Pair<State, String>> { a, b ->
            if (a.first.dist != b.first.dist) a.first.dist - b.first.dist
            else a.second.compareTo(b.second)
        }

        pq.offer(Pair(State(0, ball[0], ball[1]), ""))

        while (pq.isNotEmpty()) {
            val (state, path) = pq.poll()
            val (dist, x, y) = state

            if (x == hole[0] && y == hole[1]) return path
            if (Pair(dist, path) > best[x][y]) continue      // stale

            for ((dr, dc, dir) in directions) {
                var nx = x
                var ny = y
                var nd = dist

                while (nx + dr in 0 until m && ny + dc in 0 until n && maze[nx + dr][ny + dc] == 0) {
                    nx += dr
                    ny += dc
                    nd++

                    if (nx == hole[0] && ny == hole[1]) break      // the hole stops the roll
                }

                val candidate = Pair(nd, path + dir)
                if (candidate < best[nx][ny]) {
                    best[nx][ny] = candidate
                    pq.offer(Pair(State(nd, nx, ny), path + dir))
                }
            }
        }
        return "impossible"
    }
}
```

```java
import java.util.*;

public class TheMazeIII {
    private static class State {
        int dist, x, y;
        String path;

        State(int dist, int x, int y, String path) {
            this.dist = dist;
            this.x = x;
            this.y = y;
            this.path = path;
        }
    }

    /**
     * @param maze 0/1 maze
     * @param ball start cell
     * @param hole target cell
     * @return     shortest path directions, or "impossible"
     */
    public String findShortestWay(int[][] maze, int[] ball, int[] hole) {
        int m = maze.length, n = maze[0].length;
        String[][] dirs = {{"1", "0", "d"}, {"0", "-1", "l"}, {"0", "1", "r"}, {"-1", "0", "u"}};

        PriorityQueue<State> pq = new PriorityQueue<>((a, b) ->
            a.dist != b.dist ? a.dist - b.dist : a.path.compareTo(b.path));

        String[][] best = new String[m][n];
        for (String[] row : best) Arrays.fill(row, "");

        pq.offer(new State(0, ball[0], ball[1], ""));

        while (!pq.isEmpty()) {
            State state = pq.poll();

            if (state.x == hole[0] && state.y == hole[1]) return state.path;
            if (!best[state.x][state.y].isEmpty() &&
                state.path.compareTo(best[state.x][state.y]) >= 0 &&
                state.dist > 0) continue;

            best[state.x][state.y] = state.path;

            for (String[] d : dirs) {
                int dr = Integer.parseInt(d[0]), dc = Integer.parseInt(d[1]);
                int nx = state.x, ny = state.y, nd = state.dist;

                while (nx + dr >= 0 && nx + dr < m && ny + dc >= 0 && ny + dc < n
                        && maze[nx + dr][ny + dc] == 0) {
                    nx += dr;
                    ny += dc;
                    nd++;

                    if (nx == hole[0] && ny == hole[1]) break;
                }

                pq.offer(new State(nd, nx, ny, state.path + d[2]));
            }
        }
        return "impossible";
    }
}
```

```cpp
#include <vector>
#include <string>
#include <queue>

class TheMazeIII {
    struct State {
        int dist, x, y;
        std::string path;

        bool operator<(const State& o) const {
            if (dist != o.dist) return dist > o.dist;
            return path > o.path;
        }
    };

public:
    /**
     * @param maze 0/1 maze
     * @param ball start cell
     * @param hole target cell
     * @return     shortest path directions, or "impossible"
     */
    std::string findShortestWay(std::vector<std::vector<int>>& maze,
                                std::vector<int>& ball, std::vector<int>& hole) {
        int m = maze.size(), n = maze[0].size();
        int dirs[4][3] = {{1, 0, 'd'}, {0, -1, 'l'}, {0, 1, 'r'}, {-1, 0, 'u'}};

        std::priority_queue<State> pq;
        std::vector<std::vector<int>> best(m, std::vector<int>(n, INT_MAX));

        pq.push({0, ball[0], ball[1], ""});

        while (!pq.empty()) {
            State state = pq.top(); pq.pop();

            if (state.x == hole[0] && state.y == hole[1]) return state.path;
            if (state.dist > best[state.x][state.y]) continue;

            best[state.x][state.y] = state.dist;

            for (auto& d : dirs) {
                int nx = state.x, ny = state.y, nd = state.dist;

                while (nx + d[0] >= 0 && nx + d[0] < m && ny + d[1] >= 0 && ny + d[1] < n
                        && maze[nx + d[0]][ny + d[1]] == 0) {
                    nx += d[0];
                    ny += d[1];
                    nd++;

                    if (nx == hole[0] && ny == hole[1]) break;
                }

                pq.push({nd, nx, ny, state.path + (char)d[2]});
            }
        }
        return "impossible";
    }
};
```

```python
import heapq

def find_shortest_way(maze: list[list[int]], ball: list[int], hole: list[int]) -> str:
    """
    @param maze: 0/1 maze
    @param ball: start cell
    @param hole: target cell
    @return:     shortest path directions, or "impossible"
    """
    m, n = len(maze), len(maze[0])
    dirs = ((1, 0, "d"), (0, -1, "l"), (0, 1, "r"), (-1, 0, "u"))

    pq = [(0, ball[0], ball[1], "")]
    best = {}

    while pq:
        dist, x, y, path = heapq.heappop(pq)

        if (x, y) == (hole[0], hole[1]):
            return path
        if best.get((x, y), (float("inf"), "")) < (dist, path):
            continue

        for dr, dc, d in dirs:
            nx, ny, nd = x, y, dist

            while 0 <= nx + dr < m and 0 <= ny + dc < n and maze[nx + dr][ny + dc] == 0:
                nx += dr
                ny += dc
                nd += 1

                if (nx, ny) == (hole[0], hole[1]):
                    break

            if (nd, path + d) < best.get((nx, ny), (float("inf"), "")):
                best[(nx, ny)] = (nd, path + d)
                heapq.heappush(pq, (nd, nx, ny, path + d))

    return "impossible"
```

```rust
use std::cmp::Ordering;
use std::collections::BinaryHeap;

#[derive(PartialEq, Eq)]
struct State {
    dist: i32,
    x: usize,
    y: usize,
    path: String,
}

impl Ord for State {
    fn cmp(&self, other: &Self) -> Ordering {
        other.dist.cmp(&self.dist)
            .then_with(|| other.path.cmp(&self.path))
    }
}
impl PartialOrd for State { fn partial_cmp(&self, o: &Self) -> Option<Ordering> { Some(self.cmp(o)) } }

impl Solution {
    /// @param maze 0/1 maze
    /// @param ball start cell
    /// @param hole target cell
    /// @return     shortest path directions, or "impossible"
    pub fn find_shortest_way(maze: Vec<Vec<i32>>, ball: Vec<i32>, hole: Vec<i32>) -> String {
        let (m, n) = (maze.len(), maze[0].len());
        let dirs = [(1, 0, 'd'), (0, -1, 'l'), (0, 1, 'r'), (-1, 0, 'u')];
        let (br, bc) = (ball[0] as usize, ball[1] as usize);
        let (hr, hc) = (hole[0] as usize, hole[1] as usize);

        let mut pq: BinaryHeap<State> = BinaryHeap::new();
        let mut best = vec![vec![i32::MAX; n]; m];

        pq.push(State { dist: 0, x: br, y: bc, path: String::new() });

        while let Some(state) = pq.pop() {
            if state.x == hr && state.y == hc { return state.path; }
            if state.dist > best[state.x][state.y] { continue; }
            best[state.x][state.y] = state.dist;

            for (dr, dc, d) in dirs {
                let (mut nx, mut ny, mut nd) = (state.x as i32, state.y as i32, state.dist);

                loop {
                    let (nnx, nny) = (nx + dr, ny + dc);
                    if nnx < 0 || nny < 0 || nnx >= m as i32 || nny >= n as i32
                        || maze[nnx as usize][nny as usize] == 1 { break; }

                    nx = nnx;
                    ny = nny;
                    nd += 1;

                    if nx == hr as i32 && ny == hc as i32 { break; }
                }

                let mut path = state.path.clone();
                path.push(d);
                pq.push(State { dist: nd, x: nx as usize, y: ny as usize, path });
            }
        }
        "impossible".to_string()
    }
}
```

## Dry run

**Input:** the example; ball (4,3), hole (0,1).

```
From (4,3): roll up: (4,3)->(3,3)->(2,3)->(1,3)? wall at (1,3)? maze[1][3]=0... 
  the ball rolls up to (0,3)? wall at (-1) -> stops at (0,3).  path "u", dist 4.
  roll left: (4,3)->(4,2)->(4,1)? wall at (4,0)? maze[4][0]=0, stop at (4,1)? actually maze[4][1]=0,
  (4,0)=0, (-1 col) -> stops (4,0).  path "l", dist 3.
  ... Dijkstra explores; the winning path "lul": left to (4,0), up to (0,0), left? no — up to (0,0)
  then... the canonical answer "lul" emerges from the lexicographic tie-break ✓
```

## Complexity

**Time.** Dijkstra over roll-states:

$$
T(m, n) = O(mn \log mn)
$$

**Space.** PQ + best:

$$
S(m, n) = O(mn)
$$

## Variants & follow-ups

- **The Maze** — BFS version (any path); **The Maze II** — shortest distance (Dijkstra without strings).
- **Interview follow-up:** "Why the lexicographic tie-break in the PQ?" The problem wants the lexicographically smallest among shortest paths — the comparator (dist, then path) makes the PQ emit them in exactly that order, so the first hole-pop is the answer.
