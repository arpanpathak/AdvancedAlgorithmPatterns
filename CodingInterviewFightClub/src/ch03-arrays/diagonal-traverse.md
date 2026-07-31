# 3.22 Diagonal Traverse

> **Source:** [`src/main/kotlin/array/DiagonalTraverse.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/DiagonalTraverse.kt)
> **Pattern:** direction-flipping walk · **Core page**

## The Problem

Return the matrix's elements in zigzag diagonal order (up-right, then down-left, alternating).

- Constraints: m, n ≤ 10⁴ cells.

## Examples

```
Input:  mat = [[1,2,3],[4,5,6],[7,8,9]]
Output: [1,2,4,7,5,3,6,8,9]
```

## Intuition — a walker with a direction that flips at the edges

Start at (0,0), direction UP-RIGHT. Each step: emit the cell; move `(dir_r, dir_c)`; when the next step exits, adjust — the classic boundary handling:

```
UP direction (dr=-1, dc=1):
  if r < 0 (left the top): r = 0; flip to DOWN
  else if c == n: r += 2; c = n - 1; flip to DOWN     (exit right, must step down two... )
DOWN direction (dr=1, dc=-1):
  if c < 0: c = 0; flip to UP
  else if r == m: c += 2; r = m - 1; flip to UP
```

**Why the `+2` corrections?** A failed move has *already* overshot by one row/col — the correction must land the walker inside, then continue in the new direction. The repo's enum (`UP`/`DOWN`) makes the flip explicit.

**Why flip only at boundaries?** Inside the matrix the diagonal continues; only exiting the grid changes direction. The boundary checks are the whole logic — one index arithmetic slip breaks the zigzag.

## Approach 1 — Index-sum buckets (O(mn) extra)

Group cells by `r + c` (the diagonal id), alternate reverses: correct, needs a bucket structure.

## Approach 2 — Direction walker (the repo's version, optimal)

```kotlin
class DiagonalTraverse {
    enum class Direction { UP, DOWN }

    /**
     * @param mat input matrix
     * @return    zigzag diagonal order
     */
    fun findDiagonalOrder(mat: Array<IntArray>): IntArray {
        val m = mat.size
        val n = mat[0].size
        val result = mutableListOf<Int>()

        var i = 0
        var j = 0
        var direction = Direction.UP

        while (result.size < m * n) {
            result.add(mat[i][j])

            when (direction) {
                Direction.UP -> {
                    if (j == n - 1) { i++; direction = Direction.DOWN }        // exit right
                    else if (i == 0) { j++; direction = Direction.DOWN }       // exit top
                    else { i--; j++ }
                }
                Direction.DOWN -> {
                    if (i == m - 1) { j++; direction = Direction.UP }          // exit bottom
                    else if (j == 0) { i++; direction = Direction.UP }         // exit left
                    else { i++; j-- }
                }
            }
        }
        return result.toIntArray()
    }
}
```

```java
public class DiagonalTraverse {
    /**
     * @param mat input matrix
     * @return    zigzag diagonal order
     */
    public int[] findDiagonalOrder(int[][] mat) {
        int m = mat.length, n = mat[0].length;
        int[] result = new int[m * n];
        int idx = 0, r = 0, c = 0, dir = 1;              // 1 = up, -1 = down

        while (idx < m * n) {
            result[idx++] = mat[r][c];

            if (dir == 1) {
                if (c == n - 1) { r++; dir = -1; }
                else if (r == 0) { c++; dir = -1; }
                else { r--; c++; }
            } else {
                if (r == m - 1) { c++; dir = 1; }
                else if (c == 0) { r++; dir = 1; }
                else { r++; c--; }
            }
        }
        return result;
    }
}
```

```cpp
#include <vector>

class DiagonalTraverse {
public:
    /**
     * @param mat input matrix
     * @return    zigzag diagonal order
     */
    std::vector<int> findDiagonalOrder(std::vector<std::vector<int>>& mat) {
        int m = mat.size(), n = mat[0].size();
        std::vector<int> result(m * n);
        int idx = 0, r = 0, c = 0, dir = 1;              // 1 = up, -1 = down

        while (idx < m * n) {
            result[idx++] = mat[r][c];

            if (dir == 1) {
                if (c == n - 1) { r++; dir = -1; }
                else if (r == 0) { c++; dir = -1; }
                else { r--; c++; }
            } else {
                if (r == m - 1) { c++; dir = 1; }
                else if (c == 0) { r++; dir = 1; }
                else { r++; c--; }
            }
        }
        return result;
    }
};
```

```python
def find_diagonal_order(mat: list[list[int]]) -> list[int]:
    """
    @param mat: input matrix
    @return:    zigzag diagonal order
    """
    m, n = len(mat), len(mat[0])
    result = []
    r = c = 0
    up = True

    while len(result) < m * n:
        result.append(mat[r][c])

        if up:
            if c == n - 1:
                r += 1
                up = False
            elif r == 0:
                c += 1
                up = False
            else:
                r -= 1
                c += 1
        else:
            if r == m - 1:
                c += 1
                up = True
            elif c == 0:
                r += 1
                up = True
            else:
                r += 1
                c -= 1

    return result
```

```rust
impl Solution {
    /// @param mat input matrix
    /// @return    zigzag diagonal order
    pub fn find_diagonal_order(mat: Vec<Vec<i32>>) -> Vec<i32> {
        let (m, n) = (mat.len() as i32, mat[0].len() as i32);
        let mut result = Vec::with_capacity((m * n) as usize);
        let (mut r, mut c) = (0i32, 0i32);
        let mut up = true;

        while result.len() < (m * n) as usize {
            result.push(mat[r as usize][c as usize]);

            if up {
                if c == n - 1 { r += 1; up = false; }
                else if r == 0 { c += 1; up = false; }
                else { r -= 1; c += 1; }
            } else {
                if r == m - 1 { c += 1; up = true; }
                else if c == 0 { r += 1; up = true; }
                else { r += 1; c -= 1; }
            }
        }
        result
    }
}
```

## Dry run

**Input:** `mat = [[1,2,3],[4,5,6],[7,8,9]]`.

```
(0,0)=1 UP: not edge -> (0,1)... wait: i==0 -> j++ DOWN: (0,1)=2.
  DOWN from (0,1): j!=0? j=1: not i==m-1, not j==0 -> i++, j--: (1,0)=4.
  DOWN from (1,0): j==0 -> i++, UP: (2,0)=7.
  UP from (2,0): i!=0, j!=n-1 -> i--, j++: (1,1)=5.
  UP from (1,1): -> (0,2)=3.
  UP from (0,2): i==0 -> j++, DOWN: (1,2)=6.
  DOWN from (1,2): i==m-1 -> j++, UP: (2,1)=8.
  UP from (2,1): j!=n-1, i!=0 -> i--, j++: (2,2)? no: i-- = 1, j++ = 2 -> (1,2) visited... 

recheck: from (2,1) UP: i>0 and j<n-1 -> i--, j++ -> (1,2) — already visited!  The loop ends only
when result.size == 9; the (1,2) re-emit would be wrong... but the direction logic at (2,1):

Actually trace correctly: (2,1)=8 is emitted.  UP: i=2 > 0, j=1 < 2 -> i--, j++ -> (1,2).
(1,2)=6 already emitted -> the walker re-emits?  NO — let me re-read: after emitting (1,2)=6 earlier,
we flipped to DOWN at (0,2) because i==0.  The correct final steps:
  (1,2)=6 emitted, then from (1,2) DOWN: i==m-1 -> j++, UP -> (2,2)=9.

The walker path: (0,0) (0,1) (1,0) (2,0) (1,1) (0,2) (1,2) (2,1) (2,2) — all 9, correct ✓
```

Each emitted cell is exactly one diagonal position; the flip conditions fire only at the four exit cases. The output `[1,2,4,7,5,3,6,8,9]` matches the zigzag — up-diagonals and down-diagonals alternate by construction.

## Complexity

**Time.** One pass:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** The result:

$$
S(m, n) = O(m \cdot n)
$$

## Variants & follow-ups

- **Diagonal Traverse II** (`array/DiagonalTraverse_II.kt`) — ragged input: bucket by `r+c` instead of walking.
- **Spiral Matrix** ([3.x](../ch03-arrays/pattern-primer.md)) — the same direction-flip walker on a spiral.
- **Interview follow-up:** "Why are the four exit conditions ordered?" The corner cells (e.g. top-right) satisfy *two* edge conditions — the order decides which wins. Checking the column exit before the row exit makes the walker hug the correct edge; the order is the difference between a correct zigzag and an infinite loop.
