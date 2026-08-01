# 6.34 Longest Increasing Path In A Matrix

> **Source**: [`src/main/kotlin/array/dp/LongestIncreasingSequenceInAMatrix.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/LongestIncreasingSequenceInAMatrix.kt)
> **Pattern**: memoized DFS on a grid · **Core page**

## The Problem

The longest strictly-increasing path on a grid (4-directional).

- Constraints: m, n ≤ 200.

## Examples

```
Input:  matrix = [[9,9,4],[6,6,8],[2,1,1]]   -> Output: 4  (1,2,6,9)
```

## Intuition — DFS with a cache; a cell's longest path extends its smaller neighbors

```kotlin
fun dfs(i: Int, j: Int): Int {
    if (cache[i][j] != 0) return cache[i][j]

    var best = 1
    for ((di, dj) in dirs) {
        val ni = i + di
        val nj = j + dj

        if (ni in 0 until m && nj in 0 until n && matrix[ni][nj] > matrix[i][j]) {
            best = maxOf(best, 1 + dfs(ni, nj))
        }
    }
    cache[i][j] = best
    return best
}
return (0 until m).maxOf { i -> (0 until n).maxOf { j -> dfs(i, j) } }
```

## Approach 1 — Memoized DFS (the repo's version, optimal)

```kotlin
class LongestIncreasingSequenceInAMatrix {
    /**
     * @param matrix grid
     * @return       longest increasing path
     */
    fun longestIncreasingPath(matrix: Array<IntArray>): Int {
        val (m, n) = matrix.size to matrix[0].size
        val cache = Array(m) { IntArray(n) }
        val dirs = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)

        fun dfs(i: Int, j: Int): Int {
            if (cache[i][j] != 0) return cache[i][j]

            var best = 1
            for ((di, dj) in dirs) {
                val ni = i + di
                val nj = j + dj

                if (ni in 0 until m && nj in 0 until n && matrix[ni][nj] > matrix[i][j]) {
                    best = maxOf(best, 1 + dfs(ni, nj))
                }
            }
            cache[i][j] = best
            return best
        }

        var answer = 0
        for (i in 0 until m) {
            for (j in 0 until n) {
                answer = maxOf(answer, dfs(i, j))
            }
        }
        return answer
    }
}
```

```java
public class LongestIncreasingPathInAMatrix {
    private int[][] matrix, cache;
    private int m, n;
    private int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private int dfs(int i, int j) {
        if (cache[i][j] != 0) return cache[i][j];

        int best = 1;
        for (int[] d : dirs) {
            int ni = i + d[0], nj = j + d[1];

            if (ni >= 0 && nj >= 0 && ni < m && nj < n && matrix[ni][nj] > matrix[i][j]) {
                best = Math.max(best, 1 + dfs(ni, nj));
            }
        }
        return cache[i][j] = best;
    }

    /**
     * @param matrix grid
     * @return       longest increasing path
     */
    public int longestIncreasingPath(int[][] matrix) {
        this.matrix = matrix;
        m = matrix.length;
        n = matrix[0].length;
        cache = new int[m][n];

        int best = 0;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                best = Math.max(best, dfs(i, j));
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class LongestIncreasingPathInAMatrix {
    int dfs(int i, int j, std::vector<std::vector<int>>& matrix,
            std::vector<std::vector<int>>& cache) {
        if (cache[i][j]) return cache[i][j];

        int dirs[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int best = 1;

        for (auto& d : dirs) {
            int ni = i + d[0], nj = j + d[1];

            if (ni >= 0 && nj >= 0 && ni < (int)matrix.size() && nj < (int)matrix[0].size()
                    && matrix[ni][nj] > matrix[i][j]) {
                best = std::max(best, 1 + dfs(ni, nj, matrix, cache));
            }
        }
        return cache[i][j] = best;
    }

public:
    /**
     * @param matrix grid
     * @return       longest increasing path
     */
    int longestIncreasingPath(std::vector<std::vector<int>>& matrix) {
        int m = matrix.size(), n = matrix[0].size();
        std::vector<std::vector<int>> cache(m, std::vector<int>(n, 0));
        int best = 0;

        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                best = std::max(best, dfs(i, j, matrix, cache));
        return best;
    }
};
```

```python
def longest_increasing_path(matrix: list[list[int]]) -> int:
    """
    @param matrix: grid
    @return:       longest increasing path
    """
    m, n = len(matrix), len(matrix[0])
    cache = [[0] * n for _ in range(m)]
    dirs = ((1, 0), (-1, 0), (0, 1), (0, -1))

    def dfs(i: int, j: int) -> int:
        if cache[i][j]:
            return cache[i][j]

        best = 1
        for di, dj in dirs:
            ni, nj = i + di, j + dj

            if 0 <= ni < m and 0 <= nj < n and matrix[ni][nj] > matrix[i][j]:
                best = max(best, 1 + dfs(ni, nj))

        cache[i][j] = best
        return best

    return max(dfs(i, j) for i in range(m) for j in range(n))
```

```rust
impl Solution {
    /// @param matrix grid
    /// @return       longest increasing path
    pub fn longest_increasing_path(matrix: Vec<Vec<i32>>) -> i32 {
        let (m, n) = (matrix.len(), matrix[0].len());
        let mut cache = vec![vec![0; n]; m];
        let dirs = [(1, 0), (-1, 0), (0, 1), (0, -1)];

        fn dfs(i: usize, j: usize, matrix: &Vec<Vec<i32>>, cache: &mut Vec<Vec<i32>>,
               m: usize, n: usize, dirs: &[(i32, i32)]) -> i32 {
            if cache[i][j] != 0 { return cache[i][j]; }

            let mut best = 1;
            for (di, dj) in dirs {
                let ni = i as i32 + di;
                let nj = j as i32 + dj;

                if ni >= 0 && nj >= 0 && (ni as usize) < m && (nj as usize) < n
                    && matrix[ni as usize][nj as usize] > matrix[i][j] {
                    best = best.max(1 + dfs(ni as usize, nj as usize, matrix, cache, m, n, dirs));
                }
            }
            cache[i][j] = best;
            best
        }

        let mut best = 0;
        for i in 0..m {
            for j in 0..n {
                best = best.max(dfs(i, j, &matrix, &mut cache, m, n, &dirs));
            }
        }
        best
    }
}
```

## Dry run

**Input:** the example.

```
dfs(2,0)=1 (1).  dfs(2,1): neighbors (2,0)=1? 1<1 no.  (1,1)=1 no.  -> 1.
dfs(2,2)=1.  dfs(1,2)=8: (0,2)=4 < 8? no... 4<8: 1+dfs(0,2).  dfs(0,2)=4: (0,1)=9? 9>4: 1+dfs(0,1).
  dfs(0,1)=9: neighbors 9>9 no, 6? no -> 1.  so dfs(0,2) = 2.  dfs(1,2) = 1+2 = 3? wait 8's
  neighbors: (0,2)=4 -> 1+dfs(0,2)=3.  (2,2)=1 no.  (1,1)=6 no.  -> 3.
dfs(1,0)=6: (0,0)=9: 1+dfs(0,0)=2.  dfs(0,0)=9: 1.  -> 2.  (2,0)=1? 1<6 no.
dfs(0,1)=9: 1.  dfs(0,2): 4 -> (0,1)? 9>4 no... (1,2)=8 > 4: 1+dfs(1,2)=4.
  so dfs(0,2) = 4!  (path 4,8 = length 2?  no: dfs(0,2): best = 1 + dfs(1,2) = 4.
  dfs(1,2) = 8: neighbors (0,2)=4 (1+dfs(0,2)) — cycle risk?  dfs(0,2) requires dfs(1,2)...
  But 4 < 8, so dfs(0,2) = 1 + dfs(1,2) and dfs(1,2) = 1 + dfs(0,2)??  NO — dfs(1,2)=8 checks
  neighbors > 8: (0,2)=4 not > 8.  So dfs(1,2) = max over neighbors bigger than 8 — none -> 1.
  Then dfs(0,2) = 4: neighbor (1,2)=8 > 4 -> 1 + dfs(1,2) = 2.  OK.
  (1,2) = 1.  (0,2) = 2.  (0,1) = 1.  (1,0) = 2 (9).  (0,0) = 1.
  (1,1)=6: (0,1)=9 -> 1+1 = 2.  (0,0)=9 -> 2.  (1,2)=8 -> 2.  (2,1)=1 no.  -> 2.
  (2,0)=1: (1,0)=6 -> 1+dfs(1,0)=3!  dfs(1,0): (0,0)=9 -> 1+1 = 2.  so (2,0) = 1+2 = 3.
  (2,1)=1: (1,1)=6 -> 1+2 = 3.  (2,2)=1: (1,2)=8 -> 1+1 = 2.
  best = 3?  Expected 4 (1,2,6,9: (2,0)=1 -> (1,0)=6 -> (0,0)=9 = 3; (2,1)=1 -> (1,1)=6 -> (0,1)=9 = 3;
  (2,2)=1 -> (1,2)=8 = 2... the longest: (2,0) 1 -> (1,0) 6 -> (0,0) 9 = 3?  But expected 4!
  Path 1,2,6,9: (2,1)=1? no — (2,0)=1 -> (1,0)=6 -> (0,0)=9: 3.  Hmm the known answer for
  [[9,9,4],[6,6,8],[2,1,1]] is 4: 1->2? there's no 2.  Actually path: (2,1)=1 -> (1,1)=6 -> (0,1)=9: 3.
  Wait the known answer IS 4: (2,1)=1, (2,2)=1? no.  path (2,1) 1 -> (1,1) 6 -> (0,1) 9 = 3.
  Let me recompute: matrix = [[9,9,4],[6,6,8],[2,1,1]].  Longest: (2,0)=2 -> (1,0)=6 -> (0,0)=9 = 3.
  Hmm (2,1)=1 -> (1,1)=6 -> (0,1)=9 = 3.  (1,2)=8 -> (0,2)=4? 4<8 no.  8 has no bigger neighbor -> 1.
  Actually the official answer is 4: path (2,1) 1 → (1,1) 6 → (0,1) 9? that's 3.  OH — (2,0)=2?
  The example is [[9,9,4],[6,6,8],[2,1,1]] with answer 4 — path: (2,0)=2? no it's 2? (2,0)=2!
  Wait the matrix row 3 is [2,1,1] — so (2,0)=2.  Path: (2,0)=2 -> (1,0)=6 -> (0,0)=9 = 3? 
  (2,0)=2 -> (2,1)=1 no.  Hmm... official: 1→2→6→9: (2,1)=1 -> (2,0)=2 -> (1,0)=6 -> (0,0)=9 = 4 ✓
  My earlier trace used (2,0)=1 — it's actually 2!  With (2,0)=2: dfs(2,0) = 1+dfs(1,0) = 1+2 = 3;
  dfs(2,1)=1: neighbors (2,0)=2 > 1 -> 1+3 = 4 ✓
Output: 4 ✓
```

## Complexity

**Time.** Each cell once:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** Cache + recursion:

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why is memoization safe (no cycles)?" Strictly-increasing steps can't loop — every move increases the value, so the DFS is a DAG and the cache is exact.
