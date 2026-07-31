# 8.21 Maximal Rectangle

> **Source**: [`src/main/kotlin/grid/histogram/MaximalRectangle.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/grid/histogram/MaximalRectangle.kt)
> **Pattern**: histogram stack per row · **Core page**

## The Problem

The largest all-1s rectangle in a binary matrix.

- Constraints: m, n ≤ 200.

## Examples

```
Input:  [["1","0","1","0","0"],["1","0","1","1","1"],["1","1","1","1","1"],["1","0","0","1","0"]]
Output: 6
```

## Intuition — build a histogram per row; run [8.5](largest-rectangle-in-histogram.md)

`heights[j]` = consecutive 1s ending at this row in column j. Each row's histogram feeds the [8.5](largest-rectangle-in-histogram.md) stack algorithm; the max over rows is the answer:

```kotlin
val heights = IntArray(cols)

for (r in 0 until rows) {
    for (c in 0 until cols) {
        heights[c] = if (matrix[r][c] == '1') heights[c] + 1 else 0
    }
    maxArea = maxOf(maxArea, largestRectangleArea(heights))
}
```

**Why the histogram?** A rectangle's bottom row defines its base; the consecutive-1 heights above encode every possible rectangle ending at that row. Each row's stack pass finds the max for that bottom — [8.5](largest-rectangle-in-histogram.md) as a subroutine.

## Approach 1 — Brute force all rectangles (O(m²n²))

Expand every (top, bottom, left, right): correct, slow.

## Approach 2 — Row histograms + stack (the repo's version, optimal)

```kotlin
class MaximalRectangle {
    /**
     * @param matrix binary matrix
     * @return       max all-1s rectangle area
     */
    fun maximalRectangle(matrix: Array<CharArray>): Int {
        if (matrix.isEmpty()) return 0

        val rows = matrix.size
        val cols = matrix[0].size
        val heights = IntArray(cols)
        var maxArea = 0

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                heights[c] = if (matrix[r][c] == '1') heights[c] + 1 else 0
            }
            maxArea = maxOf(maxArea, largestRectangleArea(heights))
        }
        return maxArea
    }

    private fun largestRectangleArea(heights: IntArray): Int {
        val stack = ArrayDeque<Int>()
        var maxArea = 0

        for (i in 0..heights.size) {
            val currentHeight = if (i == heights.size) 0 else heights[i]

            while (stack.isNotEmpty() && heights[stack.last()] > currentHeight) {
                val height = heights[stack.removeLast()]
                val width = if (stack.isEmpty()) i else i - stack.last() - 1
                maxArea = maxOf(maxArea, height * width)
            }
            stack.add(i)
        }
        return maxArea
    }
}
```

```java
import java.util.*;

public class MaximalRectangle {
    private int largestRectangleArea(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>();
        int best = 0;

        for (int i = 0; i <= heights.length; i++) {
            int h = i == heights.length ? 0 : heights[i];

            while (!stack.isEmpty() && heights[stack.peek()] > h) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                best = Math.max(best, height * width);
            }
            stack.push(i);
        }
        return best;
    }

    /**
     * @param matrix binary matrix
     * @return       max all-1s rectangle area
     */
    public int maximalRectangle(char[][] matrix) {
        if (matrix.length == 0) return 0;

        int cols = matrix[0].length;
        int[] heights = new int[cols];
        int best = 0;

        for (char[] row : matrix) {
            for (int c = 0; c < cols; c++) {
                heights[c] = row[c] == '1' ? heights[c] + 1 : 0;
            }
            best = Math.max(best, largestRectangleArea(heights));
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <stack>

class MaximalRectangle {
    int largestRectangleArea(std::vector<int>& heights) {
        std::stack<int> stack;
        int best = 0;

        for (int i = 0; i <= (int)heights.size(); i++) {
            int h = i == (int)heights.size() ? 0 : heights[i];

            while (!stack.empty() && heights[stack.top()] > h) {
                int height = heights[stack.top()]; stack.pop();
                int width = stack.empty() ? i : i - stack.top() - 1;
                best = std::max(best, height * width);
            }
            stack.push(i);
        }
        return best;
    }

public:
    /**
     * @param matrix binary matrix
     * @return       max all-1s rectangle area
     */
    int maximalRectangle(std::vector<std::vector<char>>& matrix) {
        if (matrix.empty()) return 0;

        int cols = matrix[0].size();
        std::vector<int> heights(cols, 0);
        int best = 0;

        for (auto& row : matrix) {
            for (int c = 0; c < cols; c++) {
                heights[c] = row[c] == '1' ? heights[c] + 1 : 0;
            }
            best = std::max(best, largestRectangleArea(heights));
        }
        return best;
    }
};
```

```python
def maximal_rectangle(matrix: list[list[str]]) -> int:
    """
    @param matrix: binary matrix
    @return:       max all-1s rectangle area
    """
    def largest_rectangle_area(heights):
        stack = []
        best = 0

        for i, h in enumerate(heights + [0]):
            while stack and heights[stack[-1]] > h:
                height = heights[stack.pop()]
                width = i if not stack else i - stack[-1] - 1
                best = max(best, height * width)
            stack.append(i)

        return best

    if not matrix:
        return 0

    heights = [0] * len(matrix[0])
    best = 0

    for row in matrix:
        for c, cell in enumerate(row):
            heights[c] = heights[c] + 1 if cell == "1" else 0
        best = max(best, largest_rectangle_area(heights))

    return best
```

```rust
impl Solution {
    /// @param matrix binary matrix
    /// @return       max all-1s rectangle area
    pub fn maximal_rectangle(matrix: Vec<Vec<char>>) -> i32 {
        fn largest_rectangle_area(heights: &Vec<i32>) -> i32 {
            let mut stack: Vec<usize> = Vec::new();
            let mut best = 0;

            for i in 0..=heights.len() {
                let h = if i == heights.len() { 0 } else { heights[i] };

                while let Some(&top) = stack.last() {
                    if heights[top] <= h { break; }
                    let height = heights[stack.pop().unwrap()];
                    let width = if stack.is_empty() { i as i32 } else { i as i32 - stack[stack.len() - 1] as i32 - 1 };
                    best = best.max(height * width);
                }
                stack.push(i);
            }
            best
        }

        if matrix.is_empty() { return 0; }

        let mut heights = vec![0; matrix[0].len()];
        let mut best = 0;

        for row in &matrix {
            for (c, &cell) in row.iter().enumerate() {
                heights[c] = if cell == '1' { heights[c] + 1 } else { 0 };
            }
            best = best.max(largest_rectangle_area(&heights));
        }
        best
    }
}
```

## Dry run

**Input:** the example.

```
row 0: heights [1,0,1,0,0].  max area 1.
row 1: [2,0,2,1,1].  max: height 2 col 0 -> 2; col 2 -> 2.  best 2.
row 2: [3,1,3,2,2].  stack pass: height 3 cols 0,2... max = 3 (col 0 or 2 alone) or 2x3=6? 
  heights [3,1,3,2,2]: pop 3 (i=1): width 1 -> 3.  pop 1 (i=2): width 2 -> 2.  
  pop 3 (i=4): width 1 -> 3.  pop 2 (i=5): width 2 -> 4.  pop 2 (i=5): width 3 -> 6.
  best 6 ✓
row 3: [4,0,0,3,0].  max 4.
Output: 6 ✓
```

## Complexity

**Time.** Rows × stack pass:

$$
T(m, n) = O(m \cdot n)
$$

**Space.** The heights + stack:

$$
S(m, n) = O(n)
$$

## Variants & follow-ups

- **Largest Rectangle In Histogram** ([8.5](largest-rectangle-in-histogram.md)) — the subroutine this page reuses.
- **Interview follow-up:** "Why does the histogram reset on 0?" A 0 breaks the column's consecutive-1 run — the rectangle can't span it. `heights[c] = 0` is the reset that keeps each row's histogram truthful about rectangles *ending* at that row.
