# 3.19 Pascal's Triangle

> **Source:** [`src/main/kotlin/math/dp/PascalsTriangle.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/dp/PascalsTriangle.kt)
> **Pattern:** build rows from the previous · **Core page**

## The Problem

The first `numRows` rows of Pascal's triangle (each cell = sum of the two above).

- Constraints: $1 \le numRows \le 30$.

## Examples

```
Input:  numRows = 5
Output: [[1],[1,1],[1,2,1],[1,3,3,1],[1,4,6,4,1]]
```

## Intuition — a row's interior is the previous row's adjacent sums

Each row is 1 at both ends; cell `j` = `row[i-1][j-1] + row[i-1][j]`. Pre-fill every row with 1s, then fix the interior:

```kotlin
val result = MutableList(numRows) { MutableList(it + 1) { 1 } }

for (i in 2 until numRows) {
    for (j in 1 until i) {
        result[i][j] = result[i - 1][j - 1] + result[i - 1][j]
    }
}
return result
```

**Why pre-fill with 1s?** The triangle's edges are all 1 — `MutableList(it + 1) { 1 }` builds each row pre-loaded, so only the interior needs computing. Zero edge-handling branches.

**Why `j in 1 until i`?** The interior spans columns 1..i-1 (row i has i+1 cells; the 0 and i are the already-set edges). The recurrence's `j-1`/`j` reads are in-bounds by construction.

## Approach 1 — Build from the previous row explicitly

Push 1s as you go: same idea, more bookkeeping.

## Approach 2 — Pre-fill + fix interior (the repo's version, optimal)

```kotlin
class PascalsTriangle {
    /**
     * @param numRows number of rows
     * @return        Pascal's triangle rows
     */
    fun generate(numRows: Int): List<List<Int>> {
        val result = MutableList(numRows) { MutableList(it + 1) { 1 } }

        for (i in 2 until numRows) {
            for (j in 1 until i) {
                result[i][j] = result[i - 1][j - 1] + result[i - 1][j]
            }
        }
        return result
    }
}
```

```java
import java.util.*;

public class PascalsTriangle {
    /**
     * @param numRows number of rows
     * @return        Pascal's triangle rows
     */
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            List<Integer> row = new ArrayList<>(Collections.nCopies(i + 1, 1));

            for (int j = 1; j < i; j++) {
                row.set(j, result.get(i - 1).get(j - 1) + result.get(i - 1).get(j));
            }
            result.add(row);
        }
        return result;
    }
}
```

```cpp
#include <vector>

class PascalsTriangle {
public:
    /**
     * @param numRows number of rows
     * @return        Pascal's triangle rows
     */
    std::vector<std::vector<int>> generate(int numRows) {
        std::vector<std::vector<int>> result;

        for (int i = 0; i < numRows; i++) {
            std::vector<int> row(i + 1, 1);            // edges are 1

            for (int j = 1; j < i; j++) {
                row[j] = result[i - 1][j - 1] + result[i - 1][j];
            }
            result.push_back(row);
        }
        return result;
    }
};
```

```python
def generate(num_rows: int) -> list[list[int]]:
    """
    @param num_rows: number of rows
    @return:         Pascal's triangle rows
    """
    result = [[1] * (i + 1) for i in range(num_rows)]   # edges are 1

    for i in range(2, num_rows):
        for j in range(1, i):
            result[i][j] = result[i - 1][j - 1] + result[i - 1][j]

    return result
```

```rust
impl Solution {
    /// @param num_rows number of rows
    /// @return         Pascal's triangle rows
    pub fn generate(num_rows: i32) -> Vec<Vec<i32>> {
        let n = num_rows as usize;
        let mut result: Vec<Vec<i32>> = Vec::with_capacity(n);

        for i in 0..n {
            let mut row = vec![1; i + 1];           // edges are 1
            for j in 1..i {
                row[j] = result[i - 1][j - 1] + result[i - 1][j];
            }
            result.push(row);
        }
        result
    }
}
```

## Dry run

**Input:** `numRows = 5`.

```
result = [[1], [1,1], [1,1,1], [1,1,1,1], [1,1,1,1,1]]   (pre-filled)

i=2: j=1: result[2][1] = result[1][0] + result[1][1] = 1+1 = 2.  row: [1,2,1]
i=3: j=1: result[3][1] = 1+1 = 2.  j=2: result[3][2] = result[2][1]+result[2][2] = 2+1 = 3.  row: [1,3,3,1]
i=4: j=1: 2.  j=2: 3+3=6.  j=3: 3+1=4.  row: [1,4,6,4,1]

Output: [[1],[1,1],[1,2,1],[1,3,3,1],[1,4,6,4,1]] ✓
```

The pre-fill does the edge work: only 6 interior cells get computed across all rows. The recurrence `row[i][j] = row[i-1][j-1] + row[i-1][j]` is the identity "each cell is the sum of the two above" in one line — the [2.0](../ch02-dynamic-programming/pattern-primer.md) DP table in miniature (the triangle IS a DP table).

## Complexity

**Time.** Total cells:

$$
T(n) = O(n^2)
$$

**Space.** The triangle itself:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Pascal's Triangle II** (`math/dp/PascalsTriangle_II.kt`) — one row only: rolling the recurrence over a single array.
- **Interview follow-up:** "Why pre-fill with 1s instead of pushing edges in the loop?" The triangle's edges are always 1 — pre-filling makes the interior loop branch-free. The only cells needing arithmetic are the interior, and their indices (`1 until i`) match exactly the cells the recurrence can compute safely.
