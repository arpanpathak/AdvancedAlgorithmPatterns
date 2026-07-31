# 10.18 Equal Row And Column Pairs

> **Source:** [`src/main/kotlin/array/hashtable/EqualRowAndColumnPairs.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/EqualRowAndColumnPairs.kt)
> **Pattern:** sequence-vector keys · **Core page**

## The Problem

Count `(row, col)` pairs where the row's values equal the column's values.

- Constraints: n ≤ 200.

## Examples

```
Input:  grid = [[3,2,1],[1,7,6],[2,7,7]]   -> Output: 1   (row 0 == col 0)
Input:  grid = [[3,1,2,2],[1,4,4,5],[2,4,2,2],[2,4,2,2]] -> Output: 3
```

## Intuition — rows as list-keys in a map; count matching columns

A row is a sequence — use it as a **map key** ([10.12](rank-transform-of-an-array.md)/[10.17](group-shifted-strings.md) key-design). Count each row's occurrences; for each column, add the count of its row-key:

```kotlin
val rowCount = mutableMapOf<List<Int>, Int>()
for (row in grid) rowCount[row.toList()] = rowCount.getOrDefault(row.toList(), 0) + 1

var count = 0
for (c in 0 until n) {
    val column = List(n) { grid[it][c] }
    count += rowCount[column] ?: 0
}
return count
```

**Why `List<Int>` as the key?** Structural equality — `[3,2,1]` equals any list with the same elements. The [9.2](../ch09-strings/group-anagrams.md) frequency-list key idiom, applied to raw values.

## Approach 1 — Brute force triple loop (O(n³))

Compare each row to each column element-wise: correct, slow.

## Approach 2 — Row-vector keys (the repo's version, optimal)

```kotlin
class EqualRowAndColumnPairs {
    /**
     * @param grid n x n grid
     * @return     count of equal row/column pairs
     */
    fun equalPairs(grid: Array<IntArray>): Int {
        val n = grid.size
        val rowCount = mutableMapOf<List<Int>, Int>()

        for (row in grid) {
            val key = row.toList()
            rowCount[key] = rowCount.getOrDefault(key, 0) + 1
        }

        var count = 0
        for (c in 0 until n) {
            val column = List(n) { grid[it][c] }
            count += rowCount[column] ?: 0
        }
        return count
    }
}
```

```java
import java.util.*;

public class EqualRowAndColumnPairs {
    /**
     * @param grid n x n grid
     * @return     count of equal row/column pairs
     */
    public int equalPairs(int[][] grid) {
        int n = grid.length;
        Map<List<Integer>, Integer> rows = new HashMap<>();

        for (int[] row : grid) {
            List<Integer> key = new ArrayList<>();
            for (int v : row) key.add(v);
            rows.put(key, rows.getOrDefault(key, 0) + 1);
        }

        int count = 0;
        for (int c = 0; c < n; c++) {
            List<Integer> col = new ArrayList<>();
            for (int r = 0; r < n; r++) col.add(grid[r][c]);
            count += rows.getOrDefault(col, 0);
        }
        return count;
    }
}
```

```cpp
#include <vector>
#include <map>

class EqualRowAndColumnPairs {
public:
    /**
     * @param grid n x n grid
     * @return     count of equal row/column pairs
     */
    int equalPairs(std::vector<std::vector<int>>& grid) {
        int n = grid.size();
        std::map<std::vector<int>, int> rows;

        for (auto& row : grid) rows[row]++;

        int count = 0;
        for (int c = 0; c < n; c++) {
            std::vector<int> col;
            for (int r = 0; r < n; r++) col.push_back(grid[r][c]);
            count += rows[col];
        }
        return count;
    }
};
```

```python
def equal_pairs(grid: list[list[int]]) -> int:
    """
    @param grid: n x n grid
    @return:     count of equal row/column pairs
    """
    rows = {}
    for row in grid:
        rows[tuple(row)] = rows.get(tuple(row), 0) + 1

    count = 0
    for c in range(len(grid)):
        col = tuple(grid[r][c] for r in range(len(grid)))
        count += rows.get(col, 0)

    return count
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param grid n x n grid
    /// @return     count of equal row/column pairs
    pub fn equal_pairs(grid: Vec<Vec<i32>>) -> i32 {
        let n = grid.len();
        let mut rows: HashMap<Vec<i32>, i32> = HashMap::new();

        for row in &grid {
            *rows.entry(row.clone()).or_insert(0) += 1;
        }

        let mut count = 0;
        for c in 0..n {
            let col: Vec<i32> = (0..n).map(|r| grid[r][c]).collect();
            count += rows.get(&col).copied().unwrap_or(0);
        }
        count
    }
}
```

## Dry run

**Input:** `grid = [[3,1,2,2],[1,4,4,5],[2,4,2,2],[2,4,2,2]]`.

```
rows: [3,1,2,2]:1, [1,4,4,5]:1, [2,4,2,2]:2
columns: [3,1,2,2] -> 1.  [1,4,4,4] -> 0.  [2,4,2,2] -> 2.  [2,5,2,2] -> 0.

count = 1 + 0 + 2 + 0 = 3 ✓
```

The duplicate row `[2,4,2,2]` appears twice — its map count (2) makes both its column matches count, hence 3 total. The `List` key's structural equality is the whole mechanism: no string encoding needed.

## Complexity

**Time.** Rows + columns:

$$
T(n) = O(n^2)
$$

**Space.** The row map:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Group Anagrams** ([9.2](../ch09-strings/group-anagrams.md)) — the same sequence-key grouping.
- **Interview follow-up:** "Why is `List<Int>` a safe key?" Lists have structural equality/hashCode in Kotlin/Java/Python (tuples) — unlike arrays, which compare by identity. The key choice is the correctness: `IntArray` as a key would silently never match.
