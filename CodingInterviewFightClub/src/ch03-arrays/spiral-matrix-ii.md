# 3.34 Spiral Matrix II

> **Source**: [`src/main/kotlin/array/SpiralMatrix_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/SpiralMatrix_II.kt)
> **Pattern**: boundary-filling walk · **Core page**

## The Problem

Generate the n×n matrix filled 1..n² in spiral order.

- Constraints: n ≤ 20.

## Examples

```
Input:  n = 3   -> Output: [[1,2,3],[8,9,4],[7,6,5]]
```

## Intuition — the [3.x](../ch03-arrays/pattern-primer.md) spiral walk, writing numbers

The inverse of Spiral Matrix I: four boundaries shrink as each side fills:

```kotlin
var (top, bottom, left, right, num) = arrayOf(0, n - 1, 0, n - 1, 1)

while (top <= bottom && left <= right) {
    for (i in left..right) matrix[top][i] = num++    // →
    top++
    for (i in top..bottom) matrix[i][right] = num++  // ↓
    right--
    for (i in right downTo left) matrix[bottom][i] = num++  // ←
    bottom--
    for (i in bottom downTo top) matrix[i][left] = num++    // ↑
    left++
}
return matrix
```

## Approach 1 — Boundary walk (the repo's version, optimal)

```kotlin
class SpiralMatrix_II {
    /**
     * @param n matrix size
     * @return  spiral-filled n x n matrix
     */
    fun generateMatrix(n: Int): Array<IntArray> {
        val matrix = Array(n) { IntArray(n) }
        var (top, bottom, left, right, num) = arrayOf(0, n - 1, 0, n - 1, 1)

        while (top <= bottom && left <= right) {
            for (i in left..right) matrix[top][i] = num++
            top++

            for (i in top..bottom) matrix[i][right] = num++
            right--

            for (i in right downTo left) matrix[bottom][i] = num++
            bottom--

            for (i in bottom downTo top) matrix[i][left] = num++
            left++
        }
        return matrix
    }
}
```

```java
public class SpiralMatrixII {
    /**
     * @param n matrix size
     * @return  spiral-filled n x n matrix
     */
    public int[][] generateMatrix(int n) {
        int[][] matrix = new int[n][n];
        int top = 0, bottom = n - 1, left = 0, right = n - 1, num = 1;

        while (top <= bottom && left <= right) {
            for (int i = left; i <= right; i++) matrix[top][i] = num++;
            top++;
            for (int i = top; i <= bottom; i++) matrix[i][right] = num++;
            right--;
            for (int i = right; i >= left; i--) matrix[bottom][i] = num++;
            bottom--;
            for (int i = bottom; i >= top; i--) matrix[i][left] = num++;
            left++;
        }
        return matrix;
    }
}
```

```cpp
#include <vector>

class SpiralMatrixII {
public:
    /**
     * @param n matrix size
     * @return  spiral-filled n x n matrix
     */
    std::vector<std::vector<int>> generateMatrix(int n) {
        std::vector<std::vector<int>> matrix(n, std::vector<int>(n));
        int top = 0, bottom = n - 1, left = 0, right = n - 1, num = 1;

        while (top <= bottom && left <= right) {
            for (int i = left; i <= right; i++) matrix[top][i] = num++;
            top++;
            for (int i = top; i <= bottom; i++) matrix[i][right] = num++;
            right--;
            for (int i = right; i >= left; i--) matrix[bottom][i] = num++;
            bottom--;
            for (int i = bottom; i >= top; i--) matrix[i][left] = num++;
            left++;
        }
        return matrix;
    }
};
```

```python
def generate_matrix(n: int) -> list[list[int]]:
    """
    @param n: matrix size
    @return:  spiral-filled n x n matrix
    """
    matrix = [[0] * n for _ in range(n)]
    top, bottom, left, right = 0, n - 1, 0, n - 1
    num = 1

    while top <= bottom and left <= right:
        for i in range(left, right + 1):
            matrix[top][i] = num
            num += 1
        top += 1

        for i in range(top, bottom + 1):
            matrix[i][right] = num
            num += 1
        right -= 1

        for i in range(right, left - 1, -1):
            matrix[bottom][i] = num
            num += 1
        bottom -= 1

        for i in range(bottom, top - 1, -1):
            matrix[i][left] = num
            num += 1
        left += 1

    return matrix
```

```rust
impl Solution {
    /// @param n matrix size
    /// @return  spiral-filled n x n matrix
    pub fn generate_matrix(n: i32) -> Vec<Vec<i32>> {
        let n = n as usize;
        let mut matrix = vec![vec![0; n]; n];
        let (mut top, mut bottom, mut left, mut right) = (0usize, n - 1, 0usize, n - 1);
        let mut num = 1;

        while top <= bottom && left <= right {
            for i in left..=right { matrix[top][i] = num; num += 1; }
            top += 1;
            for i in top..=bottom { matrix[i][right] = num; num += 1; }
            right = right.wrapping_sub(1);
            for i in (left..=right).rev() { matrix[bottom][i] = num; num += 1; }
            bottom = bottom.wrapping_sub(1);
            for i in (top..=bottom).rev() { matrix[i][left] = num; num += 1; }
            left += 1;
        }
        matrix
    }
}
```

## Dry run

**Input:** `n = 3`.

```
top=0,bottom=2,left=0,right=2, num=1
→: (0,0)=1,(0,1)=2,(0,2)=3.  top=1.
↓: (1,2)=4,(2,2)=5.  right=1.
←: (2,1)=6,(2,0)=7.  bottom=1.
↑: (1,0)=8.  left=1.
→: (1,1)=9.  top=2.  (top > bottom -> stop)

Output: [[1,2,3],[8,9,4],[7,6,5]] ✓
```

## Complexity

**Time.** n² cells:

$$
T(n) = O(n^2)
$$

**Space.** The matrix:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Spiral Matrix** — the read direction of this write.
- **Interview follow-up:** "Why does the while-condition guard each pass?" After the last row fill, the boundaries shrink — the next three loops could write out of bounds if `top > bottom`. The condition terminates exactly when the center is consumed.
